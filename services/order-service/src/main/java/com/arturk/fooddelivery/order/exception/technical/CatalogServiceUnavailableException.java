package com.arturk.fooddelivery.order.exception.technical;

public class CatalogServiceUnavailableException extends TechnicalOrderAppException {

    private static final String CODE = "ORDER-MS-04-ERROR";

    public CatalogServiceUnavailableException(String details) {
        super(CODE, "Catalog service is unavailable.", details);
    }
}
