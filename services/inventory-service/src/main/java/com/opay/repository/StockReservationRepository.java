package com.opay.repository;

import com.opay.model.ReservationStatus;
import com.opay.model.StockReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StockReservationRepository extends JpaRepository<StockReservation, UUID>
{
    boolean existsByOrderId(UUID orderId);

    List<StockReservation> findByOrderIdAndStatus(UUID id, ReservationStatus reservationStatus);
}
