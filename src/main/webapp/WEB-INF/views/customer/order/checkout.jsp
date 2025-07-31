<%@page contentType="text/html" pageEncoding="UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
            <!DOCTYPE html>
            <html lang="en">

            <head>
                <title>Checkout - LightHouse</title>
                <!-- Include common header resources -->
                <jsp:include page="/WEB-INF/views/customer/common/head.jsp" />

                <style>
                    body {
                        background-color: var(--bg-light);
                        background-image:
                            radial-gradient(circle at 25% 10%, rgba(255, 192, 203, 0.1) 0%, transparent 60%),
                            radial-gradient(circle at 75% 75%, rgba(232, 62, 140, 0.05) 0%, transparent 60%);
                        background-attachment: fixed;
                    }

                    /* Enhanced checkout steps with advanced animations */
                    .checkout-steps-container {
                        margin-bottom: 60px;
                        padding: 20px;
                        background-color: rgba(255, 255, 255, 0.8);
                        border-radius: var(--border-radius);
                        box-shadow: var(--box-shadow);
                    }

                    .checkout-steps {
                        display: flex;
                        justify-content: space-between;
                        position: relative;
                        z-index: 1;
                    }

                    .checkout-steps::before {
                        content: '';
                        position: absolute;
                        top: 30px;
                        left: 0;
                        width: 100%;
                        height: 6px;
                        background: linear-gradient(90deg,
                                var(--primary-color) 0%,
                                var(--primary-color) 33.33%,
                                #e9ecef 33.33%,
                                #e9ecef 100%);
                        border-radius: 10px;
                        box-shadow: 0 2px 10px rgba(232, 62, 140, 0.15);
                        z-index: -1;
                        transition: all 1s ease;
                    }

                    .step {
                        flex: 1;
                        text-align: center;
                        position: relative;
                        padding: 0 10px;
                    }

                    .step-icon {
                        width: 60px;
                        height: 60px;
                        border-radius: 50%;
                        background: linear-gradient(45deg, #f8f9fa, #e9ecef);
                        color: var(--text-medium);
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        margin: 0 auto 15px;
                        position: relative;
                        z-index: 2;
                        transition: all 0.5s cubic-bezier(0.68, -0.55, 0.27, 1.55);
                        font-weight: bold;
                        font-size: 1.2rem;
                        border: 3px solid white;
                        box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
                    }

                    .step.active .step-icon {
                        background: linear-gradient(135deg, var(--primary-color), var(--primary-light));
                        color: white;
                        transform: scale(1.3) translateY(-8px);
                        box-shadow: 0 15px 25px rgba(232, 62, 140, 0.4);
                        animation: pulse-animation 2s infinite;
                    }

                    @keyframes pulse-animation {
                        0% {
                            box-shadow: 0 0 0 0 rgba(232, 62, 140, 0.7);
                        }

                        70% {
                            box-shadow: 0 0 0 10px rgba(232, 62, 140, 0);
                        }

                        100% {
                            box-shadow: 0 0 0 0 rgba(232, 62, 140, 0);
                        }
                    }

                    .step.completed .step-icon {
                        background: linear-gradient(135deg, var(--secondary-color), var(--secondary-light));
                        color: white;
                        animation: check-animation 0.5s ease-in-out;
                    }

                    @keyframes check-animation {
                        0% {
                            transform: scale(1);
                        }

                        50% {
                            transform: scale(1.2);
                        }

                        100% {
                            transform: scale(1);
                        }
                    }

                    .step-title {
                        font-weight: 600;
                        color: var(--text-medium);
                        transition: all 0.3s ease;
                        font-size: 1.1rem;
                        margin-bottom: 5px;
                    }

                    .step.active .step-title {
                        color: var(--primary-color);
                        font-weight: 700;
                        transform: translateY(-3px);
                    }

                    .step.completed .step-title {
                        color: var(--secondary-color);
                    }

                    .step-description {
                        color: var(--text-light);
                        font-size: 0.85rem;
                        max-width: 120px;
                        margin: 0 auto;
                        transition: all 0.3s ease;
                    }

                    .step.active .step-description {
                        color: var(--primary-dark);
                    }

                    /* Step connector lines with animation */
                    .step-connector {
                        position: absolute;
                        top: 30px;
                        left: 50%;
                        width: 100%;
                        height: 6px;
                        background-color: #e9ecef;
                        z-index: -1;
                    }

                    .step-connector.active {
                        background-color: var(--primary-color);
                        animation: fill-line 1s ease-in-out forwards;
                    }

                    @keyframes fill-line {
                        0% {
                            width: 0%;
                        }

                        100% {
                            width: 100%;
                        }
                    }

                    /* Cart items styling */
                    .cart-img {
                        width: 80px;
                        height: 60px;
                        object-fit: cover;
                        border-radius: var(--border-radius);
                        transition: all 0.3s ease;
                        box-shadow: var(--box-shadow);
                    }

                    .cart-item {
                        transition: all 0.3s ease;
                        border-bottom: 1px solid rgba(232, 62, 140, 0.1);
                        padding-bottom: 15px;
                        margin-bottom: 15px;
                        animation: fadeIn 0.5s ease-in-out;
                    }

                    @keyframes fadeIn {
                        from {
                            opacity: 0;
                            transform: translateY(20px);
                        }

                        to {
                            opacity: 1;
                            transform: translateY(0);
                        }
                    }

                    .cart-item:hover {
                        transform: translateX(5px);
                    }

                    .cart-item:hover .cart-img {
                        transform: scale(1.05);
                        box-shadow: var(--box-shadow-hover);
                    }

                    .cart-item-0 {
                        animation-delay: 0s;
                    }

                    .cart-item-1 {
                        animation-delay: 0.1s;
                    }

                    .cart-item-2 {
                        animation-delay: 0.2s;
                    }

                    .cart-item-3 {
                        animation-delay: 0.3s;
                    }

                    .cart-item-4 {
                        animation-delay: 0.4s;
                    }

                    .cart-item-5 {
                        animation-delay: 0.5s;
                    }

                    .cart-item-6 {
                        animation-delay: 0.6s;
                    }

                    .cart-item-7 {
                        animation-delay: 0.7s;
                    }

                    .cart-item-8 {
                        animation-delay: 0.8s;
                    }

                    .cart-item-9 {
                        animation-delay: 0.9s;
                    }

                    /* Card styling */
                    .card {
                        border-radius: var(--border-radius);
                        border: none;
                        box-shadow: var(--box-shadow);
                        transition: all var(--transition-speed);
                        overflow: hidden;
                        background-color: rgba(255, 255, 255, 0.95);
                        margin-bottom: 25px;
                        animation: card-appear 0.6s ease-out;
                    }

                    @keyframes card-appear {
                        from {
                            opacity: 0;
                            transform: translateY(30px);
                        }

                        to {
                            opacity: 1;
                            transform: translateY(0);
                        }
                    }

                    .card:hover {
                        transform: translateY(-5px);
                        box-shadow: var(--box-shadow-hover);
                    }

                    .card-header {
                        background: linear-gradient(135deg, var(--primary-dark), var(--primary-color));
                        color: white;
                        font-weight: 600;
                        border: none;
                        padding: 15px 20px;
                    }

                    /* Button styling */
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

                    .btn-outline-secondary {
                        background: transparent;
                        border: 2px solid var(--text-medium);
                        color: var(--text-medium);
                        border-radius: var(--border-radius);
                        transition: all var(--transition-speed);
                        font-weight: 600;
                        text-transform: uppercase;
                        letter-spacing: 0.5px;
                    }

                    .btn-outline-secondary:hover {
                        background: var(--text-medium);
                        color: white;
                        transform: translateY(-3px);
                        box-shadow: var(--box-shadow-hover);
                    }

                    .complete-btn {
                        padding: 12px 24px;
                        font-weight: 700;
                        text-transform: uppercase;
                        letter-spacing: 1px;
                        position: relative;
                        overflow: hidden;
                        z-index: 1;
                    }

                    .complete-btn::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: -100%;
                        width: 100%;
                        height: 100%;
                        background: linear-gradient(90deg, rgba(255, 255, 255, 0.2), rgba(255, 255, 255, 0));
                        transform: skewX(-25deg);
                        transition: all 0.75s;
                        z-index: -1;
                    }

                    .complete-btn:hover::before {
                        left: 100%;
                    }

                    /* Payment options */
                    .payment-option {
                        display: block;
                        padding: 15px;
                        border-radius: var(--border-radius);
                        margin-bottom: 15px;
                        cursor: pointer;
                        transition: all 0.3s ease;
                        border: 2px solid transparent;
                        background-color: rgba(255, 255, 255, 0.5);
                        transform-origin: center;
                    }

                    .payment-option:hover {
                        transform: translateY(-3px) scale(1.02);
                        box-shadow: var(--box-shadow);
                        background-color: white;
                    }

                    .payment-option.active {
                        border-color: var(--primary-color);
                        background-color: rgba(232, 62, 140, 0.05);
                        box-shadow: var(--box-shadow);
                        animation: highlight-pulse 2s infinite;
                    }

                    @keyframes highlight-pulse {
                        0% {
                            box-shadow: 0 0 0 0 rgba(232, 62, 140, 0.4);
                        }

                        70% {
                            box-shadow: 0 0 0 8px rgba(232, 62, 140, 0);
                        }

                        100% {
                            box-shadow: 0 0 0 0 rgba(232, 62, 140, 0);
                        }
                    }

                    /* Security badges */
                    .secure-badge {
                        display: flex;
                        align-items: center;
                        margin-bottom: 12px;
                        transition: all 0.3s ease;
                        padding: 8px 12px;
                        border-radius: var(--border-radius);
                        background-color: rgba(255, 255, 255, 0.7);
                        font-size: 0.9rem;
                        color: var(--text-medium);
                    }

                    .secure-badge:hover {
                        transform: translateX(5px);
                        color: var(--primary-color);
                        background-color: white;
                        box-shadow: var(--box-shadow);
                    }

                    .secure-badge i {
                        color: var(--primary-color);
                        margin-right: 10px;
                        font-size: 1.1rem;
                        animation: secure-icon-pulse 2s infinite;
                    }

                    @keyframes secure-icon-pulse {
                        0% {
                            transform: scale(1);
                        }

                        50% {
                            transform: scale(1.2);
                        }

                        100% {
                            transform: scale(1);
                        }
                    }

                    /* Hero title and subtitle */
                    .hero-title {
                        font-weight: 700;
                        font-size: 2.8rem;
                        background: linear-gradient(90deg, #fff, #ffe1ed);
                        -webkit-background-clip: text;
                        -webkit-text-fill-color: transparent;
                        margin-bottom: 1.5rem;
                        text-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
                        animation: title-appear 1s ease-out;
                    }

                    @keyframes title-appear {
                        from {
                            opacity: 0;
                            transform: translateY(-20px);
                        }

                        to {
                            opacity: 1;
                            transform: translateY(0);
                        }
                    }

                    .hero-subtitle {
                        font-size: 1.2rem;
                        color: white;
                        line-height: 1.6;
                        text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
                        animation: subtitle-appear 1s ease-out 0.3s both;
                    }

                    @keyframes subtitle-appear {
                        from {
                            opacity: 0;
                            transform: translateY(20px);
                        }

                        to {
                            opacity: 1;
                            transform: translateY(0);
                        }
                    }

                    /* Animation for elements on scroll */
                    .animate-on-scroll {
                        opacity: 0;
                        transform: translateY(30px);
                        transition: opacity 0.8s ease, transform 0.8s ease;
                    }

                    .animate-on-scroll.in-view {
                        opacity: 1;
                        transform: translateY(0);
                    }

                    /* Match index.jsp styling for consistency */
                    .category-badge {
                        background-color: rgba(232, 62, 140, 0.1);
                        color: var(--primary-color);
                        font-size: 0.8rem;
                        padding: 5px 12px;
                        border-radius: 20px;
                        margin-right: 5px;
                        margin-bottom: 5px;
                        display: inline-block;
                        transition: all 0.3s ease;
                        font-weight: 500;
                    }

                    .category-badge:hover {
                        background-color: var(--primary-color);
                        color: white;
                        transform: translateY(-2px);
                    }

                    .price {
                        font-weight: bold;
                        color: var(--primary-color);
                        font-size: 1.2rem;
                    }

                    /* Fix Categories dropdown hover */
                    .nav-item.dropdown:hover .dropdown-menu {
                        display: block;
                        opacity: 1;
                        visibility: visible;
                        transform: translateY(0);
                    }

                    .nav-item.dropdown .dropdown-menu {
                        display: block;
                        opacity: 0;
                        visibility: hidden;
                        transform: translateY(20px);
                        transition: all 0.3s ease;
                    }

                    /* Better search button */
                    .search-btn {
                        background-color: var(--primary-color);
                        color: white;
                        border: none;
                        padding: 0.5rem 1.5rem;
                        border-radius: var(--border-radius);
                        transition: all 0.3s ease;
                        box-shadow: var(--box-shadow);
                    }

                    .search-btn:hover {
                        background-color: var(--primary-dark);
                        transform: translateY(-2px);
                        box-shadow: var(--box-shadow-hover);
                    }

                    /* Checkout form styling */
                    .checkout-form {
                        background-color: rgba(255, 255, 255, 0.95);
                        border-radius: var(--border-radius);
                        box-shadow: var(--box-shadow);
                        padding: 30px;
                        margin-bottom: 30px;
                        transition: all var(--transition-speed);
                    }

                    .checkout-form:hover {
                        box-shadow: var(--box-shadow-hover);
                    }

                    .order-summary {
                        background-color: rgba(255, 255, 255, 0.95);
                        border-radius: var(--border-radius);
                        box-shadow: var(--box-shadow);
                        padding: 30px;
                        position: sticky;
                        top: 100px;
                        transition: all var(--transition-speed);
                    }

                    .order-summary:hover {
                        box-shadow: var(--box-shadow-hover);
                    }

                    .credit-card-form {
                        background-color: rgba(255, 255, 255, 0.7);
                        border-color: rgba(232, 62, 140, 0.2) !important;
                        transition: all var(--transition-speed);
                    }

                    .credit-card-form:hover {
                        background-color: rgba(255, 255, 255, 0.95);
                        box-shadow: var(--box-shadow);
                    }

                    .credit-card-form .form-control {
                        border-color: rgba(232, 62, 140, 0.2);
                    }

                    .credit-card-form .form-control:focus {
                        border-color: var(--primary-color);
                        box-shadow: var(--box-shadow-focus);
                    }

                    .credit-card-form .input-group-text {
                        background-color: rgba(232, 62, 140, 0.1);
                        color: var(--primary-color);
                        border-color: rgba(232, 62, 140, 0.2);
                    }

                    .payment-card-option {
                        position: relative;
                        margin-bottom: 20px;
                        background-color: white;
                        border-radius: 12px;
                        padding: 20px;
                        cursor: pointer;
                        box-shadow: var(--box-shadow);
                        transition: all 0.3s ease;
                        display: flex;
                        align-items: center;
                    }

                    .payment-card-option:hover {
                        transform: translateY(-5px);
                        box-shadow: var(--box-shadow-hover);
                    }

                    .payment-card-option input[type="radio"] {
                        display: none;
                    }

                    .payment-card-option img {
                        max-width: 40px;
                        max-height: 30px;
                        margin-right: 15px;
                    }

                    .payment-card-option.checked {
                        border: 2px solid var(--primary-color);
                        background-color: rgba(232, 62, 140, 0.05);
                    }

                    .payment-card-option.checked::after {
                        content: '✓';
                        position: absolute;
                        top: 10px;
                        right: 10px;
                        width: 25px;
                        height: 25px;
                        background-color: var(--primary-color);
                        color: white;
                        font-size: 14px;
                        border-radius: 50%;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        font-weight: bold;
                        animation: checkmark-animation 0.5s ease-in-out;
                    }

                    .floating-element:nth-child(1) {
                        top: 2% !important;
                    }

                    @keyframes checkmark-animation {
                        0% {
                            transform: scale(0);
                            opacity: 0;
                        }

                        50% {
                            transform: scale(1.5);
                        }

                        100% {
                            transform: scale(1);
                            opacity: 1;
                        }
                    }

                    .payment-card-text {
                        font-weight: 600;
                    }
                </style>
            </head>

            <body>
                <!-- Navigation Bar -->
                <jsp:include page="/WEB-INF/views/customer/common/navigation.jsp" />

                <!-- Header Section with Animations -->
                <header class="page-header">
                    <div class="floating-element"><i class="fas fa-graduation-cap"></i></div>
                    <div class="floating-element"><i class="fas fa-book"></i></div>
                    <div class="floating-element"><i class="fas fa-credit-card"></i></div>
                    <div class="floating-element"><i class="fas fa-shopping-cart"></i></div>
                    <div class="floating-element"><i class="fas fa-shield-alt"></i></div>
                    <div class="container">
                        <h1 class="hero-title">Complete Your Purchase</h1>
                        <p class="hero-subtitle">Just one step away from accessing your selected courses</p>
                    </div>
                </header>

                <div class="container mb-5">
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger alert-dismissible fade show animate__animated animate__shakeX"
                            role="alert">
                            <i class="fas fa-exclamation-circle me-2"></i> ${error}
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>

                    <!-- Checkout Steps -->
                    <div class="checkout-steps-container animate-on-scroll">
                        <div class="checkout-steps">
                            <div class="step completed">
                                <div class="step-icon">
                                    <i class="fas fa-shopping-cart"></i>
                                </div>
                                <div class="step-title">Cart</div>
                                <div class="step-description">Items selected</div>
                            </div>
                            <div class="step active">
                                <div class="step-icon">
                                    <i class="fas fa-credit-card"></i>
                                </div>
                                <div class="step-title">Checkout</div>
                                <div class="step-description">Payment details</div>
                            </div>
                            <div class="step">
                                <div class="step-icon">
                                    <i class="fas fa-check-circle"></i>
                                </div>
                                <div class="step-title">Confirmation</div>
                                <div class="step-description">Order complete</div>
                            </div>
                        </div>
                    </div>

                    <form action="${pageContext.request.contextPath}/order/checkout" method="post">
                        <!-- Add hidden field for direct checkout -->
                        <c:if test="${isDirectCheckout}">
                            <input type="hidden" name="courseId" value="${course.courseID}">
                        </c:if>

                        <div class="row">
                            <!-- Order Summary -->
                            <div class="col-lg-8">
                                <div class="card mb-4 animate-on-scroll">
                                    <div class="card-header">
                                        <h4 class="mb-0"><i class="fas fa-shopping-basket me-2"></i>Order Summary</h4>
                                    </div>
                                    <div class="card-body">
                                        <!-- Direct checkout - single course -->
                                        <c:if test="${isDirectCheckout}">
                                            <div class="d-flex align-items-center cart-item">
                                                <img src="${pageContext.request.contextPath}/${course.imageUrl}"
                                                    class="cart-img me-3" alt="${course.name}">
                                                <div class="flex-grow-1">
                                                    <h5 class="mb-1">${course.name}</h5>
                                                    <p class="text-muted mb-0">
                                                        <i class="fas fa-chalkboard-teacher me-1"></i>
                                                        <c:forEach var="instructor" items="${course.instructors}"
                                                            varStatus="loop">
                                                            ${instructor.name}<c:if test="${!loop.last}">, </c:if>
                                                        </c:forEach>
                                                    </p>
                                                    <div class="d-flex flex-wrap align-items-center gap-2 mt-2">
                                                        <c:choose>
                                                            <c:when test="${not empty course.categories}">
                                                                <c:forEach var="category" items="${course.categories}">
                                                                    <span
                                                                        class="badge bg-primary">${category.name}</span>
                                                                </c:forEach>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge bg-secondary">Uncategorized</span>
                                                            </c:otherwise>
                                                        </c:choose>

                                                        <span class="badge bg-secondary">${course.level}</span>

                                                        <span class="text-muted d-flex align-items-center">
                                                            <i class="fas fa-clock me-1"></i> ${course.duration}
                                                        </span>
                                                    </div>

                                                </div>
                                                <div class="ms-md-auto mt-2 mt-md-0 text-md-end text-start">
                                                    <h5 class="mb-0 text-primary">
                                                        <fmt:formatNumber value="${course.price}" type="number" />đ
                                                    </h5>
                                                </div>
                                            </div>
                                        </c:if>

                                        <!-- Cart checkout - multiple courses -->
                                        <c:if test="${not isDirectCheckout}">
                                            <c:forEach var="item" items="${sessionScope.cart.selectedItems}"
                                                varStatus="status">
                                                <div
                                                    class="d-flex flex-wrap align-items-start p-3 border rounded cart-item cart-item-${status.index}">
                                                    <!-- Image -->
                                                    <img src="${pageContext.request.contextPath}/${item.course.imageUrl}"
                                                        class="cart-img me-3 mb-2 mb-md-0" alt="${item.course.name}"
                                                        style="width: 100px; height: 70px; object-fit: cover; border-radius: 8px; flex-shrink: 0;">

                                                    <!-- Content box -->
                                                    <div class="me-auto" style="min-width: 200px; max-width: 100%;">
                                                        <h5 class="mb-1 text-break">${item.course.name}</h5>

                                                        <c:if test="${not empty item.course.instructors}">
                                                            <p class="text-muted mb-1 text-break">
                                                                <i class="fas fa-chalkboard-teacher me-1"></i>
                                                                <c:forEach var="instructor"
                                                                    items="${item.course.instructors}"
                                                                    varStatus="status">
                                                                    ${instructor.name}<c:if test="${!status.last}">,
                                                                    </c:if>
                                                                </c:forEach>
                                                            </p>
                                                        </c:if>

                                                        <div class="d-flex flex-wrap align-items-center gap-2 mt-2">
                                                            <c:choose>
                                                                <c:when test="${not empty item.course.categories}">
                                                                    <c:forEach var="category"
                                                                        items="${item.course.categories}">
                                                                        <span
                                                                            class="badge bg-primary text-wrap">${category.name}</span>
                                                                    </c:forEach>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span
                                                                        class="badge bg-secondary text-wrap">Uncategorized</span>
                                                                </c:otherwise>
                                                            </c:choose>

                                                            <span
                                                                class="badge bg-secondary text-wrap">${item.course.level}</span>
                                                            <span
                                                                class="text-muted d-inline-flex align-items-center text-wrap">
                                                                <i class="fas fa-clock me-1"></i>
                                                                ${item.course.duration}
                                                            </span>
                                                        </div>
                                                    </div>

                                                    <!-- Price -->
                                                    <div class="text-md-end text-start mt-2 mt-md-0 order-3 order-md-0 ms-md-auto"
                                                        style="min-width: 120px;">
                                                        <h5 class="mb-0 text-primary">
                                                            <fmt:formatNumber value="${item.price}" type="number" />đ
                                                        </h5>
                                                    </div>
                                                </div>

                                            </c:forEach>
                                        </c:if>
                                    </div>
                                </div>

                                <!-- Payment Methods -->
                                <div id="payment-methods" class="payment-section">
                                    <h4 class="section-title mb-4"><i class="fas fa-credit-card me-2"></i>Payment Method
                                    </h4>

                                    <div class="payment-options">
                                        <label class="payment-card-option checked">
                                            <input type="radio" name="payment-method" value="VNPAY" checked
                                                class="d-none">
                                            <img src="${pageContext.request.contextPath}/assets/imgs/vnpay.png"
                                                alt="VNPAY">
                                            <div>
                                                <div class="payment-card-text">VNPAY</div>
                                                <div class="text-muted small">Fast and secure payment via VNPAY</div>
                                            </div>
                                        </label>
                                    </div>
                                </div>
                            </div>

                            <!-- Order Total -->
                            <div class="col-lg-4">
                                <div class="card order-summary animate-on-scroll">
                                    <div class="card-header">
                                        <h4 class="mb-0"><i class="fas fa-receipt me-2"></i>Order Total</h4>
                                    </div>
                                    <div class="card-body">
                                        <div class="d-flex justify-content-between mb-3">
                                            <c:choose>
                                                <c:when test="${isDirectCheckout}">
                                                    <span>Subtotal (1 item)</span>
                                                    <span>
                                                        <fmt:formatNumber value="${course.price}" type="number" />đ
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span>Subtotal (${sessionScope.cart.selectedItemCount} selected
                                                        items)</span>
                                                    <span>
                                                        <fmt:formatNumber
                                                            value="${sessionScope.cart.selectedTotalPrice}"
                                                            type="number" />đ
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>

                                        <hr>

                                        <div class="d-flex justify-content-between mb-4 fw-bold">
                                            <span>Total</span>
                                            <c:choose>
                                                <c:when test="${isDirectCheckout}">
                                                    <span class="price fs-4">
                                                        <fmt:formatNumber value="${course.price}" type="number" />đ
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="price fs-4">
                                                        <fmt:formatNumber
                                                            value="${sessionScope.cart.selectedTotalPrice}"
                                                            type="number" />đ
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>

                                        <div class="d-grid gap-2">
                                            <button type="submit" class="btn btn-primary btn-lg complete-btn">
                                                <i class="fas fa-lock me-2"></i> Complete Purchase
                                            </button>
                                            <c:choose>
                                                <c:when test="${isDirectCheckout}">
                                                    <a href="${pageContext.request.contextPath}/course/${course.courseID}"
                                                        class="btn btn-outline-secondary">
                                                        <i class="fas fa-arrow-left me-2"></i> Back to Course
                                                    </a>
                                                </c:when>
                                                <c:otherwise>
                                                    <a href="${pageContext.request.contextPath}/cart"
                                                        class="btn btn-outline-secondary">
                                                        <i class="fas fa-arrow-left me-2"></i> Back to Cart
                                                    </a>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>

                                        <div class="mt-4">
                                            <div class="secure-badge">
                                                <i class="fas fa-shield-alt"></i> Secure payment
                                            </div>
                                            <div class="secure-badge">
                                                <i class="fas fa-undo"></i> 30-day money-back guarantee
                                            </div>
                                            <div class="secure-badge">
                                                <i class="fas fa-infinity"></i> Lifetime access to course content
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>

                <!-- Include footer -->
                <jsp:include page="/WEB-INF/views/customer/common/footer.jsp" />

                <!-- Include common scripts -->
                <jsp:include page="/WEB-INF/views/customer/common/scripts.jsp" />
            </body>

            </html>