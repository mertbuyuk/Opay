package com.opay.Opay.repository;

import com.opay.Opay.model.Order;
import com.opay.Opay.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findByMerchantId(UUID merchantId);
}
