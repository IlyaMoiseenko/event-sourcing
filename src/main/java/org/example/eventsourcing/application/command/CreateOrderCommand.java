package org.example.eventsourcing.application.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 * Команда для создания нового заказа.
 */
@Getter
public class CreateOrderCommand {

    @NotBlank
    private final String customerId; // Идентификатор клиента

    /**
     * Создает новую команду для создания заказа.
     * @param customerId идентификатор клиента
     */
    public CreateOrderCommand(String customerId) {
        this.customerId = customerId;
    }
}
