package vn.ute.repository;

import java.util.Collection;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;

import vn.ute.config.JpaConfig_24162130;
import vn.ute.entity.Product_24162130;

public class ProductRepository_24162130 implements IProductRepository_24162130 {

    // LEFT JOIN FETCH: san pham thieu category/seller van duoc tra ve.
    // Category va Seller la quan he LAZY, neu khong FETCH thi truy cap sau khi dong
    // EntityManager (trong JSP) se nem LazyInitializationException.
    private static final String SELECT_WITH_DETAILS =
            "SELECT p FROM Product p "
            + "LEFT JOIN FETCH p.category "
            + "LEFT JOIN FETCH p.seller ";

    private static final String ORDER = " ORDER BY p.createDate DESC, p.productId DESC";

    @Override
    public List<Product_24162130> findAllWithDetails() {
        EntityManager em = JpaConfig_24162130.getEntityManager();
        try {
            return em.createQuery(SELECT_WITH_DETAILS + ORDER, Product_24162130.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Product_24162130> findBySellerId(int sellerId) {
        EntityManager em = JpaConfig_24162130.getEntityManager();
        try {
            return em.createQuery(SELECT_WITH_DETAILS + "WHERE p.seller.sellerId = :sid" + ORDER,
                            Product_24162130.class)
                    .setParameter("sid", sellerId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Product_24162130 findByIdWithDetails(int productId) {
        EntityManager em = JpaConfig_24162130.getEntityManager();
        try {
            return em.createQuery(SELECT_WITH_DETAILS + "WHERE p.productId = :pid", Product_24162130.class)
                    .setParameter("pid", productId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Product_24162130> findPaging(int page, int pageSize) {
        EntityManager em = JpaConfig_24162130.getEntityManager();
        try {
            int firstResult = Math.max(page - 1, 0) * pageSize;
            return em.createQuery(SELECT_WITH_DETAILS + ORDER, Product_24162130.class)
                    .setFirstResult(firstResult)
                    .setMaxResults(pageSize)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public long count() {
        EntityManager em = JpaConfig_24162130.getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(p) FROM Product p", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public Product_24162130 findById(EntityManager em, int productId) {
        return em.find(Product_24162130.class, productId);
    }

    @Override
    public List<Product_24162130> findByIdsForUpdate(EntityManager em, Collection<Integer> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return List.of();
        }
        List<Product_24162130> products = em.createQuery(
                        "SELECT p FROM Product p WHERE p.productId IN :ids ORDER BY p.productId",
                        Product_24162130.class)
                .setParameter("ids", productIds)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .getResultList();
        // refresh kem khoa = doc lai dong moi nhat tu DB (current read), khong dung ban sao cu trong
        // persistence context / snapshot cua transaction -> stock luon la gia tri that tai luc dat hang
        for (Product_24162130 p : products) {
            em.refresh(p, LockModeType.PESSIMISTIC_WRITE);
        }
        return products;
    }

    @Override
    public Product_24162130 save(Product_24162130 product) {
        EntityManager em = JpaConfig_24162130.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(product);
            tx.commit();
            return product;
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Product_24162130 product) {
        EntityManager em = JpaConfig_24162130.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(product);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(int productId) {
        EntityManager em = JpaConfig_24162130.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // CartItem tham chieu Product qua khoa ngoai: go san pham khoi cac gio hang truoc khi xoa
            em.createQuery("DELETE FROM CartItem i WHERE i.product.productId = :pid")
                    .setParameter("pid", productId)
                    .executeUpdate();
            Product_24162130 ref = em.find(Product_24162130.class, productId);
            if (ref != null) {
                em.remove(ref);
            }
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
