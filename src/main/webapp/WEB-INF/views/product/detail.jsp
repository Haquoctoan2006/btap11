<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<fmt:setLocale value="vi_VN"/>
<c:url var="noImg" value="/assets/images/no-image.svg"/>

<%-- Xu ly duong dan hinh anh: URL http(s) dung nguyen ban; "uploads/..." (anh moi upload o Cau 5)
     hoac ten file (anh mau co san) deu quy ve duong dan tinh trong webapp --%>
<c:choose>
    <c:when test="${empty product.images}">
        <c:set var="imgSrc" value="${noImg}"/>
    </c:when>
    <c:when test="${fn:startsWith(product.images, 'http://') or fn:startsWith(product.images, 'https://')}">
        <c:set var="imgSrc" value="${product.images}"/>
    </c:when>
    <c:when test="${fn:startsWith(product.images, 'uploads/')}">
        <c:url var="imgSrc" value="/${product.images}"/>
    </c:when>
    <c:otherwise>
        <c:url var="imgSrc" value="/assets/images/products/${product.images}"/>
    </c:otherwise>
</c:choose>

<!DOCTYPE html>
<html>
<head><title>Chi tiết sản phẩm - <c:out value="${product.productName}"/></title></head>
<body>

<c:set var="me" value="${sessionScope.currentUser}"/>
<c:set var="stockQty" value="${empty product.stock ? 0 : product.stock}"/>
<c:set var="sellable" value="${stockQty gt 0 and product.status != 0 and not empty product.price}"/>

<%@ include file="/WEB-INF/views/common/flash.jspf" %>

<nav aria-label="breadcrumb" class="mb-3">
    <ol class="breadcrumb">
        <li class="breadcrumb-item"><a href="<c:url value='/home'/>">Trang chủ</a></li>
        <li class="breadcrumb-item"><a href="<c:url value='/products'/>">Sản phẩm</a></li>
        <li class="breadcrumb-item active" aria-current="page"><c:out value="${product.productName}"/></li>
    </ol>
</nav>

<div class="card shadow-sm">
    <div class="card-body">
        <div class="row g-4">
            <%-- ============ [imageLink] ============ --%>
            <div class="col-md-5">
                <img src="<c:out value='${imgSrc}'/>"
                     alt="<c:out value='${product.productName}'/>"
                     class="img-fluid rounded border w-100"
                     style="object-fit: cover; max-height: 420px;"
                     onerror="this.onerror=null;this.src='${noImg}'">
            </div>

            <%-- ============ THONG TIN SAN PHAM ============ --%>
            <div class="col-md-7">
                <h3 class="mb-3"><c:out value="${product.productName}"/></h3>

                <table class="table table-borderless mb-3">
                    <tbody>
                    <tr>
                        <th style="width: 180px;" class="text-muted fw-normal">Tên sản phẩm:</th>
                        <td class="fw-semibold"><c:out value="${product.productName}"/></td>
                    </tr>
                    <tr>
                        <th class="text-muted fw-normal">Mã sản phẩm:</th>
                        <td><c:out value="${product.productCode}" default="—"/></td>
                    </tr>
                    <tr>
                        <th class="text-muted fw-normal">Danh mục:</th>
                        <td><c:out value="${product.category.categoryName}" default="—"/></td>
                    </tr>
                    <tr>
                        <th class="text-muted fw-normal">Giá:</th>
                        <td>
                            <c:choose>
                                <c:when test="${product.price != null}">
                                    <strong class="text-danger fs-5">
                                        <fmt:formatNumber value="${product.price}" type="number"
                                                           maxFractionDigits="0"/> ₫
                                    </strong>
                                </c:when>
                                <c:otherwise>—</c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <th class="text-muted fw-normal">Tồn kho:</th>
                        <td>
                            <c:choose>
                                <c:when test="${stockQty gt 0}"><c:out value="${stockQty}"/> sản phẩm</c:when>
                                <c:otherwise><span class="badge text-bg-danger">Hết hàng</span></c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    <tr>
                        <th class="text-muted fw-normal">Amount:</th>
                        <td><c:out value="${product.amount}" default="—"/></td>
                    </tr>
                    <tr>
                        <th class="text-muted fw-normal">Cửa hàng:</th>
                        <td>
                            <c:choose>
                                <c:when test="${not empty product.seller}">
                                    Mã cửa hàng: <c:out value="${product.seller.sellerId}"/>
                                    <c:if test="${not empty product.seller.sellername}">
                                        - <c:out value="${product.seller.sellername}"/>
                                    </c:if>
                                </c:when>
                                <c:otherwise>—</c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                    </tbody>
                </table>

                <h5 class="text-muted">Description:</h5>
                <p style="white-space: pre-line;"><c:out value="${product.description}" default="Chưa có mô tả."/></p>

                <%-- ============ THEM VAO GIO HANG ============ --%>
                <div class="my-3">
                    <c:choose>
                        <c:when test="${empty me}">
                            <a href="<c:url value='/login'/>" class="btn btn-primary">Đăng nhập để thêm vào giỏ hàng</a>
                        </c:when>
                        <c:when test="${me.admin or me.seller}">
                            <span class="text-muted">Chức năng mua hàng chỉ dành cho tài khoản User.</span>
                        </c:when>
                        <c:when test="${sellable}">
                            <form method="post" action="<c:url value='/cart/add'/>" class="d-flex align-items-center gap-2">
                                <input type="hidden" name="productId" value="${product.productId}">
                                <label for="qty" class="mb-0 text-muted">Số lượng:</label>
                                <input type="number" id="qty" name="quantity" value="1" min="1" max="${stockQty}"
                                       class="form-control" style="width:90px;" required>
                                <button type="submit" class="btn btn-primary">Thêm vào giỏ hàng</button>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <button type="button" class="btn btn-secondary" disabled>Hết hàng</button>
                        </c:otherwise>
                    </c:choose>
                </div>

                <a href="<c:url value='/products'/>" class="btn btn-outline-secondary mt-2">
                    &laquo; Quay lại danh sách sản phẩm
                </a>
            </div>
        </div>
    </div>
</div>

</body>
</html>
