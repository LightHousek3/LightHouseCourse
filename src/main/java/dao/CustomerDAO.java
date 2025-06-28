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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Customer;

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
            String sql = "INSERT INTO Customers (Username, Password, Email, IsActive, FullName, Phone, Address, Avatar, AuthProvider, AuthProviderId) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
            String sql = "UPDATE Customers SET Username = ?, Password = ?, Email = ?, IsActive = ?, "
                    + "FullName = ?, Phone = ?, Address = ?, Avatar = ? WHERE CustomerID = ? ";

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
     * @param customerId The ID of the customer
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
}
