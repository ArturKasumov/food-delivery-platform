package com.arturk.fooddelivery.payment.exception.technical;

public class SerializationException extends TechnicalPaymentAppException {

    private static final String CODE = "PAYMENT-MS-02-ERROR";

    public SerializationException() {
        this(null);
    }

    public SerializationException(String details) {
        super(CODE, "Failed to serialize object.", details);
    }
}
