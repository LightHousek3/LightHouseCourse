package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.Quiz;
import model.Question;
import db.DBContext;
import java.util.Collections;
import java.util.Comparator;
import model.Answer;

/**
 * Data Access Object for Quiz entity.
 */
public class QuizDAO extends DBContext {

    /**
     * Get a quiz by ID
     * 
     * @param quizId The quiz ID
     * @return The quiz object, or null if not found
     */
    public Quiz getQuizById(int quizId) {
        Quiz quiz = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM Quizzes WHERE QuizID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, quizId);
            rs = ps.executeQuery();

            if (rs.next()) {
                quiz = mapRow(rs);
                // Load questions for the quiz
                loadQuestionsForQuiz(quiz);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return quiz;
    }

    /**
     * Get quizzes for a specific lesson
     * 
     * @param lessonId The lesson ID
     * @return List of quizzes for the lesson
     */
    public List<Quiz> getQuizzesByLessonId(int lessonId) {
        List<Quiz> quizzes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM Quizzes WHERE LessonID = ? ORDER BY QuizID";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, lessonId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Quiz quiz = mapRow(rs);
                quizzes.add(quiz);
            }

            // Load questions for each quiz
            for (Quiz quiz : quizzes) {
                loadQuestionsForQuiz(quiz);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return quizzes;
    }

    /**
     * Save a quiz (insert new or update existing)
     * 
     * @param quiz The quiz to save
     * @return true if successful, false otherwise
     */
    public boolean save(Quiz quiz) {
        if (quiz.getQuizID() > 0) {
            return update(quiz);
        } else {
            return insert(quiz) > 0;
        }
    }

    /**
     * Insert a new quiz
     * 
     * @param quiz The quiz to insert
     * @return The new quiz ID, or -1 if failed
     */
    private int insert(Quiz quiz) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int quizId = -1;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            String sql = "INSERT INTO Quizzes (LessonID, Title, Description, TimeLimit, PassingScore) "
                    + "VALUES (?, ?, ?, ?, ?)";

            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, quiz.getLessonID());
            ps.setString(2, quiz.getTitle());
            ps.setString(3, quiz.getDescription());

            if (quiz.getTimeLimit() != null) {
                ps.setInt(4, quiz.getTimeLimit());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }

            ps.setInt(5, quiz.getPassingScore());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    quizId = rs.getInt(1);
                    quiz.setQuizID(quizId);

                    // Insert questions if available
                    if (quiz.getQuestions() != null && !quiz.getQuestions().isEmpty()) {
                        // Implementation for inserting questions would go here
                        // This would involve another DAO or method to handle Question entities
                    }

                    conn.commit();
                } else {
                    conn.rollback();
                }
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return quizId;
    }

    /**
     * Update an existing quiz
     * 
     * @param quiz The quiz to update
     * @return true if successful, false otherwise
     */
    private boolean update(Quiz quiz) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            String sql = "UPDATE Quizzes SET LessonID = ?, Title = ?, Description = ?, "
                    + "TimeLimit = ?, PassingScore = ?"
                    + "WHERE QuizID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, quiz.getLessonID());
            ps.setString(2, quiz.getTitle());
            ps.setString(3, quiz.getDescription());

            if (quiz.getTimeLimit() != null) {
                ps.setInt(4, quiz.getTimeLimit());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }

            ps.setInt(5, quiz.getPassingScore());
            ps.setInt(6, quiz.getQuizID());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                // Update questions if available
                if (quiz.getQuestions() != null && !quiz.getQuestions().isEmpty()) {
                    // Implementation for updating questions would go here
                    // This would involve another DAO or method to handle Question entities
                }

                conn.commit();
                success = true;
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return success;
    }

    /**
     * Delete a quiz by ID
     * 
     * @param quizId The quiz ID to delete
     * @return true if successful, false otherwise
     */
    public boolean delete(int quizId) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // First delete related records (like quiz attempts, questions, etc.)
            // This would involve multiple DELETE statements with appropriate foreign keys
            // Then delete the quiz itself
            String sql = "DELETE FROM Quizzes WHERE QuizID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, quizId);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                conn.commit();
                success = true;
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return success;
    }

    /**
     * Check if a user has completed a quiz
     * 
     * @param userId The user ID
     * @param quizId The quiz ID
     * @return true if the user has completed the quiz, false otherwise
     */
    public boolean isQuizCompletedByUser(int userId, int quizId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean completed = false;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM QuizAttempts WHERE UserID = ? AND QuizID = ? AND Score >= (SELECT PassingScore FROM Quizzes WHERE QuizID = ?)";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, quizId);
            ps.setInt(3, quizId);
            rs = ps.executeQuery();

            if (rs.next()) {
                completed = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return completed;
    }

    /**
     * Map a ResultSet row to a Quiz object
     * 
     * @param rs The ResultSet to map
     * @return A Quiz object
     * @throws SQLException If a database error occurs
     */
    private Quiz mapRow(ResultSet rs) throws SQLException {
        Quiz quiz = new Quiz();
        quiz.setQuizID(rs.getInt("QuizID"));
        quiz.setLessonID(rs.getInt("LessonID"));
        quiz.setTitle(rs.getString("Title"));
        quiz.setDescription(rs.getString("Description"));

        int timeLimit = rs.getInt("TimeLimit");
        if (!rs.wasNull()) {
            quiz.setTimeLimit(timeLimit);
        }

        quiz.setPassingScore(rs.getInt("PassingScore"));

        // Calculate total questions
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet countRs = null;

        try {
            conn = getConnection();
            String sql = "SELECT COUNT(*) AS TotalQuestions FROM Questions WHERE QuizID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, quiz.getQuizID());
            countRs = ps.executeQuery();

            if (countRs.next()) {
                quiz.setTotalQuestions(countRs.getInt("TotalQuestions"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (countRs != null) {
                    countRs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                // Don't close the main connection here
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return quiz;
    }

    /**
     * Load questions for a quiz
     * 
     * @param quiz The quiz to load questions for
     */
    private void loadQuestionsForQuiz(Quiz quiz) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Question> questions = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT * FROM Questions WHERE QuizID = ? ORDER BY OrderIndex";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, quiz.getQuizID());
            rs = ps.executeQuery();

            while (rs.next()) {
                Question question = mapQuestionRow(rs);
                questions.add(question);
            }

            // Load answers for each question
            for (Question question : questions) {
                loadAnswersForQuestion(question);
            }

            quiz.setQuestions(questions);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Map a ResultSet row to a Question object
     * 
     * @param rs The ResultSet to map
     * @return A Question object
     * @throws SQLException If a database error occurs
     */
    private Question mapQuestionRow(ResultSet rs) throws SQLException {
        Question question = new Question();
        question.setQuestionID(rs.getInt("QuestionID"));
        question.setQuizID(rs.getInt("QuizID"));
        question.setContent(rs.getString("Content"));
        question.setType(rs.getString("Type"));
        question.setPoints(rs.getInt("Points"));

        return question;
    }

    /**
     * Load answers for a question
     * 
     * @param question The question to load answers for
     */
    private void loadAnswersForQuestion(Question question) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Answer> answers = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT * FROM Answers WHERE QuestionID = ? ORDER BY OrderIndex";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, question.getQuestionID());
            rs = ps.executeQuery();

            while (rs.next()) {
                Answer answer = mapAnswerRow(rs);
                answers.add(answer);
            }

            question.setAnswers(answers);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Map a ResultSet row to an Answer object
     * 
     * @param rs The ResultSet to map
     * @return An Answer object
     * @throws SQLException If a database error occurs
     */
    private Answer mapAnswerRow(ResultSet rs) throws SQLException {
        Answer answer = new Answer();
        answer.setAnswerID(rs.getInt("AnswerID"));
        answer.setQuestionID(rs.getInt("QuestionID"));
        answer.setContent(rs.getString("Content"));
        answer.setCorrect(rs.getBoolean("IsCorrect"));

        return answer;
    }

    /**
     * Get a lesson by quiz ID
     * 
     * @param quizId The quiz ID
     * @return The lesson ID, or -1 if not found
     */
    public int getLessonIdByQuizId(int quizId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int lessonId = -1;

        try {
            conn = getConnection();
            String sql = "SELECT LessonID FROM Quizzes WHERE QuizID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, quizId);
            rs = ps.executeQuery();

            if (rs.next()) {
                lessonId = rs.getInt("LessonID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return lessonId;
    }

    /**
     * Get the best score for a user on a specific quiz
     *
     * @param userId The user ID
     * @param quizId The quiz ID
     * @return The best score, or -1 if the user has not attempted the quiz
     */
    public int getBestScoreForUser(int userId, int quizId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int bestScore = -1;

        try {
            conn = getConnection();
            String sql = "SELECT MAX(Score) AS BestScore FROM QuizAttempts "
                    + "WHERE UserID = ? AND QuizID = ? AND EndTime IS NOT NULL";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, quizId);
            rs = ps.executeQuery();

            if (rs.next() && !rs.wasNull()) {
                bestScore = rs.getInt("BestScore");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return bestScore;
    }

    /**
     * Get the number of attempts a user has made on a specific quiz
     *
     * @param userId The user ID
     * @param quizId The quiz ID
     * @return The number of attempts
     */
    public int getAttemptCountForUser(int userId, int quizId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int attemptCount = 0;

        try {
            conn = getConnection();
            String sql = "SELECT COUNT(*) AS AttemptCount FROM QuizAttempts "
                    + "WHERE UserID = ? AND QuizID = ? AND EndTime IS NOT NULL";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, quizId);
            rs = ps.executeQuery();

            if (rs.next()) {
                attemptCount = rs.getInt("AttemptCount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return attemptCount;
    }

    /**
     * Get the latest quiz attempt for a user
     *
     * @param userId The user ID
     * @param quizId The quiz ID
     * @return The attempt ID of the latest attempt, or -1 if none exists
     */
    public int getLatestAttemptIdForUser(int userId, int quizId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int attemptId = -1;

        try {
            conn = getConnection();
            String sql = "SELECT TOP 1 AttemptID FROM QuizAttempts "
                    + "WHERE UserID = ? AND QuizID = ? "
                    + "ORDER BY StartTime DESC";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, quizId);
            rs = ps.executeQuery();

            if (rs.next()) {
                attemptId = rs.getInt("AttemptID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return attemptId;
    }

    /**
     * Check if a user has passed a quiz
     *
     * @param userId The user ID
     * @param quizId The quiz ID
     * @return true if the user has passed the quiz, false otherwise
     */
    public boolean hasUserPassedQuiz(int userId, int quizId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean passed = false;

        try {
            conn = getConnection();
            String sql = "SELECT TOP 1 1 FROM QuizAttempts "
                    + "WHERE UserID = ? AND QuizID = ? AND IsPassed = 1";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, quizId);
            rs = ps.executeQuery();

            if (rs.next()) {
                passed = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return passed;
    }

    /**
     * Get quiz details with all questions and answers preloaded
     *
     * @param quizId The quiz ID
     * @return The quiz with questions and answers loaded
     */
    public Quiz getQuizWithQuestionsAndAnswers(int quizId) {
        Quiz quiz = getQuizById(quizId);

        if (quiz != null && quiz.getQuestions() != null) {
            // Sort questions by orderIndex for consistent display
            Collections.sort(quiz.getQuestions(), new Comparator<Question>() {
                @Override
                public int compare(Question q1, Question q2) {
                    return Integer.compare(q1.getOrderIndex(), q2.getOrderIndex());
                }
            });

            // Ensure answers are loaded for each question
            for (Question question : quiz.getQuestions()) {
                if (question.getAnswers() != null) {
                    // Randomize answer order for multiple choice questions
                    if ("multiple_choice".equals(question.getType())) {
                        Collections.shuffle(question.getAnswers());
                    }
                }
            }
        }

        return quiz;
    }
}
