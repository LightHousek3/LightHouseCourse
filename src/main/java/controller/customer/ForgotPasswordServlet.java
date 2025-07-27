package controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Random;
import dao.CustomerDAO;
import model.Customer;
import util.EmailUtils;
import util.PasswordEncrypt;

/**
 * Servlet for handling forgot password functionality
 */
@WebServlet(name = "ForgotPasswordServlet", urlPatterns = {
        "/forgot-password",
        "/reset-password",
        "/reset-password/verify"
})
public class ForgotPasswordServlet extends HttpServlet {

    private final CustomerDAO customerDAO = new CustomerDAO();
    private static final int TOKEN_EXPIRY_MINUTES = 10; // 10 minutes
    private static final Random random = new Random();

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
        String servletPath = request.getServletPath();

        switch (servletPath) {
            case "/forgot-password":
                // Show forgot password form
                request.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp")
                        .forward(request, response);
                break;
            case "/reset-password/verify":
                // Show verification form
                request.getRequestDispatcher("/WEB-INF/views/auth/verify-code.jsp").forward(request,
                        response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/login");
                break;
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
        String servletPath = request.getServletPath();

        switch (servletPath) {
            case "/forgot-password":
                processForgotPassword(request, response);
                break;
            case "/reset-password/verify":
                processVerifyResetCode(request, response);
                break;
            case "/reset-password":
                processResendCode(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/login");
                break;
        }
    }

    /**
     * Process forgot password request
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    private void processForgotPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");

        // Validate email
        if (email == null || email.trim().isEmpty() || !email.matches("^\\S+@\\S+\\.\\S+$")) {
            request.setAttribute("error", "Please enter a valid email address.");
            request.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(request,
                    response);
            return;
        }

        // Check if email exists in database
        Customer customer = customerDAO.getCustomerByEmail(email);
        if (customer == null || !customer.isActive()) {
            request.setAttribute("error", "No active account found with this email address.");
            request.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(request,
                    response);
            return;
        }

        // Check if user can request a new token (60 seconds between requests)
        long remainingTime = customerDAO.getRemainingTimeForNewToken(email);
        if (remainingTime > 0) {
            request.setAttribute("error", "Please wait before requesting another code.");
            request.setAttribute("remainingTime", remainingTime);
            request.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(request,
                    response);
            return;
        }

        // Generate 6-digit verification code
        String resetCode = String.format("%06d", random.nextInt(1000000));

        // Calculate expiry time (10 minutes from now)
        Timestamp expiryTime = new Timestamp(System.currentTimeMillis() + (TOKEN_EXPIRY_MINUTES * 60 * 1000));

        // Update token in database
        boolean updated = customerDAO.updatePasswordResetToken(email, resetCode, expiryTime);
        if (!updated) {
            request.setAttribute("error", "Failed to process your request. Please try again later.");
            request.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(request,
                    response);
            return;
        }

        // Send email with reset code
        try {
            EmailUtils.sendPasswordResetEmail(email, resetCode);
        } catch (Exception e) {
            request.setAttribute("error", "Failed to send reset code. Please try again later.");
            request.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(request,
                    response);
            return;
        }

        // Store email in session for verification
        HttpSession session = request.getSession();
        // Clear any registration-related attributes to avoid conflicts
        session.removeAttribute("pendingEmail");
        session.removeAttribute("pendingToken");
        session.removeAttribute("verifyFailCount");

        // Set password reset specific attributes
        session.setAttribute("resetEmail", email);
        session.setAttribute("resetFailCount", 0);

        // Redirect to verification page
        response.sendRedirect(request.getContextPath() + "/reset-password/verify");
    }

    /**
     * Process verify reset code request
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    private void processVerifyResetCode(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("resetEmail");
        String enteredCode = request.getParameter("code");

        // Check if session has expired
        if (email == null) {
            request.setAttribute("error", "Your session has expired. Please start the password reset process again.");
            request.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(request,
                    response);
            return;
        }

        // Check if code is empty
        if (enteredCode == null || enteredCode.trim().isEmpty()) {
            request.setAttribute("error", "Please enter the verification code.");

            // Get remaining time for cooldown and pass it to the view
            long remainingTime = customerDAO.getRemainingTimeForNewToken(email);
            if (remainingTime > 0) {
                request.setAttribute("remainingTime", remainingTime);
            }

            request.getRequestDispatcher("/WEB-INF/views/auth/verify-code.jsp").forward(request,
                    response);
            return;
        }

        // Verify the code
        Customer customer = customerDAO.verifyPasswordResetToken(email, enteredCode);
        if (customer == null) {
            Integer failCount = (Integer) session.getAttribute("resetFailCount");
            if (failCount == null)
                failCount = 0;

            failCount++;
            session.setAttribute("resetFailCount", failCount);

            if (failCount >= 5) {
                session.removeAttribute("resetEmail");
                session.removeAttribute("resetFailCount");

                request.setAttribute("error",
                        "Too many failed attempts. Please start the password reset process again.");
                request.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp")
                        .forward(request, response);
                return;
            }

            // Get remaining time for cooldown and pass it to the view
            long remainingTime = customerDAO.getRemainingTimeForNewToken(email);
            if (remainingTime > 0) {
                request.setAttribute("remainingTime", remainingTime);
            }

            request.setAttribute("error",
                    "Invalid or expired verification code. Attempts remaining: " + (5 - failCount));
            request.getRequestDispatcher("/WEB-INF/views/auth/verify-code.jsp").forward(request,
                    response);
            return;
        }

        // Generate new password
        String newPassword = String.format("%06d", random.nextInt(1000000));
        String encryptedPassword = PasswordEncrypt.encrypt(newPassword);

        // Update password in database
        boolean updated = customerDAO.resetPassword(email, encryptedPassword);
        if (!updated) {
            // Get remaining time for cooldown and pass it to the view
            long remainingTime = customerDAO.getRemainingTimeForNewToken(email);
            if (remainingTime > 0) {
                request.setAttribute("remainingTime", remainingTime);
            }

            request.setAttribute("error", "Failed to reset password. Please try again later.");
            request.getRequestDispatcher("/WEB-INF/views/auth/verify-code.jsp").forward(request,
                    response);
            return;
        }

        // Send email with new password
        try {
            EmailUtils.sendNewPasswordEmail(email, newPassword);
        } catch (Exception e) {
            request.setAttribute("message",
                    "Your password has been reset, but we couldn't send you the new password by email. Please contact support.");
            request.getRequestDispatcher("/WEB-INF/views/auth/verify-code.jsp").forward(request,
                    response);
            return;
        }

        // Clean up session
        session.removeAttribute("resetEmail");
        session.removeAttribute("resetFailCount");

        // Show success message directly on verify-code page
        request.setAttribute("message",
                "Your password has been reset successfully. Please check your email for the new password.");
        request.getRequestDispatcher("/WEB-INF/views/auth/verify-code.jsp").forward(request,
                response);
    }

    /**
     * Process resend code request
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    private void processResendCode(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("resetEmail");

        // Check if session has expired
        if (email == null) {
            request.setAttribute("error", "Your session has expired. Please start the password reset process again.");
            request.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(request,
                    response);
            return;
        }

        // Check if user can request a new token (60 seconds between requests)
        long remainingTime = customerDAO.getRemainingTimeForNewToken(email);
        if (remainingTime > 0) {
            request.setAttribute("error", "Please wait before requesting another code.");
            request.setAttribute("remainingTime", remainingTime);
            request.getRequestDispatcher("/WEB-INF/views/auth/verify-code.jsp").forward(request,
                    response);
            return;
        }

        // Generate new 6-digit verification code
        String resetCode = String.format("%06d", random.nextInt(1000000));

        // Calculate expiry time (10 minutes from now)
        Timestamp expiryTime = new Timestamp(System.currentTimeMillis() + (TOKEN_EXPIRY_MINUTES * 60 * 1000));

        // Update token in database
        boolean updated = customerDAO.updatePasswordResetToken(email, resetCode, expiryTime);
        if (!updated) {
            request.setAttribute("error", "Failed to process your request. Please try again later.");
            request.getRequestDispatcher("/WEB-INF/views/auth/verify-code.jsp").forward(request,
                    response);
            return;
        }

        // Send email with reset code
        try {
            EmailUtils.sendPasswordResetEmail(email, resetCode);
        } catch (Exception e) {
            request.setAttribute("error", "Failed to send reset code. Please try again later.");
            request.getRequestDispatcher("/WEB-INF/views/auth/verify-code.jsp").forward(request,
                    response);
            return;
        }

        // Reset fail count
        session.setAttribute("resetFailCount", 0);

        // Show verification page with success message
        request.setAttribute("message", "A new verification code has been sent to your email.");
        request.getRequestDispatcher("/WEB-INF/views/auth/verify-code.jsp").forward(request,
                response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Forgot Password Servlet";
    }
}