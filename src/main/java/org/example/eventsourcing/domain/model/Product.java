package org.example.eventsourcing.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Класс, представляющий товар в заказе.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {

    private String productId; // Идентификатор продукта
    private String name;      // Название продукта
    private BigDecimal price; // Цена продукта
    private int quantity;     // Количество
}
