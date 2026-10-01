package vn.ute.repository;

import java.util.Collection;
import java.util.List;

import jakarta.persistence.EntityManager;

import vn.ute.entity.Product_24162130;

/** Data Access Layer - truy van bang Product. */
public interface IProductRepository_24162130 {

    /**
     * Tat ca san pham, da nap san Category va Seller (JOIN FETCH) nen dung duoc
     * o JSP sau khi EntityManager da dong (khong bi LazyInitializationException).
     */
    List<Product_24162130> findAllWithDetails();

    /** San pham cua 1 cua hang, cung da nap san Category va Seller. */
    List<Product_24162130> findBySellerId(int sellerId);

    /** Cau 4: 1 san pham theo id, da nap san Category va Seller (dung cho trang chi tiet). */
    Product_24162130 findByIdWithDetails(int productId);

    /** Cau 5: phan trang danh sach san pham (da nap san Category, Seller). */
    List<Product_24162130> findPaging(int page, int pageSize);

    /** Cau 5: tong so san pham (dung tinh totalPages). */
    long count();

    /** Gio hang/dat hang: doc 1 san pham bang EntityManager cua transaction goi (khong dong EM). */
    Product_24162130 findById(EntityManager em, int productId);

    /**
     * Dat hang: khoa dong (SELECT ... FOR UPDATE) cac san pham theo thu tu productId tang dan
     * de doc dung ton kho moi nhat va tranh deadlock giua 2 don dat cung luc.
     */
    List<Product_24162130> findByIdsForUpdate(EntityManager em, Collection<Integer> productIds);

    Product_24162130 save(Product_24162130 product);

    void update(Product_24162130 product);

    void delete(int productId);
}
