package vn.ute.entity;

import jakarta.persistence.*;

/** Bang CartItem trong database ShopOnline. */
@Entity(name = "CartItem")
@Table(name = "CartItem")
public class CartItem_24162130 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cartItemId")
    private Integer cartItemId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "unitPrice")
    private Double unitPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId")
    private Product_24162130 product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cartId")
    private Cart_24162130 cart;

    public CartItem_24162130() {
    }

    public Integer getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(Integer cartItemId) {
        this.cartItemId = cartItemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Product_24162130 getProduct() {
        return product;
    }

    public void setProduct(Product_24162130 product) {
        this.product = product;
    }

    public Cart_24162130 getCart() {
        return cart;
    }

    public void setCart(Cart_24162130 cart) {
        this.cart = cart;
    }
}
