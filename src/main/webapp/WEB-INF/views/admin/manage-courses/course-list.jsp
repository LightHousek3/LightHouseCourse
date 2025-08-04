<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="en">

    <head>

        <title>Course Management</title>
        <jsp:include page="../common/head.jsp" />
        <style>
            .course-img {
                width: 80px;
                height: 60px;
                object-fit: cover;
                border-radius: 5px;
            }
        </style>
    </head>

    <body>

        <!-- Admin Sidebar -->
        <c:set var="activeMenu" value="courses" scope="request" />
        <jsp:include page="/WEB-INF/views/admin/common/sidebar.jsp" />

        <!-- Admin Content -->
        <div class="admin-content">


            <!-- Header -->
            <div class="admin-header d-flex justify-content-between align-items-center">
                <button class="btn d-lg-none" id="toggleSidebarBtn">
                    <i class="fas fa-bars"></i>
                </button>

                <h2 class="mb-0 d-none d-lg-block">
                    <c:choose>
                        <c:when test="${isPendingView}">Course Approval Requests</c:when>
                        <c:otherwise>Manage Course</c:otherwise>
                    </c:choose>
                </h2>
                <div class="d-flex align-items-center">
                    <c:if test="${not isPendingView}">
                        <a href="${pageContext.request.contextPath}/admin/course/pending"
                           class="btn btn-lg btn-warning me-2">
                            <i class="fas fa-clock me-2"></i>View All Pending
                        </a>
                    </c:if>
                    <c:if test="${isPendingView}">
                        <a href="${pageContext.request.contextPath}/admin/courses" class="btn btn-lg btn-primary">
                            <i class="fas fa-arrow-left me-2"></i> Back
                        </a>
                    </c:if>
                </div>
            </div>

            <!-- Success/Error Messages -->
            <c:if test="${not empty sessionScope.message}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    ${sessionScope.message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
                <c:remove var="message" scope="session"/>
            </c:if>
            <c:if test="${not empty sessionScope.error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    ${sessionScope.error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
                <c:remove var="error" scope="session"/>
            </c:if>

            <c:if test="${not isPendingView}">
                <!-- Search Section -->
                <div class="card mb-3">
                    <div class="card-body">
                        <form action="${pageContext.request.contextPath}/admin/courses/search" method="get" class="row g-3">
                            <div class="col-md-6">
                                <label for="searchKeyword" class="form-label">Search Courses</label>
                                <input type="text" class="form-control" id="searchKeyword" name="keyword"
                                       value="${param.keyword}" placeholder="Course name or instructor">
                            </div>
                            <div class="col-md-2 d-flex align-items-end">
                                <button type="submit" class="btn btn-md btn-outline-primary w-100">Search</button>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Filter & Sort Section -->
                <div class="card mb-4">
                    <div class="card-body">
                        <form action="${pageContext.request.contextPath}/admin/courses/filter" method="get" class="row g-3">
                            <!-- Preserve keyword from search when filtering -->
                            <input type="hidden" id="filterKeyword" name="keyword" value="${param.keyword}"/>
                            <div class="col-md-3">
                                <label for="categoryFilter" class="form-label">Category</label>
                                <select class="form-select" id="categoryFilter" name="category">
                                    <option value="">All Categories</option>
                                    <c:forEach var="category" items="${categories}">
                                        <option value="${category.categoryID}"
                                                ${param.category eq category.categoryID ? 'selected' : '' }>
                                            ${category.name}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="col-md-3">
                                <label for="statusFilter" class="form-label">Status</label>
                                <select class="form-select" id="statusFilter" name="status">
                                    <option value="">All</option>
                                    <option value="pending" ${param.status eq 'pending' || isPendingView ? 'selected' : '' }>Pending</option>
                                    <option value="approved" ${param.status eq 'approved' ? 'selected' : '' }>Approved</option>
                                    <option value="rejected" ${param.status eq 'rejected' ? 'selected' : '' }>Rejected</option>
                                    <option value="banned" ${param.status eq 'banned' ? 'selected' : '' }>Banned</option>
                                </select>
                            </div>

                            <div class="col-md-3">
                                <label for="sortBy" class="form-label">Sort By</label>
                                <select class="form-select" id="sortBy" name="sort">
                                    <option value="id" ${param.sort eq 'id' ? 'selected' : '' }>ID</option>
                                    <option value="name" ${param.sort eq 'name' ? 'selected' : '' }>Name</option>
                                    <option value="price" ${param.sort eq 'price' ? 'selected' : '' }>Price</option>
                                    <option value="date" ${param.sort eq 'date' ? 'selected' : '' }>Date Added</option>
                                </select>
                            </div>

                            <div class="col-md-2 d-flex align-items-end">
                                <button type="submit" class="btn btn-md btn-outline-success w-100">Apply Filter</button>
                            </div>
                        </form>
                    </div>
                </div>
            </c:if>


            <!-- Course List -->
            <div class="card">
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Image</th>
                                    <th>Course Name</th>
                                    <th>Instructor</th>
                                    <th>Price</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="course" items="${courses}">
                                    <tr>
                                        <td>${course.courseID}</td>
                                        <td>
                                            <img src="${pageContext.request.contextPath}/${course.imageUrl}"
                                                 alt="${course.name}" class="course-img">
                                        </td>
                                        <td>${course.name}</td>
                                        <td style="max-width: 200px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"
                                            data-bs-toggle="tooltip">
                                            <c:forEach var="i" items="${course.instructors}" varStatus="status">
                                                ${i.fullName}<c:if test="${!status.last}">, </c:if>
                                            </c:forEach>
                                        </td>
                                        <td>
                                            <fmt:formatNumber value="${course.price}" type="number" groupingUsed="true" />đ
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${course.approvalStatus eq 'banned'}">
                                                    <span class="status-badge status-banned">Banned</span>
                                                </c:when>
                                                <c:when test="${course.approvalStatus eq 'pending'}">
                                                    <span class="status-badge status-pending">Pending</span>
                                                </c:when>
                                                <c:when test="${course.approvalStatus eq 'approved'}">
                                                    <span class="status-badge status-approved">Approved</span>
                                                </c:when>
                                                <c:when test="${course.approvalStatus eq 'rejected'}">
                                                    <span class="status-badge status-rejected">Rejected</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="status-badge status-cus">Unknown</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <div class="btn-group">
                                                <a href="${pageContext.request.contextPath}/admin/course/view/${course.courseID}"
                                                   class="btn btn-sm btn-outline-primary">
                                                    <i class="fas fa-eye"></i>
                                                </a>

                                                <c:if test="${course.approvalStatus eq 'pending'}">
                                                    <a href="${pageContext.request.contextPath}/admin/course/approve/${course.courseID}"
                                                       class="btn btn-sm btn-outline-success"
                                                       onclick="return confirm('Are you sure you want to approve this course?')">
                                                        <i class="fas fa-check"></i>
                                                    </a>
                                                    <a href="${pageContext.request.contextPath}/admin/course/reject/${course.courseID}"
                                                       class="btn btn-sm btn-outline-danger">
                                                        <i class="fas fa-times"></i>
                                                    </a>
                                                </c:if>
                                                <c:if test="${course.approvalStatus eq 'approved'}">
                                                    <a href="${pageContext.request.contextPath}/admin/course/ban/${course.courseID}"
                                                       class="btn btn-sm btn-outline-warning"
                                                       onclick="return confirm('Are you sure you want to ban this course?')">
                                                        <i class="fas fa-ban"></i>
                                                    </a>
                                                </c:if>
                                                <c:if test="${course.approvalStatus eq 'banned'}">
                                                    <a href="${pageContext.request.contextPath}/admin/course/unban/${course.courseID}"
                                                       class="btn btn-sm btn-outline-primary"
                                                       onclick="return confirm('Unban this course and make it available again?')">
                                                        <i class="fas fa-undo"></i>
                                                    </a>
                                                </c:if>

                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>

                                <c:if test="${empty courses}">
                                    <tr>
                                        <td colspan="9" class="text-center py-5">
                                            <i class="fas fa-exclamation-circle text-muted fa-2x mb-3"></i>
                                            <p class="mb-0">
                                                <c:choose>
                                                    <c:when test="${isPendingView}">
                                                        No pending course approval requests found.
                                                    </c:when>
                                                    <c:when test="${searchView}">
                                                        No courses found. Please adjust your keyword search.
                                                    </c:when>
                                                    <c:otherwise>
                                                        No courses found. Please adjust your filters.
                                                    </c:otherwise>
                                                </c:choose>
                                            </p>
                                        </td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>

                    <c:if test="${totalPages > 1}">
                        <nav>
                            <ul class="pagination justify-content-center">
                                <c:if test="${currentPage > 1}">
                                    <li class="page-item">
                                        <c:choose>
                                            <c:when test="${isPendingView}">
                                                <a class="page-link btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/course/pending?page=${currentPage - 1}">&laquo;</a>
                                            </c:when>
                                            <c:when test="${searchView}">
                                                <a class="page-link btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/courses/search?keyword=${keyword}&page=${currentPage - 1}">&laquo;</a>
                                            </c:when>
                                            <c:when test="${filterView}">
                                                <a class="page-link btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/courses/filter?keyword=${param.keyword}&category=${param.category}&status=${param.status}&sort=${param.sort}&page=${currentPage - 1}">&laquo;</a>
                                            </c:when>
                                            <c:otherwise>
                                                <a class="page-link btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/courses?page=${currentPage - 1}">&laquo;</a>
                                            </c:otherwise>
                                        </c:choose>
                                    </li>
                                </c:if>

                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <li class="page-item ${i == currentPage ? 'active' : ''}">
                                        <c:choose>
                                            <c:when test="${isPendingView}">
                                                <a class="page-link btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/course/pending?page=${i}">${i}</a>
                                            </c:when>
                                            <c:when test="${searchView}">
                                                <a class="page-link btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/courses/search?keyword=${keyword}&page=${i}">${i}</a>
                                            </c:when>
                                            <c:when test="${filterView}">
                                                <a class="page-link btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/courses/filter?keyword=${param.keyword}&category=${param.category}&status=${param.status}&sort=${param.sort}&page=${i}">${i}</a>
                                            </c:when>
                                            <c:otherwise>
                                                <a class="page-link btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/courses?page=${i}">${i}</a>
                                            </c:otherwise>
                                        </c:choose>
                                    </li>
                                </c:forEach>

                                <c:if test="${currentPage < totalPages}">
                                    <li class="page-item">
                                        <c:choose>
                                            <c:when test="${isPendingView}">
                                                <a class="page-link btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/course/pending?page=${currentPage + 1}">&raquo;</a>
                                            </c:when>
                                            <c:when test="${searchView}">
                                                <a class="page-link btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/courses/search?keyword=${keyword}&page=${currentPage + 1}">&raquo;</a>
                                            </c:when>
                                            <c:when test="${filterView}">
                                                <a class="page-link btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/courses/filter?keyword=${param.keyword}&category=${param.category}&status=${param.status}&sort=${param.sort}&page=${currentPage + 1}">&raquo;</a>
                                            </c:when>
                                            <c:otherwise>
                                                <a class="page-link btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/courses?page=${currentPage + 1}">&raquo;</a>
                                            </c:otherwise>
                                        </c:choose>
                                    </li>
                                </c:if>
                            </ul>
                        </nav>
                    </c:if>
                </div>
            </div>
        </div>
        <!-- Bootstrap Bundle with Popper -->
        <jsp:include page="../common/scripts.jsp" />

        <script>
            {
                const searchInput = document.getElementById('searchKeyword');
                const hiddenFilterInput = document.getElementById('filterKeyword');

                if (searchInput && hiddenFilterInput) {
                    searchInput.addEventListener('input', () => {
                        hiddenFilterInput.value = searchInput.value;
                    });
                }
            }

            // Auto dismiss alerts after 5 seconds
            setTimeout(function () {
                document.querySelectorAll('.alert').forEach(function (alert) {
                    const bsAlert = new bootstrap.Alert(alert);
                    bsAlert.close();
                });
            }, 5000);
        </script>

    </body>

</html>