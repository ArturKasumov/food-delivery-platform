package com.arturk.fooddelivery.catalog.config;

import com.arturk.fooddelivery.catalog.exception.business.BusinessCatalogAppException;
import io.grpc.Status;
import io.grpc.StatusException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;

@Configuration
@Slf4j
public class GrpcExceptionHandlerConfig {

    @Bean
    public GrpcExceptionHandler businessExceptionHandler() {
        return exception -> {
            if (exception instanceof BusinessCatalogAppException) {
                log.error("Business error occurred", exception);
                return new StatusException(
                        Status.INVALID_ARGUMENT.withDescription(exception.getMessage())
                );
            }

            return null;
        };
    }

    @Bean
    public GrpcExceptionHandler defaultGrpcExceptionHandler() {
        return exception -> {
            log.error("Unexpected error occurred", exception);
            return new StatusException(
                    Status.INTERNAL.withDescription("Some error occurred: " + exception.getMessage())
            );
        };
    }
}
