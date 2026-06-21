package com.arturk.fooddelivery.catalog.exception.business;

public class RestaurantNotFoundException extends BusinessCatalogAppException {

    private static final String CODE = "CATALOG-MS-02-ERROR";

    public RestaurantNotFoundException() {
        this(null);
    }

    public RestaurantNotFoundException(String details) {
        super(CODE, "Restaurant not found", details);
    }
}
