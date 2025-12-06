package com.blink.product_service.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.blink.product_service.model.Product;
import com.blink.product_service.model.ProductAttribute;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponse {
    private String id;
    private String name;
    private String description;
    private String sku;
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private Integer discountPercentage;
    private String categoryId;
    private String categoryName;  // Category bilgisi de eklenecek
    private int stock;
    private boolean inStock;
    private List<String> images;
    private String brand;
    private List<ProductAttribute> attributes;
    private double rating;
    private int reviewCount;
    private boolean active;
    private boolean featured;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProductResponse fromEntity(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product. getDescription())
                .sku(product.getSku())
                .price(product.getPrice())
                .discountedPrice(product.getDiscountedPrice())
                .discountPercentage(product.getDiscountPercentage())
                .categoryId(product.getCategoryId())
                .stock(product.getStock())
                .inStock(product.isInStock())
                .images(product. getImages())
                .brand(product. getBrand())
                .attributes(product. getAttributes())
                .rating(product. getRating())
                .reviewCount(product.getReviewCount())
                .active(product.isActive())
                .featured(product.isFeatured())
                .createdAt(product.getCreatedAt())
                . updatedAt(product.getUpdatedAt())
                .build();
    }
}
