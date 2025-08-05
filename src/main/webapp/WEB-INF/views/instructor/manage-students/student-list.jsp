<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="vi_VN" />

<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Manage Students - LightHouse</title>
        <jsp:include page="../common/head.jsp" />
        <style>
            .progress {
                width: 30px;
                height: 12px;
            }
            .table-responsive {
                overflow-x: auto;
            }
            .filter-section {
                background-color: #f8f9fa;
                padding: 15px;
                border-radius: 8px;
                margin-bottom: 20px;
            }
            .student-avatar {
                width: 40px;
                height: 40px;
                border-radius: 50%;
                object-fit: cover;
            }
            .action-btn {
                margin: 0 2px;
            }
            .badge {
                font-size: 85%;
            }
            .search-box {
                max-width: 300px;
            }
            .btn-pink {
                background-color: #ec4899;
                border: none;
            }

            .btn-pink:hover {
                background-color: #db2777;
            }
        </style>
    </head>
    <body>
        <%-- Instructor Sidebar --%>
        <c:set var="activeMenu" value="students" scope="request" />
        <jsp:include page="../common/sidebar.jsp" />

        <%-- Instructor Content --%>
        <div class="instructor-content">
            <!-- Header -->
            <div class="instructor-header d-flex justify-content-between align-items-center">
                <button class="btn d-lg-none" id="toggleSidebarBtn">
                    <i class="fas fa-bars"></i>
                </button>
                <h2 class="m-0 d-none d-lg-block">Manage Students</h2>
                <div class="d-flex align-items-center">
                    <div>
                        <a href="${pageContext.request.contextPath}/instructor/students"
                           class="btn btn-lg btn-primary">
                            <i class="fas fa-sync-alt me-2"></i> Reset Filters
                        </a>
                    </div>
                </div>
            </div>
            <!-- Main Content -->
            <!-- Success/Error Alerts -->
            <c:if test="${not empty success}">
                <div class="alert alert-success alert-dismissible fade show mt-3" role="alert">
                    ${success}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show mt-3" role="alert">
                    ${error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>
            <!-- Filter Section -->
            <div class="card mb-4">
                <div class="m-3 mb-0 bg-white d-flex justify-content-between align-items-center">
                    <h5 class="mb-0">Students Enrolled in Your Courses</h5>
                </div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/instructor/students" method="get" id="filterForm">
                        <div class="row g-3 align-items-end">
                            <!-- Search -->
                            <div class="col-md-3">
                                <label for="search" class="form-label">Search</label>
                                <input type="text" class="form-control " id="search" name="search"
                                       placeholder="Name or Email" value="${searchTerm}">
                            </div>

                            <!-- Course Filter -->
                            <div class="col-md-3">
                                <label for="course" class="form-label">Filter by Course</label>
                                <select class="form-select" id="course" name="course">
                                    <option value="">All Courses</option>
                                    <c:forEach items="${courses}" var="course">
                                        <option value="${course.courseID}"
                                                ${courseFilter eq course.courseID ? 'selected' : ''}>
                                            ${course.name}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>

                            <!-- Progress Filter -->
                            <div class="col-md-3">
                                <label for="progress" class="form-label">Filter by Progress</label>
                                <select class="form-select" id="progress" name="progress">
                                    <option value="">All Progress</option>
                                    <option value="completed" ${progressFilter eq 'completed' ? 'selected' : ''}>Completed</option>
                                    <option value="in-progress" ${progressFilter eq 'in-progress' ? 'selected' : ''}>In Progress</option>
                                    <option value="not-started" ${progressFilter eq 'not-started' ? 'selected' : ''}>Not Started</option>
                                </select>
                            </div>

                            <!-- Action Buttons -->
                            <div class="col-md-3 d-flex gap-2">
                                <button type="submit" class="btn btn-primary w-auto">Apply Filters</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>


            <div class="card-body bg-white rounded-4 shadow">
                <c:if test="${empty students}">
                    <div class="alert alert-info">No students found matching your criteria.</div>
                </c:if>

                <c:if test="${not empty students}">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Student</th>
                                    <th>Email</th>
                                    <th>Course</th>
                                    <th>Progress</th>
                                    <th>Last Access</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody >
                                <c:forEach items="${students}" var="data">
                                    <tr>
                                        <td class="align-middle                                 ">
                                            <div class="d-flex align-items-center">
                                                <img src="${pageContext.request.contextPath}${data.student.avatar}" 
                                                     alt="Avatar" class="student-avatar me-2">
                                                <span>${data.student.fullName}</span>
                                            </div>
                                        </td>
                                        <td class="align-middle">${data.student.email}</td>
                                        <td class="align-middle">${data.course.name}</td>
                                        <td class="align-middle">
                                            <div class="d-flex align-items-center">
                                                <div class="progress flex-grow-1 me-3">
                                                    <div class="progress-bar ${data.progress.completionPercentage.intValue() == 100 ? 'bg-success' : 'bg-primary'}" 
                                                         role="progressbar" 
                                                         style="width: ${data.progress.completionPercentage}%" 
                                                         aria-valuenow="${data.progress.completionPercentage}" 
                                                         aria-valuemin="0" 
                                                         aria-valuemax="100"></div>
                                                </div>
                                                <span class="me-4">${data.progress.completionPercentage}%</span>
                                            </div>
                                        </td>
                                        <td class="align-middle">
                                            <fmt:formatDate value="${data.progress.lastAccessDate}" pattern="dd/MM/yyyy HH:mm" />
                                        </td>
                                        <td class="align-middle">
                                            <div class="btn-group">
                                                <a href="${pageContext.request.contextPath}/instructor/students/progress?studentId=${data.student.customerID}&courseId=${data.course.courseID}" 
                                                   class="btn btn-sm btn-primary action-btn" title="View Progress">
                                                    <i class="fas fa-chart-line"></i>
                                                </a>
                                                <button class="btn btn-sm btn-info action-btn" 
                                                        data-bs-toggle="modal" 
                                                        data-bs-target="#emailModal" 
                                                        data-student-id="${data.student.customerID}" 
                                                        data-student-name="${data.student.fullName}"
                                                        data-student-email="${data.student.email}"
                                                        title="Send Email">
                                                    <i class="fas fa-envelope"></i>
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:if>

                <!-- Pagination -->
                <c:if test="${totalPages > 1}">
                    <nav aria-label="Page navigation" class="mt-4">
                        <ul class="pagination">
                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                <a class="page-link btn btn-outline-primary" href="${pageContext.request.contextPath}/instructor/students?page=${currentPage - 1}${resolved != null ? '&resolved=' += resolved : ''}${courseId != null ? '&courseId=' += courseId : ''}${lessonId != null ? '&lessonId=' += lessonId : ''}">
                                    &laquo;
                                </a>
                            </li>
                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                    <a class="page-link btn btn-outline-primary" href="${pageContext.request.contextPath}/instructor/students?page=${i}${resolved != null ? '&resolved=' += resolved : ''}${courseId != null ? '&courseId=' += courseId : ''}${lessonId != null ? '&lessonId=' += lessonId : ''}">
                                        ${i}
                                    </a>
                                </li>
                            </c:forEach>
                            <li
                                class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                <a class="page-link btn btn-outline-primary" href="${pageContext.request.contextPath}/instructor/students?page=${currentPage + 1}${resolved != null ? '&resolved=' += resolved : ''}${courseId != null ? '&courseId=' += courseId : ''}${lessonId != null ? '&lessonId=' += lessonId : ''}">
                                    &raquo;
                                </a>
                            </li>
                        </ul>
                    </nav>
                </c:if>

                <!-- Email Modal -->
                <div class="modal fade" id="emailModal" tabindex="-1" aria-labelledby="emailModalLabel" aria-hidden="true">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title" id="emailModalLabel">Send Email to Student</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <form action="${pageContext.request.contextPath}/instructor/students" method="post">
                                <div class="modal-body">
                                    <input type="hidden" name="action" value="send-email">
                                    <input type="hidden" name="studentId" id="modalStudentId">

                                    <div class="mb-3">
                                        <label for="studentName" class="form-label">Student Name</label>
                                        <input type="text" class="form-control" id="modalStudentName" readonly>
                                    </div>

                                    <div class="mb-3">
                                        <label for="studentEmail" class="form-label">Student Email</label>
                                        <input type="email" class="form-control" id="modalStudentEmail" readonly>
                                    </div>

                                    <div class="mb-3">
                                        <label for="subject" class="form-label">Subject</label>
                                        <input type="text" class="form-control" id="subject" name="subject" required>
                                    </div>

                                    <div class="mb-3">
                                        <label for="message" class="form-label">Message</label>
                                        <textarea class="form-control" id="message" name="message" rows="5" required></textarea>
                                    </div>
                                </div>
                                <div class="modal-footer">
                                    <button type="button" class="btn btn-secondary w-auto" data-bs-dismiss="modal">Cancel</button>
                                    <button type="submit" class="btn btn-primary w-auto">Send Email</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>

            </div>
            <!--</div>-->
        </div>

        <jsp:include page="../common/scripts.jsp" />

        <script>
            // Handle email modal
            const emailModal = document.getElementById('emailModal');
            if (emailModal) {
                emailModal.addEventListener('show.bs.modal', event => {
                    const button = event.relatedTarget;
                    const studentId = button.getAttribute('data-student-id');
                    const studentName = button.getAttribute('data-student-name');
                    const studentEmail = button.getAttribute('data-student-email');

                    document.getElementById('modalStudentId').value = studentId;
                    document.getElementById('modalStudentName').value = studentName;
                    document.getElementById('modalStudentEmail').value = studentEmail;
                });
            }

            // Clear filters
            document.getElementById('clearFilters').addEventListener('click', function () {
                document.getElementById('search').value = '';
                document.getElementById('course').value = '';
                document.getElementById('progress').value = '';
                document.getElementById('filterForm').submit();
            });

            // Auto-hide alerts after 5 seconds
            setTimeout(function () {
                const alerts = document.querySelectorAll('.alert-dismissible');
                alerts.forEach(alert => {
                    const bsAlert = new bootstrap.Alert(alert);
                    bsAlert.close();
                });
            }, 5000);
        </script>
        <script>
            document.getElementById('filterForm').addEventListener('submit', function (e) {
                const searchInput = document.getElementById('search');
                searchInput.value = searchInput.value.trim();
            });

            document.getElementById('clearFilters').addEventListener('click', function () {
                document.getElementById('search').value = '';
                document.getElementById('course').value = '';
                document.getElementById('progress').value = '';
                document.getElementById('filterForm').submit();
            });
        </script>
    </body>
</html> 