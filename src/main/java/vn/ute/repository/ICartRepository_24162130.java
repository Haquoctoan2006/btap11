package vn.ute.repository;

import java.util.List;

import jakarta.persistence.EntityManager;

import vn.ute.entity.Cart_24162130;
import vn.ute.entity.CartItem_24162130;

/**
 * Data Access Layer - Cart / CartItem.
 * Moi truy van deu rang buoc theo userId lay tu Session (chong IDOR).
 * Cac method nhan EntityManager chay trong transaction cua Service (khong tu dong EM).
 */
public interface ICartRepository_24162130 {

    /** Doc (tu mo/dong EM): cac CartItem cua gio hang dang hoat dong cua user, da nap san Product. */
    List<CartItem_24162130> findItemsWithProductByUser(int userId);

    /** Gio hang dang hoat dong cua user (neu co nhieu thi lay gio cu nhat); null neu chua co. */
    Cart_24162130 findActiveCartByUser(EntityManager em, int userId);

    /** Tao gio hang moi cho user (status = dang hoat dong). */
    Cart_24162130 createActiveCart(EntityManager em, int userId);

    /** Dong CartItem cua 1 san pham trong 1 gio hang; null neu chua co. */
    CartItem_24162130 findItemByCartAndProduct(EntityManager em, int cartId, int productId);

    /**
     * CartItem theo id NHUNG chi khi thuoc gio hang dang hoat dong cua dung user nay.
     * Tra ve null neu id khong ton tai hoac thuoc user khac. Da nap san Product.
     */
    CartItem_24162130 findItemByIdAndUser(EntityManager em, int cartItemId, int userId);

    /** Tat ca CartItem cua 1 gio hang (Product de lazy, chua nap). */
    List<CartItem_24162130> findItemsByCart(EntityManager em, int cartId);

    void persistItem(EntityManager em, CartItem_24162130 item);

    void removeItem(EntityManager em, CartItem_24162130 item);

    /** Xoa moi CartItem cua gio hang; tra ve so dong da xoa. */
    int deleteAllItems(EntityManager em, int cartId);
}
