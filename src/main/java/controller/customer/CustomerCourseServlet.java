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
import dao.OrderDAO;
import dao.RefundRequestDAO;
import dao.RatingDAO;
import model.Course;
import model.Category;
import model.SuperUser;
import util.Validator;
import java.util.Collections;
import model.Rating;
import java.util.Comparator;

/**
 * Course controller for listing courses and showing course details.
 */
@WebServlet(name = "CustomerCourseServlet", urlPatterns = {"/courses", "/course/*"})
public class CustomerCourseServlet extends HttpServlet {

    private CourseDAO courseDAO;
    private CategoryDAO categoryDAO;
    private OrderDAO orderDAO;
    private RefundRequestDAO refundRequestDAO;
    private RatingDAO ratingDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        courseDAO = new CourseDAO();
        categoryDAO = new CategoryDAO();
        orderDAO = new OrderDAO();
        refundRequestDAO = new RefundRequestDAO();
        ratingDAO = new RatingDAO();
    }

    /**
     * Handles the HTTP GET request - listing courses or showing details.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        List<Category> categories = categoryDAO.getAllCategories();
        request.setAttribute("categories", categories);

        if (pathInfo != null && pathInfo.length() > 1) {
            String idStr = pathInfo.substring(1);
            if (!idStr.matches("\\d+")) {
                response.sendRedirect(request.getContextPath() + "/courses");
                return;
            }
            showCourseDetail(request, response);
        } else {
            listCourses(request, response);
        }
    }

    /**
     * Show the list of courses with optional filtering.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
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
        request.getRequestDispatcher("/WEB-INF/views/customer/manage-courses/courses-list.jsp").forward(request, response);
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
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
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
        System.out.println("Course: " + course);
        if (course != null) {
            System.out.println("Course Image URL: " + course.getImageUrl());

            // Generate full image path for debugging
            String fullImagePath = request.getContextPath() + "/" + course.getImageUrl();
            System.out.println("Full image path: " + fullImagePath);
        } else {
            System.out.println("Course is null");
        }

        if (course == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Check if user already purchased this course
        SuperUser user = (SuperUser) request.getSession().getAttribute("user");
        boolean alreadyPurchased = false;
        boolean hasPendingRefund = false;
        boolean hasApprovedRefund = false;
        boolean canRateCourse = false;
        Rating userRating = null;

//        if (user != null) {
////            alreadyPurchased = orderDAO.hasUserPurchasedCourse(user.getUserID(), courseId);
////
////            // Check for pending and approved refund requests
////            if (alreadyPurchased) {
////                hasPendingRefund = refundRequestDAO.hasPendingRefundForCourse(user.getUserID(), courseId);
////                hasApprovedRefund = refundRequestDAO.hasApprovedRefundForCourse(user.getUserID(), courseId);
////
////                // Check if user can rate the course (purchased and >80% complete)
////                CourseProgressDAO progressDAO = new CourseProgressDAO();
////                CourseProgress progress = progressDAO.getByUserAndCourse(user.getUserID(), courseId);
////                if (progress != null && progress.getCompletionPercentage().compareTo(new BigDecimal("80")) >= 0) {
////                    canRateCourse = true;
////                }
//
//                // Get user's rating if it exists
//                userRating = ratingDAO.getByUserAndCourse(user.getUserID(), courseId);
//            }
//        }
        // Get course ratings
//        List<Rating> ratings = ratingDAO.getByCourseId(courseId);
        double averageRating = ratingDAO.getAverageRatingForCourse(courseId);
        int ratingCount = ratingDAO.getRatingCountForCourse(courseId);

        // Set attributes for the JSP
        request.setAttribute("course", course);
        request.setAttribute("alreadyPurchased", alreadyPurchased);
        request.setAttribute("hasPendingRefund", hasPendingRefund);
        request.setAttribute("hasApprovedRefund", hasApprovedRefund);
        request.setAttribute("canRateCourse", canRateCourse);
        request.setAttribute("userRating", userRating);
//        request.setAttribute("ratings", ratings);
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
        request.getRequestDispatcher("/WEB-INF/views/customer/manage-courses/course-detail.jsp").forward(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Course Servlet - Displays course listings and details";
    }
}
