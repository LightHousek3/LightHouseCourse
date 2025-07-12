<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Create Courses For Instructor</title>
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
                    <p class="mb-0 d-none d-sm-block">Create your course content</p>
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

                <div>
                    <div>
                        <div class="card border border-primary border-2 rounded-4 shadow">
                            <div class="card-header bg-primary text-white rounded-top-4">
                                <h3 class="mb-0">Create New Course</h3>
                            </div>
                            <div class="card-body bg-light rounded-bottom-4">
                                <form id="course-form" action="${pageContext.request.contextPath}/instructor/courses/create" method="post" enctype="multipart/form-data">
                                    <!-- Course info -->
                                    <div class="mb-3">
                                        <label class="form-label fw-bold">Course Name</label>
                                        <input type="text" name="name" class="form-control" value="${param.name}" required/>
                                        <c:if test="${not empty errors['name']}">
                                            <div class="error-text">${errors['name']}</div>
                                        </c:if>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label fw-bold">Description</label>
                                        <textarea name="description" class="form-control" rows="3" required>${param.description}</textarea>
                                        <c:if test="${not empty errors['description']}">
                                            <div class="error-text">${errors['description']}</div>
                                        </c:if>
                                    </div>
                                    <div class="row">
                                        <div class="col-12 col-md-3 mb-3">
                                            <label class="form-label fw-bold">Price (VND)</label>
                                            <input type="number" min="0" name="price" class="form-control" value="${param.price}" required/>
                                            <c:if test="${not empty errors['price']}">
                                                <div class="error-text">${errors['price']}</div>
                                            </c:if>
                                        </div>
                                        <div class="col-12 col-md-3 mb-3">
                                            <label class="form-label fw-bold">Level</label>
                                            <select name="level" class="form-select" required>
                                                <option value="">Select Level</option>
                                                <option value="Beginner" <c:if test="${param.level eq 'Beginner'}">selected</c:if>>Beginner</option>
                                                <option value="Intermediate" <c:if test="${param.level eq 'Intermediate'}">selected</c:if>>Intermediate</option>
                                                <option value="Advanced" <c:if test="${param.level eq 'Advanced'}">selected</c:if>>Advanced</option>
                                                </select>
                                            <c:if test="${not empty errors['level']}">
                                                <div class="error-text">${errors['level']}</div>
                                            </c:if>
                                        </div>
                                        <div class="col-12 col-md-3 mb-3">
                                            <label class="form-label fw-bold">Duration (Weeks)</label>
                                            <input type="number" min="1" name="duration" class="form-control" value="${param.duration}" placeholder="e.g. 20" required/>
                                            <c:if test="${not empty errors['duration']}">
                                                <div class="error-text">${errors['duration']}</div>
                                            </c:if>
                                        </div>
                                        <div class="col-12 col-md-3 mb-3">
                                            <label class="form-label fw-bold">Course Image</label>
                                            <input id="fileSizeImage" type="file" name="imageFile" accept="image/*" class="form-control" required/>
                                            <small id="fileSizeImageInfo" class="form-text text-muted"></small>
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

                                            <input type="hidden" name="instructorIds" id="instructorIdsInput" value="${selectedInstructorIdsAsString}" required/>
                                            <c:if test="${not empty errors['instructorIds']}">
                                                <div class="error-text">${errors['instructorIds']}</div>
                                            </c:if>
                                        </div>
                                        <div class="col-12 col-md-6 mb-3">
                                            <label class="form-label fw-bold">Categories</label>
                                            <button type="button" class="btn btn-primary w-100" data-bs-toggle="modal" data-bs-target="#categoryModal">
                                                <span id="categoryDisplay">Choose Categories</span>
                                            </button>

                                            <input type="hidden" name="categoryIds" id="categoryIdsInput" value="${selectedCategoryIdsAsString}" required/>
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
                        </div>
                    </div>
                </div>
            </div>
        </div>
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


        <jsp:include page="../common/scripts.jsp" />

        <script>
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

            // check size file cho image
            document.getElementById("fileSizeImage").onchange = function () {
                const maxSize = 200 * 1024 * 1024; // 200MB
                if (this.files[0] && this.files[0].size > maxSize) {
                    alert("File is too large! Max 200MB.");
                    this.value = ""; // Xóa chọn file
                }
            };
            // hiện file size cho image
            document.getElementById('fileSizeImage').addEventListener('change', function () {
                const fileInfo = document.getElementById('fileSizeImageInfo');
                if (this.files && this.files[0]) {
                    let size = this.files[0].size;
                    let sizeStr = (size < 1024 * 1024)
                            ? (size / 1024).toFixed(1) + " KB"
                            : (size / 1024 / 1024).toFixed(2) + " MB";
                    fileInfo.textContent = "Selected file size: " + sizeStr;
                } else {
                    fileInfo.textContent = "";
                }
            });

        </script>


    </body>
</html>
