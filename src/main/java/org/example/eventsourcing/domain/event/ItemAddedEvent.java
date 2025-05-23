package org.example.eventsourcing.domain.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.example.eventsourcing.domain.model.OrderId;
import org.example.eventsourcing.domain.model.Product;

import java.time.Instant;
import java.util.UUID;

/**
 * Событие, представляющее добавление товара в заказ.
 */
@Getter
public class ItemAddedEvent implements OrderEvent {
    private final String eventId;    // Уникальный идентификатор события
    private final OrderId orderId;   // Идентификатор заказа
    private final Product product;   // Добавленный товар
    private final Instant timestamp; // Время создания события

    /**
     * Создает новое событие добавления товара.
     *
     * @param orderId идентификатор заказа
     * @param product добавленный товар
     */
    public ItemAddedEvent(
            @JsonProperty("orderId") OrderId orderId,
            @JsonProperty("product") Product product) {
        this.eventId = UUID.randomUUID().toString();
        this.orderId = orderId;
        this.product = product;
        this.timestamp = Instant.now();
    }
}
