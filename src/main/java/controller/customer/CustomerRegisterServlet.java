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
import jakarta.servlet.http.HttpSession;
import model.Customer;
import util.EmailUtils;
import util.PasswordEncrypt;

/**
 *
 * @author NhiDTY-CE180492
 */
@WebServlet(name = "CustomerRegisterServlet", urlPatterns = {"/register"})
public class CustomerRegisterServlet extends HttpServlet {

    private final CustomerDAO customerDAO = new CustomerDAO();

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Hiển thị form đăng ký
        request.getRequestDispatcher("/WEB-INF/views/customer/register-account/register-account.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        System.out.println(">>> Email nhập từ form: " + email);

        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirm-password");

        String fullName = request.getParameter("fullname");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");

        // Validate input
        if (username == null || username.trim().isEmpty()) {
            request.setAttribute("error", "Username is required.");
            request.getRequestDispatcher("/WEB-INF/views/customer/register-account/register-account.jsp").forward(request, response);
            return;
        }

        if (customerDAO.usernameExists(username)) {
            request.setAttribute("error", "Username already exists.");
            request.getRequestDispatcher("/WEB-INF/views/customer/register-account/register-account.jsp").forward(request, response);
            return;
        }

        if (email == null || !email.matches("^\\S+@\\S+\\.\\S+$")) {
            request.setAttribute("error", "Invalid email format.");
            request.getRequestDispatcher("/WEB-INF/views/customer/register-account/register-account.jsp").forward(request, response);
            return;
        }

        if (customerDAO.emailExists(email)) {
            request.setAttribute("error", "Email already registered.");
            request.getRequestDispatcher("/WEB-INF/views/customer/register-account/register-account.jsp").forward(request, response);
            return;
        }

        if (password == null || password.length() < 6) {
            request.setAttribute("error", "Password must be at least 6 characters.");
            request.getRequestDispatcher("/WEB-INF/views/customer/register-account/register-account.jsp").forward(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match.");
            request.getRequestDispatcher("/WEB-INF/views/customer/register-account/register-account.jsp").forward(request, response);
            return;
        }

        // Encrypt password
        String encryptedPassword = PasswordEncrypt.encrypt(password);

        // Tạo mã xác minh 6 số ngẫu nhiên
       String token = String.format("%06d", new java.util.Random().nextInt(1000000));

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

        int customerId = customerDAO.insertCustomer(customer);

        if (customerId > 0) {
            // Gửi email xác minh
            try {
                EmailUtils.sendVerificationEmail(email, token);

                // Lưu token và email vào session để xác minh
                HttpSession session = request.getSession();
                session.setAttribute("pendingEmail", email);
                session.setAttribute("pendingToken", token);

                // Điều hướng đến trang nhập mã xác minh
                request.getRequestDispatcher("/verify").forward(request, response);
                return;
            } catch (Exception e) {
                customerDAO.deleteCustomer(customerId);
                request.setAttribute("error", "Failed to send verification email.");
                request.getRequestDispatcher("/WEB-INF/views/customer/register-account/register-account.jsp").forward(request, response);
                return;
            }
        } else {
            request.setAttribute("error", "Registration failed. Please try again.");
            request.getRequestDispatcher("/WEB-INF/views/customer/register-account/register-account.jsp").forward(request, response);
        }

        /* // Tự động đăng nhập
            HttpSession session = request.getSession();
            superuser.setSuperUserID(userId);
            session.setAttribute("user", superuser);
            response.sendRedirect(request.getContextPath() + "/home?register=success");
        } else {
            request.setAttribute("error", "Registration failed. Please try again.");
            request.getRequestDispatcher("/WEB-INF/views/customer/register-account/register-account.jsp").forward(request, response);
        }*/
    }

}
