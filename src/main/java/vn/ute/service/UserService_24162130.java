package vn.ute.service;

import java.util.Locale;
import java.util.regex.Pattern;

import vn.ute.constants.AppConstants_24162130;
import vn.ute.entity.User_24162130;
import vn.ute.exception.ServiceException_24162130;
import vn.ute.repository.IUserRepository_24162130;
import vn.ute.repository.UserRepository_24162130;
import vn.ute.utils.MailUtil_24162130;
import vn.ute.utils.OtpUtil_24162130;
import vn.ute.utils.OtpUtil_24162130.OtpState;
import vn.ute.utils.PasswordUtil_24162130;

public class UserService_24162130 implements IUserService_24162130 {

    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9_.]{4,30}$");
    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)*\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^0\\d{9,10}$");

    /** Hash gia de dang nhap sai username van ton thoi gian nhu dung username (chong do thoi gian). */
    private static final String DUMMY_HASH = PasswordUtil_24162130.hash("dummy-password");

    private static final String LOGIN_FAILED = "Sai tên đăng nhập/email hoặc mật khẩu";

    private final IUserRepository_24162130 userRepository = new UserRepository_24162130();

    // ------------------------------------------------------------------ Dang ky
    @Override
    public void register(String username, String fullname, String email, String phone, String rawPassword)
            throws ServiceException_24162130 {
        username = trim(username);
        fullname = trim(fullname);
        email = trim(email).toLowerCase(Locale.ROOT);
        phone = trim(phone);

        if (!USERNAME.matcher(username).matches()) {
            throw new ServiceException_24162130("Tên đăng nhập gồm 4-30 ký tự: chữ, số, dấu chấm hoặc gạch dưới");
        }
        if (fullname.length() < 2 || fullname.length() > 50) {
            throw new ServiceException_24162130("Họ tên phải từ 2 đến 50 ký tự");
        }
        if (email.length() > 100 || !EMAIL.matcher(email).matches()) {
            throw new ServiceException_24162130("Email không đúng định dạng");
        }
        if (!phone.isEmpty() && !PHONE.matcher(phone).matches()) {
            throw new ServiceException_24162130("Số điện thoại không hợp lệ (ví dụ: 0912345678)");
        }
        if (rawPassword == null || rawPassword.length() < 6 || rawPassword.length() > 72) {
            throw new ServiceException_24162130("Mật khẩu phải từ 6 đến 72 ký tự");
        }

        User_24162130 byEmail = userRepository.findByEmail(email);
        User_24162130 byUsername = userRepository.findByUsername(username);

        if (byUsername != null && (byEmail == null || !byUsername.getUserId().equals(byEmail.getUserId()))) {
            throw new ServiceException_24162130("Tên đăng nhập đã được sử dụng");
        }
        if (byEmail != null && byEmail.getStatus() != null
                && byEmail.getStatus() == AppConstants_24162130.STATUS_ACTIVE) {
            throw new ServiceException_24162130("Email đã được đăng ký");
        }

        String otp = OtpUtil_24162130.generate();

        // Email da dang ky nhung chua kich hoat -> cho dang ky lai (cap nhat thong tin + OTP moi)
        User_24162130 user = (byEmail != null) ? byEmail : new User_24162130();
        user.setUsername(username);
        user.setFullname(fullname);
        user.setEmail(email);
        user.setPhone(phone.isEmpty() ? null : phone);
        user.setPassword(PasswordUtil_24162130.hash(rawPassword));
        user.setStatus(AppConstants_24162130.STATUS_INACTIVE);
        user.setCode(OtpUtil_24162130.newCode(otp));

        if (byEmail == null) {
            user.setRoleId(AppConstants_24162130.ROLE_USER); // dang ky qua web luon la User
            userRepository.insert(user);
        } else {
            userRepository.update(user);
        }

        MailUtil_24162130.sendOtp(email, fullname, otp);
    }

    // ------------------------------------------------------------------ OTP
    @Override
    public void verifyOtp(String email, String otp) throws ServiceException_24162130 {
        User_24162130 user = userRepository.findByEmail(trim(email).toLowerCase(Locale.ROOT));
        if (user == null) {
            throw new ServiceException_24162130("Mã OTP không đúng hoặc đã hết hạn");
        }
        if (user.getStatus() != null && user.getStatus() == AppConstants_24162130.STATUS_ACTIVE) {
            throw new ServiceException_24162130("Tài khoản đã được kích hoạt, bạn có thể đăng nhập");
        }

        OtpState state = OtpUtil_24162130.parse(user.getCode());
        if (state == null) {
            throw new ServiceException_24162130("Chưa có mã OTP hợp lệ, vui lòng bấm \"Gửi lại OTP\"");
        }
        if (state.attempts() >= AppConstants_24162130.OTP_MAX_ATTEMPTS) {
            throw new ServiceException_24162130("Bạn đã nhập sai quá nhiều lần, vui lòng bấm \"Gửi lại OTP\"");
        }
        if (state.isExpired()) {
            throw new ServiceException_24162130("Mã OTP đã hết hạn, vui lòng bấm \"Gửi lại OTP\"");
        }
        if (!OtpUtil_24162130.matches(state.otp(), otp)) {
            user.setCode(OtpUtil_24162130.encode(state.otp(), state.expiresAt(), state.attempts() + 1));
            userRepository.update(user);
            throw new ServiceException_24162130("Mã OTP không đúng");
        }

        user.setStatus(AppConstants_24162130.STATUS_ACTIVE);
        user.setCode(null);
        userRepository.update(user);
    }

    @Override
    public void resendOtp(String email) throws ServiceException_24162130 {
        User_24162130 user = userRepository.findByEmail(trim(email).toLowerCase(Locale.ROOT));
        if (user == null) {
            throw new ServiceException_24162130("Email chưa được đăng ký");
        }
        if (user.getStatus() != null && user.getStatus() == AppConstants_24162130.STATUS_ACTIVE) {
            throw new ServiceException_24162130("Tài khoản đã được kích hoạt, không cần gửi lại OTP");
        }

        OtpState old = OtpUtil_24162130.parse(user.getCode());
        if (old != null) {
            long waitMs = old.issuedAt() + AppConstants_24162130.OTP_RESEND_SECONDS * 1000L
                    - System.currentTimeMillis();
            if (waitMs > 0) {
                throw new ServiceException_24162130(
                        "Vui lòng đợi " + (waitMs / 1000 + 1) + " giây trước khi gửi lại OTP");
            }
        }

        String otp = OtpUtil_24162130.generate();
        user.setCode(OtpUtil_24162130.newCode(otp));
        userRepository.update(user);
        MailUtil_24162130.sendOtp(user.getEmail(), user.getFullname(), otp);
    }

    // ------------------------------------------------------------------ Dang nhap
    @Override
    public User_24162130 login(String identifier, String rawPassword) throws ServiceException_24162130 {
        identifier = trim(identifier);
        User_24162130 user = identifier.isEmpty() ? null : userRepository.findByUsernameOrEmail(identifier);

        if (user == null) {
            PasswordUtil_24162130.matches(rawPassword, DUMMY_HASH);
            throw new ServiceException_24162130(LOGIN_FAILED);
        }
        if (!PasswordUtil_24162130.matches(rawPassword, user.getPassword())) {
            throw new ServiceException_24162130(LOGIN_FAILED);
        }
        if (user.getStatus() == null || user.getStatus() != AppConstants_24162130.STATUS_ACTIVE) {
            throw new ServiceException_24162130(
                    "Tài khoản chưa được kích hoạt. Vui lòng nhập mã OTP đã gửi qua email");
        }
        return user;
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
