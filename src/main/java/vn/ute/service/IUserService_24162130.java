package vn.ute.service;

import vn.ute.entity.User_24162130;
import vn.ute.exception.ServiceException_24162130;

/** Business Layer - nghiep vu tai khoan (dang ky, OTP, dang nhap). */
public interface IUserService_24162130 {

    /** Dang ky tai khoan moi (chua kich hoat) va gui OTP qua email. */
    void register(String username, String fullname, String email, String phone, String rawPassword)
            throws ServiceException_24162130;

    /** Xac thuc OTP, dung thi kich hoat tai khoan. */
    void verifyOtp(String email, String otp) throws ServiceException_24162130;

    /** Gui lai OTP moi cho tai khoan chua kich hoat. */
    void resendOtp(String email) throws ServiceException_24162130;

    /** Dang nhap bang username hoac email. Sai thong tin / chua kich hoat -> nem loi. */
    User_24162130 login(String identifier, String rawPassword) throws ServiceException_24162130;
}
