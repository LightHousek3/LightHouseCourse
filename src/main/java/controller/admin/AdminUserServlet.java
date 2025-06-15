/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.admin;

import dao.UserDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.User;
import service.EmailService;
import util.PasswordEncrypt;
import util.Validator;

/**
 * Admin dashboard controller.
 *
 * @author DangPH - CE180896
 */
@WebServlet(name = "AdminUserServlet", urlPatterns = {"/admin/users", "/admin/users/*"})
public class AdminUserServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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
        String pathInfo = request.getPathInfo();
        String role = request.getParameter("role");

        if (pathInfo == null || pathInfo.equals("/")) {
            // List all users with optional role filter
            List<User> users;
            if (role != null && !role.isEmpty()) {
                users = userDAO.getUsersByRole(role);
            } else {
                users = userDAO.getAllUsers();
            }
            request.setAttribute("users", users);
            request.setAttribute("selectedRole", role);
            request.getRequestDispatcher("/WEB-INF/views/admin/manage-users/user-list.jsp").forward(request, response);
            return;
        }

        if (pathInfo.startsWith("/details/")) {
            // Show user details
            String userIdStr = pathInfo.substring("/details/".length());
            try {
                int userId = Integer.parseInt(userIdStr);
                User selectedUser = userDAO.getUserById(userId);
                request.setAttribute("selectedUser", selectedUser);
                request.getRequestDispatcher("/WEB-INF/views/admin/manage-users/user-detail.jsp").forward(request, response);
                return;
            } catch (NumberFormatException e) {
                request.setAttribute("selectedUser", null);
                request.getRequestDispatcher("/WEB-INF/views/admin/manage-users/user-detail.jsp").forward(request, response);
                return;
            }
        }

        if (pathInfo.equals("/add")) {
            // Show add user form
            request.getRequestDispatcher("/WEB-INF/views/admin/manage-users/user-add.jsp").forward(request, response);
            return;
        }

        if (pathInfo.startsWith("/edit/")) {
            // Show edit user form
            String userIdStr = pathInfo.substring("/edit/".length());
            try {
                int userId = Integer.parseInt(userIdStr);
                User selectedUser = userDAO.getUserById(userId);

                if (selectedUser == null) {
                    request.setAttribute("error", "User not found");
                    request.getRequestDispatcher("/WEB-INF/views/admin/manage-users/user-update.jsp").forward(request, response);
                    return;
                }

                request.setAttribute("selectedUser", selectedUser);
                request.getRequestDispatcher("/WEB-INF/views/admin/manage-users/user-update.jsp").forward(request, response);
                return;
            } catch (NumberFormatException e) {
                request.setAttribute("selectedUser", null);
                request.getRequestDispatcher("/WEB-INF/views/admin/manage-users/user-update.jsp").forward(request, response);
                return;
            }
        }
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
        String action = request.getParameter("action") != null ? request.getParameter("action") : "";
        switch (action) {
            case "sendEmail":
                // Send email to a user
                handleSendEmail(request, response);
                return;
            case "addUser":
                // Send email to a user
                handleAddUser(request, response);
                return;
            case "updateUser":
                // Send email to a user
                handleUpdateUser(request, response);
                return;
            case "deleteUser":
                // Send email to a user
                handleDeleteUser(request, response);
                return;
            case "toggleStatus":
                // Send email to a user
                handleToggleUserStatus(request, response);
                return;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Action can't be done!");
        }

    }

    /**
     * Handle adding a new user
     */
    private void handleAddUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get form data
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String hashedPassword = PasswordEncrypt.encryptSHA256(password);

        // Basic validation
        if (username == null || username.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/users/add?error=missing_fields");
            return;
        }
        // Validation flags
        boolean hasError = false;

        // Validate username
        if (!Validator.isValidUsername(username)) {
            request.setAttribute("usernameError", "Username must be 3-20 characters and contain only letters, numbers, and underscores");
            hasError = true;
        } else if (userDAO.usernameExists(username)) {
            request.setAttribute("usernameError", "Username already exists");
            hasError = true;
        }
        
        // Validate fullName
        if (!Validator.isValidFullname(fullName)) {
            request.setAttribute("fullnameError", "Full name must be 3–50 characters and only contain letters and spaces.");
            hasError = true;
        }

        // Validate email
        if (!Validator.isValidEmail(email)) {
            request.setAttribute("emailError", "Please enter a valid email address");
            hasError = true;
        } else if (userDAO.emailExists(email)) {
            request.setAttribute("emailError", "Email already exists");
            hasError = true;
        }
        
        // Validate password
        if (!Validator.isValidPassword(password)) {
            request.setAttribute("passwordError", "Password must be at least 6 characters");
            hasError = true;
        }

        // Validate phone (optional)
        if (phone != null && !phone.trim().isEmpty() && !Validator.isValidPhone(phone)) {
            request.setAttribute("phoneError", "Please enter a valid phone number (10-11 digits)");
            hasError = true;
        }

        // If validation failed, return to form with input preserved
        if (hasError) {
            request.setAttribute("username", username);
            request.setAttribute("email", email);
            request.setAttribute("fullname", fullName);
            request.setAttribute("phone", phone);
            request.setAttribute("address", address);
            request.setAttribute("role", role); // giữ lại lựa chọn vai trò

            request.getRequestDispatcher("/WEB-INF/views/admin/manage-users/user-add.jsp").forward(request, response);
            return;
        }

        // Create user object
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(hashedPassword); // In reality, you would encrypt this password
        newUser.setRole(role);
        newUser.setActive(true);
        newUser.setFullName(fullName);
        newUser.setPhone(phone);
        newUser.setAddress(address);

        // Insert user
        int userId = userDAO.insertUser(newUser);

        if (userId > 0) {
            response.sendRedirect(request.getContextPath() + "/admin/users?success=added");
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/users/add?error=insert_failed");
        }
    }

    /**
     * Handle updating an existing user
     */
    private void handleUpdateUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get form data       
        String userIdStr = request.getParameter("userId");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String role = request.getParameter("role");
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String newPassword = request.getParameter("newPassword");
        int userId = Integer.parseInt(userIdStr);
        User existingUser = userDAO.getUserById(userId);

        // Basic validation
        if (userIdStr == null || username == null || username.trim().isEmpty()
                || email == null || email.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/users?error=missing_fields");
            return;
        }

        // Validation flags
        boolean hasError = false;

        // Validate username
        if (!Validator.isValidUsername(username)) {
            request.setAttribute("usernameError", "Username must be 3-20 characters and contain only letters, numbers, and underscores");
            hasError = true;
        }

        // Validate fullName
        if (!Validator.isValidFullname(fullName)) {
            request.setAttribute("fullnameError", "Full name must be 3–50 characters and only contain letters and spaces.");
            hasError = true;
        }

        // Validate email
        if (!Validator.isValidEmail(email)) {
            request.setAttribute("emailError", "Please enter a valid email address");
            hasError = true;
        }
        // Validate password
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            if (!Validator.isValidPassword(newPassword)) {
                request.setAttribute("passwordError", "Password must be at least 6 characters");
                hasError = true;
            }
        }

        // Validate phone (optional)
        if (phone != null && !phone.trim().isEmpty() && !Validator.isValidPhone(phone)) {
            request.setAttribute("phoneError", "Please enter a valid phone number (10-11 digits)");
            hasError = true;
        }

        // If validation failed, return to form with input preserved
        if (hasError) {
            request.setAttribute("selectedUser", existingUser);
            request.setAttribute("username", username);
            request.setAttribute("email", email);
            request.setAttribute("fullname", fullName);
            request.setAttribute("phone", phone);
            request.setAttribute("address", address);
            request.setAttribute("role", role); // giữ lại lựa chọn vai trò

            request.getRequestDispatcher("/WEB-INF/views/admin/manage-users/user-update.jsp").forward(request, response);
            return;
        }

        try {
            if (existingUser == null) {
                response.sendRedirect(request.getContextPath() + "/admin/users?error=user_not_found");
                return;
            }

            // Check if username or email already exists (and it's not this user's)
            User checkUser = userDAO.getUserByUsername(username);
            if (checkUser != null && checkUser.getUserID() != userId) {
                response.sendRedirect(
                        request.getContextPath() + "/admin/users/edit/" + userId + "?error=username_exists");
                return;
            }

            checkUser = userDAO.getUserByEmail(email);
            if (checkUser != null && checkUser.getUserID() != userId) {
                response.sendRedirect(request.getContextPath() + "/admin/users/edit/" + userId + "?error=email_exists");
                return;
            }

            // Update user object
            existingUser.setUsername(username);
            existingUser.setEmail(email);
            existingUser.setRole(role);
            existingUser.setFullName(fullName);
            existingUser.setPhone(phone);
            existingUser.setAddress(address);

            // Update user
            boolean updated = userDAO.updateUser(existingUser);

            // Update password if provided
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                userDAO.changePassword(userId, PasswordEncrypt.encryptSHA256(newPassword)); // In reality, you would encrypt this password
            }

            if (updated) {
                response.sendRedirect(request.getContextPath() + "/admin/users?success=updated");
            } else {
                response.sendRedirect(
                        request.getContextPath() + "/admin/users/edit/" + userId + "?error=update_failed");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/users?error=invalid_id");
        }
    }

    /**
     * Handle deleting a user
     */
    private void handleDeleteUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userIdStr = request.getParameter("userId");

        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/users?error=missing_id");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdStr);

            // Check if user exists
            User existingUser = userDAO.getUserById(userId);
            if (existingUser == null) {
                response.sendRedirect(request.getContextPath() + "/admin/users?error=user_not_found");
                return;
            }

            // Check if trying to deleteUser themselves
            User adminUser = (User) request.getSession().getAttribute("user");
            if (adminUser != null && adminUser.getUserID() == userId) {
                response.sendRedirect(request.getContextPath() + "/admin/users?error=cannot_delete_self");
                return;
            }

            // Delete user
            boolean deleted = userDAO.deleteUser(userId);

            if (deleted) {
                response.sendRedirect(request.getContextPath() + "/admin/users?success=deleted");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/users?error=delete_failed");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/users?error=invalid_id");
        }
    }

    /**
     * Handle toggling user active status
     */
    private void handleToggleUserStatus(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userIdStr = request.getParameter("userId");

        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/users?error=missing_id");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdStr);

            // Check if user exists
            User existingUser = userDAO.getUserById(userId);
            if (existingUser == null) {
                response.sendRedirect(request.getContextPath() + "/admin/users?error=user_not_found");
                return;
            }

            // Check if trying to deactivate themselves
            User adminUser = (User) request.getSession().getAttribute("user");
            if (adminUser != null && adminUser.getUserID() == userId) {
                response.sendRedirect(request.getContextPath() + "/admin/users?error=cannot_deactivate_self");
                return;
            }

            // Toggle status
            existingUser.setActive(!existingUser.isActive());
            boolean updated = userDAO.updateUser(existingUser);

            if (updated) {
                response.sendRedirect(request.getContextPath() + "/admin/users?success=status_toggled");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/users?error=toggle_failed");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/users?error=invalid_id");
        }
    }

    /**
     * Handle sending email to a user
     */
    private void handleSendEmail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get form data
        String recipient = request.getParameter("recipient");
        String subject = request.getParameter("subject");
        String message = request.getParameter("message");

        // Basic validation
        if (!Validator.isValidEmail(recipient) || !Validator.isNotEmpty(subject) || !Validator.isNotEmpty(message)) {
            response.sendRedirect(request.getContextPath() + "/admin/users?error=email_failed");
            return;
        }

        try {
            // Get the admin user info for the email footer
            User adminUser = (User) request.getSession().getAttribute("user");
            String senderName = adminUser != null ? adminUser.getFullName() : "Admin";

            // Format HTML message
            String htmlMessage = formatHtmlEmail(message, senderName);

            // Send email using EmailService
            EmailService.sendEmail(recipient, subject, htmlMessage);

            // Redirect back to users page with success message
            response.sendRedirect(request.getContextPath() + "/admin/users?success=email_sent");
        } catch (Exception e) {
            // Log the error
            System.err.println("Error sending email: " + e.getMessage());
            e.printStackTrace();

            // Redirect with error message
            response.sendRedirect(request.getContextPath() + "/admin/users?error=email_failed");
        }
    }

    /**
     * Format plain text message as HTML email
     *
     * @param message Plain text message
     * @param senderName Name of the sender
     * @return HTML formatted message
     */
    private String formatHtmlEmail(String message, String senderName) {
        return "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<style>"
                + "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; }"
                + ".email-container { border: 1px solid #e0e0e0; border-radius: 5px; padding: 20px; }"
                + ".header { border-bottom: 1px solid #e0e0e0; padding-bottom: 10px; margin-bottom: 20px; }"
                + ".footer { border-top: 1px solid #e0e0e0; margin-top: 30px; padding-top: 10px; font-size: 12px; color: #888; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='email-container'>"
                + "<div class='header'><strong>Message from LightHouse Admin</strong></div>"
                + "<div class='content'>" + message.replace("\n", "<br>") + "</div>"
                + "<div class='footer'>This email was sent by " + senderName + " from the LightHouse Admin Panel."
                + "<br>Please do not reply to this email.</div>"
                + "</div>"
                + "</body>"
                + "</html>";
    }

    @Override
    public String getServletInfo() {
        return "Admin User Servlet - Manages user accounts";
    }

}
