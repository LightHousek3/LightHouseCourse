<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">

    <head>
        <title>Update Super User - LightHouse Admin</title>
        <jsp:include page="../common/head.jsp" />
        <style>
            .fa-user-edit {
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
                box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
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
                background: rgba(0, 123, 255, 0.05);
            }

            .avatar-preview {
                width: 100px;
                height: 100px;
                border-radius: 50%;
                object-fit: cover;
                margin-bottom: 15px;
                border: 3px solid #e9ecef;
            }

            .avatar-placeholder {
                width: 100px;
                height: 100px;
                border-radius: 50%;
                background: #e9ecef;
                margin: 0 auto 15px auto;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 40px;
                color: #adb5bd;
            }

            .avatar-upload {
                display: inline-block;
                padding: 8px 15px;
                background: #f8f9fa;
                border: 1px solid #e9ecef;
                border-radius: 6px;
                font-size: 14px;
                font-weight: 500;
                color: #495057;
                cursor: pointer;
                transition: all 0.3s;
            }

            .avatar-upload:hover {
                background: #e9ecef;
            }

            /* Button Group */
            .form-actions {
                display: flex;
                justify-content: flex-end;
                gap: 10px;
                margin-top: 30px;
            }

            .btn-cancel {
                background: #6c757d;
                border: none;
                padding: 10px 20px;
                border-radius: 8px;
                color: white;
                font-weight: 500;
            }

            .btn-cancel:hover {
                background: #425566;
                color: white;
            }

            .btn-submit {
                background: #007bff;
                border: none;
                padding: 10px 20px;
                border-radius: 8px;
                color: white;
                font-weight: 500;
            }

            .btn-submit:hover {
                background: #0865c8;
                color: white;
            }

            /* Form Validation */
            .form-control.is-invalid {
                border-color: #dc3545;
                padding-right: calc(1.5em + 0.75rem);
                background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 12 12' width='12' height='12' fill='none' stroke='%23dc3545'%3e%3ccircle cx='6' cy='6' r='4.5'/%3e%3cpath stroke-linejoin='round' d='M5.8 3.6h.4L6 6.5z'/%3e%3ccircle cx='6' cy='8.2' r='.6' fill='%23dc3545' stroke='none'/%3e%3c/svg%3e");
                background-repeat: no-repeat;
                background-position: right calc(0.375em + 0.1875rem) center;
                background-size: calc(0.75em + 0.375rem) calc(0.75em + 0.375rem);
            }

            .invalid-feedback {
                display: block;
                width: 100%;
                margin-top: 0.25rem;
                font-size: 0.875em;
                color: #dc3545;
            }

            .role-selector {
                display: flex;
                gap: 15px;
            }

            .role-card {
                flex: 1;
                padding: 15px;
                border: 2px solid #e9ecef;
                border-radius: 8px;
                text-align: center;
                cursor: pointer;
                transition: all 0.3s;
            }

            .role-card.selected {
                border-color: #0275d8;
                background-color: rgba(2, 117, 216, 0.05);
            }

            .role-card:hover:not(.selected) {
                border-color: #adb5bd;
            }

            .role-icon {
                font-size: 2rem;
                margin-bottom: 10px;
                color: #6c757d;
            }

            .role-card.selected .role-icon {
                color: #0275d8;
            }

            .role-title {
                font-weight: 600;
                font-size: 1.1rem;
            }

            .role-description {
                font-size: 0.85rem;
                color: #6c757d;
                margin-top: 5px;
            }

            /* Password toggle button styling */
            .password-toggle-icon {
                position: absolute;
                right: 10px;
                top: 50%;
                transform: translateY(-50%);
                cursor: pointer;
                color: #6c757d;
                z-index: 10;
            }

            .password-toggle-icon:hover {
                color: #495057;
            }

            .password-field-wrapper {
                position: relative;
            }

            .password-field-wrapper input {
                padding-right: 40px;
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
                            <h2 class="m-0"><i class="fas fa-user-edit"></i> Update Super User</h2>
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
                                        <li class="breadcrumb-item active" aria-current="page">Update Super User
                                        </li>
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

                    <c:if test="${empty selectedUser}">
                        <div class="alert alert-danger" role="alert">
                            <i class="fas fa-exclamation-triangle me-2"></i> User not found!
                        </div>
                    </c:if>

                    <c:if test="${not empty selectedUser}">
                        <!-- Content Card -->
                        <div class="content-card">
                            <div class="card-body">
                                <form action="${pageContext.request.contextPath}/admin/superusers" method="post"
                                      id="updateSuperUserForm">
                                    <input type="hidden" name="action" value="updateUser">
                                    <input type="hidden" name="superUserId" value="${selectedUser.superUserID}">
                                    <input type="hidden" name="role" id="selectedRole"
                                           value="${selectedUser.role}">

                                    <!-- Role Selection Section -->
                                    <div class="form-section">
                                        <h3 class="section-title"><i class="fas fa-user-tag me-2"></i>
                                            User Role</h3>
                                        <div class="role-selector">
                                            <div class="role-card ${selectedUser.role == 'admin' ? 'selected' : ''}"
                                                 data-role="admin">
                                                <div class="role-icon">
                                                    <i class="fas fa-user-shield"></i>
                                                </div>
                                                <div class="role-title">Admin</div>
                                                <div class="role-description">Can manage all aspects of the
                                                    system</div>
                                            </div>
                                            <div class="role-card ${selectedUser.role == 'instructor' ? 'selected' : ''}"
                                                 data-role="instructor">
                                                <div class="role-icon">
                                                    <i class="fas fa-chalkboard-teacher"></i>
                                                </div>
                                                <div class="role-title">Instructor</div>
                                                <div class="role-description">Can create and manage courses
                                                </div>
                                            </div>
                                        </div>
                                        <c:if test="${not empty roleError}">
                                            <div class="invalid-feedback">
                                                ${roleError}
                                            </div>
                                        </c:if>
                                    </div>

                                    <!-- Account Information Section -->
                                    <div class="form-section">
                                        <h3 class="section-title"><i class="fas fa-user-shield me-2"></i>
                                            Account Information</h3>
                                        <div class="row g-3">
                                            <div class="col-md-6">
                                                <label for="username" class="form-label">Username <span
                                                        class="required">*</span></label>
                                                <input type="text"
                                                       class="form-control ${not empty usernameError ? 'is-invalid' : ''}"
                                                       id="username" name="username"
                                                       value="${selectedUser.username}" required>
                                                <c:if test="${not empty usernameError}">
                                                    <div class="invalid-feedback">
                                                        ${usernameError}
                                                    </div>
                                                </c:if>
                                            </div>
                                            <div class="col-md-6">
                                                <label for="password" class="form-label">Password</label>
                                                <div class="password-field-wrapper">
                                                    <input type="password"
                                                           class="form-control ${not empty passwordError ? 'is-invalid' : ''}"
                                                           id="password" name="password"
                                                           value="${selectedUser.password}"
                                                           placeholder="Leave unchanged to keep current password">
                                                    <span class="password-toggle-icon" id="togglePassword">
                                                        <i class="fas fa-eye"></i>
                                                    </span>
                                                </div>
                                                <c:if test="${not empty passwordError}">
                                                    <div class="invalid-feedback">
                                                        ${passwordError}
                                                    </div>
                                                </c:if>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Personal Information Section -->
                                    <div class="form-section">
                                        <h3 class="section-title"><i class="fas fa-id-card me-2"></i> Personal
                                            Information</h3>
                                        <div class="row g-3">
                                            <div class="col-md-6">
                                                <label for="fullName" class="form-label">Full Name <span
                                                        class="required">*</span></label>
                                                <input type="text"
                                                       class="form-control ${not empty fullnameError ? 'is-invalid' : ''}"
                                                       id="fullName" name="fullName"
                                                       value="${selectedUser.fullName}" required>
                                                <c:if test="${not empty fullnameError}">
                                                    <div class="invalid-feedback">
                                                        ${fullnameError}
                                                    </div>
                                                </c:if>
                                            </div>
                                            <div class="col-md-6">
                                                <label for="email" class="form-label">Email <span
                                                        class="required">*</span></label>
                                                <input type="email"
                                                       class="form-control ${not empty emailError ? 'is-invalid' : ''}"
                                                       id="email" name="email" value="${selectedUser.email}"
                                                       required>
                                                <c:if test="${not empty emailError}">
                                                    <div class="invalid-feedback">
                                                        ${emailError}
                                                    </div>
                                                </c:if>
                                            </div>
                                            <div class="col-md-6">
                                                <label for="phone" class="form-label">Phone Number</label>
                                                <input type="tel"
                                                       class="form-control ${not empty phoneError ? 'is-invalid' : ''}"
                                                       id="phone" name="phone" value="${selectedUser.phone}">
                                                <c:if test="${not empty phoneError}">
                                                    <div class="invalid-feedback">
                                                        ${phoneError}
                                                    </div>
                                                </c:if>
                                            </div>
                                            <div class="col-md-6">
                                                <label for="address" class="form-label">Address</label>
                                                <textarea class="form-control" id="address" name="address"
                                                          rows="1">${selectedUser.address}</textarea>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Instructor Information Section -->
                                    <div class="form-section instructor-fields" id="instructorFields"
                                         style="display: ${selectedUser.role == 'instructor' ? 'block' : 'none'}">
                                        <h3 class="section-title"><i class="fas fa-chalkboard-teacher me-2"></i>
                                            Instructor Information</h3>
                                        <div class="row g-3">
                                            <div class="col-md-12">
                                                <label for="specialization" class="form-label">Specialization
                                                    <span class="required">*</span></label>
                                                <input type="text" class="form-control" id="specialization"
                                                       name="specialization" value="${specialization}"
                                                       placeholder="e.g. Web Development, Data Science, Mobile App Development">
                                                <c:if test="${not empty specializationError}">
                                                    <div class="invalid-feedback">
                                                        ${specializationError}
                                                    </div>
                                                </c:if>
                                            </div>
                                            <div class="col-md-12">
                                                <label for="biography" class="form-label">Biography <span
                                                        class="required">*</span></label>
                                                <textarea class="form-control" id="biography" name="biography"
                                                          rows="4"
                                                          placeholder="Professional background and teaching experience">${biography}</textarea>
                                                <c:if test="${not empty biographyError}">
                                                    <div class="invalid-feedback">
                                                        ${biographyError}
                                                    </div>
                                                </c:if>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Profile Image Section -->
                                    <div class="form-section">
                                        <h3 class="section-title"><i class="fas fa-image me-2"></i> Profile
                                            Image</h3>
                                        <div class="row">
                                            <div class="col-md-12 mx-auto">
                                                <div class="avatar-section">
                                                    <c:choose>
                                                        <c:when test="${not empty selectedUser.avatar}">
                                                            <c:choose>
                                                                <c:when test="${fn:startsWith(selectedUser.avatar, '/assets')}">
                                                                    <img src="${pageContext.request.contextPath}${selectedUser.avatar}"
                                                                         alt="Customer Avatar" class="avatar-preview"
                                                                         id="avatarPreview">
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <img src="${selectedUser.avatar}"
                                                                         alt="Customer Avatar" class="avatar-preview"
                                                                         id="avatarPreview">
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
                                                    <div>
                                                        <label for="avatarInput" class="avatar-upload">
                                                            <i class="fas fa-upload me-2"></i> Upload Image
                                                        </label>
                                                        <input type="file" id="avatarInput" name="avatar"
                                                               accept="image/*" style="display: none;">
                                                        <input type="hidden" id="avatarUrl" name="avatarUrl"
                                                               value="${selectedUser.avatar}">
                                                    </div>
                                                    <small class="text-muted d-block mt-2">
                                                        Recommended: Square image, 500x500 pixels or larger
                                                    </small>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Form Actions -->
                                    <div class="form-actions">
                                        <a href="${pageContext.request.contextPath}/admin/superusers"
                                           class="btn btn-md btn-cancel">
                                            <i class="fas fa-times me-2"></i> Cancel
                                        </a>
                                        <button type="submit" class="btn btn-md btn-submit">
                                            <i class="fas fa-save me-2"></i> Save
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
        <jsp:include page="../common/scripts.jsp" />

        <script>
            document.addEventListener('DOMContentLoaded', () => {
                // Role selection
                document.querySelectorAll('.role-card').forEach(card => {
                    card.addEventListener('click', function () {
                        // Remove 'selected' class from all cards
                        document.querySelectorAll('.role-card').forEach(c => c.classList.remove('selected'));

                        // Add 'selected' class to clicked card
                        this.classList.add('selected');

                        // Update hidden input with selected role
                        const selectedRole = this.getAttribute('data-role');
                        document.getElementById('selectedRole').value = selectedRole;

                        // Show/hide instructor fields based on role
                        const instructorFields = document.getElementById('instructorFields');
                        if (selectedRole === 'instructor') {
                            instructorFields.style.display = 'block';
                        } else {
                            instructorFields.style.display = 'none';
                        }
                    });
                });

                // Image preview with existing data retention
                document.getElementById('avatarInput').addEventListener('change', function (e) {
                    const file = e.target.files[0];
                    if (file) {
                        const reader = new FileReader();
                        reader.onload = function (e) {
                            const preview = document.getElementById('avatarPreview');
                            const placeholder = document.getElementById('avatarPlaceholder');

                            if (preview) {
                                preview.src = e.target.result;
                            } else {
                                // Create preview if it doesn't exist
                                const newPreview = document.createElement('img');
                                newPreview.src = e.target.result;
                                newPreview.alt = 'User Avatar';
                                newPreview.id = 'avatarPreview';
                                newPreview.className = 'avatar-preview';

                                if (placeholder) {
                                    placeholder.parentNode.replaceChild(newPreview, placeholder);
                                }
                            }

                            // Update hidden field for form submission
                            document.getElementById('avatarUrl').value = e.target.result;
                        };
                        reader.readAsDataURL(file);
                    }
                });

                // Toggle password visibility
                document.getElementById('togglePassword').addEventListener('click', function () {
                    const passwordInput = document.getElementById('password');
                    const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
                    passwordInput.setAttribute('type', type);

                    // Toggle icon
                    const icon = this.querySelector('i');
                    icon.classList.toggle('fa-eye');
                    icon.classList.toggle('fa-eye-slash');
                });
            });
        </script>
    </body>

</html>