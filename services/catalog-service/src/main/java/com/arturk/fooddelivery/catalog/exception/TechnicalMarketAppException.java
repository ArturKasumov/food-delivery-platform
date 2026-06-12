package com.arturk.fooddelivery.catalog.exception;

public class TechnicalMarketAppException extends ApplicationException {

    public TechnicalMarketAppException(String code, String description, String details) {
        super(code, description, details);
    }
}
