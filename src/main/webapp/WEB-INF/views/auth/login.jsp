<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <title>Login - LightHouse</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta http-equiv="X-UA-Compatible" content="ie=edge">

        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">

        <!-- Font Awesome -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

        <!-- Google Fonts - Poppins -->
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">

        <!-- Custom CSS -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css?version=<%= System.currentTimeMillis()%>">
        <style>

            .card:hover {
                transform: translateY(0);
            }

            h4 {
                margin-bottom: 0;
            }

            .social-btn {
                margin-bottom: 10px;
                width: 100%;
                text-align: center;
                padding: 10px;
                border-radius: var(--border-radius);
                display: flex;
                align-items: center;
                justify-content: center;
                text-decoration: none;
            }
            .google-btn {
                background-color: #fff;
                color: #444;
                border: 1px solid #ddd;
            }
            .google-btn:hover {
                background-color: #f8f9fa;
                color: #333;
            }
            .facebook-btn {
                background-color: #3b5998;
                color: #fff;
                border: none;
            }
            .facebook-btn:hover {
                background-color: #2d4373;
                color: #fff;
            }
            .divider {
                margin: 20px 0;
                text-align: center;
                border-bottom: 1px solid #ddd;
                line-height: 0;
            }
            .divider span {
                background-color: #fff;
                padding: 0 10px;
            }
        </style>
    </head>
    <body>
        <div class="container mt-3">
            <div class="row justify-content-center">
                <div class="col-md-6 col-lg-5">
                    <div class="card shadow">
                        <div class="card-header btn-primary text-white text-center py-3">
                            <h4>Login to Your Account</h4>
                        </div>
                        <div class="card-body p-4">

                            <c:if test="${not empty error}">
                                <c:set var="errorMessage" value="${error}" />
                                <c:remove var="error" scope="request" />
                                <div class="alert alert-danger" role="alert">
                                    ${errorMessage}
                                </div>
                            </c:if>


                            <!-- Login Form -->
                            <form action="${pageContext.request.contextPath}/login" method="post">
                                <div class="mb-3">
                                    <label for="username" class="form-label">Username</label>
                                    <input type="text" class="form-control" id="username" name="username" value="${not empty savedUsername ? savedUsername : ''}" required>
                                </div>
                                <div class="mb-3">
                                    <label for="password" class="form-label">Password</label>
                                    <input type="password" class="form-control" id="password" name="password" required>
                                </div>
                                <div class="mb-3 form-check">
                                    <input type="checkbox" class="form-check-input" id="remember-me" name="remember-me" ${not empty isRemembered ? 'checked' : ''}>
                                    <label class="form-check-label" for="remember-me">Remember me</label>
                                </div>
                                <div class="d-grid gap-2">
                                    <button type="submit" class="btn btn-primary">Login</button>
                                </div>
                            </form>

                            <!-- Divider -->
                            <div class="divider">
                                <span>OR</span>
                            </div>

                            <!-- Social Login Buttons -->
                            <div class="social-login mb-3">
                                <a href="${pageContext.request.contextPath}/login?method=googleLogin" class="btn social-btn google-btn w-100 mb-2">
                                    <i class="fab fa-google me-2"></i> Continue with Google
                                </a>
                                <a href="${pageContext.request.contextPath}/login?method=facebookLogin" class="btn social-btn facebook-btn w-100 text-white">
                                    <i class="fab fa-facebook-f me-2"></i> Continue with Facebook
                                </a>
                            </div>


                        </div>
                        <div class="card-footer text-center py-3">
                            <div class="small">
                                Don't have an account? <a href="${pageContext.request.contextPath}/register">Register now</a>
                            </div>
                            <div class="small mt-2">
                                <a href="${pageContext.request.contextPath}/forgot-password">Forgot password?</a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Bootstrap JS -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>

        <!-- Custom JavaScript -->
    </body>
</html> 