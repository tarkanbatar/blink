package com.blink.order_service.model;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductInfo {
    private String id;
    private String name;
    private String sku;
    private Integer stock;
    private BigDecimal price;
}
