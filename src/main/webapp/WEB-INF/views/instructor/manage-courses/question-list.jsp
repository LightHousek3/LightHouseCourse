<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Manage Question For Instructor</title>
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
                    <p class="mb-0 d-none d-sm-block">Edit your course content</p>
                </div>
                <div class="d-flex align-items-center">
                    <div class="text-center">
                        <a href="${pageContext.request.contextPath}/instructor/courses/lessons/view/${lessonID}?courseID=${courseID}"
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
                                <h3>Questions in this quiz</h3>
                            </div>

                            <div class="card-body bg-light rounded-bottom-4">
                                <div class="d-flex gap-2 float-end">
                                    <button type="button" class="btn btn-lg btn-primary" 
                                            data-bs-toggle="modal" 
                                            data-bs-target="#addQuestionModal">
                                        <i class="fas fa-plus me-2"></i> Question
                                    </button>
                                </div>
                                <!-- Modal Bootstrap (Create Question) -->
                                <div class="modal fade" id="addQuestionModal" tabindex="-1" aria-labelledby="addQuestionModalLabel" aria-hidden="true">
                                    <div class="modal-dialog">
                                        <div class="modal-content rounded-4 shadow">
                                            <div class="modal-header">
                                                <h5 class="modal-title" id="addQuestionModalLabel">Create New Question</h5>
                                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                            </div>
                                            <form action="${pageContext.request.contextPath}/instructor/lessons/quizzes/questions/create" method="post">
                                                <div class="modal-body">
                                                    <input type="hidden" name="quizID" value="${quizID}" />
                                                    <input type="hidden" name="lessonID" value="${lessonID}" />
                                                    <input type="hidden" name="courseID" value="${courseID}" />
                                                    <div class="mb-3">
                                                        <label class="form-label fw-bold">Question Content</label>
                                                        <textarea 
                                                            name="questionContent" 
                                                            class="form-control" 
                                                            rows="2" 
                                                            required
                                                            placeholder="Content of question"
                                                            >${empty sessionScope.questionFormValues ? '' : sessionScope.questionFormValues['questionContent']}</textarea>
                                                        <c:if test="${not empty sessionScope.questionErrors['question']}">
                                                            <div class="text-danger">${sessionScope.questionErrors['question']}</div>
                                                        </c:if>
                                                    </div>
                                                    <div class="mb-3">
                                                        <label class="form-label fw-bold">Question Point</label>
                                                        <input 
                                                            class="form-control" 
                                                            type="number" 
                                                            name="points" 
                                                            min="1" 
                                                            value="${empty sessionScope.questionFormValues ? '' : sessionScope.questionFormValues['points']}" 
                                                            required
                                                            placeholder="Point of question"
                                                            >
                                                        <c:if test="${not empty sessionScope.questionErrors['points']}">
                                                            <div class="text-danger">${sessionScope.questionErrors['points']}</div>
                                                        </c:if>
                                                    </div>
                                                    <div class="mb-3">
                                                        <label class="form-label fw-bold">Question Index</label>
                                                        <input 
                                                            class="form-control" 
                                                            type="number" 
                                                            name="orderIndex" 
                                                            min="1" 
                                                            value="${empty sessionScope.questionFormValues ? '' : sessionScope.questionFormValues['orderIndex']}" 
                                                            required
                                                            placeholder="Index of question"
                                                            >
                                                        <c:if test="${not empty sessionScope.questionErrors['orderIndex']}">
                                                            <div class="text-danger">${sessionScope.questionErrors['orderIndex']}</div>
                                                        </c:if>
                                                    </div>
                                                    <div class="mb-2 fw-bold">Answers</div>
                                                    <c:if test="${not empty sessionScope.questionErrors['correctAnswer']}">
                                                        <div class="text-danger">${sessionScope.questionErrors['correctAnswer']}</div>
                                                    </c:if>
                                                    <c:forEach var="i" begin="1" end="4">
                                                        <div class="input-group mb-2">
                                                            <span class="input-group-text">#${i}</span>
                                                            <input 
                                                                type="text" 
                                                                name="answerContent${i}" 
                                                                class="form-control" 
                                                                value="${empty sessionScope.questionFormValues ? '' : sessionScope.questionFormValues['answer'.concat(i)]}" 
                                                                required
                                                                placeholder="Answer (${i}) of question"
                                                                >
                                                            <div class="input-group-text">
                                                                <input 
                                                                    id="answer${i}" 
                                                                    class="form-check-input mt-0" 
                                                                    type="radio" 
                                                                    name="correctAnswer" 
                                                                    value="${i}" <c:if test="${sessionScope.questionFormValues != null && sessionScope.questionFormValues['correctAnswer'] == i}">checked</c:if> 
                                                                    >
                                                                <label for="answer${i}"><i class="bi bi-check2-circle"></i></label>
                                                            </div>

                                                        </div>
                                                        <c:if test="${not empty sessionScope.questionErrors['answer'.concat(i)]}">
                                                            <div class="text-danger">${sessionScope.questionErrors['answer'.concat(i)]}</div>
                                                        </c:if>
                                                    </c:forEach>
                                                </div>
                                                <div class="modal-footer">
                                                    <button type="button" class="btn btn-lg btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                                    <button type="submit" class="btn btn-lg btn-primary">Create</button>
                                                </div>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                                <c:choose>
                                    <c:when test="${empty questions}">
                                        <div class="pt-5 text-center">
                                            <div class="h1"><i class="bi bi-question-octagon"></i></div>
                                            <h3>No Question Yet</h3>
                                            <p class="text-muted">You haven't created any question yet. Get started by
                                                creating your first question!</p>
                                        </div>
                                    </c:when>
                                    <c:otherwise>

                                        <table class="table table-bordered">
                                            <thead>
                                                <tr>
                                                    <th>Index</th>
                                                    <th>Question / Answer</th>
                                                    <th class="text-center">Action</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="q" items="${questions}">
                                                    <tr>
                                                        <td>${q.orderIndex}</td>
                                                        <td><strong><c:out value="${q.content}"></c:out></strong>
                                                                <ul class="list-group border border-primary">
                                                                <c:forEach var="ans" items="${q.answers}" varStatus="aStatus">
                                                                    <li class="list-group-item <c:if test="${ans.correct}">bg-primary text-white</c:if> ">
                                                                        <c:choose >
                                                                            <c:when test="${ans.correct}">
                                                                                <i class="bi bi-check-circle-fill"></i>
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                ${aStatus.index
                                                                                  +
                                                                                  1}.
                                                                            </c:otherwise>
                                                                        </c:choose>

                                                                        <c:out value="${ans.content}"></c:out>

                                                                        </li>
                                                                </c:forEach>
                                                            </ul>
                                                        </td>
                                                        <td class="text-center">
                                                            <div class="btn-group">
                                                                <a class="btn btn-sm btn-outline-primary" href="${pageContext.request.contextPath}/instructor/lessons/quizzes/questions/edit/${q.questionID}?courseID=${courseID}&lessonID=${lessonID}"><i class="fas fa-edit"></i></a>
                                                                <button class="btn btn-sm btn-outline-danger" 
                                                                        data-bs-toggle="modal"
                                                                        data-bs-target="#deleteQuestionModal"
                                                                        data-quiz-id="${quizID}"
                                                                        data-question-id="${q.questionID}"
                                                                        data-order-index="${q.orderIndex}"
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
        <!-- Modal delete question item -->
        <div class="modal fade"
             id="deleteQuestionModal"
             tabindex="-1"
             aria-labelledby="deleteQuestionModalLabel"
             aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title"
                            id="deleteQuestionModalLabel">
                            Confirm Delete <strong>Question</strong></h5>
                        <button type="button" class="btn-close"
                                data-bs-dismiss="modal"
                                aria-label="Close">
                        </button>
                    </div>
                    <div class="modal-body">
                        Are you sure you want to delete this <strong>Question</strong> item with index is <strong id="deleteOrderIndex"></strong> ?
                        <br>
                        <strong>This action cannot be undone.</strong>
                    </div>
                    <div class="modal-footer">
                        <button type="button"
                                class="btn btn-md btn-secondary"
                                data-bs-dismiss="modal">Cancel</button>
                        <form method="post" action="${pageContext.request.contextPath}/instructor/lessons/quizzes/questions/delete" style="display:inline">
                            <input type="hidden" name="questionID" id="deleteQuestionID"/>
                            <input type="hidden" name="quizID" id="deleteQuizID"/>
                            <input type="hidden" name="lessonID" value="${lessonID}" />
                            <input type="hidden" name="courseID" value="${courseID}" />
                            <button type="submit" class="btn btn-md btn-danger">Delete</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <jsp:include page="../common/scripts.jsp" />

        <script>

            // Auto dismiss alerts after 5 seconds
            setTimeout(function () {
                document.querySelectorAll('.alert').forEach(function (alert) {
                    const bsAlert = new bootstrap.Alert(alert);
                    bsAlert.close();
                });
            }, 5000);

            //mở lại modal validation for create question
            document.addEventListener('DOMContentLoaded', function () {
            <c:if test="${sessionScope.openAddQuestionModal}">
                var modal = new bootstrap.Modal(document.getElementById('addQuestionModal'));
                modal.show();
            </c:if>
            });
            // Mở modal delete quiz
            document.getElementById('deleteQuestionModal').addEventListener('show.bs.modal', function (event) {
                const button = event.relatedTarget;
                document.getElementById('deleteQuestionID').value = button.dataset.questionId;
                document.getElementById('deleteQuizID').value = button.dataset.quizId;
                document.getElementById('deleteOrderIndex').innerHTML = button.dataset.orderIndex;
            });

        </script>
    </body>
</html>

