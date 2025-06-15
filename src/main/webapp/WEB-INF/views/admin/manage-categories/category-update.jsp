<%-- 
    Document   : category-update
    Created on : Jun 13, 2025, 8:24:32 PM
    Author     : NhiDTYCE-180492
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<fmt:setLocale value="vi_VN" />

<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Edit Category</title>
        <jsp:include page="../common/head.jsp" />
        <style>
            .btn-primary {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
                border: none !important;
                color: white !important;
            }

            .btn-primary:hover {
                background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%) !important;
                transform: translateY(-1px);
                box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
            }

            .card-header {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
            }
        </style>
    </head>
    <body>

        <c:set var="activeMenu" value="categories" scope="request" />
        <jsp:include page="../common/sidebar.jsp" />

        <div class="admin-content">
            <!-- Header -->
            <div class="admin-header d-flex justify-content-between align-items-center">
                <button class="btn d-lg-none" id="toggleSidebarBtn">
                    <i class="fas fa-bars"></i>
                </button>
                <h2 class="m-0 d-none d-lg-block">Edit Category</h2>
                <a href="${pageContext.request.contextPath}/admin/categories" class="btn btn-secondary">
                    <i class="fas fa-arrow-left"></i> Back to Categories
                </a>
            </div>

            <div class="mt-4 form-container">
                <div class="card">
                    <div class="card-header">
                        <h4><i class="fas fa-edit me-2"></i>Category Information</h4>
                    </div>
                    <div class="card-body p-4">
                        <c:if test="${param.error == 'missing_fields'}">
                            <div class="alert alert-danger">Please fill in all required fields (name and description).</div>
                        </c:if>

                        <c:if test="${param.error == 'duplicate'}">
                            <div class="alert alert-danger">Category name already exists. Please choose another name.</div>
                        </c:if>
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger">${error}</div>
                        </c:if>

                        <form action="${pageContext.request.contextPath}/admin/categories-update" method="post">
                            <!-- Hidden ID -->
                            <input type="hidden" name="categoryId" value="${category.categoryID}" />

                            <div class="mb-4">
                                <label class="form-label">Category Name <span class="text-danger">*</span></label>
                                <input type="text" name="name"
                                       value="${param.name != null ? param.name : category.name}"
                                       class="form-control" required maxlength="100" />

                            </div>

                            <div class="mb-4">
                                <label class="form-label">Description <span class="text-danger">*</span></label>
                                <textarea name="description" rows="4" class="form-control" maxlength="500" required>${param.description != null ? param.description : category.description}</textarea>



                            </div>

                            <div class="d-flex justify-content-end gap-3">
                                <a href="${pageContext.request.contextPath}/admin/categories" class="btn btn-secondary">
                                    <i class="fas fa-times me-1"></i> Cancel
                                </a>
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-save me-1"></i> Update
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <jsp:include page="../common/scripts.jsp" />
    </body>
</html>