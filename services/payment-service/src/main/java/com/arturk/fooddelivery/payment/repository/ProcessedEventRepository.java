package com.arturk.fooddelivery.payment.repository;

import com.arturk.fooddelivery.payment.domain.ProcessedEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEventEntity, UUID> {
}
