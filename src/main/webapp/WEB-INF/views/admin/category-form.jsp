<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="isEdit" value="${not empty category and not empty category.categoryId}"/>
<c:url var="noImg" value="/assets/images/no-image.svg"/>
<!DOCTYPE html>
<html>
<head><title><c:out value="${isEdit ? 'Sửa danh mục' : 'Thêm danh mục'}"/></title></head>
<body>

<h3 class="mb-3"><c:out value="${isEdit ? 'Sửa danh mục' : 'Thêm danh mục mới'}"/></h3>

<c:if test="${not empty error}">
    <div class="alert alert-danger"><c:out value="${error}"/></div>
</c:if>

<form method="post" class="needs-validation" novalidate enctype="multipart/form-data"
      action="<c:url value='${isEdit ? "/admin/category/edit" : "/admin/category/add"}'/>">

    <c:if test="${isEdit}">
        <input type="hidden" name="categoryId" value="${category.categoryId}">
    </c:if>

    <div class="mb-3">
        <label class="form-label">Tên danh mục <span class="text-danger">*</span></label>
        <input type="text" name="categoryName" class="form-control" required maxlength="200"
               value="<c:out value='${category.categoryName}'/>">
        <div class="invalid-feedback">Vui lòng nhập tên danh mục.</div>
    </div>

    <div class="mb-3">
        <label class="form-label">Trạng thái</label>
        <select name="status" class="form-select">
            <option value="1" ${empty category.status or category.status == 1 ? 'selected' : ''}>Hoạt động</option>
            <option value="0" ${category.status == 0 ? 'selected' : ''}>Ngừng hoạt động</option>
        </select>
    </div>

    <div class="mb-3">
        <label class="form-label">Hình ảnh</label>
        <c:if test="${isEdit and not empty category.images}">
            <div class="mb-2">
                <img src="<c:url value='/${category.images}'/>" width="90" height="90"
                     style="object-fit:cover;" class="rounded border"
                     onerror="this.onerror=null;this.src='${noImg}'">
            </div>
        </c:if>
        <input type="file" name="images" class="form-control" accept="image/*">
        <div class="form-text">
            <c:choose>
                <c:when test="${isEdit}">Không chọn ảnh mới nếu muốn giữ nguyên ảnh cũ.</c:when>
                <c:otherwise>Chọn ảnh danh mục (không bắt buộc).</c:otherwise>
            </c:choose>
        </div>
    </div>

    <button type="submit" class="btn btn-primary">Lưu</button>
    <a href="<c:url value='/admin/categories'/>" class="btn btn-outline-secondary">Hủy</a>
</form>

</body>
</html>
