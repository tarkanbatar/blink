package com.blink.product_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.blink.product_service.dto.request.CreateProductRequest;
import com.blink.product_service.dto.request.ProductFilterRequest;
import com.blink.product_service.dto.request.UpdateProductRequest;
import com.blink.product_service.dto.response.PagedResponse;
import com.blink.product_service.dto.response.ProductResponse;
import com.blink.product_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody CreateProductRequest request
    ) {
        log.info("Creating product: {}", request.getName());
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        log.info("Updating product: {}", id);
        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        log. info("Deleting product: {}", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent(). build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String id) {
        ProductResponse response = productService.getProductById(id);
        return ResponseEntity. ok(response);
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductResponse> getProductBySku(@PathVariable String sku) {
        ProductResponse response = productService.getProductBySku(sku);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PagedResponse<ProductResponse> response = productService.getAllProducts(page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<PagedResponse<ProductResponse>> getProductsByCategory(
            @PathVariable String categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PagedResponse<ProductResponse> response = productService.getProductsByCategory(categoryId, page, size);
        return ResponseEntity. ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PagedResponse<ProductResponse>> searchProducts(
            @RequestBody ProductFilterRequest filter
    ) {
        log.info("Searching products with filter: {}", filter);
        PagedResponse<ProductResponse> response = productService.searchProducts(filter);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/featured")
    public ResponseEntity<List<ProductResponse>> getFeaturedProducts() {
        List<ProductResponse> response = productService.getFeaturedProducts();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductResponse> updateStock(
            @PathVariable String id,
            @RequestParam int quantity
    ) {
        log.info("Updating stock for product {}: {}", id, quantity);
        ProductResponse response = productService.updateStock(id, quantity);
        return ResponseEntity.ok(response);
    }
}
