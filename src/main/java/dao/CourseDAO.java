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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.Category;
import model.Course;
import model.Instructor;
import model.Lesson;
import model.User;

/**
 * Data Access Object for Course entity.
 *
 * @author DangPH - CE180896
 */
public class CourseDAO extends DBContext {

    private UserDAO userDAO;
    private RatingDAO ratingDAO;
    private LessonDAO lessonDAO;

    public CourseDAO() {
        this.userDAO = new UserDAO();
        this.ratingDAO = new RatingDAO();
        this.lessonDAO = new LessonDAO();
    }

    /**
     * Get a course by ID.
     *
     * @param courseId The course ID
     * @return The course, or null if not found
     */
    public Course getCourseById(int courseId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Course course = null;
        String sql = "SELECT c.* FROM Courses c WHERE c.CourseID = ?";

        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, courseId);
            rs = ps.executeQuery();
            if (rs.next()) {
                course = mapCourse(rs);

                // Get course categories
                List<Category> categories = getCourseCategories(courseId);
                course.setCategories(categories);

                // Get course instructors
                List<Instructor> instructors = getInstructorsForCourse(courseId);
                course.setInstructors(instructors);

                // Get course ratings
                double avgRating = ratingDAO.getAverageRatingForCourse(courseId);
                int ratingCount = ratingDAO.getRatingCountForCourse(courseId);
                course.setAverageRating(avgRating);
                course.setRatingCount(ratingCount);

                // Get lessons for this course
                List<Lesson> lessons = lessonDAO.getLessonsByCourseId(courseId);
                course.setLessons(lessons);

            }
        } catch (SQLException e) {
            System.err.println("Error getting course by ID: " + e.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }

        return course;
    }

    /**
     * Get categories for a course
     *
     * @param courseId The course ID
     * @return List of categories for the course
     */
    public List<Category> getCourseCategories(int courseId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT c.* FROM Categories c "
                + "JOIN CourseCategory cc ON c.CategoryID = cc.CategoryID "
                + "WHERE cc.CourseID = ?";

        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, courseId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Category category = new Category();
                category.setCategoryID(rs.getInt("CategoryID"));
                category.setName(rs.getString("Name"));
                category.setDescription(rs.getString("Description"));
                categories.add(category);
            }
        } catch (SQLException e) {
            System.err.println("Error getting categories for course: " + e.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }

        return categories;
    }

    /**
     * Get instructors for a course
     *
     * @param courseId The course ID
     * @return List of instructors for the course
     */
    public List<Instructor> getInstructorsForCourse(int courseId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Instructor> instructors = new ArrayList<>();
        String sql = "SELECT i.*, u.FullName, u.Email FROM Instructors i "
                + "JOIN CourseInstructors ci ON i.InstructorID = ci.InstructorID "
                + "JOIN Users u ON i.UserID = u.UserID "
                + "WHERE ci.CourseID = ?";

        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, courseId);

            rs = ps.executeQuery();
            while (rs.next()) {
                Instructor instructor = new Instructor();
                instructor.setInstructorID(rs.getInt("InstructorID"));
                instructor.setUserID(rs.getInt("UserID"));
                instructor.setBiography(rs.getString("Biography"));
                instructor.setSpecialization(rs.getString("Specialization"));

                // Handle nullable columns
                if (rs.getTimestamp("ApprovalDate") != null) {
                    instructor.setApprovalDate(rs.getTimestamp("ApprovalDate"));
                }

                // Set User from user data
                User user = userDAO.getUserById(instructor.getUserID());
                instructor.setUser(user);

                instructors.add(instructor);
            }
        } catch (SQLException e) {
            System.err.println("Error getting instructors for course: " + e.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }

        return instructors;
    }

    /**
     * Count all active courses in the database.
     *
     * @return The total number of courses
     */
    public int countAllCourses() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;

        try {
            conn = getConnection();
            String sql = "SELECT COUNT(*) AS total FROM Courses WHERE ApprovalStatus = 'APPROVED'";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error counting courses: " + e.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }

        return count;
    }

    /**
     * Get all courses with pagination.
     *
     * @param offset The offset (for pagination)
     * @param limit The limit (for pagination)
     * @param sortBy The field to sort by (optional)
     * @param sortOrder The sort order (ASC or DESC, optional)
     * @return List of courses
     */
    public List<Course> getAllCoursesWithLimit(int offset, int limit, String sortBy, String sortOrder) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Course> courses = new ArrayList<>();

        try {
            conn = getConnection();

            String orderClause = "CourseID";
            if (sortBy != null && !sortBy.isEmpty()) {
                // Validate sortBy to prevent SQL injection
                switch (sortBy.toLowerCase()) {
                    case "name":
                        orderClause = "Name";
                        break;
                    case "price":
                        orderClause = "Price";
                        break;
                    case "date":
                        orderClause = "SubmissionDate";
                        break;
                    default:
                        orderClause = "CourseID";
                        break;
                }
            }

            String direction = "ASC";
            if (sortOrder != null && sortOrder.equalsIgnoreCase("DESC")) {
                direction = "DESC";
            }

            String sql;
            // Use a simpler query that works on all SQL Server versions
            if (limit > 0) {
                sql = "SELECT TOP " + limit + " * FROM Courses ORDER BY " + orderClause + " " + direction;
                ps = conn.prepareStatement(sql);
            } else {
                sql = "SELECT * FROM Courses ORDER BY " + orderClause + " " + direction;
                ps = conn.prepareStatement(sql);
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                Course course = mapCourse(rs);
                courses.add(course);
            }

            // Get additional data for each course
            if (!courses.isEmpty()) {
                for (Course course : courses) {
                    try {
                        // Get categories
                        List<Category> categories = getCourseCategories(course.getCourseID());
                        course.setCategories(categories);

                        // Get instructors
                        List<Instructor> instructors = getInstructorsForCourse(course.getCourseID());
                        course.setInstructors(instructors);

                        // Get ratings
                        double avgRating = ratingDAO.getAverageRatingForCourse(course.getCourseID());
                        int ratingCount = ratingDAO.getRatingCountForCourse(course.getCourseID());
                        course.setAverageRating(avgRating);
                        course.setRatingCount(ratingCount);

                        // Get lessons instead of directly loading videos
                        List<Lesson> lessons = lessonDAO.getLessonsByCourseId(course.getCourseID());
                        course.setLessons(lessons);

                    } catch (Exception e) {
                        System.err.println("Error getting additional data for course ID " + course.getCourseID() + ": "
                                + e.getMessage());
                    }
                }
            } else {
                System.err.println("No courses were found in the database.");
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in getAllWithLimit: " + e.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }

        return courses;
    }

    /**
     * Map a ResultSet row to a Course object
     *
     * @param rs The ResultSet containing course data
     * @return A Course object
     * @throws SQLException If a database error occurs
     */
    private Course mapCourse(ResultSet rs) throws SQLException {
        Course course = new Course();

        course.setCourseID(rs.getInt("CourseID"));
        course.setName(rs.getString("Name"));
        course.setDescription(rs.getString("Description"));
        course.setPrice(rs.getDouble("Price"));
        course.setImageUrl(rs.getString("ImageUrl"));
        course.setDuration(rs.getString("Duration"));
        course.setLevel(rs.getString("Level"));
        course.setApprovalStatus(rs.getString("ApprovalStatus"));

        // Handle nullable timestamps
        Timestamp submissionDate = rs.getTimestamp("SubmissionDate");
        if (submissionDate != null) {
            course.setSubmissionDate(submissionDate);
        }

        Timestamp approvalDate = rs.getTimestamp("ApprovalDate");
        if (approvalDate != null) {
            course.setApprovalDate(approvalDate);
        }

        course.setRejectionReason(rs.getString("RejectionReason"));

        return course;
    }
}
