package com.blink.product_service.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import com.blink.product_service.model.Product;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);

    Page<Product> findByActiveTrue(Pageable pageable);

    Page<Product> findByCategoryIdAndActiveTrue(String categoryId, Pageable pageable);

    Page<Product> findByBrandAndActiveTrue(String brand, Pageable pageable);

    List<Product> findByFeaturedTrueAndActiveTrue();

    Page<Product> findByPriceBetweenAndActiveTrue(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Page<Product> findByStockGreaterThanAndActiveTrue(int minStock, Pageable pageable);

    // mongo text search on name and description
    @Query("{ $text: { $search: ?0 }, active: true }")      // '?0' means first parameter
    Page<Product> searchByText(String keyword, Pageable pageable);

    Page<Product> findByCategoryIdInAndActiveTrue(List<String> categoryIds, Pageable pageable);
    Page<Product> findByBrandInAndActiveTrue(List<String> brands, Pageable pageable);

}
