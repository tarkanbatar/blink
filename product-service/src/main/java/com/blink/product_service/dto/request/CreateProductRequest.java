package com.blink.product_service.dto.request;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateProductRequest {
    
    @NotBlank(message = "Prdoduct name cannot be blank")
    @Size(min = 2, max = 200, message = "Product name must be between 2 and 200 characters")
    private String name;

    @Size(max = 2000, message = "Description can be at most 2000 characters long")
    private String description;

    @NotBlank(message = "SKU cannot be blank")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "SKU can only contain uppercase letters, numbers, and hyphens")
    private String sku;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    @DecimalMin(value = "0.01", message = "Discounted price must be greater than 0")
    private BigDecimal discountPrice;

    @NotBlank(message = "Category must be selected")
    private String categoryId;

    @Min(value = 0, message = "Stock cannot be negative")
    private int stock = 0;

    private List<String> images;

    private String brand;

    private Map<String, String> attributes;

    private boolean featured = false;
}
