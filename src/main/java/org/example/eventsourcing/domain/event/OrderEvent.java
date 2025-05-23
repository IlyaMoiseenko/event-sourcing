package org.example.eventsourcing.domain.event;

import org.example.eventsourcing.domain.model.OrderId;

/**
 * Базовый интерфейс для всех событий домена, связанных с заказами.
 */
public interface OrderEvent {

    /**
     * Возвращает идентификатор заказа.
     *
     * @return идентификатор заказа
     */
    OrderId getOrderId();

    /**
     * Возвращает уникальный идентификатор события.
     *
     * @return идентификатор события
     */
    String getEventId();
}
