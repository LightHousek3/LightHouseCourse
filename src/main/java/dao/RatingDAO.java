/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import db.DBContext;

/**
 * Data Access Object for Rating entity.
 * @author DangPH - CE180896
 */
public class RatingDAO extends DBContext {
    
    /**
     * Get the average rating for a course.
     * 
     * @param courseId The ID of the course
     * @return The average rating (1-5), or 0 if no ratings
     */
    public double getAverageRatingForCourse(int courseId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        double average = 0.0;

        try {
            conn = getConnection();
            String sql = "SELECT AVG(Stars) as AvgRating FROM Ratings WHERE CourseID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, courseId);

            rs = ps.executeQuery();
            if (rs.next()) {
                average = rs.getDouble("AvgRating");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return average;
    }
    
    /**
     * Get the count of ratings for a course.
     * 
     * @param courseId The ID of the course
     * @return The count of ratings
     */
    public int getRatingCountForCourse(int courseId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;

        try {
            conn = getConnection();
            String sql = "SELECT COUNT(*) as RatingCount FROM Ratings WHERE CourseID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, courseId);

            rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt("RatingCount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return count;
    }
}
