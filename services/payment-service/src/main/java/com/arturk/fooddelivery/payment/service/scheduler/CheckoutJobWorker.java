package com.arturk.fooddelivery.payment.service.scheduler;

import com.arturk.fooddelivery.payment.constants.CorrelationIdConstants;
import com.arturk.fooddelivery.payment.domain.CheckoutJobEntity;
import com.arturk.fooddelivery.payment.dto.psp.CreateCheckoutSessionResponse;
import com.arturk.fooddelivery.payment.exception.technical.PspClientException;
import com.arturk.fooddelivery.payment.service.checkout.CheckoutService;
import com.arturk.fooddelivery.payment.service.client.PspClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(prefix = "app.checkout.worker", name = "enabled", havingValue = "true")
public class CheckoutJobWorker {

    private final CheckoutService checkoutJobService;
    private final PspClient pspClient;

    @Scheduled(fixedDelayString = "${app.checkout.worker.fixed-delay}")
    public void processPendingCheckoutJobs() {
        List<UUID> jobIds = checkoutJobService.claimPendingCheckoutJobIds();

        if (jobIds.isEmpty()) {
            return;
        }

        checkoutJobService.getProcessingCheckoutTasks(jobIds)
                .forEach(this::process);
    }

    private void process(CheckoutJobEntity checkoutJob) {
        try (MDC.MDCCloseable ignored =
                     MDC.putCloseable(CorrelationIdConstants.MDC_KEY, checkoutJob.getCorrelationId())) {
            try {
                CreateCheckoutSessionResponse response = pspClient.createCheckoutSession(checkoutJob);
                checkoutJobService.completeCheckoutSessionCreation(checkoutJob.getId(), response);
            } catch (Exception exception) {
                String failureReason = normalizeMessage(exception);
                boolean retryable = exception instanceof PspClientException pspException
                        && pspException.isRetryable();

                checkoutJobService.handleCheckoutSessionCreationFailure(
                        checkoutJob.getId(),
                        failureReason,
                        retryable
                );
                log.warn("Failed to create PSP checkout for payment: {}, retryable: {}",
                        checkoutJob.getPaymentId(), retryable, exception);
            }
        }
    }

    private String normalizeMessage(Exception exception) {
        return exception.getMessage() == null || exception.getMessage().isBlank()
                ? exception.getClass().getSimpleName()
                : exception.getMessage();
    }
}
