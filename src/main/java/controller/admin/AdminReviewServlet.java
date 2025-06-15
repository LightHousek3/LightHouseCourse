/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.admin;

import dao.CourseDAO;
import dao.RatingDAO;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

import model.Course;
import model.Rating;

/**
 *
 * @author DATLT-CE181501
 */
@WebServlet(name = "AdminRatingServlet", urlPatterns = {"/admin/reviews"})
public class AdminReviewServlet extends HttpServlet {

    private RatingDAO ratingDAO;
    private CourseDAO courseDAO;

    @Override
    public void init() throws SecurityException {
        ratingDAO = new RatingDAO();
        courseDAO = new CourseDAO();
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

        // Get filtering parameters
        String courseIdParam = request.getParameter("courseId");
        String ratingParam = request.getParameter("rating");

        List<Rating> ratings;

        // Apply filters if provided
        if (courseIdParam != null && !courseIdParam.isEmpty() && ratingParam != null && !ratingParam.isEmpty()) {
            try {
                int courseId = Integer.parseInt(courseIdParam);
                int stars = Integer.parseInt(ratingParam);
                ratings = ratingDAO.getRatingsByCourseIdAndStar(courseId, stars); 
            } catch (NumberFormatException e) {
                ratings = ratingDAO.getAllRatings();
            }
        } else if (courseIdParam != null && !courseIdParam.isEmpty()) {
            try {
                int courseId = Integer.parseInt(courseIdParam);
                ratings = ratingDAO.getRatingsByCourseId(courseId);
            } catch (NumberFormatException e) {
                ratings = ratingDAO.getAllRatings();
            }
        } else if (ratingParam != null && !ratingParam.isEmpty()) {
            try {
                int stars = Integer.parseInt(ratingParam);
                ratings = ratingDAO.getRatingsByStar(stars);
            } catch (NumberFormatException e) {
                ratings = ratingDAO.getAllRatings();
            }
        } else {
            ratings = ratingDAO.getAllRatings();
        }

        // Get all course for the dropdown
        List<Course> courses = courseDAO.getAllCoursesWithLimit(0, 0, null, null);

        request.setAttribute("ratings", ratings);
        request.setAttribute("courses", courses);
        request.getRequestDispatcher("/WEB-INF/views/admin/manage-reviews/review-list.jsp").forward(request, response);
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
        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            // Delete a rating
            handleDeleteRating(request, response);
        } else {
            // Unsupported operation
            response.sendRedirect(request.getContextPath() + "/admin/manage-reviews/review-list.jsp?success=false&message=Unsupported+operation");
        }
    }

    private void handleDeleteRating(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int ratingId = Integer.parseInt(request.getParameter("ratingId"));

            // Delete the rating
            boolean deleted = ratingDAO.deleteReview(ratingId);

            // Redirect back to ratings page with success/error message
            String redirectUrl = request.getContextPath() + "/admin/reviews";
            if (deleted) {
                redirectUrl += "?success=true&message=Rating+deleted+successfully";
            } else {
                redirectUrl += "?success=false&message=Failed+to+delete+rating";
            }

            response.sendRedirect(redirectUrl);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-reviews/review-list?success=false&message=Invalid+rating+ID");
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
