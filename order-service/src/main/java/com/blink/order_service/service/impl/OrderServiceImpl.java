package com.blink.order_service.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blink.order_service.client.CartServiceClient;
import com.blink.order_service.client.ProductServiceClient;
import com.blink.order_service.client.UserServiceClient;
import com.blink.order_service.dto.request.CreateOrderRequest;
import com.blink.order_service.dto.request.UpdateOrderStatusRequest;
import com.blink.order_service.dto.response.OrderResponse;
import com.blink.order_service.event.OrderEventPublisher;
import com.blink.order_service.exception.InsufficientStockException;
import com.blink.order_service.exception.OrderCancellationException;
import com.blink.order_service.exception.OrderNotFoundException;
import com.blink.order_service.model.CartInfo;
import com.blink.order_service.model.CartItemInfo;
import com.blink.order_service.model.Order;
import com.blink.order_service.model.OrderEvent;
import com.blink.order_service.model.OrderItem;
import com.blink.order_service.model.OrderStatus;
import com.blink.order_service.model.ShippingAddress;
import com.blink.order_service.model.UserInfo;
import com.blink.order_service.repository.OrderRepository;
import com.blink.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional // Ensure that all methods are wrapped in a transaction
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartServiceClient cartServiceClient;
    private final ProductServiceClient productServiceClient;
    private final UserServiceClient userServiceClient;
    private final OrderEventPublisher orderEventPublisher;

    /**
     * Create Order Flow:
     * 1. Fetch User Info
     * 2. Fetch Cart Items
     * 3. Check Stock
     * 4. Create Order
     * 5. Decrease Stock
     * 6. Clear Cart
     * 7. Publish Order Created Event on Kafka
     */
    @Override
    public OrderResponse createOrder(String userId, CreateOrderRequest request) {
        log.info("Creating order for user: {}", userId);

        UserInfo userInfo = userServiceClient.getUser(userId);
        if (userInfo == null) {
            log.error("User not found: {}", userId);
            throw new RuntimeException("User not found");
        }

        CartInfo cart = cartServiceClient.getCart(userId);
        if (cart == null || cart.getItems().isEmpty()) {
            log.error("Cart is empty for user: {}", userId);
            throw new RuntimeException("Cart is empty");
        }

        for (CartItemInfo item : cart.getItems()) {
            var product = productServiceClient.getProduct(item.getProductId());
            if (product == null || product.getStock() < item.getQuantity()) {
                throw new InsufficientStockException(item.getProductName(), item.getQuantity(),
                        product != null ? product.getStock() : 0);
            }
        }

        ShippingAddress shippingAddress = buildShippingAddress(request, userInfo);

        List<OrderItem> orderItems = cart.getItems().stream().map(item -> {
            var product = productServiceClient.getProduct(item.getProductId());
            return OrderItem.builder()
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .productImage(item.getProductImage())
                    .sku(product != null ? product.getSku() : "")
                    .quantity(item.getQuantity())
                    .unitPrice(product != null ? product.getPrice() : null)
                    .build();
        }).collect(Collectors.toList());

        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .userId(userId)
                .userEmail(userInfo.getEmail())
                .items(orderItems)
                .shippingAddress(shippingAddress)
                .status(OrderStatus.PENDING)
                .paymentMethod(request.getPaymentMethod())
                .notes(request.getNotes())
                .shippingCost(calculateShippingCost(cart.getTotalPrice()))
                .tax(calculateTax(cart.getTotalPrice()))
                .estimatedDeliveryDate(LocalDateTime.now().plusDays(5))
                .build();

        order.calculateTotals();

        Order savedOrder = orderRepository.save(order);
        log.info("Order created with id: {} for user: {}", savedOrder.getId(), userId);

        // ! Decrease Stock -- BURASI SIKINTILI OLABILIR KESIN TEST ET
        for (CartItemInfo item : cart.getItems()) {
            productServiceClient.decreaseStock(item.getProductId(), item.getQuantity());
        }

        cartServiceClient.clearCart(userId);
        orderEventPublisher.publishOrderCreated(savedOrder);

        return OrderResponse.fromEntity(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(String orderId) {
        log.info("Fetching order by id: {}", orderId);
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        return OrderResponse.fromEntity(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByNumber(String orderNumber) {
        log.info("Fetching order by number: {}", orderNumber);
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new OrderNotFoundException(orderNumber));
        return OrderResponse.fromEntity(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(String userId, int page, int size) {
        log.info("Fetching orders for user: {}", userId);
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable).getContent().stream()
                .map(OrderResponse::fromEntity).collect(Collectors.toList());
    }

    @Override
    public OrderResponse updateOrderStatus(String orderId, UpdateOrderStatusRequest request) {
        log.info("Updating order status for orderId: {} to {}", orderId, request.getStatus());

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        order.setStatus((OrderStatus) request.getStatus());

        if (request.getTrackingNumber() != null) {
            order.setTrackingNumber(request.getTrackingNumber());
        }

        if (request.getStatus() == OrderStatus.DELIVERED) {
            order.setActualDeliveryDate(LocalDateTime.now());
        }

        if (request.getStatus() == OrderStatus.CANCELLED && request.getCancellationReason() != null) {
            order.setCancellationReason(request.getCancellationReason());
        }

        Order updatedOrder = orderRepository.save(order);
        log.info("Order status updated for orderId: {} to {}", orderId, request.getStatus());

        orderEventPublisher.publishOrderStatusUpdated(updatedOrder);
        return OrderResponse.fromEntity(updatedOrder);
    }

    @Override
    public OrderResponse cancelOrder(String orderId, String reason) {
        log.info("Cancelling order:  {}", orderId);

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));

        if (! order.isCancellable()) {
            throw new OrderCancellationException(orderId, "Sipariş durumu iptal edilmeye uygun değil:  " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancellationReason(reason);

        Order cancelledOrder = orderRepository.save(order);

        for (OrderItem item : order.getItems()) 
            productServiceClient.increaseStock(item.getProductId(), item.getQuantity());
        

        log.info("Order cancelled: {}", orderId);

        orderEventPublisher.publishOrderStatusUpdated(cancelledOrder);

        return OrderResponse.fromEntity(cancelledOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStatus(OrderStatus status, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return orderRepository.findByStatus(status, pageable)
                                .getContent()
                                .stream()
                                .map(OrderResponse::fromEntity)
                                .collect(Collectors.toList());
    }


    // ==================== HELPER METHODS ====================


    private String generateOrderNumber() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uniquePart = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        return String.format("ORD-%s-%s", datePart, uniquePart);
    }

    private ShippingAddress buildShippingAddress(CreateOrderRequest request, UserInfo userInfo) {
        if (request.getShippingAddress() != null) {
            return ShippingAddress.builder()
                    .fullName(request.getShippingAddress().getFullName())
                    .addressLine1(request.getShippingAddress().getAddressLine1())
                    .addressLine2(request.getShippingAddress().getAddressLine2())
                    .city(request.getShippingAddress().getCity())
                    .state(request.getShippingAddress().getState())
                    .postalCode(request.getShippingAddress().getPostalCode())
                    .country(request.getShippingAddress().getCountry())
                    .phone(request.getShippingAddress().getPhone())
                    .build();
        } else if (userInfo.getDefaultAddress() != null) {
            var addr = userInfo.getDefaultAddress();
            return ShippingAddress.builder()
                    .fullName(userInfo.getFullName())
                    .addressLine1(addr.getAddressLine1())
                    .addressLine2(addr.getAddressLine2())
                    .city(addr.getCity())
                    .state(addr.getState())
                    .postalCode(addr.getZipCode())
                    .country(addr.getCountry())
                    .phone(userInfo.getPhone())
                    .build();
        } else {
            throw new RuntimeException("No shipping address provided and no default address found for user.");
        }
    }

    private BigDecimal calculateShippingCost (BigDecimal orderTotal) {
        if (orderTotal.compareTo(new BigDecimal("100")) >= 0) {
            return orderTotal.multiply(new BigDecimal("0.05")); // 5% shipping cost for orders above 100
        } else {
            return orderTotal.multiply(new BigDecimal("0.10")); // 10% shipping cost for orders below 100
        }
    }

    private BigDecimal calculateTax (BigDecimal orderTotal) {
        return orderTotal.multiply(new BigDecimal("0.18")); // 18% tax
    }
}
