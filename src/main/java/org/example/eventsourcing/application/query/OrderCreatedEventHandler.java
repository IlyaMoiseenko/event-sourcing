package org.example.eventsourcing.application.query;

import org.example.eventsourcing.domain.event.OrderCreatedEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Обработчик события создания заказа.
 */
@Component
public class OrderCreatedEventHandler implements EventHandler<OrderCreatedEvent> {

    /**
     * Обрабатывает событие создания заказа.
     *
     * @param event событие создания заказа
     * @param view проекция заказа
     */
    @Override
    public void handle(OrderCreatedEvent event, OrderView view) {
        view.setOrderId(event.getOrderId().getValue());
        view.setCustomerId(event.getCustomerId());
        view.setItems(new ArrayList<>());
        view.setConfirmed(false);
    }
}
