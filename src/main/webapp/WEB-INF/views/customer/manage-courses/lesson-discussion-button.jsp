<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!-- Discussion Button -->
<div class="lesson-discussion-button-container">
    <button class="btn btn-light btn-discussion js-discussion-btn" title="Open lesson discussions">
        <i class="fa fa-comments"></i> Discussion
    </button>
</div>

<!-- Initialize Discussion Module -->
<script>
    document.addEventListener('DOMContentLoaded', function () {
        // Initialize discussion module
        LessonDiscussion.init(
            ${currentLesson.lessonID},
            ${course.courseID},
            '${currentLesson.title}'
        );
    });
</script>

<!-- Discussion CSS -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/lesson-discussion.css?version=<%= System.currentTimeMillis() %>">

<!-- Discussion Script -->
<script src="${pageContext.request.contextPath}/assets/js/lesson-discussion.js?version=<%= System.currentTimeMillis() %>"></script>
