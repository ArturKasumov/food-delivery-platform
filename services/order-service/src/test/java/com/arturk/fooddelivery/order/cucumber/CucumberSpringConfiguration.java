package com.arturk.fooddelivery.order.cucumber;

import com.arturk.fooddelivery.order.config.KafkaTestConfiguration;
import com.arturk.fooddelivery.order.service.grpc.client.CatalogValidationClient;
import com.arturk.fooddelivery.order.support.TestContainersSupport;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(KafkaTestConfiguration.class)
public class CucumberSpringConfiguration extends TestContainersSupport {

    @MockitoBean
    CatalogValidationClient catalogValidationClient;
}
