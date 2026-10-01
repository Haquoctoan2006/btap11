package vn.ute.repository;

import java.util.List;

import jakarta.persistence.EntityManager;

import vn.ute.config.JpaConfig_24162130;
import vn.ute.constants.AppConstants_24162130;
import vn.ute.entity.Cart_24162130;
import vn.ute.entity.CartItem_24162130;
import vn.ute.entity.User_24162130;

public class CartRepository_24162130 implements ICartRepository_24162130 {

    @Override
    public List<CartItem_24162130> findItemsWithProductByUser(int userId) {
        EntityManager em = JpaConfig_24162130.getEntityManager();
        try {
            // JOIN FETCH Product de JSP dung duoc sau khi EntityManager da dong
            return em.createQuery("SELECT i FROM CartItem i JOIN FETCH i.product "
                            + "WHERE i.cart.user.userId = :uid AND i.cart.status = :st "
                            + "ORDER BY i.cartItemId", CartItem_24162130.class)
                    .setParameter("uid", userId)
                    .setParameter("st", AppConstants_24162130.CART_STATUS_ACTIVE)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Cart_24162130 findActiveCartByUser(EntityManager em, int userId) {
        List<Cart_24162130> list = em.createQuery("SELECT c FROM Cart c WHERE c.user.userId = :uid "
                        + "AND c.status = :st ORDER BY c.cartId", Cart_24162130.class)
                .setParameter("uid", userId)
                .setParameter("st", AppConstants_24162130.CART_STATUS_ACTIVE)
                .setMaxResults(1)
                .getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public Cart_24162130 createActiveCart(EntityManager em, int userId) {
        Cart_24162130 cart = new Cart_24162130();
        cart.setUser(em.getReference(User_24162130.class, userId));
        cart.setStatus(AppConstants_24162130.CART_STATUS_ACTIVE);
        cart.setBuyDate(java.time.LocalDateTime.now());
        em.persist(cart);
        em.flush(); // dam bao cartId da duoc sinh truoc khi dung tiep trong cung transaction
        return cart;
    }

    @Override
    public CartItem_24162130 findItemByCartAndProduct(EntityManager em, int cartId, int productId) {
        List<CartItem_24162130> list = em.createQuery("SELECT i FROM CartItem i "
                        + "WHERE i.cart.cartId = :cid AND i.product.productId = :pid ORDER BY i.cartItemId",
                        CartItem_24162130.class)
                .setParameter("cid", cartId)
                .setParameter("pid", productId)
                .setMaxResults(1)
                .getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public CartItem_24162130 findItemByIdAndUser(EntityManager em, int cartItemId, int userId) {
        List<CartItem_24162130> list = em.createQuery("SELECT i FROM CartItem i JOIN FETCH i.product "
                        + "WHERE i.cartItemId = :id AND i.cart.user.userId = :uid AND i.cart.status = :st",
                        CartItem_24162130.class)
                .setParameter("id", cartItemId)
                .setParameter("uid", userId)
                .setParameter("st", AppConstants_24162130.CART_STATUS_ACTIVE)
                .getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<CartItem_24162130> findItemsByCart(EntityManager em, int cartId) {
        return em.createQuery("SELECT i FROM CartItem i WHERE i.cart.cartId = :cid ORDER BY i.cartItemId",
                        CartItem_24162130.class)
                .setParameter("cid", cartId)
                .getResultList();
    }

    @Override
    public void persistItem(EntityManager em, CartItem_24162130 item) {
        em.persist(item);
    }

    @Override
    public void removeItem(EntityManager em, CartItem_24162130 item) {
        em.remove(item);
    }

    @Override
    public int deleteAllItems(EntityManager em, int cartId) {
        return em.createQuery("DELETE FROM CartItem i WHERE i.cart.cartId = :cid")
                .setParameter("cid", cartId)
                .executeUpdate();
    }
}
