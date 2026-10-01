package vn.ute.dto;

/** Du lieu trang xac nhan don hang: gio hang da hop le + thong tin goi y cho form. */
public class CheckoutInfo_24162130 {

    private final CartSummary_24162130 cart;
    private final String fullname;
    private final String phone;

    public CheckoutInfo_24162130(CartSummary_24162130 cart, String fullname, String phone) {
        this.cart = cart;
        this.fullname = fullname;
        this.phone = phone;
    }

    public CartSummary_24162130 getCart() {
        return cart;
    }

    public String getFullname() {
        return fullname;
    }

    public String getPhone() {
        return phone;
    }
}
