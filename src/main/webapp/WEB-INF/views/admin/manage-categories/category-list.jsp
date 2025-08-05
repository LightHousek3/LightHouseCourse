<%-- Document : category-list Created on : Jun 13, 2025, 3:48:25 PM Author : NhiDTYCE-180492 --%>

<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="en">

    <head>
        <title>Manage Categories</title>
        <jsp:include page="../common/head.jsp" />
        <style>
            .stat-card {
                border-radius: 10px;
                overflow: hidden;
                transition: transform 0.3s;
            }

            .stat-card:hover {
                transform: translateY(-5px);
                box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
            }

            .stat-icon {
                font-size: 2.5rem;
                width: 70px;
                height: 70px;
                display: flex;
                align-items: center;
                justify-content: center;
                border-radius: 50%;
                margin-right: 15px;
            }

            .table th,
            .table td {
                vertical-align: middle !important;
            }

            .btn-sm {
                padding: 5px 10px;
                font-size: 0.875rem;
            }

            .bg-gradient-primary {
                background: linear-gradient(135deg, #3a8ffe 0%, #9658fe 100%);
            }

            .pagination-info {
                color: #6c757d;
                font-size: 0.9rem;
                margin-bottom: 15px;
            }

            .pagination .page-item.active .page-link {
                background-color: #e9f4ff;
                border-color: #e9f4ff;
            }

            .pagination .page-link {
                color: #3a8ffe;
            }

            .pagination .page-link:hover {
                color: #2971fe;
                background-color: #e9f4ff;
                border-color: #dee2e6;
            }
        </style>
    </head>

    <body>
        <!-- Admin Sidebar -->
        <c:set var="activeMenu" value="categories" scope="request" />
        <jsp:include page="../common/sidebar.jsp" />

        <!-- Main content -->
        <div class="admin-content">
            <!-- Header -->
            <div class="admin-header d-flex justify-content-between align-items-center">
                <button class="btn d-lg-none" id="toggleSidebarBtn">
                    <i class="fas fa-bars"></i>
                </button>
                <h2 class="m-0 d-none d-lg-block">Manage Categories</h2>
                <a href="${pageContext.request.contextPath}/admin/categories?action=create"
                   class="btn btn-lg btn-primary">
                    <i class="fas fa-plus me-2"></i> Add New Category
                </a>
            </div>
            <c:if test="${not empty message}">
                <div class="alert alert-info">${message}</div>
            </c:if>

            <!-- Success/Error Messages -->
            <c:if test="${param.success == 'added'}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    Category has been added successfully!
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>
            <c:if test="${param.success == 'updated'}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    Category has been updated successfully!
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>
            <c:if test="${param.success == 'deleted'}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    Category has been deleted successfully!
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>
            <c:if test="${param.error == 'delete_failed'}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <strong>Error:</strong> Unable to delete category. The category may have related
                    records.
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>
            <div class="mt-4">
                <div class="card">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <strong>Category List</strong>
                        <c:if test="${totalCategories > 0}">
                            <small class="text-muted">
                                Total: ${totalCategories} categories
                            </small>
                        </c:if>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-hover align-middle">
                                <thead class="table-light">
                                    <tr>
                                        <th style="width: 5%;">ID</th>
                                        <th style="width: 20%;">Name</th>
                                        <th style="width: 50%;">Description</th>
                                        <th style="width: 25%;">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="category" items="${categories}">
                                        <tr>
                                            <td>${category.categoryID}</td>
                                            <td><strong>${category.name}</strong></td>
                                            <td>${category.description}</td>
                                            <td>
                                                <div class="btn-group">
                                                    <a href="${pageContext.request.contextPath}/admin/categories?action=edit&id=${category.categoryID}&page=${currentPage}"
                                                       class="btn btn btn-warning" title="Edit Category">
                                                        <i class="fas fa-edit"></i>
                                                    </a>
                                                    <button class="btn btn-sm btn-danger"
                                                            onclick="confirmDelete(${category.categoryID}, ${currentPage})"
                                                            title="Delete Category">
                                                        <i class="fas fa-trash-alt"></i>
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    <c:if test="${empty categories}">
                                        <tr>
                                            <td colspan="4" class="text-center py-4">
                                                <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
                                                <p class="text-muted mb-0">No categories found.</p>
                                                <a href="${pageContext.request.contextPath}/admin/categories?action=create"
                                                   class="btn btn-primary btn-sm mt-2">
                                                    <i class="fas fa-plus"></i> Add First Category
                                                </a>
                                            </td>
                                        </tr>
                                    </c:if>
                                </tbody>
                            </table>

                            <!-- Enhanced Pagination -->
                            <c:if test="${totalPages > 1}">
                                <nav aria-label="Category pagination" class="mt-4">
                                    <ul class="pagination justify-content-center">
                                        <!-- First Page -->
                                        <c:if test="${currentPage > 1}">
                                            <li class="page-item">
                                                <a class="page-link btn btn-outline-primary" href="?page=1"
                                                   title="First Page">
                                                    <i class="fas fa-angle-double-left"></i>
                                                </a>
                                            </li>
                                        </c:if>


                                        <!-- Page Numbers -->
                                        <c:set var="startPage" value="${currentPage - 2}" />
                                        <c:set var="endPage" value="${currentPage + 2}" />

                                        <c:if test="${startPage < 1}">
                                            <c:set var="startPage" value="1" />
                                        </c:if>

                                        <c:if test="${endPage > totalPages}">
                                            <c:set var="endPage" value="${totalPages}" />
                                        </c:if>

                                        <!-- Show ellipsis if there are pages before startPage -->
                                        <c:if test="${startPage > 1}">
                                            <li class="page-item">
                                                <a class="page-link btn btn-outline-primary"
                                                   href="?page=1">1</a>
                                            </li>
                                            <c:if test="${startPage > 2}">
                                                <li class="page-item disabled">
                                                    <span
                                                        class="page-link btn btn-outline-primary">...</span>
                                                </li>
                                            </c:if>
                                        </c:if>

                                        <!-- Page number links -->
                                        <c:forEach begin="${startPage}" end="${endPage}" var="i">
                                            <li class="page-item ${i == currentPage ? 'active' : ''}">
                                                <c:choose>
                                                    <c:when test="${i == currentPage}">
                                                        <span class="page-link btn btn-primary">${i}</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <a class="page-link btn btn-outline-primary"
                                                           href="?page=${i}">${i}</a>
                                                    </c:otherwise>
                                                </c:choose>
                                            </li>
                                        </c:forEach>

                                        <!-- Show ellipsis if there are pages after endPage -->
                                        <c:if test="${endPage < totalPages}">
                                            <c:if test="${endPage < totalPages - 1}">
                                                <li class="page-item disabled">
                                                    <span
                                                        class="page-link btn btn-outline-primary">...</span>
                                                </li>
                                            </c:if>
                                            <li class="page-item">
                                                <a class="page-link btn btn-outline-primary"
                                                   href="?page=${totalPages}">${totalPages}</a>
                                            </li>
                                        </c:if>

                                        <!-- Last Page -->
                                        <c:if test="${currentPage < totalPages}">
                                            <li class="page-item">
                                                <a class="page-link btn btn-outline-primary"
                                                   href="?page=${totalPages}" title="Last Page">
                                                    <i class="fas fa-angle-double-right"></i>
                                                </a>
                                            </li>
                                        </c:if>
                                    </ul>
                                </nav>

                                <!-- Page Size Info -->
                                <div class="text-center mt-3">
                                    <small class="text-muted">
                                        <i class="fas fa-info-circle"></i>
                                        Displaying ${itemsPerPage} items per page |
                                        Page ${currentPage} of ${totalPages}
                                    </small>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Delete Confirmation Modal -->
        <div class="modal fade" id="deleteConfirmModal" tabindex="-1"
             aria-labelledby="deleteConfirmModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header bg-danger text-white">
                        <h5 class="modal-title" id="deleteConfirmModalLabel">Confirm Deletion</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"
                                aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <p>Are you sure you want to delete this category?</p>
                        <p class="text-danger"><small>This action cannot be undone.</small></p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-md btn-secondary"
                                data-bs-dismiss="modal">Cancel</button>
                        <button type="button" class="btn btn-md btn-danger" id="confirmDeleteBtn">Delete</button>
                    </div>
                </div>
            </div>
        </div>

        <jsp:include page="../common/scripts.jsp" />

        <script>
            // Variables to store category ID and current page for deletion
            let categoryToDelete = null;
            let currentPageForDelete = null;

            function confirmDelete(categoryId, currentPage) {
                // Store the values for use in the modal
                categoryToDelete = categoryId;
                currentPageForDelete = currentPage;

                // Show the modal
                const deleteModal = new bootstrap.Modal(document.getElementById('deleteConfirmModal'));
                deleteModal.show();
            }

            // Handle the delete confirmation from the modal
            document.getElementById('confirmDeleteBtn').addEventListener('click', function () {
                if (categoryToDelete !== null) {
                    const form = document.createElement("form");
                    form.method = "POST";
                    form.action = "${pageContext.request.contextPath}/admin/categories";

                    const actionInput = document.createElement("input");
                    actionInput.type = "hidden";
                    actionInput.name = "action";
                    actionInput.value = "delete";

                    const idInput = document.createElement("input");
                    idInput.type = "hidden";
                    idInput.name = "categoryId";
                    idInput.value = categoryToDelete;

                    const pageInput = document.createElement("input");
                    pageInput.type = "hidden";
                    pageInput.name = "currentPage";
                    pageInput.value = currentPageForDelete;

                    form.appendChild(actionInput);
                    form.appendChild(idInput);
                    form.appendChild(pageInput);
                    document.body.appendChild(form);
                    form.submit();
                }

                // Close the modal
                const deleteModal = bootstrap.Modal.getInstance(document.getElementById('deleteConfirmModal'));
                deleteModal.hide();
            });

            // Auto-hide success messages after 5 seconds
            document.addEventListener('DOMContentLoaded', function () {
                const alerts = document.querySelectorAll('.alert-success');
                alerts.forEach(function (alert) {
                    setTimeout(function () {
                        const bsAlert = new bootstrap.Alert(alert);
                        bsAlert.close();
                    }, 5000);
                });
            });

            // Add smooth scrolling to pagination links
            document.querySelectorAll('.pagination a').forEach(function (link) {
                link.addEventListener('click', function (e) {
                    // Show loading state
                    const originalText = this.innerHTML;
                    this.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';

                    // Note: The page will redirect, so we don't need to restore the text
                });
            });
        </script>

    </body>

</html>