package com.arturk.fooddelivery.payment.exception.technical;

import lombok.Getter;

@Getter
public class PspClientException extends TechnicalPaymentAppException {

    private final boolean retryable;

    private static final String CODE = "PAYMENT-MS-04-ERROR";

    public PspClientException(boolean retryable) {
        this(null, retryable);
    }

    public PspClientException(String details, boolean retryable) {
        super(CODE, "Error during calling PSP client.", details);
        this.retryable = retryable;
    }
}
