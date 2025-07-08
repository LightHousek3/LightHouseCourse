<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="en">

    <head>
        <title>${course.name} - LightHouse</title>
        <!-- Include common head with styles -->
        <jsp:include page="/WEB-INF/views/customer/common/head.jsp" />
        <style>
            .course-header {
                background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
                color: white;
                padding: 60px 0;
            }

            .course-instructor {
                display: flex;
                align-items: center;
                margin: 20px 0;
            }

            .instructor-img {
                width: 60px;
                height: 60px;
                border-radius: 50%;
                margin-right: 15px;
                object-fit: cover;
                box-shadow: var(--box-shadow);
            }

            .price-tag {
                font-size: 2rem;
                font-weight: bold;
                color: var(--primary-color);
            }

            .course-feature {
                display: flex;
                align-items: center;
                margin-bottom: 15px;
                color: darkblue;
            }

            .course-feature i {
                width: 30px;
                color: var(--primary-color);
                font-size: 1.2rem;
            }

            .course-img {
                width: 100%;
                border-radius: var(--border-radius);
                box-shadow: var(--box-shadow);
                transition: all var(--transition-speed);
            }

            .category-badge {
                background-color: rgba(232, 62, 140, 0.1);
                font-size: 0.9rem;
                padding: 5px 15px;
                border-radius: 20px;
                margin-right: 10px;
                margin-bottom: 10px;
                display: inline-block;
                transition: all 0.3s ease;
                font-weight: 500;
            }

            .category-badge:hover {
                background-color: var(--primary-color);
                color: white;
                transform: translateY(-2px);
            }

            .course-section {
                padding: 30px 0;
            }

            .enrolled-badge {
                background-color: var(--success);
                color: white;
                padding: 10px 20px;
                border-radius: var(--border-radius);
                display: inline-block;
                margin-bottom: 20px;
            }

            .card-header {
                background-color: var(--primary-color);
                color: white;
                font-weight: 600;
            }

            /* Rating styles */
            .rating-section {
                margin-top: 2rem;
            }

            .rating-header {
                display: flex;
                align-items: center;
                justify-content: space-between;
                margin-bottom: 1.5rem;
            }

            .overall-rating {
                display: flex;
                align-items: center;
            }

            .rating-stars {
                color: #ffc107;
                /* Star color */
                font-size: 1.2rem;
                margin-right: 0.5rem;
            }

            .empty-star {
                color: #e4e5e9;
            }

            .rating-count {
                color: #6c757d;
                font-size: 0.9rem;
            }

            .rating-form {
                background: #f8f9fa;
                border-radius: 10px;
                padding: 1.5rem;
                margin-bottom: 2rem;
            }

            .star-rating {
                direction: rtl;
                display: inline-block;
                padding: 0;
            }

            .star-rating input[type="radio"] {
                display: none;
            }

            .star-rating label {
                color: #e4e5e9;
                font-size: 1.8rem;
                cursor: pointer;
                padding: 0 0.1rem;
                transition: all 0.3s ease;
            }

            .star-rating label:hover,
            .star-rating label:hover~label,
            .star-rating input[type="radio"]:checked~label {
                color: #ffc107;
            }

            .rating-card {
                border: 1px solid #e9ecef;
                border-radius: 10px;
                margin-bottom: 1.5rem;
                overflow: hidden;
            }

            .rating-user {
                display: flex;
                align-items: center;
                margin-bottom: 0.5rem;
            }

            .rating-avatar {
                width: 40px;
                height: 40px;
                border-radius: 50%;
                background-color: #e9ecef;
                display: flex;
                align-items: center;
                justify-content: center;
                margin-right: 10px;
                font-weight: bold;
                font-size: 1.2rem;
                color: #6c757d;
            }

            .rating-user-details {
                font-size: 0.9rem;
            }

            .rating-user-name {
                font-weight: 600;
                margin-bottom: 0;
            }

            .rating-date {
                color: #6c757d;
                font-size: 0.8rem;
            }

            .rating-content {
                padding: 1rem;
            }

            .rating-comment {
                margin-top: 0.5rem;
                white-space: pre-line;
            }

            .rating-actions {
                text-align: right;
                margin-top: 1rem;
            }

            .rating-actions .btn {
                padding: 0.25rem 0.5rem;
                font-size: 0.8rem;
            }

            .user-rating-prompt {
                background-color: #f8f9fa;
                padding: 1.5rem;
                border-radius: 10px;
                text-align: center;
                margin-bottom: 1.5rem;
            }
        </style>
    </head>

    <body>
        <!-- Include navigation -->
        <jsp:include page="/WEB-INF/views/customer/common/navigation.jsp" />

        <!-- Course Header -->
        <header class="course-header">
            <div class="container">
                <div class="row align-items-center">
                    <div class="col-lg-8">
                        <h1 class="display-4 mb-3">${course.name}</h1>
                        <div class="mb-4">
                            <c:if test="${not empty course.categories}">
                                <c:forEach var="category" items="${course.categories}">
                                    <span class="category-badge">${category.name}</span>
                                </c:forEach>
                            </c:if>
                        </div>
                        <p class="lead">${course.description}</p>

                        <div class="course-instructor">
                            <img src="${pageContext.request.contextPath}/assets/images/default-instructor.jpg"
                                 class="instructor-img" alt="Instructor">
                            <div>
                                <small>Expert Educator</small>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-4">
                        <div class="card">
                            <c:choose>
                                <c:when test="${not empty course.imageUrl}">
                                    <img src="${pageContext.request.contextPath}/${course.imageUrl}" class="card-img-top course-img" alt="${course.name}">
                                </c:when>
                                <c:otherwise>
                                    <img src="${pageContext.request.contextPath}/assets/images/default-course.jpg" class="card-img-top course-img" alt="No image">
                                </c:otherwise>
                            </c:choose>
                            <div class="card-body">
                                <div class="price-tag mb-3">
                                    <fmt:formatNumber value="${course.price}" type="number" />đ
                                </div>

                                <!-- Display appropriate button based on user's status -->
                                <c:choose>
                                    <c:when test="${hasApprovedRefund}">
                                        <button class="btn btn-secondary w-100 mb-3" disabled>
                                            <i class="fas fa-check-circle me-2"></i>Refund Approved
                                        </button>
                                        <div class="alert alert-info mb-0">
                                            <small>Your refund has been approved. Access to this course has
                                                been revoked.</small>
                                        </div>
                                    </c:when>
                                    <c:when test="${hasPendingRefund}">
                                        <button class="btn btn-warning w-100 mb-3" disabled>
                                            <i class="fas fa-clock me-2"></i>Refund Pending
                                        </button>
                                        <div class="alert alert-info mb-0">
                                            <small>Your refund request is currently being processed.</small>
                                        </div>
                                    </c:when>
                                    <c:when test="${alreadyPurchased}">
                                        <a href="${pageContext.request.contextPath}/learn/course/${course.courseID}"
                                           class="btn btn-success w-100 mb-3">
                                            <i class="fas fa-play-circle me-2"></i>Start Learning
                                        </a>
                                        <a href="${pageContext.request.contextPath}/refund/request/${course.courseID}"
                                           class="btn btn-outline-danger w-100">
                                            <i class="fas fa-undo-alt me-2"></i>Request Refund
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <form action="${pageContext.request.contextPath}/cart/add"
                                              method="post">
                                            <input type="hidden" name="courseId" value="${course.courseID}">
                                            <button type="submit" class="btn btn-primary w-100 mb-3">
                                                <i class="fas fa-shopping-cart me-2"></i>Add to Cart
                                            </button>
                                        </form>
                                        <a href="${pageContext.request.contextPath}/checkout?courseId=${course.courseID}"
                                           class="btn btn-outline-primary w-100">
                                            <i class="fas fa-credit-card me-2"></i>Buy Now
                                        </a>
                                    </c:otherwise>
                                </c:choose>

                                <div class="course-features mt-4">
                                    <div class="course-feature">
                                        <i class="fas fa-graduation-cap"></i>
                                        <span>Full Lifetime Access</span>
                                    </div>
                                    <div class="course-feature">
                                        <i class="fas fa-mobile-alt"></i>
                                        <span>Access on Mobile & TV</span>
                                    </div>
                                    <div class="course-feature">
                                        <i class="fas fa-signal"></i>
                                        <span>Level: ${course.level}</span>
                                    </div>
                                    <div class="course-feature">
                                        <i class="fas fa-clock"></i>
                                        <span>Duration: ${course.duration}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </header>

        <!-- Course Content -->
        <section class="course-section">
            <div class="container">
                <div class="row">
                    <div class="col-lg-8">
                        <div class="card mb-4">
                            <div class="card-header">
                                <h4 class="mb-0">What You'll Learn</h4>
                            </div>
                            <div class="card-body">
                                <div class="row">
                                    <div class="col-md-6">
                                        <ul class="list-unstyled mb-0">
                                            <li class="mb-2"><i class="fas fa-check text-success me-2"></i>
                                                Master core concepts in this subject</li>
                                            <li class="mb-2"><i class="fas fa-check text-success me-2"></i>
                                                Apply techniques to real-world scenarios</li>
                                            <li class="mb-2"><i class="fas fa-check text-success me-2"></i>
                                                Build professional-quality projects</li>
                                        </ul>
                                    </div>
                                    <div class="col-md-6">
                                        <ul class="list-unstyled mb-0">
                                            <li class="mb-2"><i class="fas fa-check text-success me-2"></i>
                                                Understand advanced methodologies</li>
                                            <li class="mb-2"><i class="fas fa-check text-success me-2"></i>
                                                Troubleshoot common challenges</li>
                                            <li class="mb-2"><i class="fas fa-check text-success me-2"></i>
                                                Develop your own unique approach</li>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="card mb-4">
                            <div class="card-header">
                                <h4 class="mb-0">Course Description</h4>
                            </div>
                            <div class="card-body">
                                <p>
                                    ${course.description}
                                </p>
                                <p>
                                    This comprehensive course is designed to take you from beginner to
                                    professional.
                                    Whether you're just starting out or looking to enhance your existing
                                    skills,
                                    this course provides all the knowledge and practical experience you need
                                    to succeed.
                                </p>
                                <p>
                                    Join thousands of students who have already transformed their careers
                                    with this course!
                                </p>
                            </div>
                        </div>
                    </div>

                    <div class="col-lg-4">
                        <div class="card mb-4">
                            <div class="card-header">
                                <h4 class="mb-0">Course Includes</h4>
                            </div>
                            <div class="card-body">
                                <ul class="list-group list-group-flush">
                                    <li
                                        class="list-group-item d-flex justify-content-between align-items-center">
                                        <span><i class="fas fa-video text-primary me-2"></i> Video
                                            Lectures</span>
                                        <span>10+ hours</span>
                                    </li>
                                    <li
                                        class="list-group-item d-flex justify-content-between align-items-center">
                                        <span><i class="fas fa-file-alt text-primary me-2"></i> Downloadable
                                            Resources</span>
                                        <span>25+</span>
                                    </li>
                                    <li
                                        class="list-group-item d-flex justify-content-between align-items-center">
                                        <span><i class="fas fa-infinity text-primary me-2"></i> Full
                                            Lifetime Access</span>
                                        <span><i class="fas fa-check text-success"></i></span>
                                    </li>
                                    <li
                                        class="list-group-item d-flex justify-content-between align-items-center">
                                        <span><i class="fas fa-mobile-alt text-primary me-2"></i> Access on
                                            Mobile & TV</span>
                                        <span><i class="fas fa-check text-success"></i></span>
                                    </li>
                                </ul>
                            </div>
                        </div>

                        <div class="card">
                            <div class="card-header">
                                <h4 class="mb-0">Related Courses</h4>
                            </div>
                            <div class="card-body">
                                <p class="text-center">Explore similar courses to enhance your learning.</p>
                                <div class="d-grid gap-2">
                                    <a href="${pageContext.request.contextPath}/home"
                                       class="btn btn-outline-primary">Browse All Courses</a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- Include footer -->
        <jsp:include page="/WEB-INF/views/customer/common/footer.jsp"/>

        <!-- Bootstrap & jQuery JS -->
        <jsp:include page="/WEB-INF/views/customer/common/scripts.jsp"/>
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script
        src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/js/script.js"></script>

        <script>
            // Handle form submission via AJAX for ratings
            $(document).ready(function () {
                // Add Rating Form
                $('#addRatingForm').submit(function (e) {
                    e.preventDefault();

                    $.ajax({
                        type: "POST",
                        url: $(this).attr('action'),
                        data: $(this).serialize(),
                        dataType: "json",
                        success: function (response) {
                            if (response.success) {
                                // Reload the page to show the new rating
                                location.reload();
                            } else {
                                alert(response.message);
                            }
                        },
                        error: function () {
                            alert("An error occurred. Please try again.");
                        }
                    });
                });

                // Update Rating Form
                $('#updateRatingForm').submit(function (e) {
                    e.preventDefault();

                    $.ajax({
                        type: "POST",
                        url: $(this).attr('action'),
                        data: $(this).serialize(),
                        dataType: "json",
                        success: function (response) {
                            if (response.success) {
                                // Reload the page to show the updated rating
                                location.reload();
                            } else {
                                alert(response.message);
                            }
                        },
                        error: function () {
                            alert("An error occurred. Please try again.");
                        }
                    });
                });
            });
        </script>
    </body>
</html>