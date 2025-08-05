<%-- Document : dashboard Created on : Jun 12, 2025, 22:12:40 PM Author : DangPH - CE180896 --%>

    <%@page contentType="text/html" pageEncoding="UTF-8" %>
        <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
            <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
                <fmt:setLocale value="vi_VN" />
                <!DOCTYPE html>
                <html lang="en">

                <head>
                    <title>Admin Dashboard - LightHouse Admin</title>
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
                    </style>
                </head>

                <body>
                    <%-- Admin Sidebar --%>
                        <c:set var="activeMenu" value="dashboard" scope="request" />
                        <jsp:include page="./common/sidebar.jsp" />

                        <%-- Admin Content --%>
                            <div class="admin-content">
                                <!-- Header -->
                                <div class="admin-header d-flex justify-content-between align-items-center">
                                    <button class="btn d-lg-none" id="toggleSidebarBtn">
                                        <i class="fas fa-bars"></i>
                                    </button>
                                    <h2 class="m-0 d-none d-lg-block">Dashboard</h2>
                                    <div class="d-flex align-items-center">
                                        <span class="me-3">Welcome, ${admin.fullName}!</span>
                                        <div class="dropdown">
                                            <button class="btn btn-lg btn-outline-secondary dropdown-toggle gap-1"
                                                type="button" id="userDropdown" data-bs-toggle="dropdown"
                                                aria-expanded="false">
                                                <img src="${pageContext.request.contextPath}${admin.avatar}"
                                                    style="width: 30px; height: 30px; border-radius: 50%;"
                                                    alt="Avatar" /> ${admin.fullName}
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
                                                        <h6 class="text-muted mb-1">Total Instructors</h6>
                                                        <h2 class="mb-0">${totalInstructors}</h2>
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
                                                        <i class="fas fa-users"></i>
                                                    </div>
                                                    <div>
                                                        <h6 class="text-muted mb-1">Total Customers</h6>
                                                        <h2 class="mb-0">${totalCustomers}</h2> 
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
                                                        <i class="fas fa-undo-alt"></i>
                                                    </div>
                                                    <div>
                                                        <h6 class="text-muted mb-1">Total Refunds</h6>
                                                        <h2 class="mb-0">${totalPendingRefunds}</h2>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <!-- Recent Refunds -->
                                <div class="row mb-4">
                                    <div class="col-12">
                                        <div class="card">
                                            <div class="card-header d-flex justify-content-between align-items-center">
                                                <h5 class="mb-0">Recent Refunds</h5>
                                                <a href="${pageContext.request.contextPath}/admin/refunds?status=pending"
                                                    class="btn btn-warning btn-lg"><i class="fas fa-clock me-2"></i>View
                                                    All Pending</a>
                                            </div>
                                            <div class="card-body">
                                                <div class="table-responsive">
                                                    <table class="table table-hover align-middle">
                                                        <thead>
                                                            <tr>
                                                                <th>ID</th>
                                                                <th>User</th>
                                                                <th>Request Date</th>
                                                                <th>Course(s)</th>
                                                                <th>Amount</th>
                                                                <th>Status</th>
                                                                <th>Action</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody>
                                                            <c:forEach var="refund" items="${recentRefundRequests}">
                                                                <tr>
                                                                    <td>#${refund.refundID}</td>
                                                                    <td>${refund.userName}</td>
                                                                    <td>
                                                                        <fmt:formatDate value="${refund.requestDate}"
                                                                            pattern="yyyy-MM-dd HH:mm" />
                                                                    </td>
                                                                    <td>
                                                                        <c:choose>
                                                                            <c:when
                                                                                test="${not empty refund.courseName}">
                                                                                ${refund.courseName}
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <span class="text-muted">Full Order
                                                                                    #${refund.orderID}</span>
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </td>
                                                                    <td>
                                                                        <fmt:formatNumber value="${refund.refundAmount}"
                                                                            type="currency" />
                                                                    </td>
                                                                    <td>
                                                                        <c:choose>
                                                                            <c:when
                                                                                test="${refund.status eq 'approved'}">
                                                                                <span
                                                                                    class="status-badge status-completed">Approved</span>
                                                                            </c:when>
                                                                            <c:when
                                                                                test="${refund.status eq 'pending'}">
                                                                                <span
                                                                                    class="status-badge status-pending">Pending</span>
                                                                            </c:when>
                                                                            <c:when
                                                                                test="${refund.status eq 'rejected'}">
                                                                                <span
                                                                                    class="status-badge status-cancelled">Rejected</span>
                                                                            </c:when>
                                                                        </c:choose>
                                                                    </td>
                                                                    <td>
                                                                        <a href="${pageContext.request.contextPath}/admin/refunds/view/${refund.refundID}"
                                                                            class="btn btn-sm btn-outline-primary">
                                                                            <i class="fas fa-eye"></i>
                                                                        </a>
                                                                    </td>
                                                                </tr>
                                                            </c:forEach>

                                                            <c:if test="${empty recentRefundRequests}">
                                                                <tr>
                                                                    <td colspan="7" class="text-center py-3">
                                                                        <i class="fas fa-info-circle me-2"></i> No
                                                                        refund
                                                                        requests found.
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

                                <!-- Recent Courses -->
                                <div class="row">
                                    <div class="col-12">
                                        <div class="card">
                                            <div class="card-header d-flex justify-content-between align-items-center">
                                                <h5 class="mb-0">Recent Courses</h5>
                                                <a href="${pageContext.request.contextPath}/admin/course/pending"
                                                    class="btn btn-lg btn-warning"><i class="fas fa-clock me-2"></i>View
                                                    All Pending</a>
                                            </div>
                                            <div class="card-body">
                                                <div class="table-responsive">
                                                    <table class="table table-hover align-middle">
                                                        <thead>
                                                            <tr>
                                                                <th>ID</th>
                                                                <th>Image</th>
                                                                <th>Course Name</th>
                                                                <th>Instructor</th>
                                                                <th>Submission Date</th>
                                                                <th>Status</th>
                                                                <th>Action</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody>
                                                            <c:forEach var="course" items="${recentCourses}">
                                                                <tr>
                                                                    <td>${course.courseID}</td>
                                                                    <td>
                                                                        <img src="${pageContext.request.contextPath}/${course.imageUrl}"
                                                                            alt="${course.name}"
                                                                            style="width: 60px; height: 40px; object-fit: cover; border-radius: 4px;">
                                                                    </td>
                                                                    <td>${course.name}</td>
                                                                    <td>
                                                                        <c:forEach var="i" items="${course.instructors}"
                                                                            varStatus="status">
                                                                            ${i.fullName}<c:if test="${!status.last}">,
                                                                            </c:if>
                                                                        </c:forEach>
                                                                    </td>
                                                                    <td>
                                                                        <fmt:formatDate value="${course.submissionDate}"
                                                                            pattern="yyyy-MM-dd" />
                                                                    </td>
                                                                    <td>
                                                                        <c:choose>
                                                                            <c:when
                                                                                test="${course.approvalStatus eq 'pending'}">
                                                                                <span
                                                                                    class="status-badge status-pending">Pending</span>
                                                                            </c:when>
                                                                            <c:when
                                                                                test="${course.approvalStatus eq 'approved'}">
                                                                                <span
                                                                                    class="status-badge status-approved">Approved</span>
                                                                            </c:when>
                                                                            <c:when
                                                                                test="${course.approvalStatus eq 'rejected'}">
                                                                                <span
                                                                                    class="status-badge status-rejected">Rejected</span>
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <span
                                                                                    class="badge bg-secondary">Unknown</span>
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </td>
                                                                    <td>
                                                                        <a href="${pageContext.request.contextPath}/admin/course/view/${course.courseID}"
                                                                            class="btn btn-sm btn-outline-primary me-1">
                                                                            <i class="fas fa-eye"></i>
                                                                        </a>
                                                                        <c:if
                                                                            test="${course.approvalStatus eq 'pending'}">
                                                                            <a href="${pageContext.request.contextPath}/admin/course/approve/${course.courseID}"
                                                                                class="btn btn-sm btn-outline-success me-1"
                                                                                onclick="return confirm('Are you sure you want to approve this course?')">
                                                                                <i class="fas fa-check"></i>
                                                                            </a>
                                                                            <a href="${pageContext.request.contextPath}/admin/course/reject/${course.courseID}"
                                                                                class="btn btn-sm btn-outline-danger"
                                                                                onclick="return confirm('Are you sure you want to reject this course?')">
                                                                                <i class="fas fa-times"></i>
                                                                            </a>
                                                                        </c:if>
                                                                    </td>
                                                                </tr>
                                                            </c:forEach>

                                                            <c:if test="${empty recentCourses}">
                                                                <tr>
                                                                    <td colspan="7" class="text-center py-3">
                                                                        <i class="fas fa-info-circle me-2"></i> No
                                                                        pending
                                                                        course
                                                                        approvals found.
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