<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- Footer -->
<footer class="py-5">
    <div class="container">
        <div class="row">
            <div class="col-lg-4 mb-4 mb-lg-0">
                <h5 class="text-white mb-3">
                    <!-- SVG Graduation Cap Logo -->
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" class="me-2" style="fill: white;">
                        <path d="M12 3L1 9l4 2.18v6L12 21l7-3.82v-6l2-1.09V17h2V9L12 3m6.82 6L12 12.72 5.18 9 12 5.28 18.82 9M17 16l-5 2.72L7 16v-3.73L12 15l5-2.73V16z"/>
                    </svg>
                    LightHouse
                </h5>
                <p class="text-white-50">An online learning platform that illuminates your path to success with a wide range of courses to help you achieve your personal and professional goals.</p>
            </div>
            <div class="col-lg-2 col-md-4 mb-4 mb-md-0">
                <h5 class="text-white mb-3">Categories</h5>
                <ul class="list-unstyled">
                    <c:forEach var="category" items="${categories}" begin="0" end="4">
                        <li class="mb-2"><a href="${pageContext.request.contextPath}/home?category=${category.categoryID}" class="text-white-50">${category.name}</a></li>
                    </c:forEach>
                </ul>
            </div>
            <div class="col-lg-4 col-md-4">
                <h5 class="text-white mb-3">Contact Us</h5>
                <ul class="list-unstyled">
                    <li class="mb-2 text-white-50"><i class="fas fa-map-marker-alt me-2"></i>123 Education St, Learning City</li>
                    <li class="mb-2 text-white-50"><i class="fas fa-phone me-2"></i>(123) 456-7890</li>
                    <li class="mb-2 text-white-50"><i class="fas fa-envelope me-2"></i>info@lighthouse.edu</li>
                </ul>
                <div class="mt-3">
                    <a href="#" class="text-white me-3"><i class="fab fa-facebook-f"></i></a>
                    <a href="#" class="text-white me-3"><i class="fab fa-twitter"></i></a>
                    <a href="#" class="text-white me-3"><i class="fab fa-instagram"></i></a>
                    <a href="#" class="text-white"><i class="fab fa-linkedin-in"></i></a>
                </div>
            </div>
        </div>
        <hr class="mt-4 mb-4" style="border-color: rgba(255,255,255,0.2);">
            <div class="row align-items-center">
                <div class="col-md-6 text-center text-md-start">
                    <p class="text-white-50 mb-0">&copy; 2025 LightHouse Learning. All rights reserved.</p>
                </div>
                <div class="col-md-6 text-center text-md-end">
                    <a href="#" class="text-white-50 me-3">Privacy Policy</a>
                    <a href="#" class="text-white-50 me-3">Terms of Service</a>
                    <a href="#" class="text-white-50">Cookie Policy</a>
                </div>
            </div>
    </div>
</footer> 