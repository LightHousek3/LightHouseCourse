/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Course;
import model.Order;
import model.OrderDetail;
import model.User;
import db.DBContext;

/**
 * Data Access Object for Order entity.
 *
 * @author DangPH - CE180896
 */
public class OrderDAO extends DBContext {

    private UserDAO userDAO;
    private CourseDAO courseDAO;

    public OrderDAO() {
        this.userDAO = new UserDAO();
        this.courseDAO = new CourseDAO();
    }

    /**
     * Get all orders with optional limit.
     *
     * @param limit the maximum number of orders to retrieve (nullable)
     * @return list of orders with their details and user info
     */
    public List<Order> getAllOrdersWithLimit(Integer limit) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Order> orders = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = (limit != null)
                    ? "SELECT TOP " + limit + " * FROM Orders ORDER BY OrderDate DESC"
                    : "SELECT * FROM Orders ORDER BY OrderDate DESC";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Order order = mapOrder(rs);
                orders.add(order);
            }

            // Get details for each order
            for (Order order : orders) {
                List<OrderDetail> details = getOrderDetails(order.getOrderID());
                order.setOrderDetails(details);

                // Get user information
                User user = userDAO.getUserById(order.getUserID());
                order.setUser(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return orders;
    }

    /**
     * Get the order details for an order.
     *
     * @param orderId The ID of the order
     * @return List of order details
     */
    private List<OrderDetail> getOrderDetails(int orderId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<OrderDetail> details = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT * FROM OrderDetails WHERE OrderID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, orderId);

            rs = ps.executeQuery();
            while (rs.next()) {
                OrderDetail detail = mapOrderDetail(rs);

                // Get course information
                Course course = courseDAO.getCourseById(detail.getCourseID());
                detail.setCourse(course);

                details.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return details;
    }

    /**
     * Map a ResultSet to an Order object
     *
     * @param rs ResultSet containing order data
     * @return Order object
     * @throws SQLException if database error occurs
     */
    private Order mapOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderID(rs.getInt("OrderID"));
        order.setUserID(rs.getInt("UserID"));
        order.setOrderDate(rs.getTimestamp("OrderDate"));
        order.setTotalAmount(rs.getDouble("TotalAmount"));
        order.setStatus(rs.getString("Status"));
        order.setPaymentMethod(rs.getString("PaymentMethod"));
        order.setPaymentTransactionID(rs.getString("PaymentTransactionID"));
        order.setPaymentData(rs.getString("PaymentData"));
        return order;
    }

    /**
     * Map a ResultSet row to an OrderDetail object.
     *
     * @param rs The ResultSet
     * @return The mapped OrderDetail
     * @throws SQLException If a database error occurs
     */
    private OrderDetail mapOrderDetail(ResultSet rs) throws SQLException {
        OrderDetail detail = new OrderDetail();
        detail.setOrderDetailID(rs.getInt("OrderDetailID"));
        detail.setOrderID(rs.getInt("OrderID"));
        detail.setCourseID(rs.getInt("CourseID"));
        detail.setPrice(rs.getDouble("Price"));
        return detail;
    }
}
