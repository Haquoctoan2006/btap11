package vn.ute.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import vn.ute.constants.AppConstants_24162130;
import vn.ute.dto.SessionUser_24162130;
import vn.ute.exception.ServiceException_24162130;
import vn.ute.service.CartService_24162130;
import vn.ute.service.ICartService_24162130;

/**
 * Gio hang cua User (AuthFilter da bat buoc dang nhap + vai tro User cho /cart/*).
 *   GET  /cart          : xem gio hang
 *   POST /cart/add      : them san pham (productId, quantity)
 *   POST /cart/update   : doi so luong (cartItemId, quantity)
 *   POST /cart/remove   : xoa 1 dong (cartItemId)
 *   POST /cart/clear    : xoa toan bo gio
 * userId luon lay tu Session, khong bao gio doc tu request.
 */
@WebServlet(urlPatterns = { "/cart", "/cart/add", "/cart/update", "/cart/remove", "/cart/clear" })
public class CartController_24162130 extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(CartController_24162130.class.getName());
    private static final String VIEW = "/WEB-INF/views/cart/cart.jsp";

    private final transient ICartService_24162130 cartService = new CartService_24162130();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!"/cart".equals(req.getServletPath())) {
            // Mo thang /cart/add... bang GET: ve trang gio hang
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }
        SessionUser_24162130 user = currentUser(req);
        try {
            req.setAttribute("cart", cartService.getCart(user.getUserId()));
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Loi khi tai gio hang userId=" + user.getUserId(), e);
            req.setAttribute("error", "Không tải được giỏ hàng, vui lòng thử lại sau");
        }
        req.getRequestDispatcher(VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SessionUser_24162130 user = currentUser(req);
        HttpSession session = req.getSession();
        String ctx = req.getContextPath();

        switch (req.getServletPath()) {
            case "/cart/add" -> {
                Integer productId = parseInt(req.getParameter("productId"));
                Integer qty = req.getParameter("quantity") == null ? Integer.valueOf(1)
                        : parseInt(req.getParameter("quantity"));
                if (productId == null) {
                    session.setAttribute("flashError", "Sản phẩm không hợp lệ");
                    resp.sendRedirect(ctx + "/products");
                    return;
                }
                String back = ctx + "/product/detail?id=" + productId;
                if (qty == null) {
                    session.setAttribute("flashError", "Số lượng không hợp lệ");
                    resp.sendRedirect(back);
                    return;
                }
                try {
                    String name = cartService.addToCart(user.getUserId(), productId, qty);
                    session.setAttribute("flashSuccess", "Đã thêm \"" + name + "\" vào giỏ hàng");
                    resp.sendRedirect(ctx + "/cart");
                } catch (ServiceException_24162130 e) {
                    session.setAttribute("flashError", e.getMessage());
                    resp.sendRedirect(back);
                }
            }
            case "/cart/update" -> {
                Integer itemId = parseInt(req.getParameter("cartItemId"));
                Integer qty = parseInt(req.getParameter("quantity"));
                if (itemId == null) {
                    session.setAttribute("flashError", "Sản phẩm trong giỏ không hợp lệ");
                } else if (qty == null) {
                    session.setAttribute("flashError", "Số lượng không hợp lệ, vui lòng nhập số nguyên");
                } else {
                    try {
                        cartService.updateQuantity(user.getUserId(), itemId, qty);
                        session.setAttribute("flashSuccess", "Đã cập nhật số lượng");
                    } catch (ServiceException_24162130 e) {
                        session.setAttribute("flashError", e.getMessage());
                    }
                }
                resp.sendRedirect(ctx + "/cart");
            }
            case "/cart/remove" -> {
                Integer itemId = parseInt(req.getParameter("cartItemId"));
                if (itemId == null) {
                    session.setAttribute("flashError", "Sản phẩm trong giỏ không hợp lệ");
                } else {
                    try {
                        cartService.removeItem(user.getUserId(), itemId);
                        session.setAttribute("flashSuccess", "Đã xóa sản phẩm khỏi giỏ hàng");
                    } catch (ServiceException_24162130 e) {
                        session.setAttribute("flashError", e.getMessage());
                    }
                }
                resp.sendRedirect(ctx + "/cart");
            }
            case "/cart/clear" -> {
                try {
                    cartService.clearCart(user.getUserId());
                    session.setAttribute("flashSuccess", "Đã xóa toàn bộ giỏ hàng");
                } catch (ServiceException_24162130 e) {
                    session.setAttribute("flashError", e.getMessage());
                }
                resp.sendRedirect(ctx + "/cart");
            }
            default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /** User trong Session (AuthFilter dam bao da dang nhap truoc khi toi day). */
    private static SessionUser_24162130 currentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (SessionUser_24162130) session.getAttribute(AppConstants_24162130.SESSION_USER);
    }

    private static Integer parseInt(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(raw.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
