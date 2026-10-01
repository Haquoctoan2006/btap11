package vn.ute.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import vn.ute.entity.Product_24162130;
import vn.ute.exception.ServiceException_24162130;
import vn.ute.service.IProductService_24162130;
import vn.ute.service.ProductService_24162130;

/**
 * Cau 4: trang chi tiet 1 san pham (bam vao ten san pham o trang Cau 3).
 *   GET /product/detail?id=xxx
 */
@WebServlet(urlPatterns = { "/product/detail" })
public class ProductDetailController_24162130 extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ProductDetailController_24162130.class.getName());
    private static final String VIEW = "/WEB-INF/views/product/detail.jsp";

    private final transient IProductService_24162130 productService = new ProductService_24162130();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        Integer productId = parseId(idParam);

        // Tham so id rong hoac sai dinh dang so -> 404
        if (productId == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Mã sản phẩm không hợp lệ");
            return;
        }

        try {
            Product_24162130 product = productService.getProductDetail(productId);
            req.setAttribute("product", product);
            req.getRequestDispatcher(VIEW).forward(req, resp);
        } catch (ServiceException_24162130 e) {
            // Khong tim thay san pham -> 404
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Loi khi tai chi tiet san pham id=" + idParam, e);
            req.setAttribute(RequestDispatcher.ERROR_MESSAGE, "Không tải được thông tin sản phẩm");
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private Integer parseId(String idParam) {
        if (idParam == null || idParam.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(idParam.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
