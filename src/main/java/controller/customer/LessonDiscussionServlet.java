package controller.customer;

import dao.DiscussionDAO;
import dao.DiscussionReplyDAO;
import dao.LessonDAO;
import dao.CustomerDAO;
import model.Discussion;
import model.DiscussionReply;
import model.Lesson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import model.Customer;

@WebServlet(urlPatterns = {
        "/lesson/discussions/*", // GET: Get discussions for a lesson
        "/lesson/discussion/create", // POST: Create a new discussion
        "/lesson/discussion/reply", // POST: Add a reply to a discussion
        "/lesson/discussion/update", // POST: Update a discussion
        "/lesson/discussion/delete", // POST: Delete a discussion
        "/lesson/discussion/reply/update", // POST: Update a reply
        "/lesson/discussion/reply/delete" // POST: Delete a reply
})
public class LessonDiscussionServlet extends HttpServlet {

    private DiscussionDAO discussionDAO;
    private DiscussionReplyDAO replyDAO;
    private LessonDAO lessonDAO;
    private CustomerDAO CustomerDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        discussionDAO = new DiscussionDAO();
        replyDAO = new DiscussionReplyDAO();
        lessonDAO = new LessonDAO();
        CustomerDAO = new CustomerDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Customer currentUser = getCustomerFromSession(request);
        if (currentUser == null) {
            redirectToLogin(request, response);
            return;
        }

        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.startsWith("/")) {
            try {
                int lessonId = Integer.parseInt(pathInfo.substring(1));
                handleGetDiscussionsByLesson(request, response, lessonId, currentUser);
            } catch (NumberFormatException e) {
                sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid lesson ID format.");
            }
        } else {
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Lesson ID is required.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Customer currentUser = getCustomerFromSession(request);
        if (currentUser == null) {
            redirectToLogin(request, response);
            return;
        }

        String servletPath = request.getServletPath();
        if (null == servletPath) {
            sendJsonError(response, HttpServletResponse.SC_NOT_FOUND, "Unknown endpoint.");
        } else
            switch (servletPath) {
                case "/lesson/discussion/create":
                    handleCreateDiscussion(request, response, currentUser);
                    break;
                case "/lesson/discussion/reply":
                    handleCreateReply(request, response, currentUser);
                    break;
                case "/lesson/discussion/update":
                    handleUpdateDiscussion(request, response, currentUser);
                    break;
                case "/lesson/discussion/delete":
                    handleDeleteDiscussion(request, response, currentUser);
                    break;
                case "/lesson/discussion/reply/update":
                    handleUpdateReply(request, response, currentUser);
                    break;
                case "/lesson/discussion/reply/delete":
                    handleDeleteReply(request, response, currentUser);
                    break;
                default:
                    sendJsonError(response, HttpServletResponse.SC_NOT_FOUND, "Unknown endpoint.");
                    break;
            }
    }

    /**
     * Get discussions for a specific lesson
     */
    private void handleGetDiscussionsByLesson(HttpServletRequest request, HttpServletResponse response,
            int lessonId, Customer currentUser) throws IOException {

        // Verify the lesson exists
        Lesson lesson = lessonDAO.getLessonById(lessonId);
        if (lesson == null) {
            sendJsonError(response, HttpServletResponse.SC_NOT_FOUND, "Lesson not found.");
            return;
        }

        // Get all discussions for this lesson
        List<Discussion> discussions = discussionDAO.getDiscussionsByLessonId(lessonId);

        // For each discussion, load its replies
        for (Discussion discussion : discussions) {
            List<DiscussionReply> replies = replyDAO.getRepliesByDiscussionId(discussion.getDiscussionID());

            // Add user information to each reply
            for (DiscussionReply reply : replies) {
                Customer replyUser = CustomerDAO.getCustomerById(reply.getAuthorID());
                if (replyUser != null) {
                    reply.setUserName(replyUser.getFullName());
                }
            }

            discussion.setReplies(replies);
            discussion.setReplyCount(replies.size());
        }

        // Send as JSON response
        sendJsonResponse(response, createDiscussionsJson(discussions, currentUser));
    }

    /**
     * Create a new discussion for a lesson
     */
    private void handleCreateDiscussion(HttpServletRequest request, HttpServletResponse response, Customer currentUser)
            throws IOException {

        try {
            int lessonId = Integer.parseInt(request.getParameter("lessonId"));
            int courseId = Integer.parseInt(request.getParameter("courseId"));
            String content = request.getParameter("content");
            System.out.println(" lessonID" + lessonId);
            System.out.println(" courseId" + courseId);
            System.out.println(" content" + content);

            // Basic validation
            if (content == null || content.trim().isEmpty()) {
                sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Title and content are required.");
                return;
            }

            // Create and save the discussion
            Discussion discussion = new Discussion(courseId, lessonId, currentUser.getCustomerID(), content);
            int discussionId = discussionDAO.createDiscussion(discussion);

            if (discussionId > 0) {
                // Successfully created
                discussion = discussionDAO.getDiscussionById(discussionId);
                discussion.setUserName(currentUser.getFullName());

                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Discussion created successfully.");
                jsonResponse.add("discussion", createDiscussionJson(discussion, currentUser));

                sendJsonResponse(response, jsonResponse);
            } else {
                sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create discussion.");
            }

        } catch (NumberFormatException e) {
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid lessonId or courseId format.");
        }
    }

    /**
     * Add a reply to an existing discussion
     */
    private void handleCreateReply(HttpServletRequest request, HttpServletResponse response, Customer currentUser)
            throws IOException {

        try {
            int discussionId = Integer.parseInt(request.getParameter("discussionId"));
            String content = request.getParameter("content");

            // Basic validation
            if (content == null || content.trim().isEmpty()) {
                sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Reply content is required.");
                return;
            }

            // Get the discussion to verify it exists
            Discussion discussion = discussionDAO.getDiscussionById(discussionId);
            if (discussion == null) {
                sendJsonError(response, HttpServletResponse.SC_NOT_FOUND, "Discussion not found.");
                return;
            }

            // Create and save the reply
            DiscussionReply reply = new DiscussionReply(discussionId, currentUser.getCustomerID(),
                    content);

            boolean success = replyDAO.createReply(reply);

            if (success) {
                // Get the complete reply with ID
                reply = replyDAO.getReplyById(reply.getReplyID());
                reply.setUserName(currentUser.getFullName());

                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Reply added successfully.");
                jsonResponse.add("reply", createReplyJson(reply, currentUser));

                sendJsonResponse(response, jsonResponse);
            } else {
                sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create reply.");
            }

        } catch (NumberFormatException e) {
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid discussionId format.");
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An error occurred: " + e.getMessage());
        }
    }

    /**
     * Update an existing discussion
     */
    private void handleUpdateDiscussion(HttpServletRequest request, HttpServletResponse response, Customer currentUser)
            throws IOException {

        try {
            int discussionId = Integer.parseInt(request.getParameter("discussionId"));
            String content = request.getParameter("content");

            // Basic validation
            if (content == null || content.trim().isEmpty()) {
                sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Discussion content is required.");
                return;
            }

            // Update the discussion
            boolean success = discussionDAO.updateDiscussion(discussionId, currentUser.getCustomerID(), content);

            if (success) {
                // Get the updated discussion
                Discussion discussion = discussionDAO.getDiscussionById(discussionId);

                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Discussion updated successfully.");
                jsonResponse.add("discussion", createDiscussionJson(discussion, currentUser));

                sendJsonResponse(response, jsonResponse);
            } else {
                sendJsonError(response, HttpServletResponse.SC_FORBIDDEN,
                        "Failed to update discussion. You may not have permission to edit this discussion.");
            }

        } catch (NumberFormatException e) {
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid discussion ID format.");
        }
    }

    /**
     * Delete a discussion
     */
    private void handleDeleteDiscussion(HttpServletRequest request, HttpServletResponse response, Customer currentUser)
            throws IOException {

        try {
            int discussionId = Integer.parseInt(request.getParameter("discussionId"));

            // Delete the discussion
            boolean success = discussionDAO.deleteDiscussion(discussionId, currentUser.getCustomerID());

            if (success) {
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Discussion deleted successfully.");
                jsonResponse.addProperty("discussionId", discussionId);

                sendJsonResponse(response, jsonResponse);
            } else {
                sendJsonError(response, HttpServletResponse.SC_FORBIDDEN,
                        "Failed to delete discussion. You may not have permission to delete this discussion.");
            }

        } catch (NumberFormatException e) {
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid discussion ID format.");
        }
    }

    /**
     * Update an existing reply
     */
    private void handleUpdateReply(HttpServletRequest request, HttpServletResponse response, Customer currentUser)
            throws IOException {

        try {
            int replyId = Integer.parseInt(request.getParameter("replyId"));
            String content = request.getParameter("content");

            // Basic validation
            if (content == null || content.trim().isEmpty()) {
                sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Reply content is required.");
                return;
            }

            // Update the reply
            boolean success = replyDAO.updateReply(replyId, currentUser.getCustomerID(), "customer", content);

            if (success) {
                // Get the updated reply
                DiscussionReply reply = replyDAO.getReplyById(replyId);
                reply.setUserName(currentUser.getFullName());

                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Reply updated successfully.");
                jsonResponse.add("reply", createReplyJson(reply));

                sendJsonResponse(response, jsonResponse);
            } else {
                sendJsonError(response, HttpServletResponse.SC_FORBIDDEN,
                        "Failed to update reply. You may not have permission to edit this reply.");
            }

        } catch (NumberFormatException e) {
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid reply ID format.");
        }
    }

    /**
     * Delete a reply
     */
    private void handleDeleteReply(HttpServletRequest request, HttpServletResponse response, Customer currentUser)
            throws IOException {

        try {
            int replyId = Integer.parseInt(request.getParameter("replyId"));

            // Delete the reply
            boolean success = replyDAO.deleteReply(replyId, currentUser.getCustomerID(), "customer");

            if (success) {
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Reply deleted successfully.");
                jsonResponse.addProperty("replyId", replyId);

                sendJsonResponse(response, jsonResponse);
            } else {
                sendJsonError(response, HttpServletResponse.SC_FORBIDDEN,
                        "Failed to delete reply. You may not have permission to delete this reply.");
            }

        } catch (NumberFormatException e) {
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid reply ID format.");
        }
    }

    /**
     * Create a JSON object for a discussion
     * 
     * @param discussion  The discussion
     * @param currentUser The current user
     * @return A JSON object
     */
    private JsonObject createDiscussionJson(Discussion discussion, Customer currentUser) {
        JsonObject json = new JsonObject();
        json.addProperty("id", discussion.getDiscussionID());
        json.addProperty("courseId", discussion.getCourseID());
        json.addProperty("lessonId", discussion.getLessonID());
        json.addProperty("authorId", discussion.getAuthorID());
        json.addProperty("authorType", discussion.getAuthorType());
        json.addProperty("content", discussion.getContent());
        json.addProperty("createdAt", discussion.getCreatedAt() != null ? discussion.getCreatedAt().toString() : null);
        json.addProperty("updatedAt", discussion.getUpdatedAt() != null ? discussion.getUpdatedAt().toString() : null);
        json.addProperty("isResolved", discussion.isResolved());
        json.addProperty("userName", discussion.getAuthorName() != null ? discussion.getAuthorName() : "Unknown");
        json.addProperty("replyCount", discussion.getReplyCount());

        // Add isAuthor flag to indicate if the current user is the author
        boolean isAuthor = currentUser != null &&
                discussion.getAuthorID() == currentUser.getCustomerID() &&
                "customer".equals(discussion.getAuthorType());
        json.addProperty("isAuthor", isAuthor);

        // Add replies if available
        if (discussion.getReplies() != null && !discussion.getReplies().isEmpty()) {
            JsonArray repliesJson = new JsonArray();
            for (DiscussionReply reply : discussion.getReplies()) {
                repliesJson.add(createReplyJson(reply, currentUser));
            }
            json.add("replies", repliesJson);
        }

        return json;
    }

    /**
     * Create a JSON array for a list of discussions
     * 
     * @param discussions The list of discussions
     * @param currentUser The current user
     * @return A JSON object with the discussions array
     */
    private JsonObject createDiscussionsJson(List<Discussion> discussions, Customer currentUser) {
        JsonObject json = new JsonObject();
        JsonArray discussionsArray = new JsonArray();

        for (Discussion discussion : discussions) {
            discussionsArray.add(createDiscussionJson(discussion, currentUser));
        }

        json.addProperty("success", true);
        json.add("discussions", discussionsArray);
        return json;
    }

    /**
     * Create a JSON object for a reply
     * 
     * @param reply The reply
     * @return A JSON object
     */
    private JsonObject createReplyJson(DiscussionReply reply) {
        return createReplyJson(reply, null);
    }

    /**
     * Create a JSON object for a reply
     * 
     * @param reply       The reply
     * @param currentUser The current user
     * @return A JSON object
     */
    private JsonObject createReplyJson(DiscussionReply reply, Customer currentUser) {
        JsonObject json = new JsonObject();
        json.addProperty("id", reply.getReplyID());
        json.addProperty("discussionId", reply.getDiscussionID());
        json.addProperty("authorId", reply.getAuthorID());
        json.addProperty("authorType", reply.getAuthorType());
        json.addProperty("content", reply.getContent());
        json.addProperty("createdAt", reply.getCreatedAt() != null ? reply.getCreatedAt().toString() : null);
        json.addProperty("updatedAt", reply.getUpdatedAt() != null ? reply.getUpdatedAt().toString() : null);
        json.addProperty("userName", reply.getAuthorName() != null ? reply.getAuthorName() : "Unknown");
        json.addProperty("isInstructorReply", "instructor".equals(reply.getAuthorType()));

        // Add isAuthor flag to indicate if the current user is the author
        boolean isAuthor = currentUser != null &&
                reply.getAuthorID() == currentUser.getCustomerID() &&
                "customer".equals(reply.getAuthorType());
        json.addProperty("isAuthor", isAuthor);

        return json;
    }

    /**
     * Send a JSON error response
     * 
     * @param response The HTTP response
     * @param status   The HTTP status code
     * @param message  The error message
     * @throws IOException If an I/O error occurs
     */
    private void sendJsonError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JsonObject json = new JsonObject();
        json.addProperty("success", false);
        json.addProperty("message", message);

        PrintWriter out = response.getWriter();
        out.print(json.toString());
        out.flush();
    }

    /**
     * Send a JSON response
     * 
     * @param response The HTTP response
     * @param json     The JSON object to send
     * @throws IOException If an I/O error occurs
     */
    private void sendJsonResponse(HttpServletResponse response, JsonObject json) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.print(json.toString());
        out.flush();
    }

    /**
     * Get the customer from the session
     * 
     * @param request The HTTP request
     * @return The customer, or null if not logged in
     */
    private Customer getCustomerFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (Customer) session.getAttribute("user");
        }
        return null;
    }

    /**
     * Redirect to the login page
     * 
     * @param request  The HTTP request
     * @param response The HTTP response
     * @throws IOException If an I/O error occurs
     */
    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/login");
    }
}
