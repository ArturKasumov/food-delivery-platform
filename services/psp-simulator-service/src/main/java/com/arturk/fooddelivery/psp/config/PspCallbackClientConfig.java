package com.arturk.fooddelivery.psp.config;

import com.arturk.fooddelivery.psp.config.properties.PspCallbackProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class PspCallbackClientConfig {

    @Bean
    public RestClient pspCallbackRestClient(PspCallbackProperties properties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout((int) properties.connectTimeout().toMillis());
        requestFactory.setReadTimeout((int) properties.readTimeout().toMillis());

        return RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }
}
