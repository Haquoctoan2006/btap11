<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
            <%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
                <fmt:setLocale value="vi_VN" />
                <c:url var="noImg" value="/assets/images/no-image.svg" />
                <!DOCTYPE html>
                <html>

                <head>
                    <title>Sản phẩm theo cửa hàng</title>
                </head>

                <body>

                    <c:set var="me" value="${sessionScope.currentUser}" />

                    <div class="d-flex flex-wrap justify-content-between align-items-end mb-3">
                        <h2 class="mb-0">Sản phẩm theo cửa hàng</h2>
                        <span class="text-muted">
                            Tổng: <strong>
                                <c:out value="${totalProducts}" />
                            </strong> sản phẩm
                            thuộc <strong>
                                <c:out value="${fn:length(sellerList)}" />
                            </strong> nhóm cửa hàng
                        </span>
                    </div>

                    <c:if test="${not empty error}">
                        <div class="alert alert-danger">
                            <c:out value="${error}" />
                        </div>
                    </c:if>

                    <c:if test="${empty sellerList and empty error}">
                        <div class="alert alert-secondary">Chưa có cửa hàng hoặc sản phẩm nào trong hệ thống.</div>
                    </c:if>

                    <%--============MOI CUA HANG (SELLER) MOT KHOI============--%>
                        <c:forEach items="${sellerList}" var="group">
                            <section class="card shadow-sm mb-4">

                                <%-- Tieu de: Ma cua hang: <sellerId> - <sellername> --%>
                                        <div
                                            class="card-header bg-dark text-white d-flex justify-content-between align-items-center">
                                            <h5 class="mb-0">
                                                <c:choose>
                                                    <c:when test="${group.unassigned}">Sản phẩm chưa thuộc cửa hàng nào
                                                    </c:when>
                                                    <c:otherwise>
                                                        Mã cửa hàng:
                                                        <c:out value="${group.seller.sellerId}" />
                                                        <c:if test="${not empty group.seller.sellername}">
                                                            -
                                                            <c:out value="${group.seller.sellername}" />
                                                        </c:if>
                                                    </c:otherwise>
                                                </c:choose>
                                            </h5>
                                            <span class="badge text-bg-light">
                                                <c:out value="${group.productCount}" /> sản phẩm
                                            </span>
                                        </div>

                                        <div class="card-body">
                                            <c:if test="${empty group.products}">
                                                <p class="text-muted mb-0">Cửa hàng này chưa có sản phẩm nào.</p>
                                            </c:if>

                                            <div class="row g-3">
                                                <c:forEach items="${group.products}" var="p">

                                                    <%-- Hinh anh: URL http(s) dung nguyen ban, ten file thi lay trong
                                                        /assets/images/products/ --%>
                                                        <c:choose>
                                                            <c:when test="${empty p.images}">
                                                                <c:set var="imgSrc" value="${noImg}" />
                                                            </c:when>
                                                            <c:when
                                                                test="${fn:startsWith(p.images, 'http://') or fn:startsWith(p.images, 'https://')}">
                                                                <c:set var="imgSrc" value="${p.images}" />
                                                            </c:when>
                                                            <c:when test="${fn:startsWith(p.images, 'uploads/')}">
                                                                <c:url var="imgSrc" value="/${p.images}" />
                                                            </c:when>
                                                            <c:otherwise>
                                                                <c:url var="imgSrc"
                                                                    value="/assets/images/products/${p.images}" />
                                                            </c:otherwise>
                                                        </c:choose>

                                                        <%-- Link sang trang chi tiet (Cau 4): /product/detail?id=...
                                                            --%>
                                                            <c:url var="detailUrl" value="/product/detail">
                                                                <c:param name="id" value="${p.productId}" />
                                                            </c:url>

                                                            <div class="col-md-6">
                                                                <div class="card h-100">
                                                                    <div class="row g-0 h-100">
                                                                        <div class="col-5">
                                                                            <%-- Bam vao anh cung vao trang chi tiet
                                                                                --%>
                                                                                <a href="${detailUrl}">
                                                                                    <img src="<c:out value='${imgSrc}'/>"
                                                                                        alt="<c:out value='${p.productName}'/>"
                                                                                        class="w-100 h-100 rounded-start"
                                                                                        style="object-fit: cover; min-height: 180px;"
                                                                                        onerror="this.onerror=null;this.src='${noImg}'">
                                                                                </a>
                                                                        </div>
                                                                        <div class="col-7">
                                                                            <div class="card-body py-3">
                                                                                <p class="mb-1">
                                                                                    <span class="text-muted">Tên sản
                                                                                        phẩm:</span>
                                                                                    <a href="${detailUrl}"
                                                                                        class="fw-semibold text-decoration-none">
                                                                                        <c:out
                                                                                            value="${p.productName}" />
                                                                                    </a>
                                                                                </p>
                                                                                <p class="mb-1">
                                                                                    <span class="text-muted">Mã sản
                                                                                        phẩm:</span>
                                                                                    <c:out value="${p.productCode}" />
                                                                                </p>
                                                                                <p class="mb-1">
                                                                                    <span class="text-muted">Danh
                                                                                        mục:</span>
                                                                                    <c:out
                                                                                        value="${p.category.categoryName}"
                                                                                        default="—" />
                                                                                </p>
                                                                                <p class="mb-1">
                                                                                    <span class="text-muted">Giá:</span>
                                                                                    <c:choose>
                                                                                        <c:when
                                                                                            test="${p.price != null}">
                                                                                            <strong class="text-danger">
                                                                                                <fmt:formatNumber
                                                                                                    value="${p.price}"
                                                                                                    type="number"
                                                                                                    maxFractionDigits="0" />
                                                                                                ₫
                                                                                            </strong>
                                                                                        </c:when>
                                                                                        <c:otherwise>—</c:otherwise>
                                                                                    </c:choose>
                                                                                </p>
                                                                                <p class="mb-0">
                                                                                    <span
                                                                                        class="text-muted">Amount:</span>
                                                                                    <c:out value="${p.amount}"
                                                                                        default="—" />
                                                                                </p>

                                                                                <%--============NUT XEM CHI TIET / THEM
                                                                                    VAO GIO============--%>
                                                                                    <div
                                                                                        class="mt-2 d-flex gap-2 align-items-center">
                                                                                        <a href="${detailUrl}"
                                                                                            class="btn btn-sm btn-outline-primary">Xem
                                                                                            chi tiết</a>
                                                                                        <c:choose>
                                                                                            <c:when test="${empty me}">
                                                                                                <a href="<c:url value='/login'/>"
                                                                                                    class="btn btn-sm btn-primary">Đăng
                                                                                                    nhập để mua</a>
                                                                                            </c:when>
                                                                                            <c:when
                                                                                                test="${me.admin or me.seller}">
                                                                                            </c:when>
                                                                                            <c:when
                                                                                                test="${not empty p.stock and p.stock gt 0 and p.status != 0 and not empty p.price}">
                                                                                                <form method="post"
                                                                                                    action="<c:url value='/cart/add'/>"
                                                                                                    class="m-0">
                                                                                                    <input type="hidden"
                                                                                                        name="productId"
                                                                                                        value="${p.productId}">
                                                                                                    <input type="hidden"
                                                                                                        name="quantity"
                                                                                                        value="1">
                                                                                                    <button
                                                                                                        type="submit"
                                                                                                        class="btn btn-sm btn-primary">Thêm
                                                                                                        vào giỏ</button>
                                                                                                </form>
                                                                                            </c:when>
                                                                                            <c:otherwise>
                                                                                                <button type="button"
                                                                                                    class="btn btn-sm btn-secondary"
                                                                                                    disabled>Hết
                                                                                                    hàng</button>
                                                                                            </c:otherwise>
                                                                                        </c:choose>
                                                                                    </div>
                                                                            </div>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                </c:forEach>
                                            </div>
                                        </div>
                            </section>
                        </c:forEach>

                </body>

                </html>