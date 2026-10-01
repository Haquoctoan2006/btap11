<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="me" value="${sessionScope.currentUser}"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><sitemesh:write property='title'/> | Shop Online</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <sitemesh:write property='head'/>
    <style>
        body { background-color: #f5f6f8; }
        .navbar-brand { font-weight: 700; letter-spacing: .3px; }
        .site-footer { background: #212529; color: #adb5bd; }
    </style>
</head>
<body class="d-flex flex-column min-vh-100">

<!-- ============ HEADER ============ -->
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand" href="<c:url value='/home'/>">Shop Online</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav"
                aria-controls="mainNav" aria-expanded="false" aria-label="Mở menu">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="mainNav">
            <ul class="navbar-nav me-auto">
                <li class="nav-item">
                    <c:choose>
                        <c:when test="${not empty me}">
                            <a class="nav-link" href="<c:url value='${me.homePath}'/>">Trang chủ</a>
                        </c:when>
                        <c:otherwise>
                            <a class="nav-link" href="<c:url value='/home'/>">Trang chủ</a>
                        </c:otherwise>
                    </c:choose>
                </li>
                <li class="nav-item"><a class="nav-link" href="<c:url value='/products'/>">Sản phẩm</a></li>
                <c:if test="${not empty me and not me.admin and not me.seller}">
                    <li class="nav-item"><a class="nav-link" href="<c:url value='/cart'/>">Giỏ hàng</a></li>
                    <li class="nav-item"><a class="nav-link" href="<c:url value='/orders'/>">Đơn hàng của tôi</a></li>
                </c:if>
                <c:if test="${not empty me and me.admin}">
                    <li class="nav-item"><a class="nav-link" href="<c:url value='/admin/home'/>">Trang quản trị</a></li>
                </c:if>
            </ul>

            <ul class="navbar-nav align-items-lg-center">
                <c:choose>
                    <c:when test="${not empty me}">
                        <li class="nav-item">
                            <span class="navbar-text me-lg-3">
                                Xin chào, <strong><c:out value="${me.fullname}"/></strong>
                                <span class="badge text-bg-secondary"><c:out value="${me.roleName}"/></span>
                            </span>
                        </li>
                        <li class="nav-item">
                            <form method="post" action="<c:url value='/logout'/>" class="d-inline">
                                <button type="submit" class="btn btn-outline-light btn-sm">Đăng xuất</button>
                            </form>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li class="nav-item"><a class="nav-link" href="<c:url value='/login'/>">Đăng nhập</a></li>
                        <li class="nav-item"><a class="nav-link" href="<c:url value='/register'/>">Đăng ký</a></li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </div>
</nav>

<!-- ============ NOI DUNG ============ -->
<main class="container my-4 flex-grow-1">
    <sitemesh:write property='body'/>
</main>

<!-- ============ FOOTER ============ -->
<footer class="site-footer py-3 mt-auto">
    <div class="container text-center small">
        Họ tên: <strong><c:out value="${applicationScope.studentName}"/></strong>
        &nbsp;|&nbsp; MSSV: <strong><c:out value="${applicationScope.studentId}"/></strong>
        &nbsp;|&nbsp; Mã đề: <strong><c:out value="${applicationScope.examCode}"/></strong>
    </div>
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Hien thi loi validate HTML5 kieu Bootstrap cho moi form .needs-validation
    (function () {
        'use strict';
        document.querySelectorAll('form.needs-validation').forEach(function (form) {
            form.addEventListener('submit', function (event) {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            }, false);
        });
    })();
</script>
</body>
</html>
