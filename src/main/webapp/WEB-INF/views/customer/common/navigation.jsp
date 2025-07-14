<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!-- Navigation Bar -->
<style>
    .dropdown-menu.scrollable-dropdown {
        max-height: 300px; /* Chiều cao tối đa */
        overflow-y: auto; /* Thanh cuộn dọc khi cần */
        overflow-x: hidden; /* Ẩn thanh cuộn ngang */
        border-radius: 8px;
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
    }

    /* Tùy chỉnh thanh cuộn webkit */
    .dropdown-menu.scrollable-dropdown::-webkit-scrollbar {
        width: 6px;
    }

    .dropdown-menu.scrollable-dropdown::-webkit-scrollbar-track {
        background: #f8f9fa;
        border-radius: 3px;
    }

    .dropdown-menu.scrollable-dropdown::-webkit-scrollbar-thumb {
        background: #dee2e6;
        border-radius: 3px;
        transition: background 0.3s ease;
    }

    .dropdown-menu.scrollable-dropdown::-webkit-scrollbar-thumb:hover {
        background: #adb5bd;
    }

    /* Tùy chỉnh thanh cuộn Firefox */
    .dropdown-menu.scrollable-dropdown {
        scrollbar-width: thin;
        scrollbar-color: #dee2e6 #f8f9fa;
    }

    /* Hiệu ứng hover cho dropdown items */
    .dropdown-menu.scrollable-dropdown .dropdown-item {
        transition: all 0.3s ease;
        border-radius: 4px;
        margin: 2px 8px;
        padding: 8px 12px;
    }

    .dropdown-menu.scrollable-dropdown .dropdown-item:hover {
        background: linear-gradient(135deg, #e83e8c, #ffb6c1);
        color: white;
        transform: translateX(5px);
    }

    .avatar-customer {
        width: 30px;
        height: 30px;
        border-radius: 50%
    }

    /* Responsive design */
    @media (max-width: 768px) {
        .dropdown-menu.scrollable-dropdown {
            max-height: 250px;
        }
    }

    @media (max-width: 576px) {
        .dropdown-menu.scrollable-dropdown {
            max-height: 200px;
        }
    }

    @media (max-width: 768px) {
        .navbar-collapse .navbar-nav {
            margin-bottom: 0;
        }
        .navbar-collapse.has-category .navbar-nav {
            margin-bottom: 10px;
        }
    }
</style>
<nav class="navbar navbar-expand-lg navbar-light bg-white py-3 sticky-top">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/home">
            <!-- SVG Graduation Cap Logo with soft pink gradient -->
            <svg xmlns="http://www.w3.org/2000/svg" width="50" height="50" viewBox="0 0 24 24" class="me-2">
                <defs>
                    <linearGradient id="grad1" x1="0%" y1="0%" x2="100%" y2="100%">
                        <stop offset="0%" style="stop-color:#e83e8c;stop-opacity:1" />
                        <stop offset="100%" style="stop-color:#ffb6c1;stop-opacity:1" />
                    </linearGradient>
                </defs>
                <path fill="url(#grad1)" d="M12 3L1 9l4 2.18v6L12 21l7-3.82v-6l2-1.09V17h2V9L12 3m6.82 6L12 12.72 5.18 9 12 5.28 18.82 9M17 16l-5 2.72L7 16v-3.73L12 15l5-2.73V16z"/>
            </svg>
            LightHouse
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse ${not empty categories ? 'has-category' : ''}" id="navbarNav">

            <ul class="navbar-nav me-auto">
                <li class="nav-item">
                    <a class="nav-link ${pageContext.request.servletPath eq '/index.jsp' ? 'active' : ''}" href="${pageContext.request.contextPath}/">Home</a>
                </li>
                <c:if test="${not empty categories}">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown">
                            Categories
                        </a>
                        <ul class="dropdown-menu scrollable-dropdown">
                            <!-- Thêm header cho dropdown -->
                            <li><h6 class="dropdown-header">Browse Categories</h6></li>
                            <li><hr class="dropdown-divider"></li>

                            <!-- All categories link -->
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/courses">
                                    <i class="fas fa-th-large me-2"></i>All Categories
                                </a></li>

                            <!-- Individual categories -->
                            <c:forEach var="category" items="${categories}">
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/courses?category=${category.categoryID}">
                                        <i class="fas fa-folder me-2"></i>${category.name}
                                    </a></li>
                                </c:forEach>
                        </ul>
                    </li>
                </c:if>
            </ul>

            <!-- Search Form -->
            <form class="d-flex mx-auto" action="${pageContext.request.contextPath}/courses" method="get">
                <input class="form-control me-2" type="search" name="keyword" placeholder="Search courses..." aria-label="Search">
                    <button class="search-btn" type="submit"><i class="fas fa-search"></i></button>
            </form>

            <!-- User Navigation -->
            <ul class="navbar-nav ms-auto">
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/cart">
                        <i class="fas fa-shopping-cart"></i> Cart
                        <c:if test="${not empty sessionScope.cart and not empty sessionScope.cart.items}">
                            <span class="badge bg-primary position-relative" style="top: -15px">${sessionScope.cart.itemCount}</span>
                        </c:if>
                    </a>
                </li>

                <c:choose>
                    <c:when test="${empty sessionScope.user}">
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/login">Login</a>
                        </li>
                        <li class="nav-item">
                            <a class="btn btn-primary" href="${pageContext.request.contextPath}/register">Register</a>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li class="nav-item dropdown">
                            <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button" data-bs-toggle="dropdown">
                                <c:choose>
                                    <c:when test="${not empty sessionScope.user.avatar}">
                                        <c:choose>
                                            <c:when test="${fn:startsWith(sessionScope.user.avatar, 'https')}">
                                                <img src="${sessionScope.user.avatar}"
                                                     alt="Customer Avatar" class="avatar-customer"
                                                     id="avatarCustomer">
                                                </c:when>
                                                <c:otherwise>
                                                    <img src="${pageContext.request.contextPath}${sessionScope.user.avatar}"
                                                         alt="Customer Avatar" class="avatar-customer"
                                                         id="avatarCustomer">
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="avatar-placeholder"
                                                     id="avatarPlaceholder">
                                                    <i class="fas fa-user"></i>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                        ${sessionScope.user.username}
                                        </a>
                                        <ul class="dropdown-menu dropdown-menu-end">
                                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/profile">My Profile</a></li>
                                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/my-courses">My Courses</a></li>
                                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/order/history">My Orders</a></li>
                                            <li><hr class="dropdown-divider"></li>
                                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">Logout</a></li>
                                        </ul>
                                        </li>
                                    </c:otherwise>
                                </c:choose>
                                </ul>
                                </div>
                                </div>
                                </nav> 
