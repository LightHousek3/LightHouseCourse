<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
            <!DOCTYPE html>
            <html lang="en">

            <head>
                <title>Shopping Cart - LightHouse</title>
                <!-- Include common header resources -->
                <jsp:include page="/WEB-INF/views/customer/common/head.jsp" />
                <style>
                    .header-content {
                        position: relative;
                        z-index: 1;
                    }

                    .header-title {
                        font-weight: 700;
                        text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
                    }

                    .cart-item {
                        transition: all 0.3s ease;
                        border: none;
                        border-radius: var(--border-radius);
                        overflow: hidden;
                        box-shadow: var(--box-shadow);
                    }

                    .cart-item:hover {
                        transform: translateY(-5px);
                        box-shadow: var(--box-shadow-hover);
                    }

                    .cart-img {
                        width: 120px;
                        height: 80px;
                        object-fit: cover;
                        border-radius: var(--border-radius);
                        transition: transform 0.5s ease;
                        box-shadow: var(--box-shadow);
                    }

                    .cart-item:hover .cart-img {
                        transform: scale(1.05);
                    }

                    .cart-summary {
                        background-color: rgba(255, 255, 255, 0.98);
                        border-radius: var(--border-radius);
                        padding: 25px;
                        border: none;
                        box-shadow: var(--box-shadow);
                        transition: all 0.3s ease;
                    }

                    .cart-summary:hover {
                        box-shadow: var(--box-shadow-hover);
                        transform: translateY(-5px);
                    }

                    .empty-cart {
                        text-align: center;
                        padding: 50px 0;
                    }

                    .empty-cart i {
                        font-size: 5rem;
                        color: var(--primary-light);
                        margin-bottom: 20px;
                    }

                    .card-header {
                        background: linear-gradient(135deg, var(--primary-dark), var(--primary-color));
                        color: white;
                        border: none;
                    }

                    .btn-primary {
                        background: linear-gradient(45deg, var(--primary-color), var(--primary-light));
                        border: none;
                        padding: 0.6rem 1.5rem;
                        font-weight: 600;
                        letter-spacing: 0.5px;
                        box-shadow: var(--box-shadow);
                        transition: all 0.3s ease;
                    }

                    .btn-primary:hover {
                        background: linear-gradient(45deg, var(--primary-dark), var(--primary-color));
                        transform: translateY(-3px);
                        box-shadow: var(--box-shadow-hover);
                    }

                    .btn-outline-primary {
                        color: var(--primary-color);
                        border-color: var(--primary-color);
                        transition: all 0.3s ease;
                    }

                    .btn-outline-primary:hover {
                        background-color: var(--primary-color);
                        color: white;
                        transform: translateY(-3px);
                        box-shadow: var(--box-shadow-hover);
                    }

                    .btn-outline-danger {
                        color: var(--danger);
                        border-color: var(--danger);
                        transition: all 0.3s ease;
                    }

                    .btn-outline-danger:hover {
                        background-color: var(--danger);
                        color: white;
                        transform: translateY(-3px);
                        box-shadow: 0 5px 15px rgba(250, 82, 82, 0.3);
                    }

                    .text-primary {
                        color: var(--primary-color) !important;
                    }

                    .text-success {
                        color: var(--primary-color) !important;
                        font-size: 1.2rem;
                    }

                    .category-badge {
                        background-color: rgba(232, 62, 140, 0.1);
                        color: var(--primary-color);
                        border-radius: 20px;
                        padding: 5px 12px;
                        margin-right: 5px;
                        margin-bottom: 5px;
                        display: inline-block;
                        font-size: 0.8rem;
                        font-weight: 500;
                        transition: all 0.3s ease;
                    }

                    .category-badge:hover {
                        background-color: var(--primary-color);
                        color: white;
                        transform: translateY(-2px);
                    }

                    .price-vnd {
                        display: inline-block;
                        color: #f52991;
                        font-weight: bold;
                        font-size: 1.2rem;
                    }

                    /* Custom checkbox styling */
                    .custom-checkbox-container {
                        display: flex;
                        align-items: center;
                        cursor: pointer;
                    }

                    .custom-checkbox-container .form-check-input {
                        width: 22px;
                        height: 22px;
                        cursor: pointer;
                        margin-right: 8px;
                    }

                    .select-all-container {
                        display: flex;
                        align-items: center;
                        margin-bottom: 15px;
                        padding: 15px;
                        background-color: rgba(232, 62, 140, 0.05);
                        border-radius: var(--border-radius);
                    }

                    .select-all-label {
                        font-weight: 600;
                        margin-left: 8px;
                        cursor: pointer;
                    }

                    .cart-item.selected {
                        border-left: 4px solid var(--primary-color);
                        background-color: rgba(232, 62, 140, 0.05);
                    }

                    .price-section {
                        transition: all 0.3s ease;
                    }

                    .strike-through {
                        text-decoration: line-through;
                        color: #999;
                        font-size: 0.9rem;
                    }

                    /* Price highlight animation */
                    @keyframes priceHighlight {
                        0% {
                            transform: scale(1);
                        }

                        50% {
                            transform: scale(1.1);
                            color: var(--primary-color);
                        }

                        100% {
                            transform: scale(1);
                        }
                    }

                    .price-highlight {
                        animation: priceHighlight 0.5s ease;
                    }

                    /* Disabled button styling */
                    .btn-disabled {
                        background: #cccccc !important;
                        cursor: not-allowed;
                        opacity: 0.7;
                        box-shadow: none !important;
                    }

                    .btn-disabled:hover {
                        transform: none !important;
                    }
                </style>
            </head>

            <body>
                <!-- Include navigation -->
                <jsp:include page="/WEB-INF/views/customer/common/navigation.jsp" />

                <!-- Page Header -->
                <header class="page-header">
                    <div class="floating-element"><i class="fas fa-graduation-cap"></i></div>
                    <div class="floating-element"><i class="fas fa-book"></i></div>
                    <div class="floating-element"><i class="fas fa-credit-card"></i></div>
                    <div class="floating-element"><i class="fas fa-shopping-cart"></i></div>
                    <div class="floating-element"><i class="fas fa-shield-alt"></i></div>
                    <div class="container header-content">
                        <h1 class="display-4 header-title">Shopping Cart</h1>
                        <p class="lead">Review your selected courses before checkout</p>
                    </div>
                </header>

                <div class="container mb-5">
                    <c:if test="${not empty param.error or not empty error}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="fas fa-exclamation-circle me-2"></i>
                            <c:choose>
                                <c:when test="${param.error eq 'empty'}">
                                    Your cart is empty. Please add courses before proceeding to checkout.
                                </c:when>
                                <c:when test="${not empty error}">
                                    ${error}
                                </c:when>
                                <c:otherwise>
                                    An error occurred. Please try again.
                                </c:otherwise>
                            </c:choose>
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>

                    <c:choose>
                        <c:when test="${empty sessionScope.cart or empty sessionScope.cart.items}">
                            <!-- Empty Cart -->
                            <div class="empty-cart">
                                <i class="fas fa-shopping-cart"></i>
                                <h3>Your cart is empty</h3>
                                <p class="text-muted mb-4">Looks like you haven't added any courses to your cart yet.
                                </p>
                                <a href="${pageContext.request.contextPath}/home?scroll=true" class="btn btn-primary">
                                    <i class="fas fa-book-open me-2"></i>Browse Courses
                                </a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <!-- Cart with Items -->
                            <div class="row">
                                <!-- Cart Items -->
                                <div class="col-lg-8">
                                    <div class="card mb-4 shadow-sm">
                                        <div class="card-header">
                                            <h4 class="mb-0">Cart Items (${sessionScope.cart.itemCount})</h4>
                                        </div>
                                        <div class="card-body">
                                            <!-- Select All Checkbox -->
                                            <div class="select-all-container">
                                                <div class="custom-checkbox-container">
                                                    <input type="checkbox" id="select-all" class="form-check-input"
                                                        ${sessionScope.cart.selectedItemCount==sessionScope.cart.itemCount
                                                        ? 'checked' : '' }>
                                                    <label for="select-all" class="select-all-label">Select All</label>
                                                </div>
                                            </div>

                                            <form id="cart-form">
                                                <c:forEach var="item" items="${sessionScope.cart.items}"
                                                    varStatus="status">
                                                    <div class="card cart-item mb-3 ${item.selected ? 'selected' : ''}">
                                                        <div class="card-body">
                                                            <div class="row align-items-center">
                                                                <!-- Checkbox -->
                                                                <div class="col-md-1 col-2 mb-2 mb-md-0">
                                                                    <div class="custom-checkbox-container">
                                                                        <input type="checkbox" name="selectedCourses"
                                                                            value="${item.course.courseID}"
                                                                            class="form-check-input course-checkbox"
                                                                            id="course-${item.course.courseID}"
                                                                            data-course-id="${item.course.courseID}"
                                                                            data-price="${item.price}" ${item.selected
                                                                            ? 'checked' : '' }>
                                                                    </div>
                                                                </div>

                                                                <!-- Image -->
                                                                <div class="col-md-2 col-4 mb-2 mb-md-0">
                                                                    <img src="${pageContext.request.contextPath}/${item.course.imageUrl}"
                                                                        class="cart-img" alt="${item.course.name}">
                                                                </div>

                                                                <!-- Course Info -->
                                                                <div class="col-md-5 col-6 mb-2 mb-md-0">
                                                                    <h5>${item.course.name}</h5>
                                                                    <c:if test="${not empty item.course.instructors}">
                                                                        <p class="text-muted mb-0">
                                                                            <i
                                                                                class="fas fa-chalkboard-teacher me-1"></i>
                                                                            <c:forEach var="instructor"
                                                                                items="${item.course.instructors}"
                                                                                varStatus="status">
                                                                                ${instructor.name}<c:if
                                                                                    test="${!status.last}">, </c:if>
                                                                            </c:forEach>
                                                                        </p>
                                                                    </c:if>
                                                                    <div class="mt-1">
                                                                        <c:forEach var="category"
                                                                            items="${item.course.categories}">
                                                                            <span
                                                                                class="category-badge">${category.name}</span>
                                                                        </c:forEach>
                                                                    </div>
                                                                </div>

                                                                <!-- Price -->
                                                                <div
                                                                    class="col-md-2 col-6 text-md-center price-section">
                                                                    <span class="price-vnd">
                                                                        <fmt:formatNumber value="${item.price}"
                                                                            pattern="#,##0" />đ
                                                                    </span>
                                                                </div>

                                                                <!-- Actions -->
                                                                <div class="col-md-2 col-6 text-end">
                                                                    <a href="${pageContext.request.contextPath}/cart/remove?id=${item.course.courseID}"
                                                                        class="btn btn-outline-danger btn-sm">
                                                                        <i class="fas fa-trash-alt"></i> Remove
                                                                    </a>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </c:forEach>
                                            </form>
                                        </div>
                                        <div class="card-footer bg-white">
                                            <div class="d-flex justify-content-between">
                                                <a href="${pageContext.request.contextPath}/home?scroll=true"
                                                    class="btn btn-outline-primary">
                                                    <i class="fas fa-arrow-left me-2"></i> Continue Shopping
                                                </a>
                                                <a href="${pageContext.request.contextPath}/cart/clear"
                                                    class="btn btn-outline-danger">
                                                    <i class="fas fa-trash-alt me-2"></i> Clear Cart
                                                </a>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <!-- Cart Summary -->
                                <div class="col-lg-4">
                                    <div class="card cart-summary">
                                        <div class="card-body">
                                            <h4 class="card-title mb-4">Order Summary</h4>

                                            <div class="d-flex justify-content-between mb-3">
                                                <span>Total Items</span>
                                                <span id="total-item-count">${sessionScope.cart.itemCount}</span>
                                            </div>

                                            <div class="d-flex justify-content-between mb-3">
                                                <span>Selected Items</span>
                                                <span
                                                    id="selected-item-count">${sessionScope.cart.selectedItemCount}</span>
                                            </div>

                                            <div class="d-flex justify-content-between mb-3">
                                                <span>Subtotal</span>
                                                <span>
                                                    <span id="total-price"
                                                        class="${sessionScope.cart.selectedItemCount != sessionScope.cart.itemCount ? 'strike-through' : ''}">
                                                        <fmt:formatNumber value="${sessionScope.cart.totalPrice}"
                                                            pattern="#,##0" />đ
                                                    </span>
                                                </span>
                                            </div>

                                            <hr>

                                            <div class="d-flex justify-content-between mb-4 fw-bold">
                                                <span>Selected Total</span>
                                                <span class="price-vnd" id="selected-total-price">
                                                    <fmt:formatNumber value="${sessionScope.cart.selectedTotalPrice}"
                                                        pattern="#,##0" />đ
                                                </span>
                                            </div>

                                            <div class="d-grid gap-2">
                                                <button type="button" id="checkout-btn"
                                                    class="btn btn-primary btn-lg ${sessionScope.cart.selectedItemCount == 0 ? 'btn-disabled' : ''}">
                                                    <i class="fas fa-lock me-2"></i> Proceed to Checkout
                                                </button>
                                            </div>

                                            <div class="mt-4">
                                                <p class="text-muted small mb-2">
                                                    <i class="fas fa-shield-alt me-2"></i> Secure checkout
                                                </p>
                                                <p class="text-muted small mb-2">
                                                    <i class="fas fa-undo me-2"></i> 30-day money-back guarantee
                                                </p>
                                                <p class="text-muted small mb-0">
                                                    <i class="fas fa-infinity me-2"></i> Lifetime access to course
                                                    content
                                                </p>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Include footer -->
                <jsp:include page="/WEB-INF/views/customer/common/footer.jsp" />

                <!-- JavaScript for cart item selection -->
                <script>
                    document.addEventListener('DOMContentLoaded', function () {
                        // DOM elements
                        const selectAllCheckbox = document.getElementById('select-all');
                        const courseCheckboxes = document.querySelectorAll('.course-checkbox');
                        const selectedTotalPriceElement = document.getElementById('selected-total-price');
                        const selectedItemCountElement = document.getElementById('selected-item-count');
                        const totalPriceElement = document.getElementById('total-price');
                        const checkoutBtn = document.getElementById('checkout-btn');
                        const cartForm = document.getElementById('cart-form');

                        // Format price in VND
                        function formatPrice(price) {
                            return new Intl.NumberFormat('vi-VN').format(price) + 'đ';
                        }

                        // Function to handle checkout button click
                        function handleCheckoutClick() {
                            const selectedCount = document.querySelectorAll('.course-checkbox:checked').length;

                            if (selectedCount === 0) {
                                alert('Please select at least one course to checkout.');
                                return;
                            }

                            // Redirect to checkout page
                            window.location.href = '${pageContext.request.contextPath}/order/checkout';
                        }

                        // Function to manage checkout button event listener
                        function updateCheckoutButtonState(selectedCount) {
                            // Remove existing event listener first to prevent duplicates
                            checkoutBtn.removeEventListener('click', handleCheckoutClick);

                            if (selectedCount === 0) {
                                checkoutBtn.classList.add('btn-disabled');
                                // No event listener added when no items are selected
                            } else {
                                checkoutBtn.classList.remove('btn-disabled');
                                // Add event listener when items are selected
                                checkoutBtn.addEventListener('click', handleCheckoutClick);
                            }
                        }

                        // Update UI based on checkbox states
                        function updateUI() {
                            // Count selected items and calculate total price
                            let selectedCount = 0;
                            let selectedTotal = 0;

                            courseCheckboxes.forEach(checkbox => {
                                if (checkbox.checked) {
                                    selectedCount++;
                                    selectedTotal += parseFloat(checkbox.getAttribute('data-price'));

                                    // Update item styling
                                    const cartItem = checkbox.closest('.cart-item');
                                    cartItem.classList.add('selected');
                                } else {
                                    // Remove selected styling
                                    const cartItem = checkbox.closest('.cart-item');
                                    cartItem.classList.remove('selected');
                                }
                            });

                            // Update select all checkbox
                            selectAllCheckbox.checked = (selectedCount === courseCheckboxes.length && courseCheckboxes.length > 0);

                            // Update selected count display
                            selectedItemCountElement.textContent = selectedCount;

                            // Update selected total price with animation
                            selectedTotalPriceElement.textContent = formatPrice(selectedTotal);
                            selectedTotalPriceElement.classList.remove('price-highlight');
                            void selectedTotalPriceElement.offsetWidth; // Trigger reflow
                            selectedTotalPriceElement.classList.add('price-highlight');

                            // Update total price styling
                            if (selectedCount === courseCheckboxes.length) {
                                totalPriceElement.classList.remove('strike-through');
                            } else {
                                totalPriceElement.classList.add('strike-through');
                            }

                            // Update checkout button state and event listener
                            updateCheckoutButtonState(selectedCount);
                        }

                        // Handle server updates
                        function updateServer(url) {
                            return fetch(url, {
                                method: 'GET',
                                headers: {
                                    'Content-Type': 'application/json',
                                    'X-Requested-With': 'XMLHttpRequest'
                                }
                            })
                                .then(response => {
                                    if (!response.ok) {
                                        throw new Error('Network response was not ok');
                                    }
                                    return response.json();
                                });
                        }

                        // Handle individual course checkbox change
                        courseCheckboxes.forEach(checkbox => {
                            checkbox.addEventListener('change', function () {
                                const courseId = this.getAttribute('data-course-id');
                                const isChecked = this.checked;

                                // Update UI first for responsive feel
                                updateUI();

                                // Then update server
                                const url = `${pageContext.request.contextPath}/cart/${isChecked ? 'select' : 'deselect'}?id=${courseId}&ajax=true`;
                                updateServer(url).catch(error => {
                                    console.error('Error updating selection:', error);
                                    // No need to revert UI state as we'll refresh the page if there's an error
                                });
                            });
                        });

                        // Handle select all checkbox
                        selectAllCheckbox.addEventListener('change', function () {
                            const isChecked = this.checked;

                            // Update all checkboxes
                            courseCheckboxes.forEach(checkbox => {
                                checkbox.checked = isChecked;
                            });

                            // Update UI
                            updateUI();

                            // Update server
                            const url = `${pageContext.request.contextPath}/cart/${isChecked ? 'select-all' : 'deselect-all'}?ajax=true`;
                            updateServer(url).catch(error => {
                                console.error('Error updating selections:', error);
                                // No need to revert UI state as we'll refresh the page if there's an error
                            });
                        });

                        // Initialize UI on page load
                        updateUI();
                    });
                </script>
            </body>

            </html>