package com.arturk.fooddelivery.order.exception.technical;

public class CatalogServiceUnavailableException extends TechnicalMarketAppException {

    private static final String CODE = "ORDER-MS-04-ERROR";

    public CatalogServiceUnavailableException(String details) {
        super(CODE, "Catalog service is unavailable.", details);
    }
}
