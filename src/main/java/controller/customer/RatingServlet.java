package controller.customer;

import dao.CourseProgressDAO;
import dao.OrderDAO;
import dao.RatingDAO;
import model.CourseProgress;
import model.Rating;
import model.Customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import model.SuperUser;

/**
 * Controller for handling course rating operations.
 */
@WebServlet(name = "RatingServlet", urlPatterns = {"/rating/*"})
public class RatingServlet extends HttpServlet {

    private RatingDAO ratingDAO;
    private OrderDAO orderDAO;
    private CourseProgressDAO progressDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        ratingDAO = new RatingDAO();
        orderDAO = new OrderDAO();
        progressDAO = new CourseProgressDAO();
    }

    /**
     * Handles GET requests to the servlet.
     *
     * @param request
     * @param response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getPathInfo();

        // Check if the user is logged in
        Customer user = getLoggedInUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Handle different GET operations
        if (path == null || path.equals("/")) {
            // Default action - redirect to home
            response.sendRedirect(request.getContextPath() + "/");
        } else if (path.startsWith("/delete/")) {
            // Delete a rating
            handleDeleteRating(request, response, user);
        } else {
            // Unsupported operation
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Handles POST requests to the servlet.
     *
     * @param request
     * @param response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getPathInfo();

        // Check if the user is logged in
        Customer user = getLoggedInUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Handle different POST operations
        if (path == null || path.equals("/")) {
            // Add a new rating
            handleAddRating(request, response, user);
        } else if (path.equals("/update")) {
            // Update an existing rating
            handleUpdateRating(request, response, user);
        } else {
            // Unsupported operation
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Handle adding a new rating.
     */
    private void handleAddRating(HttpServletRequest request, HttpServletResponse response, Customer user)
            throws IOException {
        int courseId = Integer.parseInt(request.getParameter("courseId"));
        int stars = Integer.parseInt(request.getParameter("stars"));
        String comment = request.getParameter("comment");

        // Validate input
        if (stars < 1 || stars > 5) {
            sendJsonResponse(response, false, "Invalid star rating. Must be between 1-5.");
            return;
        }

        // Check if user has purchased the course
        if (!orderDAO.hasCustomerPurchasedCourse(user.getCustomerID(), courseId)) {
            sendJsonResponse(response, false, "You must purchase the course before rating it.");
            return;
        }

        // Check if user has completed at least 80% of the course
        CourseProgress progress = progressDAO.getByCustomerAndCourse(user.getCustomerID(), courseId);
        if (progress == null || progress.getCompletionPercentage().compareTo(new BigDecimal("80")) < 0) {
            sendJsonResponse(response, false, "You need to complete at least 80% of the course before rating it.");
            return;
        }

        // Check if user has already rated this course
        if (ratingDAO.getByCustomerAndCourse(user.getCustomerID(), courseId) != null) {
            sendJsonResponse(response, false, "You have already rated this course. You can edit your existing rating.");
            return;
        }

        // Create and save the rating
        Rating rating = new Rating(courseId, user.getCustomerID(), stars, comment);
        int ratingId = ratingDAO.insertRating(rating);

        if (ratingId > 0) {
            // Rating added successfully
            rating = ratingDAO.getRatingById(ratingId);
            sendJsonResponse(response, true, "Your rating has been submitted successfully.", rating);
        } else {
            // Error adding rating
            sendJsonResponse(response, false, "An error occurred while submitting your rating. Please try again.");
        }
    }

    /**
     * Handle updating an existing rating.
     */
    private void handleUpdateRating(HttpServletRequest request, HttpServletResponse response, Customer user)
            throws IOException {
        int ratingId = Integer.parseInt(request.getParameter("ratingId"));
        int stars = Integer.parseInt(request.getParameter("stars"));
        String comment = request.getParameter("comment");

        // Validate input
        if (stars < 1 || stars > 5) {
            sendJsonResponse(response, false, "Invalid star rating. Must be between 1-5.");
            return;
        }

        // Get the existing rating
        Rating rating = ratingDAO.getRatingById(ratingId);
        if (rating == null) {
            sendJsonResponse(response, false, "Rating not found.");
            return;
        }

        // Check if this rating belongs to the current user
        if (rating.getCustomerID() != user.getCustomerID()) {
            sendJsonResponse(response, false, "You can only edit your own ratings.");
            return;
        }

        // Update the rating
        rating.setStars(stars);
        rating.setComment(comment);
        rating.setUpdatedAt(new Date());

        boolean updated = ratingDAO.updateRating(rating);
        if (updated) {
            // Rating updated successfully
            rating = ratingDAO.getRatingById(ratingId);
            sendJsonResponse(response, true, "Your rating has been updated successfully.", rating);
        } else {
            // Error updating rating
            sendJsonResponse(response, false, "An error occurred while updating your rating. Please try again.");
        }
    }

    /**
     * Handle deleting a rating.
     */
    private void handleDeleteRating(HttpServletRequest request, HttpServletResponse response, Customer user)
            throws IOException {
        String path = request.getPathInfo();
        String[] pathParts = path.split("/");

        if (pathParts.length != 3) {
            sendJsonResponse(response, false, "Requirements are invalid.");
            return;
        }

        int ratingId;
        try {
            ratingId = Integer.parseInt(pathParts[2]);
        } catch (NumberFormatException e) {
            sendJsonResponse(response, false, "ID assessment is invalid.");
            return;
        }

        // Get the rating
        Rating rating = ratingDAO.getRatingById(ratingId);
        if (rating == null) {
            sendJsonResponse(response, false, "No evaluation found.");
            return;
        }

        // Lấy admin (SuperUser) từ session nếu có
        HttpSession session = request.getSession(false);
        SuperUser superUser = null;
        if (session != null) {
            superUser = (SuperUser) session.getAttribute("superUser");
        }

        // Check quyền xoá: phải là chủ sở hữu hoặc admin
        boolean isOwner = rating.getCustomerID() == user.getCustomerID();
        boolean isAdmin = (superUser != null && superUser.isAdmin());

        if (!isOwner && !isAdmin) {
            sendJsonResponse(response, false, "You can only delete your own reviews.");
            return;
        }

        // Delete the rating
        boolean deleted = ratingDAO.deleteRating(ratingId);
        if (deleted) {
            sendJsonResponse(response, true, "Delete success evaluation!");
        } else {
            sendJsonResponse(response, false, "Error occurred when deleted evaluation. Please try again.");
        }
    }

    /**
     * Get the currently logged in user.
     *
     * @param request The HTTP request
     * @return The logged in user, or null if not logged in
     */
    private Customer getLoggedInUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (Customer) session.getAttribute("user");
        }
        return null;
    }

    /**
     * Send a JSON response to the client.
     *
     * @param response The HTTP response
     * @param success Whether the operation was successful
     * @param message The message to send
     * @param data Optional data to include in the response
     * @throws IOException If an I/O error occurs
     */
    private void sendJsonResponse(HttpServletResponse response, boolean success, String message, Object... data)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("success", success);
        jsonResponse.put("message", message);

        if (data.length > 0) {
            jsonResponse.put("data", data[0]);
        }

        // Convert to JSON string and send
        response.getWriter().write(convertToJson(jsonResponse));
    }

    /**
     * Simple method to convert a Map to JSON string. In a production app, use a
     * proper JSON library like Jackson or Gson.
     *
     * @param map The map to convert
     * @return JSON string representation
     */
    private String convertToJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                sb.append(",");
            }
            first = false;

            sb.append("\"").append(entry.getKey()).append("\":");

            if (entry.getValue() == null) {
                sb.append("null");
            } else if (entry.getValue() instanceof String) {
                sb.append("\"").append(escapeJson((String) entry.getValue())).append("\"");
            } else if (entry.getValue() instanceof Number) {
                sb.append(entry.getValue().toString());
            } else if (entry.getValue() instanceof Boolean) {
                sb.append(entry.getValue().toString());
            } else if (entry.getValue() instanceof Map) {
                sb.append(convertToJson((Map<String, Object>) entry.getValue()));
            } else if (entry.getValue() instanceof Rating) {
                Rating rating = (Rating) entry.getValue();
                Map<String, Object> ratingMap = new HashMap<>();
                ratingMap.put("ratingID", rating.getRatingID());
                ratingMap.put("courseID", rating.getCourseID());
                ratingMap.put("customerID", rating.getCustomerID());
                ratingMap.put("stars", rating.getStars());
                ratingMap.put("comment", rating.getComment());
                ratingMap.put("createdAt", rating.getCreatedAt().getTime());
                ratingMap.put("updatedAt", rating.getUpdatedAt().getTime());
                ratingMap.put("username", rating.getUsername());
                sb.append(convertToJson(ratingMap));
            } else {
                sb.append("\"").append(escapeJson(entry.getValue().toString())).append("\"");
            }
        }

        sb.append("}");
        return sb.toString();
    }

    /**
     * Escape special characters for JSON.
     *
     * @param input The input string
     * @return The escaped string
     */
    private String escapeJson(String input) {
        if (input == null) {
            return "";
        }

        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
