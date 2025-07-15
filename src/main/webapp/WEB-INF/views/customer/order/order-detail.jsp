<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="en_US" />
<!DOCTYPE html>
<html lang="en">

    <head>
        <title>Order Details - CloneCoursera</title>
        <!-- Include common header resources -->
        <jsp:include page="/WEB-INF/views/customer/common/head.jsp" />
        <style>
            body {
                background-color: #f8f9fa;
            }

            .course-img {
                width: 100px;
                height: 70px;
                object-fit: cover;
                border-radius: 8px;
                box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                transition: transform 0.3s;
            }

            .course-img:hover {
                transform: scale(1.05);
            }

            .status-badge {
                padding: 8px 15px;
                border-radius: 20px;
                font-weight: 600;
                font-size: 0.9rem;
            }

            .status-completed {
                background-color: #d4edda;
                color: #28a745;
            }

            .status-pending {
                background-color: #fff3cd;
                color: #ffc107;
            }

            .status-cancelled {
                background-color: #f8d7da;
                color: #dc3545;
            }

            .card {
                border: none;
                border-radius: 15px;
                box-shadow: 0 8px 15px rgba(0, 0, 0, 0.1);
                transition: transform 0.3s;
            }

            .card:hover {
                transform: translateY(-5px);
            }

            .card-header {
                border-radius: 15px 15px 0 0 !important;
                background: linear-gradient(135deg, #e83e8c 0%, #fd86b3 100%);
            }

            .btn-primary {
                background: linear-gradient(135deg, #e83e8c 0%, #fd86b3 100%);
                border: none;
                box-shadow: 0 4px 6px rgba(232, 62, 140, 0.2);
            }

            .btn-primary:hover {
                background: linear-gradient(135deg, #d32e7b 0%, #ec75a2 100%);
                box-shadow: 0 6px 8px rgba(232, 62, 140, 0.3);
            }

            .btn-outline-primary {
                color: #e83e8c;
                border-color: #e83e8c;
            }

            .btn-outline-primary:hover {
                background-color: #e83e8c;
                border-color: #e83e8c;
            }

            .course-card {
                border-radius: 12px;
                overflow: hidden;
            }

            .text-primary {
                color: #e83e8c !important;
            }

            .text-success {
                color: #28a745 !important;
            }

            .progress {
                height: 10px;
                border-radius: 10px;
                background-color: #e9ecef;
                margin: 10px 0;
                overflow: hidden;
            }

            .progress-bar {
                background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
            }

            .progress-0 {
                width: 0%;
            }

            .progress-5 {
                width: 5%;
            }

            .progress-10 {
                width: 10%;
            }

            .progress-15 {
                width: 15%;
            }

            .progress-20 {
                width: 20%;
            }

            .progress-25 {
                width: 25%;
            }

            .progress-30 {
                width: 30%;
            }

            .progress-35 {
                width: 35%;
            }

            .progress-40 {
                width: 40%;
            }

            .progress-45 {
                width: 45%;
            }

            .progress-50 {
                width: 50%;
            }

            .progress-55 {
                width: 55%;
            }

            .progress-60 {
                width: 60%;
            }

            .progress-65 {
                width: 65%;
            }

            .progress-70 {
                width: 70%;
            }

            .progress-75 {
                width: 75%;
            }

            .progress-80 {
                width: 80%;
            }

            .progress-85 {
                width: 85%;
            }

            .progress-90 {
                width: 90%;
            }

            .progress-95 {
                width: 95%;
            }

            .progress-100 {
                width: 100%;
            }

            .floating-element:nth-child(3) {
                top: 6% !important;
                right: 2% !important;
            }
            .floating-element:nth-child(1) {
                top: 2% !important;
            }

        </style>
    </head>

    <body>
        <!-- Include navigation -->
        <jsp:include page="/WEB-INF/views/customer/common/navigation.jsp" />

        <!-- Page Header with Floating Elements -->
        <header class="page-header">
            <div class="floating-element"><i class="fas fa-receipt"></i></div>
            <div class="floating-element"><i class="fas fa-box-open"></i></div>
            <div class="floating-element"><i class="fas fa-credit-card"></i></div>
            <div class="floating-element"><i class="fas fa-check-circle"></i></div>
            <div class="floating-element"><i class="fas fa-shopping-cart"></i></div>
            <div class="container">
                <div class="row">
                    <div class="col-lg-8">
                        <h1 class="display-4 fw-bold">Order Details</h1>
                        <p class="lead">Thank you for your order #${order.orderID}</p>
                    </div>
                    <div class="col-lg-4 text-lg-end">
                        <h4 class="mt-3">
                            <c:choose>
                                <c:when test="${order.status eq 'completed'}">
                                    <span class="status-badge status-completed"><i
                                            class="fas fa-check-circle me-2"></i>Completed</span>
                                    </c:when>
                                    <c:when test="${order.status eq 'pending'}">
                                    <span class="status-badge status-pending"><i
                                            class="fas fa-clock me-2"></i>Pending</span>
                                    </c:when>
                                    <c:when test="${order.status eq 'cancelled'}">
                                    <span class="status-badge status-cancelled"><i
                                            class="fas fa-times-circle me-2"></i>Cancelled</span>
                                    </c:when>
                                </c:choose>
                        </h4>
                        <p class="text-white mt-2">Order Date:
                            <fmt:formatDate value="${order.orderDate}" pattern="MMMM dd, yyyy" />
                        </p>
                    </div>
                </div>
            </div>
        </header>

        <div class="container mb-5">
            <c:if test="${not empty param.success}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="fas fa-check-circle me-2"></i> Your order has been placed successfully! You can
                    now access your purchased courses.
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <div class="row">
                <!-- Order Details -->
                <div class="col-lg-8">
                    <div class="card mb-4">
                        <div class="card-header text-white d-flex justify-content-between align-items-center">
                            <h4 class="mb-0"><i class="fas fa-info-circle me-2"></i>Order Information</h4>
                            <c:if test="${orderEligibleForRefund}">
                                <a href="${pageContext.request.contextPath}/refund/request/order/${order.orderID}"
                                   class="btn btn-sm btn-outline-light">
                                    <i class="fas fa-undo-alt me-1"></i>Request Refund for All Courses
                                </a>
                            </c:if>
                        </div>
                        <div class="card-body">
                            <div class="row mb-4">
                                <div class="col-md-6 mb-3 mb-md-0">
                                    <h5 class="fw-bold"><i class="fas fa-shopping-bag me-2"></i>Order Details
                                    </h5>
                                    <p class="mb-1"><strong>Order ID:</strong> #${order.orderID}</p>
                                    <p class="mb-1"><strong>Date:</strong>
                                        <fmt:formatDate value="${order.orderDate}"
                                                        pattern="MMMM dd, yyyy HH:mm" />
                                    </p>
                                    <p class="mb-1"><strong>Payment Method:</strong> ${order.paymentMethod}</p>
                                    <p class="mb-0"><strong>Total:</strong>
                                        <fmt:formatNumber value="${order.totalAmount}" type="number" />đ
                                    </p>
                                </div>
                                <div class="col-md-6">
                                    <h5 class="fw-bold"><i class="fas fa-user me-2"></i>Customer Information
                                    </h5>
                                    <p class="mb-1"><strong>Name:</strong> ${order.customer.fullName}</p>
                                    <p class="mb-1"><strong>Email:</strong> ${order.customer.email}</p>
                                    <c:if test="${not empty order.customer.phone}">
                                        <p class="mb-1"><strong>Phone:</strong> ${order.customer.phone}</p>
                                    </c:if>
                                    <c:if test="${not empty order.customer.address}">
                                        <p class="mb-0"><strong>Address:</strong> ${order.customer.address}</p>
                                    </c:if>
                                </div>
                            </div>

                            <h5 class="mb-3 fw-bold"><i class="fas fa-graduation-cap me-2"></i>Purchased Courses
                            </h5>

                            <c:forEach var="detail" items="${order.orderDetails}" varStatus="status">
                                <div class="card mb-3 course-card">
                                    <div class="card-body">
                                        <div class="row align-items-center">
                                            <div class="col-lg-2 col-md-3 mb-3 mb-md-0 text-center">
                                                <c:choose>
                                                    <c:when test="${not empty detail.course.imageUrl}">
                                                        <img src="${pageContext.request.contextPath}/${detail.course.imageUrl}"
                                                             alt="${detail.course.name}" class="course-img">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <img src="${pageContext.request.contextPath}/assets/images/courses/course-placeholder.jpg"
                                                             alt="${detail.course.name}" class="course-img">
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <div class="col-lg-5 col-md-5 mb-3 mb-md-0">
                                                <h5 class="course-title">
                                                    <a href="${pageContext.request.contextPath}/course/${detail.courseID}"
                                                       class="text-decoration-none text-dark">
                                                        ${detail.course.name}
                                                    </a>
                                                </h5>
                                                <p class="mb-0 text-muted">
                                                    <c:choose>
                                                        <c:when test="${not empty detail.course and not empty detail.course.instructors}">
                                                            <c:choose>
                                                                <c:when test="${not empty detail.course.instructors[0].name}">
                                                                    <span class="text-muted">
                                                                        <i class="fas fa-user me-1"></i>${detail.course.instructors[0].name}
                                                                    </span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="text-muted"><i class="fas fa-user me-1"></i>Instructor</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="text-muted"><i class="fas fa-user me-1"></i>Instructor</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </p>

                                                <!-- Progress Bar -->
                                                <div class="mt-2">
                                                    <div class="d-flex justify-content-between small">
                                                        <span>Course Progress</span>
                                                        <c:set var="progress"
                                                               value="${detail.getAttribute('progress')}" />
                                                        <c:set var="progressValue"
                                                               value="${progress != null ? progress.completionPercentage : 0}" />
                                                        <span class="fw-bold">${progressValue}%</span>
                                                    </div>
                                                    <div class="progress">
                                                        <c:set var="progressClass"
                                                               value="progress-${progressValue - (progressValue % 5)}" />
                                                        <div class="progress-bar ${progressClass}"
                                                             role="progressbar" style="width: ${progressValue}%"
                                                             aria-valuenow="${progressValue}" aria-valuemin="0"
                                                             aria-valuemax="100"></div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="col-lg-3 col-6 col-md-4 text-center text-lg-start">
                                                <p class="mb-0 fs-5 fw-bold">
                                                    <fmt:formatNumber value="${detail.price}" type="number" />đ
                                                </p>
                                            </div>
                                            <div class="col-lg-2 col-6 col-md-12 text-end">
                                                <c:if test="${order.status ne 'pending' and order.status ne 'cancelled'}">
                                                    <a href="${pageContext.request.contextPath}/course/${detail.courseID}"
                                                       class="btn btn-sm btn-primary">
                                                        <i class="fas fa-play-circle me-1"></i>Start Learning
                                                    </a>
                                                </c:if>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </div>

                <!-- Order Summary -->
                <div class="col-lg-4">
                    <div class="card mb-4">
                        <div class="card-header text-white">
                            <h4 class="mb-0"><i class="fas fa-receipt me-2"></i>Order Summary</h4>
                        </div>
                        <div class="card-body">
                            <div class="d-flex justify-content-between mb-3">
                                <span>Subtotal</span>
                                <span>
                                    <fmt:formatNumber value="${order.totalAmount}" type="number" />đ
                                </span>
                            </div>
                            <c:if test="${order.totalAmount > originalAmount}">
                                <div class="d-flex justify-content-between mb-3 text-success">
                                    <span>Discount</span>
                                    <span>- 
                                        <fmt:formatNumber value="${order.totalAmount - originalAmount}"
                                                          type="number" />đ
                                    </span>
                                </div>
                            </c:if>
                            <hr>
                            <div class="d-flex justify-content-between mb-3 fw-bold">
                                <span>Total</span>
                                <span>
                                    <fmt:formatNumber value="${order.totalAmount}" type="number" />đ
                                </span>
                            </div>
                            <div class="text-center mt-4">
                                <a href="${pageContext.request.contextPath}/my-courses"
                                   class="btn btn-primary w-100">
                                    <i class="fas fa-graduation-cap me-2"></i>Go to My Courses
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Include footer -->
        <jsp:include page="/WEB-INF/views/customer/common/footer.jsp" />

        <!-- JS Script References -->
        <jsp:include page="/WEB-INF/views/customer/common/scripts.jsp" />
    </body>

</html>