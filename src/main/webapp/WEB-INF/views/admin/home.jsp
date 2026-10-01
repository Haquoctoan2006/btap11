<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head><title>Bảng điều khiển</title></head>
<body>
<h2 class="mb-3">Bảng điều khiển quản trị</h2>
<p>Xin chào <strong><c:out value="${sessionScope.currentUser.fullname}"/></strong>, bạn đang ở khu vực dành cho Admin.</p>
<div class="d-flex gap-2">
    <a href="<c:url value='/admin/categories'/>" class="btn btn-outline-primary">Quản lý Danh mục</a>
    <a href="<c:url value='/admin/products'/>" class="btn btn-outline-primary">Quản lý Sản phẩm</a>
</div>
</body>
</html>
