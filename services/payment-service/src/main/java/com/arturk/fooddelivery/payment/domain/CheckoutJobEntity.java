package com.arturk.fooddelivery.payment.domain;

import com.arturk.fooddelivery.payment.enums.CheckoutJobStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "checkout_jobs")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class CheckoutJobEntity {

    @Id
    private UUID id;

    @Column(name = "payment_id", nullable = false, unique = true)
    private UUID paymentId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CheckoutJobStatus status;

    @Column(name = "correlation_id", nullable = false, length = 64)
    private String correlationId;

    @Column(name = "retry_attempt", nullable = false)
    private int retryAttempt;

    @Column(name = "next_attempt_at")
    private LocalDateTime nextAttemptAt;

    @Column(name = "last_error", columnDefinition = "text")
    private String lastError;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public CheckoutJobEntity(UUID paymentId,
                             UUID orderId,
                             BigDecimal amount,
                             String correlationId) {
        this.id = UUID.randomUUID();
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.correlationId = correlationId;
        this.status = CheckoutJobStatus.PENDING;
        this.retryAttempt = 0;
        this.nextAttemptAt = LocalDateTime.now();
    }

    public void markProcessing() {
        this.status = CheckoutJobStatus.PROCESSING;
    }

    public void markCompleted() {
        this.status = CheckoutJobStatus.COMPLETED;
        this.nextAttemptAt = null;
        this.lastError = null;
    }

    public void applyRetryableFailure(String lastError, LocalDateTime nextAttemptAt) {
        this.status = CheckoutJobStatus.PENDING;
        this.retryAttempt++;
        this.lastError = lastError;
        this.nextAttemptAt = nextAttemptAt;
    }

    public void markFailed(String lastError) {
        this.status = CheckoutJobStatus.FAILED;
        this.retryAttempt++;
        this.lastError = lastError;
        this.nextAttemptAt = null;
    }
}
