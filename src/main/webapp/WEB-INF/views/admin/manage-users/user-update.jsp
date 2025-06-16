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

            .invalid-feedback{
                display: block;
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
        <div class="main-content ">
            <!-- Page Header -->
            <div class="page-header admin-header d-flex justify-content-between align-items-center">
                <button class="btn d-lg-none" id="toggleSidebarBtn">
                    <i class="fas fa-bars"></i>
                </button>
                <div class="m-0 d-none d-lg-block">
                    <h1 class="page-title">
                        <i class="fas fa-edit"></i>
                        Update User
                    </h1>
                    <nav class="breadcrumb-nav">
                        <ol class="breadcrumb">
                            <li class="breadcrumb-item">
                                <a href="${pageContext.request.contextPath}/admin/users">User Management</a>
                            </li>
                            <li class="breadcrumb-item active">Update User</li>
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

            <!-- Add User Form -->
            <div class="content-card">
                <div class="card-header">
                    <h2 class="card-title">
                        <i class="bi bi-person-circle"></i>
                        User Information
                    </h2>
                </div>
                <c:choose>
                    <c:when test="${not empty selectedUser}">
                        <form id="updateUser" method="post" action="${pageContext.request.contextPath}/admin/users" autocomplete="off" novalidate>
                            <input type="hidden" name="action" value="updateUser">
                            <c:if test="${selectedUser != null}">
                                <input type="hidden" name="userId" value="${selectedUser.userID}">
                            </c:if>
                            <!--<input type="hidden" name="userId" value="${selectedUser.userID}" />-->

                            <div class="card-body">
                                <div class="form-section">
                                    <div class="row g-3">

                                        <!-- Username -->
                                        <div class="col-md-6">
                                            <label for="username" class="form-label">Username <span class="required">*</span></label>
                                            <input type="text" class="form-control ${not empty usernameError ? 'is-invalid' : ''}" id="username" name="username"
                                                   value="${selectedUser.username}" required>
                                            <c:if test="${not empty usernameError}">
                                                <div class="invalid-feedback">${usernameError}</div>
                                            </c:if>
                                        </div>

                                        <!-- Full Name -->
                                        <div class="col-md-6">
                                            <label for="fullName" class="form-label">Full Name <span class="required">*</span></label>
                                            <input type="text" class="form-control ${not empty fullnameError ? 'is-invalid' : ''}" 
                                                   id="fullName" name="fullName" placeholder="Enter Full Name" 
                                                   value="${selectedUser.fullName}" required>
                                            <c:if test="${not empty fullnameError}">
                                                <div class="invalid-feedback">${fullnameError}</div>
                                            </c:if>
                                        </div>

                                        <!-- Email -->
                                        <div class="col-md-6">
                                            <label for="email" class="form-label">Email <span class="required">*</span></label>
                                            <input type="email" class="form-control ${not empty emailError ? 'is-invalid' : ''}" 
                                                   id="email" name="email" value="${selectedUser.email}" required>
                                            <c:if test="${not empty emailError}">
                                                <div class="invalid-feedback">${emailError}</div>
                                            </c:if>
                                        </div>

                                        <!-- Phone -->
                                        <div class="col-md-6">
                                            <label for="phone" class="form-label">Phone Number</label>
                                            <input type="tel" class="form-control  ${not empty phoneError ? 'is-invalid' : ''}" id="phone" name="phone"
                                                   value="${selectedUser.phone}">
                                            <c:if test="${not empty phoneError}">
                                                <div class="invalid-feedback">${phoneError}</div>
                                            </c:if>
                                        </div>

                                        <!-- Password -->
                                        <div class="col-md-6">
                                            <label for="newPassword" class="form-label">New Password</label>
                                            <div class="password-wrapper">
                                                <input type="password"
                                                       class="form-control ${not empty passwordError ? 'is-invalid' : ''}"
                                                       id="newPassword"
                                                       name="newPassword"
                                                       placeholder="Leave blank to keep current password"
                                                       autocomplete="new-password">
                                                <button type="button" class="password-toggle" id="togglePassword">
                                                    <i class="bi bi-eye"></i>
                                                </button>
                                            </div>
                                            <c:if test="${not empty passwordError}">
                                                <p class="invalid-feedback">${passwordError}</p>
                                            </c:if>
                                        </div>

                                        <!-- Role -->
                                        <div class="col-md-6">
                                            <label for="role" class="form-label">Role <span class="required">*</span></label>
                                            <select class="form-control" id="role" name="role" required>
                                                <option value="admin" ${selectedUser.role == 'admin' ? 'selected' : ''}>Admin</option>
                                                <option value="instructor" ${selectedUser.role == 'instructor' ? 'selected' : ''}>Instructor</option>
                                                <option value="user" ${selectedUser.role == 'user' ? 'selected' : ''}>User</option>
                                            </select>
                                        </div>

                                        <!-- Address -->
                                        <div class="col-12">
                                            <label for="address" class="form-label">Address</label>
                                            <textarea class="form-control" id="address" name="address" rows="3"
                                                      placeholder="Enter Address">${selectedUser.address}</textarea>
                                        </div>
                                    </div>
                                </div>

                                <!-- Avatar Upload -->
                                <div class="form-section mt-4">
                                    <h3 class="section-title"><i class="bi bi-camera"></i> Profile Avatar</h3>
                                    <div class="avatar-section">
                                        <div class="avatar-placeholder" id="avatarPlaceholder">
                                            <i class="bi bi-person-circle"></i>
                                        </div>
                                        <img id="avatarPreview" class="avatar-preview d-none" alt="Avatar Preview">
                                        <div>
                                            <input type="file" class="file-input" id="avatar" name="avatar" accept="image/*">
                                            <label for="avatar" class="file-label">
                                                <i class="bi bi-cloud-upload me-2"></i>
                                                Choose Avatar
                                            </label>
                                        </div>
                                        <div class="form-text mt-2">
                                            Supported formats: JPG, PNG, GIF (Max size: 5MB)
                                        </div>
                                    </div>
                                </div>
                            </div>
                    </div>

                    <!-- Form Actions -->
                    <div class="form-actions mt-4">
                        <button type="submit" class="btn btn-primary">
                            <i class="bi bi-save me-2"></i> Update User
                        </button>
                    </div>
                </form>

            </c:when>
            <c:otherwise>
                <div class="alert alert-warning text-center">
                    <strong>User not found</strong>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="../common/scripts.jsp" />
<script>
    // Password toggle
    document.getElementById('togglePassword').addEventListener('click', function () {
        const passwordInput = document.getElementById('newPassword');
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