package com.arturk.fooddelivery.payment.service.callback;

import com.arturk.fooddelivery.payment.domain.PaymentEntity;
import com.arturk.fooddelivery.payment.dto.psp.PspPaymentCallbackRequest;
import com.arturk.fooddelivery.payment.enums.PspCallbackStatus;
import com.arturk.fooddelivery.payment.exception.business.PaymentNotFoundException;
import com.arturk.fooddelivery.payment.repository.PaymentRepository;
import com.arturk.fooddelivery.payment.service.outbox.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentCallbackHandler {

    private final PaymentRepository paymentRepository;
    private final OutboxService outboxService;

    @Transactional
    public void applyPspCallback(PspPaymentCallbackRequest callback) {
        PaymentEntity payment = paymentRepository
                .findByIdForUpdate(callback.paymentId())
                .orElseThrow(PaymentNotFoundException::new);

        if (payment.isFinalStatus()) {
            log.warn(
                    "Ignoring PSP callback for session: {} because payment {} is already {}",
                    callback.sessionId(),
                    payment.getId(),
                    payment.getStatus()
            );
            return;
        }

        switch (callback.status()) {
            case PspCallbackStatus.PAID:
                payment.applyPaymentCompleted();
                break;
            case PspCallbackStatus.CANCELLED:
                payment.applyPaymentFailed("CUSTOMER_CANCELLED");
                break;
            default:
                throw new UnsupportedOperationException("Unsupported callback status: " + callback.status());
        }

        outboxService.savePaymentResultEvent(payment);
        log.info("Callback applied to payment: {} updated to: {}", payment.getId(), payment.getStatus());
    }
}