package com.arturk.fooddelivery.order.support;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

@SuppressWarnings("resource")
public abstract class TestContainersSupport {

    private static final Network NETWORK = Network.newNetwork();

    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine");

    static final ConfluentKafkaContainer KAFKA = new ConfluentKafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.7.1"))
            .withNetwork(NETWORK)
            .withNetworkAliases("kafka")
            .withListener("kafka:19092");

    static final GenericContainer<?> SCHEMA_REGISTRY = new GenericContainer<>(DockerImageName.parse("confluentinc/cp-schema-registry:7.7.1"))
            .withNetwork(NETWORK)
            .withExposedPorts(8081)
            .withEnv("SCHEMA_REGISTRY_HOST_NAME", "schema-registry")
            .withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:8081")
            .withEnv(
                    "SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS",
                    "PLAINTEXT://kafka:19092"
            )
            .dependsOn(KAFKA);

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        Startables.deepStart(POSTGRES, KAFKA, SCHEMA_REGISTRY).join();

        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
        registry.add("spring.kafka.producer.properties.schema.registry.url", TestContainersSupport::schemaRegistryUrl);
        registry.add("spring.kafka.consumer.properties.schema.registry.url", TestContainersSupport::schemaRegistryUrl);
        registry.add("spring.kafka.properties.schema.registry.url", TestContainersSupport::schemaRegistryUrl);
    }

    public static String kafkaBootstrapServers() {
        return KAFKA.getBootstrapServers();
    }

    public static String schemaRegistryUrl() {
        return "http://" + SCHEMA_REGISTRY.getHost() + ":" + SCHEMA_REGISTRY.getMappedPort(8081);
    }
}
