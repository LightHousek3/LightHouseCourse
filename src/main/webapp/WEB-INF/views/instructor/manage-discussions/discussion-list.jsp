<%-- Document : discussion-list Created on : Jun 25, 2025, 10:30:00 AM Author : DangPH - CE180896 --%>

<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="en">

    <head>
        <title>Manage Discussions - Instructor Dashboard</title>
        <jsp:include page="../common/head.jsp" />
    </head>

    <body>
        <%-- Instructor Sidebar --%>
        <c:set var="activeMenu" value="discussions" scope="request" />
        <jsp:include page="../common/sidebar.jsp" />

        <%-- Instructor Content --%>
        <div class="instructor-content">
            <!-- Header -->
            <div class="instructor-header d-flex justify-content-between align-items-center">
                <button class="btn d-lg-none" id="toggleSidebarBtn">
                    <i class="fas fa-bars"></i>
                </button>
                <h2 class="m-0 d-none d-lg-block">Manage Discussions</h2>
                <div class="d-flex gap-2">
                    <a href="${pageContext.request.contextPath}/instructor/discussions"
                       class="btn btn-lg btn-primary">
                        <i class="fas fa-sync-alt me-2"></i> Reset Filters
                    </a>
                </div>
            </div>

            <!-- Content -->
            <!-- Notifications -->
            <c:if test="${param.success != null}">
                <div class="alert alert-success alert-dismissible fade show my-3" role="alert">
                    <c:choose>
                        <c:when test="${param.success eq 'reply_added'}">
                            <strong>Success!</strong> Your reply has been added.
                        </c:when>
                        <c:when test="${param.success eq 'marked_resolved'}">
                            <strong>Success!</strong> The discussion has been marked as
                            resolved.
                        </c:when>
                        <c:when test="${param.success eq 'marked_unresolved'}">
                            <strong>Success!</strong> The discussion has been marked as
                            unresolved.
                        </c:when>
                        <c:when test="${param.success eq 'marked_accepted'}">
                            <strong>Success!</strong> The answer has been marked as accepted.
                        </c:when>
                        <c:when test="${param.success eq 'unmarked_accepted'}">
                            <strong>Success!</strong> The answer has been unmarked as accepted.
                        </c:when>
                        <c:otherwise>
                            <strong>Success!</strong> Operation completed successfully.
                        </c:otherwise>
                    </c:choose>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"
                            aria-label="Close"></button>
                </div>
            </c:if>
            <c:if test="${param.error != null}">
                <div class="alert alert-danger alert-dismissible fade show my-3" role="alert">
                    <c:choose>
                        <c:when test="${param.error eq 'empty_content'}">
                            <strong>Error!</strong> Reply content cannot be empty.
                        </c:when>
                        <c:when test="${param.error eq 'reply_failed'}">
                            <strong>Error!</strong> Failed to add your reply.
                        </c:when>
                        <c:when test="${param.error eq 'update_failed'}">
                            <strong>Error!</strong> Failed to update discussion status.
                        </c:when>
                        <c:when test="${param.error eq 'not_authorized'}">
                            <strong>Error!</strong> You are not authorized to perform this
                            action.
                        </c:when>
                        <c:when test="${param.error eq 'discussion_not_found'}">
                            <strong>Error!</strong> Discussion not found.
                        </c:when>
                        <c:otherwise>
                            <strong>Error!</strong> An error occurred.
                        </c:otherwise>
                    </c:choose>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"
                            aria-label="Close"></button>
                </div>
            </c:if>

            <!-- Filters -->
            <div class="card mb-4">
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/instructor/discussions"
                          method="GET" class="row align-items-end g-3">
                        <div class="col-md-3">
                            <div class="mb-1">
                                Filter by Courses
                            </div>
                            <select class="form-select" name="courseId" id="courseSelect">
                                <option value="">All Courses</option>
                                <c:forEach var="course" items="${instructorCourses}">
                                    <option value="${course.courseID}" ${courseId==course.courseID
                                                     ? 'selected' : '' }>
                                                ${course.name}</option>
                                            </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-3">
                                <div class="mb-1">
                                    Filter by Lessons
                                </div>
                                <select class="form-select" name="lessonId" id="lessonSelect"
                                        ${courseId==null ? 'disabled' : '' }>
                                    <option value="">All Lessons</option>
                                    <c:if test="${courseId != null}">
                                        <c:forEach var="lesson" items="${lessons}">
                                            <option value="${lesson[0]}" ${lessonId==lesson[0]
                                                             ? 'selected' : '' }>${lesson[1]}</option>
                                        </c:forEach>
                                    </c:if>
                                </select>
                            </div>
                            <div class="col-md-3">
                                <div class="mb-1">
                                    Filter by Status
                                </div>
                                <select class="form-select" name="resolved">
                                    <option value="" ${resolved==null ? 'selected' : '' }>All
                                        Status</option>
                                    <option value="true" ${resolved==true ? 'selected' : '' }>
                                        Resolved</option>
                                    <option value="false" ${resolved==false ? 'selected' : '' }>
                                        Unresolved</option>
                                </select>
                            </div>
                            <div class="col-md-3">
                                <button type="submit" class="btn btn-md btn-primary w-100">Apply
                                    Filter</button>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Discussion List -->
                <div class="card">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h5 class="mb-0">Discussions (${totalDiscussions})</h5>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-hover align-middle">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Course</th>
                                        <th>Lesson</th>
                                        <th>Created</th>
                                        <th>Status</th>
                                        <th>Replies</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="discussion" items="${discussions}">
                                        <tr>
                                            <td>#${discussion.discussionID}</td>
                                            <td>${discussion.courseName}</td>
                                            <td>${discussion.lessonTitle}</td>
                                            <td>
                                                <fmt:formatDate value="${discussion.createdAt}"
                                                                pattern="yyyy-MM-dd HH:mm" />
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${discussion.isResolved}">
                                                        <span
                                                            class="status-badge status-resolved">Resolved</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span
                                                            class="status-badge status-unresolved">Unresolved</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>${discussion.replyCount}</td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/instructor/discussions/view/${discussion.discussionID}"
                                                   class="btn btn-sm btn-outline-primary">
                                                    <i class="fas fa-eye"></i>
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    <c:if test="${empty discussions}">
                                        <tr>
                                            <td colspan="8" class="text-center py-4">
                                                <i class="fas fa-comments fa-2x text-muted mb-3"></i>
                                                <p>No discussions found matching your criteria.</p>
                                            </td>
                                        </tr>
                                    </c:if>
                                </tbody>
                            </table>
                        </div>

                        <!-- Pagination -->
                        <c:if test="${totalPages > 1}">
                            <nav aria-label="Page navigation" class="mt-4">
                                <ul class="pagination">
                                    <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                        <a class="page-link btn btn-outline-primary" href="${pageContext.request.contextPath}/instructor/discussions?page=${currentPage - 1}${resolved != null ? '&resolved=' += resolved : ''}${courseId != null ? '&courseId=' += courseId : ''}${lessonId != null ? '&lessonId=' += lessonId : ''}">
                                            &laquo;
                                        </a>
                                    </li>
                                    <c:forEach begin="1" end="${totalPages}" var="i">
                                        <li class="page-item ${currentPage == i ? 'active' : ''}">
                                            <a class="page-link btn btn-outline-primary" href="${pageContext.request.contextPath}/instructor/discussions?page=${i}${resolved != null ? '&resolved=' += resolved : ''}${courseId != null ? '&courseId=' += courseId : ''}${lessonId != null ? '&lessonId=' += lessonId : ''}">
                                                ${i}
                                            </a>
                                        </li>
                                    </c:forEach>
                                    <li
                                        class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                        <a class="page-link btn btn-outline-primary" href="${pageContext.request.contextPath}/instructor/discussions?page=${currentPage + 1}${resolved != null ? '&resolved=' += resolved : ''}${courseId != null ? '&courseId=' += courseId : ''}${lessonId != null ? '&lessonId=' += lessonId : ''}">
                                            &raquo;
                                        </a>
                                    </li>
                                </ul>
                            </nav>
                        </c:if>
                    </div>
                </div>
            </div>

            <jsp:include page="../common/scripts.jsp" />
            <script>
                // Course and lesson filter interaction
                document.addEventListener('DOMContentLoaded', function () {
                    const courseSelect = document.getElementById('courseSelect');
                    const lessonSelect = document.getElementById('lessonSelect');

                    courseSelect.addEventListener('change', function () {
                        const courseId = this.value;
                        if (courseId) {
                            // Enable lesson select when a course is selected
                            lessonSelect.disabled = false;
                            // Submit form to get lessons for this course
                            const form = this.closest('form');
                            form.submit();
                        } else {
                            // Disable lesson select when "All Courses" is selected
                            lessonSelect.disabled = true;
                            lessonSelect.value = "";
                            // Submit form to refresh with all courses
                            const form = this.closest('form');
                            form.submit();
                        }
                    });
                });
            </script>
        </body>

    </html>