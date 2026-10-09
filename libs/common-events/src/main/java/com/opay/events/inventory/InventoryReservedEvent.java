package com.opay.events.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReservedEvent {
    private UUID eventId;
    private UUID orderId;
    private Instant occuredAt;
}
