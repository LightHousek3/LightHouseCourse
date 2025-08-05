<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <!DOCTYPE html>
            <html>

            <head>
                <title>My Courses - LightHouse</title>
                <!-- Include common head with styles -->
                <jsp:include page="/WEB-INF/views/customer/common/head.jsp" />
                <style>
                    .course-card {
                        height: 100%;
                        transition: transform 0.3s;
                    }

                    .course-card:hover {
                        transform: translateY(-5px);
                    }

                    .progress {
                        height: 10px;
                    }

                    .badge-level {
                        position: absolute;
                        top: 10px;
                        right: 10px;
                        z-index: 1;
                    }

                    .course-image {
                        height: 160px;
                        object-fit: cover;
                    }

                    .progress-container .progress-bar {
                        background-color: #198754;
                    }

                    /* Progress bar width classes */
                    .progress-0 {
                        width: 0%;
                    }

                    .progress-10 {
                        width: 10%;
                    }

                    .progress-20 {
                        width: 20%;
                    }

                    .progress-30 {
                        width: 30%;
                    }

                    .progress-40 {
                        width: 40%;
                    }

                    .progress-50 {
                        width: 50%;
                    }

                    .progress-60 {
                        width: 60%;
                    }

                    .progress-70 {
                        width: 70%;
                    }

                    .progress-80 {
                        width: 80%;
                    }

                    .progress-90 {
                        width: 90%;
                    }

                    .progress-100 {
                        width: 100%;
                    }
                </style>
            </head>

            <body>
                <!-- Include navigation -->
                <jsp:include page="/WEB-INF/views/customer/common/navigation.jsp" />

                <div class="container my-4">
                    <h2>My Courses</h2>
                    <c:choose>
                        <c:when test="${empty purchasedCourses}">
                            <div class="alert alert-info" role="alert">
                                <h4 class="alert-heading">No courses purchased yet!</h4>
                                <p>You haven't purchased any courses yet. Browse our catalog to find courses that
                                    interest you.</p>
                                <hr>
                                <a href="${pageContext.request.contextPath}/home?scroll=true"
                                    class="btn btn-primary">Browse
                                    Courses</a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
                                <c:forEach var="i" begin="0" end="${purchasedCourses.size() - 1}">
                                    <c:set var="course" value="${purchasedCourses[i]}" />
                                    <c:set var="orderDetail" value="${orderDetails[i]}" />
                                    <c:set var="progress" value="${progressList[i]}" />

                                    <div class="col">
                                        <div class="card course-card shadow h-100 position-relative">
                                            <c:if test="${not empty course.level}">
                                                <span class="badge badge-level
                                              ${course.level eq 'Beginner' ? 'bg-success' : 
                                                course.level eq 'Intermediate' ? 'bg-warning' : 'bg-danger'}">
                                                    ${course.level}
                                                </span>
                                            </c:if>
                                            <img src="${pageContext.request.contextPath}/${not empty course.imageUrl ? course.imageUrl : 'assets/images/course-placeholder.jpg'}"
                                                class="card-img-top course-image" alt="${course.name}">
                                            <div class="card-body d-flex flex-column">
                                                <h5 class="card-title">${course.name}</h5>
                                                <p class="card-text text-muted mb-1">
                                                    <c:choose>
                                                        <c:when
                                                            test="${not empty course.instructors && not empty course.instructors[0]}">
                                                            <c:forEach var="instructor" items="${course.instructors}"
                                                                varStatus="status">
                                                                ${instructor.fullName}<c:if test="${!status.last}">, </c:if>
                                                            </c:forEach>
                                                        </c:when>
                                                        <c:otherwise>
                                                            No instructor assigned
                                                        </c:otherwise>
                                                    </c:choose>
                                                </p>

                                                <!-- Course progress section -->
                                                <div class="progress-container">
                                                    <div class="progress mt-2 mb-2">
                                                        <fmt:formatNumber var="progressValue"
                                                            value="${progress.completionPercentage}" pattern="0.##" />
                                                        <fmt:formatNumber var="progressRounded"
                                                            value="${(progressValue - progressValue % 10)}"
                                                            pattern="0" />
                                                        <div class="progress-bar progress-${progressRounded}"
                                                            role="progressbar" aria-valuenow="${progressValue}"
                                                            aria-valuemin="0" aria-valuemax="100">
                                                        </div>
                                                    </div>
                                                    <div class="d-flex justify-content-between align-items-center mb-3">
                                                        <small class="text-muted">${progressValue}%
                                                            complete</small>
                                                        <c:if test="${progressValue < 100.00}">
                                                            <span class="badge bg-warning text-dark">In Progress</span>
                                                        </c:if>
                                                        <c:if test="${progressValue eq 100.00}">
                                                            <span class="badge bg-success">Completed</span>
                                                        </c:if>
                                                    </div>
                                                </div>

                                                <div class="mt-auto">
                                                    <a href="${pageContext.request.contextPath}/learning/${course.courseID}"
                                                        class="btn btn-primary btn-sm w-100">Continue Learning</a>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Include footer -->
                <jsp:include page="/WEB-INF/views/customer/common/footer.jsp" />

                <!-- Custom js -->
                <jsp:include page="/WEB-INF/views/customer/common/scripts.jsp" />
            </body>

            </html>