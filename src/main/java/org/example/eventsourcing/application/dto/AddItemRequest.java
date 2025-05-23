package org.example.eventsourcing.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO для добавления товара в заказ.
 */
@Data
public class AddItemRequest {
    @NotBlank
    private String productId; // Идентификатор продукта

    @NotBlank
    private String name;      // Название продукта

    @NotNull
    private BigDecimal price; // Цена продукта
    private int quantity;      // Количество
}
