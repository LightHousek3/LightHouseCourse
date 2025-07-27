<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>

    <head>
        <title>Verification Code - LightHouse</title>
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
                min-height: 100vh;
                display: flex;
                align-items: center;
                justify-content: center;
                padding: 40px 20px;
                overflow: hidden;
            }

            .verify-container {
                max-width: 500px;
                width: 100%;
                margin: 0 auto;
            }

            .card-verfy {
                border: none;
                border-radius: var(--border-radius);
                box-shadow: var(--box-shadow);
                overflow: hidden;
                background-color: rgba(255, 255, 255, 0.98);
            }

            .card-verfy-header {
                background: linear-gradient(135deg, var(--primary-dark), var(--primary-color));
                color: white;
                text-align: center;
                border-radius: var(--border-radius) var(--border-radius) 0 0 !important;
                padding: 30px;
                margin-bottom: 0;
            }

            .verify-title {
                font-weight: 700;
                font-size: 2rem;
                margin-bottom: 0;
                color: white;
            }

            .card-verfy-body {
                padding: 40px;
            }

            .form-control {
                padding: 15px 20px;
                border: 2px solid #f8c4d9;
                border-radius: var(--border-radius);
                transition: all 0.3s ease;
                height: 55px;
                font-size: 1.5rem;
                text-align: center;
                font-weight: 600;
                letter-spacing: 8px;
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
                text-align: center;
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

            .expiration-notice {
                text-align: center;
            }

            .expiration-notice small {
                color: var(--text-medium);
                font-size: 0.9rem;
            }

            .resend-container button {
                font-size: 0.9rem;
                color: var(--text-medium);
            }

            .timer {
                font-size: 1.2rem;
                font-weight: 700;
                color: var(--primary-color);
            }

            .resend-container {
                text-align: center;
            }

            .resend-form span {
                display: inline-block;
                font-size: 0.9rem !important;
            }

            .success-icon {
                font-size: 4rem;
                color: #28a745;
                margin-bottom: 20px;
                animation: pulse 2s infinite;
            }

            @keyframes pulse {
                0% {
                    transform: scale(1);
                }

                50% {
                    transform: scale(1.1);
                }

                100% {
                    transform: scale(1);
                }
            }

            /* Animation */
            .card-verfy {
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
                .verify-container {
                    max-width: 90%;
                }

                .card-verfy-body {
                    padding: 30px 20px;
                }

                .verify-title {
                    font-size: 1.6rem;
                }

                .form-control {
                    font-size: 1.2rem;
                    height: 50px;
                    letter-spacing: 5px;
                }

                .btn-primary {
                    height: 50px;
                    font-size: 1rem;
                }
            }

            @media (max-width: 576px) {
                .card-verfy-header {
                    padding: 25px 15px;
                }

                .card-verfy-body {
                    padding: 25px 15px;
                }

                .verify-title {
                    font-size: 1.4rem;
                }
            }
        </style>
    </head>

    <body>
        <div class="container verify-container">
            <div class="card-verfy animate__animated animate__fadeIn">
                <div class="card-verfy-header">
                    <h3 class="verify-title">
                        <i class="fas fa-shield-alt me-2"></i>
                        <c:choose>
                            <c:when test="${not empty sessionScope.resetEmail}">
                                Verify Reset Code
                            </c:when>
                            <c:otherwise>
                                Verify Your Email
                            </c:otherwise>
                        </c:choose>
                    </h3>
                </div>
                <div class="card-verfy-body">
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger" role="alert">
                            <i class="fas fa-exclamation-circle me-2"></i>
                            ${error}
                        </div>
                    </c:if>

                    <c:choose>
                        <%-- Show success message --%>
                        <c:when test="${not empty message}">
                            <div class="text-center">
                                <div class="success-icon">
                                    <i class="fas fa-check-circle"></i>
                                </div>
                                <div class="alert alert-success" role="alert">
                                    <i class="fas fa-check-circle me-2"></i>
                                    ${message}
                                </div>
                                <div class="mt-4">
                                    <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">
                                        <i class="fas fa-sign-in-alt me-2"></i>Go to Login
                                    </a>
                                </div>
                            </div>
                        </c:when>

                        <%-- Show verification form for password reset --%>
                        <c:when test="${not empty sessionScope.resetEmail}">
                            <p class="instruction-text">
                                We've sent a verification code to your email address. Please enter it below
                                to reset your password.
                            </p>
                            <form action="${pageContext.request.contextPath}/reset-password/verify"
                                  method="post">
                                <div class="form-group">
                                    <label for="code" class="form-label">Enter Verification Code</label>
                                    <input type="text" class="form-control" id="code" name="code" required
                                           placeholder="000000" maxlength="6" pattern="[0-9]{6}"
                                           inputmode="numeric">
                                </div>

                                <div class="form-group d-flex justify-content-center">
                                    <button type="submit" class="btn btn-primary">
                                        <i class="fas fa-check-circle me-2"></i>Verify Code
                                    </button>
                                </div>
                            </form>

                            <div class="d-flex justify-content-between align-items-center">
                                <div class="expiration-notice">
                                    <small>
                                        <i class="fas fa-clock me-1"></i>Code expires in 10 minutes
                                    </small>
                                </div>
                                <div class="resend-container">
                                    <form action="${pageContext.request.contextPath}/reset-password"
                                          method="post" class="resend-form">
                                        <button type="submit" id="resend-button"
                                                class="btn d-inline-flex justify-content-center align-items-center"
                                                disabled>
                                            <i class="fas fa-sync-alt me-1"></i>
                                            <div id="countdown-timer" class="timer">
                                                <span id="minutes">01</span>:<span id="seconds">00</span>
                                            </div>
                                        </button>
                                    </form>
                                </div>
                            </div>
                        </c:when>

                        <%-- Show verification form for registration --%>
                        <c:otherwise>
                            <p class="instruction-text">
                                We've sent a verification code to your email address. Please enter it
                                below to verify your account.
                            </p>
                            <form action="${pageContext.request.contextPath}/verify" method="post">
                                <div class="form-group">
                                    <label for="code" class="form-label">Enter Verification Code</label>
                                    <input type="text" class="form-control" id="code" name="code"
                                           required placeholder="000000" maxlength="6" pattern="[0-9]{6}"
                                           inputmode="numeric">
                                </div>

                                <div class="form-group d-flex justify-content-center">
                                    <button type="submit" class="btn btn-primary">
                                        <i class="fas fa-check-circle me-2"></i>Verify Code
                                    </button>
                                </div>
                            </form>

                            <div class="expiration-notice text-center mt-3">
                                <small>
                                    <i class="fas fa-clock me-1"></i>Code expires in 10 minutes
                                </small>
                            </div>
                        </c:otherwise>
                    </c:choose>

                    <c:if test="${empty message}">
                        <div class="back-to-login">
                            <a href="${pageContext.request.contextPath}/login">
                                <i class="fas fa-arrow-left me-1"></i> Back to Login
                            </a>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>

        <!-- Bootstrap JS -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>

        <!-- Countdown Timer Script -->
        <c:if test="${not empty sessionScope.resetEmail && empty message}">
            <script>
                document.addEventListener('DOMContentLoaded', function () {
                    const minutesSpan = document.getElementById('minutes');
                    const secondsSpan = document.getElementById('seconds');
                    const resendButton = document.getElementById('resend-button');

                    // Get remaining time from server or stored in session
                    let timeLeft;

                    // If server provided a new remaining time, use it and update storage
                <c:if test="${not empty remainingTime}">
                    timeLeft = <c:out value="${remainingTime}" />;
                    sessionStorage.setItem('resetCodeTimeLeft', timeLeft);
                </c:if>

                    // If no server time but we have stored time, use that
                    if (typeof timeLeft === 'undefined') {
                        const storedTime = sessionStorage.getItem('resetCodeTimeLeft');
                        if (storedTime && parseInt(storedTime) > 0) {
                            timeLeft = parseInt(storedTime);
                        } else {
                            // Default fallback
                            timeLeft = 60;
                        }
                    }

                    function updateTimer() {
                        const minutes = Math.floor(timeLeft / 60);
                        const seconds = timeLeft % 60;

                        minutesSpan.textContent = minutes.toString().padStart(2, '0');
                        secondsSpan.textContent = seconds.toString().padStart(2, '0');

                        if (timeLeft <= 0) {
                            clearInterval(timerInterval);
                            resendButton.disabled = false;
                            minutesSpan.textContent = '00';
                            secondsSpan.textContent = '00';
                            sessionStorage.removeItem('resetCodeTimeLeft');
                        } else {
                            timeLeft--;
                            sessionStorage.setItem('resetCodeTimeLeft', timeLeft);
                        }
                    }

                    // Initial call to set up the display
                    updateTimer();

                    // Update the timer every second
                    const timerInterval = setInterval(updateTimer, 1000);

                    // Focus on the code input field
                    document.getElementById('code').focus();
                });
            </script>
        </c:if>
    </body>

</html>