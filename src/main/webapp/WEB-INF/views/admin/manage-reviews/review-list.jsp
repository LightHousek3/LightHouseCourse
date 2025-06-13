<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">

    <head>

        <title>Manage Course Reviews - LightHouseCourse</title>
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
            }

            .rating-body {
                padding: 15px;
            }

            .rating-comment {
                background-color: #f8f9fa;
                padding: 15px;
                border-radius: 6px;
                margin-top: 10px;
            }

            .rating-meta {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-top: 10px;
                font-size: 0.85rem;
                color: #6c757d;
            }

            .rating-filter {
                background-color: white;
                padding: 15px;
                border-radius: 8px;
                margin-bottom: 20px;
                box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
            }
        </style>
    </head>

    <body>
        <!-- Include Admin Sidebar -->
        <c:set var="activeMenu" value="ratings" scope="request" />
        <jsp:include page="../common/sidebar.jsp" />

        <div class="admin-content">
            <div class="container-fluid">
                <div class="admin-header d-flex justify-content-between align-items-center">
                        <h2 class="mb-0">Manage Reviews</h2>
                        <div class="d-flex align-items-center">
                            <span class="me-3">Welcome,${sessionScope.user.username != null ? sessionScope.user.username : "Admin"}!</span>

                            <div class="dropdown">
                                <button class="btn btn-outline-secondary dropdown-toggle" type="button"
                                    id="userDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                                    <i class="fas fa-user-circle me-1"></i> Admin
                                </button>
                                <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="userDropdown">
                                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/profile">My
                                            Profile</a></li>
                                    <li>
                                        <hr class="dropdown-divider">
                                    </li>
                                    <li><a class="dropdown-item"
                                            href="${pageContext.request.contextPath}/logout">Logout</a></li>
                                </ul>
                            </div>
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
                    <form action="${pageContext.request.contextPath}/admin/ratings" method="get"
                          class="row g-3">
                        <div class="col-md-3">
                            <label for="courseFilter" class="form-label">Filter by Course</label>
                            <select class="form-select" id="courseFilter" name="courseId">
                                <option value="">All Courses</option>
                                <c:forEach var="course" items="${courses}">
                                    <option value="${course.courseID}" ${param.courseId eq course.courseID
                                                     ? 'selected' : '' }>
                                                ${course.name}
                                            </option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-3">
                                <label for="ratingFilter" class="form-label">Filter by Rating</label>
                                <select class="form-select" id="ratingFilter" name="rating">
                                    <option value="">All Ratings</option>
                                    <option value="5" ${param.rating eq '5' ? 'selected' : '' }>5 Stars</option>
                                    <option value="4" ${param.rating eq '4' ? 'selected' : '' }>4 Stars</option>
                                    <option value="3" ${param.rating eq '3' ? 'selected' : '' }>3 Stars</option>
                                    <option value="2" ${param.rating eq '2' ? 'selected' : '' }>2 Stars</option>
                                    <option value="1" ${param.rating eq '1' ? 'selected' : '' }>1 Star</option>
                                </select>
                            </div>
                            <div class="col-md-3 d-flex align-items-end">
                                <button type="submit" class="btn btn-primary w-100">
                                    <i class="fas fa-filter me-2"></i>Apply Filter
                                </button>
                            </div>
                            <div class="col-md-3 d-flex align-items-end">
                                <a href="${pageContext.request.contextPath}/admin/reviews"
                                   class="btn btn-outline-secondary w-100">
                                    <i class="fas fa-sync-alt me-2"></i>Reset Filters
                                </a>
                            </div>
                        </form>
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
                            <div class="row">
                                <c:forEach var="rating" items="${ratings}">
                                    <div class="col-md-6 mb-4">
                                        <div class="rating-card">
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
                                                        <a href="${pageContext.request.contextPath}/course/${rating.courseID}"
                                                           target="_blank" class="text-primary">
                                                            ${rating.courseName}
                                                        </a>
                                                    </div>
                                                </div>
                                                <form action="${pageContext.request.contextPath}/admin/ratings"
                                                      method="post"
                                                      onsubmit="return confirm('Are you sure you want to delete this rating?');">
                                                    <input type="hidden" name="action" value="delete">
                                                    <input type="hidden" name="ratingId" value="${rating.ratingID}">
                                                    <button type="submit" class="btn btn-sm btn-danger">
                                                        <i class="fas fa-trash-alt"></i> Delete
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
                                                    <c:if test="${rating.createdAt != rating.updatedAt}">
                                                        <span class="text-muted">
                                                            <i class="fas fa-edit me-1"></i> Edited
                                                        </span>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
                                   
            <jsp:include page="../common/scripts.jsp" />
        </body>

    </html>