<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">

    <head>
        <title>Update Customer - LightHouse Admin</title>
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

            /* Social Login Styles */
            .social-login-badge {
                font-size: 0.9rem;
                padding: 0.5rem 1rem;
                border-radius: 50px;
                display: inline-flex;
                align-items: center;
                gap: 8px;
                margin-bottom: 15px;
            }

            .badge-google {
                background-color: #f8f9fa;
                color: #ea4335;
                border: 1px solid #ea4335;
            }

            .badge-facebook {
                background-color: #f8f9fa;
                color: #3b5998;
                border: 1px solid #3b5998;
            }

            .social-note {
                color: #6c757d;
                font-size: 0.9rem;
                margin-top: 10px;
                font-style: italic;
            }

            .password-field-wrapper {
                position: relative;
            }

            .password-toggle-icon {
                position: absolute;
            }
        </style>
    </head>

    <body>
        <!-- Include Admin Sidebar -->
        <c:set var="activeMenu" value="customers" scope="request" />
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
                            <h2 class="m-0"><i class="fas fa-user-edit"></i> Update Customer</h2>
                            <div>
                                <nav aria-label="breadcrumb" class="breadcrumb-nav">
                                    <ol class="breadcrumb">
                                        <li class="breadcrumb-item"><a
                                                href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a>
                                        </li>
                                        <li class="breadcrumb-item"><a
                                                href="${pageContext.request.contextPath}/admin/customers">Customers</a>
                                        </li>
                                        <li class="breadcrumb-item active" aria-current="page">Update Customer</li>
                                    </ol>
                                </nav>
                            </div>
                        </div>
                        <div class="header-actions row">
                            <a href="${pageContext.request.contextPath}/admin/customers" class="col-md-6 col-sm-12 btn btn-lg btn-primary">
                                <i class="fas fa-arrow-left me-2"></i> Back
                            </a>
                            <a href="${pageContext.request.contextPath}/admin/customers/edit/${selectedCustomer.customerID}"
                               class="col-md-6 col-sm-12 btn btn-lg btn-outline-secondary">
                                <i class="fas fa-sync-alt me-1"></i> Refresh
                            </a>
                        </div>
                    </div>

                    <c:if test="${empty selectedCustomer}">
                        <div class="alert alert-danger" role="alert">
                            <i class="fas fa-exclamation-triangle me-2"></i> Customer not found!
                        </div>
                    </c:if>

                    <c:if test="${not empty selectedCustomer}">
                        <!-- Content Card -->
                        <div class="content-card">
                            <div class="card-body">
                                <form action="${pageContext.request.contextPath}/admin/customers" method="post"
                                      id="updateCustomerForm">
                                    <input type="hidden" name="action" value="updateCustomer">
                                    <input type="hidden" name="customerId"
                                           value="${selectedCustomer.customerID}">
                                    <input id="selectedCustomerInput" type="hidden" name="selectedCustomer">

                                    <!-- Account Information Section -->
                                    <div class="form-section">
                                        <h3 class="section-title"><i class="fas fa-user-shield me-2"></i>
                                            Account Information</h3>

                                        <c:if test="${selectedCustomer.isSocialLogin()}">
                                            <div class="alert alert-info" role="alert">
                                                <i class="fas fa-info-circle me-2"></i> This customer logs in
                                                via
                                                <c:choose>
                                                    <c:when test="${selectedCustomer.authProvider eq 'google'}">
                                                        <span class="social-login-badge badge-google">
                                                            <i class="fab fa-google"></i> Google
                                                        </span>
                                                    </c:when>
                                                    <c:when
                                                        test="${selectedCustomer.authProvider eq 'facebook'}">
                                                        <span class="social-login-badge badge-facebook">
                                                            <i class="fab fa-facebook-f"></i> Facebook
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        ${selectedCustomer.authProvider}
                                                    </c:otherwise>
                                                </c:choose>
                                                <div class="social-note">
                                                    Note: Some account settings are managed by the
                                                    authentication provider.
                                                </div>
                                            </div>
                                        </c:if>

                                        <div class="row g-3">
                                            <div class="col-md-6">
                                                <label for="username" class="form-label">Username <span
                                                        class="required"></span></label>
                                                <input type="text"
                                                       class="form-control ${not empty usernameError ? 'is-invalid' : ''}"
                                                       id="username" name="username"
                                                       value="${selectedCustomer.username}"
                                                       ${selectedCustomer.isSocialLogin() ? 'readonly' : ''}>
                                                <c:if test="${not empty usernameError}">
                                                    <div class="invalid-feedback">
                                                        ${usernameError}
                                                    </div>
                                                </c:if>
                                            </div>
                                            <div class="col-md-6">
                                                <label for="password" class="form-label">Password<span
                                                        class="required"></span></label>
                                                <div class="password-field-wrapper">
                                                    <input type="password"
                                                           class="form-control ${not empty passwordError ? 'is-invalid' : ''}"
                                                           id="password" name="password" value="${selectedCustomer.password}">
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
                                                       value="${selectedCustomer.fullName}">
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
                                                       id="email" name="email" value="${selectedCustomer.email}">
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
                                                       id="phone" name="phone" value="${selectedCustomer.phone}">
                                                <c:if test="${not empty phoneError}">
                                                    <div class="invalid-feedback">
                                                        ${phoneError}
                                                    </div>
                                                </c:if>
                                            </div>
                                            <div class="col-md-6">
                                                <label for="address" class="form-label">Address</label>
                                                <textarea class="form-control" id="address" name="address"
                                                          rows="1">${selectedCustomer.address}</textarea>
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
                                                        <c:when test="${not empty selectedCustomer.avatar}">
                                                            <c:choose>
                                                                <c:when test="${fn:startsWith(selectedCustomer.avatar, 'data:image')}">
                                                                    <img src="${selectedCustomer.avatar}"
                                                                         alt="Customer Avatar" class="avatar-preview"
                                                                         id="avatarPreview">
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <img src="${pageContext.request.contextPath}${selectedCustomer.avatar}"
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
                                                               value="${selectedCustomer.avatar}">
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
                                        <a href="${pageContext.request.contextPath}/admin/customers"
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
                // Image preview
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
                                newPreview.alt = 'Customer Avatar';
                                newPreview.id = 'avatarPreview';
                                newPreview.className = 'avatar-preview';

                                if (placeholder) {
                                    placeholder.parentNode.replaceChild(newPreview, placeholder);
                                }
                            }

                            // Update hidden field for form submission
                            // In a real application, you would upload this file and get a URL
                            // For demo purposes, we're just using the data URL
                            document.getElementById('avatarUrl').value = e.target.result;
                        };
                        reader.readAsDataURL(file);
                    }
                });
            });

        </script>
    </body>

</html>