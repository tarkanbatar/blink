package com.blink.product_service.datafetcher;

import java.util.List;
import java.util.Map;

import com.blink.product_service.model.Category;
import com.blink.product_service.service.CategoryService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@DgsComponent
@RequiredArgsConstructor
@Slf4j
public class CategoryDataFetcher {
    private final CategoryService categoryService;

    // ==================== QUERIES ====================

    @DgsQuery
    public Category category(@InputArgument String id) {
        log.info("GraphQL: Getting category by id: {}", id);
        return categoryService.getCategoryById(id);
    }

    @DgsQuery
    public List<Category> categories() {
        log.info("GraphQL: Getting all categories");
        return categoryService.getAllCategories();
    }

    @DgsQuery
    public List<Category> rootCategories() {
        log.info("GraphQL: Getting root categories");
        return categoryService.getRootCategories();
    }

    @DgsQuery
    public List<Category> subCategories(@InputArgument String parentId) {
        log. info("GraphQL: Getting subcategories for: {}", parentId);
        return categoryService.getSubCategories(parentId);
    }

    /**
     * Category type'ındaki subCategories field'ı için resolver
     */
    @DgsData(parentType = "Category", field = "subCategories")
    public List<Category> subCategoriesResolver(DgsDataFetchingEnvironment dfe) {
        Category category = dfe. getSource();
        return categoryService.getSubCategories(category. getId());
    }

    // ==================== MUTATIONS ====================

    @DgsMutation
    public Category createCategory(@InputArgument("input") Map<String, Object> input) {
        log.info("GraphQL: Creating category");
        Category category = mapToCategory(input);
        return categoryService.createCategory(category);
    }

    @DgsMutation
    public Category updateCategory(
            @InputArgument String id,
            @InputArgument("input") Map<String, Object> input
    ) {
        log.info("GraphQL: Updating category: {}", id);
        Category category = mapToCategory(input);
        return categoryService.updateCategory(id, category);
    }

    @DgsMutation
    public boolean deleteCategory(@InputArgument String id) {
        log.info("GraphQL: Deleting category: {}", id);
        categoryService.deleteCategory(id);
        return true;
    }

    // ==================== HELPER METHODS ====================

    private Category mapToCategory(Map<String, Object> input) {
        return Category.builder()
                .name((String) input.get("name"))
                .description((String) input.get("description"))
                .parentId((String) input.get("parentId"))
                .imageUrl((String) input.get("imageUrl"))
                .displayOrder(input.get("displayOrder") != null 
                    ? (Integer) input.get("displayOrder") 
                    : 0)
                . build();
    }
}
