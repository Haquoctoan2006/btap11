<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head><title>Trang chủ Seller</title></head>
<body>
<div class="p-5 mb-4 bg-white rounded-3 shadow-sm">
    <h1 class="display-6 fw-bold">Trang chủ người bán (Seller)</h1>
    <p class="lead mb-1">Xin chào <strong><c:out value="${sessionScope.currentUser.fullname}"/></strong>.</p>
    <p class="text-muted">
        Mã cửa hàng của bạn:
        <strong><c:out value="${empty sessionScope.currentUser.sellerId ? 'Chưa gán cửa hàng' : sessionScope.currentUser.sellerId}"/></strong>
    </p>
    <a class="btn btn-primary" href="<c:url value='/products'/>">Xem danh sách sản phẩm</a>
</div>
</body>
</html>
