<%-- 
    Document   : 500
    Created on : Jun 15, 2025, 5:00:39 PM
    Author     : DangPH - CE180896
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Server Error | LightHouse Course</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            body {
                background-color: #f8f9fa;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            }

            .container {
                display: flex;
                justify-content: center;
                align-items: center;
                height: 100vh;
            }

            .error-container {
                text-align: center;
            }

            .error-title {
                font-size: 36px;
                margin-top: 0;
                margin-bottom: 20px;
                color: #343a40;
            }

            .error-message {
                font-size: 18px;
                margin-bottom: 30px;
                color: #6c757d;
                max-width: 600px;
                margin-left: auto;
                margin-right: auto;
            }

            .btn-primary {
                background-color: #0d6efd;
                border-color: #0d6efd;
                padding: 10px 24px;
                font-size: 16px;
            }

            .btn-primary:hover {
                background-color: #0b5ed7;
                border-color: #0a58ca;
            }

        </style>
    </head>

    <body>
        <div class="container">
            <div class="error-container">
                <img src="${pageContext.request.contextPath}/assets/imgs/errors/500.png" alt="Lighthouse-500"
                     class="lighthouse-img">
                <h2 class="error-title">Server Error</h2>
                <p class="error-message">
                    Oops! Something went wrong on our end. Our team has been notified and we're working to fix the
                    issue.
                    Please try again later or contact support if the problem persists.
                </p>
                <div class="d-flex justify-content-center gap-3">
                    <button onclick="window.history.back()" class="btn btn-outline-secondary">Go Back</button>
                </div>
            </div>
        </div>
    </body>

</html>