package org.example.eventsourcing.application.mapper;


import org.example.eventsourcing.application.dto.OrderResponse;
import org.example.eventsourcing.application.query.OrderView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Маппер для преобразования между OrderView и OrderResponse.
 */
@Mapper(componentModel = "spring")
public interface OrderMapper {
    /**
     * Преобразует проекцию в DTO ответа.
     * @param view проекция заказа
     * @return DTO ответа
     */
    @Mapping(source = "orderId", target = "orderId")
    @Mapping(source = "customerId", target = "customerId")
    @Mapping(source = "items", target = "items")
    @Mapping(source = "confirmed", target = "confirmed")
    OrderResponse toResponse(OrderView view);
}
