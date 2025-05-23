package org.example.eventsourcing.infrastructure.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.eventsourcing.domain.event.OrderEvent;
import org.example.eventsourcing.domain.model.Product;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Утилита для сериализации и десериализации событий.
 */
@Component
public class EventSerializer {
    private final ObjectMapper objectMapper; // Объект для работы с JSON

    /**
     * Создает новый сериализатор событий.
     *
     * @param objectMapper объект для работы с JSON
     */
    public EventSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Сериализует событие в JSON.
     *
     * @param event событие домена
     * @return строка JSON
     */
    @SneakyThrows
    public String serialize(OrderEvent event) {
        return objectMapper.writeValueAsString(event);
    }

    /**
     * Сериализует продукты в JSON.
     *
     * @param event событие домена
     * @return строка JSON
     */
    @SneakyThrows
    public String serialize(List<Product> products) {
        return objectMapper.writeValueAsString(products);
    }

    /**
     * Десериализует JSON в событие.
     *
     * @param data строка JSON
     * @param eventClass класс события
     * @return событие домена
     */
    @SneakyThrows
    public <T extends OrderEvent> T deserialize(String data, Class<T> eventClass) {
        return objectMapper.readValue(data, eventClass);
    }
}
