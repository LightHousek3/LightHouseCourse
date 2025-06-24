/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller.instructor;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import dao.CourseDAO;
import dao.DiscussionDAO;
import dao.InstructorDAO;
import dao.RatingDAO;
import model.Course;
import model.Instructor;
import model.SuperUser;

/**
 *
 * @author DangPH - CE180896
 */
@WebServlet(name = "InstructorDashboardServlet", urlPatterns = { "/instructor/dashboard" })
public class InstructorDashboardServlet extends HttpServlet {

    private InstructorDAO instructorDAO;
    private CourseDAO courseDAO;
    private DiscussionDAO discussionDAO;
    private RatingDAO ratingDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        instructorDAO = new InstructorDAO();
        courseDAO = new CourseDAO();
        discussionDAO = new DiscussionDAO();
        ratingDAO = new RatingDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check if user is logged in
        HttpSession session = request.getSession();
        // Test account si exist
        SuperUser superUser = new SuperUser();
        superUser.setSuperUserID(4);
        superUser.setAvatar("/assets/imgs/avatars/instructor1.png");
        session.setAttribute("user", superUser);
        // End test
        SuperUser user = (SuperUser) session.getAttribute("user");

        // Get instructor information using getInstructorBySuperUserId
        Instructor instructor = instructorDAO.getInstructorBySuperUserId(user.getSuperUserID());
        if (instructor == null) {
            // If no instructor record exists, redirect to 404 page
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Get dashboard statistics
        populateDashboardStatistics(request, instructor);

        // Add instructor to request attributes
        request.setAttribute("avatar", user.getAvatar());
        request.setAttribute("instructor", instructor);

        // Forward to dashboard page
        request.getRequestDispatcher("/WEB-INF/views/instructor/dashboard.jsp").forward(request, response);
    }

    /**
     * Populates the request with dashboard statistics
     */
    private void populateDashboardStatistics(HttpServletRequest request, Instructor instructor) {
        // Get statistical information
        int instructorId = instructor.getInstructorID();

        // Get total courses for this instructor
        int totalCourses = courseDAO.getCoursesCountByInstructorId(instructorId);

        // Get total students enrolled in instructor's courses
        int totalStudents = courseDAO.getStudentsCountByInstructorId(instructorId);
        
        // Get average rating for instructor's courses
        double averageRating = ratingDAO.getAverageRatingByInstructorId(instructorId);

        // Get recent courses (limit to 5)
        List<Course> recentCourses = courseDAO.getRecentCoursesByInstructorId(instructorId, 5);

        // Get courses by status
        List<Course> allCourses = courseDAO.getCoursesByInstructorId(instructorId);
        int pendingCourses = 0;
        int approvedCourses = 0;
        int rejectedCourses = 0;
        int draftCourses = 0;

        for (Course course : allCourses) {
            String status = course.getApprovalStatus();
            if ("pending".equalsIgnoreCase(status)) {
                pendingCourses++;
            } else if ("approved".equalsIgnoreCase(status)) {
                approvedCourses++;
            } else if ("rejected".equalsIgnoreCase(status)) {
                rejectedCourses++;
            } else if ("draft".equalsIgnoreCase(status)) {
                draftCourses++;
            }
        }

        // Get unresolved discussions count
        int unresolvedDiscussions = 0;
        if (!allCourses.isEmpty()) {
            // Extract course IDs for all instructor's courses
            List<Integer> courseIds = new ArrayList<>();
            for (Course course : allCourses) {
                courseIds.add(course.getCourseID());
            }

            // Get discussion counts - passing null for courseId and lessonId, false for
            // resolved, and null for searchTerm
            unresolvedDiscussions = discussionDAO.countDiscussionsForCourses(courseIds, null, null, false, null);
        }

        // Set attributes for the view
        request.setAttribute("totalCourses", totalCourses);
        request.setAttribute("totalStudents", totalStudents);
        request.setAttribute("averageRating", averageRating);
        request.setAttribute("recentCourses", recentCourses);
        request.setAttribute("pendingCourses", pendingCourses);
        request.setAttribute("approvedCourses", approvedCourses);
        request.setAttribute("rejectedCourses", rejectedCourses);
        request.setAttribute("draftCourses", draftCourses);
        request.setAttribute("unresolvedDiscussions", unresolvedDiscussions);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check if user is logged in
        HttpSession session = request.getSession();
        SuperUser user = (SuperUser) session.getAttribute("user");

        if (user == null) {
            // Redirect to login if not logged in
            response.sendRedirect(request.getContextPath() + "/auth/login?redirect=/instructor/dashboard");
            return;
        }

        // Check if user is an instructor
        if (!"instructor".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/become-instructor");
            return;
        }

        // Get the action parameter to determine what to do
        String action = request.getParameter("action");

        if (action == null) {
            // If no action specified, just show the dashboard
            doGet(request, response);
            return;
        }

        // Handle dashboard-specific actions if needed
        switch (action) {
            case "refresh_stats":
                // Just refresh the stats by calling doGet
                doGet(request, response);
                break;
            default:
                // For unknown actions, redirect to dashboard
                response.sendRedirect(request.getContextPath() + "/instructor/dashboard");
                break;
        }
    }
}
