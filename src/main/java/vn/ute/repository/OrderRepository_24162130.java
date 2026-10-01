package vn.ute.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import vn.ute.config.JpaConfig_24162130;
import vn.ute.entity.Order_24162130;

public class OrderRepository_24162130 implements IOrderRepository_24162130 {

    @Override
    public void save(EntityManager em, Order_24162130 order) {
        em.persist(order);
    }

    @Override
    public List<Order_24162130> findByUser(int userId, String status) {
        EntityManager em = JpaConfig_24162130.getEntityManager();
        try {
            boolean filter = status != null && !status.isBlank();
            String jpql = "SELECT o FROM ShopOrder o WHERE o.user.userId = :uid"
                    + (filter ? " AND o.status = :st" : "")
                    + " ORDER BY o.orderDate DESC, o.orderId DESC";
            TypedQuery<Order_24162130> q = em.createQuery(jpql, Order_24162130.class)
                    .setParameter("uid", userId);
            if (filter) {
                q.setParameter("st", status.trim());
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Order_24162130 findDetailByIdAndUser(int orderId, int userId) {
        EntityManager em = JpaConfig_24162130.getEntityManager();
        try {
            List<Order_24162130> list = em.createQuery("SELECT DISTINCT o FROM ShopOrder o "
                            + "LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.product "
                            + "WHERE o.orderId = :id AND o.user.userId = :uid", Order_24162130.class)
                    .setParameter("id", orderId)
                    .setParameter("uid", userId)
                    .getResultList();
            return list.isEmpty() ? null : list.get(0);
        } finally {
            em.close();
        }
    }

    @Override
    public Map<String, Long> countByStatus(int userId) {
        EntityManager em = JpaConfig_24162130.getEntityManager();
        try {
            List<Object[]> rows = em.createQuery("SELECT o.status, COUNT(o) FROM ShopOrder o "
                            + "WHERE o.user.userId = :uid GROUP BY o.status", Object[].class)
                    .setParameter("uid", userId)
                    .getResultList();
            Map<String, Long> result = new HashMap<>();
            for (Object[] r : rows) {
                String key = r[0] == null ? "" : r[0].toString().trim().toUpperCase(Locale.ROOT);
                result.merge(key, ((Number) r[1]).longValue(), Long::sum);
            }
            return result;
        } finally {
            em.close();
        }
    }
}
