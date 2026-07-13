package com.arturk.fooddelivery.payment.domain;

import com.arturk.fooddelivery.payment.enums.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "payments")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class PaymentEntity {

    @Id
    private UUID id;

    @Column(name = "order_id", nullable = false, unique = true)
    private UUID orderId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus status;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "provider_session_id", unique = true)
    private UUID providerSessionId;

    @Column(name = "checkout_url", length = 1000)
    private String checkoutUrl;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public PaymentEntity(UUID orderId, UUID customerId, BigDecimal amount) {
        this.id = UUID.randomUUID();
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
    }

    public void applyPaymentCompleted() {
        this.status = PaymentStatus.COMPLETED;
        this.failureReason = null;
    }

    public void applyPaymentFailed(String failureReason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = failureReason;
    }

    public void applyCheckoutCreated(UUID providerSessionId, String checkoutUrl) {
        this.providerSessionId = providerSessionId;
        this.checkoutUrl = checkoutUrl;
        this.status = PaymentStatus.AWAITING_CUSTOMER;
        this.failureReason = null;
    }

    public boolean isFinalStatus() {
        return EnumSet.of(PaymentStatus.COMPLETED, PaymentStatus.FAILED).contains(status);
    }
}
