package com.arturk.fooddelivery.catalog.exception.technical;

import com.arturk.fooddelivery.catalog.exception.ApplicationException;

public class TechnicalCatalogAppException extends ApplicationException {

    public TechnicalCatalogAppException(String code, String description, String details) {
        super(code, description, details);
    }
}
