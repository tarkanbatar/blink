package com.blink.product_service.service.impl;

import org.springframework.cache.annotation.Caching;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blink.product_service.dto.request.CreateProductRequest;
import com.blink.product_service.dto.request.ProductFilterRequest;
import com.blink.product_service.dto.request.UpdateProductRequest;
import com.blink.product_service.dto.response.PagedResponse;
import com.blink.product_service.dto.response.ProductResponse;
import com.blink.product_service.exception.InsufficientStockException;
import com.blink.product_service.exception.ProductNotFoundException;
import com.blink.product_service.exception.SkuAlreadyExistsException;
import com.blink.product_service.model.Product;
import com.blink.product_service.model.ProductAttribute;
import com.blink.product_service.repository.ProductRepository;
import com.blink.product_service.service.CategoryService;
import com.blink.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final MongoTemplate mongoTemplate;

    /**
     * @Caching: Used for combining multiple caching operations.
     */

    @Override
    @Caching(evict = {@CacheEvict(value="products", allEntries=true), @CacheEvict(value="categories", allEntries=true)})
    public ProductResponse createProduct(CreateProductRequest request) {
        log.info("Creating product: {}", request.getName());

        if(productRepository.existsBySku(request.getSku())){
            log.warn("SKU already exists: {}", request.getSku());
            throw new SkuAlreadyExistsException(request.getSku());
        }

        categoryService.getCategoryById(request.getCategoryId());

        List<ProductAttribute> attributes = new ArrayList<>();

        if(request.getAttributes() != null) 
            request.getAttributes().forEach((key, value) -> attributes.add(ProductAttribute.builder().key(key).value(value).build()));
        
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                . sku(request.getSku(). toUpperCase())
                .price(request.getPrice())
                .discountPrice(request.getDiscountPrice())
                .categoryId(request.getCategoryId())
                .stock(request.getStock())
                .images(request.getImages() != null ? request. getImages() : new ArrayList<>())
                .brand(request.getBrand())
                .attributes(attributes)
                .featured(request.isFeatured())
                .build();

        Product savedProduct = productRepository.save(product);
        log.info("Product created: {}", savedProduct.getId());

        return ProductResponse.fromEntity(savedProduct);
    }

    @Override
    @Caching(evict = {@CacheEvict(value = "products", key = "'all'"),@CacheEvict(value = "featuredProducts", allEntries = true)})
    public ProductResponse updateProduct(String id, UpdateProductRequest request) {
        log.info("Updating product: {}", id);

        Product product = findById(id);

        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request. getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getDiscountPrice() != null) {
            product.setDiscountPrice(request. getDiscountPrice());
        }
        if (request. getCategoryId() != null) {
            categoryService.getCategoryById(request.getCategoryId());  // Validate
            product.setCategoryId(request. getCategoryId());
        }
        if (request.getStock() != null) {
            product.setStock(request.getStock());
        }
        if (request.getImages() != null) {
            product.setImages(request.getImages());
        }
        if (request.getBrand() != null) {
            product. setBrand(request. getBrand());
        }
        if (request.getAttributes() != null) {
            List<ProductAttribute> attributes = new ArrayList<>();
            request.getAttributes().forEach((key, value) ->
                attributes. add(ProductAttribute. builder().key(key).value(value).build())
            );
            product.setAttributes(attributes);
        }
        if (request. getActive() != null) {
            product.setActive(request.getActive());
        }
        if (request. getFeatured() != null) {
            product.setFeatured(request.getFeatured());
        }

        Product updatedProduct = productRepository. save(product);
        log.info("Product updated: {}", id);

        return ProductResponse.fromEntity(updatedProduct);
    }

    @Override
    @Caching(evict = {@CacheEvict(value = "products", key = "'all'"),@CacheEvict(value = "featuredProducts", allEntries = true)})
    public void deleteProduct(String id) {
        log.info("Deleting product: {}", id);
        Product product = findById(id);
        product.setActive(false);
        productRepository.save(product);
    }

    @Override
    @Cacheable(value = "products", key = "#id")
    @Transactional(readOnly = true)
    public ProductResponse getProductById(String id) {
        Product product = findById(id);
        return ProductResponse.fromEntity(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku.toUpperCase()).orElseThrow(() -> new ProductNotFoundException("sku", sku));
        return ProductResponse.fromEntity(product);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Product> productPage = productRepository. findByActiveTrue(pageable);
        Page<ProductResponse> responsePage = productPage.map(product ->  ProductResponse.fromEntity(product));
        
        return PagedResponse.from(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getProductsByCategory(String categoryId, int page, int size) {
        List<String> categoryIds = categoryService.getCategoryAndSubCategoryIds(categoryId);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Product> productPage = productRepository.findByCategoryIdInAndActiveTrue(categoryIds, pageable);
        
        Page<ProductResponse> responsePage = productPage.map(product -> ProductResponse.fromEntity(product));
        
        return PagedResponse.from(responsePage);
    }

    // Advanced search and filterin with MongoTemplate
    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> searchProducts(ProductFilterRequest filter) {
        log.info("Searching products with filters: {}", filter);

        Query query = new Query();
        
        //only active products
        query.addCriteria(Criteria.where("active").is(true));

        //category filter
        if(filter.getCategoryId() != null && !filter.getCategoryId().isBlank()) {
            List<String> categoryIds = categoryService.getCategoryAndSubCategoryIds(filter.getCategoryId());
            query.addCriteria(Criteria.where("categoryId").in(categoryIds));
        }

        //brand filter
        if (filter.getBrands() != null && !filter.getBrands().isEmpty()) {
            query.addCriteria(Criteria.where("brand").in(filter.getBrands()));
        }

        //price range filter
        if (filter. getMinPrice() != null || filter.getMaxPrice() != null) {
            Criteria priceCriteria = Criteria.where("price");
            if (filter.getMinPrice() != null) {
                priceCriteria = priceCriteria.gte(filter.getMinPrice());
            }
            if (filter.getMaxPrice() != null) {
                priceCriteria = priceCriteria.lte(filter.getMaxPrice());
            }
            query.addCriteria(priceCriteria);
        }

        //in-stock filter
        if (filter.getInStock() != null && filter.getInStock()) {
            query.addCriteria(Criteria.where("stock").gt(0));
        }

        //featured filter
        if (filter. getFeatured() != null && filter.getFeatured()) {
            query.addCriteria(Criteria.where("featured").is(true));
        }

        //minimum rating filter
        if (filter.getMinRating() != null) {
            query.addCriteria(Criteria.where("rating").gte(filter.getMinRating()));
        }

        // keyword search 
        if(filter.getKeyword() != null && filter.getKeyword().isBlank()) {
            Criteria keywordCriteria = new Criteria().orOperator(Criteria.where("name").regex(filter.getKeyword(), "i"),  // i means case-insensitive
                                                                Criteria.where("description").regex(filter.getKeyword(), "i"));
            query.addCriteria(keywordCriteria);
        }

        //Sort
        Sort.Direction direction = "asc".equalsIgnoreCase(filter.getSortDirection()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        query.with(Sort.by(direction, filter.getSortBy()));

        //Pagination
        long total = mongoTemplate.count(query, Product.class);

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize());
        query.with(pageable);

        List<Product> products = mongoTemplate.find(query, Product.class);
        List<ProductResponse> responses = products.stream().map(p -> ProductResponse.fromEntity(p)).collect(Collectors.toList());

        Page<ProductResponse> page = PageableExecutionUtils.getPage(responses, pageable, () -> total);

        return PagedResponse.from(page);
    }

    @Override
    @Cacheable(value = "featuredProducts")
    @Transactional(readOnly = true)
    public List<ProductResponse> getFeaturedProducts() {
        return productRepository.findByFeaturedTrueAndActiveTrue()
                .stream()
                .map(product -> ProductResponse.fromEntity(product))
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = "products", key = "#id")
    public ProductResponse updateStock(String id, int quantity) {
        log.info("Updating stock for product {}: {}", id, quantity);
        
        Product product = findById(id);
        product.setStock(quantity);
        Product updated = productRepository.save(product);
        
        return ProductResponse.fromEntity(updated);
    }

    @Override
    @CacheEvict(value = "products", key = "#productId")
    public void decreaseStock(String productId, int quantity) {
        log.info("Decreasing stock for product {}: -{}", productId, quantity);
        
        Product product = findById(productId);
        
        if (product. getStock() < quantity) {
            throw new InsufficientStockException(productId, quantity, product.getStock());
        }
        
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    @Override
    @CacheEvict(value = "products", key = "#productId")
    public void increaseStock(String productId, int quantity) {
        log.info("Increasing stock for product {}: +{}", productId, quantity);
        
        Product product = findById(productId);
        product.setStock(product.getStock() + quantity);
        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Product findById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("id", id));
    }
}
