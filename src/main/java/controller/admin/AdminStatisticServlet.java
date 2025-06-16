/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller.admin;

import dao.OrderDetailDAO;
import dao.RatingDAO;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author DangPH - CE180896
 */
@WebServlet(name = "AdminStatisticsServlet", urlPatterns = {
        "/admin/statistics",
        "/admin/statistics/purchase",
        "/admin/statistics/purchase/monthly",
        "/admin/statistics/rating",
        "/admin/statistics/rating/monthly",
})
public class AdminStatisticServlet extends HttpServlet {
   
    private OrderDetailDAO orderDetailDAO;
    private RatingDAO ratingDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        orderDetailDAO = new OrderDetailDAO();
        ratingDAO = new RatingDAO();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathServlet = request.getServletPath();

        switch (pathServlet) {
            case "/admin/statistics/purchase":
                getPurchaseStatistics(request, response);
                break;
            case "/admin/statistics/purchase/monthly":
                getMonthlyPurchaseStatistics(request, response);
                break;
            case "/admin/statistics/rating":
                getRatingStatistics(request, response);
                break;
            case "/admin/statistics/rating/monthly":
                getMonthlyRatingStatistics(request, response);
                break;
            case "/admin/statistics/years":
                getAvailableYears(request, response);
                break;
            default:
                showStatisticsPage(request, response);
                break;
        }
    }

    private void showStatisticsPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Get available years for filtering
            List<Integer> orderYears = orderDetailDAO.getOrderYears();
            List<Integer> ratingYears = ratingDAO.getRatingYears();

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

            request.getRequestDispatcher("/WEB-INF/views/admin/view-statistics/statistics.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading statistics data");
        }
    }

    private void getPurchaseStatistics(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Get year parameter, default to current year
            int year = getCurrentYear(request);

            Map<String, Integer> coursesPurchaseCount = orderDetailDAO.getCoursePurchaseCountsByYear(year);

            // Create data object for JSON serialization
            Map<String, Object> result = new HashMap<>();
            result.put("labels", coursesPurchaseCount.keySet().toArray(new String[0]));
            result.put("data", coursesPurchaseCount.values().toArray(new Integer[0]));

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

    private void getMonthlyPurchaseStatistics(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Get year parameter, default to current year
            int year = getCurrentYear(request);

            Map<String, int[]> monthlyCounts = orderDetailDAO.getCoursePurchaseCountsByMonth(year);

            // Get list of month names
            List<String> months = getMonthNames();

            // Build dataset for each course
            List<Map<String, Object>> datasets = new ArrayList<>();

            int colorIndex = 0;
            for (Map.Entry<String, int[]> entry : monthlyCounts.entrySet()) {
                String courseName = entry.getKey();
                int[] monthData = entry.getValue();

                // Generate a color for this course
                String color = getChartColor(colorIndex++);

                // Create dataset
                Map<String, Object> dataset = new HashMap<>();
                dataset.put("label", courseName);
                dataset.put("data", monthData);
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

    private void getRatingStatistics(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Get year parameter, default to current year
            int year = getCurrentYear(request);

            Map<String, Double> coursesRating = ratingDAO.getAverageRatingsByYear(year);

            // Format ratings to display with one decimal place
            Map<String, String> formattedRatings = new HashMap<>();
            for (Map.Entry<String, Double> entry : coursesRating.entrySet()) {
                formattedRatings.put(entry.getKey(), String.format("%.1f", entry.getValue()));
            }

            // Create data object for JSON serialization
            Map<String, Object> result = new HashMap<>();
            result.put("labels", coursesRating.keySet().toArray(new String[0]));
            result.put("data", formattedRatings.values().toArray(new String[0]));

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

    private void getMonthlyRatingStatistics(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // Get year parameter, default to current year
            int year = getCurrentYear(request);

            Map<String, double[]> monthlyRatings = ratingDAO.getAverageRatingsByMonth(year);

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

    private void getAvailableYears(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            List<Integer> orderYears = orderDetailDAO.getOrderYears();
            List<Integer> ratingYears = ratingDAO.getRatingYears();

            // Combine and deduplicate years
            Set<Integer> allYears = new HashSet<>();
            allYears.addAll(orderYears);
            allYears.addAll(ratingYears);

            List<Integer> years = new ArrayList<>(allYears);
            Collections.sort(years, Collections.reverseOrder());

            // Create data object for JSON serialization
            Map<String, Object> result = new HashMap<>();
            result.put("years", years);

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
