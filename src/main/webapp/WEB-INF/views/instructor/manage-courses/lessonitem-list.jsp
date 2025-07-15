<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Manage Lesson Item For Instructor</title>
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
                    <h2 class="mb-0 d-none d-lg-block">Manage Courses</h2>
                    <p class="text-muted mb-0 d-none d-lg-block">Edit your course content</p>
                </div>
                <div class="d-flex align-items-center">

                    <a href="${pageContext.request.contextPath}/instructor/courses/edit/${courseID}"
                       class="btn btn-lg btn-primary">
                        <i class="fas fa-arrow-left me-2"></i> Back
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

                <div>

                    <div>

                        <div class="card border border-primary border-2 rounded-4 shadow">

                            <div class="card-header bg-primary text-white rounded-top-4">
                                <h3>Resource in this lesson</h3>
                            </div>

                            <div class="card-body bg-light rounded-bottom-4">
                                <div class="d-flex gap-2 float-end">
                                    <button type="button" class="btn btn-md btn-primary" 
                                            data-bs-toggle="modal" 
                                            data-bs-target="#addVideoModal">
                                        <i class="fas fa-plus me-2"></i> Video
                                    </button>
                                    <button type="button" class="btn btn-md btn-success" 
                                            data-bs-toggle="modal" 
                                            data-bs-target="#addMaterialModal">
                                        <i class="fas fa-plus me-2"></i> Material
                                    </button>
                                    <button type="button" class="btn btn-md btn-warning text-white" 
                                            data-bs-toggle="modal" 
                                            data-bs-target="#addQuizModal">
                                        <i class="fas fa-plus me-2"></i> Quiz
                                    </button>
                                </div>
                                <!-- Modal Bootstrap (Create Video) -->
                                <div class="modal fade" id="addVideoModal" tabindex="-1" aria-labelledby="addVideoModalLabel" aria-hidden="true">
                                    <div class="modal-dialog">
                                        <div class="modal-content rounded-4 shadow">
                                            <div class="modal-header">
                                                <h5 class="modal-title" id="addVideoModalLabel">Create New Video</h5>
                                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                            </div>
                                            <form action="${pageContext.request.contextPath}/instructor/lessons/videos/create" method="post" enctype="multipart/form-data">
                                                <div class="modal-body">
                                                    <input type="hidden" name="lessonID" value="${lessonID}"/>
                                                    <input type="hidden" name="courseID" value="${courseID}"/>
                                                    <div class="mb-3">
                                                        <label class="form-label">Title Video</label>
                                                        <input type="text" class="form-control" name="titleVideo" value="${sessionScope.video != null ? sessionScope.video.title : ''}" required>
                                                        <c:if test="${not empty sessionScope.errors['titleVideo']}">
                                                            <div class="text-danger">${sessionScope.errors['titleVideo']}</div>
                                                        </c:if>
                                                    </div>
                                                    <div class="mb-3">
                                                        <label class="form-label">Description Video</label>
                                                        <textarea class="form-control" name="descriptionVideo" required>${sessionScope.video != null ? sessionScope.video.description : ''}</textarea>
                                                        <c:if test="${not empty sessionScope.errors['descriptionVideo']}">
                                                            <div class="text-danger">${sessionScope.errors['descriptionVideo']}</div>
                                                        </c:if>
                                                    </div>
                                                    <div class="mb-3">
                                                        <label class="form-label">File Video</label>
                                                        <input id="sizeFileVideo" type="file" class="form-control" name="videoUrl" accept="video/*" required>
                                                        <small id="fileVideoSizeInfo" class="form-text text-muted"></small>
                                                        <c:if test="${not empty sessionScope.errors['videoUrl']}">
                                                            <div class="text-danger">${sessionScope.errors['videoUrl']}</div>
                                                        </c:if>
                                                    </div>
                                                    <div class="mb-3">
                                                        <label class="form-label">Duration Video</label>
                                                        <input type="number" class="form-control" name="duration" min="1" value="${sessionScope.video != null ? sessionScope.video.duration : ''}" required>
                                                        <c:if test="${not empty sessionScope.errors['duration']}">
                                                            <div class="text-danger">${sessionScope.errors['duration']}</div>
                                                        </c:if>
                                                    </div>
                                                </div>
                                                <div class="modal-footer">
                                                    <button type="button" class="btn btn-lg btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                                    <button type="submit" class="btn btn-lg btn-primary">Create</button>
                                                </div>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                                <!-- Modal Bootstrap (Create Material) -->
                                <div class="modal fade" id="addMaterialModal" tabindex="-1" aria-labelledby="addMaterialModalLabel" aria-hidden="true">
                                    <div class="modal-dialog">
                                        <div class="modal-content rounded-4 shadow">
                                            <div class="modal-header">
                                                <h5 class="modal-title" id="addVideoModalLabel">Create New Material</h5>
                                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                            </div>
                                            <form action="${pageContext.request.contextPath}/instructor/lessons/materials/create" method="post" enctype="multipart/form-data">
                                                <div class="modal-body">
                                                    <input type="hidden" name="lessonID" value="${lessonID}"/>
                                                    <input type="hidden" name="courseID" value="${courseID}"/>
                                                    <div class="mb-3">
                                                        <label class="form-label">Title Material</label>
                                                        <input type="text" class="form-control" name="titleMaterial" value="${sessionScope.material != null ? sessionScope.material.title : ''}" required>
                                                        <c:if test="${not empty sessionScope.errors['titleMaterial']}">
                                                            <div class="text-danger">${sessionScope.errors['titleMaterial']}</div>
                                                        </c:if>
                                                    </div>
                                                    <div class="mb-3">
                                                        <label class="form-label">Description Material</label>
                                                        <textarea class="form-control" name="descriptionMaterial" required>${sessionScope.material != null ? sessionScope.material.description : ''}</textarea>
                                                        <c:if test="${not empty sessionScope.errors['descriptionMaterial']}">
                                                            <div class="text-danger">${sessionScope.errors['descriptionMaterial']}</div>
                                                        </c:if>
                                                    </div>
                                                    <div class="mb-3">
                                                        <label class="form-label">File Material</label>
                                                        <input id="sizeFileMaterial" type="file" class="form-control" name="materialUrl" accept=".pdf,.doc,.docx,.ppt,.pptx" required>
                                                        <small id="fileMaterialSizeInfo" class="form-text text-muted"></small>
                                                        <c:if test="${not empty sessionScope.errors['materialUrl']}">
                                                            <div class="text-danger">${sessionScope.errors['materialUrl']}</div>
                                                        </c:if>
                                                    </div>
                                                    <div class="mb-3">
                                                        <label class="form-label">Content Material</label>
                                                        <textarea class="form-control" name="contentMaterial" required>${sessionScope.material != null ? sessionScope.material.content : ''}</textarea>
                                                        <c:if test="${not empty sessionScope.errors['contentMaterial']}">
                                                            <div class="text-danger">${sessionScope.errors['contentMaterial']}</div>
                                                        </c:if>
                                                    </div>
                                                </div>
                                                <div class="modal-footer">
                                                    <button type="button" class="btn btn-lg btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                                    <button type="submit" class="btn btn-lg btn-primary">Create</button>
                                                </div>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                                <!-- Modal Bootstrap (Create Quiz) -->
                                <div class="modal fade" id="addQuizModal" tabindex="-1" aria-labelledby="addQuizModalLabel" aria-hidden="true">
                                    <div class="modal-dialog">
                                        <div class="modal-content rounded-4 shadow">
                                            <div class="modal-header">
                                                <h5 class="modal-title" id="addQuizModalLabel">Create New Quiz</h5>
                                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                            </div>
                                            <form action="${pageContext.request.contextPath}/instructor/lessons/quizzes/create" method="post">
                                                <div class="modal-body">
                                                    <input type="hidden" name="lessonID" value="${lessonID}"/>
                                                    <input type="hidden" name="courseID" value="${courseID}"/>
                                                    <div class="mb-3">
                                                        <label class="form-label">Title Quiz</label>
                                                        <input type="text" class="form-control" name="titleQuiz" value="${sessionScope.quiz != null ? sessionScope.quiz.title : ''}" required>
                                                        <c:if test="${not empty sessionScope.errors['titleQuiz']}">
                                                            <div class="text-danger">${sessionScope.errors['titleQuiz']}</div>
                                                        </c:if>
                                                    </div>
                                                    <div class="mb-3">
                                                        <label class="form-label">Description Quiz</label>
                                                        <textarea class="form-control" name="descriptionQuiz" required>${sessionScope.quiz != null ? sessionScope.quiz.description : ''}</textarea>
                                                        <c:if test="${not empty sessionScope.errors['descriptionQuiz']}">
                                                            <div class="text-danger">${sessionScope.errors['descriptionQuiz']}</div>
                                                        </c:if>
                                                    </div>
                                                    <div class="mb-3">
                                                        <label class="form-label">Time Limit</label>
                                                        <input type="number" class="form-control" name="timeLimit" value="${sessionScope.quiz != null ? sessionScope.quiz.timeLimit : ''}">
                                                        <c:if test="${not empty sessionScope.errors['timeLimit']}">
                                                            <div class="text-danger">${sessionScope.errors['timeLimit']}</div>
                                                        </c:if>
                                                    </div>
                                                    <div class="mb-3">
                                                        <label class="form-label">Passing Score</label>
                                                        <input type="number" class="form-control" name="passingScore" value="${sessionScope.quiz != null ? sessionScope.quiz.passingScore : ''}" required>
                                                        <c:if test="${not empty sessionScope.errors['passingScore']}">
                                                            <div class="text-danger">${sessionScope.errors['passingScore']}</div>
                                                        </c:if>
                                                    </div>

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
                                    <c:when test="${empty lessonItems}">
                                        <div class="pt-5 text-center">
                                            <div class="h1"><i class="bi bi-file-earmark-fill"></i></div>
                                            <h3>No Lesson Item Yet</h3>
                                            <p class="text-muted">You haven't created any lesson Item yet. Get started by
                                                creating your first lesson item!</p>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <table class="table table-hover table-striped table-bordered align-middle">
                                            <thead>
                                                <tr>
                                                    <th>Index</th>
                                                    <th>Title</th>
                                                    <th>Types</th>
                                                    <th class="text-center">Actions</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="li" items="${lessonItems}">
                                                    <tr>
                                                        <td>${li.orderIndex}</td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${li.itemType eq 'video'}">
                                                                    <b>${li.item.title}</b><br/>
                                                                    <a href="${pageContext.request.contextPath}/${li.item.videoUrl}" target="_blank">View or Download Video</a>
                                                                </c:when>
                                                                <c:when test="${li.itemType eq 'material'}">
                                                                    <b>${li.item.title}</b><br/>
                                                                    <a href="${pageContext.request.contextPath}/${li.item.fileUrl}" target="_blank">View or Download Material</a>
                                                                </c:when>
                                                                <c:when test="${li.itemType eq 'quiz'}">
                                                                    <b>${li.item.title}</b><br/>
                                                                    <a href="${pageContext.request.contextPath}/instructor/lessons/quizzes/view/${li.item.quizID}?courseID=${courseID}&lessonID=${lessonID}">View Questions</a>

                                                                </c:when>
                                                            </c:choose>
                                                        </td>

                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${li.itemType eq 'video'}">
                                                                    <span class="badge bg-primary">Video</span>
                                                                </c:when>
                                                                <c:when test="${li.itemType eq 'material'}">
                                                                    <span class="badge bg-success">Material</span>
                                                                </c:when>
                                                                <c:when test="${li.itemType eq 'quiz'}">
                                                                    <span class="badge bg-warning text-white">Quiz</span>
                                                                </c:when>
                                                            </c:choose>
                                                        </td>


                                                        <td class="text-center">
                                                            <div class="btn-group">
                                                                <c:choose>
                                                                    <c:when test="${li.itemType eq 'video'}">
                                                                        <button
                                                                            class="btn btn-sm btn-outline-primary" 
                                                                            data-bs-toggle="modal"
                                                                            data-bs-target="#editVideoItemModal"
                                                                            data-lesson-id="${li.lessonID}"
                                                                            data-video-id="${li.item.videoID}"
                                                                            data-title="${li.item.title}"
                                                                            data-description="${li.item.description}"
                                                                            data-duration="${li.item.duration}"
                                                                            >
                                                                            <i class="fas fa-edit"></i>
                                                                        </button>

                                                                        <button class="btn btn-sm btn-outline-danger" 
                                                                                data-bs-toggle="modal"
                                                                                data-bs-target="#deleteVideoItemModal"
                                                                                data-lesson-id="${li.lessonID}"
                                                                                data-video-id="${li.item.videoID}"
                                                                                data-title="${li.item.title}"
                                                                                >
                                                                            <i class="fas fa-trash-alt"></i>
                                                                        </button>
                                                                    </c:when>
                                                                    <c:when test="${li.itemType eq 'material'}">
                                                                        <button id="btn-edit-material" class="btn btn-sm btn-outline-primary"
                                                                                data-bs-toggle="modal"
                                                                                data-bs-target="#editMaterialItemModal"
                                                                                data-lesson-id="${li.lessonID}"
                                                                                data-material-id="${li.item.materialID}"
                                                                                data-title="${li.item.title}"
                                                                                data-description="${li.item.description}"
                                                                                data-content="${li.item.content}"
                                                                                >
                                                                            <i class="fas fa-edit"></i>
                                                                        </button>

                                                                        <button id="btn-delete-material" class="btn btn-sm btn-outline-danger"
                                                                                data-bs-toggle="modal"
                                                                                data-bs-target="#deleteMaterialItemModal"
                                                                                data-lesson-id="${li.lessonID}"
                                                                                data-material-id="${li.item.materialID}"
                                                                                data-title="${li.item.title}"
                                                                                >
                                                                            <i class="fas fa-trash-alt"></i>
                                                                        </button>
                                                                    </c:when>
                                                                    <c:when test="${li.itemType eq 'quiz'}">                                                                    
                                                                        <button id="btn-edit-quiz" class="btn btn-sm btn-outline-primary" 
                                                                                data-bs-toggle="modal"
                                                                                data-bs-target="#editQuizItemModal"
                                                                                data-lesson-id="${li.lessonID}"
                                                                                data-quiz-id="${li.item.quizID}"
                                                                                data-title="${li.item.title}"
                                                                                data-description="${li.item.description}"
                                                                                data-timelimit="${li.item.timeLimit}"
                                                                                data-passingscore="${li.item.passingScore}"
                                                                                >
                                                                            <i class="fas fa-edit"></i>
                                                                        </button>

                                                                        <button id="btn-delete-quiz" class="btn btn-sm btn-outline-danger" 
                                                                                data-bs-toggle="modal"
                                                                                data-bs-target="#deleteQuizItemModal"
                                                                                data-lesson-id="${li.lessonID}"
                                                                                data-quiz-id="${li.item.quizID}"
                                                                                data-title="${li.item.title}"
                                                                                >
                                                                            <i class="fas fa-trash-alt"></i>
                                                                        </button>
                                                                    </c:when>
                                                                </c:choose>
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

        <!-- Modal Bootstrap (Edit Video) -->
        <div class="modal fade" id="editVideoItemModal" tabindex="-1" aria-labelledby="editVideoItem" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content rounded-4 shadow">
                    <div class="modal-header">
                        <h5 class="modal-title" id="editVideoItem">Edit Video</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form action="${pageContext.request.contextPath}/instructor/lessons/videos/edit" method="post" enctype="multipart/form-data">
                        <div class="modal-body">
                            <input type="hidden" name="lessonID" id="editVideoLessonID" value="${sessionScope.editVideo != null ? sessionScope.editVideo.lessonID : ''}"/>
                            <input type="hidden" name="videoID" id="editVideoID" value="${sessionScope.editVideo != null ? sessionScope.editVideo.videoID : ''}"/>
                            <input type="hidden" name="courseID" value="${courseID}"/>
                            <div class="mb-3 text-start">
                                <label class="form-label">Title Video</label>
                                <input type="text" class="form-control" name="titleVideo" id="editTitleVideo" value="${sessionScope.editVideo != null ? sessionScope.editVideo.title : ''}" required>
                                <c:if test="${not empty sessionScope.editErrors['titleVideo']}">
                                    <div class="text-danger">${sessionScope.editErrors['titleVideo']}</div>
                                </c:if>
                            </div>
                            <div class="mb-3 text-start">
                                <label class="form-label">Description Video</label>
                                <textarea class="form-control" name="descriptionVideo" id="editDescriptionVideo" required>${sessionScope.editVideo != null ? sessionScope.editVideo.description : ''}</textarea>
                                <c:if test="${not empty sessionScope.editErrors['descriptionVideo']}">
                                    <div class="text-danger">${sessionScope.editErrors['descriptionVideo']}</div>
                                </c:if>
                            </div>
                            <div class="mb-3 text-start">
                                <label class="form-label">File Video</label>
                                <input id="fileSizeVideoForModal" type="file" class="form-control" name="videoUrl" accept="video/*">
                                <small id="fileSizeVideoInfoForModal" class="form-text text-muted"></small>
                                <c:if test="${not empty sessionScope.editErrors['videoUrl']}">
                                    <div class="text-danger">${sessionScope.editErrors['videoUrl']}</div>
                                </c:if>
                            </div>
                            <div class="mb-3 text-start">
                                <label class="form-label">Duration Video</label>
                                <input type="number" class="form-control" name="duration" id="editDuration" min="1" value="${sessionScope.editVideo != null ? sessionScope.editVideo.duration : ''}" required>
                                <c:if test="${not empty sessionScope.editErrors['duration']}">
                                    <div class="text-danger">${sessionScope.editErrors['duration']}</div>
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

        <!-- Modal delete video item -->
        <div class="modal fade"
             id="deleteVideoItemModal"
             tabindex="-1"
             aria-labelledby="deleteVideoItem"
             aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title"
                            id="deleteVideoItem">
                            Confirm Delete <strong>Video</strong></h5>
                        <button type="button" class="btn-close"
                                data-bs-dismiss="modal"
                                aria-label="Close">
                        </button>
                    </div>
                    <div class="modal-body">
                        Are you sure you want to delete this <strong>Video</strong> item with title is <strong id="deleteVideoTitle"></strong> ?
                        <br>
                        <strong>This action cannot be undone.</strong>
                    </div>
                    <div class="modal-footer">
                        <button type="button"
                                class="btn btn-md btn-secondary"
                                data-bs-dismiss="modal">Cancel</button>
                        <form method="post" action="${pageContext.request.contextPath}/instructor/lessons/videos/delete" style="display:inline">
                            <input type="hidden" name="videoID" id="deleteVideoID"/>
                            <input type="hidden" name="lessonID" id="deleteVideoLessonID"/>
                            <input type="hidden" name="courseID" value="${courseID}"/>
                            <button type="submit" class="btn btn-md btn-danger">Delete</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Bootstrap (Edit Material) -->
        <div class="modal fade" id="editMaterialItemModal" tabindex="-1" aria-labelledby="editMaterialItem" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content rounded-4 shadow">
                    <div class="modal-header">
                        <h5 class="modal-title" id="editMaterialItem">Edit Material</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form action="${pageContext.request.contextPath}/instructor/lessons/materials/edit" method="post" enctype="multipart/form-data">
                        <div class="modal-body">
                            <input type="hidden" name="lessonID" id="editMaterialLessonID" value="${sessionScope.editMaterial != null ? sessionScope.editMaterial.lessonID : ''}"/>
                            <input type="hidden" name="materialID" id="editMaterialID" value="${sessionScope.editMaterial != null ? sessionScope.editMaterial.materialID : ''}"/>
                            <input type="hidden" name="courseID" value="${courseID}"/>
                            <div class="mb-3 text-start">
                                <label class="form-label">Title Material</label>
                                <input type="text" class="form-control" name="titleMaterial" id="editTitleMaterial" value="${sessionScope.editMaterial != null ? sessionScope.editMaterial.title : ''}" required>
                                <c:if test="${not empty sessionScope.editErrors['titleMaterial']}">
                                    <div class="text-danger">${sessionScope.editErrors['titleMaterial']}</div>
                                </c:if>
                            </div>
                            <div class="mb-3 text-start">
                                <label class="form-label">Description Material</label>
                                <textarea class="form-control" name="descriptionMaterial" id="editDescriptionMaterial" required>${sessionScope.editMaterial != null ? sessionScope.editMaterial.description : ''}</textarea>
                                <c:if test="${not empty sessionScope.editErrors['descriptionMaterial']}">
                                    <div class="text-danger">${sessionScope.editErrors['descriptionMaterial']}</div>
                                </c:if>
                            </div>
                            <div class="mb-3 text-start">
                                <label class="form-label">File Material</label>
                                <input id="fileSizeMaterialForModal" type="file" class="form-control" name="materialUrl" accept=".pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx">
                                <small id="fileSizeMaterialInfoForModal" class="form-text text-muted"></small>
                                <c:if test="${not empty sessionScope.editErrors['materialUrl']}">
                                    <div class="text-danger">${sessionScope.editErrors['materialUrl']}</div>
                                </c:if>
                            </div>
                            <div class="mb-3 text-start">
                                <label class="form-label">Content Material</label>
                                <textarea class="form-control" name="contentMaterial" id="editContentMaterial" required>${sessionScope.editMaterial != null ? sessionScope.editMaterial.content : ''}</textarea>
                                <c:if test="${not empty sessionScope.editErrors['contentMaterial']}">
                                    <div class="text-danger">${sessionScope.editErrors['contentMaterial']}</div>
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
        <!-- Modal delete material item -->
        <div class="modal fade"
             id="deleteMaterialItemModal"
             tabindex="-1"
             aria-labelledby="deleteMaterialItem"
             aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title"
                            id="deleteMaterialItemModal">
                            Confirm Delete <strong>Material</strong></h5>
                        <button type="button" class="btn-close"
                                data-bs-dismiss="modal"
                                aria-label="Close">
                        </button>
                    </div>
                    <div class="modal-body">
                        Are you sure you want to delete this <strong>Material</strong> item with title is <strong id="deleteMaterialTitle"></strong> ?
                        <br>
                        <strong>This action cannot be undone.</strong>
                    </div>
                    <div class="modal-footer">
                        <button type="button"
                                class="btn btn-md btn-secondary"
                                data-bs-dismiss="modal">Cancel</button>
                        <form method="post" action="${pageContext.request.contextPath}/instructor/lessons/materials/delete" style="display:inline">
                            <input type="hidden" name="materialID" id="deleteMaterialID"/>
                            <input type="hidden" name="lessonID" id="deleteMaterialLessonID"/>
                            <input type="hidden" name="courseID" value="${courseID}"/>
                            <button type="submit" class="btn btn-md btn-danger">Delete</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Bootstrap (Edit Quiz) -->
        <div class="modal fade" id="editQuizItemModal" tabindex="-1" aria-labelledby="editQuizItem" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content rounded-4 shadow">
                    <div class="modal-header">
                        <h5 class="modal-title" id="editQuizItem">Edit Quiz</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form action="${pageContext.request.contextPath}/instructor/lessons/quizzes/edit" method="post">
                        <div class="modal-body">
                            <input type="hidden" name="lessonID" value="${sessionScope.editQuiz != null ? sessionScope.editQuiz.lessonID : ''}" id="editQuizLessonID"/>
                            <input type="hidden" name="quizID" value="${sessionScope.editQuiz != null ? sessionScope.editQuiz.quizID : ''}" id="editQuizID"/>
                            <input type="hidden" name="courseID" value="${courseID}"/>
                            <div class="mb-3 text-start">
                                <label class="form-label">Title Quiz</label>
                                <input type="text" class="form-control" name="titleQuiz" id="editTitleQuiz" value="${sessionScope.editQuiz != null ? sessionScope.editQuiz.title : ''}" required>
                                <c:if test="${not empty sessionScope.editErrors['titleQuiz']}">
                                    <div class="text-danger">${sessionScope.editErrors['titleQuiz']}</div>
                                </c:if>
                            </div>
                            <div class="mb-3 text-start">
                                <label class="form-label">Description Quiz</label>
                                <textarea class="form-control" name="descriptionQuiz" id="editDescriptionQuiz" required>${sessionScope.editQuiz != null ? sessionScope.editQuiz.description : ''}</textarea>
                                <c:if test="${not empty sessionScope.editErrors['descriptionQuiz']}">
                                    <div class="text-danger">${sessionScope.editErrors['descriptionQuiz']}</div>
                                </c:if>
                            </div>
                            <div class="mb-3 text-start">
                                <label class="form-label">Time Limit</label>
                                <input type="number" class="form-control" name="timeLimit" id="editTimeLimitQuiz" value="${sessionScope.editQuiz != null ? sessionScope.editQuiz.timeLimit : ''}">
                                <c:if test="${not empty sessionScope.editErrors['timeLimit']}">
                                    <div class="text-danger">${sessionScope.editErrors['timeLimit']}</div>
                                </c:if>
                            </div>
                            <div class="mb-3 text-start">
                                <label class="form-label">Passing Score</label>
                                <input type="number" class="form-control" name="passingScore" id="editPassingScoreQuiz" value="${sessionScope.editQuiz != null ? sessionScope.editQuiz.passingScore : ''}" required>
                                <c:if test="${not empty sessionScope.editErrors['passingScore']}">
                                    <div class="text-danger">${sessionScope.editErrors['passingScore']}</div>
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
        <!-- Modal delete quiz item -->
        <div class="modal fade"
             id="deleteQuizItemModal"
             tabindex="-1"
             aria-labelledby="deleteQuizItem"
             aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title"
                            id="deleteQuizItem">
                            Confirm Delete <strong>Quiz</strong></h5>
                        <button type="button" class="btn-close"
                                data-bs-dismiss="modal"
                                aria-label="Close">
                        </button>
                    </div>
                    <div class="modal-body">
                        Are you sure you want to delete this <strong>Quiz</strong> item with title is <strong id="deleteTitleQuiz"></strong> ?
                        <br>
                        <strong>This action cannot be undone.</strong>
                    </div>
                    <div class="modal-footer">
                        <button type="button"
                                class="btn btn-md btn-secondary"
                                data-bs-dismiss="modal">Cancel</button>
                        <form method="post" action="${pageContext.request.contextPath}/instructor/lessons/quizzes/delete" style="display:inline">
                            <input type="hidden" name="quizID" id="deleteQuizID"/>
                            <input type="hidden" name="lessonID" id="deleteQuizLessonID"/>
                            <input type="hidden" name="courseID" value="${courseID}"/>
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

            //mở lại modal validation for create quiz, víeo, material
            document.addEventListener('DOMContentLoaded', function () {
            <c:if test="${sessionScope.openAddVideoModal}">
                var modal = new bootstrap.Modal(document.getElementById('addVideoModal'));
                modal.show();
            </c:if>
            <c:if test="${sessionScope.openAddMaterialModal}">
                var modal = new bootstrap.Modal(document.getElementById('addMaterialModal'));
                modal.show();
            </c:if>
            <c:if test="${sessionScope.openAddQuizModal}">
                var modal = new bootstrap.Modal(document.getElementById('addQuizModal'));
                modal.show();
            </c:if>
            });

            // check size file create material modal
            document.getElementById("sizeFileMaterial").onchange = function () {
                const maxSize = 50 * 1024 * 1024; // 50MB
                if (this.files[0] && this.files[0].size > maxSize) {
                    alert("File is too large! Max 50MB.");
                    this.value = ""; // Xóa chọn file
                }
            };
            // check size file edit material modal
            document.getElementById("fileSizeMaterialForModal").onchange = function () {
                const maxSize = 50 * 1024 * 1024; // 50MB
                if (this.files[0] && this.files[0].size > maxSize) {
                    alert("File is too large! Max 50MB.");
                    this.value = ""; // Xóa chọn file
                }
            };
            // check size file create video modal
            document.getElementById("sizeFileVideo").onchange = function () {
                const maxSize = 200 * 1024 * 1024; // 200MB
                if (this.files[0] && this.files[0].size > maxSize) {
                    alert("File is too large! Max 200MB.");
                    this.value = ""; // Xóa chọn file
                }
            };
            // check size file edit video modal
            document.getElementById("fileSizeVideoForModal").onchange = function () {
                const maxSize = 200 * 1024 * 1024; // 200MB
                if (this.files[0] && this.files[0].size > maxSize) {
                    alert("File is too large! Max 200MB.");
                    this.value = ""; // Xóa chọn file
                }
            };

            // hiện file size cho create material
            document.getElementById('sizeFileMaterial').addEventListener('change', function () {
                const fileInfo = document.getElementById('fileMaterialSizeInfo');
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
            // hiện file size cho create video
            document.getElementById('sizeFileVideo').addEventListener('change', function () {
                const fileInfo = document.getElementById('fileVideoSizeInfo');
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

            // hiện file size cho edit material modal
            document.getElementById('fileSizeMaterialForModal').addEventListener('change', function () {
                const fileInfo = document.getElementById('fileSizeMaterialInfoForModal');
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
            // hiện file size cho edit video modal
            document.getElementById('fileSizeVideoForModal').addEventListener('change', function () {
                const fileInfo = document.getElementById('fileSizeVideoInfoForModal');
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

            // Mở modal edit video
            document.getElementById('editVideoItemModal').addEventListener('show.bs.modal', function (event) {
                const button = event.relatedTarget;
                if (button) {
                    document.getElementById('editVideoLessonID').value = button.dataset.lessonId;
                    document.getElementById('editVideoID').value = button.dataset.videoId;
                    document.getElementById('editTitleVideo').value = button.dataset.title;
                    document.getElementById('editDescriptionVideo').value = button.dataset.description;
                    document.getElementById('editDuration').value = button.dataset.duration;
                }
            });

            // Mở modal delete video
            document.getElementById('deleteVideoItemModal').addEventListener('show.bs.modal', function (event) {
                const button = event.relatedTarget;
                document.getElementById('deleteVideoLessonID').value = button.dataset.lessonId;
                document.getElementById('deleteVideoID').value = button.dataset.videoId;
                document.getElementById('deleteVideoTitle').innerHTML = button.dataset.title;
            });

            // Mở modal edit material
            document.getElementById('editMaterialItemModal').addEventListener('show.bs.modal', function (event) {
                const button = event.relatedTarget;
                if (button) {
                    document.getElementById('editMaterialLessonID').value = button.dataset.lessonId;
                    document.getElementById('editMaterialID').value = button.dataset.materialId;
                    document.getElementById('editTitleMaterial').value = button.dataset.title;
                    document.getElementById('editDescriptionMaterial').value = button.dataset.description;
                    document.getElementById('editContentMaterial').value = button.dataset.content;
                }
            });

            // Mở modal delete material
            document.getElementById('deleteMaterialItemModal').addEventListener('show.bs.modal', function (event) {
                const button = event.relatedTarget;
                document.getElementById('deleteMaterialLessonID').value = button.dataset.lessonId;
                document.getElementById('deleteMaterialID').value = button.dataset.materialId;
                document.getElementById('deleteMaterialTitle').innerHTML = button.dataset.title;
            });

            // Mở modal edit quiz
            document.getElementById('editQuizItemModal').addEventListener('show.bs.modal', function (event) {
                const button = event.relatedTarget;
                if (button) {
                    document.getElementById('editQuizLessonID').value = button.dataset.lessonId;
                    document.getElementById('editQuizID').value = button.dataset.quizId;
                    document.getElementById('editTitleQuiz').value = button.dataset.title;
                    document.getElementById('editDescriptionQuiz').value = button.dataset.description;
                    document.getElementById('editTimeLimitQuiz').value = button.dataset.timelimit;
                    document.getElementById('editPassingScoreQuiz').value = button.dataset.passingscore;
                }
            });

            // Mở modal delete quiz
            document.getElementById('deleteQuizItemModal').addEventListener('show.bs.modal', function (event) {
                const button = event.relatedTarget;
                document.getElementById('deleteQuizLessonID').value = button.dataset.lessonId;
                document.getElementById('deleteQuizID').value = button.dataset.quizId;
                document.getElementById('deleteTitleQuiz').innerHTML = button.dataset.title;
            });

            //mở lại modal validation for edit quiz, víeo, material
            document.addEventListener('DOMContentLoaded', function () {
            <c:if test="${sessionScope.openEditVideoModal}">
                var modal = new bootstrap.Modal(document.getElementById('editVideoItemModal'));
                modal.show();
            </c:if>
            <c:if test="${sessionScope.openEditMaterialModal}">
                var modal = new bootstrap.Modal(document.getElementById('editMaterialItemModal'));
                modal.show();
            </c:if>
            <c:if test="${sessionScope.openEditQuizModal}">
                var modal = new bootstrap.Modal(document.getElementById('editQuizItemModal'));
                modal.show();
            </c:if>
            });


        </script>
    </body>
</html>

