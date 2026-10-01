package vn.ute.dto;

/** 1 dong trong trang gio hang (du lieu da tinh san, JSP chi viec hien thi). */
public class CartLine_24162130 {

    private final int cartItemId;
    private final int productId;
    private final String productName;
    private final String images;
    private final double unitPrice;
    private final Double currentPrice;
    private final int quantity;
    private final int stock;
    private final String issue;

    public CartLine_24162130(int cartItemId, int productId, String productName, String images,
            double unitPrice, Double currentPrice, int quantity, int stock, String issue) {
        this.cartItemId = cartItemId;
        this.productId = productId;
        this.productName = productName;
        this.images = images;
        this.unitPrice = unitPrice;
        this.currentPrice = currentPrice;
        this.quantity = quantity;
        this.stock = stock;
        this.issue = issue;
    }

    public int getCartItemId() {
        return cartItemId;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getImages() {
        return images;
    }

    /** Gia luc them vao gio (CartItem.unitPrice). */
    public double getUnitPrice() {
        return unitPrice;
    }

    /** Gia hien tai cua san pham (co the khac unitPrice neu shop doi gia). */
    public Double getCurrentPrice() {
        return currentPrice;
    }

    public boolean isPriceChanged() {
        return currentPrice != null && Math.abs(currentPrice - unitPrice) > 0.0001;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getStock() {
        return stock;
    }

    public double getSubtotal() {
        return unitPrice * quantity;
    }

    /** Thong bao van de (het hang, vuot ton kho, ngung kinh doanh...) hoac null neu hop le. */
    public String getIssue() {
        return issue;
    }

    public boolean isHasIssue() {
        return issue != null;
    }
}
