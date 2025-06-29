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
                    <h2 class="m-0 d-none d-lg-block">Manage Courses</h2>
                    <p class="mb-0 d-none d-sm-block">Create, edit and manage your course content</p>
                </div>

                <div class="d-flex align-items-center">
                    <span class="me-3">Welcome, ${instructor.name}!</span>
                    <div class="dropdown">
                        <button class="btn btn-lg btn-outline-secondary dropdown-toggle gap-1"
                                type="button" id="userDropdown" data-bs-toggle="dropdown"
                                aria-expanded="false">
                            <img src="${pageContext.request.contextPath}${avatar}" style="width: 30px; border-radius: 50%" alt="Avatar"/> ${instructor.name}
                        </button>
                    </div>
                </div>
            </div>
            <div class="row mb-4">

                <div class="text-center mb-4">
                    <a href="${pageContext.request.contextPath}/instructor/courses/create"
                       class="btn btn-lg btn-primary float-end">
                        <i class="fas fa-plus me-2"></i>Create Course
                    </a>
                </div>
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
                    <div class="card-body p-0">
                        <div class="tabs-container d-flex">
                            <button class="tab-button flex-grow-1 active" data-status="all">All
                                Courses</button>
                            <button class="tab-button flex-grow-1" data-status="draft">Drafts</button>
                            <button class="tab-button flex-grow-1" data-status="pending">Pending Review</button>
                            <button class="tab-button flex-grow-1" data-status="approved">Approved</button>
                            <button class="tab-button flex-grow-1" data-status="rejected">Rejected</button>
                        </div>
                    </div>
                </div>
                <!-- Search and Filters -->
                <!--                <div class="card mb-4">
                                    <div class="card-body">
                                        <div class="row g-3">
                                            <div class="col-md-8">
                                                <div class="input-group">
                                                    <input type="text" class="form-control"
                                                           placeholder="Search your courses" id="courseSearch">
                                                    <button class="btn btn-outline-secondary" type="button">
                                                        <i class="fas fa-search"></i>
                                                    </button>
                                                </div>
                                            </div>
                                            <div class="col-md-4">
                                                <select class="form-select" id="courseSort">
                                                    <option value="newest">Newest First</option>
                                                    <option value="oldest">Oldest First</option>
                                                    <option value="name_asc">Name (A-Z)</option>
                                                    <option value="name_desc">Name (Z-A)</option>
                                                </select>
                                            </div>
                                        </div>
                                    </div>
                                </div>         -->
                <!-- Content Area -->
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
                                        <!-- Ảnh khóa học và badge trạng thái -->
                                        <div class="position-relative">
                                            <img src="${pageContext.request.contextPath}/${course.imageUrl}" class="card-img-top" alt="${course.name}">
                                            <span class="badge position-absolute top-0 start-0 m-2 px-3 py-2
                                                  ${course.approvalStatus == 'approved' ? 'status-badge status-approved' :
                                                    course.approvalStatus == 'rejected' ? 'status-badge status-rejected' :
                                                    course.approvalStatus == 'pending' ? 'status-badge status-pending' : 'status-badge status-banned'}">
                                                      ${course.approvalStatus == 'approved' ? 'Approved' : 
                                                        course.approvalStatus == 'rejected' ? 'Rejected' : 
                                                        course.approvalStatus == 'pending' ? 'Pending' : 'Banned'}
                                                  </span>
                                            </div>

                                            <!-- Nội dung khóa học -->
                                            <div class="card-body d-flex flex-column">
                                                <h5 class="card-title">${course.name}</h5>
                                                <p class="card-text text-muted">${fn:substring(course.description, 0, 100)}...</p>

                                                <!-- Thông tin thêm -->
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

                                                <!-- Nút edit -->
                                                <div class="mt-auto d-flex justify-content-end">
                                                    <a href="${pageContext.request.contextPath}/instructor/courses/edit/${course.courseID}" 
                                                       class="btn btn-lg btn-outline-primary">Edit</a>
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
            <script>
                document.addEventListener('DOMContentLoaded', function () {
                    // Tab functionality
                    const tabButtons = document.querySelectorAll('.tab-button');
                    const courseItems = document.querySelectorAll('.course-item');

                    tabButtons.forEach(button => {
                        button.addEventListener('click', function () {
                            // Remove active class from all buttons
                            tabButtons.forEach(btn => btn.classList.remove('active'));

                            // Add active class to clicked button
                            this.classList.add('active');

                            // Filter courses based on status
                            const status = this.dataset.status;

                            courseItems.forEach(item => {
                                if (status === 'all' || item.dataset.status === status) {
                                    item.style.display = 'block';
                                } else {
                                    item.style.display = 'none';
                                }
                            });
                        });
                    });
                });

            </script>
        </body>
    </html>
