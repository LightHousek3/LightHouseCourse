<%-- 
    Document   : view-instructor-information
    Created on : Jul 15, 2025, 12:14:04 AM
    Author     : NhiDTY-CE180492
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">

    <head>
        <title>Instructor Profile - LightHouse</title>
        <!-- Include common head with styles -->
        <jsp:include page="/WEB-INF/views/customer/common/head.jsp" />
        <style>
            .profile-header {
                background: linear-gradient(135deg, #3e8ce8 0%, #86b3fd 50%, #c1d5ff 100%);
                color: white;
                padding: 80px 0;
                margin-bottom: 40px;
                position: relative;
                overflow: hidden;
            }

            .profile-header::before {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                bottom: 0;
                background: url('${pageContext.request.contextPath}/assets/images/pattern.png');
                opacity: 0.1;
            }

            .profile-header .container {
                position: relative;
                z-index: 2;
            }

            .floating-wrapper {
                position: unset;
            }

            .floating-element {
                position: absolute;
                color: rgba(255, 255, 255, 0.2);
                animation: float 6s infinite ease-in-out;
            }

            .floating-element:nth-child(1) {
                top: 20%;
                left: 10%;
                font-size: 3.5rem;
                animation-delay: 0s;
            }

            .floating-element:nth-child(2) {
                top: 60%;
                left: 20%;
                font-size: 2.5rem;
                animation-delay: 1s;
            }

            .floating-element:nth-child(3) {
                top: 30%;
                right: 15%;
                font-size: 3rem;
                animation-delay: 2s;
            }

            .floating-element:nth-child(4) {
                top: 80%;
                right: 10%;
                font-size: 2rem;
                animation-delay: 3s;
            }

            @keyframes float {
                0% {
                    transform: translateY(0px) rotate(0deg);
                }

                50% {
                    transform: translateY(-20px) rotate(10deg);
                }

                100% {
                    transform: translateY(0px) rotate(0deg);
                }
            }

            .profile-avatar {
                width: 140px;
                height: 140px;
                border-radius: 50%;
                object-fit: cover;
                border: 5px solid rgba(255, 255, 255, 0.3);
                box-shadow: 0 10px 15px rgba(0, 0, 0, 0.1);
            }

            .profile-name {
                font-size: 2.5rem;
                font-weight: 700;
                margin-bottom: 0.5rem;
                text-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            }

            .profile-role {
                display: inline-block;
                padding: 6px 15px;
                background-color: rgba(255, 255, 255, 0.2);
                border-radius: 20px;
                font-size: 0.9rem;
                margin-bottom: 1rem;
            }

            .card {
                border: none;
                border-radius: 15px;
                box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
                transition: all 0.3s;
                margin-bottom: 25px;
            }

            .card:hover {
                transform: translateY(-5px);
                box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
            }

            .course-card {
                overflow: hidden;
                height: 100%;
            }

            .course-card .card-img-top {
                height: 180px;
                object-fit: cover;
                transition: transform 0.5s;
            }

            .course-card:hover .card-img-top {
                transform: scale(1.05);
            }

            .course-card .card-body {
                padding: 1.5rem;
            }

            .course-card .card-title {
                font-size: 1.25rem;
                font-weight: 600;
                margin-bottom: 0.75rem;
            }

            .course-card .card-text {
                color: #6c757d;
                margin-bottom: 1rem;
            }

            .badge.level {
                font-weight: 500;
                padding: 0.5em 0.75em;
                margin-right: 0.5rem;
            }

            .badge.beginner {
                background-color: #28a745;
            }

            .badge.intermediate {
                background-color: #fd7e14;
            }

            .badge.advanced {
                background-color: #dc3545;
            }

            .instructor-stats {
                text-align: center;
                padding: 1.5rem;
            }

            .stat-number {
                font-size: 2.5rem;
                font-weight: 700;
                color: #3e8ce8;
                margin-bottom: 0.5rem;
            }

            .stat-label {
                font-size: 0.9rem;
                color: #6c757d;
                text-transform: uppercase;
                letter-spacing: 1px;
            }

            .rating-stars {
                color: #ffc107;
                font-size: 1.2rem;
                margin-right: 0.5rem;
            }

            .course-price {
                font-weight: bold;
                color: #3e8ce8;
                font-size: 1.2rem;
            }

            .course-duration {
                color: #6c757d;
                font-size: 0.9rem;
            }
        </style>
    </head>
    <body>
        <!-- Include navigation -->
        <jsp:include page="/WEB-INF/views/customer/common/navigation.jsp" />
        <!-- Instructor Profile Header with Floating Elements -->
        <header class="profile-header">
            <div class="floating-wrapper">
                <div class="floating-element"><i class="fas fa-chalkboard-teacher"></i></div>
                <div class="floating-element"><i class="fas fa-graduation-cap"></i></div>
                <div class="floating-element"><i class="fas fa-book-open"></i></div>
                <div class="floating-element"><i class="fas fa-award"></i></div>
            </div>
            <div class="container">
                <div class="row justify-content-center text-center">
                    <div class="col-lg-8">
                        <c:choose>
                            <c:when test="${selectedUser.avatar != null}">
                                <img src="${pageContext.request.contextPath}${selectedUser.avatar}"
                                     alt="${selectedUser.fullName}" class="profile-avatar mb-3">
                            </c:when>
                            <c:otherwise>
                                <img src="https://ui-avatars.com/api/?name=${selectedUser.fullName}&background=random"
                                     alt="${selectedUser.fullName}" class="profile-avatar mb-3">
                            </c:otherwise>
                        </c:choose>
                        <h1 class="profile-name">${selectedUser.fullName}</h1>
                        <div class="profile-role">
                            <i class="fas fa-chalkboard-teacher me-2"></i>
                            Instructor
                        </div>
                        <p class="text-white-50">
                            <c:if test="${not empty selectedUser.specialization}">
                                <span class="me-3"><i class="fas fa-star me-1"></i>
                                    ${selectedUser.specialization}</span>
                                </c:if>
                            <span><i class="fas fa-calendar-alt me-1"></i> Instructor since
                                <fmt:formatDate value="${selectedUser.approvalDate}" pattern="MMMM yyyy" />
                            </span>
                        </p>
                    </div>
                </div>
            </div>
        </header>

        <div class="container mb-5">
            <!-- Instructor Stats -->
            <div class="row mb-5">
                <div class="col-md-4">
                    <div class="card instructor-stats">
                        <div class="stat-number">${totalCourses}</div>
                        <div class="stat-label">Courses Created</div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card instructor-stats">
                        <div class="stat-number">
                            <fmt:formatNumber value="${averageRating}" maxFractionDigits="1" />
                        </div>
                        <div class="stat-label">Average Rating</div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card instructor-stats">
                        <c:choose>
                            <c:when test="${selectedUser.totalStudents > 0}">
                                <div class="stat-number">${selectedUser.totalStudents}</div>
                            </c:when>
                            <c:otherwise>
                                <div class="stat-number">0</div>
                            </c:otherwise>
                        </c:choose>
                        <div class="stat-label">Total Students</div>
                    </div>
                </div>
            </div>

            <!-- About Instructor -->
            <div class="card mb-5">
                <div class="card-body p-4">
                    <h3 class="card-title mb-4">
                        <i class="fas fa-user-circle me-2 text-primary"></i>
                        About ${selectedUser.name}
                    </h3>
                    <p class="card-text">
                        <c:choose>
                            <c:when test="${not empty selectedUser.biography}">
                                ${selectedUser.biography}
                            </c:when>
                            <c:otherwise>
                                This instructor has not added a biography yet.
                            </c:otherwise>
                        </c:choose>
                    </p>
                </div>
            </div>

            <!-- Instructor's Courses -->
            <h3 class="mb-4">
                <i class="fas fa-book me-2 text-primary"></i>
                Courses by ${selectedUser.fullName}
            </h3>

            <div class="row">
                <c:choose>
                    <c:when test="${empty courses}">
                        <div class="col-12">
                            <div class="alert alert-info" role="alert">
                                <i class="fas fa-info-circle me-2"></i> This instructor has not published any
                                courses yet.
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="course" items="${courses}">
                            <div class="col-md-6 col-lg-4 mb-4">
                                <div class="card course-card h-100">
                                    <c:choose>
                                        <c:when test="${not empty course.imageUrl}">
                                            <img src="${pageContext.request.contextPath}/${course.imageUrl}" class="card-img-top course-img" alt="${course.name}" />
                                        </c:when>
                                        <c:otherwise>
                                            <img src="${pageContext.request.contextPath}/assets/images/default-course.jpg" class="card-img-top course-img" alt="No image" />
                                        </c:otherwise>
                                    </c:choose>


                                    <div class="card-body d-flex flex-column">
                                        <h5 class="card-title">${course.name}</h5>

                                        <div class="mb-3">
                                            <span
                                                class="badge level ${course.level.toLowerCase()}">${course.level}</span>
                                            <span class="course-duration"><i class="far fa-clock me-1"></i>
                                                ${course.duration}</span>
                                        </div>

                                        <p class="card-text">
                                            <c:choose>
                                                <c:when test="${not empty course.description && fn:length(course.description) > 100}">
                                                    ${fn:substring(course.description, 0, 100)}...
                                                </c:when>
                                                <c:when test="${not empty course.description}">
                                                    ${course.description}
                                                </c:when>
                                                <c:otherwise>
                                                    No description available.
                                                </c:otherwise>
                                            </c:choose>
                                        </p>


                                        <div class="d-flex justify-content-between align-items-center mt-auto">
                                            <div class="rating-stars small">
                                                <c:forEach begin="1" end="5" var="i">
                                                    <c:choose>
                                                        <c:when test="${i <= course.averageRating}">
                                                            <i class="fas fa-star"></i>
                                                        </c:when>
                                                        <c:when
                                                            test="${i > course.averageRating && i - 1 < course.averageRating}">
                                                            <i class="fas fa-star-half-alt"></i>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <i class="far fa-star"></i>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:forEach>
                                                <span class="text-muted ms-1">(${course.ratingCount})</span>
                                            </div>
                                            <span class="course-price">
                                                <c:choose>
                                                    <c:when test="${course.price == 0}">Free</c:when>
                                                    <c:otherwise>
                                                        <fmt:formatNumber value="${course.price}" type="number" groupingUsed="true" var="formattedPrice" />
                                                        ${fn:replace(formattedPrice, ",", ".")}đ
                                                    </c:otherwise>
                                                </c:choose>
                                            </span>


                                        </div>

                                        <a href="${pageContext.request.contextPath}/course/${course.courseID}"
                                           class="btn btn-primary mt-3">
                                            View Course
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Include footer -->
        <jsp:include page="/WEB-INF/views/customer/common/footer.jsp"/>
        <!-- Common Scripts -->
        <jsp:include page="/WEB-INF/views/customer/common/scripts.jsp" />
    </body>
</html>
