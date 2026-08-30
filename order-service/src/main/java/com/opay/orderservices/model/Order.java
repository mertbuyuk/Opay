package com.opay.orderservices.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    /**
     *event driven icin bu yaklasim önemli
     *bu halde nesne olustugu an id miz var
     *ancak generationtype ile yaparsak o zaman insertte yani save de id ancak olusur
     *bu da eventi yayinlarken cökme yasarsak 1. nesne var eventinde islem yok gibi tutarsizliklar yol acar
     * bu sekilde önce event sonra save ile id li islemi devam ettiririz, save i eventten sonraya
     * almaya ve tutarli gitmeye yarar/
     *
     * isNew metoduna dikkat o her zaman idyi check eder
     */
    @Id
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 30,name = "order_status")
    private OrderStatus orderStatus;

    @Column(name = "total_amount", nullable = false,precision = 19,scale = 2)
    private BigDecimal totalAmount;

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,orphanRemoval = true)
    private ArrayList<OrderItem> orderItems = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at",updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    /**
     *cift tarafli iliski oldugu icin bu helperla foreign keyi doldurmamiz gerek
     */
    public void addItem(OrderItem item){
        orderItems.add(item);
        item.setOrder(this);
    }
}
