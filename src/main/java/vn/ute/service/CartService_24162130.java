package vn.ute.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import vn.ute.config.JpaConfig_24162130;
import vn.ute.constants.AppConstants_24162130;
import vn.ute.dto.CartLine_24162130;
import vn.ute.dto.CartSummary_24162130;
import vn.ute.entity.Cart_24162130;
import vn.ute.entity.CartItem_24162130;
import vn.ute.entity.Product_24162130;
import vn.ute.exception.ServiceException_24162130;
import vn.ute.repository.CartRepository_24162130;
import vn.ute.repository.ICartRepository_24162130;
import vn.ute.repository.IProductRepository_24162130;
import vn.ute.repository.IUserRepository_24162130;
import vn.ute.repository.ProductRepository_24162130;
import vn.ute.repository.UserRepository_24162130;

public class CartService_24162130 implements ICartService_24162130 {

    private static final Logger LOG = Logger.getLogger(CartService_24162130.class.getName());

    private final ICartRepository_24162130 cartRepository = new CartRepository_24162130();
    private final IProductRepository_24162130 productRepository = new ProductRepository_24162130();
    private final IUserRepository_24162130 userRepository = new UserRepository_24162130();

    // ------------------------------------------------------------------ Xem gio
    @Override
    public CartSummary_24162130 getCart(int userId) {
        List<CartLine_24162130> lines = new ArrayList<>();
        for (CartItem_24162130 item : cartRepository.findItemsWithProductByUser(userId)) {
            Product_24162130 p = item.getProduct();
            int qty = item.getQuantity() == null ? 0 : item.getQuantity();
            int stock = stockOf(p);
            double unit = item.getUnitPrice() != null ? item.getUnitPrice()
                    : (p.getPrice() == null ? 0 : p.getPrice());
            lines.add(new CartLine_24162130(item.getCartItemId(), p.getProductId(), p.getProductName(),
                    p.getImages(), unit, p.getPrice(), qty, stock, issueOf(p, qty)));
        }
        return new CartSummary_24162130(lines);
    }

    // ------------------------------------------------------------------ Them
    @Override
    public String addToCart(int userId, int productId, int quantity) throws ServiceException_24162130 {
        if (quantity < 1) {
            throw new ServiceException_24162130("Số lượng phải lớn hơn 0");
        }
        EntityManager em = JpaConfig_24162130.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // Khoa dong user: 2 lan bam "Them" lien tiep khong tao 2 gio / 2 CartItem trung nhau
            if (userRepository.findByIdForUpdate(em, userId) == null) {
                throw new ServiceException_24162130("Không tìm thấy tài khoản, vui lòng đăng nhập lại");
            }
            Product_24162130 product = productRepository.findById(em, productId);
            requireSellable(product);

            Cart_24162130 cart = cartRepository.findActiveCartByUser(em, userId);
            if (cart == null) {
                cart = cartRepository.createActiveCart(em, userId);
            }
            CartItem_24162130 item = cartRepository.findItemByCartAndProduct(em, cart.getCartId(), productId);
            int current = (item == null || item.getQuantity() == null) ? 0 : item.getQuantity();
            long newQty = (long) current + quantity;
            int stock = stockOf(product);
            if (newQty > stock) {
                throw new ServiceException_24162130(current > 0
                        ? "Sản phẩm \"" + product.getProductName() + "\" chỉ còn " + stock
                                + " trong kho, bạn đã có " + current + " trong giỏ hàng"
                        : "Sản phẩm \"" + product.getProductName() + "\" chỉ còn " + stock + " trong kho");
            }

            if (item == null) {
                item = new CartItem_24162130();
                item.setCart(cart);
                item.setProduct(product);
                item.setQuantity((int) newQty);
                item.setUnitPrice(product.getPrice()); // gia tai thoi diem them vao gio
                cartRepository.persistItem(em, item);
            } else {
                item.setQuantity((int) newQty);
            }
            tx.commit();
            return product.getProductName();
        } catch (ServiceException_24162130 e) {
            rollback(tx);
            throw e;
        } catch (RuntimeException e) {
            rollback(tx);
            LOG.log(Level.SEVERE, "Loi khi them vao gio hang userId=" + userId + ", productId=" + productId, e);
            throw new ServiceException_24162130("Không thể thêm vào giỏ hàng, vui lòng thử lại");
        } finally {
            em.close();
        }
    }

    // ------------------------------------------------------------------ Sua so luong
    @Override
    public void updateQuantity(int userId, int cartItemId, int quantity) throws ServiceException_24162130 {
        if (quantity < 1) {
            throw new ServiceException_24162130(
                    "Số lượng phải lớn hơn 0. Muốn bỏ sản phẩm khỏi giỏ hãy bấm nút Xóa");
        }
        EntityManager em = JpaConfig_24162130.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (userRepository.findByIdForUpdate(em, userId) == null) {
                throw new ServiceException_24162130("Không tìm thấy tài khoản, vui lòng đăng nhập lại");
            }
            // Chi tim thay neu CartItem thuoc gio cua CHINH user nay (chong IDOR)
            CartItem_24162130 item = cartRepository.findItemByIdAndUser(em, cartItemId, userId);
            if (item == null) {
                throw new ServiceException_24162130("Sản phẩm không có trong giỏ hàng của bạn");
            }
            Product_24162130 product = item.getProduct();
            requireSellable(product);
            int stock = stockOf(product);
            if (quantity > stock) {
                throw new ServiceException_24162130("Sản phẩm \"" + product.getProductName()
                        + "\" chỉ còn " + stock + " trong kho, không thể đặt " + quantity);
            }
            item.setQuantity(quantity);
            tx.commit();
        } catch (ServiceException_24162130 e) {
            rollback(tx);
            throw e;
        } catch (RuntimeException e) {
            rollback(tx);
            LOG.log(Level.SEVERE, "Loi khi cap nhat gio hang userId=" + userId + ", cartItemId=" + cartItemId, e);
            throw new ServiceException_24162130("Không thể cập nhật số lượng, vui lòng thử lại");
        } finally {
            em.close();
        }
    }

    // ------------------------------------------------------------------ Xoa 1 dong
    @Override
    public void removeItem(int userId, int cartItemId) throws ServiceException_24162130 {
        EntityManager em = JpaConfig_24162130.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            CartItem_24162130 item = cartRepository.findItemByIdAndUser(em, cartItemId, userId);
            if (item == null) {
                throw new ServiceException_24162130("Sản phẩm không có trong giỏ hàng của bạn");
            }
            cartRepository.removeItem(em, item);
            tx.commit();
        } catch (ServiceException_24162130 e) {
            rollback(tx);
            throw e;
        } catch (RuntimeException e) {
            rollback(tx);
            LOG.log(Level.SEVERE, "Loi khi xoa CartItem userId=" + userId + ", cartItemId=" + cartItemId, e);
            throw new ServiceException_24162130("Không thể xóa sản phẩm, vui lòng thử lại");
        } finally {
            em.close();
        }
    }

    // ------------------------------------------------------------------ Xoa ca gio
    @Override
    public void clearCart(int userId) throws ServiceException_24162130 {
        EntityManager em = JpaConfig_24162130.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Cart_24162130 cart = cartRepository.findActiveCartByUser(em, userId);
            if (cart != null) {
                cartRepository.deleteAllItems(em, cart.getCartId());
            }
            tx.commit();
        } catch (RuntimeException e) {
            rollback(tx);
            LOG.log(Level.SEVERE, "Loi khi xoa gio hang userId=" + userId, e);
            throw new ServiceException_24162130("Không thể xóa giỏ hàng, vui lòng thử lại");
        } finally {
            em.close();
        }
    }

    // ------------------------------------------------------------------ Helpers
    /** San pham phai ton tai, dang kinh doanh va con hang. */
    private static void requireSellable(Product_24162130 p) throws ServiceException_24162130 {
        if (p == null) {
            throw new ServiceException_24162130("Sản phẩm không tồn tại");
        }
        if (isInactive(p)) {
            throw new ServiceException_24162130("Sản phẩm \"" + p.getProductName() + "\" hiện không còn kinh doanh");
        }
        if (stockOf(p) <= 0) {
            throw new ServiceException_24162130("Sản phẩm \"" + p.getProductName() + "\" đã hết hàng");
        }
        if (p.getPrice() == null) {
            throw new ServiceException_24162130("Sản phẩm \"" + p.getProductName() + "\" chưa có giá, không thể mua");
        }
    }

    /** Thong bao van de cua 1 dong gio hang (null = hop le). */
    private static String issueOf(Product_24162130 p, int qty) {
        if (isInactive(p)) {
            return "Sản phẩm không còn kinh doanh";
        }
        int stock = stockOf(p);
        if (stock <= 0) {
            return "Hết hàng";
        }
        if (qty > stock) {
            return "Chỉ còn " + stock + " sản phẩm trong kho, vui lòng giảm số lượng";
        }
        if (qty < 1) {
            return "Số lượng không hợp lệ";
        }
        return null;
    }

    static boolean isInactive(Product_24162130 p) {
        return p.getStatus() != null && p.getStatus() == AppConstants_24162130.STATUS_INACTIVE;
    }

    static int stockOf(Product_24162130 p) {
        return p.getStock() == null ? 0 : p.getStock();
    }

    private static void rollback(EntityTransaction tx) {
        if (tx != null && tx.isActive()) {
            tx.rollback();
        }
    }
}
