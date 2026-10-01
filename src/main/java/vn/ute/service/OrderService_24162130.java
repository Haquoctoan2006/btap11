package vn.ute.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import vn.ute.config.JpaConfig_24162130;
import vn.ute.constants.AppConstants_24162130;
import vn.ute.constants.OrderStatus_24162130;
import vn.ute.dto.CartSummary_24162130;
import vn.ute.dto.CheckoutInfo_24162130;
import vn.ute.entity.Cart_24162130;
import vn.ute.entity.CartItem_24162130;
import vn.ute.entity.Order_24162130;
import vn.ute.entity.OrderItem_24162130;
import vn.ute.entity.Product_24162130;
import vn.ute.entity.User_24162130;
import vn.ute.exception.ServiceException_24162130;
import vn.ute.repository.CartRepository_24162130;
import vn.ute.repository.ICartRepository_24162130;
import vn.ute.repository.IOrderRepository_24162130;
import vn.ute.repository.IProductRepository_24162130;
import vn.ute.repository.IUserRepository_24162130;
import vn.ute.repository.OrderRepository_24162130;
import vn.ute.repository.ProductRepository_24162130;
import vn.ute.repository.UserRepository_24162130;

public class OrderService_24162130 implements IOrderService_24162130 {

    private static final Logger LOG = Logger.getLogger(OrderService_24162130.class.getName());
    private static final Pattern PHONE = Pattern.compile("^0\\d{9,10}$");

    private final ICartService_24162130 cartService = new CartService_24162130();
    private final ICartRepository_24162130 cartRepository = new CartRepository_24162130();
    private final IOrderRepository_24162130 orderRepository = new OrderRepository_24162130();
    private final IProductRepository_24162130 productRepository = new ProductRepository_24162130();
    private final IUserRepository_24162130 userRepository = new UserRepository_24162130();

    // ------------------------------------------------------------------ Trang xac nhan
    @Override
    public CheckoutInfo_24162130 prepareCheckout(int userId) throws ServiceException_24162130 {
        CartSummary_24162130 cart = cartService.getCart(userId);
        if (cart.isEmpty()) {
            throw new ServiceException_24162130("Giỏ hàng của bạn đang trống");
        }
        if (cart.isHasIssue()) {
            throw new ServiceException_24162130(
                    "Giỏ hàng có sản phẩm không hợp lệ (hết hàng hoặc vượt tồn kho), vui lòng điều chỉnh trước khi thanh toán");
        }
        User_24162130 user = userRepository.findById(userId);
        if (user == null) {
            throw new ServiceException_24162130("Không tìm thấy tài khoản, vui lòng đăng nhập lại");
        }
        return new CheckoutInfo_24162130(cart, user.getFullname(), user.getPhone());
    }

    // ------------------------------------------------------------------ Dat hang
    @Override
    public int placeOrder(int userId, String shippingAddress, String phone, String paymentMethod)
            throws ServiceException_24162130 {
        // 1. Kiem tra dau vao (khong can DB)
        String address = shippingAddress == null ? "" : shippingAddress.trim();
        String phoneNo = phone == null ? "" : phone.trim();
        if (!AppConstants_24162130.PAYMENT_COD.equals(paymentMethod)) {
            throw new ServiceException_24162130("Hiện chỉ hỗ trợ thanh toán khi nhận hàng (COD)");
        }
        if (address.length() < 5 || address.length() > 500) {
            throw new ServiceException_24162130("Địa chỉ nhận hàng phải từ 5 đến 500 ký tự");
        }
        if (!PHONE.matcher(phoneNo).matches()) {
            throw new ServiceException_24162130("Số điện thoại không hợp lệ (ví dụ: 0912345678)");
        }

        EntityManager em = JpaConfig_24162130.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            // 2. Khoa dong user: 2 lan bam "Dat hang" cung luc se chay lan luot, lan 2 thay gio da rong
            User_24162130 user = userRepository.findByIdForUpdate(em, userId);
            if (user == null) {
                throw new ServiceException_24162130("Không tìm thấy tài khoản, vui lòng đăng nhập lại");
            }

            // 3. Lay CartItem tu DATABASE (khong dung du lieu nao tu browser)
            Cart_24162130 cart = cartRepository.findActiveCartByUser(em, userId);
            List<CartItem_24162130> items = cart == null ? List.of()
                    : cartRepository.findItemsByCart(em, cart.getCartId());
            if (items.isEmpty()) {
                throw new ServiceException_24162130("Giỏ hàng của bạn đang trống");
            }

            // 4. Doc lai ton kho moi nhat: khoa dong Product theo thu tu id
            TreeSet<Integer> productIds = new TreeSet<>();
            for (CartItem_24162130 it : items) {
                productIds.add(productIdOf(em, it));
            }
            Map<Integer, Product_24162130> products = new HashMap<>();
            for (Product_24162130 p : productRepository.findByIdsForUpdate(em, productIds)) {
                products.put(p.getProductId(), p);
            }

            // 5. Kiem tra TAT CA san pham truoc khi tao don; gom het loi de bao 1 lan
            List<String> problems = new ArrayList<>();
            for (CartItem_24162130 it : items) {
                int pid = productIdOf(em, it);
                Product_24162130 p = products.get(pid);
                int qty = it.getQuantity() == null ? 0 : it.getQuantity();
                if (p == null) {
                    problems.add("Có sản phẩm không còn tồn tại");
                } else if (CartService_24162130.isInactive(p)) {
                    problems.add("\"" + p.getProductName() + "\" không còn kinh doanh");
                } else if (qty < 1) {
                    problems.add("\"" + p.getProductName() + "\" có số lượng không hợp lệ");
                } else if (CartService_24162130.stockOf(p) < qty) {
                    problems.add("\"" + p.getProductName() + "\" không đủ hàng (còn "
                            + CartService_24162130.stockOf(p) + ", bạn đặt " + qty + ")");
                } else if (it.getUnitPrice() == null && p.getPrice() == null) {
                    problems.add("\"" + p.getProductName() + "\" chưa có giá");
                }
            }
            if (!problems.isEmpty()) {
                throw new ServiceException_24162130("Không thể đặt hàng: " + String.join("; ", problems));
            }

            // 6. Tao Order + OrderItem, tru kho, xoa CartItem da mua
            Order_24162130 order = new Order_24162130();
            order.setUser(user);
            order.setOrderDate(LocalDateTime.now());
            order.setPaymentMethod(AppConstants_24162130.PAYMENT_COD);
            order.setStatus(OrderStatus_24162130.NEW.getCode());
            order.setShippingAddress(address);
            order.setPhone(phoneNo);

            double total = 0;
            for (CartItem_24162130 it : items) {
                int pid = productIdOf(em, it);
                Product_24162130 p = products.get(pid);
                int qty = it.getQuantity();
                double unit = it.getUnitPrice() != null ? it.getUnitPrice() : p.getPrice();
                double subtotal = unit * qty;

                OrderItem_24162130 oi = new OrderItem_24162130();
                oi.setProduct(p);
                oi.setQuantity(qty);
                oi.setUnitPrice(unit);
                oi.setSubtotal(subtotal);
                order.addItem(oi);
                total += subtotal;

                p.setStock(CartService_24162130.stockOf(p) - qty);
                cartRepository.removeItem(em, it);
            }
            order.setTotalAmount(total);
            cart.setBuyDate(order.getOrderDate());
            orderRepository.save(em, order);

            tx.commit();
            return order.getOrderId();
        } catch (ServiceException_24162130 e) {
            rollback(tx);
            throw e;
        } catch (RuntimeException e) {
            rollback(tx);
            LOG.log(Level.SEVERE, "Loi khi dat hang userId=" + userId + " (da rollback)", e);
            throw new ServiceException_24162130("Đặt hàng thất bại, hệ thống đã hoàn tác. Vui lòng thử lại");
        } finally {
            em.close();
        }
    }

    // ------------------------------------------------------------------ Lich su
    @Override
    public List<Order_24162130> getOrderHistory(int userId, String statusCode) {
        OrderStatus_24162130 status = (statusCode == null || statusCode.isBlank()) ? null
                : OrderStatus_24162130.fromCode(statusCode);
        // Ma khong hop le (hoac "ALL") -> hien tat ca, khong bao loi
        return orderRepository.findByUser(userId, status == null ? null : status.getCode());
    }

    @Override
    public Map<String, Long> countByStatus(int userId) {
        return orderRepository.countByStatus(userId);
    }

    @Override
    public Order_24162130 getOrderDetail(int userId, int orderId) throws ServiceException_24162130 {
        Order_24162130 order = orderRepository.findDetailByIdAndUser(orderId, userId);
        if (order == null) {
            // Khong phan biet "khong ton tai" va "cua nguoi khac" de khong lo thong tin
            throw new ServiceException_24162130("Không tìm thấy đơn hàng #" + orderId);
        }
        return order;
    }

    /** Lay productId cua CartItem ma khong nap Product (de Product con lazy, doc ton kho moi bang FOR UPDATE). */
    private static int productIdOf(EntityManager em, CartItem_24162130 item) {
        return (Integer) em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(item.getProduct());
    }

    private static void rollback(EntityTransaction tx) {
        if (tx != null && tx.isActive()) {
            tx.rollback();
        }
    }
}
