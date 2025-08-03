package dao;

import db.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.PaymentTransaction;

/**
 * Data Access Object for PaymentTransaction entity.
 *
 * @author DangPH - CE180896
 */
public class PaymentTransactionDAO extends DBContext {

    /**
     * Insert a new payment transaction into the database.
     *
     * @param transaction The transaction to insertPaymentTransaction
     * @return The generated transaction ID, or -1 if insertion failed
     */
    public int insertPaymentTransaction(PaymentTransaction transaction) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int transactionId = -1;

        try {
            conn = getConnection();
            String sql = "INSERT INTO PaymentTransactions (OrderID, RefundRequestID, TransactionType, Provider, " +
                    "ProviderTransactionID, BankAccountInfo, CreatedAt) VALUES (?, ?, ?, ?, ?, ?, ?)";

            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, transaction.getOrderID()); // Can be null
            ps.setObject(2, transaction.getRefundRequestID()); // Can be null
            ps.setString(3, transaction.getTransactionType());
            ps.setString(4, transaction.getProvider());
            ps.setString(5, transaction.getProviderTransactionID());
            ps.setString(6, transaction.getBankAccountInfo());
            ps.setTimestamp(7, transaction.getCreatedAt());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    transactionId = rs.getInt(1);
                    transaction.setTransactionID(transactionId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return transactionId;
    }

    /**
     * Get payment transactions by order ID.
     *
     * @param orderId The order ID to search for
     * @return The payment transaction for the order or null if not found
     */
    public PaymentTransaction getByOrderId(int orderId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        PaymentTransaction transaction = null;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM PaymentTransactions WHERE OrderID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, orderId);

            rs = ps.executeQuery();
            if (rs.next()) {
                transaction = mapPaymentTransaction(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return transaction;
    }

    /**
     * Get payment transaction by transaction ID.
     *
     * @param transactionId The transaction ID to search for
     * @return The payment transaction or null if not found
     */
    public PaymentTransaction getById(int transactionId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        PaymentTransaction transaction = null;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM PaymentTransactions WHERE TransactionID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, transactionId);

            rs = ps.executeQuery();
            if (rs.next()) {
                transaction = mapPaymentTransaction(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return transaction;
    }

    /**
     * Get payment transactions by provider transaction ID.
     *
     * @param providerTransactionId The provider transaction ID to search for
     * @return The payment transaction or null if not found
     */
    public PaymentTransaction getByProviderTransactionId(String providerTransactionId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        PaymentTransaction transaction = null;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM PaymentTransactions WHERE ProviderTransactionID = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, providerTransactionId);

            rs = ps.executeQuery();
            if (rs.next()) {
                transaction = mapPaymentTransaction(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return transaction;
    }

    /**
     * Map a result set row to a PaymentTransaction object.
     *
     * @param rs The result set containing transaction data
     * @return The mapped PaymentTransaction object
     * @throws SQLException if a database error occurs
     */
    private PaymentTransaction mapPaymentTransaction(ResultSet rs) throws SQLException {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setTransactionID(rs.getInt("TransactionID"));

        // Handle nullable fields
        int orderID = rs.getInt("OrderID");
        if (!rs.wasNull()) {
            transaction.setOrderID(orderID);
        }

        int refundRequestID = rs.getInt("RefundRequestID");
        if (!rs.wasNull()) {
            transaction.setRefundRequestID(refundRequestID);
        }

        transaction.setTransactionType(rs.getString("TransactionType"));
        transaction.setProvider(rs.getString("Provider"));
        transaction.setProviderTransactionID(rs.getString("ProviderTransactionID"));
        transaction.setBankAccountInfo(rs.getString("BankAccountInfo"));
        transaction.setCreatedAt(rs.getTimestamp("CreatedAt"));

        return transaction;
    }
}