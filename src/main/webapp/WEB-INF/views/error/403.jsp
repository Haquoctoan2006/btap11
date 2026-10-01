<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<% response.setStatus(403); %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>403 - Không có quyền truy cập</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light d-flex align-items-center min-vh-100">
<div class="container text-center">
    <h1 class="display-3 fw-bold">403</h1>
    <p class="lead">Bạn không có quyền truy cập trang này.</p>
    <a class="btn btn-primary" href="<c:url value='/home'/>">Về trang chủ</a>
</div>
</body>
</html>
