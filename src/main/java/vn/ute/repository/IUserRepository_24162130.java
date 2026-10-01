package vn.ute.repository;

import jakarta.persistence.EntityManager;

import vn.ute.entity.User_24162130;

/** Data Access Layer - truy van bang Users. */
public interface IUserRepository_24162130 {

    void insert(User_24162130 user);

    User_24162130 update(User_24162130 user);

    User_24162130 findById(int userId);

    User_24162130 findByUsername(String username);

    User_24162130 findByEmail(String email);

    /** Tim theo username HOAC email (dung cho dang nhap). */
    User_24162130 findByUsernameOrEmail(String identifier);

    /** Khoa dong user (FOR UPDATE) de tuan tu hoa cac thao tac gio hang/dat hang cua cung 1 user. */
    User_24162130 findByIdForUpdate(EntityManager em, int userId);
}
