<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <!DOCTYPE html>
            <html>

            <head>
                <title>Request Course Refund - LightHouse Course</title>
                <jsp:include page="/WEB-INF/views/customer/common/head.jsp" />
            </head>

            <body>
                <jsp:include page="/WEB-INF/views/customer/common/navigation.jsp" />

                <div class="container mt-4">
                    <div class="row justify-content-center">
                        <div class="col-lg-8">
                            <div class="card shadow">
                                <div class="card-header bg-primary text-white">
                                    <h4 class="mb-0">Request Refund</h4>
                                </div>
                                <div class="card-body">
                                    <c:if test="${not empty error}">
                                        <div class="alert alert-danger mb-4">
                                            ${error}
                                        </div>
                                    </c:if>

                                    <div class="alert alert-info">
                                        <p><strong>Refund Policy:</strong></p>
                                        <ul>
                                            <li>Refunds are only available for courses purchased within the last 7 days.
                                            </li>
                                            <li>You must not have completed more than 20% of the course content.</li>
                                            <li>Approved refunds will be processed for ${refundPercentage}% of the
                                                original price.</li>
                                            <li>All refund requests are subject to review and approval.</li>
                                        </ul>
                                    </div>

                                    <div class="course-info mb-4">
                                        <h5>Course Information</h5>
                                        <table class="table">
                                            <tr>
                                                <th>Course Name:</th>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${not empty courseName}">${courseName}</c:when>
                                                        <c:otherwise>
                                                            <c:forEach items="${order.orderDetails}" var="detail"
                                                                varStatus="loop">
                                                                <c:if test="${not empty detail.course}">
                                                                    ${detail.course.name}<c:if test="${!loop.last}">,
                                                                    </c:if>
                                                                </c:if>
                                                            </c:forEach>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                            <tr>
                                                <th>Purchase Date:</th>
                                                <td>
                                                    <fmt:formatDate
                                                        value="${not empty orderDate ? orderDate : order.orderDate}"
                                                        pattern="dd MMM yyyy" />
                                                </td>
                                            </tr>
                                            <tr>
                                                <th>Original Price:</th>
                                                <td>
                                                    <fmt:formatNumber
                                                        value="${not empty originalPrice ? originalPrice : order.totalAmount}"
                                                        pattern="#,###.##" /> đ
                                                </td>
                                            </tr>
                                            <tr>
                                                <th>Refund Amount (${refundPercentage}%):</th>
                                                <td class="text-success">
                                                    <fmt:formatNumber value="${refundAmount}" pattern="#,###.##" /> đ
                                                </td>
                                            </tr>
                                        </table>
                                    </div>

                                    <form
                                        action="${pageContext.request.contextPath}/refund/request/order/${order.orderID}"
                                        method="post">
                                        <input type="hidden" name="action" value="submitRefund">
                                        <input type="hidden" name="orderDetailId" value="${orderDetailId}">
                                        <input type="hidden" name="refundAmount" value="${refundAmount}">

                                        <div class="mb-3">
                                            <label for="reason" class="form-label">Reason for Refund Request</label>
                                            <textarea class="form-control" id="reason" name="reason" rows="4"
                                                required></textarea>
                                            <div class="form-text">Please provide a detailed explanation for your refund
                                                request.</div>
                                        </div>

                                        <div class="form-check mb-3">
                                            <input class="form-check-input" type="checkbox" id="confirmation" required>
                                            <label class="form-check-label" for="confirmation">
                                                I understand that my request will be reviewed and I may not be eligible
                                                for a refund if I don't meet the requirements.
                                            </label>
                                        </div>

                                        <div class="d-flex justify-content-between">
                                            <a href="${pageContext.request.contextPath}/order/history"
                                                class="btn btn-secondary">Cancel</a>
                                            <button type="submit" class="btn btn-primary">Submit Refund Request</button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <jsp:include page="/WEB-INF/views/customer/common/footer.jsp" />
                <jsp:include page="/WEB-INF/views/customer/common/scripts.jsp" />
            </body>

            </html>