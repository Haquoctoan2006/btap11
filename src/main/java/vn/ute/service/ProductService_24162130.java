package vn.ute.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.ute.constants.AppConstants_24162130;
import vn.ute.dto.PageResult_24162130;
import vn.ute.dto.SellerProductGroup_24162130;
import vn.ute.entity.Category_24162130;
import vn.ute.entity.Product_24162130;
import vn.ute.entity.Seller_24162130;
import vn.ute.exception.ServiceException_24162130;
import vn.ute.repository.CategoryRepository_24162130;
import vn.ute.repository.ICategoryRepository_24162130;
import vn.ute.repository.IProductRepository_24162130;
import vn.ute.repository.ISellerRepository_24162130;
import vn.ute.repository.ProductRepository_24162130;
import vn.ute.repository.SellerRepository_24162130;

public class ProductService_24162130 implements IProductService_24162130 {

    private final ISellerRepository_24162130 sellerRepository = new SellerRepository_24162130();
    private final IProductRepository_24162130 productRepository = new ProductRepository_24162130();
    private final ICategoryRepository_24162130 categoryRepository = new CategoryRepository_24162130();

    @Override
    public List<SellerProductGroup_24162130> getProductsGroupedBySeller() {
        // Chi 2 truy van cho toan trang (khong bi N+1): 1 lay cua hang, 1 lay san pham
        List<Seller_24162130> sellers = sellerRepository.findAll();
        List<Product_24162130> products = productRepository.findAllWithDetails();

        Map<Integer, List<Product_24162130>> bySeller = new HashMap<>();
        List<Product_24162130> unassigned = new ArrayList<>();
        for (Product_24162130 p : products) {
            Seller_24162130 s = p.getSeller();
            if (s == null || s.getSellerId() == null) {
                unassigned.add(p);
            } else {
                bySeller.computeIfAbsent(s.getSellerId(), k -> new ArrayList<>()).add(p);
            }
        }

        List<SellerProductGroup_24162130> result = new ArrayList<>();
        for (Seller_24162130 s : sellers) {
            List<Product_24162130> list = bySeller.remove(s.getSellerId());
            result.add(new SellerProductGroup_24162130(s, list == null ? List.of() : list));
        }
        if (!unassigned.isEmpty()) {
            result.add(new SellerProductGroup_24162130(null, unassigned));
        }
        return result;
    }

    @Override
    public Product_24162130 getProductDetail(int productId) throws ServiceException_24162130 {
        Product_24162130 product = productRepository.findByIdWithDetails(productId);
        if (product == null) {
            throw new ServiceException_24162130("Không tìm thấy sản phẩm có mã #" + productId);
        }
        return product;
    }

    @Override
    public PageResult_24162130<Product_24162130> getProductsPaging(int page, int pageSize) {
        int safePage = Math.max(page, 1);
        int safeSize = pageSize <= 0 ? AppConstants_24162130.DEFAULT_PAGE_SIZE : pageSize;

        long total = productRepository.count();
        int totalPages = (int) Math.max(1, Math.ceil(total / (double) safeSize));
        if (safePage > totalPages) {
            safePage = totalPages;
        }

        List<Product_24162130> items = productRepository.findPaging(safePage, safeSize);
        return new PageResult_24162130<>(items, safePage, safeSize, total);
    }

    @Override
    public List<Category_24162130> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Seller_24162130> getAllSellers() {
        return sellerRepository.findAll();
    }

    @Override
    public Product_24162130 createProduct(String productName, Long productCode, Integer categoryId,
            Integer sellerId, String description, Double price, Integer amount, Integer stock, Integer status,
            String images) throws ServiceException_24162130 {
        validateName(productName);
        validatePrice(price);

        Product_24162130 product = new Product_24162130();
        product.setProductName(productName.trim());
        product.setProductCode(productCode);
        product.setCategory(resolveCategory(categoryId));
        product.setSeller(resolveSeller(sellerId));
        product.setDescription(description);
        product.setPrice(price);
        product.setAmount(amount == null ? 0 : amount);
        product.setStock(stock == null ? 0 : stock);
        product.setStatus(status == null ? AppConstants_24162130.STATUS_ACTIVE : status);
        product.setImages(images);
        product.setWishlist(0);
        product.setCreateDate(LocalDate.now());
        return productRepository.save(product);
    }

    @Override
    public void updateProduct(int productId, String productName, Long productCode, Integer categoryId,
            Integer sellerId, String description, Double price, Integer amount, Integer stock, Integer status,
            String images) throws ServiceException_24162130 {
        validateName(productName);
        validatePrice(price);

        Product_24162130 product = productRepository.findByIdWithDetails(productId);
        if (product == null) {
            throw new ServiceException_24162130("Không tìm thấy sản phẩm có mã #" + productId);
        }

        product.setProductName(productName.trim());
        product.setProductCode(productCode);
        product.setCategory(resolveCategory(categoryId));
        product.setSeller(resolveSeller(sellerId));
        product.setDescription(description);
        product.setPrice(price);
        product.setAmount(amount == null ? 0 : amount);
        product.setStock(stock == null ? 0 : stock);
        product.setStatus(status == null ? product.getStatus() : status);
        // Chi thay anh khi co anh moi; khong chon anh moi thi giu nguyen anh cu
        if (images != null && !images.isBlank()) {
            product.setImages(images);
        }
        productRepository.update(product);
    }

    @Override
    public void deleteProduct(int productId) throws ServiceException_24162130 {
        Product_24162130 product = productRepository.findByIdWithDetails(productId);
        if (product == null) {
            throw new ServiceException_24162130("Không tìm thấy sản phẩm có mã #" + productId);
        }
        try {
            productRepository.delete(productId);
        } catch (RuntimeException e) {
            // San pham da nam trong don hang (OrderItem) thi khong xoa duoc de giu lich su don hang
            throw new ServiceException_24162130(
                    "Không thể xóa sản phẩm \"" + product.getProductName()
                            + "\" vì đã có trong đơn hàng. Hãy đặt trạng thái ngừng kinh doanh thay vì xóa");
        }
    }

    private Category_24162130 resolveCategory(Integer categoryId) {
        return categoryId == null ? null : categoryRepository.findById(categoryId);
    }

    private Seller_24162130 resolveSeller(Integer sellerId) {
        return sellerId == null ? null : sellerRepository.findById(sellerId);
    }

    private void validateName(String productName) throws ServiceException_24162130 {
        if (productName == null || productName.isBlank()) {
            throw new ServiceException_24162130("Tên sản phẩm không được để trống");
        }
        if (productName.trim().length() > 200) {
            throw new ServiceException_24162130("Tên sản phẩm không được vượt quá 200 ký tự");
        }
    }

    private void validatePrice(Double price) throws ServiceException_24162130 {
        if (price != null && price < 0) {
            throw new ServiceException_24162130("Giá sản phẩm không được nhỏ hơn 0");
        }
    }
}
