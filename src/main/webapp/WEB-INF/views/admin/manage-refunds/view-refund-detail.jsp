<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html>

    <head>
        <title>Refund Request Details</title>
        <jsp:include page="../common/head.jsp" />
    </head>

    <body>
        <div class="admin-container">
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
                        <a href="${pageContext.request.contextPath}/admin/refunds"
                       class="btn btn-outline-success me-3">
                        <i class="fas fa-arrow-left me-1"></i> Back to List
                    </a>
                    </div>
                </div>

                <!-- Success Message -->
                <div class="messages-container px-3 mt-3">
                    <c:if test="${param.success eq 'true'}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="fas fa-check-circle me-2"></i>
                            <strong>Success!</strong> The refund request has been processed successfully.
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>

                    <!-- Error Messages -->
                    <c:if test="${not empty param.error}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="fas fa-exclamation-circle me-2"></i>
                            <c:choose>
                                <c:when test="${param.error eq 'message_required'}">
                                    <strong>Error:</strong> You must provide a message explaining your decision.
                                </c:when>
                                <c:when test="${param.error eq 'already_processed'}">
                                    <strong>Error:</strong> This refund request has already been processed.
                                </c:when>
                                <c:when test="${param.error eq 'processing_failed'}">
                                    <strong>Error:</strong> Failed to process the refund request. Please try again.
                                </c:when>
                                <c:otherwise>
                                    <strong>Error:</strong> An unexpected error occurred. Please try again.
                                </c:otherwise>
                            </c:choose>
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>
                </div>

                <c:if test="${not empty refundRequest}">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="card shadow mb-4">
                                <div class="card-header d-flex align-items-center">
                                    <i class="fas fa-info-circle me-2 text-primary"></i>
                                    <h5 class="mb-0">Request Information</h5>
                                </div>
                                <div class="card-body">
                                    <div class="info-row">
                                        <div class="info-label">Request ID: #${refundRequest.refundID}</div>
                                    </div>
                                    <div class="info-row">
                                        <div class="info-label">Status:
                                            <c:choose>
                                                <c:when test="${refundRequest.status eq 'pending'}">
                                                    <span class="badge bg-warning text-dark">
                                                        <i class="fas fa-clock me-1"></i> Pending
                                                    </span>
                                                </c:when>
                                                <c:when test="${refundRequest.status eq 'approved'}">
                                                    <span class="badge bg-success">
                                                        <i class="fas fa-check-circle me-1"></i> Approved
                                                    </span>
                                                </c:when>
                                                <c:when test="${refundRequest.status eq 'rejected'}">
                                                    <span class="badge bg-danger">
                                                        <i class="fas fa-times-circle me-1"></i> Rejected
                                                    </span>
                                                </c:when>
                                            </c:choose>
                                        </div>
                                    </div>
                                    <div class="info-row">
                                        <div class="info-label">Request Date:
                                            <i class="far fa-calendar-alt me-1 text-muted"></i>
                                            <fmt:formatDate value="${refundRequest.requestDate}"
                                                            pattern="dd MMM yyyy HH:mm" />
                                        </div>
                                    </div>
                                    <div class="info-row">
                                        <div class="info-label">Refund Amount:
                                            <span class="fw-bold text-success">
                                                <fmt:formatNumber value="${refundRequest.refundAmount}"
                                                                  type="currency" />
                                            </span>
                                        </div>
                                    </div>
                                    <c:if test="${not empty refundRequest.processedDate}">
                                        <div class="info-row">
                                            <div class="info-label">Processed Date:
                                                <i class="far fa-calendar-check me-1 text-muted"></i>
                                                <fmt:formatDate value="${refundRequest.processedDate}"
                                                                pattern="dd MMM yyyy HH:mm" />
                                            </div>
                                        </div>
                                        <div class="info-row">
                                            <div class="info-label">Processed By:
                                                <i class="fas fa-user-shield me-1 text-muted"></i>
                                                ${refundRequest.adminName}
                                            </div>
                                        </div>
                                    </c:if>
                                </div>
                            </div>

                            <div class="card shadow mb-4">
                                <div class="card-header d-flex align-items-center">
                                    <i class="fas fa-book me-2 text-primary"></i>
                                    <h5 class="mb-0">Course Information</h5>
                                </div>
                                <div class="card-body">
                                    <div class="info-row">
                                        <div class="info-label">Course Name: ${refundRequest.courseName}</div>
                                    </div>
                                    <div class="info-row">
                                        <div class="info-label">Order Date:
                                            <i class="far fa-calendar-alt me-1 text-muted"></i>
                                            <fmt:formatDate value="${refundRequest.orderDate}"
                                                            pattern="dd MMM yyyy" />
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <div class="card shadow mb-4">
                                <div class="card-header d-flex align-items-center">
                                    <i class="fas fa-user me-2 text-primary"></i>
                                    <h5 class="mb-0">User Information</h5>
                                </div>
                                <div class="card-body">
                                    <div class="info-row">
                                        <div class="info-label">Username:
                                            <i class="fas fa-user-circle me-1 text-muted"></i>
                                            ${refundRequest.userName}
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="card shadow mb-4">
                                <div class="card-header d-flex align-items-center">
                                    <i class="fas fa-comment-alt me-2 text-primary"></i>
                                    <h5 class="mb-0">Refund Reason</h5>
                                </div>
                                <div class="card-body">
                                    <div class="reason-box">${refundRequest.reason}</div>
                                    <c:if
                                        test="${not empty refundRequest.adminMessage && refundRequest.status ne 'pending'}">
                                        <div class="mt-4">
                                            <h6 class="mb-2"><i class="fas fa-reply me-2 text-primary"></i>Admin
                                                Response:</h6>
                                            <div class="reason-box border-primary">${refundRequest.adminMessage}
                                            </div>
                                        </div>
                                    </c:if>
                                </div>
                            </div>

                            <c:if test="${refundRequest.status eq 'pending'}">
                                <div class="card shadow process-card">
                                    <div class="card-header bg-primary text-white d-flex align-items-center">
                                        <i class="fas fa-tasks me-2"></i>
                                        <h5 class="mb-0">Process This Request</h5>
                                    </div>
                                    <div class="card-body">
                                        <form action="${pageContext.request.contextPath}/admin/refunds"
                                              method="post">
                                            <input type="hidden" name="action" value="processRefund">
                                            <input type="hidden" name="refundId"
                                                   value="${refundRequest.refundID}">

                                            <div class="mb-3">
                                                <label for="status" class="form-label">
                                                    <i class="fas fa-check-circle me-1"></i>
                                                    Decision <span class="text-danger">*</span>
                                                </label>
                                                <select class="form-select" id="status" name="status" required>
                                                    <option value="">-- Select Decision --</option>
                                                    <option value="approved">Approve Refund</option>
                                                    <option value="rejected">Reject Refund</option>
                                                </select>
                                            </div>

                                            <div class="mb-3">
                                                <label for="adminMessage" class="form-label">
                                                    <i class="fas fa-comment-alt me-1"></i>
                                                    Message to User <span class="text-danger">*</span>
                                                </label>
                                                <textarea class="form-control" id="adminMessage"
                                                          name="adminMessage" rows="4"
                                                          placeholder="Provide a reason for your decision..."
                                                          required></textarea>
                                                <div class="form-text">This message will be visible to the user
                                                    in
                                                    their refund details.</div>
                                            </div>

                                            <div class="d-grid">
                                                <button type="submit" class="btn btn-primary btn-lg">
                                                    <i class="fas fa-paper-plane me-2"></i> Submit Decision
                                                </button>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </c:if>
            </div>
        </div>
        <jsp:include page="../common/scripts.jsp" />
    </body>
</html>