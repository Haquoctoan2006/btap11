package vn.ute.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import vn.ute.constants.AppConstants_24162130;
import vn.ute.dto.SessionUser_24162130;
import vn.ute.entity.User_24162130;
import vn.ute.exception.ServiceException_24162130;
import vn.ute.service.IUserService_24162130;
import vn.ute.service.UserService_24162130;

/**
 * GET  /login   : form dang nhap
 * POST /login   : dang nhap -> thanh cong: vao trang chu theo vai tro; that bai: quay lai trang dang nhap
 * POST /logout  : dang xuat (huy Session)
 */
@WebServlet(urlPatterns = { "/login", "/logout" })
public class LoginController_24162130 extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(LoginController_24162130.class.getName());
    private static final String LOGIN_VIEW = "/WEB-INF/views/account/login.jsp";

    private final IUserService_24162130 userService = new UserService_24162130();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("/logout".equals(req.getServletPath())) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }
        HttpSession session = req.getSession(false);
        Object attr = session == null ? null : session.getAttribute(AppConstants_24162130.SESSION_USER);
        if (attr instanceof SessionUser_24162130 user) {
            resp.sendRedirect(req.getContextPath() + user.getHomePath());
            return;
        }
        req.getRequestDispatcher(LOGIN_VIEW).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("/logout".equals(req.getServletPath())) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String identifier = req.getParameter("identifier");
        String password = req.getParameter("password");

        try {
            User_24162130 user = userService.login(identifier, password);

            // Doi session id sau khi dang nhap de chong session fixation
            if (req.getSession(false) != null) {
                req.changeSessionId();
            }
            SessionUser_24162130 su = SessionUser_24162130.from(user);
            HttpSession session = req.getSession(true);
            session.setAttribute(AppConstants_24162130.SESSION_USER, su);

            resp.sendRedirect(req.getContextPath() + su.getHomePath());
        } catch (ServiceException_24162130 e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("identifier", identifier);
            req.getRequestDispatcher(LOGIN_VIEW).forward(req, resp);
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Loi he thong khi dang nhap", e);
            req.setAttribute("error", "Lỗi hệ thống, vui lòng thử lại sau");
            req.setAttribute("identifier", identifier);
            req.getRequestDispatcher(LOGIN_VIEW).forward(req, resp);
        }
    }
}
