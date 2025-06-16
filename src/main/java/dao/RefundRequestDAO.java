package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

import model.RefundRequest;
import db.DBContext;
import java.sql.Statement;

/**
 * Data Access Object for RefundRequest entities. Handles database operations
 * related to refund requests.
 */
public class RefundRequestDAO extends DBContext {

    /**
     * Inserts a new refund request into the database.
     *
     * @param request The RefundRequest object to insertRefundRequests
     * @return The generated refund ID if successful, -1 if failed
     */
    public int insertRefundRequests(RefundRequest request) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int refundId = -1;

        try {
            conn = getConnection();
            String sql = "INSERT INTO RefundRequests (OrderID, UserID, RequestDate, Status, RefundAmount, Reason, RefundPercentage) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";

            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, request.getOrderID());
            ps.setInt(2, request.getUserID());
            ps.setTimestamp(3, request.getRequestDate() != null ? request.getRequestDate()
                    : new Timestamp(System.currentTimeMillis()));
            ps.setString(4, request.getStatus() != null ? request.getStatus() : "pending");
            ps.setDouble(5, request.getRefundAmount());
            ps.setString(6, request.getReason()); // Ensure reason is saved
            ps.setInt(7, request.getRefundPercentage() > 0 ? request.getRefundPercentage() : 80);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    refundId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return refundId;
    }

    /**
     * Processes a refund request (approve or reject).
     *
     * @param refundId The ID of the refund request
     * @param status The new status ("approved" or "rejected")
     * @param adminId The ID of the admin processing the request
     * @param adminMessage The message from admin explaining the decision
     * (required)
     * @return true if update successful, false otherwise
     */
    public boolean processRequest(int refundId, String status, int adminId, String adminMessage) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Check if adminMessage is not empty
            if (adminMessage == null || adminMessage.trim().isEmpty()) {
                return false;
            }

            String sql = "UPDATE RefundRequests SET Status = ?, ProcessedDate = ?, ProcessedBy = ?, AdminMessage = ? "
                    + "WHERE RefundID = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setInt(3, adminId);
            ps.setString(4, adminMessage);
            ps.setInt(5, refundId);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 1) {
                // If request is approved, update order status
                if ("approved".equals(status)) {
                    updateOrderForApprovedRefund(conn, refundId);
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
     * Updates order when refund request is approved.
     *
     * @param conn Database connection
     * @param refundId The ID of the refund request
     * @throws SQLException If a SQL error occurs
     */
    private void updateOrderForApprovedRefund(Connection conn, int refundId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Get order information
            String getInfoSql = "SELECT r.OrderID FROM RefundRequests r WHERE r.RefundID = ?";

            ps = conn.prepareStatement(getInfoSql);
            ps.setInt(1, refundId);
            rs = ps.executeQuery();

            if (rs.next()) {
                int orderId = rs.getInt("OrderID");

                // Update order status to "refunded"
                rs.close();
                ps.close();

                String updateOrderSql = "UPDATE Orders SET Status = 'refunded' WHERE OrderID = ?";
                ps = conn.prepareStatement(updateOrderSql);
                ps.setInt(1, orderId);
                ps.executeUpdate();

                // Get refund amount
                ps.close();
                String getRefundAmountSql = "SELECT RefundAmount FROM RefundRequests WHERE RefundID = ?";
                ps = conn.prepareStatement(getRefundAmountSql);
                ps.setInt(1, refundId);
                rs = ps.executeQuery();

                if (rs.next()) {
                    double refundAmount = rs.getDouble("RefundAmount");

                    // Add refund transaction
                    rs.close();
                    ps.close();

                    String insertTransactionSql = "INSERT INTO PaymentTransactions "
                            + "(RefundRequestID, TransactionType, Provider, ProviderTransactionID, BankAccountInfo, CreatedAt) "
                            + "VALUES (?, 'refund', ?, ?, NULL, GETDATE())";

                    ps = conn.prepareStatement(insertTransactionSql);
                    ps.setInt(1, refundId);
                    ps.setString(2, "VNPAY"); // Default provider
                    ps.setString(3, "REFUND_" + refundId);
                    ps.executeUpdate();
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
     * Gets refund request by ID.
     *
     * @param refundId The ID of the refund request
     * @return RefundRequest object, null if not found
     */
    public RefundRequest getRefundById(int refundId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        RefundRequest refund = null;

        try {
            conn = getConnection();
            String sql = "SELECT r.*, u.FullName as UserName, "
                    + "o.TotalAmount as OriginalAmount, "
                    + "a.FullName as AdminName, "
                    + "STUFF((SELECT ', ' + c.Name "
                    + "       FROM OrderDetails od "
                    + "       JOIN Courses c ON od.CourseID = c.CourseID "
                    + "       WHERE od.OrderID = r.OrderID "
                    + "       FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)'), 1, 2, '') AS CourseName "
                    + "FROM RefundRequests r "
                    + "JOIN Users u ON r.UserID = u.UserID "
                    + "JOIN Orders o ON r.OrderID = o.OrderID "
                    + "LEFT JOIN Users a ON r.ProcessedBy = a.UserID "
                    + "WHERE r.RefundID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, refundId);
            rs = ps.executeQuery();

            if (rs.next()) {
                refund = mapRefundRequest(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return refund;
    }

    /**
     * Gets all refund requests.
     *
     * @return List of RefundRequest objects
     */
    public List<RefundRequest> getAllRefundRequests(int page, int pageSize) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<RefundRequest> refunds = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT r.*, u.FullName as UserName, "
                    + "o.TotalAmount as OriginalAmount, a.FullName as AdminName, "
                    + "STUFF((SELECT ', ' + c.Name "
                    + "       FROM OrderDetails od "
                    + "       JOIN Courses c ON od.CourseID = c.CourseID "
                    + "       WHERE od.OrderID = r.OrderID "
                    + "       FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)'), 1, 2, '') AS CourseName "
                    + "FROM RefundRequests r "
                    + "JOIN Users u ON r.UserID = u.UserID "
                    + "JOIN Orders o ON r.OrderID = o.OrderID "
                    + "LEFT JOIN Users a ON r.ProcessedBy = a.UserID "
                    + "ORDER BY r.RequestDate DESC "
                    + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, (page - 1) * pageSize);
            ps.setInt(2, pageSize);
            rs = ps.executeQuery();

            while (rs.next()) {
                RefundRequest refund = mapRefundRequest(rs);
                refunds.add(refund);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }
        return refunds;
    }

    /**
     * Get count total requests by status
     *
     * @param status
     * @return total
     */
    public int getTotalRequestsByStatusAndSearch(String status, String search) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int total = 0;
        try {
            conn = getConnection();
            StringBuilder sql = new StringBuilder(
                    "SELECT COUNT(*) AS total "
                    + "FROM RefundRequests r "
                    + "JOIN Users u ON r.UserID = u.UserID "
                    + "JOIN Orders o ON r.OrderID = o.OrderID "
                    + "WHERE 1=1 ");

            List<Object> params = new ArrayList<>();

            if (status != null && !status.trim().isEmpty()) {
                sql.append("AND r.Status = ? ");
                params.add(status);
            }

            if (search != null && !search.trim().isEmpty()) {
                sql.append("AND (u.FullName LIKE ? OR EXISTS ( "
                        + "    SELECT 1 FROM OrderDetails od "
                        + "    JOIN Courses c ON od.CourseID = c.CourseID "
                        + "    WHERE od.OrderID = r.OrderID AND c.Name LIKE ? "
                        + ") OR CAST(r.RefundID as VARCHAR) LIKE ?) ");
                String keyword = "%" + search.trim() + "%";
                params.add(keyword);
                params.add(keyword);
                params.add(keyword);
            }

            ps = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }
        return total;
    }

    public List<RefundRequest> getByStatusAndSearch(String status, String search, int page, int pageSize) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<RefundRequest> refunds = new ArrayList<>();
        try {
            conn = getConnection();
            StringBuilder sql = new StringBuilder(
                    "SELECT r.*, u.FullName as UserName, o.OrderDate, "
                    + "o.TotalAmount as OriginalAmount, a.FullName as AdminName, "
                    + "STUFF((SELECT ', ' + c.Name "
                    + "       FROM OrderDetails od "
                    + "       JOIN Courses c ON od.CourseID = c.CourseID "
                    + "       WHERE od.OrderID = r.OrderID "
                    + "       FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)'), 1, 2, '') AS CourseName "
                    + "FROM RefundRequests r "
                    + "JOIN Users u ON r.UserID = u.UserID "
                    + "JOIN Orders o ON r.OrderID = o.OrderID "
                    + "LEFT JOIN Users a ON r.ProcessedBy = a.UserID "
                    + "WHERE 1=1 ");

            List<Object> params = new ArrayList<>();

            // Add status filter
            if (status != null && !status.trim().isEmpty()) {
                sql.append("AND r.Status = ? ");
                params.add(status);
            }

            // Add search filter
            if (search != null && !search.trim().isEmpty()) {
                sql.append("AND (u.FullName LIKE ? OR EXISTS ( "
                        + "    SELECT 1 FROM OrderDetails od "
                        + "    JOIN Courses c ON od.CourseID = c.CourseID "
                        + "    WHERE od.OrderID = r.OrderID AND c.Name LIKE ? "
                        + ") OR CAST(r.RefundID as VARCHAR) LIKE ?) ");
                String keyword = "%" + search.trim() + "%";
                params.add(keyword); // for FullName
                params.add(keyword); // for CourseName
                params.add(keyword); // for RefundID
            }

            // Add order by and pagination
            sql.append("ORDER BY r.RequestDate DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
            params.add((page - 1) * pageSize);
            params.add(pageSize);

            ps = conn.prepareStatement(sql.toString());

            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            rs = ps.executeQuery();
            while (rs.next()) {
                RefundRequest refund = mapRefundRequest(rs);
                refunds.add(refund);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }
        return refunds;
    }

    /**
     * Get count total requests
     *
     * @return total
     */
    public int getTotalRequests() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int total = 0;

        try {
            conn = getConnection();
            String sql = "SELECT COUNT(*) FROM RefundRequests";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }
        return total;
    }

    /**
     * Gets refund requests by status.
     *
     * @param status The status to filter by ("pending", "approved", "rejected")
     * @return List of RefundRequest objects
     */
    public List<RefundRequest> getByStatus(String status, int page, int pageSize) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<RefundRequest> refunds = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT r.*, u.FullName as UserName, "
                    + "o.TotalAmount as OriginalAmount, a.FullName as AdminName, "
                    + "STUFF((SELECT ', ' + c.Name "
                    + "       FROM OrderDetails od "
                    + "       JOIN Courses c ON od.CourseID = c.CourseID "
                    + "       WHERE od.OrderID = r.OrderID "
                    + "       FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)'), 1, 2, '') AS CourseName "
                    + "FROM RefundRequests r "
                    + "JOIN Users u ON r.UserID = u.UserID "
                    + "JOIN Orders o ON r.OrderID = o.OrderID "
                    + "LEFT JOIN Users a ON r.ProcessedBy = a.UserID "
                    + "WHERE r.Status = ? "
                    + "ORDER BY r.RequestDate DESC "
                    + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

            ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, (page - 1) * pageSize);
            ps.setInt(3, pageSize);
            rs = ps.executeQuery();

            while (rs.next()) {
                RefundRequest refund = mapRefundRequest(rs);
                refunds.add(refund);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }
        return refunds;
    }

    /**
     * Gets refund requests by user ID.
     *
     * @param userId The user ID
     * @return List of RefundRequest objects
     */
    public List<RefundRequest> getByUser(int userId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<RefundRequest> refunds = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT r.*, u.FullName as UserName, "
                    + "o.TotalAmount as OriginalAmount, a.FullName as AdminName, "
                    + "STUFF((SELECT ', ' + c.Name "
                    + "       FROM OrderDetails od "
                    + "       JOIN Courses c ON od.CourseID = c.CourseID "
                    + "       WHERE od.OrderID = r.OrderID "
                    + "       FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)'), 1, 2, '') AS CourseName "
                    + "FROM RefundRequests r "
                    + "JOIN Users u ON r.UserID = u.UserID "
                    + "JOIN Orders o ON r.OrderID = o.OrderID "
                    + "LEFT JOIN Users a ON r.ProcessedBy = a.UserID "
                    + "WHERE r.UserID = ? "
                    + "ORDER BY r.RequestDate DESC";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            while (rs.next()) {
                RefundRequest refund = mapRefundRequest(rs);
                refunds.add(refund);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return refunds;
    }

    /**
     * Checks if a user has a pending refund request for an order
     *
     * @param userId The user ID
     * @param orderId The order ID
     * @return true if there's a pending refund request, false otherwise
     */
    public boolean hasPendingRefundForOrder(int userId, int orderId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean hasPending = false;

        try {
            conn = getConnection();
            String sql = "SELECT COUNT(*) FROM RefundRequests WHERE UserID = ? AND OrderID = ? AND Status = 'pending'";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, orderId);
            rs = ps.executeQuery();

            if (rs.next()) {
                hasPending = rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return hasPending;
    }

    /**
     * Checks if a user has an approved refund request for an order
     *
     * @param userId The user ID
     * @param orderId The order ID
     * @return true if there's an approved refund request, false otherwise
     */
    public boolean hasApprovedRefundForOrder(int userId, int orderId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean hasApproved = false;

        try {
            conn = getConnection();
            String sql = "SELECT COUNT(*) FROM RefundRequests WHERE UserID = ? AND OrderID = ? AND Status = 'approved'";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, orderId);
            rs = ps.executeQuery();

            if (rs.next()) {
                hasApproved = rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return hasApproved;
    }

    /**
     * Maps a ResultSet row to a RefundRequest object.
     *
     * @param rs The ResultSet containing refund request data
     * @return A populated RefundRequest object
     * @throws SQLException If a SQL error occurs
     */
    private RefundRequest mapRefundRequest(ResultSet rs) throws SQLException {
        RefundRequest refund = new RefundRequest();
        refund.setRefundID(rs.getInt("RefundID"));
        refund.setOrderID(rs.getInt("OrderID"));
        refund.setUserID(rs.getInt("UserID"));
        refund.setRequestDate(rs.getTimestamp("RequestDate"));
        refund.setStatus(rs.getString("Status"));
        refund.setRefundAmount(rs.getDouble("RefundAmount"));
        refund.setReason(rs.getString("Reason"));

        // Process nullable columns
        Timestamp processedDate = rs.getTimestamp("ProcessedDate");
        if (processedDate != null) {
            refund.setProcessedDate(processedDate);
        }

        // ProcessedBy can be NULL in the database
        int processedBy = rs.getInt("ProcessedBy");
        if (!rs.wasNull()) {
            refund.setProcessedBy(processedBy);
        }

        String adminMessage = rs.getString("AdminMessage");
        if (adminMessage != null) {
            refund.setAdminMessage(adminMessage);
        }

        refund.setRefundPercentage(rs.getInt("RefundPercentage"));

        // Set additional display fields if available in the result set
        try {
            refund.setUserName(rs.getString("UserName"));
        } catch (SQLException e) {
            // Column not in result set, ignore
        }

        try {
            refund.setAdminName(rs.getString("AdminName"));
        } catch (SQLException e) {
            // Column not in result set, ignore
        }

        try {
            refund.setOriginalAmount(rs.getDouble("OriginalAmount"));
        } catch (SQLException e) {
            // Column not in result set, ignore
        }

        try {
            refund.setCourseName(rs.getString("CourseName"));
        } catch (SQLException e) {
            // Column not in result set, ignore
        }

        return refund;
    }

    /**
     * Retrieves all refund requests with a limit.
     *
     * @param limit Maximum number of refund requests to retrieve
     * @return List of RefundRequest objects
     */
    public List<RefundRequest> getAllRefundRequestsWithLimit(int limit) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<RefundRequest> refundRequests = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT r.RefundID, r.OrderID, r.UserID, r.RequestDate, r.Status, r.RefundAmount, "
                    + "r.Reason, r.ProcessedDate, r.ProcessedBy, r.AdminMessage, r.RefundPercentage, "
                    + "u.Username AS UserName, o.TotalAmount AS OriginalAmount, "
                    + "admin.Username AS AdminName, "
                    + "STUFF((SELECT ', ' + c.Name "
                    + "       FROM OrderDetails od "
                    + "       JOIN Courses c ON od.CourseID = c.CourseID "
                    + "       WHERE od.OrderID = r.OrderID "
                    + "       FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)'), 1, 2, '') AS CourseName "
                    + "FROM RefundRequests r "
                    + "JOIN Users u ON r.UserID = u.UserID "
                    + "JOIN Orders o ON r.OrderID = o.OrderID "
                    + "LEFT JOIN Users admin ON r.ProcessedBy = admin.UserID "
                    + "ORDER BY r.RequestDate DESC";

            if (limit > 0) {
                sql += " OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY";
            }

            ps = conn.prepareStatement(sql);

            if (limit > 0) {
                ps.setInt(1, limit);
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                refundRequests.add(mapRefundRequest(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return refundRequests;
    }
}
