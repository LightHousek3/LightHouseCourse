/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.instructor;

import dao.InstructorDAO;
import dao.SuperUserDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Instructor;
import model.SuperUser;
import service.EmailService;
import util.PasswordEncrypt;
import util.Validator;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;
import javax.servlet.annotation.MultipartConfig;

/**
 *
 * @author NhiDTY - CE180492
 */
@WebServlet(name = "InstructorProfileServlet", urlPatterns = {"/instructor/profile", "/instructor/profile/*"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1 MB
        maxFileSize = 5 * 1024 * 1024, // 5 MB
        maxRequestSize = 10 * 1024 * 1024 // 10 MB
)
public class InstructorProfileServlet extends HttpServlet {

    private InstructorDAO instructorDAO;
    private SuperUserDAO superUserDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        instructorDAO = new InstructorDAO();
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
        HttpSession session = request.getSession();
        // Test account si exist
        SuperUser superUser = superUserDAO.getSuperUserById(7);
        if (superUser == null) {
            // Nếu vẫn null thì báo lỗi rõ ràng
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Test SuperUserID=4 not found in database!");
            return;
        }
        session.setAttribute("user", superUser);

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            // Lấy instructor từ DB theo superUserID
            Instructor instructor = instructorDAO.getInstructorBySuperUserId(superUser.getSuperUserID());

            if (instructor == null) {
                request.setAttribute("error", "Instructor profile not found");
                request.getRequestDispatcher("/WEB-INF/views/instructor/manage-profile/view-profile.jsp").forward(request, response);
                return;
            }

            request.setAttribute("instructor", instructor);
            request.setAttribute("avatar", instructor.getAvatar());

            //  Handle success message
            String success = request.getParameter("success");
            if ("updated".equals(success)) {
                request.setAttribute("successMessage", "Profile updated successfully!");
            } else if ("password_changed".equals(success)) {
                request.setAttribute("successMessage", "Password updated successfully!");
            }

            //  Handle error message
            String error = request.getParameter("error");
            if ("notfound".equals(error)) {
                request.setAttribute("errorMessage", "Profile not found!");
            } else if ("update_failed".equals(error)) {
                request.setAttribute("errorMessage", "Failed to update profile!");
            }

            request.getRequestDispatcher("/WEB-INF/views/instructor/manage-profile/view-profile.jsp").forward(request, response);
            return;
        }

        if (pathInfo.equals("/edit")) {
            // Show edit profile form
            Instructor instructor = instructorDAO.getInstructorBySuperUserId(superUser.getSuperUserID());
            if (instructor == null) {
                request.setAttribute("error", "Instructor profile not found");
                request.getRequestDispatcher("/WEB-INF/views/instructor/manage-profile/edit-profile.jsp").forward(request, response);
                return;
            }

            request.setAttribute("instructor", instructor);
            // FIX: Handle error message for edit form
            String error = request.getParameter("error");
            if ("update_failed".equals(error)) {
                request.setAttribute("errorMessage", "Failed to update profile!");
            }

            request.getRequestDispatcher("/WEB-INF/views/instructor/manage-profile/edit-profile.jsp").forward(request, response);
            return;
        }

        if (pathInfo.startsWith("/edit/")) {
            // Show edit specific instructor form
            String instructorIdStr = pathInfo.substring("/edit/".length());
            try {
                int instructorId = Integer.parseInt(instructorIdStr);
                Instructor selectedInstructor = instructorDAO.getInstructorById(instructorId);

                if (selectedInstructor == null) {
                    request.setAttribute("error", "Instructor not found");
                    request.getRequestDispatcher("/WEB-INF/views/instructor/manage-profile/edit-profile.jsp").forward(request, response);
                    return;
                }

                request.setAttribute("instructor", selectedInstructor);  // Đổi lại key nếu cần
                request.getRequestDispatcher("/WEB-INF/views/instructor/manage-profile/edit-profile.jsp").forward(request, response);
                return;
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid instructor ID");
                request.getRequestDispatcher("/WEB-INF/views/instructor/manage-profile/edit-profile.jsp").forward(request, response);
            }
        }

        // ...existing code...
        if (pathInfo.equals("/change")) {
            // Show change password form
            request.getRequestDispatcher("/WEB-INF/views/instructor/manage-profile/change-password.jsp").forward(request, response);
            return;
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
            case "updateProfile":
                handleUpdateProfile(request, response);
                return;
            case "toggleStatus":
                handleToggleInstructorStatus(request, response);
                return;
            case "changePassword":
                handleChangePassword(request, response);
                return;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Action can't be done!");
        }
    }

    /**
     * Handle updating instructor profile (for logged-in instructor)
     */
    private void handleUpdateProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        if (session.getAttribute("user") == null) {
            SuperUser superUser = new SuperUser();
            superUser.setSuperUserID(4); // ID test
            superUser.setAvatar("/assets/imgs/avatars/instructor1.png");
            superUser.setFullName("Test Instructor");
            superUser.setRole("instructor");
            session.setAttribute("user", superUser);
        }
        // --- End test ---

        SuperUser user = (SuperUser) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Get form data
        String email = request.getParameter("email");
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String avatarUrl = request.getParameter("avatarUrl");
        String bio = request.getParameter("bio");
        String specialization = request.getParameter("specialization");

        // Lấy instructorId từ session, nếu chưa có thì set mặc định là 1
        Instructor existingInstructor = instructorDAO.getInstructorBySuperUserId(user.getSuperUserID());
        if (existingInstructor == null) {
            response.sendRedirect(request.getContextPath() + "/instructor/profile?error=notfound");
            return;
        }
        // Validation flags
        boolean hasError = false;

        // Validate email
        if (!Validator.isValidEmail(email)) {
            request.setAttribute("emailError", "Please enter a valid email address");
            hasError = true;
        }

        // Validate fullName
        if (!Validator.isValidFullname(fullName)) {
            request.setAttribute("fullNameError", "Full name must be 3–50 characters and only contain letters and spaces.");
            hasError = true;
        }

        // Validate phone (optional)
        if (phone != null && !phone.trim().isEmpty() && !Validator.isValidPhone(phone)) {
            request.setAttribute("phoneError", "Please enter a valid phone number (10-11 digits)");
            hasError = true;
        }

        // If validation failed, return to form with input preserved
        if (hasError) {
            existingInstructor.setEmail(email);
            existingInstructor.setFullName(fullName);
            existingInstructor.setPhone(phone);
            existingInstructor.setAddress(address);
            existingInstructor.setAvatar(avatarUrl);
            existingInstructor.setBio(bio);
            existingInstructor.setSpecialization(specialization);
            request.setAttribute("instructor", existingInstructor);
            System.out.println("error");
            request.getRequestDispatcher("/WEB-INF/views/instructor/manage-profile/edit-profile.jsp").forward(request, response);
            return;
        }

        // Update instructor data
        existingInstructor.setEmail(email);
        existingInstructor.setFullName(fullName);
        existingInstructor.setPhone(phone);
        existingInstructor.setAddress(address);
        existingInstructor.setBio(bio);
        existingInstructor.setSpecialization(specialization);

        // Check if avatarUrl is a base64 image
        if (avatarUrl != null && avatarUrl.startsWith("data:image")) {
            String fileName = saveAvatarImage(avatarUrl, "instructor");
            if (fileName != null) {
                avatarUrl = "/assets/imgs/avatars/" + fileName;
                existingInstructor.setAvatar(avatarUrl);
            } else {
                // Nếu upload thất bại, giữ nguyên avatar cũ
                if (existingInstructor.getAvatar() == null || existingInstructor.getAvatar().isEmpty()) {
                    existingInstructor.setAvatar("/assets/imgs/avatars/default-user.png");
                }
            }
        } else if (avatarUrl != null && !avatarUrl.trim().isEmpty()) {
            // Nếu là đường dẫn ảnh cũ, giữ nguyên
            existingInstructor.setAvatar(avatarUrl);
        } else {
            // Nếu không có gì, giữ nguyên avatar cũ hoặc set default nếu chưa có
            if (existingInstructor.getAvatar() == null || existingInstructor.getAvatar().isEmpty()) {
                existingInstructor.setAvatar("/assets/imgs/avatars/default-user.png");
            }
        }

        existingInstructor.setSuperUserID(user.getSuperUserID());

        if (existingInstructor.getPassword() == null || existingInstructor.getPassword().trim().isEmpty()) {
            // Lấy password hiện tại từ database
            Instructor currentInstructor = instructorDAO.getInstructorBySuperUserId(user.getSuperUserID());
            existingInstructor.setPassword(currentInstructor.getPassword());
        }

        boolean updated = instructorDAO.editInstructor(existingInstructor);

        if (updated) {
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPhone(phone);
            user.setAddress(address);
            if (avatarUrl != null && !avatarUrl.trim().isEmpty()) {
                user.setAvatar(avatarUrl);
            }
            session.setAttribute("user", user);
            // FIX: Redirect với success message
            response.sendRedirect(request.getContextPath() + "/instructor/profile?success=updated");
        } else {
            response.sendRedirect(request.getContextPath() + "/instructor/profile/edit?error=update_failed");
        }
    }

    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        SuperUser user = (SuperUser) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // ✅ Lấy lại user từ DB để so sánh mật khẩu
        SuperUser dbUser = superUserDAO.getSuperUserById(user.getSuperUserID());
        if (dbUser == null) {
            response.sendRedirect(request.getContextPath() + "/instructor/profile?error=notfound");
            return;
        }

        boolean hasError = false;

        // ✅ Kiểm tra mật khẩu hiện tại đúng không
        if (!PasswordEncrypt.encryptSHA256(currentPassword).equals(dbUser.getPassword())) {
            request.setAttribute("currentPasswordError", "Current password is incorrect.");
            hasError = true;
        }

        if (!Validator.isValidPassword(newPassword)) {
            request.setAttribute("newPasswordError", "Password must be at least 6 characters.");
            hasError = true;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("confirmPasswordError", "Passwords do not match.");
            hasError = true;
        }

        if (hasError) {
            request.getRequestDispatcher("/WEB-INF/views/instructor/manage-profile/change-password.jsp").forward(request, response);
            return;
        }

        // ✅ Mã hóa và cập nhật SuperUser
        String encryptedNewPassword = PasswordEncrypt.encryptSHA256(newPassword);
        dbUser.setPassword(encryptedNewPassword);
        boolean updated = superUserDAO.updatePassword(dbUser);

        if (updated) {
            // cập nhật session nếu cần
            user.setPassword(encryptedNewPassword);
            response.sendRedirect(request.getContextPath() + "/instructor/profile?success=password_changed");
        } else {
            response.sendRedirect(request.getContextPath() + "/instructor/profile/change?error=update_failed");
        }
    }

    /**
     * Handle updating an existing instructor
     */
    private void handleUpdateInstructor(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get form data
        String instructorIdStr = request.getParameter("instructorId");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String password = request.getParameter("password");
        String avatarUrl = request.getParameter("avatarUrl");
        String bio = request.getParameter("bio");
        String specialization = request.getParameter("specialization");
        int instructorId;
        Instructor existingInstructor;

        // Basic validation
        try {
            instructorId = Integer.parseInt(instructorIdStr);
            existingInstructor = instructorDAO.getInstructorById(instructorId);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/instructor/profile/list?error=invalid_id");
            return;
        }

        // Validation flags
        boolean hasError = false;

        // Validate username
        if (!Validator.isValidUsername(username)) {
            request.setAttribute("usernameError",
                    "Username must be 3-20 characters and contain only letters, numbers, and underscores");
            hasError = true;
        } else if ((instructorDAO.usernameExists(username) || superUserDAO.usernameExists(username))
                && !username.equals(existingInstructor.getUsername())) {
            request.setAttribute("usernameError", "Username already exists");
            hasError = true;
        }

// Validate email
        if (!Validator.isValidEmail(email)) {
            request.setAttribute("emailError", "Please enter a valid email address");
            hasError = true;
        } else if ((instructorDAO.emailExists(email) || superUserDAO.emailExists(email))
                && !email.equals(existingInstructor.getEmail())) {
            request.setAttribute("emailError", "Email already exists");
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
            existingInstructor.setUsername(username);
            existingInstructor.setPassword(password);
            existingInstructor.setEmail(email);
            existingInstructor.setFullName(fullName);
            existingInstructor.setPhone(phone);
            existingInstructor.setAddress(address);
            existingInstructor.setAvatar(avatarUrl);
            existingInstructor.setBio(bio);
            existingInstructor.setSpecialization(specialization);
            request.setAttribute("selectedInstructor", existingInstructor);

            request.getRequestDispatcher("/WEB-INF/views/instructor/manage-profile/edit-profile.jsp").forward(request, response);
            return;
        }

        try {
            // Make sure we're dealing with a valid instructor
            if (existingInstructor == null) {
                response.sendRedirect(request.getContextPath() + "/instructor/profile/list?error=invalid_id");
                return;
            }

            // Check whether instructor has changed password
            if (password != null && !password.trim().isEmpty()) {
                String encryptedPassword = PasswordEncrypt.encryptSHA256(password);
                existingInstructor.setPassword(encryptedPassword);
            }

            existingInstructor.setUsername(username);
            existingInstructor.setEmail(email);
            existingInstructor.setFullName(fullName);
            existingInstructor.setPhone(phone);
            existingInstructor.setAddress(address);
            existingInstructor.setBio(bio);
            existingInstructor.setSpecialization(specialization);

            // Check if avatarUrl is a base64 image
            if (avatarUrl != null && avatarUrl.startsWith("data:image")) {
                String fileName = saveAvatarImage(avatarUrl, "instructor");
                if (fileName != null) {
                    avatarUrl = "/assets/imgs/avatars/" + fileName;
                    existingInstructor.setAvatar(avatarUrl);
                }
            }

            boolean updated = instructorDAO.editInstructor(existingInstructor);

            if (updated) {
                System.out.println("update success");
                response.sendRedirect(request.getContextPath() + "/instructor/profile/list?success=updated");
            } else {
                response.sendRedirect(
                        request.getContextPath() + "/instructor/profile/edit/" + instructorId + "?error=update_failed");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/instructor/profile/list?error=invalid_id");
        }

    }

    /**
     * Handle toggling instructor active status
     */
    private void handleToggleInstructorStatus(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String instructorIdStr = request.getParameter("instructorId");
        if (instructorIdStr == null || instructorIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/instructor/profile/list?error=missing_id");
            return;
        }

        try {
            int instructorId = Integer.parseInt(instructorIdStr);

            // Check if instructor exists
            Instructor existingInstructor = instructorDAO.getInstructorById(instructorId);
            if (existingInstructor == null) {
                response.sendRedirect(request.getContextPath() + "/instructor/profile/list?error=instructor_not_found");
                return;
            }

            // Toggle active status
            existingInstructor.setActive(!existingInstructor.isActive());
            boolean updated = instructorDAO.editInstructor(existingInstructor);

            if (updated) {
                response.sendRedirect(request.getContextPath() + "/instructor/profile/list?success=status_changed");
            } else {
                response.sendRedirect(request.getContextPath() + "/instructor/profile/list?error=status_change_failed");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/instructor/profile/list?error=invalid_id");
        }
    }

    /**
     * Handle sending email to an instructor
     */
    private void handleSendEmail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get form data
        String recipient = request.getParameter("recipient");
        String subject = request.getParameter("subject");
        String message = request.getParameter("message");

        // Basic validation
        if (!Validator.isValidEmail(recipient) || !Validator.isNotEmpty(subject) || !Validator.isNotEmpty(message)) {
            response.sendRedirect(request.getContextPath() + "/instructor/profile/list?error=email_failed");
            return;
        }

        try {
            // Get the instructor user info for the email footer
            HttpSession session = request.getSession();
            SuperUser senderUser = (SuperUser) session.getAttribute("user");
            String senderName = senderUser != null ? senderUser.getFullName() : "System";

            // Format HTML message
            String htmlMessage = formatHtmlEmail(message, senderName);

            // Send email using EmailService
            EmailService.sendEmail(recipient, subject, htmlMessage);

            // Redirect back to instructors page with success message
            response.sendRedirect(request.getContextPath() + "/instructor/profile/list?success=email_sent");
        } catch (Exception e) {
            // Log the error
            System.err.println("Error sending email: " + e.getMessage());
            e.printStackTrace();

            // Redirect with error message
            response.sendRedirect(request.getContextPath() + "/instructor/profile/list?error=email_failed");
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
                + "<div class='header'><strong>Message from LightHouse Instructor Panel</strong></div>"
                + "<div class='content'>" + message.replace("\n", "<br>") + "</div>"
                + "<div class='footer'>This email was sent by " + senderName + " from the LightHouse Instructor Panel."
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

            // Create a unique filename
            String fileName = prefix + "-" + UUID.randomUUID().toString() + "." + extension;

            // Get real path of the web application
            String applicationPath = getServletContext().getRealPath("");
            String avatarDirPath = applicationPath + File.separator + "assets"
                    + File.separator + "imgs" + File.separator + "avatars";

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
        return "Instructor Profile Servlet - Manages instructor profile and accounts";
    }

}
