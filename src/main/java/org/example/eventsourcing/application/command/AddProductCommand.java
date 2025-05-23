package org.example.eventsourcing.application.command;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.example.eventsourcing.domain.model.OrderId;
import org.example.eventsourcing.domain.model.Product;

@Getter
public class AddProductCommand {

    @NotNull
    private final OrderId orderId;

    @NotNull
    private final Product product;

    /**
     * Создает новую команду для добавления товара.
     * @param orderId идентификатор заказа
     * @param product товар
     */
    public AddProductCommand(OrderId orderId, Product product) {
        this.orderId = orderId;
        this.product = product;
    }
}
