package com.arturk.fooddelivery.payment.exception.business;

public class CheckoutJobNotFoundException extends BusinessPaymentAppException {

    private static final String CODE = "PAYMENT-MS-05-ERROR";

    public CheckoutJobNotFoundException() {
        this(null);
    }

    public CheckoutJobNotFoundException(String details) {
        super(CODE, "CheckoutJob not found.", details);
    }
}
