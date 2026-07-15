package com.arturk.fooddelivery.payment.repository;

import com.arturk.fooddelivery.payment.domain.OutboxEventEntity;
import com.arturk.fooddelivery.payment.enums.OutboxEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, UUID> {

    @Query(value = """
            SELECT *
            FROM outbox_events
            WHERE (
                status IN ('PENDING', 'FAILED')
                OR (status = 'PROCESSING' AND updated_at < :processingBefore)
            )
            AND next_attempt_at <= CURRENT_TIMESTAMP
            AND retry_attempt < :maxRetryAttempts
            ORDER BY created_at
            LIMIT :batchSize
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<OutboxEventEntity> findPublishableEventsForUpdate(
            @Param("batchSize") int batchSize,
            @Param("maxRetryAttempts") int maxRetryAttempts,
            @Param("processingBefore") LocalDateTime processingBefore
    );

    List<OutboxEventEntity> findAllByIdInAndStatus(Collection<UUID> ids, OutboxEventStatus status);
}
