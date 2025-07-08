<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Courses - LightHouse</title>
        <!-- Include common header resources -->
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

            .course-card {
                transition: all 0.3s ease;
                height: 100%;
                border: none;
                border-radius: var(--border-radius);
                box-shadow: var(--box-shadow);
                overflow: hidden;
            }
            .course-card:hover {
                transform: translateY(-5px);
                box-shadow: var(--box-shadow-hover);
            }
            .card-img-top {
                height: 180px;
                object-fit: cover;
                transition: transform 0.5s ease;
            }
            .course-card:hover .card-img-top {
                transform: scale(1.05);
            }
            .price {
                font-weight: bold;
                color: var(--primary-color);
                font-size: 1.2rem;
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
            .list-group-item.active {
                background-color: var(--primary-color);
                border-color: var(--primary-color);
            }
            .card-header {
                background: linear-gradient(135deg, var(--primary-dark), var(--primary-color));
                color: white;
                border: none;
            }
            .search-btn {
                background: linear-gradient(135deg, var(--primary-color), var(--primary-light));
                border: none;
                transition: all 0.3s ease;
            }
            .search-btn:hover {
                background: linear-gradient(135deg, var(--primary-dark), var(--primary-color));
                transform: translateY(-2px);
                box-shadow: var(--box-shadow-hover);
            }
            .header-content {
                position: relative;
                z-index: 1;
            }
            .header-title {
                font-weight: 700;
                text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
            }
            .category-sidebar {
                max-height: 400px; /* Chiều cao tối đa */
                overflow-y: auto; /* Thanh cuộn dọc khi cần */
                overflow-x: hidden; /* Ẩn thanh cuộn ngang */
            }

            /* Tùy chỉnh thanh cuộn webkit (Chrome, Safari, Edge) */
            .category-sidebar::-webkit-scrollbar {
                width: 8px;
            }

            .category-sidebar::-webkit-scrollbar-track {
                background: #f1f1f1;
                border-radius: 10px;
            }

            .category-sidebar::-webkit-scrollbar-thumb {
                background: #888;
                border-radius: 10px;
                transition: background 0.3s ease;
            }

            .category-sidebar::-webkit-scrollbar-thumb:hover {
                background: #555;
            }

            /* Tùy chỉnh thanh cuộn Firefox */
            .category-sidebar {
                scrollbar-width: thin;
                scrollbar-color: #888 #f1f1f1;
            }

            /* Responsive design */
            @media (max-width: 768px) {
                .category-sidebar {
                    max-height: 300px;
                }
            }

            @media (max-width: 576px) {
                .category-sidebar {
                    max-height: 250px;
                }
            }
            /* Paging */
            .pagination {
                display: flex;
                justify-content: center;
                align-items: center;
                gap: 2px;
            }

            .pagination li{
                z-index: 10;
            }

            .page-link:focus {
                box-shadow: none !important;
            }

            .page-item.active a {
                background-color: var(--primary-color) !important;
                color: #fff !important;
            }

            .page-item:first-child .page-link, .page-item:last-child .page-link {
                border-radius: 8px !important;
            }

            .active>.page-link, .page-link.active {
                z-index: 3;
                color: var(--bs-pagination-active-color);
                background-color: var(--bs-pagination-active-bg);
                border: none !important;
            }


            /* Buttons */
            .btnp {
                border-radius: 8px;
                width: 50px;
                height: 40px;
                display: flex;
                justify-content: center;
                align-items: center;
                font-weight: 500;
                transition: all 0.3s ease !important;
            }

            .btnp-lg {
                width: 190px;
                height: 40px;
                font-size: 15px !important;
            }

            .btnp-md {
                width: 120px !important;
                height: 40px !important;
                font-size: 15px !important;
            }

            .action-btnp {
                padding: 4px 10px;
                border-radius: 5px;
                font-size: 0.8rem;
            }
            .alert-danger {
                background: none;
                border: none;
                padding: 0;
                color: #dc3545;
                margin-bottom: 0;
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
            <div class="container header-content">
                <h1 class="display-4 header-title">
                    <c:choose>
                        <c:when test="${not empty categoryName}">
                            ${categoryName} Courses
                        </c:when>
                        <c:when test="${not empty keyword && empty errorMessage}">
                            Search Results: "${keyword}"
                        </c:when>
                        <c:when test="${not empty errorMessage}">
                            <div class="alert alert-danger mt-2" role="alert">
                                <i class="fas fa-exclamation-circle me-2"></i>
                                Warning: Your search might have limited results due to invalid keywords.
                            </div>
                        </c:when>
                        <c:otherwise>
                            All Courses
                        </c:otherwise>
                    </c:choose>
                </h1>


                <p class="lead">Illuminate your learning journey with our wide range of courses</p>
            </div>
        </header>

        <div class="container mb-5">
            <div class="row">
                <!-- Category Sidebar -->
                <div class="col-lg-3 mb-4">
                    <div class="card shadow-sm">
                        <div class="card-header">
                            <h5 class="mb-0">Categories</h5>
                        </div>
                        <div class="list-group list-group-flush category-sidebar">
                            <a href="${pageContext.request.contextPath}/courses" class="list-group-item list-group-item-action ${empty categoryId ? 'active' : ''}">
                                All Categories
                            </a>
                            <c:forEach var="category" items="${categories}">
                                <a href="${pageContext.request.contextPath}/courses?category=${category.categoryID}" 
                                   class="list-group-item list-group-item-action ${category.categoryID == categoryId ? 'active' : ''}">
                                    ${category.name}
                                </a>
                            </c:forEach>
                        </div>
                    </div>

                    <!-- Search Form -->
                    <div class="card mt-4 shadow-sm">
                        <div class="card-header">
                            <h5 class="mb-0">Search Courses</h5>
                        </div>
                        <div class="card-body">
                            <form action="${pageContext.request.contextPath}/courses" method="get">
                                <div class="mb-3">
                                    <input type="text" class="form-control" name="keyword" placeholder="Search by keyword..." value="${keyword}">
                                </div>
                                <c:if test="${not empty categoryId}">
                                    <input type="hidden" name="category" value="${categoryId}">
                                </c:if>
                                <c:if test="${not empty errorMessage}">
                                    <div class="alert alert-danger" role="alert">
                                        ${errorMessage}
                                    </div>
                                </c:if>

                                <div class="d-grid">
                                    <button type="submit" class="btn search-btn text-white">
                                        <i class="fas fa-search me-2"></i>Search
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>

                <!-- Course Listing -->
                <div class="col-lg-9">
                    <!-- Course Count -->
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <p class="mb-0">Showing ${courses.size()} of ${totalCourses} courses</p>
                        <form id="sortForm" method="get" action="${pageContext.request.contextPath}/courses" style="display:inline;">
                            <c:if test="${not empty keyword}">
                                <input type="hidden" name="keyword" value="${keyword}" />
                            </c:if>
                            <c:if test="${not empty categoryId}">
                                <input type="hidden" name="category" value="${categoryId}" />
                            </c:if>
                            <label class="me-2">Sort by:</label>
                            <select name="sortParam" class="form-select form-select-sm d-inline-block w-auto" onchange="document.getElementById('sortForm').submit();">
                                <option value="newest" ${sort == 'newest' ? 'selected' : ''}>Newest</option>
                                <option value="price_asc" ${sort == 'price_asc' ? 'selected' : ''}>Price: Low to High</option>
                                <option value="price_desc" ${sort == 'price_desc' ? 'selected' : ''}>Price: High to Low</option>
                                <option value="popularity" ${sort == 'popularity' ? 'selected' : ''}>Popularity</option>
                            </select>
                        </form>
                    </div>

                    <c:if test="${empty courses}">
                        <div class="alert alert-info">
                            <i class="fas fa-info-circle me-2"></i>No courses found. Try a different search or category.
                        </div>
                    </c:if>

                    <!-- Course Grid -->
                    <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
                        <c:forEach var="course" items="${courses}">
                            <div class="col animate-on-scroll">
                                <div class="card course-card h-100">
                                    <img src="${pageContext.request.contextPath}/${course.imageUrl}" class="card-img-top" alt="${course.name}">
                                    <div class="card-body">
                                        <!-- Phần categories với chiều cao cố định -->
                                        <div class="categories-container">
                                            <c:forEach var="category" items="${course.categories}">
                                                <span class="category-badge">${category.name}</span>
                                            </c:forEach>
                                        </div>

                                        <!-- Phần tiêu đề với chiều cao cố định -->
                                        <h5 class="card-title mb-3">${course.name}</h5>

                                        <!-- Phần instructor với chiều cao cố định -->
                                        <div class="instructor-container">
                                            <p class="card-text text-muted mb-0"><i class="fas fa-chalkboard-teacher me-2"></i>
                                                <c:choose>
                                                    <c:when test="${not empty course.instructors}">
                                                        <c:forEach var="inst" items="${course.instructors}" varStatus="status">
                                                            ${inst.name}<c:if test="${!status.last}">, </c:if>
                                                        </c:forEach>
                                                    </c:when>
                                                    <c:otherwise>
                                                        Unknown Instructor
                                                    </c:otherwise>
                                                </c:choose>
                                            </p>
                                        </div>
                                        <!-- Phần giá và mức độ - luôn ở dưới cùng -->
                                        <div class="d-flex justify-content-between mt-auto mb-3">
                                            <span class="price">
                                                <fmt:formatNumber value="${course.price}" type="number" />đ
                                            </span>
                                            <span class="text-muted"><i class="fas fa-signal me-1"></i>${course.level}</span>
                                        </div>

                                        <!-- Phần nút -->
                                        <div class="d-grid gap-2">
                                            <a href="${pageContext.request.contextPath}/course/${course.courseID}" class="btn btn-outline-primary">
                                                <i class="fas fa-info-circle me-2"></i>View Details
                                            </a>
                                            <a href="javascript:void(0);" onclick="addToCart(${course.courseID})" class="btn btn-primary">
                                                <i class="fas fa-shopping-cart me-2"></i>Add to Cart
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>

                    </div>

                    <!-- Pagination -->
                    <c:if test="${totalPages > 1}">
                        <nav aria-label="Page navigation" class="mt-5">
                            <ul class="pagination justify-content-center">
                                <!-- Previous Button -->
                                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                    <a class="page-link btnp btn-outline-primary" href="${pageContext.request.contextPath}/courses?page=${currentPage - 1}${not empty categoryId ? '&category='.concat(categoryId) : ''}${not empty keyword ? '&keyword='.concat(keyword) : ''}">&laquo;</a>
                                </li>

                                <!-- Page Numbers -->
                                <c:forEach begin="1" end="${totalPages}" var="pageNumber">
                                    <li class="page-item ${pageNumber == currentPage ? 'active' : ''}">
                                        <a class="page-link btnp btn-outline-primary" href="${pageContext.request.contextPath}/courses?page=${pageNumber}${not empty categoryId ? '&category='.concat(categoryId) : ''}${not empty keyword ? '&keyword='.concat(keyword) : ''}${not empty sort ? '&sortParam='.concat(sort) : ''}">
                                            ${pageNumber}
                                        </a>
                                    </li>
                                </c:forEach>

                                <!-- Next Button -->
                                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                    <a class="page-link btnp btn-outline-primary" href="${pageContext.request.contextPath}/courses?page=${currentPage + 1}${not empty categoryId ? '&category='.concat(categoryId) : ''}${not empty keyword ? '&keyword='.concat(keyword) : ''}">&raquo;</a>
                                </li>
                            </ul>
                        </nav>
                    </c:if>
                </div>
            </div>
        </div>

        <!-- Include footer -->
        <jsp:include page="/WEB-INF/views/customer/common/footer.jsp" />

        <!-- Script for add to cart functionality -->
        <jsp:include page="/WEB-INF/views/customer/common/scripts.jsp" />
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                // Initialize animations for elements
                const animatedElements = document.querySelectorAll('.animate-on-scroll');
                const checkIfInView = () => {
                    animatedElements.forEach(element => {
                        const elementTop = element.getBoundingClientRect().top;
                        const windowHeight = window.innerHeight;

                        if (elementTop < windowHeight * 0.85) {
                            element.classList.add('in-view');
                        }
                    });
                };

                // Run on page load
                checkIfInView();

                // Run on scroll
                window.addEventListener('scroll', checkIfInView);
            });
        </script>
    </body>
</html> 