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
import java.util.ArrayList;
import java.util.List;
import model.User;

/**
 * Data Access Object for User entity.
 * @author DangPH - CE180896
 */
public class UserDAO extends DBContext {
    
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
