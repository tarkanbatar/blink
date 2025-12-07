package com.blink.product_service.dto.request;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProductRequest {
    @Size(min = 2, max = 200, message = "Ürün adı 2-200 karakter arasında olmalıdır")
    private String name;

    @Size(max = 2000, message = "Açıklama en fazla 2000 karakter olabilir")
    private String description;

    @DecimalMin(value = "0.01", message = "Fiyat 0'dan büyük olmalıdır")
    private BigDecimal price;

    @DecimalMin(value = "0. 00", message = "İndirimli fiyat negatif olamaz")
    private BigDecimal discountPrice;

    private String categoryId;

    @Min(value = 0, message = "Stok negatif olamaz")
    private Integer stock;

    private List<String> images;

    private String brand;

    private Map<String, String> attributes;

    private Boolean active;

    private Boolean featured;
}
