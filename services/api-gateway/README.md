# api-gateway

Single external entry point for the food delivery backend.

## Routes

| Public path          | Target service    | Default target URL      |
|----------------------|-------------------|-------------------------|
| `/api/v1/catalog/**` | `catalog-service` | `http://localhost:8081` |
| `/api/v1/orders/**`  | `order-service`   | `http://localhost:8082` |

Override services target URL:

```bash
CATALOG_SERVICE_URL=http://localhost:8081
```

```bash
ORDER_SERVICE_URL=http://localhost:8082
```

Run locally:

```bash
mvn spring-boot:run
```

Gateway port:

```text
http://localhost:8080
```
