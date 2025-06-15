/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import db.DBContext;
import static db.DBContext.closeResources;
import static db.DBContext.getConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.User;

/**
 * Data Access Object for User entity.
 * @author DangPH - CE180896
 */
public class UserDAO extends DBContext {
    
    /**
     * Insert a new user into the database.
     * 
     * @param user The user to insert
     * @return The ID of the inserted user, or -1 if insertion failed
     */
    public int insertUser(User user) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int userId = -1;

        try {
            conn = getConnection();
            String sql = "INSERT INTO Users (Username, Password, Email, Role, IsActive, FullName, Phone, Address) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getRole());
            ps.setBoolean(5, user.isActive());
            ps.setString(6, user.getFullName());
            ps.setString(7, user.getPhone());
            ps.setString(8, user.getAddress());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    userId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return userId;
    }
    
    /**
     * Delete a user from the database.
     * 
     * @param userId The ID of the user to deleteUser
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteUser(int userId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "DELETE FROM Users WHERE UserID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);

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
     * Get a user by ID.
     * 
     * @param userId The ID of the user
     * @return The user, or null if not found
     */
    public User getUserById(int userId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        User user = null;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM Users WHERE UserID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);

            rs = ps.executeQuery();
            if (rs.next()) {
                user = mapUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return user;
    }
    
    /**
     * Get a user by username.
     * 
     * @param username The username of the user
     * @return The user, or null if not found
     */
    public User getUserByUsername(String username) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        User user = null;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM Users WHERE Username = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, username);

            rs = ps.executeQuery();
            if (rs.next()) {
                user = mapUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return user;
    }
    
    /**
     * Get a user by email.
     * 
     * @param email The email of the user
     * @return The user, or null if not found
     */
    public User getUserByEmail(String email) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        User user = null;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM Users WHERE Email = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, email);

            rs = ps.executeQuery();
            if (rs.next()) {
                user = mapUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return user;
    }
    
    /**
     * Update user information in the database.
     * 
     * @param user The user to updateUser
     * @return true if updateUser successful, false otherwise
     */
    public boolean updateUser(User user) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "UPDATE Users SET Username = ?, Email = ?, Role = ?, IsActive = ?, "
                    + "FullName = ?, Phone = ?, Address = ? WHERE UserID = ? ";

            ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getRole());
            ps.setBoolean(4, user.isActive());
            ps.setString(5, user.getFullName());
            ps.setString(6, user.getPhone());
            ps.setString(7, user.getAddress());
            ps.setInt(8, user.getUserID());

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
     * Update user password in the database.
     * 
     * @param userId      The ID of the user
     * @param newPassword The new password (already encrypted)
     * @return true if updateUser successful, false otherwise
     */
    public boolean changePassword(int userId, String newPassword) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "UPDATE Users SET Password = ? WHERE UserID = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, newPassword);
            ps.setInt(2, userId);

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
     * Get all users based on the role.
     * 
     * @param role Can be customer or instructor
     * @return The number of user with specific role
     */
    public int countUserWithRole(String role) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT COUNT(*) Total FROM Users WHERE Role = ?";
            
            ps = conn.prepareStatement(sql);
            ps.setString(1, role);
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
     * Get users by role.
     * 
     * @param role The role to filter by (e.g., "admin", "user", "instructor")
     * @return List of users with the specified role
     */
    public List<User> getUsersByRole(String role) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<User> users = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT * FROM Users WHERE Role = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, role);
            rs = ps.executeQuery();

            while (rs.next()) {
                User user = mapUser(rs);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return users;
    }
    
    /**
     * Get all users.
     * 
     * @return List of all users
     */
    public List<User> getAllUsers() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<User> users = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT * FROM Users";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                User user = mapUser(rs);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return users;
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
            String sql = "SELECT COUNT(*) FROM Users WHERE Username = ?";

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
            String sql = "SELECT COUNT(*) FROM Users WHERE Email = ?";

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
     * Map ResultSet to User object including social login fields.
     */
    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserID(rs.getInt("UserID"));
        user.setUsername(rs.getString("Username"));
        user.setPassword(rs.getString("Password"));
        user.setEmail(rs.getString("Email"));
        user.setRole(rs.getString("Role"));
        user.setActive(rs.getBoolean("IsActive"));
        user.setFullName(rs.getString("FullName"));
        user.setPhone(rs.getString("Phone"));
        user.setAddress(rs.getString("Address"));
        user.setAvatar(rs.getString("Avatar"));

        // Handle potential null values for auth provider columns
        String authProvider = rs.getString("AuthProvider");
        user.setAuthProvider(authProvider != null ? authProvider : "local");

        String authProviderId = rs.getString("AuthProviderId");
        user.setAuthProviderId(authProviderId);

        return user;
    }
}
