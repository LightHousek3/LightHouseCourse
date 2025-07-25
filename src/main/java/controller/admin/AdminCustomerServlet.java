/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.admin;

import dao.CustomerDAO;
import dao.SuperUserDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import model.Customer;
import model.SuperUser;
import service.EmailService;
import util.PasswordEncrypt;
import util.Validator;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;
import javax.servlet.annotation.MultipartConfig;
import java.util.Arrays;

/**
 * Admin servlet for managing Customer accounts.
 *
 * @author DangPH - CE180896
 */
@WebServlet(name = "AdminCustomerServlet", urlPatterns = {"/admin/customers", "/admin/customers/*"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1 MB
        maxFileSize = 5 * 1024 * 1024, // 5 MB
        maxRequestSize = 10 * 1024 * 1024 // 10 MB
)
public class AdminCustomerServlet extends HttpServlet {

    private CustomerDAO customerDAO;
    private SuperUserDAO superUserDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        customerDAO = new CustomerDAO();
        superUserDAO = new SuperUserDAO();
    }

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

        if (pathInfo == null || pathInfo.equals("/")) {
            // List all customers
            List<Customer> customers = customerDAO.getAllCustomers();
            request.setAttribute("customers", customers);
            request.getRequestDispatcher("/WEB-INF/views/admin/manage-customers/customer-list.jsp").forward(request,
                    response);
            return;
        }

        if (pathInfo.startsWith("/view/")) {
            // Show customer details
            String userIdStr = pathInfo.substring("/view/".length());
            try {
                int userId = Integer.parseInt(userIdStr);
                Customer selectedCustomer = customerDAO.getCustomerById(userId);
                request.setAttribute("selectedCustomer", selectedCustomer);
                request.getRequestDispatcher("/WEB-INF/views/admin/manage-customers/customer-detail.jsp").forward(
                        request,
                        response);
                return;
            } catch (NumberFormatException e) {
                request.setAttribute("selectedCustomer", null);
                request.getRequestDispatcher("/WEB-INF/views/admin/manage-customers/customer-detail.jsp").forward(
                        request,
                        response);
                return;
            }
        }

        if (pathInfo.equals("/add")) {
            // Show add customer form
            request.getRequestDispatcher("/WEB-INF/views/admin/manage-customers/customer-add.jsp").forward(request,
                    response);
            return;
        }

        if (pathInfo.startsWith("/edit/")) {
            // Show edit customer form
            String userIdStr = pathInfo.substring("/edit/".length());
            try {
                int userId = Integer.parseInt(userIdStr);
                Customer selectedCustomer = customerDAO.getCustomerById(userId);

                if (selectedCustomer == null) {
                    request.setAttribute("error", "Customer not found");
                    request.getRequestDispatcher("/WEB-INF/views/admin/manage-customers/customer-update.jsp").forward(
                            request,
                            response);
                    return;
                }

                request.setAttribute("selectedCustomer", selectedCustomer);
                request.getRequestDispatcher("/WEB-INF/views/admin/manage-customers/customer-update.jsp").forward(
                        request,
                        response);
                return;
            } catch (NumberFormatException e) {
                request.setAttribute("selectedCustomer", null);
                request.getRequestDispatcher("/WEB-INF/views/admin/manage-customers/customer-update.jsp").forward(
                        request,
                        response);
                return;
            }
        }

        // Not found
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
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
                handleSendEmail(request, response);
                return;
            case "addCustomer":
                handleAddCustomer(request, response);
                return;
            case "updateCustomer":
                handleUpdateCustomer(request, response);
                return;
            case "deleteCustomer":
                handleDeleteCustomer(request, response);
                return;
            case "toggleStatus":
                handleToggleCustomerStatus(request, response);
                return;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Action can't be done!");
        }
    }

    /**
     * Handle adding a new customer
     */
    private void handleAddCustomer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get form data
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String avatarUrl = request.getParameter("avatarUrl");
        String hashedPassword = PasswordEncrypt.encryptSHA256(password);

        // Basic validation
        if (username == null || username.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/customers/add?error=missing_fields");
            return;
        }
        // Validation flags
        boolean hasError = false;

        // Validate username
        if (!Validator.isValidUsername(username)) {
            request.setAttribute("usernameError",
                    "Username must be 3-20 characters and contain only letters, numbers, and underscores");
            hasError = true;
        } else if (customerDAO.usernameExists(username) || superUserDAO.usernameExists(username)) {
            request.setAttribute("usernameError", "Username already exists");
            hasError = true;
        }

        // Validate fullName
        if (!Validator.isValidFullname(fullName)) {
            request.setAttribute("fullnameError",
                    "Full name must be 3–50 characters and only contain letters and spaces.");
            hasError = true;
        }

        // Validate email
        if (!Validator.isValidEmail(email)) {
            request.setAttribute("emailError", "Please enter a valid email address");
            hasError = true;
        } else if (customerDAO.emailExists(email) || superUserDAO.emailExists(email)) {
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
            request.setAttribute("password", password);
            request.setAttribute("avatarUrl", avatarUrl);

            request.getRequestDispatcher("/WEB-INF/views/admin/manage-customers/customer-add.jsp").forward(request,
                    response);
            return;
        }

        // Check if avatarUrl is a base64 image
        if (avatarUrl != null && avatarUrl.startsWith("data:image")) {
            String fileName = saveAvatarImage(avatarUrl, "customer");
            if (fileName != null) {
                avatarUrl = "/assets/imgs/avatars/" + fileName;
            } else {
                // Set default avatar if upload fails
                avatarUrl = "/assets/imgs/avatars/default-user.png";
            }
        } else {
            // Set default avatar
            avatarUrl = "/assets/imgs/avatars/default-user.png";
        }

        // Create a new Customer
        Customer newCustomer = new Customer();
        newCustomer.setUsername(username);
        newCustomer.setEmail(email);
        newCustomer.setPassword(hashedPassword);
        newCustomer.setActive(true);
        newCustomer.setFullName(fullName);
        newCustomer.setPhone(phone);
        newCustomer.setAddress(address);
        newCustomer.setAuthProvider("local");
        // Set avatar
        newCustomer.setAvatar(avatarUrl);

        // Insert Customer
        int userId = customerDAO.insertCustomer(newCustomer);

        if (userId > 0) {
            response.sendRedirect(request.getContextPath() + "/admin/customers?success=added");
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/customers/add?error=insert_failed");
        }
    }

    /**
     * Handle updating an existing customer
     */
    private void handleUpdateCustomer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get form data
        String customerIdStr = request.getParameter("customerId");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String password = request.getParameter("password");
        String avatarUrl = request.getParameter("avatarUrl");
        int customerId;
        Customer existingCustomer;

        // Basic validation
        try {
            customerId = Integer.parseInt(customerIdStr);
            existingCustomer = customerDAO.getCustomerById(customerId);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/customers?error=invalid_id");
            return;
        }

        // Validation flags
        boolean hasError = false;

        // Validate username
        if (!Validator.isValidUsername(username)) {
            request.setAttribute("usernameError",
                    "Username must be 3-20 characters and contain only letters, numbers, and underscores");
            hasError = true;
        }

        // Validate fullName
        if (!Validator.isValidFullname(fullName)) {
            request.setAttribute("fullnameError",
                    "Full name must be 3–50 characters and only contain letters and spaces.");
            hasError = true;
        }

        // Validate email
        if (!Validator.isValidEmail(email)) {
            request.setAttribute("emailError", "Please enter a valid email address");
            hasError = true;
        }

        // Validate password
        if (!Validator.isValidPassword(password)) {
            request.setAttribute("passwordError", "Password must be at least 6 characters");
            hasError = true;
        }

        // Validate phone (optional)
        if (!Validator.isValidPhone(phone)) {
            request.setAttribute("phoneError", "Please enter a valid phone number (10-11 digits)");
            hasError = true;
        }

        // If validation failed, return to form with input preserved
        if (hasError) {
            existingCustomer.setUsername(username);
            existingCustomer.setPassword(password);
            existingCustomer.setEmail(email);
            existingCustomer.setFullName(fullName);
            existingCustomer.setPhone(phone);
            existingCustomer.setAddress(address);
            existingCustomer.setAvatar(avatarUrl);
            request.setAttribute("selectedCustomer", existingCustomer);

            request.getRequestDispatcher("/WEB-INF/views/admin/manage-customers/customer-update.jsp").forward(request,
                    response);
            return;
        }

        try {
            // Make sure we're dealing with a valid customer
            if (existingCustomer == null) {
                response.sendRedirect(request.getContextPath() + "/admin/customers?error=invalid_id");
                return;
            }

            // Check whether customer has change password
            if (!existingCustomer.getPassword().equals(password)) {
                // Encrypt password
                String encyptedPassword = PasswordEncrypt.encryptSHA256(password);
                existingCustomer.setPassword(encyptedPassword);
            }

            existingCustomer.setUsername(username);
            existingCustomer.setEmail(email);
            existingCustomer.setFullName(fullName);
            existingCustomer.setPhone(phone);
            existingCustomer.setAddress(address);

            // Check if avatarUrl is a base64 image
            if (avatarUrl != null && avatarUrl.startsWith("data:image")) {
                String fileName = saveAvatarImage(avatarUrl, "customer");
                if (fileName != null) {
                    avatarUrl = "/assets/imgs/avatars/" + fileName;
                    existingCustomer.setAvatar(avatarUrl);
                }
            }

            boolean updated = customerDAO.updateCustomer(existingCustomer);

            if (updated) {
                response.sendRedirect(request.getContextPath() + "/admin/customers?success=updated");
            } else {
                response.sendRedirect(
                        request.getContextPath() + "/admin/customers/edit/" + customerId + "?error=update_failed");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/customers?error=invalid_id");
        }
    }

    /**
     * Handle deleting a customer
     */
    private void handleDeleteCustomer(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String customerIdStr = request.getParameter("customerId");

        if (customerIdStr == null || customerIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/customers?error=invalid_id");
            return;
        }

        try {
            int customerId = Integer.parseInt(customerIdStr);

            // Check if customer exists
            Customer existingCustomer = customerDAO.getCustomerById(customerId);
            if (existingCustomer == null) {
                response.sendRedirect(request.getContextPath() + "/admin/customers?error=invalid_id");
                return;
            }

            // Delete customer
            boolean deleted = customerDAO.deleteCustomer(customerId);

            if (deleted) {
                response.sendRedirect(request.getContextPath() + "/admin/customers?success=deleted");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/customers?error=delete_failed");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/customers?error=invalid_id");
        }
    }

    /**
     * Handle toggling customer active status
     */
    private void handleToggleCustomerStatus(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String customerIdStr = request.getParameter("customerId");
        if (customerIdStr == null || customerIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/customers?error=missing_id");
            return;
        }

        try {
            int customerId = Integer.parseInt(customerIdStr);

            // Check if customer exists
            Customer existingCustomer = customerDAO.getCustomerById(customerId);
            if (existingCustomer == null) {
                response.sendRedirect(request.getContextPath() + "/admin/customers?error=customer_not_found");
                return;
            }

            // Toggle active status
            existingCustomer.setActive(!existingCustomer.isActive());
            boolean updated = customerDAO.updateCustomer(existingCustomer);

            if (updated) {
                response.sendRedirect(request.getContextPath() + "/admin/customers?success=status_changed");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/customers?error=status_change_failed");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/customers?error=invalid_id");
        }
    }

    /**
     * Handle sending email to a customer
     */
    private void handleSendEmail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get form data
        String recipient = request.getParameter("recipient");
        String subject = request.getParameter("subject");
        String message = request.getParameter("message");

        // Basic validation
        if (!Validator.isValidEmail(recipient) || !Validator.isNotEmpty(subject) || !Validator.isNotEmpty(message)) {
            response.sendRedirect(request.getContextPath() + "/admin/customers?error=email_failed");
            return;
        }

        try {
            // Get the admin user info for the email footer
            HttpSession session = request.getSession();
            SuperUser adminUser;
            try {
                adminUser = (SuperUser) session.getAttribute("user");
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            String senderName = adminUser != null ? adminUser.getFullName() : "Admin";

            // Format HTML message
            String htmlMessage = formatHtmlEmail(message, senderName);

            // Send email using EmailService
            EmailService.sendEmail(recipient, subject, htmlMessage);

            // Redirect back to customers page with success message
            response.sendRedirect(request.getContextPath() + "/admin/customers?success=email_sent");
        } catch (Exception e) {
            // Log the error
            System.err.println("Error sending email: " + e.getMessage());
            e.printStackTrace();

            // Redirect with error message
            response.sendRedirect(request.getContextPath() + "/admin/customers?error=email_failed");
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

    /**
     * Save a base64 encoded image to the avatar directory
     *
     * @param base64Image The base64 encoded image data
     * @param prefix Prefix for the filename
     * @return The saved filename or null if there was an error
     */
    private String saveAvatarImage(String base64Image, String prefix) {
        try {
            // Extract image data from the base64 string
            String[] parts = base64Image.split(",");
            if (parts.length < 2) {
                return null;
            }

            // Get image format
            String imageType = parts[0];
            String extension = "png";
            if (imageType.contains("jpeg")) {
                extension = "jpg";
            } else if (imageType.contains("gif")) {
                extension = "gif";
            }

            // Decode the base64 data
            byte[] imageBytes = Base64.getDecoder().decode(parts[1]);

            // Get real path of the web application
            String applicationPath = getServletContext().getRealPath("");
            String avatarDirPath = applicationPath + File.separator + "assets"
                    + File.separator + "imgs" + File.separator + "avatars";

            // Ensure directory exists
            File avatarDir = new File(avatarDirPath);
            if (!avatarDir.exists()) {
                avatarDir.mkdirs();
            }

            // Check if an identical image already exists
            File[] existingFiles = avatarDir.listFiles();
            if (existingFiles != null) {
                for (File file : existingFiles) {
                    if (file.isFile()) {
                        byte[] existingImageBytes = Files.readAllBytes(file.toPath());
                        if (Arrays.equals(imageBytes, existingImageBytes)) {
                            // Return the name of the existing file
                            return file.getName();
                        }
                    }
                }
            }

            // Create a unique filename if no identical image was found
            String fileName = prefix + "-" + UUID.randomUUID().toString() + "." + extension;
            String filePath = avatarDirPath + File.separator + fileName;

            // Save the file
            Files.write(Paths.get(filePath), imageBytes);

            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getServletInfo() {
        return "Admin Customer Servlet - Manages customer accounts";
    }
}
