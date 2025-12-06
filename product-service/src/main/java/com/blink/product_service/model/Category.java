package com.blink.product_service.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "categories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category implements Serializable{
    private static final long serialVersionUID = 1L; // For Serializable class versioning

    @Id
    private String id;

    @Indexed(unique = true) // unique = true to prevent duplicate category names
    private String name;

    private String description;
    private String parentId; // For sub-categories, if it is null, then it's a main category
    private String imageUrl;

    @Builder.Default
    private boolean isActive = true;

    @Builder.Default
    private int displayOrder = 0;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}