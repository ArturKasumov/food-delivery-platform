package com.arturk.fooddelivery.order.exception;

import lombok.Getter;

@Getter
public abstract class ApplicationException extends RuntimeException {
    private final String code;
    private final String description;
    private final String details;

    protected ApplicationException(String code, String description, String details) {
        super(description);
        this.code = code;
        this.description = description;
        this.details = details;
    }

}
