package controller.customer;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import dao.QuizDAO;
import dao.QuizAttemptDAO;
import model.Quiz;
import model.QuizAttempt;
import model.UserAnswer;
import model.Customer;
import org.apache.http.HttpResponse;

/**
 * Servlet implementation class QuizServlet Handles quiz interactions such as
 * starting quizzes, answering questions, and submitting completed quizzes.
 */
@WebServlet(urlPatterns = {
    "/quiz/take/*", // Start/continue quiz
    "/api/quiz/*" // API endpoints for quiz interaction
})
public class CustomerQuizServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private QuizDAO quizDAO;
    private QuizAttemptDAO attemptDAO;
    private Gson gson;

    /**
     * Initialize the servlet
     */
    @Override
    public void init() {
        quizDAO = new QuizDAO();
        attemptDAO = new QuizAttemptDAO();
        gson = new GsonBuilder().serializeNulls().create();
    }

    /**
     * Handle GET requests
     *
     * @param request
     * @param response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // /quiz/take/* URLs
        if (request.getServletPath().equals("/quiz/take")) {
            try {
                String pathPart = pathInfo.substring(1);
                int quizId = Integer.parseInt(pathPart);

                // Get the current user
                HttpSession session = request.getSession();
                Customer currentCustomer = (Customer) session.getAttribute("user");
                if (currentCustomer == null) {
                    response.sendRedirect(request.getContextPath() + "/login");
                    return;
                }

                // Get quiz with all questions and answers
                Quiz quiz = quizDAO.getQuizWithQuestionsAndAnswers(quizId);
                if (quiz == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Quiz not found");
                    return;
                }

                // Check if user has an ongoing attempt
                QuizAttempt attempt = attemptDAO.getActiveAttempt(quizId, currentCustomer.getCustomerID());
                // Serialize attempt to JSON for the JSP
                String attemptJson = attempt != null ? gson.toJson(attempt) : "";

                // Set attributes for the JSP
                request.setAttribute("quiz", quiz);
                request.setAttribute("attempt", attemptJson);

                // Forward to the quiz-take JSP
                request.getRequestDispatcher("/WEB-INF/views/customer/manage-courses/quiz-take.jsp").forward(request, response);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid quiz ID");
            } catch (Exception e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred");
            }
        } // API endpoints
        else if (request.getServletPath().equals("/api/quiz")) {
            String endpoint = pathInfo.substring(1);
            switch (endpoint) {
                case "info":
                    handleQuizInfo(request, response);
                    break;
                case "attempt":
                    handleGetAttempt(request, response);
                    break;
                case "time-remaining":
                    handleTimeRemaining(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        }
    }

    /**
     * Handle POST requests
     *
     * @param request
     * @param response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "/";
        }

        // Only API endpoints should be handled here
        if (request.getServletPath().equals("/api/quiz")) {
            String endpoint = pathInfo.substring(1);
            switch (endpoint) {
                case "start":
                    handleStartQuiz(request, response);
                    break;
                case "save-answer":
                    handleSaveAnswer(request, response);
                    break;
                case "submit":
                    handleSubmitQuiz(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    break;
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Handle quiz info API endpoint
     */
    private void handleQuizInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String quizIdParam = request.getParameter("quizId");
            if (quizIdParam == null) {
                sendError(response, "Quiz ID is required");
                return;
            }

            int quizId = Integer.parseInt(quizIdParam);
            Quiz quiz = quizDAO.getQuizById(quizId);

            if (quiz == null) {
                sendError(response, "Quiz not found");
                return;
            }

            // Don't include questions/answers in this endpoint for security
            quiz.setQuestions(null);

            sendJsonResponse(response, quiz);
        } catch (NumberFormatException e) {
            sendError(response, "Invalid quiz ID format");
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, "Error retrieving quiz information");
        }
    }

    /**
     * Handle get attempt API endpoint
     */
    private void handleGetAttempt(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String attemptIdParam = request.getParameter("attemptId");
            if (attemptIdParam == null) {
                sendError(response, "Attempt ID is required");
                return;
            }

            int attemptId = Integer.parseInt(attemptIdParam);
            QuizAttempt attempt = attemptDAO.getQuizAttemptById(attemptId);

            if (attempt == null) {
                sendError(response, "Attempt not found");
                return;
            }

            // Check if the user owns this attempt
            Customer currentCustomer = (Customer) request.getSession().getAttribute("user");
            if (currentCustomer == null || currentCustomer.getCustomerID()!= attempt.getCustomerID()) {
                sendError(response, "You do not have access to this attempt");
                return;
            }

            sendJsonResponse(response, attempt);
        } catch (NumberFormatException e) {
            sendError(response, "Invalid attempt ID format");
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, "Error retrieving attempt");
        }
    }

    /**
     * Handle time remaining API endpoint
     */
    private void handleTimeRemaining(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String attemptIdParam = request.getParameter("attemptId");
            if (attemptIdParam == null) {
                sendError(response, "Attempt ID is required");
                return;
            }

            int attemptId = Integer.parseInt(attemptIdParam);
            QuizAttempt attempt = attemptDAO.getQuizAttemptById(attemptId);

            if (attempt == null) {
                sendError(response, "Attempt not found");
                return;
            }

            // Check if the user owns this attempt
            Customer currentCustomer = (Customer) request.getSession().getAttribute("user");
            if (currentCustomer == null || currentCustomer.getCustomerID() != attempt.getCustomerID()) {
                sendError(response, "You do not have access to this attempt");
                return;
            }

            // Check if attempt is already completed
            if (attempt.getEndTime() != null) {
                JsonObject result = new JsonObject();
                result.addProperty("timeRemaining", 0);
                result.addProperty("isCompleted", true);
                sendJsonResponse(response, result);
                return;
            }

            // Get time remaining
            int timeRemaining = attemptDAO.getTimeRemaining(attemptId);
            JsonObject result = new JsonObject();
            result.addProperty("timeRemaining", timeRemaining);
            result.addProperty("isCompleted", false);

            sendJsonResponse(response, result);
        } catch (NumberFormatException e) {
            sendError(response, "Invalid attempt ID format");
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, "Error retrieving time remaining");
        }
    }

    /**
     * Handle start quiz API endpoint
     */
    private void handleStartQuiz(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String quizIdParam = request.getParameter("quizId");
            if (quizIdParam == null) {
                sendError(response, "Quiz ID is required");
                return;
            }

            int quizId = Integer.parseInt(quizIdParam);
            Quiz quiz = quizDAO.getQuizById(quizId);

            if (quiz == null) {
                sendError(response, "Quiz not found");
                return;
            }

            // Get the current user
            Customer currentUser = (Customer) request.getSession().getAttribute("user");
            if (currentUser == null) {
                sendError(response, "You must be logged in to start a quiz");
                return;
            }

            // Check if user already has an active attempt
            QuizAttempt activeAttempt = attemptDAO.getActiveAttempt(quizId, currentUser.getCustomerID());
            if (activeAttempt != null) {
                sendJsonResponse(response, activeAttempt);
                return;
            }

            // Create a new attempt
            QuizAttempt attempt = attemptDAO.startAttempt(quizId, currentUser.getCustomerID());
            if (attempt == null) {
                sendError(response, "Failed to start quiz attempt");
                return;
            }

            sendJsonResponse(response, attempt);
        } catch (NumberFormatException e) {
            sendError(response, "Invalid quiz ID format");
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, "Error starting quiz");
        }
    }

    /**
     * Handle save answer API endpoint
     */
    private void handleSaveAnswer(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String attemptIdParam = request.getParameter("attemptId");
            String questionIdParam = request.getParameter("questionId");
            String answerIdParam = request.getParameter("answerId");
            String userResponseParam = request.getParameter("userResponse");

            if (attemptIdParam == null || questionIdParam == null) {
                sendError(response, "Attempt ID and Question ID are required");
                return;
            }

            int attemptId = Integer.parseInt(attemptIdParam);
            int questionId = Integer.parseInt(questionIdParam);

            // Get the current attempt
            QuizAttempt attempt = attemptDAO.getQuizAttemptById(attemptId);
            if (attempt == null) {
                sendError(response, "Attempt not found");
                return;
            }

            // Check if the user owns this attempt
            Customer currentCustomer = (Customer) request.getSession().getAttribute("user");
            if (currentCustomer == null || currentCustomer.getCustomerID()!= attempt.getCustomerID()) {
                sendError(response, "You do not have access to this attempt");
                return;
            }

            // Check if attempt is already completed
            if (attempt.getEndTime() != null) {
                sendError(response, "This attempt has already been submitted");
                return;
            }

            // Create the user answer
            UserAnswer userAnswer = new UserAnswer();
            userAnswer.setAttemptID(attemptId);
            userAnswer.setQuestionID(questionId);

            if (answerIdParam != null && !answerIdParam.trim().isEmpty()) {
                userAnswer.setAnswerID(Integer.parseInt(answerIdParam));
            }

            // Save the answer
            boolean success = attemptDAO.saveUserAnswer(userAnswer);
            if (!success) {
                sendError(response, "Failed to save answer");
                return;
            }
            // Return success response
            JsonObject result = new JsonObject();
            result.addProperty("success", true);
            sendJsonResponse(response, result);
        } catch (NumberFormatException e) {
            sendError(response, "Invalid ID format");
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, "Error saving answer");
        }
    }

    /**
     * Handle submit quiz API endpoint
     */
    private void handleSubmitQuiz(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String attemptIdParam = request.getParameter("attemptId");
            if (attemptIdParam == null) {
                sendError(response, "Attempt ID is required");
                return;
            }

            int attemptId = Integer.parseInt(attemptIdParam);

            // Get the current attempt
            QuizAttempt attempt = attemptDAO.getQuizAttemptById(attemptId);
            if (attempt == null) {
                sendError(response, "Attempt not found");
                return;
            }

            // Check if the user owns this attempt
            Customer currentCustomer = (Customer) request.getSession().getAttribute("user");
            if (currentCustomer == null || currentCustomer.getCustomerID()!= attempt.getCustomerID()) {
                sendError(response, "You do not have access to this attempt");
                return;
            }

            // Check if attempt is already completed
            if (attempt.getEndTime() != null) {
                sendJsonResponse(response, attempt);
                return;
            }

            // Submit the attempt
            QuizAttempt submittedAttempt = attemptDAO.submitAttempt(attemptId);
            if (submittedAttempt == null) {
                sendError(response, "Failed to submit quiz");
                return;
            }
            sendJsonResponse(response, submittedAttempt);
        } catch (NumberFormatException e) {
            sendError(response, "Invalid attempt ID format");
        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, "Error submitting quiz");
        }
    }

    /**
     * Send a JSON response
     */
    private void sendJsonResponse(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(data));
        out.flush();
    }

    /**
     * Send an error response
     */
    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        JsonObject errorJson = new JsonObject();
        errorJson.addProperty("success", false);
        errorJson.addProperty("message", message);

        PrintWriter out = response.getWriter();
        out.print(gson.toJson(errorJson));
        out.flush();
    }
}
