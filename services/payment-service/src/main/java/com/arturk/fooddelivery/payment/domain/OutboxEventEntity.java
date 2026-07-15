package com.arturk.fooddelivery.payment.domain;

import com.arturk.fooddelivery.payment.enums.OutboxEventStatus;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "outbox_events")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class OutboxEventEntity {

    @Id
    private UUID id;

    @Column(name = "aggregate_type", nullable = false, length = 30)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(name = "correlation_id", nullable = false, length = 64)
    private String correlationId;

    @Column(name = "topic", nullable = false, length = 50)
    private String topic;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 15)
    private OutboxEventStatus status;

    @Column(name = "retry_attempt", nullable = false)
    private int retryAttempt = 0;

    @Column(name = "error", columnDefinition = "text")
    private String error;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "next_attempt_at")
    private LocalDateTime nextAttemptAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public OutboxEventEntity(UUID id,
                             String aggregateType,
                             UUID aggregateId,
                             String eventType,
                             String correlationId,
                             String topic,
                             String payload) {
        this.id = id;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.correlationId = correlationId;
        this.topic = topic;
        this.payload = payload;
        this.status = OutboxEventStatus.PENDING;
        this.retryAttempt = 0;
        this.nextAttemptAt = LocalDateTime.now();
    }

    public void markProcessing() {
        this.status = OutboxEventStatus.PROCESSING;
    }

    public void markPublished() {
        this.status = OutboxEventStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
        this.nextAttemptAt = null;
        this.error = null;
    }

    public void markFailed(String error, LocalDateTime nextAttemptAt) {
        this.status = OutboxEventStatus.FAILED;
        this.retryAttempt++;
        this.error = error;
        this.nextAttemptAt = nextAttemptAt;
    }

    public void markDead(String error) {
        this.status = OutboxEventStatus.DEAD;
        this.retryAttempt++;
        this.error = error;
        this.nextAttemptAt = null;
    }

    public void reprocess() {
        if (status != OutboxEventStatus.DEAD) {
            throw new IllegalStateException("Only DEAD outbox events can be reprocessed");
        }

        this.status = OutboxEventStatus.PENDING;
        this.retryAttempt = 0;
        this.nextAttemptAt = LocalDateTime.now();
    }
}
