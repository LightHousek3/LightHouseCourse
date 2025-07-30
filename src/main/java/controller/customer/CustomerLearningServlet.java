package controller.customer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

import dao.CourseDAO;
import dao.OrderDAO;
import dao.VideoDAO;
import dao.RefundRequestDAO;
import dao.CourseProgressDAO;
import dao.MaterialDAO;
import dao.QuizDAO;
import dao.LessonDAO;
import dao.LessonItemDAO;
import dao.LessonItemProgressDAO;
import model.Course;
import model.Video;
import model.Lesson;
import model.Material;
import model.Quiz;
import model.CourseProgress;
import model.LessonItem;
import model.Customer;
import util.Validator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.LessonProgress;

/**
 * Learning controller for handling course learning pages.
 */
@WebServlet(name = "LearningServlet", urlPatterns = {
        "/learning/*",
        "/learning/lesson/*",
        "/learning/video/*",
        "/learning/material/*",
        "/learning/quiz/*",
        "/api/learning/*"
})
public class CustomerLearningServlet extends HttpServlet {

    // DAOs
    private CourseDAO courseDAO;
    private VideoDAO videoDAO;
    private OrderDAO orderDAO;
    private RefundRequestDAO refundDAO;
    private CourseProgressDAO progressDAO;
    private MaterialDAO materialDAO;
    private QuizDAO quizDAO;
    private LessonDAO lessonDAO;
    private LessonItemDAO lessonItemDAO;
    private LessonItemProgressDAO lessonItemProgressDAO;

    // Constants
    private static final String JSP_PATH = "/WEB-INF/views/customer/manage-courses/course-learning.jsp";
    private static final String ERROR_COURSE_NOT_FOUND = "Course not found";
    private static final String ERROR_LESSON_NOT_FOUND = "Lesson not found";
    private static final String ERROR_ITEM_NOT_FOUND = "Item not found";
    private static final String ERROR_NOT_PURCHASED = "not_purchased";
    private static final String ERROR_REFUND_PENDING = "refund_pending";
    private static final String ERROR_REFUND_APPROVED = "refund_approved";
    private static final String ERROR_LESSON_LOCKED = "Previous content must be completed first";
    private static final String ERROR_LESSONITEM_LOCKED = "Previous lessons must be completed first";

    @Override
    public void init() throws ServletException {
        super.init();
        // Initialize all DAOs
        courseDAO = new CourseDAO();
        videoDAO = new VideoDAO();
        orderDAO = new OrderDAO();
        refundDAO = new RefundRequestDAO();
        progressDAO = new CourseProgressDAO();
        materialDAO = new MaterialDAO();
        quizDAO = new QuizDAO();
        lessonDAO = new LessonDAO();
        lessonItemDAO = new LessonItemDAO();
        lessonItemProgressDAO = new LessonItemProgressDAO();
    }

    /**
     * Handles GET requests to show course, lesson, or content items.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Customer customer = getCustomerFromSession(request);
        if (customer == null) {
            redirectToLogin(request, response);
            return;
        }

        String servletPath = request.getServletPath();
        String pathInfo = request.getPathInfo();

        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try {
            switch (servletPath) {
                case "/learning":
                    showCourseLearning(request, response, customer);
                    break;
                case "/learning/video":
                    showSpecificContent(request, response, customer, "video");
                    break;
                case "/learning/material":
                    showSpecificContent(request, response, customer, "material");
                    break;
                case "/learning/quiz":
                    showSpecificContent(request, response, customer, "quiz");
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        } catch (ResourceNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (AccessDeniedException e) {
            // AccessDeniedException already handles the redirection
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred: " + e.getMessage());
        }
    }

    /**
     * Handles POST requests for API endpoints like marking content as complete.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Customer customer = getCustomerFromSession(request);
        if (customer == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            sendJsonResponse(response, false, "User not authenticated");
            return;
        }

        String servletPath = request.getServletPath();
        String pathInfo = request.getPathInfo();

        try {
            if (servletPath.equals("/api/learning") && pathInfo != null && pathInfo.equals("/mark-complete")) {
                handleMarkComplete(request, response, customer);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(response, false, "Error processing request: " + e.getMessage());
        }
    }

    /**
     * Shows the main course learning page.
     * When accessed via /learning/{courseID}, directly redirects to the last
     * accessed lesson item
     * rather than showing the course overview page.
     */
    private void showCourseLearning(HttpServletRequest request, HttpServletResponse response, Customer customer)
            throws ServletException, IOException, ResourceNotFoundException, AccessDeniedException {

        int courseId = extractIdFromPath(request);
        Course course = getCourse(courseId);
        checkCourseAccess(customer, course, request, response);

        loadAndPrepareCourse(course);

        // Find the last progress point directly
        Map<String, Object> lastProgressPoint = findLastProgressPoint(customer.getCustomerID(), course);
        if (lastProgressPoint != null) {
            // Redirect directly to the last accessed lesson item
            String itemType = (String) lastProgressPoint.get("type");
            int itemId = (int) lastProgressPoint.get("id");
            int lessonId = (int) lastProgressPoint.get("lessonId");

            response.sendRedirect(
                    request.getContextPath() + "/learning/" + itemType + "/" + itemId);
            return;
        } else {
            // If no progress yet, redirect to the first lesson
            if (course.getLessons() != null && !course.getLessons().isEmpty()) {
                Lesson firstLesson = course.getLessons().get(0);
                response.sendRedirect(
                        request.getContextPath() + "/learning/lesson/" + firstLesson.getLessonID());
                return;
            }
        }

        // If we reach here, there are no lessons in the course fall back to showing course overview
        setRequestAttributes(request, course, null, null);
        request.getRequestDispatcher(JSP_PATH).forward(request, response);
    }

    /**
     * Shows a specific content item (video, material, quiz).
     */
    private void showSpecificContent(HttpServletRequest request, HttpServletResponse response, Customer customer,
            String contentType)
            throws ServletException, IOException, ResourceNotFoundException, AccessDeniedException {

        int contentId = extractIdFromPath(request);
        Object content;
        int lessonId;

        // Get the appropriate content based on type
        switch (contentType) {
            case "video":
                Video video = getVideo(contentId);
                content = video;
                lessonId = video.getLessonID();
                break;
            case "material":
                Material material = getMaterial(contentId);
                content = material;
                lessonId = material.getLessonID();
                break;
            case "quiz":
                Quiz quiz = getQuiz(contentId);
                content = quiz;
                lessonId = quiz.getLessonID();
                if (lessonId <= 0) {
                    lessonId = quizDAO.getLessonIdByQuizId(contentId);
                    if (lessonId <= 0) {
                        throw new ResourceNotFoundException("Cannot determine lesson for this quiz");
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid content type: " + contentType);
        }

        Lesson currentLesson = getLesson(lessonId);
        Course course = getCourse(currentLesson.getCourseID());
        checkCourseAccess(customer, course, request, response);

        loadAndPrepareCourse(course);

        // Get the lesson item
        LessonItem currentLessonItem = getLessonItem(contentType, contentId);

        // Check if user can access this specific item (previous items in the lesson must be completed)
        if (!canAccessLessonItem(customer.getCustomerID(), currentLesson, currentLessonItem)) {
            LessonItem lastAccessibleItem = findLastAccessibleLessonItem(customer.getCustomerID(), currentLesson);

            if (lastAccessibleItem != null) {
                String itemType = lastAccessibleItem.getItemType().toLowerCase();
                int itemId = lastAccessibleItem.getItemID();

                request.getSession().setAttribute("errorMessage", ERROR_LESSONITEM_LOCKED);

                response.sendRedirect(
                        request.getContextPath() + "/learning/" + itemType + "/" + itemId);
                return;
            }
        }

        showContentItem(request, response, customer, contentType, content, course, currentLesson, currentLessonItem);
    }

    /**
     * Shows a content item with navigation and completion tracking.
     */
    private void showContentItem(HttpServletRequest request, HttpServletResponse response,
            Customer customer, String contentType, Object content, Course course, Lesson currentLesson, LessonItem currentLessonItem)
            throws ServletException, IOException, ResourceNotFoundException, AccessDeniedException {

        // Update video access tracking if applicable
        if (contentType.equals("video")) {
            lessonItemProgressDAO.updateLessonItemAccess(customer.getCustomerID(), currentLessonItem.getLessonItemID());
        }

        // Create completion maps
        CompletionMaps completionMaps = createCompletionMaps(course, customer.getCustomerID());
        updateLessonCompletionStatus(course, completionMaps);

        // Navigation
        Map<String, Object> navigationInfo = createNavigationInfo(
                course, currentLesson, currentLessonItem);

        // Progress
        CourseProgress progress = getOrCreateCourseProgress(customer.getCustomerID(), course.getCourseID());

        // Set all attributes for view
        request.setAttribute("course", course);
        request.setAttribute("currentLesson", currentLesson);
        request.setAttribute("contentType", contentType);
        request.setAttribute("navigation", navigationInfo);
        request.setAttribute("videoCompletedMap", completionMaps.videoMap);
        request.setAttribute("materialCompletedMap", completionMaps.materialMap);
        request.setAttribute("quizCompletedMap", completionMaps.quizMap);
        request.setAttribute("progress", progress);

        // Display error message if one exists
        String errorMessage = (String) request.getSession().getAttribute("errorMessage");
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
            request.getSession().removeAttribute("errorMessage"); // Clear the message
        }

        // Set the specific content
        switch (contentType) {
            case "video":
                request.setAttribute("currentVideo", content);
                break;
            case "material":
                request.setAttribute("currentMaterial", content);
                break;
            case "quiz":
                request.setAttribute("currentQuiz", content);
                break;
        }

        // Forward to learning page
        request.getRequestDispatcher(JSP_PATH).forward(request, response);
    }

    /**
     * Handles marking content as complete.
     */
    private void handleMarkComplete(HttpServletRequest request, HttpServletResponse response, Customer customer)
            throws IOException {
        // Parse JSON request
        JsonObject requestData = parseJsonRequest(request);
        if (!requestData.has("type") || !requestData.has("id") || !requestData.has("lessonId")) {
            sendJsonResponse(response, false, "Missing required parameters");
            return;
        }

        String type = requestData.get("type").getAsString();
        int itemId = requestData.get("id").getAsInt();
        int lessonId = requestData.get("lessonId").getAsInt();

        try {
            // Get resources
            Lesson lesson = getLesson(lessonId);
            Course course = getCourse(lesson.getCourseID());
            LessonItem lessonItem = getLessonItem(type, itemId);

            // Mark as complete - this will update lesson and course progress automatically
            boolean success = lessonItemProgressDAO.markItemComplete(
                    customer.getCustomerID(), lessonItem.getLessonItemID());

            if (!success) {
                sendJsonResponse(response, false, "Failed to mark content as complete");
                return;
            }

            // Check if lesson is complete
            boolean lessonComplete = isLessonComplete(lesson, customer.getCustomerID());

            // Get lesson progress percentage
            LessonProgress lessonProgress = lessonDAO.getLessonProgress(customer.getCustomerID(), lessonId);
            BigDecimal lessonCompletionPercentage = lessonProgress != null ? lessonProgress.getCompletionPercentage()
                    : BigDecimal.ZERO;

            CourseProgress courseProgress = progressDAO.getByCustomerAndCourse(
                    customer.getCustomerID(), course.getCourseID());

            // Get next navigation item
            Map<String, Object> nextItem = findNextNavigationItem(course, lesson, lessonItem);

            // Build response
            JsonObject responseObj = new JsonObject();
            responseObj.addProperty("success", true);
            responseObj.addProperty("message", "Content marked as complete");
            responseObj.addProperty("lessonComplete", lessonComplete);
            responseObj.addProperty("lessonCompletionPercentage", lessonCompletionPercentage.doubleValue());
            responseObj.addProperty("newCompletionPercentage",
                    courseProgress != null ? courseProgress.getCompletionPercentage().doubleValue() : 0);

            if (nextItem != null) {
                responseObj.addProperty("nextType", (String) nextItem.get("type"));
                responseObj.addProperty("nextId", (Integer) nextItem.get("id"));
                responseObj.addProperty("nextLessonId", (Integer) nextItem.get("lessonId"));
                if (nextItem.containsKey("title")) {
                    responseObj.addProperty("nextTitle", (String) nextItem.get("title"));
                }
            }

            response.setContentType("application/json");
            response.getWriter().write(responseObj.toString());

        } catch (ResourceNotFoundException e) {
            sendJsonResponse(response, false, e.getMessage());
        }
    }

    /**
     * Helper class to store completion maps for different content types.
     */
    private static class CompletionMaps {

        Map<Integer, Boolean> videoMap = new HashMap<>();
        Map<Integer, Boolean> materialMap = new HashMap<>();
        Map<Integer, Boolean> quizMap = new HashMap<>();
    }

    /**
     * ResourceNotFoundException for when a requested resource is not found.
     */
    private static class ResourceNotFoundException extends Exception {

        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    /**
     * AccessDeniedException for when a customer doesn't have access to a
     * resource.
     */
    private static class AccessDeniedException extends Exception {

        public AccessDeniedException(String message) {
            super(message);
        }
    }

    // ================ HELPER METHODS ================
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

    /**
     * Extracts the ID from the path.
     */
    private int extractIdFromPath(HttpServletRequest request) throws ResourceNotFoundException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            throw new ResourceNotFoundException("Resource ID not specified");
        }

        String[] pathParts = pathInfo.split("/");
        if (pathParts.length < 2 || !Validator.isValidNumber(pathParts[1])) {
            throw new ResourceNotFoundException("Invalid resource ID");
        }

        return Integer.parseInt(pathParts[1]);
    }

    /**
     * Gets a course by ID.
     */
    private Course getCourse(int courseId) throws ResourceNotFoundException {
        Course course = courseDAO.getCourseById(courseId);
        if (course == null) {
            throw new ResourceNotFoundException(ERROR_COURSE_NOT_FOUND);
        }
        return course;
    }

    /**
     * Gets a lesson by ID.
     */
    private Lesson getLesson(int lessonId) throws ResourceNotFoundException {
        Lesson lesson = lessonDAO.getLessonById(lessonId);
        if (lesson == null) {
            throw new ResourceNotFoundException(ERROR_LESSON_NOT_FOUND);
        }
        return lesson;
    }

    /**
     * Gets a video by ID.
     */
    private Video getVideo(int videoId) throws ResourceNotFoundException {
        Video video = videoDAO.getVideoById(videoId);
        if (video == null) {
            throw new ResourceNotFoundException("Video not found");
        }
        return video;
    }

    /**
     * Gets a material by ID.
     */
    private Material getMaterial(int materialId) throws ResourceNotFoundException {
        Material material = materialDAO.getMaterialById(materialId);
        if (material == null) {
            throw new ResourceNotFoundException("Material not found");
        }
        return material;
    }

    /**
     * Gets a quiz by ID.
     */
    private Quiz getQuiz(int quizId) throws ResourceNotFoundException {
        Quiz quiz = quizDAO.getQuizById(quizId);
        if (quiz == null) {
            throw new ResourceNotFoundException("Quiz not found");
        }
        return quiz;
    }

    /**
     * Gets a lesson item by type and content ID.
     */
    private LessonItem getLessonItem(String type, int itemId) throws ResourceNotFoundException {
        LessonItem lessonItem = lessonItemDAO.getByItemTypeAndItemId(type, itemId);
        if (lessonItem == null) {
            throw new ResourceNotFoundException(ERROR_ITEM_NOT_FOUND);
        }
        return lessonItem;
    }

    /**
     * Checks if the customer has access to the course.
     * The logic considers the most recent order for the course.
     */
    private void checkCourseAccess(Customer customer, Course course, HttpServletRequest request,
            HttpServletResponse response) throws IOException, AccessDeniedException {

        // Check if customer has purchased this course (based on most recent order)
        boolean hasPurchased = orderDAO.hasCustomerPurchasedCourse(customer.getCustomerID(), course.getCourseID());
        if (!hasPurchased) {
            response.sendRedirect(
                    request.getContextPath() + "/course/" + course.getCourseID() + "?error=" + ERROR_NOT_PURCHASED);
            throw new AccessDeniedException("Customer hasn't purchased this course");
        }

        // Check for pending refund requests on the most recent order
        if (refundDAO.hasPendingRefundForCourse(customer.getCustomerID(), course.getCourseID())) {
            response.sendRedirect(
                    request.getContextPath() + "/course/" + course.getCourseID() + "?error=" + ERROR_REFUND_PENDING);
            throw new AccessDeniedException("Customer has a pending refund for this course");
        }

        // Check for approved refund requests on the most recent order
        if (refundDAO.hasApprovedRefundForCourse(customer.getCustomerID(), course.getCourseID())) {
            response.sendRedirect(
                    request.getContextPath() + "/course/" + course.getCourseID() + "?error=" + ERROR_REFUND_APPROVED);
            throw new AccessDeniedException("Customer has an approved refund for this course");
        }

        // If we reach here, customer has a valid purchase with no pending or approved
        // refunds
    }

    /**
     * Loads course content and adds lesson items.
     */
    private void loadAndPrepareCourse(Course course) {
        // Load lessons if not already loaded
        if (course.getLessons() == null || course.getLessons().isEmpty()) {
            List<Lesson> lessons = lessonDAO.getLessonsByCourseId(course.getCourseID());
            course.setLessons(lessons);
        }

        // Load detailed content
        loadCourseContent(course);
    }

    /**
     * Creates completion maps for all content types.
     */
    private CompletionMaps createCompletionMaps(Course course, int customerId) {
        CompletionMaps maps = new CompletionMaps();
        populateCompletionMaps(course, customerId, maps.videoMap, maps.materialMap, maps.quizMap);
        return maps;
    }

    /**
     * Gets or creates a course progress for the customer.
     */
    private CourseProgress getOrCreateCourseProgress(int customerId, int courseId) {
        CourseProgress progress = progressDAO.getByCustomerAndCourse(customerId, courseId);
        if (progress == null) {
            progress = new CourseProgress(customerId, courseId);
            progress.setCompletionPercentage(BigDecimal.ZERO);
            progressDAO.insertCourseProgress(progress);
        }
        return progress;
    }

    /**
     * Sets common request attributes for the view.
     */
    private void setRequestAttributes(HttpServletRequest request, Course course, Lesson currentLesson,
            CourseProgress progress) {
        request.setAttribute("course", course);
        request.setAttribute("progress", progress);

        if (currentLesson != null) {
            request.setAttribute("currentLesson", currentLesson);
        }
    }
   
    /**
     * Creates navigation info with previous and next items.
     */
    private Map<String, Object> createNavigationInfo(Course course, Lesson currentLesson,
            LessonItem currentLessonItem) {
        Map<String, Object> navigationInfo = new HashMap<>();
        Map<String, Object> previousItem = findPreviousNavigationItem(course, currentLesson, currentLessonItem);
        Map<String, Object> nextItem = findNextNavigationItem(course, currentLesson, currentLessonItem);

        navigationInfo.put("previous", previousItem);
        navigationInfo.put("next", nextItem);
        return navigationInfo;
    }

    /**
     * Updates the completion status for all lessons in a course.
     */
    private void updateLessonCompletionStatus(Course course, CompletionMaps maps) {
        for (Lesson lesson : course.getLessons()) {
            boolean videosCompleted = checkAllItemsCompleted(lesson.getLessonItems(), maps.videoMap, "video");
            boolean materialsCompleted = checkAllItemsCompleted(lesson.getLessonItems(), maps.materialMap, "material");
            boolean quizzesCompleted = checkAllItemsCompleted(lesson.getLessonItems(), maps.quizMap, "quiz");

            lesson.setCompleted(videosCompleted && materialsCompleted && quizzesCompleted);
        }
    }

    /**
     * Checks if a lesson is complete for a customer.
     */
    private boolean isLessonComplete(Lesson lesson, int customerId) throws ResourceNotFoundException {
        List<LessonItem> lessonItems = lessonItemDAO.getLessonItemsByLessonId(lesson.getLessonID());
        if (lessonItems == null || lessonItems.isEmpty()) {
            return true;
        }
        for (LessonItem item : lessonItems) {
            if (item != null && !lessonItemProgressDAO.isItemCompleted(customerId, item.getLessonItemID())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Parses a JSON request.
     */
    private JsonObject parseJsonRequest(HttpServletRequest request) throws IOException {
        BufferedReader reader = request.getReader();
        StringBuilder jsonBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBody.append(line);
        }
        return new JsonParser().parse(jsonBody.toString()).getAsJsonObject();
    }

    /**
     * Find the next navigation item.
     */
    private Map<String, Object> findNextNavigationItem(Course course, Lesson currentLesson, LessonItem currentItem) {
        // Get current lesson item index
        int currentLessonItemId = currentItem.getLessonItemID();
        List<LessonItem> lessonItems = currentLesson.getLessonItems();

        // Find the next item in the current lesson
        for (int i = 0; i < lessonItems.size(); i++) {
            if (lessonItems.get(i).getLessonItemID() == currentLessonItemId) {
                if (i < lessonItems.size() - 1) {
                    return createNavigationInfoMap(lessonItems.get(i + 1), currentLesson.getLessonID());
                }
                break;
            }
        }

        // Find the next lesson
        List<Lesson> lessons = course.getLessons();
        for (int i = 0; i < lessons.size(); i++) {
            if (lessons.get(i).getLessonID() == currentLesson.getLessonID()) {
                if (i < lessons.size() - 1) {
                    Lesson nextLesson = lessons.get(i + 1);
                    if (nextLesson.getLessonItems() != null && !nextLesson.getLessonItems().isEmpty()) {
                        return createNavigationInfoMap(
                                nextLesson.getLessonItems().get(0),
                                nextLesson.getLessonID());
                    }
                }
                break;
            }
        }
        return null;
    }

    /**
     * Find the previous navigation item.
     */
    private Map<String, Object> findPreviousNavigationItem(Course course, Lesson currentLesson,
            LessonItem currentItem) {
        // Get current lesson item index
        int currentLessonItemId = currentItem.getLessonItemID();
        List<LessonItem> lessonItems = currentLesson.getLessonItems();

        // Find the previous item in the current lesson
        for (int i = 0; i < lessonItems.size(); i++) {
            if (lessonItems.get(i).getLessonItemID() == currentLessonItemId) {
                if (i > 0) {
                    return createNavigationInfoMap(lessonItems.get(i - 1), currentLesson.getLessonID());
                }
                break;
            }
        }

        // Find the previous lesson
        List<Lesson> lessons = course.getLessons();
        for (int i = 0; i < lessons.size(); i++) {
            if (lessons.get(i).getLessonID() == currentLesson.getLessonID()) {
                if (i > 0) {
                    Lesson prevLesson = lessons.get(i - 1);
                    if (prevLesson.getLessonItems() != null && !prevLesson.getLessonItems().isEmpty()) {
                        return createNavigationInfoMap(
                                prevLesson.getLessonItems().get(prevLesson.getLessonItems().size() - 1),
                                prevLesson.getLessonID());
                    }
                }
                break;
            }
        }
        return null;
    }

    /**
     * Creates navigation info map for a lesson item.
     */
    private Map<String, Object> createNavigationInfoMap(LessonItem item, int lessonId) {
        Map<String, Object> result = new HashMap<>();
        result.put("type", item.getItemType().toLowerCase());
        result.put("id", item.getItemID());
        result.put("lessonId", lessonId);

        // Get content title if available
        if (item.getItem() != null) {
            String title = "";
            Object content = item.getItem();

            try {
                if (content instanceof Video) {
                    title = ((Video) content).getTitle();
                } else if (content instanceof Material) {
                    title = ((Material) content).getTitle();
                } else if (content instanceof Quiz) {
                    title = ((Quiz) content).getTitle();
                }

                if (!title.isEmpty()) {
                    result.put("title", title);
                }
            } catch (Exception e) {
                // If any error occurs, just continue without adding a title
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Loads all content for a course.
     */
    private void loadCourseContent(Course course) {
        if (course == null || course.getLessons() == null) {
            return;
        }

        for (Lesson lesson : course.getLessons()) {
            int lessonId = lesson.getLessonID();

            // Load lesson items
            List<LessonItem> lessonItems = lessonItemDAO.getLessonItemsByLessonId(lessonId);
            lesson.setLessonItems(lessonItems);

            // Load content objects
            if (lessonItems != null) {
                for (LessonItem item : lessonItems) {
                    if (item == null) {
                        continue;
                    }
                    loadItemContent(item);
                }
            }
        }
    }

    /**
     * Loads content for a lesson item.
     */
    private void loadItemContent(LessonItem item) {
        String itemType = item.getItemType().toLowerCase();
        int itemId = item.getItemID();

        switch (itemType) {
            case "video":
                Video video = videoDAO.getVideoById(itemId);
                if (video != null) {
                    item.setItem(video);
                }
                break;
            case "material":
                Material material = materialDAO.getMaterialById(itemId);
                if (material != null) {
                    item.setItem(material);
                }
                break;
            case "quiz":
                Quiz quiz = quizDAO.getQuizById(itemId);
                if (quiz != null) {
                    item.setItem(quiz);
                }
                break;
        }
    }

    /**
     * Populates completion maps for course content.
     */
    private void populateCompletionMaps(Course course, int customerId,
            Map<Integer, Boolean> videoCompletedMap,
            Map<Integer, Boolean> materialCompletedMap,
            Map<Integer, Boolean> quizCompletedMap) {

        if (course == null || course.getLessons() == null) {
            return;
        }

        for (Lesson lesson : course.getLessons()) {
            if (lesson == null || lesson.getLessonItems() == null) {
                continue;
            }

            for (LessonItem item : lesson.getLessonItems()) {
                if (item == null) {
                    continue;
                }

                boolean isCompleted = lessonItemProgressDAO.isItemCompleted(customerId, item.getLessonItemID());

                switch (item.getItemType().toLowerCase()) {
                    case "video":
                        videoCompletedMap.put(item.getItemID(), isCompleted);
                        break;
                    case "material":
                        materialCompletedMap.put(item.getItemID(), isCompleted);
                        break;
                    case "quiz":
                        quizCompletedMap.put(item.getItemID(), isCompleted);
                        break;
                }
            }
        }
    }

    /**
     * Check if all items of a specific type are completed.
     */
    private boolean checkAllItemsCompleted(List<LessonItem> itemList,
            Map<Integer, Boolean> completionMap, String itemType) {

        if (itemList == null || itemList.isEmpty()) {
            return true;
        }

        for (LessonItem item : itemList) {
            if (item != null && item.getItemType().equalsIgnoreCase(itemType)) {
                int itemId = item.getItemID();
                if (!completionMap.getOrDefault(itemId, false)) {
                    return false;
                }
            }
        }

        // If there are no items of this type, it's considered "completed"
        return true;
    }

    /**
     * Sends a JSON response.
     */
    private void sendJsonResponse(HttpServletResponse response, boolean success, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String json = "{\"success\":" + success + ",\"message\":\"" + message + "\"}";
        response.getWriter().write(json);
    }

    /**
     * Returns servlet description.
     */
    @Override
    public String getServletInfo() {
        return "Learning Servlet - Handles course learning pages";
    }

    /**
     * Find the last progress point (lesson and item) for the user in a course.
     * This is used for "continue learning" functionality.
     */
    private Map<String, Object> findLastProgressPoint(int customerId, Course course) {
        if (course == null || course.getLessons() == null || course.getLessons().isEmpty()) {
            return null;
        }

        // Go through each lesson in sequence
        for (Lesson lesson : course.getLessons()) {
            if (lesson.getLessonItems() == null || lesson.getLessonItems().isEmpty()) {
                continue;
            }

            // Find the first incomplete item in this lesson
            for (LessonItem item : lesson.getLessonItems()) {
                boolean isCompleted = lessonItemProgressDAO.isItemCompleted(customerId, item.getLessonItemID());
                if (!isCompleted) {
                    // This is the next item to complete
                    Map<String, Object> progressPoint = new HashMap<>();
                    progressPoint.put("type", item.getItemType().toLowerCase());
                    progressPoint.put("id", item.getItemID());
                    progressPoint.put("lessonId", lesson.getLessonID());
                    return progressPoint;
                }
            }
        }

        // If all items are completed, return the last item
        Lesson lastLesson = course.getLessons().get(course.getLessons().size() - 1);
        if (lastLesson.getLessonItems() != null && !lastLesson.getLessonItems().isEmpty()) {
            LessonItem lastItem = lastLesson.getLessonItems().get(lastLesson.getLessonItems().size() - 1);
            Map<String, Object> progressPoint = new HashMap<>();
            progressPoint.put("type", lastItem.getItemType().toLowerCase());
            progressPoint.put("id", lastItem.getItemID());
            progressPoint.put("lessonId", lastLesson.getLessonID());
            return progressPoint;
        }

        return null;
    }

    /**
     * Check if a user can access a specific lesson item.
     * This enforces sequential learning - users must complete items in order within
     * a lesson.
     */
    private boolean canAccessLessonItem(int customerId, Lesson lesson, LessonItem targetItem) {
        if (lesson == null || lesson.getLessonItems() == null || targetItem == null) {
            return false;
        }

        // Allow access to first item in lesson
        if (lesson.getLessonItems().get(0).getLessonItemID() == targetItem.getLessonItemID()) {
            return true;
        }

        // Check if all previous items in this lesson are completed
        for (int i = 0; i < lesson.getLessonItems().size(); i++) {
            LessonItem item = lesson.getLessonItems().get(i);

            // Once we reach our target item, we've verified all previous items are completed
            if (item.getLessonItemID() == targetItem.getLessonItemID()) {
                return true;
            }

            // Check if this item is completed
            if (!lessonItemProgressDAO.isItemCompleted(customerId, item.getLessonItemID())) {
                return false; // Found an incomplete previous item
            }
        }

        return false; // Target item not found in lesson
    }

    /**
     * Find the last accessible lesson item for a user in a lesson.
     */
    private LessonItem findLastAccessibleLessonItem(int customerId, Lesson lesson) {
        if (lesson == null || lesson.getLessonItems() == null || lesson.getLessonItems().isEmpty()) {
            return null;
        }

        LessonItem lastAccessibleItem = lesson.getLessonItems().get(0);

        for (int i = 1; i < lesson.getLessonItems().size(); i++) {
            LessonItem prevItem = lesson.getLessonItems().get(i - 1);

            // If previous item is completed, this item is accessible
            if (lessonItemProgressDAO.isItemCompleted(customerId, prevItem.getLessonItemID())) {
                lastAccessibleItem = lesson.getLessonItems().get(i);
            } else {
                // Return the last accessible item
                return lastAccessibleItem;
            }
        }

        return lastAccessibleItem;
    }
}
