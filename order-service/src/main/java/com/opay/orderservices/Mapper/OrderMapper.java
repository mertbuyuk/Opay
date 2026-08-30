package com.opay.orderservices.Mapper;

import com.opay.orderservices.dto.OrderItemRequest;
import com.opay.orderservices.dto.OrderItemResponse;
import com.opay.orderservices.dto.OrderResponse;
import com.opay.orderservices.model.Order;
import com.opay.orderservices.model.OrderItem;

import java.util.List;

public class OrderMapper {

    public OrderMapper() {

    }

    public static OrderItem toOrderItem(OrderItemRequest orderItemRequest) {
        return OrderItem.builder()
                .sku(orderItemRequest.getSku())
                .quantity(orderItemRequest.getQuantity())
                .unitPrice(orderItemRequest.getUnitPrice())
                .build();

    }

    public static OrderResponse toOrderResponse(Order order) {
        List<OrderItemResponse> orderItemResponses = order.getOrderItems()
                .stream().map(OrderMapper::toItemResponse).toList();

        return OrderResponse.builder()
                .id(order.getId())
                .merchantId(order.getMerchantId())
                .items(orderItemResponses)
                .createdAt(order.getCreatedAt())
                .totalAmount(order.getTotalAmount())
                .status(order.getOrderStatus())
                .build();
    }

    private static OrderItemResponse toItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .sku(item.getSku())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .lineTotal(item.lineTotal())
                .build();
    }

}

