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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import dao.CourseDAO;
import dao.DiscussionDAO;
import dao.DiscussionReplyDAO;
import dao.InstructorDAO;
import model.Course;
import model.Discussion;
import model.DiscussionReply;
import model.Instructor;
import model.SuperUser;

/**
 * Servlet for instructor discussion management
 *
 * @author DangPH - CE180896
 */
@WebServlet(name = "InstructorDiscussionServlet", urlPatterns = { "/instructor/discussions",
        "/instructor/discussions/view/*", "/instructor/discussions/getReply/*" })
public class InstructorDiscussionServlet extends HttpServlet {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final DiscussionDAO discussionDAO = new DiscussionDAO();
    private final DiscussionReplyDAO replyDAO = new DiscussionReplyDAO();
    private final InstructorDAO instructorDAO = new InstructorDAO();
    private final CourseDAO courseDAO = new CourseDAO();

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

        String servletPath = request.getServletPath();

        switch (servletPath) {
            case "/instructor/discussions/view":
                viewDiscussion(request, response, instructor);
                break;
            case "/instructor/discussions/getReply":
                getReplyForEdit(request, response, instructor);
                break;
            default:
                listDiscussions(request, response, instructor);
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
        HttpSession session = request.getSession();
        SuperUser user;
        
        try {
            user = (SuperUser) session.getAttribute("user");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Get instructor information
        Instructor instructor = instructorDAO.getInstructorBySuperUserId(user.getSuperUserID());
        if (instructor == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String action = request.getParameter("action");

        if (action != null) {
            switch (action) {
                case "reply":
                    addReply(request, response, instructor);
                    break;
                case "mark_resolved":
                    updateDiscussionStatus(request, response, instructor);
                    break;
                case "delete_reply":
                    deleteReply(request, response, instructor);
                    break;
                case "edit_reply":
                    editReply(request, response, instructor);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/instructor/discussions");
                    break;
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/instructor/discussions");
        }
    }

    /**
     * Display the list of discussions for courses taught by the instructor
     *
     * @param request    The HTTP request
     * @param response   The HTTP response
     * @param instructor The instructor user
     * @throws ServletException If a servlet-specific error occurs
     * @throws IOException      If an I/O error occurs
     */
    private void listDiscussions(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {
        // Get instructor's courses
        List<Integer> instructorCourseIds = instructorDAO.getInstructorCourseIds(instructor.getInstructorID());
        List<Course> instructorCourses = courseDAO.getCoursesByInstructorId(instructor.getInstructorID());

        if (instructorCourseIds.isEmpty()) {
            request.setAttribute("discussions", new ArrayList<>());
            request.setAttribute("totalPages", 0);
            request.setAttribute("currentPage", 1);
            request.getRequestDispatcher("/WEB-INF/views/instructor/manage-discussions/discussion-list.jsp").forward(request,
                    response);
            return;
        }

        // Get filter parameters
        Integer courseId = null;
        String courseIdParam = request.getParameter("courseId");
        if (courseIdParam != null && !courseIdParam.isEmpty()) {
            try {
                courseId = Integer.parseInt(courseIdParam);
                // Verify that the instructor teaches this course
                if (!instructorCourseIds.contains(courseId)) {
                    courseId = null;
                }
            } catch (NumberFormatException e) {
                // Invalid course ID, ignore
            }
        }

        Integer lessonId = null;
        String lessonIdParam = request.getParameter("lessonId");
        if (lessonIdParam != null && !lessonIdParam.isEmpty() && courseId != null) {
            try {
                lessonId = Integer.parseInt(lessonIdParam);
            } catch (NumberFormatException e) {
                // Invalid lesson ID, ignore
            }
        }

        Boolean resolved = null;
        String resolvedParam = request.getParameter("resolved");
        if (resolvedParam != null && !resolvedParam.isEmpty()) {
            if (resolvedParam.equals("true")) {
                resolved = true;
            } else if (resolvedParam.equals("false")) {
                resolved = false;
            }
        }

        // Get pagination parameters
        int page = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) {
                    page = 1;
                }
            } catch (NumberFormatException e) {
                // Invalid page number, use default
            }
        }

        // Get total count and calculate pages
        int totalDiscussions = discussionDAO.countDiscussionsForCourses(instructorCourseIds, courseId, lessonId,
                resolved, null);
        int totalPages = (int) Math.ceil((double) totalDiscussions / DEFAULT_PAGE_SIZE);

        // Ensure page is within valid range
        if (page > totalPages && totalPages > 0) {
            page = totalPages;
        }

        // Get discussions for the current page
        List<Discussion> discussions = discussionDAO.getDiscussionsForCourses(
                instructorCourseIds, courseId, lessonId, resolved, null, page, DEFAULT_PAGE_SIZE);

        // Get lessons for selected course if any
        List<Object[]> lessons = new ArrayList<>();
        if (courseId != null) {
            lessons = discussionDAO.getLessonsByCourseId(courseId);
        }

        // Set attributes for the view
        request.setAttribute("discussions", discussions);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", page);
        request.setAttribute("courseId", courseId);
        request.setAttribute("lessonId", lessonId);
        request.setAttribute("resolved", resolved);
        request.setAttribute("instructorCourses", instructorCourses);
        request.setAttribute("lessons", lessons);
        request.setAttribute("totalDiscussions", totalDiscussions);

        // Forward to the view
        request.getRequestDispatcher("/WEB-INF/views/instructor/manage-discussions/discussion-list.jsp").forward(request,
                response);
    }

    /**
     * View a single discussion and its replies
     *
     * @param request    The HTTP request
     * @param response   The HTTP response
     * @param instructor The instructor user
     * @throws ServletException If a servlet-specific error occurs
     * @throws IOException      If an I/O error occurs
     */
    private void viewDiscussion(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.split("/").length < 2) {
            response.sendRedirect(request.getContextPath() + "/instructor/discussions?error=not_found");
            return;
        }
        String discussionIdParam = pathInfo.split("/")[1];
        if (discussionIdParam == null || discussionIdParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/instructor/discussions?error=not_found");
            return;
        }

        try {
            int discussionId = Integer.parseInt(discussionIdParam);
            Discussion discussion = discussionDAO.getDiscussionById(discussionId);

            if (discussion == null) {
                response.sendRedirect(request.getContextPath() + "/instructor/discussions?error=not_found");
                return;
            }

            // Check if instructor teaches this course
            List<Integer> instructorCourseIds = instructorDAO.getInstructorCourseIds(instructor.getInstructorID());
            boolean canReply = instructorCourseIds.contains(discussion.getCourseID());

            // Get replies for this discussion
            List<DiscussionReply> replies = replyDAO.getRepliesByDiscussionId(discussionId);
            discussion.setReplies(replies);

            // Check if this discussion was automatically marked as unresolved
            // This would be determined by checking if the most recent reply is from a
            // customer and the discussion was previously resolved
            boolean wasAutoUnresolved = false;
            if (!replies.isEmpty()) {
                DiscussionReply latestReply = replies.get(replies.size() - 1);
                if ("customer".equals(latestReply.getAuthorType()) && !discussion.getIsResolved()) {
                    // Check if there's an instructor reply before this customer reply
                    boolean hasInstructorReplyBefore = false;
                    for (int i = replies.size() - 2; i >= 0; i--) {
                        if ("instructor".equals(replies.get(i).getAuthorType())) {
                            hasInstructorReplyBefore = true;
                            break;
                        }
                    }

                    if (hasInstructorReplyBefore) {
                        wasAutoUnresolved = true;
                        // Add a parameter to show the auto-unresolved notification
                        if (request.getParameter("success") == null) {
                            response.sendRedirect(request.getContextPath()
                                    + "/instructor/discussions/view/" + discussionId + "?success=auto_unresolved");
                            return;
                        }
                    }
                }
            }

            // Set attributes for the view
            request.setAttribute("discussion", discussion);
            request.setAttribute("canReply", canReply);
            request.setAttribute("wasAutoUnresolved", wasAutoUnresolved);

            // Forward to the view
            request.getRequestDispatcher("/WEB-INF/views/instructor/manage-discussions/view-discussion.jsp").forward(request,
                    response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/instructor/discussions?error=invalid_id");
        }
    }

    /**
     * Add a reply to a discussion
     *
     * @param request    The HTTP request
     * @param response   The HTTP response
     * @param instructor The instructor user
     * @throws ServletException If a servlet-specific error occurs
     * @throws IOException      If an I/O error occurs
     */
    private void addReply(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {
        String discussionIdParam = request.getParameter("discussionId");
        String content = request.getParameter("content");

        if (discussionIdParam == null || discussionIdParam.isEmpty() || content == null || content.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/instructor/discussions?error=empty_content");
            return;
        }

        try {
            int discussionId = Integer.parseInt(discussionIdParam);
            Discussion discussion = discussionDAO.getDiscussionById(discussionId);

            if (discussion == null) {
                response.sendRedirect(request.getContextPath() + "/instructor/discussions?error=not_found");
                return;
            }

            // Check if instructor teaches this course
            if (!replyDAO.isInstructorForCourse(instructor.getInstructorID(), discussion.getCourseID())) {
                response.sendRedirect(request.getContextPath()
                        + "/instructor/discussions/view/" + discussionId + "?error=not_authorized");
                return;
            }

            // Create and save the reply
            DiscussionReply reply = new DiscussionReply();
            reply.setDiscussionID(discussionId);
            reply.setAuthorID(instructor.getInstructorID());
            reply.setAuthorType("instructor");
            reply.setContent(content);
            reply.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));

            boolean success = replyDAO.addReply(reply);

            if (success) {
                response.sendRedirect(request.getContextPath()
                        + "/instructor/discussions/view/" + discussionId + "?success=reply_added");
            } else {
                response.sendRedirect(request.getContextPath()
                        + "/instructor/discussions/view/" + discussionId + "?error=reply_failed");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/instructor/discussions?error=invalid_id");
        }
    }

    /**
     * Update a discussion's resolved status
     *
     * @param request    The HTTP request
     * @param response   The HTTP response
     * @param instructor The instructor user
     * @throws ServletException If a servlet-specific error occurs
     * @throws IOException      If an I/O error occurs
     */
    private void updateDiscussionStatus(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {
        String discussionIdParam = request.getParameter("discussionId");
        String resolvedParam = request.getParameter("resolved");

        if (discussionIdParam == null || discussionIdParam.isEmpty() || resolvedParam == null
                || resolvedParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/instructor/discussions?error=missing_params");
            return;
        }

        try {
            int discussionId = Integer.parseInt(discussionIdParam);
            boolean resolved = Boolean.parseBoolean(resolvedParam);

            // Only allow marking discussions as resolved, not unresolved
            if (!resolved) {
                response.sendRedirect(request.getContextPath()
                        + "/instructor/discussions/view/" + discussionId + "?error=not_allowed");
                return;
            }

            Discussion discussion = discussionDAO.getDiscussionById(discussionId);

            if (discussion == null) {
                response.sendRedirect(request.getContextPath() + "/instructor/discussions?error=not_found");
                return;
            }

            // If discussion is already resolved, do nothing
            if (discussion.getIsResolved()) {
                response.sendRedirect(request.getContextPath()
                        + "/instructor/discussions/view/" + discussionId);
                return;
            }

            // Check if instructor teaches this course
            if (!replyDAO.isInstructorForCourse(instructor.getInstructorID(), discussion.getCourseID())) {
                response.sendRedirect(request.getContextPath()
                        + "/instructor/discussions/view/" + discussionId + "?error=not_authorized");
                return;
            }

            // Update the discussion status
            boolean success = discussionDAO.updateDiscussionResolved(discussionId, resolved);

            if (success) {
                // Just redirect
                response.sendRedirect(request.getContextPath()
                        + "/instructor/discussions/view/" + discussionId);
            } else {
                response.sendRedirect(request.getContextPath()
                        + "/instructor/discussions/view/" + discussionId + "?error=update_failed");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/instructor/discussions?error=invalid_id");
        }
    }

    /**
     * Delete a reply
     *
     * @param request    The HTTP request
     * @param response   The HTTP response
     * @param instructor The instructor user
     * @throws ServletException If a servlet-specific error occurs
     * @throws IOException      If an I/O error occurs
     */
    private void deleteReply(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {
        String replyIdParam = request.getParameter("replyId");
        String discussionIdParam = request.getParameter("discussionId");

        if (replyIdParam == null || replyIdParam.isEmpty() || discussionIdParam == null
                || discussionIdParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/instructor/discussions?error=missing_params");
            return;
        }

        try {
            int replyId = Integer.parseInt(replyIdParam);
            int discussionId = Integer.parseInt(discussionIdParam);

            // Delete the reply (only if it belongs to this instructor)
            boolean success = replyDAO.deleteReply(replyId, instructor.getInstructorID(), "instructor");

            if (success) {
                // Check if the discussion is resolved and if there are any instructor replies
                // left
                Discussion discussion = discussionDAO.getDiscussionById(discussionId);
                if (discussion != null && discussion.getIsResolved()) {
                    boolean hasInstructorReplies = replyDAO.hasInstructorReplies(discussionId);

                    // If no instructor replies left and discussion is resolved, set it back to
                    // unresolved
                    if (!hasInstructorReplies) {
                        discussionDAO.updateDiscussionResolved(discussionId, false);
                        response.sendRedirect(request.getContextPath()
                                + "/instructor/discussions/view/" + discussionId
                                + "?success=reply_deleted&auto_unresolved=true");
                        return;
                    }
                }

                response.sendRedirect(request.getContextPath()
                        + "/instructor/discussions/view/" + discussionId + "?success=reply_deleted");
            } else {
                response.sendRedirect(request.getContextPath()
                        + "/instructor/discussions/view/" + discussionId + "?error=delete_failed");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/instructor/discussions?error=invalid_id");
        }
    }

    /**
     * Edit a reply
     *
     * @param request    The HTTP request
     * @param response   The HTTP response
     * @param instructor The instructor user
     * @throws ServletException If a servlet-specific error occurs
     * @throws IOException      If an I/O error occurs
     */
    private void editReply(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {
        String replyIdParam = request.getParameter("replyId");
        String discussionIdParam = request.getParameter("discussionId");
        String content = request.getParameter("content");

        if (replyIdParam == null || replyIdParam.isEmpty()
                || discussionIdParam == null || discussionIdParam.isEmpty()
                || content == null || content.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/instructor/discussions?error=missing_params");
            return;
        }

        try {
            int replyId = Integer.parseInt(replyIdParam);
            int discussionId = Integer.parseInt(discussionIdParam);

            // Update the reply (only if it belongs to this instructor)
            boolean success = replyDAO.updateReply(replyId, instructor.getInstructorID(), "instructor", content);

            if (success) {
                response.sendRedirect(request.getContextPath()
                        + "/instructor/discussions/view/" + discussionId + "?success=reply_updated");
            } else {
                response.sendRedirect(request.getContextPath()
                        + "/instructor/discussions/view/" + discussionId + "?error=update_failed");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/instructor/discussions?error=invalid_id");
        }
    }

    /**
     * Get reply data for editing
     *
     * @param request    The HTTP request
     * @param response   The HTTP response
     * @param instructor The instructor user
     * @throws ServletException If a servlet-specific error occurs
     * @throws IOException      If an I/O error occurs
     */
    private void getReplyForEdit(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.split("/").length < 2) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Reply not found");
            return;
        }

        String replyIdStr = pathInfo.split("/")[1];
        try {
            int replyId = Integer.parseInt(replyIdStr);
            DiscussionReply reply = replyDAO.getReplyById(replyId);

            if (reply == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("Reply not found");
                return;
            }

            // Check if this reply belongs to the instructor
            if (reply.getAuthorID() != instructor.getInstructorID() || !"instructor".equals(reply.getAuthorType())) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Not authorized to edit this reply");
                return;
            }

            // Return the reply content
            response.setContentType("text/plain");
            response.getWriter().write(reply.getContent());

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid reply ID");
        }
    }
}
