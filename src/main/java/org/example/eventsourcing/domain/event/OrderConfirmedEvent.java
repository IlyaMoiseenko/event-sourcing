package org.example.eventsourcing.domain.event;

import lombok.Getter;
import org.example.eventsourcing.domain.model.OrderId;

import java.time.Instant;
import java.util.UUID;

/**
 * Событие, представляющее подтверждение заказа.
 */
@Getter
public class OrderConfirmedEvent implements OrderEvent {
    private final String eventId;    // Уникальный идентификатор события
    private final OrderId orderId;   // Идентификатор заказа
    private final Instant timestamp; // Время создания события

    /**
     * Создает новое событие подтверждения заказа.
     *
     * @param orderId идентификатор заказа
     */
    public OrderConfirmedEvent(OrderId orderId) {
        this.eventId = UUID.randomUUID().toString();
        this.orderId = orderId;
        this.timestamp = Instant.now();
    }
}
