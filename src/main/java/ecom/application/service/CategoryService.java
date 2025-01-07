package ecom.application.service;

import ecom.application.payload.CategoryRequest;
import ecom.application.payload.CategoryResponse;

public interface CategoryService {
    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    CategoryRequest createCategory(CategoryRequest categoryRequest);

    CategoryRequest deleteCategory(Long categoryId);


    CategoryRequest updateCategory(CategoryRequest categoryRequest, Long categoryId);
}

