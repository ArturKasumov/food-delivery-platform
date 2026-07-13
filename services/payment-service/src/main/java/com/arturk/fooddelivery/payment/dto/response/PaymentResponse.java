package com.arturk.fooddelivery.payment.dto.response;

import com.arturk.fooddelivery.payment.domain.PaymentEntity;
import com.arturk.fooddelivery.payment.enums.PaymentStatus;

import java.util.UUID;

public record PaymentResponse(
        UUID paymentId,
        UUID orderId,
        PaymentStatus status,
        String checkoutUrl,
        String failureReason
) {
    public static PaymentResponse from(PaymentEntity payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getStatus(),
                payment.getCheckoutUrl(),
                payment.getFailureReason()
        );
    }
}
