/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.admin;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import dao.CourseDAO;
import dao.OrderDAO;
import dao.UserDAO;
import dao.RefundRequestDAO;
import model.Course;
import model.Order;
import model.RefundRequest;

/**
 * Admin dashboard controller.
 *
 * @author DangPH - CE180896
 */
@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardServlet extends HttpServlet {

    private CourseDAO courseDAO;
    private OrderDAO orderDAO;
    private UserDAO userDAO;
    private RefundRequestDAO refundRequestDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        courseDAO = new CourseDAO();
        orderDAO = new OrderDAO();
        userDAO = new UserDAO();
        refundRequestDAO = new RefundRequestDAO();
    }

    /**
     * Handles the HTTP GET request - displaying the admin dashboard.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("login success");
        // Total courses
        int totalCourses = courseDAO.countAllCourses();
        request.setAttribute("totalCourses", totalCourses);

        // Total customers
        int totalCustomers = userDAO.countUserWithRole("customer");
        request.setAttribute("totalCustomers", totalCustomers);

        // Total instructor
        int totalInstructors = userDAO.countUserWithRole("instructor");
        request.setAttribute("totalInstructors", totalInstructors);

        // Recent courses (last 5)
        List<Course> recentCourses = courseDAO.getAllCoursesWithLimit(0, 5, null, "DESC");
        request.setAttribute("recentCourses", recentCourses);

        // Recent refund requests (last 5)
        List<RefundRequest> recentRefundRequests = refundRequestDAO.getAllRefundRequestsWithLimit(5);
        request.setAttribute("recentRefundRequests", recentRefundRequests);

        // Forward to dashboard page
        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Admin Dashboard Servlet - Displays the admin dashboard";
    }

}
