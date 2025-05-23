package org.example.eventsourcing.domain.aggregate;

import lombok.Getter;
import org.example.eventsourcing.domain.event.ItemAddedEvent;
import org.example.eventsourcing.domain.event.OrderConfirmedEvent;
import org.example.eventsourcing.domain.event.OrderCreatedEvent;
import org.example.eventsourcing.domain.event.OrderEvent;
import org.example.eventsourcing.domain.model.OrderId;
import org.example.eventsourcing.domain.model.Product;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Order {

    private OrderId orderId;
    private String customerId;
    private final List<Product> products = new ArrayList<>();
    private boolean confirmed;
    private final List<OrderEvent> uncommittedEvents = new ArrayList<>();

    /**
     * Создает новый заказ.
     *
     * @param orderId идентификатор заказа
     * @param customerId идентификатор клиента
     * @return новый объект Order
     */
    public static Order create(OrderId orderId, String customerId) {
        Order order = new Order();
        order.orderId = orderId;
        order.customerId = customerId;
        order.uncommittedEvents.add(new OrderCreatedEvent(orderId, customerId));

        return order;
    }

    /**
     * Применяет историческое событие для восстановления состояния.
     *
     * @param event событие домена
     */
    public void apply(OrderEvent event) {
        if (event instanceof OrderCreatedEvent createdEvent) {
            this.orderId = createdEvent.getOrderId();
            this.customerId = createdEvent.getCustomerId();
        } else if (event instanceof ItemAddedEvent itemAddedEvent) {
            this.products.add(itemAddedEvent.getProduct());
        } else if (event instanceof OrderConfirmedEvent) {
            this.confirmed = true;
        }
    }

    /**
     * Добавляет товар в заказ.
     *
     * @param product товар
     * @throws IllegalStateException если заказ уже подтвержден
     */
    public void addItem(Product product) {
        if (confirmed) {
            throw new IllegalStateException("Нельзя добавить товар в подтвержденный заказ");
        }
        uncommittedEvents.add(new ItemAddedEvent(orderId, product));
        products.add(product);
    }

    /**
     * Подтверждает заказ.
     *
     * @throws IllegalStateException если заказ уже подтвержден или пуст
     */
    public void confirm() {
        if (confirmed) {
            throw new IllegalStateException("Заказ уже подтвержден");
        }
        if (products.isEmpty()) {
            throw new IllegalStateException("Нельзя подтвердить пустой заказ");
        }

        confirmed = true;
        uncommittedEvents.add(new OrderConfirmedEvent(orderId));
    }

    /**
     * Возвращает несохраненные события и очищает их список.
     *
     * @return список несохраненных событий
     */
    public List<OrderEvent> getUncommittedEvents() {
        List<OrderEvent> events = new ArrayList<>(uncommittedEvents);
        uncommittedEvents.clear();

        return events;
    }
}
