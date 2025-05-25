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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

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
        log.info("Received request to create order for customerId: {}", request.getCustomerId());
        OrderId orderId = commandHandler.handle(
                new CreateOrderCommand(request.getCustomerId())
        );
        log.info("Order created successfully with orderId: {}", orderId.getValue());
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
        log.info("Received request to add item to orderId: {}. ProductId: {}", orderId, request.getProductId());
        Product item = new Product(request.getProductId(), request.getName(), request.getPrice(), request.getQuantity());
        commandHandler.handle(new AddProductCommand(new OrderId(orderId), item));
        log.info("Item added successfully to orderId: {}", orderId);
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
        log.info("Received request to confirm orderId: {}", orderId);
        commandHandler.handle(new ConfirmOrderCommand(new OrderId(orderId)));
        log.info("Order confirmed successfully: {}", orderId);
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
        log.info("Received request to get order by orderId: {}", orderId);
        Optional<OrderResponse> response = viewRepository.findById(orderId)
                .map(orderMapper::toResponse);

        if (response.isPresent()) {
            log.info("Order found for orderId: {}", orderId);
            return ResponseEntity.ok(response.get());
        } else {
            log.warn("Order not found for orderId: {}", orderId);
            return ResponseEntity.notFound().build();
        }
    }
}
