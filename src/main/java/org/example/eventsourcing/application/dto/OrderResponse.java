package org.example.eventsourcing.application.dto;


import lombok.Data;
import org.example.eventsourcing.domain.model.Product;

import java.util.List;

/**
 * DTO для ответа с информацией о заказе.
 */
@Data
public class OrderResponse {
    private String orderId;    // Идентификатор заказа
    private String customerId; // Идентификатор клиента
    private List<Product> items;  // Список товаров
    private boolean confirmed; // Статус подтверждения
}
