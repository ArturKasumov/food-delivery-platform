package com.arturk.fooddelivery.order.exception.business;

public class OrderNotFoundException extends BusinessOrderAppException {

    private static final String CODE = "ORDER-MS-02-ERROR";

    public OrderNotFoundException() {
        this(null);
    }

    public OrderNotFoundException(String details) {
        super(CODE, "Order not found.", details);
    }
}
