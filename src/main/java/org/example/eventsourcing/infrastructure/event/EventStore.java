package org.example.eventsourcing.infrastructure.event;


import org.example.eventsourcing.domain.event.OrderEvent;
import org.example.eventsourcing.domain.model.OrderId;

import java.util.List;

/**
 * Интерфейс для операций с хранилищем событий.
 */
public interface EventStore {
    /**
     * Сохраняет события для указанного заказа.
     * @param orderId идентификатор заказа
     * @param events список событий
     */
    void saveEvents(OrderId orderId, List<OrderEvent> events);

    /**
     * Загружает события для указанного заказа.
     * @param orderId идентификатор заказа
     * @return список событий
     */
    List<OrderEvent> loadEvents(OrderId orderId);
}
