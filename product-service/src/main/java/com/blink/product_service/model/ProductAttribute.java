package com.blink.product_service.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Since it has to store various attribute types, we can use a generic key-value structure here.

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttribute implements Serializable {

    private static final long serialVersionUID = 1L;

    private String key;
    private String value;
    
}
