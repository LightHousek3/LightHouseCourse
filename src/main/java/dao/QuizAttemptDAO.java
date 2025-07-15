package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import db.DBContext;
import model.Answer;
import model.Question;
import model.Quiz;
import model.QuizAttempt;
import model.UserAnswer;

/**
 * Data Access Object for QuizAttempt entity.
 */
public class QuizAttemptDAO extends DBContext {

    /**
     * Start a new quiz attempt
     *
     * @param quizId The quiz ID
     * @param customerId The customer ID
     * @return The created QuizAttempt object with generated ID, or null if
     * failed
     */
    public QuizAttempt startAttempt(int quizId, int customerId) {
        QuizAttempt attempt = new QuizAttempt(quizId, customerId);
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "INSERT INTO QuizAttempts (QuizID, CustomerID, StartTime) VALUES (?, ?, ?)";

            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, quizId);
            ps.setInt(2, customerId);
            ps.setTimestamp(3, attempt.getStartTime());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    attempt.setAttemptID(rs.getInt(1));

                    // Load quiz details
                    QuizDAO quizDAO = new QuizDAO();
                    Quiz quiz = quizDAO.getQuizById(quizId);
                    if (quiz != null) {
                        attempt.setQuizTitle(quiz.getTitle());
                        attempt.setTotalQuestions(quiz.getTotalQuestions());
                    }

                    return attempt;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return null;
    }

    /**
     * Get an attempt by its ID
     *
     * @param attemptId The attempt ID
     * @return The QuizAttempt object, or null if not found
     */
    public QuizAttempt getQuizAttemptById(int attemptId) {
        QuizAttempt attempt = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT qa.*, q.Title as QuizTitle, u.Username as UserName "
                    + "FROM QuizAttempts qa "
                    + "JOIN Quizzes q ON qa.QuizID = q.QuizID "
                    + "JOIN Customers u ON qa.CustomerID = u.CustomerID "
                    + "WHERE qa.AttemptID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, attemptId);
            rs = ps.executeQuery();

            if (rs.next()) {
                attempt = mapRow(rs);
                loadUserAnswers(attempt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return attempt;
    }

    /**
     * Get active attempt for a user and quiz
     *
     * @param quizId The quiz ID
     * @param customerId The customer ID
     * @return The active QuizAttempt if exists, or null
     */
    public QuizAttempt getActiveAttempt(int quizId, int customerId) {
        QuizAttempt attempt = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            String sql = "SELECT TOP 1 qa.*, q.Title as QuizTitle, u.Username as UserName "
                    + "FROM QuizAttempts qa "
                    + "JOIN Quizzes q ON qa.QuizID = q.QuizID "
                    + "JOIN Customers u ON qa.CustomerID = u.CustomerID "
                    + "WHERE qa.QuizID = ? AND qa.CustomerID = ? AND qa.EndTime IS NULL "
                    + "ORDER BY qa.StartTime DESC";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, quizId);
            ps.setInt(2, customerId);
            rs = ps.executeQuery();

            if (rs.next()) {
                attempt = mapRow(rs);
                loadUserAnswers(attempt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return attempt;
    }

    /**
     * Save user's answer
     *
     * @param userAnswer The UserAnswer object
     * @return true if successful, false otherwise
     */
    public boolean saveUserAnswer(UserAnswer userAnswer) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            // Check if answer exists already
            String checkSql = "SELECT UserAnswerID FROM UserAnswers WHERE AttemptID = ? AND QuestionID = ?";
            ps = conn.prepareStatement(checkSql);
            ps.setInt(1, userAnswer.getAttemptID());
            ps.setInt(2, userAnswer.getQuestionID());

            rs = ps.executeQuery();

            if (rs.next()) {
                // Update existing answer
                int userAnswerID = rs.getInt("UserAnswerID");
                rs.close();
                ps.close();

                String updateSql = "UPDATE UserAnswers SET AnswerID = ? WHERE UserAnswerID = ?";
                ps = conn.prepareStatement(updateSql);

                ps.setInt(1, userAnswer.getAnswerID());

                ps.setInt(2, userAnswerID);
            } else {
                // Insert new answer
                rs.close();
                ps.close();

                String insertSql = "INSERT INTO UserAnswers (AttemptID, QuestionID, AnswerID) VALUES (?, ?, ?)";
                ps = conn.prepareStatement(insertSql);

                ps.setInt(1, userAnswer.getAttemptID());
                ps.setInt(2, userAnswer.getQuestionID());
                ps.setInt(3, userAnswer.getAnswerID());

            }

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(rs, ps, conn);
        }
    }

    /**
     * Submit a quiz attempt and grade it
     *
     * @param attemptId The attempt ID to submit
     * @return The updated QuizAttempt with score and pass/fail status, or null
     * if failed
     */
    public QuizAttempt submitAttempt(int attemptId) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // Get the attempt
            QuizAttempt attempt = getQuizAttemptById(attemptId);
            if (attempt == null) {
                return null;
            }

            // If already submitted, just return the attempt
            if (attempt.getEndTime() != null) {
                return attempt;
            }

            // Mark end time
            attempt.setEndTime(new Timestamp(System.currentTimeMillis()));

            // Get quiz to check passing score
            QuizDAO quizDAO = new QuizDAO();
            Quiz quiz = quizDAO.getQuizById(attempt.getQuizID());

            if (quiz == null) {
                conn.rollback();
                return null;
            }

            // Grade the attempt
            int totalPoints = 0;
            int earnedPoints = 0;
            Map<Integer, Boolean> answerCorrectMap = new HashMap<>();

            // Get correct answers for each question
            for (Question question : quiz.getQuestions()) {
                totalPoints += question.getPoints();

                for (Answer answer : question.getAnswers()) {
                    if (answer.isCorrect()) {
                        answerCorrectMap.put(answer.getAnswerID(), true);
                    }
                }
            }

            // Check user answers against correct answers
            for (UserAnswer userAnswer : attempt.getUserAnswers()) {
                Integer answerId = userAnswer.getAnswerID();
                if (answerId != null && answerCorrectMap.containsKey(answerId)) {
                    // Answer is correct
                    userAnswer.setCorrect(true);

                    // Find the question and add its points
                    for (Question question : quiz.getQuestions()) {
                        if (question.getQuestionID() == userAnswer.getQuestionID()) {
                            earnedPoints += question.getPoints();
                            break;
                        }
                    }
                } else {
                    userAnswer.setCorrect(false);
                }

                // Update user answer in database
                String updateAnswerSql = "UPDATE UserAnswers SET IsCorrect = ? WHERE AttemptID = ? AND QuestionID = ?";
                ps = conn.prepareStatement(updateAnswerSql);
                ps.setBoolean(1, userAnswer.getCorrect());
                ps.setInt(2, attemptId);
                ps.setInt(3, userAnswer.getQuestionID());
                ps.executeUpdate();
                ps.close();
            }

            // Calculate score as percentage
            int score = totalPoints > 0 ? (int) ((earnedPoints * 100.0) / totalPoints) : 0;
            attempt.setScore(score);
            attempt.setPassed(score >= quiz.getPassingScore());

            // Update attempt in database
            String updateAttemptSql = "UPDATE QuizAttempts SET EndTime = ?, Score = ?, IsPassed = ? WHERE AttemptID = ?";
            ps = conn.prepareStatement(updateAttemptSql);
            ps.setTimestamp(1, attempt.getEndTime());
            ps.setInt(2, score);
            ps.setBoolean(3, attempt.getPassed());
            ps.setInt(4, attemptId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                // If passed, mark quiz as completed for lesson item progress
                if (attempt.getPassed()) {
                    LessonItemDAO lessonItemDAO = new LessonItemDAO();
                    LessonItemProgressDAO progressDAO = new LessonItemProgressDAO();

                    int lessonItemId = lessonItemDAO.getByItemTypeAndItemId("quiz", attempt.getQuizID()).getLessonItemID();
                    if (lessonItemId > 0) {
                        progressDAO.markItemAsCompleted(attempt.getCustomerID(), lessonItemId);
                    }
                }

                conn.commit();
                return attempt;
            } else {
                conn.rollback();
                return null;
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
            return null;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    /**
     * Load user answers for an attempt
     */
    private void loadUserAnswers(QuizAttempt attempt) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT ua.* FROM UserAnswers ua WHERE ua.AttemptID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, attempt.getAttemptID());
            rs = ps.executeQuery();

            List<UserAnswer> userAnswers = new ArrayList<>();
            while (rs.next()) {
                UserAnswer userAnswer = new UserAnswer();
                userAnswer.setUserAnswerID(rs.getInt("UserAnswerID"));
                userAnswer.setAttemptID(rs.getInt("AttemptID"));
                userAnswer.setQuestionID(rs.getInt("QuestionID"));

                // Handle nullable columns
                if (rs.getObject("AnswerID") != null) {
                    userAnswer.setAnswerID(rs.getInt("AnswerID"));
                }

                if (rs.getObject("IsCorrect") != null) {
                    userAnswer.setCorrect(rs.getBoolean("IsCorrect"));
                }

                userAnswers.add(userAnswer);
            }

            attempt.setUserAnswers(userAnswers);
            attempt.setAnsweredQuestions(userAnswers.size());

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }
    }

    /**
     * Map a ResultSet row to a QuizAttempt object
     */
    private QuizAttempt mapRow(ResultSet rs) throws SQLException {
        QuizAttempt attempt = new QuizAttempt();
        attempt.setAttemptID(rs.getInt("AttemptID"));
        attempt.setQuizID(rs.getInt("QuizID"));
        attempt.setCustomerID(rs.getInt("CustomerID"));
        attempt.setStartTime(rs.getTimestamp("StartTime"));
        attempt.setEndTime(rs.getTimestamp("EndTime"));

        // Handle nullable columns
        if (rs.getObject("Score") != null) {
            attempt.setScore(rs.getInt("Score"));
        }

        if (rs.getObject("IsPassed") != null) {
            attempt.setPassed(rs.getBoolean("IsPassed"));
        }

        // Additional info
        attempt.setQuizTitle(rs.getString("QuizTitle"));

        return attempt;
    }

    /**
     * Get all attempts for a user on a specific quiz
     *
     * @param customerId The customerId ID
     * @param quizId The quiz ID
     * @return List of QuizAttempt objects
     */
    public List<QuizAttempt> getAllAttemptsForUser(int customerId, int quizId) {
        List<QuizAttempt> attempts = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT qa.*, q.Title as QuizTitle, u.Username as UserName "
                    + "FROM QuizAttempts qa "
                    + "JOIN Quizzes q ON qa.QuizID = q.QuizID "
                    + "JOIN Customers u ON qa.CustomerID = u.CustomerID "
                    + "WHERE qa.CustomerID = ? AND qa.QuizID = ? "
                    + "ORDER BY qa.StartTime DESC";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);
            ps.setInt(2, quizId);
            rs = ps.executeQuery();

            while (rs.next()) {
                QuizAttempt attempt = mapRow(rs);
                attempts.add(attempt);
            }

            // Load user answers for each attempt
            for (QuizAttempt attempt : attempts) {
                loadUserAnswers(attempt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return attempts;
    }

    /**
     * Get the time remaining for a quiz attempt
     *
     * @param attemptId The attempt ID
     * @return Time remaining in seconds, or -1 if no time limit
     */
    public int getTimeRemaining(int attemptId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int timeRemaining = -1;

        try {
            conn = getConnection();
            String sql = "SELECT qa.StartTime, q.TimeLimit "
                    + "FROM QuizAttempts qa "
                    + "JOIN Quizzes q ON qa.QuizID = q.QuizID "
                    + "WHERE qa.AttemptID = ? AND qa.EndTime IS NULL";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, attemptId);
            rs = ps.executeQuery();

            if (rs.next()) {
                Timestamp startTime = rs.getTimestamp("StartTime");
                int timeLimit = rs.getInt("TimeLimit");

                if (!rs.wasNull()) { // Check if TimeLimit is not null
                    long currentTimeMs = System.currentTimeMillis();
                    long startTimeMs = startTime.getTime();
                    long elapsedTimeSeconds = (currentTimeMs - startTimeMs) / 1000;
                    long timeLimitSeconds = timeLimit * 60L;

                    timeRemaining = (int) (timeLimitSeconds - elapsedTimeSeconds);
                    if (timeRemaining < 0) {
                        timeRemaining = 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return timeRemaining;
    }

    /**
     * Get the count of answered questions for an attempt
     *
     * @param attemptId The attempt ID
     * @return The number of questions that have been answered
     */
    public int getAnsweredQuestionCount(int attemptId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;

        try {
            conn = getConnection();
            String sql = "SELECT COUNT(*) AS AnsweredCount FROM UserAnswers WHERE AttemptID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, attemptId);
            rs = ps.executeQuery();

            if (rs.next()) {
                count = rs.getInt("AnsweredCount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return count;
    }

    /**
     * Check if a quiz attempt is completed
     *
     * @param attemptId The attempt ID
     * @return true if the attempt is completed, false otherwise
     */
    public boolean isAttemptCompleted(int attemptId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean completed = false;

        try {
            conn = getConnection();
            String sql = "SELECT EndTime FROM QuizAttempts WHERE AttemptID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, attemptId);
            rs = ps.executeQuery();

            if (rs.next()) {
                Timestamp endTime = rs.getTimestamp("EndTime");
                completed = (endTime != null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return completed;
    }

    /**
     * Calculate the score of a quiz attempt based on user answers This is
     * useful to preview a score before final submission
     *
     * @param attemptId The attempt ID
     * @return The calculated score (0-100)
     */
    public int calculateAttemptScore(int attemptId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int earnedPoints = 0;
        int totalPoints = 0;

        try {
            conn = getConnection();

            // First, get the quiz ID for this attempt
            String attemptSql = "SELECT QuizID FROM QuizAttempts WHERE AttemptID = ?";
            ps = conn.prepareStatement(attemptSql);
            ps.setInt(1, attemptId);
            rs = ps.executeQuery();

            if (!rs.next()) {
                return 0; // Attempt not found
            }

            int quizId = rs.getInt("QuizID");
            rs.close();
            ps.close();

            // Get the correct answers for this quiz
            String answerSql = "SELECT q.QuestionID, q.Points, a.AnswerID "
                    + "FROM Questions q "
                    + "JOIN Answers a ON q.QuestionID = a.QuestionID "
                    + "WHERE q.QuizID = ? AND a.IsCorrect = 1";

            ps = conn.prepareStatement(answerSql);
            ps.setInt(1, quizId);
            rs = ps.executeQuery();

            // Create a map of correct answers and their point values
            Map<Integer, Integer> questionPoints = new HashMap<>(); // QuestionID -> Points
            Map<Integer, Integer> correctAnswers = new HashMap<>(); // QuestionID -> AnswerID

            while (rs.next()) {
                int questionId = rs.getInt("QuestionID");
                int points = rs.getInt("Points");
                int answerId = rs.getInt("AnswerID");

                questionPoints.put(questionId, points);
                correctAnswers.put(questionId, answerId);
                totalPoints += points;
            }

            rs.close();
            ps.close();

            // Now get the user's answers for this attempt
            String userAnswerSql = "SELECT QuestionID, AnswerID FROM UserAnswers WHERE AttemptID = ?";
            ps = conn.prepareStatement(userAnswerSql);
            ps.setInt(1, attemptId);
            rs = ps.executeQuery();

            while (rs.next()) {
                int questionId = rs.getInt("QuestionID");
                int answerId = rs.getInt("AnswerID");

                // Check if this answer is correct
                if (correctAnswers.containsKey(questionId) && correctAnswers.get(questionId) == answerId) {
                    earnedPoints += questionPoints.getOrDefault(questionId, 0);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        // Calculate score as percentage
        return totalPoints > 0 ? (int) ((earnedPoints * 100.0) / totalPoints) : 0;
    }

    /**
     * Get a list of question IDs that the user has answered in an attempt
     *
     * @param attemptId The attempt ID
     * @return List of question IDs that have been answered
     */
    public List<Integer> getAnsweredQuestionIds(int attemptId) {
        List<Integer> questionIds = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT QuestionID FROM UserAnswers WHERE AttemptID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, attemptId);
            rs = ps.executeQuery();

            while (rs.next()) {
                questionIds.add(rs.getInt("QuestionID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return questionIds;
    }
}
