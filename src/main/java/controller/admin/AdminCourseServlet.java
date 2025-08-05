package controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import dao.CourseDAO;
import dao.CategoryDAO;
import dao.LessonDAO;
import dao.LessonItemDAO;
import dao.VideoDAO;
import dao.MaterialDAO;
import dao.QuizDAO;
import java.util.HashMap;
import java.util.Map;
import model.Course;
import model.Category;
import model.Lesson;
import model.LessonItem;
import model.Video;
import model.Material;
import model.Quiz;
import util.Validator;

/**
 * Admin course approval management controller.
 */
@WebServlet(name = "AdminCourseServlet", urlPatterns = {
    "/admin/courses",
    "/admin/courses/search",
    "/admin/courses/filter",
    "/admin/course/view/*",
    "/admin/course/approve",
    "/admin/course/reject/*",
    "/admin/course/pending",
    "/admin/course/ban",
    "/admin/course/unban"
})
public class AdminCourseServlet extends HttpServlet {

    private CourseDAO courseDAO;
    private CategoryDAO categoryDAO;
    private LessonItemDAO lessonItemDAO;
    private VideoDAO videoDAO;
    private MaterialDAO materialDAO;
    private QuizDAO quizDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        courseDAO = new CourseDAO();
        categoryDAO = new CategoryDAO();
        lessonItemDAO = new LessonItemDAO();
        videoDAO = new VideoDAO();
        materialDAO = new MaterialDAO();
        quizDAO = new QuizDAO();
    }

    /**
     * Handles the HTTP GET request - displaying course listing or details.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String servletPath = request.getServletPath();
        String pathInfo = request.getPathInfo();

        switch (servletPath) {
            case "/admin/courses/search":
                // Display course listing by search
                listCoursesBySearch(request, response);
                break;
            case "/admin/courses/filter":
                // Display course listing by filter
                listCoursesByFilter(request, response);
                break;
            case "/admin/courses":
                // Display course listing
                listCourses(request, response);
                break;
            case "/admin/course/pending":
                // Display pending courses
                listPendingCourses(request, response);
                break;
            case "/admin/course/view":
                if (pathInfo != null) {
                    // View course details
                    viewCourseDetails(request, response);
                }
                break;
            case "/admin/course/reject":
                if (pathInfo != null) {
                    // Show reject form
                    showRejectForm(request, response);
                }
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Handles the HTTP POST request - processing course rejection.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String servletPath = request.getServletPath();

        switch (servletPath) {
            case "/admin/course/reject":
                // Process course rejection
                rejectCourse(request, response);
                break;
            case "/admin/course/approve":
                approveCourse(request, response);
                break;
            case "/admin/course/ban":
                banCourse(request, response);
                break;
            case "/admin/course/unban":
                unbanCourse(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    /**
     * Display the listing of all courses for admin.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private void listCourses(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // page, pageSize
        int pageSize = 5;
        int page = 1;

        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try {
                page = Integer.parseInt(pageParam);

            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        int offSet = (page - 1) * pageSize;

        // Get all courses
        List<Course> courses = courseDAO.getAllCoursesWithLimit(offSet, pageSize, null);
        int totalCourses = courseDAO.countAllCourses(null);
        int totalPages = (int) Math.ceil((double) totalCourses / pageSize);
        request.setAttribute("courses", courses);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        // Get all categories for filtering
        List<Category> categories = categoryDAO.getAllCategories();
        request.setAttribute("categories", categories);

        // Forward to course listing page
        request.getRequestDispatcher("/WEB-INF/views/admin/manage-courses/course-list.jsp").forward(request, response);
    }

    /**
     * Display the listing of pending courses for admin to review.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private void listPendingCourses(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int pageSize = 5;
        int page = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        int offSet = (page - 1) * pageSize;
        List<Course> courses = courseDAO.getAllCoursesWithLimit(offSet, pageSize, "pending");
        int totalCourses = courseDAO.countAllCourses("pending");
        int totalPages = (int) Math.ceil((double) totalCourses / pageSize);
        request.setAttribute("isPendingView", true);
        request.setAttribute("currentPage", page);
        request.setAttribute("courses", courses);
        request.setAttribute("totalPages", totalPages);

        // Get all categories for filtering
        List<Category> categories = categoryDAO.getAllCategories();
        request.setAttribute("categories", categories);

        // Forward to course listing page with pending filter
        request.getRequestDispatcher("/WEB-INF/views/admin/manage-courses/course-list.jsp").forward(request, response);
    }

    /**
     * View detailed information about a course.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private void viewCourseDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Extract course ID from path
        String pathInfo = request.getPathInfo();
        String[] pathParts = pathInfo.split("/");

        if (pathParts.length < 2 || !Validator.isValidInteger(pathParts[1])) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        int courseId = Integer.parseInt(pathParts[1]);

        Course course = courseDAO.getCourseById(courseId);

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

        for (Lesson lesson : course.getLessons()) {
            System.out.println("lesson @");
            for (LessonItem lessonItem : lesson.getLessonItems()) {
                System.out.println(lessonItem.getItemType() + " - " + lessonItem.getItem());
            }
            System.out.println("End lesson @");
        }
        // Forward to course details page
        request.getRequestDispatcher("/WEB-INF/views/admin/manage-courses/course-detail.jsp").forward(request, response);
    }

    /**
     * Approve a course.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private void approveCourse(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String courseId = request.getParameter("courseID");

        if (!Validator.isValidInteger(courseId)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        int courseIdValid = Integer.parseInt(courseId);

        Course course = courseDAO.getCourseById(courseIdValid);

        if (course == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (!courseDAO.isCourseStatus(courseIdValid, "pending")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Update course approval status
        boolean updated = courseDAO.approveCourse(courseIdValid);

        if (updated) {
            // Redirect to pending courses list with success message
            request.getSession().setAttribute("message", "Course successfully approved.");
        } else {
            // Redirect with error message
            request.getSession().setAttribute("error", "Course approval failed. System error – contact admin.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/courses");

    }

    /**
     * Show the form for rejecting a course.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private void showRejectForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Extract course ID from path
        String pathInfo = request.getPathInfo();
        String[] pathParts = pathInfo.split("/");

        if (pathParts.length < 2 || !Validator.isValidInteger(pathParts[1])) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        int courseId = Integer.parseInt(pathParts[1]);

        Course course = courseDAO.getCourseById(courseId);

        if (course == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (!courseDAO.isCourseStatus(courseId, "pending")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        request.setAttribute("course", course);

        // Forward to rejection form
        request.getRequestDispatcher("/WEB-INF/views/admin/manage-courses/course-reject.jsp").forward(request, response);
    }

    /**
     * Process course rejection.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private void rejectCourse(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Map<String, String> errors = new HashMap<>();
        String courseId = request.getParameter("courseId");
        String rejectionReason = request.getParameter("rejectionReason");
        if (!Validator.isValidInteger(courseId)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (Validator.isNullOrEmpty(rejectionReason)) {
            errors.put("rejectionReason", "Rejection Reason is required.");
        } else if (!Validator.isValidText(rejectionReason, 500)) {
            errors.put("rejectionReason", "Maximum length is 500 characters.");
        } else {
            rejectionReason = rejectionReason.trim();
        }

        int courseIdvalid = Integer.parseInt(courseId);

        if (!errors.isEmpty()) {
            request.getSession().setAttribute("errors", errors);
            response.sendRedirect(request.getContextPath() + "/admin/course/reject/" + courseIdvalid);
            return;
        }

        Course course = courseDAO.getCourseById(courseIdvalid);

        if (course == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (!courseDAO.isCourseStatus(courseIdvalid, "pending")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        boolean updated = courseDAO.rejectCourse(courseIdvalid, rejectionReason);
        if (updated) {
            // Redirect to pending courses list with success message
            request.getSession().setAttribute("message", "Course successfully rejected.");
        } else {
            // Show error message
            request.getSession().setAttribute("error", "Course rejection failed. System error – contact admin.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/courses");
    }

    private void listCoursesBySearch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // page, pageSize
        int pageSize = 5;
        int page = 1;

        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try {
                page = Integer.parseInt(pageParam);

            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        int offSet = (page - 1) * pageSize;
        String searchParam = request.getParameter("keyword");
        String key = "";
        if (searchParam != null && !searchParam.trim().isEmpty()) {
            key = searchParam.trim();
        }

        // Get all courses
        List<Course> courses = courseDAO.searchCoursesByNameOrInstructor(key, offSet, pageSize);
        int totalCourses = courseDAO.countCoursesByKeyword(key);
        int totalPages = (int) Math.ceil((double) totalCourses / pageSize);
        request.setAttribute("searchView", true);
        request.setAttribute("keyword", key);
        request.setAttribute("courses", courses);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        // Get all categories for filtering
        List<Category> categories = categoryDAO.getAllCategories();
        request.setAttribute("categories", categories);

        // Forward to course listing page
        request.getRequestDispatcher("/WEB-INF/views/admin/manage-courses/course-list.jsp").forward(request, response);
    }

    private void listCoursesByFilter(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // page, pageSize
        int pageSize = 5;
        int page = 1;

        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try {
                page = Integer.parseInt(pageParam);

            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        int offSet = (page - 1) * pageSize;

        String keyword = request.getParameter("keyword");
        String key = "";
        if (keyword != null) {
            key = keyword;
        }
        String categoryParam = request.getParameter("category");
        Integer categoryId = (categoryParam != null && !categoryParam.isEmpty()) ? Integer.parseInt(categoryParam) : null;
        String status = request.getParameter("status");
        String sortBy = request.getParameter("sort");

        List<Course> courses = courseDAO.advancedSearchCourses(key, categoryId, status, sortBy, null, offSet, pageSize);
        int totalCourses = courseDAO.countCoursesForAdvancedSearch(key, categoryId, status);

        int totalPages = (int) Math.ceil((double) totalCourses / pageSize);

        request.setAttribute("keyword", key);
        request.setAttribute("category", categoryId);
        request.setAttribute("status", status);
        request.setAttribute("sort", sortBy);
        request.setAttribute("filterView", true);
        request.setAttribute("courses", courses);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        // Get all categories for filtering
        List<Category> categories = categoryDAO.getAllCategories();
        request.setAttribute("categories", categories);

        // Forward to course listing page
        request.getRequestDispatcher("/WEB-INF/views/admin/manage-courses/course-list.jsp").forward(request, response);
    }

    private void banCourse(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String courseId = request.getParameter("courseID");

        if (!Validator.isValidInteger(courseId)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        int courseIdValid = Integer.parseInt(courseId);
        Course course = courseDAO.getCourseById(courseIdValid);

        if (course == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (!courseDAO.isCourseStatus(courseIdValid, "approved")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        boolean updated = courseDAO.banCourse(courseIdValid);
        if (updated) {
            request.getSession().setAttribute("message", "Course has been banned successfully.");
        } else {
            request.getSession().setAttribute("error", "Failed to ban the course. System error – contact admin.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/courses");
    }

    private void unbanCourse(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String courseId = request.getParameter("courseID");

        if (!Validator.isValidInteger(courseId)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        int courseIdValid = Integer.parseInt(courseId);
        Course course = courseDAO.getCourseById(courseIdValid);

        if (course == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (!courseDAO.isCourseStatus(courseIdValid, "banned")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        boolean updated = courseDAO.unbanCourse(courseIdValid);
        if (updated) {
            request.getSession().setAttribute("message", "Course has been unbanned successfully.");
        } else {
            request.getSession().setAttribute("error", "Failed to unban the course. System error – contact admin.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/courses");
    }
}
