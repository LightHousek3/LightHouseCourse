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
    "/lesson/discussion/reply" // POST: Add a reply to a discussion
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
        } else switch (servletPath) {
            case "/lesson/discussion/create":
                
                handleCreateDiscussion(request, response, currentUser);
                break;
            case "/lesson/discussion/reply":
                handleCreateReply(request, response, currentUser);
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
                jsonResponse.add("reply", createReplyJson(reply));

                sendJsonResponse(response, jsonResponse);
            } else {
                sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create reply.");
            }

        } catch (NumberFormatException e) {
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid discussionId format.");
        }
    }

    /**
     * Create JSON representation of discussions list
     */
    private JsonObject createDiscussionsJson(List<Discussion> discussions, Customer currentUser) {
        JsonObject result = new JsonObject();
        JsonArray discussionsArray = new JsonArray();

        for (Discussion discussion : discussions) {
            discussionsArray.add(createDiscussionJson(discussion, currentUser));
        }

        result.addProperty("success", true);
        result.addProperty("count", discussions.size());
        result.add("discussions", discussionsArray);

        return result;
    }

    /**
     * Create JSON representation of a single discussion
     */
    private JsonObject createDiscussionJson(Discussion discussion, Customer currentUser) {
        JsonObject json = new JsonObject();

        json.addProperty("id", discussion.getDiscussionID());
        json.addProperty("content", discussion.getContent());
        json.addProperty("createdAt", discussion.getCreatedAt() != null ? discussion.getCreatedAt().toString() : "");
        json.addProperty("updatedAt", discussion.getUpdatedAt() != null ? discussion.getUpdatedAt().toString() : "");
        json.addProperty("isResolved", discussion.isResolved());
        json.addProperty("userName", discussion.getUserName());
        json.addProperty("courseName", discussion.getCourseName());
        json.addProperty("lessonTitle", discussion.getLessonTitle() != null ? discussion.getLessonTitle() : "");
        json.addProperty("replyCount", discussion.getReplyCount());
        json.addProperty("isOwner", discussion.getAuthorID()== currentUser.getCustomerID());

        // Include replies if available
        if (discussion.getReplies() != null) {
            JsonArray repliesArray = new JsonArray();

            for (DiscussionReply reply : discussion.getReplies()) {
                repliesArray.add(createReplyJson(reply));
            }
            
            json.add("replies", repliesArray);
        }

        return json;
    }

    /**
     * Create JSON representation of a discussion reply
     */
    private JsonObject createReplyJson(DiscussionReply reply) {
        JsonObject json = new JsonObject();

        json.addProperty("id", reply.getReplyID());
        json.addProperty("discussionId", reply.getDiscussionID());
        json.addProperty("content", reply.getContent());
        json.addProperty("createdAt", reply.getCreatedAt() != null ? reply.getCreatedAt().toString() : "");
        json.addProperty("updatedAt", reply.getUpdatedAt() != null ? reply.getUpdatedAt().toString() : "");
        json.addProperty("userName", reply.getUserName());
        return json;
    }

    /**
     * Send JSON error response
     */
    private void sendJsonError(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JsonObject error = new JsonObject();
        error.addProperty("success", false);
        error.addProperty("message", message);

        PrintWriter out = response.getWriter();
        out.print(error.toString());
        out.flush();
    }

    /**
     * Send JSON success response
     */
    private void sendJsonResponse(HttpServletResponse response, JsonObject json) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.print(json.toString());
        out.flush();
    }
    
    /**
     * Gets the customer from the session.
     */
    private Customer getCustomerFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null ? (Customer) session.getAttribute("user") : null;
    }

    /**
     * Redirects to the login page.
     */
    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/login");
    }
}
