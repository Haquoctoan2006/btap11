<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head><title>Xác thực OTP</title></head>
<body>
<div class="row justify-content-center">
    <div class="col-md-6 col-lg-5">
        <div class="card shadow-sm">
            <div class="card-body p-4">
                <h3 class="card-title mb-3 text-center">Kích hoạt tài khoản</h3>
                <p class="text-muted text-center">
                    Mã OTP gồm 6 chữ số đã được gửi tới<br>
                    <strong><c:out value="${email}"/></strong>
                </p>

                <c:if test="${not empty message}">
                    <div class="alert alert-success"><c:out value="${message}"/></div>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger"><c:out value="${error}"/></div>
                </c:if>

                <form method="post" action="<c:url value='/verify-otp'/>" class="needs-validation" novalidate>
                    <input type="hidden" name="email" value="<c:out value='${email}'/>">
                    <div class="mb-3">
                        <label for="otp" class="form-label">Mã OTP</label>
                        <input type="text" id="otp" name="otp" class="form-control text-center fs-4"
                               required pattern="[0-9]{6}" maxlength="6" inputmode="numeric"
                               autocomplete="one-time-code" autofocus>
                        <div class="invalid-feedback">Vui lòng nhập đúng 6 chữ số.</div>
                    </div>
                    <button type="submit" class="btn btn-primary w-100">Xác nhận</button>
                </form>

                <form method="post" action="<c:url value='/resend-otp'/>" class="mt-3 text-center">
                    <input type="hidden" name="email" value="<c:out value='${email}'/>">
                    <button type="submit" class="btn btn-link">Gửi lại OTP</button>
                </form>
                <p class="mb-0 text-center small text-muted">Mã có hiệu lực trong 5 phút.</p>
            </div>
        </div>
    </div>
</div>
</body>
</html>
