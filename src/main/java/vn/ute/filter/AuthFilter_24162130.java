package vn.ute.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import vn.ute.constants.AppConstants_24162130;
import vn.ute.dto.SessionUser_24162130;

/**
 * Phan quyen:
 *  - /admin/*  : chi Admin
 *  - /seller/* : chi Seller
 *  - /cart, /checkout, /orders (va con): chi User (nguoi mua)
 * Chua dang nhap -> ve trang dang nhap; sai vai tro -> 403.
 */
public class AuthFilter_24162130 implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        Object attr = session == null ? null : session.getAttribute(AppConstants_24162130.SESSION_USER);

        if (!(attr instanceof SessionUser_24162130 user)) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String path = req.getServletPath();
        boolean buyerArea = isUnder(path, "/cart") || isUnder(path, "/checkout") || isUnder(path, "/orders");
        boolean plainUser = !user.isAdmin() && !user.isSeller();
        if ((path.startsWith("/admin") && !user.isAdmin()) || (path.startsWith("/seller") && !user.isSeller())
                || (buyerArea && !plainUser)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Khong cho trinh duyet cache trang da dang nhap (bam Back sau khi dang xuat)
        resp.setHeader("Cache-Control", "no-store");
        chain.doFilter(request, response);
    }

    /** path == prefix hoac nam duoi prefix + "/" (tranh khop nham "/cartoon"). */
    private static boolean isUnder(String path, String prefix) {
        return path.equals(prefix) || path.startsWith(prefix + "/");
    }
}
