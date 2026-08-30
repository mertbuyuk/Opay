package com.opay.orderservices.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @Builder.Default
    private UUID uuid = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order")
    @JsonBackReference
    private Order order;

    @Column(nullable = false,length = 100)
    private String sku;

    @Column(nullable = false)
    private Integer quantity;

    /**
     * para hesaplarinda veya kritik matematiksel islemlerde bigdecimal
     * cünkü float veya double ondalik sayilari tam olarak temsil edemez
     * kücük sapmalar birikerek büyük hatalar olusturur
     */
    @Column(name = "unit_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;

    public BigDecimal lineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
