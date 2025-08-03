<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <!DOCTYPE html>
        <html>

        <head>
            <title>Forgot Password - LightHouse</title>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <meta http-equiv="X-UA-Compatible" content="ie=edge">

            <!-- Bootstrap CSS -->
            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css"
                rel="stylesheet">

            <!-- Font Awesome -->
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

            <!-- Google Fonts - Poppins -->
            <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700;800&display=swap"
                rel="stylesheet">

            <!-- Custom CSS -->
            <link rel="stylesheet"
                href="${pageContext.request.contextPath}/assets/css/style.css?version=<%= System.currentTimeMillis()%>">
            <style>
                body {
                    background-color: var(--bg-light);
                    background-image: url('data:image/svg+xml;charset=utf8,%3Csvg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1440 320"%3E%3Cpath fill="%23e83e8c" fill-opacity="0.05" d="M0,96L48,112C96,128,192,160,288,154.7C384,149,480,107,576,122.7C672,139,768,213,864,213.3C960,213,1056,139,1152,128C1248,117,1344,171,1392,197.3L1440,224L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z"%3E%3C/path%3E%3C/svg%3E');
                    background-size: cover;
                    background-position: center bottom;
                    background-repeat: no-repeat;
                    background-attachment: fixed;
                    font-family: 'Poppins', sans-serif;
                    min-height: 100vh;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    padding: 40px 20px;
                }

                .forgot-password-container {
                    max-width: 500px;
                    width: 100%;
                    margin: 0 auto;
                }

                .card-forgot {
                    border: none;
                    border-radius: var(--border-radius);
                    box-shadow: var(--box-shadow);
                    overflow: hidden;
                    background-color: rgba(255, 255, 255, 0.98);
                }

                .card-forgot-header {
                    background: linear-gradient(135deg, var(--primary-dark), var(--primary-color));
                    color: white;
                    text-align: center;
                    border-radius: var(--border-radius) var(--border-radius) 0 0 !important;
                    padding: 30px;
                    margin-bottom: 0;
                }

                .forgot-password-title {
                    font-weight: 700;
                    font-size: 2rem;
                    margin-bottom: 0;
                    color: white;
                }

                .card-forgot-body {
                    padding: 40px;
                }

                .form-control {
                    padding: 15px 20px;
                    border: 2px solid #f8c4d9;
                    border-radius: var(--border-radius);
                    transition: all 0.3s ease;
                    height: 55px;
                    font-size: 1.1rem;
                }

                .form-control:focus {
                    border-color: var(--primary-color);
                    box-shadow: var(--box-shadow-focus);
                    background-color: white;
                    transform: none;
                }

                .form-label {
                    font-weight: 600;
                    color: var(--text-medium);
                    margin-bottom: 10px;
                    font-size: 1rem;
                }

                .btn-primary {
                    background: linear-gradient(45deg, var(--primary-color), var(--primary-light));
                    border: none;
                    padding: 15px 30px;
                    font-weight: 600;
                    letter-spacing: 0.5px;
                    border-radius: var(--border-radius);
                    transition: all 0.3s ease;
                    height: 55px;
                    font-size: 1.1rem;
                    width: 100%;
                }

                .btn-primary:hover {
                    background: linear-gradient(45deg, var(--primary-dark), var(--primary-color));
                    transform: translateY(-3px);
                    box-shadow: var(--box-shadow-hover);
                }

                .btn-primary:focus {
                    box-shadow: var(--box-shadow-focus);
                }

                .form-group {
                    margin-bottom: 25px;
                }

                .back-to-login {
                    text-align: center;
                    margin-top: 20px;
                }

                .back-to-login a {
                    color: var(--primary-color);
                    text-decoration: none;
                    font-weight: 600;
                    transition: all 0.3s ease;
                }

                .back-to-login a:hover {
                    color: var(--primary-dark);
                    text-decoration: underline;
                }

                .instruction-text {
                    text-align: center;
                    margin-bottom: 30px;
                    color: var(--text-medium);
                }

                /* Animation */
                .card-forgot {
                    animation: fadeInUp 0.6s ease-out;
                }

                @keyframes fadeInUp {
                    from {
                        opacity: 0;
                        transform: translateY(30px);
                    }

                    to {
                        opacity: 1;
                        transform: translateY(0);
                    }
                }

                /* Mobile responsive */
                @media (max-width: 768px) {
                    .forgot-password-container {
                        max-width: 90%;
                    }

                    .card-forgot-body {
                        padding: 30px 20px;
                    }

                    .forgot-password-title {
                        font-size: 1.6rem;
                    }

                    .form-control {
                        font-size: 1rem;
                        height: 50px;
                    }

                    .btn-primary {
                        height: 50px;
                        font-size: 1rem;
                    }
                }

                @media (max-width: 576px) {
                    .card-forgot-header {
                        padding: 25px 15px;
                    }

                    .card-forgot-body {
                        padding: 25px 15px;
                    }

                    .forgot-password-title {
                        font-size: 1.4rem;
                    }
                }
            </style>
        </head>

        <body>
            <div class="container forgot-password-container">
                <div class="card-forgot animate__animated animate__fadeIn">
                    <div class="card-forgot-header">
                        <h3 class="forgot-password-title">
                            <i class="fas fa-key me-2"></i>
                            Forgot Password
                        </h3>
                    </div>
                    <div class="card-forgot-body">
                        <p class="instruction-text">
                            Enter your email address and we'll send you a verification code to reset your password.
                        </p>

                        <c:if test="${not empty error}">
                            <div class="alert alert-danger" role="alert">
                                <i class="fas fa-exclamation-circle me-2"></i>
                                ${error}
                                <c:if test="${not empty remainingTime}">
                                    <div class="mt-2">
                                        <small>You can request a new code in <span
                                                id="remaining-seconds">${remainingTime}</span> seconds.</small>
                                    </div>

                                    <script>
                                        document.addEventListener('DOMContentLoaded', function () {
                                            let timeLeft = <c:out value="${remainingTime}" />;
                                            const secondsSpan = document.getElementById('remaining-seconds');

                                            function updateTimer() {
                                                secondsSpan.textContent = timeLeft;

                                                if (timeLeft <= 0) {
                                                    clearInterval(timerInterval);
                                                    location.reload();
                                                } else {
                                                    timeLeft--;
                                                }
                                            }

                                            // Update the timer every second
                                            const timerInterval = setInterval(updateTimer, 1000);
                                        });
                                    </script>
                                </c:if>
                            </div>
                        </c:if>

                        <form action="${pageContext.request.contextPath}/forgot-password" method="post">
                            <div class="form-group">
                                <label for="email" class="form-label">Email Address</label>
                                <input type="email" class="form-control" id="email" name="email" required
                                    placeholder="Enter your registered email">
                            </div>

                            <div class="form-group">
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-paper-plane me-2"></i>Send Reset Code
                                </button>
                            </div>
                        </form>

                        <div class="back-to-login">
                            <a href="${pageContext.request.contextPath}/login">
                                <i class="fas fa-arrow-left me-1"></i> Back to Login
                            </a>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Bootstrap JS -->
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
        </body>

        </html>