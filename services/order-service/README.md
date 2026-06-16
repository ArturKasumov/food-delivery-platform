# order-service

Owns customer orders and order lifecycle state.

## API

- `POST /api/v1/orders`
- `GET /api/v1/orders/{orderId}`
- `GET /api/v1/orders?customerId={customerId}`

## Local Runtime

The service expects PostgreSQL:

- url: `jdbc:postgresql://localhost:5433/order_db`
- username: `admin`
- password: `pwd`

Run PostgreSQL from the project root:

```bash
docker compose -f infra/docker/docker-compose.yml up -d order-postgres
```

The default service port is `8082`.

## OpenAPI

Swagger UI:

```text
http://localhost:8082/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8082/v3/api-docs
```
