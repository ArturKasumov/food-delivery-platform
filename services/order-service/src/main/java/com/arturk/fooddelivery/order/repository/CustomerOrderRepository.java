package com.arturk.fooddelivery.order.repository;

import com.arturk.fooddelivery.order.domain.CustomerOrderEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.UUID;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrderEntity, UUID> {

    List<CustomerOrderEntity> findAllByCustomerIdOrderByCreatedAtDesc(UUID customerId, Pageable pageable);
}
