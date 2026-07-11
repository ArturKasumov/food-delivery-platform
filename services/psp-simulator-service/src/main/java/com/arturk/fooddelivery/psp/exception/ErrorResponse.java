package com.arturk.fooddelivery.psp.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponse(
        String message,
        List<String> details,
        LocalDateTime timestamp
) {
}
