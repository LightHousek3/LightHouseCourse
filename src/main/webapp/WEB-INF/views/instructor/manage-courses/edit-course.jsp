<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Edit Course And Lesson For Instructor</title>
        <jsp:include page="../common/head.jsp" />
        <style>
            .lesson-block {
                border: 1px solid #ced4da;
                border-radius: 0.5rem;
                margin-bottom: 1.5rem;
                padding: 1rem;
                background: #fafbfc;
            }
            .lesson-header {
                display: flex;
                align-items: center;
                justify-content: space-between;
            }
            .error-text {
                color: #d63333;
                font-size: 0.98em;
            }
            .lesson-block {
                margin-bottom: 1.5rem;
                border-radius: 12px;
                box-shadow: 0 2px 8px rgba(0,0,0,0.05);
            }
            .lesson-header {
                border-bottom: 1px solid #b7e2fa;
            }
            .fixed-action-btn {
                position: fixed;
                right: 30px;
                bottom: 30px;
                z-index: 100;
                display: flex;
                flex-direction: row;
                gap: 10px;
            }
            @media (max-width: 768px) {
                .fixed-action-btn {
                    right: 10px;
                    bottom: 10px;
                    flex-direction: column;
                    gap: 8px;
                }
                .fixed-action-btn .btn {
                    width: 90vw;
                }
            }
            #instructorDisplay, #categoryDisplay {
                display: inline-block;
                max-width: 180px;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
                vertical-align: middle;
                cursor: pointer;
            }

            .border-primary {
                border-color: #e83e8c !important;
            }
            .bg-primary {
                background-color: #e83e8c !important;
            }

            /* 1. Tắt hover hoàn toàn (tab không active) */
            .nav-tabs .nav-link:not(.active):hover,
            .nav-tabs .nav-link:not(.active):focus {
                background: none !important;
                color: #fff !important;      /* Chữ trắng khi hover (tab không active) */
                border-color: transparent;
                cursor: pointer;
                box-shadow: none;
            }

            /* 2. Tab đang active: chữ đen, nền trắng */
            .nav-tabs .nav-link.active {
                color: #222 !important;      /* Chữ đen */
                background-color: #fff !important;
                border-color: #e83e8c #e83e8c #fff #e83e8c;
                font-weight: 600;
            }

            /* 3. Tab không active: chữ trắng, nền transparent */
            .nav-tabs .nav-link {
                color: #fff !important;      /* Chữ trắng */
                background: transparent !important;
                border-color: transparent;
                font-weight: 500;
                transition: none;
            }

            /* 4. Tùy chọn: làm cho tab nổi bật hơn khi active */
            .nav-tabs .nav-link.active {
                box-shadow: 0 2px 8px rgba(0,0,0,0.06);
                border-radius: 12px 12px 0 0;
            }

            /* 5. Tab nền card-header là màu chủ đạo, nên trắng sẽ nổi */
            .card-header.bg-primary {
                background-color: #e83e8c !important;
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
                    <p class="mb-0 d-none d-sm-block">Edit your course content</p>
                </div>
                <div class="d-flex align-items-center">
                    <div class="text-center">
                        <a href="${pageContext.request.contextPath}/instructor/courses"
                           class="btn btn-lg btn-primary float-end">
                            <i class="fas fa-arrow-left me-2"></i> Back
                        </a>
                    </div>
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

                <div>

                    <div>
                        <div class="card border border-primary border-2 rounded-4 shadow">

                            <div class="card-header bg-primary text-white rounded-top-4">
                                <ul class="nav nav-tabs card-header-tabs" id="editCourseTab" role="tablist">
                                    <li class="nav-item">
                                        <button class="nav-link active" id="course-info-tab" data-bs-toggle="tab"
                                                data-bs-target="#course-info" type="button" role="tab"
                                                aria-controls="course-info" aria-selected="true">
                                            Information Course
                                        </button>
                                    </li>
                                    <li class="nav-item">
                                        <button class="nav-link" id="lesson-manage-tab" data-bs-toggle="tab"
                                                data-bs-target="#lesson-manage" type="button" role="tab"
                                                aria-controls="lesson-manage" aria-selected="false">
                                            Information Lesson
                                        </button>
                                    </li>
                                </ul>
                            </div>

                            <div class="card-body bg-light rounded-bottom-4">
                                <div class="tab-content">

                                    <div class="tab-pane fade show active" id="course-info" role="tabpanel" aria-labelledby="course-info-tab">
                                        <h3>Edit Course</h3>
                                        <form id="course-form" action="${pageContext.request.contextPath}/instructor/courses/edit" method="post" enctype="multipart/form-data">
                                            <div class="row">
                                                <!-- Course info -->
                                                <input type="hidden" name="courseID" value="${course.courseID}" />
                                                <div class="col-12 col-md-6 mb-3">
                                                    <label class="form-label fw-bold">Course Name</label>
                                                    <input 
                                                        type="text" 
                                                        name="name" 
                                                        class="form-control" 
                                                        value="${course.name}" 
                                                        required
                                                        placeholder="Name of course"
                                                        />
                                                    <c:if test="${not empty errors['name']}">
                                                        <div class="error-text">${errors['name']}</div>
                                                    </c:if>
                                                </div>
                                                <div class="col-12 col-md-6 mb-3">
                                                    <label class="form-label fw-bold">Description</label>
                                                    <textarea 
                                                        name="description" 
                                                        class="form-control" 
                                                        rows="3" 
                                                        required
                                                        placeholder="Description of course"
                                                        >${course.description}</textarea>
                                                    <c:if test="${not empty errors['description']}">
                                                        <div class="error-text">${errors['description']}</div>
                                                    </c:if>
                                                </div>
                                                <div class="col-12 col-md-3 mb-3">
                                                    <label class="form-label fw-bold">Price (VND)</label>
                                                    <fmt:formatNumber value="${course.price}" type="number" pattern="#0" maxFractionDigits="0" var="FormattedValue" />
                                                    <input 
                                                        type="number" 
                                                        min="0" 
                                                        name="price" 
                                                        class="form-control"
                                                        value="${FormattedValue}" 
                                                        required
                                                        placeholder="Price of course"
                                                        />
                                                    <c:if test="${not empty errors['price']}">
                                                        <div class="error-text">${errors['price']}</div>
                                                    </c:if>
                                                </div>
                                                <div class="col-12 col-md-3 mb-3">
                                                    <label class="form-label fw-bold">Level</label>
                                                    <select name="level" class="form-select" required>
                                                        <option value="">Select Level</option>
                                                        <option value="Beginner" <c:if test="${course.level eq 'Beginner'}">selected</c:if>>Beginner</option>
                                                        <option value="Intermediate" <c:if test="${course.level eq 'Intermediate'}">selected</c:if>>Intermediate</option>
                                                        <option value="Advanced" <c:if test="${course.level eq 'Advanced'}">selected</c:if>>Advanced</option>
                                                        </select>
                                                    <c:if test="${not empty errors['level']}">
                                                        <div class="error-text">${errors['level']}</div>
                                                    </c:if>
                                                </div>
                                                <div class="col-12 col-md-3 mb-3">
                                                    <label class="form-label fw-bold">Duration (Weeks)</label>
                                                    <input type="number" min="1" name="duration" class="form-control" value="${course.durationNumber}" placeholder="e.g. 20" required/>
                                                    <c:if test="${not empty errors['duration']}">
                                                        <div class="error-text">${errors['duration']}</div>
                                                    </c:if>
                                                </div>
                                                <div class="col-12 col-md-3 mb-3">
                                                    <div class="d-flex align-items-center mb-2">
                                                        <label class="form-label fw-bold">Course Image
                                                            <c:if test="${not empty course.imageUrl}">
                                                                <a href="#" data-bs-toggle="modal" data-bs-target="#currentImageModal"
                                                                   class="link-primary" style="font-size: 0.96em;">
                                                                    View Image
                                                                </a>
                                                            </c:if>
                                                        </label>

                                                    </div>
                                                    <input type="file" name="imageFile" accept="image/*" class="form-control"/>
                                                    <c:if test="${not empty errors['imageFile']}">
                                                        <div class="error-text">${errors['imageFile']}</div>
                                                    </c:if>
                                                </div>
                                            </div>
                                            <div class="row">
                                                <div class="col-12 col-md-6 mb-3">
                                                    <label class="form-label fw-semibold">Instructors</label>
                                                    <button type="button" class="btn btn-primary w-100" data-bs-toggle="modal" data-bs-target="#instructorModal">
                                                        <span id="instructorDisplay">Choose Instructors</span>
                                                    </button>
                                                    <input type="hidden" name="instructorIds" id="instructorIdsInput" value="${selectedInstructorIds}" required/>
                                                    <c:if test="${not empty errors['instructorIds']}">
                                                        <div class="error-text">${errors['instructorIds']}</div>
                                                    </c:if>
                                                </div>
                                                <div class="col-12 col-md-6 mb-3">
                                                    <label class="form-label fw-bold">Categories</label>
                                                    <button type="button" class="btn btn-primary w-100" data-bs-toggle="modal" data-bs-target="#categoryModal">
                                                        <span id="categoryDisplay">Choose Categories</span>
                                                    </button>

                                                    <input type="hidden" name="categoryIds" id="categoryIdsInput" value="${selectedCategoryIds}" required/>
                                                    <c:if test="${not empty errors['categoryIds']}">
                                                        <div class="error-text">${errors['categoryIds']}</div>
                                                    </c:if>
                                                </div>
                                            </div>
                                            <div class="fixed-action-btn">
                                                <button type="submit" form="course-form" name="action" value="draft" class="btn btn-lg btn-secondary">
                                                    <i class="bi bi-file-earmark-plus me-2"></i>Save as Draft
                                                </button>
                                            </div>
                                        </form>
                                    </div>
                                    <div class="tab-pane fade" id="lesson-manage" role="tabpanel" aria-labelledby="lesson-manage-tab">
                                        <h3>Lessons in this course</h3>
                                        <div>
                                            <button type="button" class="btn btn-lg btn-success float-end" 
                                                    data-bs-toggle="modal" 
                                                    data-bs-target="#addLessonModal">
                                                <i class="fas fa-plus me-2"></i> Add Lesson
                                            </button>
                                        </div>
                                        <c:choose>
                                            <c:when test="${empty lessons}">
                                                <div class="pt-5 text-center">
                                                    <div class="h1"><i class="fas fa-book"></i></div>
                                                    <h3>No Lesson Yet</h3>
                                                    <p class="text-muted">You haven't created any lesson yet. Get started by
                                                        creating your first lesson!</p>
                                                </div>
                                            </c:when>
                                            <c:otherwise>


                                                <table class="table table-hover table-striped table-bordered align-middle">
                                                    <thead>
                                                        <tr>
                                                            <th>Index</th>
                                                            <th>Lesson Title</th>
                                                            <th class="text-center">Actions</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <c:forEach var="lesson" items="${lessons}">
                                                            <tr>
                                                                <td>${lesson.orderIndex}</td>
                                                                <td>${lesson.title}</td>
                                                                <td class="text-center">
                                                                    <div class="btn-group">
                                                                        <a href="${pageContext.request.contextPath}/instructor/courses/lessons/view/${lesson.lessonID}?courseID=${lesson.courseID}"
                                                                           class="btn btn-sm btn-outline-info"><i class="fas fa-eye"></i></a>
                                                                        <button type="button" class="btn btn-sm btn-outline-primary"
                                                                                data-bs-toggle="modal"
                                                                                data-bs-target="#editLessonModal"
                                                                                data-lesson-id="${lesson.lessonID}"
                                                                                data-course-id="${lesson.courseID}"
                                                                                data-title="${lesson.title}"
                                                                                data-order-index="${lesson.orderIndex}"
                                                                                >
                                                                            <i class="fas fa-edit"></i>
                                                                        </button>
                                                                        <button type="button" class="btn btn-sm btn-outline-danger"
                                                                                data-bs-toggle="modal"
                                                                                data-bs-target="#deleteLessonModal"
                                                                                data-lesson-id="${lesson.lessonID}"
                                                                                data-course-id="${lesson.courseID}"
                                                                                data-title="${lesson.title}"
                                                                                >
                                                                            <i class="fas fa-trash-alt"></i>
                                                                        </button>
                                                                    </div> 
                                                                </td>
                                                            </tr>

                                                        </c:forEach>
                                                    </tbody>
                                                </table>


                                            </c:otherwise>


                                        </c:choose>



                                    </div>
                                </div>

                            </div>

                        </div>

                    </div>

                </div>

            </div>

        </div>
        <!-- Modal Instructor -->              
        <div class="modal fade" id="instructorModal" tabindex="-1" aria-labelledby="instructorModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-scrollable">
                <div class="modal-content rounded-4 shadow">
                    <div class="modal-header">
                        <h5 class="modal-title" id="instructorModalLabel">Select Instructors</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div id="instructorList">
                            <c:forEach var="inst" items="${instructorList}">
                                <div class="form-check mb-2">
                                    <input class="form-check-input instructor-checkbox" type="checkbox" value="${inst.instructorID}" id="instCheck${inst.instructorID}"
                                           <c:if test="${selectedInstructorIds != null && fn:contains(selectedInstructorIds, inst.instructorID)}">checked</c:if>
                                               />
                                           <label class="form-check-label" for="instCheck${inst.instructorID}">${inst.name}</label>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                    <div class="modal-footer d-flex justify-content-between">
                        <button type="button" class="btn btn-lg btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="button" class="btn btn-lg btn-primary" id="selectInstructorsBtn">Select</button>
                    </div>
                </div>
            </div>
        </div>
        <!-- Modal Category -->
        <div class="modal fade" id="categoryModal" tabindex="-1" aria-labelledby="categoryModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-scrollable">
                <div class="modal-content rounded-4 shadow">
                    <div class="modal-header">
                        <h5 class="modal-title" id="categoryModalLabel">Select Categories</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div id="categoryList" class="row">
                            <c:forEach var="cat" items="${categories}">
                                <div class="col-6 col-lg-4">
                                    <div class="form-check mb-2">
                                        <input class="form-check-input category-checkbox" type="checkbox" value="${cat.categoryID}" id="catCheck${cat.categoryID}"
                                               <c:if test="${selectedCategoryIds != null && fn:contains(selectedCategoryIds, cat.categoryID)}">checked</c:if>
                                                   /> 
                                               <label class="form-check-label" for="catCheck${cat.categoryID}">${cat.name}</label>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                    <div class="modal-footer d-flex justify-content-between">
                        <button type="button" class="btn btn-lg btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="button" class="btn btn-lg btn-primary" id="selectCategoriesBtn">Select</button>
                    </div>
                </div>
            </div>
        </div>
        <!-- Modal Image -->
        <div class="modal fade" id="currentImageModal" tabindex="-1" aria-labelledby="currentImageModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content rounded-4">
                    <div class="modal-header">
                        <h5 class="modal-title" id="currentImageModalLabel">Course Image Preview</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                    </div>
                    <div class="modal-body text-center">
                        <img src="${pageContext.request.contextPath}/${course.imageUrl}"
                             alt="Current Course Image"
                             style="max-width: 95%; max-height: 340px; border-radius: 12px; object-fit: cover; box-shadow: 0 4px 12px rgba(0,0,0,0.13);" />
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal add lesson -->
        <div class="modal fade" id="addLessonModal" tabindex="-1" aria-labelledby="addLessonModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content rounded-4 shadow">
                    <div class="modal-header">
                        <h5 class="modal-title" id="addLessonModalLabel">Create New Lesson</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form method="post" action="${pageContext.request.contextPath}/instructor/courses/lessons/create">
                        <div class="modal-body">
                            <input type="hidden" name="courseID" value="${course.courseID}"/>
                            <div class="mb-3">
                                <label class="form-label fw-bold">Lesson Title</label>
                                <input type="text" 
                                       name="title"
                                       class="form-control"
                                       value="${sessionScope.lesson != null ? sessionScope.lesson.title : ''}"
                                       maxlength="150"
                                       required
                                       placeholder="Title of lesson"/>
                                <c:if test="${not empty sessionScope.lessonErrors['title']}">
                                    <span class="error-text">${sessionScope.lessonErrors['title']}</span>
                                </c:if>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-bold">Order</label>
                                <input type="number" 
                                       name="orderIndex" 
                                       class="form-control"
                                       min="1"
                                       value="${sessionScope.lesson != null ? sessionScope.lesson.orderIndex : ''}"
                                       required
                                       placeholder="Order of lesson in the course"/>
                                <c:if test="${not empty sessionScope.lessonErrors['orderIndex']}">
                                    <span class="error-text">${sessionScope.lessonErrors['orderIndex']}</span>
                                </c:if>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-lg btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                <button type="submit" class="btn btn-lg btn-primary">Create</button>
                            </div>
                        </div> 
                    </form>


                </div>
            </div>
        </div>

        <!-- Modal edit lesson -->
        <div class="modal fade" id="editLessonModal" tabindex="-1" aria-labelledby="editLessonModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content rounded-4 shadow">
                    <div class="modal-header">
                        <h5 class="modal-title" id="editLessonModalLabel">Edit Lesson</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form method="post" action="${pageContext.request.contextPath}/instructor/courses/lessons/edit">
                        <div class="modal-body">
                            <input type="hidden" name="courseID" id="editLessonCourseID" value="${sessionScope.editLesson != null ? sessionScope.editLesson.courseID : ''}"/>
                            <input type="hidden" name="lessonID" id="editLessonLessonID" value="${sessionScope.editLesson != null ? sessionScope.editLesson.lessonID : ''}"/>
                            <div class="mb-3">
                                <label class="form-label fw-bold">Lesson Title</label>
                                <input type="text" 
                                       name="title" 
                                       class="form-control"
                                       id="editLessonTitle"
                                       value="${sessionScope.editLesson != null ? sessionScope.editLesson.title : ''}"
                                       maxlength="150"
                                       required
                                       placeholder="Title of lesson"
                                       />
                                <c:if test="${not empty sessionScope.editLessonErrors['title']}">
                                    <span class="error-text alert">${sessionScope.editLessonErrors['title']}</span>
                                </c:if>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-bold">Order</label>
                                <input type="number" 
                                       name="orderIndex" 
                                       class="form-control"
                                       min="1"
                                       id="editLessonOrderIndex"
                                       value="${sessionScope.editLesson != null ? sessionScope.editLesson.orderIndex : ''}"
                                       required
                                       placeholder="Order of lesson in the course"
                                       />
                                <c:if test="${not empty sessionScope.editLessonErrors['orderIndex']}">
                                    <span class="error-text alert">${sessionScope.editLessonErrors['orderIndex']}</span>
                                </c:if>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-lg btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            <button type="submit" class="btn btn-lg btn-primary">Edit</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Modal delete lesson  -->
        <div class="modal fade"
             id="deleteLessonModal"
             tabindex="-1"
             aria-labelledby="deleteLessonModalLabel"
             aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title"
                            id="deleteLessonModalLabel">
                            Confirm Delete <strong>Lesson</strong></h5>
                        <button type="button" class="btn-close"
                                data-bs-dismiss="modal"
                                aria-label="Close">
                        </button>
                    </div>
                    <div class="modal-body">
                        Are you sure you want to delete this lesson with title <strong id="deleteLessonTitle"></strong> ?
                        <br>
                        <strong>This action cannot be undone.</strong>
                    </div>
                    <div class="modal-footer">
                        <button type="button"
                                class="btn btn-md btn-secondary"
                                data-bs-dismiss="modal">Cancel</button>
                        <form action="${pageContext.request.contextPath}/instructor/courses/lessons/delete" method="post">
                            <input type="hidden" name="lessonID" id="deleteLessonLessonID"/>
                            <input type="hidden" name="courseID" id="deleteLessonCourseID" />
                            <button type="submit" class="btn btn-md btn-danger">Delete</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>




        <jsp:include page="../common/scripts.jsp" />

        <script>
            // select giảm độ dài text
            function showSelectedNames(names, placeholder, displayElem) {
                if (names.length === 0) {
                    displayElem.innerText = placeholder;
                    displayElem.removeAttribute('title');
                } else if (names.length <= 2) {
                    displayElem.innerText = names.join(', ');
                    displayElem.title = names.join(', ');
                } else {
                    displayElem.innerText = names.slice(0, 2).join(', ') + ` and ` + names.length + ` more`;
                    displayElem.title = names.join(', ');
                }
            }

            // Instructors modal select
            document.getElementById('selectInstructorsBtn').onclick = function () {
                let checked = document.querySelectorAll('.instructor-checkbox:checked');
                let ids = [], names = [];
                checked.forEach(chk => {
                    ids.push(chk.value);
                    names.push(chk.nextElementSibling.innerText);
                });
                document.getElementById('instructorIdsInput').value = ids.join(',');
                let display = document.getElementById('instructorDisplay');
                showSelectedNames(names, 'Choose Instructors', display);

                // Close modal
                let modal = bootstrap.Modal.getInstance(document.getElementById('instructorModal'));
                modal.hide();
            };

            // Khi mở lại modal: tick lại các checkbox đã chọn
            document.getElementById('instructorModal').addEventListener('show.bs.modal', function () {
                let ids = document.getElementById('instructorIdsInput').value.split(',');
                document.querySelectorAll('.instructor-checkbox').forEach(chk => {
                    chk.checked = ids.includes(chk.value);
                });
            });

            // Categories modal select
            document.getElementById('selectCategoriesBtn').onclick = function () {
                let checked = document.querySelectorAll('.category-checkbox:checked');
                let ids = [], names = [];
                checked.forEach(chk => {
                    ids.push(chk.value);
                    names.push(chk.nextElementSibling.innerText);
                });
                document.getElementById('categoryIdsInput').value = ids.join(',');
                let display = document.getElementById('categoryDisplay');
                showSelectedNames(names, 'Choose Categories', display);

                // Close modal
                let modal = bootstrap.Modal.getInstance(document.getElementById('categoryModal'));
                modal.hide();
            };

            // Khi mở lại modal: tick lại các checkbox đã chọn
            document.getElementById('categoryModal').addEventListener('show.bs.modal', function () {
                let ids = document.getElementById('categoryIdsInput').value.split(',');
                document.querySelectorAll('.category-checkbox').forEach(chk => {
                    chk.checked = ids.includes(chk.value);
                });
            });

            document.addEventListener("DOMContentLoaded", function () {
                // Show checked instructor names
                let instructorNames = [];
                document.querySelectorAll('.instructor-checkbox:checked').forEach(chk => {
                    let name = chk.nextElementSibling.innerText;
                    instructorNames.push(name);
                });
                showSelectedNames(instructorNames, 'Choose Instructors', document.getElementById('instructorDisplay'));

                // Show checked category names
                let categoryNames = [];
                document.querySelectorAll('.category-checkbox:checked').forEach(chk => {
                    let name = chk.nextElementSibling.innerText;
                    categoryNames.push(name);
                });
                showSelectedNames(categoryNames, 'Choose Categories', document.getElementById('categoryDisplay'));
            });

            // Mở Tab lesson hoặc course lại khi tạo hoặc edit xong.
            document.addEventListener('DOMContentLoaded', function () {
                const urlParams = new URLSearchParams(window.location.search);
                let tabParam = urlParams.get('tab');
                let tabId = null;

                if (tabParam === 'lesson') {
                    tabId = 'lesson-manage-tab';
                } else if (tabParam === 'course') {
                    tabId = 'course-info-tab';
                }
                if (tabId) {
                    let tab = new bootstrap.Tab(document.getElementById(tabId));
                    tab.show();
                }
            });


            // Auto dismiss alerts after 5 seconds
            setTimeout(function () {
                document.querySelectorAll('.alert').forEach(function (alert) {
                    const bsAlert = new bootstrap.Alert(alert);
                    bsAlert.close();
                });
            }, 5000);

            // mở lại modal validation for create, edit lesson
            document.addEventListener('DOMContentLoaded', function () {
            <c:if test="${sessionScope.openAddLessonModal}">
                var modal = new bootstrap.Modal(document.getElementById('addLessonModal'));
                modal.show();
            </c:if>
            <c:if test="${sessionScope.openEditLessonModal}">
                var modal = new bootstrap.Modal(document.getElementById('editLessonModal'));
                modal.show();
            </c:if>
            });

            // Mở modal edit lesson
            document.getElementById('editLessonModal').addEventListener('show.bs.modal', function (event) {
                const button = event.relatedTarget;
                if (button) {
                    document.getElementById('editLessonLessonID').value = button.dataset.lessonId;
                    document.getElementById('editLessonCourseID').value = button.dataset.courseId;
                    document.getElementById('editLessonTitle').value = button.dataset.title;
                    document.getElementById('editLessonOrderIndex').value = button.dataset.orderIndex;
                }
            });

            // Mở modal delete lesson
            document.getElementById('deleteLessonModal').addEventListener('show.bs.modal', function (event) {
                const button = event.relatedTarget;
                document.getElementById('deleteLessonLessonID').value = button.dataset.lessonId;
                document.getElementById('deleteLessonCourseID').value = button.dataset.courseId;
                document.getElementById('deleteLessonTitle').innerHTML = button.dataset.title;
            });
        </script>
    </body>
</html>
