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
@WebServlet(name = "CustomerCourseServlet", urlPatterns = { "/courses", "/course/*", "/my-courses" })
public class CustomerCourseServlet extends HttpServlet {

    private CourseDAO courseDAO;
    private CategoryDAO categoryDAO;
    private OrderDAO orderDAO;
    private RefundRequestDAO refundRequestDAO;
    private RatingDAO ratingDAO;
    private CourseProgressDAO progressDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        courseDAO = new CourseDAO();
        categoryDAO = new CategoryDAO();
        orderDAO = new OrderDAO();
        refundRequestDAO = new RefundRequestDAO();
        ratingDAO = new RatingDAO();
        progressDAO = new CourseProgressDAO();
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
        }
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
