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
     * @param customerId       The customer ID
     * @param lessonItemId The lesson item ID
     * @return The LessonItemProgress, or null if not found
     */
    public LessonItemProgress getByUserAndLessonItem(int customerId, int lessonItemId) {
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
     * @param customerId   The customer ID
     * @param lessonId The lesson ID
     * @return List of LessonItemProgress for the lesson
     */
    public List<LessonItemProgress> getByUserAndLesson(int customerId, int lessonId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<LessonItemProgress> progressList = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT lip.* FROM LessonItemProgress lip " +
                    "JOIN LessonItems li ON lip.LessonItemID = li.LessonItemID " +
                    "WHERE lip.CustomerID = ? AND li.LessonID = ?";

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
     * Save a LessonItemProgress (insertLessonItemProgress or updateLessonItemProgress)
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
                    +
                    "VALUES (?, ?, ?, ?, ?)";

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
            String sql = "UPDATE LessonItemProgress SET CustomerID = ?, LessonItemID = ?, IsCompleted = ?, " +
                    "CompletionDate = ?, LastAccessDate = ? WHERE ProgressID = ?";

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
     * @param customerId       The customer ID
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

            // If successful, trigger the LessonProgress updateLessonItemProgress through the database trigger
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
     * @param customerId       The customer ID
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
                        +
                        "VALUES (?, ?, 0, ?)";
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
     * @param customerId       The customer ID
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
     * 
     * @param customerId       The customer ID
     * @param lessonItemId The lesson item ID
     * @return true if successful, false otherwise
     */
    public boolean markItemComplete(int customerId, int lessonItemId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean success = false;

        try {
            conn = getConnection();

            // Check if a record already exists
            LessonItemProgress progress = getByUserAndLessonItem(customerId, lessonItemId);

            if (progress != null) {
                // Update existing record
                String sql = "UPDATE LessonItemProgress SET IsCompleted = 1, CompletionDate = ? WHERE CustomerID = ? AND LessonItemID = ?";

                ps = conn.prepareStatement(sql);
                ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                ps.setInt(2, customerId);
                ps.setInt(3, lessonItemId);

                int rowsAffected = ps.executeUpdate();
                success = (rowsAffected > 0);
            } else {
                // Create new record
                progress = new LessonItemProgress();
                progress.setCustomerID(customerId);
                progress.setLessonItemID(lessonItemId);
                progress.setCompleted(true);
                progress.setCompletionDate(new Timestamp(System.currentTimeMillis()));
                progress.setLastAccessDate(new Timestamp(System.currentTimeMillis()));

                success = (insertLessonItemProgress(progress) > 0);
            }

            // If successful, also updateLessonItemProgress course progress
            if (success) {
                // Get the lesson ID for this lesson item
                String lessonSql = "SELECT LessonID FROM LessonItems WHERE LessonItemID = ?";
                ps = conn.prepareStatement(lessonSql);
                ps.setInt(1, lessonItemId);
                rs = ps.executeQuery();

                if (rs.next()) {
                    int lessonId = rs.getInt("LessonID");

                    // Get the course ID for this lesson
                    String courseSql = "SELECT CourseID FROM Lessons WHERE LessonID = ?";
                    ps = conn.prepareStatement(courseSql);
                    ps.setInt(1, lessonId);
                    rs = ps.executeQuery();

                    if (rs.next()) {
                        int courseId = rs.getInt("CourseID");

                        // Update course progress
                        CourseProgressDAO courseProgressDAO = new CourseProgressDAO();
                        courseProgressDAO.recalculateProgress(customerId, courseId);
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
}