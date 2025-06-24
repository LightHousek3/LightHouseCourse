<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">

    <head>

        <title>Manage Course Reviews - LightHouse Admin</title>
        <jsp:include page="../common/head.jsp" />
        <style>
            .rating {
                color: #ffc107;
                font-size: 1.1rem;
            }

            .empty-star {
                color: #e4e5e9;
            }

            .rating-card {
                border-radius: 8px;
                box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
                margin-bottom: 20px;
                transition: all 0.3s ease;
                display: flex;
                flex-direction: column;
                height: 100%;
                /* Make sure all cards have equal height - will automatically identify the highest box */
            }

            .rating-card:hover {
                box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
                transform: translateY(-2px);
            }

            .rating-header {
                background-color: #f8f9fa;
                padding: 15px;
                border-radius: 8px 8px 0 0;
                border-bottom: 1px solid #e9ecef;
                flex-shrink: 0;
                /* Do not allow shrinking */
            }

            .rating-body {
                padding: 15px;
                flex-grow: 1;
                /* Occupy the remaining space */
                display: flex;
                flex-direction: column;
            }

            .rating-comment {
                background-color: #f8f9fa;
                padding: 15px;
                border-radius: 6px;
                margin-top: 10px;
                flex-grow: 1;
                /* Allow expanded comments */
                display: flex;
                align-items: flex-start;
                /* Text from top to */
            }

            .rating-comment p {
                margin: 0;
                line-height: 1.5;
            }

            .rating-meta {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-top: auto;
                /* Push to the bottom */
                padding-top: 15px;
                font-size: 0.85rem;
                color: #6c757d;
                flex-shrink: 0;
                /* Do not allow shrinking */
                border-top: 1px solid #e9ecef;
                /* Add borders to separate */
            }

            .rating-filter {
                background-color: white;
                padding: 15px;
                border-radius: 8px;
                margin-bottom: 20px;
                box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
            }

            .review-item.hidden {
                display: none !important;
            }

            /* Style for the no results message */
            #noResultsMessage {
                display: none;
                flex-direction: column;
                align-items: center;
                padding: 30px;
                text-align: center;
                width: 100%;
                background-color: #f8f9fa;
                border-radius: 8px;
                margin-top: 20px;
            }
        </style>
    </head>

    <body>
        <!-- Include Admin Sidebar -->
        <c:set var="activeMenu" value="ratings" scope="request" />
        <jsp:include page="../common/sidebar.jsp" />

        <div class="admin-content">
            <div class="container-fluid">
                <!-- Header -->
                <div class="admin-header d-flex justify-content-between align-items-center">
                    <button class="btn d-lg-none" id="toggleSidebarBtn">
                        <i class="fas fa-bars"></i>
                    </button>
                    <h2 class="m-0 d-none d-lg-block">Manage Reviews</h2>
                    <div class="d-flex gap-2">
                        <a href="${pageContext.request.contextPath}/admin/reviews"
                           class="col-md-3 btn btn-lg btn-outline-secondary">
                            <i class="fas fa-sync-alt me-2"></i>Reset Filters
                        </a>
                    </div>
                </div>

                <!-- Success or Error Message -->
                <c:if test="${param.success != null}">
                    <div class="alert alert-${param.success eq 'true' ? 'success' : 'danger'} alert-dismissible fade show"
                         role="alert">
                        <c:choose>
                            <c:when test="${param.success eq 'true'}">
                                <i class="fas fa-check-circle me-2"></i>
                            </c:when>
                            <c:otherwise>
                                <i class="fas fa-times-circle me-2"></i>
                            </c:otherwise>
                        </c:choose>
                        ${param.message}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"
                                aria-label="Close"></button>
                    </div>
                </c:if>

                <!-- Filter Section -->
                <div class="rating-filter">
                    <div class="row">
                        <div class="col-lg-3">
                            <div class="me-3 mb-2">
                                <label class="me-2">Search reviews</label>
                            </div>
                            <div class="search-container">
                                <i class="fas fa-search"></i>
                                <input type="text" id="searchInput" class="search-input"
                                       placeholder="Search reviews...">
                            </div>
                        </div>
                        <form action="${pageContext.request.contextPath}/admin/reviews" method="get"
                              class="col-lg-9">
                            <div class="row">
                                <div class="col-lg-4 d-flex flex-column">
                                    <label for="courseFilter" class="form-label">Filter by Course</label>
                                    <select class="form-select" id="courseFilter" name="courseId">
                                        <option value="">All Courses</option>
                                        <c:forEach var="course" items="${courses}">
                                            <option value="${course.courseID}" ${param.courseId eq
                                                             course.courseID.toString() ? 'selected' : '' }>
                                                        ${course.name}
                                                    </option>

                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="col-lg-4 d-flex flex-column">
                                        <label for="ratingFilter" class="form-label">Filter by Stars</label>
                                        <select class="form-select" id="ratingFilter" name="rating">
                                            <option value="">All Stars</option>
                                            <option value="5" ${param.rating eq '5' ? 'selected' : '' }>5 Stars</option>
                                            <option value="4" ${param.rating eq '4' ? 'selected' : '' }>4 Stars</option>
                                            <option value="3" ${param.rating eq '3' ? 'selected' : '' }>3 Stars</option>
                                            <option value="2" ${param.rating eq '2' ? 'selected' : '' }>2 Stars</option>
                                            <option value="1" ${param.rating eq '1' ? 'selected' : '' }>1 Star</option>
                                        </select>
                                    </div>
                                    <div class="col-lg-4 d-flex align-items-end">
                                        <button type="submit" class="btn btn-md btn-primary">
                                            Apply Filter
                                        </button>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>

                    <!-- Ratings List -->
                    <c:choose>
                        <c:when test="${empty ratings}">
                            <div class="card">
                                <div class="card-body text-center py-5">
                                    <i class="fas fa-star-half-alt fa-4x mb-3 text-muted"></i>
                                    <h3>No Ratings Found</h3>
                                    <p class="text-muted">There are no course ratings available at this time.</p>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="row" id="reviewsContainer">
                                <c:forEach var="rating" items="${ratings}">
                                    <div class="col-md-6 mb-4 d-flex review-item" data-comment="${rating.comment}"
                                         data-username="${rating.username}" data-course="${rating.courseName}">
                                        <div class="rating-card flex-fill">
                                            <div
                                                class="rating-header d-flex justify-content-between align-items-center">
                                                <div>
                                                    <div class="rating">
                                                        <c:forEach begin="1" end="5" var="i">
                                                            <c:choose>
                                                                <c:when test="${i <= rating.stars}">
                                                                    <i class="fas fa-star"></i>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <i class="fas fa-star empty-star"></i>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:forEach>
                                                        <span class="ms-2 text-dark">${rating.stars}.0</span>
                                                    </div>
                                                    <div class="mt-1">
                                                        <span class="fw-bold">${rating.username}</span> rated
                                                        <span
                                                            class="text-primary fw-bold">${rating.courseName}</span>

                                                    </div>
                                                </div>
                                                <form action="${pageContext.request.contextPath}/admin/reviews"
                                                      method="post"
                                                      onsubmit="return confirm('Are you sure you want to delete this rating?');">
                                                    <input type="hidden" name="action" value="delete">
                                                    <input type="hidden" name="ratingId" value="${rating.ratingID}">
                                                    <button type="submit" class="btn btn-sm btn-danger">
                                                        <i class="fas fa-trash-alt"></i>
                                                    </button>
                                                </form>
                                            </div>
                                            <div class="rating-body">
                                                <div class="rating-comment">
                                                    <p class="mb-0">${rating.comment}</p>
                                                </div>
                                                <div class="rating-meta">
                                                    <span>
                                                        <i class="far fa-calendar-alt me-1"></i>
                                                        <fmt:formatDate value="${rating.createdAt}"
                                                                        pattern="MMM dd, yyyy 'at' hh:mm a" />
                                                    </span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                            <!-- No Results Message -->
                            <div id="noResultsMessage" class="my-4">
                                <i class="fas fa-search fa-3x mb-3 text-muted"></i>
                                <h3>No Matching Reviews</h3>
                                <p class="text-muted">No reviews match your search criteria. Try different keywords.
                                </p>
                                <button id="clearSearchBtn" class="btn btn-lg btn-outline-secondary mt-2">
                                    <i class="fas fa-times me-2"></i>Clear Search
                                </button>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <jsp:include page="../common/scripts.jsp" />

            <!-- Search functionality script -->
            <script>
                document.addEventListener('DOMContentLoaded', function () {
                    const searchInput = document.getElementById('searchInput');
                    const reviewItems = document.querySelectorAll('.review-item');
                    const noResultsMessage = document.getElementById('noResultsMessage');
                    const clearSearchBtn = document.getElementById('clearSearchBtn');

                    if (!searchInput || reviewItems.length === 0)
                        return;

                    // Function to filter reviews based on search input
                    function filterReviews() {
                        const searchTerm = searchInput.value.trim().toLowerCase();
                        let visibleCount = 0;

                        reviewItems.forEach(item => {
                            const comment = item.dataset.comment.toLowerCase();
                            const username = item.dataset.username.toLowerCase();
                            const course = item.dataset.course.toLowerCase();

                            // Check if the search term matches any of the review data
                            if (comment.includes(searchTerm) ||
                                    username.includes(searchTerm) ||
                                    course.includes(searchTerm)) {
                                item.classList.remove('hidden');
                                visibleCount++;
                            } else {
                                item.classList.add('hidden');
                            }
                        });

                        // Show/hide no results message
                        if (visibleCount === 0 && searchTerm !== '') {
                            noResultsMessage.style.display = 'flex';
                        } else {
                            noResultsMessage.style.display = 'none';
                        }
                    }

                    // Add event listener for search input
                    searchInput.addEventListener('input', filterReviews);

                    // Clear search function
                    if (clearSearchBtn) {
                        clearSearchBtn.addEventListener('click', function () {
                            searchInput.value = '';
                            filterReviews();
                            searchInput.focus();
                        });
                    }
                });
            </script>
        </body>

    </html>