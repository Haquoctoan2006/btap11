package vn.ute.entity;

import jakarta.persistence.*;

/** Bang Seller trong database ShopOnline. */
@Entity(name = "Seller")
@Table(name = "Seller")
public class Seller_24162130 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sellerId")
    private Integer sellerId;

    @Column(name = "sellername", length = 50)
    private String sellername;

    @Column(name = "images", length = 500)
    private String images;

    @Column(name = "status")
    private Integer status;

    public Seller_24162130() {
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellername() {
        return sellername;
    }

    public void setSellername(String sellername) {
        this.sellername = sellername;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
