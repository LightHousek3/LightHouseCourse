<%-- 
    Document   : register-account
    Created on : Jul 8, 2025, 9:18:11 PM
    Author     : NhiDTY-CE180492
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Register - LightHouse</title>
        <!-- Include common header resources -->
        <jsp:include page="/WEB-INF/views/customer/common/head.jsp" />
        <style>
            body {
                background-color: var(--bg-light);
                background-image: url('data:image/svg+xml;charset=utf8,%3Csvg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1440 320"%3E%3Cpath fill="%23e83e8c" fill-opacity="0.05" d="M0,96L48,112C96,128,192,160,288,154.7C384,149,480,107,576,122.7C672,139,768,213,864,213.3C960,213,1056,139,1152,128C1248,117,1344,171,1392,197.3L1440,224L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z"%3E%3C/path%3E%3C/svg%3E');
                background-size: cover;
                background-position: center bottom;
                background-repeat: no-repeat;
                background-attachment: fixed;
                min-height: 100vh;
                display: flex;
                align-items: center;
                justify-content: center;
                padding: 40px 0;
            }
            .register-container {
                max-width: 700px;
                margin: 0 auto;
            }
            .card {
                border: none;
                border-radius: var(--border-radius);
                box-shadow: var(--box-shadow);
                overflow: hidden;
                background-color: rgba(255, 255, 255, 0.98);
                transition: all 0.3s ease;
            }
            .card:hover {
                box-shadow: var(--box-shadow-hover);
                transform: translateY(-5px);
            }
            .card-header {
                background: linear-gradient(135deg, var(--primary-dark), var(--primary-color));
                color: white;
                text-align: center;
                border-radius: 10px 10px 0 0 !important;
                padding: 25px;
            }
            .btn-primary {
                background: linear-gradient(45deg, var(--primary-color), var(--primary-light));
                border: none;
                padding: 0.8rem 1.5rem;
                font-weight: 600;
                letter-spacing: 0.5px;
            }
            .btn-primary:hover {
                background: linear-gradient(45deg, var(--primary-dark), var(--primary-color));
                transform: translateY(-3px);
                box-shadow: var(--box-shadow-hover);
            }
            .form-control {
                padding: 0.8rem 1rem;
                border: 2px solid #f8c4d9;
                border-radius: var(--border-radius);
                transition: all 0.3s ease;
                height: 50px;
            }
            .form-control:focus {
                border-color: var(--primary-color);
                box-shadow: var(--box-shadow-focus);
                transform: none;
            }
            .input-group-text {
                background-color: var(--primary-color);
                color: white;
                border: none;
                border-radius: var(--border-radius) 0 0 var(--border-radius);
            }
            .form-check-input:checked {
                background-color: var(--primary-color);
                border-color: var(--primary-color);
                height: 50px; /* Đặt chiều cao cố định cho icon */
                display: flex;
                align-items: center;
                justify-content: center;
            }
            .form-check-input:checked {
                background-color: var(--primary-color);
                border-color: var(--primary-color);
            }
            .register-title {
                font-weight: 700;
                font-size: 1.8rem;
            }
            .form-label {
                font-weight: 500;
                color: var(--text-medium);
            }
            .input-group {
                height: 50px;
            }
        </style>
    </head>
    <body>
        <div class="container register-container">
            <div class="card animate__animated animate__fadeIn">
                <div class="card-header">
                    <h3 class="register-title mb-0">
                        <!-- SVG Graduation Cap Logo -->
                        <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" class="me-2" style="fill: white;">
                        <path d="M12 3L1 9l4 2.18v6L12 21l7-3.82v-6l2-1.09V17h2V9L12 3m6.82 6L12 12.72 5.18 9 12 5.28 18.82 9M17 16l-5 2.72L7 16v-3.73L12 15l5-2.73V16z"/>
                        </svg>
                        Create Your LightHouse Account
                    </h3>
                </div>
                <div class="card-body p-4">
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger">
                            <i class="fas fa-exclamation-circle me-2"></i>${error}
                        </div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/register" method="post">
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="username" class="form-label">Username*</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fas fa-user"></i></span>
                                    <input type="text" class="form-control ${not empty usernameError ? 'is-invalid' : ''}" 
                                           id="username" name="username" value="${username}" required>
                                    <c:if test="${not empty usernameError}">
                                        <div class="invalid-feedback">${usernameError}</div>
                                    </c:if>
                                </div>
                            </div>

                            <div class="col-md-6 mb-3">
                                <label for="email" class="form-label">Email*</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fas fa-envelope"></i></span>
                                    <input type="email" class="form-control ${not empty emailError ? 'is-invalid' : ''}" 
                                           id="email" name="email" value="${email}" required>
                                    <c:if test="${not empty emailError}">
                                        <div class="invalid-feedback">${emailError}</div>
                                    </c:if>
                                </div>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="password" class="form-label">Password*</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fas fa-lock"></i></span>
                                    <input type="password" class="form-control ${not empty passwordError ? 'is-invalid' : ''}" 
                                           id="password" name="password" required>
                                    <c:if test="${not empty passwordError}">
                                        <div class="invalid-feedback">${passwordError}</div>
                                    </c:if>
                                </div>
                            </div>

                            <div class="col-md-6 mb-3">
                                <label for="confirm-password" class="form-label">Confirm Password*</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fas fa-lock"></i></span>
                                    <input type="password" class="form-control ${not empty confirmPasswordError ? 'is-invalid' : ''}" 
                                           id="confirm-password" name="confirm-password" required>
                                    <c:if test="${not empty confirmPasswordError}">
                                        <div class="invalid-feedback">${confirmPasswordError}</div>
                                    </c:if>
                                </div>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="fullname" class="form-label">Full Name</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-user-circle"></i></span>
                                <input type="text" class="form-control" id="fullname" name="fullname" value="${fullname}">
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="phone" class="form-label">Phone</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-phone"></i></span>
                                <input type="text" class="form-control ${not empty phoneError ? 'is-invalid' : ''}" 
                                       id="phone" name="phone" value="${phone}">
                                <c:if test="${not empty phoneError}">
                                    <div class="invalid-feedback">${phoneError}</div>
                                </c:if>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="address" class="form-label">Address</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-home"></i></span>
                                <input type="text" class="form-control" id="address" name="address" value="${address}" >
                            </div>
                        </div>

                        <div class="d-grid gap-2 mt-4">
                            <button type="submit" class="btn btn-primary btn-lg">
                                <i class="fas fa-user-plus me-2"></i>Register
                            </button>
                        </div>
                    </form>

                    <div class="mt-4 text-center">
                        <p>Already have an account? <a href="${pageContext.request.contextPath}/login" class="text-primary fw-bold">Login here</a></p>
                        <a href="${pageContext.request.contextPath}/" class="btn btn-link">
                            <i class="fas fa-home me-1"></i>Back to Home
                        </a>
                    </div>
                </div>
            </div>
        </div>

        <!-- Include common scripts -->
        <jsp:include page="/WEB-INF/views/customer/common/scripts.jsp" />
    </body>
</html> 