<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>My Profile - LightHouse</title>
        <jsp:include page="/WEB-INF/views/customer/common/head.jsp" />
        <style>
            .profile-header {
                background: linear-gradient(135deg, #e83e8c 0%, #fd86b3 50%, #ffc1d5 100%);
                color: white;
                padding: 80px 0;
                margin-bottom: 40px;
                position: relative;
                overflow: hidden;
            }

            .profile-header::before {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                bottom: 0;
                background: url('${pageContext.request.contextPath}/assets/images/pattern.png');
                opacity: 0.1;
            }

            .profile-header .container {
                position: relative;
                z-index: 2;
            }

            .floating-element {
                position: absolute;
                color: rgba(255, 255, 255, 0.2);
                animation: float 6s infinite ease-in-out;
            }

            .floating-element:nth-child(1) {
                top: 20%;
                left: 10%;
                font-size: 3.5rem;
                animation-delay: 0s;
            }

            .floating-element:nth-child(2) {
                top: 60%;
                left: 20%;
                font-size: 2.5rem;
                animation-delay: 1s;
            }

            .floating-element:nth-child(3) {
                top: 30%;
                right: 15%;
                font-size: 3rem;
                animation-delay: 2s;
            }

            .floating-element:nth-child(4) {
                top: 70%;
                right: 10%;
                font-size: 2rem;
                animation-delay: 3s;
            }

            @keyframes float {
                0% {
                    transform: translateY(0px) rotate(0deg);
                }
                50% {
                    transform: translateY(-20px) rotate(10deg);
                }
                100% {
                    transform: translateY(0px) rotate(0deg);
                }
            }

            .profile-avatar {
                width: 120px;
                height: 120px;
                border-radius: 50%;
                object-fit: cover;
                border: 5px solid rgba(255, 255, 255, 0.3);
                box-shadow: 0 10px 15px rgba(0, 0, 0, 0.1);
            }

            .profile-name {
                font-size: 2rem;
                font-weight: 700;
                margin-bottom: 0.5rem;
                text-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            }

            .profile-role {
                display: inline-block;
                padding: 6px 15px;
                background-color: rgba(255, 255, 255, 0.2);
                border-radius: 20px;
                font-size: 0.9rem;
                margin-bottom: 1rem;
            }

            .nav-pills .nav-link {
                color: #495057;
                background-color: transparent;
                border-radius: 0;
                padding: 1rem 1.5rem;
                font-weight: 500;
                border-bottom: 3px solid transparent;
            }

            .nav-pills .nav-link.active {
                color: var(--primary-color);
                background-color: transparent;
                border-bottom: 3px solid var(--primary-color);
            }

            .card {
                border: none;
                border-radius: 15px;
                box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
                transition: all 0.3s;
            }

            .card:hover {
                transform: translateY(-5px);
                box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
            }

            .info-label {
                color: #6c757d;
                font-size: 0.9rem;
                margin-bottom: 0.25rem;
            }

            .info-value {
                font-weight: 500;
                font-size: 1.1rem;
                margin-bottom: 1.5rem;
            }

            .btn-primary {
                background: linear-gradient(135deg, #e83e8c 0%, #fd86b3 100%);
                border: none;
                box-shadow: 0 5px 15px rgba(232, 62, 140, 0.2);
                transition: all 0.3s;
            }

            .btn-primary:hover {
                background: linear-gradient(135deg, #d32e7b 0%, #ec75a2 100%);
                box-shadow: 0 8px 20px rgba(232, 62, 140, 0.3);
                transform: translateY(-2px);
            }

            .form-control:focus {
                border-color: #fd86b3;
                box-shadow: 0 0 0 0.25rem rgba(232, 62, 140, 0.25);
            }

            .input-group-text {
                background-color: #f8f9fa;
                color: #6c757d;
            }
        </style>
    </head>
    <body>
        <!-- Include navigation -->
        <jsp:include page="/WEB-INF/views/customer/common/navigation.jsp" />

        <!-- Profile Header with Floating Elements -->
        <header class="profile-header">
            <div class="floating-wrapper">
                <div class="floating-element"><i class="fas fa-user-circle"></i></div>
                <div class="floating-element"><i class="fas fa-cog"></i></div>
                <div class="floating-element"><i class="fas fa-graduation-cap"></i></div>
                <div class="floating-element"><i class="fas fa-award"></i></div>
            </div>
            <div class="container">
                <div class="row justify-content-center text-center">
                    <div class="col-lg-8">
                        <img src="https://ui-avatars.com/api/?name=${user.fullName}&background=random" 
                             alt="${user.username}" class="profile-avatar mb-3">
                        <h1 class="profile-name">${not empty user.fullName ? user.fullName : user.username}</h1>
                        <p class="text-white-50">Member since 2023</p>
                    </div>
                </div>
            </div>
        </header>

        <div class="container mb-5">
            <!-- Alerts -->
            <c:if test="${not empty success}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="fas fa-check-circle me-2"></i> ${success}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="fas fa-exclamation-circle me-2"></i> ${error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <c:if test="${not empty passwordSuccess}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="fas fa-lock me-2"></i> ${passwordSuccess}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <!-- Profile Navigation Tabs -->
            <ul class="nav nav-pills mb-4 justify-content-center" id="profileTabs" role="tablist">
                <li class="nav-item" role="presentation">
                    <a class="nav-link ${activeTab == 'info' || empty activeTab ? 'active' : ''}" id="profile-info-tab" data-bs-toggle="pill" href="#profile-info" role="tab" 
                       aria-controls="profile-info" aria-selected="true">
                        <i class="fas fa-user me-2"></i>Profile Information
                    </a>
                </li>
                <li class="nav-item" role="presentation">
                    <a class="nav-link ${activeTab == 'edit' ? 'active' : ''}" id="profile-edit-tab" data-bs-toggle="pill" href="#profile-edit" role="tab" 
                       aria-controls="profile-edit" aria-selected="false">
                        <i class="fas fa-edit me-2"></i>Edit Profile
                    </a>
                </li>
                <li class="nav-item" role="presentation">
                    <a class="nav-link ${activeTab == 'password' ? 'active' : ''}" id="password-change-tab" data-bs-toggle="pill" href="#password-change" role="tab" 
                       aria-controls="password-change" aria-selected="false">
                        <i class="fas fa-lock me-2"></i>Change Password
                    </a>
                </li>
            </ul>

            <!-- Tab Content -->
            <div class="tab-content" id="profileTabsContent">
                <!-- Profile Info Tab -->
                <div class="tab-pane fade ${activeTab == 'info' || empty activeTab ? 'show active' : ''}" id="profile-info" role="tabpanel" aria-labelledby="profile-info-tab">
                    <div class="card">
                        <div class="card-header bg-white py-3">
                            <h4 class="mb-0"><i class="fas fa-info-circle me-2 text-primary"></i>Personal Information</h4>
                        </div>
                        <div class="card-body p-4">
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <div class="info-label">Username</div>
                                    <div class="info-value">${user.username}</div>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <div class="info-label">Email</div>
                                    <div class="info-value">${user.email}</div>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <div class="info-label">Full Name</div>
                                    <div class="info-value">${not empty user.fullName ? user.fullName : '-'}</div>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <div class="info-label">Phone</div>
                                    <div class="info-value">${not empty user.phone ? user.phone : '-'}</div>
                                </div>
                                <div class="col-12">
                                    <div class="info-label">Address</div>
                                    <div class="info-value">${not empty user.address ? user.address : '-'}</div>
                                </div>
                                <div class="col-12 mt-3">
                                    <button class="btn btn-primary" onclick="document.getElementById('profile-edit-tab').click()">
                                        <i class="fas fa-edit me-2"></i>Edit Profile
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Edit Profile Tab -->
                <div class="tab-pane fade ${activeTab == 'edit' ? 'show active' : ''}" id="profile-edit" role="tabpanel" aria-labelledby="profile-edit-tab">
                    <div class="card">
                        <div class="card-header bg-white py-3">
                            <h4 class="mb-0"><i class="fas fa-edit me-2 text-primary"></i>Edit Profile</h4>
                        </div>
                        <div class="card-body p-4">
                            <form action="${pageContext.request.contextPath}/profile" method="post">
                                <input type="hidden" name="action" value="updateProfile">
                                <div class="row">
                                    <div class="col-md-6 mb-3">
                                        <label for="username" class="form-label">Username</label>
                                        <div class="input-group">
                                            <span class="input-group-text"><i class="fas fa-user"></i></span>
                                            <input type="text" class="form-control" id="username" name="username" 
                                                   value="${user.username}" readonly>
                                        </div>
                                        <small class="form-text text-muted">Username cannot be changed.</small>
                                    </div>
                                    <div class="col-md-6 mb-3">
                                        <label for="email" class="form-label">Email*</label>
                                        <div class="input-group">
                                            <span class="input-group-text"><i class="fas fa-envelope"></i></span>
                                            <input type="email" class="form-control ${not empty emailError ? 'is-invalid' : ''}" 
                                                   id="email" name="email" value="${user.email}" required>
                                            <c:if test="${not empty emailError}">
                                                <div class="invalid-feedback">${emailError}</div>
                                            </c:if>
                                        </div>
                                    </div>
                                    <div class="col-md-12 mb-3">
                                        <label for="fullName" class="form-label">Full Name</label>
                                        <div class="input-group">
                                            <span class="input-group-text"><i class="fas fa-user-circle"></i></span>
                                            <input type="text" class="form-control ${not empty fullNameError ? 'is-invalid' : ''}" id="fullName" name="fullName" 
                                                   value="${param.fullName != null ? param.fullName : user.fullName}">
                                            <c:if test="${not empty fullNameError}">
                                                <p class="invalid-feedback">${fullNameError}</p>
                                            </c:if>
                                        </div>
                                    </div>
                                    <div class="col-md-6 mb-3">
                                        <label for="phone" class="form-label">Phone</label>
                                        <div class="input-group">
                                            <span class="input-group-text"><i class="fas fa-phone"></i></span>
                                            <input type="text" class="form-control ${not empty phoneError ? 'is-invalid' : ''}" id="phone" name="phone" 
                                                   value="${param.phone != null ? param.phone : user.phone}">
                                            <c:if test="${not empty phoneError}">
                                                <div class="invalid-feedback">${phoneError}</div>
                                            </c:if>
                                        </div>
                                    </div>
                                    <div class="col-md-12 mb-4">
                                        <label for="address" class="form-label">Address</label>
                                        <div class="input-group">
                                            <span class="input-group-text"><i class="fas fa-home"></i></span>
                                            <textarea class="form-control ${not empty addressError ? 'is-invalid' : ''}" id="address" name="address" 
                                                      rows="3">${param.address != null ? param.address : user.address}</textarea>
                                            <c:if test="${not empty addressError}">
                                                <div class="invalid-feedback">${addressError}</div>
                                            </c:if>

                                        </div>
                                    </div>
                                    <div class="col-12">
                                        <button type="submit" class="btn btn-primary">
                                            <i class="fas fa-save me-2"></i>Save Changes
                                        </button>
                                        <button type="reset" class="btn btn-outline-secondary ms-2">
                                            <i class="fas fa-undo me-2"></i>Reset
                                        </button>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>

                <!-- Change Password Tab -->
                <div class="tab-pane fade ${activeTab == 'password' ? 'show active' : ''}" id="password-change" role="tabpanel" aria-labelledby="password-change-tab">
                    <div class="card">
                        <div class="card-header bg-white py-3">
                            <h4 class="mb-0"><i class="fas fa-lock me-2 text-primary"></i>Change Password</h4>
                        </div>
                        <div class="card-body p-4">
                            <c:choose>
                                <c:when test="${user.authProvider eq 'local'}">
                                    <form action="${pageContext.request.contextPath}/profile" method="post">
                                        <input type="hidden" name="action" value="changePassword">
                                        <div class="mb-3">
                                            <label for="currentPassword" class="form-label">Current Password*</label>
                                            <div class="input-group">
                                                <span class="input-group-text"><i class="fas fa-lock"></i></span>
                                                <input type="password" class="form-control ${not empty passwordError ? 'is-invalid' : ''}" 
                                                       id="currentPassword" name="currentPassword" required autocomplete="current-password">
                                                <c:if test="${not empty passwordError}">
                                                    <div class="invalid-feedback">${passwordError}</div>
                                                </c:if>
                                            </div>
                                        </div>
                                        <div class="mb-3">
                                            <label for="newPassword" class="form-label">New Password*</label>
                                            <div class="input-group">
                                                <span class="input-group-text"><i class="fas fa-key"></i></span>
                                                <input type="password" class="form-control ${not empty newPasswordError ? 'is-invalid' : ''}" 
                                                       id="newPassword" name="newPassword" required autocomplete="new-password">
                                                <c:if test="${not empty newPasswordError}">
                                                    <div class="invalid-feedback">${newPasswordError}</div>
                                                </c:if>
                                            </div>
                                        </div>
                                        <div class="mb-4">
                                            <label for="confirmPassword" class="form-label">Confirm New Password*</label>
                                            <div class="input-group">
                                                <span class="input-group-text"><i class="fas fa-check-double"></i></span>
                                                <input type="password" class="form-control ${not empty confirmPasswordError ? 'is-invalid' : ''}" 
                                                       id="confirmPassword" name="confirmPassword" required autocomplete="new-password">
                                                <c:if test="${not empty confirmPasswordError}">
                                                    <div class="invalid-feedback">${confirmPasswordError}</div>
                                                </c:if>
                                            </div>
                                        </div>
                                        <button type="submit" class="btn btn-primary">
                                            <i class="fas fa-save me-2"></i>Change Password
                                        </button>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <div class="alert alert-info mb-0">
                                        <i class="fab fa-${user.authProvider.toLowerCase()} me-2"></i>
                                        You are logged in using ${user.authProvider}. Password management is handled by ${user.authProvider}.
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Include footer -->
        <jsp:include page="/WEB-INF/views/customer/common/footer.jsp" />
        <jsp:include page="/WEB-INF/views/customer/common/scripts.jsp" />
    </body>
</html> 