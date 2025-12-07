package com.blink.product_service.dto.request;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class ProductFilterRequest {
    private String categoryId;
    private List<String> brands;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean inStock;
    private Boolean featured;
    private Double minRating;
    private String keyword;

    //Sort
    private String sortBy = "createdAt";
    private String sortDirection = "desc";

    //Pagination
    private int page = 0;
    private int size = 20;
}
