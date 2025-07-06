/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import dao.CourseDAO;
import dao.CategoryDAO;
import jakarta.servlet.http.HttpSession;
import model.Course;
import model.Category;
import model.SuperUser;

/**
 * Home page controller that shows featured courses.
 */
@WebServlet(name="CustomerHomeServlet", urlPatterns={ "/home", "" })
public class CustomerHomeServlet extends HttpServlet {
   
    private CourseDAO courseDAO;
    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        courseDAO = new CourseDAO();
        categoryDAO = new CategoryDAO();
    }

    /**
     * Handles the HTTP GET request - displaying the home page.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get categories for sidebar
        List<Category> categories = categoryDAO.getAllCategories();
        request.setAttribute("categories", categories);

        try {
            // Get featured courses (limit to 8 courses for homepage)
            List<Course> featuredCourses = courseDAO.getAllWithLimit(0, 8);
            request.setAttribute("featuredCourses", featuredCourses);

            // Get total courses count
            int totalCourses = courseDAO.countAllCourses("approved");
            request.setAttribute("totalCourses", totalCourses);
        } catch (Exception e) {
            System.err.println("Error loading courses: " + e.getMessage());
            e.printStackTrace();
        }

        // Get cart
        HttpSession session = request.getSession();
        SuperUser user = (SuperUser) session.getAttribute("user");

        // Forward to home page
        request.getRequestDispatcher("/WEB-INF/views/customer/manage-courses/homepage.jsp").forward(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Home Servlet - Displays the homepage with featured courses";
    }
}
