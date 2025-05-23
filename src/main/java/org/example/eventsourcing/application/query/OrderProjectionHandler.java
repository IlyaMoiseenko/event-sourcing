package org.example.eventsourcing.application.query;

import org.example.eventsourcing.domain.event.OrderEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderProjectionHandler {

    private static final String EVENT_PREFIX = "event:"; // Префикс для идемпотентности
    private final OrderViewRepository repository;       // Репозиторий проекций
    private final EventHandlerRegistry handlerRegistry;  // Реестр обработчиков
    private final RedisTemplate<String, String> redisTemplate; // Клиент Redis

    /**
     * Создает новый обработчик проекций.
     * @param repository репозиторий проекций
     * @param handlerRegistry реестр обработчиков
     * @param redisTemplate клиент Redis
     */
    public OrderProjectionHandler(OrderViewRepository repository, EventHandlerRegistry handlerRegistry, RedisTemplate<String, String> redisTemplate) {
        this.repository = repository;
        this.handlerRegistry = handlerRegistry;
        this.redisTemplate = redisTemplate;
    }

    /**
     * Обрабатывает пакет событий из Kafka.
     *
     * @param events список событий
     */
    @KafkaListener(topics = "${app.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void handle(List<OrderEvent> events) {
        for (OrderEvent event : events) {
            String eventKey = EVENT_PREFIX + event.getEventId();
            if (Boolean.TRUE.equals(redisTemplate.hasKey(eventKey))) {
                continue; // Пропускаем уже обработанное событие
            }

            OrderView view = repository.findById(event.getOrderId().getValue())
                    .orElse(new OrderView());

            EventHandler<OrderEvent> handler = handlerRegistry.getHandler(event);
            handler.handle(event, view);
            repository.save(view);
            redisTemplate.opsForValue().set(eventKey, "processed");
        }
    }
}
