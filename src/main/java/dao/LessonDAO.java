/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import controller.instructor.InstructorCourseServlet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Lesson;
import db.DBContext;
import static db.DBContext.getConnection;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Material;
import model.Quiz;
import model.Video;
import model.LessonItem;
import model.LessonProgress;

/**
 * Data Access Object for Lesson entity.
 *
 * @author DangPH - CE180896
 */
public class LessonDAO extends DBContext {

    private MaterialDAO materialDAO;
    private QuizDAO quizDAO;
    private VideoDAO videoDAO;

    public LessonDAO() {
        this.materialDAO = new MaterialDAO();
        this.quizDAO = new QuizDAO();
        this.videoDAO = new VideoDAO();
    }

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
     * @return true if creation was successful, false otherwise
     */
    public boolean createLesson(Lesson lesson) {
        String sql = "INSERT INTO lessons (CourseID, Title, OrderIndex, CreatedAt, UpdatedAt) VALUES (?, ?, ?, GETDATE(), GETDATE())";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, lesson.getCourseID());
            stmt.setString(2, lesson.getTitle());
            stmt.setInt(3, lesson.getOrderIndex());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
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

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get a lesson by ID.
     *
     * @param lessonId The lesson ID
     * @return The lesson object, or null if not found
     */
    public Lesson getLessonById(int lessonId) {
        String sql = "SELECT * FROM lessons WHERE LessonID = ?";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, lessonId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapLesson(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void insertFullLesson(Connection conn, Lesson lesson) throws SQLException {
        // Insert the lesson itself
        String sql = "INSERT INTO Lessons (CourseID, Title, OrderIndex, CreatedAt) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Set the lesson values
            ps.setInt(1, lesson.getCourseID());
            ps.setString(2, lesson.getTitle());
            ps.setInt(3, lesson.getOrderIndex());
            ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));

            // Execute the insert for the lesson
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 1) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        lesson.setLessonID(rs.getInt(1)); // Assign the generated Lesson ID
                    }
                }
                // Create a list to track items by order
                List<LessonItem> lessonItems = lesson.getLessonItems();
                for (LessonItem item : lessonItems) {
                    item.setLessonID(lesson.getLessonID());
                    switch (item.getItemType()) {
                        case "video":
                            Video video = (Video) item.getItem();
                            video.setLessonID(lesson.getLessonID());
                            int videoId = videoDAO.insertWithConnection(conn, video);
                            item.setItemID(videoId);
                            break;
                        case "material":
                            Material material = (Material) item.getItem();
                            material.setLessonID(lesson.getLessonID());
                            int materialId = materialDAO.insertWithConnection(conn, material);
                            item.setItemID(materialId);
                            break;
                        case "quiz":
                            Quiz quiz = (Quiz) item.getItem();
                            quiz.setLessonID(lesson.getLessonID());
                            int quizId = quizDAO.insertWithConnection(conn, quiz);
                            item.setItem(quizId);
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown lesson item type : " + item.getItemType());
                    }

                }
                insertLessonItems(conn, lessonItems);
            } else {
                throw new SQLException("Insert Lesson Failed!");
            }
        }
    }

    /**
     * Insert multiple LessonItems in a batch operation
     *
     * @param conn        The database connection
     * @param lessonItems List of LessonItems to insert
     * @throws SQLException if a database error occurs
     */
    private void insertLessonItems(Connection conn, List<LessonItem> lessonItems) throws SQLException {
        if (lessonItems == null || lessonItems.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO LessonItems (LessonID, OrderIndex, ItemType, ItemID) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (LessonItem item : lessonItems) {
                ps.setInt(1, item.getLessonID());
                ps.setInt(2, item.getOrderIndex());
                ps.setString(3, item.getItemType());
                ps.setInt(4, item.getItemID());
                ps.addBatch();
            }
            ps.executeBatch();
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
     * @param lessonId  the lesson ID
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

    /**
     * Update the order index of a lesson
     *
     * @param lessonId      The lesson ID to update
     * @param newOrderIndex The new order index value
     * @return true if successful, false otherwise
     */
    public boolean updateLessonOrder(int lessonId, int newOrderIndex) {
        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = false;

        try {
            conn = getConnection();
            String sql = "UPDATE Lessons SET OrderIndex = ? WHERE LessonID = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, newOrderIndex);
            ps.setInt(2, lessonId);

            int affectedRows = ps.executeUpdate();
            success = (affectedRows > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return success;
    }

    /**
     * Updates the order indices of all lessons in a course based on provided
     * list of IDs
     *
     * @param courseId  The course ID
     * @param lessonIds List of lesson IDs in desired order
     * @return true if successful, false otherwise
     */
    public boolean reorderLessons(int courseId, List<Integer> lessonIds) {
        if (lessonIds == null || lessonIds.isEmpty()) {
            return false;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        boolean success = true;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            String sql = "UPDATE Lessons SET OrderIndex = ? WHERE LessonID = ? AND CourseID = ?";

            ps = conn.prepareStatement(sql);

            for (int i = 0; i < lessonIds.size(); i++) {
                ps.setInt(1, i); // OrderIndex starts from 0
                ps.setInt(2, lessonIds.get(i));
                ps.setInt(3, courseId);
                ps.addBatch();
            }

            int[] results = ps.executeBatch();
            for (int result : results) {
                if (result <= 0) {
                    success = false;
                    break;
                }
            }

            if (success) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (SQLException e) {
            success = false;
            e.printStackTrace();
            try {
                if (conn != null && !conn.getAutoCommit()) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return success;
    }

    public List<Integer> getLessonOrderIndexesByCourse(int courseId) {
        List<Integer> orderIndexes = new ArrayList<>();
        String sql = "SELECT OrderIndex FROM Lessons WHERE CourseID = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orderIndexes.add(rs.getInt("OrderIndex"));
                }
            } catch (SQLException exception) {
                Logger.getLogger(InstructorCourseServlet.class.getName()).log(Level.SEVERE, null, exception);
            }
        } catch (SQLException except) {
            Logger.getLogger(InstructorCourseServlet.class.getName()).log(Level.SEVERE, null, except);
        }
        return orderIndexes;
    }

    public List<Integer> getLessonOrderIndexesByCourseExceptLesson(int courseId, int exceptLessonId) {
        List<Integer> orderIndexes = new ArrayList<>();
        String sql = "SELECT OrderIndex FROM Lessons WHERE CourseID = ? AND LessonID <> ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ps.setInt(2, exceptLessonId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orderIndexes.add(rs.getInt("OrderIndex"));
                }
            } catch (SQLException exception) {
                Logger.getLogger(InstructorCourseServlet.class.getName()).log(Level.SEVERE, null, exception);
            }
        } catch (SQLException except) {
            Logger.getLogger(InstructorCourseServlet.class.getName()).log(Level.SEVERE, null, except);
        }
        return orderIndexes;
    }

    public boolean updateLesson(Lesson lesson) {
        String sql = "UPDATE Lessons SET Title = ?, OrderIndex = ?, UpdatedAt = GETDATE() WHERE LessonID = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lesson.getTitle());
            ps.setInt(2, lesson.getOrderIndex());
            ps.setInt(3, lesson.getLessonID());
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException exception) {
            Logger.getLogger(InstructorCourseServlet.class.getName()).log(Level.SEVERE, null, exception);
        }
        return false;
    }

    public boolean isInstructorOwnerOfLesson(int instructorId, int lessonId) {
        String sql = "SELECT 1 FROM Lessons l "
                + "JOIN CourseInstructors ci ON l.CourseID = ci.CourseID "
                + "WHERE l.LessonID = ? AND ci.InstructorID = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lessonId);
            ps.setInt(2, instructorId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            // log
            return false;
        }
    }

    public boolean deleteLessonById(int lessonId) {
        String sql = "DELETE FROM Lessons WHERE LessonID = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lessonId);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            Logger.getLogger(InstructorCourseServlet.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    public List<LessonItem> getLessonItemsByLessonID(int lessonId) {
        List<LessonItem> items = new ArrayList<>();
        String sql = "SELECT li.LessonItemID, li.LessonID, li.OrderIndex, li.ItemType, li.ItemID, "
                + "v.VideoID, v.Title AS VideoTitle, v.VideoUrl, v.Duration, v.Description AS VideoDescription, "
                + "m.MaterialID, m.Title AS MaterialTitle, m.FileUrl, m.Description AS MaterialDescription, m.Content, "
                + "q.QuizID, q.Title AS QuizTitle, q.Description AS QuizDescription, q.TimeLimit, q.PassingScore "
                + "FROM LessonItems li "
                + "LEFT JOIN Videos v ON li.ItemType = 'video' AND li.ItemID = v.VideoID "
                + "LEFT JOIN Materials m ON li.ItemType = 'material' AND li.ItemID = m.MaterialID "
                + "LEFT JOIN Quizzes q ON li.ItemType = 'quiz' AND li.ItemID = q.QuizID "
                + "WHERE li.LessonID = ? "
                + "ORDER BY li.OrderIndex";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lessonId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LessonItem li = new LessonItem();
                    li.setLessonItemID(rs.getInt("LessonItemID"));
                    li.setLessonID(rs.getInt("LessonID"));
                    li.setOrderIndex(rs.getInt("OrderIndex"));
                    li.setItemType(rs.getString("ItemType"));
                    li.setItemID(rs.getInt("ItemID"));

                    switch (li.getItemType()) {
                        case "video":
                            Video v = new Video();
                            v.setVideoID(rs.getInt("VideoID"));
                            v.setTitle(rs.getString("VideoTitle"));
                            v.setVideoUrl(rs.getString("VideoUrl"));
                            v.setDescription(rs.getString("VideoDescription"));
                            v.setDuration(rs.getInt("Duration"));
                            li.setItem(v);
                            break;
                        case "material":
                            Material m = new Material();
                            m.setMaterialID(rs.getInt("MaterialID"));
                            m.setTitle(rs.getString("MaterialTitle"));
                            m.setFileUrl(rs.getString("FileUrl"));
                            m.setDescription(rs.getString("MaterialDescription"));
                            m.setContent(rs.getString("Content"));
                            li.setItem(m);
                            break;
                        case "quiz":
                            Quiz q = new Quiz();
                            q.setQuizID(rs.getInt("QuizID"));
                            q.setTitle(rs.getString("QuizTitle"));
                            q.setDescription(rs.getString("QuizDescription"));
                            q.setTimeLimit(rs.getInt("TimeLimit"));
                            q.setPassingScore(rs.getInt("PassingScore"));
                            li.setItem(q);
                            break;
                    }
                    items.add(li);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error get lesson items by lessonId: " + e.getMessage());
        }
        return items;
    }

    public int addQuizToLesson(int lessonID, Quiz quiz) {
        int quizID = -1;
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psQuiz = conn.prepareStatement(
                    "INSERT INTO Quizzes (LessonID, Title, Description, TimeLimit, PassingScore) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                psQuiz.setInt(1, lessonID);
                psQuiz.setString(2, quiz.getTitle());
                psQuiz.setString(3, quiz.getDescription());
                // TimeLimit có thể null
                if (quiz.getTimeLimit() == null) {
                    psQuiz.setNull(4, Types.INTEGER);
                } else {
                    psQuiz.setInt(4, quiz.getTimeLimit());
                }
                psQuiz.setInt(5, quiz.getPassingScore());
                if (psQuiz.executeUpdate() == 0) {
                    throw new SQLException("Insert quiz failed, no rows affected.");
                }
                try (ResultSet rs = psQuiz.getGeneratedKeys()) {
                    if (rs.next()) {
                        quizID = rs.getInt(1);
                    } else {
                        throw new SQLException("Insert quiz failed, no ID obtained.");
                    }
                }
                insertLessonItem(conn, lessonID, "quiz", quizID);
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                System.err.println("Add quiz failed: " + ex.getMessage());
                return -1;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            System.err.println("Add quiz failed (connection): " + ex.getMessage());
            return -1;
        }
        return quizID;
    }

    public int addMaterialToLesson(int lessonID, Material material) {
        int materialID = -1;
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psMat = conn.prepareStatement(
                    "INSERT INTO Materials (LessonID, Title, Description, Content, FileUrl) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                psMat.setInt(1, lessonID);
                psMat.setString(2, material.getTitle());
                psMat.setString(3, material.getDescription());
                psMat.setString(4, material.getContent());
                psMat.setString(5, material.getFileUrl());
                if (psMat.executeUpdate() == 0) {
                    throw new SQLException("Insert material failed, no rows affected.");
                }
                try (ResultSet rs = psMat.getGeneratedKeys()) {
                    if (rs.next()) {
                        materialID = rs.getInt(1);
                    } else {
                        throw new SQLException("Insert material failed, no ID obtained.");
                    }
                }
                insertLessonItem(conn, lessonID, "material", materialID);
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                System.err.println("Add material failed: " + ex.getMessage());
                return -1;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            System.err.println("Add material failed (connection): " + ex.getMessage());
            return -1;
        }
        return materialID;
    }

    public int addVideoToLesson(int lessonID, Video video) {
        int videoID = -1;
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psVid = conn.prepareStatement(
                    "INSERT INTO Videos (LessonID, Title, Description, VideoUrl, Duration) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                psVid.setInt(1, lessonID);
                psVid.setString(2, video.getTitle());
                psVid.setString(3, video.getDescription());
                psVid.setString(4, video.getVideoUrl());
                psVid.setInt(5, video.getDuration());
                if (psVid.executeUpdate() == 0) {
                    throw new SQLException("Insert video failed, no rows affected.");
                }
                try (ResultSet rs = psVid.getGeneratedKeys()) {
                    if (rs.next()) {
                        videoID = rs.getInt(1);
                    } else {
                        throw new SQLException("Insert video failed, no ID obtained.");
                    }
                }
                insertLessonItem(conn, lessonID, "video", videoID);
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                System.err.println("Add video failed: " + ex.getMessage());
                return -1;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            System.err.println("Add video failed (connection): " + ex.getMessage());
            return -1;
        }
        return videoID;
    }

    private void insertLessonItem(Connection conn, int lessonID, String itemType, int itemID) throws SQLException {
        int orderIndex = 1;
        String sqlOrder = "SELECT ISNULL(MAX(OrderIndex),0)+1 FROM LessonItems WHERE LessonID=?";
        try (PreparedStatement psOrder = conn.prepareStatement(sqlOrder)) {
            psOrder.setInt(1, lessonID);
            try (ResultSet rs = psOrder.executeQuery()) {
                if (rs.next()) {
                    orderIndex = rs.getInt(1);
                }
            }
        }
        String sqlLessonItem = "INSERT INTO LessonItems (LessonID, OrderIndex, ItemType, ItemID) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sqlLessonItem)) {
            ps.setInt(1, lessonID);
            ps.setInt(2, orderIndex);
            ps.setString(3, itemType);
            ps.setInt(4, itemID);
            ps.executeUpdate();
        }
    }

}
