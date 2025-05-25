package org.example.eventsourcing.domain.aggregate;

import org.example.eventsourcing.domain.event.ItemAddedEvent;
import org.example.eventsourcing.domain.event.OrderCreatedEvent;
import org.example.eventsourcing.domain.event.OrderEvent;
import org.example.eventsourcing.domain.model.OrderId;
import org.example.eventsourcing.domain.model.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void shouldCreateOrderAndAddProduct() {
        // Given
        OrderId orderId = OrderId.generate();
        String customerId = "customer-123";

        // When
        Order order = Order.create(orderId, customerId);

        // Then
        assertEquals(orderId, order.getOrderId());
        assertEquals(customerId, order.getCustomerId());
        assertFalse(order.isConfirmed());
        assertTrue(order.getProducts().isEmpty());

        List<OrderEvent> initialEvents = order.getUncommittedEvents();
        assertEquals(1, initialEvents.size());
        assertInstanceOf(OrderCreatedEvent.class, initialEvents.get(0));
        OrderCreatedEvent createdEvent = (OrderCreatedEvent) initialEvents.get(0);
        assertEquals(orderId, createdEvent.getOrderId());
        assertEquals(customerId, createdEvent.getCustomerId());

        // Given a product
        Product product = new Product("prod-001", "Test Product", BigDecimal.TEN, 1);

        // When
        order.addItem(product);

        // Then
        assertEquals(1, order.getProducts().size());
        assertTrue(order.getProducts().contains(product));

        List<OrderEvent> eventsAfterAdd = order.getUncommittedEvents();
        assertEquals(1, eventsAfterAdd.size()); // Events are cleared after getUncommittedEvents
                                                 // and addItem adds a new one
        assertInstanceOf(ItemAddedEvent.class, eventsAfterAdd.get(0));
        ItemAddedEvent itemAddedEvent = (ItemAddedEvent) eventsAfterAdd.get(0);
        assertEquals(orderId, itemAddedEvent.getOrderId());
        assertEquals(product, itemAddedEvent.getProduct());

        // Verify applying the event (simulating event sourcing load)
        Order newOrderInstance = new Order();
        newOrderInstance.apply(createdEvent); // Apply the creation event
        newOrderInstance.apply(itemAddedEvent); // Apply the item added event

        assertEquals(orderId, newOrderInstance.getOrderId());
        assertEquals(customerId, newOrderInstance.getCustomerId());
        assertEquals(1, newOrderInstance.getProducts().size());
        assertEquals(product, newOrderInstance.getProducts().get(0));
        assertFalse(newOrderInstance.isConfirmed());
    }

    @Test
    void shouldThrowExceptionWhenAddingItemToConfirmedOrder() {
        // Given
        OrderId orderId = OrderId.generate();
        Order order = Order.create(orderId, "customer-123");
        Product product1 = new Product("prod-001", "Product 1", BigDecimal.ONE, 1);
        order.addItem(product1);
        order.getUncommittedEvents(); // Clear events

        order.confirm(); // Confirm the order
        order.getUncommittedEvents(); // Clear events

        // When & Then
        Product product2 = new Product("prod-002", "Product 2", BigDecimal.TEN, 1);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            order.addItem(product2);
        });
        assertEquals("Нельзя добавить товар в подтвержденный заказ", exception.getMessage());
        assertTrue(order.getUncommittedEvents().isEmpty()); // No new event should be generated
    }

    @Test
    void shouldConfirmOrder() {
        // Given
        OrderId orderId = OrderId.generate();
        Order order = Order.create(orderId, "customer-123");
        Product product = new Product("prod-001", "Test Product", BigDecimal.TEN, 1);
        order.addItem(product);
        order.getUncommittedEvents(); // Clear previous events (OrderCreated, ItemAdded)

        // When
        order.confirm();

        // Then
        assertTrue(order.isConfirmed());
        List<OrderEvent> confirmEvents = order.getUncommittedEvents();
        assertEquals(1, confirmEvents.size());
        assertInstanceOf(org.example.eventsourcing.domain.event.OrderConfirmedEvent.class, confirmEvents.get(0));
        assertEquals(orderId, confirmEvents.get(0).getOrderId());
    }

    @Test
    void shouldThrowExceptionWhenConfirmingEmptyOrder() {
        // Given
        OrderId orderId = OrderId.generate();
        Order order = Order.create(orderId, "customer-123");
        order.getUncommittedEvents(); // Clear events

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, order::confirm);
        assertEquals("Нельзя подтвердить пустой заказ", exception.getMessage());
        assertFalse(order.isConfirmed());
        assertTrue(order.getUncommittedEvents().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenConfirmingAlreadyConfirmedOrder() {
        // Given
        OrderId orderId = OrderId.generate();
        Order order = Order.create(orderId, "customer-123");
        Product product = new Product("prod-001", "Test Product", BigDecimal.TEN, 1);
        order.addItem(product);
        order.confirm();
        order.getUncommittedEvents(); // Clear events

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, order::confirm);
        assertEquals("Заказ уже подтвержден", exception.getMessage());
        assertTrue(order.getUncommittedEvents().isEmpty());
    }
}
