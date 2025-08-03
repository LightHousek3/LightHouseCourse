/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.customer;

import dao.CategoryDAO;
import dao.CustomerDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import model.Category;
import model.Customer;
import util.PasswordEncrypt;
import util.Validator;

@WebServlet(name = "CustomerProfileServlet", urlPatterns = {"/profile"})
public class CustomerProfileServlet extends HttpServlet {

    private CustomerDAO customerDAO;
    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        customerDAO = new CustomerDAO();
        categoryDAO = new CategoryDAO();
    }

    /**
     * Handles the HTTP GET request - displaying the user profile page.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Get user from session
        Customer user = (Customer) session.getAttribute("user");

        // Get fresh user data from database
        Customer freshUserData = customerDAO.getCustomerById(user.getCustomerID());
        if (freshUserData != null) {
            request.setAttribute("user", freshUserData);
        } else {
            request.setAttribute("user", user); // Fallback to session user
        }
        
        // Get categories for sidebar
        List<Category> categories = categoryDAO.getAllCategories();
        request.setAttribute("categories", categories);

        // Forward to profile JSP
        request.getRequestDispatcher("/WEB-INF/views/customer/manage-profile/view-profile.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP POST request - updating user profile.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check if user is logged in
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Get current user from session
        Customer currentUser = (Customer) session.getAttribute("user");
        String action = request.getParameter("action");

        // Validation flags
        boolean hasError = false;

        // Update profile
        if ("updateProfile".equals(action)) {
            // Get form data
            String email = request.getParameter("email");
            String fullName = request.getParameter("fullName");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");

            //Avatar keep it if not changed
            String avatar = request.getParameter("avatar");
            if (avatar == null || avatar.trim().isEmpty()) {
                avatar = currentUser.getAvatar();
            }

            // Password remains the same
            String password = currentUser.getPassword();

            // Create a user object with updated info
            Customer updatedCustomer = new Customer();
            updatedCustomer.setCustomerID(currentUser.getCustomerID());
            updatedCustomer.setUsername(currentUser.getUsername());
            updatedCustomer.setEmail(email);
            updatedCustomer.setActive(currentUser.isActive());
            updatedCustomer.setFullName(fullName);
            updatedCustomer.setPhone(phone);
            updatedCustomer.setAddress(address);
            updatedCustomer.setAuthProvider(currentUser.getAuthProvider());
            updatedCustomer.setAuthProviderId(currentUser.getAuthProviderId());
            updatedCustomer.setAvatar(avatar);
            updatedCustomer.setPassword(password);

            // Validate email
            if (!Validator.isValidEmail(email)) {
                request.setAttribute("emailError", "Please enter a valid email address");
                hasError = true;
            } else if (!email.equals(currentUser.getEmail()) && customerDAO.emailExists(email)) {
                request.setAttribute("emailError", "Email already exists");
                hasError = true;
            }
            // Validate phone
            if (phone == null || phone.trim().isEmpty()) {
                request.setAttribute("phoneError", "Phone number cannot be empty."); //Must not be empty
                hasError = true;
            } else if (!Validator.isValidPhone(phone)) {
                request.setAttribute("phoneError", "Phone number must contain 10 or 11 digits."); //Must contain 10 or 11 numeric digits only
                hasError = true;
            }
            // Validate fullname
            if (!Validator.isValidFullname(fullName)) {
                request.setAttribute("fullNameError", "Invalid full name. Please check the format.");
                hasError = true;
            }
            // Validate address
            if (address == null || address.trim().isEmpty()) {
                request.setAttribute("addressError", "Address cannot be empty");
                hasError = true;
            }

            if (!hasError) {
                // Update user in database
                boolean updateSuccess = customerDAO.updateCustomer(updatedCustomer);
                if (updateSuccess) {
                    // Update session user
                    session.setAttribute("user", updatedCustomer);
                    request.setAttribute("success", "Your profile has been updated successfully.");
                } else {
                    request.setAttribute("error", "Failed to update profile. Please try again.");
                    request.setAttribute("user", currentUser);
                    request.setAttribute("activeTab", "edit");
                }
                request.getRequestDispatcher("/WEB-INF/views/customer/manage-profile/view-profile.jsp").forward(request, response);
            } else {
                // Forward back to profile page
                request.setAttribute("user", currentUser);
                request.setAttribute("activeTab", "edit");
                request.getRequestDispatcher("/WEB-INF/views/customer/manage-profile/view-profile.jsp").forward(request, response);
            }
            // Change password
        } else if ("changePassword".equals(action)) {
            // Get password change data (if provided)
            String currentPassword = request.getParameter("currentPassword");
            String newPassword = request.getParameter("newPassword");
            String confirmPassword = request.getParameter("confirmPassword");
            // Handle password change if requested
            if (currentPassword == null || newPassword == null || confirmPassword == null
                    || currentPassword.trim().isEmpty() || newPassword.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
                request.setAttribute("error", "All password fields are required");
                hasError = true;
                // User wants to change password
            } else {
                // Verify current password
                String encryptedCurrentPassword = PasswordEncrypt.encryptSHA256(currentPassword);
                if (currentUser.getPassword() == null || !encryptedCurrentPassword.equals(currentUser.getPassword())) {
                    request.setAttribute("passwordError", "Current password is incorrect");
                    hasError = true;
                }

                // Validate new password
                if (newPassword == null || newPassword.trim().isEmpty() || !Validator.isValidPassword(newPassword)) {
                    request.setAttribute("newPasswordError", "New password must be at least 6 characters");
                    hasError = true;
                }

                // Validate password confirmation
                if (confirmPassword == null || !confirmPassword.equals(newPassword)) {
                    request.setAttribute("confirmPasswordError", "Passwords do not match");
                    hasError = true;
                }
            }

            if (!hasError) {
                // Encrypt and set new password
                String encryptedNewPassword = PasswordEncrypt.encryptSHA256(newPassword);
                boolean passwordUpdateSuccess = customerDAO.changePassword(currentUser.getCustomerID(), encryptedNewPassword);
                // Update password               
                if (passwordUpdateSuccess) {
                    currentUser.setPassword(encryptedNewPassword);
                    session.setAttribute("user", currentUser);
                    request.setAttribute("passwordSuccess", "Password updated successfully");
                } else {
                    request.setAttribute("error", "Failed to update password. Please try again.");
                    request.setAttribute("user", currentUser);
                    request.setAttribute("activeTab", "password");
                }
            }
            request.setAttribute("user", currentUser);
            request.setAttribute("activeTab", "password");
            request.getRequestDispatcher("/WEB-INF/views/customer/manage-profile/view-profile.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/profile");
        }
    }

    @Override
    public String getServletInfo() {
        return "Profile Servlet - Handles user profile display and updates";
    }
}
