package com.blink.product_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.blink.product_service.model.Category;

@Repository
public interface CategoryRepository extends MongoRepository<Category,String>{
    Optional<Category> findByName(String name);
    boolean existsByName(String name);
    List<Category> findByParentIdIsNullAndActiveTrue();
    List<Category> findByParentIdAndActiveTrue(String parentId);
    List<Category> findByActiveTrueOrderByDisplayOrderAsc();

}
