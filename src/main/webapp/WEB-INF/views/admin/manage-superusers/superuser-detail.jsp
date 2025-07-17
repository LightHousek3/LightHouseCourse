<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">

    <head>
        <title>Super User Details - LightHouse Admin</title>
        <jsp:include page="../common/head.jsp" />
        <style>
            .fa-user-circle {
                color: #0808f9;
            }

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

            .header-actions {
                display: flex;
                gap: 10px;
                align-items: center;
            }

            /* User Profile */
            .user-profile {
                padding: 30px;
                background-color: #fff;
                border-radius: 10px;
                box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
            }

            .profile-header {
                display: flex;
                align-items: center;
                margin-bottom: 20px;
                padding-bottom: 20px;
                border-bottom: 1px solid #e9ecef;
            }

            .avatar-container {
                margin-right: 30px;
            }

            .avatar {
                width: 150px;
                height: 150px;
                border-radius: 50%;
                object-fit: cover;
                border: 5px solid #f8f9fa;
                box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
            }

            .avatar-placeholder {
                display: flex;
                align-items: center;
                justify-content: center;
                width: 150px;
                height: 150px;
                background-color: #e9ecef;
                border-radius: 50%;
                color: #adb5bd;
                font-size: 64px;
                border: 5px solid #f8f9fa;
                box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
            }

            .user-info h2 {
                margin-bottom: 10px;
                font-size: 28px;
                color: #343a40;
            }

            .profile-section {
                margin-bottom: 30px;
            }

            .profile-section h3 {
                font-size: 18px;
                color: #495057;
                margin-bottom: 15px;
                padding-bottom: 10px;
                border-bottom: 1px solid #e9ecef;
                display: flex;
                align-items: center;
                gap: 8px;
            }

            .info-list {
                list-style: none;
                padding: 0;
                margin: 0;
            }

            .info-list li {
                margin-bottom: 15px;
                display: flex;
                align-items: center;
            }

            .info-list li i {
                width: 24px;
                margin-right: 15px;
                color: #6c757d;
                text-align: center;
            }

            .info-list .info-label {
                font-weight: 600;
                color: #495057;
                width: 120px;
                flex-shrink: 0;
            }

            .info-list .info-value {
                color: #212529;
            }

            .btn-toggle-status {
                padding: 10px 20px;
                border-radius: 8px;
                font-weight: 500;
                border: none;
            }

            .btn-toggle-status.activate {
                background-color: #28a745;
                color: #fff;
            }

            .btn-toggle-status.deactivate {
                background-color: #ffc107;
                color: #212529;
            }

            .btn-toggle-status:hover {
                opacity: 0.9;
            }

            .modal-confirm .modal-content {
                border-radius: 10px;
            }

            .modal-confirm .modal-header {
                border-bottom: none;
                position: relative;
                padding: 25px 15px 10px;
            }

            .modal-confirm .modal-footer {
                border-top: none;
                padding: 15px 25px 25px;
            }

            .no-user-message {
                text-align: center;
                padding: 40px;
                background-color: #f8f9fa;
                border-radius: 8px;
            }

            .no-user-icon {
                font-size: 48px;
                color: #6c757d;
                margin-bottom: 20px;
            }
        </style>
    </head>

    <body>
        <!-- Include Admin Sidebar -->
        <c:set var="activeMenu" value="superusers" scope="request" />
        <jsp:include page="../common/sidebar.jsp" />

        <div class="admin-container">
            <!-- Admin Content -->
            <div class="admin-content">
                <div class="container-fluid">
                    <!-- Header -->
                    <div class="admin-header d-flex justify-content-between align-items-center">
                        <button class="btn d-lg-none" id="toggleSidebarBtn">
                            <i class="fas fa-bars"></i>
                        </button>
                        <div class="d-none d-lg-block">
                            <h2 class="m-0"><i class="fas fa-user-circle"></i> Super User Details</h2>
                            <div>
                                <nav aria-label="breadcrumb" class="breadcrumb-nav">
                                    <ol class="breadcrumb">
                                        <li class="breadcrumb-item"><a
                                                href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a>
                                        </li>
                                        <li class="breadcrumb-item"><a
                                                href="${pageContext.request.contextPath}/admin/superusers">Super
                                                Users</a>
                                        </li>
                                        <li class="breadcrumb-item active" aria-current="page">User Details</li>
                                    </ol>
                                </nav>
                            </div>
                        </div>
                        <div class="header-actions">
                            <a href="${pageContext.request.contextPath}/admin/superusers"
                               class="btn btn-lg btn-primary">
                                <i class="fas fa-arrow-left me-2"></i> Back
                            </a>
                        </div>
                    </div>

                    <!-- User Profile Section -->
                    <c:choose>
                        <c:when test="${empty selectedUser}">
                            <div class="no-user-message">
                                <div class="no-user-icon">
                                    <i class="fas fa-user-slash"></i>
                                </div>
                                <h3>User Not Found</h3>
                                <p>The requested user does not exist or has been deleted.</p>
                                <a href="${pageContext.request.contextPath}/admin/superusers"
                                   class="btn btn-primary">
                                    <i class="fas fa-arrow-left me-2"></i> Back to Super Users
                                </a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="user-profile">
                                <div class="profile-header">
                                    <div class="avatar-container">
                                        <c:choose>
                                            <c:when test="${not empty selectedUser.avatar}">
                                                <c:choose>
                                                    <c:when test="${fn:startsWith(selectedUser.avatar, '/assets')}">
                                                        <img src="${pageContext.request.contextPath}${selectedUser.avatar}"
                                                             alt="Customer Avatar" class="avatar"
                                                             id="avatarPreview" referrerpolicy="no-referrer">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <img src="${selectedUser.avatar}"
                                                             alt="Customer Avatar" class="avatar"
                                                             id="avatarPreview" referrerpolicy="no-referrer">
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="avatar-placeholder"
                                                     id="avatarPlaceholder">
                                                    <i class="fas fa-user"></i>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="user-info">
                                        <h2>${selectedUser.fullName}</h2>
                                        <div class="d-flex gap-2">
                                            <c:choose>
                                                <c:when test="${selectedUser.role eq 'admin'}">
                                                    <span class="status-badge status-admin">
                                                        Admin
                                                    </span>
                                                </c:when>
                                                <c:when test="${selectedUser.role eq 'instructor'}">
                                                    <span class="status-badge status-instructor">
                                                        Instructor
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="status-badge">${selectedUser.role}</span>
                                                </c:otherwise>
                                            </c:choose>
                                            <c:choose>
                                                <c:when test="${selectedUser.isActive}">
                                                    <span class="status-badge status-active">
                                                        Active
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="status-badge status-inactive">
                                                        Inactive
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <p class="text-muted mt-2">
                                            <strong>ID:</strong> ${selectedUser.superUserID}
                                        </p>
                                    </div>
                                </div>

                                <!-- Account Information -->
                                <div class="profile-section">
                                    <h3><i class="fas fa-user-shield me-2"></i>Account Information</h3>
                                    <ul class="info-list">
                                        <li>
                                            <i class="fas fa-user"></i>
                                            <span class="info-label">Username:</span>
                                            <span class="info-value">${selectedUser.username}</span>
                                        </li>
                                        <li>
                                            <i class="fas fa-envelope"></i>
                                            <span class="info-label">Email:</span>
                                            <span class="info-value">${selectedUser.email}</span>
                                        </li>
                                    </ul>
                                </div>

                                <!-- Contact Information -->
                                <div class="profile-section">
                                    <h3><i class="fas fa-address-card me-2"></i>Contact Information</h3>
                                    <ul class="info-list">
                                        <li>
                                            <i class="fas fa-phone"></i>
                                            <span class="info-label">Phone:</span>
                                            <span class="info-value">${not empty selectedUser.phone ?
                                                                       selectedUser.phone : 'Not provided'}</span>
                                        </li>
                                        <li>
                                            <i class="fas fa-map-marker-alt"></i>
                                            <span class="info-label">Address:</span>
                                            <span class="info-value">${not empty selectedUser.address ?
                                                                       selectedUser.address : 'Not provided'}</span>
                                        </li>
                                    </ul>
                                </div>

                                <!-- Instructor Information (only shown for instructors) -->
                                <c:if test="${selectedUser.role eq 'instructor'}">
                                    <div class="profile-section">
                                        <h3><i class="fas fa-chalkboard-teacher me-2"></i>Instructor Information
                                        </h3>
                                        <ul class="info-list">
                                            <li>
                                                <i class="fas fa-certificate"></i>
                                                <span class="info-label">Specialization:</span>
                                                <span class="info-value">${not empty specialization ?
                                                                           specialization : 'Not specified'}</span>
                                            </li>
                                            <li>
                                                <i class="fas fa-book-open"></i>
                                                <span class="info-label">Biography:</span>
                                                <span class="info-value">
                                                    ${not empty biography ? biography :
                                                      'No biography available'}
                                                </span>
                                            </li>
                                        </ul>
                                    </div>
                                </c:if>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <jsp:include page="../common/scripts.jsp" />
    </body>

</html>