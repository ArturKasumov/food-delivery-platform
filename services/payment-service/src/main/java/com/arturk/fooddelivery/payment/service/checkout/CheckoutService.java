package com.arturk.fooddelivery.payment.service.checkout;

import com.arturk.fooddelivery.payment.config.properties.CheckoutWorkerProperties;
import com.arturk.fooddelivery.payment.domain.CheckoutJobEntity;
import com.arturk.fooddelivery.payment.domain.PaymentEntity;
import com.arturk.fooddelivery.payment.dto.psp.CreateCheckoutSessionResponse;
import com.arturk.fooddelivery.payment.enums.CheckoutJobStatus;
import com.arturk.fooddelivery.payment.exception.business.CheckoutJobNotFoundException;
import com.arturk.fooddelivery.payment.exception.business.PaymentNotFoundException;
import com.arturk.fooddelivery.payment.repository.CheckoutJobRepository;
import com.arturk.fooddelivery.payment.repository.PaymentRepository;
import com.arturk.fooddelivery.payment.service.outbox.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutService {

    private final CheckoutJobRepository checkoutJobRepository;
    private final PaymentRepository paymentRepository;
    private final CheckoutWorkerProperties checkoutWorkerProperties;
    private final OutboxService outboxService;


    @Transactional
    public List<UUID> claimPendingCheckoutJobIds() {
        LocalDateTime processingBefore = LocalDateTime.now().minus(checkoutWorkerProperties.processingTimeout());
        List<CheckoutJobEntity> jobs =
                checkoutJobRepository.findPendingJobsForUpdate(
                        checkoutWorkerProperties.batchSize(),
                        checkoutWorkerProperties.maxRetryAttempts(),
                        processingBefore
                );

        jobs.forEach(CheckoutJobEntity::markProcessing);

        return jobs.stream()
                .map(CheckoutJobEntity::getId)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CheckoutJobEntity> getProcessingCheckoutTasks(List<UUID> jobIds) {
        return checkoutJobRepository
                .findAllByIdInAndStatus(jobIds, CheckoutJobStatus.PROCESSING);
    }

    @Transactional
    public void completeCheckoutSessionCreation(UUID jobId, CreateCheckoutSessionResponse response) {
        CheckoutJobEntity checkoutJob = getJobForUpdate(jobId);
        if (checkoutJob.getStatus() != CheckoutJobStatus.PROCESSING) {
            log.warn("Ignoring late checkout success for job: {} with status: {}", jobId, checkoutJob.getStatus());
            return;
        }

        PaymentEntity payment = getPaymentForUpdate(checkoutJob.getPaymentId());
        if (payment.isFinalStatus()) {
            checkoutJob.markFailed("Payment is already: " + payment.getStatus());
            log.warn("Ignoring checkout success because payment: {} is already: {}",
                    payment.getId(), payment.getStatus());
            return;
        }

        payment.applyCheckoutCreated(response.sessionId(), response.checkoutUrl());
        checkoutJob.markCompleted();

        log.info("Created PSP checkout session: {} for payment: {}",
                response.sessionId(), checkoutJob.getPaymentId());
    }

    @Transactional
    public void handleCheckoutSessionCreationFailure(UUID jobId,
                                                     String failureReason,
                                                     boolean retryable) {
        CheckoutJobEntity job = getJobForUpdate(jobId);
        if (job.getStatus() != CheckoutJobStatus.PROCESSING) {
            log.warn("Ignoring late checkout failure for job: {} with status: {}", jobId, job.getStatus());
            return;
        }

        PaymentEntity payment = getPaymentForUpdate(job.getPaymentId());

        if (payment.isFinalStatus()) {
            job.markFailed("Payment is already: " + payment.getStatus());
            log.warn("Ignoring checkout failure because payment: {} is already: {}",
                    payment.getId(), payment.getStatus());
            return;
        }

        int failedAttempt = job.getRetryAttempt() + 1;
        if (retryable && failedAttempt < checkoutWorkerProperties.maxRetryAttempts()) {
            LocalDateTime nextAttemptAt = LocalDateTime.now().plus(calculateRetryDelay(failedAttempt));
            job.applyRetryableFailure(failureReason, nextAttemptAt);
            log.warn("Scheduled PSP checkout retry for job: {}, payment: {}, attempt: {}, nextAttemptAt: {}",
                    job.getId(), job.getPaymentId(), failedAttempt, nextAttemptAt);
            return;
        }

        job.markFailed(failureReason);
        payment.applyPaymentFailed("CHECKOUT_CREATION_FAILED: " + failureReason);
        outboxService.savePaymentResultEvent(payment);
    }

    private CheckoutJobEntity getJobForUpdate(UUID jobId) {
        return checkoutJobRepository.findByIdForUpdate(jobId)
                .orElseThrow(CheckoutJobNotFoundException::new);
    }

    private PaymentEntity getPaymentForUpdate(UUID paymentId) {
        return paymentRepository.findByIdForUpdate(paymentId)
                .orElseThrow(PaymentNotFoundException::new);
    }

    private Duration calculateRetryDelay(int failedAttempt) {
        Duration delay = checkoutWorkerProperties.retryInitialDelay();
        for (int attempt = 1; attempt < failedAttempt; attempt++) {
            delay = delay.multipliedBy(2);
        }
        return delay;
    }
}
