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
                background-color: var(--secondary-color);
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

            .text-expert {
                background: linear-gradient(90deg, rgb(7, 174, 234) 0%, rgb(43, 245, 152) 100%);
                color: transparent;
                background-clip: text;
                font-size: 16px;
                font-weight: 600;
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

                        <c:if test="${not empty course.instructors}">
                            <c:forEach var="inst" items="${course.instructors}" varStatus="loop">
                                    <a href="${pageContext.request.contextPath}/course/instructor-info?instructorId=${inst.instructorID}"
                                       class="course-instructor text-decoration-none text-dark d-flex align-items-center">

                                        <c:choose>
                                            <c:when test="${not empty inst.avatar}">
                                                <img src="${pageContext.request.contextPath}${inst.avatar}"
                                                     class="instructor-img" alt="Instructor" />
                                            </c:when>
                                            <c:otherwise>
                                                <img src="${pageContext.request.contextPath}/assets/images/default-instructor.jpg"
                                                     class="instructor-img" alt="Instructor" />
                                            </c:otherwise>
                                        </c:choose>

                                        <div class="ms-3">
                                            <div class="fw-bold">${inst.fullName}</div>
                                            <small class="text-muted text-expert">Expert ${inst.specialization}</small>
                                        </div>
                                    </a>
                            </c:forEach>
                        </c:if>



                    </div>
                    <div class="col-lg-4">
                        <div class="card course-card" data-course-id="${course.courseID}">
                            <c:choose>
                                <c:when test="${not empty course.imageUrl}">
                                    <img src="${pageContext.request.contextPath}/${course.imageUrl}"
                                         class="card-img-top course-img" alt="${course.name}">
                                </c:when>
                                <c:otherwise>
                                    <img src="${pageContext.request.contextPath}/assets/images/default-course.jpg"
                                         class="card-img-top course-img" alt="No image">
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
                                        <a href="${pageContext.request.contextPath}/learning/${course.courseID}"
                                           class="btn btn-success w-100 mb-3">
                                            <i class="fas fa-play-circle me-2"></i>Start Learning
                                        </a>
                                        <c:if test="${order.getAttribute('eligibleForRefund') == true}">
                                            <a href="${pageContext.request.contextPath}/refund/request/order/${order.orderID}"
                                               class="btn btn-outline-danger mt-2">
                                                Request Refund
                                            </a>
                                        </c:if>
                                    </c:when>
                                    <c:otherwise>
                                        <button type="button"
                                                class="btn btn-primary add-to-cart-btn w-100 mb-3"
                                                data-course-id="${course.courseID}">
                                            <i class="fas fa-shopping-cart me-2"></i>Add to Cart
                                        </button>
                                        <a href="${ pageContext.request.contextPath}/order/checkout?courseId=${course.courseID}"
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
                        <!-- Course Ratings Section -->
                        <div class="card mb-4">
                            <div class="card-header">
                                <h4 class="mb-0">Student Ratings</h4>
                            </div>
                            <div class="card-body">
                                <div class="rating-header">
                                    <div class="overall-rating">
                                        <div class="rating-stars me-2">
                                            <c:forEach begin="1" end="5" var="i">
                                                <c:choose>
                                                    <c:when test="${i <= Math.round(averageRating)}">
                                                        <i class="fas fa-star"></i>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <i class="fas fa-star empty-star"></i>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:forEach>
                                        </div>
                                        <div>
                                            <h3 class="mb-0">
                                                <fmt:formatNumber value="${averageRating}" pattern="#0.0" />
                                            </h3>
                                        </div>
                                    </div>
                                    <div class="rating-count">
                                        <c:choose>
                                            <c:when test="${ratingCount == 0}">
                                                No ratings yet
                                            </c:when>
                                            <c:when test="${ratingCount == 1}">
                                                1 rating
                                            </c:when>
                                            <c:otherwise>
                                                ${ratingCount} ratings
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>

                                <!-- User's Rating Form (if eligible) -->
                                <c:choose>
                                    <c:when test="${alreadyPurchased && canRateCourse}">
                                        <c:choose>
                                            <c:when test="${userRating != null}">
                                                <!-- Edit existing rating -->
                                                <div class="rating-form">
                                                    <h5 class="mb-3">Update Your Rating</h5>
                                                    <form id="updateRatingForm"
                                                          action="${pageContext.request.contextPath}/rating/update"
                                                          method="post">
                                                        <input type="hidden" name="ratingId"
                                                               value="${userRating.ratingID}">
                                                        <div class="mb-3">
                                                            <label class="form-label">Your Rating:</label>
                                                            <div class="star-rating">
                                                                <input type="radio" id="star5-edit"
                                                                       name="stars" value="5"
                                                                       ${userRating.stars==5 ? 'checked' : ''
                                                                       } />
                                                                <label for="star5-edit"><i
                                                                        class="fas fa-star"></i></label>
                                                                <input type="radio" id="star4-edit"
                                                                       name="stars" value="4"
                                                                       ${userRating.stars==4 ? 'checked' : ''
                                                                       } />
                                                                <label for="star4-edit"><i
                                                                        class="fas fa-star"></i></label>
                                                                <input type="radio" id="star3-edit"
                                                                       name="stars" value="3"
                                                                       ${userRating.stars==3 ? 'checked' : ''
                                                                       } />
                                                                <label for="star3-edit"><i
                                                                        class="fas fa-star"></i></label>
                                                                <input type="radio" id="star2-edit"
                                                                       name="stars" value="2"
                                                                       ${userRating.stars==2 ? 'checked' : ''
                                                                       } />
                                                                <label for="star2-edit"><i
                                                                        class="fas fa-star"></i></label>
                                                                <input type="radio" id="star1-edit"
                                                                       name="stars" value="1"
                                                                       ${userRating.stars==1 ? 'checked' : ''
                                                                       } />
                                                                <label for="star1-edit"><i
                                                                        class="fas fa-star"></i></label>
                                                            </div>
                                                        </div>
                                                        <div class="mb-3">
                                                            <label for="comment-edit"
                                                                   class="form-label">Your Review:</label>
                                                            <textarea class="form-control" id="comment-edit"
                                                                      name="comment" rows="4"
                                                                      required>${userRating.comment}</textarea>
                                                        </div>
                                                        <div class="d-flex">
                                                            <button type="submit"
                                                                    class="btn btn-primary me-2">Update
                                                                Review</button>
                                                            <a href="${pageContext.request.contextPath}/rating/delete/${userRating.ratingID}"
                                                               class="btn btn-outline-danger btn-delete-rating"
                                                               data-rating-id="${userRating.ratingID}">
                                                                Delete Review
                                                            </a>
                                                        </div>
                                                    </form>
                                                </div>
                                            </c:when>
                                            <c:otherwise>
                                                <!-- Add new rating -->
                                                <div class="rating-form">
                                                    <h5 class="mb-3">Rate This Course</h5>
                                                    <form id="addRatingForm"
                                                          action="${pageContext.request.contextPath}/rating"
                                                          method="post">
                                                        <input type="hidden" name="courseId"
                                                               value="${course.courseID}">
                                                        <div class="mb-3">
                                                            <label class="form-label">Your Rating:</label>
                                                            <div class="star-rating">
                                                                <input type="radio" id="star5" name="stars"
                                                                       value="5" required />
                                                                <label for="star5"><i
                                                                        class="fas fa-star"></i></label>
                                                                <input type="radio" id="star4" name="stars"
                                                                       value="4" />
                                                                <label for="star4"><i
                                                                        class="fas fa-star"></i></label>
                                                                <input type="radio" id="star3" name="stars"
                                                                       value="3" />
                                                                <label for="star3"><i
                                                                        class="fas fa-star"></i></label>
                                                                <input type="radio" id="star2" name="stars"
                                                                       value="2" />
                                                                <label for="star2"><i
                                                                        class="fas fa-star"></i></label>
                                                                <input type="radio" id="star1" name="stars"
                                                                       value="1" />
                                                                <label for="star1"><i
                                                                        class="fas fa-star"></i></label>
                                                            </div>
                                                        </div>
                                                        <div class="mb-3">
                                                            <label for="comment" class="form-label">Your
                                                                Review:</label>
                                                            <textarea class="form-control" id="comment"
                                                                      name="comment" rows="4" required></textarea>
                                                        </div>
                                                        <button type="submit" class="btn btn-primary">Submit
                                                            Review</button>
                                                    </form>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:when>
                                    <c:when test="${alreadyPurchased && !canRateCourse}">
                                        <div class="user-rating-prompt mb-4">
                                            <i class="fas fa-info-circle fa-2x text-primary mb-3"></i>
                                            <h5>Complete More Course Content to Rate</h5>
                                            <p class="text-muted">You need to complete at least 80% of the
                                                course content to leave a rating.</p>
                                        </div>
                                    </c:when>
                                </c:choose>

                                <!-- List of ratings -->
                                <c:choose>
                                    <c:when test="${empty ratings}">
                                        <div class="text-center py-4">
                                            <i class="fas fa-star-half-alt fa-3x text-muted mb-3"></i>
                                            <h5>No Ratings Yet</h5>
                                            <p class="text-muted">Be the first to rate this course!</p>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="ratings-list">
                                            <c:forEach var="rating" items="${ratings}">
                                                <div class="rating-card">
                                                    <div class="rating-content">
                                                        <div class="rating-user">
                                                            <div class="rating-avatar">
                                                                ${fn:toUpperCase(fn:substring(rating.username,
                                                                  0, 1))}
                                                            </div>
                                                            <div class="rating-user-details">
                                                                <p class="rating-user-name">
                                                                    ${rating.username}</p>
                                                                <p class="rating-date">
                                                                    <fmt:formatDate
                                                                        value="${rating.createdAt}"
                                                                        pattern="MMM dd, yyyy" />
                                                                    <c:if
                                                                        test="${rating.createdAt != rating.updatedAt}">
                                                                        <span
                                                                            class="text-muted">(edited)</span>
                                                                    </c:if>
                                                                </p>
                                                            </div>
                                                        </div>

                                                        <div class="rating-stars mb-2">
                                                            <c:forEach begin="1" end="5" var="i">
                                                                <c:choose>
                                                                    <c:when test="${i <= rating.stars}">
                                                                        <i class="fas fa-star"></i>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <i
                                                                            class="fas fa-star empty-star"></i>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </c:forEach>
                                                        </div>

                                                        <div class="rating-comment">
                                                            ${rating.comment}
                                                        </div>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
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
                                        <span><i class="fas fa-check text-success"></i></span>
                                    </li>
                                    <li
                                        class="list-group-item d-flex justify-content-between align-items-center">
                                        <span><i class="fas fa-file-alt text-primary me-2"></i> Downloadable
                                            Resources</span>
                                        <span><i class="fas fa-check text-success"></i></span>
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
                                    <a href="${pageContext.request.contextPath}/home?scroll=true"
                                       class="btn btn-outline-primary">Browse All Courses</a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
        <!-- Delete Confirm Modal -->
        <div class="modal fade" id="deleteConfirmModal" tabindex="-1" aria-labelledby="deleteConfirmLabel"
             aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header bg-danger text-white">
                        <h5 class="modal-title" id="deleteConfirmLabel">Confirm Delete</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"
                                aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        Are you sure you want to delete your review?
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary"
                                data-bs-dismiss="modal">Cancel</button>
                        <button type="button" id="confirmDeleteBtn" class="btn btn-danger">Yes,
                            Delete</button>
                    </div>
                </div>
            </div>
        </div>


        <!-- Include footer -->
        <jsp:include page="/WEB-INF/views/customer/common/footer.jsp" />

        <!-- Bootstrap & jQuery JS -->
        <jsp:include page="/WEB-INF/views/customer/common/scripts.jsp" />
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script
        src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>

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
            // Handle form submission via AJAX for ratings
            $(document).ready(function () {
                const message = sessionStorage.getItem("notificationMessage");
                const type = sessionStorage.getItem("notificationType");
                if (message) {
                    showNotification(message, type || 'info');
                    sessionStorage.removeItem("notificationMessage");
                    sessionStorage.removeItem("notificationType");
                }
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
                                sessionStorage.setItem("notificationMessage", response.message);
                                sessionStorage.setItem("notificationType", "success");
                                location.reload();
                            } else {
                                showNotification(response.message, 'error');
                            }
                        },
                        error: function () {
                            showNotification("An error occurred, please try again later.", "error");
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
                                sessionStorage.setItem("notificationMessage", response.message);
                                sessionStorage.setItem("notificationType", "success");
                                location.reload();
                            } else {
                                showNotification(response.message, 'error');
                            }
                        },
                        error: function () {
                            showNotification("An error occurred, please try again later.", "error");
                        }
                    });
                });
            });

            let deleteUrl = "";

            $(document).on("click", ".btn-delete-rating", function (e) {
                e.preventDefault();
                deleteUrl = $(this).attr("href");

                // Open modal confirm
                const modal = new bootstrap.Modal(document.getElementById('deleteConfirmModal'));
                modal.show();
            });

            // When press button "Yes, Delete"
            $('#confirmDeleteBtn').on("click", function () {
                // Close modal
                const modalEl = document.getElementById('deleteConfirmModal');
                const modalInstance = bootstrap.Modal.getInstance(modalEl);
                modalInstance.hide();

                // Send AJAX delete
                $.ajax({
                    type: "GET",
                    url: deleteUrl,
                    dataType: "json",
                    success: function (response) {
                        if (response.success) {
                            sessionStorage.setItem("notificationMessage", response.message);
                            sessionStorage.setItem("notificationType", "success");
                            location.reload();
                        } else {
                            showNotification(response.message, 'error');
                        }
                    },
                    error: function () {
                        showNotification("Unable to delete review. Please try again.", "error");
                    }
                });
            });

        </script>



    </body>

</html>