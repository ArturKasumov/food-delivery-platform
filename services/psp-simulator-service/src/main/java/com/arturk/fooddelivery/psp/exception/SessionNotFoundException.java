package com.arturk.fooddelivery.psp.exception;

public class SessionNotFoundException extends RuntimeException {

    public SessionNotFoundException() {
        super("Checkout session not found");
    }
}
