/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.instructor;

import dao.CategoryDAO;
import dao.CourseDAO;
import dao.InstructorDAO;
import dao.LessonDAO;
import dao.LessonItemDAO;
import dao.MaterialDAO;
import dao.QuizDAO;
import dao.VideoDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import model.Answer;
import model.Category;
import model.Course;
import model.Instructor;
import model.Lesson;
import model.LessonItem;
import model.Material;
import model.Question;
import model.Quiz;
import model.SuperUser;
import model.Video;
import util.FileUploadUtil;
import util.Validator;

/**
 *
 * @author Pham Quoc Tu - CE181513
 */
@WebServlet(name = "InstructorCourseServlet", urlPatterns = {
    "/instructor/courses",
    "/instructor/courses/create",
    "/instructor/courses/delete",
    "/instructor/courses/submit",
    "/instructor/courses/change",
    "/instructor/courses/edit/*",
    "/instructor/courses/view/*",
    "/instructor/courses/lessons/create",
    "/instructor/courses/lessons/delete",
    "/instructor/courses/lessons/edit/*",
    "/instructor/courses/lessons/view/*",
    "/instructor/lessons/quizzes/create",
    "/instructor/lessons/materials/create",
    "/instructor/lessons/videos/create",
    "/instructor/lessons/materials/edit",
    "/instructor/lessons/videos/edit",
    "/instructor/lessons/quizzes/edit",
    "/instructor/lessons/materials/delete",
    "/instructor/lessons/videos/delete",
    "/instructor/lessons/quizzes/delete",
    "/instructor/lessons/quizzes/view/*",
    "/instructor/lessons/quizzes/questions/create",
    "/instructor/lessons/quizzes/questions/edit/*",
    "/instructor/lessons/quizzes/questions/delete"
})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1MB
        maxFileSize = 250 * 1024 * 1024, // 250MB
        maxRequestSize = 500 * 1024 * 1024 // 500MB
)
public class InstructorCourseServlet extends HttpServlet {

    private CourseDAO courseDAO;
    private LessonDAO lessonDAO;
    private InstructorDAO instructorDAO;
    private CategoryDAO categoryDAO;
    private QuizDAO quizDAO;
    private VideoDAO videoDAO;
    private MaterialDAO materialDAO;
    private LessonItemDAO lessonItemDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        courseDAO = new CourseDAO();
        lessonDAO = new LessonDAO();
        instructorDAO = new InstructorDAO();
        categoryDAO = new CategoryDAO();
        quizDAO = new QuizDAO();
        videoDAO = new VideoDAO();
        materialDAO = new MaterialDAO();
        lessonItemDAO = new LessonItemDAO();
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();
        String pathInfo = request.getPathInfo();

        // Check if user is logged in
        HttpSession session = request.getSession();
        SuperUser user;

        try {
            user = (SuperUser) session.getAttribute("user");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        // Get instructor information using getInstructorBySuperUserId
        Instructor instructor = instructorDAO.getInstructorBySuperUserId(user.getSuperUserID());
        // Add instructor to request attributes
        request.setAttribute("avatar", user.getAvatar());
        request.setAttribute("instructor", instructor);
        if (instructor == null) {
            // If no instructor record exists, redirect to 404 page
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (path.equals("/instructor/courses") && pathInfo == null) {
            listCourses(request, response, instructor);
        } else if (path.equals("/instructor/courses/create") && pathInfo == null) {
            showFormCreateCourse(request, response, instructor);
        } else if (path.equals("/instructor/courses/edit") && pathInfo != null) {
            showFormEditCourse(request, response, instructor);
        } else if (path.equals("/instructor/courses/lessons/view") && pathInfo != null) {
            listLessonItems(request, response, instructor);
        } else if (path.equals("/instructor/lessons/quizzes/view") && pathInfo != null) {
            listQuestion(request, response, instructor);
        } else if (path.equals("/instructor/lessons/quizzes/questions/edit") && pathInfo != null) {
            showFormEditQuestion(request, response, instructor);
        } else if (path.equals("/instructor/courses/view") && pathInfo != null) {
            viewDetailCourse(request, response, instructor);
        }

    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();
        String pathInfo = request.getPathInfo();
        // Check if user is logged in
        HttpSession session = request.getSession();
        SuperUser user;

        try {
            user = (SuperUser) session.getAttribute("user");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // Get instructor information using getInstructorBySuperUserId
        Instructor instructor = instructorDAO.getInstructorBySuperUserId(user.getSuperUserID());
        // Add instructor to request attributes
        request.setAttribute("avatar", user.getAvatar());
        request.setAttribute("instructor", instructor);
        if (instructor == null) {
            // If no instructor record exists, redirect to 404 page
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (path.equals("/instructor/courses/create")) {
            createCourse(request, response, instructor);
        } else if (path.equals("/instructor/courses/lessons/create")) {
            createLesson(request, response, instructor);
        } else if (path.equals("/instructor/courses/lessons/edit")) {
            editLesson(request, response, instructor);
        } else if (path.equals("/instructor/courses/edit")) {
            editCourse(request, response, instructor);
        } else if (path.equals("/instructor/courses/delete")) {
            deleteCourse(request, response, instructor);
        } else if (path.equals("/instructor/courses/lessons/delete")) {
            deleteLesson(request, response, instructor);
        } else if (path.equals("/instructor/courses/submit")) {
            submitCourse(request, response, instructor);
        } else if (path.equals("/instructor/courses/change")) {
            cancelSubmitCourse(request, response, instructor);
        } else if (path.equals("/instructor/lessons/quizzes/create")) {
            createQuiz(request, response, instructor);
        } else if (path.equals("/instructor/lessons/materials/create")) {
            createMaterial(request, response, instructor);
        } else if (path.equals("/instructor/lessons/videos/create")) {
            createVideo(request, response, instructor);
        } else if (path.equals("/instructor/lessons/quizzes/edit")) {
            editQuiz(request, response, instructor);
        } else if (path.equals("/instructor/lessons/videos/edit")) {
            editVideo(request, response, instructor);
        } else if (path.equals("/instructor/lessons/materials/edit")) {
            editMaterial(request, response, instructor);
        } else if (path.equals("/instructor/lessons/quizzes/delete")) {
            deleteQuiz(request, response, instructor);
        } else if (path.equals("/instructor/lessons/videos/delete")) {
            deleteVideo(request, response, instructor);
        } else if (path.equals("/instructor/lessons/materials/delete")) {
            deleteMaterial(request, response, instructor);
        } else if (path.equals("/instructor/lessons/quizzes/questions/create")) {
            createQuestion(request, response, instructor);
        } else if (path.equals("/instructor/lessons/quizzes/questions/edit")) {
            editQuestion(request, response, instructor);
        } else if (path.equals("/instructor/lessons/quizzes/questions/delete")) {
            deleteQuestion(request, response, instructor);
        }

    }

    private void listCourses(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {

        List<Course> courses = courseDAO.getCoursesByInstructorId(instructor.getInstructorID());
        request.setAttribute("courses", courses);
        request.getRequestDispatcher("/WEB-INF/views/instructor/manage-courses/courses.jsp").forward(request, response);
    }

    private void showFormCreateCourse(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {

        List<Category> categories = categoryDAO.getAllCategories();
        List<Instructor> instructors = instructorDAO.getAllInstructorsExcept(instructor.getInstructorID());
        request.setAttribute("categories", categories);
        request.setAttribute("instructorList", instructors);
        request.getRequestDispatcher("/WEB-INF/views/instructor/manage-courses/create-course.jsp").forward(request, response);
    }

    private void createCourse(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {

        Map<String, String> errors = new HashMap<>();

        // All attributes of course from request
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String priceStr = request.getParameter("price");
        String durationStr = request.getParameter("duration");
        String level = request.getParameter("level");
        String instructorIdsStr = request.getParameter("instructorIds");
        String categoryIdsStr = request.getParameter("categoryIds");
        String action = request.getParameter("action"); // only is "draft" not check, if devtool fix action ignore
        // Validate for action  clean
        if (!("draft".equals(action))) {
            action = "draft";
        }
        // Validate for name not null, not empty, not exceed 50 characters
        if (Validator.isNullOrEmpty(name)) {
            errors.put("name", "Course name is required.");
        } else if (!Validator.isValidText(name, 80)) {
            errors.put("name", "Maximum length is 80 characters.");
        } else {
            name = name.trim();
            if (courseDAO.isCourseNameExists(name, null)) {
                errors.put("name", "This course name is already taken.");
            }
        }

        if (Validator.isNullOrEmpty(description)) {
            errors.put("description", "Description is required.");
        } else if (!Validator.isValidText(description, 250)) {
            errors.put("description", "Maximum length is 250 characters.");
        } else {
            description = description.trim();
        }
        double price = 0.0;
        if (Validator.isNullOrEmpty(priceStr)) {
            errors.put("price", "Price is required.");
        } else if (!Validator.isValidDouble(priceStr, 0.0, 10000000.0)) {
            errors.put("price", "Price must be a valid number from 0 to 10000000.");
        } else {
            price = Validator.parseDoubleOrDefault(priceStr, 0.0);
        }

        if (Validator.isNullOrEmpty(level)) {
            errors.put("level", "Level is required.");
        }
        int duration = 1;
        if (Validator.isNullOrEmpty(durationStr)) {
            errors.put("duration", "Duration is required.");
        } else if (!Validator.isValidInteger(durationStr, 1, 50)) {
            errors.put("duration", "Duration must be a valid number from 1 to 50.");
        } else {
            duration = Validator.parseIntOrDefault(durationStr, 1);
        }
        if (Validator.isNullOrEmpty(categoryIdsStr)) {
            errors.put("categoryIds", "Please select at least one category.");
        }
        String imgUrl = FileUploadUtil.handleUpload(
                request.getPart("imageFile"),
                request.getServletContext().getRealPath("/assets/imgs/courses"),
                "/assets/imgs/courses",
                5L * 1024 * 1024,
                new String[]{".jpg", ".jpeg", ".png", ".gif"},
                errors,
                "imageFile",
                true);

        List<Integer> instructorIds = new ArrayList<>();
        List<Integer> categoryIds = new ArrayList<>();

        // Validate instructor
        if (instructorIdsStr != null && !instructorIdsStr.trim().isEmpty()) {
            for (String id : instructorIdsStr.split(",")) {
                if (id != null && !id.trim().isEmpty()) {
                    instructorIds.add(Integer.parseInt(id.trim()));
                }
            }
        }
        // Validate category
        if (categoryIdsStr != null && !categoryIdsStr.trim().isEmpty()) {
            for (String id : categoryIdsStr.split(",")) {
                if (id != null && !id.trim().isEmpty()) {
                    categoryIds.add(Integer.parseInt(id.trim()));
                }
            }
        }
        // If có lỗi validate thì trả lại form 
        if (!errors.isEmpty()) {

            List<Category> categories = categoryDAO.getAllCategories();
            List<Instructor> instructors = instructorDAO.getAllInstructorsExcept(instructor.getInstructorID());
            request.setAttribute("categories", categories);
            request.setAttribute("instructorList", instructors);
            request.setAttribute("selectedCategoryIds", categoryIds);
            request.setAttribute("selectedInstructorIds", instructorIds);
            request.setAttribute("selectedInstructorIdsAsString", instructorIdsStr);
            request.setAttribute("selectedCategoryIdsAsString", categoryIdsStr);
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("/WEB-INF/views/instructor/manage-courses/create-course.jsp").forward(request, response);
            return;
        }
        if (!instructorIds.contains(instructor.getInstructorID())) {
            instructorIds.add(instructor.getInstructorID());
        }
        // If không lỗi validate thì tạo đối tượng course để insert
        Course course = new Course(name, description, price, imgUrl, duration + " weeks", level, action);
        // Bắt đầu insert courese
        int created = courseDAO.insertCourseFull(course, instructorIds, categoryIds);
        // Kiểm tra xem thành công hay thất bại
        if (created > 0) {
            // Thành công thì redirect về trang list course cùng session messeage (được xóa ngay sau khi hiện)
            request.getSession().setAttribute("message", "Course created successfully.");
        } else {
            // Thất bại thì redirect về trang list course cùng session error (được xóa ngay sau khi hiện)
            request.getSession().setAttribute("error", "Course creation failed. System error occurred.");
        }
        response.sendRedirect(request.getContextPath() + "/instructor/courses");
    }

    private void showFormEditCourse(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {

        String partInfo = request.getPathInfo();
        String[] pathParts = partInfo.split("/");
        if (pathParts.length < 2 || !Validator.isValidInteger(pathParts[1])) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // lấy courseID valid từ url sau kiểm tra
        int courseIdValid = Integer.parseInt(pathParts[1]);
        // dùng courseID valid và instructorID để lấy thông tin khóa học
        // không cho phép lấy khóa học của instructorID khác
        Course course = courseDAO.getCourseByIdAndInstructor(courseIdValid, instructor.getInstructorID());
        // nếu course là null thì instructor không sở hữu khóa học đó
        if (course == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // nếu course not null thì phải kiểm tra trạng thái trước
        // trạng thái banned, approved, pending sẽ không được phép edit
        if (courseDAO.isCourseStatus(courseIdValid, "banned")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (courseDAO.isCourseStatus(courseIdValid, "approved")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (courseDAO.isCourseStatus(courseIdValid, "pending")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // bởi vì trên db lưu durationStr là dạng 1 weeks, 2 weeks,... 
        // nên chỉ lấy phần đầu
        String[] durationInfo = course.getDuration().split(" ");
        // "2 weeks" durationInfo[0] = 2, durationInfo[1] = weeks
        course.setDurationNumber(Integer.parseInt(durationInfo[0]));
        // lấy những thông tin cần thiết của course và lessons của nó bằng courseIdValid
        List<Integer> categoryIds = courseDAO.getCategoryIdsByCourseId(courseIdValid);
        List<Integer> instructorIds = courseDAO.getInstructorIdsByCourseId(courseIdValid);
        List<Lesson> lessons = lessonDAO.getLessonsByCourseId(courseIdValid);
        List<Category> categories = categoryDAO.getAllCategories();
        List<Instructor> instructors = instructorDAO.getAllInstructorsExcept(instructor.getInstructorID());
        // điền lại những category và instuctor đã chọn
        request.setAttribute("selectedCategoryIds", Validator.joinIntegerList(categoryIds));
        request.setAttribute("selectedInstructorIds", Validator.joinIntegerList(instructorIds));
        request.setAttribute("categories", categories);
        request.setAttribute("instructorList", instructors);
        request.setAttribute("lessons", lessons);
        request.setAttribute("course", course);

        // load session
        request.setAttribute("lessonErrors", request.getSession().getAttribute("lessonErrors"));
        request.setAttribute("editLessonErrors", request.getSession().getAttribute("editLessonErrors"));
        request.setAttribute("lesson", request.getSession().getAttribute("lesson"));
        request.setAttribute("editLesson", request.getSession().getAttribute("editLesson"));
        request.setAttribute("openAddLessonModal", request.getSession().getAttribute("openAddLessonModal"));
        request.setAttribute("openEditLessonModal", request.getSession().getAttribute("openEditLessonModal"));

        request.getRequestDispatcher("/WEB-INF/views/instructor/manage-courses/edit-course.jsp").forward(request, response);

        // remove session
        request.getSession().removeAttribute("lessonErrors");
        request.getSession().removeAttribute("editLessonErrors");
        request.getSession().removeAttribute("lesson");
        request.getSession().removeAttribute("editLesson");
        request.getSession().removeAttribute("openAddLessonModal");
        request.getSession().removeAttribute("openEditLessonModal");
    }

    private void editCourse(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {

        Map<String, String> errors = new HashMap<>();
        // All attributes of course from request
        String courseID = request.getParameter("courseID");
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String priceStr = request.getParameter("price");
        String durationStr = request.getParameter("duration");
        String level = request.getParameter("level");
        String instructorIdsStr = request.getParameter("instructorIds");
        String categoryIdsStr = request.getParameter("categoryIds");
        String action = request.getParameter("action"); // only is "draft"
        if (!Validator.isValidInteger(courseID)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (!("draft".equals(action))) {
            action = "draft";
        }

        if (Validator.isNullOrEmpty(name)) {
            errors.put("name", "Course name is required.");
        } else if (!Validator.isValidText(name, 80)) {
            errors.put("name", "Maximum length is 80 characters.");
        } else {
            name = name.trim();
            if (courseDAO.isCourseNameExists(name, Integer.parseInt(courseID))) {
                errors.put("name", "This course name is already taken.");
            }
        }
        if (Validator.isNullOrEmpty(description)) {
            errors.put("description", "Description is required.");
        } else if (!Validator.isValidText(description, 250)) {
            errors.put("description", "Maximum length is 250 characters.");
        } else {
            description = description.trim();
        }

        double price = 0.0;
        if (Validator.isNullOrEmpty(priceStr)) {
            errors.put("price", "Price is required.");
        } else if (!Validator.isValidDouble(priceStr, 0.0, 10000000.0)) {
            errors.put("price", "Price must be a valid number from 0 to 10000000.");
        } else {
            price = Validator.parseDoubleOrDefault(priceStr, 0.0);
        }

        if (Validator.isNullOrEmpty(level)) {
            errors.put("level", "Level is required.");
        }

        int duration = 1;
        if (Validator.isNullOrEmpty(durationStr)) {
            errors.put("duration", "Duration is required.");
        } else if (!Validator.isValidInteger(durationStr, 1, 50)) {
            errors.put("duration", "Duration must be a valid number from 1 to 50.");
        } else {
            duration = Validator.parseIntOrDefault(durationStr, 1);
        }
        if (Validator.isNullOrEmpty(categoryIdsStr)) {
            errors.put("categoryIds", "Please select at least one category.");
        }

        String imgUrl = FileUploadUtil.handleUpload(
                request.getPart("imageFile"),
                request.getServletContext().getRealPath("/assets/imgs/courses"),
                "/assets/imgs/courses",
                5L * 1024 * 1024,
                new String[]{".jpg", ".jpeg", ".png", ".gif"},
                errors,
                "imageFile",
                false);
        int courseIdValid = Integer.parseInt(courseID);
        int durationValid = duration;
        double priceValid = price;
        List<Integer> instructorIds = new ArrayList<>();
        List<Integer> categoryIds = new ArrayList<>();
        // Validate instructor
        if (instructorIdsStr != null && !instructorIdsStr.trim().isEmpty()) {
            for (String id : instructorIdsStr.split(",")) {
                if (id != null && !id.trim().isEmpty()) {
                    instructorIds.add(Integer.parseInt(id.trim()));
                }
            }
        }
        // Validate category
        if (categoryIdsStr != null && !categoryIdsStr.trim().isEmpty()) {
            for (String id : categoryIdsStr.split(",")) {
                if (id != null && !id.trim().isEmpty()) {
                    categoryIds.add(Integer.parseInt(id.trim()));
                }
            }
        }
        List<Category> categories = categoryDAO.getAllCategories();
        List<Instructor> instructors = instructorDAO.getAllInstructorsExcept(instructor.getInstructorID());
        List<Lesson> lesson = lessonDAO.getLessonsByCourseId(courseIdValid);
        Course courseToGiveImageUrl = courseDAO.getCourseByIdAndInstructor(courseIdValid, instructor.getInstructorID());
        String oldImage = (courseToGiveImageUrl != null) ? courseToGiveImageUrl.getImageUrl() : "";
        if (!errors.isEmpty()) {

            Course course = new Course();
            course.setCourseID(courseIdValid);
            course.setName(name);
            course.setDescription(description);
            course.setPrice(priceValid);
            course.setLevel(level);
            course.setDurationNumber(durationValid);
            course.setImageUrl(oldImage);
            request.setAttribute("errors", errors);
            request.setAttribute("course", course);
            request.setAttribute("lessons", lesson);
            request.setAttribute("categories", categories);
            request.setAttribute("instructorList", instructors);
            request.setAttribute("selectedCategoryIds", Validator.joinIntegerList(categoryIds));
            request.setAttribute("selectedInstructorIds", Validator.joinIntegerList(instructorIds));
            request.getRequestDispatcher("/WEB-INF/views/instructor/manage-courses/edit-course.jsp").forward(request, response);
            return;
        }
        if (!instructorIds.contains(instructor.getInstructorID())) {
            instructorIds.add(instructor.getInstructorID());
        }
        Course course = new Course();
        course.setCourseID(courseIdValid);
        course.setPrice(priceValid);
        course.setName(name);
        course.setLevel(level);
        course.setDescription(description);
        course.setApprovalStatus(action);
        course.setDuration(durationValid + " weeks");
        if (Validator.isNullOrEmpty(imgUrl)) {
            course.setImageUrl(oldImage);
        } else {
            course.setImageUrl(imgUrl);
        }
        boolean updated = courseDAO.updateCourseFull(course, instructorIds, categoryIds);
        if (updated) {
            request.getSession().setAttribute("message", "Course updated successfully.");
        } else {
            request.getSession().setAttribute("error", "Update course failed due to a system error. Please try again or contact admin.");
        }
        response.sendRedirect(request.getContextPath() + "/instructor/courses/edit/" + courseIdValid + "?tab=course");
    }

    private void deleteCourse(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {

        String courseID = request.getParameter("courseID");
        if (!Validator.isValidInteger(courseID)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        int courseIdvalid = Integer.parseInt(courseID);
        boolean isOwner = courseDAO.isInstructorOwnerOfCourse(instructor.getInstructorID(), courseIdvalid);
        if (!isOwner) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        boolean deleted = courseDAO.deleteCourseById(courseIdvalid);
        if (deleted) {
            request.getSession().setAttribute("message", "Course deleted successfully.");
        } else {
            request.getSession().setAttribute("error", "Failed to delete course. System error occurred.");
        }
        response.sendRedirect(request.getContextPath() + "/instructor/courses");
    }

    private void submitCourse(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {

        String courseID = request.getParameter("courseID");
        if (!Validator.isValidInteger(courseID)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        int courseIdValid = Integer.parseInt(courseID);

        boolean isOwner = courseDAO.isInstructorOwnerOfCourse(instructor.getInstructorID(), courseIdValid);
        if (!isOwner) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String STATUS = "pending";
        boolean updatedStatus = courseDAO.updateCourseApprovalStatus(courseIdValid, STATUS);
        if (updatedStatus) {
            request.getSession().setAttribute("message", "Course status updated successfully.");
        } else {
            request.getSession().setAttribute("error", "Failed to update course status. System error occurred.");
        }
        response.sendRedirect(request.getContextPath() + "/instructor/courses");
    }

    private void cancelSubmitCourse(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {

        String courseID = request.getParameter("courseID");
        if (!Validator.isValidInteger(courseID)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        int courseIdValid = Integer.parseInt(courseID);

        boolean isOwner = courseDAO.isInstructorOwnerOfCourse(instructor.getInstructorID(), courseIdValid);
        if (!isOwner) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String STATUS = "draft";
        boolean updatedStatus = courseDAO.updateCourseApprovalStatus(courseIdValid, STATUS);
        if (updatedStatus) {
            request.getSession().setAttribute("message", "Course status updated successfully.");
        } else {
            request.getSession().setAttribute("error", "Failed to update course status. System error occurred.");
        }
        response.sendRedirect(request.getContextPath() + "/instructor/courses");
    }

    private void createLesson(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {

        Map<String, String> errors = new HashMap<>();
        String courseID = request.getParameter("courseID");
        if (!Validator.isValidInteger(courseID)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        int courseIdValid = Integer.parseInt(courseID);
        String title = request.getParameter("title");
        if (Validator.isNullOrEmpty(title)) {
            errors.put("title", "Lesson title is required.");
        } else if (!Validator.isValidText(title, 150)) {
            errors.put("title", "Maximun length is 150 characters.");
        } else {
            title = title.trim();
            if (lessonDAO.isLessonTitleExists(title, courseIdValid, null)) {
                errors.put("title", "This lesson title is already taken.");
            }
        }
        int orderIndex = 1;
        String orderIndexStr = request.getParameter("orderIndex");
        if (!Validator.isValidInteger(orderIndexStr)) {
            errors.put("orderIndex", "Lesson order must be a valid number.");
        } else {
            orderIndex = Integer.parseInt(orderIndexStr);
            if (orderIndex <= 0) {
                errors.put("orderIndex", "Lesson order must be a valid number.");
            }
            List<Integer> orderIndexCurrent = lessonDAO.getLessonOrderIndexesByCourse(courseIdValid);
            if (!orderIndexCurrent.isEmpty()) {
                for (Integer integer : orderIndexCurrent) {
                    if (integer == orderIndex) {
                        errors.put("orderIndex", "Lesson order is duplicated.");
                        break;
                    }
                }
            }
        }
        int orderIndexValid = orderIndex;
        if (!errors.isEmpty()) {
            Lesson lesson = new Lesson();
            lesson.setTitle(title);
            lesson.setOrderIndex(orderIndexValid);
            request.getSession().setAttribute("lesson", lesson);
            request.getSession().setAttribute("lessonErrors", errors);
            request.getSession().setAttribute("openAddLessonModal", true);
            response.sendRedirect(request.getContextPath() + "/instructor/courses/edit/" + courseIdValid + "?tab=lesson");
            return;
        }
        Lesson lesson = new Lesson();
        lesson.setCourseID(courseIdValid);
        lesson.setTitle(title);
        lesson.setOrderIndex(orderIndexValid);
        boolean created = lessonDAO.createLesson(lesson);
        if (created) {
            request.getSession().setAttribute("message", "New lesson created successfully.");

        } else {
            request.getSession().setAttribute("error", "Create lesson failed due to a system error. Please try again or contact admin.");
        }
        response.sendRedirect(request.getContextPath() + "/instructor/courses/edit/" + courseIdValid + "?tab=lesson");
    }

    private void editLesson(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {

        Map<String, String> errors = new HashMap<>();
        String courseID = request.getParameter("courseID");
        String lessonID = request.getParameter("lessonID");
        if (!Validator.isValidInteger(courseID) || !Validator.isValidInteger(lessonID)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        int courseIdValid = Integer.parseInt(courseID);
        int lessonIdValid = Integer.parseInt(lessonID);
        String title = request.getParameter("title");
        if (Validator.isNullOrEmpty(title)) {
            errors.put("title", "Lesson title is required.");
        } else if (!Validator.isValidText(title, 150)) {
            errors.put("title", "Maximun length is 150 characters.");
        } else {
            title = title.trim();
            if (lessonDAO.isLessonTitleExists(title, courseIdValid, lessonIdValid)) {
                errors.put("title", "This lesson title is already taken.");
            }
        }
        int orderIndex = 1;
        String orderIndexStr = request.getParameter("orderIndex");
        if (!Validator.isValidInteger(orderIndexStr)) {
            errors.put("orderIndex", "Lesson order must be a valid number.");
        } else {
            orderIndex = Integer.parseInt(orderIndexStr);
            if (orderIndex <= 0) {
                errors.put("orderIndex", "Lesson order must be a valid number.");
            }
            List<Integer> orderIndexCurrent = lessonDAO.getLessonOrderIndexesByCourseExceptLesson(courseIdValid, lessonIdValid);
            if (!orderIndexCurrent.isEmpty()) {
                for (Integer integer : orderIndexCurrent) {
                    if (integer == orderIndex) {
                        errors.put("orderIndex", "Lesson order is duplicated.");
                        break;
                    }
                }
            }
        }
        int orderIndexValid = orderIndex;

        if (!errors.isEmpty()) {

            Lesson lessonReturn = new Lesson();
            lessonReturn.setCourseID(courseIdValid);
            lessonReturn.setLessonID(lessonIdValid);
            lessonReturn.setTitle(title);
            lessonReturn.setOrderIndex(orderIndexValid);
            request.getSession().setAttribute("editLesson", lessonReturn);
            request.getSession().setAttribute("editLessonErrors", errors);
            request.getSession().setAttribute("openEditLessonModal", true);
            response.sendRedirect(request.getContextPath() + "/instructor/courses/edit/" + courseIdValid + "?tab=lesson");
            return;
        }

        Lesson lesson = new Lesson();
        lesson.setCourseID(courseIdValid);
        lesson.setLessonID(lessonIdValid);
        lesson.setTitle(title);
        lesson.setOrderIndex(orderIndexValid);
        boolean updated = lessonDAO.updateLesson(lesson);
        if (updated) {
            request.getSession().setAttribute("message", "Lesson updated successfully.");
        } else {
            request.getSession().setAttribute("error", "Update lesson failed due to a system error. Please try again or contact admin.");
        }
        response.sendRedirect(request.getContextPath() + "/instructor/courses/edit/" + courseIdValid + "?tab=lesson");
    }

    private void deleteLesson(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {

        String courseID = request.getParameter("courseID");
        String lessonID = request.getParameter("lessonID");
        if (!Validator.isValidInteger(lessonID) || !Validator.isValidInteger(courseID)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        int courseIdValid = Integer.parseInt(courseID);
        int lessonIdValid = Integer.parseInt(lessonID);
        boolean isOwner = lessonDAO.isInstructorOwnerOfLesson(instructor.getInstructorID(), lessonIdValid);
        if (!isOwner) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        boolean deleted = lessonDAO.deleteLessonById(lessonIdValid);
        if (deleted) {
            request.getSession().setAttribute("message", "Lesson deleted successfully.");
        } else {
            request.getSession().setAttribute("error", "Failed to delete lesson. System error occurred.");
        }
        response.sendRedirect(request.getContextPath() + "/instructor/courses/edit/" + courseIdValid + "?tab=lesson");
        return;
    }

    private void listLessonItems(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {

        String partInfo = request.getPathInfo();
        String courseID = request.getParameter("courseID");
        String[] pathParts = partInfo.split("/");
        if (pathParts.length < 2 || !Validator.isValidInteger(pathParts[1]) || !Validator.isValidInteger(courseID)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        int lessonIdValid = Integer.parseInt(pathParts[1]);
        int courseIdValid = Integer.parseInt(courseID);
        List<LessonItem> lessonItems = lessonDAO.getLessonItemsByLessonID(lessonIdValid);
        request.setAttribute("courseID", courseIdValid);
        request.setAttribute("lessonID", lessonIdValid);
        request.setAttribute("lessonItems", lessonItems);
        // Lấy lại từ session
        request.setAttribute("quiz", request.getSession().getAttribute("quiz"));
        request.setAttribute("editQuiz", request.getSession().getAttribute("editQuiz"));
        request.setAttribute("material", request.getSession().getAttribute("material"));
        request.setAttribute("editMaterial", request.getSession().getAttribute("editMaterial"));
        request.setAttribute("video", request.getSession().getAttribute("video"));
        request.setAttribute("editVideo", request.getSession().getAttribute("editVideo"));
        request.setAttribute("errors", request.getSession().getAttribute("errors"));
        request.setAttribute("editErrors", request.getSession().getAttribute("editErrors"));
        request.setAttribute("openAddQuizModal", request.getSession().getAttribute("openAddQuizModal"));
        request.setAttribute("openEditQuizModal", request.getSession().getAttribute("openEditQuizModal"));
        request.setAttribute("openAddMaterialModal", request.getSession().getAttribute("openAddMaterialModal"));
        request.setAttribute("openAddMaterialModal", request.getSession().getAttribute("openEditMaterialModal"));
        request.setAttribute("openAddVideoModal", request.getSession().getAttribute("openAddVideoModal"));
        request.setAttribute("openAddVideoModal", request.getSession().getAttribute("openEditVideoModal"));
        request.getRequestDispatcher("/WEB-INF/views/instructor/manage-courses/lessonitem-list.jsp").forward(request, response);
        // Xoá để reload không còn nữa!
        request.getSession().removeAttribute("quiz");
        request.getSession().removeAttribute("editQuiz");
        request.getSession().removeAttribute("material");
        request.getSession().removeAttribute("editMaterial");
        request.getSession().removeAttribute("video");
        request.getSession().removeAttribute("editVideo");
        request.getSession().removeAttribute("errors");
        request.getSession().removeAttribute("editErrors");
        request.getSession().removeAttribute("openAddQuizModal");
        request.getSession().removeAttribute("openEditQuizModal");
        request.getSession().removeAttribute("openAddMaterialModal");
        request.getSession().removeAttribute("openEditMaterialModal");
        request.getSession().removeAttribute("openAddVideoModal");
        request.getSession().removeAttribute("openEditVideoModal");
    }

    private void createVideo(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {

        Map<String, String> errors = new HashMap<>();
        String lessonId = request.getParameter("lessonID");
        String courseId = request.getParameter("courseID");
        String titleVideo = request.getParameter("titleVideo");
        String descriptionVideo = request.getParameter("descriptionVideo");
        String durationStr = request.getParameter("duration");
        Part videoUrl = request.getPart("videoUrl");
        if (!Validator.isValidInteger(lessonId) || !Validator.isValidInteger(courseId)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        int lessonIdValid = Integer.parseInt(lessonId);
        int courseIdValid = Integer.parseInt(courseId);
        if (Validator.isNullOrEmpty(titleVideo)) {
            errors.put("titleVideo", "Title video is required.");
        } else if (!Validator.isValidText(titleVideo, 150)) {
            errors.put("titleVideo", "Maximun length is 150 characters.");
        } else {
            titleVideo = titleVideo.trim();
            if (videoDAO.isVideoTitleExists(titleVideo, lessonIdValid, null)) {
                errors.put("titleVideo", "This video title is already taken.");
            }
        }

        if (Validator.isNullOrEmpty(descriptionVideo)) {
            errors.put("descriptionVideo", "Description video is required.");
        } else if (!Validator.isValidText(descriptionVideo, 250)) {
            errors.put("descriptionVideo", "Maximun length is 250 characters.");
        } else {
            descriptionVideo = descriptionVideo.trim();
        }
        int duration = 1;
        if (!Validator.isValidInteger(durationStr)) {
            errors.put("duration", "Duration must be a valid number.");
        } else {
            duration = Integer.parseInt(durationStr);
            if (duration <= 0) {
                errors.put("duration", "Duration must be a valid number.");
            }
        }

        String videoURL = FileUploadUtil.handleUpload(
                videoUrl,
                request.getServletContext().getRealPath("/assets/videos"),
                "/assets/videos",
                200 * 1024 * 1024, // 200MB
                new String[]{".mp4", ".avi", ".mov"},
                errors,
                "videoUrl",
                true);

        if (!errors.isEmpty()) {
            Video video = new Video();
            video.setTitle(titleVideo);
            video.setDescription(descriptionVideo);
            video.setDuration(duration);
            request.getSession().setAttribute("errors", errors);
            request.getSession().setAttribute("video", video);
            request.getSession().setAttribute("openAddVideoModal", true);
            response.sendRedirect(request.getContextPath() + "/instructor/courses/lessons/view/" + lessonIdValid + "?courseID=" + courseIdValid);
            return;
        }
        int durationValid = duration;
        Video video = new Video();
        video.setTitle(titleVideo);
        video.setDescription(descriptionVideo);
        video.setDuration(durationValid);
        video.setVideoUrl(videoURL);

        int created = lessonDAO.addVideoToLesson(lessonIdValid, video);
        if (created > 0) {
            request.getSession().setAttribute("message", "Video created successfully.");
        } else {
            request.getSession().setAttribute("error", "Failed to create video. Please try again or contact admin.");
        }
        response.sendRedirect(request.getContextPath() + "/instructor/courses/lessons/view/" + lessonIdValid + "?courseID=" + courseIdValid);
    }

    private void editVideo(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {

        Map<String, String> errors = new HashMap<>();
        String lessonID = request.getParameter("lessonID");
        String videoID = request.getParameter("videoID");
        String courseId = request.getParameter("courseID");
        String titleVideo = request.getParameter("titleVideo");
        String descriptionVideo = request.getParameter("descriptionVideo");
        String durationStr = request.getParameter("duration");
        Part videoUrl = request.getPart("videoUrl");
        if (!Validator.isValidInteger(lessonID) || !Validator.isValidInteger(videoID) || !Validator.isValidInteger(courseId)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        int lessonIdValid = Integer.parseInt(lessonID);
        int videoIdValid = Integer.parseInt(videoID);
        int courseIdValid = Integer.parseInt(courseId);
        if (Validator.isNullOrEmpty(titleVideo)) {
            errors.put("titleVideo", "Title video is required.");
        } else if (!Validator.isValidText(titleVideo, 150)) {
            errors.put("titleVideo", "Maximun length is 150 characters.");
        } else {
            titleVideo = titleVideo.trim();
            if (videoDAO.isVideoTitleExists(titleVideo, lessonIdValid, videoIdValid)) {
                errors.put("titleVideo", "This video title is already taken.");
            }
        }

        if (Validator.isNullOrEmpty(descriptionVideo)) {
            errors.put("descriptionVideo", "Description video is required.");
        } else if (!Validator.isValidText(descriptionVideo, 250)) {
            errors.put("descriptionVideo", "Maximun length is 250 characters.");
        } else {
            descriptionVideo = descriptionVideo.trim();
        }
        int duration = 1;
        if (!Validator.isValidInteger(durationStr)) {
            errors.put("duration", "Duration must be a valid number.");
        } else {
            duration = Integer.parseInt(durationStr);
            if (duration <= 0) {
                errors.put("duration", "Duration must be a valid number.");
            }
        }

        String videoURL = FileUploadUtil.handleUpload(
                videoUrl,
                request.getServletContext().getRealPath("/assets/videos"),
                "/assets/videos",
                200 * 1024 * 1024, // 200MB
                new String[]{".mp4", ".avi", ".mov"},
                errors,
                "videoUrl",
                false);

        if (!errors.isEmpty()) {
            Video video = new Video();
            video.setVideoID(videoIdValid);
            video.setLessonID(lessonIdValid);
            video.setTitle(titleVideo);
            video.setDescription(descriptionVideo);
            video.setDuration(duration);
            request.getSession().setAttribute("editErrors", errors);
            request.getSession().setAttribute("editVideo", video);
            request.getSession().setAttribute("openEditVideoModal", true);
            response.sendRedirect(request.getContextPath() + "/instructor/courses/lessons/view/" + lessonIdValid + "?courseID=" + courseIdValid);
            return;
        }
        int durationValid = duration;
        Video video = new Video();
        video.setLessonID(lessonIdValid);
        video.setVideoID(videoIdValid);
        video.setTitle(titleVideo);
        video.setDescription(descriptionVideo);
        video.setDuration(durationValid);
        video.setVideoUrl(videoURL);
        boolean updated = videoDAO.updateVideoItem(videoIdValid, video);
        if (updated) {
            request.getSession().setAttribute("message", "Video updated successfully.");
        } else {
            request.getSession().setAttribute("error", "Failed to update video. Please try again or contact admin.");
        }
        response.sendRedirect(request.getContextPath() + "/instructor/courses/lessons/view/" + lessonIdValid + "?courseID=" + courseIdValid);
    }

    private void deleteVideo(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {

        String lessonID = request.getParameter("lessonID");
        String videoID = request.getParameter("videoID");
        String courseId = request.getParameter("courseID");
        if (!Validator.isValidInteger(videoID) || !Validator.isValidInteger(lessonID) || !Validator.isValidInteger(courseId)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        int lessonIdValid = Integer.parseInt(lessonID);
        int videoIdValid = Integer.parseInt(videoID);
        int courseIdValid = Integer.parseInt(courseId);
        boolean isOwner = lessonDAO.isInstructorOwnerOfLesson(instructor.getInstructorID(), lessonIdValid);
        if (!isOwner) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        boolean deleted = videoDAO.deleteVideoItem(videoIdValid);
        if (deleted) {
            request.getSession().setAttribute("message", "Video deleted successfully.");
        } else {
            request.getSession().setAttribute("error", "Failed to delete video. Please try again or contact admin.");
        }
        response.sendRedirect(request.getContextPath() + "/instructor/courses/lessons/view/" + lessonIdValid + "?courseID=" + courseIdValid);
    }

    private void createMaterial(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {

        Map<String, String> errors = new HashMap<>();
        String lessonId = request.getParameter("lessonID");
        String courseId = request.getParameter("courseID");
        String titleMaterial = request.getParameter("titleMaterial");
        String descriptionMaterial = request.getParameter("descriptionMaterial");
        String contentMaterial = request.getParameter("contentMaterial");
        Part materialUrl = request.getPart("materialUrl");
        if (!Validator.isValidInteger(lessonId) || !Validator.isValidInteger(courseId)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        int lessonIdValid = Integer.parseInt(lessonId);
        int courseIdValid = Integer.parseInt(courseId);
        if (Validator.isNullOrEmpty(titleMaterial)) {
            errors.put("titleMaterial", "Title material is required.");
        } else if (!Validator.isValidText(titleMaterial, 150)) {
            errors.put("titleMaterial", "Maximun length is 150 characters.");
        } else {
            titleMaterial = titleMaterial.trim();
            if (materialDAO.isMaterialTitleExists(titleMaterial, lessonIdValid, null)) {
                errors.put("titleMaterial", "This material title is already taken.");
            }
        }
        if (Validator.isNullOrEmpty(descriptionMaterial)) {
            errors.put("descriptionMaterial", "Description material is required.");
        } else if (!Validator.isValidText(descriptionMaterial, 250)) {
            errors.put("descriptionMaterial", "Maximun length is 250 characters.");
        } else {
            descriptionMaterial = descriptionMaterial.trim();
        }
        if (Validator.isNullOrEmpty(contentMaterial)) {
            errors.put("contentMaterial", "Content material is required.");
        } else if (!Validator.isValidText(contentMaterial, 250)) {
            errors.put("contentMaterial", "Maximun length is 250 characters.");
        } else {
            contentMaterial = contentMaterial.trim();
        }
        String materialURL = FileUploadUtil.handleUpload(
                materialUrl,
                request.getServletContext().getRealPath("/assets/materials"),
                "assets/materials",
                50 * 1024 * 1024, // 50MB
                new String[]{".pdf", ".doc", ".docx", ".ppt", ".pptx"},
                errors,
                "materialUrl",
                true);
        if (!errors.isEmpty()) {
            Material material = new Material();
            material.setTitle(titleMaterial);
            material.setDescription(descriptionMaterial);
            material.setContent(contentMaterial);
            request.getSession().setAttribute("errors", errors);
            request.getSession().setAttribute("material", material);
            request.getSession().setAttribute("openAddMaterialModal", true);
            response.sendRedirect(request.getContextPath() + "/instructor/courses/lessons/view/" + lessonIdValid + "?courseID=" + courseIdValid);
            return;
        }
        Material material = new Material();
        material.setTitle(titleMaterial);
        material.setDescription(descriptionMaterial);
        material.setFileUrl(materialURL);
        material.setContent(contentMaterial);

        int created = lessonDAO.addMaterialToLesson(lessonIdValid, material);
        if (created > 0) {
            request.getSession().setAttribute("message", "Material created successfully.");
        } else {
            request.getSession().setAttribute("error", "Failed to create material. Please try again or contact admin.");
        }
        response.sendRedirect(request.getContextPath() + "/instructor/courses/lessons/view/" + lessonIdValid + "?courseID=" + courseIdValid);
    }

    private void editMaterial(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {

        Map<String, String> errors = new HashMap<>();
        String lessonId = request.getParameter("lessonID");
        String materialID = request.getParameter("materialID");
        String courseId = request.getParameter("courseID");
        String titleMaterial = request.getParameter("titleMaterial");
        String descriptionMaterial = request.getParameter("descriptionMaterial");
        String contentMaterial = request.getParameter("contentMaterial");
        Part materialUrl = request.getPart("materialUrl");
        if (!Validator.isValidInteger(lessonId) || !Validator.isValidInteger(materialID) || !Validator.isValidInteger(courseId)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        int lessonIdValid = Integer.parseInt(lessonId);
        int materialIdValid = Integer.parseInt(materialID);
        int courseIdValid = Integer.parseInt(courseId);
        if (Validator.isNullOrEmpty(titleMaterial)) {
            errors.put("titleMaterial", "Title material is required.");
        } else if (!Validator.isValidText(titleMaterial, 150)) {
            errors.put("titleMaterial", "Maximun length is 150 characters.");
        } else {
            titleMaterial = titleMaterial.trim();
            if (materialDAO.isMaterialTitleExists(titleMaterial, lessonIdValid, materialIdValid)) {
                errors.put("titleMaterial", "This material title is already taken.");
            }
        }
        if (Validator.isNullOrEmpty(descriptionMaterial)) {
            errors.put("descriptionMaterial", "Description material is required.");
        } else if (!Validator.isValidText(descriptionMaterial, 250)) {
            errors.put("descriptionMaterial", "Maximun length is 250 characters.");
        } else {
            descriptionMaterial = descriptionMaterial.trim();
        }
        if (Validator.isNullOrEmpty(contentMaterial)) {
            errors.put("contentMaterial", "Content material is required.");
        } else if (!Validator.isValidText(contentMaterial, 250)) {
            errors.put("contentMaterial", "Maximun length is 250 characters.");
        } else {
            contentMaterial = contentMaterial.trim();
        }
        String materialURL = FileUploadUtil.handleUpload(
                materialUrl,
                request.getServletContext().getRealPath("/assets/materials"),
                "assets/materials",
                50 * 1024 * 1024, // 50MB
                new String[]{".pdf", ".doc", ".docx", ".ppt", ".pptx"},
                errors,
                "materialUrl",
                false);
        if (!errors.isEmpty()) {
            Material material = new Material();
            material.setMaterialID(materialIdValid);
            material.setLessonID(lessonIdValid);
            material.setTitle(titleMaterial);
            material.setDescription(descriptionMaterial);
            material.setContent(contentMaterial);
            request.getSession().setAttribute("editErrors", errors);
            request.getSession().setAttribute("editMaterial", material);
            request.getSession().setAttribute("openEditMaterialModal", true);
            response.sendRedirect(request.getContextPath() + "/instructor/courses/lessons/view/" + lessonIdValid + "?courseID=" + courseIdValid);
            return;
        }
        Material material = new Material();
        material.setTitle(titleMaterial);
        material.setDescription(descriptionMaterial);
        material.setFileUrl(materialURL);
        material.setContent(contentMaterial);
        boolean updated = materialDAO.updateMaterialItem(materialIdValid, material);
        if (updated) {
            request.getSession().setAttribute("message", "Material updated successfully.");
        } else {
            request.getSession().setAttribute("error", "Failed to update material. Please try again or contact admin.");
        }
        response.sendRedirect(request.getContextPath() + "/instructor/courses/lessons/view/" + lessonIdValid + "?courseID=" + courseIdValid);

    }

    private void deleteMaterial(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {

        String lessonID = request.getParameter("lessonID");
        String materialID = request.getParameter("materialID");
        String courseId = request.getParameter("courseID");
        if (!Validator.isValidInteger(materialID) || !Validator.isValidInteger(lessonID) || !Validator.isValidInteger(courseId)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        int lessonIdValid = Integer.parseInt(lessonID);
        int materialIdValid = Integer.parseInt(materialID);
        int courseIdValid = Integer.parseInt(courseId);
        boolean isOwner = lessonDAO.isInstructorOwnerOfLesson(instructor.getInstructorID(), lessonIdValid);
        if (!isOwner) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        boolean deleted = materialDAO.deleteMaterialItem(materialIdValid);
        if (deleted) {
            request.getSession().setAttribute("message", "Material deleted successfully.");
        } else {
            request.getSession().setAttribute("error", "Failed to delete material. Please try again or contact admin.");
        }
        response.sendRedirect(request.getContextPath() + "/instructor/courses/lessons/view/" + lessonIdValid + "?courseID=" + courseIdValid);
    }

    private void createQuiz(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {

        Map<String, String> errors = new HashMap<>();
        String lessonId = request.getParameter("lessonID");
        String courseId = request.getParameter("courseID");
        String titleQuiz = request.getParameter("titleQuiz");
        String descriptionQuiz = request.getParameter("descriptionQuiz");
        String timeLimitStr = request.getParameter("timeLimit");
        String passingScoreStr = request.getParameter("passingScore");
        if (!Validator.isValidInteger(lessonId) || !Validator.isValidInteger(courseId)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        int lessonIdValid = Integer.parseInt(lessonId);
        int courseIdValid = Integer.parseInt(courseId);
        if (Validator.isNullOrEmpty(titleQuiz)) {
            errors.put("titleQuiz", "Title quiz is required.");
        } else if (!Validator.isValidText(titleQuiz, 150)) {
            errors.put("titleQuiz", "Maximun length is 150 characters.");
        } else {
            titleQuiz = titleQuiz.trim();
            if (quizDAO.isQuizTitleExists(titleQuiz, lessonIdValid, null)) {
                errors.put("titleQuiz", "This quiz title is already taken.");
            }
        }
        if (Validator.isNullOrEmpty(descriptionQuiz)) {
            errors.put("descriptionQuiz", "Description quiz is required.");
        } else if (!Validator.isValidText(descriptionQuiz, 250)) {
            errors.put("descriptionQuiz", "Maximun length is 250 characters.");
        } else {
            descriptionQuiz = descriptionQuiz.trim();
        }
        Integer timeLimit = null;
        if (!Validator.isNullOrEmpty(timeLimitStr)) {
            if (!Validator.isValidInteger(timeLimitStr)) {
                errors.put("timeLimit", "Time limit must be a valid number.");
            } else {
                try {
                    int passed = Integer.parseInt(timeLimitStr);
                    if (passed < 0) {
                        errors.put("timeLimit", "Time limit must be a number greater than or equal to 0.");
                    } else {
                        if (passed != 0) {
                            timeLimit = passed;
                        }
                    }
                } catch (NumberFormatException ex) {
                    errors.put("timeLimit", "Time limit must be a valid number.");
                }
            }
        }
        int passingScore = 70;
        if (!Validator.isValidInteger(passingScoreStr)) {
            errors.put("passingScore", "Passing must be a valid number.");
        } else {
            try {
                int passed = Integer.parseInt(passingScoreStr);
                if (passed <= 0 || passed > 100) {
                    errors.put("passingScore", "Passing must be between 1 and 100.");
                } else {
                    passingScore = passed;
                }
            } catch (NumberFormatException ex) {
                errors.put("passingScore", "Pasing must be a valid number.");
            }
        }

        if (!errors.isEmpty()) {
            Quiz quiz = new Quiz();
            quiz.setTitle(titleQuiz);
            quiz.setDescription(descriptionQuiz);
            quiz.setTimeLimit(timeLimit != null ? timeLimit : 0);
            quiz.setPassingScore(passingScore);
            request.getSession().setAttribute("errors", errors);
            request.getSession().setAttribute("quiz", quiz);
            request.getSession().setAttribute("openAddQuizModal", true);
            response.sendRedirect(request.getContextPath() + "/instructor/courses/lessons/view/" + lessonIdValid + "?courseID=" + courseIdValid);
            return;
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(titleQuiz);
        quiz.setDescription(descriptionQuiz);
        quiz.setTimeLimit(timeLimit);
        quiz.setPassingScore(passingScore);
        int created = lessonDAO.addQuizToLesson(lessonIdValid, quiz);
        if (created > 0) {
            request.getSession().setAttribute("message", "Quiz created successfully.");
        } else {
            request.getSession().setAttribute("error", "Failed to create quiz. Please try again or contact admin.");
        }
        response.sendRedirect(request.getContextPath() + "/instructor/courses/lessons/view/" + lessonIdValid + "?courseID=" + courseIdValid);

    }

    private void editQuiz(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {

        Map<String, String> errors = new HashMap<>();
        String lessonID = request.getParameter("lessonID");
        String quizID = request.getParameter("quizID");
        String courseId = request.getParameter("courseID");
        String titleQuiz = request.getParameter("titleQuiz");
        String descriptionQuiz = request.getParameter("descriptionQuiz");
        String timeLimitStr = request.getParameter("timeLimit");
        String passingScoreStr = request.getParameter("passingScore");
        if (!Validator.isValidInteger(lessonID) || !Validator.isValidInteger(quizID) || !Validator.isValidInteger(courseId)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        int lessonIdValid = Integer.parseInt(lessonID);
        int quizIdValid = Integer.parseInt(quizID);
        int courseIdValid = Integer.parseInt(courseId);
        if (Validator.isNullOrEmpty(titleQuiz)) {
            errors.put("titleQuiz", "Title quiz is required.");
        } else if (!Validator.isValidText(titleQuiz, 150)) {
            errors.put("titleQuiz", "Maximun length is 150 characters.");
        } else {
            titleQuiz = titleQuiz.trim();
            if (quizDAO.isQuizTitleExists(titleQuiz, lessonIdValid, quizIdValid)) {
                errors.put("titleQuiz", "This quiz title is already taken.");
            }
        }
        if (Validator.isNullOrEmpty(descriptionQuiz)) {
            errors.put("descriptionQuiz", "Description quiz is required.");
        } else if (!Validator.isValidText(descriptionQuiz, 250)) {
            errors.put("descriptionQuiz", "Maximun length is 250 characters.");
        } else {
            descriptionQuiz = descriptionQuiz.trim();
        }

        Integer timeLimit = null;
        if (!Validator.isNullOrEmpty(timeLimitStr)) {
            if (!Validator.isValidInteger(timeLimitStr)) {
                errors.put("timeLimit", "Time limit must be a valid number.");
            } else {
                try {
                    int passed = Integer.parseInt(timeLimitStr);
                    if (passed < 0) {
                        errors.put("timeLimit", "Time limit must be a number greater than or equal to 0.");
                    } else {
                        if (passed != 0) {
                            timeLimit = passed;
                        }
                    }
                } catch (NumberFormatException ex) {
                    errors.put("timeLimit", "Time limit must be a valid number.");
                }
            }
        }
        int passingScore = 70;
        if (!Validator.isValidInteger(passingScoreStr)) {
            errors.put("passingScore", "Passing must be a valid number.");
        } else {
            try {
                int passed = Integer.parseInt(passingScoreStr);
                if (passed <= 0 || passed > 100) {
                    errors.put("passingScore", "Passing must be between 1 and 100.");
                } else {
                    passingScore = passed;
                }
            } catch (NumberFormatException ex) {
                errors.put("passingScore", "Pasing must be a valid number.");
            }
        }
        if (!errors.isEmpty()) {
            Quiz quiz = new Quiz();
            quiz.setQuizID(quizIdValid);
            quiz.setLessonID(lessonIdValid);
            quiz.setTitle(titleQuiz);
            quiz.setDescription(descriptionQuiz);
            quiz.setTimeLimit(timeLimit != null ? timeLimit : 0);
            quiz.setPassingScore(passingScore);
            request.getSession().setAttribute("editErrors", errors);
            request.getSession().setAttribute("editQuiz", quiz);
            request.getSession().setAttribute("openEditQuizModal", true);
            response.sendRedirect(request.getContextPath() + "/instructor/courses/lessons/view/" + lessonIdValid + "?courseID=" + courseIdValid);
            return;
        }
        Quiz quiz = new Quiz();
        quiz.setTitle(titleQuiz);
        quiz.setDescription(descriptionQuiz);
        quiz.setTimeLimit(timeLimit);
        quiz.setPassingScore(passingScore);
        boolean updated = quizDAO.updateQuizItem(quizIdValid, quiz);
        if (updated) {
            request.getSession().setAttribute("message", "Quiz updated successfully.");
        } else {
            request.getSession().setAttribute("error", "Failed to update quiz. Please try again or contact admin.");
        }
        response.sendRedirect(request.getContextPath() + "/instructor/courses/lessons/view/" + lessonIdValid + "?courseID=" + courseIdValid);

    }

    private void deleteQuiz(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {

        String lessonID = request.getParameter("lessonID");
        String quizID = request.getParameter("quizID");
        String courseId = request.getParameter("courseID");
        if (!Validator.isValidInteger(quizID) || !Validator.isValidInteger(lessonID) || !Validator.isValidInteger(courseId)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        int lessonIdValid = Integer.parseInt(lessonID);
        int quizIdValid = Integer.parseInt(quizID);
        int courseIdValid = Integer.parseInt(courseId);
        boolean isOwner = lessonDAO.isInstructorOwnerOfLesson(instructor.getInstructorID(), lessonIdValid);
        if (!isOwner) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        boolean deleted = quizDAO.deleteQuizItem(quizIdValid);
        if (deleted) {
            request.getSession().setAttribute("message", "Quiz deleted successfully.");
        } else {
            request.getSession().setAttribute("error", "Failed to delete quiz. Please try again or contact admin.");
        }
        response.sendRedirect(request.getContextPath() + "/instructor/courses/lessons/view/" + lessonIdValid + "?courseID=" + courseIdValid);
    }

    private void listQuestion(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {

        String partInfo = request.getPathInfo();
        String courseId = request.getParameter("courseID");
        String lessonId = request.getParameter("lessonID");
        String[] pathParts = partInfo.split("/");
        if (pathParts.length < 2 || !Validator.isValidInteger(pathParts[1]) || !Validator.isValidInteger(courseId) || !Validator.isValidInteger(lessonId)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        int quizIdValid = Integer.parseInt(pathParts[1]);
        int courseIdValid = Integer.parseInt(courseId);
        int lessonIdValid = Integer.parseInt(lessonId);
        List<Question> questions = quizDAO.getQuestionsByQuizId(quizIdValid);
        request.setAttribute("quizID", quizIdValid);
        request.setAttribute("questions", questions);
        request.setAttribute("courseID", courseIdValid);
        request.setAttribute("lessonID", lessonIdValid);

        // load session
        request.setAttribute("questionErrors", request.getSession().getAttribute("questionErrors"));
        request.setAttribute("questionFormValues", request.getSession().getAttribute("questionFormValues"));
        request.setAttribute("openAddQuestionModal", request.getSession().getAttribute("openAddQuestionModal"));
        request.getRequestDispatcher("/WEB-INF/views/instructor/manage-courses/question-list.jsp").forward(request, response);
        // remove session
        request.getSession().removeAttribute("questionErrors");
        request.getSession().removeAttribute("questionFormValues");
        request.getSession().removeAttribute("openAddQuestionModal");

    }

    private void createQuestion(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {

        Map<String, String> errors = new HashMap<>();
        int totalAnswers = 4;

        String quizID = request.getParameter("quizID");
        String lessonID = request.getParameter("lessonID");
        String courseID = request.getParameter("courseID");
        if (!Validator.isValidInteger(quizID) || !Validator.isValidInteger(lessonID) || !Validator.isValidInteger(courseID)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        int quizIdValid = Integer.parseInt(quizID);
        int lessonIdValid = Integer.parseInt(lessonID);
        int courseIdValid = Integer.parseInt(courseID);

        // --- Lấy dữ liệu từ form ---
        String questionContent = request.getParameter("questionContent");
        String orderIndexStr = request.getParameter("orderIndex");
        String pointsStr = request.getParameter("points");
        String correctAnswerStr = request.getParameter("correctAnswer");

        // --- Validate câu hỏi ---
        if (Validator.isNullOrEmpty(questionContent)) {
            errors.put("question", "Question content is required.");
        } else if (!Validator.isValidText(questionContent, 150)) {
            errors.put("question", "Maximum length is 150 characters.");
        } else {
            questionContent = questionContent.trim();
            if (quizDAO.isQuestionContentExists(questionContent, quizIdValid, null)) {
                errors.put("question", "This question content is already taken.");
            }
        }

        // --- Validate điểm ---
        int points = 1;
        if (!Validator.isValidInteger(pointsStr)) {
            errors.put("points", "Question point must be a valid number.");
        } else {
            int parsed = Integer.parseInt(pointsStr);
            if (parsed > 0) {
                points = parsed;
            } else {
                errors.put("points", "Question point must be a number geater than 0.");
            }
        }

        // --- Validate thứ tự câu hỏi ---
        int orderIndex = 1;
        if (!Validator.isValidInteger(orderIndexStr)) {
            errors.put("orderIndex", "Question index must be a valid number.");
        } else {
            int parsed = Integer.parseInt(orderIndexStr);
            if (parsed <= 0) {
                errors.put("orderIndex", "Question index must be a positive number.");
            } else if (quizDAO.isDuplicateQuestionOrderIndex(quizIdValid, parsed)) {
                errors.put("orderIndex", "Question order is duplicated.");
            } else {
                orderIndex = parsed;
            }
        }

        // --- Validate đáp án đúng ---
        int correctAnswer = -1;
        if (Validator.isNullOrEmpty(correctAnswerStr)) {
            errors.put("correctAnswer", "Please select the correct answer.");
        } else if (Validator.isValidInteger(correctAnswerStr)) {
            correctAnswer = Integer.parseInt(correctAnswerStr);
            if (correctAnswer < 1 || correctAnswer > totalAnswers) {
                errors.put("correctAnswer", "Correct answer is out of valid range.");
            }
        } else {
            errors.put("correctAnswer", "Correct answer must be a valid number.");
        }
        Set<String> duplicateAnswer = new HashSet<>();
        // --- Validate nội dung từng đáp án ---
        List<Answer> listAnswer = new ArrayList<>();
        for (int i = 1; i <= totalAnswers; i++) {
            String content
                    = request.getParameter("answerContent" + i) != null
                    ? request.getParameter("answerContent" + i).trim()
                    : request.getParameter("answerContent" + i);
            Answer a = new Answer();
            a.setContent(content);
            a.setOrderIndex(i);
            a.setCorrect(i == correctAnswer);
            listAnswer.add(a);

            if (Validator.isNullOrEmpty(content)) {
                errors.put("answer" + i, "Answer #" + i + " is required.");
            } else if (!Validator.isValidText(content, 50)) {
                errors.put("answer" + i, "Answer #" + i + " Maximum length is 50 characters.");
            } else {
                content = content.trim();
                String toLowCaseAnswerContent = content.toLowerCase();
                if (duplicateAnswer.contains(toLowCaseAnswerContent)) {
                    String indexDupAns = "";
                    for (Answer answer : listAnswer) {
                        if (answer.getContent().equalsIgnoreCase(toLowCaseAnswerContent) && !(answer.getOrderIndex() == i)) {
                            indexDupAns += "," + answer.getOrderIndex();
                        }
                    }
                    if (!Validator.isNullOrEmpty(indexDupAns)) {
                        indexDupAns = indexDupAns.substring(1);
                    }
                    errors.put("answer" + i, "Answer #" + i + " is duplicated with a previous answer (" + indexDupAns + ").");
                } else {
                    duplicateAnswer.add(toLowCaseAnswerContent);
                }
            }
        }

        // --- Nếu có lỗi, quay lại form và giữ dữ liệu ---
        if (!errors.isEmpty()) {
            Map<String, String> formValues = new HashMap<>();
            formValues.put("questionContent", questionContent);
            formValues.put("orderIndex", orderIndexStr);
            formValues.put("points", pointsStr);
            formValues.put("correctAnswer", correctAnswerStr);
            for (int i = 1; i <= totalAnswers; i++) {
                formValues.put("answer" + i,
                        request.getParameter("answerContent" + i) != null
                        ? request.getParameter("answerContent" + i).trim()
                        : request.getParameter("answerContent" + i));
            }

            request.getSession().setAttribute("questionErrors", errors);
            request.getSession().setAttribute("questionFormValues", formValues);
            request.getSession().setAttribute("openAddQuestionModal", true);
            response.sendRedirect(request.getContextPath() + "/instructor/lessons/quizzes/view/" + quizIdValid + "?courseID=" + courseIdValid + "&lessonID=" + lessonIdValid);
            return;
        }

        // --- Nếu hợp lệ, tạo mới câu hỏi và lưu ---
        Question question = new Question();
        question.setContent(questionContent);
        question.setType("multiple_choice");
        question.setPoints(points);
        question.setOrderIndex(orderIndex);
        question.setAnswers(listAnswer);

        int created = quizDAO.insertQuestionWithAnswers(question, quizIdValid, listAnswer);
        if (created > 0) {
            request.getSession().setAttribute("message", "Question created successfully.");
        } else {
            request.getSession().setAttribute("error", "Failed to create question. Please try again or contact admin.");
        }
        response.sendRedirect(request.getContextPath() + "/instructor/lessons/quizzes/view/" + quizIdValid + "?courseID=" + courseIdValid + "&lessonID=" + lessonIdValid);

    }

    private void showFormEditQuestion(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {

        String partInfo = request.getPathInfo();
        String lessonID = request.getParameter("lessonID");
        String courseID = request.getParameter("courseID");
        String[] pathParts = partInfo.split("/");
        if (pathParts.length < 2 || !Validator.isValidInteger(pathParts[1]) || !Validator.isValidInteger(lessonID) || !Validator.isValidInteger(courseID)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        int questionIdValid = Integer.parseInt(pathParts[1]);
        int lessonIdValid = Integer.parseInt(lessonID);
        int courseIdValid = Integer.parseInt(courseID);
        request.setAttribute("lessonID", lessonIdValid);
        request.setAttribute("courseID", courseIdValid);
        Question question = quizDAO.getQuestionAndAnswersById(questionIdValid);
        request.setAttribute("question", question);
        request.getRequestDispatcher("/WEB-INF/views/instructor/manage-courses/edit-question.jsp").forward(request, response);
    }

    private void editQuestion(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {

        Map<String, String> errors = new HashMap<>();
        int totalAnswers = 4;

        // Lấy và kiểm tra quizID & questionID
        String quizID = request.getParameter("quizID");
        String questionID = request.getParameter("questionID");
        String lessonID = request.getParameter("lessonID");
        String courseID = request.getParameter("courseID");
        if (!Validator.isValidInteger(quizID) || !Validator.isValidInteger(questionID) || !Validator.isValidInteger(lessonID) || !Validator.isValidInteger(courseID)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        int quizIdValid = Integer.parseInt(quizID);
        int questionIdValid = Integer.parseInt(questionID);
        int lessonIdValid = Integer.parseInt(lessonID);
        int courseIdValid = Integer.parseInt(courseID);

        // Lấy các tham số khác
        String questionContent = request.getParameter("questionContent");
        String pointsStr = request.getParameter("points");
        String orderIndexStr = request.getParameter("orderIndex");
        String correctAnswerStr = request.getParameter("correctAnswer");

        // Validate nội dung câu hỏi
        if (Validator.isNullOrEmpty(questionContent)) {
            errors.put("question", "Question content is required.");
        } else if (!Validator.isValidText(questionContent, 150)) {
            errors.put("question", "Maximum length is 150 characters.");
        } else {
            questionContent = questionContent.trim();
            if (quizDAO.isQuestionContentExists(questionContent, quizIdValid, questionIdValid)) {
                errors.put("question", "This question content is already taken.");
            }
        }

        // Validate điểm
        int pointValid = 1;
        if (Validator.isValidInteger(pointsStr)) {
            int parsed = Integer.parseInt(pointsStr);
            if (parsed > 0) {
                pointValid = parsed;
            } else {
                errors.put("points", "Question point must be a number geater than 0.");
            }
        } else {
            errors.put("points", "Question point must be a valid number.");
        }

        // Validate vị trí câu hỏi
        int orderIndexValid = 1;
        if (Validator.isValidInteger(orderIndexStr)) {
            int parsed = Integer.parseInt(orderIndexStr);
            if (parsed > 0) {
                List<Integer> usedIndexes = quizDAO.getQuestionOrderIndexes(quizIdValid, questionIdValid);
                if (usedIndexes.contains(parsed)) {
                    errors.put("orderIndex", "Question index is duplicated.");
                } else {
                    orderIndexValid = parsed;
                }
            } else {
                errors.put("orderIndex", "Question index must be positive.");
            }
        } else {
            errors.put("orderIndex", "Question index must be a valid number.");
        }

        // Validate đáp án đúng
        int correctAnswerValid = -1;
        if (Validator.isNullOrEmpty(correctAnswerStr)) {
            errors.put("correctAnswer", "Please select the correct answer.");
        } else if (Validator.isValidInteger(correctAnswerStr)) {
            correctAnswerValid = Integer.parseInt(correctAnswerStr);
            if (correctAnswerValid < 1 || correctAnswerValid > totalAnswers) {
                errors.put("correctAnswer", "Correct answer is out of valid range.");
            }
        } else {
            errors.put("correctAnswer", "Invalid correct answer.");
        }
        Set<String> duplicateAnswer = new HashSet<>();
        // Validate nội dung các đáp án
        List<Answer> listAnswer = new ArrayList<>();
        for (int i = 1; i <= totalAnswers; i++) {
            String content
                    = request.getParameter("answerContent" + i) != null
                    ? request.getParameter("answerContent" + i).trim()
                    : request.getParameter("answerContent" + i);
            String answerIDStr = request.getParameter("answerID" + i);
            Answer a = new Answer();

            if (Validator.isValidInteger(answerIDStr)) {
                a.setAnswerID(Integer.parseInt(answerIDStr));
            }

            a.setContent(content);
            a.setOrderIndex(i);
            a.setCorrect(i == correctAnswerValid);
            listAnswer.add(a);

            if (Validator.isNullOrEmpty(content)) {
                errors.put("answer" + i, "Answer #" + i + " is required.");
            } else if (!Validator.isValidText(content, 50)) {
                errors.put("answer" + i, "Answer #" + i + " Maximum length is 50 characters.");
            } else {
                String toLowCaseAnswerContent = content.toLowerCase();
                if (duplicateAnswer.contains(toLowCaseAnswerContent)) {
                    String indexDupAns = "";
                    for (Answer answer : listAnswer) {
                        if (answer.getContent().equalsIgnoreCase(toLowCaseAnswerContent) && !(answer.getOrderIndex() == i)) {
                            indexDupAns += "," + answer.getOrderIndex();
                        }
                    }
                    if (!Validator.isNullOrEmpty(indexDupAns)) {
                        indexDupAns = indexDupAns.substring(1);
                    }
                    errors.put("answer" + i, "Answer #" + i + " is duplicated with a previous answer (" + indexDupAns + ").");
                } else {
                    duplicateAnswer.add(toLowCaseAnswerContent);
                }
            }
        }

        // Nếu có lỗi, chuyển về trang chỉnh sửa kèm dữ liệu
        if (!errors.isEmpty()) {
            Question question = new Question();
            question.setQuizID(quizIdValid);
            question.setQuestionID(questionIdValid);
            question.setContent(questionContent);
            question.setPoints(pointValid);
            question.setOrderIndex(orderIndexValid);
            question.setAnswers(listAnswer);

            request.setAttribute("lessonID", lessonIdValid);
            request.setAttribute("courseID", courseIdValid);

            request.setAttribute("question", question);
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("/WEB-INF/views/instructor/manage-courses/edit-question.jsp").forward(request, response);
            return;
        }

        // Cập nhật nếu hợp lệ
        Question question = new Question();
        question.setQuestionID(questionIdValid);
        question.setContent(questionContent);
        question.setType("multiple_choice");
        question.setPoints(pointValid);
        question.setOrderIndex(orderIndexValid);

        boolean updated = quizDAO.updateQuestionWithAnswers(question, listAnswer);

        if (updated) {
            request.getSession().setAttribute("message", "Question updated successfully.");
        } else {
            request.getSession().setAttribute("error", "Failed to update question. Please try again or contact admin.");
        }
        response.sendRedirect(request.getContextPath() + "/instructor/lessons/quizzes/view/" + quizIdValid + "?courseID=" + courseIdValid + "&lessonID=" + lessonIdValid);

    }

    private void deleteQuestion(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {

        String questionID = request.getParameter("questionID");
        String quizID = request.getParameter("quizID");
        String lessonID = request.getParameter("lessonID");
        String courseID = request.getParameter("courseID");
        if (!Validator.isValidInteger(questionID) || !Validator.isValidInteger(quizID) || !Validator.isValidInteger(lessonID) || !Validator.isValidInteger(courseID)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        int questionIdValid = Integer.parseInt(questionID);
        int quizIdValid = Integer.parseInt(quizID);
        int lessonIdValid = Integer.parseInt(lessonID);
        int courseIdValid = Integer.parseInt(courseID);
        boolean deleted = quizDAO.deleteQuestion(questionIdValid);
        if (deleted) {
            request.getSession().setAttribute("message", "Question deleted successfully.");
        } else {
            request.getSession().setAttribute("error", "Failed to delete question. Please try again or contact admin.");
        }
        response.sendRedirect(request.getContextPath() + "/instructor/lessons/quizzes/view/" + quizIdValid + "?courseID=" + courseIdValid + "&lessonID=" + lessonIdValid);
    }

    private void viewDetailCourse(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        String[] pathParts = pathInfo.split("/");
        if (pathParts.length < 2 || !Validator.isValidInteger(pathParts[1])) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        int courseIdValid = Integer.parseInt(pathParts[1]);

        Course course = courseDAO.getCourseById(courseIdValid);
        if (course == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Get lessons
        List<Lesson> lessons = course.getLessons();

        // For each lesson, fetch its lesson items and content
        for (Lesson lesson : lessons) {
            // Get lesson items for this lesson
            List<LessonItem> lessonItems = lessonItemDAO.getLessonItemsByLessonId(lesson.getLessonID());

            // For each lesson item, fetch the actual content based on item type
            for (LessonItem item : lessonItems) {
                String itemType = item.getItemType();
                int itemId = item.getItemID();
                switch (itemType) {
                    case "video":
                        Video video = videoDAO.getVideoById(itemId);
                        item.setItem(video);
                        break;
                    case "material":
                        Material material = materialDAO.getMaterialById(itemId);
                        item.setItem(material);
                        break;
                    case "quiz":
                        Quiz quiz = quizDAO.getQuizById(itemId);
                        item.setItem(quiz);
                        break;
                    default:
                        // Unknown item type
                        break;
                }
            }

            // Set the lesson items on the lesson
            lesson.setLessonItems(lessonItems);
        }

        course.setLessons(lessons);

        request.setAttribute("course", course);
        // Forward to course details page
        request.getRequestDispatcher("/WEB-INF/views/instructor/manage-courses/course-detail.jsp").forward(request, response);

    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
