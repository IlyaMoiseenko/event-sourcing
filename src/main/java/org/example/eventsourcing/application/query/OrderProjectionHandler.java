package org.example.eventsourcing.application.query;

import org.example.eventsourcing.domain.event.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderProjectionHandler {
    private static final Logger log = LoggerFactory.getLogger(OrderProjectionHandler.class);

    private final String eventPrefix;                   // Префикс для идемпотентности
    private final OrderViewRepository repository;       // Репозиторий проекций
    private final EventHandlerRegistry handlerRegistry;  // Реестр обработчиков
    private final RedisTemplate<String, String> redisTemplate; // Клиент Redis

    /**
     * Создает новый обработчик проекций.
     * @param repository репозиторий проекций
     * @param handlerRegistry реестр обработчиков
     * @param redisTemplate клиент Redis
     * @param eventPrefix префикс для ключей идемпотентности в Redis
     */
    public OrderProjectionHandler(
            OrderViewRepository repository,
            EventHandlerRegistry handlerRegistry,
            RedisTemplate<String, String> redisTemplate,
            @Value("${application.event.idempotency.redis-prefix}") String eventPrefix
    ) {
        this.repository = repository;
        this.handlerRegistry = handlerRegistry;
        this.redisTemplate = redisTemplate;
        this.eventPrefix = eventPrefix;
    }

    /**
     * Обрабатывает пакет событий из Kafka.
     *
     * @param events список событий
     */
    @KafkaListener(topics = "${app.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void handle(List<OrderEvent> events) {
        log.info("Received batch of {} events from Kafka.", events.size());
        for (OrderEvent event : events) {
            String eventKey = eventPrefix + event.getEventId();
            log.debug("Processing event: {}, eventId: {}, orderId: {} with eventKey: {}",
                    event.getClass().getSimpleName(), event.getEventId(), event.getOrderId().getValue(), eventKey);

            try {
                if (Boolean.TRUE.equals(redisTemplate.hasKey(eventKey))) {
                    log.info("Event eventId: {} for orderId: {} already processed (idempotency check via Redis using key '{}'). Skipping.",
                            event.getEventId(), event.getOrderId().getValue(), eventKey);
                    continue;
                }

                log.debug("Fetching or creating OrderView for orderId: {}", event.getOrderId().getValue());
                OrderView view = repository.findById(event.getOrderId().getValue())
                        .orElseGet(() -> {
                            log.info("Creating new OrderView for orderId: {}", event.getOrderId().getValue());
                            return new OrderView();
                        });

                EventHandler<OrderEvent> handler = handlerRegistry.getHandler(event);
                if (handler != null) {
                    log.debug("Invoking handler {} for event: {}, orderId: {}",
                            handler.getClass().getSimpleName(), event.getClass().getSimpleName(), event.getOrderId().getValue());
                    handler.handle(event, view);
                    repository.save(view);
                    log.info("OrderView updated and saved for event: {}, orderId: {}",
                            event.getClass().getSimpleName(), event.getOrderId().getValue());
                    redisTemplate.opsForValue().set(eventKey, "processed");
                    log.debug("Marked eventId: {} for orderId: {} as processed in Redis with key '{}'.",
                            event.getEventId(), event.getOrderId().getValue(), eventKey);
                } else {
                    log.warn("No handler found for event type: {} with eventId: {}",
                            event.getClass().getSimpleName(), event.getEventId());
                }
            } catch (Exception e) {
                log.error("Error processing event eventId: {} for orderId: {}. Error: {}",
                        event.getEventId(), event.getOrderId().getValue(), e.getMessage(), e);
                // Depending on requirements, might rethrow, or send to a DLQ
            }
        }
        log.info("Finished processing batch of {} events.", events.size());
    }
}
