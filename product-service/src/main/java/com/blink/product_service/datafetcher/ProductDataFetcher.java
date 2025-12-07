package com.blink.product_service.datafetcher;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.blink.product_service.dto.request.CreateProductRequest;
import com.blink.product_service.dto.request.ProductFilterRequest;
import com.blink.product_service.dto.request.UpdateProductRequest;
import com.blink.product_service.dto.response.PagedResponse;
import com.blink.product_service.dto.response.ProductResponse;
import com.blink.product_service.service.ProductService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@DgsComponent
@RequiredArgsConstructor
@Slf4j
public class ProductDataFetcher {

    private final ProductService productService;

    @DgsQuery
    public ProductResponse product(@InputArgument String id){
        log.info("Fetching product with id: {}", id);
        return productService.getProductById(id);
    }

    @DgsQuery
    public ProductResponse productBySku(@InputArgument String sku){
        log.info("Fetching product with sku: {}", sku);
        return productService.getProductBySku(sku);
    }

    @DgsQuery
    public PagedResponse<ProductResponse> products(
            @InputArgument Integer page,
            @InputArgument Integer size
    ) {
        int p = page != null ?  page : 0;
        int s = size != null ? size : 20;
        log.info("GraphQL: Getting all products, page: {}, size: {}", p, s);
        return productService.getAllProducts(p, s);
    }

    @DgsQuery
    public PagedResponse<ProductResponse> productsByCategory(
            @InputArgument String categoryId,
            @InputArgument Integer page,
            @InputArgument Integer size
    ) {
        int p = page != null ? page : 0;
        int s = size != null ? size : 20;
        log.info("GraphQL: Getting products by category: {}", categoryId);
        return productService.getProductsByCategory(categoryId, p, s);
    }

    @DgsQuery
    public PagedResponse<ProductResponse> searchProducts(
            @InputArgument("filter") Map<String, Object> filterMap
    ) {
        log.info("GraphQL: Searching products with filter");
        ProductFilterRequest filter = mapToFilterRequest(filterMap);
        return productService.searchProducts(filter);
    }

    @DgsQuery
    public List<ProductResponse> featuredProducts() {
        log.info("GraphQL: Getting featured products");
        return productService.getFeaturedProducts();
    }

    // ==================== MUTATIONS ====================

    @DgsMutation
    public ProductResponse createProduct(@InputArgument("input") Map<String, Object> input) {
        log.info("GraphQL: Creating product");
        CreateProductRequest request = mapToCreateRequest(input);
        return productService.createProduct(request);
    }

    @DgsMutation
    public ProductResponse updateProduct(
            @InputArgument String id,
            @InputArgument("input") Map<String, Object> input
    ) {
        log.info("GraphQL: Updating product: {}", id);
        UpdateProductRequest request = mapToUpdateRequest(input);
        return productService.updateProduct(id, request);
    }

    @DgsMutation
    public boolean deleteProduct(@InputArgument String id) {
        log.info("GraphQL: Deleting product: {}", id);
        productService.deleteProduct(id);
        return true;
    }

    @DgsMutation
    public ProductResponse updateStock(
            @InputArgument String id,
            @InputArgument Integer quantity
    ) {
        log.info("GraphQL: Updating stock for product {}: {}", id, quantity);
        return productService.updateStock(id, quantity);
    }

    // ==================== HELPER METHODS ====================

    @SuppressWarnings("unchecked")
    private CreateProductRequest mapToCreateRequest(Map<String, Object> input) {
        CreateProductRequest request = new CreateProductRequest();
        request.setName((String) input.get("name"));
        request.setDescription((String) input.get("description"));
        request.setSku((String) input.get("sku"));
        
        if (input. get("price") != null) {
            request.setPrice(new BigDecimal(input.get("price").toString()));
        }
        if (input.get("discountedPrice") != null) {
            request.setDiscountPrice(new BigDecimal(input. get("discountedPrice").toString()));
        }
        
        request.setCategoryId((String) input.get("categoryId"));
        
        if (input. get("stock") != null) {
            request.setStock((Integer) input.get("stock"));
        }
        
        request.setImages((List<String>) input. get("images"));
        request.setBrand((String) input. get("brand"));
        
        if (input.get("featured") != null) {
            request.setFeatured((Boolean) input.get("featured"));
        }

        // Attributes
        if (input.get("attributes") != null) {
            List<Map<String, String>> attrs = (List<Map<String, String>>) input.get("attributes");
            Map<String, String> attrMap = new java.util.HashMap<>();
            for (Map<String, String> attr : attrs) {
                attrMap.put(attr. get("key"), attr.get("value"));
            }
            request.setAttributes(attrMap);
        }

        return request;
    }

    @SuppressWarnings("unchecked")
    private UpdateProductRequest mapToUpdateRequest(Map<String, Object> input) {
        UpdateProductRequest request = new UpdateProductRequest();
        
        if (input. containsKey("name")) {
            request. setName((String) input.get("name"));
        }
        if (input. containsKey("description")) {
            request.setDescription((String) input.get("description"));
        }
        if (input.get("price") != null) {
            request.setPrice(new BigDecimal(input.get("price"). toString()));
        }
        if (input.get("discountedPrice") != null) {
            request.setDiscountPrice(new BigDecimal(input.get("discountedPrice").toString()));
        }
        if (input.containsKey("categoryId")) {
            request.setCategoryId((String) input.get("categoryId"));
        }
        if (input.get("stock") != null) {
            request.setStock((Integer) input.get("stock"));
        }
        if (input.containsKey("images")) {
            request.setImages((List<String>) input.get("images"));
        }
        if (input.containsKey("brand")) {
            request. setBrand((String) input.get("brand"));
        }
        if (input. get("active") != null) {
            request.setActive((Boolean) input.get("active"));
        }
        if (input.get("featured") != null) {
            request.setFeatured((Boolean) input. get("featured"));
        }

        return request;
    }

    @SuppressWarnings("unchecked")
    private ProductFilterRequest mapToFilterRequest(Map<String, Object> input) {
        ProductFilterRequest filter = new ProductFilterRequest();
        
        if (input.get("categoryId") != null) {
            filter.setCategoryId((String) input.get("categoryId"));
        }
        if (input.get("brands") != null) {
            filter.setBrands((List<String>) input.get("brands"));
        }
        if (input.get("minPrice") != null) {
            filter.setMinPrice(new BigDecimal(input.get("minPrice").toString()));
        }
        if (input.get("maxPrice") != null) {
            filter.setMaxPrice(new BigDecimal(input.get("maxPrice").toString()));
        }
        if (input.get("inStock") != null) {
            filter.setInStock((Boolean) input. get("inStock"));
        }
        if (input.get("featured") != null) {
            filter.setFeatured((Boolean) input. get("featured"));
        }
        if (input.get("minRating") != null) {
            filter.setMinRating(Double.parseDouble(input.get("minRating"). toString()));
        }
        if (input.get("keyword") != null) {
            filter.setKeyword((String) input.get("keyword"));
        }
        if (input.get("sortBy") != null) {
            filter.setSortBy((String) input. get("sortBy"));
        }
        if (input.get("sortDirection") != null) {
            filter.setSortDirection((String) input. get("sortDirection"));
        }
        if (input.get("page") != null) {
            filter.setPage((Integer) input.get("page"));
        }
        if (input.get("size") != null) {
            filter.setSize((Integer) input. get("size"));
        }

        return filter;
    }
    
}
