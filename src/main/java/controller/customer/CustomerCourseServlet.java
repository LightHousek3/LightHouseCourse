package controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import dao.CourseDAO;
import dao.CategoryDAO;
import dao.CourseProgressDAO;
import dao.InstructorDAO;
import dao.OrderDAO;
import dao.RefundRequestDAO;
import dao.RatingDAO;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import model.Course;
import model.Category;
import util.Validator;
import java.util.Collections;
import model.Rating;
import java.util.Comparator;
import model.CourseProgress;
import model.Customer;
import model.OrderDetail;

/**
 * Course controller for showing course details and my courses.
 */
@WebServlet(name = "CustomerCourseServlet", urlPatterns = { "/courses", "/course/*", "/my-courses",
        "/course/instructor-info" })
public class CustomerCourseServlet extends HttpServlet {

    private CourseDAO courseDAO;
    private CategoryDAO categoryDAO;
    private OrderDAO orderDAO;
    private RefundRequestDAO refundRequestDAO;
    private RatingDAO ratingDAO;
    private CourseProgressDAO progressDAO;
    private InstructorDAO instructorDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        courseDAO = new CourseDAO();
        categoryDAO = new CategoryDAO();
        orderDAO = new OrderDAO();
        refundRequestDAO = new RefundRequestDAO();
        ratingDAO = new RatingDAO();
        progressDAO = new CourseProgressDAO();
        instructorDAO = new dao.InstructorDAO();
    }

    /**
     * Handles the HTTP GET request - showing details or my courses.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        String servletPath = request.getServletPath();
        List<Category> categories = categoryDAO.getAllCategories();
        request.setAttribute("categories", categories);

        switch (servletPath) {
            case "/courses":
                // Redirect to home page with scroll parameter
                response.sendRedirect(request.getContextPath() + "/home?scroll=true");
                break;
            case "/course":
                if (pathInfo == null) {
                    response.sendRedirect(request.getContextPath() + "/home?scroll=true");
                    return;
                }
                String idStr = pathInfo.substring(1);
                if (!idStr.matches("\\d+")) {
                    response.sendRedirect(request.getContextPath() + "/home?scroll=true");
                    return;
                }
                showCourseDetail(request, response);
                break;
            case "/my-courses":
                showPurchasedCourses(request, response);
                break;
            case "/course/instructor-info":
                showInstructorInfo(request, response);
                break;
        }
    }

    /**
     * Show the list of courses with optional filtering.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    private void listCourses(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String sortParam = request.getParameter("sortParam");
        if (sortParam == null || sortParam.isEmpty()) {
            sortParam = "newest";
        }

        // Get filter parameters
        String keyword = request.getParameter("keyword");
        String categoryParam = request.getParameter("category");
        String pageParam = request.getParameter("page");

        // Validate keyword
        if (keyword != null && !isValidKeyword(keyword)) {
            request.setAttribute("errorMessage",
                    "Invalid keyword. Requirements:"
                            + "<ul>"
                            + "<li>Length 2-50 characters</li>"
                            + "<li>Must not contain special characters</li>"
                            + "<li>Maximum 5 words</li>"
                            + "<li>Must not repeat any word or phrase more than 3 times</li>"
                            + "</ul>");
            request.setAttribute("keyword", keyword);
            keyword = null;
        }

        // Default values
        int categoryId = 0;
        int page = 1;
        int pageSize = 9; // Courses per page

        // Parse parameters
        if (categoryParam != null && Validator.isValidNumber(categoryParam)) {
            categoryId = Integer.parseInt(categoryParam);
        }

        if (pageParam != null && Validator.isValidNumber(pageParam)) {
            page = Integer.parseInt(pageParam);
            if (page < 1) {
                page = 1;
            }
        }

        // Get courses based on filters
        List<Course> courses;
        int totalCourses;

        if (keyword != null && !keyword.trim().isEmpty()) {
            // Search by keyword (and optionally category)
            courses = courseDAO.searchCourseByNameOrCategory(keyword, categoryId);
            totalCourses = courses.size();
            request.setAttribute("keyword", keyword);
        } else if (categoryId > 0) {
            // Filter by category
            courses = courseDAO.getByCategory(categoryId);
            totalCourses = courses.size();
            Category category = categoryDAO.getCategoryById(categoryId);
            if (category != null) {
                request.setAttribute("categoryName", category.getName());
            }
        } else {
            // No filters, get all courses
            courses = courseDAO.getAllCourses();
            totalCourses = courses.size();
        }

        // Sort courses
        if (courses != null && !courses.isEmpty()) {
            switch (sortParam) {
                case "price_asc":
                    Collections.sort(courses, new Comparator<Course>() {
                        @Override
                        public int compare(Course c1, Course c2) {
                            return Double.compare(c1.getPrice(), c2.getPrice());
                        }
                    });
                    break;
                case "price_desc":
                    Collections.sort(courses, new Comparator<Course>() {
                        @Override
                        public int compare(Course c1, Course c2) {
                            return Double.compare(c2.getPrice(), c1.getPrice());
                        }
                    });
                    break;
                case "popularity":
                    Collections.sort(courses, new Comparator<Course>() {
                        @Override
                        public int compare(Course c1, Course c2) {
                            return Integer.compare(c2.getEnrollmentCount(), c1.getEnrollmentCount());
                        }
                    });
                    break;
                default: // newest
                    Collections.sort(courses, new Comparator<Course>() {
                        @Override
                        public int compare(Course c1, Course c2) {
                            return Integer.compare(c2.getCourseID(), c1.getCourseID());
                        }
                    });
                    break;
            }
        }

        // Pagination (in-memory)
        int offset = (page - 1) * pageSize;
        int end = Math.min(offset + pageSize, courses.size());
        List<Course> pagedCourses;
        if (offset < end) {
            pagedCourses = courses.subList(offset, end);
        } else {
            pagedCourses = new java.util.ArrayList<>();
        }

        // Set attributes for pagination
        int totalPages = (int) Math.ceil((double) totalCourses / pageSize);

        request.setAttribute("courses", pagedCourses);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalCourses", totalCourses);
        request.setAttribute("categoryId", categoryId);
        request.setAttribute("sort", sortParam);

        // Forward to course listing page
        request.getRequestDispatcher("/WEB-INF/views/customer/manage-courses/courses-list.jsp").forward(request,
                response);
    }

    /**
     * Validates the search keyword
     *
     * @param keyword The keyword to validate
     * @return true if keyword is valid, false otherwise
     */
    private boolean isValidKeyword(String keyword) {
        // Kiểm tra keyword null hoặc trống
        if (keyword == null || keyword.trim().isEmpty()) {
            return true;
        }

        keyword = keyword.trim();

        // Kiểm tra độ dài từ khóa (2-50 ký tự)
        if (keyword.length() < 2 || keyword.length() > 50) {
            return false;
        }

        // Kiểm tra ký tự đặc biệt không cho phép
        String specialCharsPattern = "[!@#$%^&*(),.?\":{}|<>]";
        if (keyword.matches(".*" + specialCharsPattern + ".*")) {
            return false;
        }

        // Kiểm tra số từ trong từ khóa (tối đa 5 từ)
        if (keyword.split("\\s+").length > 5) {
            return false;
        }

        // Kiểm tra từ lặp lại (thêm mới)
        String[] words = keyword.split("\\s+");
        String previousWord = "";
        int repeatCount = 1;
        int maxRepeat = 3; // Số lần lặp tối đa cho phép

        for (String word : words) {
            if (word.equals(previousWord)) {
                repeatCount++;
                if (repeatCount > maxRepeat) {
                    return false;
                }
            } else {
                repeatCount = 1;
                previousWord = word;
            }
        }

        // Kiểm tra chuỗi con lặp lại (thêm mới)
        String normalizedKeyword = keyword.toLowerCase();
        for (int length = 3; length <= normalizedKeyword.length() / 2; length++) {
            for (int i = 0; i <= normalizedKeyword.length() - length; i++) {
                String substring = normalizedKeyword.substring(i, i + length);
                int count = 0;
                int lastIndex = 0;

                while ((lastIndex = normalizedKeyword.indexOf(substring, lastIndex)) != -1) {
                    count++;
                    lastIndex++;

                    if (count > 3) { // Nếu một chuỗi con xuất hiện quá 3 lần
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Show the details of a specific course.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    private void showCourseDetail(HttpServletRequest request, HttpServletResponse response)
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

        // Check if user already purchased this course
        Customer user = (Customer) request.getSession().getAttribute("user");
        boolean alreadyPurchased = false;
        boolean hasPendingRefund = false;
        boolean hasApprovedRefund = false;
        boolean canRateCourse = false;
        Rating userRating = null;

        if (user != null) {
            // Check if user has purchased this course (based on most recent order)
            alreadyPurchased = orderDAO.hasCustomerPurchasedCourse(user.getCustomerID(), courseId);

            // Check for pending and approved refund requests on the most recent order
            if (alreadyPurchased) {
                hasPendingRefund = refundRequestDAO.hasPendingRefundForCourse(user.getCustomerID(), courseId);
                hasApprovedRefund = refundRequestDAO.hasApprovedRefundForCourse(user.getCustomerID(), courseId);

                // Check if user can rate the course (purchased and >80% complete)
                CourseProgressDAO progressDAO = new CourseProgressDAO();
                CourseProgress progress = progressDAO.getByCustomerAndCourse(user.getCustomerID(), courseId);
                if (progress != null && progress.getCompletionPercentage().compareTo(new BigDecimal("80")) >= 0) {
                    canRateCourse = true;
                }

                // Get user's rating if it exists
                userRating = ratingDAO.getByCustomerAndCourse(user.getCustomerID(), courseId);
            }
        }
        // Get course ratings
        List<Rating> ratings = ratingDAO.getRatingsByCourseId(courseId);
        double averageRating = ratingDAO.getAverageRatingForCourse(courseId);
        int ratingCount = ratingDAO.getRatingCountForCourse(courseId);

        // Set attributes for the JSP
        request.setAttribute("course", course);
        request.setAttribute("alreadyPurchased", alreadyPurchased);
        request.setAttribute("hasPendingRefund", hasPendingRefund);
        request.setAttribute("hasApprovedRefund", hasApprovedRefund);
        request.setAttribute("canRateCourse", canRateCourse);
        request.setAttribute("userRating", userRating);
        request.setAttribute("ratings", ratings);
        request.setAttribute("averageRating", averageRating);
        request.setAttribute("ratingCount", ratingCount);

        // Set error message if applicable
        String error = request.getParameter("error");
        if (error != null) {
            switch (error) {
                case "not_purchased":
                    request.setAttribute("errorMessage", "You need to purchase this course to access its content.");
                    break;
                case "refund_pending":
                    request.setAttribute("errorMessage",
                            "You cannot access this course while your refund request is pending. Please wait for the request to be processed.");
                    break;
                case "refund_approved":
                    request.setAttribute("errorMessage",
                            "Your refund for this course has been approved. You no longer have access to this content.");
                    break;
                default:
                    // No message for unknown error types
                    break;
            }
        }

        // Forward to course detail page
        request.getRequestDispatcher("/WEB-INF/views/customer/manage-courses/course-detail.jsp").forward(request,
                response);
    }

    private void showPurchasedCourses(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("user");

        if (customer == null)
            return;

        try {
            // Get user's purchased courses with order details - now only returns the most
            // recent orders for each course
            List<Object[]> purchasedCoursesData = orderDAO
                    .getCustomerPurchasedCoursesWithOrderDetails(customer.getCustomerID());
            List<Course> purchasedCourses = new ArrayList<>();
            List<OrderDetail> orderDetails = new ArrayList<>();
            List<CourseProgress> progressList = new ArrayList<>();

            // Process the results - separate courses and order details
            for (Object[] data : purchasedCoursesData) {
                if (data.length < 2 || data[0] == null || data[1] == null) {
                    continue; // Skip invalid data rows
                }

                Course course = (Course) data[0];
                OrderDetail detail = (OrderDetail) data[1];

                // The getCustomerPurchasedCoursesWithOrderDetails now only returns completed
                // orders
                // with no refund status on the most recent order
                purchasedCourses.add(course);
                orderDetails.add(detail);

                // Get progress for each course
                CourseProgress progress = progressDAO.getByCustomerAndCourse(customer.getCustomerID(),
                        course.getCourseID());
                if (progress == null) {
                    progress = new CourseProgress(customer.getCustomerID(), course.getCourseID());
                    progress.setCompletionPercentage(BigDecimal.ZERO);
                } else if (progress.getCompletionPercentage() == null) {
                    progress.setCompletionPercentage(BigDecimal.ZERO);
                }
                progressList.add(progress);
            }

            request.setAttribute("purchasedCourses", purchasedCourses);
            request.setAttribute("orderDetails", orderDetails);
            request.setAttribute("progressList", progressList);

            // Forward to my courses page
            request.getRequestDispatcher("/WEB-INF/views/customer/manage-courses/my-courses.jsp").forward(request,
                    response);

        } catch (Exception e) {
            // Log the exception
            getServletContext().log("Error in CustomerCourseServlet", e);
        }
    }

    private void showInstructorInfo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String instructorIdParam = request.getParameter("instructorId");
        if (!Validator.isValidNumber(instructorIdParam)) {
            response.sendRedirect(request.getContextPath() + "/courses");
            return;
        }

        int instructorId = Integer.parseInt(instructorIdParam);
        model.Instructor instructor = new dao.InstructorDAO().getInstructorById(instructorId);

        if (instructor == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Instructor not found.");
            return;
        }

        // Thêm dữ liệu bổ sung
        List<Course> instructorCourses = courseDAO.getCoursesByInstructorId(instructorId);
        double averageRating = ratingDAO.getAverageRatingByInstructorId(instructorId);
        int ratingCount = ratingDAO.getRatingCountByInstructorId(instructorId);
        int totalStudents = orderDAO.getTotalStudentsByInstructorId(instructorId);

        instructor.setTotalStudents(totalStudents);

        // Set attribute and forward to view
        request.setAttribute("selectedUser", instructor);
        request.setAttribute("courses", instructorCourses);
        request.setAttribute("totalCourses", instructorCourses.size());
        request.setAttribute("averageRating", averageRating);
        request.setAttribute("ratingCount", ratingCount);
        request.setAttribute("totalStudents", totalStudents);

        request.getRequestDispatcher("/WEB-INF/views/customer/manage-courses/view-instructor-information.jsp")
                .forward(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Course Servlet - Displays course details and my courses";
    }
}
