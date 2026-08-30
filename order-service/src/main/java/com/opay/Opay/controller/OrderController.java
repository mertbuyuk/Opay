package com.opay.Opay.controller;

import com.opay.Opay.dto.CreateOrderRequest;
import com.opay.Opay.dto.OrderResponse;
import com.opay.Opay.service.OrderService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    /**
     * HTTP/1.1 201 Created
     * Location: /orders/550e8400-e29b-41d4-a716-446655440000 -> created bunu ekler
     * Content-Type: application/json
     *
     * { "id": "550e8400-...", "merchantId": "...", ... }
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest orderRequest){
        OrderResponse orderResponse = orderService.createOrder(orderRequest);
        return ResponseEntity.created(URI.create("/orders/"+orderResponse.getId())).body(orderResponse);
        //return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
    }

    /** Kimlikte pathvariable*/
    @GetMapping("/{orderId}")
    public OrderResponse getOrder(@PathVariable UUID orderId){
        return orderService.getOrder(orderId);
    }

    /**filtre icin request param*/
    @GetMapping
    List<OrderResponse> getOrdersList(@RequestParam(required = false) UUID merchantId){
        return orderService.listOrders(merchantId);
    }
}
