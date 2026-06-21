# catalog-service

Owns restaurantEntity and menu catalog data.

## API

- `GET /api/v1/catalog/restaurants`
- `POST /api/v1/catalog/restaurants`
- `GET /api/v1/catalog/restaurants/{restaurantId}/menu-items`
- `POST /api/v1/catalog/restaurants/{restaurantId}/menu-items`

## Local Runtime

The service expects PostgreSQL:

- JDBC URL: `jdbc:postgresql://localhost:5432/catalog_db`
- Username: `admin`
- Password: `pwd`

Database schema is managed by Liquibase XML changelogs.

The default service port is `8081`.

## OpenAPI

Swagger UI:

```text
http://localhost:8081/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8081/v3/api-docs
```
