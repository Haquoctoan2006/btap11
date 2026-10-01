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
import vn.ute.entity.Category_24162130;
import vn.ute.exception.ServiceException_24162130;
import vn.ute.service.CategoryService_24162130;
import vn.ute.service.ICategoryService_24162130;
import vn.ute.utils.FileUploadUtil_24162130;

/**
 * Cau 5: CRUD + phan trang cho bang Category (chi Admin duoc vao - AuthFilter chan /admin/*).
 *   GET  /admin/categories        -> danh sach (co phan trang)
 *   GET  /admin/category/add      -> form them moi
 *   POST /admin/category/add      -> luu them moi
 *   GET  /admin/category/edit     -> form chinh sua (?id=)
 *   POST /admin/category/edit     -> luu chinh sua (?id=)
 *   POST /admin/category/delete   -> xoa (?id=)
 */
@WebServlet(urlPatterns = {
        "/admin/categories", "/admin/category/add", "/admin/category/edit", "/admin/category/delete"
})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 10)
public class AdminCategoryController_24162130 extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(AdminCategoryController_24162130.class.getName());

    private static final String LIST_VIEW = "/WEB-INF/views/admin/category-list.jsp";
    private static final String FORM_VIEW = "/WEB-INF/views/admin/category-form.jsp";
    private static final String LIST_URL = "/admin/categories";

    private final transient ICategoryService_24162130 categoryService = new CategoryService_24162130();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        switch (path) {
            case "/admin/category/add" -> showForm(req, resp, null);
            case "/admin/category/edit" -> showForm(req, resp, parseId(req.getParameter("id")));
            default -> showList(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        switch (path) {
            case "/admin/category/add" -> doCreate(req, resp);
            case "/admin/category/edit" -> doUpdate(req, resp);
            case "/admin/category/delete" -> handleDelete(req, resp);
            default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    // ---------------------------------------------------------------- LIST

    private void showList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int page = parsePositiveInt(req.getParameter("page"), AppConstants_24162130.DEFAULT_PAGE);
        int size = parsePositiveInt(req.getParameter("size"), AppConstants_24162130.DEFAULT_PAGE_SIZE);

        req.setAttribute("pageResult", categoryService.getCategoriesPaging(page, size));
        req.getRequestDispatcher(LIST_VIEW).forward(req, resp);
    }

    // ---------------------------------------------------------------- FORM

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Integer categoryId)
            throws ServletException, IOException {
        if (categoryId != null) {
            try {
                req.setAttribute("category", categoryService.getById(categoryId));
            } catch (ServiceException_24162130 e) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
                return;
            }
        }
        req.getRequestDispatcher(FORM_VIEW).forward(req, resp);
    }

    // ---------------------------------------------------------------- CREATE

    private void doCreate(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String name = req.getParameter("categoryName");
        Integer status = parseIntOrNull(req.getParameter("status"));

        try {
            String images = uploadImage(req);
            categoryService.create(name, status, images);
            resp.sendRedirect(req.getContextPath() + LIST_URL);
        } catch (ServiceException_24162130 e) {
            rebindAndForward(req, resp, null, name, status, e.getMessage());
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Loi khi them Category", e);
            rebindAndForward(req, resp, null, name, status, "Có lỗi xảy ra, vui lòng thử lại");
        }
    }

    // ---------------------------------------------------------------- UPDATE

    private void doUpdate(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer categoryId = parseId(req.getParameter("categoryId"));
        String name = req.getParameter("categoryName");
        Integer status = parseIntOrNull(req.getParameter("status"));

        if (categoryId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu mã danh mục");
            return;
        }
        try {
            String images = uploadImage(req);
            categoryService.update(categoryId, name, status, images);
            resp.sendRedirect(req.getContextPath() + LIST_URL);
        } catch (ServiceException_24162130 e) {
            rebindAndForward(req, resp, categoryId, name, status, e.getMessage());
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Loi khi sua Category id=" + categoryId, e);
            rebindAndForward(req, resp, categoryId, name, status, "Có lỗi xảy ra, vui lòng thử lại");
        }
    }

    // ---------------------------------------------------------------- DELETE

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer categoryId = parseId(req.getParameter("id"));
        if (categoryId != null) {
            try {
                categoryService.delete(categoryId);
            } catch (ServiceException_24162130 e) {
                req.getSession().setAttribute("flashError", e.getMessage());
            }
        }
        resp.sendRedirect(req.getContextPath() + LIST_URL);
    }

    // ---------------------------------------------------------------- HELPERS

    /** Tra ve duong dan anh moi neu co chon file, null neu khong chon (giu anh cu). */
    private String uploadImage(HttpServletRequest req) throws IOException, ServletException {
        Part part = req.getPart("images");
        return FileUploadUtil_24162130.saveIfPresent(part, getServletContext());
    }

    private void rebindAndForward(HttpServletRequest req, HttpServletResponse resp, Integer categoryId,
            String name, Integer status, String errorMessage) throws ServletException, IOException {
        Category_24162130 category = new Category_24162130();
        category.setCategoryId(categoryId);
        category.setCategoryName(name);
        category.setStatus(status);
        req.setAttribute("category", category);
        req.setAttribute("error", errorMessage);
        req.getRequestDispatcher(FORM_VIEW).forward(req, resp);
    }

    private Integer parseId(String raw) {
        return parseIntOrNull(raw);
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

    private int parsePositiveInt(String raw, int defaultValue) {
        Integer v = parseIntOrNull(raw);
        return (v == null || v < 1) ? defaultValue : v;
    }
}
