package com.blink.order_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.blink.order_service.dto.request.CreateOrderRequest;
import com.blink.order_service.dto.request.UpdateOrderStatusRequest;
import com.blink.order_service.dto.response.OrderResponse;
import com.blink.order_service.model.OrderStatus;
import com.blink.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestHeader("X-User-Id") String userId, @Valid @RequestBody CreateOrderRequest request) {
        log.info("POST /api/orders - User: {}",userId);
        OrderResponse response = orderService.createOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable String orderId) {
        log.info("GET /api/orders/{}", orderId);
        OrderResponse response = orderService.getOrderById(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<OrderResponse> getOrderByNumber(@PathVariable String orderNumber) {
        log.info("GET /api/orders/number/{}", orderNumber);
        OrderResponse response = orderService.getOrderByNumber(orderNumber);
        return ResponseEntity. ok(response);
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponse>> getMyOrders( @RequestHeader("X-User-Id") String userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size ) {
        log.info("GET /api/orders/my-orders - User: {}", userId);
        List<OrderResponse> response = orderService.getUserOrders(userId, page, size);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus( @PathVariable String orderId, @Valid @RequestBody UpdateOrderStatusRequest request ) {
        log.info("PATCH /api/orders/{}/status - New Status: {}", orderId, request.getStatus());
        OrderResponse response = orderService.updateOrderStatus(orderId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder( @PathVariable String orderId, @RequestParam(required = false) String reason ) {
        log.info("POST /api/orders/{}/cancel - Reason: {}", orderId, reason);
        OrderResponse response = orderService.cancelOrder(orderId, reason);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus( @PathVariable OrderStatus status, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size ) {
        log.info("GET /api/orders/status/{}", status);
        List<OrderResponse> response = orderService.getOrdersByStatus(status, page, size);
        return ResponseEntity.ok(response);
    }
}
