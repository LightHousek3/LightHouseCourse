package dao;

import db.DBContext;
import model.LessonItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for LessonItem
 */
public class LessonItemDAO extends DBContext {

    /**
     * Get a LessonItem by ID
     * 
     * @param lessonItemId The lesson item ID
     * @return The LessonItem, or null if not found
     */
    public LessonItem getById(int lessonItemId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        LessonItem lessonItem = null;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM LessonItems WHERE LessonItemID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, lessonItemId);
            rs = ps.executeQuery();

            if (rs.next()) {
                lessonItem = mapRow(rs);
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

        return lessonItem;
    }

    /**
     * Get LessonItem by item type and item ID
     * 
     * @param itemType The item type (video, material, quiz)
     * @param itemId   The item ID
     * @return The LessonItem, or null if not found
     */
    public LessonItem getByItemTypeAndItemId(String itemType, int itemId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        LessonItem lessonItem = null;

        try {
            conn = getConnection();
            String sql = "SELECT * FROM LessonItems WHERE ItemType = ? AND ItemID = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, itemType);
            ps.setInt(2, itemId);
            rs = ps.executeQuery();

            if (rs.next()) {
                lessonItem = mapRow(rs);
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

        return lessonItem;
    }

    /**
     * Get all lesson items for a lesson
     * 
     * @param lessonId The lesson ID
     * @return List of LessonItems for the lesson
     */
    public List<LessonItem> getLessonItemsByLessonId(int lessonId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<LessonItem> lessonItems = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT * FROM LessonItems WHERE LessonID = ? ORDER BY OrderIndex";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, lessonId);
            rs = ps.executeQuery();

            while (rs.next()) {
                lessonItems.add(mapRow(rs));
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

        return lessonItems;
    }

    /**
     * Save a LessonItem (insertLessonItem or updateLessonItem)
     * 
     * @param lessonItem The LessonItem to saveLessonItem
     * @return true if successful, false otherwise
     */
    public boolean saveLessonItem(LessonItem lessonItem) {
        if (lessonItem.getLessonItemID() > 0) {
            return updateLessonItem(lessonItem);
        } else {
            return insertLessonItem(lessonItem) > 0;
        }
    }

    /**
     * Insert a new LessonItem
     * 
     * @param lessonItem The LessonItem to insertLessonItem
     * @return The new lesson item ID, or -1 if failed
     */
    private int insertLessonItem(LessonItem lessonItem) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int lessonItemId = -1;

        try {
            conn = getConnection();
            String sql = "INSERT INTO LessonItems (LessonID, OrderIndex, ItemType, ItemID) VALUES (?, ?, ?, ?)";

            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, lessonItem.getLessonID());
            ps.setInt(2, lessonItem.getOrderIndex());
            ps.setString(3, lessonItem.getItemType());
            ps.setInt(4, lessonItem.getItemID());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    lessonItemId = rs.getInt(1);
                    lessonItem.setLessonItemID(lessonItemId);
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

        return lessonItemId;
    }

    /**
     * Update an existing LessonItem
     * 
     * @param lessonItem The LessonItem to updateLessonItem
     * @return true if successful, false otherwise
     */
    private boolean updateLessonItem(LessonItem lessonItem) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = getConnection();
            String sql = "UPDATE LessonItems SET LessonID = ?, OrderIndex = ?, ItemType = ?, ItemID = ? " +
                    "WHERE LessonItemID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, lessonItem.getLessonID());
            ps.setInt(2, lessonItem.getOrderIndex());
            ps.setString(3, lessonItem.getItemType());
            ps.setInt(4, lessonItem.getItemID());
            ps.setInt(5, lessonItem.getLessonItemID());

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
     * Delete a lesson item
     * 
     * @param lessonItemId The lesson item ID to deleteLessonItem
     * @return true if successful, false otherwise
     */
    public boolean deleteLessonItem(int lessonItemId) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = getConnection();
            String sql = "DELETE FROM LessonItems WHERE LessonItemID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, lessonItemId);

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
     * Map a ResultSet row to a LessonItem object
     * 
     * @param rs The ResultSet to map
     * @return A LessonItem object
     * @throws SQLException If a database error occurs
     */
    private LessonItem mapRow(ResultSet rs) throws SQLException {
        LessonItem lessonItem = new LessonItem();
        lessonItem.setLessonItemID(rs.getInt("LessonItemID"));
        lessonItem.setLessonID(rs.getInt("LessonID"));
        lessonItem.setOrderIndex(rs.getInt("OrderIndex"));
        lessonItem.setItemType(rs.getString("ItemType"));
        lessonItem.setItemID(rs.getInt("ItemID"));
        return lessonItem;
    }
}