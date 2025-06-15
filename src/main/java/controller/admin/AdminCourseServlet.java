package controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import dao.CourseDAO;
import dao.CategoryDAO;
import dao.LessonDAO;
import dao.LessonItemDAO;
import dao.VideoDAO;
import dao.MaterialDAO;
import dao.QuizDAO;
import java.net.URLEncoder;
import javax.persistence.metamodel.SetAttribute;
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
    "/admin/course/approve/*",
    "/admin/course/reject/*",
    "/admin/course/pending",
    "/admin/course/ban/*",
    "/admin/course/unban/*"
})
public class AdminCourseServlet extends HttpServlet {

    private CourseDAO courseDAO;
    private CategoryDAO categoryDAO;
    private LessonDAO lessonDAO;
    private LessonItemDAO lessonItemDAO;
    private VideoDAO videoDAO;
    private MaterialDAO materialDAO;
    private QuizDAO quizDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        courseDAO = new CourseDAO();
        categoryDAO = new CategoryDAO();
        lessonDAO = new LessonDAO();
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
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
                break;
            case "/admin/course/approve":
                if (pathInfo != null) {
                    // Approve course
                    approveCourse(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
                break;
            case "/admin/course/reject":
                if (pathInfo != null) {
                    // Show reject form
                    showRejectForm(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
                break;
            case "/admin/course/ban":
                if (pathInfo != null) {
                    try {
                        int courseID = Integer.parseInt(pathInfo.substring(1));
                        if (courseDAO.isCourseStatus(courseID, "pending")) {
                            response.sendError(HttpServletResponse.SC_NOT_FOUND);
                            return;
                        }
                        if (courseDAO.isCourseStatus(courseID, "rejected")) {
                            response.sendError(HttpServletResponse.SC_NOT_FOUND);
                            return;
                        }
                        if (courseDAO.isCourseStatus(courseID, "banned")) {
                            response.sendError(HttpServletResponse.SC_NOT_FOUND);
                            return;
                        }

                        boolean success = courseDAO.banCourse(courseID);
                        response.sendRedirect(request.getContextPath() + "/admin/courses?message="
                                + URLEncoder.encode(success ? "Course has been banned successfully" : "Failed to ban the course", "UTF-8")
                                + "&status=" + (success ? "success" : "danger"));

                    } catch (NumberFormatException e) {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
                break;
            case "/admin/course/unban":
                if (pathInfo != null) {
                    try {
                        int courseID = Integer.parseInt(pathInfo.substring(1));
                        if (courseDAO.isCourseStatus(courseID, "pending")) {
                            response.sendError(HttpServletResponse.SC_NOT_FOUND);
                            return;
                        }
                        if (courseDAO.isCourseStatus(courseID, "rejected")) {
                            response.sendError(HttpServletResponse.SC_NOT_FOUND);
                            return;
                        }
                        if (courseDAO.isCourseStatus(courseID, "approved")) {
                            response.sendError(HttpServletResponse.SC_NOT_FOUND);
                            return;
                        }
                        boolean success = courseDAO.unbanCourse(courseID);
                        response.sendRedirect(request.getContextPath() + "/admin/courses?message="
                                + URLEncoder.encode(success ? "Course has been unbanned successfully" : "Failed to unban the course", "UTF-8")
                                + "&status=" + (success ? "success" : "danger"));

                    } catch (NumberFormatException e) {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
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

        if (servletPath.equals("/admin/course/reject")) {
            // Process course rejection
            rejectCourse(request, response);
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
        // Handle success and error messages
        String successParam = request.getParameter("success");
        String errorParam = request.getParameter("error");

        if (successParam != null) {
            String message = "";
            switch (successParam) {
                case "approved":
                    message = "Course has been successfully approved.";
                    break;
                case "rejected":
                    message = "Course has been rejected.";
                    break;
                default:
                    message = "Operation completed successfully.";
            }
            request.setAttribute("message", message);
        }

        if (errorParam != null) {
            String error = "";
            switch (errorParam) {
                case "approval-failed":
                    error = "Failed to approve the course.";
                    break;
                case "rejection-failed":
                    error = "Failed to reject the course.";
                    break;
                default:
                    error = "An error occurred during the operation.";
            }
            request.setAttribute("error", error);
        }

        // Forward to course listing page
        request.getRequestDispatcher("/WEB-INF/views/admin/manage-courses/courses.jsp").forward(request, response);
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
        // Handle success and error messages
        String successParam = request.getParameter("success");
        String errorParam = request.getParameter("error");

        if (successParam != null) {
            String message = "";
            switch (successParam) {
                case "approved":
                    message = "Course has been successfully approved.";
                    break;
                case "rejected":
                    message = "Course has been rejected.";
                    break;
                default:
                    message = "Operation completed successfully.";
            }
            request.setAttribute("message", message);
        }

        if (errorParam != null) {
            String error = "";
            switch (errorParam) {
                case "approval-failed":
                    error = "Failed to approve the course.";
                    break;
                case "rejection-failed":
                    error = "Failed to reject the course.";
                    break;
                default:
                    error = "An error occurred during the operation.";
            }
            request.setAttribute("error", error);
        }

        // Forward to course listing page with pending filter
        request.getRequestDispatcher("/WEB-INF/views/admin/manage-courses/courses.jsp").forward(request, response);
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

        if (pathParts.length < 2 || !Validator.isValidNumber(pathParts[1])) {
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
        request.getRequestDispatcher("/WEB-INF/views/admin/manage-courses/course-details.jsp").forward(request, response);
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

        // Extract course ID from path
        String pathInfo = request.getPathInfo();
        String[] pathParts = pathInfo.split("/");

        if (pathParts.length < 2 || !Validator.isValidNumber(pathParts[1])) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        int courseId = Integer.parseInt(pathParts[1]);
        Course course = courseDAO.getCourseById(courseId);

        if (course == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (courseDAO.isCourseStatus(courseId, "banned")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (courseDAO.isCourseStatus(courseId, "rejected")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (courseDAO.isCourseStatus(courseId, "approved")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Update course approval status
        course.setApprovalStatus("approved");
        course.setApprovalDate(new Timestamp(System.currentTimeMillis()));

        boolean updated = courseDAO.updateCourse(course);

        if (updated) {
            // Redirect to pending courses list with success message
            response.sendRedirect(request.getContextPath() + "/admin/course/pending?success=approved");
        } else {
            // Redirect with error message
            response.sendRedirect(request.getContextPath() + "/admin/course/pending?error=approval-failed");
        }
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

        if (pathParts.length < 2 || !Validator.isValidNumber(pathParts[1])) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        int courseId = Integer.parseInt(pathParts[1]);
        Course course = courseDAO.getCourseById(courseId);

        if (course == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (courseDAO.isCourseStatus(courseId, "banned")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (courseDAO.isCourseStatus(courseId, "rejected")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (courseDAO.isCourseStatus(courseId, "approved")) {
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

        int courseId = Integer.parseInt(request.getParameter("courseId"));
        String rejectionReason = request.getParameter("rejectionReason");

        Course course = courseDAO.getCourseById(courseId);

        if (course == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (courseDAO.isCourseStatus(courseId, "banned")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (courseDAO.isCourseStatus(courseId, "rejected")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (courseDAO.isCourseStatus(courseId, "approved")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Update course status
        course.setApprovalStatus("rejected");
        course.setRejectionReason(rejectionReason);

        boolean updated = courseDAO.updateCourse(course);

        if (updated) {
            // Redirect to pending courses list with success message
            response.sendRedirect(request.getContextPath() + "/admin/course/pending?success=rejected");
        } else {
            // Show error message
            response.sendRedirect(request.getContextPath() + "/admin/course/pending?error=rejection-failed");
        }
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
        if (searchParam != null) {
            key = searchParam;
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
        // Handle success and error messages
        String successParam = request.getParameter("success");
        String errorParam = request.getParameter("error");

        if (successParam != null) {
            String message = "";
            switch (successParam) {
                case "approved":
                    message = "Course has been successfully approved.";
                    break;
                case "rejected":
                    message = "Course has been rejected.";
                    break;
                default:
                    message = "Operation completed successfully.";
            }
            request.setAttribute("message", message);
        }

        if (errorParam != null) {
            String error = "";
            switch (errorParam) {
                case "approval-failed":
                    error = "Failed to approve the course.";
                    break;
                case "rejection-failed":
                    error = "Failed to reject the course.";
                    break;
                default:
                    error = "An error occurred during the operation.";
            }
            request.setAttribute("error", error);
        }

        // Forward to course listing page
        request.getRequestDispatcher("/WEB-INF/views/admin/manage-courses/courses.jsp").forward(request, response);

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
        // Handle success and error messages
        String successParam = request.getParameter("success");
        String errorParam = request.getParameter("error");

        if (successParam != null) {
            String message = "";
            switch (successParam) {
                case "approved":
                    message = "Course has been successfully approved.";
                    break;
                case "rejected":
                    message = "Course has been rejected.";
                    break;
                default:
                    message = "Operation completed successfully.";
            }
            request.setAttribute("message", message);
        }

        if (errorParam != null) {
            String error = "";
            switch (errorParam) {
                case "approval-failed":
                    error = "Failed to approve the course.";
                    break;
                case "rejection-failed":
                    error = "Failed to reject the course.";
                    break;
                default:
                    error = "An error occurred during the operation.";
            }
            request.setAttribute("error", error);
        }

        // Forward to course listing page
        request.getRequestDispatcher("/WEB-INF/views/admin/manage-courses/courses.jsp").forward(request, response);
    }
}
