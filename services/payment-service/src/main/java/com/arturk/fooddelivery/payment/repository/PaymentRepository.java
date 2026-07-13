package com.arturk.fooddelivery.payment.repository;

import com.arturk.fooddelivery.payment.domain.PaymentEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<PaymentEntity, UUID> {

    Optional<PaymentEntity> findByOrderId(UUID orderId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select payment from PaymentEntity payment where payment.id = :paymentId")
    Optional<PaymentEntity> findByIdForUpdate(@Param("paymentId") UUID paymentId);
}
