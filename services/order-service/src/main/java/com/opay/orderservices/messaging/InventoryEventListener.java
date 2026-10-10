package com.opay.orderservices.messaging;

import com.opay.events.inventory.InventoryOutOfStockEvent;
import com.opay.events.topic.KafkaTopics;
import com.opay.orderservices.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventListener {

    private final OrderService orderService;

    @KafkaListener(topics = KafkaTopics.INVENTORY_OUT_OF_STOCK)
    public void inventoryOutOfStock(InventoryOutOfStockEvent event){
        log.info("Received InventoryOutOfStock for order {} (sku={})", event.getOrderId(), event.getSku());
        orderService.markInventoryOutOfStock(event.getOrderId());
    }
}
