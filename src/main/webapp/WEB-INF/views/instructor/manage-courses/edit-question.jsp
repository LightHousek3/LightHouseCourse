<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Edit Question For Instructor</title>
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
                    <h2 class="mb-0 d-none d-lg-block">Manage Courses</h2>
                    <p class="text-muted mb-0 d-none d-lg-block">Create your course content</p>
                </div>
                <div class="d-flex align-items-center">

                    <a href="${pageContext.request.contextPath}/instructor/lessons/quizzes/view/${question.quizID}?courseID=${courseID}&lessonID=${lessonID}"
                       class="btn btn-lg btn-primary">
                        <i class="fas fa-arrow-left me-2"></i> Back
                    </a>

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
                                <h3 class="mb-0">Edit Question</h3>
                            </div>
                            <div class="card-body bg-light rounded-bottom-4">
                                <form method="post" action="${pageContext.request.contextPath}/instructor/lessons/quizzes/questions/edit">
                                    <input type="hidden" name="quizID" value="${question.quizID}" />
                                    <input type="hidden" name="questionID" value="${question.questionID}"/>
                                    <input type="hidden" name="courseID" value="${courseID}"/>
                                    <input type="hidden" name="lessonID" value="${lessonID}"/>
                                    <div class="mb-3">
                                        <label class="form-label fw-bold">Question Content</label>
                                        <textarea 
                                            name="questionContent" 
                                            class="form-control" 
                                            rows="2" 
                                            required
                                            placeholder="Content of question"
                                            >${question.content}</textarea>
                                        <c:if test="${not empty errors['question']}">
                                            <div class="text-danger">${errors['question']}</div>
                                        </c:if>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label fw-bold">Question Point</label>
                                        <input 
                                            class="form-control" 
                                            type="number" 
                                            name="points" 
                                            min="1" 
                                            value="${question.points}" 
                                            required
                                            placeholder="Point of question"
                                            >
                                        <c:if test="${not empty errors['points']}">
                                            <div class="text-danger">${errors['points']}</div>
                                        </c:if>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label fw-bold">Question Index</label>
                                        <input 
                                            class="form-control" 
                                            type="number" 
                                            name="orderIndex" 
                                            min="1" 
                                            value="${question.orderIndex}" 
                                            required
                                            placeholder="Index of question"
                                            >
                                        <c:if test="${not empty errors['orderIndex']}">
                                            <div class="text-danger">${errors['orderIndex']}</div>
                                        </c:if>
                                    </div>
                                    <div class="mb-2 fw-bold">Answers</div>
                                    <c:if test="${not empty errors['correctAnswer']}">
                                        <div class="text-danger">${errors['correctAnswer']}</div>
                                    </c:if>
                                    <c:forEach var="ans" items="${question.answers}" varStatus="aStatus">
                                        <div class="input-group mb-2">
                                            <span class="input-group-text">#${aStatus.index + 1}</span>
                                            <input type="hidden" name="answerID${aStatus.index + 1}" value="${ans.answerID}" />
                                            <input 
                                                type="text" 
                                                name="answerContent${aStatus.index + 1}" 
                                                class="form-control" 
                                                value="${ans.content}" 
                                                required
                                                placeholder="Answer (${aStatus.index + 1}) of question"
                                                >
                                            <div class="input-group-text">
                                                <input id="answer${aStatus.index + 1}" class="form-check-input mt-0" type="radio" name="correctAnswer" <c:if test="${ans.correct}">checked</c:if> value="${aStatus.index + 1}">
                                                <label for="answer${aStatus.index + 1}"><i class="bi bi-check2-circle"></i></label>
                                            </div>

                                        </div>
                                        <c:if test="${not empty errors['answer'.concat(aStatus.index + 1)]}">
                                            <div class="text-danger">${errors['answer'.concat(aStatus.index + 1)]}</div>
                                        </c:if>
                                    </c:forEach>

                                    <div class="d-flex justify-content-between">
                                        <button type="submit" class="btn btn-lg btn-primary">Save Question</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <jsp:include page="../common/scripts.jsp" />

        <script>

        </script>


    </body>
</html>


