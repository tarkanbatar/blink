package com.blink.order_service.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.blink.order_service.model.Order;
import com.blink.order_service.model.OrderStatus;

import java.util.List;
import java.time.LocalDateTime;


public interface OrderRepository extends MongoRepository<Order, String> {
    Optional<Order> findByOrderNumber(String orderNumber);
    
    // User's orders
    Page<Order> findByUserIdOrderByCreatedAt(String userId, Pageable pageable);
    Page<Order> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
    List<Order> findByUserIdAndStatus(String userId, OrderStatus status);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    Page<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    List<Order> findByUserIdAndCreatedAtBetween(String userId, LocalDateTime startDate, LocalDateTime endDate);
    List<Order> findByStatusAndCreatedAtBefore(OrderStatus status, LocalDateTime dateTime);

}
