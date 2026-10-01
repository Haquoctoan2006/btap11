package vn.ute.service;

import vn.ute.dto.CartSummary_24162130;
import vn.ute.exception.ServiceException_24162130;

/** Business Layer - gio hang. Moi ham deu nhan userId lay tu Session, khong bao gio tu request. */
public interface ICartService_24162130 {

    /** Gio hang hien tai (rong neu chua co), kem tong tien va canh bao ton kho. */
    CartSummary_24162130 getCart(int userId);

    /**
     * Them san pham vao gio (tao gio neu chua co; da co san pham thi cong don quantity).
     * @return ten san pham da them (de hien thong bao)
     */
    String addToCart(int userId, int productId, int quantity) throws ServiceException_24162130;

    /** Dat so luong moi (&gt;0 va &lt;= ton kho) cho 1 dong thuoc gio cua chinh user. */
    void updateQuantity(int userId, int cartItemId, int quantity) throws ServiceException_24162130;

    /** Xoa 1 dong thuoc gio cua chinh user. */
    void removeItem(int userId, int cartItemId) throws ServiceException_24162130;

    /** Xoa toan bo gio hang cua user. */
    void clearCart(int userId) throws ServiceException_24162130;
}
