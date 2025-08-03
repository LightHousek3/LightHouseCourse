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

/**
 *
 * @author NhiDTY-CE180492
 */
@WebServlet(name = "VerifyServlet", urlPatterns = { "/verify" })
public class VerifyServlet extends HttpServlet {
    private final CustomerDAO customerDAO = new CustomerDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/auth/verify-code.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String enteredToken = request.getParameter("code");
        String storedToken = (String) session.getAttribute("pendingToken");
        String email = (String) session.getAttribute("pendingEmail");

        // Lấy số lần nhập sai hiện tại từ session (nếu chưa có thì mặc định là 0)
        Integer failCount = (Integer) session.getAttribute("verifyFailCount");
        if (failCount == null)
            failCount = 0;

        // Kiểm tra session
        if (storedToken == null || email == null) {
            request.setAttribute("error", "Session expired. Please try another way.");
            request.getRequestDispatcher("/WEB-INF/views/auth/verify-code.jsp").forward(request, response);
            return;
        }

        // Kiểm tra code nhập vào
        if (enteredToken == null || enteredToken.trim().isEmpty()) {
            request.setAttribute("error", "Please enter verification code.");
            request.getRequestDispatcher("/WEB-INF/views/auth/verify-code.jsp").forward(request, response);
            return;
        }

        if (enteredToken.equals(storedToken)) {
            // Verify token has not expired in the database
            Customer customer = customerDAO.getCustomerByEmail(email);
            if (customer == null || customer.isTokenExpired()) {
                request.setAttribute("error", "Verification code has expired. Please register again.");
                request.getRequestDispatcher("/WEB-INF/views/auth/verify-code.jsp").forward(request, response);
                return;
            }

            // Kích hoạt tài khoản
            boolean activate = customerDAO.activateCustomerByEmailAndToken(email, enteredToken);

            if (activate) {
                // Xóa session data - clear only registration-related attributes
                session.removeAttribute("pendingToken");
                session.removeAttribute("pendingEmail");
                session.removeAttribute("verifyFailCount");

                // Make sure we don't have any password reset attributes
                session.removeAttribute("resetEmail");
                session.removeAttribute("resetFailCount");

                // Show success message directly on verify-code page
                request.setAttribute("message", "🎉 Your account has been verified successfully! You can now log in!");
                request.getRequestDispatcher("/WEB-INF/views/auth/verify-code.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Failed to activate your account. Please try again.");
                request.getRequestDispatcher("/WEB-INF/views/auth/verify-code.jsp").forward(request, response);
            }
            return;
        }

        // Sai mã → tăng failCount
        failCount++;
        session.setAttribute("verifyFailCount", failCount);

        if (failCount >= 5) {
            // Xử lý nếu nhập sai 5 lần → yêu cầu gửi mã mới
            session.removeAttribute("pendingToken");
            session.removeAttribute("verifyFailCount");

            request.setAttribute("error",
                    "❌ You entered the wrong code 5 times. Please request a new verification code.");
            request.getRequestDispatcher("/WEB-INF/views/auth/verify-code.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "❌ Invalid verification code. Attempts remaining: " + (5 - failCount));
            request.getRequestDispatcher("/WEB-INF/views/auth/verify-code.jsp").forward(request, response);
        }
    }
}