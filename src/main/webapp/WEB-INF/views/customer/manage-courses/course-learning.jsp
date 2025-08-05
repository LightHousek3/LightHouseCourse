<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
            <!DOCTYPE html>
            <html lang="en">

            <head>
                <title>${course.name} - Learning - LightHouse</title>
                <!-- Include common head with styles -->
                <jsp:include page="/WEB-INF/views/customer/common/head.jsp" />
                <style>
                    body {
                        background-color: var(--bg-light);
                        overflow-x: hidden;
                    }

                    .learning-container {
                        display: flex;
                        height: calc(100vh - 92px);
                        overflow: hidden;
                        border-top: 1px solid var(--primary-color);
                    }

                    /* Left Panel Styles */
                    .left-panel {
                        width: 320px;
                        height: 100%;
                        display: flex;
                        flex-direction: column;
                        background: linear-gradient(to bottom, var(--bg-white), var(--bg-light));
                        border-right: 1px solid var(--text-light);
                        box-shadow: var(--box-shadow);
                        z-index: 100;
                        position: sticky;
                        top: 56px;
                        overflow: hidden;
                    }

                    /* Loading animation styles */
                    .loading-overlay {
                        position: absolute;
                        top: 0;
                        left: 0;
                        width: 100%;
                        height: 100%;
                        background-color: rgba(255, 255, 255, 0.9);
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        z-index: 80;
                        opacity: 0;
                        visibility: hidden;
                        transition: opacity 0.3s ease, visibility 0.3s ease;
                    }

                    .loading-overlay.active {
                        opacity: 1;
                        visibility: visible;
                    }

                    .loading-dots {
                        display: flex;
                        align-items: center;
                    }

                    .loading-dots .dot {
                        width: 10px;
                        height: 10px;
                        margin: 0 5px;
                        border-radius: 50%;
                        background-color: var(--primary-color);
                        animation: wave 1.5s infinite ease-in-out;
                    }

                    .loading-dots .dot:nth-child(2) {
                        animation-delay: 0.2s;
                    }

                    .loading-dots .dot:nth-child(3) {
                        animation-delay: 0.4s;
                    }

                    @keyframes wave {

                        0%,
                        60%,
                        100% {
                            transform: translateY(0);
                        }

                        30% {
                            transform: translateY(-10px);
                        }
                    }

                    /* Progress Section */
                    .progress-section {
                        padding: 1.5rem;
                        background: linear-gradient(135deg, var(--primary-light) 0%, var(--primary-color) 100%);
                        border-bottom: 1px solid rgba(255, 255, 255, 0.1);
                        color: var(--text-white);
                        z-index: 90;
                    }

                    .progress-header {
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                        margin-bottom: 1rem;
                    }

                    .progress-title {
                        font-weight: 600;
                        color: var(--text-white);
                    }

                    .progress-percentage {
                        font-weight: 700;
                        color: var(--text-white);
                        background-color: rgba(255, 255, 255, 0.2);
                        padding: 0.25rem 0.5rem;
                        border-radius: 20px;
                    }

                    .progress-bar-container {
                        height: 8px;
                        background-color: rgba(255, 255, 255, 0.3);
                        border-radius: 4px;
                        overflow: hidden;
                        margin-bottom: 0.5rem;
                    }

                    .progress-bar-fill {
                        height: 100%;
                        background-color: var(--text-white);
                        width: 0%;
                        transition: width 0.5s ease;
                    }

                    /* Add progress-bar-fill dynamic class to handle percentage styling */
                    .progress-bar-fill-dynamic {
                        width: var(--completion-percentage);
                    }

                    .progress-text {
                        display: flex;
                        justify-content: space-between;
                        font-size: 0.85rem;
                        color: var(--text-white);
                    }

                    /* Error message styling */
                    .error-message {
                        background-color: rgba(220, 53, 69, 0.1);
                        border: 1px solid #dc3545;
                        border-radius: 4px;
                        padding: 10px 15px;
                        margin-bottom: 15px;
                        color: #dc3545;
                        font-weight: 500;
                    }

                    /* Course Outline */
                    .course-outline {
                        flex: 1;
                        overflow-y: auto;
                        scrollbar-width: thin;
                        scrollbar-color: var(--primary-light) var(--bg-light);
                    }

                    .course-outline::-webkit-scrollbar {
                        width: 6px;
                    }

                    .course-outline::-webkit-scrollbar-track {
                        background: var(--bg-light);
                    }

                    .course-outline::-webkit-scrollbar-thumb {
                        background-color: var(--primary-light);
                        border-radius: 6px;
                    }

                    .course-outline-header {
                        padding: 1rem;
                        font-weight: 600;
                        color: var(--text-dark);
                        background-color: rgba(var(--primary-color-rgb), 0.05);
                        border-bottom: 1px solid var(--text-light);
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                    }

                    .sidebar-toggle {
                        display: none;
                        background: none;
                        border: none;
                        cursor: pointer;
                        font-size: 1.2rem;
                    }

                    .lesson-list {
                        list-style: none;
                        padding: 0;
                        margin: 0;
                    }

                    .lesson-item {
                        border-bottom: 1px solid rgba(var(--primary-color-rgb), 0.1);
                        margin-bottom: 2px;
                    }

                    .lesson-header {
                        padding: 1rem;
                        background-color: var(--bg-white);
                        cursor: pointer;
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                        transition: all var(--transition-speed);
                    }

                    .lesson-header:hover {
                        opacity: 0.9;
                    }

                    .lesson-header.active {
                        background: linear-gradient(45deg, var(--secondary-color), var(--secondary-light));
                        color: var(--text-white);
                        box-shadow: 0 2px 5px rgba(var(--primary-color-rgb), 0.3);
                    }

                    .lesson-header.locked {
                        color: var(--text-medium);
                        background-color: rgba(var(--text-medium-rgb), 0.05);
                    }

                    .lesson-header.locked .lesson-number {
                        background-color: var(--text-medium);
                    }

                    .lesson-title-wrapper {
                        display: flex;
                        align-items: center;
                        gap: 0.8rem;
                    }

                    .lesson-number {
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        width: 28px;
                        height: 28px;
                        border-radius: 50%;
                        background-color: var(--primary-color);
                        color: var(--text-white);
                        font-size: 0.8rem;
                        font-weight: 600;
                        box-shadow: 0 2px 4px rgba(var(--primary-color-rgb), 0.4);
                    }

                    .lesson-title {
                        font-weight: 500;
                    }

                    .lesson-status {
                        display: flex;
                        align-items: center;
                        gap: 0.5rem;
                    }

                    .lesson-status-indicator {
                        font-size: 0.8rem;
                    }

                    .lesson-status-text {
                        font-size: 0.8rem;
                    }

                    .content-list {
                        list-style: none;
                        padding: 0;
                        margin: 0;
                        background-color: rgba(var(--bg-light-rgb), 0.6);
                        max-height: 0;
                        overflow: hidden;
                        transition: max-height 0.5s ease;
                    }

                    .lesson-item.expanded .content-list {
                        max-height: 1000px;
                    }

                    .content-item {
                        border-bottom: 1px solid rgba(var(--text-light-rgb), 0.5);
                    }

                    .content-link {
                        display: flex;
                        align-items: center;
                        padding: 0.75rem 1rem 0.75rem 1.3rem;
                        text-decoration: none;
                        color: var(--text-dark);
                        transition: background linear 0.1s;
                    }

                    .content-link.active {
                        position: relative;
                    }

                    .content-link.active::before {
                        position: absolute;
                        content: "";
                        left: 0;
                        top: 0;
                        width: 3px;
                        height: 100%;
                        color: var(--primary-color);
                        border-left: 5px solid var(--primary-color);
                    }

                    .content-link.locked {
                        color: var(--text-medium);
                        cursor: not-allowed;
                        opacity: 0.7;
                        pointer-events: none;
                    }

                    .content-link.locked .content-icon {
                        color: var(--text-medium);
                    }

                    .content-link.locked .content-status {
                        color: var(--text-medium);
                    }

                    .content-icon {
                        margin-right: 0.75rem;
                        width: 18px;
                        text-align: center;
                        color: var(--primary-color);
                    }

                    .content-title {
                        flex: 1;
                        font-size: 0.9rem;
                    }

                    .content-status {
                        font-size: 0.8rem;
                    }

                    /* Main Content Area */
                    .main-content {
                        flex: 1;
                        display: flex;
                        flex-direction: column;
                        height: 100%;
                        overflow-y: auto;
                        scrollbar-width: thin;
                        scrollbar-color: var(--primary-light) var(--bg-light);
                    }

                    .main-content::-webkit-scrollbar {
                        width: 8px;
                    }

                    .main-content::-webkit-scrollbar-track {
                        background: var(--bg-light);
                    }

                    .main-content::-webkit-scrollbar-thumb {
                        background-color: var(--primary-light);
                        border-radius: 8px;
                    }

                    /* Mobile Menu Button */
                    .mobile-menu-button {
                        display: none;
                        position: fixed;
                        bottom: 20px;
                        right: 20px;
                        width: 50px;
                        height: 50px;
                        border-radius: 50%;
                        background-color: var(--primary-color);
                        color: white;
                        border: none;
                        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
                        z-index: 100;
                        font-size: 1.5rem;
                        cursor: pointer;
                        transition: transform 0.2s ease;
                    }

                    .mobile-menu-button:hover,
                    .mobile-menu-button:focus {
                        transform: scale(1.05);
                    }

                    /* Content Header */
                    .content-header {
                        padding: 1.5rem;
                        background-color: var(--bg-white);
                        border-bottom: 1px solid var(--text-light);
                        box-shadow: var(--box-shadow);
                    }

                    .content-breadcrumb {
                        display: flex;
                        align-items: center;
                        gap: 0.5rem;
                        margin-bottom: 0.5rem;
                        font-size: 0.9rem;
                        color: var(--text-medium);
                    }

                    .content-heading {
                        font-size: 1.75rem;
                        font-weight: 700;
                        color: var(--text-dark);
                        margin-bottom: 0.5rem;
                    }

                    .content-subheading {
                        color: var(--text-medium);
                    }

                    /* Content Area */
                    .content-area {
                        flex: 1;
                        padding: 0 1.5rem 3.5rem;
                        margin-top: 1.5rem;
                    }

                    /* Video Section */
                    .video-container {
                        position: relative;
                        width: 100%;
                        padding-bottom: 45%;
                        overflow: hidden;
                        margin-bottom: 1.5rem;
                    }

                    .video-container video {
                        position: absolute;
                        top: 0;
                        left: 0;
                        width: 100%;
                        height: 100%;
                        object-fit: contain;
                        border-radius: 10px;
                        border: 2px solid pink;
                    }

                    /* Material Section */
                    .material-container {
                        position: relative;
                        width: 100%;
                        border-radius: 10px;
                        border: 1px solid var(--primary-light);
                        padding: 1.5rem;
                        margin-bottom: 1.5rem;
                        background-color: var(--bg-white);
                        box-shadow: var(--box-shadow);
                    }

                    .material-content {
                        line-height: 1.6;
                    }

                    .material-title {
                        color: var(--primary-color);
                        margin-bottom: 1rem;
                        font-weight: 600;
                    }

                    /* Quiz Section */
                    .quiz-container {
                        position: relative;
                        width: 100%;
                        border-radius: 10px;
                        border: 1px solid var(--secondary-light);
                        padding: 1.5rem;
                        margin-bottom: 1.5rem;
                        background-color: var(--bg-white);
                        box-shadow: var(--box-shadow);
                    }

                    .quiz-title {
                        color: var(--secondary-color);
                        margin-bottom: 1rem;
                        font-weight: 600;
                    }

                    /* Content Item Highlight */
                    .content-link:not(.locked):hover .content-icon {
                        transform: scale(1.2);
                        transition: transform 0.2s ease;
                    }

                    .content-icon {
                        transition: transform 0.2s ease;
                    }

                    /* Navigation Controls */
                    .content-navigation {
                        display: flex;
                        justify-content: space-between;
                        margin-top: 2rem;
                        padding-top: 1rem;
                        border-top: 1px solid var(--text-light);
                    }

                    .nav-button {
                        display: inline-flex;
                        align-items: center;
                        padding: 0.5rem 1rem;
                        border-radius: var(--border-radius);
                        text-decoration: none;
                        font-weight: 500;
                        transition: all var(--transition-speed);
                    }

                    .nav-button-prev {
                        color: var(--text-medium);
                        border: 1px solid var(--text-light);
                    }

                    .nav-button-prev:hover {
                        background-color: var(--bg-light);
                        color: var(--text-dark);
                    }

                    .nav-button-next {
                        background-color: var(--primary-color);
                        color: var(--text-white);
                    }

                    .nav-button-next:hover {
                        background-color: var(--primary-dark);
                    }

                    .nav-button-next.disabled {
                        background-color: var(--text-medium);
                        cursor: not-allowed;
                    }

                    .nav-button i {
                        font-size: 0.8rem;
                    }

                    .nav-button-prev i {
                        margin-right: 0.5rem;
                    }

                    .nav-button-next i {
                        margin-left: 0.5rem;
                    }

                    /* Mark Complete Button */
                    .mark-complete-button {
                        display: block;
                        width: 100%;
                        padding: 0.75rem;
                        background-color: var(--success);
                        color: var(--text-white);
                        border: none;
                        border-radius: var(--border-radius);
                        font-weight: 600;
                        margin-top: 1rem;
                        cursor: pointer;
                        transition: background-color var(--transition-speed);
                    }

                    .mark-complete-button:hover {
                        background-color: #2d9147;
                    }

                    .mark-complete-button.completed {
                        background-color: var(--text-medium);
                    }
                    
                    .course-outline-modal {
                        display: none;
                    }

                    /* Responsive Design */
                    @media (max-width: 992px) {
                        .learning-container {
                            flex-direction: column;
                        }

                        .left-panel {
                            width: 100%;
                            position: relative;
                            top: 0;
                            display: contents;
                        }

                        .sidebar-toggle {
                            display: block;
                        }

                        .mobile-menu-button {
                            display: flex;
                            justify-content: center;
                            align-items: center;
                        }

                        .course-outline {
                            display: none;
                            padding-bottom: 0;
                        }

                        .course-outline-header {
                            background: var(--primary-color);
                            color: #fff;
                        }

                        /* Modal styles for mobile */
                        .course-outline-modal {
                            position: fixed;
                            top: 0;
                            left: 0;
                            width: 100%;
                            height: 100%;
                            background-color: rgba(0, 0, 0, 0.5);
                            z-index: 1000;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            opacity: 0;
                            visibility: hidden;
                            transition: opacity 0.3s ease, visibility 0.3s ease;
                        }

                        .course-outline-modal.active {
                            opacity: 1;
                            visibility: visible;
                        }

                        .course-outline-modal-content {
                            position: fixed;
                            top: 92px;
                            left: 0;
                            bottom: 0;
                            background-color: white;
                            width: 50%;
                            max-width: 500px;
                            overflow: hidden;
                            display: flex;
                            flex-direction: column;
                            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
                            transform: translateX(-100px);
                            transition: transform 0.3s ease;
                        }

                        .course-outline-modal.active .course-outline-modal-content {
                            transform: translateX(0);
                        }

                        .course-outline-modal-close {
                            background: none;
                            border: none;
                            color: white;
                            font-size: 1.5rem;
                            cursor: pointer;
                            padding: 0;
                            line-height: 1;
                        }

                        .course-outline-modal-body {
                            flex: 1;
                            overflow-y: auto;
                        }

                        .course-outline-modal .course-outline {
                            display: block;
                            height: 100%;
                            border-radius: 0;
                            box-shadow: none;
                        }

                        .content-area {
                            margin: 1rem;
                            padding: 0 1.5rem 6rem;
                        }
                    }

                    /* Lock Icon Animation */
                    @keyframes lock-wiggle {
                        0% {
                            transform: rotate(0deg);
                        }

                        25% {
                            transform: rotate(-10deg);
                        }

                        50% {
                            transform: rotate(0deg);
                        }

                        75% {
                            transform: rotate(10deg);
                        }

                        100% {
                            transform: rotate(0deg);
                        }
                    }

                    .lesson-item.locked:hover .fa-lock {
                        animation: lock-wiggle 0.5s ease;
                    }

                    /* Preview Mode Styles */
                    .lesson-item.preview .lesson-header {
                        color: var(--text-medium);
                        background-color: rgba(255, 193, 7, 0.1);
                    }

                    .lesson-item.preview .lesson-header:hover {
                        background-color: rgba(255, 193, 7, 0.2);
                    }

                    .text-warning {
                        color: #ffc107 !important;
                    }

                    .content-link.preview-only {
                        display: flex;
                        align-items: center;
                        padding: 0.75rem 1rem 0.75rem 3rem;
                        text-decoration: none;
                        color: var(--text-medium) !important;
                        cursor: default;
                        opacity: 0.8;
                        position: relative;
                    }

                    .content-link.preview-only::after {
                        content: "Xem trước";
                        position: absolute;
                        right: 8px;
                        top: 8px;
                        font-size: 0.7em;
                        background-color: rgba(255, 193, 7, 0.3);
                        padding: 2px 5px;
                        border-radius: 3px;
                        color: #856404;
                    }

                    .lesson-item.preview .content-link {
                        position: relative;
                    }

                    @keyframes eye-blink {
                        0% {
                            transform: scale(1);
                        }

                        45% {
                            transform: scale(1);
                        }

                        50% {
                            transform: scale(0.8);
                        }

                        55% {
                            transform: scale(1);
                        }

                        100% {
                            transform: scale(1);
                        }
                    }

                    .fa-eye {
                        animation: eye-blink 3s infinite;
                    }

                    /* Completion Icon Animation */
                    @keyframes check-pop {
                        0% {
                            transform: scale(0);
                        }

                        70% {
                            transform: scale(1.2);
                        }

                        100% {
                            transform: scale(1);
                        }
                    }

                    .content-item.newly-completed .fa-check-circle {
                        animation: check-pop 0.5s ease;
                    }
                </style>
            </head>

            <body>
                <!-- Include navigation -->
                <jsp:include page="/WEB-INF/views/customer/common/navigation.jsp" />

                <div class="learning-container">
                    <!-- Left Panel with Course Outline -->
                    <div class="left-panel">
                        <!-- Progress Section -->
                        <div class="progress-section">
                            <div class="progress-header">
                                <div class="progress-title">Course Progress</div>
                                <div class="progress-percentage" id="progressPercentage">
                                    <fmt:formatNumber value="${progress.completionPercentage}" pattern="0.##" />%
                                </div>
                            </div>
                            <div class="progress-bar-container">
                                <div class="progress-bar-fill progress-bar-fill-dynamic" id="progressBarFill"
                                    style="--completion-percentage: ${progress.completionPercentage}%;"></div>
                            </div>
                            <div class="progress-text">
                                <span>
                                    <c:set var="completedLessons" value="${0}" />
                                    <c:forEach var="lesson" items="${course.lessons}">
                                        <c:if test="${lesson.completed}">
                                            <c:set var="completedLessons" value="${completedLessons + 1}" />
                                        </c:if>
                                    </c:forEach>
                                    <span id="completionText">${completedLessons} of ${course.lessons.size()}
                                        lessons</span>
                                </span>
                                <c:choose>
                                    <c:when test="${progress.completionPercentage == 100}">
                                        <span class="text-success">Completed</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="btn-warning">In Progress</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                        <!-- Course Outline -->
                        <div class="course-outline" id="courseOutline">
                            <div class="course-outline-header">
                                <span>Course Content</span>

                                <button class="course-outline-modal-close d-lg-none" id="modalClose">&times;</button>
                            </div>
                            <!-- Loading overlay -->
                            <div class="loading-overlay" id="loadingOverlay">
                                <div class="loading-dots">
                                    <div class="dot"></div>
                                    <div class="dot"></div>
                                    <div class="dot"></div>
                                </div>
                            </div>
                            <ul class="lesson-list">
                                <c:forEach var="lesson" items="${course.lessons}" varStatus="lessonStatus">
                                    <c:set var="isCurrentLesson" value="${currentLesson.lessonID == lesson.lessonID}" />

                                    <!-- Check if all items in this lesson are completed -->
                                    <c:set var="allItemsCompleted" value="true" />
                                    <c:forEach var="lessonItem" items="${lesson.lessonItems}">

                                        <c:set var="itemType" value="${lessonItem.itemType.toLowerCase()}" />
                                        <c:set var="itemId" value="${lessonItem.itemID}" />
                                        <c:set var="isItemCompleted" value="false" />

                                        <c:choose>
                                            <c:when test="${itemType == 'video' && videoCompletedMap[itemId] == true}">
                                                <c:set var="isItemCompleted" value="true" />
                                            </c:when>
                                            <c:when
                                                test="${itemType == 'material' && materialCompletedMap[itemId] == true}">
                                                <c:set var="isItemCompleted" value="true" />
                                            </c:when>
                                            <c:when test="${itemType == 'quiz' && quizCompletedMap[itemId] == true}">
                                                <c:set var="isItemCompleted" value="true" />
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="isItemCompleted" value="false" />
                                            </c:otherwise>
                                        </c:choose>

                                        <c:if test="${!isItemCompleted && lesson.lessonItems.size() > 0}">
                                            <c:set var="allItemsCompleted" value="false" />
                                        </c:if>
                                    </c:forEach>

                                    <!-- Check if this lesson is unlocked -->
                                    <c:set var="isLessonUnlocked" value="false" />
                                    <c:choose>
                                        <c:when test="${lessonStatus.index == 0}">
                                            <!-- First lesson is always unlocked -->
                                            <c:set var="isLessonUnlocked" value="true" />
                                        </c:when>
                                        <c:when test="${lessonStatus.index > 0}">
                                            <!-- Check if previous lesson is completed -->
                                            <c:set var="prevLesson" value="${course.lessons[lessonStatus.index-1]}" />
                                            <c:set var="prevLessonCompleted" value="true" />
                                            <c:forEach var="prevLessonItem" items="${prevLesson.lessonItems}">
                                                <c:set var="prevItemType"
                                                    value="${prevLessonItem.itemType.toLowerCase()}" />
                                                <c:set var="prevItemId" value="${prevLessonItem.itemID}" />
                                                <c:set var="isPrevItemCompleted" value="false" />

                                                <c:choose>
                                                    <c:when
                                                        test="${prevItemType == 'video' && videoCompletedMap[prevItemId] == true}">
                                                        <c:set var="isPrevItemCompleted" value="true" />
                                                    </c:when>
                                                    <c:when
                                                        test="${prevItemType == 'material' && materialCompletedMap[prevItemId] == true}">
                                                        <c:set var="isPrevItemCompleted" value="true" />
                                                    </c:when>
                                                    <c:when
                                                        test="${prevItemType == 'quiz' && quizCompletedMap[prevItemId] == true}">
                                                        <c:set var="isPrevItemCompleted" value="true" />
                                                    </c:when>
                                                </c:choose>

                                                <c:if
                                                    test="${!isPrevItemCompleted && prevLesson.lessonItems.size() > 0}">
                                                    <c:set var="prevLessonCompleted" value="false" />
                                                </c:if>
                                            </c:forEach>

                                            <c:set var="isLessonUnlocked" value="${prevLessonCompleted}" />
                                        </c:when>
                                    </c:choose>

                                    <li class="lesson-item ${isCurrentLesson ? 'expanded' : ''}">
                                        <div
                                            class="lesson-header ${isCurrentLesson ? 'active' : ''} ${!isLessonUnlocked ? 'locked' : ''}">
                                            <div class="lesson-title-wrapper">
                                                <div class="lesson-number">${lessonStatus.index + 1}</div>
                                                <div class="lesson-title">${lesson.title}</div>
                                            </div>
                                            <div class="lesson-status">
                                                <c:choose>
                                                    <c:when
                                                        test="${allItemsCompleted && lesson.lessonItems.size() > 0}">
                                                        <span class="lesson-status-indicator text-success">
                                                            <i class="fas fa-check-circle"></i>
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${!isLessonUnlocked}">
                                                        <span class="lesson-status-indicator text-secondary">
                                                            <i class="fas fa-lock"></i>
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${isCurrentLesson}">
                                                        <span class="lesson-status-indicator">
                                                            <i class="fas fa-eye"></i>
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="lesson-status-indicator">
                                                            <i class="far fa-circle"></i>
                                                        </span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>

                                        <!-- List of videos, materials, quizzes using combined content list -->
                                        <ul class="content-list">
                                            <!-- Process all lesson items in order -->
                                            <c:set var="lastCompletedItemIndex" value="-1" />
                                            <c:forEach var="lessonItem" items="${lesson.lessonItems}"
                                                varStatus="itemStatus">
                                                <c:set var="itemType" value="${lessonItem.itemType.toLowerCase()}" />
                                                <c:set var="itemId" value="${lessonItem.itemID}" />
                                                <c:set var="orderIdex" value="${lessonItem.orderIndex}" />
                                                <c:set var="contentItem" value="${lessonItem.item}" />
                                                <c:set var="isCurrentContent" value="false" />
                                                <c:set var="isContentCompleted" value="false" />
                                                <c:set var="contentIcon" value="" />
                                                <c:set var="contentTitle" value="" />
                                                <c:set var="contentStatus" value="" />

                                                <c:choose>
                                                    <c:when test="${itemType == 'video'}">
                                                        <c:set var="isCurrentContent"
                                                            value="${contentType == 'video' && currentVideo != null && currentVideo.videoID == itemId}" />
                                                        <c:set var="isContentCompleted"
                                                            value="${videoCompletedMap[itemId] == true}" />
                                                        <c:if test="${isContentCompleted}">
                                                            <c:set var="lastCompletedItemIndex"
                                                                value="${itemStatus.index}" />
                                                        </c:if>
                                                        <c:set var="contentIcon" value="fas fa-video" />
                                                        <c:set var="contentTitle" value="${contentItem.title}" />
                                                        <c:set var="contentStatus">
                                                            <c:choose>
                                                                <c:when test="${isContentCompleted}">
                                                                    <i class="fas fa-check-circle text-success"></i>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <c:set var="minutes"
                                                                        value="${Math.floor(contentItem.duration / 60)}" />
                                                                    <c:set var="seconds"
                                                                        value="${contentItem.duration % 60}" />
                                                                    <fmt:formatNumber value="${minutes}" pattern="#"
                                                                        var="formattedMinutes" />
                                                                    <fmt:formatNumber value="${seconds}" pattern="00"
                                                                        var="formattedSeconds" />
                                                                    <span
                                                                        class="video-duration">${formattedMinutes}:${formattedSeconds}</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:set>
                                                    </c:when>
                                                    <c:when test="${itemType == 'material'}">
                                                        <c:set var="isCurrentContent"
                                                            value="${contentType == 'material' && currentMaterial != null && currentMaterial.materialID == itemId}" />
                                                        <c:set var="isContentCompleted"
                                                            value="${materialCompletedMap[itemId] == true}" />
                                                        <c:if test="${isContentCompleted}">
                                                            <c:set var="lastCompletedItemIndex"
                                                                value="${itemStatus.index}" />
                                                        </c:if>
                                                        <c:set var="contentIcon" value="fas fa-file-alt" />
                                                        <c:set var="contentTitle" value="${contentItem.title}" />
                                                        <c:set var="contentStatus">
                                                            <c:choose>
                                                                <c:when test="${isContentCompleted}">
                                                                    <i class="fas fa-check-circle text-success"></i>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <i class="fas fa-file-alt"></i>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:set>
                                                    </c:when>
                                                    <c:when test="${itemType == 'quiz'}">
                                                        <c:set var="isCurrentContent"
                                                            value="${contentType == 'quiz' && currentQuiz != null && currentQuiz.quizID == itemId}" />
                                                        <c:set var="isContentCompleted"
                                                            value="${quizCompletedMap[itemId] == true}" />
                                                        <c:if test="${isContentCompleted}">
                                                            <c:set var="lastCompletedItemIndex"
                                                                value="${itemStatus.index}" />
                                                        </c:if>
                                                        <c:set var="contentIcon" value="fas fa-question-circle" />
                                                        <c:set var="contentTitle" value="${contentItem.title}" />
                                                        <c:set var="contentStatus">
                                                            <c:choose>
                                                                <c:when test="${isContentCompleted}">
                                                                    <i class="fas fa-check-circle text-success"></i>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span>${contentItem.totalQuestions} questions</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:set>
                                                    </c:when>
                                                </c:choose>

                                                <!-- Determine if this item is unlocked -->
                                                <c:set var="isItemUnlocked" value="false" />
                                                <c:choose>
                                                    <c:when test="${!isLessonUnlocked}">
                                                        <!-- If lesson is locked, all items are locked -->
                                                        <c:set var="isItemUnlocked" value="false" />
                                                    </c:when>
                                                    <c:when test="${itemStatus.index == 0}">
                                                        <!-- First item in an unlocked lesson is always unlocked -->
                                                        <c:set var="isItemUnlocked" value="true" />
                                                    </c:when>
                                                    <c:when test="${isCurrentContent}">
                                                        <!-- Current content is always unlocked -->
                                                        <c:set var="isItemUnlocked" value="true" />
                                                    </c:when>
                                                    <c:when test="${itemStatus.index <= lastCompletedItemIndex + 1}">
                                                        <!-- Item is unlocked if previous item is completed -->
                                                        <c:set var="isItemUnlocked" value="true" />
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:set var="isItemUnlocked" value="false" />
                                                    </c:otherwise>
                                                </c:choose>


                                                <li class="content-item ${isCurrentContent?'active':''}${isContentCompleted ? 'completed' : ''}"
                                                    data-id="${itemId}" data-type="${itemType}"
                                                    order-index="${orderIdex}">
                                                    <c:choose>
                                                        <c:when test="${isItemUnlocked}">
                                                            <a href="${pageContext.request.contextPath}/learning/${itemType}/${itemId}"
                                                                class="content-link ${isCurrentContent ? 'active' : ''}">
                                                                <span class="content-icon">
                                                                    <i class="${contentIcon}"></i>
                                                                </span>
                                                                <span class="content-title">${contentTitle}</span>
                                                                <span class="content-status">
                                                                    ${contentStatus}
                                                                </span>
                                                            </a>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="content-link locked">
                                                                <span class="content-icon">
                                                                    <i class="${contentIcon}"></i>
                                                                </span>
                                                                <span class="content-title">${contentTitle}</span>
                                                                <span class="content-status">
                                                                    <i class="fas fa-lock"></i>
                                                                </span>
                                                            </span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </li>
                                </c:forEach>
                            </ul>
                        </div>
                    </div>

                    <!-- Main Content Area -->
                    <div class="main-content-area flex-grow-1 overflow-auto">
                        <!-- Error message display -->
                        <c:if test="${not empty errorMessage}">
                            <div class="error-message mx-4 mt-3">
                                <i class="fa fa-exclamation-triangle me-2"></i> ${errorMessage}
                            </div>
                        </c:if>

                        <div class="main-content">
                            <!-- Content Header -->
                            <div class="content-header">
                                <div class="content-breadcrumb">
                                    <a href="${pageContext.request.contextPath}/my-courses"
                                        class="text-decoration-none text-muted">My
                                        Courses</a>
                                    <i class="fas fa-chevron-right fa-xs"></i>
                                    <a href="${pageContext.request.contextPath}/learning/${course.courseID}"
                                        class="text-decoration-none text-muted">${course.name}</a>
                                    <c:if test="${currentLesson != null}">
                                        <i class="fas fa-chevron-right fa-xs"></i>
                                        <span>Lesson
                                            ${currentLesson.orderIndex}:
                                            ${currentLesson.title}</span>
                                    </c:if>
                                </div>
                                <h1 class="content-heading">
                                    <c:choose>
                                        <c:when test="${contentType == 'material' && currentMaterial != null}">
                                            ${currentMaterial.title}
                                        </c:when>
                                        <c:when test="${contentType == 'quiz' && currentQuiz != null}">
                                            ${currentQuiz.title}
                                        </c:when>
                                    </c:choose>
                                </h1>

                                <!-- Discussion Button -->
                                <c:if test="${currentLesson != null}">
                                    <jsp:include page="lesson-discussion-button.jsp" />
                                </c:if>

                                <p class="content-subheading">
                                    <c:choose>
                                        <c:when test="${contentType == 'video' && currentVideo != null}">
                                            <%-- Video description is now shown below the video --%>
                                        </c:when>
                                        <c:when test="${contentType == 'material' && currentMaterial != null}">
                                            ${currentMaterial.description}
                                        </c:when>
                                        <c:when test="${contentType == 'quiz' && currentQuiz != null}">
                                            ${currentQuiz.description}
                                        </c:when>
                                        <c:otherwise>
                                            ${currentLesson.description}
                                        </c:otherwise>
                                    </c:choose>
                                </p>
                            </div>

                            <!-- Content Area -->
                            <div class="content-area">
                                <!-- Mobile menu button -->
                                <button class="mobile-menu-button" id="mobileMenuButton">
                                    <i class="fas fa-list"></i>
                                </button>
                                <c:choose>
                                    <%-- Video Content --%>
                                        <c:when test="${contentType == 'video' && currentVideo != null}">
                                            <div class="video-container">
                                                <!-- Video player -->
                                                <video id="courseVideo" controls>
                                                    <source
                                                        src="${pageContext.request.contextPath}/${currentVideo.videoUrl}"
                                                        type="video/mp4">
                                                    Your browser does not support the video tag.
                                                </video>
                                            </div>

                                            <%-- Video title and description --%>
                                                <div class="video-details mt-4 mb-4">
                                                    <h3 class="video-title">
                                                        ${currentVideo.title}
                                                    </h3>
                                                    <p class="video-description">
                                                        ${currentVideo.description}
                                                    </p>
                                                </div>

                                                <!-- Video Navigation -->
                                                <div class="content-navigation">
                                                    <c:choose>
                                                        <c:when
                                                            test="${navigation != null && navigation.previous != null}">
                                                            <a href="${pageContext.request.contextPath}/learning/${navigation.previous.type}/${navigation.previous.id}"
                                                                class="nav-button nav-button-prev">
                                                                <i class="fas fa-arrow-left"></i>
                                                                Previous
                                                            </a>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="nav-button nav-button-prev disabled">
                                                                <i class="fas fa-arrow-left"></i>
                                                                Previous
                                                            </span>
                                                        </c:otherwise>
                                                    </c:choose>

                                                    <c:choose>
                                                        <c:when test="${navigation != null && navigation.next != null}">
                                                            <c:set var="isCompleted" value="false" />
                                                            <c:choose>
                                                                <c:when
                                                                    test="${contentType == 'video' && videoCompletedMap[currentVideo.videoID] == true}">
                                                                    <c:set var="isCompleted" value="true" />
                                                                </c:when>
                                                                <c:when
                                                                    test="${contentType == 'material' && materialCompletedMap[currentMaterial.materialID] == true}">
                                                                    <c:set var="isCompleted" value="true" />
                                                                </c:when>
                                                                <c:when
                                                                    test="${contentType == 'quiz' && quizCompletedMap[currentQuiz.quizID] == true}">
                                                                    <c:set var="isCompleted" value="true" />
                                                                </c:when>
                                                            </c:choose>

                                                            <button
                                                                class="nav-button nav-button-next ${isCompleted ? '' : 'disabled'}"
                                                                id="nextButton" ${isCompleted ? '' : 'disabled' }>
                                                                Next <i class="fas fa-arrow-right"></i>
                                                            </button>
                                                        </c:when>
                                                    </c:choose>
                                                </div>
                                        </c:when>

                                        <%-- Material Content --%>
                                            <c:when test="${contentType == 'material' && currentMaterial != null}">
                                                <div class="material-container">
                                                    <h3 class="material-title">${currentMaterial.title}</h3>
                                                    <div class="material-content">
                                                        ${currentMaterial.content}
                                                    </div>
                                                    <c:if test="${not empty currentMaterial.fileUrl}">
                                                        <div class="mt-4 text-center">
                                                            <a href="${pageContext.request.contextPath}/${currentMaterial.fileUrl}"
                                                                target="_blank" class="btn btn-primary">
                                                                <i class="fas fa-file-download me-2"></i> View Material
                                                            </a>
                                                        </div>
                                                    </c:if>
                                                </div>

                                                <!-- Material Navigation -->
                                                <div class="content-navigation">
                                                    <c:choose>
                                                        <c:when
                                                            test="${navigation != null && navigation.previous != null}">
                                                            <a href="${pageContext.request.contextPath}/learning/${navigation.previous.type}/${navigation.previous.id}"
                                                                class="nav-button nav-button-prev">
                                                                <i class="fas fa-arrow-left"></i>
                                                                Previous
                                                            </a>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="nav-button nav-button-prev disabled">
                                                                <i class="fas fa-arrow-left"></i>
                                                                Previous
                                                            </span>
                                                        </c:otherwise>
                                                    </c:choose>

                                                    <c:choose>
                                                        <c:when test="${navigation != null && navigation.next != null}">
                                                            <a href="${pageContext.request.contextPath}/learning/${navigation.next.type}/${navigation.next.id}"
                                                                class="nav-button nav-button-next" id="nextButton">
                                                                Next <i class="fas fa-arrow-right"></i>
                                                            </a>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:set var="isCompleted" value="false" />
                                                            <c:if
                                                                test="${materialCompletedMap[currentMaterial.materialID] == true}">
                                                                <c:set var="isCompleted" value="true" />
                                                            </c:if>
                                                            <!-- Don't show the Next button if there's no next item -->
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </c:when>

                                            <%-- Quiz Content --%>
                                                <c:when test="${contentType == 'quiz' && currentQuiz != null}">
                                                    <div class="quiz-container">
                                                        <h3 class="quiz-title mb-3">
                                                            ${currentQuiz.title}
                                                        </h3>
                                                        <div class="quiz-details">
                                                            <p class="mb-4">
                                                                ${currentQuiz.description}
                                                            </p>
                                                            <div class="quiz-info mb-4">
                                                                <div class="d-flex align-items-center mb-3">
                                                                    <i
                                                                        class="fas fa-question-circle me-2 text-primary"></i>
                                                                    <span>${currentQuiz.totalQuestions}
                                                                        questions</span>
                                                                </div>
                                                                <div class="d-flex align-items-center mb-3">
                                                                    <i class="fas fa-clock me-2 text-primary"></i>
                                                                    <span>Time
                                                                        limit:
                                                                        <fmt:formatNumber
                                                                            value="${Math.floor(currentQuiz.timeLimit / 60)}"
                                                                            pattern="#" />
                                                                        minutes
                                                                    </span>
                                                                </div>
                                                                <div class="d-flex align-items-center">
                                                                    <i class="fas fa-trophy me-2 text-primary"></i>
                                                                    <span>Passing
                                                                        score:
                                                                        ${currentQuiz.passingScore}%</span>
                                                                </div>
                                                            </div>
                                                            <div class="text-center">
                                                                <c:choose>
                                                                    <c:when
                                                                        test="${quizCompletedMap[currentQuiz.quizID] == true}">
                                                                        <div class="alert alert-success">
                                                                            <i class="fas fa-check-circle me-2"></i>
                                                                            You've
                                                                            already
                                                                            completed
                                                                            this
                                                                            quiz.
                                                                        </div>
                                                                        <button class="btn btn-primary"
                                                                            onclick="startQuiz()">
                                                                            <i class="fas fa-redo me-2"></i>
                                                                            Retake
                                                                            Quiz
                                                                        </button>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <button class="btn btn-primary"
                                                                            onclick="startQuiz()">
                                                                            <i class="fas fa-play me-2"></i>
                                                                            Start
                                                                            Quiz
                                                                        </button>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </div>
                                                        </div>
                                                    </div>

                                                    <!-- Quiz Navigation -->
                                                    <div class="content-navigation">
                                                        <c:choose>
                                                            <c:when
                                                                test="${navigation != null && navigation.previous != null}">
                                                                <a href="${pageContext.request.contextPath}/learning/${navigation.previous.type}/${navigation.previous.id}"
                                                                    class="nav-button nav-button-prev">
                                                                    <i class="fas fa-arrow-left"></i>
                                                                    Previous
                                                                </a>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="nav-button nav-button-prev disabled">
                                                                    <i class="fas fa-arrow-left"></i>
                                                                    Previous
                                                                </span>
                                                            </c:otherwise>
                                                        </c:choose>

                                                        <c:choose>
                                                            <c:when
                                                                test="${navigation != null && navigation.next != null && quizCompletedMap[currentQuiz.quizID] == true}">
                                                                <a href="${pageContext.request.contextPath}/learning/${navigation.next.type}/${navigation.next.id}"
                                                                    class="nav-button nav-button-next" id="nextButton">
                                                                    Next <i class="fas fa-arrow-right"></i>
                                                                </a>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <c:if
                                                                    test="${navigation != null && navigation.next != null}">
                                                                    <button class="nav-button nav-button-next disabled"
                                                                        id="nextButton" disabled>
                                                                        Next <i class="fas fa-arrow-right"></i>
                                                                    </button>
                                                                </c:if>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </c:when>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Course Outline Modal for Mobile -->
                <div class="course-outline-modal" id="courseOutlineModal">
                    <div class="course-outline-modal-content">
                        <div class="course-outline-modal-body" id="courseOutlineModalBody">
                            <!-- Course outline will be cloned here -->
                        </div>
                    </div>
                </div>

                <!-- Bootstrap JS -->
                <script
                    src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>

                <script>
                    document.addEventListener('DOMContentLoaded', function () {
                        // Progress bar setup
                        const progressBarFill = document.querySelector('.progress-bar-fill');
                        if (progressBarFill) {
                            progressBarFill.style.width = '${progress.completionPercentage}%';
                        }

                        // Lesson expand/collapse
                        const lessonHeaders = document.querySelectorAll('.lesson-header');
                        lessonHeaders.forEach(header => {
                            header.addEventListener('click', function () {
                                const lessonItem = this.parentElement;
                                lessonItem.classList.toggle('expanded');
                            });
                        });

                        // Auto-expand current lesson without scrolling (fix jumping issue)
                        const currentLessonItem = document.querySelector('.lesson-item.expanded');

                        // Function to scroll to a specific lesson item
                        function scrollToLessonItem(itemId, itemType) {
                            const targetItem = document.querySelector(`.content-item[data-id="\${itemId}"][data-type="\${itemType}"]`);
                            if (targetItem) {
                                const targetLesson = document.querySelector('.lesson-header.active');
                                const courseOutline = document.querySelector('.course-outline');
                                if (courseOutline) {
                                    // Get the position of the item relative to the course-outline
                                    const offsetTop = targetLesson.offsetTop;
                                    // Scroll the course-outline to the position with some padding
                                    courseOutline.scrollTop = offsetTop - 200;
                                }
                            }

                            // Hide loading overlay after scrolling is complete
                            setTimeout(() => {
                                hideLoadingOverlay();
                            }, 500);
                        }

                        // Function to show loading overlay
                        function showLoadingOverlay() {
                            const loadingOverlay = document.getElementById('loadingOverlay');
                            if (loadingOverlay) {
                                loadingOverlay.classList.add('active');

                                // Hide the lesson list while loading
                                const lessonList = document.querySelector('.lesson-list');
                                if (lessonList) {
                                    lessonList.style.opacity = '0';
                                }
                            }
                        }

                        // Function to hide loading overlay
                        function hideLoadingOverlay() {
                            const loadingOverlay = document.getElementById('loadingOverlay');
                            if (loadingOverlay) {
                                loadingOverlay.classList.remove('active');

                                // Show the lesson list after loading
                                const lessonList = document.querySelector('.lesson-list');
                                if (lessonList) {
                                    lessonList.style.opacity = '1';
                                }
                            }
                        }

                        // Add click event listeners to all content links
                        document.querySelectorAll('.content-link:not(.locked)').forEach(link => {
                            link.addEventListener('click', function (e) {
                                // Show loading overlay when clicking on a lesson item
                                showLoadingOverlay();

                                // Extract the itemId and itemType from the URL
                                const url = this.getAttribute('href');
                                if (url) {
                                    const urlParts = url.split('/');
                                    const itemType = urlParts[urlParts.length - 2];
                                    const itemId = urlParts[urlParts.length - 1].split('?')[0];

                                    // Store the values in session storage to use after page load
                                    sessionStorage.setItem('lastClickedItemId', itemId);
                                    sessionStorage.setItem('lastClickedItemType', itemType);
                                }
                            });
                        });

                        // Check if we have stored values from a previous click
                        const lastClickedItemId = sessionStorage.getItem('lastClickedItemId');
                        const lastClickedItemType = sessionStorage.getItem('lastClickedItemType');

                        // If we have the stored values and the current page matches, scroll to the item
                        if (lastClickedItemId && lastClickedItemType) {
                            // Get the current URL to check if we're on the right page
                            const currentUrl = window.location.href;
                            if (currentUrl.includes(`/learning/\${lastClickedItemType}/\${lastClickedItemId}`)) {
                                // Use setTimeout to ensure the DOM is fully loaded
                                setTimeout(() => {
                                    scrollToLessonItem(lastClickedItemId, lastClickedItemType);
                                    // Clear the session storage items after use
                                    sessionStorage.removeItem('lastClickedItemId');
                                    sessionStorage.removeItem('lastClickedItemType');
                                }, 300);
                            }
                        }

                        // Get next button and set up its event listener
                        const nextButton = document.getElementById('nextButton');
                        if (nextButton) {
                            nextButton.addEventListener('click', function () {
                                if (nextButton.classList.contains('disabled') || nextButton.disabled) {
                                    return; // Don't proceed if button is disabled
                                }

                                // Determine what content type we're dealing with
                                const contentType = "${contentType}";
                                let contentId = 0;
                                // Disable button to prevent double submissions
                                nextButton.disabled = true;
                                nextButton.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Processing...';
                                if (contentType === 'video') {
                                    contentId = parseInt("${currentVideo != null ? currentVideo.videoID : 0}");
                                    // Check if there's a next navigation item
                                    if ('${navigation.next != null}' === 'true') {
                                        window.location.href = "${pageContext.request.contextPath}/learning/${navigation.next.type}/${navigation.next.id}";
                                    }
                                } else if (contentType === 'material') {
                                    contentId = parseInt("${currentMaterial != null ? currentMaterial.materialID : 0}");
                                    if (contentId > 0) {
                                        // Call the mark complete function and navigate to next
                                        markComplete(contentId, contentType);
                                    }
                                } else if (contentType === 'quiz') {
                                    contentId = parseInt("${currentQuiz != null ? currentQuiz.quizID : 0}");
                                    // Check if there's a next navigation item
                                    if ('${navigation.next != null}' === 'true') {
                                        window.location.href = "${pageContext.request.contextPath}/learning/${navigation.next.type}/${navigation.next.id}";
                                    }
                                }
                            });
                        }

                        // Content type specific handling
                        const contentType = "${contentType}";

                        // Video completion tracking
                        if (contentType === 'video') {
                            const videoElement = document.getElementById('courseVideo');
                            if (videoElement && nextButton) {
                                let isEnded = false;
                                let playTime = 0;
                                const videoId = parseInt("${currentVideo != null ? currentVideo.videoID : 0}");
                                const videoDuration = parseFloat("${currentVideo != null ? currentVideo.duration : 0}");

                                // Only track if the video hasn't been completed yet
                                const isVideoCompletedStr = "${currentVideo != null && videoCompletedMap[currentVideo.videoID] == true ? 'true' : 'false'}";
                                const isVideoCompleted = isVideoCompletedStr === "true";

                                if (isVideoCompleted) {
                                    // If already completed, enable the next button if it exists
                                    if (nextButton) {
                                        nextButton.disabled = false;
                                        nextButton.classList.remove('disabled');

                                        // Also unlock the next item if not already unlocked
                                        unlockNextItem();
                                    }
                                } else if (videoId > 0) {
                                    // Otherwise track progress
                                    videoElement.addEventListener('timeupdate', function timeUpdateHandler() {
                                        playTime = videoElement.currentTime;
                                        // If user watched at least 80% of the video, enable the Next button and unlock next item
                                        if (playTime >= videoDuration * 0.8) {
                                            if (nextButton) {
                                                nextButton.disabled = false;
                                                nextButton.classList.remove('disabled');
                                            }

                                            // Unlock next item if not already unlocked
                                            unlockNextItem();

                                            // If not already marked as complete and watched 80%
                                            if (!isEnded) {
                                                isEnded = true;
                                                markComplete(videoId, contentType)
                                            }

                                            // Stop tracking the video once threshold is reached
                                            videoElement.removeEventListener('timeupdate', timeUpdateHandler);
                                        }
                                    });
                                }
                            }
                        }
                        // For material content
                        else if (contentType === 'material' && nextButton) {
                            // Materials don't require completion to navigate
                            nextButton.disabled = false;
                            nextButton.classList.remove('disabled');

                            // But we should still mark it complete when they click next
                            const materialId = parseInt("${currentMaterial != null ? currentMaterial.materialID : 0}");
                            const isMaterialCompletedStr = "${currentMaterial != null && materialCompletedMap[currentMaterial.materialID] == true ? 'true' : 'false'}";
                            const isMaterialCompleted = isMaterialCompletedStr === "true";

                            if (isMaterialCompleted) {
                                // If already completed, unlock the next item
                                unlockNextItem();
                            }

                            if (!isMaterialCompleted && materialId > 0 &&
                                nextButton.getAttribute('href') === undefined) {
                                // Only for the last material that has a button, not an anchor
                                nextButton.addEventListener('click', function () {
                                    markComplete(materialId, 'material');
                                });
                            }
                        }
                        // For quiz content
                        else if (contentType === 'quiz' && nextButton) {
                            const quizId = parseInt("${currentQuiz != null ? currentQuiz.quizID : 0}");
                            const isQuizCompletedStr = "${currentQuiz != null && quizCompletedMap[currentQuiz.quizID] == true ? 'true' : 'false'}";
                            const isQuizCompleted = isQuizCompletedStr === "true";
                            // Enable next button only if quiz is completed with passing score
                            if (isQuizCompleted) {
                                nextButton.disabled = false;
                                nextButton.classList.remove('disabled');

                                // Also unlock the next item
                                unlockNextItem();
                            }
                        }

                        // Mobile sidebar toggle
                        const sidebarToggle = document.getElementById('sidebarToggle');
                        if (sidebarToggle) {
                            sidebarToggle.addEventListener('click', function () {
                                // Show the modal on mobile
                                showCourseOutlineModal();
                            });
                        }

                        // Mobile menu button
                        const mobileMenuButton = document.getElementById('mobileMenuButton');
                        if (mobileMenuButton) {
                            mobileMenuButton.addEventListener('click', function () {
                                // Show the modal on mobile
                                showCourseOutlineModal();
                            });
                        }

                        // Function to show course outline modal
                        function showCourseOutlineModal() {
                            const modal = document.getElementById('courseOutlineModal');
                            const modalBody = document.getElementById('courseOutlineModalBody');
                            const courseOutline = document.getElementById('courseOutline');

                            // Clone the course outline content to the modal
                            if (modalBody && courseOutline) {
                                // Clear previous content
                                modalBody.innerHTML = '';

                                // Clone the course outline content
                                const clonedContent = courseOutline.cloneNode(true);

                                // Make sure the cloned content is visible
                                clonedContent.style.display = 'block';
                                clonedContent.style.height = '100%';

                                // Add the cloned content to the modal body
                                modalBody.appendChild(clonedContent);

                                // Add event listeners to the cloned lesson headers
                                const clonedLessonHeaders = modalBody.querySelectorAll('.lesson-header');
                                clonedLessonHeaders.forEach(header => {
                                    header.addEventListener('click', function () {
                                        const lessonItem = this.parentElement;
                                        lessonItem.classList.toggle('expanded');
                                    });
                                });

                                // Add event listeners to the cloned content links
                                const clonedContentLinks = modalBody.querySelectorAll('.content-link:not(.locked)');
                                clonedContentLinks.forEach(link => {
                                    link.addEventListener('click', function (e) {
                                        // Show loading overlay when clicking on a lesson item
                                        showLoadingOverlay();

                                        // Extract the itemId and itemType from the URL
                                        const url = this.getAttribute('href');
                                        if (url) {
                                            const urlParts = url.split('/');
                                            const itemType = urlParts[urlParts.length - 2];
                                            const itemId = urlParts[urlParts.length - 1].split('?')[0];

                                            // Store the values in session storage to use after page load
                                            sessionStorage.setItem('lastClickedItemId', itemId);
                                            sessionStorage.setItem('lastClickedItemType', itemType);
                                        }

                                        // Close the modal
                                        modal.classList.remove('active');
                                    });
                                });

                                // Add event listener to close button in the cloned content
                                const closeButton = modalBody.querySelector('.course-outline-modal-close');
                                if (closeButton) {
                                    closeButton.addEventListener('click', function () {
                                        modal.classList.remove('active');
                                    });
                                }
                            }

                            // Show the modal
                            modal.classList.add('active');
                        }

                        // Modal close button - keep this for backward compatibility
                        const modalClose = document.getElementById('modalClose');
                        if (modalClose) {
                            modalClose.addEventListener('click', function () {
                                const modal = document.getElementById('courseOutlineModal');
                                modal.classList.remove('active');
                            });
                        }

                        // Close modal when clicking outside the content
                        const modal = document.getElementById('courseOutlineModal');
                        if (modal) {
                            modal.addEventListener('click', function (e) {
                                if (e.target === this) {
                                    modal.classList.remove('active');
                                }
                            });
                        }
                    });

                    // Function to unlock the next item in sequence
                    function unlockNextItem() {
                        // Find current item in the list
                        const currentContentItem = document.querySelector(`.content-item.active`);
                        if (currentContentItem) {
                            // Find its parent list
                            const contentList = currentContentItem.closest('.content-list');

                            // Find the next content item that might be locked
                            let nextContentItem = currentContentItem.nextElementSibling;

                            // If we're at the end of the current lesson's items
                            if (!nextContentItem) {
                                // Try to find the next lesson
                                const currentLessonItem = contentList.closest('.lesson-item');
                                if (currentLessonItem) {
                                    const nextLessonItem = currentLessonItem.nextElementSibling;
                                    if (nextLessonItem) {
                                        // Get lesson header to unlock
                                        const nextLessonHeader = nextLessonItem.querySelector('.lesson-header');
                                        nextLessonHeader.classList.remove('locked');
                                        // Get lesson status to remove lock icon
                                        const nextLessonStatus = nextLessonItem.querySelector('.lesson-status span');
                                        nextLessonStatus.innerHTML = '';
                                        // Get the first item in next lesson
                                        const nextLessonContentList = nextLessonItem.querySelector('.content-list');
                                        if (nextLessonContentList) {
                                            nextContentItem = nextLessonContentList.querySelector('.content-item');
                                        }
                                    }
                                }
                            }

                            // If we found a next item
                            if (nextContentItem) {

                                const lockedLink = nextContentItem.querySelector('.content-link.locked');
                                console.log(nextContentItem);
                                if (lockedLink) {
                                    // Get necessary data from the DOM
                                    const itemType = nextContentItem.getAttribute('data-type');
                                    const itemId = nextContentItem.getAttribute('data-id');
                                    const itemTitle = nextContentItem.querySelector('.content-title').textContent;
                                    const itemIcon = nextContentItem.querySelector('.content-icon i').className;
                                    const itemStatus = nextContentItem.querySelector('.content-status').innerHTML;

                                    // Create a new link to replace the locked span
                                    const newLink = document.createElement('a');
                                    newLink.href = `${pageContext.request.contextPath}/learning/\${itemType}/\${itemId}`;
                                    newLink.className = 'content-link';
                                    newLink.innerHTML = `
                                        <span class="content-icon">
                                            <i class="\${itemIcon}"></i>
                                        </span>
                                        <span class="content-title">\${itemTitle}</span>
                                        <span class="content-status">
                                            \${itemStatus.replace('<i class="fas fa-lock"></i>', '')}
                                        </span>
                                        `;

                                    // Replace the locked span with the new link
                                    nextContentItem.replaceChild(newLink, lockedLink);
                                }
                            }
                        }
                    }

                    // Mark content as complete
                    function markComplete(contentId, type) {
                        const lessonId = parseInt("${currentLesson != null ? currentLesson.lessonID : 0}");

                        fetch('${pageContext.request.contextPath}/api/learning/mark-complete', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json',
                            },
                            body: JSON.stringify({
                                id: contentId,
                                type: type,
                                lessonId: lessonId
                            })
                        })
                            .then(response => response.json())
                            .then(data => {
                                if (data.success) {
                                    // Update course progress
                                    const progressBarFill = document.querySelector('.progress-bar-fill');
                                    const progressPercentage = document.getElementById('progressPercentage');

                                    if (progressBarFill) {
                                        progressBarFill.style.width = data.newCompletionPercentage + '%';
                                        if (progressPercentage) {
                                            progressPercentage.innerText = data.newCompletionPercentage + '%';
                                        }
                                    }

                                    // Update lesson progress
                                    const lessonProgressElement = document.querySelector(`.lesson-item[data-id="${lessonId}"] .lesson-progress-bar-fill`);
                                    if (lessonProgressElement) {
                                        lessonProgressElement.style.width = data.lessonCompletionPercentage + '%';
                                    }

                                    // Update lesson completion text if exists
                                    const lessonPercentageElement = document.querySelector(`.lesson-item[data-id="${lessonId}"] .lesson-percentage`);
                                    if (lessonPercentageElement) {
                                        lessonPercentageElement.innerText = Math.round(data.lessonCompletionPercentage) + '%';
                                    }

                                    // For video completions, don't auto-navigate - just update UI
                                    if (type === 'video') {
                                        const nextButton = document.getElementById('nextButton');
                                        if (nextButton) {
                                            nextButton.disabled = false;
                                            nextButton.classList.remove('disabled');
                                            nextButton.innerHTML = 'Next <i class="fas fa-arrow-right"></i>';
                                        }

                                        const videoItem = document.querySelector(`.content-item.active`);
                                        if (videoItem) {
                                            videoItem.classList.add('completed');
                                            const statusIcon = videoItem.querySelector('.content-status');
                                            if (statusIcon) {
                                                statusIcon.innerHTML = '<i class="fas fa-check-circle text-success"></i>';
                                            }
                                        }
                                    }
                                    // For non-video content, proceed with navigation when Next is clicked
                                    else if (data.nextType && data.nextId && data.nextLessonId) {
                                        window.location.href = '${pageContext.request.contextPath}/learning/' +
                                            data.nextType + '/' + data.nextId + '?lessonId=' + data.nextLessonId;
                                    }

                                    // Check if the lesson is complete and update its visual status
                                    if (data.lessonComplete) {
                                        const lessonItem = document.querySelector(`.lesson-item[data-id="${lessonId}"]`);
                                        if (lessonItem) {
                                            lessonItem.classList.add('completed');
                                            const lessonStatusIcon = lessonItem.querySelector('.lesson-status span');
                                            if (lessonStatusIcon) {
                                                lessonStatusIcon.innerHTML = '<i class="fas fa-check-circle text-success"></i>';
                                            }
                                        }
                                    }
                                }
                            })
                            .catch(error => {
                                console.error('Error:', error);

                                // Re-enable the button in case of error
                                const nextButton = document.getElementById('nextButton');
                                if (nextButton) {
                                    nextButton.disabled = false;
                                    nextButton.classList.remove('disabled');
                                    nextButton.innerHTML = 'Next <i class="fas fa-arrow-right"></i>';
                                }
                            });
                    }

                    // Start quiz function
                    function startQuiz() {
                        const quizId = parseInt("${contentType == 'quiz' && currentQuiz != null ? currentQuiz.quizID : 0}");
                        if (quizId > 0) {
                            window.location.href = '${pageContext.request.contextPath}/quiz/take/' + quizId;
                        }
                    }
                </script>
            </body>

            </html>