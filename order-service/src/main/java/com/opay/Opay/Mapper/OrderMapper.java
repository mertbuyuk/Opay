package com.opay.Opay.Mapper;

import com.opay.Opay.dto.OrderItemRequest;
import com.opay.Opay.dto.OrderItemResponse;
import com.opay.Opay.dto.OrderResponse;
import com.opay.Opay.model.Order;
import com.opay.Opay.model.OrderItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

