package com.arturk.fooddelivery.payment.dto;

public record PaymentAuthorizationResult(
        boolean isSuccessful,
        String failureReason
) {

    public static PaymentAuthorizationResult succeed() {
        return new PaymentAuthorizationResult(true, null);
    }

    public static PaymentAuthorizationResult failed(String failureReason) {
        return new PaymentAuthorizationResult(false, failureReason);
    }
}
