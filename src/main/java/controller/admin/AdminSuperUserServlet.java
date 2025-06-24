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
import java.util.List;
import model.SuperUser;
import service.EmailService;
import util.PasswordEncrypt;
import util.Validator;
import model.Instructor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Part;

/**
 * Admin servlet for managing SuperUsers (admins and instructors).
 *
 * @author DangPH - CE180896
 */
@WebServlet(name = "AdminSuperUserServlet", urlPatterns = { "/admin/superusers", "/admin/superusers/*" })
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1 MB
    maxFileSize = 5 * 1024 * 1024,   // 5 MB
    maxRequestSize = 10 * 1024 * 1024 // 10 MB
)
public class AdminSuperUserServlet extends HttpServlet {

    private CustomerDAO customerDAO;
    private SuperUserDAO superUserDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        customerDAO = new CustomerDAO();
        superUserDAO = new SuperUserDAO();
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the
    // + sign on the left to edit the code.">
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
        String pathInfo = request.getPathInfo();
        String role = request.getParameter("role");

        if (pathInfo == null || pathInfo.equals("/")) {
            // List all users with optional role filter
            List<SuperUser> users;
            if (role != null && !role.isEmpty()) {
                users = superUserDAO.getSuperUsersByRole(role);
            } else {
                // By default, show all superusers (admins and instructors)
                users = superUserDAO.getAllSuperUsers();
            }
            request.setAttribute("users", users);
            request.setAttribute("selectedRole", role);
            request.getRequestDispatcher("/WEB-INF/views/admin/manage-superusers/superuser-list.jsp").forward(request,
                    response);
            return;
        }

        if (pathInfo.startsWith("/view/")) {
            // Show superuser details
            String userIdStr = pathInfo.substring("/view/".length());
            try {
                int userId = Integer.parseInt(userIdStr);
                SuperUser selectedUser = superUserDAO.getSuperUserById(userId);
                request.setAttribute("selectedUser", selectedUser);

                // If role is instructor, get instructor details
                if (selectedUser != null && "instructor".equals(selectedUser.getRole())) {
                    // Here you would get instructor details
                    // Instructor instructor = instructorDAO.getInstructorBySuperUserId(userId);
                    // request.setAttribute("instructor", instructor);

                    // For demonstration, create a sample instructor object
                    Instructor instructor = new Instructor();
                    instructor.setInstructorID(1);
                    instructor.setSuperUserID(userId);
                    instructor.setBiography(
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed commodo lobortis mauris, non efficitur eros pharetra eget.");
                    instructor.setSpecialization("Web Development, Java Programming");
                    request.setAttribute("instructor", instructor);
                }

                request.getRequestDispatcher("/WEB-INF/views/admin/manage-superusers/superuser-detail.jsp").forward(
                        request,
                        response);
                return;
            } catch (NumberFormatException e) {
                request.setAttribute("selectedUser", null);
                request.getRequestDispatcher("/WEB-INF/views/admin/manage-superusers/superuser-detail.jsp").forward(
                        request,
                        response);
                return;
            }
        }

        if (pathInfo.equals("/add")) {
            // Show add superuser form
            request.getRequestDispatcher("/WEB-INF/views/admin/manage-superusers/superuser-add.jsp").forward(request,
                    response);
            return;
        }

        if (pathInfo.startsWith("/edit/")) {
            // Show edit superuser form
            String userIdStr = pathInfo.substring("/edit/".length());
            try {
                int userId = Integer.parseInt(userIdStr);
                SuperUser selectedUser = superUserDAO.getSuperUserById(userId);

                if (selectedUser == null) {
                    request.setAttribute("error", "SuperUser not found");
                    request.getRequestDispatcher("/WEB-INF/views/admin/manage-superusers/superuser-update.jsp").forward(
                            request,
                            response);
                    return;
                }

                request.setAttribute("selectedUser", selectedUser);

                // If role is instructor, get instructor details
                if ("instructor".equals(selectedUser.getRole())) {
                    // Here you would get instructor details
                    // Instructor instructor = instructorDAO.getInstructorBySuperUserId(userId);
                    // request.setAttribute("biography", instructor.getBiography());
                    // request.setAttribute("specialization", instructor.getSpecialization());

                    // For demonstration, set sample instructor data
                    request.setAttribute("biography",
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed commodo lobortis mauris, non efficitur eros pharetra eget.");
                    request.setAttribute("specialization", "Web Development, Java Programming");
                }

                request.getRequestDispatcher("/WEB-INF/views/admin/manage-superusers/superuser-update.jsp").forward(
                        request,
                        response);
                return;
            } catch (NumberFormatException e) {
                request.setAttribute("selectedUser", null);
                request.getRequestDispatcher("/WEB-INF/views/admin/manage-superusers/superuser-update.jsp").forward(
                        request,
                        response);
                return;
            }
        }
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
        String action = request.getParameter("action") != null ? request.getParameter("action") : "";
        switch (action) {
            case "sendEmail":
                // Send email to a user
                handleSendEmail(request, response);
                return;
            case "addUser":
                // Add a new superuser
                handleAddUser(request, response);
                return;
            case "updateUser":
                // Update an existing superuser
                handleUpdateUser(request, response);
                return;
            case "deleteUser":
                // Delete a superuser
                handleDeleteUser(request, response);
                return;
            case "toggleStatus":
                // Toggle superuser status
                handleToggleUserStatus(request, response);
                return;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Action can't be done!");
        }

    }

    /**
     * Handle adding a new superuser
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
        String avatarUrl = request.getParameter("avatarUrl");

        // Instructor specific fields
        String biography = request.getParameter("biography");
        String specialization = request.getParameter("specialization");

        String hashedPassword = PasswordEncrypt.encryptSHA256(password);

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

        // Validate role - must be either admin or instructor for SuperUsers
        if (role == null || (!role.equals("admin") && !role.equals("instructor"))) {
            request.setAttribute("roleError", "Role must be either admin or instructor");
            hasError = true;
        }

        // Validate instructor fields if role is instructor
        if (role != null && role.equals("instructor")) {
            if (biography == null || biography.trim().isEmpty()) {
                request.setAttribute("biographyError", "Biography is required for instructors");
                hasError = true;
            }

            if (specialization == null || specialization.trim().isEmpty()) {
                request.setAttribute("specializationError", "Specialization is required for instructors");
                hasError = true;
            }
        }

        // If validation failed, return to form with input preserved
        if (hasError) {
            request.setAttribute("username", username);
            request.setAttribute("password", password);
            request.setAttribute("email", email);
            request.setAttribute("fullname", fullName);
            request.setAttribute("phone", phone);
            request.setAttribute("address", address);
            request.setAttribute("role", role);
            request.setAttribute("avatarUrl", avatarUrl);
            request.setAttribute("biography", biography);
            request.setAttribute("specialization", specialization);

            request.getRequestDispatcher("/WEB-INF/views/admin/manage-superusers/superuser-add.jsp").forward(request,
                    response);
            return;
        }

        int userId = -1;

        // Create a SuperUser since this servlet is dedicated to managing SuperUsers
        SuperUser newSuperUser = new SuperUser();
        newSuperUser.setUsername(username);
        newSuperUser.setEmail(email);
        newSuperUser.setPassword(hashedPassword);
        newSuperUser.setRole(role);
        newSuperUser.setActive(true);
        newSuperUser.setFullName(fullName);
        newSuperUser.setPhone(phone);
        newSuperUser.setAddress(address);

        // Set avatar URL
        if (avatarUrl != null && avatarUrl.startsWith("data:image")) {
            String fileName = saveAvatarImage(avatarUrl, "superuser");
            if (fileName != null) {
                avatarUrl = "/assets/imgs/avatars/" + fileName;
            } else {
                // Set default avatar based on role if upload fails
                avatarUrl = role.equals("admin") ? "/assets/imgs/avatars/default-admin.png" 
                                                : "/assets/imgs/avatars/default-instructor.png";
            }
        } else {
            // Set default avatar based on role
            avatarUrl = role.equals("admin") ? "/assets/imgs/avatars/default-admin.png" 
                                            : "/assets/imgs/avatars/default-instructor.png";
        }
        newSuperUser.setAvatar(avatarUrl);

        // Insert SuperUser
        userId = superUserDAO.insertSuperUser(newSuperUser);

        // If user is an instructor, create the instructor record
        if (userId > 0 && role.equals("instructor")) {
            Instructor instructor = new Instructor();
            instructor.setSuperUserID(userId);
            instructor.setBiography(biography);
            instructor.setSpecialization(specialization);
            instructor.setApprovalDate(new java.util.Date()); // Set current date as approval date

            // Here you would call a DAO method to insert the instructor
            // instructorDAO.insertInstructor(instructor);
        }

        if (userId > 0) {
            response.sendRedirect(request.getContextPath() + "/admin/superusers?success=added");
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/superusers/add?error=insert_failed");
        }
    }

    /**
     * Handle updating an existing superuser
     */
    private void handleUpdateUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get form data
        String superUserIdStr = request.getParameter("superUserId");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String role = request.getParameter("role");
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String password = request.getParameter("password");
        String avatarUrl = request.getParameter("avatarUrl");

        // Instructor specific fields
        String biography = request.getParameter("biography");
        String specialization = request.getParameter("specialization");

        int superUserId;
        SuperUser existingSuperUser = null;

        // Basic validation
        try {
            superUserId = Integer.parseInt(superUserIdStr);

            // Get the SuperUser
            existingSuperUser = superUserDAO.getSuperUserById(superUserId);
            if (existingSuperUser == null) {
                response.sendRedirect(request.getContextPath() + "/admin/superusers?error=user_not_found");
                return;
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/superusers?error=invalid_id");
            return;
        }

        // Validation flags
        boolean hasError = false;

        // Validate username
        if (!Validator.isValidUsername(username)) {
            request.setAttribute("usernameError",
                    "Username must be 3-20 characters and contain only letters, numbers, and underscores");
            hasError = true;
        } else if (!username.equals(existingSuperUser.getUsername())
                && (customerDAO.usernameExists(username) || superUserDAO.usernameExists(username))) {
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
        } else if (!email.equals(existingSuperUser.getEmail())
                && (customerDAO.emailExists(email) || superUserDAO.emailExists(email))) {
            request.setAttribute("emailError", "Email already exists");
            hasError = true;
        }

        // Validate password (only if being changed)
        if (password != null && !password.trim().isEmpty() && !password.equals(existingSuperUser.getPassword())) {
            if (!Validator.isValidPassword(password)) {
                request.setAttribute("passwordError", "Password must be at least 6 characters");
                hasError = true;
            }
        }

        // Validate phone (optional)
        if (phone != null && !phone.trim().isEmpty() && !Validator.isValidPhone(phone)) {
            request.setAttribute("phoneError", "Please enter a valid phone number (10-11 digits)");
            hasError = true;
        }

        // Validate role - must be either admin or instructor
        if (role == null || (!role.equals("admin") && !role.equals("instructor"))) {
            request.setAttribute("roleError", "Role must be either admin or instructor");
            hasError = true;
        }

        // Validate instructor fields if role is instructor
        if (role != null && role.equals("instructor")) {
            if (biography == null || biography.trim().isEmpty()) {
                request.setAttribute("biographyError", "Biography is required for instructors");
                hasError = true;
            }

            if (specialization == null || specialization.trim().isEmpty()) {
                request.setAttribute("specializationError", "Specialization is required for instructors");
                hasError = true;
            }
        }

        // If validation failed, return to form with input preserved
        if (hasError) {
            // For form re-display, set the input values as attributes
            request.setAttribute("selectedUser", existingSuperUser); // Original data

            // Override with form inputs for re-display
            existingSuperUser.setUsername(username);
            existingSuperUser.setEmail(email);
            existingSuperUser.setRole(role);
            existingSuperUser.setFullName(fullName);
            existingSuperUser.setPhone(phone);
            existingSuperUser.setAddress(address);

            // Instructor data
            request.setAttribute("biography", biography);
            request.setAttribute("specialization", specialization);

            request.getRequestDispatcher("/WEB-INF/views/admin/manage-superusers/superuser-update.jsp").forward(request,
                    response);
            return;
        }

        try {
            boolean updated = false;

            // Update the SuperUser
            SuperUser superUser = existingSuperUser;
            superUser.setUsername(username);
            superUser.setEmail(email);
            superUser.setRole(role);
            superUser.setFullName(fullName);
            superUser.setPhone(phone);
            superUser.setAddress(address);

            // Update avatar if role has changed
            if (!superUser.getRole().equals(role)) {
                String currentAvatar = superUser.getAvatar();
                if (currentAvatar == null || currentAvatar.contains("default-")) {
                    String defaultAvatar = role.equals("admin") ? "/assets/imgs/avatars/default-admin.png"
                            : "/assets/imgs/avatars/default-instructor.png";
                    superUser.setAvatar(defaultAvatar);
                }
            }

            // Check if avatarUrl is a base64 image
            if (avatarUrl != null && avatarUrl.startsWith("data:image")) {
                String fileName = saveAvatarImage(avatarUrl, "superuser");
                if (fileName != null) {
                    avatarUrl = "/assets/imgs/avatars/" + fileName;
                    superUser.setAvatar(avatarUrl);
                }
            }

            updated = superUserDAO.updateSuperUser(superUser);

            // Update password if provided
            if (updated && password != null && !password.trim().isEmpty()) {
                String hashedPassword = PasswordEncrypt.encryptSHA256(password);
                superUserDAO.changePassword(superUserId, hashedPassword);
            }

            // Handle instructor information
            if (updated && role.equals("instructor")) {
                // Check if instructor record exists
                // Here you would call a method like
                // instructorDAO.getInstructorBySuperUserId(superUserId)
                // Instructor existingInstructor =
                // instructorDAO.getInstructorBySuperUserId(superUserId);

                // If exists, update it
                // if (existingInstructor != null) {
                // existingInstructor.setBiography(biography);
                // existingInstructor.setSpecialization(specialization);
                // instructorDAO.updateInstructor(existingInstructor);
                // } else {
                // // Create new instructor record
                // Instructor newInstructor = new Instructor();
                // newInstructor.setSuperUserID(superUserId);
                // newInstructor.setBiography(biography);
                // newInstructor.setSpecialization(specialization);
                // newInstructor.setApprovalDate(new java.util.Date()); // Set current date as
                // approval date
                // instructorDAO.insertInstructor(newInstructor);
                // }
            }

            if (updated) {
                response.sendRedirect(request.getContextPath() + "/admin/superusers?success=updated");
            } else {
                response.sendRedirect(
                        request.getContextPath() + "/admin/superusers/edit/" + superUserId + "?error=update_failed");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/superusers?error=invalid_id");
        }
    }

    /**
     * Handle deleting a superuser
     */
    private void handleDeleteUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String superUserIdStr = request.getParameter("superUserId");

        if (superUserIdStr == null || superUserIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/superusers?error=missing_id");
            return;
        }

        try {
            int superUserId = Integer.parseInt(superUserIdStr);

            // Check if superuser exists
            SuperUser existingSuperUser = superUserDAO.getSuperUserById(superUserId);
            if (existingSuperUser == null) {
                response.sendRedirect(request.getContextPath() + "/admin/superusers?error=user_not_found");
                return;
            }

            // Delete superuser
            boolean deleted = superUserDAO.deleteSuperUser(superUserId);

            if (deleted) {
                response.sendRedirect(request.getContextPath() + "/admin/superusers?success=deleted");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/superusers?error=delete_failed");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/superusers?error=invalid_id");
        }
    }

    /**
     * Handle toggling superuser status (active/inactive)
     */
    private void handleToggleUserStatus(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String superUserIdStr = request.getParameter("superUserId");
        if (superUserIdStr == null || superUserIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/superusers?error=missing_id");
            return;
        }

        try {
            int superUserId = Integer.parseInt(superUserIdStr);

            // Check if superuser exists
            SuperUser superUser = superUserDAO.getSuperUserById(superUserId);
            if (superUser == null) {
                response.sendRedirect(request.getContextPath() + "/admin/superusers?error=user_not_found");
                return;
            }

            // Toggle status
            superUser.setActive(!superUser.isActive());
            boolean updated = superUserDAO.updateSuperUser(superUser);

            if (updated) {
                response.sendRedirect(request.getContextPath() + "/admin/superusers?success=status_changed");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/superusers?error=status_change_failed");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/superusers?error=invalid_id");
        }
    }

    /**
     * Handle sending email to a superuser
     */
    private void handleSendEmail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get form data
        String recipient = request.getParameter("recipient");
        String subject = request.getParameter("subject");
        String message = request.getParameter("message");

        // Basic validation
        if (!Validator.isValidEmail(recipient) || !Validator.isNotEmpty(subject) || !Validator.isNotEmpty(message)) {
            response.sendRedirect(request.getContextPath() + "/admin/superusers?error=email_failed");
            return;
        }

        try {
            // Get the admin user info for the email footer
            Object adminUser = request.getSession().getAttribute("user");
            String senderName = "Admin";

            if (adminUser instanceof SuperUser) {
                senderName = ((SuperUser) adminUser).getFullName();
            }

            // Format HTML message
            String htmlMessage = formatHtmlEmail(message, senderName);

            // Send email using EmailService
            EmailService.sendEmail(recipient, subject, htmlMessage);

            // Redirect back to superusers page with success message
            response.sendRedirect(request.getContextPath() + "/admin/superusers?success=email_sent");
        } catch (Exception e) {
            // Log the error
            System.err.println("Error sending email: " + e.getMessage());
            e.printStackTrace();

            // Redirect with error message
            response.sendRedirect(request.getContextPath() + "/admin/superusers?error=email_failed");
        }
    }

    /**
     * Format plain text message as HTML email
     *
     * @param message    Plain text message
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
            
            // Create a unique filename
            String fileName = prefix + "-" + UUID.randomUUID().toString() + "." + extension;
            
            // Get real path of the web application
            String applicationPath = getServletContext().getRealPath("");
            String avatarDirPath = applicationPath + File.separator + "assets" + 
                                   File.separator + "imgs" + File.separator + "avatars";
            
            // Ensure directory exists
            File avatarDir = new File(avatarDirPath);
            if (!avatarDir.exists()) {
                avatarDir.mkdirs();
            }
            
            // Save the file
            String filePath = avatarDirPath + File.separator + fileName;
            Files.write(Paths.get(filePath), imageBytes);
            
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getServletInfo() {
        return "Admin SuperUser Servlet - Manages admin and instructor accounts";
    }
}
