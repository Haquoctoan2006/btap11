package vn.ute.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import vn.ute.constants.AppConstants_24162130;
import vn.ute.entity.Product_24162130;
import vn.ute.exception.ServiceException_24162130;
import vn.ute.service.IProductService_24162130;
import vn.ute.service.ProductService_24162130;
import vn.ute.utils.FileUploadUtil_24162130;

/**
 * Cau 5: CRUD + phan trang cho bang Product (chi Admin duoc vao - AuthFilter chan /admin/*).
 *   GET  /admin/products        -> danh sach (co phan trang)
 *   GET  /admin/product/add     -> form them moi
 *   POST /admin/product/add     -> luu them moi
 *   GET  /admin/product/edit    -> form chinh sua (?id=)
 *   POST /admin/product/edit    -> luu chinh sua (?id=)
 *   POST /admin/product/delete  -> xoa (?id=)
 */
@WebServlet(urlPatterns = {
        "/admin/products", "/admin/product/add", "/admin/product/edit", "/admin/product/delete"
})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 10)
public class AdminProductController_24162130 extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(AdminProductController_24162130.class.getName());

    private static final String LIST_VIEW = "/WEB-INF/views/admin/product-list.jsp";
    private static final String FORM_VIEW = "/WEB-INF/views/admin/product-form.jsp";
    private static final String LIST_URL = "/admin/products";

    private final transient IProductService_24162130 productService = new ProductService_24162130();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        switch (path) {
            case "/admin/product/add" -> showForm(req, resp, null);
            case "/admin/product/edit" -> showForm(req, resp, parseIntOrNull(req.getParameter("id")));
            default -> showList(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        switch (path) {
            case "/admin/product/add" -> doCreate(req, resp);
            case "/admin/product/edit" -> doUpdate(req, resp);
            case "/admin/product/delete" -> handleDelete(req, resp);
            default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    // ---------------------------------------------------------------- LIST

    private void showList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int page = parsePositiveInt(req.getParameter("page"), AppConstants_24162130.DEFAULT_PAGE);
        int size = parsePositiveInt(req.getParameter("size"), AppConstants_24162130.DEFAULT_PAGE_SIZE);

        req.setAttribute("pageResult", productService.getProductsPaging(page, size));
        req.getRequestDispatcher(LIST_VIEW).forward(req, resp);
    }

    // ---------------------------------------------------------------- FORM

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Integer productId)
            throws ServletException, IOException {
        if (productId != null) {
            try {
                req.setAttribute("product", productService.getProductDetail(productId));
            } catch (ServiceException_24162130 e) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
                return;
            }
        }
        req.setAttribute("categories", productService.getAllCategories());
        req.setAttribute("sellers", productService.getAllSellers());
        req.getRequestDispatcher(FORM_VIEW).forward(req, resp);
    }

    // ---------------------------------------------------------------- CREATE

    private void doCreate(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        FormInput in = readForm(req);
        try {
            String images = uploadImage(req);
            productService.createProduct(in.productName, in.productCode, in.categoryId, in.sellerId,
                    in.description, in.price, in.amount, in.stock, in.status, images);
            resp.sendRedirect(req.getContextPath() + LIST_URL);
        } catch (ServiceException_24162130 e) {
            rebindAndForward(req, resp, null, in, e.getMessage());
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Loi khi them Product", e);
            rebindAndForward(req, resp, null, in, "Có lỗi xảy ra, vui lòng thử lại");
        }
    }

    // ---------------------------------------------------------------- UPDATE

    private void doUpdate(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer productId = parseIntOrNull(req.getParameter("productId"));
        FormInput in = readForm(req);

        if (productId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu mã sản phẩm");
            return;
        }
        try {
            String images = uploadImage(req);
            productService.updateProduct(productId, in.productName, in.productCode, in.categoryId, in.sellerId,
                    in.description, in.price, in.amount, in.stock, in.status, images);
            resp.sendRedirect(req.getContextPath() + LIST_URL);
        } catch (ServiceException_24162130 e) {
            rebindAndForward(req, resp, productId, in, e.getMessage());
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Loi khi sua Product id=" + productId, e);
            rebindAndForward(req, resp, productId, in, "Có lỗi xảy ra, vui lòng thử lại");
        }
    }

    // ---------------------------------------------------------------- DELETE

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer productId = parseIntOrNull(req.getParameter("id"));
        if (productId != null) {
            try {
                productService.deleteProduct(productId);
            } catch (ServiceException_24162130 e) {
                req.getSession().setAttribute("flashError", e.getMessage());
            }
        }
        resp.sendRedirect(req.getContextPath() + LIST_URL);
    }

    // ---------------------------------------------------------------- HELPERS

    private String uploadImage(HttpServletRequest req) throws IOException, ServletException {
        Part part = req.getPart("images");
        return FileUploadUtil_24162130.saveIfPresent(part, getServletContext());
    }

    private FormInput readForm(HttpServletRequest req) {
        FormInput in = new FormInput();
        in.productName = req.getParameter("productName");
        in.description = req.getParameter("description");
        in.productCode = parseLongOrNull(req.getParameter("productCode"));
        in.categoryId = parseIntOrNull(req.getParameter("categoryId"));
        in.sellerId = parseIntOrNull(req.getParameter("sellerId"));
        in.price = parseDoubleOrNull(req.getParameter("price"));
        in.amount = parseIntOrNull(req.getParameter("amount"));
        in.stock = parseIntOrNull(req.getParameter("stock"));
        in.status = parseIntOrNull(req.getParameter("status"));
        return in;
    }

    private void rebindAndForward(HttpServletRequest req, HttpServletResponse resp, Integer productId,
            FormInput in, String errorMessage) throws ServletException, IOException {
        Product_24162130 product = new Product_24162130();
        product.setProductId(productId);
        product.setProductName(in.productName);
        product.setProductCode(in.productCode);
        product.setDescription(in.description);
        product.setPrice(in.price);
        product.setAmount(in.amount);
        product.setStock(in.stock);
        product.setStatus(in.status);

        req.setAttribute("product", product);
        req.setAttribute("selectedCategoryId", in.categoryId);
        req.setAttribute("selectedSellerId", in.sellerId);
        req.setAttribute("categories", productService.getAllCategories());
        req.setAttribute("sellers", productService.getAllSellers());
        req.setAttribute("error", errorMessage);
        req.getRequestDispatcher(FORM_VIEW).forward(req, resp);
    }

    private Integer parseIntOrNull(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(raw.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Long parseLongOrNull(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(raw.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parseDoubleOrNull(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return Double.valueOf(raw.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private int parsePositiveInt(String raw, int defaultValue) {
        Integer v = parseIntOrNull(raw);
        return (v == null || v < 1) ? defaultValue : v;
    }

    /** Gom du lieu tho tu form de truyen qua lai giua cac ham xu ly. */
    private static class FormInput {
        String productName;
        String description;
        Long productCode;
        Integer categoryId;
        Integer sellerId;
        Double price;
        Integer amount;
        Integer stock;
        Integer status;
    }
}
