package vn.ute.dto;

import java.util.List;

/** Ket qua phan trang dung chung (Cau 5: Category, Product). */
public class PageResult_24162130<T> {

    private final List<T> items;
    private final int page;
    private final int pageSize;
    private final long totalItems;

    public PageResult_24162130(List<T> items, int page, int pageSize, long totalItems) {
        this.items = items;
        this.page = page;
        this.pageSize = pageSize;
        this.totalItems = totalItems;
    }

    public List<T> getItems() {
        return items;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public int getTotalPages() {
        if (pageSize <= 0) {
            return 1;
        }
        int pages = (int) Math.ceil(totalItems / (double) pageSize);
        return Math.max(pages, 1);
    }

    public boolean isHasPrev() {
        return page > 1;
    }

    public boolean isHasNext() {
        return page < getTotalPages();
    }
}
