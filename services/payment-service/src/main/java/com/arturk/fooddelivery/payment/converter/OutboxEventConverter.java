package com.arturk.fooddelivery.payment.converter;

import com.arturk.fooddelivery.payment.domain.PaymentEntity;
import com.arturk.fooddelivery.payment.dto.PaymentCompletedEventPayload;
import com.arturk.fooddelivery.payment.dto.PaymentFailedEventPayload;
import org.springframework.stereotype.Component;

@Component
public class OutboxEventConverter {

    public PaymentCompletedEventPayload toPaymentCompletedEventPayload(PaymentEntity payment) {
        return new PaymentCompletedEventPayload(
                payment.getId(),
                payment.getOrderId(),
                payment.getCustomerId(),
                payment.getAmount(),
                payment.getStatus()
        );
    }

    public PaymentFailedEventPayload toPaymentFailedEventPayload(PaymentEntity payment) {
        return new PaymentFailedEventPayload(
                payment.getId(),
                payment.getOrderId(),
                payment.getCustomerId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getFailureReason()
        );
    }
}
