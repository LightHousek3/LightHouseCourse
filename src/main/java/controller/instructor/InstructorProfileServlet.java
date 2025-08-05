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
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;
import javax.servlet.annotation.MultipartConfig;

/**
 *
 * @author NhiDTY - CE180492
 */
@WebServlet(name = "InstructorProfileServlet", urlPatterns = { "/instructor/profile", "/instructor/profile/*" })
@MultipartConfig(fileSizeThreshold = 1024 * 1024, // 1 MB
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
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        SuperUser superUser;

        try {
            superUser = (SuperUser) session.getAttribute("user");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (superUser == null) {
            // Nếu vẫn null thì báo lỗi rõ ràng
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "SuperUser not found!");
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            // Lấy instructor từ DB theo superUserID
            Instructor instructor = instructorDAO.getInstructorBySuperUserId(superUser.getSuperUserID());

            if (instructor == null) {
                request.setAttribute("error", "Instructor profile not found");
                request.getRequestDispatcher("/WEB-INF/views/instructor/manage-profile/view-profile.jsp")
                        .forward(request, response);
                return;
            }

            request.setAttribute("instructor", instructor);
            request.setAttribute("avatar", instructor.getAvatar());

            // Handle success message
            String success = request.getParameter("success");
            if ("updated".equals(success)) {
                request.setAttribute("successMessage", "Profile updated successfully!");
            } else if ("password_changed".equals(success)) {
                request.setAttribute("successMessage", "Password updated successfully!");
            }

            // Handle error message
            String error = request.getParameter("error");
            if ("notfound".equals(error)) {
                request.setAttribute("errorMessage", "Profile not found!");
            } else if ("update_failed".equals(error)) {
                request.setAttribute("errorMessage", "Failed to update profile!");
            }

            request.getRequestDispatcher("/WEB-INF/views/instructor/manage-profile/view-profile.jsp").forward(request,
                    response);
            return;
        }

        if (pathInfo.equals("/edit")) {
            // Show edit profile form
            Instructor instructor = instructorDAO.getInstructorBySuperUserId(superUser.getSuperUserID());
            if (instructor == null) {
                request.setAttribute("error", "Instructor profile not found");
                request.getRequestDispatcher("/WEB-INF/views/instructor/manage-profile/edit-profile.jsp")
                        .forward(request, response);
                return;
            }

            request.setAttribute("instructor", instructor);
            String error = request.getParameter("error");
            if ("update_failed".equals(error)) {
                request.setAttribute("errorMessage", "Failed to update profile!");
            }

            request.getRequestDispatcher("/WEB-INF/views/instructor/manage-profile/edit-profile.jsp").forward(request,
                    response);
            return;
        }

        if (pathInfo.equals("/change")) {
            // Show change password form
            request.getRequestDispatcher("/WEB-INF/views/instructor/manage-profile/change-password.jsp")
                    .forward(request, response);
            return;
        }
        // Not found
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
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
            case "updateProfile":
                handleUpdateProfile(request, response);
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

        SuperUser user;

        try {
            user = (SuperUser) session.getAttribute("user");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

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

        // Lấy instructorId từ session
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
            request.setAttribute("fullNameError",
                    "Full name must be 3–50 characters and only contain letters and spaces.");
            hasError = true;
        }

        // Validate phone (optional)
        if (phone == null || phone.trim().isEmpty() || !Validator.isValidPhone(phone)) {
            request.setAttribute("phoneError", "Please enter a valid phone number (10-11 digits)");
            hasError = true;
        }

        // Validate address
        if (address == null || address.trim().isEmpty()) {
            request.setAttribute("addressError", "Address is required.");
            hasError = true;
        }

        // Validate biography
        if (bio == null || bio.trim().isEmpty()) {
            request.setAttribute("biographyError", "Biography is required.");
            hasError = true;
        }

        // Validate specialization
        if (specialization == null || specialization.trim().isEmpty()) {
            request.setAttribute("specializationError", "Specialization is required.");
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
            request.getRequestDispatcher("/WEB-INF/views/instructor/manage-profile/edit-profile.jsp").forward(request,
                    response);
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
            // FIX: Redirect với success message
            response.sendRedirect(request.getContextPath() + "/instructor/profile?success=updated");
        } else {
            response.sendRedirect(request.getContextPath() + "/instructor/profile/edit?error=update_failed");
        }
    }

    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        SuperUser user;

        try {
            user = (SuperUser) session.getAttribute("user");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // Lấy lại user từ DB để so sánh mật khẩu
        SuperUser dbUser = superUserDAO.getSuperUserById(user.getSuperUserID());
        if (dbUser == null) {
            response.sendRedirect(request.getContextPath() + "/instructor/profile?error=notfound");
            return;
        }

        boolean hasError = false;

        // Kiểm tra mật khẩu hiện tại đúng không
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
            request.getRequestDispatcher("/WEB-INF/views/instructor/manage-profile/change-password.jsp")
                    .forward(request, response);
            return;
        }

        // Mã hóa và cập nhật SuperUser
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
        return "Instructor Profile Servlet - Manages instructor profile and accounts";
    }

}
