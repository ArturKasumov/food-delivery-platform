package com.arturk.fooddelivery.payment.exception.technical;

public class SerializationException extends TechnicalPaymentAppException {

    private static final String CODE = "ORDER-MS-05-ERROR";

    public SerializationException() {
        this(null);
    }

    public SerializationException(String details) {
        super(CODE, "Failed to serialize object.", details);
    }
}
