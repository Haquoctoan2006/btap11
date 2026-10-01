package vn.ute.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import vn.ute.constants.AppConstants_24162130;
import vn.ute.constants.OrderStatus_24162130;

/**
 * Bang `Order` trong database ShopOnline.
 * "Order" la tu khoa SQL nen ten bang duoc quote bang dau huyen, ten entity JPQL la "ShopOrder".
 */
@Entity(name = "ShopOrder")
@Table(name = "`Order`")
public class Order_24162130 {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderId")
    private Integer orderId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userId", nullable = false)
    private User_24162130 user;

    @Column(name = "orderDate", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "totalAmount", nullable = false)
    private Double totalAmount;

    @Column(name = "paymentMethod", length = 20, nullable = false)
    private String paymentMethod;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "shippingAddress", length = 500)
    private String shippingAddress;

    @Column(name = "phone", length = 20)
    private String phone;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @OrderBy("orderItemId ASC")
    private List<OrderItem_24162130> items = new ArrayList<>();

    public Order_24162130() {
    }

    public void addItem(OrderItem_24162130 item) {
        item.setOrder(this);
        items.add(item);
    }

    // ---- Getter tien cho JSP (khong anh xa xuong DB vi entity dung field access) ----

    /** Nhan tieng Viet cua trang thai; gia tri la thi hien nguyen van de de phat hien. */
    public String getStatusLabel() {
        OrderStatus_24162130 s = OrderStatus_24162130.fromCode(status);
        return s == null ? String.valueOf(status) : s.getLabel();
    }

    /** Mau Bootstrap cua badge trang thai. */
    public String getStatusBadge() {
        OrderStatus_24162130 s = OrderStatus_24162130.fromCode(status);
        return s == null ? "secondary" : s.getBadge();
    }

    public String getPaymentLabel() {
        return AppConstants_24162130.PAYMENT_COD.equalsIgnoreCase(paymentMethod)
                ? AppConstants_24162130.PAYMENT_COD_LABEL
                : String.valueOf(paymentMethod);
    }

    /** JSTL fmt:formatDate khong ho tro LocalDateTime nen format san thanh chuoi. */
    public String getOrderDateText() {
        return orderDate == null ? "" : orderDate.format(DATE_FMT);
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public User_24162130 getUser() {
        return user;
    }

    public void setUser(User_24162130 user) {
        this.user = user;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<OrderItem_24162130> getItems() {
        return items;
    }

    public void setItems(List<OrderItem_24162130> items) {
        this.items = items;
    }
}
