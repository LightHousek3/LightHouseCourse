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
import model.CourseProgress;

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
     * Recalculate and update the course completion percentage based on completed
     * lesson items.
     * 
     * @param customerId The customer ID
     * @param courseId   The course ID
     * @return true if successful, false otherwise
     */
    public boolean recalculateProgress(int customerId, int courseId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;

        try {
            conn = getConnection();

            // Get the total count of lesson items in the course
            String sqlTotalItems = "SELECT COUNT(*) FROM LessonItems li " +
                    "JOIN Lessons l ON li.LessonID = l.LessonID " +
                    "WHERE l.CourseID = ?";
            ps = conn.prepareStatement(sqlTotalItems);
            ps.setInt(1, courseId);
            rs = ps.executeQuery();

            int totalItems = 0;
            if (rs.next()) {
                totalItems = rs.getInt(1);
            }

            if (totalItems == 0) {
                return false; // No items to calculate progress
            }

            // Get the count of completed lesson items
            String sqlCompletedItems = "SELECT COUNT(*) FROM LessonItemProgress lip " +
                    "JOIN LessonItems li ON lip.LessonItemID = li.LessonItemID " +
                    "JOIN Lessons l ON li.LessonID = l.LessonID " +
                    "WHERE lip.CustomerID = ? AND l.CourseID = ? AND lip.IsCompleted = 1";
            ps = conn.prepareStatement(sqlCompletedItems);
            ps.setInt(1, customerId);
            ps.setInt(2, courseId);
            rs = ps.executeQuery();

            int completedItems = 0;
            if (rs.next()) {
                completedItems = rs.getInt(1);
            }

            // Calculate completion percentage
            double completionPercentage = (double) completedItems / totalItems * 100;

            // Fix potential rounding issues - ensure 100% when all items are completed
            if (completedItems == totalItems) {
                completionPercentage = 100.0;
            }

            // Round to 2 decimal places to avoid floating point precision issues
            completionPercentage = Math.round(completionPercentage * 100) / 100.0;

            boolean isCompleted = (completedItems == totalItems);

            // Update course progress
            CourseProgress progress = getByCustomerAndCourse(customerId, courseId);

            if (progress == null) {
                // Create new progress record
                progress = new CourseProgress();
                progress.setCustomerID(customerId);
                progress.setCourseID(courseId);
                progress.setCompletionPercentage(BigDecimal.valueOf(completionPercentage));
                progress.setLastAccessDate(new Timestamp(System.currentTimeMillis()));
                progress.setCompleted(isCompleted);

                success = (insertCourseProgress(progress) > 0);
            } else {
                // Update existing progress record
                progress.setCompletionPercentage(BigDecimal.valueOf(completionPercentage));
                progress.setLastAccessDate(new Timestamp(System.currentTimeMillis()));
                progress.setCompleted(isCompleted);

                success = updateCourseProgress(progress);
            }

            // Update lesson completion status based on lesson items
            if (success) {
                updateLessonCompletionStatus(customerId, courseId, conn);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return success;
    }

    /**
     * Update lesson completion status based on completed lesson items.
     * 
     * @param customerId The customer ID
     * @param courseId   The course ID
     * @param conn       The database connection
     * @return true if successful, false otherwise
     */
    private boolean updateLessonCompletionStatus(int customerId, int courseId, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = true;

        try {
            // Get all lessons in the course
            String sqlLessons = "SELECT LessonID FROM Lessons WHERE CourseID = ?";
            ps = conn.prepareStatement(sqlLessons);
            ps.setInt(1, courseId);
            rs = ps.executeQuery();

            while (rs.next() && success) {
                int lessonId = rs.getInt("LessonID");

                // Check if all lesson items are completed
                String sqlCheckItems = "SELECT " +
                        "COUNT(*) AS totalItems, " +
                        "SUM(CASE WHEN lip.IsCompleted = 1 THEN 1 ELSE 0 END) AS completedItems " +
                        "FROM LessonItems li " +
                        "LEFT JOIN LessonItemProgress lip ON li.LessonItemID = lip.LessonItemID AND lip.CustomerID = ? "
                        +
                        "WHERE li.LessonID = ?";

                PreparedStatement psItems = conn.prepareStatement(sqlCheckItems);
                psItems.setInt(1, customerId);
                psItems.setInt(2, lessonId);
                ResultSet rsItems = psItems.executeQuery();

                if (rsItems.next()) {
                    int totalItems = rsItems.getInt("totalItems");
                    int completedItems = rsItems.getInt("completedItems");
                    boolean isLessonCompleted = (totalItems > 0 && completedItems == totalItems);

                    // Update lesson progress
                    String sqlUpdateLesson = "SELECT * FROM LessonProgress WHERE CustomerID = ? AND LessonID = ?";
                    PreparedStatement psCheckLesson = conn.prepareStatement(sqlUpdateLesson);
                    psCheckLesson.setInt(1, customerId);
                    psCheckLesson.setInt(2, lessonId);
                    ResultSet rsLesson = psCheckLesson.executeQuery();

                    if (rsLesson.next()) {
                        // Update existing record
                        String sqlUpdate = "UPDATE LessonProgress SET IsCompleted = ?, LastAccessDate = ? WHERE CustomerID = ? AND LessonID = ?";
                        PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate);
                        psUpdate.setBoolean(1, isLessonCompleted);
                        psUpdate.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                        psUpdate.setInt(3, customerId);
                        psUpdate.setInt(4, lessonId);
                        psUpdate.executeUpdate();
                        psUpdate.close();
                    } else {
                        // Insert new record
                        String sqlInsert = "INSERT INTO LessonProgress (CustomerID, LessonID, IsCompleted, LastAccessDate) VALUES (?, ?, ?, ?)";
                        PreparedStatement psInsert = conn.prepareStatement(sqlInsert);
                        psInsert.setInt(1, customerId);
                        psInsert.setInt(2, lessonId);
                        psInsert.setBoolean(3, isLessonCompleted);
                        psInsert.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                        psInsert.executeUpdate();
                        psInsert.close();
                    }

                    rsLesson.close();
                    psCheckLesson.close();
                }

                rsItems.close();
                psItems.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            success = false;
        } finally {
            closeResources(rs, ps, conn);
        }

        return success;
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
}
