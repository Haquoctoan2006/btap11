<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<fmt:setLocale value="vi_VN"/>
<c:url var="noImg" value="/assets/images/no-image.svg"/>
<!DOCTYPE html>
<html>
<head><title>Chi tiết đơn hàng #<c:out value="${order.orderId}"/></title></head>
<body>

<nav aria-label="breadcrumb" class="mb-3">
    <ol class="breadcrumb">
        <li class="breadcrumb-item"><a href="<c:url value='/home'/>">Trang chủ</a></li>
        <li class="breadcrumb-item"><a href="<c:url value='/orders'/>">Lịch sử đơn hàng</a></li>
        <li class="breadcrumb-item active" aria-current="page">Đơn hàng #<c:out value="${order.orderId}"/></li>
    </ol>
</nav>

<div class="d-flex justify-content-between align-items-center mb-3">
    <h2 class="mb-0">Đơn hàng #<c:out value="${order.orderId}"/></h2>
    <span class="badge fs-6 text-bg-${order.statusBadge}"><c:out value="${order.statusLabel}"/></span>
</div>

<div class="row g-4">
    <div class="col-lg-8">
        <div class="card shadow-sm">
            <div class="card-header bg-white fw-semibold">Sản phẩm</div>
            <div class="table-responsive">
                <table class="table align-middle mb-0">
                    <thead class="table-light">
                    <tr>
                        <th style="width:80px;">Ảnh</th>
                        <th>Sản phẩm</th>
                        <th class="text-end">Đơn giá</th>
                        <th class="text-center">Số lượng</th>
                        <th class="text-end">Thành tiền</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${order.items}" var="item">
                        <c:set var="pImages" value="${item.product.images}"/>
                        <%@ include file="/WEB-INF/views/common/product-image.jspf" %>
                        <tr>
                            <td>
                                <img src="<c:out value='${imgSrc}'/>" alt="<c:out value='${item.product.productName}'/>"
                                     class="rounded border" style="width:60px;height:60px;object-fit:cover;"
                                     onerror="this.onerror=null;this.src='${noImg}'">
                            </td>
                            <td><c:out value="${item.product.productName}"/></td>
                            <td class="text-end">
                                <fmt:formatNumber value="${item.unitPrice}" type="number" maxFractionDigits="0"/> ₫
                            </td>
                            <td class="text-center"><c:out value="${item.quantity}"/></td>
                            <td class="text-end">
                                <fmt:formatNumber value="${item.subtotal}" type="number" maxFractionDigits="0"/> ₫
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                    <tfoot>
                    <tr class="table-light">
                        <th colspan="4" class="text-end">Tổng đơn hàng:</th>
                        <th class="text-end text-danger fs-5">
                            <fmt:formatNumber value="${order.totalAmount}" type="number" maxFractionDigits="0"/> ₫
                        </th>
                    </tr>
                    </tfoot>
                </table>
            </div>
        </div>
    </div>

    <div class="col-lg-4">
        <div class="card shadow-sm">
            <div class="card-header bg-white fw-semibold">Thông tin đơn hàng</div>
            <div class="card-body">
                <dl class="mb-0">
                    <dt class="text-muted fw-normal">Mã đơn hàng</dt>
                    <dd>#<c:out value="${order.orderId}"/></dd>
                    <dt class="text-muted fw-normal">Ngày đặt</dt>
                    <dd><c:out value="${order.orderDateText}"/></dd>
                    <dt class="text-muted fw-normal">Trạng thái</dt>
                    <dd><span class="badge text-bg-${order.statusBadge}"><c:out value="${order.statusLabel}"/></span></dd>
                    <dt class="text-muted fw-normal">Phương thức thanh toán</dt>
                    <dd><c:out value="${order.paymentLabel}"/></dd>
                    <dt class="text-muted fw-normal">Số điện thoại</dt>
                    <dd><c:out value="${order.phone}" default="—"/></dd>
                    <dt class="text-muted fw-normal">Địa chỉ nhận hàng</dt>
                    <dd class="mb-0"><c:out value="${order.shippingAddress}" default="—"/></dd>
                </dl>
            </div>
        </div>
        <a href="<c:url value='/orders'/>" class="btn btn-outline-secondary mt-3 w-100">&laquo; Quay lại lịch sử đơn hàng</a>
    </div>
</div>

</body>
</html>
