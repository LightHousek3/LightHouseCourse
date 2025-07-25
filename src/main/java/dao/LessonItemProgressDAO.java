package dao;

import db.DBContext;
import model.LessonItemProgress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

/**
 * Data Access Object for LessonItemProgress
 */
public class LessonItemProgressDAO extends DBContext {

    /**
     * Get a LessonItemProgress by ID
     *
     * @param progressId The progress ID
     * @return The LessonItemProgress, or null if not found
     */
    public LessonItemProgress getLessonItemProgressById(int progressId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        LessonItemProgress progress = null;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM LessonItemProgress WHERE ProgressID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, progressId);
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
     * Get progress by user ID and lesson item ID
     *
     * @param customerId   The customer ID
     * @param lessonItemId The lesson item ID
     * @return The LessonItemProgress, or null if not found
     */
    public LessonItemProgress getLessonItemProgressByUserAndLessonItem(int customerId, int lessonItemId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        LessonItemProgress progress = null;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM LessonItemProgress WHERE CustomerID = ? AND LessonItemID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);
            ps.setInt(2, lessonItemId);
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
     * Get all progress records for a user
     *
     * @param customerId The customer ID
     * @return List of LessonItemProgress for the user
     */
    public List<LessonItemProgress> getAllLessonItemProgressByUser(int customerId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<LessonItemProgress> progressList = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT * FROM LessonItemProgress WHERE CustomerID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);
            rs = ps.executeQuery();

            while (rs.next()) {
                progressList.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return progressList;
    }

    /**
     * Get all progress records for a lesson
     *
     * @param customerId The customer ID
     * @param lessonId   The lesson ID
     * @return List of LessonItemProgress for the lesson
     */
    public List<LessonItemProgress> getByUserAndLesson(int customerId, int lessonId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<LessonItemProgress> progressList = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT lip.* FROM LessonItemProgress lip "
                    + "JOIN LessonItems li ON lip.LessonItemID = li.LessonItemID "
                    + "WHERE lip.CustomerID = ? AND li.LessonID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);
            ps.setInt(2, lessonId);
            rs = ps.executeQuery();

            while (rs.next()) {
                progressList.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return progressList;
    }

    /**
     * Save a LessonItemProgress (insertLessonItemProgress or
     * updateLessonItemProgress)
     *
     * @param progress The LessonItemProgress to saveLessonItemProgress
     * @return true if successful, false otherwise
     */
    public boolean saveLessonItemProgress(LessonItemProgress progress) {
        if (progress.getProgressID() > 0) {
            return updateLessonItemProgress(progress);
        } else {
            return insertLessonItemProgress(progress) > 0;
        }
    }

    /**
     * Insert a new LessonItemProgress
     *
     * @param progress The LessonItemProgress to insertLessonItemProgress
     * @return The new progress ID, or -1 if failed
     */
    private int insertLessonItemProgress(LessonItemProgress progress) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int progressId = -1;

        try {
            conn = getConnection();
            String sql = "INSERT INTO LessonItemProgress (CustomerID, LessonItemID, IsCompleted, CompletionDate, LastAccessDate) "
                    + "VALUES (?, ?, ?, ?, ?)";

            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, progress.getCustomerID());
            ps.setInt(2, progress.getLessonItemID());
            ps.setBoolean(3, progress.isCompleted());
            ps.setTimestamp(4, progress.getCompletionDate());
            ps.setTimestamp(5, progress.getLastAccessDate());

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
     * Update an existing LessonItemProgress
     *
     * @param progress The LessonItemProgress to updateLessonItemProgress
     * @return true if successful, false otherwise
     */
    private boolean updateLessonItemProgress(LessonItemProgress progress) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = getConnection();
            String sql = "UPDATE LessonItemProgress SET CustomerID = ?, LessonItemID = ?, IsCompleted = ?, "
                    + "CompletionDate = ?, LastAccessDate = ? WHERE ProgressID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, progress.getCustomerID());
            ps.setInt(2, progress.getLessonItemID());
            ps.setBoolean(3, progress.isCompleted());
            ps.setTimestamp(4, progress.getCompletionDate());
            ps.setTimestamp(5, progress.getLastAccessDate());
            ps.setInt(6, progress.getProgressID());

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
     * Mark a lesson item as completed for a user.
     *
     * @param customerId   The customer ID
     * @param lessonItemId The lesson item ID
     * @return True if successful, false otherwise
     */
    public boolean markItemAsCompleted(int customerId, int lessonItemId) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = getConnection();

            // Check if a progress record already exists
            String checkSql = "SELECT ProgressID FROM LessonItemProgress WHERE CustomerID = ? AND LessonItemID = ?";
            ps = conn.prepareStatement(checkSql);
            ps.setInt(1, customerId);
            ps.setInt(2, lessonItemId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Update existing record
                int progressId = rs.getInt("ProgressID");
                rs.close();
                ps.close();

                String updateSql = "UPDATE LessonItemProgress SET IsCompleted = 1, CompletionDate = GETDATE(), LastAccessDate = GETDATE() WHERE ProgressID = ?";
                ps = conn.prepareStatement(updateSql);
                ps.setInt(1, progressId);
                success = ps.executeUpdate() > 0;
            } else {
                // Insert new record
                rs.close();
                ps.close();
                String insertSql = "INSERT INTO LessonItemProgress (CustomerID, LessonItemID, IsCompleted, CompletionDate, LastAccessDate) VALUES (?, ?, 1, GETDATE(), GETDATE())";
                ps = conn.prepareStatement(insertSql);
                ps.setInt(1, customerId);
                ps.setInt(2, lessonItemId);
                success = ps.executeUpdate() > 0;
            }

            // If successful, trigger the LessonProgress updateLessonItemProgress through
            // the database trigger
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(null, ps, conn);
        }

        return success;
    }

    /**
     * Update last access time for a lesson item
     *
     * @param customerId   The customer ID
     * @param lessonItemId The lesson item ID
     * @return true if successful, false otherwise
     */
    public boolean updateLastAccess(int customerId, int lessonItemId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;

        try {
            conn = getConnection();

            // Check if entry exists
            String checkSql = "SELECT * FROM LessonItemProgress WHERE CustomerID = ? AND LessonItemID = ?";
            ps = conn.prepareStatement(checkSql);
            ps.setInt(1, customerId);
            ps.setInt(2, lessonItemId);
            rs = ps.executeQuery();

            Timestamp now = new Timestamp(System.currentTimeMillis());

            if (rs.next()) {
                // Update existing entry
                ps.close();
                String updateSql = "UPDATE LessonItemProgress SET LastAccessDate = ? WHERE CustomerID = ? AND LessonItemID = ?";
                ps = conn.prepareStatement(updateSql);
                ps.setTimestamp(1, now);
                ps.setInt(2, customerId);
                ps.setInt(3, lessonItemId);
            } else {
                // Insert new entry
                ps.close();
                String insertSql = "INSERT INTO LessonItemProgress (CustomerID, LessonItemID, IsCompleted, LastAccessDate) "
                        + "VALUES (?, ?, 0, ?)";
                ps = conn.prepareStatement(insertSql);
                ps.setInt(1, customerId);
                ps.setInt(2, lessonItemId);
                ps.setTimestamp(3, now);
            }

            int affectedRows = ps.executeUpdate();
            success = (affectedRows > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return success;
    }

    /**
     * Delete a progress record
     *
     * @param progressId The progress ID to deleteLessonItemProgress
     * @return true if successful, false otherwise
     */
    public boolean deleteLessonItemProgress(int progressId) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = getConnection();
            String sql = "DELETE FROM LessonItemProgress WHERE ProgressID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, progressId);

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
     * Map a ResultSet row to a LessonItemProgress object
     *
     * @param rs The ResultSet to map
     * @return A LessonItemProgress object
     * @throws SQLException If a database error occurs
     */
    private LessonItemProgress mapRow(ResultSet rs) throws SQLException {
        LessonItemProgress progress = new LessonItemProgress();
        progress.setProgressID(rs.getInt("ProgressID"));
        progress.setCustomerID(rs.getInt("CustomerID"));
        progress.setLessonItemID(rs.getInt("LessonItemID"));
        progress.setCompleted(rs.getBoolean("IsCompleted"));
        progress.setCompletionDate(rs.getTimestamp("CompletionDate"));
        progress.setLastAccessDate(rs.getTimestamp("LastAccessDate"));
        return progress;
    }

    /**
     * Check if a lesson item is completed by a user.
     *
     * @param customerId   The customer ID
     * @param lessonItemId The lesson item ID
     * @return true if completed, false otherwise
     */
    public boolean isItemCompleted(int customerId, int lessonItemId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean completed = false;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM LessonItemProgress WHERE CustomerID = ? AND LessonItemID = ? AND IsCompleted = 1";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);
            ps.setInt(2, lessonItemId);
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
     * Mark a lesson item as complete by a user.
     * This method handles the entire workflow:
     * 1. Mark the lesson item as complete
     * 2. Update the lesson progress
     * 3. Update the course progress
     *
     * @param customerId   The customer ID
     * @param lessonItemId The lesson item ID
     * @return true if successful, false otherwise
     */
    public boolean markItemComplete(int customerId, int lessonItemId) {
        Connection conn = null;
        boolean success = false;

        try {
            // Get connection and start transaction
            conn = getConnection();
            conn.setAutoCommit(false);

            // Step 1: Mark lesson item as complete
            success = markLessonItemAsComplete(conn, customerId, lessonItemId);
            if (!success) {
                // If marking item complete fails, rollback and return
                if (!conn.isClosed()) {
                    conn.rollback();
                }
                return false;
            }

            // Step 2: Get the lesson ID for this lesson item
            int lessonId = getLessonIdForLessonItem(conn, lessonItemId);
            if (lessonId <= 0) {
                // If can't get lesson ID, rollback and return
                if (!conn.isClosed()) {
                    conn.rollback();
                }
                return false;
            }

            // Step 3: Update lesson progress
            success = updateLessonProgress(conn, customerId, lessonId);
            if (!success) {
                // If updating lesson progress fails, rollback and return
                if (!conn.isClosed()) {
                    conn.rollback();
                }
                return false;
            }

            // Step 4: Get course ID for this lesson
            int courseId = getCourseIdForLesson(conn, lessonId);
            if (courseId <= 0) {
                // If can't get course ID, rollback and return
                if (!conn.isClosed()) {
                    conn.rollback();
                }
                return false;
            }

            // Step 5: Update course progress
            success = updateCourseProgress(conn, customerId, lessonId, courseId);
            if (!success) {
                // If updating course progress fails, rollback and return
                System.out.println("Updating course progress fails.");
                if (!conn.isClosed()) {
                    conn.rollback();
                }
                return false;
            }

            // All operations successful, commit the transaction
            if (!conn.isClosed()) {
                conn.commit();
            }

            return true;

        } catch (SQLException e) {
            // Exception occurred, rollback transaction
            success = false;
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            // Always reset auto-commit and close connection
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Get lesson ID for a lesson item
     * 
     * @param conn         Database connection
     * @param lessonItemId The lesson item ID
     * @return The lesson ID, or -1 if not found or error
     */
    private int getLessonIdForLessonItem(Connection conn, int lessonItemId) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int lessonId = -1;

        try {
            String sql = "SELECT LessonID FROM LessonItems WHERE LessonItemID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, lessonItemId);
            rs = ps.executeQuery();

            if (rs.next()) {
                lessonId = rs.getInt("LessonID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
        }

        return lessonId;
    }

    /**
     * Get course ID for a lesson
     * 
     * @param conn     Database connection
     * @param lessonId The lesson ID
     * @return The course ID, or -1 if not found or error
     */
    private int getCourseIdForLesson(Connection conn, int lessonId) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int courseId = -1;

        try {
            String sql = "SELECT CourseID FROM Lessons WHERE LessonID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, lessonId);
            rs = ps.executeQuery();

            if (rs.next()) {
                courseId = rs.getInt("CourseID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
        }

        return courseId;
    }

    /**
     * Mark a specific lesson item as complete
     * 
     * @param conn         Database connection
     * @param customerId   Customer ID
     * @param lessonItemId Lesson item ID
     * @return true if successful, false otherwise
     */
    private boolean markLessonItemAsComplete(Connection conn, int customerId, int lessonItemId) {
        PreparedStatement checkPs = null;
        ResultSet rs = null;
        PreparedStatement actionPs = null;
        boolean result = false;

        try {
            // Check if record exists
            String checkSql = "SELECT CustomerID FROM LessonItemProgress WHERE CustomerID = ? AND LessonItemID = ?";
            checkPs = conn.prepareStatement(checkSql);
            checkPs.setInt(1, customerId);
            checkPs.setInt(2, lessonItemId);
            rs = checkPs.executeQuery();

            Timestamp now = new Timestamp(System.currentTimeMillis());

            if (rs.next()) {
                // Update existing record
                String updateSql = "UPDATE LessonItemProgress SET IsCompleted = 1, CompletionDate = ?, LastAccessDate = ? "
                        + "WHERE CustomerID = ? AND LessonItemID = ?";
                actionPs = conn.prepareStatement(updateSql);
                actionPs.setTimestamp(1, now);
                actionPs.setTimestamp(2, now);
                actionPs.setInt(3, customerId);
                actionPs.setInt(4, lessonItemId);
                result = actionPs.executeUpdate() > 0;
            } else {
                // Insert new record
                String insertSql = "INSERT INTO LessonItemProgress (CustomerID, LessonItemID, IsCompleted, CompletionDate, LastAccessDate) "
                        + "VALUES (?, ?, 1, ?, ?)";
                actionPs = conn.prepareStatement(insertSql);
                actionPs.setInt(1, customerId);
                actionPs.setInt(2, lessonItemId);
                actionPs.setTimestamp(3, now);
                actionPs.setTimestamp(4, now);
                result = actionPs.executeUpdate() > 0;
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeQuietly(rs);
            closeQuietly(checkPs);
            closeQuietly(actionPs);
        }
    }

    /**
     * Update lesson progress based on completed items
     * 
     * @param conn       Database connection
     * @param customerId Customer ID
     * @param lessonId   Lesson ID
     * @return true if successful, false otherwise
     */
    private boolean updateLessonProgress(Connection conn, int customerId, int lessonId) {
        try {
            // Get total items count for this lesson
            int totalItems = getTotalLessonItems(conn, lessonId);
            if (totalItems == 0) {
                return true; // No items to track progress for
            }

            // Get completed items count for this lesson and customer
            int completedItems = getCompletedLessonItems(conn, customerId, lessonId);

            // Calculate progress
            BigDecimal completionPercentage = calculatePercentage(completedItems, totalItems);
            boolean isLessonCompleted = (completedItems == totalItems);

            // Update lesson progress in database
            return updateOrInsertLessonProgress(conn, customerId, lessonId,
                    completionPercentage, isLessonCompleted);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get total number of items in a lesson
     */
    private int getTotalLessonItems(Connection conn, int lessonId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int totalItems = 0;

        try {
            String sql = "SELECT COUNT(*) as TotalItems FROM LessonItems WHERE LessonID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, lessonId);
            rs = ps.executeQuery();

            if (rs.next()) {
                totalItems = rs.getInt("TotalItems");
            }
            return totalItems;
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
        }
    }

    /**
     * Get number of completed items for a customer in a lesson
     */
    private int getCompletedLessonItems(Connection conn, int customerId, int lessonId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int completedItems = 0;

        try {
            String sql = "SELECT COUNT(*) as CompletedItems FROM LessonItemProgress lip "
                    + "INNER JOIN LessonItems li ON lip.LessonItemID = li.LessonItemID "
                    + "WHERE li.LessonID = ? AND lip.CustomerID = ? AND lip.IsCompleted = 1";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, lessonId);
            ps.setInt(2, customerId);
            rs = ps.executeQuery();

            if (rs.next()) {
                completedItems = rs.getInt("CompletedItems");
            }
            return completedItems;
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
        }
    }

    /**
     * Calculate percentage from completed and total items
     */
    private BigDecimal calculatePercentage(int completed, int total) {
        if (total == 0)
            return BigDecimal.ZERO;
        return new BigDecimal(completed)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Update or insert lesson progress record
     */
    private boolean updateOrInsertLessonProgress(Connection conn, int customerId, int lessonId,
            BigDecimal completionPercentage, boolean isCompleted) throws SQLException {

        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean recordExists = false;

        try {
            // Check if record exists
            String checkSql = "SELECT CustomerID FROM LessonProgress WHERE CustomerID = ? AND LessonID = ?";
            ps = conn.prepareStatement(checkSql);
            ps.setInt(1, customerId);
            ps.setInt(2, lessonId);
            rs = ps.executeQuery();
            recordExists = rs.next();

            closeQuietly(rs);
            closeQuietly(ps);
            rs = null;
            ps = null;

            Timestamp now = new Timestamp(System.currentTimeMillis());

            if (recordExists) {
                // Update existing record
                String updateSql = "UPDATE LessonProgress SET IsCompleted = ?, CompletionPercentage = ?, "
                        + "CompletionDate = CASE WHEN ? = 1 THEN ? ELSE CompletionDate END, "
                        + "LastAccessDate = ? WHERE CustomerID = ? AND LessonID = ?";
                ps = conn.prepareStatement(updateSql);
                ps.setBoolean(1, isCompleted);
                ps.setBigDecimal(2, completionPercentage);
                ps.setBoolean(3, isCompleted);
                ps.setTimestamp(4, now);
                ps.setTimestamp(5, now);
                ps.setInt(6, customerId);
                ps.setInt(7, lessonId);
                return ps.executeUpdate() > 0;
            } else {
                // Insert new record
                String insertSql = "INSERT INTO LessonProgress (CustomerID, LessonID, IsCompleted, CompletionPercentage, "
                        + "CompletionDate, LastAccessDate) VALUES (?, ?, ?, ?, ?, ?)";
                ps = conn.prepareStatement(insertSql);
                ps.setInt(1, customerId);
                ps.setInt(2, lessonId);
                ps.setBoolean(3, isCompleted);
                ps.setBigDecimal(4, completionPercentage);
                ps.setTimestamp(5, isCompleted ? now : null);
                ps.setTimestamp(6, now);
                return ps.executeUpdate() > 0;
            }
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
        }
    }

    /**
     * Update course progress based on completed lessons
     * 
     * @param conn       Database connection
     * @param customerId Customer ID
     * @param courseId   Course ID
     * @return true if successful, false otherwise
     */
    private boolean updateCourseProgress(Connection conn, int customerId, int lessonId,int courseId) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            // Get the total count of lesson items in the course
            String sqlTotalItems = "SELECT COUNT(*) FROM LessonItems li "
                    + "INNER JOIN Lessons l ON li.LessonID = l.LessonID "
                    + "WHERE l.CourseID = ?";

            ps = conn.prepareStatement(sqlTotalItems);
            ps.setInt(1, courseId);
            rs = ps.executeQuery();

            int totalLessonItems = 0;
            if (rs.next()) {
                totalLessonItems = rs.getInt(1);
            }
            rs.close();
            ps.close();

            if (totalLessonItems == 0) {
                return false; // No items to calculate progress
            }

            // Get the count of completed lesson items
            String sqlCompletedItems = "SELECT COUNT(*) FROM LessonItemProgress lip "
                    + "INNER JOIN LessonItems li ON lip.LessonItemID = li.LessonItemID "
                    + "INNER JOIN Lessons l ON li.LessonID = l.LessonID "
                    + "WHERE lip.CustomerID = ? AND l.CourseID = ? AND lip.IsCompleted = 1";

            ps = conn.prepareStatement(sqlCompletedItems);
            ps.setInt(1, customerId);
            ps.setInt(2, courseId);
            rs = ps.executeQuery();

            int completedLessonItems = 0;
            if (rs.next()) {
                completedLessonItems = rs.getInt(1);
            }
            rs.close();
            ps.close();
            BigDecimal completionPercentage = calculatePercentage(completedLessonItems, totalLessonItems);
            boolean isCourseCompleted = (completedLessonItems == totalLessonItems);
            
            // Update course progress in database
            return updateOrInsertCourseProgress(conn, customerId, courseId,
                    completionPercentage, isCourseCompleted);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get total number of lessons in a course
     */
    private int getTotalCourseLessons(Connection conn, int courseId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int totalLessons = 0;

        try {
            String sql = "SELECT COUNT(*) as TotalLessons FROM Lessons WHERE CourseID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, courseId);
            rs = ps.executeQuery();

            if (rs.next()) {
                totalLessons = rs.getInt("TotalLessons");
            }
            return totalLessons;
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
        }
    }

    /**
     * Get number of completed lessons for a customer in a course
     */
    private int getCompletedCourseLessons(Connection conn, int customerId, int courseId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int completedLessons = 0;

        try {
            String sql = "SELECT COUNT(*) as CompletedLessons FROM LessonProgress lp "
                    + "INNER JOIN Lessons l ON lp.LessonID = l.LessonID "
                    + "WHERE l.CourseID = ? AND lp.CustomerID = ? AND lp.IsCompleted = 1";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, courseId);
            ps.setInt(2, customerId);
            rs = ps.executeQuery();

            if (rs.next()) {
                completedLessons = rs.getInt("CompletedLessons");
            }
            return completedLessons;
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
        }
    }

    /**
     * Update or insert course progress record
     */
    private boolean updateOrInsertCourseProgress(Connection conn, int customerId, int courseId,
            BigDecimal completionPercentage, boolean isCompleted) throws SQLException {

        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean recordExists = false;

        try {
            // Check if record exists
            String checkSql = "SELECT ProgressID FROM CourseProgress WHERE CustomerID = ? AND CourseID = ?";
            ps = conn.prepareStatement(checkSql);
            ps.setInt(1, customerId);
            ps.setInt(2, courseId);
            rs = ps.executeQuery();
            recordExists = rs.next();

            closeQuietly(rs);
            closeQuietly(ps);
            rs = null;
            ps = null;

            Timestamp now = new Timestamp(System.currentTimeMillis());

            if (recordExists) {
                // Update existing record
                String updateSql = "UPDATE CourseProgress SET IsCompleted = ?, CompletionPercentage = ?, "
                        + "LastAccessDate = ? WHERE CustomerID = ? AND CourseID = ?";
                ps = conn.prepareStatement(updateSql);
                ps.setBoolean(1, isCompleted);
                ps.setBigDecimal(2, completionPercentage);
                ps.setTimestamp(3, now);
                ps.setInt(4, customerId);
                ps.setInt(5, courseId);
                return ps.executeUpdate() > 0;
            } else {
                // Insert new record
                String insertSql = "INSERT INTO CourseProgress (CustomerID, CourseID, IsCompleted, CompletionPercentage, "
                        + "LastAccessDate) VALUES (?, ?, ?, ?, ?)";
                ps = conn.prepareStatement(insertSql);
                ps.setInt(1, customerId);
                ps.setInt(2, courseId);
                ps.setBoolean(3, isCompleted);
                ps.setBigDecimal(4, completionPercentage);
                ps.setTimestamp(5, now);
                return ps.executeUpdate() > 0;
            }
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
        }
    }

    /**
     * Helper method to close ResultSet quietly
     */
    private void closeQuietly(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                // Ignore
            }
        }
    }

    /**
     * Helper method to close PreparedStatement quietly
     */
    private void closeQuietly(PreparedStatement ps) {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                // Ignore
            }
        }
    }
}
