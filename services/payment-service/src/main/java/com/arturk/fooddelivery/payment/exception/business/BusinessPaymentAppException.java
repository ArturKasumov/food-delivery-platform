package com.arturk.fooddelivery.payment.exception.business;

import com.arturk.fooddelivery.payment.exception.ApplicationException;

public class BusinessPaymentAppException extends ApplicationException {

    public BusinessPaymentAppException(String code, String description, String details) {
        super(code, description, details);
    }
}
