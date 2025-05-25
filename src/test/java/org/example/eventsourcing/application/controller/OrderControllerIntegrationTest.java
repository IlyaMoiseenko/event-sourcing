package org.example.eventsourcing.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.eventsourcing.application.command.OrderCommandHandler;
import org.example.eventsourcing.application.dto.CreateOrderRequest;
import org.example.eventsourcing.application.dto.OrderResponse;
import org.example.eventsourcing.application.dto.ProductDto;
import org.example.eventsourcing.application.mapper.OrderMapper;
import org.example.eventsourcing.application.query.OrderView;
import org.example.eventsourcing.application.query.OrderViewRepository;
import org.example.eventsourcing.domain.event.OrderCreatedEvent;
import org.example.eventsourcing.domain.event.OrderEvent;
import org.example.eventsourcing.domain.model.OrderId;
import org.example.eventsourcing.domain.model.Product;
import org.example.eventsourcing.infrastructure.event.EventStore;
import org.example.eventsourcing.infrastructure.messaging.OrderEventProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;


@WebMvcTest(OrderController.class)
@Import({OrderCommandHandler.class, OrderMapper.class}) // Import necessary components
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventStore eventStore;

    @MockBean
    private OrderEventProducer eventProducer;

    @MockBean
    private OrderViewRepository viewRepository;
    
    // OrderMapper is now imported via @Import, so no need to @MockBean if we want to test its real logic
    // @MockBean
    // private OrderMapper orderMapper;


    @Test
    void shouldCreateOrderAndRetrieveIt() throws Exception {
        // --- Part 1: Create Order ---
        String customerId = "customer-test-001";
        CreateOrderRequest createRequest = new CreateOrderRequest();
        createRequest.setCustomerId(customerId);

        // Mock EventStore interactions for OrderCommandHandler
        // 1. saveEvents (called by OrderCommandHandler after Order.create)
        doNothing().when(eventStore).saveEvents(any(OrderId.class), any(List.class));
        // 2. eventProducer.publish (also called by OrderCommandHandler)
        doNothing().when(eventProducer).publish(any(OrderId.class), any(OrderEvent.class));

        MvcResult createMvcResult = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk()) // As per controller, it returns 200 OK
                .andExpect(jsonPath("$", notNullValue()))
                .andReturn();

        String createdOrderIdValue = createMvcResult.getResponse().getContentAsString();
        assertNotNull(createdOrderIdValue);
        System.out.println("Created Order ID: " + createdOrderIdValue);


        // Verify that eventStore.saveEvents was called by OrderCommandHandler
        verify(eventStore).saveEvents(any(OrderId.class), any(List.class));
        // Verify that eventProducer.publish was called
        verify(eventProducer).publish(any(OrderId.class), any(OrderCreatedEvent.class));


        // --- Part 2: Retrieve Order ---
        OrderId createdOrderId = new OrderId(createdOrderIdValue);

        // Prepare mock OrderView for the viewRepository
        OrderView mockOrderView = new OrderView();
        mockOrderView.setOrderId(createdOrderIdValue);
        mockOrderView.setCustomerId(customerId);
        mockOrderView.setProducts(new ArrayList<>()); // Empty for newly created order
        mockOrderView.setTotalPrice(BigDecimal.ZERO);
        mockOrderView.setConfirmed(false);
        mockOrderView.setLastUpdated(Instant.now());

        when(viewRepository.findById(createdOrderIdValue)).thenReturn(Optional.of(mockOrderView));

        // When retrieving the order
        mockMvc.perform(get("/orders/{orderId}", createdOrderIdValue)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId", is(createdOrderIdValue)))
                .andExpect(jsonPath("$.customerId", is(customerId)))
                .andExpect(jsonPath("$.products.size()", is(0)))
                .andExpect(jsonPath("$.totalPrice", is(0.0))) // Assuming BigDecimal serializes to double
                .andExpect(jsonPath("$.confirmed", is(false)));

        verify(viewRepository).findById(createdOrderIdValue);
    }
    
    private void assertNotNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new AssertionError("Expected non-null/non-empty value, but got: " + value);
        }
        // Attempt to parse as UUID to ensure it's a valid format if that's the expectation
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new AssertionError("Expected UUID format for orderId, but got: " + value, e);
        }
    }
}
