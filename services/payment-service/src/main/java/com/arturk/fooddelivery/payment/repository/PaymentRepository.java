package com.arturk.fooddelivery.payment.repository;

import com.arturk.fooddelivery.payment.domain.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<PaymentEntity, UUID> {

    Optional<PaymentEntity> findByOrderId(UUID orderId);
}
