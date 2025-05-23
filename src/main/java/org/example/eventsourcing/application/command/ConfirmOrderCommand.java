package org.example.eventsourcing.application.command;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.example.eventsourcing.domain.model.OrderId;

/**
 * Команда для подтверждения заказа.
 */
@Getter
public class ConfirmOrderCommand {
    @NotNull
    private final OrderId orderId; // Идентификатор заказа

    /**
     * Создает новую команду для подтверждения заказа.
     * @param orderId идентификатор заказа
     */
    public ConfirmOrderCommand(OrderId orderId) {
        this.orderId = orderId;
    }
}
