package org.example.eventsourcing.domain.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.example.eventsourcing.domain.model.OrderId;

import java.time.Instant;
import java.util.UUID;

@Getter
public class OrderCreatedEvent implements OrderEvent {

    private final String eventId;       // Уникальный идентификатор события
    private final OrderId orderId;      // Идентификатор заказа
    private final String customerId;    // Идентификатор клиента
    private final Instant timestamp;    // Время создания события

    /**
     * Создает новое событие создания заказа.
     *
     * @param orderId идентификатор заказа
     * @param customerId идентификатор клиента
     */
    public OrderCreatedEvent(
            @JsonProperty("eventId") OrderId orderId,
            @JsonProperty("customerId") String customerId
    ) {
        this.eventId = UUID.randomUUID().toString();
        this.orderId = orderId;
        this.customerId = customerId;
        this.timestamp = Instant.now();
    }
}
