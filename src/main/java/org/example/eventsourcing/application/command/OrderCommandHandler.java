package org.example.eventsourcing.application.command;

import org.example.eventsourcing.domain.aggregate.Order;
import org.example.eventsourcing.domain.event.OrderEvent;
import org.example.eventsourcing.domain.model.OrderId;
import org.example.eventsourcing.infrastructure.event.EventStore;
import org.example.eventsourcing.infrastructure.messaging.OrderEventProducer;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Обработчик команд для управления заказами.
 */
@Component
public class OrderCommandHandler {
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
        OrderId orderId = OrderId.generate();
        Order order = Order.create(orderId, command.getCustomerId());
        saveEvents(order);

        return orderId;
    }

    /**
     * Обрабатывает команду добавления товара.
     *
     * @param command команда добавления товара
     */
    public void handle(AddProductCommand command) {
        Order order = loadOrder(command.getOrderId());
        order.addItem(command.getProduct());
        saveEvents(order);
    }

    /**
     * Обрабатывает команду подтверждения заказа.
     *
     * @param command команда подтверждения заказа
     */
    public void handle(ConfirmOrderCommand command) {
        Order order = loadOrder(command.getOrderId());
        order.confirm();
        saveEvents(order);
    }

    /**
     * Загружает заказ из хранилища событий.
     *
     * @param orderId идентификатор заказа
     * @return объект заказа
     */
    private Order loadOrder(OrderId orderId) {
        List<OrderEvent> events = eventStore.loadEvents(orderId);
        Order order = new Order();
        events.forEach(order::apply);

        return order;
    }

    /**
     * Сохраняет события заказа и публикует их.
     *
     * @param order объект заказа
     */
    private void saveEvents(Order order) {
        List<OrderEvent> events = order.getUncommittedEvents();
        eventStore.saveEvents(order.getOrderId(), events);
        events.forEach(event -> eventProducer.publish(order.getOrderId(), event));
    }
}
