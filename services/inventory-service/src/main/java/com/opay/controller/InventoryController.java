package com.opay.controller;

import com.opay.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Tag(name = "inventory", description = "Stock inspection")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/stock/{sku}")
    @Operation(summary = "get current available quantity")
    public StockResponse getStock(@PathVariable String sku){
        return StockResponse.from(inventoryService.getStock(sku));
    }

    @PostMapping("/stock")
    @Operation(
            summary = "Set a SKU's available quantity",
            description = "Upserts -- creates the SKU if it doesn't exist yet, otherwise "
                    + "overwrites its available quantity. Demo/ops convenience, not part of the "
                    + "order-processing flow itself."
    )
    public StockResponse upsertStock(@Valid @RequestBody UpsertStockRequest request) {
        return StockResponse.from(inventoryService.upsertStock(request.getSku(), request.getAvailableQuantity()));
    }
}
