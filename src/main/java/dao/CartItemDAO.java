package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.CartItem;
import model.Course;
import db.DBContext;

public class CartItemDAO extends DBContext {

    private CourseDAO courseDAO;

    public CartItemDAO() {
        this.courseDAO = new CourseDAO();
    }

    /**
     * Add a course to user's cart in the database
     *
     * @param customerId The customer's ID
     * @param courseId   The course's ID
     * @param price      The price of the course
     * @return true if added successfully, false otherwise
     */
    public boolean addToCart(int customerId, int courseId, double price) {
        String sql = "INSERT INTO CartItems (customerID, CourseID, Price) VALUES (?, ?, ?)";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setInt(2, courseId);
            ps.setDouble(3, price);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // Nên thay bằng log (ví dụ: SLF4J) trong production
            return false;
        }
    }

    /**
     * Remove a course from user's cart in the database
     *
     * @param customerId The customer's ID
     * @param courseId   The course's ID
     * @return true if removed successfully, false otherwise
     */
    public boolean removeFromCart(int customerId, int courseId) {
        String sql = "DELETE FROM CartItems WHERE customerID = ? AND CourseID = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setInt(2, courseId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Clear all items from user's cart in the database
     *
     * @param customerId The customer's ID
     * @return true if cleared successfully, false otherwise
     */
    public boolean clearCart(int customerId) {
        String sql = "DELETE FROM CartItems WHERE customerID = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all cart items for a user
     *
     * @param customerId The customer's ID
     * @return A list of CartItem objects
     */
    public List<CartItem> getCartItems(int customerId) {
        List<CartItem> items = new ArrayList<>();
        String sql = "SELECT cartItemId, customerID, CourseID, Price, createdAt FROM CartItems WHERE customerID = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int cartItemId = rs.getInt("cartItemId");
                    int courseId = rs.getInt("CourseID");
                    double price = rs.getDouble("Price");
                    Timestamp createdAt = rs.getTimestamp("createdAt");

                    Course course = courseDAO.getCourseById(courseId);
                    if (course != null) {
                        CartItem item = new CartItem(cartItemId, customerId, courseId, price, createdAt);
                        item.setCourse(course);
                        items.add(item);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    /**
     * Check if a course is in user's cart
     *
     * @param customerId The customer's ID
     * @param courseId   The course's ID
     * @return true if the course is in cart, false otherwise
     */
    public boolean isInCart(int customerId, int courseId) {
        String sql = "SELECT COUNT(*) FROM CartItems WHERE customerID = ? AND CourseID = ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setInt(2, courseId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
