package vn.ute.repository;

import java.util.List;

import jakarta.persistence.EntityManager;

import vn.ute.config.JpaConfig_24162130;
import vn.ute.entity.Seller_24162130;

public class SellerRepository_24162130 implements ISellerRepository_24162130 {

    @Override
    public List<Seller_24162130> findAll() {
        EntityManager em = JpaConfig_24162130.getEntityManager();
        try {
            return em.createQuery("SELECT s FROM Seller s ORDER BY s.sellerId", Seller_24162130.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Seller_24162130 findById(int sellerId) {
        EntityManager em = JpaConfig_24162130.getEntityManager();
        try {
            return em.find(Seller_24162130.class, sellerId);
        } finally {
            em.close();
        }
    }
}
