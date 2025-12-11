package com.blink.order_service.datafetcher;

import java.util.List;
import java.util.Map;

import com.blink.order_service.dto.request.CreateOrderRequest;
import com.blink.order_service.dto.request.ShippingAddressRequest;
import com.blink.order_service.dto.request.UpdateOrderStatusRequest;
import com.blink.order_service.dto.response.OrderResponse;
import com.blink.order_service.model.OrderStatus;
import com.blink.order_service.service.OrderService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@DgsComponent
@RequiredArgsConstructor
@Slf4j
public class OrderDataFetcher {
    
    private final OrderService orderService;

    @DgsQuery
    public OrderResponse order(@InputArgument String id) {
        log.info("GraphQL Fetching order with id: {}", id);
        return orderService.getOrderById(id);
    }

    @DgsQuery
    public OrderResponse orderByNumber(@InputArgument String orderNumber) {
        log.info("GraphQL Fetching order with number: {}", orderNumber);
        return orderService.getOrderByNumber(orderNumber);
    }

    @DgsQuery
    public List<OrderResponse> myOrders(@InputArgument String userId, @InputArgument Integer page, @InputArgument Integer size) {
        int p = page != null ? page : 0;
        int s = size != null ? size : 10;
        log.info("GraphQL Fetching orders for user: {}", userId);
        return orderService.getUserOrders(userId, p, s);
    }

    @DgsQuery
    public List<OrderResponse> ordersByStatus(@InputArgument String status, @InputArgument Integer page, @InputArgument Integer size) {
        int p = page != null ? page : 0;
        int s = size != null ? size : 10;
        log.info("GraphQL Fetching orders with status: {}", status);
        return orderService.getOrdersByStatus(com.blink.order_service.model.OrderStatus.valueOf(status), p, s);
    }

    @DgsMutation
    public OrderResponse createOrder(@InputArgument String userId, @InputArgument("input") Map<String, Object> input) {
        log.info("GraphQL Mutation: Creating order for user: {}", userId);
        CreateOrderRequest request = mapToCreateRequest(input);
        return orderService.createOrder(userId, request);
    }

    @DgsMutation
    public OrderResponse updateOrderStatus(@InputArgument String orderId, @InputArgument("input") Map<String, Object> input) {
        log.info("GraphQL: Updating order status:  {}", orderId);
        UpdateOrderStatusRequest request = mapToUpdateStatusRequest(input);
        return orderService.updateOrderStatus(orderId, request);
    }

    @DgsMutation
    public OrderResponse cancelOrder(@InputArgument String orderId,@InputArgument String reason) {
        log.info("GraphQL: Cancelling order: {}", orderId);
        return orderService.cancelOrder(orderId, reason);
    }

    @SuppressWarnings("unchecked")
    private CreateOrderRequest mapToCreateRequest(Map<String, Object> input) {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setPaymentMethod((String) input.get("paymentMethod"));
        request.setNotes((String) input.get("notes"));
        request.setCouponCode((String) input.get("couponCode"));

        if (input.get("shippingAddress") != null) {
            Map<String, Object> addrMap = (Map<String, Object>) input.get("shippingAddress");
            ShippingAddressRequest address = new ShippingAddressRequest();
            address.setFullName((String) addrMap.get("fullName"));
            address.setPhone((String) addrMap.get("phone"));
            address.setAddressLine1((String) addrMap.get("addressLine1"));
            address.setAddressLine2((String) addrMap.get("addressLine2"));
            address.setCity((String) addrMap.get("city"));
            address.setState((String) addrMap.get("state"));
            address.setCountry((String) addrMap.get("country"));
            address.setPostalCode((String) addrMap.get("postalCode"));
            request. setShippingAddress(address);
        }

        return request;
    }

    private UpdateOrderStatusRequest mapToUpdateStatusRequest(Map<String, Object> input) {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        
        if (input.get("status") != null) {
            request.setStatus(OrderStatus.valueOf((String) input.get("status")));
        }
        request.setTrackingNumber((String) input.get("trackingNumber"));
        request.setCancellationReason((String) input.get("cancellationReason"));
        request.setNote((String) input.get("note"));

        return request;
    }
}
