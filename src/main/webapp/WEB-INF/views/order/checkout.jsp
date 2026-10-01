<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<fmt:setLocale value="vi_VN"/>
<c:url var="noImg" value="/assets/images/no-image.svg"/>
<!DOCTYPE html>
<html>
<head><title>Xác nhận đơn hàng</title></head>
<body>

<nav aria-label="breadcrumb" class="mb-3">
    <ol class="breadcrumb">
        <li class="breadcrumb-item"><a href="<c:url value='/home'/>">Trang chủ</a></li>
        <li class="breadcrumb-item"><a href="<c:url value='/cart'/>">Giỏ hàng</a></li>
        <li class="breadcrumb-item active" aria-current="page">Xác nhận đơn hàng</li>
    </ol>
</nav>

<h2 class="mb-3">Xác nhận đơn hàng</h2>

<c:if test="${not empty error}">
    <div class="alert alert-danger"><c:out value="${error}"/></div>
</c:if>

<div class="row g-4">
    <%-- ============ DANH SACH SAN PHAM ============ --%>
    <div class="col-lg-7">
        <div class="card shadow-sm">
            <div class="card-header bg-white fw-semibold">Sản phẩm trong đơn</div>
            <div class="table-responsive">
                <table class="table align-middle mb-0">
                    <thead class="table-light">
                    <tr>
                        <th style="width:80px;">Ảnh</th>
                        <th>Sản phẩm</th>
                        <th class="text-end">Đơn giá</th>
                        <th class="text-center">SL</th>
                        <th class="text-end">Thành tiền</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${checkout.cart.lines}" var="line">
                        <c:set var="pImages" value="${line.images}"/>
                        <%@ include file="/WEB-INF/views/common/product-image.jspf" %>
                        <tr>
                            <td>
                                <img src="<c:out value='${imgSrc}'/>" alt="<c:out value='${line.productName}'/>"
                                     class="rounded border" style="width:60px;height:60px;object-fit:cover;"
                                     onerror="this.onerror=null;this.src='${noImg}'">
                            </td>
                            <td><c:out value="${line.productName}"/></td>
                            <td class="text-end">
                                <fmt:formatNumber value="${line.unitPrice}" type="number" maxFractionDigits="0"/> ₫
                            </td>
                            <td class="text-center"><c:out value="${line.quantity}"/></td>
                            <td class="text-end">
                                <fmt:formatNumber value="${line.subtotal}" type="number" maxFractionDigits="0"/> ₫
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                    <tfoot>
                    <tr class="table-light">
                        <th colspan="4" class="text-end">Tổng thanh toán:</th>
                        <th class="text-end text-danger fs-5">
                            <fmt:formatNumber value="${checkout.cart.total}" type="number" maxFractionDigits="0"/> ₫
                        </th>
                    </tr>
                    </tfoot>
                </table>
            </div>
        </div>
        <a href="<c:url value='/cart'/>" class="btn btn-outline-secondary mt-3">&laquo; Quay lại giỏ hàng</a>
    </div>

    <%-- ============ THONG TIN GIAO HANG + COD ============ --%>
    <div class="col-lg-5">
        <form method="post" action="<c:url value='/checkout/place-order'/>" class="card shadow-sm needs-validation"
              novalidate>
            <div class="card-header bg-white fw-semibold">Thông tin nhận hàng</div>
            <div class="card-body">
                <div class="mb-3">
                    <label class="form-label text-muted">Người nhận</label>
                    <div class="fw-semibold"><c:out value="${checkout.fullname}"/></div>
                </div>
                <div class="mb-3">
                    <label for="phone" class="form-label">Số điện thoại <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="phone" name="phone" maxlength="11"
                           pattern="0[0-9]{9,10}" required value="<c:out value='${phone}'/>"
                           placeholder="0912345678">
                    <div class="invalid-feedback">Số điện thoại gồm 10-11 số, bắt đầu bằng 0.</div>
                </div>
                <div class="mb-3">
                    <label for="shippingAddress" class="form-label">Địa chỉ nhận hàng <span class="text-danger">*</span></label>
                    <textarea class="form-control" id="shippingAddress" name="shippingAddress" rows="3"
                              minlength="5" maxlength="500" required
                              placeholder="Số nhà, đường, phường/xã, quận/huyện, tỉnh/thành phố"><c:out value="${shippingAddress}"/></textarea>
                    <div class="invalid-feedback">Vui lòng nhập địa chỉ nhận hàng (ít nhất 5 ký tự).</div>
                </div>

                <div class="mb-3">
                    <label class="form-label">Phương thức thanh toán</label>
                    <div class="form-check border rounded p-3 ps-5">
                        <input class="form-check-input" type="radio" name="paymentMethod" id="payCod" value="COD" checked>
                        <label class="form-check-label" for="payCod">
                            <strong>Thanh toán khi nhận hàng (COD)</strong>
                            <div class="small text-muted">Bạn trả tiền mặt cho nhân viên giao hàng khi nhận được sản phẩm.</div>
                        </label>
                    </div>
                </div>

                <div class="d-flex justify-content-between align-items-center fs-5 mb-3">
                    <span>Tổng thanh toán</span>
                    <strong class="text-danger">
                        <fmt:formatNumber value="${checkout.cart.total}" type="number" maxFractionDigits="0"/> ₫
                    </strong>
                </div>
                <button type="submit" class="btn btn-success btn-lg w-100">Đặt hàng</button>
                <div class="small text-muted mt-2">
                    Hệ thống sẽ kiểm tra lại tồn kho ngay khi bạn bấm Đặt hàng.
                </div>
            </div>
        </form>
    </div>
</div>

</body>
</html>
