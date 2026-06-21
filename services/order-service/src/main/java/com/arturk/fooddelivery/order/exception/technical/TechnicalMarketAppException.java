package com.arturk.fooddelivery.order.exception.technical;

import com.arturk.fooddelivery.order.exception.ApplicationException;

public class TechnicalMarketAppException extends ApplicationException {

    public TechnicalMarketAppException(String code, String description, String details) {
        super(code, description, details);
    }
}
