package com.opay.orderservices.controller;

import com.opay.orderservices.dto.CreateOrderRequest;
import com.opay.orderservices.dto.OrderResponse;
import com.opay.orderservices.dto.PageResponse;
import com.opay.orderservices.service.OrderService;
import io.swagger.v3.oas.annotations.OpenAPI31;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order lifecycle management")
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
    @Operation(
            summary = "create an order",
            description = "Total is always compued server side from the submitted lines"
    )
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest orderRequest){
        OrderResponse orderResponse = orderService.createOrder(orderRequest);
        return ResponseEntity.created(URI.create("/orders/"+orderResponse.getId())).body(orderResponse);
        //return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
    }

    /** Kimlikte pathvariable*/
    @GetMapping("/{orderId}")
    @Operation(summary = "Get an order by id")
    public OrderResponse getOrder(@PathVariable UUID orderId){
        return orderService.getOrder(orderId);
    }

    /**filtre icin request param*/
    @GetMapping
    @Operation(
            summary = "List orders",
            description = "Filtered by merchantId. Always paginated --"
            + "default page size 20, newest first"
    )
    PageResponse<OrderResponse> getOrdersList(
            @RequestParam(required = false) UUID merchantId,
            @PageableDefault(size =20, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable){
        return orderService.listOrders(merchantId, pageable);
    }
}
