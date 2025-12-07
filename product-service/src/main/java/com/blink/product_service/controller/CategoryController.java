package com.blink.product_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blink.product_service.model.Category;
import com.blink.product_service.service.CategoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;
    
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        log.info("Creating Category: {}", category.getName());
        Category created = categoryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService. getAllCategories();
        return ResponseEntity. ok(categories);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable String id, @RequestBody Category category){
        log.info("Updating Category: {}",id);
        Category updated = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id){
        log.info("Deleting Category: {}",id);
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable String id) {
        Category category = categoryService. getCategoryById(id);
        return ResponseEntity.ok(category);
    }


    @GetMapping("/root")
    public ResponseEntity<List<Category>> getRootCategories() {
        List<Category> categories = categoryService.getRootCategories();
        return ResponseEntity. ok(categories);
    }

    @GetMapping("/{id}/subcategories")
    public ResponseEntity<List<Category>> getSubCategories(@PathVariable String id) {
        List<Category> categories = categoryService.getSubCategories(id);
        return ResponseEntity.ok(categories);
    }

    
}
