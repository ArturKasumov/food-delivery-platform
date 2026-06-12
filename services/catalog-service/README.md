# catalog-service

Owns restaurant and menu catalog data.

## API

- `GET /api/v1/catalog/restaurants`
- `POST /api/v1/catalog/restaurants`
- `GET /api/v1/catalog/restaurants/{restaurantId}/menu-items`
- `POST /api/v1/catalog/restaurants/{restaurantId}/menu-items`

## Local Runtime

The service expects MongoDB:

- URI: `mongodb://admin:pwd@localhost:27017/catalog_db`

The default service port is `8081`.
