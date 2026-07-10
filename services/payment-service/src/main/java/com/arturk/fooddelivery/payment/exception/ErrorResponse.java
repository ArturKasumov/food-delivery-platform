package com.arturk.fooddelivery.payment.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private String code;
    private String description;
    private String details;

    public ErrorResponse(String description) {
        this.description = description;
    }

    public ErrorResponse(String code, String description, String details) {
        this.code = code;
        this.description = description;
        this.details = details;
    }
}
