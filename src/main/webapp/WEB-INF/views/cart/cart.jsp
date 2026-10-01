<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<fmt:setLocale value="vi_VN"/>
<c:url var="noImg" value="/assets/images/no-image.svg"/>
<!DOCTYPE html>
<html>
<head><title>Giỏ hàng</title></head>
<body>

<div class="d-flex justify-content-between align-items-center mb-3">
    <h2 class="mb-0">Giỏ hàng của bạn</h2>
    <a href="<c:url value='/products'/>" class="btn btn-outline-secondary">&laquo; Tiếp tục mua sắm</a>
</div>

<%@ include file="/WEB-INF/views/common/flash.jspf" %>
<c:if test="${not empty error}">
    <div class="alert alert-danger"><c:out value="${error}"/></div>
</c:if>

<c:choose>
    <c:when test="${empty cart or empty cart.lines}">
        <div class="card shadow-sm">
            <div class="card-body text-center py-5">
                <p class="lead text-muted mb-3">Giỏ hàng của bạn đang trống.</p>
                <a href="<c:url value='/products'/>" class="btn btn-primary">Xem sản phẩm</a>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <c:if test="${cart.hasIssue}">
            <div class="alert alert-warning">
                Có sản phẩm hết hàng hoặc vượt tồn kho (xem cột Số lượng). Hãy chỉnh số lượng hoặc xóa sản phẩm đó trước khi thanh toán.
            </div>
        </c:if>

        <div class="card shadow-sm mb-3">
            <div class="table-responsive">
                <table class="table align-middle mb-0">
                    <thead class="table-light">
                    <tr>
                        <th style="width:90px;">Ảnh</th>
                        <th>Sản phẩm</th>
                        <th class="text-end" style="width:140px;">Đơn giá</th>
                        <th style="width:330px;">Số lượng</th>
                        <th class="text-center" style="width:90px;">Tồn kho</th>
                        <th class="text-end" style="width:150px;">Thành tiền</th>
                        <th style="width:90px;"></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${cart.lines}" var="line">
                        <c:set var="pImages" value="${line.images}"/>
                        <%@ include file="/WEB-INF/views/common/product-image.jspf" %>
                        <c:url var="detailUrl" value="/product/detail">
                            <c:param name="id" value="${line.productId}"/>
                        </c:url>
                        <tr class="${line.hasIssue ? 'table-warning' : ''}">
                            <td>
                                <img src="<c:out value='${imgSrc}'/>" alt="<c:out value='${line.productName}'/>"
                                     class="rounded border" style="width:70px;height:70px;object-fit:cover;"
                                     onerror="this.onerror=null;this.src='${noImg}'">
                            </td>
                            <td>
                                <a href="${detailUrl}" class="fw-semibold text-decoration-none">
                                    <c:out value="${line.productName}"/>
                                </a>
                                <c:if test="${line.hasIssue}">
                                    <div class="small text-danger fw-semibold"><c:out value="${line.issue}"/></div>
                                </c:if>
                                <c:if test="${line.priceChanged}">
                                    <div class="small text-muted">
                                        Giá hiện tại:
                                        <fmt:formatNumber value="${line.currentPrice}" type="number" maxFractionDigits="0"/> ₫
                                    </div>
                                </c:if>
                            </td>
                            <td class="text-end">
                                <fmt:formatNumber value="${line.unitPrice}" type="number" maxFractionDigits="0"/> ₫
                            </td>
                            <td>
                                <div class="d-flex align-items-center gap-1">
                                    <%-- Giam 1 --%>
                                    <form method="post" action="<c:url value='/cart/update'/>" class="m-0">
                                        <input type="hidden" name="cartItemId" value="${line.cartItemId}">
                                        <input type="hidden" name="quantity" value="${line.quantity - 1}">
                                        <button type="submit" class="btn btn-outline-secondary btn-sm"
                                                ${line.quantity le 1 ? 'disabled' : ''} title="Giảm">&minus;</button>
                                    </form>
                                    <%-- Nhap so luong --%>
                                    <form method="post" action="<c:url value='/cart/update'/>" class="m-0 d-flex gap-1">
                                        <input type="hidden" name="cartItemId" value="${line.cartItemId}">
                                        <input type="number" name="quantity" value="${line.quantity}" min="1"
                                               <c:if test="${line.stock gt 0}">max="${line.stock}"</c:if>
                                               class="form-control form-control-sm text-center" style="width:75px;" required>
                                        <button type="submit" class="btn btn-outline-primary btn-sm">Cập nhật</button>
                                    </form>
                                    <%-- Tang 1 --%>
                                    <form method="post" action="<c:url value='/cart/update'/>" class="m-0">
                                        <input type="hidden" name="cartItemId" value="${line.cartItemId}">
                                        <input type="hidden" name="quantity" value="${line.quantity + 1}">
                                        <button type="submit" class="btn btn-outline-secondary btn-sm"
                                                ${line.quantity ge line.stock ? 'disabled' : ''} title="Tăng">+</button>
                                    </form>
                                </div>
                            </td>
                            <td class="text-center">
                                <c:choose>
                                    <c:when test="${line.stock le 0}"><span class="badge text-bg-danger">Hết hàng</span></c:when>
                                    <c:otherwise><c:out value="${line.stock}"/></c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-end fw-semibold">
                                <fmt:formatNumber value="${line.subtotal}" type="number" maxFractionDigits="0"/> ₫
                            </td>
                            <td class="text-end">
                                <form method="post" action="<c:url value='/cart/remove'/>" class="m-0"
                                      onsubmit="return confirm('Xóa sản phẩm này khỏi giỏ hàng?');">
                                    <input type="hidden" name="cartItemId" value="${line.cartItemId}">
                                    <button type="submit" class="btn btn-outline-danger btn-sm">Xóa</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>

        <div class="card shadow-sm">
            <div class="card-body d-flex flex-wrap justify-content-between align-items-center gap-3">
                <form method="post" action="<c:url value='/cart/clear'/>" class="m-0"
                      onsubmit="return confirm('Xóa toàn bộ giỏ hàng?');">
                    <button type="submit" class="btn btn-outline-danger">Xóa toàn bộ giỏ hàng</button>
                </form>
                <div class="d-flex align-items-center gap-4">
                    <div class="text-end">
                        <div class="text-muted small">
                            Tổng cộng (<c:out value="${cart.totalQuantity}"/> sản phẩm)
                        </div>
                        <div class="fs-4 fw-bold text-danger">
                            <fmt:formatNumber value="${cart.total}" type="number" maxFractionDigits="0"/> ₫
                        </div>
                    </div>
                    <c:choose>
                        <c:when test="${cart.hasIssue}">
                            <button type="button" class="btn btn-success btn-lg" disabled>Thanh toán</button>
                        </c:when>
                        <c:otherwise>
                            <a href="<c:url value='/checkout'/>" class="btn btn-success btn-lg">Thanh toán</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </c:otherwise>
</c:choose>

</body>
</html>
