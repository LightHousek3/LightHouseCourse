<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<fmt:setLocale value="vi_VN" />

<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Manage Courses For Instructor</title>
        <jsp:include page="../common/head.jsp" />
        <style>
            .tabs-container {
                margin-bottom: 2rem;
            }

            .tab-button {
                padding: 0.75rem 1.5rem;
                background-color: #f8f9fa;
                border: none;
                border-bottom: 2px solid transparent;
                font-weight: 500;
                color: #6c757d;
            }

            .tab-button.active {
                border-bottom: 2px solid #e83e8c;
                color: #e83e8c;
                background-color: #fff;
            }
            .empty-courses {
                text-align: center;
                padding: 4rem 2rem;
                background-color: #f8f9fa;
                border-radius: 10px;
            }

            .empty-courses .icon {
                font-size: 4rem;
                color: #6c757d;
                margin-bottom: 1rem;
            }

            #statusFilter {
                min-width: 180px;
                margin-left: 1rem;
            }


        </style>
    </head>

    <body>
        <%-- Instructor Sidebar --%>
        <c:set var="activeMenu" value="courses" scope="request" />
        <jsp:include page="../common/sidebar.jsp" />
        <%-- Instructor Content --%>
        <div class="instructor-content">
            <!-- Header -->
            <div class="instructor-header d-flex justify-content-between align-items-center">
                <button class="btn d-lg-none" id="toggleSidebarBtn">
                    <i class="fas fa-bars"></i>
                </button>
                <div>
                    <h2 class="mb-0 d-none d-lg-block">Manage Courses</h2>
                    <p class="text-muted mb-0 d-none d-lg-block">Create, edit and manage your course content</p>
                </div>

                <div class="d-flex align-items-center">
                        <a href="${pageContext.request.contextPath}/instructor/courses/create"
                           class="btn btn-lg btn-primary">
                            <i class="fas fa-plus me-2"></i>Create Course
                        </a>
                </div>
            </div>
            <div class="row mb-4">
                <!-- Success/Error Messages -->
                <c:if test="${not empty sessionScope.message}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        ${sessionScope.message}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                    <c:remove var="message" scope="session"/>
                </c:if>
                <c:if test="${not empty sessionScope.error}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        ${sessionScope.error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                    <c:remove var="error" scope="session"/>
                </c:if>


                <!-- Success/Error Messages -->
                <c:if test="${not empty message}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        ${message}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        ${error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                <!-- Filter and Tab Navigation -->
                <div class="card mb-4">
                    <div class="card-body d-flex align-items-center">
                        <label for="statusFilter" class="me-2 mb-0 fw-semibold">Filter by status:</label>
                        <select id="statusFilter" class="form-select w-auto">
                            <option value="all">All Courses</option>
                            <option value="draft">Drafts</option>
                            <option value="pending">Pending Review</option>
                            <option value="approved">Approved</option>
                            <option value="rejected">Rejected</option>
                            <option value="banned">Banned</option>
                        </select>
                    </div>
                </div>

                <c:choose>
                    <c:when test="${empty courses}">
                        <div class="empty-courses mb-4">
                            <div class="icon"><i class="fas fa-book"></i></div>
                            <h3>No Courses Yet</h3>
                            <p class="text-muted">You haven't created any courses yet. Get started by
                                creating your first course!</p>
                            <div class="d-flex justify-content-center">
                                <a href="${pageContext.request.contextPath}/instructor/courses/create"
                                   class="btn btn-lg btn-primary mt-3">
                                    <i class="fas fa-plus me-2"></i> Create Your First Course
                                </a>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="row">
                            <c:forEach var="course" items="${courses}">
                                <div class="col-md-6 col-lg-4 mb-4 course-item" data-status="${course.approvalStatus}">
                                    <div class="card h-100 shadow-sm">

                                        <div class="position-relative">
                                            <img src="${pageContext.request.contextPath}/${course.imageUrl}" class="card-img-top" alt="${course.name}">
                                            <span class="badge position-absolute top-0 start-0 m-2 px-3 py-2
                                                  ${course.approvalStatus == 'approved' ? 'status-badge status-approved' :
                                                    course.approvalStatus == 'rejected' ? 'status-badge status-rejected' :
                                                    course.approvalStatus == 'pending' ? 'status-badge status-pending' : 
                                                    course.approvalStatus == 'banned' ? 'status-badge status-banned': 'status-badge status-inactive'}">
                                                      ${course.approvalStatus == 'approved' ? 'Approved' : 
                                                        course.approvalStatus == 'rejected' ? 'Rejected' : 
                                                        course.approvalStatus == 'pending' ? 'Pending' : 
                                                        course.approvalStatus == 'banned' ? 'Banned' : 'Draft'}
                                                  </span>
                                            </div>


                                            <div class="card-body d-flex flex-column">
                                                <h5 class="card-title">${course.name}</h5>
                                                <p class="card-text text-muted">${fn:substring(course.description, 0, 100)}...</p>


                                                <ul class="list-unstyled small text-muted mb-3">
                                                    <li>
                                                        <i class="fas fa-signal me-1 text-secondary"></i>
                                                        <strong>Level:</strong> ${course.level}
                                                    </li>
                                                    <li>
                                                        <i class="fas fa-tags me-1 text-secondary"></i>
                                                        <strong>Category:</strong>
                                                        <c:forEach var="cat" items="${course.categories}" varStatus="loop">
                                                            ${cat.name}<c:if test="${!loop.last}">, </c:if>
                                                        </c:forEach>
                                                    </li>
                                                    <li>
                                                        <i class="fas fa-chalkboard-teacher me-2 text-secondary"></i>
                                                        <strong>Instructor:</strong>
                                                        <c:forEach var="inst" items="${course.instructors}" varStatus="loop">
                                                            ${inst.name}<c:if test="${!loop.last}">, </c:if>
                                                        </c:forEach>
                                                    </li>
                                                </ul>
                                                <div class="mt-auto d-flex flex-column">
                                                    <c:if test="${course.approvalStatus == 'pending'}">
                                                        <button type="submit" 
                                                                class="btn btn-lg btn-outline-secondary m-1 w-100"
                                                                data-bs-toggle="modal"
                                                                data-bs-target="#cancelCourseModal"
                                                                data-course-id="${course.courseID}"
                                                                data-name="${course.name}"
                                                                >
                                                            Cancel
                                                        </button>
                                                    </c:if>
                                                    <a href="${pageContext.request.contextPath}/instructor/courses/view/${course.courseID}" 
                                                       class="btn btn-lg btn-outline-success m-1 w-100">View</a>
                                                    <c:if test="${course.approvalStatus == 'draft' || course.approvalStatus == 'rejected'}">
                                                        <a href="${pageContext.request.contextPath}/instructor/courses/edit/${course.courseID}" 
                                                           class="btn btn-lg btn-outline-primary m-1 w-100">Edit</a>
                                                    </c:if>
                                                    <c:if test="${course.approvalStatus == 'draft'}">
                                                        <button type="submit" 
                                                                class="btn btn-lg btn-outline-warning m-1 w-100"
                                                                data-bs-toggle="modal"
                                                                data-bs-target="#submitCourseModal"
                                                                data-course-id="${course.courseID}"
                                                                data-name="${course.name}"
                                                                >
                                                            Submit
                                                        </button>
                                                        <button type="submit" 
                                                                class="btn btn-lg btn-outline-danger m-1 w-100"
                                                                data-bs-toggle="modal"
                                                                data-bs-target="#deleteCourseModal"
                                                                data-course-id="${course.courseID}"
                                                                data-name="${course.name}"
                                                                >
                                                            Delete
                                                        </button>
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
            <!-- Modal delete course -->
            <div class="modal fade"
                 id="deleteCourseModal"
                 tabindex="-1"
                 aria-labelledby="deleteCourse"
                 aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title"
                                id="deleteCourse">
                                Confirm Delete <strong>Course</strong></h5>
                            <button type="button" class="btn-close"
                                    data-bs-dismiss="modal"
                                    aria-label="Close">
                            </button>
                        </div>
                        <div class="modal-body">
                            Are you sure you want to delete this course with name <strong id="deleteCourseName"></strong> ?
                            <br>
                            <strong>This action cannot be undone.</strong>
                        </div>
                        <div class="modal-footer">
                            <button type="button"
                                    class="btn btn-md btn-secondary"
                                    data-bs-dismiss="modal">Cancel</button>
                            <form method="post" action="${pageContext.request.contextPath}/instructor/courses/delete" style="display:inline;">
                                <input type="hidden" name="courseID" id="deleteCourseID"/>
                                <button type="submit" class="btn btn-md btn-danger">Delete</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
            <!-- Modal submit course -->
            <div class="modal fade"
                 id="submitCourseModal"
                 tabindex="-1"
                 aria-labelledby="submitCourse"
                 aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title"
                                id="submitCourse">
                                Confirm Submit <strong>Course</strong></h5>
                            <button type="button" class="btn-close"
                                    data-bs-dismiss="modal"
                                    aria-label="Close">
                            </button>
                        </div>
                        <div class="modal-body">
                            Are you sure you want to submit this course with name <strong id="submitCourseName"></strong> ?
                            <br>
                            <strong>Please ensure that the course content is fully completed.</strong>
                        </div>
                        <div class="modal-footer">
                            <button type="button"
                                    class="btn btn-md btn-secondary"
                                    data-bs-dismiss="modal">Cancel</button>
                            <form method="post" action="${pageContext.request.contextPath}/instructor/courses/submit" style="display: inline;">
                                <input type="hidden" name="courseID" id="submitCourseID" />
                                <button type="submit" class="btn btn-md btn-warning text-white">Submit</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
            <!-- Modal cancel submit course -->
            <div class="modal fade"
                 id="cancelCourseModal"
                 tabindex="-1"
                 aria-labelledby="cancelCourse"
                 aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title"
                                id="cancelCourse">
                                Confirm Cancel <strong>Course</strong></h5>
                            <button type="button" class="btn-close"
                                    data-bs-dismiss="modal"
                                    aria-label="Close">
                            </button>
                        </div>
                        <div class="modal-body">
                            Are you sure you want to cancel the submission of this course name <strong id="cancelCourseName"></strong> ?
                            <br>
                            <strong>Please make sure that you truly want to change your decision regarding the course content.</strong>
                        </div>
                        <div class="modal-footer">
                            <button type="button"
                                    class="btn btn-md btn-secondary"
                                    data-bs-dismiss="modal">Cancel</button>
                            <form method="post" action="${pageContext.request.contextPath}/instructor/courses/change" style="display: inline;">
                                <input type="hidden" name="courseID" id="cancelCourseID" />
                                <button type="submit" class="btn btn-md btn-warning text-white">Sure</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>  
            <jsp:include page="../common/scripts.jsp" />
            <script>

                document.addEventListener('DOMContentLoaded', function () {
                    const statusFilter = document.getElementById('statusFilter');
                    const courseItems = document.querySelectorAll('.course-item');

                    statusFilter.addEventListener('change', function () {
                        const status = this.value;
                        courseItems.forEach(item => {
                            if (status === 'all' || item.dataset.status === status) {
                                item.style.display = 'block';
                            } else {
                                item.style.display = 'none';
                            }
                        });
                    });
                });

                // Auto dismiss alerts after 5 seconds
                setTimeout(function () {
                    document.querySelectorAll('.alert').forEach(function (alert) {
                        const bsAlert = new bootstrap.Alert(alert);
                        bsAlert.close();
                    });
                }, 5000);

                // Mở modal delete course
                document.getElementById('deleteCourseModal').addEventListener('show.bs.modal', function (event) {
                    const button = event.relatedTarget;
                    document.getElementById('deleteCourseID').value = button.dataset.courseId;
                    document.getElementById('deleteCourseName').innerHTML = button.dataset.name;
                });
                // Mở modal submit course
                document.getElementById('submitCourseModal').addEventListener('show.bs.modal', function (event) {
                    const button = event.relatedTarget;
                    document.getElementById('submitCourseID').value = button.dataset.courseId;
                    document.getElementById('submitCourseName').innerHTML = button.dataset.name;
                });
                // Mở modal cancel course
                document.getElementById('cancelCourseModal').addEventListener('show.bs.modal', function (event) {
                    const button = event.relatedTarget;
                    document.getElementById('cancelCourseID').value = button.dataset.courseId;
                    document.getElementById('cancelCourseName').innerHTML = button.dataset.name;
                });
            </script>
        </body>
    </html>
