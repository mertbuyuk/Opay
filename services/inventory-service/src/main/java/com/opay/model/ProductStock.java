package com.opay.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "product_stock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductStock {
    //sku dedigi ürünün kodu gibi misal red-tshirt-short
    //kücük ölcekte long id siz bu id ile okey
    @Id
    private String sku;

    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;

    @Column(name = "reserved_quantity",nullable = false)
    private int reservedQuantity;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    public ProductStock(String sku, int availableQuantity) {
        this.sku = sku;
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = 0;
    }
}
