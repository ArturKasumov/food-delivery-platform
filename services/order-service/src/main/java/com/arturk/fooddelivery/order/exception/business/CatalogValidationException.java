package com.arturk.fooddelivery.order.exception.business;

public class CatalogValidationException extends BusinessOrderAppException {

    private static final String CODE = "ORDER-MS-03-ERROR";

    public CatalogValidationException() {
        this(null);
    }

    public CatalogValidationException(String details) {
        super(CODE, "Catalog validation failed.", details);
    }
}
