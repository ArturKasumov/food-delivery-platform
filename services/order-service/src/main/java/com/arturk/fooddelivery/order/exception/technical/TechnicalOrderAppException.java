package com.arturk.fooddelivery.order.exception.technical;

import com.arturk.fooddelivery.order.exception.ApplicationException;

public class TechnicalOrderAppException extends ApplicationException {

    public TechnicalOrderAppException(String code, String description, String details) {
        super(code, description, details);
    }
}
