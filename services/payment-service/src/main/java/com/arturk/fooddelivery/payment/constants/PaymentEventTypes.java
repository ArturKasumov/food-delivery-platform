package com.arturk.fooddelivery.payment.constants;

public final class PaymentEventTypes {

    public static final String PAYMENT_AGGREGATE_TYPE = "Payment";
    public static final String PAYMENT_COMPLETED_EVENT_TYPE = "PaymentCompletedEvent";
    public static final String PAYMENT_FAILED_EVENT_TYPE = "PaymentFailedEvent";

    private PaymentEventTypes() {
    }
}
