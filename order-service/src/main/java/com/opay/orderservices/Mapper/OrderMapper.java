package com.opay.orderservices.Mapper;

import com.opay.orderservices.dto.OrderItemRequest;
import com.opay.orderservices.dto.OrderItemResponse;
import com.opay.orderservices.dto.OrderResponse;
import com.opay.orderservices.model.Order;
import com.opay.orderservices.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderItem toOrderItem(OrderItemRequest request);

    // "items" maps automatically: MapStruct sees List<OrderItem> -> List<OrderItemResponse>
    // and looks for -- and finds -- toItemResponse(OrderItem) below to map each element.
    OrderResponse toOrderResponse(Order order);

    @Mapping(target = "lineTotal", expression = "java(item.lineTotal())")
    OrderItemResponse toItemResponse(OrderItem item);
}


