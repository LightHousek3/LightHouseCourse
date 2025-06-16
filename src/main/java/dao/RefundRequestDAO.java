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

/**
 * Data Access Object for RefundRequest entities. Handles database operations
 * related to refund requests.
 */
public class RefundRequestDAO extends DBContext {

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
            String sql = "SELECT r.RefundID, r.OrderID, r.UserID, r.RequestDate, r.Status, r.RefundAmount, " +
                    "r.Reason, r.ProcessedDate, r.ProcessedBy, r.AdminMessage, r.RefundPercentage, " +
                    "u.Username AS UserName, o.TotalAmount AS OriginalAmount, o.OrderDate, " +
                    "admin.Username AS AdminName, " +
                    "STUFF((SELECT ', ' + c.Name " +
                    "       FROM OrderDetails od " +
                    "       JOIN Courses c ON od.CourseID = c.CourseID " +
                    "       WHERE od.OrderID = r.OrderID " +
                    "       FOR XML PATH(''), TYPE).value('.', 'NVARCHAR(MAX)'), 1, 2, '') AS CourseName " +
                    "FROM RefundRequests r " +
                    "JOIN Users u ON r.UserID = u.UserID " +
                    "JOIN Orders o ON r.OrderID = o.OrderID " +
                    "LEFT JOIN Users admin ON r.ProcessedBy = admin.UserID " +
                    "ORDER BY r.RequestDate DESC";

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
