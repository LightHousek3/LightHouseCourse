<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="vi_VN" />

<!DOCTYPE html>
<html lang="en">

    <head>
        <title>Student Progress - LightHouse</title>
        <jsp:include page="../common/head.jsp" />
        <style>
            .progress {
                height: 10px;
            }

            .student-info {
                background-color: #f8f9fa;
                border-radius: 10px;
                padding: 15px;
                margin-bottom: 20px;
            }

            .student-avatar {
                width: 80px;
                height: 80px;
                border-radius: 50%;
                object-fit: cover;
            }

            .course-title {
                font-size: 1.5rem;
                margin-bottom: 15px;
            }

            .lesson-card {
                margin-bottom: 15px;
                border-radius: 8px;
                overflow: hidden;
            }

            .lesson-header {
                cursor: pointer;
                padding: 15px;
            }

            .lesson-items {
                background-color: #f8f9fa;
                padding: 0 15px;
            }

            .completed {
                color: #28a745;
            }

            .pending {
                color: #ffc107;
            }

            .item-row {
                padding: 10px 0;
                border-bottom: 1px solid #eee;
            }

            .item-row:last-child {
                border-bottom: none;
            }

            .item-icon {
                width: 24px;
                text-align: center;
                margin-right: 10px;
            }

            .breadcrumb-item a {
                text-decoration: none;
            }
        </style>
    </head>

    <body>
        <%-- Instructor Sidebar --%>
        <c:set var="activeMenu" value="students" scope="request" />
        <jsp:include page="../common/sidebar.jsp" />

        <%-- Instructor Content --%>
        <div class="instructor-content">
            <!-- Header -->
            <div class="instructor-header d-flex justify-content-between align-items-center">
                <button class="btn d-lg-none" id="toggleSidebarBtn">
                    <i class="fas fa-bars"></i>
                </button>
                <h2 class="m-0 d-none d-lg-block">Student Progress</h2>
                <div class="d-flex align-items-center">

                    <div class="dropdown">
                        <div>
                            <a href="${pageContext.request.contextPath}/instructor/students"
                               class="btn btn-lg btn-primary">
                                <i class="fas fa-arrow-left me-2"></i> Back
                            </a>
                        </div>
                    </div>
                </div>
            </div>
            <!-- Success/Error Alerts -->
            <c:if test="${not empty success}">
                <div class="alert alert-success alert-dismissible fade show mt-3" role="alert">
                    ${success}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"
                            aria-label="Close"></button>
                </div>
            </c:if>

            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show mt-3" role="alert">
                    ${error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"
                            aria-label="Close"></button>
                </div>
            </c:if>
            <!-- Student Information -->
            <div class="card mb-4">
                <div class="card-body">
                    <div class="row g-3">
                        <!-- Student Basic Info -->
                        <div class="col-12 col-lg-8">
                            <div class="d-flex flex-column flex-sm-row align-items-start">
                                <!-- Avatar -->
                                <div class="mb-3 mb-sm-0 me-sm-3 align-self-center align-self-sm-start">
                                    <img src="${pageContext.request.contextPath}${student.avatar}"
                                         alt="Student Avatar" class="rounded-circle"
                                         style="width: 80px; height: 80px; object-fit: cover;">
                                </div>

                                <!-- Student Details -->
                                <div class="flex-grow-1">
                                    <h3 class="mb-2">${student.fullName}</h3>
                                    <div class="mb-2">
                                        <i class="fas fa-envelope text-muted me-2"></i>
                                        <span class="text-break">${student.email}</span>
                                    </div>
                                    <div class="mb-3">
                                        <i class="fas fa-phone text-muted me-2"></i>
                                        <span>${student.phone}</span>
                                    </div>
                                    <button class="btn btn-info btn-sm w-auto" data-bs-toggle="modal"
                                            data-bs-target="#emailModal"
                                            data-student-id="${student.customerID}"
                                            data-student-name="${student.fullName}"
                                            data-student-email="${student.email}">
                                        <i class="fas fa-envelope me-1"></i> Send Email
                                    </button>
                                </div>
                            </div>
                        </div>

                        <!-- Course Progress -->
                        <div class="col-12 col-lg-4">
                            <div class="h-100 d-flex flex-column justify-content-center">
                                <!-- Course Name -->
                                <h5 class="text-primary mb-3 text-center text-lg-end">${course.name}
                                </h5>

                                <!-- Progress Section -->
                                <div class="text-center text-lg-end">
                                    <!-- Progress Label and Percentage -->
                                    <div
                                        class="d-flex justify-content-between justify-content-lg-end align-items-center mb-2">
                                        <span class="fw-semibold">Overall Progress:</span>
                                        <span
                                            class="fw-bold text-primary ms-2">${courseProgress.completionPercentage}%</span>
                                    </div>

                                    <!-- Progress Bar -->
                                    <div class="progress mb-3" style="height: 10px;">
                                        <div class="progress-bar ${courseProgress.completionPercentage.intValue() == 100 ? 'bg-success' : 'bg-primary'}"
                                             role="progressbar"
                                             style="width: ${courseProgress.completionPercentage}%"
                                             aria-valuenow="${courseProgress.completionPercentage}"
                                             aria-valuemin="0" aria-valuemax="100"></div>
                                    </div>

                                    <!-- Status Badge -->
                                    <div class="mb-2">
                                        <span
                                            class="badge ${courseProgress.isCompleted ? 'bg-success' : 'bg-warning'}">
                                            ${courseProgress.isCompleted ? 'Completed' : 'In Progress'}
                                        </span>
                                    </div>

                                    <!-- Last Access -->
                                    <div>
                                        <small class="text-muted">
                                            Last accessed:
                                            <fmt:formatDate value="${courseProgress.lastAccessDate}"
                                                            pattern="dd/MM/yyyy HH:mm" />
                                        </small>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Lessons Progress -->
            <div class="card">
                <div class="card-header bg-white">
                    <h5 class="mb-0">Detailed Progress</h5>
                </div>
                <div class="card-body">
                    <div class="accordion" id="lessonsAccordion">
                        <c:forEach items="${lessonsProgress}" var="lessonData" varStatus="lessonStatus">
                            <div class="lesson-card card">
                                <div class="lesson-header card-header d-flex justify-content-between align-items-center"
                                     data-bs-toggle="collapse"
                                     data-bs-target="#lesson${lessonData.lesson.lessonID}">
                                    <div>
                                        <h6 class="mb-0">
                                            <span class="me-2">
                                                <c:choose>
                                                    <c:when test="${lessonData.progress.isCompleted}">
                                                        <i class="fas fa-check-circle completed"></i>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <i class="fas fa-circle-notch pending"></i>
                                                    </c:otherwise>
                                                </c:choose>
                                            </span>
                                            Lesson ${lessonStatus.index + 1}: ${lessonData.lesson.title}
                                        </h6>
                                    </div>
                                    <div class="d-flex align-items-center">
                                        <div class="progress me-2" style="width: 100px;">
                                            <c:set var="lessonProgress" value="0" />
                                            <c:if test="${lessonData.progress != null}">
                                                <c:set var="lessonProgress"
                                                       value="${lessonData.progress.completionPercentage}" />
                                            </c:if>
                                            <div class="progress-bar ${lessonProgress == 100 ? 'bg-success' : 'bg-primary'}"
                                                 role="progressbar" style="width: ${lessonProgress}%"
                                                 aria-valuenow="${lessonProgress}" aria-valuemin="0"
                                                 aria-valuemax="100"></div>
                                        </div>
                                        <span>
                                            <fmt:formatNumber value="${lessonProgress}"
                                                              maxFractionDigits="0" />%
                                        </span>
                                        <span class="ms-3"><i class="fas fa-chevron-down"></i></span>
                                    </div>
                                </div>
                                <div id="lesson${lessonData.lesson.lessonID}" class="collapse"
                                     data-bs-parent="#lessonsAccordion">
                                    <div class="lesson-items card-body">
                                        <c:forEach items="${lessonData.items}" var="itemData"
                                                   varStatus="itemStatus">
                                            <div
                                                class="item-row d-flex justify-content-between align-items-center">
                                                <div>
                                                    <span class="item-icon">
                                                        <c:choose>
                                                            <c:when
                                                                test="${itemData.item.itemType eq 'video'}">
                                                                <i class="fas fa-video"></i>
                                                            </c:when>
                                                            <c:when
                                                                test="${itemData.item.itemType eq 'quiz'}">
                                                                <i class="fas fa-question-circle"></i>
                                                            </c:when>
                                                            <c:when
                                                                test="${itemData.item.itemType eq 'material'}">
                                                                <i class="fas fa-file-alt"></i>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <i class="fas fa-tasks"></i>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </span>
                                                    <span>${itemData.item.title}</span>
                                                </div>
                                                <div>
                                                    <c:choose>
                                                        <c:when test="${itemData.progress.isCompleted}">
                                                            <span class="badge bg-success">
                                                                <i class="fas fa-check me-1"></i>
                                                                Completed
                                                            </span>
                                                            <c:if
                                                                test="${not empty itemData.progress.completionDate}">
                                                                <small class="text-muted ms-2">
                                                                    (
                                                                    <fmt:formatDate
                                                                        value="${itemData.progress.completionDate}"
                                                                        pattern="dd/MM/yyyy" />)
                                                                </small>
                                                            </c:if>
                                                        </c:when>
                                                        <c:when test="${not empty itemData.progress}">
                                                            <span class="badge bg-warning">In
                                                                Progress</span>
                                                            <small class="text-muted ms-2">
                                                                Last accessed:
                                                                <fmt:formatDate
                                                                    value="${itemData.progress.lastAccessDate}"
                                                                    pattern="dd/MM/yyyy" />
                                                            </small>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge bg-secondary">Not
                                                                Started</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </div>

            <!-- Email Modal -->
            <div class="modal fade" id="emailModal" tabindex="-1" aria-labelledby="emailModalLabel"
                 aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title" id="emailModalLabel">Send Email to Student</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"
                                    aria-label="Close"></button>
                        </div>
                        <form action="${pageContext.request.contextPath}/instructor/students"
                              method="post">
                            <div class="modal-body">
                                <input type="hidden" name="action" value="send-email">
                                <input type="hidden" name="studentId" id="modalStudentId"
                                       value="${student.customerID}">

                                <div class="mb-3">
                                    <label for="studentName" class="form-label">Student Name</label>
                                    <input type="text" class="form-control" id="modalStudentName"
                                           value="${student.fullName}" readonly>
                                </div>

                                <div class="mb-3">
                                    <label for="studentEmail" class="form-label">Student Email</label>
                                    <input type="email" class="form-control" id="modalStudentEmail"
                                           value="${student.email}" readonly>
                                </div>

                                <div class="mb-3">
                                    <label for="subject" class="form-label">Subject</label>
                                    <input type="text" class="form-control" id="subject" name="subject"
                                           required value="Regarding your progress in ${course.name}">
                                </div>

                                <div class="mb-3">
                                    <label for="message" class="form-label">Message</label>
                                    <textarea class="form-control" id="message" name="message" rows="5"
                                              required></textarea>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary w-auto"
                                        data-bs-dismiss="modal">Cancel</button>
                                <button type="submit" class="btn btn-primary w-auto">Send Email</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

        </div>

        <jsp:include page="../common/scripts.jsp" />

        <script>
            // Auto-hide alerts after 5 seconds
            setTimeout(function () {
                const alerts = document.querySelectorAll('.alert-dismissible');
                alerts.forEach(alert => {
                    const bsAlert = new bootstrap.Alert(alert);
                    bsAlert.close();
                });
            }, 5000);
        </script>
    </body>

</html>