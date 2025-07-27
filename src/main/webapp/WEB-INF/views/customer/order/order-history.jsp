<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
            <fmt:setLocale value="en_US" />
            <!DOCTYPE html>
            <html lang="en">

            <head>
                <title>My Courses & Order History - LightHouse</title>
                <!-- Include common header resources -->
                <jsp:include page="/WEB-INF/views/customer/common/head.jsp" />
                <style>
                    .course-img {
                        width: 80px;
                        height: 60px;
                        object-fit: cover;
                        border-radius: 5px;
                    }

                    .order-card {
                        transition: transform 0.3s;
                        margin-bottom: 20px;
                        border: none;
                        box-shadow: var(--box-shadow);
                        border-radius: var(--border-radius);
                    }

                    .order-card:hover {
                        transform: translateY(-5px);
                        box-shadow: var(--box-shadow-hover);
                    }

                    .status-badge {
                        padding: 6px 12px;
                        border-radius: 20px;
                        font-weight: 600;
                        font-size: 0.8rem;
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

                    .nav-tabs .nav-link.active {
                        border-color: var(--primary-color);
                        color: var(--primary-color);
                        font-weight: 600;
                    }

                    .empty-state {
                        text-align: center;
                        padding: 50px 0;
                    }

                    .empty-state i {
                        font-size: 5rem;
                        color: #dee2e6;
                        margin-bottom: 20px;
                    }

                    .card {
                        border-radius: var(--border-radius);
                        border: none;
                        box-shadow: var(--box-shadow);
                        transition: all var(--transition-speed);
                        overflow: hidden;
                        background-color: rgba(255, 255, 255, 0.95);
                    }

                    .card:hover {
                        transform: translateY(-5px);
                        box-shadow: var(--box-shadow-hover);
                    }

                    .btn-primary {
                        background: linear-gradient(60deg, var(--primary-color), var(--primary-light));
                        color: var(--text-white);
                        border: none;
                        border-radius: var(--border-radius);
                        transition: all var(--transition-speed);
                        font-weight: 600;
                        text-transform: uppercase;
                        letter-spacing: 0.5px;
                        padding: 0.6rem 1.5rem;
                        box-shadow: var(--box-shadow);
                    }

                    .btn-primary:hover {
                        background: linear-gradient(45deg, var(--primary-dark), var(--primary-color));
                        transform: translateY(-3px);
                        box-shadow: var(--box-shadow-hover);
                    }

                    .btn-outline-primary {
                        background: transparent;
                        border: 2px solid var(--primary-color);
                        color: var(--primary-color);
                        border-radius: var(--border-radius);
                        transition: all var(--transition-speed);
                        font-weight: 600;
                        text-transform: uppercase;
                        letter-spacing: 0.5px;
                        padding: 0.6rem 1.5rem;
                        box-shadow: var(--box-shadow);
                    }

                    .btn-outline-primary:hover {
                        background: var(--primary-color);
                        color: var(--text-white);
                        transform: translateY(-3px);
                        box-shadow: var(--box-shadow-hover);
                    }

                    body {
                        background-color: var(--bg-light);
                    }

                    .hero-title {
                        font-weight: 700;
                        font-size: 2.8rem;
                        background: linear-gradient(90deg, #fff, #ffe1ed);
                        -webkit-background-clip: text;
                        -webkit-text-fill-color: transparent;
                        margin-bottom: 1.5rem;
                        text-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
                    }

                    .hero-subtitle {
                        font-size: 1.2rem;
                        color: white;
                        line-height: 1.6;
                        text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
                    }

                    .floating-element:nth-child(1) {
                        top: 2% !important;
                    }
                </style>
            </head>

            <body>
                <!-- Include navigation -->
                <jsp:include page="/WEB-INF/views/customer/common/navigation.jsp" />

                <!-- Page Header -->
                <header class="page-header">
                    <div class="floating-element"><i class="fas fa-graduation-cap"></i></div>
                    <div class="floating-element"><i class="fas fa-book"></i></div>
                    <div class="floating-element"><i class="fas fa-credit-card"></i></div>
                    <div class="floating-element"><i class="fas fa-shopping-cart"></i></div>
                    <div class="floating-element"><i class="fas fa-shield-alt"></i></div>
                    <div class="container">
                        <h1 class="hero-title">My Orders</h1>
                        <p class="hero-subtitle">View your order history and access order details</p>
                    </div>
                </header>

                <div class="container mb-5">
                    <c:if test="${not empty success}">
                        <div class="alert alert-success alert-dismissible fade show animate__animated animate__shakeX"
                            role="alert">
                            <i class="fas fa-check-circle me-2 text-success"></i> ${success}
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>
                    <!-- Order History Tab -->
                    <div class="tab-pane" id="orders">
                        <c:choose>
                            <c:when test="${empty orders}">
                                <div class="empty-state">
                                    <i class="fas fa-shopping-bag"></i>
                                    <h3>No Orders Yet</h3>
                                    <p class="text-muted mb-4">You haven't placed any orders yet. Start shopping today!
                                    </p>
                                    <a href="${pageContext.request.contextPath}/home?scroll=true"
                                        class="btn btn-primary">Browse
                                        Courses</a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="order" items="${orders}">
                                    <div class="card order-card">
                                        <div
                                            class="card-header bg-light d-flex justify-content-between align-items-center">
                                            <div>
                                                <h5 class="mb-0">Order #${order.orderID}</h5>
                                                <p class="text-muted mb-0">
                                                    <small>
                                                        <fmt:formatDate value="${order.orderDate}"
                                                            pattern="MMMM dd, yyyy" /> |
                                                        Payment: ${order.paymentMethod}
                                                    </small>
                                                </p>
                                            </div>
                                            <div class="d-flex align-items-center">
                                                <c:choose>
                                                    <c:when test="${order.status eq 'completed'}">
                                                        <span
                                                            class="status-badge status-completed me-3">Completed</span>
                                                    </c:when>
                                                    <c:when test="${order.status eq 'pending'}">
                                                        <span class="status-badge status-pending me-3">Pending</span>
                                                    </c:when>
                                                    <c:when test="${order.status eq 'cancelled'}">
                                                        <span
                                                            class="status-badge status-cancelled me-3">Cancelled</span>
                                                    </c:when>
                                                </c:choose>
                                                <span class="fw-bold">
                                                    <fmt:formatNumber value="${order.totalAmount}" type="number" />đ
                                                </span>
                                            </div>
                                        </div>
                                        <div class="card-body">
                                            <div class="row">
                                                <div class="col-md-9">
                                                    <c:forEach var="detail" items="${order.orderDetails}"
                                                        varStatus="status">
                                                        <div
                                                            class="d-flex align-items-center mb-3 ${status.last ? '' : 'pb-3 border-bottom'}">
                                                            <img src="${pageContext.request.contextPath}/${detail.course.imageUrl}"
                                                                class="course-img me-3" alt="${detail.course.name}">
                                                            <div>
                                                                <h6 class="mb-1">${detail.course.name}</h6>
                                                                <c:if test="${not empty detail.course.instructors}">
                                                                    <p class="text-muted mb-0">
                                                                        <i class="fas fa-chalkboard-teacher me-1"></i>
                                                                        <c:forEach var="instructor"
                                                                            items="${detail.course.instructors}"
                                                                            varStatus="status">
                                                                            ${instructor.name}<c:if
                                                                                test="${!status.last}">, </c:if>
                                                                        </c:forEach>
                                                                    </p>
                                                                </c:if>

                                                            </div>
                                                        </div>
                                                    </c:forEach>
                                                </div>
                                                <div class="col-md-3 text-end">
                                                    <a href="${pageContext.request.contextPath}/order/detail/${order.orderID}"
                                                        class="btn btn-outline-primary">
                                                        View Details
                                                    </a>
                                                    <c:if test="${order.getAttribute('eligibleForRefund') == true}">
                                                        <a href="${pageContext.request.contextPath}/refund/request/order/${order.orderID}"
                                                            class="btn btn-outline-danger mt-2">
                                                            Request Refund
                                                        </a>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- Include footer -->
                <jsp:include page="/WEB-INF/views/customer/common/footer.jsp" />

                <jsp:include page="/WEB-INF/views/customer/common/scripts.jsp" />

            </body>

            </html>