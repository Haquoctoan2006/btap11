<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="me" value="${sessionScope.currentUser}"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><sitemesh:write property='title'/> | Quản trị</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <sitemesh:write property='head'/>
    <style>
        body { background-color: #eef0f3; }
        .sidebar { width: 230px; min-height: calc(100vh - 56px); background: #1f2429; }
        .sidebar .nav-link { color: #ced4da; padding: .7rem 1.2rem; }
        .sidebar .nav-link:hover, .sidebar .nav-link.active { background: #343a40; color: #fff; }
        .sidebar .nav-link.disabled { color: #6c757d; }
        .admin-content { background: #fff; border-radius: .5rem; box-shadow: 0 1px 3px rgba(0,0,0,.08); }
        .site-footer { background: #212529; color: #adb5bd; }
    </style>
</head>
<body class="d-flex flex-column min-vh-100">

<!-- ============ HEADER ADMIN ============ -->
<nav class="navbar navbar-dark bg-dark">
    <div class="container-fluid">
        <a class="navbar-brand fw-bold" href="<c:url value='/admin/home'/>">Shop Online - Quản trị</a>
        <div class="d-flex align-items-center gap-3 text-white small">
            <span>Xin chào, <strong><c:out value="${me.fullname}"/></strong>
                <span class="badge text-bg-secondary"><c:out value="${me.roleName}"/></span></span>
            <a class="link-light" href="<c:url value='/home'/>">Về trang chủ</a>
            <form method="post" action="<c:url value='/logout'/>" class="d-inline">
                <button type="submit" class="btn btn-outline-light btn-sm">Đăng xuất</button>
            </form>
        </div>
    </div>
</nav>

<div class="d-flex flex-grow-1">
    <!-- ============ SIDEBAR ============ -->
    <aside class="sidebar">
        <ul class="nav flex-column pt-3">
            <li class="nav-item"><a class="nav-link" href="<c:url value='/admin/home'/>">Bảng điều khiển</a></li>
            <li class="nav-item"><a class="nav-link" href="<c:url value='/admin/categories'/>">Quản lý Danh mục</a></li>
            <li class="nav-item"><a class="nav-link" href="<c:url value='/admin/products'/>">Quản lý Sản phẩm</a></li>
        </ul>
    </aside>

    <!-- ============ NOI DUNG ============ -->
    <div class="flex-grow-1 p-3">
        <div class="admin-content p-4">
            <sitemesh:write property='body'/>
        </div>
    </div>
</div>

<!-- ============ FOOTER ============ -->
<footer class="site-footer py-3">
    <div class="container text-center small">
        Họ tên: <strong><c:out value="${applicationScope.studentName}"/></strong>
        &nbsp;|&nbsp; MSSV: <strong><c:out value="${applicationScope.studentId}"/></strong>
        &nbsp;|&nbsp; Mã đề: <strong><c:out value="${applicationScope.examCode}"/></strong>
    </div>
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
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
