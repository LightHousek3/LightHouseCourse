package controller.admin;

import dao.RefundRequestDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import model.RefundRequest;
/**
 * Admin controller for managing refund requests (No authorization required)
 */
@WebServlet(name = "AdminRefundServlet", urlPatterns = {"/admin/refunds", "/admin/refunds/*"})
public class AdminRefundServlet extends HttpServlet {

    private static final int PAGE_SIZE = 10;
    private RefundRequestDAO refundDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        refundDAO = new RefundRequestDAO();
    }

    /**
     * Handles the HTTP GET request - displays refund management interfaces No
     * user authorization check required
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        String status = request.getParameter("status");
        String search = request.getParameter("search");

        int page = 1;
        try {
            String pageStr = request.getParameter("page");
            if (pageStr != null) {
                page = Integer.parseInt(pageStr);
                if (page < 1) {
                    page = 1;
                }
            }
        } catch (NumberFormatException e) {
            page = 1;
        }

        if (pathInfo == null || pathInfo.equals("/")) {
            List<RefundRequest> refundRequests;
            int totalRequests;

            if ((status != null && !status.trim().isEmpty()) || (search != null && !search.trim().isEmpty())) {
                totalRequests = refundDAO.getTotalRequestsByStatusAndSearch(status, search);
                refundRequests = refundDAO.getByStatusAndSearch(status, search, page, PAGE_SIZE);
            } else {
                totalRequests = refundDAO.getTotalRequests();
                refundRequests = refundDAO.getAllRefundRequests(page, PAGE_SIZE);
            }

            int totalPages = (int) Math.ceil((double) totalRequests / PAGE_SIZE);

            request.setAttribute("refundRequests", refundRequests);
            request.setAttribute("selectedStatus", status);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("search", search);

            request.getRequestDispatcher("/WEB-INF/views/admin/manage-refunds/view-refund-requests.jsp")
                    .forward(request, response);
            return;
        }

        if (pathInfo.startsWith("/view/")) {
            // Show refund request details
            String refundIdStr = pathInfo.substring("/view/".length());
            try {
                int refundId = Integer.parseInt(refundIdStr);
                RefundRequest refundRequest = refundDAO.getRefundById(refundId);

                if (refundRequest == null) {
                    request.setAttribute("error", "Refund request not found with ID: " + refundId);
                    request.getRequestDispatcher("/WEB-INF/views/admin/error.jsp").forward(request, response);
                    return;
                }

                request.setAttribute("refundRequest", refundRequest);
                request.getRequestDispatcher("/WEB-INF/views/admin/manage-refunds/view-refund-detail.jsp")
                        .forward(request, response);
                return;
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Invalid refund ID format");
                request.getRequestDispatcher("/WEB-INF/views/admin/error.jsp").forward(request, response);
                return;
            } catch (Exception e) {
                request.setAttribute("error", "Error loading refund details: " + e.getMessage());
                request.getRequestDispatcher("/WEB-INF/views/admin/error.jsp").forward(request, response);
                return;
            }
        }

        // Default - redirect to main refunds listing
        response.sendRedirect(request.getContextPath() + "/admin/refunds");
    }

    /**
     * Handles the HTTP POST request - processes refund status updates No user
     * authorization check required
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("processRefund".equals(action)) {
            processRefundRequest(request, response);
            return;
        }

        // Default - redirect to main refunds listing
        response.sendRedirect(request.getContextPath() + "/admin/refunds");
    }

    /**
     * Process refund request approval/rejection
     */
    private void processRefundRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String refundIdParam = request.getParameter("refundId");
        String status = request.getParameter("status"); // "approved" or "rejected"
        String adminMessage = request.getParameter("adminMessage");

        // Validate input parameters
        if (refundIdParam == null || status == null
                || (!status.equals("approved") && !status.equals("rejected"))) {
            response.sendRedirect(request.getContextPath() + "/admin/refunds?error=invalid_parameters");
            return;
        }

        // Admin message is required
        if (adminMessage == null || adminMessage.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath()
                    + "/admin/refunds/details/" + refundIdParam + "?error=message_required");
            return;
        }

        try {
            int refundId = Integer.parseInt(refundIdParam);
            int adminId = 1; // You should get this from session/authentication

            // Get refund request details before updating status
            RefundRequest refundRequest = refundDAO.getRefundById(refundId);
            if (refundRequest == null) {
                response.sendRedirect(request.getContextPath() + "/admin/refunds?error=refund_not_found");
                return;
            }

            // Check if request is already processed
            if (!refundRequest.getStatus().equals("pending")) {
                response.sendRedirect(request.getContextPath()
                        + "/admin/refunds/details/" + refundId + "?error=already_processed");
                return;
            }

            // Process the refund request using RefundRequestDAO
            boolean success = refundDAO.processRequest(refundId, status, adminId, adminMessage);

            if (success) {
                response.sendRedirect(request.getContextPath()
                        + "/admin/refunds/details/" + refundId + "?success=true");
            } else {
                response.sendRedirect(request.getContextPath()
                        + "/admin/refunds/details/" + refundId + "?error=processing_failed");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/refunds?error=invalid_refund_id");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/refunds?error=processing_error");
        }
    }

    @Override
    public String getServletInfo() {
        return "Admin Refund Servlet - View and manage course refund requests (No authorization required)";
    }
}
