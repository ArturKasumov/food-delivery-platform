package com.arturk.fooddelivery.payment.exception.technical;

import com.arturk.fooddelivery.payment.exception.ApplicationException;

public class TechnicalPaymentAppException extends ApplicationException {

    public TechnicalPaymentAppException(String code, String description, String details) {
        super(code, description, details);
    }
}
