/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import db.DBContext;
import static db.DBContext.getConnection;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.CourseProgress;
import model.Lesson;

/**
 * Data Access Object for CourseProgress entity.
 *
 * @author DangPH - CE180896
 */
public class CourseProgressDAO extends DBContext {

    /**
     * Get course progress for a specific user and course.
     *
     * @param customerId The customer ID
     * @param courseId   The course ID
     * @return The course progress object, or null if not found
     */
    public CourseProgress getByCustomerAndCourse(int customerId, int courseId) {
        CourseProgress progress = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM CourseProgress WHERE CustomerID = ? AND CourseID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);
            ps.setInt(2, courseId);
            rs = ps.executeQuery();

            if (rs.next()) {
                progress = mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return progress;
    }

    /**
     * Update an existing course progress.
     *
     * @param progress The course progress to update
     * @return true if successful, false otherwise
     */
    private boolean updateCourseProgress(CourseProgress progress) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = getConnection();
            String sql = "UPDATE CourseProgress SET CompletionPercentage = ?, LastAccessDate = ?, IsCompleted = ? "
                    + "WHERE ProgressID = ?";

            ps = conn.prepareStatement(sql);

            // Use BigDecimal for CompletionPercentage
            if (progress.getCompletionPercentage() != null) {
                ps.setBigDecimal(1, progress.getCompletionPercentage());
            } else {
                ps.setBigDecimal(1, BigDecimal.ZERO);
            }

            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (progress.getLastAccessDate() != null) {
                ps.setTimestamp(2, progress.getLastAccessDate());
            } else {
                ps.setTimestamp(2, now);
            }

            ps.setBoolean(3, progress.isCompleted());
            ps.setInt(4, progress.getProgressID());

            int affectedRows = ps.executeUpdate();
            success = (affectedRows > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(null, ps, conn);
        }

        return success;
    }

    /**
     * Insert a new course progress.
     *
     * @param progress The course progress to insert
     * @return The new progress ID, or -1 if failed
     */
    private int insertCourseProgress(CourseProgress progress) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int progressId = -1;

        try {
            conn = getConnection();
            String sql = "INSERT INTO CourseProgress (CustomerID, CourseID, CompletionPercentage, LastAccessDate, IsCompleted) "
                    + "VALUES (?, ?, ?, ?, ?)";

            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, progress.getCustomerID());
            ps.setInt(2, progress.getCourseID());

            // Use BigDecimal for CompletionPercentage
            if (progress.getCompletionPercentage() != null) {
                ps.setBigDecimal(3, progress.getCompletionPercentage());
            } else {
                ps.setBigDecimal(3, BigDecimal.ZERO);
            }

            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (progress.getLastAccessDate() != null) {
                ps.setTimestamp(4, progress.getLastAccessDate());
            } else {
                ps.setTimestamp(4, now);
            }

            ps.setBoolean(5, progress.isCompleted());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    progressId = rs.getInt(1);
                    progress.setProgressID(progressId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return progressId;
    }

    /**
     * Update access time for a lesson item
     *
     * @param customerId   The customer ID
     * @param lessonItemId The lesson item ID
     * @return true if successful, false otherwise
     */
    public boolean updateLessonItemAccess(int customerId, int lessonItemId) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = getConnection();

            // First check if entry exists
            String checkSql = "SELECT * FROM LessonItemProgress WHERE CustomerID = ? AND LessonItemID = ?";
            ps = conn.prepareStatement(checkSql);
            ps.setInt(1, customerId);
            ps.setInt(2, lessonItemId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Update existing entry
                ps.close();
                String updateSql = "UPDATE LessonItemProgress SET LastAccessDate = ? WHERE CustomerID = ? AND LessonItemID = ?";
                ps = conn.prepareStatement(updateSql);
                ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                ps.setInt(2, customerId);
                ps.setInt(3, lessonItemId);
            } else {
                // Insert new entry
                ps.close();
                String insertSql = "INSERT INTO LessonItemProgress (CustomerID, LessonItemID, IsCompleted, LastAccessDate) VALUES (?, ?, 0, ?)";
                ps = conn.prepareStatement(insertSql);
                ps.setInt(1, customerId);
                ps.setInt(2, lessonItemId);
                ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            }

            int affectedRows = ps.executeUpdate();
            success = (affectedRows > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(null, ps, conn);
        }

        return success;
    }

    /**
     * Save course progress (insert new or update existing).
     *
     * @param progress The course progress to save
     * @return true if successful, false otherwise
     */
    public boolean saveCourseProgress(CourseProgress progress) {
        // Check if progress already exists
        CourseProgress existingProgress = getByCustomerAndCourse(progress.getCustomerID(), progress.getCourseID());

        if (existingProgress != null) {
            progress.setProgressID(existingProgress.getProgressID());
            return updateCourseProgress(progress);
        } else {
            return insertCourseProgress(progress) > 0;
        }
    }

    /**
     * Map a ResultSet row to a CourseProgress object.
     *
     * @param rs The ResultSet to map
     * @return A CourseProgress object
     * @throws SQLException If a database error occurs
     */
    private CourseProgress mapRow(ResultSet rs) throws SQLException {
        CourseProgress progress = new CourseProgress();
        progress.setProgressID(rs.getInt("ProgressID"));
        progress.setCustomerID(rs.getInt("CustomerID"));
        progress.setCourseID(rs.getInt("CourseID"));
        progress.setCompletionPercentage(rs.getBigDecimal("CompletionPercentage"));
        progress.setLastAccessDate(rs.getTimestamp("LastAccessDate"));
        progress.setCompleted(rs.getBoolean("IsCompleted"));
        return progress;
    }

    /**
     * Get course progress for a specific user and course.
     *
     * @param userId   The user ID
     * @param courseId The course ID
     * @return The course progress object, or null if not found
     */
    public CourseProgress getByUserAndCourse(int customerId, int courseId) {
        CourseProgress progress = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM CourseProgress WHERE CustomerID = ? AND CourseID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);
            ps.setInt(2, courseId);
            rs = ps.executeQuery();

            if (rs.next()) {
                progress = mapRow(rs);
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

        return progress;
    }

    /**
     * Check if a lesson is completed by a user.
     *
     * @param customerId   The user ID
     * @param lessonId The lesson ID
     * @return true if the lesson is completed, false otherwise
     */
    public boolean isLessonCompleted(int customerId, int lessonId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean completed = false;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM LessonProgress WHERE CustomerID = ? AND LessonID = ? AND IsCompleted = 1";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);
            ps.setInt(2, lessonId);
            rs = ps.executeQuery();

            if (rs.next()) {
                completed = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return completed;
    }

    /**
     * Mark a lesson as completed by a user.
     *
     * @param userId   The user ID
     * @param lessonId The lesson ID
     * @return true if successful, false otherwise
     */
    public boolean markLessonCompleted(int customerId, int lessonId) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // First check if entry exists
            String checkSql = "SELECT * FROM LessonProgress WHERE CustomerID = ? AND LessonID = ?";
            ps = conn.prepareStatement(checkSql);
            ps.setInt(1, customerId);
            ps.setInt(2, lessonId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Update existing entry
                ps.close();
                String updateSql = "UPDATE LessonProgress SET IsCompleted = 1, CompletionDate = ? WHERE CustomerID = ? AND LessonID = ?";
                ps = conn.prepareStatement(updateSql);
                ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                ps.setInt(2, customerId);
                ps.setInt(3, lessonId);
            } else {
                // Insert new entry
                ps.close();
                String insertSql = "INSERT INTO LessonProgress (CustomerID, LessonID, IsCompleted, CompletionDate) VALUES (?, ?, 1, ?)";
                ps = conn.prepareStatement(insertSql);
                ps.setInt(1, customerId);
                ps.setInt(2, lessonId);
                ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            }

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                // Get courseId for this lesson
                ps.close();
                String courseIdSql = "SELECT CourseID FROM Lessons WHERE LessonID = ?";
                ps = conn.prepareStatement(courseIdSql);
                ps.setInt(1, lessonId);
                rs = ps.executeQuery();

                if (rs.next()) {
                    int courseId = rs.getInt("CourseID");

                    // Update course completion
                    updateCourseCompletion(customerId, courseId);
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
     * Calculate and update course completion percentage.
     *
     * @param customerId   The customer ID
     * @param courseId The course ID
     * @return true if successful, false otherwise
     */
    public boolean updateCourseCompletion(int customerId, int courseId) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // Get total number of lessons in the course
            String totalLessonsSql = "SELECT COUNT(*) AS TotalLessons FROM Lessons WHERE CourseID = ?";
            ps = conn.prepareStatement(totalLessonsSql);
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();

            int totalLessons = 0;
            if (rs.next()) {
                totalLessons = rs.getInt("TotalLessons");
            }

            if (totalLessons == 0) {
                conn.rollback();
                return false;
            }

            // Get number of completed lessons
            ps.close();
            rs.close();
            String completedLessonsSql = "SELECT COUNT(*) AS CompletedLessons FROM LessonProgress LP "
                    + "JOIN Lessons L ON LP.LessonID = L.LessonID "
                    + "WHERE LP.CustomerID = ? AND L.CourseID = ? AND LP.IsCompleted = 1";
            ps = conn.prepareStatement(completedLessonsSql);
            ps.setInt(1, customerId);
            ps.setInt(2, courseId);
            rs = ps.executeQuery();

            int completedLessons = 0;
            if (rs.next()) {
                completedLessons = rs.getInt("CompletedLessons");
            }

            // Calculate completion percentage using BigDecimal
            BigDecimal completionPercentage = new BigDecimal(completedLessons)
                    .multiply(new BigDecimal(100))
                    .divide(new BigDecimal(totalLessons), 2, BigDecimal.ROUND_HALF_UP);

            boolean isCompleted = (completedLessons == totalLessons);

            // Update or insert course progress
            CourseProgress progress = getByUserAndCourse(customerId, courseId);
            if (progress != null) {
                progress.setCompletionPercentage(completionPercentage);
                progress.setLastAccessDate(new Timestamp(System.currentTimeMillis()));
                progress.setCompleted(isCompleted);
                updateCourseProgress(progress);
            } else {
                progress = new CourseProgress();
                progress.setCustomerID(customerId);
                progress.setCourseID(courseId);
                progress.setCompletionPercentage(completionPercentage);
                progress.setLastAccessDate(new Timestamp(System.currentTimeMillis()));
                progress.setCompleted(isCompleted);
                insertCourseProgress(progress);
            }

            conn.commit();
            success = true;
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
     * Get all course progress records for a user
     *
     * @param customerId The customerId ID
     * @return List of course progress records
     */
    public List<CourseProgress> getAllByCustomer(int customerId) {
        List<CourseProgress> progressList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT p.*, c.Name AS CourseName FROM CourseProgress p "
                    + "JOIN Courses c ON p.CourseID = c.CourseID "
                    + "WHERE p.CustomerID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);
            rs = ps.executeQuery();

            while (rs.next()) {
                CourseProgress progress = mapRow(rs);
                try {
                    progress.setCourseName(rs.getString("CourseName"));
                } catch (SQLException e) {
                    // Ignore if column doesn't exist
                }
                progressList.add(progress);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return progressList;
    }

    /**
     * Get the list of completed lessons for a user in a course.
     *
     * @param customerId   The customer ID
     * @param courseId The course ID
     * @return List of completed lessons
     */
    public List<Lesson> getCompletedLessons(int customerId, int courseId) {
        List<Lesson> completedLessons = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT L.* FROM Lessons L "
                    + "JOIN LessonProgress LP ON L.LessonID = LP.LessonID "
                    + "WHERE LP.CustomerID = ? AND L.CourseID = ? AND LP.IsCompleted = 1 "
                    + "ORDER BY L.OrderIndex";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);
            ps.setInt(2, courseId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Lesson lesson = new Lesson();
                lesson.setLessonID(rs.getInt("LessonID"));
                lesson.setTitle(rs.getString("Title"));
                lesson.setOrderIndex(rs.getInt("OrderIndex"));
                completedLessons.add(lesson);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return completedLessons;
    }

    /**
     * Check if a video has been completed by a user
     *
     * @param customerId  The customer ID
     * @param videoId The video ID
     * @return true if completed, false otherwise
     */
    public boolean isVideoCompleted(int customerId, int videoId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean completed = false;

        try {
            conn = getConnection();

            // First get the LessonItemID for this video
            String itemSql = "SELECT LessonItemID FROM LessonItems WHERE ItemType = 'video' AND ItemID = ?";
            ps = conn.prepareStatement(itemSql);
            ps.setInt(1, videoId);
            rs = ps.executeQuery();

            if (rs.next()) {
                int lessonItemId = rs.getInt("LessonItemID");
                rs.close();
                ps.close();

                // Now check if this lesson item is completed
                String progressSql = "SELECT * FROM LessonItemProgress WHERE CustomerID = ? AND LessonItemID = ? AND IsCompleted = 1";
                ps = conn.prepareStatement(progressSql);
                ps.setInt(1, customerId);
                ps.setInt(2, lessonItemId);
                rs = ps.executeQuery();

                if (rs.next()) {
                    completed = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return completed;
    }

    /**
     * Check if a material has been completed by a user
     *
     * @param customerId The customer ID
     * @param materialId The material ID
     * @return true if completed, false otherwise
     */
    public boolean isMaterialCompleted(int customerId, int materialId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean completed = false;

        try {
            conn = getConnection();

            // First get the LessonItemID for this material
            String itemSql = "SELECT LessonItemID FROM LessonItems WHERE ItemType = 'material' AND ItemID = ?";
            ps = conn.prepareStatement(itemSql);
            ps.setInt(1, materialId);
            rs = ps.executeQuery();

            if (rs.next()) {
                int lessonItemId = rs.getInt("LessonItemID");
                rs.close();
                ps.close();

                // Now check if this lesson item is completed
                String progressSql = "SELECT * FROM LessonItemProgress WHERE CustomerID = ? AND LessonItemID = ? AND IsCompleted = 1";
                ps = conn.prepareStatement(progressSql);
                ps.setInt(1, customerId);
                ps.setInt(2, lessonItemId);
                rs = ps.executeQuery();

                if (rs.next()) {
                    completed = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return completed;
    }

    /**
     * Mark a video as completed for a user
     *
     * @param customerId  The customer ID
     * @param videoId The video ID
     * @return true if successful, false otherwise
     */
    public boolean markVideoCompleted(int customerId, int videoId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;

        try {
            conn = getConnection();

            // First get the LessonItemID for this video
            String itemSql = "SELECT LessonItemID FROM LessonItems WHERE ItemType = 'video' AND ItemID = ?";
            ps = conn.prepareStatement(itemSql);
            ps.setInt(1, videoId);
            rs = ps.executeQuery();

            if (rs.next()) {
                int lessonItemId = rs.getInt("LessonItemID");
                rs.close();
                ps.close();

                // Now check if a progress record already exists
                String checkSql = "SELECT * FROM LessonItemProgress WHERE CustomerID = ? AND LessonItemID = ?";
                ps = conn.prepareStatement(checkSql);
                ps.setInt(1, customerId);
                ps.setInt(2, lessonItemId);
                rs = ps.executeQuery();

                if (rs.next()) {
                    // Update existing record
                    ps.close();
                    String updateSql = "UPDATE LessonItemProgress SET IsCompleted = 1, CompletionDate = ? WHERE CustomerID = ? AND LessonItemID = ?";
                    ps = conn.prepareStatement(updateSql);
                    ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                    ps.setInt(2, customerId);
                    ps.setInt(3, lessonItemId);
                    int affectedRows = ps.executeUpdate();
                    success = (affectedRows > 0);
                } else {
                    // Insert new record
                    ps.close();
                    String insertSql = "INSERT INTO LessonItemProgress (CustomerID, LessonItemID, IsCompleted, CompletionDate, LastAccessDate) VALUES (?, ?, 1, ?, ?)";
                    ps = conn.prepareStatement(insertSql);
                    ps.setInt(1, customerId);
                    ps.setInt(2, lessonItemId);
                    ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                    ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                    int affectedRows = ps.executeUpdate();
                    success = (affectedRows > 0);
                }

                // Get LessonID for this LessonItemID to update lesson progress
                if (success) {
                    ps.close();
                    String lessonSql = "SELECT LessonID FROM LessonItems WHERE LessonItemID = ?";
                    ps = conn.prepareStatement(lessonSql);
                    ps.setInt(1, lessonItemId);
                    rs = ps.executeQuery();

                    if (rs.next()) {
                        int lessonId = rs.getInt("LessonID");
                        checkAndUpdateLessonProgress(conn, customerId, lessonId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return success;
    }

    /**
     * Mark a material as completed for a user
     *
     * @param customerId  The customer ID
     * @param materialId The material ID
     * @return true if successful, false otherwise
     */
    public boolean markMaterialCompleted(int customerId, int materialId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;

        try {
            conn = getConnection();

            // First get the LessonItemID for this material
            String itemSql = "SELECT LessonItemID FROM LessonItems WHERE ItemType = 'material' AND ItemID = ?";
            ps = conn.prepareStatement(itemSql);
            ps.setInt(1, materialId);
            rs = ps.executeQuery();

            if (rs.next()) {
                int lessonItemId = rs.getInt("LessonItemID");
                rs.close();
                ps.close();

                // Now check if a progress record already exists
                String checkSql = "SELECT * FROM LessonItemProgress WHERE CustomerID = ? AND LessonItemID = ?";
                ps = conn.prepareStatement(checkSql);
                ps.setInt(1, customerId);
                ps.setInt(2, lessonItemId);
                rs = ps.executeQuery();

                if (rs.next()) {
                    // Update existing record
                    ps.close();
                    String updateSql = "UPDATE LessonItemProgress SET IsCompleted = 1, CompletionDate = ? WHERE CustomerID = ? AND LessonItemID = ?";
                    ps = conn.prepareStatement(updateSql);
                    ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                    ps.setInt(2, customerId);
                    ps.setInt(3, lessonItemId);
                    int affectedRows = ps.executeUpdate();
                    success = (affectedRows > 0);
                } else {
                    // Insert new record
                    ps.close();
                    String insertSql = "INSERT INTO LessonItemProgress (CustomerID, LessonItemID, IsCompleted, CompletionDate, LastAccessDate) VALUES (?, ?, 1, ?, ?)";
                    ps = conn.prepareStatement(insertSql);
                    ps.setInt(1, customerId);
                    ps.setInt(2, lessonItemId);
                    ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                    ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                    int affectedRows = ps.executeUpdate();
                    success = (affectedRows > 0);
                }

                // Get LessonID for this LessonItemID to update lesson progress
                if (success) {
                    ps.close();
                    String lessonSql = "SELECT LessonID FROM LessonItems WHERE LessonItemID = ?";
                    ps = conn.prepareStatement(lessonSql);
                    ps.setInt(1, lessonItemId);
                    rs = ps.executeQuery();

                    if (rs.next()) {
                        int lessonId = rs.getInt("LessonID");
                        checkAndUpdateLessonProgress(conn, customerId, lessonId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return success;
    }

    /**
     * Check if a quiz has been completed by a user
     *
     * @param customerId The customer ID
     * @param quizId The quiz ID
     * @return true if completed, false otherwise
     */
    public boolean isQuizCompleted(int customerId, int quizId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean completed = false;

        try {
            conn = getConnection();

            // First get the LessonItemID for this quiz
            String itemSql = "SELECT LessonItemID FROM LessonItems WHERE ItemType = 'quiz' AND ItemID = ?";
            ps = conn.prepareStatement(itemSql);
            ps.setInt(1, quizId);
            rs = ps.executeQuery();

            if (rs.next()) {
                int lessonItemId = rs.getInt("LessonItemID");
                rs.close();
                ps.close();

                // Now check if this lesson item is completed
                String progressSql = "SELECT * FROM LessonItemProgress WHERE CustomerID = ? AND LessonItemID = ? AND IsCompleted = 1";
                ps = conn.prepareStatement(progressSql);
                ps.setInt(1, customerId);
                ps.setInt(2, lessonItemId);
                rs = ps.executeQuery();

                if (rs.next()) {
                    completed = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return completed;
    }

    /**
     * Mark a quiz as completed for a user
     *
     * @param customerId The customer ID
     * @param quizId The quiz ID
     * @param score  The score achieved
     * @return true if successful, false otherwise
     */
    public boolean markQuizCompleted(int customerId, int quizId, int score) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;

        try {
            conn = getConnection();

            // First get the LessonItemID for this quiz
            String itemSql = "SELECT LessonItemID FROM LessonItems WHERE ItemType = 'quiz' AND ItemID = ?";
            ps = conn.prepareStatement(itemSql);
            ps.setInt(1, quizId);
            rs = ps.executeQuery();

            if (rs.next()) {
                int lessonItemId = rs.getInt("LessonItemID");
                rs.close();
                ps.close();

                // Now check if a progress record already exists
                String checkSql = "SELECT * FROM LessonItemProgress WHERE CustomerID = ? AND LessonItemID = ?";
                ps = conn.prepareStatement(checkSql);
                ps.setInt(1, customerId);
                ps.setInt(2, lessonItemId);
                rs = ps.executeQuery();

                if (rs.next()) {
                    // Update existing record
                    ps.close();
                    String updateSql = "UPDATE LessonItemProgress SET IsCompleted = 1, CompletionDate = ? WHERE CustomerID = ? AND LessonItemID = ?";
                    ps = conn.prepareStatement(updateSql);
                    ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                    ps.setInt(2, customerId);
                    ps.setInt(3, lessonItemId);
                    int affectedRows = ps.executeUpdate();
                    success = (affectedRows > 0);
                } else {
                    // Insert new record
                    ps.close();
                    String insertSql = "INSERT INTO LessonItemProgress (CustomerID, LessonItemID, IsCompleted, CompletionDate, LastAccessDate) VALUES (?, ?, 1, ?, ?)";
                    ps = conn.prepareStatement(insertSql);
                    ps.setInt(1, customerId);
                    ps.setInt(2, lessonItemId);
                    ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                    ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                    int affectedRows = ps.executeUpdate();
                    success = (affectedRows > 0);
                }

                // Get LessonID for this LessonItemID to update lesson progress
                if (success) {
                    ps.close();
                    String lessonSql = "SELECT LessonID FROM LessonItems WHERE LessonItemID = ?";
                    ps = conn.prepareStatement(lessonSql);
                    ps.setInt(1, lessonItemId);
                    rs = ps.executeQuery();

                    if (rs.next()) {
                        int lessonId = rs.getInt("LessonID");
                        checkAndUpdateLessonProgress(conn, customerId, lessonId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return success;
    }

    /**
     * Helper method to check and update lesson progress
     */
    private void checkAndUpdateLessonProgress(Connection conn, int customerId, int lessonId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Check if all LessonItems for this lesson are completed
            String allItemsCompletedSql = "SELECT "
                    + "   COUNT(*) AS TotalItems, "
                    + "   SUM(CASE WHEN lip.IsCompleted = 1 THEN 1 ELSE 0 END) AS CompletedItems "
                    + "FROM LessonItems li "
                    + "LEFT JOIN LessonItemProgress lip ON li.LessonItemID = lip.LessonItemID AND lip.CustomerID = ? "
                    + "WHERE li.LessonID = ?";

            ps = conn.prepareStatement(allItemsCompletedSql);
            ps.setInt(1, customerId);
            ps.setInt(2, lessonId);
            rs = ps.executeQuery();

            if (rs.next()) {
                int totalItems = rs.getInt("TotalItems");
                int completedItems = rs.getInt("CompletedItems");

                if (totalItems > 0 && completedItems == totalItems) {
                    // All items are completed, update LessonProgress
                    rs.close();
                    ps.close();

                    String checkLessonSql = "SELECT * FROM LessonProgress WHERE CustomerID = ? AND LessonID = ?";
                    ps = conn.prepareStatement(checkLessonSql);
                    ps.setInt(1, customerId);
                    ps.setInt(2, lessonId);
                    rs = ps.executeQuery();

                    Timestamp now = new Timestamp(System.currentTimeMillis());

                    if (rs.next()) {
                        // Update existing lesson progress
                        ps.close();
                        String updateLessonSql = "UPDATE LessonProgress SET IsCompleted = 1, CompletionDate = ?, LastAccessDate = ? WHERE CustomerID = ? AND LessonID = ?";
                        ps = conn.prepareStatement(updateLessonSql);
                        ps.setTimestamp(1, now);
                        ps.setTimestamp(2, now);
                        ps.setInt(3, customerId);
                        ps.setInt(4, lessonId);
                        ps.executeUpdate();
                    } else {
                        // Insert new lesson progress
                        ps.close();
                        String insertLessonSql = "INSERT INTO LessonProgress (CustomerID, LessonID, IsCompleted, CompletionDate, LastAccessDate) VALUES (?, ?, 1, ?, ?)";
                        ps = conn.prepareStatement(insertLessonSql);
                        ps.setInt(1, customerId);
                        ps.setInt(2, lessonId);
                        ps.setTimestamp(3, now);
                        ps.setTimestamp(4, now);
                        ps.executeUpdate();
                    }

                    // Now get the CourseID to update course progress
                    ps.close();
                    String courseSql = "SELECT CourseID FROM Lessons WHERE LessonID = ?";
                    ps = conn.prepareStatement(courseSql);
                    ps.setInt(1, lessonId);
                    rs = ps.executeQuery();

                    if (rs.next()) {
                        int courseId = rs.getInt("CourseID");
                        // Since we're already in a transaction, don't start another one
                        // Just call updateCourseCompletion directly with the current connection
                        updateCourseCompletionWithConnection(conn, customerId, courseId);
                    }
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
    }

    /**
     * Calculate and update course completion percentage using an existing
     * connection. This method is for internal use within transactions.
     *
     * @param conn       The database connection
     * @param customerId The customer ID
     * @param courseId   The course ID
     * @throws SQLException If a database error occurs
     */
    private void updateCourseCompletionWithConnection(Connection conn, int customerId, int courseId)
            throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Get total number of lessons in the course
            String totalLessonsSql = "SELECT COUNT(*) AS TotalLessons FROM Lessons WHERE CourseID = ?";
            ps = conn.prepareStatement(totalLessonsSql);
            ps.setInt(1, courseId);
            rs = ps.executeQuery();

            int totalLessons = 0;
            if (rs.next()) {
                totalLessons = rs.getInt("TotalLessons");
            }

            if (totalLessons == 0) {
                return;
            }

            // Get number of completed lessons
            ps.close();
            rs.close();
            String completedLessonsSql = "SELECT COUNT(*) AS CompletedLessons FROM LessonProgress LP "
                    + "JOIN Lessons L ON LP.LessonID = L.LessonID "
                    + "WHERE LP.CustomerID = ? AND L.CourseID = ? AND LP.IsCompleted = 1";
            ps = conn.prepareStatement(completedLessonsSql);
            ps.setInt(1, customerId);
            ps.setInt(2, courseId);
            rs = ps.executeQuery();

            int completedLessons = 0;
            if (rs.next()) {
                completedLessons = rs.getInt("CompletedLessons");
            }

            // Calculate completion percentage using BigDecimal
            BigDecimal completionPercentage = BigDecimal.ZERO;
            if (totalLessons > 0) {
                completionPercentage = new BigDecimal(completedLessons)
                        .multiply(new BigDecimal(100))
                        .divide(new BigDecimal(totalLessons), 2, BigDecimal.ROUND_HALF_UP);
            }

            boolean isCompleted = (completedLessons == totalLessons && totalLessons > 0);
            Timestamp now = new Timestamp(System.currentTimeMillis());

            // Check if there's an existing course progress record
            ps.close();
            rs.close();
            String checkProgressSql = "SELECT ProgressID FROM CourseProgress WHERE CustomerID = ? AND CourseID = ?";
            ps = conn.prepareStatement(checkProgressSql);
            ps.setInt(1, customerId);
            ps.setInt(2, courseId);
            rs = ps.executeQuery();

            if (rs.next()) {
                // Update existing record
                ps.close();
                String updateSql = "UPDATE CourseProgress SET CompletionPercentage = ?, IsCompleted = ?, LastAccessDate = ? WHERE CustomerID = ? AND CourseID = ?";
                ps = conn.prepareStatement(updateSql);
                ps.setBigDecimal(1, completionPercentage);
                ps.setBoolean(2, isCompleted);
                ps.setTimestamp(3, now);
                ps.setInt(4, customerId);
                ps.setInt(5, courseId);
                ps.executeUpdate();
            } else {
                // Insert new record
                ps.close();
                String insertSql = "INSERT INTO CourseProgress (CustomerID, CourseID, CompletionPercentage, IsCompleted, LastAccessDate) VALUES (?, ?, ?, ?, ?)";
                ps = conn.prepareStatement(insertSql);
                ps.setInt(1, customerId);
                ps.setInt(2, courseId);
                ps.setBigDecimal(3, completionPercentage);
                ps.setBoolean(4, isCompleted);
                ps.setTimestamp(5, now);
                ps.executeUpdate();
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
    }

    /**
     * Update the course progress for a user based on their completed lessons.
     *
     * @param userId   The user ID
     * @param courseId The course ID
     * @return True if successful, false otherwise
     */
    public boolean updateProgress(int customerId, int courseId) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = getConnection();

            // Calculate completion percentage based on completed lessons
            double completionPercentage = calculateCompletionPercentage(customerId, courseId);
            boolean isCompleted = (completionPercentage >= 100);

            // Check if a progress record exists
            String checkSql = "SELECT * FROM CourseProgress WHERE CustomerID = ? AND CourseID = ?";
            ps = conn.prepareStatement(checkSql);
            ps.setInt(1, customerId);
            ps.setInt(2, courseId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Update existing record
                rs.close();
                ps.close();

                String updateSql = "UPDATE CourseProgress SET CompletionPercentage = ?, IsCompleted = ?, LastAccessDate = GETDATE() WHERE CustomerID = ? AND CourseID = ?";
                ps = conn.prepareStatement(updateSql);
                ps.setBigDecimal(1, new java.math.BigDecimal(completionPercentage));
                ps.setBoolean(2, isCompleted);
                ps.setInt(3, customerId);
                ps.setInt(4, courseId);
                success = ps.executeUpdate() > 0;
            } else {
                // Insert new record
                rs.close();
                ps.close();

                String insertSql = "INSERT INTO CourseProgress (CustomerID, CourseID, CompletionPercentage, IsCompleted, LastAccessDate) VALUES (?, ?, ?, ?, GETDATE())";
                ps = conn.prepareStatement(insertSql);
                ps.setInt(1, customerId);
                ps.setInt(2, courseId);
                ps.setBigDecimal(3, new java.math.BigDecimal(completionPercentage));
                ps.setBoolean(4, isCompleted);
                success = ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
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

        return success;
    }

    /**
     * Calculate the completion percentage for a course based on completed
     * lessons.
     *
     * @param userId   The user ID
     * @param courseId The course ID
     * @return The completion percentage (0-100)
     */
    private double calculateCompletionPercentage(int customerId, int courseId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        double percentage = 0.0;

        try {
            conn = getConnection();

            // Count total lessons in course
            String totalSql = "SELECT COUNT(*) AS TotalLessons FROM Lessons WHERE CourseID = ?";
            ps = conn.prepareStatement(totalSql);
            ps.setInt(1, courseId);
            rs = ps.executeQuery();

            int totalLessons = 0;
            if (rs.next()) {
                totalLessons = rs.getInt("TotalLessons");
            }

            rs.close();
            ps.close();

            if (totalLessons == 0) {
                return 0.0; // No lessons in course
            }

            // Count completed lessons
            String completedSql = "SELECT COUNT(*) AS CompletedLessons FROM Lessons l "
                    + "JOIN LessonProgress lp ON l.LessonID = lp.LessonID "
                    + "WHERE l.CourseID = ? AND lp.CustomerID = ? AND lp.IsCompleted = 1";

            ps = conn.prepareStatement(completedSql);
            ps.setInt(1, courseId);
            ps.setInt(2, customerId);
            rs = ps.executeQuery();

            int completedLessons = 0;
            if (rs.next()) {
                completedLessons = rs.getInt("CompletedLessons");
            }

            // Calculate percentage
            percentage = (completedLessons * 100.0) / totalLessons;

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

        return percentage;
    }

}
