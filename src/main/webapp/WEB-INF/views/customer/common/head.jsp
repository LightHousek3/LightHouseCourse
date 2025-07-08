<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- Meta Tags -->
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
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css?version=<%= System.currentTimeMillis() %>">

<!-- Additional Styles for UI Consistency -->
<style>
    /* Fix Categories dropdown hover */
    .nav-item.dropdown:hover .dropdown-menu {
        display: block;
        opacity: 1;
        visibility: visible;
        transform: translateY(0);
    }
    
    .nav-item.dropdown .dropdown-menu {
        display: block;
        opacity: 0;
        visibility: hidden;
        transform: translateY(20px);
        transition: all 0.3s ease;
    }
    
    .dropdown-toggle::after {
        margin-left: 0;
    }
    
    /* Search button styling */
    .search-btn {
        background-color: var(--primary-color);
        color: white;
        border: none;
        padding: 0.5rem 1.5rem;
        border-radius: var(--border-radius);
        transition: all 0.3s ease;
        box-shadow: var(--box-shadow);
    }
    
    .search-btn:hover {
        background-color: var(--primary-dark);
        transform: translateY(-2px);
        box-shadow: var(--box-shadow-hover);
    }
</style>

<script>
    // Set the context path for JavaScript use
    window.contextPath = '${pageContext.request.contextPath}';
</script> 