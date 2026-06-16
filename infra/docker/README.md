# Local Infrastructure

### Run MongoDB for `catalog-service`:

```bash
docker compose -f infra/docker/docker-compose.yml up -d catalog-mongodb
```

MongoDB connection URI:

```text
mongodb://admin:pwd@localhost:27017/catalog_db
```

### Run PostgreSQL for `order-service`:

```bash
docker compose -f infra/docker/docker-compose.yml up -d order-postgres
```

PostgreSQL connection URI:

```text
postgresql://admin:pwd@localhost:5433/order_db
```