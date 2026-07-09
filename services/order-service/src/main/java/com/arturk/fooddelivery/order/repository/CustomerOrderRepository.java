package com.arturk.fooddelivery.order.repository;

import com.arturk.fooddelivery.order.domain.CustomerOrderEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrderEntity, UUID> {

    @EntityGraph(attributePaths = "items")
    List<CustomerOrderEntity> findAllByCustomerIdOrderByCreatedAtDesc(UUID customerId, Pageable pageable);

    @EntityGraph(attributePaths = "items")
    Optional<CustomerOrderEntity> findOrderWithItemsById(UUID id);
}
