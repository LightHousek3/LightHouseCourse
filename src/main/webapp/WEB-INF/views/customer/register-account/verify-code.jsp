<%-- 
    Document   : verify-code
    Created on : Jul 12, 2025, 8:18:33 PM
    Author     : NhiDTY-CE180492
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Verify Your Email</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        :root {
            --primary-color: #e83e8c;
            --primary-dark: #c73373;
            --primary-light: #f592b8;
            --bg-light: #f8f9fa;
            --text-medium: #6c757d;
            --border-radius: 12px;
            --box-shadow: 0 10px 30px rgba(232, 62, 140, 0.1);
            --box-shadow-hover: 0 15px 40px rgba(232, 62, 140, 0.2);
            --box-shadow-focus: 0 0 0 0.2rem rgba(232, 62, 140, 0.25);
        }

        body {
            background-color: var(--bg-light);
            background-image: url('data:image/svg+xml;charset=utf8,%3Csvg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1440 320"%3E%3Cpath fill="%23e83e8c" fill-opacity="0.05" d="M0,96L48,112C96,128,192,160,288,154.7C384,149,480,107,576,122.7C672,139,768,213,864,213.3C960,213,1056,139,1152,128C1248,117,1344,171,1392,197.3L1440,224L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z"%3E%3C/path%3E%3C/svg%3E');
            background-size: cover;
            background-position: center bottom;
            background-repeat: no-repeat;
            background-attachment: fixed;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 40px 20px;
        }

        .verify-container {
            max-width: 600px;
            width: 100%;
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

        .card-body {
            padding: 40px;
        }

        .form-control {
            padding: 15px 20px;
            border: 2px solid #f8c4d9;
            border-radius: var(--border-radius);
            transition: all 0.3s ease;
            height: 55px;
            font-size: 1.1rem;
            text-align: center;
            font-weight: 500;
            width: 100%;
            max-width: 300px;
        }

        .input-container {
            display: flex;
            justify-content: center;
            align-items: center;
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
            margin-bottom: 20px;
            font-size: 1.2rem;
            text-align: center;
            display: block;
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
            max-width: 200px;
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
            margin-bottom: 30px;
        }

        .button-container {
            display: flex;
            justify-content: center;
            margin-bottom: 30px;
        }

        .expiration-notice {
            text-align: center;
            margin-top: 20px;
        }

        .expiration-notice small {
            color: var(--text-medium);
            font-size: 1rem;
        }

        /* Animation */
        .card {
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
            
            .card-body {
                padding: 30px 20px;
            }
            
            .verify-title {
                font-size: 1.6rem;
            }
            
            .form-control {
                max-width: 100%;
                font-size: 1rem;
                height: 50px;
            }
            
            .btn-primary {
                max-width: 100%;
                height: 50px;
                font-size: 1rem;
            }
        }

        @media (max-width: 576px) {
            .card-header {
                padding: 25px 15px;
            }
            
            .card-body {
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
    <div class="card animate__animated animate__fadeIn">
        <div class="card-header">
            <h3 class="verify-title">
                <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" class="me-2" style="fill: white;">
                    <path d="M12 1L3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4zm0 10.99h7c-.53 4.12-3.28 7.79-7 8.93V12H5V6.3l7-3.11V12z"/>
                </svg>
                Verify Your Email
            </h3>
        </div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/verify" method="post">
                <div class="form-group">
                    <label for="code" class="form-label">Enter verification code</label>
                    <div class="input-container">
                        <input type="text" class="form-control" id="code" name="code" required placeholder="Enter your code here">
                    </div>
                </div>
                
                <div class="button-container">
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-check me-2"></i>Verify
                    </button>
                </div>
            </form>
            
            <div class="expiration-notice">
                <small class="text-muted">
                    <i class="fas fa-clock me-1"></i>Code expires in 10 minutes
                </small>
            </div>
        </div>
    </div>
</div>
</body>
</html>