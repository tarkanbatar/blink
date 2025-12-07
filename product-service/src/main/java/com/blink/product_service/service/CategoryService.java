package com.blink.product_service.service;

import java.util.List;
import com.blink.product_service.model.Category;

public interface CategoryService {
    
    Category createCategory(Category category);
    Category updateCategory(String id, Category category);
    void deleteCategory(String id);
    Category getCategoryById(String id);
    Category getCategoryByName(String name);
    List<Category> getAllCategories();
    List<Category> getRootCategories();
    List<Category> getSubCategories(String parentId);

    // Brings all categories and subcategories to be used for filtering.
    List<String> getCategoryAndSubCategoryIds(String categoryId);
}
