package vn.ute.repository;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import vn.ute.config.JpaConfig_24162130;
import vn.ute.entity.Category_24162130;

public class CategoryRepository_24162130 implements ICategoryRepository_24162130 {

    private static final String ORDER = " ORDER BY c.categoryId DESC";

    @Override
    public List<Category_24162130> findAll() {
        EntityManager em = JpaConfig_24162130.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM Category c" + ORDER, Category_24162130.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Category_24162130> findPaging(int page, int pageSize) {
        EntityManager em = JpaConfig_24162130.getEntityManager();
        try {
            int firstResult = Math.max(page - 1, 0) * pageSize;
            return em.createQuery("SELECT c FROM Category c" + ORDER, Category_24162130.class)
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
            return em.createQuery("SELECT COUNT(c) FROM Category c", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public Category_24162130 findById(int categoryId) {
        EntityManager em = JpaConfig_24162130.getEntityManager();
        try {
            return em.find(Category_24162130.class, categoryId);
        } finally {
            em.close();
        }
    }

    @Override
    public Category_24162130 save(Category_24162130 category) {
        EntityManager em = JpaConfig_24162130.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(category);
            tx.commit();
            return category;
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
    public void update(Category_24162130 category) {
        EntityManager em = JpaConfig_24162130.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(category);
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
    public void delete(int categoryId) {
        EntityManager em = JpaConfig_24162130.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Category_24162130 ref = em.find(Category_24162130.class, categoryId);
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

    @Override
    public boolean isInUseByProduct(int categoryId) {
        EntityManager em = JpaConfig_24162130.getEntityManager();
        try {
            Long total = em.createQuery(
                            "SELECT COUNT(p) FROM Product p WHERE p.category.categoryId = :cid", Long.class)
                    .setParameter("cid", categoryId)
                    .getSingleResult();
            return total != null && total > 0;
        } finally {
            em.close();
        }
    }
}
