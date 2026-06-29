# Local Infrastructure

Docker Compose reads environment-specific values from the shell or from an optional `.env` file in this directory.
Start from the example file when you need to customize local, stage, or prod-like settings:

```bash
cp infra/docker/.env.example infra/docker/.env
```

When running Compose from the repository root with that file, pass it explicitly:

```bash
docker compose --env-file infra/docker/.env -f infra/docker/docker-compose.yml up -d
```

### Run PostgreSQL for `catalog-service`:

```bash
docker compose -f infra/docker/docker-compose.yml up -d catalog-postgres
```

PostgreSQL connection URI:

```text
postgresql://admin:pwd@localhost:5432/catalog_db
```

### Run PostgreSQL for `order-service`:

```bash
docker compose -f infra/docker/docker-compose.yml up -d order-postgres
```

PostgreSQL connection URI:

```text
postgresql://admin:pwd@localhost:5433/order_db
```

### Run metrics stack:

```bash
docker compose -f infra/docker/docker-compose.yml up -d prometheus grafana
```

### Run centralized logging stack:

```bash
docker compose -f infra/docker/docker-compose.yml up -d loki alloy grafana
```

Spring Boot services keep human-readable console logs and also write JSON files:

```text
services/api-gateway/logs/api-gateway.json
services/catalog-service/logs/catalog-service.json
services/order-service/logs/order-service.json
```

Alloy reads these files and sends them to Loki. In Grafana, open `Explore`, select the `Loki` datasource and run:

```logql
{service_name="order-service"}
```

Search by correlation id:

```logql
{service_name="order-service"} | json | correlationId="your-correlation-id"
```

Local JSON files and Loki data are retained for 7 days. Each service also limits rotated JSON archives to `1 GB`.

Prometheus:

```text
http://localhost:9090
```

Grafana:

```text
http://localhost:3000
```

Default Grafana credentials:

```text
admin / admin
```

Prometheus scrapes local services through Docker Desktop host networking:

```text
api-gateway     -> host.docker.internal:8080/actuator/prometheus
catalog-service -> host.docker.internal:8081/actuator/prometheus
order-service   -> host.docker.internal:8082/actuator/prometheus
```

Override these targets with:

```text
API_GATEWAY_METRICS_TARGET
CATALOG_SERVICE_METRICS_TARGET
ORDER_SERVICE_METRICS_TARGET
```

### Run tracing stack:

```bash
docker compose -f infra/docker/docker-compose.yml up -d tempo alloy grafana
```

Tracing flow:

```text
Spring Boot + OpenTelemetry Java Agent
  -> OTLP localhost:4317
  -> Alloy
  -> Tempo
  -> Grafana
```

Tempo:

```text
http://localhost:3200
```

Alloy receives OTLP traces on:

```text
grpc: localhost:4317
http: localhost:4318
```

Download the OpenTelemetry Java Agent and keep it outside service source code, for example:

```text
D:\arturk\JavaProjects\food-delivery-platform\infra\opentelemetry-javaagent.jar
```

OpenTelemetry Java Agent reads its settings from service-specific config files:

```text
D:\arturk\JavaProjects\food-delivery-platform\infra\opentelemetry\api-gateway.properties
D:\arturk\JavaProjects\food-delivery-platform\infra\opentelemetry\catalog-service.properties
D:\arturk\JavaProjects\food-delivery-platform\infra\opentelemetry\order-service.properties
```

Use these VM options:

`api-gateway`:

```text
-javaagent:D:\arturk\JavaProjects\food-delivery-platform\infra\opentelemetry-javaagent.jar -Dotel.javaagent.configuration-file=D:\arturk\JavaProjects\food-delivery-platform\infra\opentelemetry\api-gateway.properties
```

`catalog-service`:

```text
-javaagent:D:\arturk\JavaProjects\food-delivery-platform\infra\opentelemetry-javaagent.jar -Dotel.javaagent.configuration-file=D:\arturk\JavaProjects\food-delivery-platform\infra\opentelemetry\catalog-service.properties
```

`order-service`:

```text
-javaagent:D:\arturk\JavaProjects\food-delivery-platform\infra\opentelemetry-javaagent.jar -Dotel.javaagent.configuration-file=D:\arturk\JavaProjects\food-delivery-platform\infra\opentelemetry\order-service.properties
```

After generating traffic, open Grafana, select the `Tempo` datasource in Explore, and search recent traces.
