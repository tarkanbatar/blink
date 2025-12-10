package com.blink.order_service.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    private String id;

    @Indexed(unique = true)
    private String orderNumber;

    @Indexed
    private String userId;

    private String userEmail;

    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();
    private ShippingAddress shippingAddress;

    @Indexed
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    private String statusDescription;
    private BigDecimal subtotal;

    @Builder.Default
    private BigDecimal shippingCost = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal tax = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal discount = BigDecimal.ZERO;

    private BigDecimal totalAmount;
    private String paymentMethod;
    private String paymentReference;
    private String notes;
    private String trackingNumber;
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime actualDeliveryDate;
    private String cancellationReason;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // ========= HELPERS =========

    public int getTotalItems() {
        return items.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }

    public boolean isCancellable() {
        return status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED;
    }

    public void calculateTotals() {
        this.subtotal = items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal:: add);

        this.totalAmount = subtotal
                .add(shippingCost != null ? shippingCost :  BigDecimal.ZERO)
                .add(tax != null ?  tax : BigDecimal.ZERO)
                .subtract(discount != null ? discount : BigDecimal. ZERO);
    }
}
