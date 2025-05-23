package org.example.eventsourcing.application.query;

import org.example.eventsourcing.domain.event.OrderConfirmedEvent;
import org.springframework.stereotype.Component;

/**
 * Обработчик события подтверждения заказа.
 */
@Component
public class OrderConfirmedEventHandler implements EventHandler<OrderConfirmedEvent> {
    /**
     * Обрабатывает событие подтверждения заказа.
     * @param event событие подтверждения заказа
     * @param view проекция заказа
     */
    @Override
    public void handle(OrderConfirmedEvent event, OrderView view) {
        view.setConfirmed(true);
    }
}
