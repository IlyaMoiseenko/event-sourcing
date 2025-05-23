package org.example.eventsourcing.application.query;

import jakarta.annotation.PostConstruct;
import org.example.eventsourcing.domain.event.ItemAddedEvent;
import org.example.eventsourcing.domain.event.OrderConfirmedEvent;
import org.example.eventsourcing.domain.event.OrderCreatedEvent;
import org.example.eventsourcing.domain.event.OrderEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Реестр обработчиков событий.
 */
@Component
public class EventHandlerRegistry {

    private final Map<Class<? extends OrderEvent>, EventHandler<? extends OrderEvent>> handlers = new HashMap<>();

    /**
     * Регистрирует обработчики событий.
     */
    @PostConstruct
    public void registerHandlers() {
        handlers.put(OrderCreatedEvent.class, new OrderCreatedEventHandler());
        handlers.put(ItemAddedEvent.class, new ItemAddedEventHandler());
        handlers.put(OrderConfirmedEvent.class, new OrderConfirmedEventHandler());
    }

    /**
     * Возвращает обработчик для события.
     *
     * @param event событие
     * @return обработчик события
     */
    @SuppressWarnings("unchecked")
    public <T extends OrderEvent> EventHandler<T> getHandler(T event) {
        return (EventHandler<T>) handlers.get(event.getClass());
    }
}
