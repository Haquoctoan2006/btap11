package vn.ute.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/** Tao EntityManager tu persistence unit "ShopOnlinePU" (MySQL root/12345). */
public final class JpaConfig_24162130 {

    private static EntityManagerFactory factory;

    private JpaConfig_24162130() {
    }

    public static synchronized EntityManager getEntityManager() {
        if (factory == null || !factory.isOpen()) {
            factory = Persistence.createEntityManagerFactory("ShopOnlinePU");
        }
        return factory.createEntityManager();
    }

    /** Goi khi ung dung dung de dong ket noi. */
    public static synchronized void close() {
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
        factory = null;
    }
}
