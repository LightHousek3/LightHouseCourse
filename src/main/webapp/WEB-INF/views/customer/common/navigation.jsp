<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!-- Navigation Bar -->
<style>
    .dropdown-menu.scrollable-dropdown {
        max-height: 300px;
        overflow-y: auto;
        overflow-x: hidden;
        border-radius: 8px;
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
    }

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

    .dropdown-menu.scrollable-dropdown {
        scrollbar-width: thin;
        scrollbar-color: #dee2e6 #f8f9fa;
    }

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

    .dropdown-menu.scrollable-dropdown {
        max-height: 300px;
        overflow-y: auto;
        overflow-x: hidden;
        border-radius: 8px;
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
    }

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

    .dropdown-menu.scrollable-dropdown {
        scrollbar-width: thin;
        scrollbar-color: #dee2e6 #f8f9fa;
    }

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

    .nav-link::after {
        background-color: #fff;
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

    .navbar {
        position: sticky;
        top: 0;
        z-index: 1050;
    }

    .category-nav {
        background-color: #282A35;
        white-space: nowrap;
        position: sticky;
        top: 92px;
        z-index: 1040;
    }

    .category-container {
        overflow: visible !important;
    }


    .category-nav .category-list {
        display: flex;
        margin: 0;
        padding: 0;
        list-style: none;
        overflow-x: auto;
        scrollbar-width: none;
        -ms-overflow-style: none;
    }

    .category-nav .category-list::-webkit-scrollbar {
        display: none;
    }

    .category-nav .category-item {
        flex-shrink: 0;
    }

    .category-nav .category-link {
        display: inline-block;
        padding: 10px 16px;
        color: white;
        text-decoration: none;
        font-size: 14px;
    }

    .category-nav .category-link:hover {
        background-color: #FF69B4;
        color: white;
    }

    .category-nav .category-link.active {
        background-color: #e83e8c;
        color: white;
    }

    .scroll-button {
        background-color: #222;
        color: #fff;
        border: none;
        width: 30px;
        height: 100%;
        cursor: pointer;
        font-size: 20px;
        opacity: 0.7;
        transition: opacity 0.3s;
        z-index: 10;
    }

    .scroll-button:hover {
        opacity: 1;
    }

    .scroll-button.left {
        position: absolute;
        left: 0;
        top: 0;
    }

    .scroll-button.right {
        position: absolute;
        right: 0;
        top: 0;
    }

    /* Fade edges */
    .category-nav::before,
    .category-nav::after {
        content: '';
        position: absolute;
        top: 0;
        width: 50px;
        height: 100%;
        pointer-events: none;
        z-index: 5;
    }

    .category-nav::before {
        left: 0;
    }

    .category-nav::after {
        right: 0;
    }

    .user-mobile-menu {
        display: none;
        padding-left: 0;
    }

    /* Mobile responsive */
    @media (max-width: 768px) {
        .category-nav .category-link {
            width: 100%;
            border-top: 1px solid #444;
        }
    }

    @media (max-width: 768px) {
        .dropdown-menu {
            display: none !important;
        }

        .user-mobile-menu {
            display: block;
            margin-top: 8px;
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
                <path fill="url(#grad1)"
                      d="M12 3L1 9l4 2.18v6L12 21l7-3.82v-6l2-1.09V17h2V9L12 3m6.82 6L12 12.72 5.18 9 12 5.28 18.82 9M17 16l-5 2.72L7 16v-3.73L12 15l5-2.73V16z" />
            </svg>
            LightHouse
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse ${not empty categories ? 'has-category' : ''}" id="navbarNav">

            <ul class="navbar-nav me-auto">
                <li class="nav-item">
                    <a class="nav-link ${pageContext.request.servletPath eq '/index.jsp' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/">Home</a>
                </li>
            </ul>

            <!-- Search Form -->
            <form class="d-flex mx-auto" action="${pageContext.request.contextPath}/home" method="get">
                <input class="form-control me-2" type="search" name="keyword"
                       placeholder="Search courses..." aria-label="Search">
                    <input type="hidden" name="scroll" value="true">
                        <button class="search-btn" type="submit"><i class="fas fa-search"></i></button>
                        </form>

                        <!-- User Navigation -->
                        <ul class="navbar-nav ms-auto">
                            <li class="nav-item">
                                <a class="nav-link" href="${pageContext.request.contextPath}/cart">
                                    <i class="fas fa-shopping-cart"></i> Cart
                                    <span class="badge bg-primary position-relative"
                                          style="top: -15px">${sessionScope.cart.itemCount != null ?
                                                         sessionScope.cart.itemCount : 0} </span>
                                </a>
                            </li>

                            <c:choose>
                                <c:when test="${empty sessionScope.user}">
                                    <li class="nav-item">
                                        <a class="nav-link" href="${pageContext.request.contextPath}/login">Login</a>
                                    </li>
                                    <li class="nav-item">
                                        <a class="btn btn-primary"
                                           href="${pageContext.request.contextPath}/register">Register</a>
                                    </li>
                                </c:when>
                                <c:otherwise>
                                    <li class="nav-item dropdown">
                                        <a class="nav-link d-flex align-items-center gap-2" href="#" id="userDropdown"
                                           role="button" data-bs-toggle="dropdown">
                                            <c:choose>
                                                <c:when test="${not empty sessionScope.user.avatar}">
                                                    <c:choose>
                                                        <c:when
                                                            test="${fn:startsWith(sessionScope.user.avatar, '/assets')}">
                                                            <img src="${pageContext.request.contextPath}${sessionScope.user.avatar}"
                                                                 alt="Customer Avatar" class="avatar-customer"
                                                                 id="avatarCustomer" referrerpolicy="no-referrer">
                                                            </c:when>
                                                            <c:otherwise>
                                                                <img src="${sessionScope.user.avatar}" alt="Customer Avatar"
                                                                     class="avatar-customer" id="avatarCustomer" referrerpolicy="no-referrer">
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="avatar-placeholder" id="avatarPlaceholder">
                                                                <i class="fas fa-user"></i>
                                                            </div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                    ${sessionScope.user.username}
                                                    </a>

                                                    <!-- Desktop Dropdown -->
                                                    <ul class="dropdown-menu">
                                                        <li><a class="dropdown-item"
                                                               href="${pageContext.request.contextPath}/profile">My Profile</a>
                                                        </li>
                                                        <li><a class="dropdown-item"
                                                               href="${pageContext.request.contextPath}/my-courses">My Courses</a>
                                                        </li>
                                                        <li><a class="dropdown-item"
                                                               href="${pageContext.request.contextPath}/order/history">My
                                                                Orders</a></li>
                                                        <li>
                                                            <hr class="dropdown-divider">
                                                        </li>
                                                        <li><a class="dropdown-item text-danger"
                                                               href="${pageContext.request.contextPath}/logout">Logout</a></li>
                                                    </ul>

                                                    <!-- Mobile List -->
                                                    <ul class="user-mobile-menu list-unstyled">
                                                        <li><a class="nav-link" href="${pageContext.request.contextPath}/profile">My
                                                                Profile</a></li>
                                                        <li><a class="nav-link"
                                                               href="${pageContext.request.contextPath}/my-courses">My Courses</a>
                                                        </li>
                                                        <li><a class="nav-link"
                                                               href="${pageContext.request.contextPath}/order/history">My
                                                                Orders</a></li>
                                                        <li><a class="nav-link text-danger"
                                                               href="${pageContext.request.contextPath}/logout">Logout</a></li>
                                                    </ul>
                                                    </li>
                                                </c:otherwise>

                                            </c:choose>
                                            </ul>
                                            </div>
                                            </div>
                                            </nav>
                                            <c:if test="${not empty categories}">
                                                <div class="category-nav">
                                                    <div>
                                                        <div class="category-container position-relative">
                                                            <button class="scroll-button left" onclick="scrollCategories('left')" id="scrollLeft">
                                                                <i class="fas fa-chevron-left"></i>
                                                            </button>
                                                            <ul class="category-list" id="categoryList">
                                                                <li class="category-item">
                                                                    <a class="category-link ${empty param.category ? 'active' : ''}"
                                                                       href="${pageContext.request.contextPath}/home?scroll=true">
                                                                        <i class="fas fa-th-large me-2"></i>All Categories
                                                                    </a>
                                                                </li>
                                                                <c:forEach var="category" items="${categories}">
                                                                    <li class="category-item">
                                                                        <a class="category-link ${param.category eq category.categoryID ? 'active' : ''}"
                                                                           href="${pageContext.request.contextPath}/home?category=${category.categoryID}&scroll=true">
                                                                            <i class="fas fa-folder me-2"></i>${category.name}
                                                                        </a>
                                                                    </li>
                                                                </c:forEach>
                                                            </ul>
                                                            <button class="scroll-button right" onclick="scrollCategories('right')" id="scrollRight">
                                                                <i class="fas fa-chevron-right"></i>
                                                            </button>
                                                        </div>
                                                    </div>
                                                </div>
                                            </c:if>
                                            <script>
                                                function scrollCategories(direction) {
                                                    const list = document.getElementById('categoryList');
                                                    const scrollAmount = 150;

                                                    if (direction === 'left') {
                                                        list.scrollBy({left: -scrollAmount, behavior: 'smooth'});
                                                    } else if (direction === 'right') {
                                                        list.scrollBy({left: scrollAmount, behavior: 'smooth'});
                                                    }
                                                    setTimeout(checkScrollButtons, 300);
                                                }
                                                function checkScrollButtons() {
                                                    const list = document.getElementById('categoryList');
                                                    const scrollLeft = list.scrollLeft;
                                                    const maxScrollLeft = list.scrollWidth - list.clientWidth;

                                                    const scrollLeftButton = document.getElementById('scrollLeft');
                                                    const scrollRightButton = document.getElementById('scrollRight');

                                                    if (scrollLeft <= 0) {
                                                        scrollLeftButton.style.display = 'none';
                                                    } else {
                                                        scrollLeftButton.style.display = 'block';
                                                    }

                                                    if (scrollLeft >= maxScrollLeft) {
                                                        scrollRightButton.style.display = 'none';
                                                    } else {
                                                        scrollRightButton.style.display = 'block';
                                                    }
                                                }
                                                window.addEventListener('load', checkScrollButtons);
                                                window.addEventListener('resize', checkScrollButtons);
                                            </script>