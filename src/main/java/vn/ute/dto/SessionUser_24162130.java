package vn.ute.dto;

import java.io.Serializable;

import vn.ute.constants.AppConstants_24162130;
import vn.ute.entity.User_24162130;

/**
 * Thong tin nguoi dung luu trong Session.
 * Khong chua mat khau / ma OTP de tranh ro ri du lieu.
 */
public class SessionUser_24162130 implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int userId;
    private final String username;
    private final String fullname;
    private final int roleId;
    private final Integer sellerId;

    public SessionUser_24162130(int userId, String username, String fullname, int roleId, Integer sellerId) {
        this.userId = userId;
        this.username = username;
        this.fullname = fullname;
        this.roleId = roleId;
        this.sellerId = sellerId;
    }

    public static SessionUser_24162130 from(User_24162130 u) {
        int role = u.getRoleId() == null ? AppConstants_24162130.ROLE_USER : u.getRoleId();
        return new SessionUser_24162130(u.getUserId(), u.getUsername(), u.getFullname(), role, u.getSellerId());
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getFullname() {
        return fullname;
    }

    public int getRoleId() {
        return roleId;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public boolean isAdmin() {
        return roleId == AppConstants_24162130.ROLE_ADMIN;
    }

    public boolean isSeller() {
        return roleId == AppConstants_24162130.ROLE_SELLER;
    }

    public String getRoleName() {
        if (isAdmin()) {
            return "Admin";
        }
        return isSeller() ? "Seller" : "User";
    }

    /** Duong dan trang chu tuong ung voi vai tro (khong gom context path). */
    public String getHomePath() {
        if (isAdmin()) {
            return "/admin/home";
        }
        return isSeller() ? "/seller/home" : "/home";
    }
}
