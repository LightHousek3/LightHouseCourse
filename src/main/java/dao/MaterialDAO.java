package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Material;
import db.DBContext;

/**
 * Data Access Object for Material entity.
 * Handles database operations related to Material entities.
 */
public class MaterialDAO extends DBContext {

    /**
     * Get a material by ID.
     * 
     * @param materialId The material ID
     * @return The material object, or null if not found
     */
    public Material getMaterialById(int materialId) {
        Material material = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM Materials WHERE MaterialID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, materialId);
            rs = ps.executeQuery();

            if (rs.next()) {
                material = mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return material;
    }

    /**
     * Get materials for a specific lesson.
     * 
     * @param lessonId The lesson ID
     * @return List of materials for the lesson
     */
    public List<Material> getMaterialsByLessonId(int lessonId) {
        List<Material> materials = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM Materials WHERE LessonID = ? ORDER BY OrderIndex";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, lessonId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Material material = mapRow(rs);
                materials.add(material);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return materials;
    }

    /**
     * Save a material (insertMaterial new or updateMaterial existing).
     * 
     * @param material The material to saveMaterial
     * @return true if successful, false otherwise
     */
    public boolean saveMaterial(Material material) {
        if (material.getMaterialID() > 0) {
            return updateMaterial(material);
        } else {
            return insertMaterial(material) > 0;
        }
    }

    /**
     * Insert a new material.
     * 
     * @param material The material to insertMaterial
     * @return The new material ID, or -1 if failed
     */
    private int insertMaterial(Material material) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int materialId = -1;

        try {
            conn = getConnection();
            String sql = "INSERT INTO Materials (LessonID, Title, Description, Content, FileUrl) "
                    + "VALUES (?, ?, ?, ?, ?, ?, GETDATE())";

            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, material.getLessonID());
            ps.setString(2, material.getTitle());
            ps.setString(3, material.getDescription());
            ps.setString(4, material.getContent());
            ps.setString(5, material.getFileUrl());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    materialId = rs.getInt(1);
                    material.setMaterialID(materialId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return materialId;
    }

    /**
     * Update an existing material.
     * 
     * @param material The material to updateMaterial
     * @return true if successful, false otherwise
     */
    private boolean updateMaterial(Material material) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = getConnection();
            String sql = "UPDATE Materials SET LessonID = ?, Title = ?, Description = ?, "
                    + "Content = ?, FileUrl = ?"
                    + "WHERE MaterialID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, material.getLessonID());
            ps.setString(2, material.getTitle());
            ps.setString(3, material.getDescription());
            ps.setString(4, material.getContent());
            ps.setString(5, material.getFileUrl());
            ps.setInt(6, material.getMaterialID());

            int affectedRows = ps.executeUpdate();
            success = (affectedRows > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return success;
    }

    /**
     * Delete a material by ID.
     * 
     * @param materialId The material ID to deleteMaterial
     * @return true if successful, false otherwise
     */
    public boolean deleteMaterial(int materialId) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = getConnection();
            String sql = "DELETE FROM Materials WHERE MaterialID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, materialId);

            int affectedRows = ps.executeUpdate();
            success = (affectedRows > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return success;
    }

    /**
     * Check if a material has been viewed by a user.
     * 
     * @param userId     The user ID
     * @param materialId The material ID
     * @return true if the material has been viewed, false otherwise
     */
    public boolean isMaterialViewedByUser(int userId, int materialId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean viewed = false;

        try {
            conn = getConnection();

            // First get the LessonItemID for this material
            String itemSql = "SELECT LessonItemID FROM LessonItems WHERE ItemType = 'material' AND ItemID = ?";
            ps = conn.prepareStatement(itemSql);
            ps.setInt(1, materialId);
            rs = ps.executeQuery();

            if (rs.next()) {
                int lessonItemId = rs.getInt("LessonItemID");
                rs.close();
                ps.close();

                // Now check if there's a progress record for this lesson item
                String progressSql = "SELECT * FROM LessonItemProgress WHERE UserID = ? AND LessonItemID = ?";
                ps = conn.prepareStatement(progressSql);
                ps.setInt(1, userId);
                ps.setInt(2, lessonItemId);
                rs = ps.executeQuery();

                if (rs.next()) {
                    viewed = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return viewed;
    }

    /**
     * Map a ResultSet row to a Material object.
     * 
     * @param rs The ResultSet to map
     * @return A Material object
     * @throws SQLException If a database error occurs
     */
    private Material mapRow(ResultSet rs) throws SQLException {
        Material material = new Material();
        material.setMaterialID(rs.getInt("MaterialID"));
        material.setLessonID(rs.getInt("LessonID"));
        material.setTitle(rs.getString("Title"));
        material.setDescription(rs.getString("Description"));
        material.setContent(rs.getString("Content"));
        material.setFileUrl(rs.getString("FileUrl"));
        return material;
    }
}