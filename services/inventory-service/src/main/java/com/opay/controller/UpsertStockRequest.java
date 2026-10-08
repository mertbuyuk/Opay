package com.opay.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpsertStockRequest {

    @NotBlank(message = "sku is required")
    private String sku;

    @Min(value = 0, message = "availableQuantity cannot be negative")
    private int availableQuantity;
}
