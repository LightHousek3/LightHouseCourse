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
import java.util.ArrayList;
import java.util.List;
import model.Rating;

/**
 * Data Access Object for Rating entity.
 *
 * @author DATLT-CE181501
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

    /**
     * Get all ratings for a course.
     *
     * @param courseId The ID of the course
     * @return List of ratings for the course
     */
    public List<Rating> getRatingsByCourseId(int courseId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Rating> ratings = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT r.*, u.Username, c.Name as CourseName FROM Ratings r "
                    + "JOIN Users u ON r.UserID = u.UserID "
                    + "JOIN Courses c ON r.CourseID = c.CourseID "
                    + "WHERE r.CourseID = ? "
                    + "ORDER BY r.CreatedAt DESC";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, courseId);

            rs = ps.executeQuery();
            while (rs.next()) {
                Rating rating = mapRating(rs);
                ratings.add(rating);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return ratings;
    }

    /**
     * Helper method to map ResultSet to Rating object.
     *
     * @param rs The ResultSet to map
     * @return The mapped Rating object
     * @throws SQLException if an SQL error occurs
     */
    private Rating mapRating(ResultSet rs) throws SQLException {
        Rating rating = new Rating();
        rating.setRatingID(rs.getInt("RatingID"));
        rating.setCourseID(rs.getInt("CourseID"));
        rating.setUserID(rs.getInt("UserID"));
        rating.setStars(rs.getInt("Stars"));
        rating.setComment(rs.getString("Comment"));
        rating.setCreatedAt(rs.getTimestamp("CreatedAt"));
        rating.setUpdatedAt(rs.getTimestamp("UpdatedAt"));

        try {
            rating.setUsername(rs.getString("Username"));
        } catch (SQLException e) {
            // Ignore if column doesn't exist
        }

        try {
            rating.setCourseName(rs.getString("CourseName"));
        } catch (SQLException e) {
            // Ignore if column doesn't exist
        }

        return rating;
    }

    /**
     * Get all ratings in the system.
     *
     * @return List of all ratings
     */
    public List<Rating> getAllRatings() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Rating> ratings = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT r.*, u.Username, c.Name as CourseName FROM Ratings r "
                    + "JOIN Users u ON r.UserID = u.UserID "
                    + "JOIN Courses c ON r.CourseID = c.CourseID "
                    + "ORDER BY r.CreatedAt DESC";

            ps = conn.prepareStatement(sql);

            rs = ps.executeQuery();
            while (rs.next()) {
                Rating rating = mapRating(rs);
                ratings.add(rating);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return ratings;
    }

    /**
     * Get all ratings with a specific star rating.
     *
     * @param stars The star rating to filter by (1-5)
     * @return List of ratings with the specified star rating
     */
    public List<Rating> getRatingsByStar(int stars) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Rating> ratings = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT r.*, u.Username, c.Name as CourseName FROM Ratings r "
                    + "JOIN Users u ON r.UserID = u.UserID "
                    + "JOIN Courses c ON r.CourseID = c.CourseID "
                    + "WHERE r.Stars = ? "
                    + "ORDER BY r.CreatedAt DESC";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, stars);

            rs = ps.executeQuery();
            while (rs.next()) {
                Rating rating = mapRating(rs);
                ratings.add(rating);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return ratings;
    }

    /**
     * Delete a rating from the database.
     *
     * @param ratingId The ID of the rating to deleteReview
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteReview(int ratingId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "DELETE FROM Ratings WHERE RatingID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, ratingId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(rs, ps, conn);
        }
    }

    public List<Rating> getRatingsByCourseIdAndStar(int courseId, int stars) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Rating> ratings = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT r.*, u.Username, c.Name as CourseName FROM Ratings r "
                    + "JOIN Users u ON r.UserID = u.UserID "
                    + "JOIN Courses c ON r.CourseID = c.CourseID "
                    + "WHERE r.CourseID = ? AND r.Stars = ? "
                    + "ORDER BY r.CreatedAt DESC";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, courseId);
            ps.setInt(2, stars);

            rs = ps.executeQuery();
            while (rs.next()) {
                Rating rating = mapRating(rs);
                ratings.add(rating);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }
        return ratings;
    }

}
