/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import db.DBContext;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.CourseProgress;
import model.Customer;
import model.LessonItemProgress;
import model.LessonProgress;

/**
 * Data Access Object for Customer entity.
 *
 * @author DangPH - CE180896
 */
public class CustomerDAO extends DBContext {

    /**
     * Insert a new customer into the database.
     *
     * @param customer The customer to insert
     * @return The ID of the inserted customer, or -1 if insertion failed
     */
    public int insertCustomer(Customer customer) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int customerId = -1;

        try {
            conn = getConnection();
            String sql = "INSERT INTO Customers (Username, Password, Email, IsActive, FullName, Phone, Address, Avatar, AuthProvider, AuthProviderId, Token) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, customer.getUsername());
            ps.setString(2, customer.getPassword());
            ps.setString(3, customer.getEmail());
            ps.setBoolean(4, customer.isActive());
            ps.setString(5, customer.getFullName());
            ps.setString(6, customer.getPhone());
            ps.setString(7, customer.getAddress());
            ps.setString(8, customer.getAvatar());
            ps.setString(9, customer.getAuthProvider());
            ps.setString(10, customer.getAuthProviderId());
            ps.setString(11, customer.getToken()); // Thêm dòng này

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    customerId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return customerId;
    }

    /**
     * Delete a customer from the database.
     *
     * @param customerId The ID of the customer to delete
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteCustomer(int customerId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "DELETE FROM Customers WHERE CustomerID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(rs, ps, conn);
        }
    }

    /**
     * Get a customer by ID.
     *
     * @param customerId The ID of the customer
     * @return The customer, or null if not found
     */
    public Customer getCustomerById(int customerId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Customer customer = null;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM Customers WHERE CustomerID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);

            rs = ps.executeQuery();
            if (rs.next()) {
                customer = mapCustomer(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return customer;
    }

    /**
     * Get a customer by username.
     *
     * @param username The username of the customer
     * @return The customer, or null if not found
     */
    public Customer getCustomerByUsername(String username) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Customer customer = null;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM Customers WHERE Username = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, username);

            rs = ps.executeQuery();
            if (rs.next()) {
                customer = mapCustomer(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return customer;
    }

    /**
     * Get a customer by email.
     *
     * @param email The email of the customer
     * @return The customer, or null if not found
     */
    public Customer getCustomerByEmail(String email) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Customer customer = null;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM Customers WHERE Email = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, email);

            rs = ps.executeQuery();
            if (rs.next()) {
                customer = mapCustomer(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return customer;
    }

    /**
     * Update customer information in the database.
     *
     * @param customer The customer to update
     * @return true if update successful, false otherwise
     */
    public boolean updateCustomer(Customer customer) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            // If the password is not null, then update.
            if (customer.getPassword() != null) {
                String sql = "UPDATE Customers SET Username = ?, Password = ?, Email = ?, IsActive = ?, "
                        + "FullName = ?, Phone = ?, Address = ?, Avatar = ? WHERE CustomerID = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, customer.getUsername());
                ps.setString(2, customer.getPassword());
                ps.setString(3, customer.getEmail());
                ps.setBoolean(4, customer.isActive());
                ps.setString(5, customer.getFullName());
                ps.setString(6, customer.getPhone());
                ps.setString(7, customer.getAddress());
                ps.setString(8, customer.getAvatar());
                ps.setInt(9, customer.getCustomerID());
            } else {
                // If password == null then do not update the Password column.
                String sql = "UPDATE Customers SET Username = ?, Email = ?, IsActive = ?, "
                        + "FullName = ?, Phone = ?, Address = ?, Avatar = ? WHERE CustomerID = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, customer.getUsername());
                ps.setString(2, customer.getEmail());
                ps.setBoolean(3, customer.isActive());
                ps.setString(4, customer.getFullName());
                ps.setString(5, customer.getPhone());
                ps.setString(6, customer.getAddress());
                ps.setString(7, customer.getAvatar());
                ps.setInt(8, customer.getCustomerID());
            }

            int rowsAffected = ps.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(rs, ps, conn);
        }
    }

    /**
     * Update customer password in the database.
     *
     * @param customerId  The ID of the customer
     * @param newPassword The new password (already encrypted)
     * @return true if update successful, false otherwise
     */
    public boolean changePassword(int customerId, String newPassword) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "UPDATE Customers SET Password = ? WHERE CustomerID = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, newPassword);
            ps.setInt(2, customerId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(rs, ps, conn);
        }
    }

    /**
     * Get all customers.
     *
     * @return List of all customers
     */
    public List<Customer> getAllCustomers() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Customer> customers = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT * FROM Customers";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Customer customer = mapCustomer(rs);
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return customers;
    }

    /**
     * Check if username already exists.
     *
     * @param username The username to check
     * @return true if exists, false otherwise
     */
    public boolean usernameExists(String username) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT COUNT(*) FROM Customers WHERE Username = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, username);

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
     * Check if email already exists.
     *
     * @param email The email to check
     * @return true if exists, false otherwise
     */
    public boolean emailExists(String email) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT COUNT(*) FROM Customers WHERE Email = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, email);

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
     * Count total number of customers in the database.
     *
     * @return The total number of customers
     */
    public int countCustomers() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT COUNT(*) Total FROM Customers";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("Total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return -1;
    }

    /**
     * Map ResultSet to Customer object including social login fields.
     */
    private Customer mapCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerID(rs.getInt("CustomerID"));
        customer.setUsername(rs.getString("Username"));
        customer.setPassword(rs.getString("Password"));
        customer.setEmail(rs.getString("Email"));
        customer.setActive(rs.getBoolean("IsActive"));
        customer.setFullName(rs.getString("FullName"));
        customer.setPhone(rs.getString("Phone"));
        customer.setAddress(rs.getString("Address"));
        customer.setAvatar(rs.getString("Avatar"));
        customer.setToken(rs.getString("Token"));

        // Handle potential null values for auth provider columns
        String authProvider = rs.getString("AuthProvider");
        customer.setAuthProvider(authProvider != null ? authProvider : "local");

        String authProviderId = rs.getString("AuthProviderId");
        customer.setAuthProviderId(authProviderId);

        return customer;
    }

    public boolean activateCustomerByEmailAndToken(String email, String token) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            String sql = "UPDATE Customers SET IsActive = 1, Token = NULL WHERE Email = ? AND Token = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, token);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    public List<CourseProgress> getCourseProgressByCustomerId(int customerId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<CourseProgress> progressList = new ArrayList<>();
        String query = "SELECT cp.[ProgressID], cp.[CustomerID], cp.[CourseID], cp.[LastAccessDate], cp.[CompletionPercentage], cp.[IsCompleted], c.[Name] AS CourseName "
                + "FROM [LightHouseCourse].[dbo].[CourseProgress] cp "
                + "LEFT JOIN [LightHouseCourse].[dbo].[Courses] c ON cp.[CourseID] = c.[CourseID] "
                + "WHERE cp.[CustomerID] = ?";

        try {
            conn = getConnection();
            if (conn == null) {
                System.out.println("Connection is null!");
                return progressList;
            }
            ps = conn.prepareStatement(query);
            ps.setInt(1, customerId);
            rs = ps.executeQuery();

            while (rs.next()) {
                CourseProgress cp = new CourseProgress();
                cp.setProgressID(rs.getInt("ProgressID"));
                cp.setCustomerID(rs.getInt("CustomerID"));
                cp.setCourseID(rs.getInt("CourseID"));
                cp.setLastAccessDate(rs.getTimestamp("LastAccessDate"));
                cp.setCompletionPercentage(BigDecimal.valueOf(rs.getFloat("CompletionPercentage")));
                cp.setCompleted(rs.getBoolean("IsCompleted"));
                cp.setCourseName(rs.getString("CourseName") != null ? rs.getString("CourseName") : "Not Available"); // Lấy
                                                                                                                     // từ
                                                                                                                     // alias
                                                                                                                     // CourseName
                progressList.add(cp);

                // Log chi tiết
                System.out.println("Fetched: ProgressID=" + cp.getProgressID() + ", CourseID=" + cp.getCourseID()
                        + ", CourseName=" + cp.getCourseName() + ", Completion=" + cp.getCompletionPercentage());
            }
            if (progressList.isEmpty()) {
                System.out.println("No course progress found for CustomerID: " + customerId);
            }
        } catch (SQLException e) {
            System.out.println("SQL Error getting course progress: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }
        return progressList;
    }

    public List<LessonProgress> getLessonProgressByCustomerId(int customerId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<LessonProgress> progressList = new ArrayList<>();
        String query = "SELECT [ProgressID], [CustomerID], [LessonID], [IsCompleted], [CompletionPercentage], [CompletionDate], [LastAccessDate] "
                + "FROM [LightHouseCourse].[dbo].[LessonProgress] WHERE [CustomerID] = ?";
        try {
            conn = getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, customerId);
            rs = ps.executeQuery();
            while (rs.next()) {
                LessonProgress lp = new LessonProgress();
                lp.setProgressID(rs.getInt("ProgressID"));
                lp.setCustomerID(rs.getInt("CustomerID"));
                lp.setLessonID(rs.getInt("LessonID"));
                lp.setIsCompleted(rs.getBoolean("IsCompleted"));
                lp.setCompletionPercentage(rs.getBigDecimal("CompletionPercentage"));
                lp.setCompletionDate(rs.getTimestamp("CompletionDate"));
                lp.setLastAccessDate(rs.getTimestamp("LastAccessDate"));
                progressList.add(lp);
            }
        } catch (SQLException e) {
            System.out.println("Error getting lesson progress: " + e.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }
        return progressList;
    }

    public List<LessonItemProgress> getLessonItemProgressByCustomerId(int customerId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<LessonItemProgress> progressList = new ArrayList<>();
        String query = "SELECT [ProgressID], [CustomerID], [LessonItemID], [IsCompleted], [CompletionDate], [LastAccessDate] "
                + "FROM [LightHouseCourse].[dbo].[LessonItemProgress] WHERE [CustomerID] = ?";
        try {
            conn = getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, customerId);
            rs = ps.executeQuery();
            while (rs.next()) {
                LessonItemProgress lip = new LessonItemProgress();
                lip.setProgressID(rs.getInt("ProgressID"));
                lip.setCustomerID(rs.getInt("CustomerID"));
                lip.setLessonItemID(rs.getInt("LessonItemID"));
                lip.setCompleted(rs.getBoolean("IsCompleted"));
                lip.setCompletionDate(rs.getTimestamp("CompletionDate"));
                lip.setLastAccessDate(rs.getTimestamp("LastAccessDate"));
                progressList.add(lip);
            }
        } catch (SQLException e) {
            System.out.println("Error getting lesson item progress: " + e.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }
        return progressList;
    }

    /**
     * Get lesson progress by customer ID and course ID.
     *
     * @param customerId The ID of the customer
     * @param courseId   The ID of the course
     * @return List of lesson progress for the specified customer and course
     */
    public List<LessonProgress> getLessonProgressByCustomerIdAndCourseId(int customerId, int courseId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<LessonProgress> progressList = new ArrayList<>();
        String query = "SELECT lp.[ProgressID], lp.[CustomerID], lp.[LessonID], lp.[IsCompleted], lp.[CompletionPercentage], lp.[CompletionDate], lp.[LastAccessDate] "
                + "FROM [LightHouseCourse].[dbo].[LessonProgress] lp "
                + "JOIN [LightHouseCourse].[dbo].[Lessons] l ON lp.LessonID = l.LessonID "
                + "WHERE lp.[CustomerID] = ? AND l.[CourseID] = ?";

        try {
            conn = getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, customerId);
            ps.setInt(2, courseId);
            rs = ps.executeQuery();
            while (rs.next()) {
                LessonProgress lp = new LessonProgress();
                lp.setProgressID(rs.getInt("ProgressID"));
                lp.setCustomerID(rs.getInt("CustomerID"));
                lp.setLessonID(rs.getInt("LessonID"));
                lp.setIsCompleted(rs.getBoolean("IsCompleted"));
                lp.setCompletionPercentage(rs.getBigDecimal("CompletionPercentage"));
                lp.setCompletionDate(rs.getTimestamp("CompletionDate"));
                lp.setLastAccessDate(rs.getTimestamp("LastAccessDate"));
                progressList.add(lp);
            }
        } catch (SQLException e) {
            System.out.println("Error getting lesson progress by customer and course: " + e.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }
        return progressList;
    }

    /**
     * Get lesson item progress by customer ID and course ID.
     *
     * @param customerId The ID of the customer
     * @param courseId   The ID of the course
     * @return List of lesson item progress for the specified customer and
     *         course
     */
    public List<LessonItemProgress> getLessonItemProgressByCustomerIdAndCourseId(int customerId, int courseId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<LessonItemProgress> progressList = new ArrayList<>();
        String query = "SELECT lip.[ProgressID], lip.[CustomerID], lip.[LessonItemID], lip.[IsCompleted], lip.[CompletionDate], lip.[LastAccessDate] "
                + "FROM [LightHouseCourse].[dbo].[LessonItemProgress] lip "
                + "JOIN [LightHouseCourse].[dbo].[LessonItems] li ON lip.LessonItemID = li.LessonItemID "
                + "JOIN [LightHouseCourse].[dbo].[Lessons] l ON li.LessonID = l.LessonID "
                + "WHERE lip.[CustomerID] = ? AND l.[CourseID] = ?";

        try {
            conn = getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, customerId);
            ps.setInt(2, courseId);
            rs = ps.executeQuery();
            while (rs.next()) {
                LessonItemProgress lip = new LessonItemProgress();
                lip.setProgressID(rs.getInt("ProgressID"));
                lip.setCustomerID(rs.getInt("CustomerID"));
                lip.setLessonItemID(rs.getInt("LessonItemID"));
                lip.setCompleted(rs.getBoolean("IsCompleted"));
                lip.setCompletionDate(rs.getTimestamp("CompletionDate"));
                lip.setLastAccessDate(rs.getTimestamp("LastAccessDate"));
                progressList.add(lip);
            }
        } catch (SQLException e) {
            System.out.println("Error getting lesson item progress by customer and course: " + e.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }
        return progressList;
    }

    /**
     * Lấy danh sách Customer dựa trên InstructorID
     *
     * @param instructorId ID của Instructor
     * @return Danh sách Customer liên quan đến InstructorID
     * @throws SQLException Nếu có lỗi truy vấn cơ sở dữ liệu
     */
    public List<Customer> getCustomersByInstructorID(int instructorId) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Customer> customers = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT DISTINCT cu.CustomerID, cu.FullName, cu.Email, cu.Username, cu.IsActive, cu.Phone, cu.Address, cu.Avatar "
                    + "FROM dbo.Customers cu "
                    + "JOIN dbo.Orders o ON cu.CustomerID = o.CustomerID "
                    + "JOIN dbo.OrderDetails od ON o.OrderID = od.OrderID "
                    + "JOIN dbo.Courses c ON od.CourseID = c.CourseID "
                    + "JOIN dbo.CourseInstructors ci ON c.CourseID = ci.CourseID "
                    + "WHERE ci.InstructorID = ?";
            System.out.println("Preparing query for instructorId: " + instructorId);
            ps = conn.prepareStatement(sql);
            ps.setInt(1, instructorId);
            rs = ps.executeQuery();
            System.out.println("Query executed for instructorId: " + instructorId);

            while (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerID(rs.getInt("CustomerID"));
                customer.setFullName(rs.getString("FullName"));
                customer.setEmail(rs.getString("Email"));
                customer.setUsername(rs.getString("Username"));
                customer.setActive(rs.getBoolean("IsActive"));
                customer.setPhone(rs.getString("Phone"));
                customer.setAddress(rs.getString("Address"));
                customer.setAvatar(rs.getString("Avatar"));
                customers.add(customer);
            }
            System.out.println("Found " + customers.size() + " customers for instructorId: " + instructorId);
        } catch (SQLException ex) {
            System.out.println("SQLException in getCustomersByInstructorID: " + ex.getMessage());
            throw ex;
        } finally {
            closeResources(rs, ps, conn);
        }

        return customers;
    }

    /**
     * Authenticate a customer.
     *
     * @param username The username
     * @param password The encrypted password
     * @return The authenticated user, or null if authentication failed
     */
    public Customer authenticate(String username, String password) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Customer user = null;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM Customers WHERE Username = ? AND Password = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);

            rs = ps.executeQuery();
            if (rs.next()) {
                user = mapCustomer(rs);

                // Check if user is active
                if (!user.isActive()) {
                    return null; // User account is disabled
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return user;
    }

    /**
     * Find a customer by their social provider ID.
     *
     * @param provider   The authentication provider (e.g., "google", "facebook")
     * @param providerId The ID from the provider
     * @return Customer object if found, null otherwise
     */
    public Customer findBySocialId(String provider, String providerId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Customer user = null;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM Customers WHERE AuthProvider = ? AND AuthProviderId = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, provider);
            ps.setString(2, providerId);

            rs = ps.executeQuery();
            if (rs.next()) {
                user = mapCustomer(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return user;
    }

    /**
     * Process a social login - either find existing user or create new one.
     *
     * @param customer The customer object from social authentication
     * @return Customer object with database ID if successful, null otherwise
     */
    public Customer processocialLogin(Customer customer) {
        // Check if user already exists with this social ID
        Customer existingCustomer = findBySocialId(customer.getAuthProvider(), customer.getAuthProviderId());

        if (existingCustomer != null) {
            // User exists - return the existing user
            return existingCustomer;
        } else {
            // Check if email exists but not linked to this social account
            Customer emailUser = getCustomerByEmail(customer.getEmail());
            if (emailUser != null) {
                // Update existing user with social details
                emailUser.setAuthProvider(customer.getAuthProvider());
                emailUser.setAuthProviderId(customer.getAuthProviderId());

                // Update in database
                if (updateSocialDetails(emailUser)) {
                    return emailUser;
                } else {
                    return null;
                }
            } else {
                // New user - insert into database
                int customerID = insertCustomer(customer);
                if (customerID > 0) {
                    customer.setCustomerID(customerID);
                    return customer;
                } else {
                    return null;
                }
            }
        }
    }

    /**
     * Update a customer's social login details.
     *
     * @param customer The customer to update customer
     * @return true if update customer successful, false otherwise
     */
    public boolean updateSocialDetails(Customer customer) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "UPDATE Customers SET AuthProvider = ?, AuthProviderId = ? WHERE CustomerID = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, customer.getAuthProvider());
            ps.setString(2, customer.getAuthProviderId());
            ps.setInt(3, customer.getCustomerID());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(rs, ps, conn);
        }
    }
}
