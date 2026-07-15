package com.arturk.fooddelivery.payment.repository;

import com.arturk.fooddelivery.payment.domain.CheckoutJobEntity;
import com.arturk.fooddelivery.payment.enums.CheckoutJobStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CheckoutJobRepository extends JpaRepository<CheckoutJobEntity, UUID> {

    @Query(value = """
            SELECT *
            FROM checkout_jobs
            WHERE (
                status = 'PENDING'
                OR (status = 'PROCESSING' AND updated_at < :processingBefore)
            )
            AND next_attempt_at <= CURRENT_TIMESTAMP
            AND retry_attempt < :maxRetryAttempts
            ORDER BY created_at
            LIMIT :batchSize
            FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<CheckoutJobEntity> findPendingJobsForUpdate(
            @Param("batchSize") int batchSize,
            @Param("maxRetryAttempts") int maxRetryAttempts,
            @Param("processingBefore") LocalDateTime processingBefore
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select job from CheckoutJobEntity job where job.id = :jobId")
    Optional<CheckoutJobEntity> findByIdForUpdate(@Param("jobId") UUID jobId);

    List<CheckoutJobEntity> findAllByIdInAndStatus(Collection<UUID> ids, CheckoutJobStatus status
    );
}
