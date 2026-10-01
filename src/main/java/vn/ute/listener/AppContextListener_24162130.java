package vn.ute.listener;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import vn.ute.config.JpaConfig_24162130;
import vn.ute.constants.AppConstants_24162130;

/** Khi khoi dong: dua thong tin sinh vien len application scope (footer) va kiem tra ket noi DB. */
@WebListener
public class AppContextListener_24162130 implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(AppContextListener_24162130.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        ctx.setAttribute("studentName", AppConstants_24162130.STUDENT_NAME);
        ctx.setAttribute("studentId", AppConstants_24162130.STUDENT_ID);
        ctx.setAttribute("examCode", AppConstants_24162130.EXAM_CODE);

        try {
            EntityManager em = JpaConfig_24162130.getEntityManager();
            em.close();
            LOG.info("Ket noi JPA/MySQL thanh cong.");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "KHONG ket noi duoc MySQL. Kiem tra: MySQL da chay chua, "
                    + "da chay database.sql chua, tai khoan root/12345 dung chua.", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        JpaConfig_24162130.close();
    }
}
