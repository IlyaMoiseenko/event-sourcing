package org.example.eventsourcing.application.query;

import lombok.Data;
import org.example.eventsourcing.domain.model.Product;

import java.util.List;

/**
 * Проекция, представляющая текущее состояние заказа.
 */
@Data
public class OrderView {
    private String orderId;      // Идентификатор заказа
    private String customerId;   // Идентификатор клиента
    private List<Product> items;    // Список товаров
    private boolean confirmed;   // Статус подтверждения
}
