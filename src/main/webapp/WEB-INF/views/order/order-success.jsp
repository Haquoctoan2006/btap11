<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<fmt:setLocale value="vi_VN"/>
<!DOCTYPE html>
<html>
<head><title>Đặt hàng thành công</title></head>
<body>

<div class="card shadow-sm">
    <div class="card-body text-center py-5">
        <div class="display-4 text-success mb-2">&#10003;</div>
        <h2 class="mb-2">Đặt hàng thành công!</h2>
        <p class="text-muted mb-4">Cảm ơn bạn đã mua hàng. Đơn hàng của bạn đã được ghi nhận.</p>

        <div class="row justify-content-center">
            <div class="col-md-7">
                <table class="table table-borderless text-start mb-4">
                    <tbody>
                    <tr>
                        <th class="text-muted fw-normal" style="width:200px;">Mã đơn hàng:</th>
                        <td class="fw-bold">#<c:out value="${order.orderId}"/></td>
                    </tr>
                    <tr>
                        <th class="text-muted fw-normal">Ngày đặt:</th>
                        <td><c:out value="${order.orderDateText}"/></td>
                    </tr>
                    <tr>
                        <th class="text-muted fw-normal">Tổng tiền:</th>
                        <td class="text-danger fw-bold">
                            <fmt:formatNumber value="${order.totalAmount}" type="number" maxFractionDigits="0"/> ₫
                        </td>
                    </tr>
                    <tr>
                        <th class="text-muted fw-normal">Phương thức thanh toán:</th>
                        <td><c:out value="${order.paymentLabel}"/></td>
                    </tr>
                    <tr>
                        <th class="text-muted fw-normal">Trạng thái:</th>
                        <td><span class="badge text-bg-${order.statusBadge}"><c:out value="${order.statusLabel}"/></span></td>
                    </tr>
                    <tr>
                        <th class="text-muted fw-normal">Số điện thoại:</th>
                        <td><c:out value="${order.phone}"/></td>
                    </tr>
                    <tr>
                        <th class="text-muted fw-normal">Địa chỉ nhận hàng:</th>
                        <td><c:out value="${order.shippingAddress}"/></td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>

        <c:url var="detailUrl" value="/orders/detail"><c:param name="id" value="${order.orderId}"/></c:url>
        <a href="${detailUrl}" class="btn btn-primary me-2">Xem chi tiết đơn hàng</a>
        <a href="<c:url value='/orders'/>" class="btn btn-outline-primary me-2">Lịch sử đơn hàng</a>
        <a href="<c:url value='/products'/>" class="btn btn-outline-secondary">Tiếp tục mua sắm</a>
    </div>
</div>

</body>
</html>
