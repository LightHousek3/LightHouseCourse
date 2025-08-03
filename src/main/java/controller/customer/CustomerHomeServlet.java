/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.customer;

import dao.CartItemDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import dao.CourseDAO;
import dao.CategoryDAO;
import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import model.Course;
import model.Category;
import model.Customer;
import util.CartUtil;
import util.Validator;
import com.google.gson.Gson;

/**
 * Home page controller that shows featured courses.
 */
@WebServlet(name = "CustomerHomeServlet", urlPatterns = { "/home", "" })
public class CustomerHomeServlet extends HttpServlet {

    private CourseDAO courseDAO;
    private CategoryDAO categoryDAO;
    private CartItemDAO cartItemDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        courseDAO = new CourseDAO();
        categoryDAO = new CategoryDAO();
        cartItemDAO = new CartItemDAO();
    }

    /**
     * Handles the HTTP GET request - displaying the home page.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check if it's an AJAX request for loading more courses
        String ajaxParam = request.getParameter("ajax");
        if (ajaxParam != null && ajaxParam.equals("true")) {
            handleAjaxRequest(request, response);
            return;
        }

        // Get categories for sidebar
        List<Category> categories = categoryDAO.getAllCategories();
        request.setAttribute("categories", categories);

        // Get filter parameters
        String keyword = request.getParameter("keyword");
        String categoryParam = request.getParameter("category");
        String sortParam = request.getParameter("sortParam");

        // Default sort by newest if not specified
        if (sortParam == null || sortParam.isEmpty()) {
            sortParam = "newest";
        }

        // Validate keyword
        if (keyword != null && !isValidKeyword(keyword)) {
            request.setAttribute("errorMessage",
                    "Invalid keyword. Requirements:"
                            + "<ul>"
                            + "<li>Length 2-50 characters</li>"
                            + "<li>Must not contain special characters</li>"
                            + "<li>Maximum 5 words</li>"
                            + "<li>Must not repeat any word or phrase more than 3 times</li>"
                            + "</ul>");
            request.setAttribute("keyword", keyword);
            keyword = null;
        }

        // Default values
        int categoryId = 0;
        int pageSize = 8; // Courses per page for homepage
        int page = 1; // Default to first page

        // Parse parameters
        if (categoryParam != null && Validator.isValidNumber(categoryParam)) {
            categoryId = Integer.parseInt(categoryParam);
        }

        List<Course> courses;
        List<Course> displayCourses;
        int totalCourses;

        try {
            // Get courses based on filters
            if (keyword != null && !keyword.trim().isEmpty()) {
                // Search by keyword (and optionally category)
                courses = courseDAO.searchCourseByNameOrCategory(keyword, categoryId);
                totalCourses = courses.size();
                request.setAttribute("keyword", keyword);
                request.setAttribute("isSearchResult", true);
                request.setAttribute("totalSearchResults", totalCourses);
            } else if (categoryId > 0) {
                // Filter by category
                courses = courseDAO.getByCategory(categoryId);
                totalCourses = courses.size();
                Category category = categoryDAO.getCategoryById(categoryId);
                if (category != null) {
                    request.setAttribute("categoryName", category.getName());
                }
                request.setAttribute("isSearchResult", true);
                request.setAttribute("totalSearchResults", totalCourses);
            } else {
                // No filters, get all courses
                courses = courseDAO.getAllCourses();
                totalCourses = courseDAO.countAllCourses("approved");
                request.setAttribute("isSearchResult", false);
            }

            // Sort courses
            sortCourses(courses, sortParam);

            // Limit results to pageSize
            if (courses != null && courses.size() > 0) {
                if (courses.size() > pageSize) {
                    displayCourses = new ArrayList<>(courses.subList(0, pageSize));
                    request.setAttribute("hasMoreCourses", true);
                } else {
                    displayCourses = courses;
                    request.setAttribute("hasMoreCourses", false);
                }
                request.setAttribute("displayCourses", displayCourses);
                request.setAttribute("allCourses", courses); // Store all results for AJAX pagination
            }

            request.setAttribute("categoryId", categoryId);
            request.setAttribute("sort", sortParam);
            request.setAttribute("totalCourses", totalCourses);
            request.setAttribute("currentPage", page);

        } catch (Exception e) {
            System.err.println("Error loading courses: " + e.getMessage());
            e.printStackTrace();
        }

        // Get cart
        HttpSession session = request.getSession();
        Customer user = (Customer) session.getAttribute("user");

        // If user is logged in, load cart items from database
        if (user != null) {
            try {
                CartUtil cart = new CartUtil();
                cart.setItems(cartItemDAO.getCartItems(user.getCustomerID()));
                session.setAttribute("cart", cart);
            } catch (Exception e) {
                System.err.println("Error loading cart: " + e.getMessage());
                e.printStackTrace();
            }
        }
        // Forward to home page
        request.getRequestDispatcher("/WEB-INF/views/customer/homepage.jsp").forward(request, response);
    }

    /**
     * Handle AJAX requests for loading more courses
     */
    private void handleAjaxRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Get parameters
        String keyword = request.getParameter("keyword");
        String categoryParam = request.getParameter("category");
        String sortParam = request.getParameter("sortParam");
        String pageParam = request.getParameter("page");

        // Default values
        int categoryId = 0;
        int page = 1;
        int pageSize = 8;

        // Parse parameters
        if (categoryParam != null && Validator.isValidNumber(categoryParam)) {
            categoryId = Integer.parseInt(categoryParam);
        }

        if (pageParam != null && Validator.isValidNumber(pageParam)) {
            page = Integer.parseInt(pageParam);
        }

        // Default sort by newest if not specified
        if (sortParam == null || sortParam.isEmpty()) {
            sortParam = "newest";
        }

        List<Course> courses;
        List<Course> nextPageCourses = new ArrayList<>();
        boolean hasMoreCourses = false;

        try {
            // Get courses based on filters
            if (keyword != null && !keyword.trim().isEmpty()) {
                // Search by keyword (and optionally category)
                courses = courseDAO.searchCourseByNameOrCategory(keyword, categoryId);
            } else if (categoryId > 0) {
                // Filter by category
                courses = courseDAO.getByCategory(categoryId);
            } else {
                // No filters, get all courses
                courses = courseDAO.getAllCourses();
            }

            // Sort courses
            sortCourses(courses, sortParam);

            // Calculate start and end indices for the requested page
            int startIndex = page * pageSize;
            int endIndex = Math.min(startIndex + pageSize, courses.size());

            // Check if there are more courses after this page
            hasMoreCourses = endIndex < courses.size();

            // Get courses for the requested page
            if (startIndex < courses.size()) {
                nextPageCourses = courses.subList(startIndex, endIndex);
            }

            // Prepare response data
            CourseResponse response_data = new CourseResponse();
            response_data.setCourses(nextPageCourses);
            response_data.setHasMore(hasMoreCourses);
            response_data.setNextPage(page + 1);

            // Convert to JSON and send response
            Gson gson = new Gson();
            String jsonResponse = gson.toJson(response_data);
            response.getWriter().write(jsonResponse);

        } catch (Exception e) {
            System.err.println("Error loading more courses: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Failed to load more courses\"}");
        }
    }

    /**
     * Helper method to sort courses based on the sort parameter
     */
    private void sortCourses(List<Course> courses, String sortParam) {
        if (courses == null || courses.isEmpty()) {
            return;
        }

        switch (sortParam) {
            case "price_asc":
                Collections.sort(courses, new Comparator<Course>() {
                    @Override
                    public int compare(Course c1, Course c2) {
                        return Double.compare(c1.getPrice(), c2.getPrice());
                    }
                });
                break;
            case "price_desc":
                Collections.sort(courses, new Comparator<Course>() {
                    @Override
                    public int compare(Course c1, Course c2) {
                        return Double.compare(c2.getPrice(), c1.getPrice());
                    }
                });
                break;
            case "popularity":
                Collections.sort(courses, new Comparator<Course>() {
                    @Override
                    public int compare(Course c1, Course c2) {
                        return Integer.compare(c2.getEnrollmentCount(), c1.getEnrollmentCount());
                    }
                });
                break;
            default: // newest
                Collections.sort(courses, new Comparator<Course>() {
                    @Override
                    public int compare(Course c1, Course c2) {
                        return Integer.compare(c2.getCourseID(), c1.getCourseID());
                    }
                });
                break;
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Home Servlet - Displays the homepage with featured courses";
    }

    /**
     * Validates the search keyword
     *
     * @param keyword The keyword to validate
     * @return true if keyword is valid, false otherwise
     */
    private boolean isValidKeyword(String keyword) {
        // Kiểm tra keyword null hoặc trống
        if (keyword == null || keyword.trim().isEmpty()) {
            return true;
        }

        keyword = keyword.trim();

        // Kiểm tra độ dài từ khóa (2-50 ký tự)
        if (keyword.length() < 2 || keyword.length() > 50) {
            return false;
        }

        // Kiểm tra ký tự đặc biệt không cho phép
        String specialCharsPattern = "[!@#$%^&*(),.?\":{}|<>]";
        if (keyword.matches(".*" + specialCharsPattern + ".*")) {
            return false;
        }

        // Kiểm tra số từ trong từ khóa (tối đa 5 từ)
        if (keyword.split("\\s+").length > 5) {
            return false;
        }

        // Kiểm tra từ lặp lại
        String[] words = keyword.split("\\s+");
        String previousWord = "";
        int repeatCount = 1;
        int maxRepeat = 3; // Số lần lặp tối đa cho phép

        for (String word : words) {
            if (word.equals(previousWord)) {
                repeatCount++;
                if (repeatCount > maxRepeat) {
                    return false;
                }
            } else {
                repeatCount = 1;
                previousWord = word;
            }
        }

        // Kiểm tra chuỗi con lặp lại
        String normalizedKeyword = keyword.toLowerCase();
        for (int length = 3; length <= normalizedKeyword.length() / 2; length++) {
            for (int i = 0; i <= normalizedKeyword.length() - length; i++) {
                String substring = normalizedKeyword.substring(i, i + length);
                int count = 0;
                int lastIndex = 0;

                while ((lastIndex = normalizedKeyword.indexOf(substring, lastIndex)) != -1) {
                    count++;
                    lastIndex++;

                    if (count > 3) { // Nếu một chuỗi con xuất hiện quá 3 lần
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Response class for AJAX requests
     */
    private class CourseResponse {
        private List<Course> courses;
        private boolean hasMore;
        private int nextPage;

        public List<Course> getCourses() {
            return courses;
        }

        public void setCourses(List<Course> courses) {
            this.courses = courses;
        }

        public boolean isHasMore() {
            return hasMore;
        }

        public void setHasMore(boolean hasMore) {
            this.hasMore = hasMore;
        }

        public int getNextPage() {
            return nextPage;
        }

        public void setNextPage(int nextPage) {
            this.nextPage = nextPage;
        }
    }
}
