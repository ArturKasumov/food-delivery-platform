package com.arturk.fooddelivery.order;

import com.arturk.fooddelivery.order.config.KafkaTestConfiguration;
import com.arturk.fooddelivery.order.config.properties.KafkaTopicsProperties;
import com.arturk.fooddelivery.order.config.properties.OutboxPublisherProperties;
import com.arturk.fooddelivery.order.service.grpc.client.CatalogValidationClient;
import com.arturk.fooddelivery.order.support.KafkaTestClient;
import com.arturk.fooddelivery.order.support.TestContainersSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
@Import(KafkaTestConfiguration.class)
public abstract class AbstractIntegrationTest extends TestContainersSupport {

    @Autowired
    protected OutboxPublisherProperties outboxPublisherProperties;

    @Autowired
    protected KafkaTopicsProperties kafkaTopicsProperties;

    @Autowired
    protected KafkaTestClient kafkaTestClient;

    @MockitoBean
    protected CatalogValidationClient catalogValidationClient;
}
