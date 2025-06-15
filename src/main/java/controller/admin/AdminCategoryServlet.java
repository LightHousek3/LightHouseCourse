/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.admin;

import dao.CategoryDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.Category;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author NhiDTYCE-180492
 */
@WebServlet(name = "AdminCategoryServlet", urlPatterns = {"/admin/categories", "/admin/categories/*", "/admin/categories-update"})
public class AdminCategoryServlet extends HttpServlet {

    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        categoryDAO = new CategoryDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("create".equals(action)) {
            // Chuyển hướng đến trang tạo mới Category
            request.getRequestDispatcher("/WEB-INF/views/admin/manage-categories/category-create.jsp").forward(request, response);
            return;
        }

        if ("edit".equals(action)) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                Category category = categoryDAO.getCategoryById(id);

                if (category != null) {
                    request.setAttribute("category", category);
                    request.getRequestDispatcher("/WEB-INF/views/admin/manage-categories/category-update.jsp").forward(request, response);
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/categories?error=invalid_id");
                }
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/admin/categories?error=invalid_id");
            }
            return;
        }

        // Handle pagination
        int currentPage = 1;
        String pageParam = request.getParameter("page");

        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageParam);
                if (currentPage < 1) {
                    currentPage = 1;
                }
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }

        // Get pagination data
        int itemsPerPage = categoryDAO.getItemsPerPage(); // 10 items per page
        int totalCategories = categoryDAO.getTotalCategoriesCount();
        int totalPages = categoryDAO.getTotalPages(totalCategories, itemsPerPage);

        // Ensure current page doesn't exceed total pages
        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages;
        }

        // Get categories for current page
        List<Category> categories = categoryDAO.getCategoriesWithPagination(currentPage, itemsPerPage);

        // Set attributes for JSP
        request.setAttribute("categories", categories);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalCategories", totalCategories);
        request.setAttribute("itemsPerPage", itemsPerPage);

        // For backward compatibility with existing JSP
        request.setAttribute("page", currentPage);
        // Forward to the category management page
        request.getRequestDispatcher("/WEB-INF/views/admin/manage-categories/category-list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String servletPath = request.getServletPath();

        String name = request.getParameter("name");
        String description = request.getParameter("description");

        name = (name != null) ? name.trim() : "";
        description = (description != null) ? description.trim() : "";

        // Encode parameters for URL
        String encodedName = "";
        String encodedDescription = "";
        try {
            encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8.toString());
            encodedDescription = URLEncoder.encode(description, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            encodedName = "";
            encodedDescription = "";
        }

        if ("add".equals(action)) {
            // Add new category
            System.out.println("=== ADDING NEW CATEGORY ===");
            System.out.println("Name input: '" + name + "'");
            System.out.println("Description input: '" + description + "'");

            // Validate required fields
            if (name.isEmpty() || description.isEmpty()) {
                System.out.println("Missing fields detected");
                response.sendRedirect(request.getContextPath() + "/admin/categories?action=create"
                        + "&error=missing_fields"
                        + "&name=" + encodedName
                        + "&description=" + encodedDescription);
                return;
            }

            // Validate character limits
            if (name.length() > 50) {
                System.out.println("Category name too long: " + name.length() + " characters");
                response.sendRedirect(request.getContextPath() + "/admin/categories?action=create"
                        + "&error=name_too_long"
                        + "&name=" + encodedName
                        + "&description=" + encodedDescription);
                return;
            }

            if (description.length() > 150) {
                System.out.println("Description too long: " + description.length() + " characters");
                response.sendRedirect(request.getContextPath() + "/admin/categories?action=create"
                        + "&error=description_too_long"
                        + "&name=" + encodedName
                        + "&description=" + encodedDescription);
                return;
            }

            // Check for duplicate name
            boolean isDuplicate = categoryDAO.checkCategoryExists(name);
            System.out.println("Duplicate check result: " + isDuplicate);

            if (isDuplicate) {
                System.out.println("Duplicate category name detected: '" + name + "'");
                response.sendRedirect(request.getContextPath() + "/admin/categories?action=create"
                        + "&error=duplicate"
                        + "&name=" + encodedName
                        + "&description=" + encodedDescription);
                return;
            }

            // Create new category
            Category category = new Category();
            category.setName(name);
            category.setDescription(description);

            int newId = categoryDAO.insertCategory(category);
            System.out.println("Insert result ID: " + newId);

            if (newId > 0) {
                System.out.println("Category added successfully with ID: " + newId);
                response.sendRedirect(request.getContextPath() + "/admin/categories?success=added");
            } else {
                System.out.println("Failed to add category");
                response.sendRedirect(request.getContextPath() + "/admin/categories?action=create&error=add_failed"
                        + "&name=" + encodedName
                        + "&description=" + encodedDescription);
            }

        } else if ("/admin/categories-update".equals(servletPath)) {
            // Update existing category
            String categoryIdStr = request.getParameter("categoryId");
            String currentPageStr = request.getParameter("currentPage");

            System.out.println("=== UPDATING CATEGORY ===");
            System.out.println("Category ID: " + categoryIdStr);
            System.out.println("Name input: '" + name + "'");
            System.out.println("Description input: '" + description + "'");

            // Validate required fields
            if (categoryIdStr == null || categoryIdStr.trim().isEmpty() || name.isEmpty() || description.isEmpty()) {
                System.out.println("Missing fields in update");
                String redirectUrl = request.getContextPath() + "/admin/categories?action=edit&id=" + categoryIdStr
                        + "&error=missing_fields"
                        + "&name=" + encodedName
                        + "&description=" + encodedDescription;

                if (currentPageStr != null && !currentPageStr.trim().isEmpty()) {
                    redirectUrl += "&page=" + currentPageStr;
                }

                response.sendRedirect(redirectUrl);
                return;
            }

            try {
                int categoryId = Integer.parseInt(categoryIdStr);

                // Check for duplicate name (excluding current category)
                boolean isDuplicate = categoryDAO.checkCategoryExistsExceptId(name, categoryId);
                System.out.println("Duplicate check for update (except ID " + categoryId + "): " + isDuplicate);

                if (isDuplicate) {
                    System.out.println("Duplicate category name detected in update: '" + name + "'");
                    String redirectUrl = request.getContextPath() + "/admin/categories?action=edit&id=" + categoryId
                            + "&error=duplicate"
                            + "&name=" + encodedName
                            + "&description=" + encodedDescription;

                    if (currentPageStr != null && !currentPageStr.trim().isEmpty()) {
                        redirectUrl += "&page=" + currentPageStr;
                    }

                    response.sendRedirect(redirectUrl);
                    return;
                }

                // Update category
                Category category = new Category();
                category.setCategoryID(categoryId);
                category.setName(name);
                category.setDescription(description);

                boolean success = categoryDAO.updateCategory(category);
                System.out.println("Update result: " + success);

                if (success) {
                    System.out.println("Category updated successfully");
                    String redirectUrl = request.getContextPath() + "/admin/categories?success=updated";

                    if (currentPageStr != null && !currentPageStr.trim().isEmpty()) {
                        redirectUrl += "&page=" + currentPageStr;
                    }

                    response.sendRedirect(redirectUrl);
                } else {
                    System.out.println("Failed to update category");
                    String redirectUrl = request.getContextPath() + "/admin/categories?action=edit&id=" + categoryId
                            + "&error=update_failed"
                            + "&name=" + encodedName
                            + "&description=" + encodedDescription;

                    if (currentPageStr != null && !currentPageStr.trim().isEmpty()) {
                        redirectUrl += "&page=" + currentPageStr;
                    }

                    response.sendRedirect(redirectUrl);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid category ID: " + categoryIdStr);
                response.sendRedirect(request.getContextPath() + "/admin/categories?error=invalid_id");
            }

        } else if ("delete".equals(action)) {
            // Delete category logic remains the same
            String categoryIdStr = request.getParameter("categoryId");
            String currentPageStr = request.getParameter("currentPage");

            if (categoryIdStr != null && !categoryIdStr.trim().isEmpty()) {
                try {
                    int categoryId = Integer.parseInt(categoryIdStr);
                    boolean success = categoryDAO.deleteCategory(categoryId);
                    if (success) {
                        int totalCategories = categoryDAO.getTotalCategoriesCount();
                        int itemsPerPage = categoryDAO.getItemsPerPage();
                        int totalPages = categoryDAO.getTotalPages(totalCategories, itemsPerPage);

                        int currentPage = 1;
                        if (currentPageStr != null && !currentPageStr.trim().isEmpty()) {
                            try {
                                currentPage = Integer.parseInt(currentPageStr);
                            } catch (NumberFormatException e) {
                                currentPage = 1;
                            }
                        }

                        if (currentPage > totalPages && totalPages > 0) {
                            currentPage = totalPages;
                        }

                        String redirectUrl = request.getContextPath() + "/admin/categories?success=deleted";
                        if (currentPage > 1) {
                            redirectUrl += "&page=" + currentPage;
                        }

                        response.sendRedirect(redirectUrl);
                    } else {
                        String redirectUrl = request.getContextPath() + "/admin/categories?error=delete_failed";
                        if (currentPageStr != null && !currentPageStr.trim().isEmpty()) {
                            redirectUrl += "&page=" + currentPageStr;
                        }
                        response.sendRedirect(redirectUrl);
                    }
                } catch (NumberFormatException e) {
                    response.sendRedirect(request.getContextPath() + "/admin/categories?error=invalid_id");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/categories?error=missing_id");
            }
        } else {
            // Default - redirect to category list
            response.sendRedirect(request.getContextPath() + "/admin/categories");
        }
    }
}
