package vn.ute.entity;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.*;

/** Bang Cart trong database ShopOnline. */
@Entity(name = "Cart")
@Table(name = "Cart")
public class Cart_24162130 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cartId")
    private Integer cartId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User_24162130 user;

    @Column(name = "buyDate")
    private LocalDateTime buyDate;

    @Column(name = "status")
    private Integer status;

    @OneToMany(mappedBy = "cart")
    private List<CartItem_24162130> items;

    public Cart_24162130() {
    }

    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    public User_24162130 getUser() {
        return user;
    }

    public void setUser(User_24162130 user) {
        this.user = user;
    }

    public LocalDateTime getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(LocalDateTime buyDate) {
        this.buyDate = buyDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<CartItem_24162130> getItems() {
        return items;
    }

    public void setItems(List<CartItem_24162130> items) {
        this.items = items;
    }
}
