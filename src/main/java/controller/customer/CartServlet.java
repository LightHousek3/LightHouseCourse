package controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import dao.CourseDAO;
import dao.CartItemDAO;
import dao.CategoryDAO;
import java.util.List;
import util.CartUtil;
import model.Category;
import model.Course;
import model.Customer;
import util.Validator;

/**
 * Shopping cart controller for handling cart operations.
 */
@WebServlet(name = "CartServlet", urlPatterns = { "/cart", "/cart/add", "/cart/remove", "/cart/clear" })
public class CartServlet extends HttpServlet {

    private CourseDAO courseDAO;
    private CartItemDAO cartItemDAO;
    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        courseDAO = new CourseDAO();
        cartItemDAO = new CartItemDAO();
        categoryDAO = new CategoryDAO();
    }

    /**
     * Handles the HTTP GET request - displaying the cart.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get categories for sidebar
        List<Category> categories = categoryDAO.getAllCategories();
        request.setAttribute("categories", categories);

        String servletPath = request.getServletPath();

        if (servletPath.equals("/cart")) {
            // Display cart
            showCart(request, response);
        } else if (servletPath.equals("/cart/add")) {
            // Add to cart
            addToCart(request, response);
        } else if (servletPath.equals("/cart/remove")) {
            // Remove from cart
            removeFromCart(request, response);
        } else if (servletPath.equals("/cart/clear")) {
            // Clear cart
            clearCart(request, response);
        }
    }

    /**
     * Handles the HTTP POST request.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        doGet(request, response);
    }

    /**
     * Show the shopping cart.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    private void showCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get or createCourse cart from session
        HttpSession session = request.getSession();
        CartUtil cart = (CartUtil) session.getAttribute("cart");
        Customer user = (Customer) session.getAttribute("user");

        if (cart == null) {
            cart = new CartUtil();
            session.setAttribute("cart", cart);
        }

        // If user is logged in, load cart items from database
        if (user != null) {
            cart.setItems(cartItemDAO.getCartItems(user.getCustomerID()));

        }

        // Forward to cart page
        request.getRequestDispatcher("/WEB-INF/views/customer/cart/cart.jsp").forward(request, response);
    }

    /**
     * Add a course to the cart.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    private void addToCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get course ID
        String courseIdParam = request.getParameter("id");

        if (courseIdParam == null || !Validator.isValidNumber(courseIdParam)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid course ID");
            return;
        }

        int courseId = Integer.parseInt(courseIdParam);

        // Get course from database
        Course course = courseDAO.getCourseById(courseId);
        if (course == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Course not found");
            return;
        }

        // Get or createCourse cart
        HttpSession session = request.getSession();
        CartUtil cart = (CartUtil) session.getAttribute("cart");
        Customer user = (Customer) session.getAttribute("user");

        if (user == null) {
            // Return JSON with error message instead of status code
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            String errorJson = "{\"success\":false,\"message\":\"Please log in to add courses to cart\"}";
            response.getWriter().write(errorJson);
            return;
        }

        if (cart == null) {
            cart = new CartUtil();
            session.setAttribute("cart", cart);
        }

        // Check if course is already in cart
        boolean alreadyInCart = cart.containsCourse(courseId);
        boolean added = false;
        String message = "";

        if (alreadyInCart) {
            message = "This course is already in your cart";
        } else {
            // Add course to cart
            added = cart.addItem(course);
            // If user is logged in, also add to database
            if (added && user != null) {
                cartItemDAO.addToCart(user.getCustomerID(), courseId, course.getPrice());
            }

            if (!added) {
                message = "Failed to add course to cart. Please try again.";
            }
        }

        // Return JSON response
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // JSON response with success status, cart count and message
        String jsonResponse = "{\"success\":" + added +
                ",\"count\":" + cart.getItemCount() +
                ",\"message\":\"" + message + "\"}";
        response.getWriter().write(jsonResponse);
    }

    /**
     * Remove a course from the cart.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    private void removeFromCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get course ID
        String courseIdParam = request.getParameter("id");

        if (courseIdParam == null || !Validator.isValidNumber(courseIdParam)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid course ID");
            return;
        }

        int courseId = Integer.parseInt(courseIdParam);

        // Get cart from session
        HttpSession session = request.getSession();
        CartUtil cart = (CartUtil) session.getAttribute("cart");
        Customer user = (Customer) session.getAttribute("user");

        if (cart == null) {
            // Redirect to cart page
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        // Remove item from cart
        boolean removed = cart.removeItem(courseId);

        // If user is logged in, also remove from database
        if (removed && user != null) {
            cartItemDAO.removeFromCart(user.getCustomerID(), courseId);
        }

        // Check if AJAX request
        String isAjax = request.getParameter("ajax");

        if (isAjax != null && isAjax.equals("true")) {
            // Return JSON response
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            // Simple JSON response with success status, cart count and total
            String jsonResponse = "{\"success\":" + removed + ",\"count\":" + cart.getItemCount()
                    + ",\"total\":" + cart.getTotalPrice() + "}";
            response.getWriter().write(jsonResponse);
        } else {
            // Redirect back to cart page
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }

    /**
     * Clear the shopping cart.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    private void clearCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get cart from session
        HttpSession session = request.getSession();
        CartUtil cart = (CartUtil) session.getAttribute("cart");
        Customer user = (Customer) session.getAttribute("user");

        if (cart != null) {
            // Clear the cart
            cart.clear();

            // If user is logged in, also clear from database
            if (user != null) {
                cartItemDAO.clearCart(user.getCustomerID());
            }
        }

        // Check if AJAX request
        String isAjax = request.getParameter("ajax");

        if (isAjax != null && isAjax.equals("true")) {
            // Return JSON response
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            // Simple JSON response with success status
            String jsonResponse = "{\"success\":true}";
            response.getWriter().write(jsonResponse);
        } else {
            // Redirect back to cart page
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Cart Servlet - Handles shopping cart operations";
    }
}
