<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">

    <head>
        <title>Manage Super Users - LightHouse Admin</title>
        <jsp:include page="../common/head.jsp" />
        <style>
            .pagination {
                gap: 0;
            }

            .table-container {
                position: relative;
            }

            .pagination-container {
                display: flex;
                align-items: center;
                margin-top: 1rem;
                gap: 10px;
            }

            .pagination-button {
                margin: 0 1px;
            }

            .sort-button {
                background: none;
                border: none;
                padding: 0;
                cursor: pointer;
                margin-left: 5px;
                display: inline-flex;
                align-items: center;
                justify-content: center;
                width: 20px;
                height: 20px;
            }

            .sort-icon {
                position: relative;
                display: inline-block;
                width: 10px;
                height: 14px;
            }

            .sort-icon::before,
            .sort-icon::after {
                content: '';
                position: absolute;
                left: 0;
                width: 0;
                height: 0;
                border-style: solid;
            }

            .sort-icon::before {
                top: 0;
                border-width: 0 5px 6px 5px;
                border-color: transparent transparent #adb5bd transparent;
            }

            .sort-icon::after {
                bottom: 0;
                border-width: 6px 5px 0 5px;
                border-color: #adb5bd transparent transparent transparent;
            }

            .sort-icon.asc::before {
                border-color: transparent transparent #0d6efd transparent;
            }

            .sort-icon.desc::after {
                border-color: #0d6efd transparent transparent transparent;
            }

            .sort-button:hover .sort-icon::before,
            .sort-button:hover .sort-icon::after {
                border-color: #6c757d transparent #6c757d transparent;
            }

            .sort-button:hover .sort-icon.asc::before {
                border-color: transparent transparent #0d6efd transparent;
            }

            .sort-button:hover .sort-icon.desc::after {
                border-color: #0d6efd transparent transparent transparent;
            }

            .table th {
                white-space: nowrap;
                position: relative;
            }

            .table th span {
                display: inline-block;
                vertical-align: middle;
            }

            .table th .sort-button {
                vertical-align: middle;
            }

            .email-modal .form-group {
                margin-bottom: 1rem;
                text-align: start;
            }

            .email-modal label {
                font-weight: 500;
                margin-bottom: 5px;
            }

            .pagination-controls {
                display: flex;
                align-items: center;
            }

            .entries-selector {
                width: auto;
                min-width: 80px;
                margin: 0 10px;
            }
        </style>
    </head>

    <body>
        <!-- Include Admin Sidebar -->
        <c:set var="activeMenu" value="superusers" scope="request" />
        <jsp:include page="../common/sidebar.jsp" />
        <div class="admin-container">
            <!-- Admin Content -->
            <div class="admin-content">
                <div class="container-fluid">
                    <!-- Header -->
                    <div class="admin-header d-flex justify-content-between align-items-center">
                        <button class="btn d-lg-none" id="toggleSidebarBtn">
                            <i class="fas fa-bars"></i>
                        </button>
                        <h2 class="m-0 d-none d-lg-block">Manage Super Users</h2>
                        <div class="admin-header-control d-flex justify-content-end">
                            <a href="${pageContext.request.contextPath}/admin/superusers/add"
                               class="btn btn-lg btn-primary me-2">
                                <i class="fas fa-plus me-1"></i> Add Super User
                            </a>
                            <a href="${pageContext.request.contextPath}/admin/superusers"
                               class="btn btn-lg btn-outline-secondary">
                                <i class="fas fa-sync-alt me-1"></i> Refresh
                            </a>
                        </div>
                    </div>

                    <!-- Flash Messages -->
                    <c:if test="${param.success eq 'added'}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="fas fa-check-circle me-2"></i>
                            Super User added successfully!
                            <button type="button" class="btn-close" data-bs-dismiss="alert"
                                    aria-label="Close"></button>
                        </div>
                    </c:if>
                    <c:if test="${param.success eq 'updated'}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="fas fa-check-circle me-2"></i>
                            Super User updated successfully!
                            <button type="button" class="btn-close" data-bs-dismiss="alert"
                                    aria-label="Close"></button>
                        </div>
                    </c:if>
                    <c:if test="${param.success eq 'deleted'}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="fas fa-check-circle me-2"></i>
                            Super User deleted successfully!
                            <button type="button" class="btn-close" data-bs-dismiss="alert"
                                    aria-label="Close"></button>
                        </div>
                    </c:if>
                    <c:if test="${param.success eq 'email_sent'}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="fas fa-check-circle me-2"></i>
                            Email sent successfully!
                            <button type="button" class="btn-close" data-bs-dismiss="alert"
                                    aria-label="Close"></button>
                        </div>
                    </c:if>
                    <c:if test="${param.success eq 'status_changed'}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="fas fa-check-circle me-2"></i>
                            Super User status updated successfully!
                            <button type="button" class="btn-close" data-bs-dismiss="alert"
                                    aria-label="Close"></button>
                        </div>
                    </c:if>

                    <!-- Error Messages -->
                    <c:if test="${not empty param.error}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="fas fa-exclamation-circle me-2"></i>
                            <c:choose>
                                <c:when test="${param.error eq 'missing_fields'}">
                                    <strong>Error:</strong> Please fill in all required fields.
                                </c:when>
                                <c:when test="${param.error eq 'email_exists'}">
                                    <strong>Error:</strong> A user with this email already exists.
                                </c:when>
                                <c:when test="${param.error eq 'username_exists'}">
                                    <strong>Error:</strong> Username already exists. Please choose another one.
                                </c:when>
                                <c:when test="${param.error eq 'invalid_email'}">
                                    <strong>Error:</strong> Please enter a valid email address.
                                </c:when>
                                <c:when test="${param.error eq 'delete_failed'}">
                                    <strong>Error:</strong> Unable to delete user. The user may have related
                                    records.
                                </c:when>
                                <c:when test="${param.error eq 'email_failed'}">
                                    <strong>Error:</strong> Failed to send email. Please try again.
                                </c:when>
                                <c:when test="${param.error eq 'status_toggle_failed'}">
                                    <strong>Error:</strong> Failed to update user status. Please try again.
                                </c:when>
                                <c:when test="${param.error eq 'invalid_id'}">
                                    <strong>Error:</strong> Invalid user ID.
                                </c:when>
                                <c:otherwise>
                                    <strong>Error:</strong> ${param.error}
                                </c:otherwise>
                            </c:choose>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"
                                    aria-label="Close"></button>
                        </div>
                    </c:if>

                    <!-- Search & Filter Controls -->
                    <div class="card mb-3">
                        <div class="row card-body">
                            <div class="col-lg-3">
                                <div class="me-3 mb-2">
                                    <label class="me-2">Search super users</label>
                                </div>
                                <div class="search-container">
                                    <i class="fas fa-search"></i>
                                    <input type="text" id="searchInput" class="search-input"
                                           placeholder="Search super users...">
                                </div>
                            </div>
                            <div class="col-lg-3">
                                <div class="me-3 mb-2">
                                    <label class="me-2">Filter by role</label>
                                </div>
                                <select id="filterRole" class="form-select">
                                    <option>All</option>
                                    <option ${selectedRole == "admin" ? "selected" : ""}>Admin</option>
                                    <option ${selectedRole == "instructor" ? "selected" : ""}>Instructor</option>
                                </select>
                            </div>
                        </div>
                    </div>

                    <!-- Content Card -->
                    <div class="card">
                        <div class="card-body">

                            <!-- User Table -->
                            <div class="table-responsive">
                                <table class="table table-hover" id="superUsersTable">
                                    <thead>
                                        <tr>
                                            <th scope="col">
                                                <span>ID</span>
                                                <button class="sort-button" data-sort="id">
                                                    <span class="sort-icon"></span>
                                                </button>
                                            </th>
                                            <th scope="col">
                                                <span>Username</span>
                                                <button class="sort-button" data-sort="username">
                                                    <span class="sort-icon"></span>
                                                </button>
                                            </th>
                                            <th scope="col">
                                                <span>Full Name</span>
                                                <button class="sort-button" data-sort="name">
                                                    <span class="sort-icon"></span>
                                                </button>
                                            </th>
                                            <th scope="col">
                                                <span>Email</span>
                                                <button class="sort-button" data-sort="email">
                                                    <span class="sort-icon"></span>
                                                </button>
                                            </th>
                                            <th scope="col">
                                                <span>Role</span>
                                                <button class="sort-button" data-sort="role">
                                                    <span class="sort-icon"></span>
                                                </button>
                                            </th>
                                            <th scope="col">
                                                <span>Status</span>
                                                <button class="sort-button" data-sort="status">
                                                    <span class="sort-icon"></span>
                                                </button>
                                            </th>
                                            <th scope="col" class="text-center">Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="user" items="${users}">
                                            <c:if test="${user['class'].simpleName eq 'SuperUser'}">
                                                <tr>
                                                    <td>${user.superUserID}</td>
                                                    <td>${user.username}</td>
                                                    <td>${user.fullName}</td>
                                                    <td>${user.email}</td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${user.role eq 'admin'}">
                                                                <span class="status-badge status-admin">Admin</span>
                                                            </c:when>
                                                            <c:when test="${user.role eq 'instructor'}">
                                                                <span
                                                                    class="status-badge status-instructor">Instructor</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                ${user.role}
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${user.isActive}">
                                                                <span
                                                                    class="status-badge status-active">Active</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span
                                                                    class="status-badge status-inactive">Inactive</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td class="text-center">
                                                        <div class="btn-group">
                                                            <a href="${pageContext.request.contextPath}/admin/superusers/view/${user.superUserID}"
                                                               class="btn btn-sm btn-info">
                                                                <i class="fas fa-eye"></i>
                                                            </a>
                                                            <a href="${pageContext.request.contextPath}/admin/superusers/edit/${user.superUserID}"
                                                               class="btn btn-sm btn-secondary">
                                                                <i class="fas fa-edit"></i>
                                                            </a>
                                                            <button type="button" class="btn btn-sm btn-danger"
                                                                    data-bs-toggle="modal"
                                                                    data-bs-target="#deleteSuperUserModal${user.superUserID}">
                                                                <i class="fas fa-trash-alt"></i>
                                                            </button>

                                                            <!-- Toggle Status Form -->
                                                            <form
                                                                action="${pageContext.request.contextPath}/admin/superusers"
                                                                method="post" class="d-inline">
                                                                <input type="hidden" name="action"
                                                                       value="toggleStatus">
                                                                <input type="hidden" name="superUserId"
                                                                       value="${user.superUserID}">
                                                                <button type="submit"
                                                                        class="btn btn-sm ${user.isActive ? 'btn-warning' : 'btn-success'}"
                                                                        title="${user.isActive ? 'Deactivate' : 'Activate'} user"
                                                                        style="border-radius: 0">
                                                                    <i
                                                                        class="fas ${user.isActive ? 'fa-ban' : 'fa-check-circle'}"></i>
                                                                </button>
                                                            </form>
                                                            <button type="button" class="btn btn-sm btn-primary"
                                                                    data-bs-toggle="modal"
                                                                    data-bs-target="#emailModal"
                                                                    data-recipient="${user.email}"
                                                                    data-recipient-name="${user.fullName}">
                                                                <i class="fas fa-envelope"></i>
                                                            </button>
                                                        </div>

                                                        <!-- Delete SuperUser Modal -->
                                                        <div class="modal fade"
                                                             id="deleteSuperUserModal${user.superUserID}"
                                                             tabindex="-1"
                                                             aria-labelledby="deleteSuperUserModalLabel${user.superUserID}"
                                                             aria-hidden="true">
                                                            <div class="modal-dialog">
                                                                <div class="modal-content">
                                                                    <div class="modal-header">
                                                                        <h5 class="modal-title"
                                                                            id="deleteSuperUserModalLabel${user.superUserID}">
                                                                            Confirm Delete</h5>
                                                                        <button type="button" class="btn-close"
                                                                                data-bs-dismiss="modal"
                                                                                aria-label="Close">
                                                                        </button>
                                                                    </div>
                                                                    <div class="modal-body">
                                                                        Are you sure you want to delete user
                                                                        <strong>${user.username}</strong> with
                                                                        role
                                                                        <strong>${user.role}</strong>?
                                                                        This action cannot be undone.
                                                                    </div>
                                                                    <div class="modal-footer">
                                                                        <button type="button"
                                                                                class="btn btn-md btn-secondary"
                                                                                data-bs-dismiss="modal">Cancel</button>
                                                                        <form
                                                                            action="${pageContext.request.contextPath}/admin/superusers"
                                                                            method="post">
                                                                            <input type="hidden" name="action"
                                                                                   value="deleteUser">
                                                                            <input type="hidden"
                                                                                   name="superUserId"
                                                                                   value="${user.superUserID}">
                                                                            <button type="submit"
                                                                                    class="btn btn-md btn-danger">Delete</button>
                                                                        </form>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:if>
                                        </c:forEach>

                                        <c:if test="${empty users}">
                                            <tr>
                                                <td colspan="7" class="text-center">No users found</td>
                                            </tr>
                                        </c:if>
                                    </tbody>
                                </table>
                            </div>

                            <div class="pagination-container">
                                <div class="pagination-controls">
                                    <span>Show</span>
                                    <select class="form-select entries-selector" id="entriesSelector">
                                        <option value="10">10</option>
                                        <option value="25">25</option>
                                        <option value="50">50</option>
                                        <option value="100">100</option>
                                    </select>
                                    <span>entries</span>
                                </div>
                                <div class="pagination">
                                    <button class="btn btn-sm btn-outline-primary pagination-button"
                                            id="prevPageBtn">&laquo;</button>
                                    <div id="paginationButtons" class="d-flex">
                                        <!-- Pagination buttons will be inserted here -->
                                    </div>
                                    <button class="btn btn-sm btn-outline-primary pagination-button"
                                            id="nextPageBtn">&raquo;</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Email Modal -->
        <div class="modal fade email-modal" id="emailModal" tabindex="-1" aria-labelledby="emailModalLabel"
             aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <form action="${pageContext.request.contextPath}/admin/superusers" method="post">
                        <input type="hidden" name="action" value="sendEmail">
                        <input type="hidden" id="emailRecipient" name="recipient" value="">
                        <div class="modal-header">
                            <h5 class="modal-title" id="emailModalLabel">Send Email</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"
                                    aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <div class="form-group">
                                <label for="emailSubject">Subject:</label>
                                <input type="text" class="form-control" id="emailSubject" name="subject"
                                       required>
                            </div>
                            <div class="form-group">
                                <label for="emailMessage">Message:</label>
                                <textarea class="form-control" id="emailMessage" name="message" rows="5"
                                          required></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-md btn-secondary"
                                    data-bs-dismiss="modal">Cancel</button>
                            <button type="submit" class="btn btn-md btn-primary">Send</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <jsp:include page="../common/scripts.jsp" />

        <script>
            // JavaScript for pagination, sorting, and searching
            document.addEventListener('DOMContentLoaded', function () {
                const table = document.getElementById('superUsersTable');
                const tbody = table.querySelector('tbody');
                const rows = Array.from(tbody.querySelectorAll('tr'));
                const entriesSelector = document.getElementById('entriesSelector');
                const searchInput = document.getElementById('searchInput');
                const prevPageBtn = document.getElementById('prevPageBtn');
                const nextPageBtn = document.getElementById('nextPageBtn');
                const paginationButtons = document.getElementById('paginationButtons');

                let currentPage = 1;
                let entriesPerPage = parseInt(entriesSelector.value);
                let filteredRows = rows;
                let sortColumn = 'id';
                let sortDirection = 'asc';

                // Search function
                function filterRows() {
                    const searchTerm = searchInput.value.toLowerCase();
                    filteredRows = rows.filter(row => {
                        return Array.from(row.cells).some(cell => cell.textContent.toLowerCase().includes(searchTerm));
                    });
                    renderTable();
                }

                // Sort function
                function sortRows(rowsToSort) {
                    return rowsToSort.sort((a, b) => {
                        let cellA, cellB;

                        switch (sortColumn) {
                            case 'id':
                                cellA = parseInt(a.cells[0].textContent);
                                cellB = parseInt(b.cells[0].textContent);
                                break;
                            case 'username':
                                cellA = a.cells[1].textContent.toLowerCase();
                                cellB = b.cells[1].textContent.toLowerCase();
                                break;
                            case 'name':
                                cellA = a.cells[2].textContent.toLowerCase();
                                cellB = b.cells[2].textContent.toLowerCase();
                                break;
                            case 'email':
                                cellA = a.cells[3].textContent.toLowerCase();
                                cellB = b.cells[3].textContent.toLowerCase();
                                break;
                            case 'role':
                                cellA = a.cells[4].textContent.toLowerCase();
                                cellB = b.cells[4].textContent.toLowerCase();
                                break;
                            case 'status':
                                cellA = a.cells[5].textContent.toLowerCase();
                                cellB = b.cells[5].textContent.toLowerCase();
                                break;
                            default:
                                cellA = a.cells[0].textContent.toLowerCase();
                                cellB = b.cells[0].textContent.toLowerCase();
                        }

                        // Handle numeric comparison for ID
                        if (sortColumn === 'id') {
                            return sortDirection === 'asc' ? cellA - cellB : cellB - cellA;
                        }

                        // String comparison for other columns
                        return sortDirection === 'asc'
                                ? cellA.localeCompare(cellB)
                                : cellB.localeCompare(cellA);
                    });
                }

                // Render table function
                function renderTable() {
                    const startIndex = (currentPage - 1) * entriesPerPage;
                    const endIndex = Math.min(startIndex + entriesPerPage, filteredRows.length);

                    // Get the visible rows for the current page before sorting
                    let visibleRows = filteredRows.slice(startIndex, endIndex);

                    // Only sort the visible rows on the current page
                    visibleRows = sortRows(visibleRows);

                    // Clear table
                    tbody.innerHTML = '';

                    // No results message
                    if (visibleRows.length === 0) {
                        const noResultsRow = document.createElement('tr');
                        const noResultsCell = document.createElement('td');
                        noResultsCell.colSpan = 7;
                        noResultsCell.className = 'text-center';
                        noResultsCell.textContent = 'No matching records found';
                        noResultsRow.appendChild(noResultsCell);
                        tbody.appendChild(noResultsRow);
                    } else {
                        // Add visible rows
                        visibleRows.forEach(row => {
                            tbody.appendChild(row);
                        });
                    }

                    // Update pagination
                    renderPagination();
                }

                // Render pagination
                function renderPagination() {
                    const totalPages = Math.ceil(filteredRows.length / entriesPerPage);
                    paginationButtons.innerHTML = '';

                    // Determine page range to display
                    let startPage = Math.max(1, currentPage - 2);
                    let endPage = Math.min(totalPages, startPage + 4);

                    if (endPage - startPage < 4) {
                        startPage = Math.max(1, endPage - 4);
                    }

                    // First page button
                    if (startPage > 1) {
                        addPageButton(1);
                        if (startPage > 2) {
                            addEllipsis();
                        }
                    }

                    // Page buttons
                    for (let i = startPage; i <= endPage; i++) {
                        addPageButton(i);
                    }

                    // Last page button
                    if (endPage < totalPages) {
                        if (endPage < totalPages - 1) {
                            addEllipsis();
                        }
                        addPageButton(totalPages);
                    }

                    // Enable/disable prev/next buttons
                    prevPageBtn.disabled = currentPage === 1;
                    nextPageBtn.disabled = currentPage === totalPages || totalPages === 0;
                }

                function addPageButton(pageNum) {
                    const button = document.createElement('button');
                    button.className = `btn btn-sm pagination-button \${pageNum === currentPage ? 'btn-primary' : 'btn-outline-primary'}`;
                    button.textContent = pageNum;
                    button.addEventListener('click', () => {
                        currentPage = pageNum;
                        renderTable();
                    });
                    paginationButtons.appendChild(button);
                }

                function addEllipsis() {
                    const span = document.createElement('span');
                    span.className = 'pagination-ellipsis mx-1';
                    span.textContent = '...';
                    paginationButtons.appendChild(span);
                }

                // Email modal recipient handling
                document.querySelectorAll('[data-bs-toggle="modal"][data-bs-target="#emailModal"]').forEach(button => {
                    button.addEventListener('click', function () {
                        const recipient = this.getAttribute('data-recipient');
                        const recipientName = this.getAttribute('data-recipient-name');

                        document.getElementById('emailRecipient').value = recipient;
                        document.getElementById('emailModalLabel').textContent = 'Send email to ' + recipientName;
                        document.getElementById('emailSubject').value = 'Hello ' + recipientName;
                        document.getElementById('emailMessage').value = 'Dear ' + recipientName + ',\n\n';
                    });
                });

                // Event listeners
                searchInput.addEventListener('input', filterRows);

                entriesSelector.addEventListener('change', function () {
                    entriesPerPage = parseInt(this.value);
                    currentPage = 1;
                    renderTable();
                });

                prevPageBtn.addEventListener('click', function () {
                    if (currentPage > 1) {
                        currentPage--;
                        renderTable();
                    }
                });

                nextPageBtn.addEventListener('click', function () {
                    const totalPages = Math.ceil(filteredRows.length / entriesPerPage);
                    if (currentPage < totalPages) {
                        currentPage++;
                        renderTable();
                    }
                });

                // Add click event to sort buttons
                document.querySelectorAll('.sort-button').forEach(button => {
                    button.addEventListener('click', function () {
                        const column = this.getAttribute('data-sort');

                        // Toggle direction if same column, otherwise default to asc
                        if (sortColumn === column) {
                            sortDirection = sortDirection === 'asc' ? 'desc' : 'asc';
                        } else {
                            sortColumn = column;
                            sortDirection = 'asc';
                        }

                        // Update sort icons
                        document.querySelectorAll('.sort-icon').forEach(icon => {
                            icon.classList.remove('asc', 'desc');
                        });

                        const sortIcon = this.querySelector('.sort-icon');
                        sortIcon.classList.add(sortDirection);

                        renderTable();
                    });
                });

                // Auto dismiss alerts after 5 seconds
                setTimeout(function () {
                    document.querySelectorAll('.alert').forEach(function (alert) {
                        const bsAlert = new bootstrap.Alert(alert);
                        bsAlert.close();
                    });
                }, 5000);

                // Initial render
                filterRows();

                // Filter by role
                const filterRole = document.getElementById('filterRole');
                filterRole.addEventListener('change', function () {
                    const role = this.value.toLowerCase();
                    window.location = role === 'all' ? `${pageContext.request.contextPath}/admin/superusers`
                            : `${pageContext.request.contextPath}/admin/superusers?role=\${role}`;
                });
            });
        </script>
    </body>

</html>