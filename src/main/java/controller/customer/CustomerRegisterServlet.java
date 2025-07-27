/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.customer;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dao.CustomerDAO;
import dao.SuperUserDAO;
import jakarta.servlet.http.HttpSession;
import java.sql.Timestamp;
import model.Customer;
import util.EmailUtils;
import util.PasswordEncrypt;
import util.Validator;

/**
 *
 * @author NhiDTY-CE180492
 */
@WebServlet(name = "CustomerRegisterServlet", urlPatterns = { "/register" })
public class CustomerRegisterServlet extends HttpServlet {

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final SuperUserDAO superUserDAO = new SuperUserDAO();
    private static final int TOKEN_EXPIRY_MINUTES = 10; // 10 minutes

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Hiển thị form đăng ký
        request.getRequestDispatcher("/WEB-INF/views/auth/register-account.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirm-password");
        String fullName = request.getParameter("fullname");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");

        // Store parameters for form repopulation
        request.setAttribute("username", username);
        request.setAttribute("email", email);
        request.setAttribute("fullname", fullName);
        request.setAttribute("phone", phone);
        request.setAttribute("address", address);

        boolean hasError = false;

        // Validate username
        if (!Validator.isValidUsername(username)) {
            request.setAttribute("usernameError",
                    "Username must be 3-20 characters long and contain only letters, numbers, and underscores.");
            hasError = true;
        } else if (customerDAO.usernameExists(username) || superUserDAO.usernameExists(username)) {
            request.setAttribute("usernameError", "Username already exists.");
            hasError = true;
        }

        // Validate email
        if (!Validator.isValidEmail(email)) {
            request.setAttribute("emailError", "Please enter a valid email address.");
            hasError = true;
        } else if (customerDAO.emailExists(email) || superUserDAO.emailExists(email)) {
            request.setAttribute("emailError", "Email already registered.");
            hasError = true;
        }

        // Validate password
        if (!Validator.isValidPassword(password)) {
            request.setAttribute("passwordError", "Password must be at least 6 characters.");
            hasError = true;
        }

        // Validate password confirmation
        if (password != null && !password.equals(confirmPassword)) {
            request.setAttribute("confirmPasswordError", "Passwords do not match.");
            hasError = true;
        }

        // Validate fullName if provided
        if (fullName != null && !fullName.trim().isEmpty() && !Validator.isValidFullname(fullName)) {
            request.setAttribute("fullnameError",
                    "Full name should contain at least 2 words, only letters, spaces, and basic punctuation.");
            hasError = true;
        }

        // Validate phone if provided
        if (phone != null && !phone.trim().isEmpty() && !Validator.isValidPhone(phone)) {
            request.setAttribute("phoneError", "Phone number must be 10-11 digits.");
            hasError = true;
        }

        // If there are any validation errors, return to the registration form
        if (hasError) {
            request.getRequestDispatcher("/WEB-INF/views/auth/register-account.jsp").forward(request, response);
            return;
        }

        // Encrypt password
        String encryptedPassword = PasswordEncrypt.encrypt(password);

        // Tạo mã xác minh 6 số ngẫu nhiên
        String token = String.format("%06d", new java.util.Random().nextInt(1000000));

        // Calculate expiry time (10 minutes from now)
        Timestamp tokenExpires = new Timestamp(System.currentTimeMillis() + (TOKEN_EXPIRY_MINUTES * 60 * 1000));

        // Tạo customer
        Customer customer = new Customer();
        customer.setUsername(username);
        customer.setEmail(email);
        customer.setPassword(encryptedPassword);
        customer.setFullName(fullName);
        customer.setPhone(phone);
        customer.setAddress(address);
        customer.setActive(false);
        customer.setAvatar("/assets/imgs/avatars/default-user.png");
        customer.setToken(token);
        customer.setTokenExpires(tokenExpires);
        customer.setLastTokenRequest(new Timestamp(System.currentTimeMillis()));

        int customerId = customerDAO.insertCustomer(customer);

        if (customerId > 0) {
            // Gửi email xác minh
            try {
                EmailUtils.sendVerificationEmail(email, token);

                // Lưu token và email vào session để xác minh
                HttpSession session = request.getSession();

                // Clear any password reset related attributes to avoid conflicts
                session.removeAttribute("resetEmail");
                session.removeAttribute("resetFailCount");

                // Set registration specific attributes
                session.setAttribute("pendingEmail", email);
                session.setAttribute("pendingToken", token);
                session.setAttribute("verifyFailCount", 0);

                // Điều hướng đến trang nhập mã xác minh
                request.getRequestDispatcher("/verify").forward(request, response);
                return;
            } catch (Exception e) {
                customerDAO.deleteCustomer(customerId);
                request.setAttribute("error", "Failed to send verification email.");
                request.getRequestDispatcher("/WEB-INF/views/auth/register-account.jsp").forward(request, response);
                return;
            }
        } else {
            request.setAttribute("error", "Registration failed. Please try again.");
            request.getRequestDispatcher("/WEB-INF/views/auth/register-account.jsp").forward(request, response);
        }
    }
}
