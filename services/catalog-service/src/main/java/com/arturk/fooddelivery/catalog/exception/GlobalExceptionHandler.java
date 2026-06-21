package com.arturk.fooddelivery.catalog.exception;

import com.arturk.fooddelivery.catalog.exception.technical.TechnicalCatalogAppException;
import com.arturk.fooddelivery.catalog.exception.business.BusinessCatalogAppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String VALIDATION_ERROR_CODE = "CATALOG-MS-01-ERROR";

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        log.error("Error occurred", exception);
        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(TechnicalCatalogAppException.class)
    public ResponseEntity<ErrorResponse> handleException(TechnicalCatalogAppException exception) {
        log.error("Technical error occurred", exception);
        ErrorResponse errorResponse = new ErrorResponse(exception.getCode(), exception.getDescription(), exception.getDetails());
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BusinessCatalogAppException.class)
    public ResponseEntity<ErrorResponse> handleException(BusinessCatalogAppException exception) {
        log.error("Business error occurred", exception);
        ErrorResponse errorResponse = new ErrorResponse(exception.getCode(), exception.getDescription(), exception.getDetails());
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        String details = exception.getBindingResult().getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining(";"));

        ErrorResponse errorResponse = new ErrorResponse(VALIDATION_ERROR_CODE, "Request validation failed", details);
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()));

    }

    private String formatFieldError(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }
}
