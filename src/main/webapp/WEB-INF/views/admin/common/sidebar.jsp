<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="admin-sidebar">
    <div class="navbar-brand d-flex align-items-center">
        LightHouse Admin
        <button id="closeSidebarBtn" class="btn btn-sm d-lg-none">
            <i class="fas fa-times"></i>
        </button>
    </div>

    <hr class="text-white-50">
    <ul class="nav flex-column">
        <li class="nav-item">
            <a class="nav-link ${activeMenu eq 'dashboard' ? 'active' : ''}"
               href="${pageContext.request.contextPath}/admin/dashboard">
                <i class="fas fa-tachometer-alt"></i> Dashboard
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link ${activeMenu eq 'users' ? 'active' : ''}"
               href="${pageContext.request.contextPath}/admin/users">
                <i class="fas fa-users"></i> Manage Users
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link ${activeMenu eq 'courses' ? 'active' : ''}"
               href="${pageContext.request.contextPath}/admin/courses">
                <i class="fas fa-book"></i> Manage Courses
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link ${activeMenu eq 'categories' ? 'active' : ''}"
               href="${pageContext.request.contextPath}/admin/categories">
                <i class="fas fa-tags"></i> Manage Categories
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link ${activeMenu eq 'ratings' ? 'active' : ''}"
               href="${pageContext.request.contextPath}/admin/rating">
                <i class="fas fa-chalkboard-teacher"></i> Manage Reviews
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link ${activeMenu eq 'refunds' ? 'active' : ''}"
               href="${pageContext.request.contextPath}/admin/refunds">
                <i class="fas fa-undo"></i> Manage Refunds
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link ${activeMenu eq 'statistics' ? 'active' : ''}"
               href="${pageContext.request.contextPath}/admin/statistics">
                <i class="fas fa-chart-line"></i> Statistics
            </a>
        </li>
        <li class="nav-item mt-5">
            <a class="nav-link text-danger" href="${pageContext.request.contextPath}/logout">
                <i class="fas fa-sign-out-alt"></i> Logout
            </a>
        </li>
    </ul>
</div>
