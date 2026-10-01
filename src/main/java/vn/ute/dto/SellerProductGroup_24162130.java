package vn.ute.dto;

import java.util.List;

import vn.ute.entity.Product_24162130;
import vn.ute.entity.Seller_24162130;

/**
 * Mot nhom hien thi: 1 cua hang (Seller) va cac san pham cua cua hang do.
 * seller == null nghia la nhom "san pham chua thuoc cua hang nao" (sellerId NULL trong DB).
 */
public class SellerProductGroup_24162130 {

    private final Seller_24162130 seller;
    private final List<Product_24162130> products;

    public SellerProductGroup_24162130(Seller_24162130 seller, List<Product_24162130> products) {
        this.seller = seller;
        this.products = products;
    }

    public Seller_24162130 getSeller() {
        return seller;
    }

    public List<Product_24162130> getProducts() {
        return products;
    }

    public int getProductCount() {
        return products.size();
    }

    public boolean isUnassigned() {
        return seller == null;
    }
}
