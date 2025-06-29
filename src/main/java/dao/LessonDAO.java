/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Lesson;
import db.DBContext;
import model.LessonProgress;

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
     * Create a new lesson in the database.
     *
     * @param lesson The lesson to createLesson
     * @re/turn true if creation was successful, false otherwise
     */
    public boolean createLesson(Lesson lesson) {
        String sql = "INSERT INTO lessons (CourseID, Title, OrderIndex, CreatedAt, UpdatedAt) VALUES (?, ?, ?, GETDATE(), GETDATE())";

        try ( Connection conn = getConnection();  PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, lesson.getCourseID());
            stmt.setString(2, lesson.getTitle());
            stmt.setInt(3, lesson.getOrderIndex());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            try ( ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    lesson.setLessonID(generatedKeys.getInt(1));
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete all lessons from the database for a specific course.
     *
     * @param courseId The ID of the course to delete lessons for
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteByCourseId(int courseId) {
        String sql = "DELETE FROM lessons WHERE CourseID = ?";

        try ( Connection conn = getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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

    /**
     * Get lesson progress for a specific student and lesson
     *
     * @param studentId the customer/student ID
     * @param lessonId the lesson ID
     * @return LessonProgress object or null if not found
     */
    public LessonProgress getLessonProgress(int studentId, int lessonId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        LessonProgress progress = null;
        
        try {
            conn = getConnection();
            String sql = "SELECT * FROM LessonProgress WHERE CustomerID = ? AND LessonID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            ps.setInt(2, lessonId);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                progress = new LessonProgress();
                progress.setProgressID(rs.getInt("ProgressID"));
                progress.setCustomerID(rs.getInt("CustomerID"));
                progress.setLessonID(rs.getInt("LessonID"));
                progress.setIsCompleted(rs.getBoolean("IsCompleted"));
                progress.setCompletionDate(rs.getTimestamp("CompletionDate"));
                progress.setLastAccessDate(rs.getTimestamp("LastAccessDate"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving lesson progress: " + e.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }
        
        return progress;
    }
}
