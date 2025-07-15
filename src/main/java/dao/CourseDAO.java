/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import controller.instructor.InstructorCourseServlet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.Category;
import model.Course;
import model.Instructor;
import model.Lesson;
import db.DBContext;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Video;

/**
 * Data Access Object for Course entity.
 *
 * @author DangPH - CE180896
 */
public class CourseDAO extends DBContext {

    private SuperUserDAO superUserDAO;
    private RatingDAO ratingDAO;
    private LessonDAO lessonDAO;
    private VideoDAO videoDAO;

    public CourseDAO() {
        this.superUserDAO = new SuperUserDAO();
        this.ratingDAO = new RatingDAO();
        this.lessonDAO = new LessonDAO();
        this.videoDAO = new VideoDAO();
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
        String sql = "SELECT i.*, su.FullName, su.Email FROM Instructors i "
                + "JOIN CourseInstructors ci ON i.InstructorID = ci.InstructorID "
                + "JOIN SuperUsers su ON i.SuperUserID = su.SuperUserID "
                + "WHERE ci.CourseID = ?";

        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, courseId);

            rs = ps.executeQuery();
            while (rs.next()) {
                Instructor instructor = new Instructor();
                instructor.setInstructorID(rs.getInt("InstructorID"));
                instructor.setSuperUserID(rs.getInt("SuperUserID"));
                instructor.setBiography(rs.getString("Biography"));
                instructor.setSpecialization(rs.getString("Specialization"));

                // Handle nullable columns
                if (rs.getTimestamp("ApprovalDate") != null) {
                    instructor.setApprovalDate(rs.getTimestamp("ApprovalDate"));
                }

                // Set name and email directly from SuperUser data
                instructor.setName(rs.getString("FullName"));
                instructor.setEmail(rs.getString("Email"));

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
     * @param offset    The offset (for pagination)
     * @param limit     The limit (for pagination)
     * @param sortBy    The field to sort by (optional)
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
     * Delete course instructors
     *
     * @param conn     The database connection
     * @param courseId The course ID
     * @throws SQLException If a database error occurs
     */
    private void deleteCourseInstructors(Connection conn, int courseId) throws SQLException {
        String sql = "DELETE FROM CourseInstructors WHERE CourseID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.executeUpdate();
        }
    }

    /**
     * Insert course instructors
     *
     * @param conn        The database connection
     * @param courseId    The course ID
     * @param instructors The list of instructors to insert
     * @throws SQLException If a database error occurs
     */
    private void insertCourseInstructors(Connection conn, int courseId, List<Instructor> instructors)
            throws SQLException {
        String sql = "INSERT INTO CourseInstructors (CourseID, InstructorID) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Instructor instructor : instructors) {
                ps.setInt(1, courseId);
                ps.setInt(2, instructor.getInstructorID());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void insertCourseCategories(Connection conn, int courseId, List<Category> categories) throws SQLException {
        String sql = "INSERT INTO CourseCategory (CourseID, CategoryID) VALUES (?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Category category : categories) {
                ps.setInt(1, courseId);
                ps.setInt(2, category.getCategoryID());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    /**
     * Delete course categories
     *
     * @param conn     The database connection
     * @param courseId The course ID
     * @throws SQLException If a database error occurs
     */
    private void deleteCourseCategories(Connection conn, int courseId) throws SQLException {
        String sql = "DELETE FROM CourseCategory WHERE CourseID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.executeUpdate();
        }
    }

    /**
     * Get all courses.
     *
     * @return List of all courses
     */
    public List<Course> getAllCourses() {
        return getAllCoursesWithLimit(0, 0, null, null); // 0, 0 means no limit
    }

    /**
     * Update course information in the database.
     *
     * @param course The course to updateLesson
     * @return true if updateLesson successful, false otherwise
     */
    public boolean updateCourse(Course course) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Start transaction

            String sql = "UPDATE Courses SET Name = ?, Description = ?, Price = ?, "
                    + "ImageUrl = ?, Duration = ?, Level = ?, "
                    + "ApprovalStatus = ?, SubmissionDate = ?, ApprovalDate = ?, "
                    + "RejectionReason = ? WHERE CourseID = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, course.getName());
            ps.setString(2, course.getDescription());
            ps.setDouble(3, course.getPrice());
            ps.setString(4, course.getImageUrl());
            ps.setString(5, course.getDuration());
            ps.setString(6, course.getLevel());
            ps.setString(7, course.getApprovalStatus());
            ps.setTimestamp(8, course.getSubmissionDate());
            ps.setTimestamp(9, course.getApprovalDate());
            ps.setString(10, course.getRejectionReason());
            ps.setInt(11, course.getCourseID());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                // Delete old categories and insert new ones
                if (course.getCategories() != null) {
                    deleteCourseCategories(conn, course.getCourseID());
                    insertCourseCategories(conn, course.getCourseID(), course.getCategories());
                }

                // Delete old instructors and insert new ones
                if (course.getInstructors() != null && !course.getInstructors().isEmpty()) {
                    deleteCourseInstructors(conn, course.getCourseID());
                    insertCourseInstructors(conn, course.getCourseID(), course.getInstructors());
                }

                // Update lessons
                if (course.getLessons() != null) {
                    // First delete existing lessons
                    lessonDAO.deleteByCourseId(course.getCourseID());

                    // Then insert new ones
                    for (Lesson lesson : course.getLessons()) {
                        lesson.setCourseID(course.getCourseID());
                        lessonDAO.createLesson(lesson);
                    }
                }

                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            closeResources(rs, ps, conn);
        }
    }

    public boolean banCourse(int courseID) {
        String checkSql = "SELECT ApprovalStatus FROM Courses WHERE CourseID = ?";
        String updateSql = "UPDATE Courses SET ApprovalStatus = 'banned' WHERE CourseID = ?";

        try (Connection conn = getConnection();
                PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            checkStmt.setInt(1, courseID);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && "approved".equalsIgnoreCase(rs.getString("ApprovalStatus"))) {
                    updateStmt.setInt(1, courseID);
                    return updateStmt.executeUpdate() == 1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean unbanCourse(int courseID) {
        String checkSql = "SELECT ApprovalStatus FROM Courses WHERE CourseID = ?";
        String updateSql = "UPDATE Courses SET ApprovalStatus = 'approved' WHERE CourseID = ?";

        try (Connection conn = getConnection();
                PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            checkStmt.setInt(1, courseID);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && "banned".equalsIgnoreCase(rs.getString("ApprovalStatus"))) {
                    updateStmt.setInt(1, courseID);
                    return updateStmt.executeUpdate() == 1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Course> getAllCoursesWithLimit(int offset, int limit, String status) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Course> courses = new ArrayList<>();

        try {
            conn = getConnection();

            String sql;
            if (limit > 0) {
                if (status != null) {
                    sql = "SELECT * FROM Courses WHERE ApprovalStatus = ? ORDER BY CourseID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
                    ps = conn.prepareStatement(sql);
                    ps.setString(1, status);
                    ps.setInt(2, offset);
                    ps.setInt(3, limit);
                } else {
                    sql = "SELECT * FROM Courses ORDER BY CourseID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
                    ps = conn.prepareStatement(sql);
                    ps.setInt(1, offset);
                    ps.setInt(2, limit);
                }

            } else {
                sql = "SELECT * FROM Courses";
                ps = conn.prepareStatement(sql);
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                Course course = mapCourse(rs);
                courses.add(course);
            }

            // Additional data
            for (Course course : courses) {
                try {
                    List<Category> categories = getCourseCategories(course.getCourseID());
                    course.setCategories(categories);

                    List<Instructor> instructors = getInstructorsForCourse(course.getCourseID());
                    course.setInstructors(instructors);

                    double avgRating = ratingDAO.getAverageRatingForCourse(course.getCourseID());
                    int ratingCount = ratingDAO.getRatingCountForCourse(course.getCourseID());
                    course.setAverageRating(avgRating);
                    course.setRatingCount(ratingCount);

                    List<Lesson> lessons = lessonDAO.getLessonsByCourseId(course.getCourseID());
                    course.setLessons(lessons);

                } catch (Exception e) {
                    System.err.println("Error getting additional data for course ID " + course.getCourseID() + ": "
                            + e.getMessage());
                }
            }

        } catch (SQLException e) {
            System.err.println("SQL Error in getAllCoursesWithLimit: " + e.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }

        return courses;
    }

    public int countAllCourses(String status) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;

        try {
            conn = getConnection();
            if (status != null) {
                String sql = "SELECT COUNT(*) AS total FROM Courses WHERE ApprovalStatus = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, status);
            } else {
                String sql = "SELECT COUNT(*) AS total FROM Courses";
                ps = conn.prepareStatement(sql);
            }

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

    public List<Course> searchCoursesByNameOrInstructor(String keyword, int offset, int limit) {
        List<Course> courses = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = "SELECT DISTINCT c.*\n"
                + "        FROM Courses c\n"
                + "        LEFT JOIN CourseInstructors ci ON c.CourseID = ci.CourseID\n"
                + "        LEFT JOIN Instructors i ON ci.InstructorID = i.InstructorID\n"
                + "        LEFT JOIN SuperUsers su ON i.SuperUserID = su.SuperUserID\n"
                + "        WHERE c.Name LIKE ? OR su.FullName LIKE ?\n"
                + "        ORDER BY c.CourseID\n"
                + "        OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);

            String likeKeyword = "%" + keyword + "%";
            ps.setString(1, likeKeyword);
            ps.setString(2, likeKeyword);
            ps.setInt(3, offset);
            ps.setInt(4, limit);

            rs = ps.executeQuery();

            while (rs.next()) {
                Course course = mapCourse(rs); // method để ánh xạ từ ResultSet sang Course
                courses.add(course);
            }
            // Lấy dữ liệu phụ thêm nếu có
            for (Course course : courses) {
                try {
                    List<Category> categories = getCourseCategories(course.getCourseID());
                    course.setCategories(categories);

                    List<Instructor> instructors = getInstructorsForCourse(course.getCourseID());
                    course.setInstructors(instructors);

                    double avgRating = ratingDAO.getAverageRatingForCourse(course.getCourseID());
                    int ratingCount = ratingDAO.getRatingCountForCourse(course.getCourseID());
                    course.setAverageRating(avgRating);
                    course.setRatingCount(ratingCount);

                    List<Lesson> lessons = lessonDAO.getLessonsByCourseId(course.getCourseID());
                    course.setLessons(lessons);

                } catch (Exception e) {
                    System.err.println("Error getting additional data for course ID " + course.getCourseID() + ": "
                            + e.getMessage());
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn); // method đóng connection, statement, resultset
        }

        return courses;
    }

    public int countCoursesByKeyword(String keyword) {
        String sql = "SELECT COUNT(DISTINCT c.CourseID)\n"
                + "        FROM Courses c\n"
                + "        LEFT JOIN CourseInstructors ci ON c.CourseID = ci.CourseID\n"
                + "        LEFT JOIN Instructors i ON ci.InstructorID = i.InstructorID\n"
                + "        LEFT JOIN SuperUsers su ON i.SuperUserID = su.SuperUserID\n"
                + "        WHERE c.Name LIKE ? OR su.FullName LIKE ?";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            String likeKeyword = "%" + keyword + "%";
            ps.setString(1, likeKeyword);
            ps.setString(2, likeKeyword);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Course> advancedSearchCourses(
            String keyword, Integer categoryId, String approvalStatus,
            String sortBy, String sortOrder, int offset, int limit) {

        List<Course> courses = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        StringBuilder sql = new StringBuilder("SELECT DISTINCT c.*\n"
                + "        FROM Courses c\n"
                + "        LEFT JOIN CourseInstructors ci ON c.CourseID = ci.CourseID\n"
                + "        LEFT JOIN Instructors i ON ci.InstructorID = i.InstructorID\n"
                + "        LEFT JOIN SuperUsers su ON i.SuperUserID = su.SuperUserID\n"
                + "        LEFT JOIN CourseCategory cc ON c.CourseID = cc.CourseID\n"
                + "        WHERE 1 = 1");

        List<Object> params = new ArrayList<>();
        String likeKeyword = "%" + (keyword == null ? "" : keyword.trim()) + "%";

        // Search keyword
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (c.Name LIKE ? OR su.FullName LIKE ?) ");
            params.add(likeKeyword);
            params.add(likeKeyword);
        }

        // Filter category
        if (categoryId != null && categoryId > 0) {
            sql.append(" AND cc.CategoryID = ? ");
            params.add(categoryId);
        }

        // Filter approval status
        if (approvalStatus != null && !approvalStatus.isEmpty()) {
            sql.append(" AND c.ApprovalStatus = ? ");
            params.add(approvalStatus);
        }

        // Sorting
        String orderColumn;
        switch (sortBy != null ? sortBy.toLowerCase() : "") {
            case "name":
                orderColumn = "c.Name";
                break;
            case "price":
                orderColumn = "c.Price";
                break;
            case "submissiondate":
                orderColumn = "c.SubmissionDate";
                break;
            default:
                orderColumn = "c.CourseID";
                break;
        }

        String orderDirection = (sortOrder != null && sortOrder.equalsIgnoreCase("desc")) ? "DESC" : "ASC";
        sql.append(" ORDER BY ").append(orderColumn).append(" ").append(orderDirection);
        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        params.add(offset);
        params.add(limit);

        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql.toString());

            // Bind parameters
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            rs = ps.executeQuery();
            while (rs.next()) {
                Course course = mapCourse(rs);
                courses.add(course);
            }
            // Lấy dữ liệu phụ thêm nếu có
            for (Course course : courses) {
                try {
                    List<Category> categories = getCourseCategories(course.getCourseID());
                    course.setCategories(categories);

                    List<Instructor> instructors = getInstructorsForCourse(course.getCourseID());
                    course.setInstructors(instructors);

                    double avgRating = ratingDAO.getAverageRatingForCourse(course.getCourseID());
                    int ratingCount = ratingDAO.getRatingCountForCourse(course.getCourseID());
                    course.setAverageRating(avgRating);
                    course.setRatingCount(ratingCount);

                    List<Lesson> lessons = lessonDAO.getLessonsByCourseId(course.getCourseID());
                    course.setLessons(lessons);

                } catch (Exception e) {
                    System.err.println("Error getting additional data for course ID " + course.getCourseID() + ": "
                            + e.getMessage());
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return courses;
    }

    public int countCoursesForAdvancedSearch(String keyword, Integer categoryId, String approvalStatus) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(DISTINCT c.CourseID)\n"
                + "        FROM Courses c\n"
                + "        LEFT JOIN CourseInstructors ci ON c.CourseID = ci.CourseID\n"
                + "        LEFT JOIN Instructors i ON ci.InstructorID = i.InstructorID\n"
                + "        LEFT JOIN SuperUsers su ON i.SuperUserID = su.SuperUserID\n"
                + "        LEFT JOIN CourseCategory cc ON c.CourseID = cc.CourseID\n"
                + "        WHERE 1=1");

        List<Object> params = new ArrayList<>();
        String likeKeyword = "%" + (keyword == null ? "" : keyword.trim()) + "%";

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (c.Name LIKE ? OR su.FullName LIKE ?) ");
            params.add(likeKeyword);
            params.add(likeKeyword);
        }

        if (categoryId != null && categoryId > 0) {
            sql.append(" AND cc.CategoryID = ? ");
            params.add(categoryId);
        }

        if (approvalStatus != null && !approvalStatus.isEmpty()) {
            sql.append(" AND c.ApprovalStatus = ? ");
            params.add(approvalStatus);
        }

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
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

    public static void main(String[] args) {
        CourseDAO aO = new CourseDAO();
        System.out.println(aO.countCoursesByKeyword(""));
        for (Course course : aO.searchCoursesByNameOrInstructor("", 0, 8)) {
            System.out.println(course.toString());
        }
    }

    public boolean isCourseStatus(int courseId, String status) {
        String sql = "SELECT 1 FROM Courses WHERE CourseID = ? AND ApprovalStatus = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            stmt.setString(2, status);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // true nếu tồn tại 1 record phù hợp
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Gets count of courses for a specific instructor
     *
     * @param instructorId The instructor ID
     * @return The number of courses created by the instructor
     */
    public int getCoursesCountByInstructorId(int instructorId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;

        try {
            conn = getConnection();
            String sql = "SELECT COUNT(*) AS total FROM CourseInstructors WHERE InstructorID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, instructorId);
            rs = ps.executeQuery();

            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error counting courses by instructor: " + e.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }

        return count;
    }

    /**
     * Gets count of students enrolled in an instructor's courses
     *
     * @param instructorId The instructor ID
     * @return The number of students enrolled in the instructor's courses
     */
    public int getStudentsCountByInstructorId(int instructorId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;

        try {
            conn = getConnection();
            String sql = "SELECT COUNT(DISTINCT cp.CustomerID) AS total "
                    + "FROM CourseProgress cp "
                    + "JOIN CourseInstructors ci ON cp.CourseID = ci.CourseID "
                    + "WHERE ci.InstructorID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, instructorId);
            rs = ps.executeQuery();

            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error counting students by instructor: " + e.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }

        return count;
    }

    /**
     * Gets total revenue from an instructor's courses
     *
     * @param instructorId The instructor ID
     * @return The total revenue from the instructor's courses
     */
    public double getRevenueByInstructorId(int instructorId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        double revenue = 0.0;

        try {
            conn = getConnection();
            String sql = "SELECT SUM(od.Price) AS total "
                    + "FROM OrderDetails od "
                    + "JOIN Orders o ON od.OrderID = o.OrderID "
                    + "JOIN CourseInstructors ci ON od.CourseID = ci.CourseID "
                    + "WHERE ci.InstructorID = ? AND o.Status = 'completed'";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, instructorId);
            rs = ps.executeQuery();

            if (rs.next()) {
                revenue = rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Error calculating revenue by instructor: " + e.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }

        return revenue;
    }

    /**
     * Gets recent courses for an instructor
     *
     * @param instructorId The instructor ID
     * @param limit        The maximum number of courses to return
     * @return List of recent courses
     */
    public List<Course> getRecentCoursesByInstructorId(int instructorId, int limit) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Course> courses = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT c.* FROM Courses c "
                    + "JOIN CourseInstructors ci ON c.CourseID = ci.CourseID "
                    + "WHERE ci.InstructorID = ? "
                    + "ORDER BY c.SubmissionDate DESC";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, instructorId);

            // Set the limit for the results
            if (limit > 0) {
                ps.setMaxRows(limit);
            }

            rs = ps.executeQuery();
            while (rs.next()) {
                Course course = mapCourse(rs);

                // Add enrollment count
                int enrollmentCount = countEnrollmentsForCourse(course.getCourseID());
                course.setEnrollmentCount(enrollmentCount);

                courses.add(course);
            }
        } catch (SQLException e) {
            System.err.println("Error getting recent courses by instructor: " + e.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }

        return courses;
    }

    /**
     * Gets all courses from a specific instructor
     *
     * @param instructorId The instructor ID
     * @return List of courses
     */
    public List<Course> getCoursesByInstructorId(int instructorId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Course> courses = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT c.* FROM Courses c "
                    + "JOIN CourseInstructors ci ON c.CourseID = ci.CourseID "
                    + "WHERE ci.InstructorID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, instructorId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Course course = mapCourse(rs);
                // get courseID
                int courseId = course.getCourseID();

                // Get course categories
                List<Category> categories = getCourseCategories(courseId);
                course.setCategories(categories);

                // Get course instructors
                List<Instructor> instructors = getInstructorsForCourse(courseId);
                course.setInstructors(instructors);
                courses.add(course);
            }
        } catch (SQLException e) {
            System.err.println("Error finding courses by instructor: " + e.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }

        return courses;
    }

    /**
     * Counts the number of enrollments for a course
     *
     * @param courseId The course ID
     * @return The enrollment count
     */
    private int countEnrollmentsForCourse(int courseId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;

        try {
            conn = getConnection();
            String sql = "SELECT COUNT(*) AS total FROM CourseProgress WHERE CourseID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, courseId);
            rs = ps.executeQuery();

            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error counting enrollments: " + e.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }

        return count;
    }

    /**
     * Gets all courses from a specific instructor using their SuperUserID
     *
     * @param superUserId The super user ID of the instructor
     * @return List of courses
     */
    public List<Course> getCoursesBySuperUserId(int superUserId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Course> courses = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT c.* FROM Courses c "
                    + "JOIN CourseInstructors ci ON c.CourseID = ci.CourseID "
                    + "JOIN Instructors i ON ci.InstructorID = i.InstructorID "
                    + "WHERE i.SuperUserID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, superUserId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Course course = mapCourse(rs);
                courses.add(course);
            }
        } catch (SQLException e) {
            System.err.println("Error finding courses by super user ID: " + e.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }

        return courses;
    }

    public int insertFullCourse(Course course) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int courseId = -1;

        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Thiết lập autoCommit=false ở đầu để giao dịch hoạt động đúng

            // Insert Course
            String sql = "INSERT INTO Courses (Name, Description, Price, ImageUrl, Duration, Level, ApprovalStatus, SubmissionDate) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, course.getName());
            ps.setString(2, course.getDescription());
            ps.setDouble(3, course.getPrice());
            ps.setString(4, course.getImageUrl());
            ps.setString(5, course.getDuration());
            ps.setString(6, course.getLevel());
            ps.setString(7, course.getApprovalStatus());
            ps.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
            int rows = ps.executeUpdate();
            if (rows == 1) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    courseId = rs.getInt(1);

                    // Categories
                    if (course.getCategories() != null && !course.getCategories().isEmpty()) {
                        insertCourseCategories(conn, courseId, course.getCategories());
                    }

                    // Instructors
                    if (course.getInstructors() != null && !course.getInstructors().isEmpty()) {
                        insertCourseInstructors(conn, courseId, course.getInstructors());
                    } else if (course.getInstructorId() > 0) {
                        // Chỉ có instructorId, không có instructors list
                        deleteCourseInstructors(conn, courseId);
                        List<Instructor> list = new ArrayList<>();
                        Instructor inst = new Instructor();
                        inst.setInstructorID(course.getInstructorId());
                        list.add(inst);
                        insertCourseInstructors(conn, courseId, list);
                    }

                    // Lessons & their children
                    if (course.getLessons() != null && !course.getLessons().isEmpty()) {
                        for (Lesson lesson : course.getLessons()) {
                            lesson.setCourseID(courseId);
                            lessonDAO.insertFullLesson(conn, lesson); // Hàm này sẽ tự insert quizzes, materials, videos
                                                                      // thuộc lesson
                        }
                    }

                    conn.commit();
                }
            } else {
                if (!conn.getAutoCommit()) { // Kiểm tra trước khi rollback
                    conn.rollback();
                }
            }
        } catch (SQLException e) {
            try {
                if (conn != null && !conn.getAutoCommit()) { // Kiểm tra trước khi rollback
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            courseId = -1;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // Đặt lại autoCommit về true
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            closeResources(rs, ps, conn);
        }
        return courseId;
    }

    public int insertCourseFull(Course course, List<Integer> instructorIds, List<Integer> categoryIds) {
        int courseId = -1;
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Insert course trước
                courseId = insertCourse(conn, course);
                // Thêm instructor(s) cho course
                insertCourseInstructor(conn, courseId, instructorIds);
                // Thêm category cho course
                insertCourseCategorie(conn, courseId, categoryIds);

                conn.commit();
            } catch (SQLException exception) {
                conn.rollback();
                throw exception;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException except) {
            Logger.getLogger(InstructorCourseServlet.class.getName()).log(Level.SEVERE, null, except);

        }
        return courseId;
    }

    public int insertCourse(Connection conn, Course course) throws SQLException {
        String sql = "INSERT INTO Courses (Name, Description, Price, ImageUrl, Duration, Level, ApprovalStatus, SubmissionDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        int courseId = -1;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, course.getName());
            ps.setString(2, course.getDescription());
            ps.setDouble(3, course.getPrice());
            ps.setString(4, course.getImageUrl());
            ps.setString(5, course.getDuration());
            ps.setString(6, course.getLevel());
            ps.setString(7, course.getApprovalStatus());
            ps.setTimestamp(8, new Timestamp(System.currentTimeMillis()));

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        courseId = rs.getInt(1);
                    }
                }
            }
        }
        return courseId;
    }

    public void insertCourseCategorie(Connection conn, int courseId, List<Integer> categoryIds) throws SQLException {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO CourseCategory (CourseID, CategoryID) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Integer catId : categoryIds) {
                ps.setInt(1, courseId);
                ps.setInt(2, catId);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public void insertCourseInstructor(Connection conn, int courseId, List<Integer> instructorIds) throws SQLException {
        if (instructorIds == null || instructorIds.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO CourseInstructors (CourseID, InstructorID) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Integer insId : instructorIds) {
                ps.setInt(1, courseId);
                ps.setInt(2, insId);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public boolean updateCourseFull(Course course, List<Integer> instructorIds, List<Integer> categoryIds) {
        boolean success = false;
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Cập nhật thông tin course chính
                updateCourse(conn, course);

                // 2. Xóa liên kết cũ
                deleteCourseInstructors(conn, course.getCourseID());
                deleteCourseCategories(conn, course.getCourseID());

                // 3. Thêm lại liên kết mới
                insertCourseInstructor(conn, course.getCourseID(), instructorIds);
                insertCourseCategorie(conn, course.getCourseID(), categoryIds);

                conn.commit();
                success = true;
            } catch (SQLException exception) {
                conn.rollback();
                throw exception;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException except) {
            Logger.getLogger(InstructorCourseServlet.class.getName()).log(Level.SEVERE, null, except);
        }
        return success;
    }

    public void updateCourse(Connection conn, Course course) throws SQLException {
        String sql = "UPDATE Courses SET Name=?, Description=?, Price=?, ImageUrl=?, Duration=?, Level=?, ApprovalStatus=? WHERE CourseID=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, course.getName());
            ps.setString(2, course.getDescription());
            ps.setDouble(3, course.getPrice());
            ps.setString(4, course.getImageUrl());
            ps.setString(5, course.getDuration());
            ps.setString(6, course.getLevel());
            ps.setString(7, course.getApprovalStatus());
            ps.setInt(8, course.getCourseID());
            ps.executeUpdate();
        }
    }

    public boolean isInstructorOwnerOfCourse(int instructorId, int courseId) {
        String sql = "SELECT 1 FROM CourseInstructors WHERE InstructorID = ? AND CourseID = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instructorId);
            ps.setInt(2, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Có record là instructor này sở hữu course này
            }
        } catch (SQLException e) {
            // Ghi log nếu muốn
            return false;
        }
    }

    public boolean deleteCourseById(int courseId) {
        boolean deleted = false;
        String sql = "DELETE FROM Courses WHERE CourseID = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            int affected = ps.executeUpdate();
            deleted = affected > 0;
        } catch (SQLException except) {
            Logger.getLogger(InstructorCourseServlet.class.getName()).log(Level.SEVERE, null, except);
        }
        return deleted;
    }

    public Course getCourseByIdAndInstructor(int courseId, int instructorId) {
        String sql = "SELECT c.* "
                + "FROM Courses c "
                + "INNER JOIN CourseInstructors ci ON c.CourseID = ci.CourseID "
                + "WHERE c.CourseID = ? AND ci.InstructorID = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.setInt(2, instructorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Course course = new Course();
                    course.setCourseID(rs.getInt("CourseID"));
                    course.setName(rs.getString("Name"));
                    course.setDescription(rs.getString("Description"));
                    course.setPrice(rs.getDouble("Price"));
                    course.setImageUrl(rs.getString("ImageUrl"));
                    course.setDuration(rs.getString("Duration"));
                    course.setLevel(rs.getString("Level"));
                    course.setApprovalStatus(rs.getString("ApprovalStatus"));
                    course.setSubmissionDate(rs.getTimestamp("SubmissionDate"));
                    course.setApprovalDate(rs.getTimestamp("ApprovalDate"));
                    course.setRejectionReason(rs.getString("RejectionReason"));
                    return course;
                }
            } catch (SQLException exception) {
                Logger.getLogger(InstructorCourseServlet.class.getName()).log(Level.SEVERE, null, exception);
            }
        } catch (SQLException except) {
            Logger.getLogger(InstructorCourseServlet.class.getName()).log(Level.SEVERE, null, except);

        }
        return null; // Không tìm thấy, hoặc instructor không có quyền
    }

    public List<Integer> getCategoryIdsByCourseId(int courseId) {
        List<Integer> categoryIds = new ArrayList<>();
        String sql = "SELECT CategoryID FROM CourseCategory WHERE CourseID = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    categoryIds.add(rs.getInt("CategoryID"));
                }
            } catch (SQLException exception) {
                Logger.getLogger(InstructorCourseServlet.class.getName()).log(Level.SEVERE, null, exception);

            }
        } catch (SQLException except) {
            Logger.getLogger(InstructorCourseServlet.class.getName()).log(Level.SEVERE, null, except);

        }
        return categoryIds;
    }

    public List<Integer> getInstructorIdsByCourseId(int courseId) {
        List<Integer> instructorIds = new ArrayList<>();
        String sql = "SELECT InstructorID FROM CourseInstructors WHERE CourseID = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    instructorIds.add(rs.getInt("InstructorID"));
                }
            } catch (SQLException exception) {
                Logger.getLogger(InstructorCourseServlet.class.getName()).log(Level.SEVERE, null, exception);
            }
        } catch (SQLException except) {
            Logger.getLogger(InstructorCourseServlet.class.getName()).log(Level.SEVERE, null, except);
        }
        return instructorIds;
    }

    public boolean updateCourseApprovalStatus(int courseId, String approvalStatus) {
        String sql = "UPDATE Courses SET ApprovalStatus = ?, SubmissionDate = ?, RejectionReason = NULL WHERE CourseID = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, approvalStatus);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setInt(3, courseId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            Logger.getLogger(CourseDAO.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    /**
     * Search courses by name or category.
     *
     * @param keyword    The searchCourseByNameOrCategory keyword for course name
     * @param categoryId The ID of the category to filter by (0 means all
     *                   categories)
     * @return List of matching courses
     */
    public List<Course> searchCourseByNameOrCategory(String keyword, int categoryId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Course> courses = new ArrayList<>();

        try {
            conn = getConnection();
            String sql;

            if (categoryId > 0) {
                // Search by name and category
                sql = "SELECT c.* FROM Courses c "
                        + "INNER JOIN CourseCategory cc ON c.CourseID = cc.CourseID "
                        + "WHERE c.Name LIKE ? AND cc.CategoryID = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + keyword + "%");
                ps.setInt(2, categoryId);
            } else {
                // Search by name only
                sql = "SELECT * FROM Courses WHERE Name LIKE ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, "%" + keyword + "%");
            }

            rs = ps.executeQuery();
            while (rs.next()) {
                Course course = mapCourse(rs);
                courses.add(course);
            }

            // Get categories for each course
            for (Course course : courses) {
                List<Category> categories = getCourseCategories(course.getCourseID());
                course.setCategories(categories);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }

        return courses;
    }

    /**
     * Get courses by category.
     *
     * @param categoryId The ID of the category
     * @return List of courses in the category
     */
    public List<Course> getByCategory(int categoryId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Course> courses = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT c.* FROM Courses c "
                    + "INNER JOIN CourseCategory cc ON c.CourseID = cc.CourseID "
                    + "WHERE cc.CategoryID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, categoryId);

            rs = ps.executeQuery();
            while (rs.next()) {
                Course course = mapCourse(rs);
                courses.add(course);
            }

            // Get categories for each course
            for (Course course : courses) {
                List<Category> categories = getCourseCategories(course.getCourseID());
                course.setCategories(categories);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(rs, ps, conn);
        }
        return courses;
    }

    /**
     * Get all courses with pagination.
     *
     * @param offset The offset (for pagination)
     * @param limit  The limit (for pagination)
     * @return List of courses
     */
    public List<Course> getAllWithLimit(int offset, int limit) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Course> courses = new ArrayList<>();

        try {
            conn = getConnection();
            String sql = "SELECT * FROM Courses WHERE ApprovalStatus = 'approved' ORDER BY CourseID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, offset);
            ps.setInt(2, limit);

            rs = ps.executeQuery();
            while (rs.next()) {
                Course course = mapCourse(rs);
                courses.add(course);
            }

            // Lấy dữ liệu phụ cho mỗi course
            for (Course course : courses) {
                try {
                    List<Category> categories = getCourseCategories(course.getCourseID());
                    course.setCategories(categories);

                    List<Instructor> instructors = getInstructorsForCourse(course.getCourseID());
                    course.setInstructors(instructors);

                    double avgRating = ratingDAO.getAverageRatingForCourse(course.getCourseID());
                    int ratingCount = ratingDAO.getRatingCountForCourse(course.getCourseID());
                    course.setAverageRating(avgRating);
                    course.setRatingCount(ratingCount);

                    List<Lesson> lessons = lessonDAO.getLessonsByCourseId(course.getCourseID());
                    course.setLessons(lessons);

                } catch (Exception e) {
                    System.err.println("Error getting additional data for course ID " + course.getCourseID() + ": "
                            + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in getAllWithLimit: " + e.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }
        return courses;
    }
}
