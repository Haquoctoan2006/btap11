package vn.ute.dto;

import java.util.List;

/** Gio hang cua user: danh sach dong + tong tien. */
public class CartSummary_24162130 {

    private final List<CartLine_24162130> lines;
    private final double total;
    private final int totalQuantity;
    private final boolean hasIssue;

    public CartSummary_24162130(List<CartLine_24162130> lines) {
        this.lines = lines;
        double sum = 0;
        int qty = 0;
        boolean issue = false;
        for (CartLine_24162130 l : lines) {
            sum += l.getSubtotal();
            qty += l.getQuantity();
            issue |= l.isHasIssue();
        }
        this.total = sum;
        this.totalQuantity = qty;
        this.hasIssue = issue;
    }

    public List<CartLine_24162130> getLines() {
        return lines;
    }

    public double getTotal() {
        return total;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    /** Co it nhat 1 dong khong hop le (het hang, vuot ton kho...) -> khong cho thanh toan. */
    public boolean isHasIssue() {
        return hasIssue;
    }

    public boolean isEmpty() {
        return lines.isEmpty();
    }
}
