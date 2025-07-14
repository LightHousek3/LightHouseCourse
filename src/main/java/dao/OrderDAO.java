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
import model.Customer;
import model.Order;
import model.OrderDetail;
import db.DBContext;

/**
 * Data Access Object for Order entity.
 *
 * @author DangPH - CE180896
 */
public class OrderDAO extends DBContext {

    private CustomerDAO customerDAO;
    private CourseDAO courseDAO;

    public OrderDAO() {
        this.customerDAO = new CustomerDAO();
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

                // Get customer information
                Customer customer = customerDAO.getCustomerById(order.getCustomerID());
                order.setCustomer(customer);
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
     * Get all courses purchased by a customer with their order details.
     * Returns a list of Object arrays where each array contains:
     * [0] - Course object
     * [1] - OrderDetail object with information about the purchase
     * 
     * @param customerId The ID of the customer
     * @return List of Object arrays containing Course and OrderDetail
     */
    public List<Object[]> getCustomerPurchasedCoursesWithOrderDetails(int customerId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Object[]> purchasedCourses = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT c.CourseID, c.Name, c.Description, c.Price, c.ImageUrl, " +
                    "c.Duration, c.Level, od.*, o.OrderDate FROM Orders o " +
                    "JOIN OrderDetails od ON o.OrderID = od.OrderID " +
                    "JOIN Courses c ON od.CourseID = c.CourseID " +
                    "WHERE o.CustomerID = ? AND o.Status = 'completed' " +
                    "ORDER BY o.OrderDate DESC";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);

            rs = ps.executeQuery();

            while (rs.next()) {
                // Create basic Course object from the result set
                Course course = new Course();
                course.setCourseID(rs.getInt("CourseID"));
                course.setName(rs.getString("Name"));
                course.setDescription(rs.getString("Description"));
                course.setPrice(rs.getDouble("Price"));
                course.setImageUrl(rs.getString("ImageUrl"));
                course.setDuration(rs.getString("Duration"));
                course.setLevel(rs.getString("Level"));

                // Get full course details including instructors using CourseDAO
                int courseId = rs.getInt("CourseID");
                Course fullCourseDetails = courseDAO.getCourseById(courseId);
                if (fullCourseDetails != null && fullCourseDetails.getInstructors() != null) {
                    course.setInstructors(fullCourseDetails.getInstructors());
                }

                // Create OrderDetail object
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrderDetailID(rs.getInt("OrderDetailID"));
                orderDetail.setOrderID(rs.getInt("OrderID"));
                orderDetail.setCourseID(rs.getInt("CourseID"));
                orderDetail.setPrice(rs.getDouble("Price"));

                // Add to list as Object array
                Object[] data = { course, orderDetail };
                purchasedCourses.add(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return purchasedCourses;
    }
    
    /**
     * Check if a user has purchased a course.
     * 
     * @param customerId   The ID of the customer
     * @param courseId The ID of the course
     * @return true if purchased, false otherwise
     */
    public boolean hasCustomerPurchasedCourse(int customerId, int courseId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT COUNT(*) FROM Orders o "
                    + "JOIN OrderDetails od ON o.OrderID = od.OrderID "
                    + "WHERE o.CustomerID = ? AND od.CourseID = ? AND o.Status = 'completed'";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);
            ps.setInt(2, courseId);

            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return false;
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
        order.setCustomerID(rs.getInt("CustomerID"));
        order.setOrderDate(rs.getTimestamp("OrderDate"));
        order.setTotalAmount(rs.getDouble("TotalAmount"));
        order.setStatus(rs.getString("Status"));
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
