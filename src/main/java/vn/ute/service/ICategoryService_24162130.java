package vn.ute.service;

import java.util.List;

import vn.ute.dto.PageResult_24162130;
import vn.ute.entity.Category_24162130;
import vn.ute.exception.ServiceException_24162130;

/** Business Layer - nghiep vu Category (Cau 5: CRUD + phan trang). */
public interface ICategoryService_24162130 {

    List<Category_24162130> getAllCategories();

    PageResult_24162130<Category_24162130> getCategoriesPaging(int page, int pageSize);

    Category_24162130 getById(int categoryId) throws ServiceException_24162130;

    Category_24162130 create(String categoryName, Integer status, String images) throws ServiceException_24162130;

    void update(int categoryId, String categoryName, Integer status, String images)
            throws ServiceException_24162130;

    void delete(int categoryId) throws ServiceException_24162130;
}
