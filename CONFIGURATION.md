# Application Configuration

This application uses environment variables to configure essential parameters, allowing for flexibility across different deployment environments (local, development, staging, production).

Below is a list of environment variables that can be set to override the default configurations specified in `application.yml`.

## MongoDB Configuration

-   **`MONGODB_URI`**:
    -   Description: The full MongoDB connection string URI. This can include username, password, replica set information, etc.
    -   Default: `mongodb://localhost:27017/order-service-default-db`
    -   Example: `export MONGODB_URI="mongodb://user:password@your-mongo-host:27017/your-db?replicaSet=rs0"`

-   **`MONGODB_DATABASE`**:
    -   Description: The name of the MongoDB database to use.
    -   Default: `order-service-default-db-name`
    -   Example: `export MONGODB_DATABASE="production_order_db"`

## Redis Configuration

-   **`REDIS_HOST`**:
    -   Description: The hostname or IP address of the Redis server.
    -   Default: `localhost`
    -   Example: `export REDIS_HOST="your-redis-host"`

-   **`REDIS_PORT`**:
    -   Description: The port number for the Redis server.
    -   Default: `6379`
    -   Example: `export REDIS_PORT="6380"`
    -   Note: If your Redis server requires a password, you would typically configure it via `spring.data.redis.password=${REDIS_PASSWORD}` in `application.yml` and add `REDIS_PASSWORD` here.

-   **`APP_EVENT_IDEMPOTENCY_REDIS_PREFIX`**:
    -   Description: The prefix used for keys in Redis to ensure idempotency of event processing in `OrderProjectionHandler`. Each processed event ID is stored with this prefix.
    -   Default: `event:`
    -   Example: `export APP_EVENT_IDEMPOTENCY_REDIS_PREFIX="order_events_processed:"`

## Kafka Configuration

-   **`KAFKA_BROKERS`**:
    -   Description: A comma-separated list of host:port pairs for the Kafka brokers. This is used for both producers and consumers.
    -   Default: `localhost:9092`
    -   Example: `export KAFKA_BROKERS="kafka1:9092,kafka2:9092,kafka3:9092"`

-   **`KAFKA_CONSUMER_GROUP_ID`**:
    -   Description: The group ID for Kafka consumers.
    -   Default: `order-service-group`
    -   Example: `export KAFKA_CONSUMER_GROUP_ID="prod-order-service-consumers"`

-   **`APP_KAFKA_TOPIC_ORDER_EVENTS`**:
    -   Description: The Kafka topic name used for publishing and consuming order events.
    -   Default: `order-events`
    -   Example: `export APP_KAFKA_TOPIC_ORDER_EVENTS="prod-order-events"`

## Setting Environment Variables

### For Local Development (Linux/macOS)

You can set these variables in your shell before running the application:

```bash
export MONGODB_URI="mongodb://localhost:27017/my_local_order_db"
export REDIS_HOST="localhost"
export KAFKA_BROKERS="localhost:9092"
# ... and so on for other variables you wish to override
```

Then, run the Spring Boot application (e.g., `./mvnw spring-boot:run` or via your IDE).

### For Dockerized Deployments

If using Docker, you can pass environment variables through a `.env` file or directly in your `docker-compose.yml` or `docker run` command.

Example `docker-compose.yml` snippet:

```yaml
services:
  order-service:
    image: your-order-service-image
    ports:
      - "8080:8080"
    environment:
      - MONGODB_URI=mongodb://mongo_db_host:27017/orders_prod
      - MONGODB_DATABASE=orders_prod
      - REDIS_HOST=redis_host
      - KAFKA_BROKERS=kafka_broker1:9092,kafka_broker2:9092
      - KAFKA_CONSUMER_GROUP_ID=orders_prod_group
      - APP_KAFKA_TOPIC_ORDER_EVENTS=prod_order_events
      - APP_EVENT_IDEMPOTENCY_REDIS_PREFIX=order_events_processed:
    # Or using an env_file:
    # env_file:
    #   - ./production.env
```

Example `.env` file:

```
MONGODB_URI=mongodb://mongo_db_host:27017/orders_prod
MONGODB_DATABASE=orders_prod
REDIS_HOST=redis_host
KAFKA_BROKERS=kafka_broker1:9092,kafka_broker2:9092
KAFKA_CONSUMER_GROUP_ID=orders_prod_group
APP_KAFKA_TOPIC_ORDER_EVENTS=prod_order_events
APP_EVENT_IDEMPOTENCY_REDIS_PREFIX=order_events_processed:
```

By using these environment variables, you can adapt the application's configuration to different environments without modifying the packaged code.
The default values provided are suitable for a typical local development setup where MongoDB, Redis, and Kafka are running on their default ports on `localhost`.
