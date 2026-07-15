package com.arturk.fooddelivery.order.enums;

public enum OutboxEventStatus {
    PENDING,
    PROCESSING,
    PUBLISHED,
    FAILED,
    DEAD
}
