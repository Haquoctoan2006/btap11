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
import vn.ute.constants.OrderStatus_24162130;
import vn.ute.dto.CheckoutInfo_24162130;
import vn.ute.dto.SessionUser_24162130;
import vn.ute.exception.ServiceException_24162130;
import vn.ute.service.IOrderService_24162130;
import vn.ute.service.OrderService_24162130;

/**
 * Thanh toan COD + lich su don hang cua User.
 *   GET  /checkout              : trang xac nhan don hang
 *   POST /checkout/place-order  : dat hang (COD)
 *   GET  /orders                : lich su don hang, loc bang ?status=NEW|CONFIRMED|...|RETURNED
 *   GET  /orders/detail?id=     : chi tiet 1 don (chi don cua chinh user)
 *   GET  /orders/success?id=    : trang dat hang thanh cong
 * userId luon lay tu Session, khong bao gio doc tu request (chong IDOR).
 */
@WebServlet(urlPatterns = { "/checkout", "/checkout/place-order", "/orders", "/orders/detail", "/orders/success" })
public class OrderController_24162130 extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(OrderController_24162130.class.getName());

    private static final String CHECKOUT_VIEW = "/WEB-INF/views/order/checkout.jsp";
    private static final String LIST_VIEW = "/WEB-INF/views/order/order-list.jsp";
    private static final String DETAIL_VIEW = "/WEB-INF/views/order/order-detail.jsp";
    private static final String SUCCESS_VIEW = "/WEB-INF/views/order/order-success.jsp";

    private final transient IOrderService_24162130 orderService = new OrderService_24162130();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SessionUser_24162130 user = currentUser(req);
        switch (req.getServletPath()) {
            case "/checkout" -> showCheckout(req, resp, user);
            case "/orders" -> showList(req, resp, user);
            case "/orders/detail" -> showDetail(req, resp, user, DETAIL_VIEW);
            case "/orders/success" -> showDetail(req, resp, user, SUCCESS_VIEW);
            default -> resp.sendRedirect(req.getContextPath() + "/orders"); // GET /checkout/place-order
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("/checkout/place-order".equals(req.getServletPath())) {
            placeOrder(req, resp, currentUser(req));
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    // ---------------------------------------------------------------- CHECKOUT

    private void showCheckout(HttpServletRequest req, HttpServletResponse resp, SessionUser_24162130 user)
            throws ServletException, IOException {
        try {
            CheckoutInfo_24162130 info = orderService.prepareCheckout(user.getUserId());
            req.setAttribute("checkout", info);
            req.setAttribute("phone", info.getPhone());
            req.getRequestDispatcher(CHECKOUT_VIEW).forward(req, resp);
        } catch (ServiceException_24162130 e) {
            // Gio rong / co san pham het hang -> quay lai gio hang kem thong bao
            req.getSession().setAttribute("flashError", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/cart");
        }
    }

    private void placeOrder(HttpServletRequest req, HttpServletResponse resp, SessionUser_24162130 user)
            throws ServletException, IOException {
        String address = req.getParameter("shippingAddress");
        String phone = req.getParameter("phone");
        String payment = req.getParameter("paymentMethod");
        try {
            int orderId = orderService.placeOrder(user.getUserId(), address, phone, payment);
            resp.sendRedirect(req.getContextPath() + "/orders/success?id=" + orderId); // PRG: F5 khong dat lai
        } catch (ServiceException_24162130 e) {
            try {
                // Gio van hop le (loi o form) -> hien lai form kem thong bao + du lieu da nhap
                req.setAttribute("checkout", orderService.prepareCheckout(user.getUserId()));
                req.setAttribute("error", e.getMessage());
                req.setAttribute("shippingAddress", address);
                req.setAttribute("phone", phone);
                req.getRequestDispatcher(CHECKOUT_VIEW).forward(req, resp);
            } catch (ServiceException_24162130 cartProblem) {
                // Gio rong / het hang -> ve /cart, hien ly do cua lan dat hang that bai
                req.getSession().setAttribute("flashError", e.getMessage());
                resp.sendRedirect(req.getContextPath() + "/cart");
            }
        }
    }

    // ---------------------------------------------------------------- LICH SU

    private void showList(HttpServletRequest req, HttpServletResponse resp, SessionUser_24162130 user)
            throws ServletException, IOException {
        String statusParam = req.getParameter("status");
        OrderStatus_24162130 selected = OrderStatus_24162130.fromCode(statusParam); // null = tat ca
        try {
            req.setAttribute("orders", orderService.getOrderHistory(user.getUserId(),
                    selected == null ? null : selected.getCode()));
            java.util.Map<String, Long> counts = orderService.countByStatus(user.getUserId());
            req.setAttribute("statusCounts", counts);
            req.setAttribute("totalOrders", counts.values().stream().mapToLong(Long::longValue).sum());
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Loi khi tai lich su don hang userId=" + user.getUserId(), e);
            req.setAttribute("error", "Không tải được lịch sử đơn hàng, vui lòng thử lại sau");
        }
        req.setAttribute("selectedStatus", selected == null ? "ALL" : selected.getCode());
        req.setAttribute("statuses", OrderStatus_24162130.values());
        req.getRequestDispatcher(LIST_VIEW).forward(req, resp);
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp, SessionUser_24162130 user,
            String view) throws ServletException, IOException {
        Integer orderId = parseInt(req.getParameter("id"));
        if (orderId == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Mã đơn hàng không hợp lệ");
            return;
        }
        try {
            req.setAttribute("order", orderService.getOrderDetail(user.getUserId(), orderId));
            req.getRequestDispatcher(view).forward(req, resp);
        } catch (ServiceException_24162130 e) {
            // Don khong ton tai HOAC la cua user khac: cung tra 404
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }

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
