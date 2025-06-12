/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Lesson;
import db.DBContext;

/**
 * Data Access Object for Lesson entity.
 *
 * @author DangPH - CE180896
 */
public class LessonDAO extends DBContext {

    /**
     * Get all lessons for a specific course.
     *
     * @param courseId The course ID
     * @return List of lessons belonging to the course
     */
    public List<Lesson> getLessonsByCourseId(int courseId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Lesson> lessons = new ArrayList<>();
        String sql = "SELECT * FROM lessons WHERE CourseID = ? ORDER BY OrderIndex ASC";

        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, courseId);
            rs = ps.executeQuery();
            while (rs.next()) {
                lessons.add(mapLesson(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return lessons;
    }

    /**
     * Map a ResultSet row to a Lesson object.
     *
     * @param rs The ResultSet to map
     * @return A Lesson object
     * @throws SQLException If a database error occurs
     */
    private Lesson mapLesson(ResultSet rs) throws SQLException {
        Lesson lesson = new Lesson();
        lesson.setLessonID(rs.getInt("LessonID"));
        lesson.setTitle(rs.getString("Title"));
        lesson.setCourseID(rs.getInt("CourseID"));
        lesson.setOrderIndex(rs.getInt("OrderIndex"));

        return lesson;
    }
}
