<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">

    <head>
        <title>Manage User - LightHouse Admin</title>
        <jsp:include page="../common/head.jsp" />
        <style>
            /* Reset và Base Styles */
            * {
                box-sizing: border-box;
            }

            body {
                font-family: 'Poppins', sans-serif;
                background-color: #f8f9fa;
                margin: 0;
                padding: 0;
            }

            /* Main Content Area */
            .main-content {
                margin-left: 250px; /* Điều chỉnh theo width của sidebar */
                min-height: 100vh;
                padding: 20px;
                transition: margin-left 0.3s ease;
            }

            /* Page Header */
            .page-header {
                background: white;
                border-radius: 12px;
                padding: 25px;
                margin-bottom: 25px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.08);
                border: 1px solid #e9ecef;
            }

            .page-title {
                display: flex;
                align-items: center;
                gap: 12px;
                margin: 0;
                color: #2c3e50;
                font-weight: 600;
                font-size: 28px;
            }

            .page-title i {
                color: #007bff;
                font-size: 32px;
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

            .btn-back {
                background: #6c757d;
                border: none;
                padding: 10px 20px;
                border-radius: 8px;
                color: white;
                font-weight: 500;
                transition: all 0.3s;
                text-decoration: none;
                display: inline-flex;
                align-items: center;
            }

            .btn-back:hover {
                background: #545b62;
                transform: translateY(-1px);
                color: white;
                text-decoration: none;
            }

            /* Content Card */
            .content-card {
                background: white;
                border-radius: 12px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.08);
                border: 1px solid #e9ecef;
                overflow: hidden;
            }

            .card-header {
                padding: 20px 25px;
                border-bottom: 1px solid #e9ecef;
                background: #ffffff;
            }

            .card-title {
                margin: 0;
                color: #495057;
                font-weight: 600;
                font-size: 18px;
                display: flex;
                align-items: center;
                gap: 8px;
            }

            .card-body {
                padding: 30px;
            }

            /* Form Sections */
            .form-section {
                margin-bottom: 30px;
            }

            .section-title {
                color: #495057;
                font-weight: 600;
                font-size: 16px;
                margin-bottom: 15px;
                padding-bottom: 8px;
                border-bottom: 1px solid #e9ecef;
                display: flex;
                align-items: center;
                gap: 8px;
            }

            .form-label {
                font-weight: 600;
                color: #495057;
                margin-bottom: 8px;
                font-size: 14px;
            }

            .form-label .required {
                color: #dc3545;
            }

            .form-control {
                border: 1px solid #e9ecef;
                border-radius: 8px;
                padding: 12px 15px;
                font-size: 14px;
                transition: all 0.3s;
            }

            .form-control:focus {
                border-color: #007bff;
                box-shadow: 0 0 0 0.2rem rgba(0,123,255,0.25);
            }

            .input-group-text {
                background: #f8f9fa;
                border: 1px solid #e9ecef;
                color: #6c757d;
            }

            /* Avatar Section */
            .avatar-section {
                text-align: center;
                padding: 20px;
                border: 2px dashed #e9ecef;
                border-radius: 12px;
                background: #fafbfc;
                transition: all 0.3s;
            }

            .avatar-section:hover {
                border-color: #007bff;
                background: rgba(0,123,255,0.05);
            }

            .avatar-preview {
                width: 100px;
                height: 100px;
                border-radius: 50%;
                object-fit: cover;
                border: 3px solid #007bff;
                margin-bottom: 15px;
            }

            .avatar-placeholder {
                width: 100px;
                height: 100px;
                border-radius: 50%;
                background: #e9ecef;
                display: flex;
                align-items: center;
                justify-content: center;
                margin: 0 auto 15px;
                color: #6c757d;
                font-size: 40px;
            }

            .file-input {
                display: none;
            }

            .file-label {
                background: #007bff;
                color: white;
                padding: 8px 20px;
                border-radius: 6px;
                cursor: pointer;
                transition: all 0.3s;
                display: inline-block;
                font-size: 14px;
            }

            .file-label:hover {
                background: #0056b3;
            }

            .form-text {
                color: #6c757d;
                font-size: 12px;
                margin-top: 5px;
            }

            /* Password Toggle */
            .password-wrapper {
                position: relative;
            }

            .password-toggle {
                position: absolute;
                right: 10px;
                top: 50%;
                transform: translateY(-50%);
                background: none;
                border: none;
                color: #6c757d;
                cursor: pointer;
                z-index: 10;
            }

            /* Form Actions */
            .form-actions {
                padding: 20px 30px;
                background: #fafbfc;
                border-top: 1px solid #e9ecef;
                display: flex;
                justify-content: flex-end;
                gap: 15px;
            }

            .btn-primary {
                background: #007bff;
                border: none;
                padding: 12px 30px;
                border-radius: 8px;
                font-weight: 500;
                transition: all 0.3s;
            }

            .btn-primary:hover {
                background: #0056b3;
                transform: translateY(-1px);
                box-shadow: 0 4px 12px rgba(0,123,255,0.3);
            }

            .btn-secondary {
                background: #6c757d;
                border: none;
                padding: 12px 30px;
                border-radius: 8px;
                font-weight: 500;
                transition: all 0.3s;
            }

            .btn-secondary:hover {
                background: #545b62;
                transform: translateY(-1px);
            }

            /* Alert */
            .alert {
                border: none;
                border-radius: 8px;
                padding: 15px 20px;
                margin-bottom: 20px;
            }

            .alert-success {
                background: #d4edda;
                color: #155724;
                border-left: 4px solid #28a745;
            }

            .badge{
                padding: 10px 20px;
                border-radius: 20px
            }

            /* Responsive */
            @media (max-width: 768px) {
                .main-content {
                    margin-left: 0;
                    padding: 15px;
                }

                .page-header {
                    padding: 20px;
                }

                .card-body {
                    padding: 20px;
                }

                .form-actions {
                    flex-direction: column;
                    padding: 20px;
                }

                .header-actions {
                    margin-top: 15px;
                }
            }
            @media (max-width: 991.98px) {
                .main-content {
                    margin-left: 0; /* Bỏ margin khi thu nhỏ */
                }
            }
        </style>
    </head>

    <body>
        <!-- Include Admin Sidebar -->
        <c:set var="activeMenu" value="users" scope="request" />
        <jsp:include page="../common/sidebar.jsp" />

        <!-- Main Content Area -->
        <div class="main-content">

            <!-- Page Header -->
            <div class="page-header admin-header d-flex justify-content-between align-items-center">
                <button class="btn d-lg-none" id="toggleSidebarBtn">
                    <i class="fas fa-bars"></i>
                </button>
                <div class="m-0 d-none d-lg-block">
                    <h1 class="page-title">
                        <i class="bi bi-person-circle"></i>
                        User Detail
                    </h1>
                    <nav class="breadcrumb-nav">
                        <ol class="breadcrumb">
                            <li class="breadcrumb-item">
                                <a href="${pageContext.request.contextPath}/admin/users">User Management</a>
                            </li>
                            <li class="breadcrumb-item active">User Detail</li>
                        </ol>
                    </nav>
                </div>
                <div class="d-flex align-items-center">
                    <div class="header-actions">
                        <a href="${pageContext.request.contextPath}/admin/users" class="btn-back">
                            <i class="bi bi-arrow-left me-2"></i>Back to Users
                        </a>
                    </div>
                </div>
            </div>

            <!-- User Info -->
            <div class="content-card">
                <div class="card-header">
                    <h2 class="card-title">
                        <i class="bi bi-person-circle"></i>
                        User Information
                    </h2>
                </div>

                <div id="userDetail" class="card-body">
                    <!-- Basic Information -->
                    <c:choose>
                        <c:when test="${not empty selectedUser}">
                            <div class="form-section">
                                <div class="row g-3">
                                    <!-- Avatar Display -->
                                    <div class="form-section mt-4">
                                        <div class="avatar-section">
                                            <c:choose>
                                                <c:when test="${not empty avatarUrl}">
                                                    <img src="${selectedUser.avatarUrl}" class="avatar-preview" alt="Avatar Preview">
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="avatar-placeholder">
                                                        <i class="bi bi-person-circle"></i>
                                                    </div>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>

                                    <!-- Username -->
                                    <div class="col-md-6">
                                        <label class="form-label">Username</label>
                                        <div class="border rounded p-2 bg-light-subtle">
                                            <p class="form-control-plaintext mb-0 ${empty selectedUser.username ? 'text-muted' : ''}">
                                                ${empty selectedUser.username ? 'Not found' : selectedUser.username}
                                            </p>
                                        </div>
                                    </div>

                                    <!-- Full Name -->
                                    <div class="col-md-6">
                                        <label class="form-label">Full Name</label>
                                        <div class="border rounded p-2 bg-light-subtle">
                                            <p class="form-control-plaintext mb-0 ${empty selectedUser.fullName ? 'text-muted' : ''}">
                                                ${empty selectedUser.fullName ? 'Not found' : selectedUser.fullName}
                                            </p>
                                        </div>
                                    </div>

                                    <!-- Email -->
                                    <div class="col-md-6">
                                        <label class="form-label">Email</label>
                                        <div class="border rounded p-2 bg-light-subtle">
                                            <p class="form-control-plaintext mb-0 ${empty selectedUser.email ? 'text-muted' : ''}">
                                                ${empty selectedUser.email ? 'Not found' : selectedUser.email}
                                            </p>
                                        </div>
                                    </div>

                                    <!-- Phone -->
                                    <div class="col-md-6">
                                        <label class="form-label">Phone Number</label>
                                        <div class="border rounded p-2 bg-light-subtle">
                                            <p class="form-control-plaintext mb-0 ${empty selectedUser.phone ? 'text-muted' : ''}">
                                                ${empty selectedUser.phone ? 'Not found' : selectedUser.phone}
                                            </p>
                                        </div>
                                    </div>

                                    <!-- Password -->
                                    <div class="col-md-6">
                                        <label class="form-label">Password</label>
                                        <div class="border rounded p-2 bg-light-subtle">
                                            <p class="form-control-plaintext mb-0 text-muted">********</p>
                                        </div>
                                    </div>

                                    <!-- Role -->
                                    <div class="col-md-6">
                                        <label class="form-label d-block">Role</label>
                                        <div class="d-flex align-items-center" style="height: 55px;">
                                            <c:choose>
                                                <c:when test="${selectedUser.role == 'admin'}">
                                                    <span class="badge bg-danger m-0">Admin</span>
                                                </c:when>
                                                <c:when test="${selectedUser.role == 'instructor'}">
                                                    <span class="badge bg-primary m-0">Instructor</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-secondary m-0">
                                                        ${empty selectedUser.role ? 'Not found' : selectedUser.role}
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>

                                    <!-- Address -->
                                    <div class="col-12">
                                        <label class="form-label">Address</label>
                                        <div class="border rounded p-2 bg-light-subtle">
                                            <p class="form-control-plaintext mb-0 ${empty selectedUser.address ? 'text-muted' : ''}">
                                                ${empty selectedUser.address ? 'Not found' : selectedUser.address}
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="alert alert-warning text-center">
                                <strong>User not found</strong>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

        </div>
    </div>

    <jsp:include page="../common/scripts.jsp" />
    <script>
        // Password toggle
        document.getElementById('togglePassword').addEventListener('click', function () {
            const passwordInput = document.getElementById('password');
            const icon = this.querySelector('i');

            if (passwordInput.type === 'password') {
                passwordInput.type = 'text';
                icon.classList.remove('bi-eye');
                icon.classList.add('bi-eye-slash');
            } else {
                passwordInput.type = 'password';
                icon.classList.remove('bi-eye-slash');
                icon.classList.add('bi-eye');
            }
        });

    </script>
</body>
</html>