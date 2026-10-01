package vn.ute.constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/** Hang so + cau hinh doc tu app.properties (footer, mail, OTP, vai tro). */
public final class AppConstants_24162130 {

    private AppConstants_24162130() {
    }

    // ---- Session ----
    public static final String SESSION_USER = "currentUser";

    // ---- Vai tro (khop du lieu bang UserRoles) ----
    public static final int ROLE_ADMIN = 1;
    public static final int ROLE_USER = 2;
    public static final int ROLE_SELLER = 3;

    // ---- Trang thai tai khoan / danh muc / san pham ----
    public static final int STATUS_INACTIVE = 0;
    public static final int STATUS_ACTIVE = 1;

    // ---- Gio hang (Cart.status): 1 = dang hoat dong ----
    public static final int CART_STATUS_ACTIVE = 1;

    // ---- Thanh toan ----
    public static final String PAYMENT_COD = "COD";
    public static final String PAYMENT_COD_LABEL = "Thanh toán khi nhận hàng (COD)";

    // ---- Trang thai don hang (luu o cot Order.status dang chuoi) ----
    public static final String ORDER_NEW = "NEW";
    public static final String ORDER_CONFIRMED = "CONFIRMED";
    public static final String ORDER_PREPARING = "PREPARING";
    public static final String ORDER_SHIPPING = "SHIPPING";
    public static final String ORDER_DELIVERING = "DELIVERING";
    public static final String ORDER_DELIVERED = "DELIVERED";
    public static final String ORDER_CANCELLED = "CANCELLED";
    public static final String ORDER_RETURNED = "RETURNED";

    // ---- Phan trang (Cau 5) ----
    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_PAGE_SIZE = 5;

    // ---- OTP ----
    public static final int OTP_VALID_MINUTES = 5;
    public static final int OTP_MAX_ATTEMPTS = 5;
    public static final int OTP_RESEND_SECONDS = 60;

    private static final Properties PROPS = load();

    // ---- Footer ----
    public static final String STUDENT_NAME = get("student.name", "HỌ VÀ TÊN SINH VIÊN");
    public static final String STUDENT_ID = get("student.id", "24162130");
    public static final String EXAM_CODE = get("exam.code", "05");

    // ---- Mail (bien moi truong uu tien hon app.properties) ----
    public static final String MAIL_HOST = get("mail.host", "smtp.gmail.com");
    public static final String MAIL_PORT = get("mail.port", "587");
    public static final String MAIL_USERNAME = env("MAIL_USERNAME", get("mail.username", ""));
    public static final String MAIL_PASSWORD = env("MAIL_PASSWORD", get("mail.password", ""));

    private static Properties load() {
        Properties p = new Properties();
        try (InputStream in = AppConstants_24162130.class.getResourceAsStream("/app.properties")) {
            if (in != null) {
                p.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            System.err.println("Khong doc duoc app.properties: " + e.getMessage());
        }
        return p;
    }

    private static String get(String key, String defaultValue) {
        String v = PROPS.getProperty(key);
        return (v == null || v.isBlank()) ? defaultValue : v.trim();
    }

    private static String env(String name, String fallback) {
        String v = System.getenv(name);
        return (v == null || v.isBlank()) ? fallback : v.trim();
    }
}
