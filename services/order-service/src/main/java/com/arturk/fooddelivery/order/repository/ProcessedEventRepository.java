package com.arturk.fooddelivery.order.repository;

import com.arturk.fooddelivery.order.domain.ProcessedEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEventEntity, UUID> {
}
