<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<fmt:setLocale value="vi_VN"/>
<!DOCTYPE html>
<html>
<head><title>Lịch sử đơn hàng</title></head>
<body>

<h2 class="mb-3">Lịch sử đơn hàng</h2>

<%@ include file="/WEB-INF/views/common/flash.jspf" %>
<c:if test="${not empty error}">
    <div class="alert alert-danger"><c:out value="${error}"/></div>
</c:if>

<%-- ============ LOC THEO TRANG THAI (so don lay tu database) ============ --%>
<ul class="nav nav-pills mb-3 flex-wrap gap-1">
    <li class="nav-item">
        <a class="nav-link ${selectedStatus == 'ALL' ? 'active' : ''}" href="<c:url value='/orders'/>">
            Tất cả <span class="badge text-bg-light"><c:out value="${empty totalOrders ? 0 : totalOrders}"/></span>
        </a>
    </li>
    <c:forEach items="${statuses}" var="s">
        <c:url var="tabUrl" value="/orders"><c:param name="status" value="${s.code}"/></c:url>
        <li class="nav-item">
            <a class="nav-link ${selectedStatus == s.code ? 'active' : ''}" href="${tabUrl}">
                <c:out value="${s.label}"/>
                <span class="badge text-bg-light"><c:out value="${empty statusCounts[s.code] ? 0 : statusCounts[s.code]}"/></span>
            </a>
        </li>
    </c:forEach>
</ul>

<div class="card shadow-sm">
    <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
            <thead class="table-light">
            <tr>
                <th>Mã đơn hàng</th>
                <th>Ngày đặt</th>
                <th class="text-end">Tổng tiền</th>
                <th>Phương thức thanh toán</th>
                <th>Trạng thái</th>
                <th style="width:120px;"></th>
            </tr>
            </thead>
            <tbody>
            <c:choose>
                <c:when test="${empty orders}">
                    <tr>
                        <td colspan="6" class="text-center text-muted py-4">
                            <c:choose>
                                <c:when test="${selectedStatus == 'ALL'}">Bạn chưa có đơn hàng nào.</c:when>
                                <c:otherwise>Không có đơn hàng nào ở trạng thái này.</c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach items="${orders}" var="o">
                        <c:url var="detailUrl" value="/orders/detail"><c:param name="id" value="${o.orderId}"/></c:url>
                        <tr>
                            <td class="fw-semibold">#<c:out value="${o.orderId}"/></td>
                            <td><c:out value="${o.orderDateText}"/></td>
                            <td class="text-end">
                                <fmt:formatNumber value="${o.totalAmount}" type="number" maxFractionDigits="0"/> ₫
                            </td>
                            <td><c:out value="${o.paymentLabel}"/></td>
                            <td><span class="badge text-bg-${o.statusBadge}"><c:out value="${o.statusLabel}"/></span></td>
                            <td class="text-end"><a href="${detailUrl}" class="btn btn-outline-primary btn-sm">Xem chi tiết</a></td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
            </tbody>
        </table>
    </div>
</div>

</body>
</html>
