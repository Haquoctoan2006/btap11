<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head><title>Trang chủ</title></head>
<body>
<div class="p-5 mb-4 bg-white rounded-3 shadow-sm">
    <h1 class="display-6 fw-bold">Chào mừng đến Shop Online</h1>
    <c:choose>
        <c:when test="${not empty sessionScope.currentUser}">
            <p class="lead mb-1">Xin chào <strong><c:out value="${sessionScope.currentUser.fullname}"/></strong>,
                bạn đã đăng nhập với vai trò <strong><c:out value="${sessionScope.currentUser.roleName}"/></strong>.</p>
            <a class="btn btn-primary mt-3" href="<c:url value='/products'/>">Xem sản phẩm</a>
        </c:when>
        <c:otherwise>
            <p class="lead">Đăng nhập hoặc đăng ký để bắt đầu mua sắm.</p>
            <a class="btn btn-primary me-2" href="<c:url value='/login'/>">Đăng nhập</a>
            <a class="btn btn-outline-primary" href="<c:url value='/register'/>">Đăng ký</a>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>
