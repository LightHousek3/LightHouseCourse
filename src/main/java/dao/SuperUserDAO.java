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
import model.SuperUser;

/**
 * Data Access Object for SuperUser entity.
 * 
 * @author DangPH - CE180896
 */
public class SuperUserDAO extends DBContext {

    /**
     * Insert a new superuser into the database.
     * 
     * @param superUser The superuser to insert
     * @return The ID of the inserted superuser, or -1 if insertion failed
     */
    public int insertSuperUser(SuperUser superUser) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int superUserId = -1;

        try {
            conn = getConnection();
            String sql = "INSERT INTO SuperUsers (Username, Password, Email, Role, IsActive, FullName, Phone, Address, Avatar) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, superUser.getUsername());
            ps.setString(2, superUser.getPassword());
            ps.setString(3, superUser.getEmail());
            ps.setString(4, superUser.getRole());
            ps.setBoolean(5, superUser.isActive());
            ps.setString(6, superUser.getFullName());
            ps.setString(7, superUser.getPhone());
            ps.setString(8, superUser.getAddress());
            ps.setString(9, superUser.getAvatar());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    superUserId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return superUserId;
    }

    /**
     * Delete a superuser from the database.
     * 
     * @param superUserId The ID of the superuser to delete
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteSuperUser(int superUserId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "DELETE FROM SuperUsers WHERE SuperUserID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, superUserId);

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
     * Get a superuser by ID.
     * 
     * @param superUserId The ID of the superuser
     * @return The superuser, or null if not found
     */
    public SuperUser getSuperUserById(int superUserId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        SuperUser superUser = null;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM SuperUsers WHERE SuperUserID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, superUserId);

            rs = ps.executeQuery();
            if (rs.next()) {
                superUser = mapSuperUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return superUser;
    }

    /**
     * Get a superuser by username.
     * 
     * @param username The username of the superuser
     * @return The superuser, or null if not found
     */
    public SuperUser getSuperUserByUsername(String username) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        SuperUser superUser = null;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM SuperUsers WHERE Username = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, username);

            rs = ps.executeQuery();
            if (rs.next()) {
                superUser = mapSuperUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return superUser;
    }

    /**
     * Get a superuser by email.
     * 
     * @param email The email of the superuser
     * @return The superuser, or null if not found
     */
    public SuperUser getSuperUserByEmail(String email) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        SuperUser superUser = null;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM SuperUsers WHERE Email = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, email);

            rs = ps.executeQuery();
            if (rs.next()) {
                superUser = mapSuperUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return superUser;
    }

    /**
     * Update superuser information in the database.
     * 
     * @param superUser The superuser to update
     * @return true if update successful, false otherwise
     */
    public boolean updateSuperUser(SuperUser superUser) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "UPDATE SuperUsers SET Username = ?, Email = ?, Role = ?, IsActive = ?, "
                    + "FullName = ?, Phone = ?, Address = ?, Avatar = ? WHERE SuperUserID = ? ";

            ps = conn.prepareStatement(sql);
            ps.setString(1, superUser.getUsername());
            ps.setString(2, superUser.getEmail());
            ps.setString(3, superUser.getRole());
            ps.setBoolean(4, superUser.isActive());
            ps.setString(5, superUser.getFullName());
            ps.setString(6, superUser.getPhone());
            ps.setString(7, superUser.getAddress());
            ps.setString(8, superUser.getAvatar());
            ps.setInt(9, superUser.getSuperUserID());

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
     * Update superuser password in the database.
     * 
     * @param superUserId The ID of the superuser
     * @param newPassword The new password (already encrypted)
     * @return true if update successful, false otherwise
     */
    public boolean changePassword(int superUserId, String newPassword) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "UPDATE SuperUsers SET Password = ? WHERE SuperUserID = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, newPassword);
            ps.setInt(2, superUserId);

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
     * Get all superusers based on the role.
     * 
     * @param role Can be admin or instructor
     * @return The number of superusers with specific role
     */
    public int countSuperUserWithRole(String role) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT COUNT(*) Total FROM SuperUsers WHERE Role = ?";

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
     * Get superusers by role.
     * 
     * @param role The role to filter by (e.g., "admin", "instructor")
     * @return List of superusers with the specified role
     */
    public List<SuperUser> getSuperUsersByRole(String role) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<SuperUser> superUsers = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT * FROM SuperUsers WHERE Role = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, role);
            rs = ps.executeQuery();

            while (rs.next()) {
                SuperUser superUser = mapSuperUser(rs);
                superUsers.add(superUser);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return superUsers;
    }

    /**
     * Get all superusers.
     * 
     * @return List of all superusers
     */
    public List<SuperUser> getAllSuperUsers() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<SuperUser> superUsers = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT * FROM SuperUsers";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                SuperUser superUser = mapSuperUser(rs);
                superUsers.add(superUser);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return superUsers;
    }

    /**
     * Check if username already exists in SuperUsers.
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
            String sql = "SELECT COUNT(*) FROM SuperUsers WHERE Username = ?";

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
     * Check if email already exists in SuperUsers.
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
            String sql = "SELECT COUNT(*) FROM SuperUsers WHERE Email = ?";

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
     * Map ResultSet to SuperUser object.
     */
    private SuperUser mapSuperUser(ResultSet rs) throws SQLException {
        SuperUser superUser = new SuperUser();
        superUser.setSuperUserID(rs.getInt("SuperUserID"));
        superUser.setUsername(rs.getString("Username"));
        superUser.setPassword(rs.getString("Password"));
        superUser.setEmail(rs.getString("Email"));
        superUser.setRole(rs.getString("Role"));
        superUser.setActive(rs.getBoolean("IsActive"));
        superUser.setFullName(rs.getString("FullName"));
        superUser.setPhone(rs.getString("Phone"));
        superUser.setAddress(rs.getString("Address"));
        superUser.setAvatar(rs.getString("Avatar"));

        return superUser;
    }

    /**
     * Get all active instructors.
     * 
     * @return List of active instructors
     */
    public List<SuperUser> getActiveInstructors() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<SuperUser> instructors = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT * FROM SuperUsers WHERE Role = 'instructor' AND IsActive = 1";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                SuperUser instructor = mapSuperUser(rs);
                instructors.add(instructor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return instructors;
    }

    /**
     * Get all active admins.
     * 
     * @return List of active admins
     */
    public List<SuperUser> getActiveAdmins() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<SuperUser> admins = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT * FROM SuperUsers WHERE Role = 'admin' AND IsActive = 1";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                SuperUser admin = mapSuperUser(rs);
                admins.add(admin);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return admins;
    }

    /**
     * Get recent instructors (limited by count).
     * 
     * @param limit Number of instructors to retrieve
     * @return List of recent instructors
     */
    public List<SuperUser> getRecentInstructors(int limit) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<SuperUser> instructors = new ArrayList<>();

        try {
            conn = getConnection();
            // Using TOP for MS SQL Server
            String sql = "SELECT TOP " + limit
                    + " * FROM SuperUsers WHERE Role = 'instructor' ORDER BY SuperUserID DESC";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                SuperUser instructor = mapSuperUser(rs);
                instructors.add(instructor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return instructors;
    }

    /**
     * Search SuperUsers by username, email, or full name.
     * 
     * @param keyword The search keyword
     * @param role    Optional role filter (can be null for all roles)
     * @return List of matching SuperUsers
     */
    public List<SuperUser> searchSuperUsers(String keyword, String role) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<SuperUser> superUsers = new ArrayList<>();

        String searchKeyword = "%" + keyword + "%";

        try {
            conn = getConnection();
            String sql;

            if (role != null && !role.trim().isEmpty()) {
                sql = "SELECT * FROM SuperUsers WHERE (Username LIKE ? OR Email LIKE ? OR FullName LIKE ?) AND Role = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, searchKeyword);
                ps.setString(2, searchKeyword);
                ps.setString(3, searchKeyword);
                ps.setString(4, role);
            } else {
                sql = "SELECT * FROM SuperUsers WHERE Username LIKE ? OR Email LIKE ? OR FullName LIKE ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, searchKeyword);
                ps.setString(2, searchKeyword);
                ps.setString(3, searchKeyword);
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                SuperUser superUser = mapSuperUser(rs);
                superUsers.add(superUser);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return superUsers;
    }

    /**
     * Check if a SuperUser with specific ID is an admin.
     * 
     * @param superUserId The SuperUser ID to check
     * @return true if the user is an admin, false otherwise
     */
    public boolean isAdmin(int superUserId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT Role FROM SuperUsers WHERE SuperUserID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, superUserId);

            rs = ps.executeQuery();
            if (rs.next()) {
                return "admin".equalsIgnoreCase(rs.getString("Role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return false;
    }

    /**
     * Check if a SuperUser with specific ID is an instructor.
     * 
     * @param superUserId The SuperUser ID to check
     * @return true if the user is an instructor, false otherwise
     */
    public boolean isInstructor(int superUserId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT Role FROM SuperUsers WHERE SuperUserID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, superUserId);

            rs = ps.executeQuery();
            if (rs.next()) {
                return "instructor".equalsIgnoreCase(rs.getString("Role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return false;
    }
}