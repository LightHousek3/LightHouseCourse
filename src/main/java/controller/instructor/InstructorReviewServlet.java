/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.instructor;

import dao.CourseDAO;
import dao.InstructorDAO;
import dao.RatingDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import model.Course;
import model.Instructor;
import model.Rating;
import model.SuperUser;

/**
 *
 * @author Le Thinh - CE180136
 */
@WebServlet(name = "InstructorReviewServlet", urlPatterns = {"/instructor/reviews"})
public class InstructorReviewServlet extends HttpServlet {

    private RatingDAO ratingDAO;
    private CourseDAO courseDAO;
    private InstructorDAO instructorDAO;

    @Override
    public void init() throws SecurityException {
        ratingDAO = new RatingDAO();
        courseDAO = new CourseDAO();
        instructorDAO = new InstructorDAO();
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

        // Check if user is logged in
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
        // Get instructor information using getInstructorBySuperUserId
        Instructor instructor = instructorDAO.getInstructorBySuperUserId(user.getSuperUserID());
        if (instructor == null) {
            // If no instructor record exists, redirect to 404 page
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Integer instructorId = instructor.getInstructorID();

        // Get filter form requests
        String courseIdParam = request.getParameter("courseId");
        String ratingParam = request.getParameter("rating");
        Integer courseId = null;
        Integer stars = null;
        String errorMessage = null;

        // Validate ID course
        if (courseIdParam != null && !courseIdParam.isEmpty()) {
            try {
                courseId = Integer.parseInt(courseIdParam);
            } catch (NumberFormatException e) {
                errorMessage = "Invalid id value! Only numbers are allowed.";
                request.setAttribute("errorMessage", errorMessage);
                request.getRequestDispatcher("/WEB-INF/views/instructor/manage-review/review-list.jsp").forward(request, response);
                return;
            }
        }

        // Validate rating
        if (ratingParam != null && !ratingParam.isEmpty()) {

            try {
                stars = Integer.parseInt(ratingParam);
                if (stars < 1 || stars > 5) {
                    errorMessage = "Invalid rating value! Rating must be between 1 and 5.";
                    request.setAttribute("errorMessage", errorMessage);
                    request.getRequestDispatcher("/WEB-INF/views/instructor/manage-review/review-list.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                errorMessage = "Invalid rating value! Only numbers are allowed.";
                request.setAttribute("errorMessage", errorMessage);
                request.getRequestDispatcher("/WEB-INF/views/instructor/manage-review/review-list.jsp").forward(request, response);
                return;

            }
        }

        // Get current page
        int page = 1;
        int pageSize = 6;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) {
                    page = 1;
                }
            } catch (NumberFormatException e) {
                errorMessage = "Invalid page number!";
                page = 1;
            }
        }

        int offset = (page - 1) * pageSize;
        // Get total number review (based filter)
        int totalReviews = 0;
        if (courseId != null && stars != null) {
            totalReviews = ratingDAO.countByInstructorCourseAndStars(instructorId, courseId, stars);
        } else if (courseId != null) {
            totalReviews = ratingDAO.countByInstructorAndCourse(instructorId, courseId);
        } else if (stars != null) {
            totalReviews = ratingDAO.countByInstructorAndStars(instructorId, stars);
        } else {
            totalReviews = ratingDAO.getTotalRatingsByInstructorId(instructorId);
        }
        int totalPages = (int) Math.ceil((double) totalReviews / pageSize);

        // Get list review của instructor
        List<Rating> ratings;
        if (courseId != null && stars != null) {
            ratings = ratingDAO.getRatingsByInstructorIdAndCourseIdAndStarsPaged(instructorId, courseId, stars, offset, pageSize);
        } else if (courseId != null) {
            ratings = ratingDAO.getRatingsByInstructorIdAndCourseIdPaged(instructorId, courseId, offset, pageSize);
        } else if (stars != null) {
            ratings = ratingDAO.getRatingsByInstructorIdAndStarsPaged(instructorId, stars, offset, pageSize);
        } else {
            ratings = ratingDAO.getRatingsByInstructorIdPaged(instructorId, offset, pageSize);
        }

        // Get list course của instructor to filter
        List<Course> courses = courseDAO.getCoursesByInstructorId(instructorId);

        request.setAttribute("ratings", ratings);
        request.setAttribute("courses", courses);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("courseId", courseId);
        request.setAttribute("rating", stars);
        request.setAttribute("errorMessage", errorMessage);
        request.getRequestDispatcher("/WEB-INF/views/instructor/manage-review/review-list.jsp").forward(request, response);
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
