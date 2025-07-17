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
 * Data Access Object for Material entity. Handles database operations related
 * to Material entities.
 */
public class MaterialDAO extends DBContext {

    /**
     * Get a material by ID.
     *
     * @param materialId The material ID
     * @return The material object, or null if not found
     */
    public Material getMaterialById(int materialId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Material material = null;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM Materials WHERE MaterialID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, materialId);
            rs = ps.executeQuery();

            if (rs.next()) {
                material = mapMaterial(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting material by ID: " + e.getMessage());
        } finally {
            closeResources(rs, ps, conn);
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
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Material> materials = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT * FROM Materials WHERE LessonID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, lessonId);
            rs = ps.executeQuery();

            while (rs.next()) {
                materials.add(mapMaterial(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting materials by lesson ID: " + e.getMessage());
        } finally {
            closeResources(rs, ps, conn);
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
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
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
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
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
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return success;
    }

    /**
     * Check if a material has been viewed by a user.
     *
     * @param userId The user ID
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
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return viewed;
    }

    public int insertWithConnection(Connection conn, Material material) throws SQLException {
        String sql = "INSERT INTO Materials (LessonID, Title, Description, Content, FileUrl) VALUES (?, ?, ?, ?, ?)";
        int materialId = -1;

        try ( PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Set values for the material
            ps.setInt(1, material.getLessonID());
            ps.setString(2, material.getTitle());
            ps.setString(3, material.getDescription());
            ps.setString(4, material.getContent());
            ps.setString(5, material.getFileUrl());

            // Execute the insert for the material
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                try ( ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        materialId = rs.getInt(1); // Get the generated material ID
                        material.setMaterialID(materialId); // Set the generated material ID
                    }
                }
            } else {
                throw new SQLException("Material insert failed!");
            }
        }

        return materialId;
    }

    /**
     * Map a ResultSet row to a Material object.
     *
     * @param rs The ResultSet to map
     * @return A Material object
     * @throws SQLException If a database error occurs
     */
    private Material mapMaterial(ResultSet rs) throws SQLException {
        Material material = new Material();
        material.setMaterialID(rs.getInt("MaterialID"));
        material.setLessonID(rs.getInt("LessonID"));
        material.setTitle(rs.getString("Title"));
        material.setDescription(rs.getString("Description"));
        material.setContent(rs.getString("Content"));
        material.setFileUrl(rs.getString("FileUrl"));
        return material;
    }

    public boolean updateMaterialItem(int materialID, Material material) {
        boolean hasNewFile = material.getFileUrl() != null && !material.getFileUrl().trim().isEmpty();
        String sql;
        if (hasNewFile) {
            sql = "UPDATE Materials SET Title = ?, Description = ?, Content = ?, FileUrl = ? WHERE MaterialID = ?";
        } else {
            sql = "UPDATE Materials SET Title = ?, Description = ?, Content = ? WHERE MaterialID = ?";
        }
        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, material.getTitle());
            ps.setString(2, material.getDescription());
            ps.setString(3, material.getContent());
            if (hasNewFile) {
                ps.setString(4, material.getFileUrl());
                ps.setInt(5, materialID);
            } else {
                ps.setInt(4, materialID);
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Update material failed: " + ex.getMessage());
            return false;
        }
    }

    public boolean deleteMaterialItem(int materialID) {
        String sqlDeleteLessonItem = "DELETE FROM LessonItems WHERE ItemType = 'material' AND ItemID = ?";
        String sqlDeleteMaterial = "DELETE FROM Materials WHERE MaterialID = ?";
        try ( Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try ( PreparedStatement ps1 = conn.prepareStatement(sqlDeleteLessonItem);  PreparedStatement ps2 = conn.prepareStatement(sqlDeleteMaterial)) {
                ps1.setInt(1, materialID);
                ps1.executeUpdate();
                ps2.setInt(1, materialID);
                int affected = ps2.executeUpdate();
                conn.commit();
                return affected > 0;
            } catch (SQLException ex) {
                conn.rollback();
                System.err.println("Delete material failed: " + ex.getMessage());
                return false;
            }
        } catch (SQLException ex) {
            System.err.println("Delete material failed: " + ex.getMessage());
            return false;
        }
    }

}
