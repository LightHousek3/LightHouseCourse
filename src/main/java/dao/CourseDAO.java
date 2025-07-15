/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Category;
import model.Course;
import model.CourseCategory;
import model.CourseProgress;
import model.Customer;
import model.Instructor;
import model.Lesson;
import db.DBContext;

/**
 * Data Access Object for Course entity.
 *
 * @author DangPH - CE180896
 */
public class CourseDAO extends DBContext {

    private SuperUserDAO superUserDAO;
    private RatingDAO ratingDAO;
    private LessonDAO lessonDAO;

    public CourseDAO() {
        this.superUserDAO = new SuperUserDAO();
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
     * Delete course instructors
     *
     * @param conn The database connection
     * @param courseId The course ID
     * @throws SQLException If a database error occurs
     */
    private void deleteCourseInstructors(Connection conn, int courseId) throws SQLException {
        String sql = "DELETE FROM CourseInstructors WHERE CourseID = ?";

        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.executeUpdate();
        }
    }

    /**
     * Insert course instructors
     *
     * @param conn The database connection
     * @param courseId The course ID
     * @param instructors The list of instructors to insert
     * @throws SQLException If a database error occurs
     */
    private void insertCourseInstructors(Connection conn, int courseId, List<Instructor> instructors)
            throws SQLException {
        String sql = "INSERT INTO CourseInstructors (CourseID, InstructorID) VALUES (?, ?)";

        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
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

        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
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
     * @param conn The database connection
     * @param courseId The course ID
     * @throws SQLException If a database error occurs
     */
    private void deleteCourseCategories(Connection conn, int courseId) throws SQLException {
        String sql = "DELETE FROM CourseCategory WHERE CourseID = ?";

        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
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

        try ( Connection conn = getConnection();  PreparedStatement checkStmt = conn.prepareStatement(checkSql);  PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            checkStmt.setInt(1, courseID);
            try ( ResultSet rs = checkStmt.executeQuery()) {
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

        try ( Connection conn = getConnection();  PreparedStatement checkStmt = conn.prepareStatement(checkSql);  PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            checkStmt.setInt(1, courseID);
            try ( ResultSet rs = checkStmt.executeQuery()) {
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
            StringBuilder sql = new StringBuilder("SELECT * FROM Courses WHERE 1=1");
            List<Object> params = new ArrayList<>();

            if (status != null && !status.isEmpty()) {
                sql.append(" AND ApprovalStatus = ?");
                params.add(status);
            } else {
                sql.append(" AND ApprovalStatus <> 'draft'");
            }

            sql.append(" ORDER BY CourseID");

            if (limit > 0) {
                sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
                params.add(offset);
                params.add(limit);
            }

            ps = conn.prepareStatement(sql.toString());

            // Bind params
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
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
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) AS total FROM Courses WHERE 1=1");
            List<Object> params = new ArrayList<>();

            if (status != null && !status.isEmpty()) {
                sql.append(" AND ApprovalStatus = ?");
                params.add(status);
            } else {
                sql.append(" AND ApprovalStatus <> 'draft'");
            }

            ps = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
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
                + "FROM Courses c\n"
                + "LEFT JOIN CourseInstructors ci ON c.CourseID = ci.CourseID\n"
                + "LEFT JOIN Instructors i ON ci.InstructorID = i.InstructorID\n"
                + "LEFT JOIN SuperUsers su ON i.SuperUserID = su.SuperUserID\n"
                + "WHERE c.ApprovalStatus <> 'draft'\n"
                + "  AND (c.Name LIKE ? OR su.FullName LIKE ?)\n"
                + "ORDER BY c.CourseID\n"
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY;";

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
                + "FROM Courses c\n"
                + "LEFT JOIN CourseInstructors ci ON c.CourseID = ci.CourseID\n"
                + "LEFT JOIN Instructors i ON ci.InstructorID = i.InstructorID\n"
                + "LEFT JOIN SuperUsers su ON i.SuperUserID = su.SuperUserID\n"
                + "WHERE c.ApprovalStatus <> 'draft'\n"
                + "  AND (c.Name LIKE ? OR su.FullName LIKE ?)";

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            String likeKeyword = "%" + keyword + "%";
            ps.setString(1, likeKeyword);
            ps.setString(2, likeKeyword);

            try ( ResultSet rs = ps.executeQuery()) {
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
        } else {
            sql.append(" AND c.ApprovalStatus <> 'draft' ");
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
        } else {
            sql.append(" AND c.ApprovalStatus <> 'draft' ");
        }

        try ( Connection conn = getConnection();  PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try ( ResultSet rs = ps.executeQuery()) {
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
        try ( Connection conn = DBContext.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            stmt.setString(2, status);

            try ( ResultSet rs = stmt.executeQuery()) {
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
     * @param limit The maximum number of courses to return
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

    /**
     * Checks if an instructor is assigned to a specific course
     *
     * @param instructorId The instructor ID
     * @param courseId The course ID
     * @return true if the instructor is assigned to the course, false otherwise
     */
    public boolean isInstructorForCourse(int instructorId, int courseId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean isInstructor = false;

        try {
            conn = getConnection();
            String sql = "SELECT 1 FROM CourseInstructors WHERE InstructorID = ? AND CourseID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, instructorId);
            ps.setInt(2, courseId);
            rs = ps.executeQuery();

            if (rs.next()) {
                isInstructor = true;
            }
        } catch (SQLException e) {
            System.err.println("Error checking if instructor teaches course: " + e.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }

        return isInstructor;
    }
    
    /**
     * Get students enrolled in courses taught by an instructor with filtering and pagination
     *
     * @param instructorId   the instructor ID
     * @param page           the current page number (1-based)
     * @param pageSize       number of items per page
     * @param searchTerm     optional search term for name/email
     * @param courseFilter   optional course ID filter
     * @param progressFilter optional progress filter (completed, in-progress, not-started)
     * @return List of maps containing student data, course data, and progress data
     */
    public List<Map<String, Object>> getStudentsForInstructor(
            int instructorId, int page, int pageSize, String searchTerm, String courseFilter, String progressFilter) {
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Map<String, Object>> studentDataList = new ArrayList<>();
        
        try {
            conn = getConnection();
            
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT ");
            // Select specific columns instead of wildcards to avoid duplicate column names
            sql.append("c.CourseID, c.Name, c.Description, c.Price, c.ImageUrl, ");
            sql.append("cu.CustomerID, cu.Username, cu.Email, cu.IsActive, cu.FullName, cu.Phone, cu.Address, cu.Avatar, ");
            sql.append("cp.ProgressID, cp.CompletionPercentage, cp.LastAccessDate, cp.IsCompleted, ");
            sql.append("ROW_NUMBER() OVER (ORDER BY cu.FullName) as RowNum ");
            sql.append("FROM Customers cu ");
            sql.append("JOIN CourseProgress cp ON cu.CustomerID = cp.CustomerID ");
            sql.append("JOIN Courses c ON cp.CourseID = c.CourseID ");
            sql.append("JOIN CourseInstructors ci ON c.CourseID = ci.CourseID ");
            sql.append("WHERE ci.InstructorID = ? ");
            
            List<Object> params = new ArrayList<>();
            params.add(instructorId);
            
            // Add search filter if provided
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                sql.append("AND (cu.FullName LIKE ? OR cu.Email LIKE ?) ");
                params.add("%" + searchTerm + "%");
                params.add("%" + searchTerm + "%");
            }
            
            // Add course filter if provided
            if (courseFilter != null && !courseFilter.trim().isEmpty()) {
                try {
                    int courseId = Integer.parseInt(courseFilter);
                    sql.append("AND c.CourseID = ? ");
                    params.add(courseId);
                } catch (NumberFormatException e) {
                    // Invalid course ID, ignore filter
                }
            }
            
            // Add progress filter if provided
            if (progressFilter != null && !progressFilter.trim().isEmpty()) {
                switch (progressFilter.toLowerCase()) {
                    case "completed":
                        sql.append("AND cp.IsCompleted = 1 ");
                        break;
                    case "in-progress":
                        sql.append("AND cp.IsCompleted = 0 AND cp.CompletionPercentage > 0 ");
                        break;
                    case "not-started":
                        sql.append("AND cp.CompletionPercentage = 0 ");
                        break;
                    default:
                        // Invalid progress filter, ignore
                        break;
                }
            }
            
            // Calculate the offset for pagination
            int offset = (page - 1) * pageSize;
            
            // Add pagination with subquery
            String paginatedSql = "SELECT * FROM (" + sql.toString() + ") AS StudentData " +
                                  "WHERE RowNum BETWEEN ? AND ?";
            
            ps = conn.prepareStatement(paginatedSql);
            
            // Set parameters
            int paramIndex = 1;
            for (Object param : params) {
                ps.setObject(paramIndex++, param);
            }
            
            ps.setInt(paramIndex++, offset + 1);
            ps.setInt(paramIndex, offset + pageSize);
            
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> studentData = new HashMap<>();
                
                // Map customer data
                Customer student = new Customer();
                student.setCustomerID(rs.getInt("CustomerID"));
                student.setUsername(rs.getString("Username"));
                student.setEmail(rs.getString("Email"));
                student.setFullName(rs.getString("FullName"));
                student.setPhone(rs.getString("Phone"));
                student.setAddress(rs.getString("Address"));
                student.setAvatar(rs.getString("Avatar"));
                student.setActive(rs.getBoolean("IsActive"));
                
                // Map course data
                Course course = new Course();
                course.setCourseID(rs.getInt("CourseID"));
                course.setName(rs.getString("Name"));
                course.setDescription(rs.getString("Description"));
                course.setPrice(rs.getDouble("Price"));
                course.setImageUrl(rs.getString("ImageUrl"));
                
                // Map progress data
                CourseProgress progress = new CourseProgress();
                progress.setProgressID(rs.getInt("ProgressID"));
                progress.setCustomerID(rs.getInt("CustomerID"));
                progress.setCourseID(rs.getInt("CourseID"));
                progress.setCompletionPercentage(rs.getBigDecimal("CompletionPercentage"));
                progress.setLastAccessDate(rs.getTimestamp("LastAccessDate"));
                progress.setCompleted(rs.getBoolean("IsCompleted"));
                
                studentData.put("student", student);
                studentData.put("course", course);
                studentData.put("progress", progress);
                
                studentDataList.add(studentData);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving students for instructor: " + e.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }
        
        return studentDataList;
    }
    
    /**
     * Count students enrolled in courses taught by an instructor with filtering
     *
     * @param instructorId   the instructor ID
     * @param searchTerm     optional search term for name/email
     * @param courseFilter   optional course ID filter
     * @param progressFilter optional progress filter (completed, in-progress, not-started)
     * @return Total count of students matching the criteria
     */
    public int countStudentsForInstructor(
            int instructorId, String searchTerm, String courseFilter, String progressFilter) {
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;
        
        try {
            conn = getConnection();
            
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT COUNT(*) as total FROM (");
            sql.append("SELECT DISTINCT cu.CustomerID ");
            sql.append("FROM Customers cu ");
            sql.append("JOIN CourseProgress cp ON cu.CustomerID = cp.CustomerID ");
            sql.append("JOIN Courses c ON cp.CourseID = c.CourseID ");
            sql.append("JOIN CourseInstructors ci ON c.CourseID = ci.CourseID ");
            sql.append("WHERE ci.InstructorID = ? ");
            
            List<Object> params = new ArrayList<>();
            params.add(instructorId);
            
            // Add search filter if provided
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                sql.append("AND (cu.FullName LIKE ? OR cu.Email LIKE ?) ");
                params.add("%" + searchTerm + "%");
                params.add("%" + searchTerm + "%");
            }
            
            // Add course filter if provided
            if (courseFilter != null && !courseFilter.trim().isEmpty()) {
                try {
                    int courseId = Integer.parseInt(courseFilter);
                    sql.append("AND c.CourseID = ? ");
                    params.add(courseId);
                } catch (NumberFormatException e) {
                    // Invalid course ID, ignore filter
                }
            }
            
            // Add progress filter if provided
            if (progressFilter != null && !progressFilter.trim().isEmpty()) {
                switch (progressFilter.toLowerCase()) {
                    case "completed":
                        sql.append("AND cp.IsCompleted = 1 ");
                        break;
                    case "in-progress":
                        sql.append("AND cp.IsCompleted = 0 AND cp.CompletionPercentage > 0 ");
                        break;
                    case "not-started":
                        sql.append("AND cp.CompletionPercentage = 0 ");
                        break;
                    default:
                        // Invalid progress filter, ignore
                        break;
                }
            }
            
            sql.append(") AS StudentCount");
            
            ps = conn.prepareStatement(sql.toString());
            
            // Set parameters
            int paramIndex = 1;
            for (Object param : params) {
                ps.setObject(paramIndex++, param);
            }
            
            rs = ps.executeQuery();
            
            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error counting students for instructor: " + e.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }
        
        return count;
    }
    
    /**
     * Get course progress for a specific student in a specific course
     *
     * @param studentId the customer/student ID
     * @param courseId  the course ID
     * @return CourseProgress object or null if not found
     */
    public CourseProgress getStudentCourseProgress(int studentId, int courseId) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        CourseProgress progress = null;
        
        try {
            conn = getConnection();
            String sql = "SELECT * FROM CourseProgress WHERE CustomerID = ? AND CourseID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                progress = new CourseProgress();
                progress.setProgressID(rs.getInt("ProgressID"));
                progress.setCustomerID(rs.getInt("CustomerID"));
                progress.setCourseID(rs.getInt("CourseID"));
                progress.setCompletionPercentage(rs.getBigDecimal("CompletionPercentage"));
                progress.setLastAccessDate(rs.getTimestamp("LastAccessDate"));
                progress.setCompleted(rs.getBoolean("IsCompleted"));
                
                // Get course and user names for display
                Course course = getCourseById(courseId);
                if (course != null) {
                    progress.setCourseName(course.getName());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving student course progress: " + e.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }
        
        return progress;
    }

    /**
     * Search courses by name or category.
     *
     * @param keyword The searchCourseByNameOrCategory keyword for course name
     * @param categoryId The ID of the category to filter by (0 means all
     * categories)
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
     * @param limit The limit (for pagination)
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

    /**
     * Approve a course by updating its ApprovalStatus to 'APPROVED' and
     * ApprovalDate to now.
     *
     * @param courseId The ID of the course to approve
     * @return true if update successful, false otherwise
     */
    public boolean approveCourse(int courseId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            String sql = "UPDATE Courses SET ApprovalStatus = 'approved', ApprovalDate = GETDATE() WHERE CourseID = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, courseId);

            int affectedRows = ps.executeUpdate();
            return affectedRows == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

    /**
     * Reject a course by updating its ApprovalStatus to 'REJECTED' and set the
     * RejectionReason.
     *
     * @param courseId The ID of the course to reject
     * @param reason The rejection reason
     * @return true if update successful, false otherwise
     */
    public boolean rejectCourse(int courseId, String reason) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            String sql = "UPDATE Courses SET ApprovalStatus = 'rejected', RejectionReason = ? WHERE CourseID = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, reason);
            ps.setInt(2, courseId);

            int affectedRows = ps.executeUpdate();
            return affectedRows == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(null, ps, conn);
        }
    }

}
