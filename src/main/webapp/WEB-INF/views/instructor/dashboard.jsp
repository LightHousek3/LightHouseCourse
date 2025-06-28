<%-- Document : dashboard Created on : Jun 23, 2025, 3:09:00 PM Author : DangPH - CE180896 --%>

    <%@page contentType="text/html" pageEncoding="UTF-8" %>
        <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
            <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
                <fmt:setLocale value="vi_VN" />
                <!DOCTYPE html>
                <html lang="en">

                <head>
                    <title>Instructor Dashboard - LightHouse</title>
                    <jsp:include page="./common/head.jsp" />
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

                        .bg-gradient-primary {
                            background: linear-gradient(135deg, #3a8ffe 0%, #9658fe 100%);
                        }

                        .bg-gradient-success {
                            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
                        }

                        .bg-gradient-warning {
                            background: linear-gradient(135deg, #ffc107 0%, #fd7e14 100%);
                        }

                        .bg-gradient-info {
                            background: linear-gradient(135deg, #17a2b8 0%, #0dcaf0 100%);
                        }

                        .bg-gradient-danger {
                            background: linear-gradient(135deg, #f51c31 0%, #6a5858 100%);
                        }

                        .text-custom {
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            max-height: 77px;
                            height: 77px;
                        }
                    </style>
                </head>

                <body>
                    <%-- Instructor Sidebar --%>
                        <c:set var="activeMenu" value="dashboard" scope="request" />
                        <jsp:include page="./common/sidebar.jsp" />

                        <%-- Instructor Content --%>
                            <div class="instructor-content">
                                <!-- Header -->
                                <div class="instructor-header d-flex justify-content-between align-items-center">
                                    <button class="btn d-lg-none" id="toggleSidebarBtn">
                                        <i class="fas fa-bars"></i>
                                    </button>
                                    <h2 class="m-0 d-none d-lg-block">Dashboard</h2>
                                    <div class="d-flex align-items-center">
                                        <span class="me-3">Welcome, ${instructor.name}!</span>
                                        <div class="dropdown">
                                            <button class="btn btn-lg btn-outline-secondary dropdown-toggle gap-1"
                                                type="button" id="userDropdown" data-bs-toggle="dropdown"
                                                aria-expanded="false">
                                                <img src="${pageContext.request.contextPath}${avatar}" style="width: 30px; height: 30px; border-radius: 50%" alt="Avatar"/> ${instructor.name}
                                            </button>
                                        </div>
                                    </div>
                                </div>

                                <!-- Statistics -->
                                <div class="row mb-4">
                                    <div class="col-xl-3 col-md-6 mb-4">
                                        <div class="card stat-card h-100">
                                            <div class="card-body">
                                                <div class="d-flex align-items-center">
                                                    <div class="stat-icon bg-gradient-primary text-white">
                                                        <i class="fas fa-book"></i>
                                                    </div>
                                                    <div>
                                                        <h6 class="text-muted mb-1">Total Courses</h6>
                                                        <h2 class="mb-0">${totalCourses}</h2>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-xl-3 col-md-6 mb-4">
                                        <div class="card stat-card h-100">
                                            <div class="card-body">
                                                <div class="d-flex align-items-center">
                                                    <div class="stat-icon bg-gradient-info text-white">
                                                        <i class="fa-solid fa-user-graduate"></i>
                                                    </div>
                                                    <div>
                                                        <h6 class="text-muted mb-1">Total Students</h6>
                                                        <h2 class="mb-0">${totalStudents}</h2>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-xl-3 col-md-6 mb-4">
                                        <div class="card stat-card h-100">
                                            <div class="card-body">
                                                <div class="d-flex align-items-center">
                                                    <div class="stat-icon bg-gradient-warning text-white">
                                                        <i class="fas fa-star"></i>
                                                    </div>
                                                    <div>
                                                        <h6 class="text-muted mb-1">Average Rating</h6>
                                                        <h2 class="mb-0">${averageRating}</h2>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-xl-3 col-md-6 mb-4">
                                        <div class="card stat-card h-100">
                                            <div class="card-body">
                                                <div class="d-flex align-items-center">
                                                    <div class="stat-icon bg-gradient-success text-white">
                                                        <i class="fas fa-comments"></i>
                                                    </div>
                                                    <div>
                                                        <h6 class="text-muted mb-1">Open Discussions</h6>
                                                        <h2 class="mb-0">${unresolvedDiscussions}</h2>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <!-- Courses Status -->
                                <div class="row mb-4">
                                    <div class="col-xl-6 mb-4">
                                        <div class="card h-100">
                                            <div class="card-header d-flex justify-content-between align-items-center">
                                                <h5 class="mb-0">Course Status</h5>
                                            </div>
                                            <div class="card-body">
                                                <div class="row g-4">
                                                    <div class="col-md-6">
                                                        <div class="card stat-card h-100 border-0 shadow-sm">
                                                            <div class="card-body">
                                                                <div class="d-flex align-items-center">
                                                                    <div
                                                                        class="stat-icon bg-gradient-warning text-white">
                                                                        <i class="fas fa-clock"></i>
                                                                    </div>
                                                                    <div>
                                                                        <h6 class="text-muted mb-1">Pending
                                                                        </h6>
                                                                        <h2 class="mb-0">${pendingCourses}</h2>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div class="col-md-6">
                                                        <div class="card stat-card h-100 border-0 shadow-sm">
                                                            <div class="card-body">
                                                                <div class="d-flex align-items-center">
                                                                    <div
                                                                        class="stat-icon bg-gradient-success text-white">
                                                                        <i class="fas fa-check-circle"></i>
                                                                    </div>
                                                                    <div>
                                                                        <h6 class="text-muted mb-1">Approved</h6>
                                                                        <h2 class="mb-0">${approvedCourses}</h2>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div class="col-md-6">
                                                        <div class="card stat-card h-100 border-0 shadow-sm">
                                                            <div class="card-body">
                                                                <div class="d-flex align-items-center">
                                                                    <div
                                                                        class="stat-icon bg-gradient-danger text-white">
                                                                        <i class="fas fa-times-circle"></i>
                                                                    </div>
                                                                    <div>
                                                                        <h6 class="text-muted mb-1">Rejected</h6>
                                                                        <h2 class="mb-0">${rejectedCourses}</h2>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div class="col-md-6">
                                                        <div class="card stat-card h-100 border-0 shadow-sm">
                                                            <div class="card-body">
                                                                <div class="d-flex align-items-center">
                                                                    <div class="stat-icon bg-gradient-info text-white">
                                                                        <i class="fas fa-edit"></i>
                                                                    </div>
                                                                    <div>
                                                                        <h6 class="text-muted mb-1">Draft</h6>
                                                                        <h2 class="mb-0">${draftCourses}</h2>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <div class="text-center mt-4">
                                                    <a href="${pageContext.request.contextPath}/instructor/courses/create"
                                                        class="btn btn-success btn-lg">
                                                        <i class="fas fa-plus me-2"></i>Create Course
                                                    </a>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="col-xl-6 mb-4">
                                        <div class="card h-100">
                                            <div class="card-header d-flex justify-content-between align-items-center">
                                                <h5 class="mb-0">Quick Actions</h5>
                                            </div>
                                            <div class="card-body">
                                                <div class="row g-4">
                                                    <div class="col-md-6">
                                                        <div class="card border-0 shadow-sm h-100">
                                                            <div
                                                                class="card-body d-flex flex-column justify-content-center align-items-center text-center">
                                                                <div class="mb-3">
                                                                    <i class="fas fa-book fa-3x text-primary"></i>
                                                                </div>
                                                                <h5>Manage Courses</h5>
                                                                <p class="text-custom text-muted">Create and manage your
                                                                    course
                                                                    content</p>
                                                                <a href="${pageContext.request.contextPath}/instructor/courses"
                                                                    class="btn btn-lg btn-outline-primary">Go to
                                                                    Courses</a>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div class="col-md-6">
                                                        <div class="card border-0 shadow-sm h-100">
                                                            <div
                                                                class="card-body d-flex flex-column justify-content-center align-items-center text-center">
                                                                <div class="mb-3">
                                                                    <i class="fas fa-comments fa-3x text-success"></i>
                                                                </div>
                                                                <h5>Discussions</h5>
                                                                <p class="text-custom text-muted">Answer student
                                                                    questions & manage
                                                                    discussions</p>
                                                                <a href="${pageContext.request.contextPath}/instructor/discussions"
                                                                    class="btn btn-lg btn-outline-warning">View
                                                                    Discussions</a>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <!-- Recent Courses -->
                                <div class="row">
                                    <div class="col-12">
                                        <div class="card">
                                            <div class="card-header d-flex justify-content-between align-items-center">
                                                <h5 class="mb-0">Recent Courses</h5>
                                                <a href="${pageContext.request.contextPath}/instructor/courses"
                                                    class="btn btn-lg btn-primary"><i class="fas fa-book me-2"></i>View
                                                    All Courses</a>
                                            </div>
                                            <div class="card-body">
                                                <div class="table-responsive">
                                                    <table class="table table-hover align-middle">
                                                        <thead>
                                                            <tr>
                                                                <th>ID</th>
                                                                <th>Course Title</th>
                                                                <th>Price</th>
                                                                <th>Approved Date</th>
                                                                <th>Status</th>
                                                                <th>Students</th>
                                                                <th>Action</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody>
                                                            <c:forEach var="course" items="${recentCourses}">
                                                                <tr>
                                                                    <td>#${course.courseID}</td>
                                                                    <td>${course.name}</td>
                                                                    <td>
                                                                        <fmt:formatNumber value="${course.price}"
                                                                            type="number" />đ
                                                                    </td>
                                                                    <td>
                                                                        <c:choose>
                                                                            <c:when
                                                                                test="${course.approvalStatus eq 'approved' && course.approvalDate != null}">
                                                                                <fmt:formatDate
                                                                                    value="${course.approvalDate}"
                                                                                    pattern="yyyy-MM-dd" />
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <span class="text-muted">Not yet
                                                                                    approved</span>
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </td>
                                                                    <td>
                                                                        <c:choose>
                                                                            <c:when
                                                                                test="${course.approvalStatus eq 'approved'}">
                                                                                <span
                                                                                    class="status-badge status-approved">Approved</span>
                                                                            </c:when>
                                                                            <c:when
                                                                                test="${course.approvalStatus eq 'pending'}">
                                                                                <span
                                                                                    class="status-badge status-pending">Pending</span>
                                                                            </c:when>
                                                                            <c:when
                                                                                test="${course.approvalStatus eq 'rejected'}">
                                                                                <span
                                                                                    class="status-badge status-rejected">Rejected</span>
                                                                            </c:when>
                                                                            <c:when
                                                                                test="${course.approvalStatus eq 'draft'}">
                                                                                <span
                                                                                    class="status-badge status-cus">Draft</span>
                                                                            </c:when>
                                                                        </c:choose>
                                                                    </td>
                                                                    <td>${course.enrollmentCount}</td>
                                                                    <td>
                                                                        <a href="${pageContext.request.contextPath}/instructor/courses/edit/${course.courseID}"
                                                                            class="btn btn-sm btn-outline-primary">
                                                                            <i class="fas fa-edit"></i>
                                                                        </a>
                                                                    </td>
                                                                </tr>
                                                            </c:forEach>

                                                            <c:if test="${empty recentCourses}">
                                                                <tr>
                                                                    <td colspan="7" class="text-center py-3">
                                                                        <i class="fas fa-info-circle me-2"></i> No
                                                                        courses found.
                                                                    </td>
                                                                </tr>
                                                            </c:if>
                                                        </tbody>
                                                    </table>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <jsp:include page="./common/scripts.jsp" />
                </body>

                </html>