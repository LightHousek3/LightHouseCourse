<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
                height: 60px; /* Điều chỉnh chiều cao phù hợp với số lượng category có thể hiển thị */
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
            .hero-slideshow{
                position: relative;
                min-height: 600px; /* hoặc 100vh nếu muốn full màn hình */
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
            <div class="slide active" style="background: linear-gradient(135deg, #e83e8c, #fd86b3, #ffc1d5, #ffebf0);">
                <div class="container">
                    <div class="slide-content text-center">
                        <h1 class="hero-title">Illuminate Your Learning Path</h1>
                        <p class="hero-subtitle mb-5">Discover high-quality courses that light up your potential and guide your journey to success.</p>
                        <a href="${pageContext.request.contextPath}/courses" class="btn btn-light btn-lg px-4 me-3">
                            <i class="fas fa-compass me-2"></i> Explore Courses
                        </a>
                        <a href="${pageContext.request.contextPath}/register" class="btn btn-outline-light btn-lg px-4">
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
                        <p class="hero-subtitle mb-5">Our courses are taught by industry professionals who bring real-world experience to the virtual classroom.</p>
                        <a href="${pageContext.request.contextPath}/courses" class="btn btn-dark btn-lg px-4 me-3">
                            <i class="fas fa-chalkboard-teacher me-2"></i> Meet Our Teachers
                        </a>
                        <a href="${pageContext.request.contextPath}/register" class="btn btn-outline-dark btn-lg px-4">
                            <i class="fas fa-user-plus me-2"></i> Start Learning
                        </a>
                    </div>
                </div>
            </div>

            <!-- Slide 3 -->
            <div class="slide" style="background: linear-gradient(45deg, #ff9a9e 0%, #fad0c4 50%, #ffecd2 100%);">
                <div class="container">
                    <div class="slide-content text-center">
                        <h1 class="hero-title">Learn At Your Own Pace</h1>
                        <p class="hero-subtitle mb-5">Access course materials anytime, anywhere. Study on your schedule and achieve your personal goals.</p>
                        <a href="${pageContext.request.contextPath}/courses" class="btn btn-primary btn-lg px-4 me-3">
                            <i class="fas fa-play-circle me-2"></i> Start Now
                        </a>
                        <a href="${pageContext.request.contextPath}/register" class="btn btn-outline-pink btn-lg px-4">
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
                <h2 class="text-center mb-2">Featured Courses</h2>
                <p class="text-center text-muted mb-5">Discover our most popular and highly-rated courses</p>

                <c:if test="${not empty param.registrationSuccess}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        Registration successful! Welcome to LightHouse.
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>

                <c:if test="${not empty param.logoutSuccess}">
                    <div class="alert alert-info alert-dismissible fade show" role="alert">
                        You have been successfully logged out.
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>

                <div class="row row-cols-1 row-cols-md-2 row-cols-lg-4 g-4">
                    <c:forEach var="course" items="${featuredCourses}">
                        <div class="col animate-on-scroll">
                            <div class="card course-card h-100" data-course-id="${course.courseID}">
                                <img src="${pageContext.request.contextPath}/${course.imageUrl}" class="card-img-top" alt="${course.name}">
                                <div class="card-body d-flex flex-column">
                                    <!-- Phần categories với chiều cao cố định -->
                                    <div class="categories-container">
                                        <c:forEach var="category" items="${course.categories}">
                                            <span class="category-badge">${category.name}</span>
                                        </c:forEach>
                                    </div>
                                    <!-- Phần tiêu đề với chiều cao cố định -->
                                    <h5 class="card-title mb-3">${course.name}</h5>
                                    <!-- Phần giá và mức độ - luôn ở dưới cùng -->
                                    <div class="d-flex justify-content-between mt-auto mb-3">
                                        <span class="price">
                                            <fmt:formatNumber value="${course.price}" type="number" />đ
                                        </span>
                                        <span class="text-muted"><i class="fas fa-signal me-1"></i>${course.level}</span>
                                    </div>
                                    <div class="d-grid gap-2">
                                        <a href="${pageContext.request.contextPath}/course/${course.courseID}" class="btn btn-outline-primary">
                                            <i class="fas fa-info-circle me-2"></i>View Details
                                        </a>
                                        <button type="button" class="btn btn-primary add-to-cart-btn" data-course-id="${course.courseID}">
                                            <i class="fas fa-shopping-cart me-2"></i>Add to Cart
                                        </button>
                                        <a href="${pageContext.request.contextPath}/order/checkout?courseId=${course.courseID}" class="btn btn-success">
                                            <i class="fas fa-credit-card me-2"></i>Buy Now
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>

                    <c:if test="${empty featuredCourses}">
                        <div class="col-12 text-center">
                            <p>No courses available at the moment. Please check back later.</p>
                        </div>
                    </c:if>
                </div>

                <div class="text-center mt-5">
                    <a href="${pageContext.request.contextPath}/courses" class="btn btn-primary px-5 py-2">
                        <i class="fas fa-th-list me-2"></i>View All Courses
                    </a>
                </div>
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
                            <a href="${pageContext.request.contextPath}/courses?category=${category.categoryID}" class="text-decoration-none">
                                <div class="card text-center h-100 category-card">
                                    <div class="card-body">
                                        <!-- SVG category icons instead of Font Awesome -->
                                        <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="category-icon mb-3">
                                        <path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/>
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
                <p class="text-center text-muted mb-5">We're dedicated to providing the best online learning experience</p>
                <div class="row g-4">
                    <div class="col-md-4 animate-on-scroll">
                        <div class="card text-center h-100 feature-card">
                            <div class="card-body">
                                <i class="fas fa-laptop-code fa-3x mb-4 feature-icon"></i>
                                <h4>Expert Instructors</h4>
                                <p class="text-muted">Learn from industry professionals with real-world experience who are passionate about sharing their knowledge.</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4 animate-on-scroll">
                        <div class="card text-center h-100 feature-card">
                            <div class="card-body">
                                <i class="fas fa-clock fa-3x mb-4 feature-icon"></i>
                                <h4>Learn at Your Pace</h4>
                                <p class="text-muted">Access course materials anytime and learn on your own schedule. Our platform adapts to your lifestyle.</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4 animate-on-scroll">
                        <div class="card text-center h-100 feature-card">
                            <div class="card-body">
                                <i class="fas fa-certificate fa-3x mb-4 feature-icon"></i>
                                <h4>Provides many important skills</h4>
                                <p class="text-muted">The course provides programming techniques, logical thinking, problem-solving methods, algorithms... These skills will stay with you throughout your studies and future work.</p>
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

        <!-- Add extra script for the add to cart buttons -->
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                const addToCartButtons = document.querySelectorAll('.add-to-cart-btn');
                addToCartButtons.forEach(button => {
                    button.addEventListener('click', function () {
                        const courseId = this.getAttribute('data-course-id');
                        addToCart(courseId);
                    });
                });
            });
        </script>
    </body>
</html> 