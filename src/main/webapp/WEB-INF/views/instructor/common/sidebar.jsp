<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="instructor-sidebar">
    <div class="navbar-brand d-flex align-items-center">
        LightHouse Instructor
        <button id="closeSidebarBtn" class="btn btn-sm d-lg-none">
            <i class="fas fa-times"></i>
        </button>
    </div>

    <hr class="text-white-50">
    <ul class="nav flex-column">
        <li class="nav-item">
            <a class="nav-link ${activeMenu eq 'dashboard' ? 'active' : ''}"
               href="${pageContext.request.contextPath}/instructor/dashboard">
                <i class="fas fa-tachometer-alt"></i> Dashboard
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link ${activeMenu eq 'courses' ? 'active' : ''}"
               href="${pageContext.request.contextPath}/instructor/courses">
                <i class="fas fa-book"></i> Manage Courses
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link ${activeMenu eq 'students' ? 'active' : ''}"
               href="${pageContext.request.contextPath}/instructor/students">
                <i class="fa-solid fa-user-graduate"></i> Manage Students
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link ${activeMenu eq 'discussions' ? 'active' : ''}"
               href="${pageContext.request.contextPath}/instructor/discussions">
                <i class="fas fa-comments"></i> Manage Discussions
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link ${activeMenu eq 'reviews' ? 'active' : ''}"
               href="${pageContext.request.contextPath}/instructor/reviews">
                <i class="fas fa-chalkboard-teacher"></i> View Reviews
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link ${activeMenu eq 'statistics' ? 'active' : ''}"
               href="${pageContext.request.contextPath}/instructor/statistics">
                <i class="fas fa-chart-line"></i> Statistics
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link ${activeMenu eq 'profile' ? 'active' : ''}"
               href="${pageContext.request.contextPath}/instructor/profile">
                <i class="fas fa-user-circle"></i> My Profile
            </a>
        </li>
        <li class="nav-item mt-5">
            <a class="nav-link text-danger" href="${pageContext.request.contextPath}/logout">
                <i class="fas fa-sign-out-alt"></i> Logout
            </a>
        </li>
    </ul>
</div>