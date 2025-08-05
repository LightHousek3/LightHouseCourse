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
import dao.CustomerDAO;
import dao.RefundRequestDAO;
import dao.SuperUserDAO;
import jakarta.servlet.http.HttpSession;
import model.Course;
import model.RefundRequest;
import model.SuperUser;

/**
 * Admin dashboard controller.
 *
 * @author DangPH - CE180896
 */
@WebServlet(name = "AdminDashboardServlet", urlPatterns = { "/admin/dashboard" })
public class AdminDashboardServlet extends HttpServlet {

    private CourseDAO courseDAO;
    private RefundRequestDAO refundRequestDAO;
    private SuperUserDAO superUserDAO;
    private CustomerDAO customerDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        courseDAO = new CourseDAO();
        refundRequestDAO = new RefundRequestDAO();
        superUserDAO = new SuperUserDAO();
        customerDAO = new CustomerDAO();
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
        // Check if admin is logged in
        HttpSession session = request.getSession();
        SuperUser admin;

        try {
            admin = (SuperUser) session.getAttribute("user");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Get dashboard data
        int totalCourses = courseDAO.countAllCourses(null);
        int totalCustomers = customerDAO.countCustomers();
        int totalInstructors = superUserDAO.countSuperUserWithRole("instructor");
        int totalPendingRefunds = refundRequestDAO.countPendingRefundRequests();

        // Get recent refund requests
        List<RefundRequest> recentRefundRequests = refundRequestDAO.getAllRefundRequestsWithLimit(5);

        // Get recent course
        List<Course> recentCourses = courseDAO.getAllCoursesWithLimit(0, 5, "date", "DESC");

        // Set attributes for the JSP
        request.setAttribute("admin", admin);
        request.setAttribute("totalCourses", totalCourses);
        request.setAttribute("totalCustomers", totalCustomers);
        request.setAttribute("totalInstructors", totalInstructors);
        request.setAttribute("totalPendingRefunds", totalPendingRefunds);
        request.setAttribute("recentRefundRequests", recentRefundRequests);
        request.setAttribute("recentCourses", recentCourses);

        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
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
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Admin Dashboard Servlet";
    }

}
