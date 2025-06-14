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
import util.Validator;

/**
 * Admin dashboard controller.
 * @author DangPH - CE180896
 */
@WebServlet(name = "AdminUserServlet", urlPatterns = {"/admin/users"})
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
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Action can't be done!");
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
