<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<% response.setStatus(404); %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>404 - Không tìm thấy trang</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light d-flex align-items-center min-vh-100">
<div class="container text-center">
    <h1 class="display-3 fw-bold">404</h1>
    <p class="lead">Không tìm thấy trang bạn yêu cầu.</p>
    <a class="btn btn-primary" href="<c:url value='/home'/>">Về trang chủ</a>
</div>
</body>
</html>
