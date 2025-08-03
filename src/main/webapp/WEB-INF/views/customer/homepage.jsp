<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="en">

    <head>
        <title>LightHouse - Illuminate Your Learning Path</title>
        <jsp:include page="/WEB-INF/views/customer/common/head.jsp" />
        <style>
            /* Đặt chiều cao cố định cho phần categories */
            .categories-container {
                height: 60px;
                /* Điều chỉnh chiều cao phù hợp với số lượng category có thể hiển thị */
                overflow: hidden;
                margin-bottom: 15px;
            }

            /* Đặt chiều cao cố định cho phần tiêu đề */
            .card-title {
                height: 50px;
                overflow: hidden;
                display: -webkit-box;
                -webkit-line-clamp: 2;
                -webkit-box-orient: vertical;
                text-overflow: ellipsis;
                margin: 0;
                font-size: 1.1rem;
                line-height: 1.4;
            }

            /* Đảm bảo card body có cấu trúc đồng nhất */
            .card-body {
                display: flex;
                flex-direction: column;
                padding: 1.5rem;
            }

            /* Đảm bảo phần instructor luôn có chiều cao cố định */
            .instructor-container {
                height: 24px;
                overflow: hidden;
                margin-bottom: 15px;
            }

            /* Đẩy các phần cuối cùng (price, level) xuống dưới cùng */
            .card-body .mt-auto {
                margin-top: auto !important;
            }

            .category-badge {
                background-color: rgba(232, 62, 140, 0.1);
                color: var(--primary-color);
                font-size: 0.8rem;
                padding: 4px 12px;
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

            .category-card {
                transition: all 0.3s ease;
                border-radius: var(--border-radius);
                overflow: hidden;
                box-shadow: var(--box-shadow);
                border: none;
            }

            .category-card:hover {
                transform: translateY(-5px);
                box-shadow: var(--box-shadow-hover);
            }

            .category-icon {
                color: var(--primary-color);
                transition: all 0.3s ease;
            }

            .category-card:hover .category-icon {
                transform: scale(1.2);
            }

            .feature-icon {
                color: var(--primary-color);
                transition: all 0.3s ease;
            }

            .feature-card:hover .feature-icon {
                transform: rotateY(180deg);
            }

            .feature-card {
                border: none;
                border-radius: var(--border-radius);
                box-shadow: var(--box-shadow);
                transition: all 0.3s ease;
            }

            .feature-card:hover {
                transform: translateY(-5px);
                box-shadow: var(--box-shadow-hover);
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

            /* Enhanced hero section styles */
            .hero-slideshow {
                position: relative;
                min-height: 600px;
                /* hoặc 100vh nếu muốn full màn hình */
                overflow: hidden;
                padding: 60px 0;
                display: flex;
                align-items: center;
            }

            .hero-slideshow .slide-content {
                background-color: rgba(255, 255, 255, 0.15);
                backdrop-filter: blur(8px);
                border-radius: 15px;
                padding: 40px;
                max-width: 650px;
                margin: 0 auto;
                box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
                border: 1px solid rgba(255, 255, 255, 0.2);
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

            /* Floating elements animation */
            .floating-element:nth-child(1) {
                top: 20%;
                left: 8%;
                font-size: 4rem;
            }

            .floating-element:nth-child(2) {
                top: 75%;
                left: 15%;
                font-size: 3.5rem;
            }

            .floating-element:nth-child(3) {
                top: -8%;
                right: 10%;
                font-size: 4rem;
            }

            .floating-element:nth-child(4) {
                top: 65%;
                right: 15%;
                font-size: 3.3rem;
            }

            .floating-element:nth-child(5) {
                top: 87%;
                right: 23%;
                font-size: 3.5rem;
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

            /* Filter options styles */
            .filter-container {
                display: flex;
                align-items: center;
                justify-content: center;
                flex-wrap: wrap;
                margin-top: 15px;
            }

            .filter-label {
                margin-right: 10px;
                font-weight: 500;
                color: #555;
            }

            .search-result-info {
                background-color: #f8f9fa;
                padding: 10px 15px;
                border-radius: 8px;
                margin-bottom: 20px;
                border-left: 4px solid var(--primary-color);
            }

            .search-result-count {
                color: var(--primary-color);
                font-weight: 600;
            }

            .section-title {
                margin-bottom: 0;
            }

            .section-filter {
                margin-bottom: 20px;
            }

            @media (max-width: 768px) {
                .filter-container {
                    justify-content: center;
                    margin-top: 15px;
                    width: 100%;
                }
            }

            /* Animation for newly loaded courses */
            .animate-on-scroll {
                opacity: 0;
                transform: translateY(20px);
                transition: opacity 0.6s ease, transform 0.6s ease;
            }

            .animate-on-scroll.in-view,
            .animate-on-scroll.fade-in {
                opacity: 1;
                transform: translateY(0);
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

            .fade-in {
                animation: fadeIn 0.6s ease forwards;
            }
        </style>
    </head>

    <body>
        <!-- Navigation Bar -->
        <jsp:include page="/WEB-INF/views/customer/common/navigation.jsp" />

        <!-- Hero Section with Slideshow -->
        <div class="hero-slideshow">
            <div class="floating-wrapper">
                <div class="floating-element"><i class="fas fa-graduation-cap"></i></div>
                <div class="floating-element"><i class="fas fa-book"></i></div>
                <div class="floating-element"><i class="fas fa-credit-card"></i></div>
                <div class="floating-element"><i class="fas fa-shopping-cart"></i></div>
                <div class="floating-element"><i class="fas fa-shield-alt"></i></div>
            </div>
            <!-- Slide 1 -->
            <div class="slide active"
                 style="background: linear-gradient(135deg, #e83e8c, #fd86b3, #ffc1d5, #ffebf0);">
                <div class="container">
                    <div class="slide-content text-center">
                        <h1 class="hero-title">Illuminate Your Learning Path</h1>
                        <p class="hero-subtitle mb-5">Discover high-quality courses that light up your potential
                            and guide your journey to success.</p>
                        <a href="${pageContext.request.contextPath}/home?scroll=true"
                           class="btn btn-light btn-lg px-4 me-3">
                            <i class="fas fa-compass me-2"></i> Explore Courses
                        </a>
                        <a href="${pageContext.request.contextPath}/register"
                           class="btn btn-outline-light btn-lg px-4">
                            <i class="fas fa-user-plus me-2"></i> Join Now
                        </a>
                    </div>
                </div>
            </div>

            <!-- Slide 2 -->
            <div class="slide" style="background: linear-gradient(135deg, #7c4dff, #b388ff, #e1bee7, #f8bbd0);">
                <div class="container">
                    <div class="slide-content text-center">
                        <h1 class="hero-title">Learn From Expert Instructors</h1>
                        <p class="hero-subtitle mb-5">Our courses are taught by industry professionals who bring
                            real-world experience to the virtual classroom.</p>
                        <a href="${pageContext.request.contextPath}/home?scroll=true"
                           class="btn btn-dark btn-lg px-4 me-3">
                            <i class="fas fa-chalkboard-teacher me-2"></i> Meet Our Teachers
                        </a>
                        <a href="${pageContext.request.contextPath}/register"
                           class="btn btn-outline-dark btn-lg px-4">
                            <i class="fas fa-user-plus me-2"></i> Start Learning
                        </a>
                    </div>
                </div>
            </div>

            <!-- Slide 3 -->
            <div class="slide"
                 style="background: linear-gradient(45deg, #ff9a9e 0%, #fad0c4 50%, #ffecd2 100%);">
                <div class="container">
                    <div class="slide-content text-center">
                        <h1 class="hero-title">Learn At Your Own Pace</h1>
                        <p class="hero-subtitle mb-5">Access course materials anytime, anywhere. Study on your
                            schedule and achieve your personal goals.</p>
                        <a href="${pageContext.request.contextPath}/home?scroll=true"
                           class="btn btn-primary btn-lg px-4 me-3">
                            <i class="fas fa-play-circle me-2"></i> Start Now
                        </a>
                        <a href="${pageContext.request.contextPath}/register"
                           class="btn btn-outline-pink btn-lg px-4">
                            <i class="fas fa-user-plus me-2"></i> Create Account
                        </a>
                    </div>
                </div>
            </div>

            <!-- Slideshow indicators will be created by the JS -->
        </div>

        <!-- Featured Courses Section -->
        <section class="py-5">
            <div class="container">
                <div class="section-header text-center">
                    <div>
                        <h2 class="section-title">Featured Courses</h2>
                        <p class="text-muted mb-0">Discover our most popular and highly-rated courses</p>
                    </div>
                </div>
                <div class="d-flex justify-content-between align-items-center">
                    <!-- Search Results Info - Only show when search is active -->
                    <div>
                        <c:if test="${isSearchResult}">
                            <div class="search-result-info">
                                <h5 class="mb-0">
                                    <c:choose>
                                        <c:when test="${not empty keyword}">
                                            Search Results for: "${keyword}"
                                        </c:when>
                                        <c:when test="${not empty categoryName}">
                                            Courses in: ${categoryName}
                                        </c:when>
                                        <c:otherwise>
                                            All Courses
                                        </c:otherwise>
                                    </c:choose>
                                </h5>
                                <p class="mb-0">Found <span
                                        class="search-result-count">${totalSearchResults}</span>
                                    courses</p>
                            </div>
                        </c:if>
                    </div>
                    <div class="section-filter">
                        <div class="filter-container">
                            <span class="filter-label">Sort By:</span>
                            <select name="sortParam" class="form-select form-select-sm d-inline-block w-auto"
                                    onchange="location.href = this.value;" id="sort-select">
                                <option
                                    value="${pageContext.request.contextPath}/home?keyword=${keyword}&category=${categoryId}&sortParam=newest&scroll=true"
                                    ${sort=='newest' ? 'selected' : '' }>
                                    Newest
                                </option>
                                <option
                                    value="${pageContext.request.contextPath}/home?keyword=${keyword}&category=${categoryId}&sortParam=price_asc&scroll=true"
                                    ${sort=='price_asc' ? 'selected' : '' }>
                                    Price: Low to High
                                </option>
                                <option
                                    value="${pageContext.request.contextPath}/home?keyword=${keyword}&category=${categoryId}&sortParam=price_desc&scroll=true"
                                    ${sort=='price_desc' ? 'selected' : '' }>
                                    Price: High to Low
                                </option>
                                <option
                                    value="${pageContext.request.contextPath}/home?keyword=${keyword}&category=${categoryId}&sortParam=popularity&scroll=true"
                                    ${sort=='popularity' ? 'selected' : '' }>
                                    Popularity
                                </option>
                            </select>

                        </div>
                    </div>
                </div>

                <c:if test="${not empty param.registrationSuccess}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        Registration successful! Welcome to LightHouse.
                        <button type="button" class="btn-close" data-bs-dismiss="alert"
                                aria-label="Close"></button>
                    </div>
                </c:if>

                <c:if test="${not empty param.logoutSuccess}">
                    <div class="alert alert-info alert-dismissible fade show" role="alert">
                        You have been successfully logged out.
                        <button type="button" class="btn-close" data-bs-dismiss="alert"
                                aria-label="Close"></button>
                    </div>
                </c:if>

                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger alert-dismissible fade show my-3" role="alert">
                        ${errorMessage}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"
                                aria-label="Close"></button>
                    </div>
                </c:if>

                <div class="row row-cols-1 row-cols-md-2 row-cols-lg-4 g-4">
                    <c:choose>
                        <c:when test="${isSearchResult}">
                            <c:forEach var="course" items="${displayCourses}">
                                <div class="col animate-on-scroll">
                                    <div class="card course-card h-100" data-course-id="${course.courseID}">
                                        <img src="${pageContext.request.contextPath}/${course.imageUrl}"
                                             class="card-img-top" alt="${course.name}">
                                        <div class="card-body d-flex flex-column">
                                            <div class="categories-container">
                                                <c:forEach var="category" items="${course.categories}">
                                                    <span class="category-badge">${category.name}</span>
                                                </c:forEach>
                                            </div>
                                            <h5 class="card-title mb-3">${course.name}</h5>
                                            <div class="d-flex justify-content-between mt-auto mb-3">
                                                <span class="price">
                                                    <fmt:formatNumber value="${course.price}" type="number" />đ
                                                </span>
                                                <span class="text-muted"><i
                                                        class="fas fa-signal me-1"></i>${course.level}</span>
                                            </div>
                                            <div class="d-grid gap-2">
                                                <a href="${pageContext.request.contextPath}/course/${course.courseID}"
                                                   class="btn btn-outline-primary">
                                                    <i class="fas fa-info-circle me-2"></i>View Details
                                                </a>
                                                <button type="button" class="btn btn-primary add-to-cart-btn"
                                                        data-course-id="${course.courseID}">
                                                    <i class="fas fa-shopping-cart me-2"></i>Add to Cart
                                                </button>
                                                <a href="${pageContext.request.contextPath}/order/checkout?courseId=${course.courseID}"
                                                   class="btn btn-success">
                                                    <i class="fas fa-credit-card me-2"></i>Buy Now
                                                </a>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>

                            <c:if test="${empty displayCourses}">
                                <div class="d-flex flex-column justify-content-center align-items-center w-100">
                                    <p>No courses found matching your search criteria. Please try different
                                        keywords or filters.</p>
                                    <a href="${pageContext.request.contextPath}/home?scroll=true"
                                       class="btn btn-md btn-primary">Reset search params</a>
                                </div>
                            </c:if>

                            <c:if test="${totalSearchResults > 8}">
                                <div class="col-12 text-center mt-4">
                                    <a href="${pageContext.request.contextPath}/home?keyword=${keyword}&category=${categoryId}&sortParam=${sort}"
                                       class="btn btn-primary px-5 py-2">
                                        <i class="fas fa-th-list me-2"></i>View All Search Results
                                    </a>
                                </div>
                            </c:if>
                        </c:when>
                        <c:otherwise>
                            <c:choose>
                                <c:when test="${not empty displayCourses}">
                                    <c:forEach var="course" items="${displayCourses}">
                                        <div class="col animate-on-scroll">
                                            <div class="card course-card h-100"
                                                 data-course-id="${course.courseID}">
                                                <img src="${pageContext.request.contextPath}/${course.imageUrl}"
                                                     class="card-img-top" alt="${course.name}">
                                                <div class="card-body d-flex flex-column">
                                                    <div class="categories-container">
                                                        <c:forEach var="category" items="${course.categories}">
                                                            <span class="category-badge">${category.name}</span>
                                                        </c:forEach>
                                                    </div>
                                                    <h5 class="card-title mb-3">${course.name}</h5>
                                                    <div class="d-flex justify-content-between mt-auto mb-3">
                                                        <span class="price">
                                                            <fmt:formatNumber value="${course.price}"
                                                                              type="number" />đ
                                                        </span>
                                                        <span class="text-muted"><i
                                                                class="fas fa-signal me-1"></i>${course.level}</span>
                                                    </div>
                                                    <div class="d-grid gap-2">
                                                        <a href="${pageContext.request.contextPath}/course/${course.courseID}"
                                                           class="btn btn-outline-primary">
                                                            <i class="fas fa-info-circle me-2"></i>View Details
                                                        </a>
                                                        <button type="button"
                                                                class="btn btn-primary add-to-cart-btn"
                                                                data-course-id="${course.courseID}">
                                                            <i class="fas fa-shopping-cart me-2"></i>Add to Cart
                                                        </button>
                                                        <a href="${pageContext.request.contextPath}/order/checkout?courseId=${course.courseID}"
                                                           class="btn btn-success">
                                                            <i class="fas fa-credit-card me-2"></i>Buy Now
                                                        </a>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <div class="col-12 text-center">
                                        <p>No courses available at the moment. Please check back later.</p>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </c:otherwise>
                    </c:choose>
                </div>

                <c:if test="${hasMoreCourses}">
                    <div class="text-center mt-5">
                        <button id="loadMoreBtn" class="btn btn-primary px-5 py-2" data-page="1"
                                data-keyword="${keyword}" data-category="${categoryId}" data-sort="${sort}">
                            <i class="fas fa-th-list me-2"></i>View More Courses
                        </button>
                    </div>
                </c:if>
            </div>
        </section>

        <!-- Categories Section -->
        <section class="py-5 bg-light">
            <div class="container">
                <h2 class="text-center mb-2">Browse by Category</h2>
                <p class="text-center text-muted mb-5">Find the perfect course in your area of interest</p>
                <div class="row justify-content-center g-4">
                    <c:forEach var="category" items="${categories}">
                        <div class="col-6 col-md-4 col-lg-2 animate-on-scroll">
                            <a href="${pageContext.request.contextPath}/home?category=${category.categoryID}&scroll=true"
                               class="text-decoration-none">
                                <div class="card text-center h-100 category-card">
                                    <div class="card-body">
                                        <!-- SVG category icons instead of Font Awesome -->
                                        <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48"
                                             viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                             stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                                             class="category-icon mb-3">
                                        <path
                                            d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z" />
                                        </svg>
                                        <h5 class="card-title">${category.name}</h5>
                                    </div>
                                </div>
                            </a>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </section>

        <!-- Features Section -->
        <section class="py-5">
            <div class="container">
                <h2 class="text-center mb-2">Why Choose LightHouse?</h2>
                <p class="text-center text-muted mb-5">We're dedicated to providing the best online learning
                    experience</p>
                <div class="row g-4">
                    <div class="col-md-4 animate-on-scroll">
                        <div class="card text-center h-100 feature-card">
                            <div class="card-body">
                                <i class="fas fa-laptop-code fa-3x mb-4 feature-icon"></i>
                                <h4>Expert Instructors</h4>
                                <p class="text-muted">Learn from industry professionals with real-world
                                    experience who are passionate about sharing their knowledge.</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4 animate-on-scroll">
                        <div class="card text-center h-100 feature-card">
                            <div class="card-body">
                                <i class="fas fa-clock fa-3x mb-4 feature-icon"></i>
                                <h4>Learn at Your Pace</h4>
                                <p class="text-muted">Access course materials anytime and learn on your own
                                    schedule. Our platform adapts to your lifestyle.</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4 animate-on-scroll">
                        <div class="card text-center h-100 feature-card">
                            <div class="card-body">
                                <i class="fas fa-certificate fa-3x mb-4 feature-icon"></i>
                                <h4>Provides many important skills</h4>
                                <p class="text-muted">The course provides programming techniques, logical
                                    thinking, problem-solving methods, algorithms... These skills will stay with
                                    you throughout your studies and future work.</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- Footer -->
        <footer class="py-5">
            <jsp:include page="/WEB-INF/views/customer/common/footer.jsp" />
        </footer>

        <!-- Include common scripts -->
        <jsp:include page="/WEB-INF/views/customer/common/scripts.jsp" />

        <!-- Add extra script for the add to cart buttons and auto-scrolling -->
        <script>
            // Function to handle adding courses to cart has been moved to script.js

            document.addEventListener('DOMContentLoaded', function () {
                const addToCartButtons = document.querySelectorAll('.add-to-cart-btn');
                addToCartButtons.forEach(button => {
                    button.addEventListener('click', function () {
                        const courseId = this.getAttribute('data-course-id');
                        addToCart(courseId);
                    });
                });

                // Add in-view class to all existing course elements
                const existingCourseElements = document.querySelectorAll('.animate-on-scroll');
                existingCourseElements.forEach((element, index) => {
                    setTimeout(() => {
                        element.classList.add('in-view');
                    }, index * 100); // Stagger the animation
                });

                // Auto-scroll to section header if search or filter is active
                const isSearchResult = <c:out value="${isSearchResult != null && isSearchResult ? 'true' : 'false'}" />;
                const hasKeyword = <c:out value="${not empty keyword ? 'true' : 'false'}" />;
                const hasCategory = <c:out value="${not empty categoryId && categoryId > 0 ? 'true' : 'false'}" />;

                if (isSearchResult || hasKeyword || hasCategory) {
                    setTimeout(function () {
                        const sectionHeader = document.querySelector('.section-header');
                        if (sectionHeader) {
                            // Get the navbar height to offset the scroll position
                            const navbar = document.querySelector('.navbar');
                            const navbarHeight = navbar ? navbar.offsetHeight : 0;

                            // Scroll to section header with offset for navbar
                            window.scrollTo({
                                top: sectionHeader.offsetTop - navbarHeight - 90, // Additional 90px padding
                                behavior: 'smooth'
                            });
                        }
                    }, 300); // Small delay to ensure everything is loaded
                }

                // Load more courses functionality
                const loadMoreBtn = document.getElementById('loadMoreBtn');
                if (loadMoreBtn) {
                    loadMoreBtn.addEventListener('click', function () {
                        const btn = this;
                        const page = parseInt(btn.getAttribute('data-page'));
                        const keyword = btn.getAttribute('data-keyword') || '';
                        const categoryId = btn.getAttribute('data-category') || '';
                        const sortParam = btn.getAttribute('data-sort') || 'newest';

                        // Show loading state
                        btn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Loading...';
                        btn.disabled = true;

                        // Build the URL with parameters
                        let url = '${pageContext.request.contextPath}/home?ajax=true&page=' + page;
                        if (keyword)
                            url += '&keyword=' + encodeURIComponent(keyword);
                        if (categoryId)
                            url += '&category=' + categoryId;
                        if (sortParam)
                            url += '&sortParam=' + sortParam;

                        // Make AJAX request
                        fetch(url)
                                .then(response => {
                                    if (!response.ok) {
                                        throw new Error('Network response was not ok');
                                    }
                                    return response.json();
                                })
                                .then(data => {
                                    // Process the response
                                    if (data.courses && data.courses.length > 0) {
                                        // Get the container to append new courses
                                        const courseContainer = document.querySelector('.row.row-cols-1.row-cols-md-2.row-cols-lg-4.g-4');

                                        // Append each course
                                        data.courses.forEach(course => {
                                            const courseElement = createCourseElement(course);
                                            courseContainer.appendChild(courseElement);

                                            // Trigger animation for new elements
                                            setTimeout(() => {
                                                courseElement.classList.add('fade-in');
                                            }, 100);
                                        });

                                        // Update button for next page or hide if no more courses
                                        if (data.hasMore) {
                                            btn.setAttribute('data-page', data.nextPage);
                                            btn.innerHTML = '<i class="fas fa-th-list me-2"></i>View More Courses';
                                            btn.disabled = false;
                                        } else {
                                            // No more courses, hide the button
                                            btn.parentNode.style.display = 'none';
                                        }
                                    } else {
                                        // No courses returned, hide the button
                                        btn.parentNode.style.display = 'none';
                                    }
                                })
                                .catch(error => {
                                    console.error('Error loading more courses:', error);
                                    btn.innerHTML = '<i class="fas fa-exclamation-circle me-2"></i>Error loading courses';
                                    setTimeout(() => {
                                        btn.innerHTML = '<i class="fas fa-th-list me-2"></i>View More Courses';
                                        btn.disabled = false;
                                    }, 2000);
                                });
                    });
                }

                // Function to create a course element from course data
                function createCourseElement(course) {
                    const colDiv = document.createElement('div');
                    colDiv.className = 'col animate-on-scroll in-view';

                    const cardDiv = document.createElement('div');
                    cardDiv.className = 'card course-card h-100';
                    cardDiv.setAttribute('data-course-id', course.courseID);

                    // Create image
                    const img = document.createElement('img');
                    img.className = 'card-img-top';
                    img.src = '${pageContext.request.contextPath}/' + course.imageUrl;
                    img.alt = course.name;

                    // Create card body
                    const cardBody = document.createElement('div');
                    cardBody.className = 'card-body d-flex flex-column';

                    // Create categories container
                    const categoriesContainer = document.createElement('div');
                    categoriesContainer.className = 'categories-container';

                    // Add categories if available
                    if (course.categories && course.categories.length > 0) {
                        course.categories.forEach(category => {
                            const categorySpan = document.createElement('span');
                            categorySpan.className = 'category-badge';
                            categorySpan.textContent = category.name;
                            categoriesContainer.appendChild(categorySpan);
                        });
                    }

                    // Create title
                    const title = document.createElement('h5');
                    title.className = 'card-title mb-3';
                    title.textContent = course.name;

                    // Create price and level container
                    const priceLevel = document.createElement('div');
                    priceLevel.className = 'd-flex justify-content-between mt-auto mb-3';

                    // Create price
                    const price = document.createElement('span');
                    price.className = 'price';
                    price.textContent = new Intl.NumberFormat('vi-VN').format(course.price) + 'đ';

                    // Create level
                    const level = document.createElement('span');
                    level.className = 'text-muted';
                    level.innerHTML = '<i class="fas fa-signal me-1"></i>' + course.level;

                    // Add price and level to container
                    priceLevel.appendChild(price);
                    priceLevel.appendChild(level);

                    // Create buttons container
                    const buttonsDiv = document.createElement('div');
                    buttonsDiv.className = 'd-grid gap-2';

                    // Create View Details button
                    const viewDetailsBtn = document.createElement('a');
                    viewDetailsBtn.className = 'btn btn-outline-primary';
                    viewDetailsBtn.href = '${pageContext.request.contextPath}/course/' + course.courseID;
                    viewDetailsBtn.innerHTML = '<i class="fas fa-info-circle me-2"></i>View Details';

                    // Create Add to Cart button
                    const addToCartBtn = document.createElement('button');
                    addToCartBtn.className = 'btn btn-primary add-to-cart-btn';
                    addToCartBtn.setAttribute('data-course-id', course.courseID);
                    addToCartBtn.innerHTML = '<i class="fas fa-shopping-cart me-2"></i>Add to Cart';

                    // Add click event directly instead of using addEventListener
                    addToCartBtn.onclick = function () {
                        const courseId = this.getAttribute('data-course-id');
                        addToCart(courseId); // Call the function from script.js
                    };

                    // Create Buy Now button
                    const buyNowBtn = document.createElement('a');
                    buyNowBtn.className = 'btn btn-success';
                    buyNowBtn.href = '${pageContext.request.contextPath}/order/checkout?courseId=' + course.courseID;
                    buyNowBtn.innerHTML = '<i class="fas fa-credit-card me-2"></i>Buy Now';

                    // Add buttons to container
                    buttonsDiv.appendChild(viewDetailsBtn);
                    buttonsDiv.appendChild(addToCartBtn);
                    buttonsDiv.appendChild(buyNowBtn);

                    // Add all elements to card body
                    cardBody.appendChild(categoriesContainer);
                    cardBody.appendChild(title);
                    cardBody.appendChild(priceLevel);
                    cardBody.appendChild(buttonsDiv);

                    // Add image and card body to card
                    cardDiv.appendChild(img);
                    cardDiv.appendChild(cardBody);

                    // Add card to column
                    colDiv.appendChild(cardDiv);

                    return colDiv;
                }
            });
        </script>
    </body>

</html>