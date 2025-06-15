<%-- 
    Document   : category-update
    Created on : Jun 13, 2025, 8:20:00 PM
    Author     : NhiDTYCE-180492
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setLocale value="vi_VN" />

<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Edit Category</title>
        <jsp:include page="../common/head.jsp" />
        <style>
            .form-container {
                max-width: 800px;
                margin: 0 auto;
            }

            .card {
                border-radius: 15px;
                box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
                border: none;
            }

            .card-header {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                border-radius: 15px 15px 0 0 !important;
                padding: 20px;
            }

            .form-control {
                border-radius: 10px;
                border: 2px solid #e9ecef;
                padding: 12px 15px;
                transition: all 0.3s ease;
            }

            .form-control:focus {
                border-color: #667eea;
                box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
            }

            .btn {
                border-radius: 10px;
                padding: 12px 30px;
                font-weight: 600;
                transition: all 0.3s ease;
            }

            .btn-primary {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                border: none;
            }

            .btn-primary:hover {
                transform: translateY(-2px);
                box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
            }

            .btn-secondary {
                background: #6c757d;
                border: none;
            }

            .btn-secondary:hover {
                transform: translateY(-2px);
                box-shadow: 0 5px 15px rgba(108, 117, 125, 0.4);
            }

            .required-field {
                color: #dc3545;
            }

            .form-label {
                font-weight: 600;
                color: #495057;
                margin-bottom: 8px;
            }

            .error-field {
                border-color: #dc3545 !important;
                box-shadow: 0 0 0 0.2rem rgba(220, 53, 69, 0.25) !important;
            }

            .char-counter {
                font-size: 0.875rem;
                color: #6c757d;
            }

            .char-counter.warning {
                color: #ffc107;
            }

            .char-counter.danger {
                color: #dc3545;
            }
        </style>
    </head>
    <body>
        <!-- Admin Sidebar -->
        <c:set var="activeMenu" value="categories" scope="request" />
        <jsp:include page="../common/sidebar.jsp" />

        <!-- Main content -->
        <div class="admin-content">
            <!-- Header -->
            <div class="admin-header d-flex justify-content-between align-items-center">
                <button class="btn d-lg-none" id="toggleSidebarBtn">
                    <i class="fas fa-bars"></i>
                </button>
                <h2 class="m-0 d-none d-lg-block">Edit Category</h2>
                <a href="${pageContext.request.contextPath}/admin/categories${not empty param.page ? '?page='.concat(param.page) : ''}" class="btn btn-secondary">
                    <i class="fas fa-arrow-left"></i> Back to Categories
                </a>
            </div>

            <div class="mt-4 form-container">
                <!-- Display error messages -->
                <c:if test="${not empty param.error}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-circle me-2"></i>
                        <c:choose>
                            <c:when test="${param.error eq 'missing_fields'}">Please fill in all required information.</c:when>
                            <c:when test="${param.error eq 'duplicate'}">Category name already exists. Please enter another name.</c:when>
                            <c:when test="${param.error eq 'update_failed'}">Category update failed, please try again.</c:when>
                            <c:when test="${param.error eq 'name_too_long'}">Category name must not exceed 50 characters.</c:when>
                            <c:when test="${param.error eq 'description_too_long'}">Description must not exceed 150 characters.</c:when>
                            <c:when test="${param.error eq 'invalid_id'}">Invalid category ID.</c:when>
                            <c:otherwise>${param.error}</c:otherwise>
                        </c:choose>
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>

                <!-- Display success messages -->
                <c:if test="${not empty param.success}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fas fa-check-circle me-2"></i>
                        <c:choose>
                            <c:when test="${param.success eq 'updated'}">Category updated successfully!</c:when>
                            <c:otherwise>${param.success}</c:otherwise>
                        </c:choose>
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>

                <div class="card">
                    <div class="card-header">
                        <h4 class="mb-0">
                            <i class="fas fa-edit me-2"></i>
                            Edit Category Information
                        </h4>
                    </div>
                    <div class="card-body p-4">
                        <form action="${pageContext.request.contextPath}/admin/categories-update" method="post" id="categoryForm">
                            <input type="hidden" name="categoryId" value="${category.categoryID}">
                            <input type="hidden" name="currentPage" value="${param.page}">

                            <div class="row">
                                <div class="col-md-12">
                                    <div class="mb-4">
                                        <label for="categoryName" class="form-label">
                                            Category Name <span class="required-field">*</span>
                                        </label>
                                        <input type="text" 
                                               class="form-control ${param.error eq 'duplicate' or param.error eq 'missing_fields' or param.error eq 'name_too_long' ? 'error-field' : ''}" 
                                               id="categoryName" 
                                               name="name" 
                                               value="${not empty param.name ? param.name : category.name}" 
                                               required 
                                               placeholder="Enter category name"
                                               maxlength="50">
                                        <div class="d-flex justify-content-between align-items-center mt-1">
                                            <div class="form-text">
                                                <i class="fas fa-info-circle me-1"></i>
                                                Maximum 50 characters
                                            </div>
                                            <div class="char-counter" id="nameCounter">
                                                <span id="nameCount">0</span>/50
                                            </div>
                                        </div>
                                        <c:if test="${param.error eq 'duplicate'}">
                                            <div class="text-danger mt-1">
                                                <i class="fas fa-exclamation-triangle me-1"></i>
                                                This category name already exists, please enter another name
                                            </div>
                                        </c:if>
                                        <c:if test="${param.error eq 'name_too_long'}">
                                            <div class="text-danger mt-1">
                                                <i class="fas fa-exclamation-triangle me-1"></i>
                                                Category name must not exceed 50 characters
                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-md-12">
                                    <div class="mb-4">
                                        <label for="categoryDescription" class="form-label">
                                            Description <span class="required-field">*</span>
                                        </label>
                                        <textarea class="form-control ${param.error eq 'missing_fields' or param.error eq 'description_too_long' ? 'error-field' : ''}" 
                                                  id="categoryDescription" 
                                                  name="description" 
                                                  rows="4" 
                                                  required
                                                  placeholder="Enter category description"
                                                  maxlength="150">${not empty param.description ? param.description : category.description}</textarea>
                                        <div class="d-flex justify-content-between align-items-center mt-1">
                                            <div class="form-text">
                                                <i class="fas fa-info-circle me-1"></i>
                                                Maximum 150 characters.
                                            </div>
                                            <div class="char-counter" id="descCounter">
                                                <span id="descCount">0</span>/150
                                            </div>
                                        </div>
                                        <c:if test="${param.error eq 'description_too_long'}">
                                            <div class="text-danger mt-1">
                                                <i class="fas fa-exclamation-triangle me-1"></i>
                                                Description must not exceed 150 characters
                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-md-12">
                                    <div class="d-flex justify-content-end gap-3">
                                        <a href="${pageContext.request.contextPath}/admin/categories${not empty param.page ? '?page='.concat(param.page) : ''}" class="btn btn-secondary">
                                            <i class="fas fa-times me-1"></i>
                                            Cancel
                                        </a>
                                        <button type="submit" class="btn btn-primary">
                                            <i class="fas fa-save me-1"></i>
                                            Update Category
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <jsp:include page="../common/scripts.jsp" />

        <script>
            // Initialize character counters on page load
            document.addEventListener('DOMContentLoaded', function () {
                updateCharCounter('categoryName', 'nameCount', 'nameCounter', 50);
                updateCharCounter('categoryDescription', 'descCount', 'descCounter', 150);
            });

            // Enhanced form validation with updated limits
            document.getElementById('categoryForm').addEventListener('submit', function (e) {
                const nameField = document.getElementById('categoryName');
                const descriptionField = document.getElementById('categoryDescription');

                // Trim values
                const name = nameField.value.trim();
                const description = descriptionField.value.trim();

                // Update field values with trimmed values
                nameField.value = name;
                descriptionField.value = description;

                console.log('Form submission - Name: "' + name + '", Description: "' + description + '"');

                if (name === '') {
                    e.preventDefault();
                    showFieldError('categoryName', 'Please enter category name.');
                    return false;
                }

                if (description === '') {
                    e.preventDefault();
                    showFieldError('categoryDescription', 'Please enter category description.');
                    return false;
                }

                if (name.length > 50) {
                    e.preventDefault();
                    showFieldError('categoryName', 'Category name must not exceed 50 characters.');
                    return false;
                }

                if (description.length > 150) {
                    e.preventDefault();
                    showFieldError('categoryDescription', 'Description must not exceed 150 characters.');
                    return false;
                }

                // Additional validation for special characters or suspicious input
                if (name.includes('  ')) { // Multiple spaces
                    nameField.value = name.replace(/\s+/g, ' '); // Replace multiple spaces with single space
                }
            });

            // Character counter function
            function updateCharCounter(fieldId, countId, counterId, maxLength) {
                const field = document.getElementById(fieldId);
                const countSpan = document.getElementById(countId);
                const counterDiv = document.getElementById(counterId);
                
                if (!field || !countSpan || !counterDiv) return;
                
                const currentLength = field.value.length;
                countSpan.textContent = currentLength;
                
                // Update counter styling based on length
                counterDiv.classList.remove('warning', 'danger');
                if (currentLength > maxLength * 0.9) {
                    counterDiv.classList.add('danger');
                } else if (currentLength > maxLength * 0.8) {
                    counterDiv.classList.add('warning');
                }
            }

            // Real-time character counting for name field
            document.getElementById('categoryName').addEventListener('input', function () {
                updateCharCounter('categoryName', 'nameCount', 'nameCounter', 50);
                
                // Remove error styling when user starts typing
                this.classList.remove('error-field');
                const errorMsg = this.parentNode.querySelector('.text-danger');
                if (errorMsg && errorMsg.textContent.includes('duplicate')) {
                    errorMsg.remove();
                }
            });

            // Real-time character counting for description field
            document.getElementById('categoryDescription').addEventListener('input', function () {
                updateCharCounter('categoryDescription', 'descCount', 'descCounter', 150);
                
                // Remove error styling when user starts typing
                this.classList.remove('error-field');
            });

            // Show field error function (enhanced)
            function showFieldError(fieldId, message) {
                const field = document.getElementById(fieldId);
                field.classList.add('error-field');
                field.focus();

                // Remove existing error message
                const existingError = field.parentNode.querySelector('.text-danger');
                if (existingError) {
                    existingError.remove();
                }

                // Add new error message
                const errorDiv = document.createElement('div');
                errorDiv.className = 'text-danger mt-1';
                errorDiv.innerHTML = '<i class="fas fa-exclamation-triangle me-1"></i>' + message;
                field.parentNode.appendChild(errorDiv);

                // Remove error styling after user starts typing
                field.addEventListener('input', function () {
                    this.classList.remove('error-field');
                    const errorMsg = this.parentNode.querySelector('.text-danger');
                    if (errorMsg) {
                        errorMsg.remove();
                    }
                }, {once: true});
            }

            // Auto-focus and select text on duplicate error
            <c:if test="${param.error eq 'duplicate'}">
            document.addEventListener('DOMContentLoaded', function () {
                const nameField = document.getElementById('categoryName');
                nameField.focus();
                nameField.select();

                // Show persistent error message
                showFieldError('categoryName', 'This category name already exists, please enter a different name');
            });
            </c:if>

            // Auto-focus on name field if name too long
            <c:if test="${param.error eq 'name_too_long'}">
            document.addEventListener('DOMContentLoaded', function () {
                const nameField = document.getElementById('categoryName');
                nameField.focus();
                nameField.select();
            });
            </c:if>

            // Auto-focus on description field if description too long
            <c:if test="${param.error eq 'description_too_long'}">
            document.addEventListener('DOMContentLoaded', function () {
                const descField = document.getElementById('categoryDescription');
                descField.focus();
                descField.select();
            });
            </c:if>

            // Debug: Log form data before submission
            document.getElementById('categoryForm').addEventListener('submit', function (e) {
                console.log('=== FORM SUBMISSION DEBUG ===');
                console.log('Action:', this.action);
                console.log('Method:', this.method);

                const formData = new FormData(this);
                for (let [key, value] of formData.entries()) {
                    console.log(key + ':', '"' + value + '"');
                }
                console.log('=== END DEBUG ===');
            });
        </script>
    </body>
</html>