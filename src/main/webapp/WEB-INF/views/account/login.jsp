<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head><title>Đăng nhập</title></head>
<body>
<div class="row justify-content-center">
    <div class="col-md-6 col-lg-5">
        <div class="card shadow-sm">
            <div class="card-body p-4">
                <h3 class="card-title mb-4 text-center">Đăng nhập</h3>

                <c:if test="${param.activated == '1'}">
                    <div class="alert alert-success">Kích hoạt tài khoản thành công! Vui lòng đăng nhập.</div>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger"><c:out value="${error}"/></div>
                </c:if>

                <form method="post" action="<c:url value='/login'/>" class="needs-validation" novalidate>
                    <div class="mb-3">
                        <label for="identifier" class="form-label">Tên đăng nhập hoặc Email</label>
                        <input type="text" id="identifier" name="identifier" class="form-control"
                               value="<c:out value='${identifier}'/>" required maxlength="100" autofocus>
                        <div class="invalid-feedback">Vui lòng nhập tên đăng nhập hoặc email.</div>
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">Mật khẩu</label>
                        <input type="password" id="password" name="password" class="form-control"
                               required minlength="6" maxlength="72">
                        <div class="invalid-feedback">Mật khẩu phải từ 6 ký tự trở lên.</div>
                    </div>
                    <button type="submit" class="btn btn-primary w-100">Đăng nhập</button>
                </form>

                <hr>
                <p class="mb-1 text-center">Chưa có tài khoản? <a href="<c:url value='/register'/>">Đăng ký</a></p>
                <p class="mb-0 text-center small text-muted">
                    Đã đăng ký nhưng chưa kích hoạt? Bấm "Đăng ký" và nhập lại cùng email để nhận OTP mới.
                </p>
            </div>
        </div>
    </div>
</div>
</body>
</html>
