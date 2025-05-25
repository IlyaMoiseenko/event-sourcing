package org.example.eventsourcing.application.command;

import org.example.eventsourcing.domain.aggregate.Order;
import org.example.eventsourcing.domain.event.OrderEvent;
import org.example.eventsourcing.domain.model.OrderId;
import org.example.eventsourcing.infrastructure.event.EventStore;
import org.example.eventsourcing.infrastructure.messaging.OrderEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Обработчик команд для управления заказами.
 */
@Component
public class OrderCommandHandler {
    private static final Logger log = LoggerFactory.getLogger(OrderCommandHandler.class);

    private final EventStore eventStore;           // Хранилище событий
    private final OrderEventProducer eventProducer; // Публикатор событий

    /**
     * Создает новый обработчик команд.
     *
     * @param eventStore хранилище событий
     * @param eventProducer публикатор событий
     */
    public OrderCommandHandler(EventStore eventStore, OrderEventProducer eventProducer) {
        this.eventStore = eventStore;
        this.eventProducer = eventProducer;
    }

    /**
     * Обрабатывает команду создания заказа.
     *
     * @param command команда создания заказа
     * @return идентификатор созданного заказа
     */
    public OrderId handle(CreateOrderCommand command) {
        log.info("Handling CreateOrderCommand for customerId: {}", command.getCustomerId());
        OrderId orderId = OrderId.generate();
        log.debug("Generated new orderId: {}", orderId.getValue());
        Order order = Order.create(orderId, command.getCustomerId());
        saveEvents(order);
        log.info("CreateOrderCommand handled successfully for orderId: {}", orderId.getValue());
        return orderId;
    }

    /**
     * Обрабатывает команду добавления товара.
     *
     * @param command команда добавления товара
     */
    public void handle(AddProductCommand command) {
        log.info("Handling AddProductCommand for orderId: {}, product: {}", command.getOrderId().getValue(), command.getProduct().getProductId());
        Order order = loadOrder(command.getOrderId());
        order.addItem(command.getProduct());
        saveEvents(order);
        log.info("AddProductCommand handled successfully for orderId: {}", command.getOrderId().getValue());
    }

    /**
     * Обрабатывает команду подтверждения заказа.
     *
     * @param command команда подтверждения заказа
     */
    public void handle(ConfirmOrderCommand command) {
        log.info("Handling ConfirmOrderCommand for orderId: {}", command.getOrderId().getValue());
        Order order = loadOrder(command.getOrderId());
        order.confirm();
        saveEvents(order);
        log.info("ConfirmOrderCommand handled successfully for orderId: {}", command.getOrderId().getValue());
    }

    /**
     * Загружает заказ из хранилища событий.
     *
     * @param orderId идентификатор заказа
     * @return объект заказа
     */
    private Order loadOrder(OrderId orderId) {
        log.debug("Loading order for orderId: {}", orderId.getValue());
        List<OrderEvent> events = eventStore.loadEvents(orderId);
        log.debug("Loaded {} events for orderId: {}", events.size(), orderId.getValue());
        Order order = new Order();
        events.forEach(event -> {
            log.debug("Applying event {} to orderId: {}", event.getClass().getSimpleName(), orderId.getValue());
            order.apply(event);
        });
        log.debug("Order loaded and events applied for orderId: {}", orderId.getValue());
        return order;
    }

    /**
     * Сохраняет события заказа и публикует их.
     *
     * @param order объект заказа
     */
    private void saveEvents(Order order) {
        log.debug("Saving events for orderId: {}", order.getOrderId().getValue());
        List<OrderEvent> events = order.getUncommittedEvents();
        if (events.isEmpty()) {
            log.debug("No uncommitted events to save for orderId: {}", order.getOrderId().getValue());
            return;
        }
        log.debug("Found {} uncommitted events for orderId: {}", events.size(), order.getOrderId().getValue());
        eventStore.saveEvents(order.getOrderId(), events);
        log.info("Saved {} events to event store for orderId: {}", events.size(), order.getOrderId().getValue());
        events.forEach(event -> {
            log.debug("Publishing event {} for orderId: {}", event.getClass().getSimpleName(), order.getOrderId().getValue());
            eventProducer.publish(order.getOrderId(), event);
        });
        log.info("Published {} events for orderId: {}", events.size(), order.getOrderId().getValue());
    }
}
