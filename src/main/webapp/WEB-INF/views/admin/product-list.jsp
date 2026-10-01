<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<fmt:setLocale value="vi_VN"/>
<c:url var="noImg" value="/assets/images/no-image.svg"/>
<!DOCTYPE html>
<html>
<head><title>Quản lý Sản phẩm</title></head>
<body>

<div class="d-flex justify-content-between align-items-center mb-3">
    <h3 class="mb-0">Quản lý Sản phẩm</h3>
    <a href="<c:url value='/admin/product/add'/>" class="btn btn-primary">+ Thêm sản phẩm</a>
</div>

<c:if test="${not empty sessionScope.flashError}">
    <div class="alert alert-danger">${sessionScope.flashError}</div>
    <c:remove var="flashError" scope="session"/>
</c:if>

<div class="table-responsive">
<table class="table table-bordered align-middle">
    <thead class="table-light">
    <tr>
        <th style="width:60px;">ID</th>
        <th style="width:70px;">Ảnh</th>
        <th style="width:110px;">Mã SP</th>
        <th>Tên sản phẩm</th>
        <th style="width:130px;">Giá</th>
        <th style="width:140px;">Danh mục</th>
        <th style="width:140px;">Cửa hàng</th>
        <th style="width:110px;">Trạng thái</th>
        <th style="width:170px;">Action</th>
    </tr>
    </thead>
    <tbody>
    <c:choose>
        <c:when test="${empty pageResult.items}">
            <tr><td colspan="9" class="text-center text-muted py-4">Chưa có sản phẩm nào.</td></tr>
        </c:when>
        <c:otherwise>
            <c:forEach items="${pageResult.items}" var="p">
                <c:choose>
                    <c:when test="${empty p.images}">
                        <c:set var="imgSrc" value="${noImg}"/>
                    </c:when>
                    <c:when test="${fn:startsWith(p.images, 'http://') or fn:startsWith(p.images, 'https://')}">
                        <c:set var="imgSrc" value="${p.images}"/>
                    </c:when>
                    <c:when test="${fn:startsWith(p.images, 'uploads/')}">
                        <c:url var="imgSrc" value="/${p.images}"/>
                    </c:when>
                    <c:otherwise>
                        <c:url var="imgSrc" value="/assets/images/products/${p.images}"/>
                    </c:otherwise>
                </c:choose>
                <tr>
                    <td><c:out value="${p.productId}"/></td>
                    <td>
                        <img src="<c:out value='${imgSrc}'/>" alt="" width="50" height="50"
                             style="object-fit:cover;" class="rounded border"
                             onerror="this.onerror=null;this.src='${noImg}'">
                    </td>
                    <td><c:out value="${p.productCode}" default="—"/></td>
                    <td><c:out value="${p.productName}"/></td>
                    <td>
                        <c:choose>
                            <c:when test="${p.price != null}">
                                <fmt:formatNumber value="${p.price}" type="number" maxFractionDigits="0"/> ₫
                            </c:when>
                            <c:otherwise>—</c:otherwise>
                        </c:choose>
                    </td>
                    <td><c:out value="${p.category.categoryName}" default="—"/></td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty p.seller}">
                                #<c:out value="${p.seller.sellerId}"/> <c:out value="${p.seller.sellername}"/>
                            </c:when>
                            <c:otherwise>—</c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${p.status == 1}"><span class="badge text-bg-success">Hoạt động</span></c:when>
                            <c:otherwise><span class="badge text-bg-secondary">Ngừng</span></c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <a class="btn btn-sm btn-outline-primary"
                           href="<c:url value='/admin/product/edit'><c:param name='id' value='${p.productId}'/></c:url>">Sửa</a>
                        <form method="post" style="display:inline"
                              action="<c:url value='/admin/product/delete'/>"
                              onsubmit="return confirm('Xóa sản phẩm #${p.productId} - ${p.productName}?');">
                            <input type="hidden" name="id" value="${p.productId}">
                            <button type="submit" class="btn btn-sm btn-outline-danger">Xóa</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </c:otherwise>
    </c:choose>
    </tbody>
</table>
</div>

<%-- ============ PHAN TRANG ============ --%>
<c:if test="${pageResult.totalPages > 1}">
    <nav>
        <ul class="pagination justify-content-center">
            <li class="page-item ${pageResult.hasPrev ? '' : 'disabled'}">
                <a class="page-link" href="<c:url value='/admin/products'><c:param name='page' value='1'/></c:url>">Trang đầu</a>
            </li>
            <li class="page-item ${pageResult.hasPrev ? '' : 'disabled'}">
                <a class="page-link" href="<c:url value='/admin/products'><c:param name='page' value='${pageResult.page - 1}'/></c:url>">Trước</a>
            </li>
            <c:forEach begin="1" end="${pageResult.totalPages}" var="i">
                <li class="page-item ${i == pageResult.page ? 'active' : ''}">
                    <a class="page-link" href="<c:url value='/admin/products'><c:param name='page' value='${i}'/></c:url>"><c:out value="${i}"/></a>
                </li>
            </c:forEach>
            <li class="page-item ${pageResult.hasNext ? '' : 'disabled'}">
                <a class="page-link" href="<c:url value='/admin/products'><c:param name='page' value='${pageResult.page + 1}'/></c:url>">Sau</a>
            </li>
            <li class="page-item ${pageResult.hasNext ? '' : 'disabled'}">
                <a class="page-link" href="<c:url value='/admin/products'><c:param name='page' value='${pageResult.totalPages}'/></c:url>">Trang cuối</a>
            </li>
        </ul>
    </nav>
</c:if>
<p class="text-muted text-center small">
    Trang <c:out value="${pageResult.page}"/> / <c:out value="${pageResult.totalPages}"/>
    &nbsp;(&nbsp;<c:out value="${pageResult.totalItems}"/> sản phẩm&nbsp;)
</p>

</body>
</html>
