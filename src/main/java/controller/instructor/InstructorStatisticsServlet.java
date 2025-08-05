/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.instructor;

import com.google.gson.Gson;
import dao.InstructorDAO;
import dao.OrderDetailDAO;
import dao.RatingDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import model.Instructor;
import model.SuperUser;

/**
 *
 * @author ADMIN
 */
@WebServlet(name = "InstructorStatisticsServlet", urlPatterns = {"/instructor/statistics",
    "/instructor/statistics/purchase",
    "/instructor/statistics/purchase/monthly",
    "/instructor/statistics/rating",
    "/instructor/statistics/rating/monthly",})
public class InstructorStatisticsServlet extends HttpServlet {

    private InstructorDAO instructorDAO;
    private OrderDetailDAO orderDetailDAO;
    private RatingDAO ratingDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        instructorDAO = new InstructorDAO();
        orderDetailDAO = new OrderDetailDAO();
        ratingDAO = new RatingDAO();
        gson = new Gson();
    }

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
        String path = request.getServletPath();

        switch (path) {
            case "/instructor/statistics/purchase":
                getPurchaseStatistics(request, response, instructor);
                break;
            case "/instructor/statistics/purchase/monthly":
                getMonthlyPurchaseStatistics(request, response, instructor);
                break;
            case "/instructor/statistics/rating":
                getRatingStatistics(request, response, instructor);
                break;
            case "/instructor/statistics/rating/monthly":
                getMonthlyRatingStatistics(request, response, instructor);
                break;
            default:
                showStatisticsPage(request, response, instructor);
        }
    }

    private void showStatisticsPage(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {
        try {
            // Get available years for filtering
            List<Integer> orderYears = orderDetailDAO.getOrderYearsByInstructor(instructor.getInstructorID());
            List<Integer> ratingYears = ratingDAO.getRatingYearsByInstructor(instructor.getInstructorID());

            // Combine and deduplicate years
            Set<Integer> allYears = new HashSet<>();
            allYears.addAll(orderYears);
            allYears.addAll(ratingYears);

            List<Integer> years = new ArrayList<>(allYears);
            Collections.sort(years, Collections.reverseOrder());

            // Set the current year as default
            int currentYear = LocalDate.now().getYear();
            if (!years.isEmpty() && !years.contains(currentYear)) {
                currentYear = years.get(0);
            }

            request.setAttribute("years", years);
            request.setAttribute("currentYear", currentYear);

            request.getRequestDispatcher("/WEB-INF/views/instructor/view-statistics/statistics.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading statistics data");
        }
    }

        private void getPurchaseStatistics(HttpServletRequest request, HttpServletResponse response, Instructor instructor) throws IOException {
            response.setContentType("application/json");
            int year = getCurrentYear(request);

            try ( PrintWriter out = response.getWriter()) {
                Map<String, Integer> data = orderDetailDAO.getCoursePurchaseCountsByYearForInstructor(year, instructor.getInstructorID());
                Map<String, Object> result = new HashMap<>();
                result.put("labels", data.keySet().toArray());
                result.put("data", data.values().toArray());
                out.print(gson.toJson(result));
            } catch (SQLException e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading purchase statistics");
            }
        }

    private void getMonthlyPurchaseStatistics(HttpServletRequest request, HttpServletResponse response, Instructor instructor) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            int year = getCurrentYear(request);
            Map<String, int[]> data = orderDetailDAO.getCoursePurchaseCountsByMonthForInstructor(year, instructor.getInstructorID());

            List<String> months = getMonthNames();
            List<Map<String, Object>> datasets = new ArrayList<>();
            int colorIndex = 0;

            for (Map.Entry<String, int[]> entry : data.entrySet()) {
                Map<String, Object> dataset = new HashMap<>();
                dataset.put("label", entry.getKey());
                dataset.put("data", entry.getValue());
                String color = getChartColor(colorIndex++);
                dataset.put("backgroundColor", color);
                dataset.put("borderColor", color);
                dataset.put("borderWidth", 1);
                datasets.add(dataset);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("labels", months);
            result.put("datasets", datasets);
            out.print(gson.toJson(result));
        } catch (SQLException e) {
            sendErrorResponse(response, out, e);
        } finally {
            out.flush();
            out.close();
        }
    }

    private void getRatingStatistics(HttpServletRequest request, HttpServletResponse response, Instructor instructor) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            int year = getCurrentYear(request);
            Map<String, Double> data = ratingDAO.getAverageRatingsByYearForInstructor(year, instructor.getInstructorID());

            Map<String, String> formatted = new HashMap<>();
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                formatted.put(entry.getKey(), String.format("%.1f", entry.getValue()));
            }

            Map<String, Object> result = new HashMap<>();
            result.put("labels", formatted.keySet().toArray());
            result.put("data", formatted.values().toArray());
            out.print(gson.toJson(result));
        } catch (SQLException e) {
            sendErrorResponse(response, out, e);
        } finally {
            out.flush();
            out.close();
        }
    }

    private void getMonthlyRatingStatistics(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Get year parameter, default to current year
            int year = getCurrentYear(request);

            Map<String, double[]> monthlyRatings = ratingDAO.getAverageRatingsByMonthByInstructor(instructor.getInstructorID(), year);

            // Get list of month names
            List<String> months = getMonthNames();

            // Build dataset for each course
            List<Map<String, Object>> datasets = new ArrayList<>();

            int colorIndex = 0;
            for (Map.Entry<String, double[]> entry : monthlyRatings.entrySet()) {
                String courseName = entry.getKey();
                double[] monthData = entry.getValue();

                // Format ratings
                String[] formattedRatings = formatRatings(monthData);

                // Generate a color for this course
                String color = getChartColor(colorIndex++);

                // Create dataset
                Map<String, Object> dataset = new HashMap<>();
                dataset.put("label", courseName);
                dataset.put("data", formattedRatings);
                dataset.put("backgroundColor", color);
                dataset.put("borderColor", color);
                dataset.put("borderWidth", 1);

                datasets.add(dataset);
            }

            // Build response object
            Map<String, Object> result = new HashMap<>();
            result.put("labels", months);
            result.put("datasets", datasets);

            // Convert to JSON
            String jsonResponse = gson.toJson(result);
            out.print(jsonResponse);
        } catch (SQLException e) {
            sendErrorResponse(response, out, e);
        } finally {
            out.flush();
            out.close();
        }
    }

    // Helper method to send error response
    private void sendErrorResponse(HttpServletResponse response, PrintWriter out, Exception e) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", e.getMessage());
        out.print(gson.toJson(errorResponse));
    }

    // Helper methods
    private int getCurrentYear(HttpServletRequest request) {
        String yearParam = request.getParameter("year");
        if (yearParam != null && !yearParam.isEmpty()) {
            try {
                return Integer.parseInt(yearParam);
            } catch (NumberFormatException e) {
                // Ignore and use current year
            }
        }
        return LocalDate.now().getYear();
    }

    private List<String> getMonthNames() {
        List<String> months = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            months.add(Month.of(i + 1).getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        }
        return months;
    }

    private String[] formatRatings(double[] ratings) {
        String[] formatted = new String[ratings.length];
        for (int i = 0; i < ratings.length; i++) {
            formatted[i] = ratings[i] > 0 ? String.format("%.1f", ratings[i]) : "0";
        }
        return formatted;
    }

    private String getChartColor(int index) {
        // Array of predefined colors for chart datasets
        String[] colors = {
            "rgba(54, 162, 235, 0.7)", // Blue
            "rgba(255, 99, 132, 0.7)", // Red
            "rgba(255, 206, 86, 0.7)", // Yellow
            "rgba(75, 192, 192, 0.7)", // Green
            "rgba(153, 102, 255, 0.7)", // Purple
            "rgba(255, 159, 64, 0.7)", // Orange
            "rgba(199, 199, 199, 0.7)", // Gray
            "rgba(83, 102, 255, 0.7)", // Blue-Purple
            "rgba(255, 99, 71, 0.7)", // Tomato
            "rgba(60, 179, 113, 0.7)" // Medium Sea Green
        };

        return colors[index % colors.length];
    }

}
