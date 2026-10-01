package vn.ute.repository;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;

import vn.ute.config.JpaConfig_24162130;
import vn.ute.entity.User_24162130;

public class UserRepository_24162130 implements IUserRepository_24162130 {

    @Override
    public void insert(User_24162130 user) {
        EntityManager em = JpaConfig_24162130.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(user);
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
    public User_24162130 update(User_24162130 user) {
        EntityManager em = JpaConfig_24162130.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            User_24162130 merged = em.merge(user);
            tx.commit();
            return merged;
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
    public User_24162130 findById(int userId) {
        EntityManager em = JpaConfig_24162130.getEntityManager();
        try {
            return em.find(User_24162130.class, userId);
        } finally {
            em.close();
        }
    }

    @Override
    public User_24162130 findByUsername(String username) {
        return findOne("SELECT u FROM User u WHERE u.username = :v", username);
    }

    @Override
    public User_24162130 findByEmail(String email) {
        return findOne("SELECT u FROM User u WHERE u.email = :v", email);
    }

    @Override
    public User_24162130 findByUsernameOrEmail(String identifier) {
        return findOne("SELECT u FROM User u WHERE u.username = :v OR u.email = :v", identifier);
    }

    @Override
    public User_24162130 findByIdForUpdate(EntityManager em, int userId) {
        return em.find(User_24162130.class, userId, LockModeType.PESSIMISTIC_WRITE);
    }

    private User_24162130 findOne(String jpql, String value) {
        EntityManager em = JpaConfig_24162130.getEntityManager();
        try {
            List<User_24162130> list = em.createQuery(jpql, User_24162130.class)
                    .setParameter("v", value)
                    .setMaxResults(1)
                    .getResultList();
            return list.isEmpty() ? null : list.get(0);
        } finally {
            em.close();
        }
    }
}
