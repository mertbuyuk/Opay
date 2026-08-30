package com.opay.orderservices.service;

import com.opay.orderservices.Mapper.OrderMapper;
import com.opay.orderservices.dto.CreateOrderRequest;
import com.opay.orderservices.dto.OrderItemRequest;
import com.opay.orderservices.dto.OrderResponse;
import com.opay.orderservices.exception.OrderNotFoundException;
import com.opay.orderservices.model.Order;
import com.opay.orderservices.model.OrderItem;
import com.opay.orderservices.model.OrderStatus;
import com.opay.orderservices.repository.OrderRepository;
import org.springframework.transaction.annotation.Transactional;import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest orderRequest){
        Order order = Order.builder()
                .merchantId(orderRequest.getMerchantId())
                .orderStatus(OrderStatus.CREATED)
                .totalAmount(BigDecimal.ZERO)
                .build();

        for (OrderItemRequest itemRequest : orderRequest.getItems()){
            OrderItem orderItem = OrderMapper.toOrderItem(itemRequest);
            order.addItem(orderItem);
        }

        order.setTotalAmount(calculateTotalAmount(order.getOrderItems()));

        Order saved = orderRepository.save(order);
        return OrderMapper.toOrderResponse(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(UUID orderId){
        Order order =  orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        return OrderMapper.toOrderResponse(order);
    }
    /** transactional gerekli cünkü lazy cekiyoruz
     * tek session acilmali ve isteyince childlara ulasmaliyiz
     * transactional olmasa exception alabilirdik
     * spring.jpa.open-in-view
     *
     * buraya ileride paginnation gereklli
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> listOrders(UUID merchantId){
        List<Order> orders = merchantId != null
                ? orderRepository.findByMerchantId(merchantId)
                : orderRepository.findAll();

        return orders.stream()
                .map(OrderMapper::toOrderResponse).toList();
    }


    public BigDecimal calculateTotalAmount(List<OrderItem> orderItemList){
        return orderItemList.stream().map(OrderItem::lineTotal)
                .reduce(BigDecimal.ZERO,BigDecimal::add);
    }
}
