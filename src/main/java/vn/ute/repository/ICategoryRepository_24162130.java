package vn.ute.repository;

import java.util.List;

import vn.ute.entity.Category_24162130;

/** Data Access Layer - CRUD + phan trang bang Category (Cau 5). */
public interface ICategoryRepository_24162130 {

    List<Category_24162130> findAll();

    List<Category_24162130> findPaging(int page, int pageSize);

    long count();

    Category_24162130 findById(int categoryId);

    Category_24162130 save(Category_24162130 category);

    void update(Category_24162130 category);

    void delete(int categoryId);

    /** True neu Category dang duoc it nhat 1 Product tham chieu (khong cho xoa). */
    boolean isInUseByProduct(int categoryId);
}
