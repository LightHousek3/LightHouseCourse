package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Category;
import db.DBContext;

/**
 * Data Access Object for Category entity.
 */
public class CategoryDAO extends DBContext {

    /**
     * Insert a new category into the database.
     * 
     * @param category The category to insert
     * @return The ID of the inserted category, or -1 if insertion failed
     */
    public int insertCategory(Category category) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int categoryId = -1;

        try {
            conn = DBContext.getConnection();
            String sql = "INSERT INTO Categories (Name, Description) VALUES (?, ?)";

            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    categoryId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return categoryId;
    }

    /**
     * Update category information in the database.
     * 
     * @param category The category to update
     * @return true if update successful, false otherwise
     */
    public boolean updateCategory(Category category) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            String sql = "UPDATE Categories SET Name = ?, Description = ? WHERE CategoryID = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, category.getName());
            ps.setString(2, category.getDescription());
            ps.setInt(3, category.getCategoryID());

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
     * Delete a category from the database.
     * 
     * @param categoryId The ID of the category to delete
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteCategory(int categoryId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();

            // First, delete associations in CourseCategory
            String deleteCategoryCoursesSql = "DELETE FROM CourseCategory WHERE CategoryID = ?";
            ps = conn.prepareStatement(deleteCategoryCoursesSql);
            ps.setInt(1, categoryId);
            ps.executeUpdate();
            ps.close();

            // Then delete the category
            String deleteCategorySql = "DELETE FROM Categories WHERE CategoryID = ?";
            ps = conn.prepareStatement(deleteCategorySql);
            ps.setInt(1, categoryId);

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
     * Get a category by ID.
     * 
     * @param categoryId The ID of the category
     * @return The category, or null if not found
     */
    public Category getById(int categoryId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Category category = null;

        try {
            conn = DBContext.getConnection();
            String sql = "SELECT * FROM Categories WHERE CategoryID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, categoryId);

            rs = ps.executeQuery();
            if (rs.next()) {
                category = mapCategory(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return category;
    }

    /**
     * Get all categories.
     * 
     * @return List of all categories
     */
    public List<Category> getAllCategories() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Category> categories = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT * FROM Categories ORDER BY Name";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Category category = mapCategory(rs);
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return categories;
    }

    /**
     * Check if category name already exists.
     * 
     * @param name The category name to check
     * @return true if exists, false otherwise
     */
    public boolean nameExists(String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBContext.getConnection();
            String sql = "SELECT COUNT(*) FROM Categories WHERE Name = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, name);

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
     * Get all categories for a specific course.
     * 
     * @param courseId The course ID
     * @return List of categories associated with the course
     */
    public List<Category> getCategoriesForCourse(int courseId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Category> categories = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT c.* FROM Categories c " +
                    "JOIN CourseCategory cc ON c.CategoryID = cc.CategoryID " +
                    "WHERE cc.CourseID = ? " +
                    "ORDER BY c.Name";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, courseId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Category category = mapCategory(rs);
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return categories;
    }

    /**
     * Map a ResultSet row to a Category object.
     * 
     * @param rs The ResultSet
     * @return The mapped Category
     * @throws SQLException If a database error occurs
     */
    private Category mapCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryID(rs.getInt("CategoryID"));
        category.setName(rs.getString("Name"));
        category.setDescription(rs.getString("Description"));
        return category;
    }
}