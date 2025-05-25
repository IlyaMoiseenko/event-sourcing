package org.example.eventsourcing.infrastructure.event;

import org.example.eventsourcing.domain.event.OrderEvent;
import org.example.eventsourcing.domain.model.OrderId;
import org.example.eventsourcing.infrastructure.util.EventSerializer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация хранилища событий с использованием MongoDB.
 */
@Component
public class MongoEventStore implements EventStore {
    private final EventRepository eventRepository;      // Репозиторий MongoDB
    private final EventSerializer eventSerializer;      // Сериализатор событий
    private final EventTypeRegistry eventTypeRegistry;  // Реестр типов событий

    /**
     * Создает новое хранилище событий.
     *
     * @param eventRepository репозиторий MongoDB
     * @param eventSerializer сериализатор событий
     * @param eventTypeRegistry реестр типов событий
     */
    public MongoEventStore(EventRepository eventRepository, EventSerializer eventSerializer, EventTypeRegistry eventTypeRegistry) {
        this.eventRepository = eventRepository;
        this.eventSerializer = eventSerializer;
        this.eventTypeRegistry = eventTypeRegistry;
    }

    /**
     * Сохраняет события для указанного заказа.
     *
     * @param orderId идентификатор заказа
     * @param events список событий
     */
    @Override
    public void saveEvents(OrderId orderId, List<OrderEvent> events) {
        List<StoredEvent> storedEvents = events.stream()
                .map(event -> new StoredEvent(orderId.getValue(), event.getClass().getSimpleName(), eventSerializer.serialize(event), event.getVersion()))
                .toList();
        eventRepository.saveAll(storedEvents);
    }

    /**
     * Загружает события для указанного заказа.
     *
     * @param orderId идентификатор заказа
     * @return список событий
     */
    @Override
    public List<OrderEvent> loadEvents(OrderId orderId) {
        return eventRepository.findByAggregateIdOrderByTimestampAsc(orderId.getValue())
                .stream()
                .map(this::deserializeEvent)
                .collect(Collectors.toList());
    }

    /**
     * Десериализует сохраненное событие.
     *
     * @param storedEvent сохраненное событие
     * @return событие домена
     */
    private OrderEvent deserializeEvent(StoredEvent storedEvent) {
        Class<? extends OrderEvent> eventClass = eventTypeRegistry.getEventClass(storedEvent.getEventType());

        return eventSerializer.deserialize(storedEvent.getEventData(), eventClass);
    }
}
