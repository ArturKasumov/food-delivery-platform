package com.arturk.fooddelivery.payment.enums;

public enum OutboxEventStatus {
    PENDING,
    PROCESSING,
    PUBLISHED,
    FAILED,
    DEAD
}
