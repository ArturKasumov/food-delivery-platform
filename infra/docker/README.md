# Local Infrastructure

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
