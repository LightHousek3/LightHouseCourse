<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html>

    <head>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
        <jsp:include page="../common/head.jsp" />
        <title>Manage Refund Requests</title>
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
                <div class="d-flex align-items-center">
                    <span class="me-3">Welcome, ${sessionScope.user.username != null ?
                                                  sessionScope.user.username : "Admin"}!</span>
                    <div class="dropdown">
                        <button class="btn btn-outline-secondary dropdown-toggle" type="button"
                                id="userDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                            <i class="fas fa-user-circle me-1"></i> Admin
                        </button>
                    </div>
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

            <!-- Status Filter -->
            <div class="card shadow mb-4">
                <div class="card-header">
                    <h5 class="mb-0">Filter Refund Requests</h5>
                </div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/admin/refunds" method="get"
                          class="row g-3 align-items-center">
                        <div class="col-auto">
                            <label for="status" class="col-form-label">Status:</label>
                        </div>
                        <div class="col-auto">
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
                        <div class="col-auto">
                            <button type="submit" class="btn btn-primary">Filter</button>
                        </div>
                    </form>
                </div>
            </div>

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
                                <table class="table table-striped table-hover">
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

                                                <td>$
                                                    <fmt:formatNumber value="${refund.refundAmount}" type="currency" />
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${refund.status eq 'pending'}">
                                                            <span
                                                                class="badge bg-warning text-dark">Pending</span>
                                                        </c:when>
                                                        <c:when test="${refund.status eq 'approved'}">
                                                            <span class="badge bg-success">Approved</span>
                                                        </c:when>
                                                        <c:when test="${refund.status eq 'rejected'}">
                                                            <span class="badge bg-danger">Rejected</span>
                                                        </c:when>
                                                    </c:choose>
                                                </td>                                                
                                                <td>
                                                    <!-- Container for Buttons -->
                                                    <div class="d-flex flex-column flex-lg-row gap-2" >
                                                        <!-- Details Button -->
                                                        <a href="${pageContext.request.contextPath}/admin/refunds/details/${refund.refundID}"
                                                           class="btn btn-info btn-sm">Details</a>  
                                                    </div>

                                                    <!-- Approve Modal -->
                                                    <div class="modal fade" id="approveModal${refund.refundID}" tabindex="-1" aria-hidden="true">
                                                        <div class="modal-dialog">
                                                            <div class="modal-content">
                                                                <div class="modal-header">
                                                                    <h5 class="modal-title">Approve Refund Request</h5>
                                                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                                </div>
                                                                <div class="modal-body">
                                                                    <p>Are you sure you want to approve this refund request?</p>
                                                                    <p><strong>Amount:</strong> $<fmt:formatNumber value="${refund.refundAmount}" pattern="#0.00" /></p>
                                                                </div>
                                                                <div class="modal-footer">
                                                                    <form action="${pageContext.request.contextPath}/admin/refunds" method="post">
                                                                        <input type="hidden" name="action" value="processRefund">
                                                                        <input type="hidden" name="refundId" value="${refund.refundID}">
                                                                        <input type="hidden" name="status" value="approved">
                                                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                                                        <button type="submit" class="btn btn-success">Approve Refund</button>
                                                                    </form>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>

                                                    <!-- Reject Modal -->
                                                    <div class="modal fade" id="rejectModal${refund.refundID}" tabindex="-1" aria-hidden="true">
                                                        <div class="modal-dialog">
                                                            <div class="modal-content">
                                                                <div class="modal-header">
                                                                    <h5 class="modal-title">Reject Refund Request</h5>
                                                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                                </div>
                                                                <div class="modal-body">
                                                                    <p>Are you sure you want to reject this refund request?</p>
                                                                    <p><strong>Amount:</strong> $<fmt:formatNumber value="${refund.refundAmount}" pattern="#0.00" /></p>
                                                                </div>
                                                                <div class="modal-footer">
                                                                    <form action="${pageContext.request.contextPath}/admin/refunds" method="post">
                                                                        <input type="hidden" name="action" value="processRefund">
                                                                        <input type="hidden" name="refundId" value="${refund.refundID}">
                                                                        <input type="hidden" name="status" value="rejected">
                                                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                                                        <button type="submit" class="btn btn-danger">Reject Refund</button>
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
                            </div>
                        </c:otherwise>
                    </c:choose>
                    <nav aria-label="Page navigation">
                        <ul class="pagination justify-content-center">
                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                <a class="page-link" href="?page=${currentPage - 1}${not empty selectedStatus ? '&status='.concat(selectedStatus) : ''}">Previous</a>
                            </li>

                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                    <a class="page-link" href="?page=${i}${not empty selectedStatus ? '&status='.concat(selectedStatus) : ''}">${i}</a>
                                </li>
                            </c:forEach>

                            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                <a class="page-link" href="?page=${currentPage + 1}${not empty selectedStatus ? '&status='.concat(selectedStatus) : ''}">Next</a>
                            </li>
                        </ul>
                    </nav>
                </div>
            </div>
        </div>

        <jsp:include page="../common/scripts.jsp" />
    </body>

</html>