package com.arturk.fooddelivery.catalog.exception.business;

import com.arturk.fooddelivery.catalog.exception.ApplicationException;

public class BusinessCatalogAppException extends ApplicationException {

    public BusinessCatalogAppException(String code, String description, String details) {
        super(code, description, details);
    }
}
