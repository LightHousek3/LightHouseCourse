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

                    /* Pagination styles */
                    .pagination-container {
                        margin-top: 20px;
                        display: flex;
                        justify-content: center;
                    }

                    .pagination {
                        gap: 0;
                    }

                    .pagination-button {
                        margin: 0 2px;
                        min-width: 36px;
                        height: 36px;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                    }

                    .pagination-ellipsis {
                        margin: 0 5px;
                        color: #6c757d;
                        display: flex;
                        align-items: center;
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
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
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
                                                <option value="5" ${param.rating eq '5' ? 'selected' : '' }>5 Stars
                                                </option>
                                                <option value="4" ${param.rating eq '4' ? 'selected' : '' }>4 Stars
                                                </option>
                                                <option value="3" ${param.rating eq '3' ? 'selected' : '' }>3 Stars
                                                </option>
                                                <option value="2" ${param.rating eq '2' ? 'selected' : '' }>2 Stars
                                                </option>
                                                <option value="1" ${param.rating eq '1' ? 'selected' : '' }>1 Star
                                                </option>
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
                                                    <button type="button"
                                                        class="btn btn-sm btn-danger delete-review-btn"
                                                        data-rating-id="${rating.ratingID}">
                                                        <i class="fas fa-trash-alt"></i>
                                                    </button>
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
                                <!-- Pagination -->
                                <div class="pagination-container mt-4 d-flex justify-content-center">
                                    <div class="pagination">
                                        <button class="btn btn-sm btn-outline-primary pagination-button"
                                            id="prevPageBtn">&laquo;</button>
                                        <div id="paginationButtons" class="d-flex">
                                            <!-- Pagination buttons will be inserted here -->
                                        </div>
                                        <button class="btn btn-sm btn-outline-primary pagination-button"
                                            id="nextPageBtn">&raquo;</button>
                                    </div>
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

                <!-- Delete Review Modal -->
                <div class="modal fade" id="deleteReviewModal" tabindex="-1" aria-labelledby="deleteReviewModalLabel"
                    aria-hidden="true">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header bg-danger text-white">
                                <h5 class="modal-title" id="deleteReviewModalLabel">Confirm Delete</h5>
                                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"
                                    aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                <p>Are you sure you want to delete this review?</p>
                                <p class="text-danger"><small>This action cannot be undone.</small></p>
                                <div class="mt-3 p-3 bg-light rounded" id="reviewPreview">
                                    <!-- Review preview content will be inserted here by JavaScript -->
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-md btn-secondary"
                                    data-bs-dismiss="modal">Cancel</button>
                                <button type="button" class="btn btn-md btn-danger"
                                    id="confirmDeleteBtn">Delete</button>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Hidden form for delete submission -->
                <form id="deleteForm" action="${pageContext.request.contextPath}/admin/reviews" method="post"
                    style="display: none;">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="ratingId" id="deleteRatingId">
                </form>

                <jsp:include page="../common/scripts.jsp" />

                <!-- Search, Delete and Pagination functionality script -->
                <script>
                    document.addEventListener('DOMContentLoaded', function () {
                        const searchInput = document.getElementById('searchInput');
                        const reviewItems = document.querySelectorAll('.review-item');
                        const noResultsMessage = document.getElementById('noResultsMessage');
                        const clearSearchBtn = document.getElementById('clearSearchBtn');
                        const deleteModal = new bootstrap.Modal(document.getElementById('deleteReviewModal'));
                        const deleteForm = document.getElementById('deleteForm');
                        const deleteRatingIdInput = document.getElementById('deleteRatingId');
                        const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');
                        const reviewPreview = document.getElementById('reviewPreview');
                        const pagination = document.querySelector('.pagination');
                        const prevPageBtn = document.getElementById('prevPageBtn');
                        const nextPageBtn = document.getElementById('nextPageBtn');
                        const paginationButtons = document.getElementById('paginationButtons');
                        const reviewsContainer = document.getElementById('reviewsContainer');

                        // Pagination variables
                        let currentPage = 1;
                        const itemsPerPage = 6; // 6 reviews per page (3 rows x 2 columns)
                        let filteredItems = Array.from(reviewItems);

                        // Set up delete buttons
                        document.querySelectorAll('.delete-review-btn').forEach(button => {
                            button.addEventListener('click', function (event) {
                                event.preventDefault();
                                const ratingId = this.getAttribute('data-rating-id');
                                const reviewItem = this.closest('.review-item');

                                // Set the rating ID in the hidden form
                                deleteRatingIdInput.value = ratingId;

                                // Create review preview content
                                const stars = reviewItem.querySelector('.rating').innerHTML;
                                const username = reviewItem.querySelector('.fw-bold').textContent;
                                const courseName = reviewItem.querySelector('.text-primary').textContent;
                                const comment = reviewItem.querySelector('.rating-comment p').textContent;

                                reviewPreview.innerHTML = `
                    <div class="rating mb-2">\${stars}</div>
                    <p class="mb-1"><strong>\${username}</strong> for <strong>\${courseName}</strong></p>
                    <p class="mb-0">\${comment}</p>
                `;

                                // Show the modal
                                deleteModal.show();
                            });
                        });

                        // Handle delete confirmation
                        confirmDeleteBtn.addEventListener('click', function () {
                            deleteForm.submit();
                        });

                        if (!searchInput || reviewItems.length === 0)
                            return;

                        // Function to filter reviews based on search input
                        function filterReviews() {
                            const searchTerm = searchInput.value.trim().toLowerCase();
                            let visibleCount = 0;

                            filteredItems = Array.from(reviewItems).filter(item => {
                                const comment = item.dataset.comment.toLowerCase();
                                const username = item.dataset.username.toLowerCase();
                                const course = item.dataset.course.toLowerCase();

                                // Check if the search term matches any of the review data
                                return comment.includes(searchTerm) ||
                                    username.includes(searchTerm) ||
                                    course.includes(searchTerm);
                            });

                            // Reset to first page when filtering
                            currentPage = 1;

                            // Show/hide no results message and update display
                            if (filteredItems.length === 0 && searchTerm !== '') {
                                noResultsMessage.style.display = 'flex';
                                pagination.style.display = 'none';
                            } else {
                                noResultsMessage.style.display = 'none';
                                pagination.style.display = 'flex';
                            }

                            renderReviews();
                        }

                        // Function to render reviews for the current page
                        function renderReviews() {
                            const startIndex = (currentPage - 1) * itemsPerPage;
                            const endIndex = Math.min(startIndex + itemsPerPage, filteredItems.length);
                            const visibleItems = filteredItems.slice(startIndex, endIndex);

                            // Hide all items first
                            reviewItems.forEach(item => {
                                item.classList.add('hidden');
                            });

                            // Show only items for current page
                            visibleItems.forEach(item => {
                                item.classList.remove('hidden');
                            });

                            // Update pagination
                            updatePagination();
                        }

                        // Function to update pagination controls
                        function updatePagination() {
                            const totalPages = Math.ceil(filteredItems.length / itemsPerPage);
                            paginationButtons.innerHTML = '';

                            // Determine page range to display
                            let startPage = Math.max(1, currentPage - 2);
                            let endPage = Math.min(totalPages, startPage + 4);

                            if (endPage - startPage < 4) {
                                startPage = Math.max(1, endPage - 4);
                            }

                            // First page button
                            if (startPage > 1) {
                                addPageButton(1);
                                if (startPage > 2) {
                                    addEllipsis();
                                }
                            }

                            // Page buttons
                            for (let i = startPage; i <= endPage; i++) {
                                addPageButton(i);
                            }

                            // Last page button
                            if (endPage < totalPages) {
                                if (endPage < totalPages - 1) {
                                    addEllipsis();
                                }
                                addPageButton(totalPages);
                            }

                            // Enable/disable prev/next buttons
                            prevPageBtn.disabled = currentPage === 1;
                            nextPageBtn.disabled = currentPage === totalPages || totalPages === 0;

                            // Hide pagination if only one page
                            const paginationContainer = document.querySelector('.pagination-container');
                            if (totalPages <= 1) {
                                paginationContainer.style.display = 'none';
                            } else {
                                paginationContainer.style.display = 'flex';
                            }
                        }

                        function addPageButton(pageNum) {
                            const button = document.createElement('button');
                            button.className = `btn btn-sm pagination-button \${pageNum === currentPage ? 'btn-primary' : 'btn-outline-primary'}`;
                            button.textContent = pageNum;
                            button.addEventListener('click', () => {
                                currentPage = pageNum;
                                renderReviews();
                            });
                            paginationButtons.appendChild(button);
                        }

                        function addEllipsis() {
                            const span = document.createElement('span');
                            span.className = 'pagination-ellipsis mx-1';
                            span.textContent = '...';
                            paginationButtons.appendChild(span);
                        }

                        // Add event listeners for pagination
                        prevPageBtn.addEventListener('click', function () {
                            if (currentPage > 1) {
                                currentPage--;
                                renderReviews();
                            }
                        });

                        nextPageBtn.addEventListener('click', function () {
                            const totalPages = Math.ceil(filteredItems.length / itemsPerPage);
                            if (currentPage < totalPages) {
                                currentPage++;
                                renderReviews();
                            }
                        });

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

                        // Initial render
                        renderReviews();
                    });
                </script>
            </body>

            </html>