<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">

    <head>
        <title>Reject Course - Admin Dashboard</title>
        <jsp:include page="../common/head.jsp" />
        <style>
            .course-info {
                padding: 15px;
                border-left: 4px solid #dc3545;
                background-color: #f8f9fa;
                margin-bottom: 20px;
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
                <h2 class="mb-0 d-none d-lg-block">Reject Course</h2>
                <div class="d-flex align-items-center">
                    <a href="${pageContext.request.contextPath}/admin/course/view/${course.courseID}"
                       class="btn btn-lg btn-primary me-2">
                        <i class="fas fa-arrow-left me-2"></i> Back
                    </a>
                </div>
            </div>

            <!-- Course Info Summary -->
            <div class="course-info mb-4">
                <h4>${course.name}</h4>
                <p class="text-muted mb-0">Course ID: ${course.courseID} | Instructor: 
                    <c:forEach var="i" items="${course.instructors}" varStatus="status">
                        ${i.name}<c:if test="${!status.last}">, </c:if>
                    </c:forEach>
                </p>
            </div>

            <!-- Rejection Form -->
            <div class="card">
                <div class="card-header">
                    <h4 class="mb-0">Provide Rejection Reason</h4>
                </div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/admin/course/reject" method="post">
                        <input type="hidden" name="courseId" value="${course.courseID}">

                        <div class="mb-3">
                            <label for="rejectionReason" class="form-label">Rejection Reason <span
                                    class="text-danger">*</span></label>
                            <textarea class="form-control" id="rejectionReason" name="rejectionReason" rows="6"
                                      required
                                      placeholder="Please explain why this course is being rejected. This information will be visible to the instructor."></textarea>
                            <small class="text-muted">Provide clear, constructive feedback so the instructor can
                                address the issues.</small>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Common Reasons</label>
                            <div class="d-flex flex-wrap gap-2">
                                <button type="button" class="btn btn-lg btn-outline-secondary reason-btn"
                                        data-reason="Insufficient content quality. The course material does not meet our quality standards.">
                                    Content Quality
                                </button>
                                <button type="button" class="btn btn-lg btn-outline-secondary reason-btn"
                                        data-reason="Inadequate course structure. The curriculum needs better organization.">
                                    Poor Structure
                                </button>
                                <button type="button" class="btn btn-lg btn-outline-secondary reason-btn"
                                        data-reason="Copyright concerns. The course appears to contain copyrighted material without proper attribution or permissions.">
                                    Copyright Issues
                                </button>
                                <button type="button" class="btn btn-lg btn-outline-secondary reason-btn"
                                        data-reason="Technical problems with course materials. Please ensure all videos and resources are working properly.">
                                    Technical Issues
                                </button>
                                <button type="button" class="btn btn-lg btn-outline-secondary reason-btn"
                                        data-reason="Misleading course description or title. The content does not match what is promised.">
                                    Misleading Description
                                </button>
                            </div>
                        </div>

                        <div class="d-flex justify-content-end gap-2 mt-4">
                            <a href="${pageContext.request.contextPath}/admin/course/view/${course.courseID}"
                               class="btn btn-md btn-secondary">
                                Cancel
                            </a>
                            <button type="submit" class="btn btn-md btn-danger">
                                <i class="fas fa-times me-2"></i> Reject
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <jsp:include page="../common/scripts.jsp" />

        <script>
            // Add click handlers for quick reasons
            document.addEventListener('DOMContentLoaded', function () {
                const reasonBtns = document.querySelectorAll('.reason-btn');
                const rejectionReasonField = document.getElementById('rejectionReason');

                reasonBtns.forEach(btn => {
                    btn.addEventListener('click', function () {
                        rejectionReasonField.value = this.getAttribute('data-reason');
                        rejectionReasonField.focus();
                    });
                });
            });
        </script>
    </body>

</html>