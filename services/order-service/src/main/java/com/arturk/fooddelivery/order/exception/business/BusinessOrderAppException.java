package com.arturk.fooddelivery.order.exception.business;

import com.arturk.fooddelivery.order.exception.ApplicationException;

public class BusinessOrderAppException extends ApplicationException {

    public BusinessOrderAppException(String code, String description, String details) {
        super(code, description, details);
    }
}
