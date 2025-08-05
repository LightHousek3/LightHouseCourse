<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="en">

    <head>
        <title>Course Details - Admin Dashboard</title>
        <jsp:include page="../common/head.jsp" />
        <style>
            .course-header-img {
                width: 100%;
                height: 300px;
                object-fit: cover;
                border-radius: 8px;
            }

            .badge.pending {
                background-color: #ffc107;
                color: #212529;
            }

            .badge.approved {
                background-color: #28a745;
            }

            .badge.rejected {
                background-color: #dc3545;
            }

            .lesson-item {
                border-left: 3px solid #3498db;
                padding-left: 15px;
                margin-bottom: 15px;
            }

            .lesson-content-item {
                border-left: 2px solid #6c757d;
                margin-left: 15px;
                padding-left: 15px;
                margin-bottom: 10px;
                margin-top: 10px;
            }

            .content-item-icon {
                display: inline-flex;
                align-items: center;
                justify-content: center;
                width: 24px;
                height: 24px;
                border-radius: 50%;
                margin-right: 8px;
                color: white;
                font-size: 12px;
            }

            .icon-video {
                background-color: #e74c3c;
            }

            .icon-material {
                background-color: #3498db;
            }

            .icon-quiz {
                background-color: #2ecc71;
            }

            .approval-actions {
                position: sticky;
                bottom: 0;
                background: white;
                border-top: 1px solid #e0e0e0;
                padding: 15px 0;
                z-index: 100;
            }

            .lesson-accordion {
                margin-bottom: 10px;
            }

            .lesson-header {
                background-color: #f8f9fa;
                border-radius: 6px;
                padding: 12px 16px;
                cursor: pointer;
                transition: all 0.2s;
                display: flex;
                justify-content: space-between;
                align-items: center;
            }

            .lesson-header:hover {
                background-color: #e9ecef;
            }

            .lesson-content {
                padding: 15px;
                border: 1px solid #e9ecef;
                border-top: none;
                border-radius: 0 0 6px 6px;
                display: block;
                /* Always show lesson content */
            }

            .lesson-header {
                border-radius: 6px 6px 0 0;
                border-bottom: none;
            }

            .content-details {
                background-color: #f8f9fa;
                border-radius: 4px;
                padding: 10px;
                margin-top: 8px;
                font-size: 0.9rem;
            }

            .content-details p:last-child {
                margin-bottom: 0;
            }

            .toggle-icon {
                transition: transform 0.3s;
            }

            .lesson-header.active .toggle-icon {
                transform: rotate(180deg);
            }

            .video-container {
                margin-top: 15px;
                border-radius: 8px;
                overflow: hidden;
                position: relative;
                padding-top: 56.25%;
                /* 16:9 aspect ratio */
            }

            .video-container video,
            .video-container iframe {
                position: absolute;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                border: none;
            }

            .quiz-question {
                background-color: #f8f9fa;
                border-radius: 8px;
                padding: 15px;
                margin-bottom: 15px;
                border-left: 3px solid #2ecc71;
            }

            .quiz-answer {
                display: flex;
                align-items: flex-start;
                margin-bottom: 8px;
                padding: 8px;
                border-radius: 4px;
            }

            .quiz-answer.correct {
                background-color: rgba(46, 204, 113, 0.1);
            }

            .quiz-answer.correct .answer-indicator {
                color: #2ecc71;
            }

            .answer-indicator {
                margin-right: 10px;
                display: inline-flex;
                align-items: center;
                justify-content: center;
                min-width: 20px;
            }

            .material-content {
                margin-top: 15px;
                padding: 20px;
                background-color: #f8f9fa;
                border-radius: 8px;
                border-left: 3px solid #3498db;
            }

            .material-content img {
                max-width: 100%;
                height: auto;
                margin: 10px 0;
            }

            .content-preview-btn {
                margin-top: 10px;
            }

            .content-preview {
                max-height: 500px;
                overflow-y: auto;
                border: 1px solid #dee2e6;
                border-radius: 4px;
                padding: 15px;
                margin-top: 15px;
            }
        </style>
    </head>

    <body>
        <!-- Admin Sidebar -->
        <c:set var="activeMenu" value="courses" scope="request" />
        <jsp:include page="/WEB-INF/views/admin/common/sidebar.jsp" />

        <!-- Admin Content -->
        <div class="admin-content">
            <!-- Header -->
            <div class="admin-header d-flex justify-content-between align-items-center">
                <button class="btn d-lg-none" id="toggleSidebarBtn">
                    <i class="fas fa-bars"></i>
                </button>
                <div>
                    <h2 class="mb-0 d-none d-lg-block">Course Details</h2>
                    <p class="text-muted mb-0 d-none d-lg-block">ID: ${course.courseID}</p>
                </div>
                <div class="d-flex align-items-center">
                    <a href="${pageContext.request.contextPath}/admin/courses"
                       class="btn btn-lg btn-primary">
                        <i class="fas fa-arrow-left me-2"></i> Back
                    </a>
                </div>
            </div>

            <!-- Course Details -->
            <div class="row">
                <div class="col-lg-8">
                    <!-- Course Header -->
                    <div class="card mb-4">
                        <div class="card-body">
                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <h3 class="card-title fw-bold">${course.name}</h3>
                                <div>
                                    <c:choose>
                                        <c:when test="${course.approvalStatus eq 'pending'}">
                                            <span class="badge pending">Pending Approval</span>
                                        </c:when>
                                        <c:when test="${course.approvalStatus eq 'approved'}">
                                            <span class="status-badge status-approved">Approved</span>
                                        </c:when>
                                        <c:when test="${course.approvalStatus eq 'rejected'}">
                                            <span class="status-badge status-rejected">Rejected</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-secondary">Unknown</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>

                            <c:if test="${not empty course.imageUrl}">
                                <img src="${pageContext.request.contextPath}/${course.imageUrl}"
                                     alt="${course.name}" class="course-header-img mb-4">
                            </c:if>

                            <h4>Description</h4>
                            <p>${course.description}</p>
                        </div>
                    </div>

                    <!-- Course Curriculum -->
                    <div class="card mb-4">
                        <div class="card-header">
                            <h4 class="mb-0">Course Curriculum</h4>
                        </div>
                        <div class="card-body">
                            <c:choose>
                                <c:when test="${empty course.lessons}">
                                    <p class="text-muted text-center py-4">No lessons have been added to
                                        this
                                        course yet.</p>
                                    </c:when>
                                    <c:otherwise>
                                    <div class="curriculum-container">
                                        <c:forEach var="lesson" items="${course.lessons}"
                                                   varStatus="lessonStatus">
                                            <div class="lesson-accordion">
                                                <div class="lesson-header" data-bs-toggle="collapse" data-bs-target="#Lesson${lesson.lessonID}">
                                                    <div>
                                                        <span class="fw-bold">${lessonStatus.index + 1}.
                                                            ${lesson.title}</span>
                                                            <c:if test="${not empty lesson.lessonItems}">
                                                            <span
                                                                class="ms-2 badge bg-secondary">${lesson.lessonItems.size()}
                                                                items</span>
                                                            </c:if>
                                                    </div>
                                                    <i class="fas fa-chevron-down toggle-icon"></i>
                                                </div>
                                                <div class="lesson-content collapse" id="Lesson${lesson.lessonID}">
                                                    <c:choose>
                                                        <c:when test="${empty lesson.lessonItems}">
                                                            <p class="text-muted fst-italic">This lesson has
                                                                no
                                                                content items.</p>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <c:forEach var="item"
                                                                           items="${lesson.lessonItems}"
                                                                           varStatus="itemStatus">
                                                                <div class="lesson-content-item">
                                                                    <c:choose>
                                                                        <c:when
                                                                            test="${fn:toLowerCase(item.itemType) eq 'video'}">
                                                                            <div
                                                                                class="content-item-icon icon-video">
                                                                                <i class="fas fa-play"></i>
                                                                            </div>
                                                                            <span class="fw-semibold">Video:
                                                                                <c:choose>
                                                                                    <c:when
                                                                                        test="${not empty item.item}">
                                                                                        ${item.item.title}
                                                                                    </c:when>
                                                                                    <c:otherwise>
                                                                                        Video
                                                                                        #${item.itemID}
                                                                                    </c:otherwise>
                                                                                </c:choose>
                                                                            </span>

                                                                            <c:if
                                                                                test="${not empty item.item}">
                                                                                <div
                                                                                    class="content-details">
                                                                                    <p><strong>Duration:</strong>
                                                                                        ${item.item.duration}
                                                                                        seconds</p>
                                                                                    <p><strong>Description:</strong>
                                                                                        ${item.item.description}
                                                                                    </p>

                                                                                    <!-- Video Player -->
                                                                                    <div
                                                                                        class="video-container">
                                                                                        <!-- Direct Video Player -->
                                                                                        <video controls
                                                                                               width="100%">
                                                                                            <source
                                                                                                src="${pageContext.request.contextPath}/${item.item.videoUrl}"
                                                                                                type="video/mp4">
                                                                                            Your browser
                                                                                            does
                                                                                            not support the
                                                                                            video tag.
                                                                                        </video>
                                                                                    </div>
                                                                                </div>
                                                                            </c:if>
                                                                        </c:when>
                                                                        <c:when
                                                                            test="${fn:toLowerCase(item.itemType) eq 'material'}">
                                                                            <div
                                                                                class="content-item-icon icon-material">
                                                                                <i
                                                                                    class="fas fa-file-alt"></i>
                                                                            </div>
                                                                            <span
                                                                                class="fw-semibold">Material:
                                                                                <c:choose>
                                                                                    <c:when
                                                                                        test="${not empty item.item}">
                                                                                        ${item.item.title}
                                                                                    </c:when>
                                                                                    <c:otherwise>
                                                                                        Material
                                                                                        #${item.itemID}
                                                                                    </c:otherwise>
                                                                                </c:choose>
                                                                            </span>

                                                                            <c:if
                                                                                test="${not empty item.item}">
                                                                                <div
                                                                                    class="content-details">
                                                                                    <p><strong>Description:</strong>
                                                                                        ${item.item.description}
                                                                                    </p>

                                                                                    <c:if
                                                                                        test="${not empty item.item.fileUrl}">
                                                                                        <p class="d-flex align-items-center gap-2">
                                                                                            <strong>File:</strong>
                                                                                            <a href="${pageContext.request.contextPath}/${item.item.fileUrl}"
                                                                                               target="_blank"
                                                                                               class="btn btn-lg btn-outline-primary me-2">
                                                                                                <i class="fas fa-eye me-1"></i>
                                                                                                View Material
                                                                                            </a>
                                                                                        </p>
                                                                                    </c:if>

                                                                                    <!-- Display Material Content -->
                                                                                    <c:if
                                                                                        test="${not empty item.item.content}">
                                                                                        <!-- Material content is displayed by default -->
                                                                                        <div id="material-${item.lessonItemID}"
                                                                                             class="content-preview">
                                                                                            <div
                                                                                                class="material-content">
                                                                                                ${item.item.content}
                                                                                            </div>
                                                                                        </div>
                                                                                    </c:if>
                                                                                </div>
                                                                            </c:if>
                                                                        </c:when>
                                                                        <c:when
                                                                            test="${fn:toLowerCase(item.itemType) eq 'quiz'}">
                                                                            <div
                                                                                class="content-item-icon icon-quiz">
                                                                                <i
                                                                                    class="fas fa-question"></i>
                                                                            </div>
                                                                            <span class="fw-semibold">Quiz:
                                                                                <c:choose>
                                                                                    <c:when
                                                                                        test="${not empty item.item}">
                                                                                        ${item.item.title}
                                                                                    </c:when>
                                                                                    <c:otherwise>
                                                                                        Quiz #${item.itemID}
                                                                                    </c:otherwise>
                                                                                </c:choose>
                                                                            </span>

                                                                            <c:if
                                                                                test="${not empty item.item}">
                                                                                <div
                                                                                    class="content-details">
                                                                                    <p><strong>Description:</strong>
                                                                                        ${item.item.description}
                                                                                    </p>
                                                                                    <p><strong>Time
                                                                                            Limit:</strong>
                                                                                        <c:if test="${not empty item.item.timeLimit}">${item.item.timeLimit} seconds</c:if>
                                                                                        <c:if test="${empty item.item.timeLimit}">no time limit</c:if>
                                                                                        </p>
                                                                                        <p><strong>Passing
                                                                                                Score:</strong>
                                                                                        ${item.item.passingScore}%
                                                                                    </p>

                                                                                    <!-- Quiz questions are displayed by default -->
                                                                                    <div id="quiz-${item.lessonItemID}"
                                                                                         class="content-preview">
                                                                                        <c:if
                                                                                            test="${not empty item.item.questions}">
                                                                                            <c:forEach
                                                                                                var="question"
                                                                                                items="${item.item.questions}"
                                                                                                varStatus="qStatus">
                                                                                                <div
                                                                                                    class="quiz-question">
                                                                                                    <h6
                                                                                                        class="fw-bold">
                                                                                                        Question
                                                                                                        ${qStatus.index
                                                                                                          +
                                                                                                          1}:
                                                                                                          ${question.content}
                                                                                                        </h6>

                                                                                                        <div
                                                                                                            class="mt-3">
                                                                                                            <c:forEach
                                                                                                                var="answer"
                                                                                                                items="${question.answers}"
                                                                                                                varStatus="aStatus">
                                                                                                                <div
                                                                                                                    class="quiz-answer ${answer.correct ? 'correct' : ''}">
                                                                                                                    <div
                                                                                                                        class="answer-indicator">
                                                                                                                        <c:choose>
                                                                                                                            <c:when
                                                                                                                                test="${answer.correct}">
                                                                                                                                <i
                                                                                                                                    class="fas fa-check-circle"></i>
                                                                                                                            </c:when>
                                                                                                                            <c:otherwise>
                                                                                                                                ${aStatus.index
                                                                                                                                  +
                                                                                                                                  1}.
                                                                                                                            </c:otherwise>
                                                                                                                        </c:choose>
                                                                                                                    </div>
                                                                                                                    <div>
                                                                                                                        <c:out value="${answer.content}" />
                                                                                                                    </div>
                                                                                                                </div>
                                                                                                            </c:forEach>
                                                                                                        </div>

                                                                                                    </div>
                                                                                                </c:forEach>
                                                                                            </c:if>

                                                                                            <c:if
                                                                                                test="${empty item.item.questions}">
                                                                                                <p
                                                                                                    class="text-muted fst-italic">
                                                                                                    No questions
                                                                                                    available
                                                                                                    for
                                                                                                    this quiz.
                                                                                                </p>
                                                                                            </c:if>
                                                                                        </div>
                                                                                    </div>
                                                                                </c:if>
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <div
                                                                                    class="content-item-icon bg-secondary">
                                                                                    <i class="fas fa-file"></i>
                                                                                </div>
                                                                                <span
                                                                                    class="fw-semibold">Unknown
                                                                                    Type: ${item.itemType}
                                                                                    #${item.itemID}</span>
                                                                                <div class="content-details">
                                                                                    <p>Error: This item type
                                                                                        could
                                                                                        not be processed
                                                                                        correctly.
                                                                                    </p>
                                                                                    <p>Debug info:</p>
                                                                                    <ul>
                                                                                        <li>Item ID:
                                                                                            ${item.itemID}
                                                                                        </li>
                                                                                        <li>Item Type:
                                                                                            ${item.itemType}
                                                                                        </li>
                                                                                        <li>Item object present:
                                                                                            ${not empty
                                                                                              item.item}
                                                                                        </li>
                                                                                        <li>Item class: ${not
                                                                                                          empty
                                                                                                          item.item ?
                                                                                                          item.item.getClass().getName()
                                                                                                          : 'null'}</li>
                                                                                    </ul>
                                                                                </div>
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </div>
                                                                </c:forEach>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                        <!-- Rejection Reason (if rejected) -->
                        <c:if test="${course.approvalStatus eq 'rejected' && not empty course.rejectionReason}">
                            <div class="card mb-4 border-danger">
                                <div class="card-header bg-danger text-white">
                                    <h4 class="mb-0">Rejection Reason</h4>
                                </div>
                                <div class="card-body">
                                    <p>${course.rejectionReason}</p>
                                </div>
                            </div>
                        </c:if>
                    </div>

                    <div class="col-lg-4">
                        <!-- Course Meta Information -->
                        <div class="card mb-4">
                            <div class="card-header">
                                <h4 class="mb-0">Course Information</h4>
                            </div>
                            <div class="card-body">
                                <div class="mb-3">
                                    <strong>Price:</strong>
                                    <span class="fs-4 fw-bold text-primary">
                                        <fmt:formatNumber value="${course.price}" type="number" />đ
                                    </span>
                                </div>

                                <div class="mb-3">
                                    <strong>Instructor:</strong>
                                    <p class="mb-0">
                                        <c:forEach var="i" items="${course.instructors}" varStatus="status">
                                            ${i.name}<c:if test="${!status.last}">, </c:if>
                                        </c:forEach>
                                    </p>
                                </div>

                                <div class="mb-3">
                                    <strong>Categories:</strong>
                                    <div>
                                        <c:forEach var="category" items="${course.categories}" varStatus="loop">
                                            <span class="badge bg-light text-dark">${category.name}</span>
                                        </c:forEach>
                                    </div>
                                </div>

                                <div class="mb-3">
                                    <strong>Level:</strong>
                                    <p class="mb-0">${course.level}</p>
                                </div>

                                <div class="mb-3">
                                    <strong>Duration:</strong>
                                    <p class="mb-0">${course.duration}</p>
                                </div>

                                <div class="mb-3">
                                    <strong>Submission Date:</strong>
                                    <p class="mb-0">
                                        <fmt:formatDate value="${course.submissionDate}" pattern="dd/MM/yyyy HH:mm" />
                                    </p>
                                </div>                                            
                                <div class="mb-3">
                                    <strong>Approved on:</strong>
                                    <p class="mb-0">
                                        <fmt:formatDate value="${course.approvalDate}"
                                                        pattern="dd/MM/yyyy HH:mm" />                                            
                                    </p>
                                </div> 
                            </div>
                        </div>

                        <!-- Course Statistics -->
                        <div class="card mb-4">
                            <div class="card-header">
                                <h4 class="mb-0">Course Content Summary</h4>
                            </div>
                            <div class="card-body">
                                <div class="d-flex justify-content-between mb-2">
                                    <strong>Total Lessons:</strong>
                                    <span>${course.lessons.size()}</span>
                                </div>

                                <c:set var="totalItems" value="0" />
                                <c:set var="videoCount" value="0" />
                                <c:set var="materialCount" value="0" />
                                <c:set var="quizCount" value="0" />

                                <c:forEach var="lesson" items="${course.lessons}">
                                    <c:set var="totalItems" value="${totalItems + lesson.lessonItems.size()}" />
                                    <c:forEach var="item" items="${lesson.lessonItems}">
                                        <c:choose>
                                            <c:when test="${fn:toLowerCase(item.itemType) eq 'video'}">
                                                <c:set var="videoCount" value="${videoCount + 1}" />
                                            </c:when>
                                            <c:when test="${fn:toLowerCase(item.itemType) eq 'material'}">
                                                <c:set var="materialCount" value="${materialCount + 1}" />
                                            </c:when>
                                            <c:when test="${fn:toLowerCase(item.itemType) eq 'quiz'}">
                                                <c:set var="quizCount" value="${quizCount + 1}" />
                                            </c:when>
                                        </c:choose>
                                    </c:forEach>
                                </c:forEach>

                                <div class="d-flex justify-content-between mb-2">
                                    <strong>Total Content Items:</strong>
                                    <span>${totalItems}</span>
                                </div>

                                <div class="d-flex justify-content-between mb-2">
                                    <div><i class="fas fa-play me-2 text-danger"></i> Videos:</div>
                                    <span>${videoCount}</span>
                                </div>

                                <div class="d-flex justify-content-between mb-2">
                                    <div><i class="fas fa-file-alt me-2 text-primary"></i> Materials:</div>
                                    <span>${materialCount}</span>
                                </div>

                                <div class="d-flex justify-content-between mb-2">
                                    <div><i class="fas fa-question me-2 text-success"></i> Quizzes:</div>
                                    <span>${quizCount}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Approval Actions (for pending courses only) -->
                <c:if test="${course.approvalStatus eq 'pending'}">
                    <div class="approval-actions">
                        <div class="container-fluid">
                            <div class="d-flex justify-content-end">
                                <a href="${pageContext.request.contextPath}/admin/course/reject/${course.courseID}"
                                   class="btn btn-md btn-danger me-2">
                                    <i class="fas fa-times me-2"></i> Reject
                                </a>
                                <form method="post" action="${pageContext.request.contextPath}/admin/course/approve" style="display: inline;">
                                    <input type="hidden" name="courseID" value="${course.courseID}"/>
                                    <button type="submit" class="btn btn-md btn-success">
                                        <i class="fas fa-check me-2"></i> Approve
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>
                </c:if>
            </div>

            <jsp:include page="../common/scripts.jsp" />
            <script>
                // All content is displayed by default for admin review
                document.addEventListener('DOMContentLoaded', function () {
                    // Add active class to all lesson headers
                    const lessonHeaders = document.querySelectorAll('.lesson-header');
                    lessonHeaders.forEach(header => {
                        header.classList.add('active');
                    });
                });
                // đóng mở
                document.addEventListener('DOMContentLoaded', () => {
                    document.querySelectorAll('.lesson-header').forEach(header => {
                        const icon = header.querySelector('.toggle-icon');
                        const targetId = header.getAttribute('data-bs-target');
                        const collapse = document.querySelector(targetId);

                        if (collapse) {
                            collapse.addEventListener('show.bs.collapse', () => {
                                icon.classList.remove('fa-chevron-down');
                                icon.classList.add('fa-chevron-up');
                            });

                            collapse.addEventListener('hide.bs.collapse', () => {
                                icon.classList.remove('fa-chevron-up');
                                icon.classList.add('fa-chevron-down');
                            });
                        }
                    });
                });


            </script>
        </body>

    </html>