package org.example.eventsourcing.infrastructure.event;

import jakarta.annotation.PostConstruct;
import org.example.eventsourcing.domain.event.ItemAddedEvent;
import org.example.eventsourcing.domain.event.OrderConfirmedEvent;
import org.example.eventsourcing.domain.event.OrderCreatedEvent;
import org.example.eventsourcing.domain.event.OrderEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Реестр для сопоставления имен типов событий с их классами.
 */
@Component
public class EventTypeRegistry {
    private final Map<String, Class<? extends OrderEvent>> eventTypes = new HashMap<>();

    /**
     * Регистрирует известные типы событий.
     */
    @PostConstruct
    public void registerEvents() {
        eventTypes.put(OrderCreatedEvent.class.getSimpleName(), OrderCreatedEvent.class);
        eventTypes.put(ItemAddedEvent.class.getSimpleName(), ItemAddedEvent.class);
        eventTypes.put(OrderConfirmedEvent.class.getSimpleName(), OrderConfirmedEvent.class);
    }

    /**
     * Возвращает класс события по его имени.
     *
     * @param eventType имя типа события
     * @return класс события
     * @throws IllegalArgumentException если тип события неизвестен
     */
    public Class<? extends OrderEvent> getEventClass(String eventType) {
        Class<? extends OrderEvent> eventClass = eventTypes.get(eventType);
        if (eventClass == null) {
            throw new IllegalArgumentException("Неизвестный тип события: " + eventType);
        }
        return eventClass;
    }
}
