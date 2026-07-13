package com.arturk.fooddelivery.payment.exception.business;

public class PaymentNotFoundException extends BusinessPaymentAppException {

    private static final String CODE = "PAYMENT-MS-03-ERROR";

    public PaymentNotFoundException() {
        this(null);
    }

    public PaymentNotFoundException(String details) {
        super(CODE, "Payment not found.", details);
    }
}
