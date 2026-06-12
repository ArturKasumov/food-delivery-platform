# Local Infrastructure

### Run MongoDB for `catalog-service`:

```bash
docker compose -f infra/docker/docker-compose.yml up -d catalog-mongodb
```

MongoDB connection URI:

```text
mongodb://admin:pwd@localhost:27017/catalog_db
```
