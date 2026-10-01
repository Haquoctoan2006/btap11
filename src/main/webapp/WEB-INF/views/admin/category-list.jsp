<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<c:url var="noImg" value="/assets/images/no-image.svg"/>
<!DOCTYPE html>
<html>
<head><title>Quản lý Danh mục</title></head>
<body>

<div class="d-flex justify-content-between align-items-center mb-3">
    <h3 class="mb-0">Quản lý Danh mục</h3>
    <a href="<c:url value='/admin/category/add'/>" class="btn btn-primary">+ Thêm danh mục</a>
</div>

<c:if test="${not empty sessionScope.flashError}">
    <div class="alert alert-danger">${sessionScope.flashError}</div>
    <c:remove var="flashError" scope="session"/>
</c:if>

<table class="table table-bordered align-middle">
    <thead class="table-light">
    <tr>
        <th style="width:70px;">ID</th>
        <th>Tên danh mục</th>
        <th style="width:120px;">Hình ảnh</th>
        <th style="width:130px;">Trạng thái</th>
        <th style="width:170px;">Action</th>
    </tr>
    </thead>
    <tbody>
    <c:choose>
        <c:when test="${empty pageResult.items}">
            <tr><td colspan="5" class="text-center text-muted py-4">Chưa có danh mục nào.</td></tr>
        </c:when>
        <c:otherwise>
            <c:forEach items="${pageResult.items}" var="c">
                <c:choose>
                    <c:when test="${empty c.images}">
                        <c:set var="imgSrc" value="${noImg}"/>
                    </c:when>
                    <c:when test="${fn:startsWith(c.images, 'http://') or fn:startsWith(c.images, 'https://')}">
                        <c:set var="imgSrc" value="${c.images}"/>
                    </c:when>
                    <c:when test="${fn:startsWith(c.images, 'uploads/')}">
                        <c:url var="imgSrc" value="/${c.images}"/>
                    </c:when>
                    <c:otherwise>
                        <c:url var="imgSrc" value="/assets/images/categories/${c.images}"/>
                    </c:otherwise>
                </c:choose>
                <tr>
                    <td><c:out value="${c.categoryId}"/></td>
                    <td><c:out value="${c.categoryName}"/></td>
                    <td>
                        <img src="<c:out value='${imgSrc}'/>" alt="" width="60" height="60"
                             style="object-fit:cover;" class="rounded border"
                             onerror="this.onerror=null;this.src='${noImg}'">
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${c.status == 1}"><span class="badge text-bg-success">Hoạt động</span></c:when>
                            <c:otherwise><span class="badge text-bg-secondary">Ngừng</span></c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <a class="btn btn-sm btn-outline-primary"
                           href="<c:url value='/admin/category/edit'><c:param name='id' value='${c.categoryId}'/></c:url>">Sửa</a>
                        <form method="post" style="display:inline"
                              action="<c:url value='/admin/category/delete'/>"
                              onsubmit="return confirm('Xóa danh mục #${c.categoryId} - ${c.categoryName}?');">
                            <input type="hidden" name="id" value="${c.categoryId}">
                            <button type="submit" class="btn btn-sm btn-outline-danger">Xóa</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </c:otherwise>
    </c:choose>
    </tbody>
</table>

<%-- ============ PHAN TRANG ============ --%>
<c:if test="${pageResult.totalPages > 1}">
    <nav>
        <ul class="pagination justify-content-center">
            <li class="page-item ${pageResult.hasPrev ? '' : 'disabled'}">
                <a class="page-link" href="<c:url value='/admin/categories'><c:param name='page' value='1'/></c:url>">Trang đầu</a>
            </li>
            <li class="page-item ${pageResult.hasPrev ? '' : 'disabled'}">
                <a class="page-link" href="<c:url value='/admin/categories'><c:param name='page' value='${pageResult.page - 1}'/></c:url>">Trước</a>
            </li>
            <c:forEach begin="1" end="${pageResult.totalPages}" var="i">
                <li class="page-item ${i == pageResult.page ? 'active' : ''}">
                    <a class="page-link" href="<c:url value='/admin/categories'><c:param name='page' value='${i}'/></c:url>"><c:out value="${i}"/></a>
                </li>
            </c:forEach>
            <li class="page-item ${pageResult.hasNext ? '' : 'disabled'}">
                <a class="page-link" href="<c:url value='/admin/categories'><c:param name='page' value='${pageResult.page + 1}'/></c:url>">Sau</a>
            </li>
            <li class="page-item ${pageResult.hasNext ? '' : 'disabled'}">
                <a class="page-link" href="<c:url value='/admin/categories'><c:param name='page' value='${pageResult.totalPages}'/></c:url>">Trang cuối</a>
            </li>
        </ul>
    </nav>
</c:if>
<p class="text-muted text-center small">
    Trang <c:out value="${pageResult.page}"/> / <c:out value="${pageResult.totalPages}"/>
    &nbsp;(&nbsp;<c:out value="${pageResult.totalItems}"/> danh mục&nbsp;)
</p>

</body>
</html>
