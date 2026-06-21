package com.arturk.fooddelivery.order.config;

import com.arturk.fooddelivery.contracts.catalog.v1.CatalogValidationServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

import static com.arturk.fooddelivery.contracts.catalog.v1.CatalogValidationServiceGrpc.newBlockingStub;

@Configuration
public class CatalogGrpcClientConfig {

    @Bean
    public CatalogValidationServiceGrpc.CatalogValidationServiceBlockingStub catalogValidationBlockingStub(
            GrpcChannelFactory channels
    ) {
        return newBlockingStub(channels.createChannel("catalog-service"));
    }
}
