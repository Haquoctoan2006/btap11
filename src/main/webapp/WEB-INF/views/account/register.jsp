<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head><title>Đăng ký</title></head>
<body>
<div class="row justify-content-center">
    <div class="col-md-8 col-lg-6">
        <div class="card shadow-sm">
            <div class="card-body p-4">
                <h3 class="card-title mb-4 text-center">Đăng ký tài khoản</h3>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger"><c:out value="${error}"/></div>
                </c:if>

                <form id="registerForm" method="post" action="<c:url value='/register'/>"
                      class="needs-validation" novalidate>
                    <div class="mb-3">
                        <label for="username" class="form-label">Tên đăng nhập</label>
                        <input type="text" id="username" name="username" class="form-control"
                               value="<c:out value='${username}'/>" required
                               pattern="[A-Za-z0-9_.]{4,30}" maxlength="30">
                        <div class="invalid-feedback">4-30 ký tự: chữ, số, dấu chấm hoặc gạch dưới.</div>
                    </div>
                    <div class="mb-3">
                        <label for="fullname" class="form-label">Họ và tên</label>
                        <input type="text" id="fullname" name="fullname" class="form-control"
                               value="<c:out value='${fullname}'/>" required minlength="2" maxlength="50">
                        <div class="invalid-feedback">Họ tên phải từ 2 đến 50 ký tự.</div>
                    </div>
                    <div class="mb-3">
                        <label for="email" class="form-label">Email (nhận mã OTP)</label>
                        <input type="email" id="email" name="email" class="form-control"
                               value="<c:out value='${email}'/>" required maxlength="100">
                        <div class="invalid-feedback">Vui lòng nhập email đúng định dạng.</div>
                    </div>
                    <div class="mb-3">
                        <label for="phone" class="form-label">Số điện thoại (không bắt buộc)</label>
                        <input type="tel" id="phone" name="phone" class="form-control"
                               value="<c:out value='${phone}'/>" pattern="0[0-9]{9,10}" maxlength="11">
                        <div class="invalid-feedback">Số điện thoại bắt đầu bằng 0, gồm 10-11 số.</div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="password" class="form-label">Mật khẩu</label>
                            <input type="password" id="password" name="password" class="form-control"
                                   required minlength="6" maxlength="72">
                            <div class="invalid-feedback">Mật khẩu từ 6 ký tự trở lên.</div>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="confirmPassword" class="form-label">Nhập lại mật khẩu</label>
                            <input type="password" id="confirmPassword" name="confirmPassword"
                                   class="form-control" required minlength="6" maxlength="72">
                            <div class="invalid-feedback">Mật khẩu nhập lại không khớp.</div>
                        </div>
                    </div>
                    <button type="submit" class="btn btn-primary w-100">Đăng ký và nhận mã OTP</button>
                </form>

                <p class="mt-3 mb-0 text-center">Đã có tài khoản? <a href="<c:url value='/login'/>">Đăng nhập</a></p>
            </div>
        </div>
    </div>
</div>

<script>
    // Kiem tra "nhap lai mat khau" phia trinh duyet (server van kiem tra lai)
    (function () {
        var pw = document.getElementById('password');
        var cf = document.getElementById('confirmPassword');
        function check() {
            cf.setCustomValidity(cf.value !== pw.value ? 'Mật khẩu nhập lại không khớp' : '');
        }
        pw.addEventListener('input', check);
        cf.addEventListener('input', check);
    })();
</script>
</body>
</html>
