package vn.ute.constants;

/**
 * Trang thai don hang. Gia tri luu trong database la {@link #getCode()} (chuoi),
 * tren giao dien hien thi {@link #getLabel()} (tieng Viet).
 */
public enum OrderStatus_24162130 {

    NEW(AppConstants_24162130.ORDER_NEW, "Đơn hàng mới", "primary"),
    CONFIRMED(AppConstants_24162130.ORDER_CONFIRMED, "Đã xác nhận", "info"),
    PREPARING(AppConstants_24162130.ORDER_PREPARING, "Chuẩn bị hàng", "warning"),
    SHIPPING(AppConstants_24162130.ORDER_SHIPPING, "Vận chuyển", "secondary"),
    DELIVERING(AppConstants_24162130.ORDER_DELIVERING, "Giao hàng", "dark"),
    DELIVERED(AppConstants_24162130.ORDER_DELIVERED, "Đã giao", "success"),
    CANCELLED(AppConstants_24162130.ORDER_CANCELLED, "Đơn hàng hủy", "danger"),
    RETURNED(AppConstants_24162130.ORDER_RETURNED, "Đơn hàng hoàn", "danger");

    private final String code;
    private final String label;
    private final String badge;

    OrderStatus_24162130(String code, String label, String badge) {
        this.code = code;
        this.label = label;
        this.badge = badge;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    /** Ten mau Bootstrap (primary, success, ...) dung cho badge. */
    public String getBadge() {
        return badge;
    }

    /** Tim theo ma, khong phan biet hoa thuong va khoang trang; khong khop -> null. */
    public static OrderStatus_24162130 fromCode(String code) {
        if (code == null) {
            return null;
        }
        String c = code.trim();
        for (OrderStatus_24162130 s : values()) {
            if (s.code.equalsIgnoreCase(c)) {
                return s;
            }
        }
        return null;
    }
}
