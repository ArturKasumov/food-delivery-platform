package com.arturk.fooddelivery.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "processed_events")
@NoArgsConstructor
public class ProcessedEventEntity {

    @Id
    @Column(name = "event_id")
    private UUID eventId;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;

    public ProcessedEventEntity(UUID eventId, String eventType) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.processedAt = LocalDateTime.now();
    }
}
