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
import dao.PaymentTransactionDAO;
import dao.RefundRequestDAO;
import java.sql.Timestamp;
import java.util.ArrayList;
import model.CartItem;
import model.Order;
import model.OrderDetail;
import model.Customer;
import model.Course;
import model.CourseProgress;
import model.PaymentTransaction;
import service.VNPayService;
import util.CartUtil;
import util.RefundUtil;
import util.Validator;

/**
 * Order controller for handling checkout and order history.
 */
@WebServlet(name = "OrderServlet", urlPatterns = {"/order/checkout", "/order/history", "/order/detail/*",
    "/order/payment"})
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
        } else if (servletPath.equals("/order/payment")) {
            handleVNPayCallback(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();

        if (servletPath.equals("/order/checkout")) {
            // Process checkout (either from cart or direct course purchase)
            processCheckout(request, response);
        }
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

            boolean alreadyPurchased = orderDAO.hasCustomerPurchasedCourse(customer.getCustomerID(), courseId);
            if (alreadyPurchased) {
                response.sendRedirect(request.getContextPath() + "/course/" + courseId + "?error=already_purchased");
                return;
            }

            request.setAttribute("course", course);
            request.setAttribute("isDirectCheckout", true);
            request.getRequestDispatcher("/WEB-INF/views/customer/order/checkout.jsp").forward(request, response);
        } else {
            CartUtil cart = (CartUtil) session.getAttribute("cart");

            if (cart == null || cart.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/cart?error=empty");
                return;
            }

            // Check if user already purchased any course in the cart
            StringBuilder alreadyPurchasedCourses = new StringBuilder();
            boolean hasAlreadyPurchasedCourses = false;

            // Check each course in the cart
            for (CartItem item : cart.getItems()) {
                int courseId = item.getCourse().getCourseID();
                boolean alreadyPurchased = orderDAO.hasCustomerPurchasedCourse(customer.getCustomerID(), courseId);

                if (alreadyPurchased) {
                    // User already purchased this course
                    if (hasAlreadyPurchasedCourses) {
                        alreadyPurchasedCourses.append(", ");
                    }
                    alreadyPurchasedCourses.append(item.getCourse().getName());
                    hasAlreadyPurchasedCourses = true;
                }
            }

            // If any course is already purchased, show error message
            if (hasAlreadyPurchasedCourses) {
                request.setAttribute("error",
                        "You have already purchased the following course(s): " + alreadyPurchasedCourses.toString()
                        + ". Please remove them from your cart before proceeding.");
                request.getRequestDispatcher("/WEB-INF/views/customer/cart/cart.jsp").forward(request, response);
                return;
            }

            request.setAttribute("isDirectCheckout", false);
            request.getRequestDispatcher("/WEB-INF/views/customer/order/checkout.jsp").forward(request, response);
        }
    }

    /**
     * Process the checkout for either cart items or a single course.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private void processCheckout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get user from session
        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("user");

        if (customer == null) {
            // Redirect to login page
            response.sendRedirect(request.getContextPath() + "/login?redirect=checkout");
            return;
        }

        // Get payment method from form
        String paymentMethod = request.getParameter("payment-method");

        if (paymentMethod == null || !paymentMethod.equals("VNPAY")) {
            // Show error message
            request.setAttribute("error", "The system doesn't support this payment method!");
            request.getRequestDispatcher("/WEB-INF/views/customer/order/checkout.jsp").forward(request, response);
            return;
        }

        // Create order
        Order order = new Order();
        order.setUserID(customer.getCustomerID());
        order.setOrderDate(new Timestamp(System.currentTimeMillis()));
        order.setStatus("pending"); // Change status to pending until payment is confirmed
        order.setPaymentMethod(paymentMethod);

        // Create order details
        List<OrderDetail> details = new ArrayList<>();
        double totalAmount = 0;

        // Check if this is a direct course checkout
        String courseIdParam = request.getParameter("courseId");

        if (courseIdParam != null && Validator.isValidNumber(courseIdParam)) {
            // Direct course checkout
            int courseId = Integer.parseInt(courseIdParam);

            // Get course from database
            Course course = courseDAO.getCourseById(courseId);

            if (course == null) {
                // Course not found
                response.sendRedirect(request.getContextPath() + "/courses?error=course_not_found");
                return;
            }

            // Check if user already purchased this course
            boolean alreadyPurchased = orderDAO.hasCustomerPurchasedCourse(customer.getCustomerID(), courseId);

            if (alreadyPurchased) {
                // User already purchased this course
                response.sendRedirect(request.getContextPath() + "/course/" + courseId + "?error=already_purchased");
                return;
            }

            // Create order detail for this course
            OrderDetail detail = new OrderDetail();
            detail.setCourseID(course.getCourseID());
            detail.setPrice(course.getPrice());
            details.add(detail);

            // Set total amount
            totalAmount = course.getPrice();
            order.setTotalAmount(totalAmount);

            // Set course attribute for error handling
            request.setAttribute("course", course);
            request.setAttribute("isDirectCheckout", true);

        } else {
            // Cart checkout
            CartUtil cart = (CartUtil) session.getAttribute("cart");

            if (cart == null || cart.isEmpty()) {
                // Redirect to cart page with message
                response.sendRedirect(request.getContextPath() + "/cart?error=empty");
                return;
            }

            // Check if user already purchased any course in the cart
            StringBuilder alreadyPurchasedCourses = new StringBuilder();
            boolean hasAlreadyPurchasedCourses = false;

            // First, check if any course is already purchased
            for (CartItem item : cart.getItems()) {
                int courseId = item.getCourse().getCourseID();
                boolean alreadyPurchased = orderDAO.hasCustomerPurchasedCourse(customer.getCustomerID(), courseId);

                if (alreadyPurchased) {
                    // User already purchased this course
                    if (hasAlreadyPurchasedCourses) {
                        alreadyPurchasedCourses.append(", ");
                    }
                    alreadyPurchasedCourses.append(item.getCourse().getName());
                    hasAlreadyPurchasedCourses = true;
                }
            }

            // If any course is already purchased, show error message
            if (hasAlreadyPurchasedCourses) {
                request.setAttribute("error",
                        "You have already purchased the following course(s): " + alreadyPurchasedCourses.toString()
                        + ". Please remove them from your cart before proceeding.");
                request.getRequestDispatcher("/WEB-INF/views/customer/order/checkout.jsp").forward(request, response);
                return;
            }

            // Create order details from cart items
            for (CartItem item : cart.getItems()) {
                OrderDetail detail = new OrderDetail();
                detail.setCourseID(item.getCourse().getCourseID());
                detail.setPrice(item.getPrice());
                details.add(detail);
            }

            // Set total amount
            totalAmount = cart.getTotalPrice();
            order.setTotalAmount(totalAmount);

            request.setAttribute("isDirectCheckout", false);
        }

        order.setOrderDetails(details);

        // Insert order into database
        int orderId = orderDAO.insertOrder(order);

        if (orderId > 0) {
            // Store orderId in session to associate with VNPay payment
            session.setAttribute("pendingOrderId", orderId);

            // If payment method is VNPAY, redirect to VNPay payment gateway
            if ("VNPAY".equals(paymentMethod)) {
                // Create VNPay payment URL with the order information
                String vnpayUrl = VNPayService.getPaymentUrl(request, response, totalAmount);

                // Redirect to VNPay payment gateway
                response.sendRedirect(vnpayUrl);
                return;
            }
        } else {
            // Show error message
            request.setAttribute("error", "Checkout failed. Please try again.");
            request.getRequestDispatcher("/WEB-INF/views/customer/order/checkout.jsp").forward(request, response);
        }
    }

    /**
     * Handle the callback from VNPay payment gateway.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private void handleVNPayCallback(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get the parameters from VNPay callback
        String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
        String vnp_TransactionNo = request.getParameter("vnp_TransactionNo");
        String vnp_TxnRef = request.getParameter("vnp_TxnRef");
        String vnp_OrderInfo = request.getParameter("vnp_OrderInfo");
        String vnp_Amount = request.getParameter("vnp_Amount");
        String vnp_BankCode = request.getParameter("vnp_BankCode");

        // Get the pending order ID from session
        HttpSession session = request.getSession();
        Integer orderId = (Integer) session.getAttribute("pendingOrderId");

        if (orderId == null) {
            // No pending order found in session
            response.sendRedirect(request.getContextPath() + "/courses?error=payment_failed");
            return;
        }

        // Clear the pending order from session
        session.removeAttribute("pendingOrderId");

        // Check if payment was successful (00 is the success code from VNPay)
        if ("00".equals(vnp_ResponseCode)) {
            // Update order status to completed
            Order order = orderDAO.getOrderById(orderId);
            if (order != null) {
                order.setStatus("completed");
                order.setPaymentMethod("VNPAY");
                order.setPaymentTransactionID(vnp_TransactionNo);

                // Create payment transaction
                PaymentTransaction transaction = new PaymentTransaction("payment", "VNPAY");
                transaction.setOrderID(orderId);
                transaction.setProviderTransactionID(vnp_TransactionNo);
                // Could add more details like bank code if needed
                if (vnp_BankCode != null && !vnp_BankCode.isEmpty()) {
                    transaction.setBankAccountInfo(vnp_BankCode);
                }

                // Save payment transaction
                PaymentTransactionDAO transactionDAO = new PaymentTransactionDAO();
                transactionDAO.insert(transaction);

                boolean updated = orderDAO.updateOrder(order);

                if (updated) {
                    // If this was a cart checkout, clear the cart
                    CartUtil cart = (CartUtil) session.getAttribute("cart");
                    if (cart != null) {
                        cart.clear();
                    }

                    // Redirect to order detail page with success message
                    response.sendRedirect(request.getContextPath() + "/order/detail/" + orderId + "?success=payment");
                    return;
                }
            }
        }

        // Payment failed or order update failed
        response.sendRedirect(request.getContextPath() + "/order/detail/" + orderId + "?error=payment_failed");
    }

    private void showOrderHistory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Customer customer = (Customer) session.getAttribute("user");

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
                CourseProgress progress = progressDAO.getByUserAndCourse(customer.getCustomerID(),
                        detail.getCourseID());
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
