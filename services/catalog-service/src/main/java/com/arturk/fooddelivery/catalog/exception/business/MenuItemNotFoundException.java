package com.arturk.fooddelivery.catalog.exception.business;

public class MenuItemNotFoundException extends BusinessCatalogAppException {

    private static final String CODE = "CATALOG-MS-03-ERROR";

    public MenuItemNotFoundException() {
        this(null);
    }

    public MenuItemNotFoundException(String details) {
        super(CODE, "MenuItem not found", details);
    }
}
