package vn.ute.service;

import java.util.List;

import vn.ute.dto.PageResult_24162130;
import vn.ute.dto.SellerProductGroup_24162130;
import vn.ute.entity.Category_24162130;
import vn.ute.entity.Product_24162130;
import vn.ute.entity.Seller_24162130;
import vn.ute.exception.ServiceException_24162130;

/** Business Layer - nghiep vu san pham. */
public interface IProductService_24162130 {

    /**
     * Tat ca san pham gom theo tung cua hang (Seller), sap xep theo sellerId.
     * Cua hang chua co san pham van xuat hien (danh sach rong). San pham chua co
     * cua hang duoc gom vao nhom cuoi cung (seller = null).
     */
    List<SellerProductGroup_24162130> getProductsGroupedBySeller();

    /** Cau 4: chi tiet 1 san pham theo id. Nem loi neu khong ton tai. */
    Product_24162130 getProductDetail(int productId) throws ServiceException_24162130;

    /** Cau 5: phan trang danh sach san pham cho trang quan tri. */
    PageResult_24162130<Product_24162130> getProductsPaging(int page, int pageSize);

    /** Danh sach danh muc / cua hang de do vao combobox trong form Them/Sua. */
    List<Category_24162130> getAllCategories();

    List<Seller_24162130> getAllSellers();

    Product_24162130 createProduct(String productName, Long productCode, Integer categoryId, Integer sellerId,
            String description, Double price, Integer amount, Integer stock, Integer status, String images)
            throws ServiceException_24162130;

    void updateProduct(int productId, String productName, Long productCode, Integer categoryId, Integer sellerId,
            String description, Double price, Integer amount, Integer stock, Integer status, String images)
            throws ServiceException_24162130;

    void deleteProduct(int productId) throws ServiceException_24162130;
}
