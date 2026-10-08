package com.opay.repository;

import com.opay.model.ProductStock;
import com.opay.model.StockReservation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductStockRepository extends JpaRepository<ProductStock,String > {

    Optional<ProductStock> findBySku(String sku);


    /**
     * Burda lock kilitliyor ve sirada ki sorguyu milisaniye bekletiyor
     * böylelikle stok 1 ve 2 istek geldi diyelim
     * ilkini isleyip stogu 0 yapiyoruz sonrasinda akis devam
     * ikinci istek stok yok görüp devam ediyor
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p from ProductStock p where p.sku =:sku")
    Optional<ProductStock> findBySkuForUpdate(@Param("sku") String sku);
}