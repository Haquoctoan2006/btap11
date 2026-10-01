package vn.ute.service;

import java.util.List;

import vn.ute.constants.AppConstants_24162130;
import vn.ute.dto.PageResult_24162130;
import vn.ute.entity.Category_24162130;
import vn.ute.exception.ServiceException_24162130;
import vn.ute.repository.CategoryRepository_24162130;
import vn.ute.repository.ICategoryRepository_24162130;

public class CategoryService_24162130 implements ICategoryService_24162130 {

    private final ICategoryRepository_24162130 categoryRepository = new CategoryRepository_24162130();

    @Override
    public List<Category_24162130> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public PageResult_24162130<Category_24162130> getCategoriesPaging(int page, int pageSize) {
        int safePage = Math.max(page, 1);
        int safeSize = pageSize <= 0 ? AppConstants_24162130.DEFAULT_PAGE_SIZE : pageSize;

        long total = categoryRepository.count();
        int totalPages = (int) Math.max(1, Math.ceil(total / (double) safeSize));
        if (safePage > totalPages) {
            safePage = totalPages;
        }

        List<Category_24162130> items = categoryRepository.findPaging(safePage, safeSize);
        return new PageResult_24162130<>(items, safePage, safeSize, total);
    }

    @Override
    public Category_24162130 getById(int categoryId) throws ServiceException_24162130 {
        Category_24162130 category = categoryRepository.findById(categoryId);
        if (category == null) {
            throw new ServiceException_24162130("Không tìm thấy danh mục có mã #" + categoryId);
        }
        return category;
    }

    @Override
    public Category_24162130 create(String categoryName, Integer status, String images)
            throws ServiceException_24162130 {
        validateName(categoryName);

        Category_24162130 category = new Category_24162130();
        category.setCategoryName(categoryName.trim());
        category.setStatus(status == null ? AppConstants_24162130.STATUS_ACTIVE : status);
        category.setImages(images);
        return categoryRepository.save(category);
    }

    @Override
    public void update(int categoryId, String categoryName, Integer status, String images)
            throws ServiceException_24162130 {
        validateName(categoryName);
        Category_24162130 category = getById(categoryId);

        category.setCategoryName(categoryName.trim());
        category.setStatus(status == null ? category.getStatus() : status);
        // Chi thay anh khi co anh moi; khong chon anh moi thi giu nguyen anh cu
        if (images != null && !images.isBlank()) {
            category.setImages(images);
        }
        categoryRepository.update(category);
    }

    @Override
    public void delete(int categoryId) throws ServiceException_24162130 {
        getById(categoryId); // nem loi neu khong ton tai
        if (categoryRepository.isInUseByProduct(categoryId)) {
            throw new ServiceException_24162130(
                    "Không thể xóa danh mục #" + categoryId + " vì đang có sản phẩm sử dụng danh mục này");
        }
        categoryRepository.delete(categoryId);
    }

    private void validateName(String categoryName) throws ServiceException_24162130 {
        if (categoryName == null || categoryName.isBlank()) {
            throw new ServiceException_24162130("Tên danh mục không được để trống");
        }
        if (categoryName.trim().length() > 200) {
            throw new ServiceException_24162130("Tên danh mục không được vượt quá 200 ký tự");
        }
    }
}
