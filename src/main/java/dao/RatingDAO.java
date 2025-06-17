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
import static db.DBContext.closeResources;
import static db.DBContext.getConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Course;
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
    
    /**
     * Get average ratings for all courses by year
     * 
     * @param year The year to filter by (or 0 for all years)
     * @return Map of course names to average ratings
     * @throws SQLException If a database error occurs
     */
    public Map<String, Double> getAverageRatingsByYear(int year) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<String, Double> averageRatings = new HashMap<>();

        CourseDAO courseDAO = new CourseDAO();
        List<Course> courses = courseDAO.getAllCourses();

        // Initialize all courses with 0.0 rating
        for (Course course : courses) {
            averageRatings.put(course.getName(), 0.0);
        }

        try {
            conn = getConnection();
            String sql = "SELECT c.Name, AVG(r.Stars) as AvgRating FROM Ratings r " +
                    "JOIN Courses c ON r.CourseID = c.CourseID ";

            if (year > 0) {
                sql += "WHERE YEAR(r.CreatedAt) = ? ";
            }

            sql += "GROUP BY c.CourseID, c.Name ORDER BY c.Name";

            ps = conn.prepareStatement(sql);

            if (year > 0) {
                ps.setInt(1, year);
            }

            rs = ps.executeQuery();
            while (rs.next()) {
                String courseName = rs.getString("Name");
                double avgRating = rs.getDouble("AvgRating");
                averageRatings.put(courseName, avgRating);
            }
        } finally {
            closeResources(rs, ps, conn);
        }

        return averageRatings;
    }
    
    /**
     * Get average ratings for all courses by month for a specific year
     * 
     * @param year The year to filter by
     * @return Map where key is course name and value is array of 12 monthly average
     *         ratings
     * @throws SQLException If a database error occurs
     */
    public Map<String, double[]> getAverageRatingsByMonth(int year) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<String, double[]> ratingsByMonth = new HashMap<>();

        // First get all courses to ensure we have entries for each course
        CourseDAO courseDAO = new CourseDAO();
        List<Course> courses = courseDAO.getAllCourses();
        for (Course course : courses) {
            ratingsByMonth.put(course.getName(), new double[12]); // Initialize array for each month (0-11)
        }

        try {
            conn = getConnection();
            String sql = "SELECT c.Name, MONTH(r.CreatedAt) as RatingMonth, AVG(r.Stars) as AvgRating " +
                    "FROM Ratings r " +
                    "JOIN Courses c ON r.CourseID = c.CourseID " +
                    "WHERE YEAR(r.CreatedAt) = ? " +
                    "GROUP BY c.CourseID, c.Name, MONTH(r.CreatedAt) " +
                    "ORDER BY c.Name, MONTH(r.CreatedAt)";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, year);

            rs = ps.executeQuery();
            while (rs.next()) {
                String courseName = rs.getString("Name");
                int month = rs.getInt("RatingMonth") - 1; // Convert 1-12 to 0-11
                double avgRating = rs.getDouble("AvgRating");

                // Update the month rating for this course
                if (ratingsByMonth.containsKey(courseName)) {
                    ratingsByMonth.get(courseName)[month] = avgRating;
                }
            }
        } finally {
            closeResources(rs, ps, conn);
        }

        return ratingsByMonth;
    }

    /**
     * Get list of distinct years that have ratings
     * 
     * @return List of years
     * @throws SQLException If a database error occurs
     */
    public List<Integer> getRatingYears() throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Integer> years = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT DISTINCT YEAR(CreatedAt) as Year FROM Ratings ORDER BY Year DESC";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                years.add(rs.getInt("Year"));
            }
        } finally {
            closeResources(rs, ps, conn);
        }

        return years;
    }
}
