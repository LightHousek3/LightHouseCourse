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
                <span class="me-3">Welcome, ${instructor.name}!</span>
                <div class="dropdown">
                    <button class="btn btn-lg btn-outline-secondary dropdown-toggle gap-1" 
                            type="button" id="userDropdown" data-bs-toggle="dropdown"
                            aria-expanded="false">
                        <img src="${pageContext.request.contextPath}${avatar}" 
                             style="width: 30px; height: 30px; border-radius: 50%" alt="Avatar"/> ${instructor.name}
                    </button>
                </div>
            </div>
        </div>

        <!-- Main Content -->
        <div class="card">
            <div class="card-header bg-white d-flex justify-content-between align-items-center">
                <h5 class="mb-0">Students Enrolled in Your Courses</h5>
                <div class="d-flex">
                    <button class="btn btn-sm btn-outline-primary me-2" data-bs-toggle="collapse" data-bs-target="#filterSection">
                        <i class="fas fa-filter me-1"></i> Filters
                    </button>
                </div>
            </div>
            
            <div class="collapse filter-section" id="filterSection">
                <div class="container">
                    <form action="${pageContext.request.contextPath}/instructor/students" method="get" id="filterForm">
                        <div class="row">
                            <div class="col-md-4 mb-3">
                                <label for="search" class="form-label">Search</label>
                                <input type="text" class="form-control" id="search" name="search" 
                                       placeholder="Name or Email" value="${searchTerm}">
                            </div>
                            <div class="col-md-4 mb-3">
                                <label for="course" class="form-label">Course</label>
                                <select class="form-select" id="course" name="course">
                                    <option value="">All Courses</option>
                                    <c:forEach items="${courses}" var="course">
                                        <option value="${course.courseID}" ${courseFilter eq course.courseID ? 'selected' : ''}>
                                            ${course.name}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-4 mb-3">
                                <label for="progress" class="form-label">Progress</label>
                                <select class="form-select" id="progress" name="progress">
                                    <option value="">All Progress</option>
                                    <option value="completed" ${progressFilter eq 'completed' ? 'selected' : ''}>Completed</option>
                                    <option value="in-progress" ${progressFilter eq 'in-progress' ? 'selected' : ''}>In Progress</option>
                                    <option value="not-started" ${progressFilter eq 'not-started' ? 'selected' : ''}>Not Started</option>
                                </select>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-12">
                                <button type="submit" class="btn btn-primary">Apply Filters</button>
                                <button type="button" class="btn btn-secondary ms-2" id="clearFilters">Clear Filters</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
            
            <div class="card-body">
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
                            <tbody>
                                <c:forEach items="${students}" var="data">
                                    <tr>
                                        <td>
                                            <div class="d-flex align-items-center">
                                                <img src="${pageContext.request.contextPath}${data.student.avatar}" 
                                                     alt="Avatar" class="student-avatar me-2">
                                                <span>${data.student.fullName}</span>
                                            </div>
                                        </td>
                                        <td>${data.student.email}</td>
                                        <td>${data.course.name}</td>
                                        <td>
                                            <div class="d-flex align-items-center">
                                                <div class="progress flex-grow-1 me-2">
                                                    <div class="progress-bar ${data.progress.completionPercentage.intValue() == 100 ? 'bg-success' : 'bg-primary'}" 
                                                         role="progressbar" 
                                                         style="width: ${data.progress.completionPercentage}%" 
                                                         aria-valuenow="${data.progress.completionPercentage}" 
                                                         aria-valuemin="0" 
                                                         aria-valuemax="100"></div>
                                                </div>
                                                <span>${data.progress.completionPercentage}%</span>
                                            </div>
                                        </td>
                                        <td>
                                            <fmt:formatDate value="${data.progress.lastAccessDate}" pattern="dd/MM/yyyy HH:mm" />
                                        </td>
                                        <td>
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
                    <nav aria-label="Page navigation">
                        <ul class="pagination justify-content-center">
                            <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/instructor/students?page=${currentPage - 1}&size=${pageSize}&search=${searchTerm}&course=${courseFilter}&progress=${progressFilter}">
                                    <i class="fas fa-chevron-left"></i>
                                </a>
                            </li>
                            
                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <c:choose>
                                    <c:when test="${i == currentPage}">
                                        <li class="page-item active">
                                            <span class="page-link">${i}</span>
                                        </li>
                                    </c:when>
                                    <c:otherwise>
                                        <li class="page-item">
                                            <a class="page-link" href="${pageContext.request.contextPath}/instructor/students?page=${i}&size=${pageSize}&search=${searchTerm}&course=${courseFilter}&progress=${progressFilter}">
                                                ${i}
                                            </a>
                                        </li>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                            
                            <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/instructor/students?page=${currentPage + 1}&size=${pageSize}&search=${searchTerm}&course=${courseFilter}&progress=${progressFilter}">
                                    <i class="fas fa-chevron-right"></i>
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
                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                    <button type="submit" class="btn btn-primary">Send Email</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
                
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
            </div>
        </div>
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
        document.getElementById('clearFilters').addEventListener('click', function() {
            document.getElementById('search').value = '';
            document.getElementById('course').value = '';
            document.getElementById('progress').value = '';
            document.getElementById('filterForm').submit();
        });
        
        // Auto-hide alerts after 5 seconds
        setTimeout(function() {
            const alerts = document.querySelectorAll('.alert-dismissible');
            alerts.forEach(alert => {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            });
        }, 5000);
    </script>
</body>
</html> 