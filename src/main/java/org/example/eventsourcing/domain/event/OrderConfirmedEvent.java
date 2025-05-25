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
    private static final int CURRENT_VERSION = 1;

    private final String eventId;    // Уникальный идентификатор события
    private final OrderId orderId;   // Идентификатор заказа
    private final Instant timestamp; // Время создания события
    private final int version;       // Версия события

    /**
     * Создает новое событие подтверждения заказа.
     *
     * @param orderId идентификатор заказа
     */
    public OrderConfirmedEvent(OrderId orderId) {
        this.eventId = UUID.randomUUID().toString();
        this.orderId = orderId;
        this.timestamp = Instant.now();
        this.version = CURRENT_VERSION;
    }

    @Override
    public int getVersion() {
        return version;
    }
}
