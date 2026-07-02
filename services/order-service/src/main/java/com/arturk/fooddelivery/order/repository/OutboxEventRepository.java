package com.arturk.fooddelivery.order.repository;

import com.arturk.fooddelivery.order.domain.OutboxEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, UUID> {
}
