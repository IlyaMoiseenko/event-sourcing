package org.example.eventsourcing.application.controller;

import jakarta.validation.Valid;
import org.example.eventsourcing.application.command.AddProductCommand;
import org.example.eventsourcing.application.command.ConfirmOrderCommand;
import org.example.eventsourcing.application.command.CreateOrderCommand;
import org.example.eventsourcing.application.command.OrderCommandHandler;
import org.example.eventsourcing.application.dto.AddItemRequest;
import org.example.eventsourcing.application.dto.CreateOrderRequest;
import org.example.eventsourcing.application.dto.OrderResponse;
import org.example.eventsourcing.application.mapper.OrderMapper;
import org.example.eventsourcing.application.query.OrderViewRepository;
import org.example.eventsourcing.domain.model.OrderId;
import org.example.eventsourcing.domain.model.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * REST-контроллер для управления заказами.
 */
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderCommandHandler commandHandler;  // Обработчик команд
    private final OrderViewRepository viewRepository; // Репозиторий проекций
    private final OrderMapper orderMapper;            // Маппер

    /**
     * Создает новый REST-контроллер.
     *
     * @param commandHandler обработчик команд
     * @param viewRepository репозиторий проекций
     * @param orderMapper маппер
     */
    public OrderController(OrderCommandHandler commandHandler, OrderViewRepository viewRepository, OrderMapper orderMapper) {
        this.commandHandler = commandHandler;
        this.viewRepository = viewRepository;
        this.orderMapper = orderMapper;
    }

    /**
     * Создает новый заказ.
     *
     * @param request запрос на создание заказа
     * @return идентификатор созданного заказа
     */
    @PostMapping
    public ResponseEntity<String> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderId orderId = commandHandler.handle(
                new CreateOrderCommand(request.getCustomerId())
        );

        return ResponseEntity.ok(orderId.getValue());
    }

    /**
     * Добавляет товар в заказ.
     *
     * @param orderId идентификатор заказа
     * @param request запрос на добавление товара
     * @return пустой ответ
     */
    @PostMapping("/{orderId}/items")
    public ResponseEntity<Void> addItem(@PathVariable String orderId, @Valid @RequestBody AddItemRequest request) {
        Product item = new Product(request.getProductId(), request.getName(), request.getPrice(), request.getQuantity());
        commandHandler.handle(new AddProductCommand(new OrderId(orderId), item));

        return ResponseEntity.ok().build();
    }

    /**
     * Подтверждает заказ.
     *
     * @param orderId идентификатор заказа
     * @return пустой ответ
     */
    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<Void> confirmOrder(@PathVariable String orderId) {
        commandHandler.handle(new ConfirmOrderCommand(new OrderId(orderId)));
        return ResponseEntity.ok().build();
    }

    /**
     * Получает информацию о заказе.
     *
     * @param orderId идентификатор заказа
     * @return информация о заказе или 404, если заказ не найден
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderId) {
        Optional<OrderResponse> response = viewRepository.findById(orderId)
                .map(orderMapper::toResponse);
        return response.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
