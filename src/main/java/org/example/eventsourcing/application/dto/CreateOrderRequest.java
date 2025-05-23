package org.example.eventsourcing.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO для создания заказа.
 */
@Data
public class CreateOrderRequest {

    @NotBlank
    private String customerId; // Идентификатор клиента
}
