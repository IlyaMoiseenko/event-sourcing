package org.example.eventsourcing.application.query;

import org.example.eventsourcing.domain.event.OrderEvent;

/**
 * Интерфейс для обработки событий заказов.
 */
public interface EventHandler<T extends OrderEvent> {

    /**
     * Обрабатывает событие и обновляет проекцию.
     *
     * @param event событие
     * @param view проекция заказа
     */
    void handle(T event, OrderView view);
}
