package controller.customer;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import dao.CourseDAO;
import dao.OrderDAO;
import dao.RefundRequestDAO;
import dao.CourseProgressDAO;
import model.Order;
import model.RefundRequest;
import model.Customer;
import util.RefundUtil;
import model.OrderDetail;

/**
 * Servlet handling refund-related operations.
 * Manages user refund requests and admin processing of those requests.
 */
@WebServlet("/refund/*")
public class RefundServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private RefundRequestDAO refundDAO;
    private OrderDAO orderDAO;
    private CourseDAO courseDAO;
    private CourseProgressDAO progressDAO;

    /**
     * Default constructor initializing DAOs
     */
    public RefundServlet() {
        super();
        refundDAO = new RefundRequestDAO();
        orderDAO = new OrderDAO();
        courseDAO = new CourseDAO();
        progressDAO = new CourseProgressDAO();
    }

    /**
     * Handles GET requests for refund operations
     * 
     * @param request
     * @param response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        // Check if user is logged in
        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("user");

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirect=" + request.getRequestURI());
            return;
        }

        // Handle different paths
        if (pathInfo.startsWith("/request/order/")) {
            // Show refund request form for an entire order
            try {
                int orderId = Integer.parseInt(pathInfo.substring("/request/order/".length()));
                Order order = orderDAO.getOrderById(orderId);

                if (order == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
                    return;
                }

                request.setAttribute("order", order);
                request.setAttribute("isEntireOrder", true);

                // Calculate refund amount
                double refundAmount = RefundUtil.calculateRefundAmount(order);
                request.setAttribute("refundAmount", refundAmount);
                request.setAttribute("refundPercentage", RefundUtil.getRefundPercentage(order));

                // Set additional attributes required by the JSP
                if (order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
                    // For an entire order refund, we need to show the course names
                    StringBuilder courseNameBuilder = new StringBuilder();
                    for (int i = 0; i < order.getOrderDetails().size(); i++) {
                        OrderDetail detail = order.getOrderDetails().get(i);
                        if (detail.getCourse() != null) {
                            courseNameBuilder.append(detail.getCourse().getName());
                            if (i < order.getOrderDetails().size() - 1) {
                                courseNameBuilder.append(", ");
                            }
                        }
                    }
                    request.setAttribute("courseName", courseNameBuilder.toString());
                }

                // Set order date and original price
                request.setAttribute("orderDate", order.getOrderDate());
                request.setAttribute("originalPrice", order.getTotalAmount());

                request.getRequestDispatcher("/WEB-INF/views/customer/order/refund-request.jsp").forward(request,
                        response);

            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid order ID");
            }
        }
    }

    /**
     * Handles POST requests for refund operations
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        // Check if user is logged in
        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("user");

        if (customer == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirect=" + request.getRequestURI());
            return;
        }

        // Handle different paths
        if (pathInfo != null && pathInfo.startsWith("/request/order/")) {
            // Submit refund request for entire order
            try {
                int orderId = Integer.parseInt(pathInfo.substring("/request/order/".length()));
                Order order = orderDAO.getOrderById(orderId);

                if (order == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
                    return;
                }

                // Get reason from form (required)
                String reason = request.getParameter("reason");
                if (reason == null || reason.trim().isEmpty()) {
                    request.setAttribute("error", "Reason is required");
                    request.setAttribute("order", order);
                    request.setAttribute("isEntireOrder", true);

                    // Calculate refund amount
                    double refundAmount = RefundUtil.calculateRefundAmount(order);
                    request.setAttribute("refundAmount", refundAmount);
                    request.setAttribute("refundPercentage", RefundUtil.getRefundPercentage(order));

                    // Set additional attributes required by the JSP (same as in doGet)
                    if (order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
                        // For an entire order refund, we need to show the course names
                        StringBuilder courseNameBuilder = new StringBuilder();
                        for (int i = 0; i < order.getOrderDetails().size(); i++) {
                            OrderDetail detail = order.getOrderDetails().get(i);
                            if (detail.getCourse() != null) {
                                courseNameBuilder.append(detail.getCourse().getName());
                                if (i < order.getOrderDetails().size() - 1) {
                                    courseNameBuilder.append(", ");
                                }
                            }
                        }
                        request.setAttribute("courseName", courseNameBuilder.toString());
                    }

                    // Set order date and original price
                    request.setAttribute("orderDate", order.getOrderDate());
                    request.setAttribute("originalPrice", order.getTotalAmount());

                    request.getRequestDispatcher("/WEB-INF/views/customer/order/refund-request.jsp").forward(request,
                            response);
                    return;
                }

                // Calculate refund amount
                double refundAmount = RefundUtil.calculateRefundAmount(order);
                int refundPercentage = RefundUtil.getRefundPercentage(order);

                // Create refund request
                RefundRequest refundRequest = new RefundRequest(
                        orderId,
                        customer.getCustomerID(),
                        reason,
                        refundAmount);
                refundRequest.setRefundPercentage(refundPercentage);

                int refundId = refundDAO.insertRefundRequests(refundRequest);

                if (refundId > 0) {
                    // Success
                    response.sendRedirect(request.getContextPath() + "/order/history?success=true");
                } else {
                    // Error
                    request.setAttribute("error", "Failed to create refund request");
                    request.getRequestDispatcher("/WEB-INF/views/customer/order/refund-request.jsp").forward(request,
                            response);
                }

            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid order ID");
            }
        }
    }
}
