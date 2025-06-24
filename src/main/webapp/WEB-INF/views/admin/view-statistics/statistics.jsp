<%-- 
    Document   : statistics
    Created on : Jun 16, 2025, 10:35:11 PM
    Author     : DangPH - CE180896
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">

    <head>
        <jsp:include page="../common/head.jsp" />
        <!-- Chart.js -->
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

        <style>
            .btn-outline-primary {
                background: #fff !important;
            }
            .statistics-card {
                box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
                margin-bottom: 30px;
                transition: transform 0.3s;
            }

            .statistics-card:hover {
                box-shadow: 0 10px 20px rgba(0, 0, 0, 0.15);
            }

            .statistics-card-header {
                background: linear-gradient(135deg, #3a8ffe 0%, #9658fe 100%);
                color: white;
                padding: 20px;
                font-weight: 600;
                display: flex;
                justify-content: space-between;
                align-items: center;
            }

            .fa-shopping-cart {
                color: #ffe317;
            }

            .fa-star {
                color: #f0f01c;
            }

            .chart-container {
                padding: 20px;
                height: 400px;
                position: relative;
            }

            .filter-controls {
                background-color: #f8f9fa;
                border-bottom: 1px solid #e9ecef;
                padding: 15px;
                margin-bottom: 20px;
                border-radius: 8px;
            }

            .tab-control {
                display: flex;
                border-radius: 6px;
                overflow: hidden;
                border: 1px solid #dee2e6;
            }

            .tab-control button {
                flex: 1;
                border: none;
                background-color: #fff;
                padding: 8px 16px;
                cursor: pointer;
                transition: all 0.2s;
                border-right: 1px solid #dee2e6;
            }

            .tab-control button{
                border-radius: 0 !important;
                border: none !important;
            }

            .tab-control button.active {
                background-color: var(--primary-dark) !important;
                color: white !important;
            }

            .year-selector {
                width: auto;
                min-width: 120px;
                min-height: 40px;
            }

            @media (max-width: 768px) {
                .statistics-card {
                    margin-bottom: 20px;
                }

                .chart-container {
                    height: 300px;
                }

                .tab-control button {
                    padding: 6px 10px;
                    font-size: 0.9rem;
                }
            }
        </style>
    </head>

    <body>
        <!-- Admin Sidebar -->
        <c:set var="activeMenu" value="statistics" scope="request" />
        <jsp:include page="../common/sidebar.jsp" />

        <!-- Admin Content -->
        <div class="admin-content">
            <!-- Header -->
            <div class="admin-header d-flex justify-content-between align-items-center">
                <button class="btn d-lg-none" id="toggleSidebarBtn">
                    <i class="fas fa-bars"></i>
                </button>
                <h2 class="m-0 d-none d-lg-block">Statistics</h2>
                <!-- Filter Controls -->
                <div class="d-flex align-items-center">
                    <label for="yearSelect" class="me-2">Year:</label>
                    <select id="yearSelect" class="form-select year-selector">
                        <c:forEach var="year" items="${years}">
                            <option value="${year}" ${year eq currentYear ? 'selected' : '' }>${year}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>

            <!-- Statistics Content -->
            <div class="row">
                <!-- Purchase Statistics -->
                <div class="col-12">
                    <div class="statistics-card">
                        <div class="statistics-card-header btn-primary">
                            <h5 class="mb-0">
                                <i class="fas fa-shopping-cart me-2"></i>Course Purchase Statistics
                            </h5>
                            <div class="tab-control" id="purchaseViewControl">
                                <button class="active btn btn-md btn-outline-primary" data-view="yearly">Yearly</button>
                                <button class="btn btn-md btn-outline-primary" data-view="monthly">Monthly</button>
                            </div>
                        </div>
                        <div class="chart-container">
                            <canvas id="purchaseChart"></canvas>
                        </div>
                    </div>
                </div>

                <!-- Rating Statistics -->
                <div class="col-12">
                    <div class="statistics-card">
                        <div class="statistics-card-header btn-primary">
                            <h5 class="mb-0">
                                <i class="fas fa-star me-2"></i>Course Rating Statistics
                            </h5>
                            <div class="tab-control" id="ratingViewControl">
                                <button class="active btn btn-md btn-outline-primary" data-view="yearly">Yearly</button>
                                <button class="btn btn-md btn-outline-primary" data-view="monthly">Monthly</button>
                            </div>
                        </div>
                        <div class="chart-container">
                            <canvas id="ratingChart"></canvas>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <jsp:include page="../common/scripts.jsp" />

        <script>
            // Global variables to store chart instances
            let purchaseChart = null;
            let ratingChart = null;

            // Current view state
            let currentState = {
                year: ${ currentYear },
                purchaseView: 'yearly',
                ratingView: 'yearly'
            };

            // Function to fetch data and create charts
            document.addEventListener('DOMContentLoaded', function () {
                // Set up event listeners
                setupEventListeners();

                // Initial load of charts
                loadPurchaseChart();
                loadRatingChart();
            });

            // Set up event listeners
            function setupEventListeners() {
                // Year selector
                document.getElementById('yearSelect').addEventListener('change', function () {
                    currentState.year = this.value;
                    loadPurchaseChart();
                    loadRatingChart();
                });

                // Purchase view controls
                const purchaseButtons = document.querySelectorAll('#purchaseViewControl button');
                purchaseButtons.forEach(button => {
                    button.addEventListener('click', function () {
                        purchaseButtons.forEach(btn => btn.classList.remove('active'));
                        this.classList.add('active');
                        currentState.purchaseView = this.getAttribute('data-view');
                        loadPurchaseChart();
                    });
                });

                // Rating view controls
                const ratingButtons = document.querySelectorAll('#ratingViewControl button');
                ratingButtons.forEach(button => {
                    button.addEventListener('click', function () {
                        ratingButtons.forEach(btn => btn.classList.remove('active'));
                        this.classList.add('active');
                        currentState.ratingView = this.getAttribute('data-view');
                        loadRatingChart();
                    });
                });
            }

            // Load purchase chart based on current state
            function loadPurchaseChart() {
                const url = currentState.purchaseView === 'yearly'
                        ? '${pageContext.request.contextPath}/admin/statistics/purchase?year=' + currentState.year
                        : '${pageContext.request.contextPath}/admin/statistics/purchase/monthly?year=' + currentState.year;

                fetch(url)
                        .then(response => response.json())
                        .then(data => createPurchaseChart(data, currentState.purchaseView))
                        .catch(error => console.error('Error fetching purchase data:', error));
            }

            // Load rating chart based on current state
            function loadRatingChart() {
                const url = currentState.ratingView === 'yearly'
                        ? '${pageContext.request.contextPath}/admin/statistics/rating?year=' + currentState.year
                        : '${pageContext.request.contextPath}/admin/statistics/rating/monthly?year=' + currentState.year;

                fetch(url)
                        .then(response => response.json())
                        .then(data => createRatingChart(data, currentState.ratingView))
                        .catch(error => console.error('Error fetching rating data:', error));
            }

            // Create Purchase Chart
            function createPurchaseChart(data, viewType) {
                const ctx = document.getElementById('purchaseChart').getContext('2d');

                // Destroy existing chart if it exists
                if (purchaseChart) {
                    purchaseChart.destroy();
                }

                const chartConfig = {
                    type: viewType === 'yearly' ? 'bar' : 'line',
                    data: viewType === 'yearly' ? {
                        labels: data.labels,
                        datasets: [{
                                label: 'Number of Purchases',
                                data: data.data,
                                backgroundColor: 'rgba(54, 162, 235, 0.6)',
                                borderColor: 'rgba(54, 162, 235, 1)',
                                borderWidth: 1
                            }]
                    } : {
                        labels: data.labels,
                        datasets: data.datasets
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: {
                                display: true,
                                position: 'top'
                            },
                            title: {
                                display: true,
                                text: 'Course Purchase Count - ' + currentState.year + (viewType === 'monthly' ? ' (Monthly)' : '')
                            }
                        },
                        scales: {
                            y: {
                                beginAtZero: true,
                                ticks: {
                                    precision: 0 // Only show integer values
                                }
                            }
                        }
                    }
                };

                // For line charts in monthly view
                if (viewType === 'monthly') {
                    chartConfig.options.elements = {
                        line: {
                            tension: 0.3 // Smooth curves
                        }
                    };
                }

                purchaseChart = new Chart(ctx, chartConfig);
            }

            // Create Rating Chart
            function createRatingChart(data, viewType) {
                const ctx = document.getElementById('ratingChart').getContext('2d');

                // Destroy existing chart if it exists
                if (ratingChart) {
                    ratingChart.destroy();
                }

                const chartConfig = {
                    type: viewType === 'yearly' ? 'bar' : 'line',
                    data: viewType === 'yearly' ? {
                        labels: data.labels,
                        datasets: [{
                                label: 'Average Rating',
                                data: data.data,
                                backgroundColor: 'rgba(255, 159, 64, 0.6)',
                                borderColor: 'rgba(255, 159, 64, 1)',
                                borderWidth: 1
                            }]
                    } : {
                        labels: data.labels,
                        datasets: data.datasets
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: {
                                display: true,
                                position: 'top'
                            },
                            title: {
                                display: true,
                                text: 'Course Average Ratings - ' + currentState.year + (viewType === 'monthly' ? ' (Monthly)' : '')
                            }
                        },
                        scales: {
                            y: {
                                beginAtZero: true,
                                max: 5, // Rating scale from 0 to 5
                                ticks: {
                                    stepSize: 1
                                }
                            }
                        }
                    }
                };

                // For line charts in monthly view
                if (viewType === 'monthly') {
                    chartConfig.options.elements = {
                        line: {
                            tension: 0.3 // Smooth curves
                        }
                    };
                }

                ratingChart = new Chart(ctx, chartConfig);
            }
        </script>
    </body>

</html>
