package com.blink.order_service.service;

import java.util.List;
import com.blink.order_service.dto.request.CreateOrderRequest;
import com.blink.order_service.dto.request.UpdateOrderStatusRequest;
import com.blink.order_service.dto.response.OrderResponse;
import com.blink.order_service.model.OrderStatus;

public interface OrderService {
    OrderResponse createOrder(String userId, CreateOrderRequest request);
    OrderResponse getOrderById(String orderId);
    OrderResponse getOrderByNumber(String orderNumber);
    List<OrderResponse> getUserOrders(String userId, int page, int size);
    OrderResponse updateOrderStatus(String orderId, UpdateOrderStatusRequest request);
    OrderResponse cancelOrder(String orderId, String reason);
    List<OrderResponse> getOrdersByStatus(OrderStatus status, int page, int size);

}
