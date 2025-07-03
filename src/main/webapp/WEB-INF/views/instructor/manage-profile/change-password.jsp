<%-- 
    Document   : change-password
    Created on : Jul 1, 2025, 3:02:26 PM
    Author     : NhiDTY-CE180492
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">

    <head>
        <title>Change Password - LightHouse Instructor</title>
        <jsp:include page="../common/head.jsp" />
        <style>

            .fa-lock {
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
                justify-content: end;
                margin: 0;
            }

            /* Content Card */
            .content-card {
                background: white;
                border-radius: 12px;
                box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
                border: 1px solid #e9ecef;
                overflow: hidden;
                max-width: 800px;
                margin: 0 auto;
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
                padding: 40px;
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

            .input-group .btn {
                position: relative;
                z-index: 2;
                height: 100%;
                padding: 15px;
            }

            .input-group-text {
                background: #f8f9fa;
                border: 1px solid #e9ecef;
                color: #6c757d;
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
                transition: all 0.3s ease;
            }

            .btn-cancel:hover {
                background: #425566;
                color: white;
                transform: translateY(-2px);
                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
            }

            .btn-submit {
                background: #e91e63;  /* Màu hồng */
                border: none;
                padding: 10px 20px;
                border-radius: 8px;
                color: white;
                font-weight: 500;
                transition: all 0.3s ease;
            }

            .btn-submit:hover {
                background: #c2185b;  /* Màu hồng đậm hơn khi hover */
                color: white;
                transform: translateY(-2px);
                box-shadow: 0 4px 8px rgba(233, 30, 99, 0.3);
            }

            /* Header actions button (Back to Profile) */
            .header-actions .btn {
                font-weight: 500;
                padding: 8px 16px;
                border-radius: 6px;
                background: linear-gradient(60deg, #e91e63, #c2185b) !important;  /* Gradient hồng */
                color: white !important;
                border: none !important;
                transition: all 0.3s ease;
            }

            .header-actions .btn:hover {
                background: linear-gradient(60deg, #c2185b, #ad1457) !important;  /* Gradient hồng đậm hơn */
                transform: translateY(-2px);
                box-shadow: 0 4px 8px rgba(233, 30, 99, 0.3);
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

            /* Password strength indicator */

            .password-requirements ul {
                list-style: none;
                padding: 0;
                margin: 5px 0 0 0;
            }

            .password-requirements li {
                margin: 2px 0;
                padding-left: 20px;
                position: relative;
            }

            .password-requirements li::before {
                content: "✗";
                position: absolute;
                left: 0;
                color: #dc3545;
                font-weight: bold;
            }

            .password-requirements li.valid::before {
                content: "✓";
                color: #28a745;
            }

            /* Security notice */
            .security-notice {
                background: #e3f2fd;
                border: 1px solid #2196f3;
                border-radius: 8px;
                padding: 15px;
                margin-bottom: 20px;
            }

            .security-notice .security-icon {
                color: #2196f3;
                font-size: 1.2em;
                margin-right: 8px;
            }

            /* Container Styles */
            .instructor-container {
                display: flex;
                min-height: 100vh;
                background-color: #f8f9fa;
            }

            /* Sidebar styles */

            /* Content area */
            .instructor-content {
                flex: 1;
                margin-left: 280px;
                padding: 20px;
                min-height: 100vh;
                width: calc(100% - 280px);
            }

            /* Header styles */
            .instructor-header {
                background: #fff;
                border-radius: 8px;
                padding: 20px;
                margin-bottom: 20px;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            }

            .instructor-header h2 {
                color: #333;
                font-weight: 600;
                margin: 0;
            }

            /* Responsive */
            @media (max-width: 991.98px) {
                .instructor-sidebar {
                    transform: translateX(-100%);
                }

                .instructor-sidebar.show {
                    transform: translateX(0);
                }

                .instructor-content {
                    margin-left: 0;
                    width: 100%;
                }
            }

            @media (max-width: 767.98px) {
                .card-body {
                    padding: 20px;
                }

                .instructor-content {
                    padding: 15px;
                }

                .content-card {
                    margin: 0 15px;
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
                    <!-- Success/Error Messages -->
                    <c:if test="${not empty successMessage}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="fas fa-check-circle me-2"></i>
                            ${successMessage}
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>

                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="fas fa-exclamation-triangle me-2"></i>
                            ${errorMessage}
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>

                    <!-- Header -->
                    <div class="instructor-header d-flex justify-content-between align-items-center">
                        <button class="btn d-lg-none" id="toggleSidebarBtn">
                            <i class="fas fa-bars"></i>
                        </button>
                        <div class="d-none d-lg-block">
                            <h2 class="m-0"><i class="fas fa-lock"></i> Change Password</h2>
                            <div>
                                <nav aria-label="breadcrumb" class="breadcrumb-nav">
                                    <ol class="breadcrumb">
                                        <li class="breadcrumb-item"><a
                                                href="${pageContext.request.contextPath}/instructor/profile">My Profile</a>
                                        </li>
                                        <li class="breadcrumb-item active" aria-current="page">Change Password</li>
                                    </ol>
                                </nav>
                            </div>
                        </div>
                        <div class="header-actions row">
                            <a href="${pageContext.request.contextPath}/instructor/profile" class="col-md-6 col-sm-12 btn btn-lg btn-primary">
                                <i class="fas fa-arrow-left me-2"></i> Back to Profile
                            </a>
                            <a href="${pageContext.request.contextPath}/instructor/profile/change"
                               class="col-md-6 col-sm-12 btn btn-lg btn-outline-secondary">
                                <i class="fas fa-sync-alt me-1"></i> Refresh
                            </a>
                        </div>
                    </div>

                    <!-- Content Card -->
                    <div class="content-card">
                        <div class="card-body">

                            <form action="${pageContext.request.contextPath}/instructor/profile" method="post"
                                  id="changePasswordForm">
                                <input type="hidden" name="action" value="changePassword">

                                <!-- Password Change Section -->
                                <div class="form-section">
                                    <h3 class="section-title"><i class="fas fa-key me-2"></i> Change Password</h3>

                                    <div class="row g-3">
                                        <div class="col-12">
                                            <label for="currentPassword" class="form-label">Current Password <span class="required">*</span></label>
                                            <div class="input-group">
                                                <input type="password"
                                                       class="form-control ${not empty currentPasswordError ? 'is-invalid' : ''}"
                                                       id="currentPassword" name="currentPassword"
                                                       placeholder="Enter your current password" required>
                                                <button class="btn btn-outline-secondary" type="button" id="toggleCurrentPassword">
                                                    <i class="fas fa-eye"></i>
                                                </button>
                                            </div>
                                            <c:if test="${not empty currentPasswordError}">
                                                <div class="invalid-feedback">
                                                    ${currentPasswordError}
                                                </div>
                                            </c:if>
                                        </div>

                                        <div class="col-12">
                                            <label for="newPassword" class="form-label">New Password <span class="required">*</span></label>
                                            <div class="input-group">
                                                <input type="password"
                                                       class="form-control ${not empty newPasswordError ? 'is-invalid' : ''}"
                                                       id="newPassword" name="newPassword"
                                                       placeholder="Enter your new password" required>
                                                <button class="btn btn-outline-secondary" type="button" id="toggleNewPassword">
                                                    <i class="fas fa-eye"></i>
                                                </button>
                                            </div>

                                            <small class="text-muted">Password must be at least 6 characters.</small>


                                            <c:if test="${not empty newPasswordError}">
                                                <div class="invalid-feedback">
                                                    ${newPasswordError}
                                                </div>
                                            </c:if>
                                        </div>

                                        <div class="col-12">
                                            <label for="confirmPassword" class="form-label">Confirm New Password <span class="required">*</span></label>
                                            <div class="input-group">
                                                <input type="password"
                                                       class="form-control ${not empty confirmPasswordError ? 'is-invalid' : ''}"
                                                       id="confirmPassword" name="confirmPassword"
                                                       placeholder="Confirm your new password" required>
                                                <button class="btn btn-outline-secondary" type="button" id="toggleConfirmPassword">
                                                    <i class="fas fa-eye"></i>
                                                </button>
                                            </div>
                                            <c:if test="${not empty confirmPasswordError}">
                                                <div class="invalid-feedback">
                                                    ${confirmPasswordError}
                                                </div>
                                            </c:if>
                                        </div>
                                    </div>
                                </div>

                                <!-- Form Actions -->
                                <div class="form-actions">
                                    <a href="${pageContext.request.contextPath}/instructor/profile"
                                       class="btn btn-md btn-cancel">
                                        <i class="fas fa-times me-2"></i> Cancel
                                    </a>
                                    <button type="submit" class="btn btn-md btn-submit">
                                        <i class="fas fa-key me-2"></i> Change Password
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <jsp:include page="../common/scripts.jsp" />

        <script>
            document.addEventListener('DOMContentLoaded', () => {
                // Password visibility toggle functionality
                function setupPasswordToggle(inputId, toggleId) {
                    const input = document.getElementById(inputId);
                    const toggle = document.getElementById(toggleId);

                    if (input && toggle) {
                        toggle.addEventListener('click', function () {
                            const type = input.getAttribute('type') === 'password' ? 'text' : 'password';
                            input.setAttribute('type', type);

                            const icon = toggle.querySelector('i');
                            icon.classList.toggle('fa-eye');
                            icon.classList.toggle('fa-eye-slash');
                        });
                    }
                }

                // Setup password toggles
                setupPasswordToggle('currentPassword', 'toggleCurrentPassword');
                setupPasswordToggle('newPassword', 'toggleNewPassword');
                setupPasswordToggle('confirmPassword', 'toggleConfirmPassword');

                // Password strength checker
                const newPasswordInput = document.getElementById('newPassword');
                const passwordStrength = document.getElementById('passwordStrength');
                const passwordStrengthBar = document.getElementById('passwordStrengthBar');
                const requirements = {
                    length: document.getElementById('req-length')
                };

                if (newPasswordInput) {
                    newPasswordInput.addEventListener('input', function () {
                        const password = this.value;

                        if (password.length === 0) {
                            passwordStrength.style.display = 'none';
                            return;
                        }

                        passwordStrength.style.display = 'block';

                        // Check requirements
                        const checks = {
                            length: password.length >= 6
                        };

                        // Update requirement indicators
                        Object.keys(checks).forEach(key => {
                            if (requirements[key]) {
                                if (checks[key]) {
                                    requirements[key].classList.add('valid');
                                } else {
                                    requirements[key].classList.remove('valid');
                                }
                            }
                        });

                        // Calculate strength
                        const validChecks = Object.values(checks).filter(Boolean).length;
                        let strengthClass = '';

                        if (validChecks <= 2) {
                            strengthClass = 'strength-weak';
                        } else if (validChecks === 3) {
                            strengthClass = 'strength-fair';
                        } else if (validChecks === 4) {
                            strengthClass = 'strength-good';
                        } else {
                            strengthClass = 'strength-strong';
                        }

                        passwordStrengthBar.className = 'password-strength-bar ' + strengthClass;
                    });
                }

                // Password confirmation validation
                const confirmPasswordInput = document.getElementById('confirmPassword');
                if (newPasswordInput && confirmPasswordInput) {
                    function validatePasswordMatch() {
                        if (confirmPasswordInput.value && newPasswordInput.value !== confirmPasswordInput.value) {
                            confirmPasswordInput.setCustomValidity('Passwords do not match');
                            confirmPasswordInput.classList.add('is-invalid');
                        } else {
                            confirmPasswordInput.setCustomValidity('');
                            confirmPasswordInput.classList.remove('is-invalid');
                        }
                    }

                    confirmPasswordInput.addEventListener('input', validatePasswordMatch);
                    newPasswordInput.addEventListener('input', validatePasswordMatch);
                }

                // Form submission validation
                const form = document.getElementById('changePasswordForm');
                if (form) {
                    form.addEventListener('submit', function (e) {
                        const currentPassword = document.getElementById('currentPassword');
                        const newPassword = document.getElementById('newPassword');
                        const confirmPassword = document.getElementById('confirmPassword');

                        // Validate all fields are filled
                        if (!currentPassword.value || !newPassword.value || !confirmPassword.value) {
                            e.preventDefault();
                            alert('Please fill in all password fields.');
                            return;
                        }

                        // Validate password match
                        if (newPassword.value !== confirmPassword.value) {
                            e.preventDefault();
                            alert('New password and confirmation do not match.');
                            confirmPassword.focus();
                            return;
                        }

                        // Validate password strength
                        const password = newPassword.value;
                        if (password.length < 6) {
                            e.preventDefault();
                            alert('Password must be at least 6 characters.');
                            newPassword.focus();
                            return;
                        }


                        // Validate new password is different from current
                        if (currentPassword.value === newPassword.value) {
                            e.preventDefault();
                            alert('New password must be different from current password.');
                            newPassword.focus();
                            return;
                        }

                        // Show loading state
                        const submitBtn = form.querySelector('button[type="submit"]');
                        if (submitBtn) {
                            submitBtn.disabled = true;
                            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i> Changing Password...';
                        }
                    });
                }
            });
        </script>
    </body>

</html>