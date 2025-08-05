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
import java.sql.Statement;
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
            String sql = "SELECT ROUND(AVG(Stars * 1.0), 1) as AvgRating FROM Ratings WHERE CourseID = ?";

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
            String sql = "SELECT r.*, c.Username, co.Name as CourseName FROM Ratings r "
                    + "JOIN Customers c ON r.CustomerID = c.CustomerID "
                    + "JOIN Courses co ON r.CourseID = co.CourseID "
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

        try {
            rating.setCustomerID(rs.getInt("CustomerID"));
        } catch (SQLException e) {
            try {
                rating.setUserID(rs.getInt("UserID"));
            } catch (SQLException ex) {
                // Ignore if neither column exists
            }
        }

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
            String sql = "SELECT r.*, c.Username, co.Name as CourseName FROM Ratings r "
                    + "JOIN Customers c ON r.CustomerID = c.CustomerID "
                    + "JOIN Courses co ON r.CourseID = co.CourseID "
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
            String sql = "SELECT r.*, c.Username, co.Name as CourseName FROM Ratings r "
                    + "JOIN Customers c ON r.CustomerID = c.CustomerID "
                    + "JOIN Courses co ON r.CourseID = co.CourseID "
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
            String sql = "SELECT r.*, c.Username, co.Name as CourseName FROM Ratings r "
                    + "JOIN Customers c ON r.CustomerID = c.CustomerID "
                    + "JOIN Courses co ON r.CourseID = co.CourseID "
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
            String sql = "SELECT c.Name, AVG(r.Stars) as AvgRating FROM Ratings r "
                    + "JOIN Courses c ON r.CourseID = c.CourseID ";

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
     * @return Map where key is course name and value is array of 12 monthly
     *         average ratings
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
            String sql = "SELECT c.Name, MONTH(r.CreatedAt) as RatingMonth, AVG(r.Stars) as AvgRating "
                    + "FROM Ratings r "
                    + "JOIN Courses c ON r.CourseID = c.CourseID "
                    + "WHERE YEAR(r.CreatedAt) = ? "
                    + "GROUP BY c.CourseID, c.Name, MONTH(r.CreatedAt) "
                    + "ORDER BY c.Name, MONTH(r.CreatedAt)";

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

    /**
     * Gets the total number of ratings for all courses taught by an instructor
     *
     * @param instructorId The instructor ID
     * @return The total number of ratings
     */
    public int getTotalRatingsByInstructorId(int instructorId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;

        try {
            conn = getConnection();
            String sql = "SELECT COUNT(r.RatingID) AS total "
                    + "FROM Ratings r "
                    + "JOIN CourseInstructors ci ON r.CourseID = ci.CourseID "
                    + "WHERE ci.InstructorID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, instructorId);
            rs = ps.executeQuery();

            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error counting ratings by instructor: " + e.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }

        return count;
    }

    /**
     * Gets the average rating for all courses taught by an instructor
     *
     * @param instructorId The instructor ID
     * @return The average rating (1-5), or 0 if no ratings
     */
    public double getAverageRatingByInstructorId(int instructorId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        double average = 0.0;

        try {
            conn = getConnection();
            // Get all ratings for instructor's courses
            String sql = "SELECT r.Stars "
                    + "FROM Ratings r "
                    + "JOIN CourseInstructors ci ON r.CourseID = ci.CourseID "
                    + "WHERE ci.InstructorID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, instructorId);
            rs = ps.executeQuery();

            // Calculate average manually for more precision
            int sum = 0;
            int count = 0;

            while (rs.next()) {
                sum += rs.getInt("Stars");
                count++;
            }

            if (count > 0) {
                // Calculate average with exactly one decimal place
                double rawAverage = (double) sum / count;
                // Format to exactly one decimal place
                average = Math.round(rawAverage * 10.0) / 10.0;
            }
        } catch (SQLException e) {
            System.err.println("Error calculating average rating by instructor: " + e.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }

        return average;
    }

    /**
     * Get all ratings for courses taught by a specific instructor.
     *
     * @param instructorId The instructor's ID
     * @return List of ratings for the instructor's courses
     */
    public List<Rating> getRatingsByInstructorId(int instructorId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Rating> ratings = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT r.*, c.Username, co.Name as CourseName FROM Ratings r "
                    + "JOIN Customers c ON r.CustomerID = c.CustomerID "
                    + "JOIN Courses co ON r.CourseID = co.CourseID "
                    + "JOIN CourseInstructors ci ON co.CourseID = ci.CourseID "
                    + "WHERE ci.InstructorID = ? "
                    + "ORDER BY r.CreatedAt DESC";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, instructorId);

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
     * Get all ratings for courses taught by a specific instructor and filtered
     * by courseId.
     *
     * @param instructorId The instructor's ID
     * @param courseId     The course ID to filter
     * @return List of ratings for the instructor's courses and courseId
     */
    public List<Rating> getRatingsByInstructorIdAndCourseId(int instructorId, int courseId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Rating> ratings = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT r.*, c.Username, co.Name as CourseName FROM Ratings r "
                    + "JOIN Customers c ON r.CustomerID = c.CustomerID "
                    + "JOIN Courses co ON r.CourseID = co.CourseID "
                    + "JOIN CourseInstructors ci ON co.CourseID = ci.CourseID "
                    + "WHERE ci.InstructorID = ? AND r.CourseID = ? "
                    + "ORDER BY r.CreatedAt DESC";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, instructorId);
            ps.setInt(2, courseId);

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
     * Get all ratings for courses taught by a specific instructor and filtered
     * by stars.
     *
     * @param instructorId The instructor's ID
     * @param stars        The star rating to filter
     * @return List of ratings for the instructor's courses and stars
     */
    public List<Rating> getRatingsByInstructorIdAndStars(int instructorId, int stars) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Rating> ratings = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT r.*, c.Username, co.Name as CourseName FROM Ratings r "
                    + "JOIN Customers c ON r.CustomerID = c.CustomerID "
                    + "JOIN Courses co ON r.CourseID = co.CourseID "
                    + "JOIN CourseInstructors ci ON co.CourseID = ci.CourseID "
                    + "WHERE ci.InstructorID = ? AND r.Stars = ? "
                    + "ORDER BY r.CreatedAt DESC";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, instructorId);
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
     * Get all ratings for courses taught by a specific instructor and filtered
     * by courseId and stars.
     *
     * @param instructorId The instructor's ID
     * @param courseId     The course ID to filter
     * @param stars        The star rating to filter
     * @return List of ratings for the instructor's courses, courseId, and stars
     */
    public List<Rating> getRatingsByInstructorIdAndCourseIdAndStars(int instructorId, int courseId, int stars) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Rating> ratings = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT r.*, c.Username, co.Name as CourseName FROM Ratings r "
                    + "JOIN Customers c ON r.CustomerID = c.CustomerID "
                    + "JOIN Courses co ON r.CourseID = co.CourseID "
                    + "JOIN CourseInstructors ci ON co.CourseID = ci.CourseID "
                    + "WHERE ci.InstructorID = ? AND r.CourseID = ? AND r.Stars = ? "
                    + "ORDER BY r.CreatedAt DESC";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, instructorId);
            ps.setInt(2, courseId);
            ps.setInt(3, stars);

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

    public List<Rating> getRatingsByInstructorIdPaged(int instructorId, int offset, int limit) {
        List<Rating> ratings = new ArrayList<>();
        String sql = "SELECT r.*, c.Username, co.Name as CourseName FROM Ratings r "
                + "JOIN Customers c ON r.CustomerID = c.CustomerID "
                + "JOIN Courses co ON r.CourseID = co.CourseID "
                + "JOIN CourseInstructors ci ON co.CourseID = ci.CourseID "
                + "WHERE ci.InstructorID = ? "
                + "ORDER BY r.CreatedAt DESC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instructorId);
            ps.setInt(2, offset);
            ps.setInt(3, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ratings.add(mapRating(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ratings;
    }

    public int countByInstructorCourseAndStars(int instructorId, int courseId, int stars) {
        String sql = "SELECT COUNT(r.RatingID) AS total "
                + "FROM Ratings r "
                + "JOIN CourseInstructors ci ON r.CourseID = ci.CourseID "
                + "WHERE ci.InstructorID = ? AND r.CourseID = ? AND r.Stars = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instructorId);
            ps.setInt(2, courseId);
            ps.setInt(3, stars);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countByInstructorAndCourse(int instructorId, int courseId) {
        String sql = "SELECT COUNT(r.RatingID) AS total "
                + "FROM Ratings r "
                + "JOIN CourseInstructors ci ON r.CourseID = ci.CourseID "
                + "WHERE ci.InstructorID = ? AND r.CourseID = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instructorId);
            ps.setInt(2, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countByInstructorAndStars(int instructorId, int stars) {
        String sql = "SELECT COUNT(r.RatingID) AS total "
                + "FROM Ratings r "
                + "JOIN CourseInstructors ci ON r.CourseID = ci.CourseID "
                + "WHERE ci.InstructorID = ? AND r.Stars = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instructorId);
            ps.setInt(2, stars);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Rating> getRatingsByInstructorIdAndCourseIdPaged(int instructorId, int courseId, int offset,
            int limit) {
        List<Rating> ratings = new ArrayList<>();
        String sql = "SELECT r.*, c.Username, co.Name as CourseName FROM Ratings r "
                + "JOIN Customers c ON r.CustomerID = c.CustomerID "
                + "JOIN Courses co ON r.CourseID = co.CourseID "
                + "JOIN CourseInstructors ci ON co.CourseID = ci.CourseID "
                + "WHERE ci.InstructorID = ? AND r.CourseID = ? "
                + "ORDER BY r.CreatedAt DESC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instructorId);
            ps.setInt(2, courseId);
            ps.setInt(3, offset);
            ps.setInt(4, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ratings.add(mapRating(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ratings;
    }

    public List<Rating> getRatingsByInstructorIdAndStarsPaged(int instructorId, int stars, int offset, int limit) {
        List<Rating> ratings = new ArrayList<>();
        String sql = "SELECT r.*, c.Username, co.Name as CourseName FROM Ratings r "
                + "JOIN Customers c ON r.CustomerID = c.CustomerID "
                + "JOIN Courses co ON r.CourseID = co.CourseID "
                + "JOIN CourseInstructors ci ON co.CourseID = ci.CourseID "
                + "WHERE ci.InstructorID = ? AND r.Stars = ? "
                + "ORDER BY r.CreatedAt DESC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instructorId);
            ps.setInt(2, stars);
            ps.setInt(3, offset);
            ps.setInt(4, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ratings.add(mapRating(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ratings;
    }

    public List<Rating> getRatingsByInstructorIdAndCourseIdAndStarsPaged(int instructorId, int courseId, int stars,
            int offset, int limit) {
        List<Rating> ratings = new ArrayList<>();
        String sql = "SELECT r.*, c.Username, co.Name as CourseName FROM Ratings r "
                + "JOIN Customers c ON r.CustomerID = c.CustomerID "
                + "JOIN Courses co ON r.CourseID = co.CourseID "
                + "JOIN CourseInstructors ci ON co.CourseID = ci.CourseID "
                + "WHERE ci.InstructorID = ? AND r.CourseID = ? AND r.Stars = ? "
                + "ORDER BY r.CreatedAt DESC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instructorId);
            ps.setInt(2, courseId);
            ps.setInt(3, stars);
            ps.setInt(4, offset);
            ps.setInt(5, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ratings.add(mapRating(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ratings;
    }

    public List<Integer> getRatingYearsByInstructor(int instructorId) throws SQLException {
        List<Integer> years = new ArrayList<>();
        String sql = "SELECT DISTINCT YEAR(r.CreatedAt) AS Year "
                + "FROM Ratings r "
                + "JOIN Courses c ON r.CourseID = c.CourseID "
                + "JOIN CourseInstructors ci ON c.CourseID = ci.CourseID "
                + "WHERE ci.InstructorID = ? "
                + "ORDER BY Year DESC";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instructorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    years.add(rs.getInt("Year"));
                }
            }
        }
        return years;
    }

    /**
     * Get average course ratings by year for a specific instructor.
     *
     * @param year         The year to filter by (0 for all years)
     * @param instructorId The instructor ID to filter courses
     * @return Map of course names to average ratings
     * @throws SQLException If a database error occurs
     */
    public Map<String, Double> getAverageRatingsByYearForInstructor(int year, int instructorId) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<String, Double> averageRatings = new HashMap<>();

        CourseDAO courseDAO = new CourseDAO();
        List<Course> courses = courseDAO.getCoursesByInstructorId(instructorId); // chỉ lấy course thuộc instructor

        // Khởi tạo trước với 0.0 để có đầy đủ labels cho biểu đồ
        for (Course course : courses) {
            averageRatings.put(course.getName(), 0.0);
        }

        try {
            conn = getConnection();
            String sql = "SELECT c.Name, AVG(r.Stars) as AvgRating "
                    + "FROM Ratings r "
                    + "JOIN Courses c ON r.CourseID = c.CourseID "
                    + "JOIN CourseInstructors ci ON c.CourseID = ci.CourseID "
                    + "WHERE ci.InstructorID = ? ";

            if (year > 0) {
                sql += "AND YEAR(r.CreatedAt) = ? ";
            }

            sql += "GROUP BY c.CourseID, c.Name ORDER BY c.Name";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, instructorId);

            if (year > 0) {
                ps.setInt(2, year);
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

    public Map<String, double[]> getAverageRatingsByMonthByInstructor(int instructorId, int year) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<String, double[]> ratingsByMonth = new HashMap<>();

        // Get all courses of this instructor
        CourseDAO courseDAO = new CourseDAO();
        List<Course> courses = courseDAO.getCoursesByInstructorId(instructorId);
        for (Course course : courses) {
            ratingsByMonth.put(course.getName(), new double[12]); // months 0-11
        }

        try {
            conn = getConnection();
            String sql = "SELECT c.Name, MONTH(r.CreatedAt) as RatingMonth, AVG(r.Stars) as AvgRating "
                    + "FROM Ratings r "
                    + "JOIN Courses c ON r.CourseID = c.CourseID "
                    + "JOIN CourseInstructors ci ON c.CourseID = ci.CourseID "
                    + "WHERE ci.InstructorID = ? AND YEAR(r.CreatedAt) = ? "
                    + "GROUP BY c.CourseID, c.Name, MONTH(r.CreatedAt) "
                    + "ORDER BY c.Name, MONTH(r.CreatedAt)";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, instructorId);
            ps.setInt(2, year);

            rs = ps.executeQuery();
            while (rs.next()) {
                String courseName = rs.getString("Name");
                int month = rs.getInt("RatingMonth") - 1;
                double avgRating = rs.getDouble("AvgRating");

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
     * Gets the total number of ratings for all courses taught by an instructor
     *
     * @param instructorId The instructor ID
     * @return The number of ratings
     */
    public int getRatingCountByInstructorId(int instructorId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;

        try {
            conn = getConnection();
            String sql = "SELECT COUNT(*) AS RatingCount "
                    + "FROM Ratings r "
                    + "JOIN CourseInstructors ci ON r.CourseID = ci.CourseID "
                    + "WHERE ci.InstructorID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, instructorId);
            rs = ps.executeQuery();

            if (rs.next()) {
                count = rs.getInt("RatingCount");
            }
        } catch (SQLException e) {
            System.err.println("Error counting ratings by instructor: " + e.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }

        return count;
    }

    /**
     * Get a user's rating for a specific course.
     *
     * @param userId   The ID of the user
     * @param courseId The ID of the course
     * @return The user's rating, or null if the user hasn't rated the course
     */
    public Rating getByCustomerAndCourse(int userId, int courseId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Rating rating = null;

        try {
            conn = getConnection();
            String sql = "SELECT r.*, u.Username, c.Name as CourseName FROM Ratings r "
                    + "JOIN Customers u ON r.CustomerID = u.CustomerID "
                    + "JOIN Courses c ON r.CourseID = c.CourseID "
                    + "WHERE r.CustomerID = ? AND r.CourseID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, courseId);

            rs = ps.executeQuery();
            if (rs.next()) {
                rating = mapRating(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return rating;
    }

    /**
     * Insert a new rating into the database.
     *
     * @param rating The rating to insert
     * @return The ID of the inserted rating, or -1 if insertion failed
     */
    public int insertRating(Rating rating) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int generatedId = -1;

        try {
            conn = getConnection();
            String sql = "INSERT INTO Ratings (CourseID, CustomerID, Stars, Comment, CreatedAt, UpdatedAt) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, rating.getCourseID());
            ps.setInt(2, rating.getCustomerID());
            ps.setInt(3, rating.getStars());
            ps.setString(4, rating.getComment());
            ps.setTimestamp(5, new java.sql.Timestamp(rating.getCreatedAt().getTime()));
            ps.setTimestamp(6, new java.sql.Timestamp(rating.getUpdatedAt().getTime()));

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 1) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                    rating.setRatingID(generatedId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return generatedId;
    }

    /**
     * Get a rating by its ID.
     *
     * @param ratingId The ID of the rating to get
     * @return The rating, or null if not found
     */
    public Rating getRatingById(int ratingId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Rating rating = null;

        try {
            conn = getConnection();
            String sql = "SELECT r.*, u.Username, c.Name as CourseName FROM Ratings r "
                    + "JOIN Customers u ON r.CustomerID = u.CustomerID "
                    + "JOIN Courses c ON r.CourseID = c.CourseID "
                    + "WHERE r.RatingID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, ratingId);

            rs = ps.executeQuery();
            if (rs.next()) {
                rating = mapRating(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return rating;
    }

    /**
     * Update an existing rating in the database.
     *
     * @param rating The rating to update
     * @return true if update was successful, false otherwise
     */
    public boolean updateRating(Rating rating) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "UPDATE Ratings SET Stars = ?, Comment = ?, UpdatedAt = ? WHERE RatingID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, rating.getStars());
            ps.setString(2, rating.getComment());
            ps.setTimestamp(3, new java.sql.Timestamp(new java.util.Date().getTime()));
            ps.setInt(4, rating.getRatingID());

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
     * Delete a rating from the database.
     *
     * @param ratingId The ID of the rating to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteRating(int ratingId) {
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

}
