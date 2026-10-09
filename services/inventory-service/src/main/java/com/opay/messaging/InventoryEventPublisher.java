package com.opay.messaging;

import com.opay.events.inventory.InventoryOutOfStockEvent;
import com.opay.events.inventory.InventoryReservedEvent;
import com.opay.events.topic.KafkaTopics;
import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventPublisher {

    private final KafkaTemplate<String,Object> kafkaTemplate;

    public void publishInventoryReserved(UUID orderId){
        InventoryReservedEvent inventoryReservedEvent = new InventoryReservedEvent(UUID.randomUUID(),orderId, Instant.now());
        log.info("Publishing InventoryReserved for order {} ", orderId);
        kafkaTemplate.send(KafkaTopics.INVENTORY_RESERVED,orderId.toString(),inventoryReservedEvent);
    }
    public void publishInventoryOutOfStock(UUID orderId, String sku){
        InventoryOutOfStockEvent inventoryOutOfStockEvent = new InventoryOutOfStockEvent(UUID.randomUUID(),orderId,sku,Instant.now());
        log.info("Publishing InventoryOutOfStock for order {} (sku={})",orderId,sku);
        kafkaTemplate.send(KafkaTopics.INVENTORY_OUT_OF_STOCK,orderId.toString(),inventoryOutOfStockEvent);
    }
}
