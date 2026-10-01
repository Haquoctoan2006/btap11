package vn.ute.repository;

import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;

import vn.ute.entity.Order_24162130;

/**
 * Data Access Layer - bang `Order` / OrderItem.
 * Moi truy van doc deu bat buoc userId (lay tu Session) de nguoi dung chi thay don cua minh.
 */
public interface IOrderRepository_24162130 {

    /** Luu don + cac OrderItem (cascade) trong transaction cua Service. */
    void save(EntityManager em, Order_24162130 order);

    /** Don cua user, moi nhat truoc. status = null -> tat ca; co gia tri -> chi don co trang thai do. */
    List<Order_24162130> findByUser(int userId, String status);

    /** Chi tiet 1 don (da nap OrderItem + Product) NEU don thuoc user nay; nguoc lai tra ve null. */
    Order_24162130 findDetailByIdAndUser(int orderId, int userId);

    /** So don cua user theo tung trang thai (khoa: ma trang thai viet hoa, vi du "NEW"). */
    Map<String, Long> countByStatus(int userId);
}
