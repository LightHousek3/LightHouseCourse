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
import java.sql.Types;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
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
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Quiz quiz = null;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM Quizzes WHERE QuizID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, quizId);
            rs = ps.executeQuery();

            if (rs.next()) {
                quiz = mapQuiz(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting quiz by ID: " + e.getMessage());
        } finally {
            closeResources(rs, ps, conn);
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
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Quiz> quizzes = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT * FROM Quizzes WHERE LessonID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, lessonId);
            rs = ps.executeQuery();

            while (rs.next()) {
                quizzes.add(mapQuiz(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting quizzes by lesson ID: " + e.getMessage());
        } finally {
            closeResources(rs, ps, conn);
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
    private Quiz mapQuiz(ResultSet rs) throws SQLException {
        Quiz quiz = new Quiz();
        quiz.setQuizID(rs.getInt("QuizID"));
        quiz.setLessonID(rs.getInt("LessonID"));
        quiz.setTitle(rs.getString("Title"));
        quiz.setDescription(rs.getString("Description"));
        quiz.setTimeLimit(rs.getObject("TimeLimit", Integer.class));
        quiz.setPassingScore(rs.getInt("PassingScore"));
        
        // Check if the IsActive column exists in the ResultSet
        try {
            quiz.setIsActive(rs.getBoolean("IsActive"));
        } catch (SQLException e) {
            // If the column doesn't exist, default to true
            quiz.setIsActive(true);
        }

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

    public int insertWithConnection(Connection conn, Quiz quiz) throws SQLException {
        String sql = "INSERT INTO Quizzes (LessonID, Title, Description, TimeLimit, PassingScore) VALUES (?, ?, ?, ?, ?)";
        int quizId = -1;

        try ( PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Set values for the quiz
            ps.setInt(1, quiz.getLessonID());
            ps.setString(2, quiz.getTitle());
            ps.setString(3, quiz.getDescription());
            ps.setInt(4, quiz.getTimeLimit());
            ps.setInt(5, quiz.getPassingScore());

            // Execute the insert for the quiz
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                try ( ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        quizId = rs.getInt(1); // Get the generated quiz ID
                        quiz.setQuizID(quizId); // Set the generated quiz ID
                    }
                }

            } else {
                throw new SQLException("Quiz insert failed!");

            }
        }
        // Insert questions (với QuizID vừa lấy được)
        if (quiz.getQuestions() != null) {
            for (Question q : quiz.getQuestions()) {
                insertQuestionWithConnection(conn, quiz.getQuizID(), q);
            }

        }

        return quizId;
    }

    private void insertQuestionWithConnection(Connection conn, int quizId, Question question) throws SQLException {
        String sql = "INSERT INTO Questions (QuizID, Content, Type, Points, OrderIndex) VALUES (?, ?, ?, ?, ?)";
        try ( PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, quizId);
            ps.setString(2, question.getContent());
            ps.setString(3, "multiple_choice");
            ps.setInt(4, question.getPoints());
            ps.setInt(5, question.getOrderIndex());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                try ( ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int questionId = rs.getInt(1);
                        question.setQuestionID(questionId);
                    }
                }
            } else {
                throw new SQLException("Question insert failed!");
            }
        }

        // Insert answers
        if (question.getAnswers() != null) {
            for (Answer a : question.getAnswers()) {
                insertAnswerWithConnection(conn, question.getQuestionID(), a);
            }
        }
    }

    private void insertAnswerWithConnection(Connection conn, int questionId, Answer answer) throws SQLException {
        String sql = "INSERT INTO Answers (QuestionID, Content, IsCorrect, OrderIndex) VALUES (?, ?, ?, ?)";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, questionId);
            ps.setString(2, answer.getContent());
            ps.setBoolean(3, answer.isCorrect());
            ps.setInt(4, answer.getOrderIndex());
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected != 1) {
                throw new SQLException("Answer insert failed!");
            }
        }
    }

    public boolean updateQuizItem(int quizID, Quiz quiz) {
        String sql = "UPDATE Quizzes SET Title = ?, Description = ?, TimeLimit = ?, PassingScore = ? WHERE QuizID = ?";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, quiz.getTitle());
            ps.setString(2, quiz.getDescription());
            // TimeLimit có thể null
            if (quiz.getTimeLimit() == null) {
                ps.setNull(3, Types.INTEGER);
            } else {
                ps.setInt(3, quiz.getTimeLimit());
            }
            ps.setInt(4, quiz.getPassingScore());
            ps.setInt(5, quizID);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Update quiz failed: " + ex.getMessage());
            return false;
        }
    }

    public boolean deleteQuizItem(int quizID) {
        String sqlDeleteLessonItem = "DELETE FROM LessonItems WHERE ItemType = 'quiz' AND ItemID = ?";
        String sqlDeleteQuiz = "DELETE FROM Quizzes WHERE QuizID = ?";
        try ( Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try ( PreparedStatement ps1 = conn.prepareStatement(sqlDeleteLessonItem);  PreparedStatement ps2 = conn.prepareStatement(sqlDeleteQuiz)) {
                ps1.setInt(1, quizID);
                ps1.executeUpdate();
                ps2.setInt(1, quizID);
                int affected = ps2.executeUpdate();
                conn.commit();
                return affected > 0;
            } catch (SQLException ex) {
                conn.rollback();
                System.err.println("Delete quiz failed: " + ex.getMessage());
                return false;
            }
        } catch (SQLException ex) {
            System.err.println("Delete quiz failed: " + ex.getMessage());
            return false;
        }
    }
    
    public List<Question> getQuestionsByQuizId(int quizId) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT q.QuestionID, q.Content AS QuestionContent, q.Type, q.Points, q.OrderIndex, "
                + "a.AnswerID, a.Content AS AnswerContent, a.IsCorrect, a.OrderIndex AS AnswerOrder "
                + "FROM Questions q LEFT JOIN Answers a ON q.QuestionID = a.QuestionID "
                + "WHERE q.QuizID = ? ORDER BY q.OrderIndex, a.OrderIndex";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            try ( ResultSet rs = ps.executeQuery()) {
                Map<Integer, Question> questionMap = new LinkedHashMap<>();
                while (rs.next()) {
                    int questionId = rs.getInt("QuestionID");
                    Question question = questionMap.getOrDefault(questionId, new Question());
                    if (!questionMap.containsKey(questionId)) {
                        question.setQuestionID(questionId);
                        question.setContent(rs.getString("QuestionContent"));
                        question.setType(rs.getString("Type"));
                        question.setPoints(rs.getInt("Points"));
                        question.setOrderIndex(rs.getInt("OrderIndex"));
                        question.setAnswers(new ArrayList<Answer>());
                        questionMap.put(questionId, question);
                    }
                    int answerId = rs.getInt("AnswerID");
                    if (answerId > 0) {
                        Answer answer = new Answer();
                        answer.setAnswerID(answerId);
                        answer.setContent(rs.getString("AnswerContent"));
                        answer.setCorrect(rs.getBoolean("IsCorrect"));
                        answer.setOrderIndex(rs.getInt("AnswerOrder"));
                        question.getAnswers().add(answer);
                    }
                }
                questions.addAll(questionMap.values());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return questions;
    }
    
    public boolean isDuplicateQuestionOrderIndex(int quizId, int orderIndex) {
        String sql = "SELECT 1 FROM Questions WHERE QuizID = ? AND OrderIndex = ?";
        try (
                 Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            ps.setInt(2, orderIndex);
            try ( ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Nếu có bản ghi => trùng
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Có thể throw ex hoặc log lỗi tùy yêu cầu hệ thống
        }
        return false;
    }
    
    public List<Integer> getQuestionOrderIndexes(int quizId, int exceptQuestionId) {
        List<Integer> orderIndexes = new ArrayList<>();
        String sql = "SELECT OrderIndex FROM Questions WHERE QuizID = ?"
                + (exceptQuestionId > 0 ? " AND QuestionID <> ?" : "");
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            if (exceptQuestionId > 0) {
                ps.setInt(2, exceptQuestionId);
            }
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orderIndexes.add(rs.getInt("OrderIndex"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Tuỳ ý: throw hoặc log
        }
        return orderIndexes;
    }

    public int insertQuestionWithAnswers(Question question, int quizId, List<Answer> answers) {
        int questionId = -1;
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Insert Question
            String sqlQ = "INSERT INTO Questions (QuizID, Content, Type, Points, OrderIndex) VALUES (?, ?, ?, ?, ?)";
            try ( PreparedStatement psQ = conn.prepareStatement(sqlQ, Statement.RETURN_GENERATED_KEYS)) {
                psQ.setInt(1, quizId);
                psQ.setString(2, question.getContent());
                psQ.setString(3, question.getType());
                psQ.setInt(4, question.getPoints());
                psQ.setInt(5, question.getOrderIndex());
                if (psQ.executeUpdate() > 0) {
                    try ( ResultSet rs = psQ.getGeneratedKeys()) {
                        if (rs.next()) {
                            questionId = rs.getInt(1);
                        }
                    }
                }
            }

            // Insert Answers
            String sqlA = "INSERT INTO Answers (QuestionID, Content, IsCorrect, OrderIndex) VALUES (?, ?, ?, ?)";
            try ( PreparedStatement psA = conn.prepareStatement(sqlA)) {
                for (Answer answer : answers) {
                    psA.setInt(1, questionId);
                    psA.setString(2, answer.getContent());
                    psA.setBoolean(3, answer.isCorrect());
                    psA.setInt(4, answer.getOrderIndex());
                    psA.addBatch();
                }
                psA.executeBatch();
            }

            conn.commit();
        } catch (SQLException ex) {
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException e2) {
            }
            ex.printStackTrace();
            questionId = -1;
        } finally {
            if (conn != null) try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ex) {
            }
        }
        return questionId;
    }

    public Question getQuestionAndAnswersById(int questionId) {
        Question question = null;
        String sql = "SELECT q.QuestionID, q.QuizID, q.Content, q.Type, q.Points, q.OrderIndex, "
                + "a.AnswerID, a.Content AS AnswerContent, a.IsCorrect, a.OrderIndex AS AnswerOrder "
                + "FROM Questions q "
                + "LEFT JOIN Answers a ON q.QuestionID = a.QuestionID "
                + "WHERE q.QuestionID = ? "
                + "ORDER BY a.OrderIndex ASC";

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, questionId);
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (question == null) {
                        question = new Question();
                        question.setQuestionID(rs.getInt("QuestionID"));
                        question.setQuizID(rs.getInt("QuizID"));
                        question.setContent(rs.getString("Content"));
                        question.setType(rs.getString("Type"));
                        question.setPoints(rs.getInt("Points"));
                        question.setOrderIndex(rs.getInt("OrderIndex"));
                        question.setAnswers(new ArrayList<Answer>());
                    }
                    // Nếu có answer thì thêm vào list
                    int answerId = rs.getInt("AnswerID");
                    if (!rs.wasNull()) {
                        Answer ans = new Answer();
                        ans.setAnswerID(answerId);
                        ans.setContent(rs.getString("AnswerContent"));
                        ans.setCorrect(rs.getBoolean("IsCorrect"));
                        ans.setOrderIndex(rs.getInt("AnswerOrder"));
                        ans.setQuestionID(questionId);
                        question.getAnswers().add(ans);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return question;
    }

    public boolean updateQuestionWithAnswers(Question question, List<Answer> answers) {
        String updateQuestionSql = "UPDATE Questions SET Content=?, Type=?, Points=?, OrderIndex=? WHERE QuestionID=?";
        String updateAnswerSql = "UPDATE Answers SET Content=?, IsCorrect=?, OrderIndex=? WHERE AnswerID=?";

        try ( Connection conn = getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            // 1. Update Question
            try ( PreparedStatement psQuestion = conn.prepareStatement(updateQuestionSql)) {
                psQuestion.setString(1, question.getContent());
                psQuestion.setString(2, question.getType());
                psQuestion.setInt(3, question.getPoints());
                psQuestion.setInt(4, question.getOrderIndex());
                psQuestion.setInt(5, question.getQuestionID());
                if (psQuestion.executeUpdate() == 0) {
                    conn.rollback();
                    return false; // Không update được question
                }
            }

            // 2. Update Answers
            try ( PreparedStatement psAnswer = conn.prepareStatement(updateAnswerSql)) {
                for (Answer answer : answers) {
                    psAnswer.setString(1, answer.getContent());
                    psAnswer.setBoolean(2, answer.isCorrect());
                    psAnswer.setInt(3, answer.getOrderIndex());
                    psAnswer.setInt(4, answer.getAnswerID());
                    if (psAnswer.executeUpdate() == 0) {
                        conn.rollback();
                        return false; // Có answer không update được
                    }
                }
            }

            conn.commit();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            // rollback nếu có lỗi
            try {
                // Nếu chưa commit thì rollback
                if (!getConnection().isClosed() && !getConnection().getAutoCommit()) {
                    getConnection().rollback();
                }
            } catch (SQLException ignore) {
            }
            return false;
        }
    }

    public boolean deleteQuestion(int questionId) {
        String sql = "DELETE FROM Questions WHERE QuestionID = ?";
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, questionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

}
