package com.arturk.fooddelivery.payment.enums;

import com.arturk.fooddelivery.payment.constants.PaymentEventTypes;
import lombok.Getter;

@Getter
public enum PaymentStatus {

    PENDING(null),
    AWAITING_CUSTOMER(null),
    COMPLETED(PaymentEventTypes.PAYMENT_COMPLETED_EVENT_TYPE),
    FAILED(PaymentEventTypes.PAYMENT_FAILED_EVENT_TYPE);

    private final String resultEventType;

    PaymentStatus(String resultEventType) {
        this.resultEventType = resultEventType;
    }
}
