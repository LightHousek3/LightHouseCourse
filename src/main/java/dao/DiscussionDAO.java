/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import db.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.Discussion;

/**
 * Data access object for discussions
 *
 * @author DangPH - CE180896
 */
public class DiscussionDAO extends DBContext {

    private Connection conn = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;

    /**
     * Count discussions for a list of courses with filtering
     *
     * @param courseIds  List of course IDs to check
     * @param courseId   Specific course ID to filter by (optional)
     * @param lessonId   Specific lesson ID to filter by (optional)
     * @param resolved   Filter by resolved status (optional)
     * @param searchTerm Search term to filter by (optional)
     * @return Count of discussions matching the criteria
     */
    public int countDiscussionsForCourses(List<Integer> courseIds, Integer courseId, Integer lessonId, Boolean resolved,
            String searchTerm) {
        if (courseIds == null || courseIds.isEmpty()) {
            return 0;
        }

        try {
            StringBuilder queryBuilder = new StringBuilder("SELECT COUNT(*) FROM Discussions WHERE ");

            List<Object> params = new ArrayList<>();

            // Apply course filter
            if (courseId != null) {
                queryBuilder.append("CourseID = ? ");
                params.add(courseId);
            } else {
                queryBuilder.append("CourseID IN (");
                // Create placeholders for course IDs
                for (int i = 0; i < courseIds.size(); i++) {
                    if (i > 0) {
                        queryBuilder.append(",");
                    }
                    queryBuilder.append("?");
                    params.add(courseIds.get(i));
                }
                queryBuilder.append(") ");
            }

            // Add lesson filter if provided
            if (lessonId != null) {
                queryBuilder.append("AND LessonID = ? ");
                params.add(lessonId);
            }

            // Add resolved filter if provided
            if (resolved != null) {
                queryBuilder.append("AND IsResolved = ? ");
                params.add(resolved);
            }

            // Add search term filter if provided
            if (searchTerm != null && !searchTerm.isEmpty()) {
                queryBuilder.append("AND (Content LIKE ?) ");
                params.add("%" + searchTerm + "%");
            }

            conn = getConnection();
            ps = conn.prepareStatement(queryBuilder.toString());

            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Integer) {
                    ps.setInt(i + 1, (Integer) param);
                } else if (param instanceof Boolean) {
                    ps.setBoolean(i + 1, (Boolean) param);
                } else if (param instanceof String) {
                    ps.setString(i + 1, (String) param);
                }
            }

            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            System.out.println("Error counting discussions: " + ex.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }

        return 0;
    }

    /**
     * Get discussions for a list of courses with filtering and pagination
     *
     * @param courseIds  List of course IDs to check
     * @param courseId   Specific course ID to filter by (optional)
     * @param lessonId   Specific lesson ID to filter by (optional)
     * @param resolved   Filter by resolved status (optional)
     * @param searchTerm Search term to filter by (optional)
     * @param page       Page number (1-based)
     * @param pageSize   Number of discussions per page
     * @return List of discussions matching the criteria
     */
    public List<Discussion> getDiscussionsForCourses(List<Integer> courseIds, Integer courseId, Integer lessonId,
            Boolean resolved, String searchTerm, int page, int pageSize) {
        List<Discussion> discussions = new ArrayList<>();

        if (courseIds == null || courseIds.isEmpty()) {
            return discussions;
        }

        try {
            StringBuilder queryBuilder = new StringBuilder(
                    "SELECT d.*, c.Name AS CourseName, l.Title AS LessonTitle, ");
            queryBuilder.append(
                    "(SELECT COUNT(*) FROM DiscussionReplies WHERE DiscussionID = d.DiscussionID) AS ReplyCount ");
            queryBuilder.append("FROM Discussions d ");
            queryBuilder.append("LEFT JOIN Courses c ON d.CourseID = c.CourseID ");
            queryBuilder.append("LEFT JOIN Lessons l ON d.LessonID = l.LessonID ");
            queryBuilder.append("WHERE ");

            List<Object> params = new ArrayList<>();

            // Apply course filter
            if (courseId != null) {
                queryBuilder.append("d.CourseID = ? ");
                params.add(courseId);
            } else {
                queryBuilder.append("d.CourseID IN (");
                // Create placeholders for course IDs
                for (int i = 0; i < courseIds.size(); i++) {
                    if (i > 0) {
                        queryBuilder.append(",");
                    }
                    queryBuilder.append("?");
                    params.add(courseIds.get(i));
                }
                queryBuilder.append(") ");
            }

            // Add lesson filter if provided
            if (lessonId != null) {
                queryBuilder.append("AND d.LessonID = ? ");
                params.add(lessonId);
            }

            // Add resolved filter if provided
            if (resolved != null) {
                queryBuilder.append("AND d.IsResolved = ? ");
                params.add(resolved);
            }

            // Add search term filter if provided
            if (searchTerm != null && !searchTerm.isEmpty()) {
                queryBuilder.append("AND (d.Content LIKE ?) ");
                params.add("%" + searchTerm + "%");
            }

            // Add order by and pagination
            queryBuilder.append("ORDER BY d.CreatedAt DESC ");
            queryBuilder.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

            conn = getConnection();
            ps = conn.prepareStatement(queryBuilder.toString());

            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Integer) {
                    ps.setInt(i + 1, (Integer) param);
                } else if (param instanceof Boolean) {
                    ps.setBoolean(i + 1, (Boolean) param);
                } else if (param instanceof String) {
                    ps.setString(i + 1, (String) param);
                }
            }

            // Set pagination parameters
            int offset = (page - 1) * pageSize;
            ps.setInt(params.size() + 1, offset);
            ps.setInt(params.size() + 2, pageSize);

            rs = ps.executeQuery();
            while (rs.next()) {
                Discussion discussion = mapDiscussion(rs);
                discussions.add(discussion);
            }
        } catch (SQLException ex) {
            System.out.println("Error getting discussions: " + ex.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }

        return discussions;
    }

    /**
     * Get a discussion by ID
     *
     * @param discussionId The discussion ID
     * @return The discussion or null if not found
     */
    public Discussion getDiscussionById(int discussionId) {
        Discussion discussion = null;

        try {
            String query = "SELECT d.*, c.Name AS CourseName, l.Title AS LessonTitle, "
                    + "CASE WHEN d.AuthorType = 'customer' THEN cu.FullName "
                    + "     WHEN d.AuthorType = 'instructor' THEN su.FullName "
                    + "     ELSE 'Unknown' END AS AuthorName, "
                    + "(SELECT COUNT(*) FROM DiscussionReplies WHERE DiscussionID = d.DiscussionID) AS ReplyCount "
                    + "FROM Discussions d "
                    + "LEFT JOIN Courses c ON d.CourseID = c.CourseID "
                    + "LEFT JOIN Lessons l ON d.LessonID = l.LessonID "
                    + "LEFT JOIN Customers cu ON d.AuthorID = cu.CustomerID AND d.AuthorType = 'customer' "
                    + "LEFT JOIN SuperUsers su ON d.AuthorID = su.SuperUserID AND d.AuthorType = 'instructor' "
                    + "WHERE d.DiscussionID = ?";

            conn = getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, discussionId);
            rs = ps.executeQuery();

            if (rs.next()) {
                discussion = mapDiscussion(rs);
            }
        } catch (SQLException ex) {
            System.out.println("Error getting discussion by ID: " + ex.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }

        return discussion;
    }

    /**
     * Update a discussion's resolved status
     *
     * @param discussionId The discussion ID
     * @param resolved     The new resolved status
     * @return True if successful, false otherwise
     */
    public boolean updateDiscussionResolved(int discussionId, boolean resolved) {
        boolean success = false;

        try {
            String query = "UPDATE Discussions SET IsResolved = ?, UpdatedAt = ? WHERE DiscussionID = ?";

            conn = getConnection();
            ps = conn.prepareStatement(query);
            ps.setBoolean(1, resolved);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setInt(3, discussionId);

            int rowsAffected = ps.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error updating discussion resolved status: " + ex.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }

        return success;
    }

    /**
     * Update a discussion's content
     *
     * @param discussionId The discussion ID
     * @param authorId     The author ID (for security check)
     * @param content      The new content
     * @return True if successful, false otherwise
     */
    public boolean updateDiscussion(int discussionId, int authorId, String content) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = getConnection();

            // First check if the user is the author
            String checkQuery = "SELECT AuthorID FROM Discussions WHERE DiscussionID = ? AND AuthorID = ? AND AuthorType = 'customer'";
            ps = conn.prepareStatement(checkQuery);
            ps.setInt(1, discussionId);
            ps.setInt(2, authorId);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                // User is not the author or discussion doesn't exist
                return false;
            }

            rs.close();
            ps.close();

            // Update the discussion
            String updateQuery = "UPDATE Discussions SET Content = ?, UpdatedAt = GETDATE() WHERE DiscussionID = ?";
            ps = conn.prepareStatement(updateQuery);
            ps.setString(1, content);
            ps.setInt(2, discussionId);

            int rowsAffected = ps.executeUpdate();
            success = rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(null, ps, conn);
        }

        return success;
    }

    /**
     * Delete a discussion and all its replies
     *
     * @param discussionId The discussion ID
     * @param authorId     The author ID (for security check)
     * @return True if successful, false otherwise
     */
    public boolean deleteDiscussion(int discussionId, int authorId) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Start transaction

            // First check if the user is the author
            String checkQuery = "SELECT AuthorID FROM Discussions WHERE DiscussionID = ? AND AuthorID = ? AND AuthorType = 'customer'";
            ps = conn.prepareStatement(checkQuery);
            ps.setInt(1, discussionId);
            ps.setInt(2, authorId);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                // User is not the author or discussion doesn't exist
                conn.rollback();
                return false;
            }

            rs.close();
            ps.close();

            // Delete the discussion (replies will be deleted by CASCADE constraint)
            String deleteQuery = "DELETE FROM Discussions WHERE DiscussionID = ?";
            ps = conn.prepareStatement(deleteQuery);
            ps.setInt(1, discussionId);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
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
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            closeResources(null, ps, conn);
        }

        return success;
    }

    /**
     * Get all lessons for a specific course
     *
     * @param courseId The course ID
     * @return List of lesson IDs and titles
     */
    public List<Object[]> getLessonsByCourseId(int courseId) {
        List<Object[]> lessons = new ArrayList<>();

        try {
            String query = "SELECT LessonID, Title FROM Lessons WHERE CourseID = ? ORDER BY OrderIndex";

            conn = getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, courseId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Object[] lesson = new Object[2];
                lesson[0] = rs.getInt("LessonID");
                lesson[1] = rs.getString("Title");
                lessons.add(lesson);
            }
        } catch (SQLException ex) {
            System.out.println("Error getting lessons by course ID: " + ex.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }

        return lessons;
    }

    /**
     * Map a ResultSet row to a Discussion object
     *
     * @param rs The ResultSet
     * @return The Discussion object
     * @throws SQLException If an error occurs
     */
    private Discussion mapDiscussion(ResultSet rs) throws SQLException {
        Discussion discussion = new Discussion();
        discussion.setDiscussionID(rs.getInt("DiscussionID"));
        discussion.setCourseID(rs.getInt("CourseID"));
        discussion.setLessonID(rs.getInt("LessonID"));
        discussion.setAuthorID(rs.getInt("AuthorID"));
        discussion.setAuthorType(rs.getString("AuthorType"));
        discussion.setContent(rs.getString("Content"));
        discussion.setCreatedAt(rs.getTimestamp("CreatedAt"));
        discussion.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
        discussion.setIsResolved(rs.getBoolean("IsResolved"));

        // Additional display fields
        if (rs.getMetaData().getColumnCount() > 9) {
            try {
                discussion.setCourseName(rs.getString("CourseName"));
            } catch (SQLException e) {
                // Column might not exist in some queries
            }

            try {
                discussion.setLessonTitle(rs.getString("LessonTitle"));
            } catch (SQLException e) {
                // Column might not exist in some queries
            }

            try {
                discussion.setAuthorName(rs.getString("AuthorName"));
            } catch (SQLException e) {
                // Column might not exist in some queries
            }

            try {
                discussion.setReplyCount(rs.getInt("ReplyCount"));
            } catch (SQLException e) {
                // Column might not exist in some queries
            }
        }

        return discussion;
    }

    /**
     * Get discussions for a specific lesson with user and course details
     *
     * @param lessonId The ID of the lesson
     * @return List of discussions for the specified lesson
     */
    public List<Discussion> getDiscussionsByLessonId(int lessonId) {
        List<Discussion> discussions = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            String sql = "SELECT d.*, c.Name as CourseName, u.FullName as AuthorName, u.Avatar as AuthorAvatar\n"
                    + "                    FROM Discussions d  \n"
                    + "                    JOIN Courses c ON d.CourseID = c.CourseID \n"
                    + "                    JOIN Customers u ON d.AuthorID = u.CustomerID \n"
                    + "                    WHERE d.LessonID = ? \n"
                    + "                    ORDER BY d.CreatedAt DESC";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, lessonId);

            rs = ps.executeQuery();

            while (rs.next()) {
                Discussion discussion = mapDiscussion(rs);
                discussion.setCourseName(rs.getString("CourseName"));
                discussion.setAuthorName(rs.getString("AuthorName"));
                discussion.setAuthorAvatar(rs.getString("AuthorAvatar"));
                discussions.add(discussion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(null, ps, conn);
        }

        return discussions;
    }

    /**
     * Creates a new discussion and returns the generated ID
     *
     * @param discussion Discussion object to create
     * @return The ID of the newly created discussion, or -1 if failed
     */
    public int createDiscussion(Discussion discussion) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int discussionId = -1;

        try {
            conn = getConnection();
            String sql = "INSERT INTO Discussions (CourseID, LessonID, AuthorID, AuthorType, Content, CreatedAt, UpdatedAt, IsResolved) "
                    + "VALUES (?, ?, ?, ?, ?, GETDATE(), GETDATE(), ?)";

            ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, discussion.getCourseID());
            ps.setInt(2, discussion.getLessonID());
            ps.setInt(3, discussion.getAuthorID());
            ps.setString(4, discussion.getAuthorType());
            ps.setString(5, discussion.getContent());
            ps.setBoolean(6, discussion.isResolved());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    discussionId = rs.getInt(1);
                    discussion.setDiscussionID(discussionId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(null, ps, conn);
        }

        return discussionId;
    }

}
