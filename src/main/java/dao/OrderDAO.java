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
import model.PaymentTransaction;
import db.DBContext;
import static db.DBContext.closeResources;
import static db.DBContext.getConnection;
import java.sql.Statement;
import model.Instructor;
import java.util.HashMap;
import java.util.Map;

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
     * Insert a new order into the database.
     *
     * @param order The order to insert
     * @return The generated order ID, or -1 if insertion failed
     */
    public int insertOrder(Order order) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int orderId = -1;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Start transaction

            String sql = "INSERT INTO Orders (CustomerID, OrderDate, TotalAmount, Status) "
                    + "VALUES (?, ?, ?, ?)";

            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, order.getCustomerID());
            ps.setTimestamp(2, order.getOrderDate());
            ps.setDouble(3, order.getTotalAmount());
            ps.setString(4, order.getStatus());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    orderId = rs.getInt(1);

                    // Insert order details
                    if (order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
                        insertOrderDetails(conn, orderId, order.getOrderDetails());
                    }

                    conn.commit();
                }
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

        return orderId;
    }

    /**
     * Insert order details into the database.
     *
     * @param conn    The database connection
     * @param orderId The ID of the order
     * @param details The list of order details to insert
     * @throws SQLException If a database error occurs
     */
    private void insertOrderDetails(Connection conn, int orderId, List<OrderDetail> details) throws SQLException {
        PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO OrderDetails (OrderID, CourseID, Price) VALUES (?, ?, ?)";
            ps = conn.prepareStatement(sql);

            for (OrderDetail detail : details) {
                ps.setInt(1, orderId);
                ps.setInt(2, detail.getCourseID());
                ps.setDouble(3, detail.getPrice());
                ps.addBatch();
            }

            ps.executeBatch();
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    /**
     * Get orders by user ID.
     *
     * @param userID The ID of the user
     * @return List of orders for the user
     */
    public List<Order> getOrdersByUserId(int customerId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Order> orders = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT * FROM Orders WHERE CustomerID = ? ORDER BY OrderDate DESC";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);

            rs = ps.executeQuery();
            while (rs.next()) {
                Order order = mapOrder(rs);
                orders.add(order);
            }

            // Get details for each order
            for (Order order : orders) {
                List<OrderDetail> details = getOrderDetails(order.getOrderID());
                order.setOrderDetails(details);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return orders;
    }

    /**
     * Get an order by ID.
     *
     * @param orderId The ID of the order
     * @return The order, or null if not found
     */
    public Order getOrderById(int orderId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Order order = null;

        try {
            conn = getConnection();
            String sql = "SELECT o.* FROM Orders o WHERE o.OrderID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, orderId);

            rs = ps.executeQuery();
            if (rs.next()) {
                order = mapOrder(rs);

                // Get order details
                List<OrderDetail> details = getOrderDetails(orderId);
                order.setOrderDetails(details);

                // Get customer info instead of user
                Customer customer = customerDAO.getCustomerById(order.getCustomerID());
                if (customer != null) {
                    order.setUserName(customer.getFullName()); // Reuse userName for display
                    order.setCustomer(customer); // Optional: store full object
                }

                // Get payment transaction info
                PaymentTransactionDAO transactionDAO = new PaymentTransactionDAO();
                List<PaymentTransaction> transactions = transactionDAO.getByOrderId(orderId);
                if (transactions != null && !transactions.isEmpty()) {
                    PaymentTransaction latestTransaction = transactions.get(0); // Latest transaction
                    order.setPaymentMethod(latestTransaction.getProvider());
                    order.setPaymentTransactionID(latestTransaction.getProviderTransactionID());
                    order.setAttribute("paymentTransaction", latestTransaction);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return order;
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

                // Lấy thông tin khóa học
                Course course = courseDAO.getCourseById(detail.getCourseID());
                detail.setCourse(course);

                // Lấy instructor (chính) của khóa học này
                List<Instructor> instructors = courseDAO.getInstructorsForCourse(detail.getCourseID());
                System.out.println(instructors);
                detail.setAttribute("instructor", instructors); // Gắn instructor vào attribute

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
        Map<Integer, Object[]> latestCourseOrders = new HashMap<>(); // To store the latest order for each course

        try {
            conn = getConnection();
            // Get all orders for the customer
            String sql = "SELECT c.CourseID, c.Name, c.Description, c.Price, c.ImageUrl, " +
                    "c.Duration, c.Level, od.*, o.OrderDate, o.Status FROM Orders o " +
                    "JOIN OrderDetails od ON o.OrderID = od.OrderID " +
                    "JOIN Courses c ON od.CourseID = c.CourseID " +
                    "WHERE o.CustomerID = ? " +
                    "ORDER BY o.OrderDate DESC";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);

            rs = ps.executeQuery();

            while (rs.next()) {
                int courseId = rs.getInt("CourseID");
                String orderStatus = rs.getString("Status");

                // Skip this order if we already have a more recent order for this course
                if (latestCourseOrders.containsKey(courseId)) {
                    continue;
                }

                // Skip if the status is not completed
                if (!"completed".equals(orderStatus)) {
                    continue;
                }

                // Create basic Course object from the result set
                Course course = new Course();
                course.setCourseID(courseId);
                course.setName(rs.getString("Name"));
                course.setDescription(rs.getString("Description"));
                course.setPrice(rs.getDouble("Price"));
                course.setImageUrl(rs.getString("ImageUrl"));
                course.setDuration(rs.getString("Duration"));
                course.setLevel(rs.getString("Level"));

                // Get full course details including instructors using CourseDAO
                Course fullCourseDetails = courseDAO.getCourseById(courseId);
                if (fullCourseDetails != null && fullCourseDetails.getInstructors() != null) {
                    course.setInstructors(fullCourseDetails.getInstructors());
                }

                // Create OrderDetail object
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrderDetailID(rs.getInt("OrderDetailID"));
                orderDetail.setOrderID(rs.getInt("OrderID"));
                orderDetail.setCourseID(courseId);
                orderDetail.setPrice(rs.getDouble("Price"));

                // Add to map as the latest order for this course
                Object[] data = { course, orderDetail };
                latestCourseOrders.put(courseId, data);
            }

            // Convert map values to list
            purchasedCourses.addAll(latestCourseOrders.values());

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
     * @param customerId The ID of the customer
     * @param courseId   The ID of the course
     * @return true if purchased, false otherwise
     */
    public boolean hasCustomerPurchasedCourse(int customerId, int courseId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            // Get the most recent order for this customer and course
            String sql = "SELECT TOP 1 o.Status FROM Orders o "
                    + "JOIN OrderDetails od ON o.OrderID = od.OrderID "
                    + "WHERE o.CustomerID = ? AND od.CourseID = ? "
                    + "ORDER BY o.OrderDate DESC";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);
            ps.setInt(2, courseId);

            rs = ps.executeQuery();
            // If the most recent order for this customer and course is 'completed' or
            // 'refund_pending',
            // then the customer has access to the course
            if (rs.next()) {
                String status = rs.getString("Status");
                return "completed".equals(status) || "refund_pending".equals(status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return false;
    }

    /**
     * Update an existing order in the database.
     * 
     * @param order The order to update
     * @return true if the update was successful, false otherwise
     */
    public boolean updateOrder(Order order) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = getConnection();
            String sql = "UPDATE Orders SET Status = ?, TotalAmount = ? WHERE OrderID = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, order.getStatus());
            ps.setDouble(2, order.getTotalAmount());
            ps.setInt(3, order.getOrderID());

            int rowsAffected = ps.executeUpdate();
            success = (rowsAffected == 1);

            // Update payment transaction if provided
            if (success && order.getPaymentMethod() != null) {
                PaymentTransactionDAO transactionDAO = new PaymentTransactionDAO();
                List<PaymentTransaction> transactions = transactionDAO.getByOrderId(order.getOrderID());

                if (transactions != null && !transactions.isEmpty()) {
                    // Already has a transaction - no need to update the provider
                    // If needed, could update other transaction details here
                } else {
                    // Create a new transaction
                    PaymentTransaction transaction = new PaymentTransaction("payment", order.getPaymentMethod());
                    transaction.setOrderID(order.getOrderID());
                    if (order.getPaymentTransactionID() != null) {
                        transaction.setProviderTransactionID(order.getPaymentTransactionID());
                    }
                    transactionDAO.insert(transaction);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(null, ps, conn);
        }

        return success;
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
