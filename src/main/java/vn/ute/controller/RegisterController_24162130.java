package vn.ute.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import vn.ute.exception.ServiceException_24162130;
import vn.ute.service.IUserService_24162130;
import vn.ute.service.UserService_24162130;

/**
 * Dang ky co kich hoat OTP qua mail:
 *  /register    : GET form, POST tao tai khoan (chua kich hoat) + gui OTP
 *  /verify-otp  : GET form nhap OTP, POST xac thuc -> kich hoat
 *  /resend-otp  : POST gui lai OTP
 */
@WebServlet(urlPatterns = { "/register", "/verify-otp", "/resend-otp" })
public class RegisterController_24162130 extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(RegisterController_24162130.class.getName());
    private static final String REGISTER_VIEW = "/WEB-INF/views/account/register.jsp";
    private static final String VERIFY_VIEW = "/WEB-INF/views/account/verify-otp.jsp";
    private static final String SYSTEM_ERROR = "Lỗi hệ thống, vui lòng thử lại sau";

    private final IUserService_24162130 userService = new UserService_24162130();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/verify-otp".equals(path)) {
            String email = req.getParameter("email");
            if (email == null || email.isBlank()) {
                resp.sendRedirect(req.getContextPath() + "/register");
                return;
            }
            req.setAttribute("email", email);
            req.getRequestDispatcher(VERIFY_VIEW).forward(req, resp);
        } else if ("/resend-otp".equals(path)) {
            resp.sendRedirect(req.getContextPath() + "/register");
        } else {
            req.getRequestDispatcher(REGISTER_VIEW).forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (req.getServletPath()) {
            case "/register" -> handleRegister(req, resp);
            case "/verify-otp" -> handleVerify(req, resp);
            case "/resend-otp" -> handleResend(req, resp);
            default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String fullname = req.getParameter("fullname");
        String email = req.getParameter("email");
        String phone = req.getParameter("phone");
        String password = req.getParameter("password");
        String confirm = req.getParameter("confirmPassword");

        try {
            if (password == null || !password.equals(confirm)) {
                throw new ServiceException_24162130("Mật khẩu nhập lại không khớp");
            }
            userService.register(username, fullname, email, phone, password);
            String encoded = URLEncoder.encode(email.trim().toLowerCase(), StandardCharsets.UTF_8);
            resp.sendRedirect(req.getContextPath() + "/verify-otp?email=" + encoded);
        } catch (ServiceException_24162130 e) {
            showRegisterError(req, resp, e.getMessage());
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Loi he thong khi dang ky", e);
            showRegisterError(req, resp, SYSTEM_ERROR);
        }
    }

    private void showRegisterError(HttpServletRequest req, HttpServletResponse resp, String message)
            throws ServletException, IOException {
        req.setAttribute("error", message);
        // Giu lai du lieu da nhap (khong giu mat khau)
        req.setAttribute("username", req.getParameter("username"));
        req.setAttribute("fullname", req.getParameter("fullname"));
        req.setAttribute("email", req.getParameter("email"));
        req.setAttribute("phone", req.getParameter("phone"));
        req.getRequestDispatcher(REGISTER_VIEW).forward(req, resp);
    }

    private void handleVerify(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String email = req.getParameter("email");
        String otp = req.getParameter("otp");
        try {
            userService.verifyOtp(email, otp);
            resp.sendRedirect(req.getContextPath() + "/login?activated=1");
        } catch (ServiceException_24162130 e) {
            showVerify(req, resp, email, "error", e.getMessage());
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Loi he thong khi xac thuc OTP", e);
            showVerify(req, resp, email, "error", SYSTEM_ERROR);
        }
    }

    private void handleResend(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String email = req.getParameter("email");
        try {
            userService.resendOtp(email);
            showVerify(req, resp, email, "message", "Đã gửi mã OTP mới, vui lòng kiểm tra email");
        } catch (ServiceException_24162130 e) {
            showVerify(req, resp, email, "error", e.getMessage());
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Loi he thong khi gui lai OTP", e);
            showVerify(req, resp, email, "error", SYSTEM_ERROR);
        }
    }

    private void showVerify(HttpServletRequest req, HttpServletResponse resp, String email, String key, String text)
            throws ServletException, IOException {
        req.setAttribute("email", email);
        req.setAttribute(key, text);
        req.getRequestDispatcher(VERIFY_VIEW).forward(req, resp);
    }
}
