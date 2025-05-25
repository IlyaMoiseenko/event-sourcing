package org.example.eventsourcing.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Класс, представляющий товар в заказе.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {

    private final String productId; // Идентификатор продукта
    private final String name;      // Название продукта
    private final BigDecimal price; // Цена продукта
    private final int quantity;     // Количество
}
