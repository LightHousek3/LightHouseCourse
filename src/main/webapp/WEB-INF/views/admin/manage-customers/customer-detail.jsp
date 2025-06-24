<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">

    <head>
        <title>Customer Details - LightHouse Admin</title>
        <jsp:include page="../common/head.jsp" />
        <style>
            .fa-user-circle {
                color: #0808f9;
            }
            
            /* Customer Details Page Styles */
            .customer-profile {
                background-color: #fff;
                border-radius: 10px;
                box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                overflow: hidden;
                margin-bottom: 2rem;
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
            }

            .info-section {
                margin-bottom: 2rem;
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


            /* Email Modal */
            .email-modal .modal-body {
                padding: 2rem;
            }

            .email-form-group {
                margin-bottom: 1.5rem;
            }

            .email-label {
                font-weight: 600;
                margin-bottom: 0.5rem;
                color: #333;
            }

            /* Responsiveness */
            @media (max-width: 767.98px) {
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
                            <h2 class="m-0"><i class="fas fa-user-circle"></i> Customer Details</h2>
                            <div>
                                <nav aria-label="breadcrumb" class="breadcrumb-nav">
                                    <ol class="breadcrumb">
                                        <li class="breadcrumb-item"><a
                                                href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a>
                                        </li>
                                        <li class="breadcrumb-item"><a
                                                href="${pageContext.request.contextPath}/admin/customers">Customers</a>
                                        </li>
                                        <li class="breadcrumb-item active" aria-current="page">Customer Details</li>
                                    </ol>
                                </nav>
                            </div>
                        </div>
                        <div class="header-actions">
                            <a href="${pageContext.request.contextPath}/admin/customers"
                               class="btn btn-lg btn-primary">
                                <i class="fas fa-arrow-left me-2"></i> Back
                            </a>
                        </div>
                    </div>

                    <c:choose>
                        <c:when test="${empty selectedCustomer}">
                            <div class="alert alert-warning" role="alert">
                                <i class="fas fa-exclamation-triangle me-2"></i> Customer not found!
                            </div>
                        </c:when>
                        <c:otherwise>
                            <!-- Customer Profile Card -->
                            <div class="customer-profile">
                                <div class="profile-header">
                                    <img src="${not empty selectedCustomer.avatar ? pageContext.request.contextPath.concat(selectedCustomer.avatar) : pageContext.request.contextPath.concat('/assets/imgs/avatars/default-user.png')}"
                                         alt="${selectedCustomer.username}" class="profile-avatar">
                                    <h3 class="profile-name">${selectedCustomer.fullName}</h3>
                                    <p class="profile-username">@${selectedCustomer.username}</p>
                                    <div class="profile-role">
                                        <span class="status-badge status-cus">Customer</span>
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
                                                            <div class="info-label">Customer ID</div>
                                                            <div class="info-value">
                                                                #${selectedCustomer.customerID}</div>
                                                        </div>
                                                    </li>
                                                    <li class="info-item">
                                                        <div class="info-icon">
                                                            <i class="fas fa-user"></i>
                                                        </div>
                                                        <div class="info-content">
                                                            <div class="info-label">Username</div>
                                                            <div class="info-value">${selectedCustomer.username}
                                                            </div>
                                                        </div>
                                                    </li>
                                                    <li class="info-item">
                                                        <div class="info-icon">
                                                            <i class="fas fa-envelope"></i>
                                                        </div>
                                                        <div class="info-content">
                                                            <div class="info-label">Email</div>
                                                            <div class="info-value">${selectedCustomer.email}
                                                            </div>
                                                        </div>
                                                    </li>
                                                    <li class="info-item">
                                                        <div class="info-icon">
                                                            <i class="fas fa-phone"></i>
                                                        </div>
                                                        <div class="info-content">
                                                            <div class="info-label">Phone</div>
                                                            <div class="info-value">${not empty
                                                                                      selectedCustomer.phone ? selectedCustomer.phone
                                                                                      : 'Not provided'}</div>
                                                        </div>
                                                    </li>
                                                    <li class="info-item">
                                                        <div class="info-icon">
                                                            <i class="fas fa-map-marker-alt"></i>
                                                        </div>
                                                        <div class="info-content">
                                                            <div class="info-label">Address</div>
                                                            <div class="info-value">${not empty
                                                                                      selectedCustomer.address ?
                                                                                      selectedCustomer.address : 'Not provided'}</div>
                                                        </div>
                                                    </li>
                                                </ul>
                                            </div>
                                        </div>

                                        <div class="col-lg-6">
                                            <!-- Account Details Section -->
                                            <div class="info-section">
                                                <h4 class="section-title">
                                                    <i class="fas fa-shield-alt me-2"></i> Account Details
                                                </h4>
                                                <ul class="info-list">
                                                    <li class="info-item">
                                                        <div class="info-icon">
                                                            <i class="fas fa-toggle-on"></i>
                                                        </div>
                                                        <div class="info-content">
                                                            <div class="info-label">Status</div>
                                                            <div class="info-value">
                                                                <c:choose>
                                                                    <c:when test="${selectedCustomer.isActive}">
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
                                                    <li class="info-item">
                                                        <div class="info-icon">
                                                            <i class="fas fa-sign-in-alt"></i>
                                                        </div>
                                                        <div class="info-content">
                                                            <div class="info-label">Auth Provider</div>
                                                            <div class="info-value">
                                                                <c:choose>
                                                                    <c:when
                                                                        test="${selectedCustomer.authProvider eq 'local'}">
                                                                        <span
                                                                            class="status-badge status-completed">Local</span>
                                                                    </c:when>
                                                                    <c:when
                                                                        test="${selectedCustomer.authProvider eq 'google'}">
                                                                        <span
                                                                            class="status-badge status-pending ">Google</span>
                                                                    </c:when>
                                                                    <c:when
                                                                        test="${selectedCustomer.authProvider eq 'facebook'}">
                                                                        <span
                                                                            class="status-badge status-cus">Facebook</span>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span
                                                                            class="badge bg-secondary">${selectedCustomer.authProvider}</span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </div>
                                                        </div>
                                                    </li>
                                                    <c:if test="${not empty selectedCustomer.authProviderId}">
                                                        <li class="info-item">
                                                            <div class="info-icon">
                                                                <i class="fas fa-id-badge"></i>
                                                            </div>
                                                            <div class="info-content">
                                                                <div class="info-label">Provider ID</div>
                                                                <div class="info-value">
                                                                    ${selectedCustomer.authProviderId}</div>
                                                            </div>
                                                        </li>
                                                    </c:if>
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