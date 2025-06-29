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
                    <a href="${pageContext.request.contextPath}/instructor/courses"
                       class="btn btn-lg btn-primary float-end">
                        <i class="fas fa-arrow-left me-2"></i> Back
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

                <div class="container py-5">
                    <div class="mx-auto" style="max-width: 900px;">
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
                                        <div class="col-12 col-md-4 mb-3">
                                            <label class="form-label fw-bold">Price (VND)</label>
                                            <input type="number" min="0" name="price" class="form-control" value="${param.price}" required/>
                                            <c:if test="${not empty errors['price']}">
                                                <div class="error-text">${errors['price']}</div>
                                            </c:if>
                                        </div>
                                        <div class="col-12 col-md-4 mb-3">
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
                                        <div class="col-12 col-md-4 mb-3">
                                            <label class="form-label fw-bold">Duration (Weeks)</label>
                                            <input type="number" min="1" name="duration" class="form-control" value="${param.duration}" placeholder="e.g. 20" required/>
                                            <c:if test="${not empty errors['duration']}">
                                                <div class="error-text">${errors['duration']}</div>
                                            </c:if>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-12 col-md-6 mb-3">
                                            <label class="form-label fw-bold">Course Image</label>
                                            <input type="file" name="imageFile" accept="image/*" class="form-control" required/>
                                            <c:if test="${not empty errors['imageFile']}">
                                                <div class="error-text">${errors['imageFile']}</div>
                                            </c:if>
                                        </div>
                                        <div class="col-12 col-md-6 mb-3">
                                            <label class="form-label fw-semibold">Instructors</label>
                                            <button type="button" class="btn btn-primary w-100" data-bs-toggle="modal" data-bs-target="#instructorModal">
                                                <span id="instructorDisplay">Choose Instructors</span>
                                            </button>

                                            <input type="hidden" name="instructorIds" id="instructorIdsInput" required>
                                            <c:if test="${not empty errors['instructorIds']}">
                                                <div class="error-text">${errors['instructorIds']}</div>
                                            </c:if>
                                        </div>
                                        <div class="col-12 col-md-6 mb-3">
                                            <label class="form-label fw-bold">Categories</label>
                                            <button type="button" class="btn btn-primary w-100" data-bs-toggle="modal" data-bs-target="#categoryModal">
                                                <span id="categoryDisplay">Choose Categories</span>
                                            </button>

                                            <input type="hidden" name="categoryIds" id="categoryIdsInput" required>
                                            <c:if test="${not empty errors['categoryIds']}">
                                                <div class="error-text">${errors['categoryIds']}</div>
                                            </c:if>
                                        </div>

                                    </div>

                                    <!-- Lessons (dynamic) -->
                                    <label class="form-label fw-bold mt-2">Lessons</label>
                                    <span class="error-text"><c:out value="${errors['lessons']}"/></span>
                                    <div id="lesson-list">
                                        <c:if test="${not empty oldLessons}">
                                            <c:forEach var="lesson" items="${oldLessons}">
                                                <div class="lesson-block card border-primary border-2 rounded-4 mb-4">
                                                    <div class="lesson-header card-header d-flex justify-content-between align-items-center bg-primary text-white rounded-top-4">
                                                        <span class="fw-semibold">Lesson <span class="lesson-num"></span></span>
                                                        <button type="button" class="btn btn-danger btn-lg" onclick="this.closest('.lesson-block').remove(); reindexLessonTempId(); updateLessonNum();">
                                                            <i class="bi bi-trash me-2"></i> Remove
                                                        </button>
                                                    </div>
                                                    <div class="card-body bg-light">
                                                        <input type="hidden" name="lessonTempId[]" value="${lesson.tempId}" />
                                                        <div class="mb-3">
                                                            <label>Lesson Title</label>
                                                            <input type="text" name="lessonTitle[]" class="form-control" value="${lesson.title}" required/>
                                                        </div>
                                                        <div class="mb-3">
                                                            <label>Description</label>
                                                            <textarea name="lessonDescription[]" class="form-control" required>${lesson.description}</textarea>
                                                        </div>
                                                        <div class="mb-2">
                                                            <button type="button" class="btn btn-outline-info btn-lg m-2" onclick="addQuiz(this)">
                                                                <i class="bi bi-question-circle me-2"></i>Add Quiz
                                                            </button>
                                                            <button type="button" class="btn btn-outline-warning btn-lg m-2" onclick="addMaterial(this, ${lesson.tempId})">
                                                                <i class="bi bi-paperclip me-2"></i>Add Material
                                                            </button>
                                                            <button type="button" class="btn btn-outline-secondary btn-lg m-2" onclick="addVideo(this, ${lesson.tempId})">
                                                                <i class="bi bi-camera-video me-2"></i>Add Video
                                                            </button>
                                                        </div>
                                                        <!-- Quizzes -->
                                                        <div class="quiz-list">
                                                            <c:forEach var="quiz" items="${lesson.quizzes}">
                                                                <div class="quiz-block card border border-info border-2 rounded-4 shadow-sm mb-4">
                                                                    <div class="card-header bg-info text-white rounded-top-4 d-flex justify-content-between align-items-center">
                                                                        <span class="fw-bold">Quiz</span>
                                                                        <button type="button" class="btn btn-sm btn-outline-danger" onclick="this.closest('.quiz-block').remove(); reindexLessonTempId();">
                                                                            <i class="bi bi-x-lg"></i>
                                                                        </button>
                                                                    </div>
                                                                    <div class="card-body bg-light rounded-bottom-4">
                                                                        <input type="hidden" name="quizLessonId[]" value="${lesson.tempId}"/>
                                                                        <input type="hidden" name="quizIndex[]" value="${quiz.quizIndex}"/>
                                                                        <div class="row mb-2">
                                                                            <div class="col-md-6">
                                                                                <label>Quiz Title</label>
                                                                                <input type="text" name="quizTitle[]" class="form-control border-info border-2 rounded-3 mb-2" value="${quiz.title}" required/>
                                                                            </div>
                                                                            <div class="col-md-6">
                                                                                <label>Quiz Description</label>
                                                                                <input type="text" name="quizDescription[]" class="form-control border-info border-2 rounded-3 mb-2" value="${quiz.description}" required/>
                                                                            </div>
                                                                        </div>
                                                                        <div class="row mb-2">
                                                                            <div class="col-6">
                                                                                <label>Time Limit (minutes)</label>
                                                                                <input type="number" name="quizTimeLimit[]" min="1" class="form-control border-info border-2 rounded-3" value="${quiz.timeLimit}" required/>
                                                                            </div>
                                                                            <div class="col-6">
                                                                                <label>Passing Score (%)</label>
                                                                                <input type="number" name="quizPassingScore[]" min="0" max="100" class="form-control border-info border-2 rounded-3" value="${quiz.passingScore}" required/>
                                                                            </div>
                                                                        </div>
                                                                        <!-- Questions -->
                                                                        <div class="question-list">
                                                                            <c:forEach var="question" items="${quiz.questions}">
                                                                                <div class="question-block border border-primary border-2 rounded-3 shadow-sm mb-3 bg-white p-3">
                                                                                    <div class="d-flex justify-content-between align-items-center mb-2">
                                                                                        <span class="fw-semibold">Question</span>
                                                                                        <button type="button" class="btn btn-sm btn-outline-danger" onclick="this.closest('.question-block').remove(); reindexLessonTempId();">
                                                                                            <i class="bi bi-x-lg"></i>
                                                                                        </button>
                                                                                    </div>
                                                                                    <input type="hidden" name="questionQuizLessonId[]" value="${lesson.tempId}"/>
                                                                                    <input type="hidden" name="questionQuizIndex[]" value="${quiz.quizIndex}"/>
                                                                                    <input type="hidden" name="questionIndex[]" value="${question.questionIndex}"/>
                                                                                    <div class="mb-2">
                                                                                        <label>Question Content</label>
                                                                                        <input type="text" name="questionContent[]" class="form-control border-primary border-2 rounded-3 mb-2" value="${question.content}" required/>
                                                                                    </div>
                                                                                    <div class="mb-2">
                                                                                        <label>Points</label>
                                                                                        <input type="number" name="questionPoints[]" min="1" class="form-control border-primary border-2 rounded-3 mb-2" value="${question.points}" required/>
                                                                                    </div>
                                                                                    <!-- Answers -->
                                                                                    <div>
                                                                                        <label class="fw-bold">Answers (Single choice only)</label>
                                                                                        <div class="answer-list">
                                                                                            <c:forEach var="answer" items="${question.answers}">
                                                                                                <div class="answer-block d-flex align-items-center mb-2">
                                                                                                    <input type="hidden" name="answerQuizLessonId[]" value="${lesson.tempId}"/>
                                                                                                    <input type="hidden" name="answerQuizIndex[]" value="${quiz.quizIndex}"/>
                                                                                                    <input type="hidden" name="answerQuestionIndex[]" value="${question.questionIndex}"/>
                                                                                                    <input type="hidden" name="answerIndex[]" value="${answer.answerIndex}"/>
                                                                                                    <input type="text" class="form-control me-2 rounded" name="answerContent[]" value="${answer.content}" placeholder="Answer Content" required/>
                                                                                                    <div class="form-check ms-2">
                                                                                                        <input class="form-check-input" type="radio"
                                                                                                               name="answerIsCorrect_${lesson.tempId}_${quiz.quizIndex}_${question.questionIndex}"
                                                                                                               value="${answer.answerIndex}"
                                                                                                               <c:if test="${answer.isCorrect}">checked</c:if>
                                                                                                                   aria-label="Correct" required/>
                                                                                                               <label class="form-check-label">Correct</label>
                                                                                                        </div>
                                                                                                        <button type="button" class="btn btn-outline-danger btn-sm ms-2" onclick="this.closest('.answer-block').remove(); reindexLessonTempId();">
                                                                                                            <i class="bi bi-x-lg"></i>
                                                                                                        </button>
                                                                                                    </div>
                                                                                            </c:forEach>
                                                                                        </div>
                                                                                    </div>
                                                                                    <!-- END Answers -->
                                                                                </div>
                                                                            </c:forEach>
                                                                        </div>
                                                                        <!-- END Questions -->
                                                                    </div>
                                                                </div>
                                                            </c:forEach>
                                                        </div>
                                                        <!-- END Quizzes -->

                                                        <!-- Materials -->
                                                        <div class="material-list">
                                                            <c:forEach var="mat" items="${lesson.materials}">
                                                                <div class="card border border-warning border-2 rounded-4 shadow-sm mb-3">
                                                                    <div class="card-header bg-warning text-dark rounded-top-4 d-flex justify-content-between align-items-center">
                                                                        <span class="fw-bold">Material</span>
                                                                        <button type="button" class="btn btn-sm btn-outline-danger" onclick="this.closest('.card').remove(); reindexLessonTempId();">
                                                                            <i class="bi bi-x-lg"></i>
                                                                        </button>
                                                                    </div>
                                                                    <div class="card-body bg-light rounded-bottom-4">
                                                                        <input type="hidden" name="materialLessonId[]" value="${lesson.tempId}" />
                                                                        <div class="mb-2">
                                                                            <label class="form-label">Material Title</label>
                                                                            <input type="text" name="materialTitle[]" class="form-control border-warning border-2 rounded-3 mb-2" value="${mat.title}" required/>
                                                                        </div>
                                                                        <div class="mb-2">
                                                                            <label class="form-label">Material Description</label>
                                                                            <input type="text" name="materialDescription[]" class="form-control border-warning border-2 rounded-3 mb-2" value="${mat.description}" required/>
                                                                        </div>
                                                                        <div class="mb-2">
                                                                            <label class="form-label">Material Content</label>
                                                                            <textarea name="materialContent[]" class="form-control border-warning border-2 rounded-3 mb-2" required>${mat.content}</textarea>
                                                                        </div>
                                                                        <div class="mb-2">
                                                                            <label class="form-label">File (zip, pdf, doc, docx)</label>
                                                                            <input type="file" name="materialFile[]" class="form-control border-warning border-2 rounded-3" required/>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </c:forEach>
                                                        </div>
                                                        <!-- END Materials -->

                                                        <!-- Videos -->
                                                        <div class="video-list">
                                                            <c:forEach var="video" items="${lesson.videos}">
                                                                <div class="card border border-secondary border-2 rounded-4 shadow-sm mb-3">
                                                                    <div class="card-header bg-secondary text-white rounded-top-4 d-flex justify-content-between align-items-center">
                                                                        <span class="fw-bold">Video</span>
                                                                        <button type="button" class="btn btn-sm btn-outline-danger" onclick="this.closest('.card').remove(); reindexLessonTempId();">
                                                                            <i class="bi bi-x-lg"></i>
                                                                        </button>
                                                                    </div>
                                                                    <div class="card-body bg-light rounded-bottom-4">
                                                                        <input type="hidden" name="videoLessonId[]" value="${lesson.tempId}" />
                                                                        <div class="mb-2">
                                                                            <label class="form-label">Video Title</label>
                                                                            <input type="text" name="videoTitle[]" class="form-control border-secondary border-2 rounded-3 mb-2" value="${video.title}" required/>
                                                                        </div>
                                                                        <div class="mb-2">
                                                                            <label class="form-label">Video Description</label>
                                                                            <input type="text" name="videoDescription[]" class="form-control border-secondary border-2 rounded-3 mb-2" value="${video.description}" required/>
                                                                        </div>
                                                                        <div class="mb-2">
                                                                            <label class="form-label">Video File (mp4, mov, avi...)</label>
                                                                            <input type="file" name="videoFile[]" class="form-control border-secondary border-2 rounded-3 mb-2" accept="video/*" required/>
                                                                        </div>
                                                                        <div class="mb-2">
                                                                            <label class="form-label">Duration (seconds)</label>
                                                                            <input type="number" name="videoDuration[]" min="1" class="form-control border-secondary border-2 rounded-3 mb-2" value="${video.duration}" required/>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </c:forEach>
                                                        </div>
                                                        <!-- END Videos -->
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </c:if>
                                    </div>
                                    <div class="mb-4">
                                        <button type="button" class="btn btn-primary w-100" onclick="addLesson()">
                                            <i class="bi bi-plus-circle me-2"></i>Add Lesson
                                        </button>
                                    </div>
                                    <div class="mb-3">
                                        <c:if test="${not empty errors['general']}">
                                            <div class="alert alert-danger">${errors['general']}</div>
                                        </c:if>
                                    </div>
                                    <div class="fixed-action-btn">
                                        <button type="submit" form="course-form" name="action" value="draft" class="btn btn-lg btn-secondary">
                                            <i class="bi bi-file-earmark-plus me-2"></i>Save as Draft
                                        </button>
                                        <button type="submit" form="course-form" name="action" value="pending" class="btn btn-lg btn-secondary">
                                            <i class="bi bi-send me-2"></i>Submit
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
                                    <input class="form-check-input instructor-checkbox" type="checkbox" value="${inst.instructorID}" id="instCheck${inst.instructorID}">
                                    <label class="form-check-label" for="instCheck${inst.instructorID}">${inst.name}</label>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                    <div class="modal-footer">
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
                                        <input class="form-check-input category-checkbox" type="checkbox" value="${cat.categoryID}" id="catCheck${cat.categoryID}">
                                        <label class="form-check-label" for="catCheck${cat.categoryID}">${cat.name}</label>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-lg btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="button" class="btn btn-lg btn-primary" id="selectCategoriesBtn">Select</button>
                    </div>
                </div>
            </div>
        </div>


        <jsp:include page="../common/scripts.jsp" />

        <script>
            let lessonIndex = 0;
            function addLesson() {
                const list = document.getElementById('lesson-list');
                const div = document.createElement('div');
                div.className = "lesson-block card border-primary border-2 rounded-4 mb-4";
                div.innerHTML = `
    <div class="lesson-header card-header d-flex justify-content-between align-items-center bg-primary text-white rounded-top-4">
        <span class="fw-semibold">Lesson <span class="lesson-num"></span></span>
        <button type="button" class="btn btn-danger btn-lg" onclick="this.closest('.lesson-block').remove(); reindexLessonTempId(); updateLessonNum();">
            <i class="bi bi-trash me-2"></i> Remove
        </button>
    </div>
    <div class="card-body bg-light">
        <input type="hidden" name="lessonTempId[]" value="` + lessonIndex + `" />
        <div class="mb-3">
            <label for="">Lesson Title</label>
            <input type="text" name="lessonTitle[]" class="form-control" placeholder="Lesson Title" required/>
        </div>
        <div class="mb-3">
            <label for="">Description</label>
            <textarea name="lessonDescription[]" class="form-control" placeholder="Description" rows="2" required></textarea>
        </div>
        <div class="mb-2">
            <button type="button" class="btn btn-outline-info btn-lg m-2" onclick="addQuiz(this)">
                <i class="bi bi-question-circle me-2"></i>Add Quiz
            </button>
            <button type="button" class="btn btn-outline-warning btn-lg m-2" onclick="addMaterial(this)">
                <i class="bi bi-paperclip me-2"></i>Add Material
            </button>
            <button type="button" class="btn btn-outline-secondary btn-lg m-2" onclick="addVideo(this)">
                <i class="bi bi-camera-video me-2"></i>Add Video
            </button>
        </div>
        <div class="quiz-list"></div>
        <div class="material-list"></div>
        <div class="video-list"></div>
    </div>
    `;
                list.appendChild(div);
                lessonIndex++;
                reindexLessonTempId();
                updateLessonNum();
            }

            function addQuiz(btn) {
                const lessonBlock = btn.closest('.lesson-block');
                const lessonTempId = lessonBlock.querySelector('input[name="lessonTempId[]"]').value;
                const quizList = lessonBlock.querySelector('.quiz-list');
                const quizIndex = quizList.querySelectorAll('.quiz-block').length;
                const div = document.createElement('div');
                div.className = "quiz-block card border border-info border-2 rounded-4 shadow-sm mb-4";
                div.innerHTML = `
    <div class="card-header bg-info text-white rounded-top-4 d-flex justify-content-between align-items-center">
        <span class="fw-bold">Quiz</span>
        <button type="button" class="btn btn-sm btn-outline-danger" onclick="this.closest('.quiz-block').remove(); reindexLessonTempId();">
            <i class="bi bi-x-lg"></i>
        </button>
    </div>
    <div class="card-body bg-light rounded-bottom-4">
        <input type="hidden" name="quizLessonId[]" value="` + lessonTempId + `"/>
        <input type="hidden" name="quizIndex[]" value="` + quizIndex + `"/>
        <div class="row mb-2">
            <div class="col-md-6">
                <label>Quiz Title</label>
                <input type="text" name="quizTitle[]" class="form-control border-info border-2 rounded-3 mb-2" placeholder="Quiz Title" required/>
            </div>
            <div class="col-md-6">
                <label>Quiz Description</label>
                <input type="text" name="quizDescription[]" class="form-control border-info border-2 rounded-3 mb-2" placeholder="Quiz Description" required/>
            </div>
        </div>
        <div class="row mb-2">
            <div class="col-6">
                <label>Time Limit (minutes)</label>
                <input type="number" name="quizTimeLimit[]" min="1" class="form-control border-info border-2 rounded-3" placeholder="Time Limit (minutes)" required/>
            </div>
            <div class="col-6">
                <label>Passing Score (%)</label>
                <input type="number" name="quizPassingScore[]" min="0" max="100" class="form-control border-info border-2 rounded-3" placeholder="Passing Score (%)" required/>
            </div>
        </div>
        <div>
            <label class="fw-bold mb-1">Questions</label>
            <div class="question-list"></div>
            <button type="button" class="btn btn-outline-dark btn-lg mt-2" onclick="addQuestion(this)">
                <i class="bi bi-plus-circle me-2"></i> Add Question
            </button>
        </div>
    </div>
    `;
                quizList.appendChild(div);
                reindexLessonTempId();
            }

            function addQuestion(btn) {
                const quizBlock = btn.closest('.quiz-block');
                const lessonTempId = quizBlock.querySelector('input[name="quizLessonId[]"]').value;
                const quizIndex = quizBlock.querySelector('input[name="quizIndex[]"]').value;
                const questionList = quizBlock.querySelector('.question-list');
                const questionIndex = questionList.querySelectorAll('.question-block').length;
                const div = document.createElement('div');
                div.className = "question-block border border-primary border-2 rounded-3 shadow-sm mb-3 bg-white p-3";
                div.innerHTML = `
    <div class="d-flex justify-content-between align-items-center mb-2">
        <span class="fw-semibold">Question</span>
        <button type="button" class="btn btn-sm btn-outline-danger" onclick="this.closest('.question-block').remove(); reindexLessonTempId();">
            <i class="bi bi-x-lg"></i>
        </button>
    </div>
    <input type="hidden" name="questionQuizLessonId[]" value="` + lessonTempId + `"/>
    <input type="hidden" name="questionQuizIndex[]" value="` + quizIndex + `"/>
    <input type="hidden" name="questionIndex[]" value="` + questionIndex + `"/>
    <div class="mb-2">
        <label>Question Content</label>
        <input type="text" name="questionContent[]" class="form-control border-primary border-2 rounded-3 mb-2" placeholder="Question Content" required/>
    </div>
    <div class="mb-2">
        <label>Points</label>
        <input type="number" name="questionPoints[]" min="1" class="form-control border-primary border-2 rounded-3 mb-2" placeholder="Points" required/>
    </div>
    <div>
        <label class="fw-bold">Answers (Single choice only)</label>
        <div class="answer-list"></div>
        <button type="button" class="btn btn-outline-secondary btn-lg mt-1" onclick="addAnswer(this)">
            <i class="bi bi-plus-circle me-2"></i> Add Answer
        </button>
    </div>
    `;
                questionList.appendChild(div);
                reindexLessonTempId();
            }

            function addAnswer(btn) {
                const questionBlock = btn.closest('.question-block');
                const lessonTempId = questionBlock.querySelector('input[name="questionQuizLessonId[]"]').value;
                const quizIndex = questionBlock.querySelector('input[name="questionQuizIndex[]"]').value;
                const questionIndex = questionBlock.querySelector('input[name="questionIndex[]"]').value;
                const answerList = questionBlock.querySelector('.answer-list');
                const answerIndex = answerList.querySelectorAll('.answer-block').length;
                const radioName = "answerIsCorrect_" + lessonTempId + "_" + quizIndex + "_" + questionIndex;
                const div = document.createElement('div');
                div.className = "answer-block d-flex align-items-center mb-2";
                div.innerHTML = `
    <input type="hidden" name="answerQuizLessonId[]" value="` + lessonTempId + `"/>
    <input type="hidden" name="answerQuizIndex[]" value="` + quizIndex + `"/>
    <input type="hidden" name="answerQuestionIndex[]" value="` + questionIndex + `"/>
    <input type="hidden" name="answerIndex[]" value="` + answerIndex + `"/>
    <input type="text" class="form-control me-2 rounded" name="answerContent[]" placeholder="Answer Content" required/>
    <div class="form-check ms-2">
        <input class="form-check-input" type="radio" name="` + radioName + `" value="` + answerIndex + `" aria-label="Correct" required>
        <label class="form-check-label">Correct</label>
    </div>
    <button type="button" class="btn btn-outline-danger btn-sm ms-2" onclick="this.closest('.answer-block').remove(); reindexLessonTempId();">
        <i class="bi bi-x-lg"></i>
    </button>
    `;
                answerList.appendChild(div);
                reindexLessonTempId();
            }

            function addMaterial(btn) {
                const lessonBlock = btn.closest('.lesson-block');
                const lessonTempId = lessonBlock.querySelector('input[name="lessonTempId[]"]').value;
                const matList = lessonBlock.querySelector('.material-list');
                const matCount = matList.querySelectorAll('.card').length;
                const unique = lessonTempId + "_" + matCount + "_" + Date.now();
                const idTitle = `materialTitle_` + unique;
                const idDesc = `materialDesc_` + unique;
                const idContent = `materialContent_` + unique;
                const idFile = `materialFile_` + unique;
                const div = document.createElement('div');
                div.className = "card border border-warning border-2 rounded-4 shadow-sm mb-3";
                div.innerHTML = `
    <div class="card-header bg-warning text-dark rounded-top-4 d-flex justify-content-between align-items-center">
        <span class="fw-bold">Material</span>
        <button type="button" class="btn btn-sm btn-outline-danger" onclick="this.closest('.card').remove(); reindexLessonTempId();">
            <i class="bi bi-x-lg"></i>
        </button>
    </div>
    <div class="card-body bg-light rounded-bottom-4">
        <input type="hidden" name="materialLessonId[]" value="` + lessonTempId + `"/>
        <div class="mb-2">
            <label for="` + idTitle + `" class="form-label">Material Title</label>
            <input type="text" id="` + idTitle + `" name="materialTitle[]" class="form-control border-warning border-2 rounded-3 mb-2" placeholder="Material Title" required/>
        </div>
        <div class="mb-2">
            <label for="` + idDesc + `" class="form-label">Material Description</label>
            <input type="text" id="` + idDesc + `" name="materialDescription[]" class="form-control border-warning border-2 rounded-3 mb-2" placeholder="Material Description" required/>
        </div>
        <div class="mb-2">
            <label for="` + idContent + `" class="form-label">Material Content</label>
            <textarea id="` + idContent + `" name="materialContent[]" class="form-control border-warning border-2 rounded-3 mb-2" placeholder="Material Content" required></textarea>
        </div>
        <div class="mb-2">
            <label for="` + idFile + `" class="form-label">File (zip, pdf, doc, docx)</label>
            <input type="file" id="` + idFile + `" name="materialFile[]" class="form-control border-warning border-2 rounded-3" required/>
        </div>
    </div>
    `;
                matList.appendChild(div);
                reindexLessonTempId();
            }

            function addVideo(btn) {
                const lessonBlock = btn.closest('.lesson-block');
                const lessonTempId = lessonBlock.querySelector('input[name="lessonTempId[]"]').value;
                const vList = lessonBlock.querySelector('.video-list');
                const videoCount = vList.querySelectorAll('.card').length;
                const unique = lessonTempId + "_" + videoCount + "_" + Date.now();
                const idTitle = `videoTitle_` + unique;
                const idDesc = `videoDesc_` + unique;
                const idFile = `videoFile_` + unique;
                const idDuration = `videoDuration_` + unique;
                const div = document.createElement('div');
                div.className = "card border border-secondary border-2 rounded-4 shadow-sm mb-3";
                div.innerHTML = `
    <div class="card-header bg-secondary text-white rounded-top-4 d-flex justify-content-between align-items-center">
        <span class="fw-bold">Video</span>
        <button type="button" class="btn btn-sm btn-outline-danger" onclick="this.closest('.card').remove(); reindexLessonTempId();">
            <i class="bi bi-x-lg"></i>
        </button>
    </div>
    <div class="card-body bg-light rounded-bottom-4">
        <input type="hidden" name="videoLessonId[]" value=` + lessonTempId + `"/>
        <div class="mb-2">
            <label for="` + idTitle + `" class="form-label">Video Title</label>
            <input type="text" id="` + idTitle + `" name="videoTitle[]" class="form-control border-secondary border-2 rounded-3 mb-2" placeholder="Video Title" required/>
        </div>
        <div class="mb-2">
            <label for="` + idDesc + `" class="form-label">Video Description</label>
            <input type="text" id="` + idDesc + `" name="videoDescription[]" class="form-control border-secondary border-2 rounded-3 mb-2" placeholder="Video Description" required/>
        </div>
        <div class="mb-2">
            <label for="` + idFile + `" class="form-label">Video File (mp4, mov, avi...)</label>
            <input type="file" id="` + idFile + `" name="videoFile[]" class="form-control border-secondary border-2 rounded-3 mb-2" accept="video/*" required/>
        </div>
        <div class="mb-2">
            <label for="` + idDuration + `" class="form-label">Duration (seconds)</label>
            <input type="number" id="` + idDuration + `" name="videoDuration[]" min="1" class="form-control border-secondary border-2 rounded-3 mb-2" placeholder="Duration (seconds)" required/>
        </div>
    </div>
    `;
                vList.appendChild(div);
                reindexLessonTempId();
            }

            // ----- UPDATE INDEXES -----
            function reindexLessonTempId() {
                const lessonBlocks = document.querySelectorAll('.lesson-block');
                lessonBlocks.forEach((block, idx) => {
                    // Update lessonTempId
                    let input = block.querySelector('input[name="lessonTempId[]"]');
                    if (input)
                        input.value = idx;
                    // Update children input value
                    block.querySelectorAll('input[name="quizLessonId[]"]').forEach(q => q.value = idx);
                    block.querySelectorAll('input[name="materialLessonId[]"]').forEach(q => q.value = idx);
                    block.querySelectorAll('input[name="videoLessonId[]"]').forEach(q => q.value = idx);
                    block.querySelectorAll('input[name="questionQuizLessonId[]"]').forEach(q => q.value = idx);
                    block.querySelectorAll('input[name="answerQuizLessonId[]"]').forEach(q => q.value = idx);
                    // Update name for radio answerIsCorrect
                    block.querySelectorAll('input[type="radio"][name^="answerIsCorrect_"]').forEach(radio => {
                        let oldName = radio.getAttribute('name');
                        let parts = oldName.split('_');
                        if (parts.length === 4) {
                            let quizIndex = parts[2];
                            let questionIndex = parts[3];
                            let newName = `answerIsCorrect_` + idx + "_" + quizIndex + "_" + questionIndex;
                            radio.setAttribute('name', newName);
                        }
                    });
                });
            }

            function updateLessonNum() {
                const lessons = document.querySelectorAll('.lesson-block .lesson-num');
                lessons.forEach((el, idx) => el.innerText = idx + 1);
            }

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
            // Đảm bảo lessonIndex tiếp tục tăng đúng sau khi reload
            document.addEventListener("DOMContentLoaded", function () {
                let lessonBlocks = document.querySelectorAll('.lesson-block');
                if (lessonBlocks.length > 0) {
                    lessonIndex = lessonBlocks.length;
                }
                reindexLessonTempId();
                updateLessonNum();
            });



        </script>


    </body>
</html>
