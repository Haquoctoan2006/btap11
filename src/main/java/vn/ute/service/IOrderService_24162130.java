package vn.ute.service;

import java.util.List;
import java.util.Map;

import vn.ute.dto.CheckoutInfo_24162130;
import vn.ute.entity.Order_24162130;
import vn.ute.exception.ServiceException_24162130;

/** Business Layer - thanh toan COD va lich su don hang. userId luon lay tu Session. */
public interface IOrderService_24162130 {

    /**
     * Chuan bi trang xac nhan don hang. Nem loi neu gio rong hoac co san pham het hang / vuot ton kho
     * (de controller dua nguoi dung quay lai /cart).
     */
    CheckoutInfo_24162130 prepareCheckout(int userId) throws ServiceException_24162130;

    /**
     * Dat hang COD trong 1 transaction: doc lai ton kho tu DB (khoa dong) -> tao Order + OrderItem
     * -> tru kho -> xoa CartItem da mua. Loi o bat ky buoc nao deu rollback.
     * Khong tin so luong/gia tu browser: chi dung du lieu CartItem trong database.
     * @return ma don hang vua tao
     */
    int placeOrder(int userId, String shippingAddress, String phone, String paymentMethod)
            throws ServiceException_24162130;

    /** Lich su don hang cua user, loc theo ma trang thai (null / "ALL" / khong hop le = tat ca). */
    List<Order_24162130> getOrderHistory(int userId, String statusCode);

    /** So don theo tung trang thai (khoa = ma viet hoa) de hien thi tren cac tab loc. */
    Map<String, Long> countByStatus(int userId);

    /** Chi tiet don; nem loi "khong tim thay" ca khi don ton tai nhung thuoc user khac. */
    Order_24162130 getOrderDetail(int userId, int orderId) throws ServiceException_24162130;
}
