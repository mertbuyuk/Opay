package com.opay.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "stock_reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockReservation {

    //UUID veritabanina sormadan olusmada id saglar bize.
    //lombok builder id null olusturur, Builder.default ise bunu düzeltir bize id saglar
    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(nullable = false, length = 100)
    private String sku;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    @CreationTimestamp
    @Column(name = "created_at",updatable = false)
    private Instant created_at;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
