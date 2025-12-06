package com.blink.product_service.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @TextIndexed(weight = 3)    // index for text search with higher weight
    private String name;

    @TextIndexed(weight = 1) 
    private String description;

    @Indexed(unique = true)
    private String sku;  // Stock Keeping Unit

    private BigDecimal price;  // Using BigDecimal for currency representation- reduces floating point issues and provide better calculations
    private BigDecimal discountPrice;

    @Indexed
    private String categoryId;

    @Builder.Default
    private int stock = 0;

    @Builder.Default
    private List<String> images = new ArrayList<>();

    @Indexed
    private String brand;

    @Builder.Default
    private List<ProductAttribute> attributes = new ArrayList<>();

    @Builder.Default
    private double rating = 0.0;

    @Builder.Default
    private int reviewCount = 0;

    @Builder.Default
    private boolean active = true;

    @Builder.Default
    private boolean featured = false;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
    

    public Integer getDiscountPercentage() {
        if (discountPrice == null || price == null || price.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        BigDecimal discount = price.subtract(discountPrice);
        BigDecimal discountPercentage = discount.multiply(BigDecimal.valueOf(100)).divide(price, 2, java.math.RoundingMode.HALF_UP);
        return discountPercentage.intValue();
    }

    public BigDecimal getDiscountedPrice() {
        return discountPrice != null ? discountPrice : price;
    }

    public boolean isInStock() {
        return stock > 0;
    }

}
