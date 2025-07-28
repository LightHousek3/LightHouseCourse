<%-- 
    Document   : view-profile
    Created on : Jun 25, 2025, 2:27:42 PM
    Author     : NhiDTY-CE180492
--%>
<%@ page isErrorPage="true" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">

    <head>
        <title>My Profile - LightHouse Instructor</title>
        <jsp:include page="../common/head.jsp" />
        <style>
            /* Profile Page Styles - Updated for Instructor */
            .fa-user-circle {
                color: #0808f9;
            }

            .instructor-profile {
                background-color: #fff;
                border-radius: 10px;
                box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                overflow: hidden;
                margin-bottom: 2rem;
                max-width: 100%;
            }

            .profile-header {
                background-color: #f8f9fa;
                padding: 2rem;
                position: relative;
                text-align: center;
            }

            .profile-avatar {
                width: 120px;
                height: 120px;
                border-radius: 50%;
                border: 5px solid #fff;
                box-shadow: 0 0 20px rgba(0, 0, 0, 0.2);
                object-fit: cover;
                margin: 0 auto 1rem;
                display: block;
            }

            .profile-name {
                font-size: 1.8rem;
                font-weight: 700;
                margin-bottom: 0.5rem;
                color: #333;
            }

            .profile-username {
                font-size: 1.1rem;
                color: #6c757d;
                margin-bottom: 1rem;
            }

            .profile-role {
                display: flex;
                margin-bottom: 1rem;
                justify-content: center;
                align-items: center;
            }

            .profile-status {
                position: absolute;
                top: 1rem;
                right: 1rem;
            }

            .profile-body {
                padding: 2rem;
                width: 100%;
                box-sizing: border-box;
            }

            /* Updated container and content styles */
            .instructor-container {
                display: flex;
                min-height: 100vh;
                background-color: #f8f9fa;
            }

            .instructor-content {
                flex: 1;
                padding: 20px;
                margin-left: 0;
                transition: margin-left 0.3s ease;
            }

            .instructor-header {
                background: #fff;
                border-radius: 8px;
                padding: 20px;
                margin-bottom: 20px;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                display: flex;
                justify-content: space-between;
                align-items: center;
                flex-wrap: wrap;
            }

            .instructor-header h2 {
                color: #333;
                font-weight: 600;
                margin: 0;
            }

            /* Header Actions - Fixed alignment */
            .header-actions {
                display: flex;
                align-items: center;
                gap: 10px;
            }

            .header-actions .btn {
                font-weight: 500;
                padding: 8px 16px;
                border-radius: 6px;
                background: linear-gradient(60deg, var(--primary-color), var(--primary-light)) !important;
                color: var(--text-white) !important;
                border: none !important;
                white-space: nowrap;
                flex-shrink: 0;
            }

            /* Fix for sections layout */
            .profile-body .row {
                margin: 0;
                width: 100%;
                display: flex;
                flex-wrap: wrap;
            }

            .profile-body .row > [class*="col-"] {
                padding-left: 15px;
                padding-right: 15px;
                flex: 1;
                min-width: 0; /* Allow flex items to shrink */
            }

            /* Ensure 50-50 width columns on large screens */
            @media (min-width: 992px) {
                .instructor-content {
                    margin-left: 250px; /* Adjust based on sidebar width */
                }
            }

            .info-section {
                margin-bottom: 2rem;
                width: 100%;
                height: auto;
            }

            .section-title {
                font-size: 1.2rem;
                font-weight: 600;
                margin-bottom: 1.5rem;
                color: #333;
                border-bottom: 1px solid #e9ecef;
                padding-bottom: 0.5rem;
                text-align: center;
            }

            .info-list {
                list-style: none;
                padding: 0;
                margin: 0;
            }

            .info-item {
                margin-bottom: 1.2rem;
                display: flex;
                align-items: flex-start;
            }

            .info-icon {
                flex: 0 0 40px;
                font-size: 1.2rem;
                color: #6c757d;
                padding-top: 0.2rem;
            }

            .info-content {
                flex: 1;
            }

            .info-label {
                font-size: 0.9rem;
                color: #6c757d;
                margin-bottom: 0.2rem;
            }

            .info-value {
                font-size: 1rem;
                color: #333;
                font-weight: 500;
                word-break: break-word;
            }

            .activity-item {
                display: flex;
                margin-bottom: 1.5rem;
            }

            .activity-icon {
                flex: 0 0 40px;
                height: 40px;
                border-radius: 50%;
                background-color: #e9ecef;
                display: flex;
                align-items: center;
                justify-content: center;
                color: #6c757d;
                margin-right: 1rem;
            }

            .activity-content {
                flex: 1;
            }

            .activity-title {
                font-weight: 600;
                margin-bottom: 0.3rem;
                color: #333;
            }

            .activity-time {
                font-size: 0.8rem;
                color: #6c757d;
            }

            .activity-details {
                font-size: 0.9rem;
                color: #6c757d;
                margin-top: 0.5rem;
            }

            .btn-icon {
                width: 40px;
                height: 40px;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                padding: 0;
                margin: 0 0.3rem;
            }

            /* Breadcrumbs */
            .breadcrumb-nav {
                background: none;
                padding: 0;
                margin: 8px 0 0 0;
                font-size: 14px;
            }

            .breadcrumb-nav .breadcrumb {
                margin: 0;
                padding: 0;
            }

            .breadcrumb-nav .breadcrumb-item a {
                color: #6c757d;
                text-decoration: none;
            }

            .breadcrumb-nav .breadcrumb-item a:hover {
                color: #007bff;
            }

            .breadcrumb-nav .breadcrumb-item.active {
                color: #495057;
            }

            /* Toggle sidebar button styles */
            #toggleSidebarBtn {
                background: none;
                border: none;
                font-size: 1.2rem;
                color: #333;
                padding: 8px;
            }

            #toggleSidebarBtn:hover {
                background-color: #f8f9fa;
                border-radius: 4px;
            }

            /* Large screens - Fixed layout issues */
            @media (min-width: 1200px) {
                .instructor-content {
                    margin-left: 260px; /* Adjust for larger sidebar */
                }

                .profile-body {
                    padding: 2.5rem;
                }

                .info-section {
                    padding: 0 1rem;
                    height: auto;
                }

                .profile-body .row > [class*="col-"] {
                    padding-left: 20px;
                    padding-right: 20px;
                }

                .info-item {
                    margin-bottom: 1.5rem;
                    min-height: 50px;
                }

                .info-value {
                    font-size: 1.1rem;
                    line-height: 1.5;
                }
            }

            /* Extra large screens */
            @media (min-width: 1400px) {
                .instructor-content {
                    margin-left: 278px; /* Match your original margin */
                }

                .instructor-profile {
                    max-width: 1238px;
                    margin: 0 auto 2rem;
                }

                .profile-body {
                    padding: 3rem;
                }

                .info-section {
                    padding: 0 1.5rem;
                    height: auto;
                }
            }

            /* Medium screens - Ensure proper column layout */
            @media (min-width: 992px) and (max-width: 1199.98px) {
                .instructor-content {
                    margin-left: 240px;
                }

                .profile-body {
                    padding: 2rem 1.5rem;
                }

                .info-section {
                    height: auto;
                }

                .profile-body .row > .col-lg-6:first-child {
                    flex: 0 0 50%;
                    max-width: 50%;
                }

                .profile-body .row > .col-lg-6:last-child {
                    flex: 0 0 50%;
                    max-width: 50%;
                }
            }

            /* Small to medium screens - Stack vertically */
            @media (min-width: 768px) and (max-width: 991.98px) {
                .instructor-content {
                    margin-left: 0;
                    padding: 15px;
                }

                .profile-body .row > [class*="col-"] {
                    margin-bottom: 2rem;
                    flex: 0 0 100%;
                    max-width: 100%;
                }

                .info-section {
                    height: auto;
                }
            }

            /* Mobile screens */
            @media (max-width: 767.98px) {
                .instructor-content {
                    margin-left: 0;
                    padding: 10px;
                }

                .instructor-header {
                    padding: 15px;
                    margin-bottom: 15px;
                    flex-direction: column;
                    gap: 15px;
                }

                .header-actions {
                    gap: 8px;
                    flex-wrap: wrap;
                    justify-content: center;
                }

                .header-actions .btn {
                    font-size: 14px;
                    padding: 6px 12px;
                }

                .profile-header {
                    padding: 1.5rem;
                }

                .profile-avatar {
                    width: 100px;
                    height: 100px;
                }

                .profile-name {
                    font-size: 1.5rem;
                }

                .profile-status {
                    position: static;
                    margin-top: 1rem;
                    text-align: center;
                }

                .profile-body {
                    padding: 1.5rem;
                }

                .profile-body .row > [class*="col-"] {
                    margin-bottom: 1.5rem;
                    padding-left: 0;
                    padding-right: 0;
                    flex: 0 0 100%;
                    max-width: 100%;
                }

                .info-section {
                    height: auto;
                    padding: 0;
                }

                .info-item {
                    min-height: 50px;
                }
            }
        </style>
    </head>

    <body>
        <!-- Include Instructor Sidebar -->
        <c:set var="activeMenu" value="profile" scope="request" />
        <jsp:include page="../common/sidebar.jsp" />
        <div class="instructor-container">
            <!-- Instructor Content -->
            <div class="instructor-content">
                <div class="container-fluid">
                    <!-- Header -->
                    <div class="instructor-header d-flex justify-content-between align-items-center">
                        <button class="btn d-lg-none" id="toggleSidebarBtn">
                            <i class="fas fa-bars"></i>
                        </button>

                        <h2 class="m-0 d-none d-lg-block"><i class="fas fa-user-circle"></i> My Profile</h2>
                        <div class="header-actions">
                            <a href="${pageContext.request.contextPath}/instructor/profile/change"
                               class="btn btn-lg btn-success">
                                <i class="fas fa-edit me-2"></i> Change Password
                            </a>

                            <a href="${pageContext.request.contextPath}/instructor/profile/edit"
                               class="btn btn-lg btn-success">
                                <i class="fas fa-edit me-2"></i> Edit Profile
                            </a>
                        </div>
                    </div>

                    <c:choose>
                        <c:when test="${empty instructor}">
                            <div class="alert alert-warning" role="alert">
                                <i class="fas fa-exclamation-triangle me-2"></i> Profile information not found!
                            </div>
                        </c:when>
                        <c:otherwise>
                            <!-- Instructor Profile Card -->
                            <div class="instructor-profile">
                                <div class="profile-header">
                                    <c:choose>
                                        <c:when test="${not empty avatar}">
                                            <img src="${pageContext.request.contextPath}${avatar}" alt="${instructor.fullName}" class="profile-avatar">
                                        </c:when>
                                        <c:otherwise>
                                            <img src="${pageContext.request.contextPath}/assets/imgs/avatars/default-user.png" alt="${instructor.fullName}" class="profile-avatar">
                                        </c:otherwise>
                                    </c:choose>
                                    <h3 class="profile-name">${instructor.fullName}</h3>
                                    <p class="profile-username">
                                        ${instructor.fullName}
                                    </p>
                                    <c:choose>
                                        <c:when test="${not empty instructor.username}">
                                            @${instructor.username}
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted">@username</span>
                                        </c:otherwise>
                                    </c:choose>
                                    </p>
                                    <div class="profile-role">
                                        <span class="status-badge status-completed">Instructor</span>
                                    </div>
                                </div>
                                <div class="profile-body">
                                    <div class="row">
                                        <div class="col-lg-6">
                                            <!-- Basic Information Section -->
                                            <div class="info-section">
                                                <h4 class="section-title">
                                                    <i class="fas fa-info-circle me-2"></i> Basic Information
                                                </h4>
                                                <ul class="info-list">
                                                    <li class="info-item">
                                                        <div class="info-icon">
                                                            <i class="fas fa-fingerprint"></i>
                                                        </div>
                                                        <div class="info-content">
                                                            <div class="info-label">Instructor ID</div>
                                                            <div class="info-value">#${instructor.instructorID}</div>
                                                        </div>
                                                    </li>
                                                    <li class="info-item">
                                                        <div class="info-icon">
                                                            <i class="fas fa-user"></i>
                                                        </div>
                                                        <div class="info-content">
                                                            <div class="info-label">Username</div>
                                                            <div class="info-value">
                                                                <c:choose>
                                                                    <c:when test="${not empty instructor.username}">
                                                                        ${instructor.username}
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span class="text-muted">Not provided</span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </div>
                                                        </div>
                                                    </li>
                                                    <!-- Full Name -->
                                                    <li class="info-item">
                                                        <div class="info-icon">
                                                            <i class="fas fa-id-card"></i>
                                                        </div>
                                                        <div class="info-content">
                                                            <div class="info-label">Full Name</div>
                                                            <div class="info-value">
                                                                <c:choose>
                                                                    <c:when test="${not empty instructor.fullName}">
                                                                        ${instructor.fullName}
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span class="text-muted">Not provided</span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </div>
                                                        </div>
                                                    </li>

                                                    <li class="info-item">
                                                        <div class="info-icon">
                                                            <i class="fas fa-envelope"></i>
                                                        </div>
                                                        <div class="info-content">
                                                            <div class="info-label">Email</div>
                                                            <div class="info-value">
                                                                <c:choose>
                                                                    <c:when test="${not empty instructor.email}">
                                                                        ${instructor.email}
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span class="text-muted">Not provided</span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </div>
                                                        </div>
                                                    </li>
                                                    <li class="info-item">
                                                        <div class="info-icon">
                                                            <i class="fas fa-phone"></i>
                                                        </div>
                                                        <div class="info-content">
                                                            <div class="info-label">Phone</div>
                                                            <div class="info-value">
                                                                <c:choose>
                                                                    <c:when test="${not empty instructor.phone}">
                                                                        ${instructor.phone}
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        Not provided
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </div>
                                                        </div>
                                                    </li>
                                                    <li class="info-item">
                                                        <div class="info-icon">
                                                            <i class="fas fa-map-marker-alt"></i>
                                                        </div>
                                                        <div class="info-content">
                                                            <div class="info-label">Address</div>
                                                            <div class="info-value">
                                                                <c:choose>
                                                                    <c:when test="${not empty instructor.address}">
                                                                        ${instructor.address}
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        Not provided
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </div>
                                                        </div>
                                                    </li>
                                                    </li><!-- Biography -->
                                                    <li class="info-item">
                                                        <div class="info-icon">
                                                            <i class="fas fa-book"></i>
                                                        </div>
                                                        <div class="info-content">
                                                            <div class="info-label">Biography</div>
                                                            <div class="info-value">
                                                                <c:choose>
                                                                    <c:when test="${not empty instructor.biography}">
                                                                        ${instructor.biography}
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span class="text-muted">Not provided</span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </div>
                                                        </div>
                                                    </li>

                                                    <!-- Specialization -->
                                                    <li class="info-item">
                                                        <div class="info-icon">
                                                            <i class="fas fa-cogs"></i>
                                                        </div>
                                                        <div class="info-content">
                                                            <div class="info-label">Specialization</div>
                                                            <div class="info-value">
                                                                <c:choose>
                                                                    <c:when test="${not empty instructor.specialization}">
                                                                        ${instructor.specialization}
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span class="text-muted">Not provided</span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </div>
                                                        </div>

                                                    <li class="info-item">
                                                        <div class="info-icon">
                                                            <i class="fas fa-toggle-on"></i>
                                                        </div>
                                                        <div class="info-content">
                                                            <div class="info-label">Status</div>
                                                            <div class="info-value">
                                                                <c:choose>
                                                                    <c:when test="${selectedInstructor.isActive}">
                                                                        <span
                                                                            class="status-badge status-active">Active</span>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span
                                                                            class="status-badge status-inactive">Inactive</span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </div>
                                                        </div>
                                                    </li>
                                                </ul>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
        <jsp:include page="../common/scripts.jsp" />
    </body>

</html>