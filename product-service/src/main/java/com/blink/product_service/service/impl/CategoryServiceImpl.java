package com.blink.product_service.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blink.product_service.model.Category;
import com.blink.product_service.repository.CategoryRepository;
import com.blink.product_service.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public Category createCategory(Category category) {
        log.info("Creating new category: {}", category.getName());
        return categoryRepository.save(category);
    }

    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public Category updateCategory(String id, Category category) {
        log.info("Updating category: {}", id);

        Category existing = getCategoryById(id);

        if (category.getName() != null) {
            existing.setName(category.getName());
        }
        if (category.getDescription() != null) {
            existing.setDescription(category.getDescription());
        }
        if (category.getImageUrl() != null) {
            existing.setImageUrl(category.getImageUrl());
        }
        if (category.getParentId() != null) {
            existing.setParentId(category.getParentId());
        }

        existing.setDisplayOrder(category.getDisplayOrder());
        existing.setActive(category.isActive());

        return categoryRepository.save(existing);
    }

    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategory(String id) {
        log.info("Deleting category: {}", id);

        Category category = getCategoryById(id);
        category.setActive(false);
        categoryRepository.save(category);
    }

    @Override
    @Cacheable(value = "categories", key = "#id")
    public Category getCategoryById(String id) {
        log.info("Fetching category by id: {}", id);

        return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    @Override
    @Cacheable(value = "categories", key = "#name")
    public Category getCategoryByName(String name) {
        log.info("Fetching category by name: {}", name);

        return categoryRepository.findByName(name).orElseThrow(() -> new RuntimeException("Category not found with name: " + name));
    }

    @Override
    @Cacheable(value = "categories", key = "'all'")
    @Transactional(readOnly = true) // used for lazy loading if needed
    public List<Category> getAllCategories() {
        log.info("Fetching all categories");
        return categoryRepository.findAll();
    }

    @Override
    @Cacheable(value = "categories", key = "'root'")
    @Transactional(readOnly = true)
    public List<Category> getRootCategories() {
        log.info("Fetching root categories");
        return categoryRepository.findByParentIdIsNullAndActiveTrue();
    }

    @Override
    @Cacheable(value = "categories", key = "'sub_' + #parentId")
    @Transactional(readOnly = true)
    public List<Category> getSubCategories(String parentId) {
        return categoryRepository.findByParentIdAndActiveTrue(parentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getCategoryAndSubCategoryIds(String categoryId) {
        List<String> ids = new ArrayList<>(); //arraylist has used here to preserve unique category ids in order 
        ids.add(categoryId);
        collectSubCategoryIds(categoryId, ids);
        return ids;
    }

    private void collectSubCategoryIds(String parentId, List<String> ids) {
        List<Category> subCategories = categoryRepository.findByParentIdAndActiveTrue(parentId);
        for (Category sub : subCategories) {
            ids.add(sub.getId());
            collectSubCategoryIds(sub.getId(), ids);  // Recursive
        }
    }

}
