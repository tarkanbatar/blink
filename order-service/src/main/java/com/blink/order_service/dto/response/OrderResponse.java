package com.blink.order_service.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.blink.order_service.model.Order;
import com.blink.order_service.model.OrderStatus;
import com.blink.order_service.model.ShippingAddress;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponse {

    private String id;
    private String orderNumber;
    private String userId;
    private String userEmail;
    private List<OrderItemResponse> items;
    private int totalItems;
    private ShippingAddress shippingAddress;
    private OrderStatus status;
    private String statusDescription;
    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String paymentReference;
    private String notes;
    private String trackingNumber;
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime actualDeliveryDate;
    private String cancellationReason;
    private boolean cancellable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static OrderResponse fromEntity(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUserId())
                .userEmail(order.getUserEmail())
                .items(order.getItems().stream()
                        .map(OrderItemResponse::fromEntity)
                        .collect(Collectors.toList()))
                .totalItems(order.getTotalItems())
                .shippingAddress(order.getShippingAddress())
                .status(order.getStatus())
                .statusDescription(order.getStatusDescription())
                .subtotal(order. getSubtotal())
                .shippingCost(order. getShippingCost())
                .tax(order.getTax())
                .discount(order. getDiscount())
                .totalAmount(order.getTotalAmount())
                .paymentMethod(order.getPaymentMethod())
                .paymentReference(order.getPaymentReference())
                .notes(order.getNotes())
                .trackingNumber(order.getTrackingNumber())
                .estimatedDeliveryDate(order.getEstimatedDeliveryDate())
                .actualDeliveryDate(order.getActualDeliveryDate())
                .cancellationReason(order.getCancellationReason())
                .cancellable(order.isCancellable())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
