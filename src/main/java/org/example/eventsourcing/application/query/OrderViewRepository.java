package org.example.eventsourcing.application.query;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.eventsourcing.domain.event.OrderCreatedEvent;
import org.example.eventsourcing.infrastructure.util.EventSerializer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Репозиторий для хранения и получения проекций заказов в Redis.
 */
@Component
public class OrderViewRepository {
    private static final String PREFIX = "order:"; // Префикс ключа в Redis
    private final RedisTemplate<String, String> redisTemplate; // Клиент Redis
    private final EventSerializer eventSerializer; // Сериализатор
    private final ObjectMapper mapper;

    /**
     * Создает новый репозиторий проекций.
     *
     * @param redisTemplate клиент Redis
     * @param eventSerializer сериализатор
     */
    public OrderViewRepository(RedisTemplate<String, String> redisTemplate, EventSerializer eventSerializer, ObjectMapper mapper) {
        this.redisTemplate = redisTemplate;
        this.eventSerializer = eventSerializer;
        this.mapper = mapper;
    }

    /**
     * Сохраняет проекцию заказа в Redis.
     *
     * @param view проекция заказа
     */
    public void save(OrderView view) {
        String key = PREFIX + view.getOrderId();
        Map<String, String> hash = new HashMap<>();
        hash.put("orderId", view.getOrderId());
        hash.put("customerId", view.getCustomerId());
        hash.put("items", eventSerializer.serialize(view.getItems()));
        hash.put("confirmed", String.valueOf(view.isConfirmed()));
        redisTemplate.opsForHash().putAll(key, hash);
    }

    /**
     * Находит проекцию заказа по идентификатору.
     *
     * @param orderId идентификатор заказа
     * @return проекция заказа или пустой Optional
     */
    public Optional<OrderView> findById(String orderId) {
        String key = PREFIX + orderId;
        Map<Object, Object> hash = redisTemplate.opsForHash().entries(key);
        if (hash.isEmpty()) {
            return Optional.empty();
        }

        OrderView view = new OrderView();
        view.setOrderId((String) hash.get("orderId"));
        view.setCustomerId((String) hash.get("customerId"));
        try {
            view.setItems(mapper.readValue((String) hash.get("items"), ArrayList.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        view.setConfirmed(Boolean.parseBoolean((String) hash.get("confirmed")));

        return Optional.of(view);
    }
}
