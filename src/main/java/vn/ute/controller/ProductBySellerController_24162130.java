package vn.ute.controller;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import vn.ute.dto.SellerProductGroup_24162130;
import vn.ute.service.IProductService_24162130;
import vn.ute.service.ProductService_24162130;

/**
 * Cau 3: hien thi tat ca san pham gom theo tung cua hang (sellerId).
 *   GET /products  hoac  GET /products/by-seller
 */
@WebServlet(urlPatterns = { "/products", "/products/by-seller" })
public class ProductBySellerController_24162130 extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ProductBySellerController_24162130.class.getName());
    private static final String VIEW = "/WEB-INF/views/product/list-by-seller.jsp";

    private final transient IProductService_24162130 productService = new ProductService_24162130();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<SellerProductGroup_24162130> sellerList = productService.getProductsGroupedBySeller();
            int total = sellerList.stream().mapToInt(SellerProductGroup_24162130::getProductCount).sum();

            req.setAttribute("sellerList", sellerList);
            req.setAttribute("totalProducts", total);
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Loi khi tai danh sach san pham theo cua hang", e);
            req.setAttribute("sellerList", List.of());
            req.setAttribute("totalProducts", 0);
            req.setAttribute("error", "Không tải được danh sách sản phẩm, vui lòng thử lại sau");
        }
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }
}
