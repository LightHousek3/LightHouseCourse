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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Course;
import model.OrderDetail;

/**
 *
 * @author DangPH - CE180896
 */
public class OrderDetailDAO extends DBContext {
    private CourseDAO courseDAO;

    public OrderDetailDAO() {
        this.courseDAO = new CourseDAO();
    }

    /**
     * Get the number of times a course has been purchased
     * 
     * @param courseID The ID of the course
     * @return The number of purchases
     * @throws SQLException If a database error occurs
     */
    public int getCoursePurchaseCount(int courseID) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;

        try {
            conn = getConnection();
            String sql = "SELECT COUNT(*) FROM OrderDetails od " +
                    "JOIN Orders o ON od.OrderID = o.OrderID " +
                    "WHERE od.CourseID = ? AND o.Status = 'completed'";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, courseID);

            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } finally {
            closeResources(rs, ps, conn);
        }

        return count;
    }

    /**
     * Get purchase counts for all courses by year
     * 
     * @param year The year to filter by (or 0 for all years)
     * @return Map of course names to purchase counts
     * @throws SQLException If a database error occurs
     */
    public Map<String, Integer> getCoursePurchaseCountsByYear(int year) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<String, Integer> purchaseCounts = new HashMap<>();

        try {
            conn = getConnection();
            String sql = "SELECT c.Name, COUNT(*) as PurchaseCount FROM OrderDetails od " +
                    "JOIN Orders o ON od.OrderID = o.OrderID " +
                    "JOIN Courses c ON od.CourseID = c.CourseID " +
                    "WHERE o.Status = 'completed' ";

            if (year > 0) {
                sql += "AND YEAR(o.OrderDate) = ? ";
            }

            sql += "GROUP BY c.CourseID, c.Name ORDER BY c.Name";

            ps = conn.prepareStatement(sql);

            if (year > 0) {
                ps.setInt(1, year);
            }

            rs = ps.executeQuery();
            while (rs.next()) {
                purchaseCounts.put(rs.getString("Name"), rs.getInt("PurchaseCount"));
            }
        } finally {
            closeResources(rs, ps, conn);
        }

        return purchaseCounts;
    }

    /**
     * Get purchase counts for all courses by month for a specific year
     * 
     * @param year The year to filter by
     * @return Map where key is course name and value is array of 12 monthly counts
     * @throws SQLException If a database error occurs
     */
    public Map<String, int[]> getCoursePurchaseCountsByMonth(int year) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<String, int[]> purchaseCountsByMonth = new HashMap<>();

        // First get all courses to ensure we have entries for each course
        List<Course> courses = courseDAO.getAllCourses();
        for (Course course : courses) {
            purchaseCountsByMonth.put(course.getName(), new int[12]); // Initialize array for each month (0-11)
        }

        try {
            conn = getConnection();
            String sql = "SELECT c.Name, MONTH(o.OrderDate) as PurchaseMonth, COUNT(*) as PurchaseCount " +
                    "FROM OrderDetails od " +
                    "JOIN Orders o ON od.OrderID = o.OrderID " +
                    "JOIN Courses c ON od.CourseID = c.CourseID " +
                    "WHERE o.Status = 'completed' AND YEAR(o.OrderDate) = ? " +
                    "GROUP BY c.CourseID, c.Name, MONTH(o.OrderDate) " +
                    "ORDER BY c.Name, MONTH(o.OrderDate)";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, year);

            rs = ps.executeQuery();
            while (rs.next()) {
                String courseName = rs.getString("Name");
                int month = rs.getInt("PurchaseMonth") - 1; // Convert 1-12 to 0-11
                int count = rs.getInt("PurchaseCount");

                // Update the month count for this course
                if (purchaseCountsByMonth.containsKey(courseName)) {
                    purchaseCountsByMonth.get(courseName)[month] = count;
                }
            }
        } finally {
            closeResources(rs, ps, conn);
        }

        return purchaseCountsByMonth;
    }

    /**
     * Get list of distinct years that have orders
     * 
     * @return List of years
     * @throws SQLException If a database error occurs
     */
    public List<Integer> getOrderYears() throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Integer> years = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT DISTINCT YEAR(OrderDate) as Year FROM Orders WHERE Status = 'completed' ORDER BY Year DESC";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                years.add(rs.getInt("Year"));
            }
        } finally {
            closeResources(rs, ps, conn);
        }

        return years;
    }

    /**
     * Get all order details for a specific order
     * 
     * @param orderID The ID of the order
     * @return List of order details
     * @throws SQLException If a database error occurs
     */
    public List<OrderDetail> getOrderDetailsByOrderId(int orderID) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<OrderDetail> orderDetails = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT * FROM OrderDetails WHERE OrderID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, orderID);

            rs = ps.executeQuery();
            while (rs.next()) {
                OrderDetail detail = mapOrderDetail(rs);

                // Load course information
                Course course = courseDAO.getCourseById(detail.getCourseID());
                detail.setCourse(course);

                orderDetails.add(detail);
            }
        } finally {
            closeResources(rs, ps, conn);
        }

        return orderDetails;
    }

    /**
     * Map a ResultSet row to an OrderDetail object
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
