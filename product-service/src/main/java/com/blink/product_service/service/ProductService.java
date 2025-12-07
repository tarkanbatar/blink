package com.blink.product_service.service;

import com.blink.product_service.dto.request.CreateProductRequest;
import com.blink.product_service.dto. request.ProductFilterRequest;
import com.blink.product_service.dto.request.UpdateProductRequest;
import com.blink.product_service.dto. response.PagedResponse;
import com.blink.product_service.dto.response.ProductResponse;
import com. blink.product_service.model.Product;

import java.util. List;

public interface ProductService {

    ProductResponse createProduct(CreateProductRequest request);
    ProductResponse updateProduct(String id, UpdateProductRequest request);
    void deleteProduct(String id);
    ProductResponse getProductById(String id);
    ProductResponse getProductBySku(String sku);
    PagedResponse<ProductResponse> getAllProducts(int page, int size);
    PagedResponse<ProductResponse> getProductsByCategory(String categoryId, int page, int size);
    PagedResponse<ProductResponse> searchProducts(ProductFilterRequest filter);
    List<ProductResponse> getFeaturedProducts();
    ProductResponse updateStock(String id, int quantity);
    void decreaseStock(String productId, int quantity);
    void increaseStock(String productId, int quantity);
    Product findById(String id);
}