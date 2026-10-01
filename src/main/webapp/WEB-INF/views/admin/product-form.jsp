<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:set var="isEdit" value="${not empty product and not empty product.productId}"/>
<c:url var="noImg" value="/assets/images/no-image.svg"/>
<%-- Khi loi validate, id danh muc/cua hang da chon duoc luu o selectedCategoryId/selectedSellerId;
     khi mo form sua binh thuong thi lay tu product.category / product.seller --%>
<c:set var="curCategoryId" value="${not empty selectedCategoryId ? selectedCategoryId : product.category.categoryId}"/>
<c:set var="curSellerId" value="${not empty selectedSellerId ? selectedSellerId : product.seller.sellerId}"/>
<!DOCTYPE html>
<html>
<head><title><c:out value="${isEdit ? 'Sửa sản phẩm' : 'Thêm sản phẩm'}"/></title></head>
<body>

<h3 class="mb-3"><c:out value="${isEdit ? 'Sửa sản phẩm' : 'Thêm sản phẩm mới'}"/></h3>

<c:if test="${not empty error}">
    <div class="alert alert-danger"><c:out value="${error}"/></div>
</c:if>

<form method="post" class="needs-validation" novalidate enctype="multipart/form-data"
      action="<c:url value='${isEdit ? "/admin/product/edit" : "/admin/product/add"}'/>">

    <c:if test="${isEdit}">
        <input type="hidden" name="productId" value="${product.productId}">
    </c:if>

    <div class="row g-3">
        <div class="col-md-8">
            <label class="form-label">Tên sản phẩm <span class="text-danger">*</span></label>
            <input type="text" name="productName" class="form-control" required maxlength="200"
                   value="<c:out value='${product.productName}'/>">
            <div class="invalid-feedback">Vui lòng nhập tên sản phẩm.</div>
        </div>

        <div class="col-md-4">
            <label class="form-label">Mã sản phẩm</label>
            <input type="number" name="productCode" class="form-control"
                   value="<c:out value='${product.productCode}'/>">
        </div>

        <div class="col-md-6">
            <label class="form-label">Danh mục</label>
            <select name="categoryId" class="form-select">
                <option value="">-- Chọn danh mục --</option>
                <c:forEach items="${categories}" var="cate">
                    <option value="${cate.categoryId}" ${cate.categoryId == curCategoryId ? 'selected' : ''}>
                        <c:out value="${cate.categoryName}"/>
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="col-md-6">
            <label class="form-label">Cửa hàng (Seller)</label>
            <select name="sellerId" class="form-select">
                <option value="">-- Chọn cửa hàng --</option>
                <c:forEach items="${sellers}" var="s">
                    <option value="${s.sellerId}" ${s.sellerId == curSellerId ? 'selected' : ''}>
                        #<c:out value="${s.sellerId}"/> - <c:out value="${s.sellername}"/>
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="col-md-4">
            <label class="form-label">Giá</label>
            <input type="number" step="0.01" min="0" name="price" class="form-control"
                   value="<c:out value='${product.price}'/>">
        </div>

        <div class="col-md-4">
            <label class="form-label">Amount</label>
            <input type="number" min="0" name="amount" class="form-control"
                   value="<c:out value='${product.amount}'/>">
        </div>

        <div class="col-md-4">
            <label class="form-label">Tồn kho (stock)</label>
            <input type="number" min="0" name="stock" class="form-control"
                   value="<c:out value='${product.stock}'/>">
        </div>

        <div class="col-md-4">
            <label class="form-label">Trạng thái</label>
            <select name="status" class="form-select">
                <option value="1" ${empty product.status or product.status == 1 ? 'selected' : ''}>Hoạt động</option>
                <option value="0" ${product.status == 0 ? 'selected' : ''}>Ngừng bán</option>
            </select>
        </div>

        <div class="col-md-8">
            <label class="form-label">Hình ảnh</label>
            <c:if test="${isEdit and not empty product.images}">
                <div class="mb-2">
                    <img src="<c:url value='/${product.images}'/>" width="90" height="90"
                         style="object-fit:cover;" class="rounded border"
                         onerror="this.onerror=null;this.src='${noImg}'">
                </div>
            </c:if>
            <input type="file" name="images" class="form-control" accept="image/*">
            <div class="form-text">
                <c:choose>
                    <c:when test="${isEdit}">Không chọn ảnh mới nếu muốn giữ nguyên ảnh cũ.</c:when>
                    <c:otherwise>Chọn ảnh sản phẩm (không bắt buộc).</c:otherwise>
                </c:choose>
            </div>
        </div>

        <div class="col-12">
            <label class="form-label">Description</label>
            <textarea name="description" class="form-control" rows="4" maxlength="500"><c:out value="${product.description}"/></textarea>
        </div>
    </div>

    <div class="mt-3">
        <button type="submit" class="btn btn-primary">Lưu</button>
        <a href="<c:url value='/admin/products'/>" class="btn btn-outline-secondary">Hủy</a>
    </div>
</form>

</body>
</html>
