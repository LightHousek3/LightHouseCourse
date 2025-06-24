<%-- Document : view-discussion Created on : Jun 25, 2025, 10:45:00 AM Author : DangPH - CE180896 --%>

    <%@page contentType="text/html" pageEncoding="UTF-8" %>
        <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
            <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
                <fmt:setLocale value="vi_VN" />
                <!DOCTYPE html>
                <html lang="en">

                <head>
                    <title>View Discussion - Instructor Dashboard</title>
                    <jsp:include page="../common/head.jsp" />
                    <style>
                        .discussion-container {
                            max-width: 900px;
                            margin: 0 auto;
                        }

                        .discussion-header {
                            background-color: snow;
                            padding: 20px;
                            border-radius: 8px;
                            margin-bottom: 20px;
                        }

                        .discussion-content {
                            background-color: #fff;
                            padding: 20px;
                            border-radius: 8px;
                            border: 1px solid #dee2e6;
                            margin-bottom: 20px;
                        }

                        .reply-item {
                            padding: 15px;
                            border-radius: 8px;
                            margin-bottom: 15px;
                            border: 1px solid #dee2e6;
                            position: relative;
                        }

                        .reply-header {
                            display: flex;
                            justify-content: space-between;
                            align-content: center;
                            margin-bottom: 10px;
                        }

                        .reply-content {
                            margin-bottom: 10px;
                        }

                        .instructor-reply {
                            border-left: 4px solid #0d6efd;
                            background-color: #f0f7ff;
                        }

                        .reply-form {
                            background-color: #eaeef2;
                            padding: 20px;
                            border-radius: 8px;
                        }
                        
                        #statusContainer {
                            height: 40px;
                            max-height: 40px;
                        }

                        .text-custom {
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            max-height: 77px;
                            height: 77px;
                        }

                        .status-badge-appear {
                            animation: badgeAppear 0.5s forwards;
                        }

                        .btn-disappear {
                            animation: buttonDisappear 0.3s forwards;
                        }

                        .reply-actions {
                            display: flex;
                            align-items: center;
                            gap: 2px;
                            position: absolute;
                            top: 15px;
                            right: 15px;
                        }

                        .reply-actions.disabled {
                            display: none;
                        }

                        .edit-form {
                            display: none;
                            margin-top: 10px;
                        }

                        @keyframes badgeAppear {
                            0% {
                                transform: scale(0);
                                opacity: 0;
                            }

                            100% {
                                transform: scale(1);
                                opacity: 1;
                            }
                        }

                        @keyframes buttonDisappear {
                            0% {
                                transform: scale(1);
                                opacity: 1;
                            }

                            100% {
                                transform: scale(0);
                                opacity: 0;
                            }
                        }
                    </style>
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
                                    <h2 class="m-0 d-none d-lg-block">View Discussion</h2>
                                    <div>
                                        <a href="${pageContext.request.contextPath}/instructor/discussions"
                                            class="btn btn-lg btn-primary">
                                            <i class="fas fa-arrow-left me-2"></i> Back
                                        </a>
                                    </div>
                                </div>

                                <!-- Content -->
                                <div class="container-fluid px-4">
                                    <!-- Notifications -->
                                    <c:if test="${param.success != null}">
                                        <div class="alert alert-success alert-dismissible fade show my-3" role="alert">
                                            <c:choose>
                                                <c:when test="${param.success eq 'reply_added'}">
                                                    <strong>Success!</strong> Your reply has been added.
                                                </c:when>
                                                <c:when test="${param.success eq 'auto_unresolved'}">
                                                    <strong>Notice!</strong> This discussion has been automatically
                                                    marked as
                                                    unresolved because a student has added a new question.
                                                </c:when>
                                                <c:when test="${param.success eq 'reply_deleted'}">
                                                    <strong>Success!</strong> The reply has been deleted.
                                                    <c:if test="${param.auto_unresolved eq 'true'}">
                                                        <br><strong>Notice:</strong> The discussion has been
                                                        automatically marked as unresolved because there are no
                                                        instructor replies.
                                                    </c:if>
                                                </c:when>
                                                <c:when test="${param.success eq 'reply_updated'}">
                                                    <strong>Success!</strong> The reply has been updated.
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
                                                <c:when test="${param.error eq 'missing_params'}">
                                                    <strong>Error!</strong> Missing params. Please try again!
                                                </c:when>
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
                                                <c:when test="${param.error eq 'delete_failed'}">
                                                    <strong>Error!</strong> Failed to delete the item.
                                                </c:when>
                                                <c:otherwise>
                                                    <strong>Error!</strong> An error occurred.
                                                </c:otherwise>
                                            </c:choose>
                                            <button type="button" class="btn-close" data-bs-dismiss="alert"
                                                aria-label="Close"></button>
                                        </div>
                                    </c:if>

                                    <!-- Discussion Details -->
                                    <div class="discussion-container">
                                        <div class="discussion-header">
                                            <div class="d-flex justify-content-between align-items-center mb-3">
                                                <h4>Discussion #${discussion.discussionID}</h4>
                                                <div id="statusContainer" class="d-flex align-items-center">
                                                    <c:choose>
                                                        <c:when test="${discussion.isResolved}">
                                                            <span
                                                                class="status-badge status-resolved me-2">Resolved</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span id="resolvedBadge"
                                                                class="status-badge status-resolved me-2"
                                                                style="display: none;">Resolved</span>
                                                            <c:if test="${canReply}">
                                                                <form id="resolveForm"
                                                                    action="${pageContext.request.contextPath}/instructor/discussions"
                                                                    method="POST" class="d-inline">
                                                                    <input type="hidden" name="action"
                                                                        value="mark_resolved">
                                                                    <input type="hidden" name="discussionId"
                                                                        value="${discussion.discussionID}">
                                                                    <input type="hidden" name="resolved" value="true">
                                                                    <button id="resolveButton" type="button"
                                                                        class="btn btn-lg btn-outline-success">
                                                                        <i class="fas fa-check-circle me-1"></i> Mark as
                                                                        Resolved
                                                                    </button>
                                                                </form>
                                                            </c:if>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>
                                            <div class="row">
                                                <div class="col-md-6">
                                                    <p><strong>Course:</strong> ${discussion.courseName}</p>
                                                    <p><strong>Lesson:</strong> ${discussion.lessonTitle}</p>
                                                </div>
                                                <div class="col-md-6">
                                                    <p><strong>Author:</strong> ${discussion.authorName}</p>
                                                    <p><strong>Created:</strong>
                                                        <fmt:formatDate value="${discussion.createdAt}"
                                                            pattern="yyyy-MM-dd HH:mm" />
                                                    </p>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="discussion-content">
                                            <h5>Question</h5>
                                            <p>
                                                <c:out value="${discussion.content}" />
                                            </p>
                                        </div>

                                        <!-- Replies -->
                                        <h5 class="mb-3">Replies (${discussion.replies.size()})</h5>
                                        <c:forEach var="reply" items="${discussion.replies}">
                                            <div id="reply-${reply.replyID}"
                                                class="reply-item ${reply.authorType eq 'instructor' ? 'instructor-reply' : ''}">
                                                <div class="reply-header">
                                                    <div>
                                                        <strong>${reply.authorName}</strong>
                                                        <c:if test="${reply.authorType eq 'instructor'}">
                                                            <span class="badge bg-primary ms-2">Instructor</span>
                                                        </c:if>
                                                        <small class="text-muted d-block">
                                                            <fmt:formatDate value="${reply.createdAt}"
                                                                pattern="yyyy-MM-dd HH:mm" />
                                                            <c:if
                                                                test="${reply.updatedAt != null && reply.updatedAt != reply.createdAt}">
                                                                (Edited:
                                                                <fmt:formatDate value="${reply.updatedAt}"
                                                                    pattern="yyyy-MM-dd HH:mm" />)
                                                            </c:if>
                                                        </small>
                                                    </div>

                                                    <!-- Reply Actions for Instructor's Own Replies -->
                                                    <c:if test="${reply.authorType eq 'instructor' && canReply}">
                                                        <div class="reply-actions">
                                                            <button
                                                                class="btn btn-sm btn-outline-primary edit-reply-btn"
                                                                data-reply-id="${reply.replyID}">
                                                                <i class="fas fa-edit"></i>
                                                            </button>
                                                            <button
                                                                class="btn btn-sm btn-outline-danger delete-reply-btn"
                                                                data-reply-id="${reply.replyID}" data-bs-toggle="modal"
                                                                data-bs-target="#deleteReplyModal">
                                                                <i class="fas fa-trash"></i>
                                                            </button>
                                                        </div>
                                                    </c:if>
                                                </div>
                                                <div class="reply-content" id="reply-content-${reply.replyID}">
                                                    <p>
                                                        <c:out value="${reply.content}" />
                                                    </p>
                                                </div>

                                                <!-- Edit Form (Hidden by default) -->
                                                <c:if test="${reply.authorType eq 'instructor' && canReply}">
                                                    <div class="edit-form" id="edit-form-${reply.replyID}">
                                                        <form
                                                            action="${pageContext.request.contextPath}/instructor/discussions"
                                                            method="POST">
                                                            <input type="hidden" name="action" value="edit_reply">
                                                            <input type="hidden" name="replyId"
                                                                value="${reply.replyID}">
                                                            <input type="hidden" name="discussionId"
                                                                value="${discussion.discussionID}">
                                                            <div class="mb-3">
                                                                <textarea class="form-control" name="content" rows="3"
                                                                    id="edit-content-${reply.replyID}"
                                                                    required></textarea>
                                                            </div>
                                                            <div class="d-flex justify-content-end">
                                                                <button type="button"
                                                                    class="btn btn-md btn-secondary me-2 cancel-edit-btn"
                                                                    data-reply-id="${reply.replyID}"><i
                                                                        class="fas fa-times me-2"></i> Cancel</button>
                                                                <button type="submit" class="btn btn-md btn-primary"><i
                                                                        class="fas fa-save me-2"></i> Save</button>
                                                            </div>
                                                        </form>
                                                    </div>
                                                </c:if>
                                            </div>
                                        </c:forEach>

                                        <c:if test="${empty discussion.replies}">
                                            <div class="text-center py-4">
                                                <i class="fas fa-comments fa-2x text-muted mb-3"></i>
                                                <p>No replies yet. Be the first to respond!</p>
                                            </div>
                                        </c:if>

                                        <!-- Reply Form - Only show if instructor is authorized -->
                                        <c:if test="${canReply}">
                                            <div class="reply-form mt-4">
                                                <h5 class="mb-3">Add Your Reply</h5>
                                                <form action="${pageContext.request.contextPath}/instructor/discussions"
                                                    method="POST">
                                                    <input type="hidden" name="action" value="reply">
                                                    <input type="hidden" name="discussionId"
                                                        value="${discussion.discussionID}">
                                                    <div class="mb-3">
                                                        <textarea class="form-control" name="content" rows="5"
                                                            placeholder="Type your reply here..." required></textarea>
                                                    </div>
                                                    <div class="d-flex justify-content-end">
                                                        <button type="submit" class="btn btn-md btn-primary">
                                                            <i class="fas fa-paper-plane me-2"></i> Post
                                                        </button>
                                                    </div>
                                                </form>
                                            </div>
                                        </c:if>
                                        <c:if test="${!canReply}">
                                            <div class="alert alert-info mt-4">
                                                <i class="fas fa-info-circle me-2"></i> You can only reply to
                                                discussions for courses you teach.
                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                            </div>

                            <!-- Delete Reply Modal -->
                            <div class="modal fade" id="deleteReplyModal" tabindex="-1" aria-hidden="true">
                                <div class="modal-dialog">
                                    <div class="modal-content">
                                        <div class="modal-header">
                                            <h5 class="modal-title">Delete Reply</h5>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal"
                                                aria-label="Close"></button>
                                        </div>
                                        <div class="modal-body">
                                            <p>Are you sure you want to delete this reply? This action cannot be undone.
                                            </p>
                                        </div>
                                        <div class="modal-footer">
                                            <form action="${pageContext.request.contextPath}/instructor/discussions"
                                                method="POST" class="d-flex align-items-center gap-2">
                                                <input type="hidden" name="action" value="delete_reply">
                                                <input type="hidden" name="replyId" id="deleteReplyId">
                                                <input type="hidden" name="discussionId"
                                                    value="${discussion.discussionID}">
                                                <button type="button" class="btn btn-md btn-secondary"
                                                    data-bs-dismiss="modal">Cancel</button>
                                                <button type="submit" class="btn btn-md btn-danger">Delete</button>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <jsp:include page="../common/scripts.jsp" />
                            <script>
                                // Handle resolve button animation
                                document.addEventListener('DOMContentLoaded', function () {
                                    const resolveButton = document.getElementById('resolveButton');
                                    const resolvedBadge = document.getElementById('resolvedBadge');
                                    const resolveForm = document.getElementById('resolveForm');

                                    if (resolveButton) {
                                        resolveButton.addEventListener('click', function () {
                                            // Add disappear animation to button
                                            resolveButton.classList.add('btn-disappear');

                                            // After button animation completes, show the badge
                                            setTimeout(function () {
                                                resolveButton.style.display = 'none';
                                                resolvedBadge.style.display = 'flex';
                                                resolvedBadge.classList.add('status-badge-appear');

                                                // Submit the form after animations
                                                setTimeout(function () {
                                                    resolveForm.submit();
                                                }, 500);
                                            }, 300);
                                        });
                                    }

                                    // Handle delete reply button
                                    const deleteReplyButtons = document.querySelectorAll('.delete-reply-btn');
                                    deleteReplyButtons.forEach(button => {
                                        button.addEventListener('click', function () {
                                            const replyId = this.getAttribute('data-reply-id');
                                            document.getElementById('deleteReplyId').value = replyId;
                                        });
                                    });

                                    // Handle edit reply button
                                    const editReplyButtons = document.querySelectorAll('.edit-reply-btn');
                                    editReplyButtons.forEach(button => {
                                        button.addEventListener('click', function () {
                                            // Disable reply buttons
                                            const parent = this.parentElement;
                                            if (parent && parent.classList.contains('reply-actions')) {
                                                parent.classList.add('disabled');
                                            }

                                            const replyId = this.getAttribute('data-reply-id');
                                            const contentElement = document.getElementById(`reply-content-\${replyId}`);
                                            const editFormElement = document.getElementById(`edit-form-\${replyId}`);
                                            const editContentElement = document.getElementById(`edit-content-\${replyId}`);
                                            // Get the reply content from API
                                            fetch(`${pageContext.request.contextPath}/instructor/discussions/getReply/\${replyId}`)
                                                .then(response => {
                                                    if (!response.ok) {
                                                        throw new Error('Failed to fetch reply content');
                                                    }
                                                    return response.text();
                                                })
                                                .then(content => {
                                                    // Set the content in the edit form
                                                    editContentElement.value = content;

                                                    // Hide content and show edit form
                                                    contentElement.style.display = 'none';
                                                    editFormElement.style.display = 'block';
                                                })
                                                .catch(error => {
                                                    console.error('Error:', error);
                                                    alert('Failed to load reply content for editing.');
                                                });
                                        });
                                    });

                                    // Handle cancel edit button
                                    const cancelEditButtons = document.querySelectorAll('.cancel-edit-btn');
                                    cancelEditButtons.forEach(button => {
                                        button.addEventListener('click', function () {
                                            // Show reply buttons
                                            const replyButtons = document.querySelector('.reply-actions.disabled');
                                            console.log(replyButtons);
                                            if (replyButtons) {
                                                replyButtons.classList.remove('disabled');
                                            }

                                            const replyId = this.getAttribute('data-reply-id');
                                            const contentElement = document.getElementById(`reply-content-\${replyId}`);
                                            const editFormElement = document.getElementById(`edit-form-\${replyId}`);

                                            // Show content and hide edit form
                                            contentElement.style.display = 'block';
                                            editFormElement.style.display = 'none';
                                        });
                                    });
                                });
                            </script>
                </body>

                </html>