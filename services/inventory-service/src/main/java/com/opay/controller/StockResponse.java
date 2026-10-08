package com.opay.controller;

import com.opay.model.ProductStock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class StockResponse{
    private String sku;
    private int availableQuantity;
    private int reservedQuantity;
    private Instant updatedAt;

    public static StockResponse from(ProductStock productStock){
        return StockResponse.builder()
                .sku(productStock.getSku())
                .availableQuantity(productStock.getAvailableQuantity())
                .reservedQuantity(productStock.getReservedQuantity())
                .updatedAt(productStock.getUpdatedAt())
                .build();
    }
}
