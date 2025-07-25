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
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.DiscussionReply;

/**
 * Data access object for discussion replies
 * 
 * @author DangPH - CE180896
 */
public class DiscussionReplyDAO extends DBContext {
    private Connection conn = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;

    /**
     * Get all replies for a specific discussion
     * 
     * @param discussionId The discussion ID
     * @return List of replies
     */
    public List<DiscussionReply> getRepliesByDiscussionId(int discussionId) {
        List<DiscussionReply> replies = new ArrayList<>();

        try {
            String query = "SELECT r.*, " +
                    "CASE WHEN r.AuthorType = 'customer' THEN cu.FullName " +
                    "     WHEN r.AuthorType = 'instructor' THEN su.FullName " +
                    "     ELSE 'Unknown' END AS AuthorName " +
                    "FROM DiscussionReplies r " +
                    "LEFT JOIN Customers cu ON r.AuthorID = cu.CustomerID AND r.AuthorType = 'customer' " +
                    "LEFT JOIN SuperUsers su ON r.AuthorID = su.SuperUserID AND r.AuthorType = 'instructor' " +
                    "WHERE r.DiscussionID = ? " +
                    "ORDER BY r.CreatedAt ASC";

            conn = getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, discussionId);
            rs = ps.executeQuery();

            while (rs.next()) {
                DiscussionReply reply = mapReply(rs);
                replies.add(reply);
            }
        } catch (SQLException ex) {
            System.out.println("Error getting replies: " + ex.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }

        return replies;
    }

    /**
     * Add a new discussion reply and update discussion status if needed
     * 
     * @param reply The reply to add
     * @return True if successful, false otherwise
     */
    public boolean addReply(DiscussionReply reply) {
        boolean success = false;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Start transaction

            String query = "INSERT INTO DiscussionReplies (DiscussionID, AuthorID, AuthorType, Content, " +
                    "CreatedAt, UpdatedAt) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, reply.getDiscussionID());
            ps.setInt(2, reply.getAuthorID());
            ps.setString(3, reply.getAuthorType());
            ps.setString(4, reply.getContent());

            Timestamp now = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(5, now);
            ps.setTimestamp(6, now);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    reply.setReplyID(rs.getInt(1));

                    // If reply is from a customer, check if discussion is resolved and update if
                    // needed
                    if ("customer".equals(reply.getAuthorType())) {
                        ps.close();
                        rs.close();

                        // Check if discussion is resolved
                        String checkQuery = "SELECT IsResolved FROM Discussions WHERE DiscussionID = ?";
                        ps = conn.prepareStatement(checkQuery);
                        ps.setInt(1, reply.getDiscussionID());
                        rs = ps.executeQuery();

                        if (rs.next() && rs.getBoolean("IsResolved")) {
                            // If discussion is resolved, update it to unresolved
                            ps.close();
                            rs.close();

                            String updateQuery = "UPDATE Discussions SET IsResolved = 0, UpdatedAt = ? WHERE DiscussionID = ?";
                            ps = conn.prepareStatement(updateQuery);
                            ps.setTimestamp(1, now);
                            ps.setInt(2, reply.getDiscussionID());

                            ps.executeUpdate();
                        }
                    }

                    conn.commit(); // Commit transaction
                    success = true;
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error adding reply: " + ex.getMessage());
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaction on error
                } catch (SQLException e) {
                    System.out.println("Error rolling back transaction: " + e.getMessage());
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit
                } catch (SQLException e) {
                    System.out.println("Error resetting auto-commit: " + e.getMessage());
                }
            }
            closeResources(rs, ps, conn);
        }

        return success;
    }

    /**
     * Create a new discussion reply
     * 
     * @param reply The reply to create
     * @return True if successful, false otherwise
     */
    public boolean createReply(DiscussionReply reply) {
        boolean success = false;

        try {
            String query = "INSERT INTO DiscussionReplies (DiscussionID, AuthorID, AuthorType, Content, " +
                    "CreatedAt, UpdatedAt) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            conn = getConnection();
            ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, reply.getDiscussionID());
            ps.setInt(2, reply.getAuthorID());
            ps.setString(3, reply.getAuthorType());
            ps.setString(4, reply.getContent());

            Timestamp now = new Timestamp(System.currentTimeMillis());
            ps.setTimestamp(5, now);
            ps.setTimestamp(6, now);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    reply.setReplyID(rs.getInt(1));
                    success = true;
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error creating reply: " + ex.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }

        return success;
    }

    /**
     * Check if an instructor is teaching a specific course
     * 
     * @param superUserId The instructor's super user ID
     * @param courseId    The course ID
     * @return True if the instructor is teaching the course, false otherwise
     */
    public boolean isInstructorTeachingCourse(int superUserId, int courseId) {
        boolean isTeaching = false;

        try {
            String query = "SELECT COUNT(*) FROM CourseInstructors ci " +
                    "JOIN Instructors i ON ci.InstructorID = i.InstructorID " +
                    "WHERE i.SuperUserID = ? AND ci.CourseID = ?";

            conn = getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, superUserId);
            ps.setInt(2, courseId);
            rs = ps.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                isTeaching = true;
            }
        } catch (SQLException ex) {
            System.out.println("Error checking if instructor is teaching course: " + ex.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }

        return isTeaching;
    }

    /**
     * Check if an instructor is associated with a course
     * 
     * @param instructorId The instructor ID
     * @param courseId     The course ID
     * @return True if the instructor is associated with the course, false otherwise
     */
    public boolean isInstructorForCourse(int instructorId, int courseId) {
        boolean isInstructor = false;

        try {
            String query = "SELECT COUNT(*) FROM CourseInstructors " +
                    "WHERE InstructorID = ? AND CourseID = ?";

            conn = getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, instructorId);
            ps.setInt(2, courseId);
            rs = ps.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                isInstructor = true;
            }
        } catch (SQLException ex) {
            System.out.println("Error checking instructor for course: " + ex.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }

        return isInstructor;
    }

    /**
     * Map a ResultSet row to a DiscussionReply object
     * 
     * @param rs The ResultSet
     * @return The DiscussionReply object
     * @throws SQLException If an error occurs
     */
    private DiscussionReply mapReply(ResultSet rs) throws SQLException {
        DiscussionReply reply = new DiscussionReply();
        reply.setReplyID(rs.getInt("ReplyID"));
        reply.setDiscussionID(rs.getInt("DiscussionID"));
        reply.setAuthorID(rs.getInt("AuthorID"));
        reply.setAuthorType(rs.getString("AuthorType"));
        reply.setContent(rs.getString("Content"));
        reply.setCreatedAt(rs.getTimestamp("CreatedAt"));
        reply.setUpdatedAt(rs.getTimestamp("UpdatedAt"));

        // Additional display fields
        try {
            reply.setAuthorName(rs.getString("AuthorName"));
        } catch (SQLException e) {
            // Column might not exist in some queries
        }

        return reply;
    }

    /**
     * Update a reply's content
     * 
     * @param replyId  The reply ID
     * @param authorId The author ID (for security check)
     * @param authorType The author type (customer or instructor)
     * @param content  The new content
     * @return True if successful, false otherwise
     */
    public boolean updateReply(int replyId, int authorId, String authorType, String content) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = getConnection();

            // First check if the user is the author
            String checkQuery = "SELECT AuthorID FROM DiscussionReplies WHERE ReplyID = ? AND AuthorID = ? AND AuthorType = ?";
            ps = conn.prepareStatement(checkQuery);
            ps.setInt(1, replyId);
            ps.setInt(2, authorId);
            ps.setString(3, authorType);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                // User is not the author or reply doesn't exist
                return false;
            }

            rs.close();
            ps.close();

            // Update the reply
            String updateQuery = "UPDATE DiscussionReplies SET Content = ?, UpdatedAt = GETDATE() WHERE ReplyID = ?";
            ps = conn.prepareStatement(updateQuery);
            ps.setString(1, content);
            ps.setInt(2, replyId);

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
     * Delete a reply
     * 
     * @param replyId  The reply ID
     * @param authorId The author ID (for security check)
     * @param authorType The author type (customer or instructor)
     * @return True if successful, false otherwise
     */
    public boolean deleteReply(int replyId, int authorId, String authorType) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = getConnection();

            // First check if the user is the author
            String checkQuery = "SELECT AuthorID FROM DiscussionReplies WHERE ReplyID = ? AND AuthorID = ? AND AuthorType = ?";
            ps = conn.prepareStatement(checkQuery);
            ps.setInt(1, replyId);
            ps.setInt(2, authorId);
            ps.setString(3, authorType);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                // User is not the author or reply doesn't exist
                return false;
            }

            rs.close();
            ps.close();

            // Delete the reply
            String deleteQuery = "DELETE FROM DiscussionReplies WHERE ReplyID = ?";
            ps = conn.prepareStatement(deleteQuery);
            ps.setInt(1, replyId);

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
     * Get a reply by ID
     * 
     * @param replyId The reply ID
     * @return The reply or null if not found
     */
    public DiscussionReply getReplyById(int replyId) {
        DiscussionReply reply = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            String query = "SELECT r.*, " +
                    "CASE WHEN r.AuthorType = 'customer' THEN cu.FullName " +
                    "     WHEN r.AuthorType = 'instructor' THEN su.FullName " +
                    "     ELSE 'Unknown' END AS AuthorName " +
                    "FROM DiscussionReplies r " +
                    "LEFT JOIN Customers cu ON r.AuthorID = cu.CustomerID AND r.AuthorType = 'customer' " +
                    "LEFT JOIN SuperUsers su ON r.AuthorID = su.SuperUserID AND r.AuthorType = 'instructor' " +
                    "WHERE r.ReplyID = ?";

            ps = conn.prepareStatement(query);
            ps.setInt(1, replyId);
            rs = ps.executeQuery();

            if (rs.next()) {
                reply = mapReply(rs);
            }
        } catch (SQLException ex) {
            System.out.println("Error getting reply by ID: " + ex.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }

        return reply;
    }

    /**
     * Check if a discussion has any instructor replies
     * 
     * @param discussionId The discussion ID
     * @return True if there are instructor replies, false otherwise
     */
    public boolean hasInstructorReplies(int discussionId) {
        boolean hasReplies = false;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();

            String query = "SELECT COUNT(*) FROM DiscussionReplies WHERE DiscussionID = ? AND AuthorType = 'instructor'";
            ps = conn.prepareStatement(query);
            ps.setInt(1, discussionId);
            rs = ps.executeQuery();

            if (rs.next()) {
                hasReplies = rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            System.out.println("Error checking instructor replies: " + ex.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }

        return hasReplies;
    }
}