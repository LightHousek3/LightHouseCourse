package controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import dao.OrderDAO;
import dao.CourseDAO;
import dao.CourseProgressDAO;
import dao.RefundRequestDAO;
import util.CartUtil;
import model.Order;
import model.OrderDetail;
import model.Customer;
import model.Course;
import model.CourseProgress;
import util.RefundUtil;
import util.Validator;

/**
 * Order controller for handling checkout and order history.
 */
@WebServlet(name = "OrderServlet", urlPatterns = {"/order/checkout", "/order/history", "/order/detail/*"})
public class OrderServlet extends HttpServlet {

    private OrderDAO orderDAO;
    private CourseDAO courseDAO;
    private CourseProgressDAO progressDAO;
    private RefundRequestDAO refundDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        orderDAO = new OrderDAO();
        courseDAO = new CourseDAO();
        progressDAO = new CourseProgressDAO();
        refundDAO = new RefundRequestDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();

        if (servletPath.equals("/order/checkout")) {
            showCheckout(request, response);
        } else if (servletPath.equals("/order/history")) {
            showOrderHistory(request, response);
        } else if (servletPath.startsWith("/order/detail")) {
            showOrderDetail(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    private void showCheckout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("user");
        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirect=checkout");
            return;
        }

        String courseIdParam = request.getParameter("courseId");
        if (courseIdParam != null && Validator.isValidNumber(courseIdParam)) {
            int courseId = Integer.parseInt(courseIdParam);
            Course course = courseDAO.getCourseById(courseId);

            if (course == null) {
                response.sendRedirect(request.getContextPath() + "/courses?error=course_not_found");
                return;
            }

            boolean alreadyPurchased = orderDAO.hasUserPurchasedCourse(customer.getCustomerID(), courseId);
            if (alreadyPurchased) {
                response.sendRedirect(request.getContextPath() + "/course/" + courseId + "?error=already_purchased");
                return;
            }

            request.setAttribute("course", course);
            request.setAttribute("isDirectCheckout", true);

        } else {
            CartUtil cart = (CartUtil) session.getAttribute("cart");

            if (cart == null || cart.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/cart?error=empty");
                return;
            }

            request.setAttribute("isDirectCheckout", false);
        }

        request.getRequestDispatcher("/WEB-INF/views/customer/order/checkout.jsp").forward(request, response);
    }

    private void showOrderHistory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("user");
        System.out.println(customer);

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirect=/order/history");
            return;
        }

        List<Order> orders = orderDAO.getOrdersByUserId(customer.getCustomerID());

        // Check eligibility for refund for each order
        for (Order order : orders) {
            boolean isEligibleForRefund = RefundUtil.isEntireOrderEligibleForRefund(order, customer.getCustomerID());
            order.setAttribute("eligibleForRefund", isEligibleForRefund);
        }
        String success = request.getParameter("success");
        if ("true".equals(success)) {
            request.setAttribute("success", "Create refund request successfully");
        }

        request.setAttribute("orders", orders);

        request.getRequestDispatcher("/WEB-INF/views/customer/order/order-history.jsp").forward(request, response);
    }

    private void showOrderDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid order ID");
            return;
        }

        int orderId;
        try {
            orderId = Integer.parseInt(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid order ID");
            return;
        }

        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("user");

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirect=/order/detail/" + orderId);
            return;
        }

        Order order = orderDAO.getOrderById(orderId);
        if (order == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
            return;
        }

        if (order.getCustomerID() != customer.getCustomerID()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You don't have permission to view this order");
            return;
        }

        if (order.getOrderDetails() != null) {
            for (OrderDetail detail : order.getOrderDetails()) {
                CourseProgress progress = progressDAO.getByUserAndCourse(customer.getCustomerID(), detail.getCourseID());
                if (progress != null) {
                    detail.setAttribute("progress", progress);

                    boolean isEligible = RefundUtil.isCourseEligibleForRefund(order, detail.getCourseID(),
                            customer.getCustomerID());
                    detail.setAttribute("eligibleForRefund", isEligible);

                    double refundAmount = RefundUtil.calculateCourseRefundAmount(order, detail.getCourseID());
                    detail.setAttribute("refundAmount", refundAmount);
                }
            }
        }

        boolean isOrderEligibleForRefund = RefundUtil.isEntireOrderEligibleForRefund(order, customer.getCustomerID());
        request.setAttribute("orderEligibleForRefund", isOrderEligibleForRefund);

        double orderRefundAmount = RefundUtil.calculateRefundAmount(order);
        request.setAttribute("orderRefundAmount", orderRefundAmount);

        request.setAttribute("order", order);

        request.getRequestDispatcher("/WEB-INF/views/customer/order/order-detail.jsp").forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Order Servlet - Handles checkout and order history for Customer";
    }
}
