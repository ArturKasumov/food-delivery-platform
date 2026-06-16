package com.arturk.fooddelivery.order.exception;

public class BusinessMarketAppException extends ApplicationException {

    public BusinessMarketAppException(String code, String description, String details) {
        super(code, description, details);
    }
}
