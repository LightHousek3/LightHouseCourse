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
 *
 * @author NhiDTYCE-180492
 */
public class CategoryDAO extends DBContext {

    private static final int ITEMS_PER_PAGE = 10;

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
            String sql = "SELECT * FROM Categories ORDER BY CategoryID ASC";

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
     * Get categories with pagination
     *
     * @param page         Current page number (starting from 1)
     * @param itemsPerPage Number of items per page
     * @return List of categories for the specified page
     */
    public List<Category> getCategoriesWithPagination(int page, int itemsPerPage) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Category> categories = new ArrayList<>();

        try {
            conn = getConnection();
            int offset = (page - 1) * itemsPerPage;

            String sql = "SELECT * FROM Categories ORDER BY CategoryID ASC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, offset);
            ps.setInt(2, itemsPerPage);
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
     * Get total number of categories
     *
     * @return Total count of categories
     */
    public int getTotalCategoriesCount() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;

        try {
            conn = getConnection();
            String sql = "SELECT COUNT(*) as total FROM Categories";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return count;
    }

    /**
     * Calculate total pages based on total items and items per page
     *
     * @param totalItems   Total number of items
     * @param itemsPerPage Items per page
     * @return Total number of pages
     */
    public int getTotalPages(int totalItems, int itemsPerPage) {
        return (int) Math.ceil((double) totalItems / itemsPerPage);
    }

    /**
     * Get default items per page
     *
     * @return Default items per page (10)
     */
    public int getItemsPerPage() {
        return ITEMS_PER_PAGE;
    }

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

            conn = getConnection();
            System.out.println("Database connection successful: " + (conn != null));

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
     *
     * @param category The category to update
     * @return true if update successful, false otherwise
     */
    public boolean updateCategory(Category category) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
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
            conn = getConnection();

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

    public Category getCategoryById(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Category category = null;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM Categories WHERE CategoryID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
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

    public boolean checkCategoryExists(String name) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT COUNT(*) as count FROM Categories WHERE LTRIM(RTRIM(LOWER(Name))) = LTRIM(RTRIM(LOWER(?)))";
            ps = conn.prepareStatement(sql);
            ps.setString(1, name.trim());
            rs = ps.executeQuery();

            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("Checking category exists: '" + name + "' - Found: " + count + " records");
                return count > 0;
            }

            return false;
        } catch (SQLException e) {
            System.err.println("Error checking category exists: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(rs, ps, conn);
        }
    }

    public boolean checkCategoryExistsExceptId(String name, int excludeId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT COUNT(*) as count FROM Categories WHERE LTRIM(RTRIM(LOWER(Name))) = LTRIM(RTRIM(LOWER(?))) AND CategoryID != ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, name.trim());
            ps.setInt(2, excludeId);
            rs = ps.executeQuery();

            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("Checking category exists except ID " + excludeId + ": '" + name + "' - Found: "
                        + count + " records");
                return count > 0;
            }

            return false;
        } catch (SQLException e) {
            System.err.println("Error checking category exists except ID: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(rs, ps, conn);
        }
    }

    public void debugAllCategories() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT CategoryID, Name FROM Categories ORDER BY CategoryID";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            System.out.println("=== DEBUG: All Categories ===");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("CategoryID") + " - Name: '" + rs.getString("Name") + "'");
            }
            System.out.println("=== END DEBUG ===");

        } catch (SQLException e) {
            System.err.println("Error debugging categories: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

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
            String sql = "SELECT c.* FROM Categories c "
                    + "JOIN CourseCategory cc ON c.CategoryID = cc.CategoryID "
                    + "WHERE cc.CourseID = ? "
                    + "ORDER BY c.Name";

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
