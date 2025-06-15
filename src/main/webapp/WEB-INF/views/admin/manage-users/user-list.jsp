<%-- Document : user-list Created on : Jun 13, 2025, 11:12:40 PM Author : DangPH - CE180896 --%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">

    <head>
        <title>Manage Users - LightHouse Admin</title>
        <jsp:include page="../common/head.jsp" />
        <style>
            .table-container {
                position: relative;
            }

            .table-controls {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 1rem;
            }

            .search-container {
                position: relative;
                max-width: 300px;
            }

            .search-container i {
                position: absolute;
                left: 10px;
                top: 10px;
                color: #6c757d;
            }

            .search-input {
                padding-left: 30px;
                border-radius: 5px;
                border: 1px solid #ced4da;
                width: 100%;
                padding: 0.375rem 0.75rem 0.375rem 30px;
            }

            .pagination-container {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-top: 1rem;
            }

            .pagination-button {
                margin: 0 2px;
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

            /* Fix for table header alignment on small screens */
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
        <c:set var="activeMenu" value="users" scope="request" />
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
                        <h2 class="m-0 d-none d-lg-block">Manage Users</h2>
                            <div class="admin-header-control d-flex justify-content-end">
                                <div class="dropdown me-2">
                                    <a class="btn btn-outline-secondary dropdown-toggle" href="#"
                                       role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                        <i class="fas fa-filter me-1"></i>
                                        <c:choose>
                                            <c:when test="${empty selectedRole}">All Roles</c:when>
                                            <c:otherwise>${selectedRole}</c:otherwise>
                                        </c:choose>
                                    </a>
                                    <ul class="dropdown-menu">
                                        <li><a class="dropdown-item"
                                               href="${pageContext.request.contextPath}/admin/users">All
                                                Roles</a></li>
                                        <li><a class="dropdown-item"
                                               href="${pageContext.request.contextPath}/admin/users?role=admin">Admin</a>
                                        </li>
                                        <li><a class="dropdown-item"
                                               href="${pageContext.request.contextPath}/admin/users?role=customer">Customer</a>
                                        </li>
                                        <li><a class="dropdown-item"
                                               href="${pageContext.request.contextPath}/admin/users?role=instructor">Instructor</a>
                                        </li>
                                    </ul>
                                </div>
                                <a href="${pageContext.request.contextPath}/admin/users/add"
                                       class="btn btn-primary me-2">
                                        <i class="fas fa-plus me-1"></i> Add User
                                    </a>
                                <a href="${pageContext.request.contextPath}/admin/users"
                                   class="btn btn-outline-primary">
                                    <i class="fas fa-sync-alt me-1"></i> Refresh
                                </a>
                            </div>
                    </div>

                    <!-- Alert for success or error messages -->
                    <c:if test="${param.success != null}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="fas fa-check-circle me-2"></i>
                            <c:choose>
                                <c:when test="${param.success eq 'email_sent'}">Email was sent successfully.
                                </c:when>
                                <c:otherwise>Operation completed successfully.</c:otherwise>
                            </c:choose>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"
                                    aria-label="Close"></button>
                        </div>
                    </c:if>
                    <c:if test="${param.error != null}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="fas fa-exclamation-circle me-2"></i>
                            <c:choose>
                                <c:when test="${param.error eq 'email_failed'}">Failed to send email.
                                </c:when>
                                <c:otherwise>An error occurred. Please try again!</c:otherwise>
                            </c:choose>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"
                                    aria-label="Close"></button>
                        </div>
                    </c:if>

                    <!-- Users Table -->
                    <div class="card shadow-sm">
                        <div class="card-body">
                            <div class="table-responsive">
                                <c:choose>
                                    <c:when test="${empty users}">
                                        <div class="text-center py-5">
                                            <div class="display-6 text-muted mb-3">
                                                <i class="fas fa-users"></i>
                                            </div>
                                            <h5 class="fw-normal">No users found</h5>
                                            <p class="text-muted">
                                                <c:choose>
                                                    <c:when test="${not empty selectedRole}">
                                                        There are no users with role: ${selectedRole}
                                                    </c:when>
                                                    <c:otherwise>
                                                        No users have been created yet
                                                    </c:otherwise>
                                                </c:choose>
                                            </p>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="table-container">
                                            <div class="table-controls">
                                                <div class="search-container">
                                                    <i class="fas fa-search"></i>
                                                    <input type="text" id="searchInput" class="search-input"
                                                           placeholder="Search users...">
                                                </div>
                                            </div>

                                            <table id="usersTable" class="table table-hover align-middle">
                                                <thead>
                                                    <tr>
                                                        <th>
                                                            <span>ID</span>
                                                            <button class="sort-button" data-sort="id">
                                                                <span class="sort-icon"></span>
                                                            </button>
                                                        </th>
                                                        <th>
                                                            <span>Username</span>
                                                            <button class="sort-button"
                                                                    data-sort="username">
                                                                <span class="sort-icon"></span>
                                                            </button>
                                                        </th>
                                                        <th>
                                                            <span>Full Name</span>
                                                            <button class="sort-button"
                                                                    data-sort="fullName">
                                                                <span class="sort-icon"></span>
                                                            </button>
                                                        </th>
                                                        <th>
                                                            <span>Email</span>
                                                            <button class="sort-button" data-sort="email">
                                                                <span class="sort-icon"></span>
                                                            </button>
                                                        </th>
                                                        <th>
                                                            <span>Role</span>
                                                            <button class="sort-button" data-sort="role">
                                                                <span class="sort-icon"></span>
                                                            </button>
                                                        </th>
                                                        <th>
                                                            <span>Status</span>
                                                            <button class="sort-button" data-sort="status">
                                                                <span class="sort-icon"></span>
                                                            </button>
                                                        </th>
                                                        <th>Actions</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach items="${users}" var="user">
                                                        <tr class="user-row">
                                                            <td class="user-id">#${user.userID}</td>
                                                            <td class="user-username">${user.username}</td>
                                                            <td class="user-fullName">
                                                                <c:choose>
                                                                    <c:when
                                                                        test="${not empty user.fullName}">
                                                                        ${user.fullName}
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span
                                                                            class="text-muted fst-italic">Not
                                                                            set</span>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                            </td>
                                                            <td class="user-email">${user.email}</td>
                                                            <td class="user-role">
                                                                <c:choose>
                                                                    <c:when test="${user.role eq 'admin'}">
                                                                        <span
                                                                            class="badge bg-danger">Admin</span>
                                                                    </c:when>
                                                                    <c:when
                                                                        test="${user.role eq 'instructor'}">
                                                                        <span
                                                                            class="badge bg-primary">Instructor</span>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span
                                                                            class="badge bg-secondary">Customer</span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </td>
                                                            <td class="user-status">
                                                                <c:choose>
                                                                    <c:when test="${user.active}">
                                                                        <span
                                                                            class="badge bg-success">Active</span>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span
                                                                            class="badge bg-danger">Inactive</span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </td>
                                                            <td>
                                                                <div class="btn-group">
                                                                    <a href="${pageContext.request.contextPath}/admin/users/details/${user.userID}"
                                                                       class="btn btn-sm btn-primary action-btn">
                                                                        <i class="fas fa-eye"></i>
                                                                    </a>
                                                                    <a href="${pageContext.request.contextPath}/admin/users/edit/${user.userID}"
                                                                       class="btn btn-sm btn-warning action-btn ms-1">
                                                                        <i class="fas fa-edit"></i>
                                                                    </a>
                                                                       <button type="button"
                                                                            class="btn btn-sm btn-danger action-btn ms-1"
                                                                            data-bs-toggle="modal"
                                                                            data-bs-target="#deleteModal${user.userID}">
                                                                        <i class="fas fa-trash"></i>
                                                                    </button>
                                                                    <form
                                                                        action="${pageContext.request.contextPath}/admin/users"
                                                                        method="post" class="d-inline ms-1">
                                                                        <input type="hidden" name="action"
                                                                               value="toggleStatus">
                                                                        <input type="hidden" name="userId"
                                                                               value="${user.userID}">
                                                                        <button type="submit"
                                                                                class="btn btn-sm ${user.active ? 'btn-outline-danger' : 'btn-outline-success'} action-btn">
                                                                            <i
                                                                                class="fas ${user.active ? 'fa-ban' : 'fa-check'}"></i>
                                                                        </button>
                                                                    </form>
                                                                    <button type="button"
                                                                            class="btn btn-sm btn-info action-btn ms-1"
                                                                            onclick="prepareEmail('${user.email}', '${user.fullName != null ? user.fullName : user.username}')"
                                                                            data-bs-toggle="modal"
                                                                            data-bs-target="#emailModal">
                                                                        <i class="fas fa-envelope"></i>
                                                                    </button>
                                                                </div>

                                                                <!-- Delete Modal -->
                                                                <div class="modal fade"
                                                                     id="deleteModal${user.userID}"
                                                                     tabindex="-1">
                                                                    <div class="modal-dialog">
                                                                        <div class="modal-content">
                                                                            <div class="modal-header">
                                                                                <h5 class="modal-title">
                                                                                    Confirm
                                                                                    Deletion</h5>
                                                                                <button type="button"
                                                                                        class="btn-close"
                                                                                        data-bs-dismiss="modal"></button>
                                                                            </div>
                                                                            <div class="modal-body">
                                                                                <p>Are you sure you want to
                                                                                    delete
                                                                                    the user
                                                                                    <strong>${user.username}</strong>?
                                                                                </p>
                                                                                <p class="text-danger">This
                                                                                    action
                                                                                    cannot be undone.</p>
                                                                            </div>
                                                                            <div class="modal-footer">
                                                                                <button type="button"
                                                                                        class="btn btn-secondary"
                                                                                        data-bs-dismiss="modal">Cancel</button>
                                                                                <form
                                                                                    action="${pageContext.request.contextPath}/admin/users"
                                                                                    method="post">
                                                                                    <input type="hidden"
                                                                                           name="action"
                                                                                           value="deleteUser">
                                                                                    <input type="hidden"
                                                                                           name="userId"
                                                                                           value="${user.userID}">
                                                                                    <button type="submit"
                                                                                            class="btn btn-danger">Delete</button>
                                                                                </form>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </tbody>
                                            </table>

                                            <div class="pagination-container">
                                                <div class="d-flex pagination-controls">
                                                    <select id="entriesPerPage"
                                                            class="form-select entries-selector">
                                                        <option value="10">10</option>
                                                        <option value="25">25</option>
                                                        <option value="50">50</option>
                                                        <option value="100">100</option>
                                                    </select>
                                                    <div id="pagination" class="btn-group"></div>
                                                </div>
                                            </div>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
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
                    <div class="modal-header">
                        <h5 class="modal-title" id="emailModalLabel">Send Email</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"
                                aria-label="Close"></button>
                    </div>
                    <form action="${pageContext.request.contextPath}/admin/users" method="post"
                          id="emailForm">
                        <div class="modal-body">
                            <input type="hidden" id="recipient" name="recipient">
                            <input type="hidden" name="action" value="sendEmail">
                            <div class="form-group">
                                <label for="subject">Subject:</label>
                                <input type="text" class="form-control" id="subject" name="subject"
                                       required>
                            </div>
                            <div class="form-group">
                                <label for="message">Message:</label>
                                <textarea class="form-control" id="message" name="message" rows="6"
                                          required></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary"
                                    data-bs-dismiss="modal">Cancel</button>
                            <button type="submit" class="btn btn-primary">Send Email</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <jsp:include page="../common/scripts.jsp" />

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                // Table variables
                const table = document.getElementById('usersTable');
                const tableBody = table.querySelector('tbody');
                const rows = Array.from(tableBody.querySelectorAll('tr'));
                const searchInput = document.getElementById('searchInput');
                const entriesPerPageSelect = document.getElementById('entriesPerPage');
                const paginationContainer = document.getElementById('pagination');
                const sortButton = document.querySelectorAll('.sort-button');

                // Pagination variables
                let currentPage = 1;
                let rowsPerPage = parseInt(entriesPerPageSelect.value);
                let filteredRows = [...rows];
                let sortColumn = 'id';
                let sortDirection = 'asc';

                // Initialize the table
                updateTable();

                // Event Listeners
                searchInput.addEventListener('input', function () {
                    currentPage = 1;
                    filterTable();
                    updateTable();
                });

                entriesPerPageSelect.addEventListener('change', function () {
                    rowsPerPage = parseInt(this.value);
                    currentPage = 1;
                    updateTable();
                });

                // Add click event to sort buttons
                sortButton.forEach(button => {
                    button.addEventListener('click', function () {
                        const column = this.getAttribute('data-sort');
                        if (sortColumn === column) {
                            sortDirection = sortDirection === 'asc' ? 'desc' : 'asc';
                        } else {
                            sortColumn = column;
                            sortDirection = 'asc';
                        }
                        sortTable();
                        updateTable();

                        // Update sort icons for all buttons
                        sortButton.forEach(btn => {
                            const sortIcon = btn.querySelector('.sort-icon');
                            sortIcon.classList.remove('asc', 'desc');
                        });

                        // Update current sort button icon
                        const sortIcon = this.querySelector('.sort-icon');
                        sortIcon.classList.add(sortDirection);
                    });
                });

                // Filter table function
                function filterTable() {
                    const searchTerm = searchInput.value.toLowerCase();

                    filteredRows = rows.filter(row => {
                        const id = row.querySelector('.user-id').textContent.toLowerCase();
                        const username = row.querySelector('.user-username').textContent.toLowerCase();
                        const fullName = row.querySelector('.user-fullName').textContent.toLowerCase();
                        const email = row.querySelector('.user-email').textContent.toLowerCase();
                        const role = row.querySelector('.user-role').textContent.toLowerCase();
                        const status = row.querySelector('.user-status').textContent.toLowerCase();

                        return id.includes(searchTerm) ||
                                username.includes(searchTerm) ||
                                fullName.includes(searchTerm) ||
                                email.includes(searchTerm) ||
                                role.includes(searchTerm) ||
                                status.includes(searchTerm);
                    });

                    sortTable(); // Maintain current sort after filtering
                }

                // Sort table function
                function sortTable() {
                    filteredRows.sort((a, b) => {
                        let valueA, valueB;

                        switch (sortColumn) {
                            case 'id':
                                valueA = parseInt(a.querySelector('.user-id').textContent.replace('#', ''));
                                valueB = parseInt(b.querySelector('.user-id').textContent.replace('#', ''));
                                break;
                            case 'username':
                                valueA = a.querySelector('.user-username').textContent.toLowerCase();
                                valueB = b.querySelector('.user-username').textContent.toLowerCase();
                                break;
                            case 'fullName':
                                valueA = a.querySelector('.user-fullName').textContent.toLowerCase();
                                valueB = b.querySelector('.user-fullName').textContent.toLowerCase();
                                break;
                            case 'email':
                                valueA = a.querySelector('.user-email').textContent.toLowerCase();
                                valueB = b.querySelector('.user-email').textContent.toLowerCase();
                                break;
                            case 'role':
                                valueA = a.querySelector('.user-role').textContent.toLowerCase();
                                valueB = b.querySelector('.user-role').textContent.toLowerCase();
                                break;
                            case 'status':
                                valueA = a.querySelector('.user-status').textContent.toLowerCase();
                                valueB = b.querySelector('.user-status').textContent.toLowerCase();
                                break;
                            default:
                                valueA = a.querySelector('.user-id').textContent.replace('#', '');
                                valueB = b.querySelector('.user-id').textContent.replace('#', '');
                        }

                        if (valueA < valueB) {
                            return sortDirection === 'asc' ? -1 : 1;
                        }
                        if (valueA > valueB) {
                            return sortDirection === 'asc' ? 1 : -1;
                        }
                        return 0;
                    });
                }

                // Update table display
                function updateTable() {
                    // Calculate pagination
                    const totalPages = Math.ceil(filteredRows.length / rowsPerPage);
                    const startIndex = (currentPage - 1) * rowsPerPage;
                    const endIndex = Math.min(startIndex + rowsPerPage, filteredRows.length);
                    const visibleRows = filteredRows.slice(startIndex, endIndex);

                    // Update rows display
                    tableBody.innerHTML = '';
                    visibleRows.forEach(row => {
                        tableBody.appendChild(row);
                    });

                    // Generate pagination controls
                    generatePagination(totalPages);
                }

                // Generate pagination buttons
                function generatePagination(totalPages) {
                    paginationContainer.innerHTML = '';

                    if (totalPages <= 0)
                        return;

                    // Previous button
                    const prevButton = document.createElement('button');
                    prevButton.className = 'btn btn-sm btn-outline-secondary pagination-button';
                    prevButton.innerHTML = '<i class="fas fa-angle-left"></i>';
                    prevButton.disabled = currentPage === 1;
                    prevButton.addEventListener('click', () => {
                        if (currentPage > 1) {
                            currentPage--;
                            updateTable();
                        }
                    });
                    paginationContainer.appendChild(prevButton);

                    // Add page number buttons - maximum 3 pages
                    const maxVisiblePages = 3;
                    let startPage = Math.max(1, currentPage - Math.floor(maxVisiblePages / 2));
                    let endPage = Math.min(startPage + maxVisiblePages - 1, totalPages);

                    if (endPage - startPage < maxVisiblePages - 1) {
                        startPage = Math.max(1, endPage - maxVisiblePages + 1);
                    }

                    for (let i = startPage; i <= endPage; i++) {
                        const pageButton = document.createElement('button');
                        pageButton.className = 'btn btn-sm ' +
                                (i === currentPage ? 'btn-primary' : 'btn-outline-secondary') +
                                ' pagination-button';
                        pageButton.innerText = i;
                        pageButton.addEventListener('click', () => {
                            currentPage = i;
                            updateTable();
                        });
                        paginationContainer.appendChild(pageButton);
                    }

                    // Next button
                    const nextButton = document.createElement('button');
                    nextButton.className = 'btn btn-sm btn-outline-secondary pagination-button';
                    nextButton.innerHTML = '<i class="fas fa-angle-right"></i>';
                    nextButton.disabled = currentPage === totalPages || totalPages === 0;
                    nextButton.addEventListener('click', () => {
                        if (currentPage < totalPages) {
                            currentPage++;
                            updateTable();
                        }
                    });
                    paginationContainer.appendChild(nextButton);
                }
            });

            // Function to prepare email modal with recipient details
            function prepareEmail(email, name) {
                document.getElementById('recipient').value = email;
                document.getElementById('subject').value = `Hello \${name}`;
                document.getElementById('message').value = `Dear \${name},\n`;
            }
        </script>
    </body>

</html>