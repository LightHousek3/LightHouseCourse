package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.RefundRequest;
import db.DBContext;

/**
 * Data Access Object for RefundRequest entities. Handles database operations
 * related to refund requests.
 */
public class RefundRequestDAO extends DBContext {

    private CustomerDAO customerDAO;
    private SuperUserDAO superUserDAO;

    public RefundRequestDAO() {
        this.customerDAO = new CustomerDAO();
        this.superUserDAO = new SuperUserDAO();
    }

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
            String sql = "INSERT INTO RefundRequests (OrderID, CustomerID, RequestDate, Status, RefundAmount, Reason, RefundPercentage) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";

            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, request.getOrderID());
            ps.setInt(2, request.getCustomerID());
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
     * @param superUserId The ID of the admin processing the request
     * @param adminMessage The message from admin explaining the decision
     * (required)
     * @return true if update successful, false otherwise
     */
    public boolean processRequest(int refundId, String status, int superUserId, String adminMessage) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
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
            ps.setInt(3, superUserId);
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
            closeResources(rs, ps, conn);
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
            closeResources(rs, ps, conn);
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
            String sql = "SELECT r.*, c.FullName as CustomerName, "
                    + "o.TotalAmount as OriginalAmount, "
                    + "a.FullName as AdminName, "
                    + "STUFF((SELECT ', ' + co.Name "
                    + "       FROM OrderDetails od "
                    + "       JOIN Courses co ON od.CourseID = co.CourseID "
                    + "       WHERE od.OrderID = r.OrderID "
                    + "       FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)'), 1, 2, '') AS CourseName "
                    + "FROM RefundRequests r "
                    + "JOIN Customers c ON r.CustomerID = c.CustomerID "
                    + "JOIN Orders o ON r.OrderID = o.OrderID "
                    + "LEFT JOIN SuperUsers a ON r.ProcessedBy = a.SuperUserID "
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
            String sql = "SELECT r.*, c.FullName as CustomerName, "
                    + "o.TotalAmount as OriginalAmount, a.FullName as AdminName, "
                    + "STUFF((SELECT ', ' + co.Name "
                    + "       FROM OrderDetails od "
                    + "       JOIN Courses co ON od.CourseID = co.CourseID "
                    + "       WHERE od.OrderID = r.OrderID "
                    + "       FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)'), 1, 2, '') AS CourseName "
                    + "FROM RefundRequests r "
                    + "JOIN Customers c ON r.CustomerID = c.CustomerID "
                    + "JOIN Orders o ON r.OrderID = o.OrderID "
                    + "LEFT JOIN SuperUsers a ON r.ProcessedBy = a.SuperUserID "
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
     * Gets the total number of refund requests matching the given status and
     * search criteria.
     *
     * @param status The status to filter by (or "all")
     * @param search The search text to filter by
     * @return The total number of matching refund requests
     */
    public int getTotalRequestsByStatusAndSearch(String status, String search) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int total = 0;

        try {
            conn = getConnection();
            StringBuilder sql = new StringBuilder(
                    "SELECT COUNT(*) FROM RefundRequests r JOIN Customers c ON r.CustomerID = c.CustomerID WHERE 1=1");

            if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("all")) {
                sql.append(" AND r.Status = ?");
            }

            if (search != null && !search.trim().isEmpty()) {
                sql.append(" AND (c.FullName LIKE ? OR r.Reason LIKE ? OR CAST(r.RefundID AS VARCHAR) = ?)");
            }

            ps = conn.prepareStatement(sql.toString());
            int paramIndex = 1;

            if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("all")) {
                ps.setString(paramIndex++, status);
            }

            if (search != null && !search.trim().isEmpty()) {
                String searchParam = "%" + search.trim() + "%";
                ps.setString(paramIndex++, searchParam);
                ps.setString(paramIndex++, searchParam);
                ps.setString(paramIndex++, search.trim());
            }

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
     * Gets refund requests filtered by status and search text.
     *
     * @param status The status to filter by (or "all")
     * @param search The search text to filter by
     * @param page The page number
     * @param pageSize The number of items per page
     * @return List of matching RefundRequest objects
     */
    public List<RefundRequest> getByStatusAndSearch(String status, String search, int page, int pageSize) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<RefundRequest> refunds = new ArrayList<>();

        try {
            conn = getConnection();
            StringBuilder sql = new StringBuilder(
                    "SELECT r.*, c.FullName as CustomerName, "
                    + "o.TotalAmount as OriginalAmount, a.FullName as AdminName, "
                    + "STUFF((SELECT ', ' + co.Name "
                    + "       FROM OrderDetails od "
                    + "       JOIN Courses co ON od.CourseID = co.CourseID "
                    + "       WHERE od.OrderID = r.OrderID "
                    + "       FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)'), 1, 2, '') AS CourseName "
                    + "FROM RefundRequests r "
                    + "JOIN Customers c ON r.CustomerID = c.CustomerID "
                    + "JOIN Orders o ON r.OrderID = o.OrderID "
                    + "LEFT JOIN SuperUsers a ON r.ProcessedBy = a.SuperUserID WHERE 1=1");

            if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("all")) {
                sql.append(" AND r.Status = ?");
            }

            if (search != null && !search.trim().isEmpty()) {
                sql.append(" AND (c.FullName LIKE ? OR STUFF((SELECT ', ' + co.Name "
                        + "       FROM OrderDetails od "
                        + "       JOIN Courses co ON od.CourseID = co.CourseID "
                        + "       WHERE od.OrderID = r.OrderID "
                        + "       FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)'), 1, 2, '') LIKE ?)");
            }

            sql.append(" ORDER BY r.RequestDate DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

            ps = conn.prepareStatement(sql.toString());
            int paramIndex = 1;

            if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("all")) {
                ps.setString(paramIndex++, status);
            }

            if (search != null && !search.trim().isEmpty()) {
                String searchParam = "%" + search.trim() + "%";
                ps.setString(paramIndex++, searchParam);
                ps.setString(paramIndex++, searchParam);
            }

            ps.setInt(paramIndex++, (page - 1) * pageSize);
            ps.setInt(paramIndex, pageSize);
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
     * Gets the total number of refund requests.
     *
     * @return The total number of refund requests
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
     * Gets refund requests filtered by status.
     *
     * @param status The status to filter by
     * @param page The page number
     * @param pageSize The number of items per page
     * @return List of matching RefundRequest objects
     */
    public List<RefundRequest> getByStatus(String status, int page, int pageSize) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<RefundRequest> refunds = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT r.*, c.FullName as CustomerName, "
                    + "o.TotalAmount as OriginalAmount, a.FullName as AdminName, "
                    + "STUFF((SELECT ', ' + co.Name "
                    + "       FROM OrderDetails od "
                    + "       JOIN Courses co ON od.CourseID = co.CourseID "
                    + "       WHERE od.OrderID = r.OrderID "
                    + "       FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)'), 1, 2, '') AS CourseName "
                    + "FROM RefundRequests r "
                    + "JOIN Customers c ON r.CustomerID = c.CustomerID "
                    + "JOIN Orders o ON r.OrderID = o.OrderID "
                    + "LEFT JOIN SuperUsers a ON r.ProcessedBy = a.SuperUserID "
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
     * Gets refund requests by customer.
     *
     * @param customerId The ID of the customer
     * @return List of RefundRequest objects for the customer
     */
    public List<RefundRequest> getByUser(int customerId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<RefundRequest> refunds = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT r.*, c.FullName as CustomerName, "
                    + "o.TotalAmount as OriginalAmount, a.FullName as AdminName, "
                    + "STUFF((SELECT ', ' + co.Name "
                    + "       FROM OrderDetails od "
                    + "       JOIN Courses co ON od.CourseID = co.CourseID "
                    + "       WHERE od.OrderID = r.OrderID "
                    + "       FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)'), 1, 2, '') AS CourseName "
                    + "FROM RefundRequests r "
                    + "JOIN Customers c ON r.CustomerID = c.CustomerID "
                    + "JOIN Orders o ON r.OrderID = o.OrderID "
                    + "LEFT JOIN SuperUsers a ON r.ProcessedBy = a.SuperUserID "
                    + "WHERE r.CustomerID = ? "
                    + "ORDER BY r.RequestDate DESC";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);
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
     * Checks if a customer has a pending refund request for an order
     *
     * @param customerId The customer ID
     * @param orderId The order ID
     * @return true if there's a pending refund request, false otherwise
     */
    public boolean hasPendingRefundForOrder(int customerId, int orderId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean hasPending = false;

        try {
            conn = getConnection();
            String sql = "SELECT COUNT(*) FROM RefundRequests WHERE CustomerID = ? AND OrderID = ? AND Status = 'pending'";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);
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
     * Checks if a customer has an approved refund request for an order
     *
     * @param customerId The customer ID
     * @param orderId The order ID
     * @return true if there's an approved refund request, false otherwise
     */
    public boolean hasApprovedRefundForOrder(int customerId, int orderId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean hasApproved = false;

        try {
            conn = getConnection();
            String sql = "SELECT COUNT(*) FROM RefundRequests WHERE CustomerID = ? AND OrderID = ? AND Status = 'approved'";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);
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

        try {
            refund.setCustomerID(rs.getInt("CustomerID"));
        } catch (SQLException e) {
            // For backward compatibility
            try {
                refund.setCustomerID(rs.getInt("CustomerID"));
            } catch (SQLException ex) {
                // Ignore if neither exists
            }
        }

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
            refund.setCustomerName(rs.getString("CustomerName"));
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
     * Gets all refund requests with a limit.
     *
     * @param limit The maximum number of records to return
     * @return Limited list of RefundRequest objects
     */
    public List<RefundRequest> getAllRefundRequestsWithLimit(int limit) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<RefundRequest> refunds = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT TOP(?) r.*, c.FullName as CustomerName, "
                    + "o.TotalAmount as OriginalAmount, a.FullName as AdminName, "
                    + "STUFF((SELECT ', ' + co.Name "
                    + "       FROM OrderDetails od "
                    + "       JOIN Courses co ON od.CourseID = co.CourseID "
                    + "       WHERE od.OrderID = r.OrderID "
                    + "       FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)'), 1, 2, '') AS CourseName "
                    + "FROM RefundRequests r "
                    + "JOIN Customers c ON r.CustomerID = c.CustomerID "
                    + "JOIN Orders o ON r.OrderID = o.OrderID "
                    + "LEFT JOIN SuperUsers a ON r.ProcessedBy = a.SuperUserID "
                    + "ORDER BY r.RequestDate DESC";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, limit);
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
     * Checks if a user has a pending refund request for a specific course
     * 
     * @param customerId   The customer ID
     * @param courseId The course ID
     * @return true if there's a pending refund request, false otherwise
     */
    public boolean hasPendingRefundForCourse(int customerId, int courseId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean hasPending = false;

        try {
            conn = DBContext.getConnection();
            // Join with OrderDetails to find refund requests for this specific course
            String sql = "SELECT COUNT(*) FROM RefundRequests r " +
                    "JOIN Orders o ON r.OrderID = o.OrderID " +
                    "JOIN OrderDetails od ON o.OrderID = od.OrderID " +
                    "WHERE r.CustomerID = ? AND od.CourseID = ? AND r.Status = 'pending'";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);
            ps.setInt(2, courseId);
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
     * Checks if a user has an approved refund request for a specific course
     * 
     * @param customerId   The customer ID
     * @param courseId The course ID
     * @return true if there's an approved refund request, false otherwise
     */
    public boolean hasApprovedRefundForCourse(int customerId, int courseId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean hasApproved = false;

        try {
            conn = DBContext.getConnection();
            // Join with OrderDetails to find refund requests for this specific course
            String sql = "SELECT COUNT(*) FROM RefundRequests r " +
                    "JOIN Orders o ON r.OrderID = o.OrderID " +
                    "JOIN OrderDetails od ON o.OrderID = od.OrderID " +
                    "WHERE r.CustomerID = ? AND od.CourseID = ? AND r.Status = 'approved'";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);
            ps.setInt(2, courseId);
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
}
