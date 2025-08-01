<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html>

    <head>
        <jsp:include page="../common/head.jsp" />
        <title>Manage Refund Requests</title>
        <style>
            .form-action {
                align-items: end !important;
            }

            .form-action .row {
                align-items: end !important;
                margin-top: 15px;
            }
        </style>
    </head>

    <body>
        <!-- Admin Sidebar -->
        <c:set var="activeMenu" value="refunds" scope="request" />
        <jsp:include page="../common/sidebar.jsp" />

        <!-- Admin Content -->
        <div class="admin-content">
            <!-- Header -->
            <div class="admin-header d-flex justify-content-between align-items-center">
                <button class="btn d-lg-none" id="toggleSidebarBtn">
                    <i class="fas fa-bars"></i>
                </button>
                <h2 class="m-0 d-none d-lg-block">Refund Requests Management</h2>
                <a href="${pageContext.request.contextPath}/admin/refunds"
                   class="btn btn-lg btn-outline-secondary">
                    <i class="fas fa-sync-alt me-2"></i>Reset Filters
                </a>
            </div>

            <!-- Status Filter -->
            <div class="card shadow mb-4">
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/admin/refunds" method="get"
                          class="form-action row g-3 align-items-center">
                        <!-- ...existing status filter... -->
                        <div class="col-md-4">
                            <label for="search" class="form-label">Search Refund Requests</label>
                            <input type="text" class="form-control" name="search" id="search"
                                   placeholder="Search by user, course" value="${param.search}" />
                        </div>
                        <div class="col-md-4">
                            <label for="status" class="col-form-label">Status</label>
                            <select class="form-select" name="status" id="status">
                                <option value="">All Requests</option>
                                <option value="pending" ${selectedStatus eq 'pending' ? 'selected' : '' }>
                                    Pending</option>
                                <option value="approved" ${selectedStatus eq 'approved' ? 'selected' : '' }>
                                    Approved</option>
                                <option value="rejected" ${selectedStatus eq 'rejected' ? 'selected' : '' }>
                                    Rejected</option>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <button type="submit" class="btn btn-md btn-outline-primary">Apply Filter</button>
                        </div>
                    </form>
                </div>
            </div>

            <c:if test="${param.success eq 'true'}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    Refund request has been processed successfully.
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <c:if test="${param.error eq 'true' || param.error eq 'invalid'}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <c:choose>
                        <c:when test="${param.error eq 'invalid'}">Invalid request parameters.</c:when>
                        <c:otherwise>Failed to process the refund request.</c:otherwise>
                    </c:choose>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>
            <div class="card shadow">
                <div class="card-body">
                    <c:choose>
                        <c:when test="${empty refundRequests}">
                            <div class="text-center p-4">
                                <p class="lead">No refund requests found with the selected criteria.</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="table-responsive">
                                <table class="table table-hover">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Request Date</th>
                                            <th>User</th>
                                            <th>Course</th>
                                            <th>Amount</th>
                                            <th>Status</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${refundRequests}" var="refund">
                                            <tr>
                                                <td>${refund.refundID}</td>
                                                <td>
                                                    <fmt:formatDate value="${refund.requestDate}"
                                                                    pattern="dd MMM yyyy" />
                                                </td>
                                                <td>${refund.userName}</td>
                                                <td>${refund.courseName}</td>

                                                <td>
                                                    <fmt:formatNumber value="${refund.refundAmount}" type="number" />đ
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${refund.status eq 'pending'}">
                                                            <span
                                                                class="status-badge status-pending">Pending</span>
                                                        </c:when>
                                                        <c:when test="${refund.status eq 'approved'}">
                                                            <span class="status-badge status-approved">Approved</span>
                                                        </c:when>
                                                        <c:when test="${refund.status eq 'rejected'}">
                                                            <span class="status-badge status-rejected">Rejected</span>
                                                        </c:when>
                                                    </c:choose>
                                                </td>                                                
                                                <td>
                                                    <!-- Container for Buttons -->
                                                    <div class="d-flex flex-column flex-lg-row gap-2" >
                                                        <!-- Details Button -->
                                                        <a href="${pageContext.request.contextPath}/admin/refunds/view/${refund.refundID}"
                                                           class="btn btn-info btn-sm"><i class="fas fa-eye"></i></a>  
                                                    </div>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:otherwise>
                    </c:choose>
                    <c:if test="${not empty refundRequests}">
                        <nav aria-label="Page navigation">
                            <ul class="pagination justify-content-center">
                                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                    <a class="page-link btn btn-outline-primary" href="?page=${currentPage - 1}${not empty selectedStatus ? '&status='.concat(selectedStatus) : ''}">&laquo;</a>
                                </li>

                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                                        <a class="page-link btn btn-outline-primary" href="?page=${i}${not empty selectedStatus ? '&status='.concat(selectedStatus) : ''}">${i}</a>
                                    </li>
                                </c:forEach>

                                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                    <a class="page-link btn btn-outline-primary" href="?page=${currentPage + 1}${not empty selectedStatus ? '&status='.concat(selectedStatus) : ''}">&raquo;</a>
                                </li>
                            </ul>
                        </nav>
                    </c:if>                    
                </div>
            </div>
        </div>
        <jsp:include page="../common/scripts.jsp" />
    </body>

</html>