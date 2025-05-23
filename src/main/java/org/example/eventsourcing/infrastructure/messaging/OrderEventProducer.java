package org.example.eventsourcing.infrastructure.messaging;

import org.example.eventsourcing.domain.event.OrderEvent;
import org.example.eventsourcing.domain.model.OrderId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Публикатор событий заказов в Kafka.
 */
@Component
public class OrderEventProducer {
    private final String topic;                               // Топик Kafka
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate; // Клиент Kafka

    /**
     * Создает новый публикатор событий.
     *
     * @param topic топик Kafka
     * @param kafkaTemplate клиент Kafka
     */
    public OrderEventProducer(@Value("${app.kafka.topic}") String topic, KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Публикует событие в Kafka.
     *
     * @param orderId идентификатор заказа
     * @param event событие домена
     */
    public void publish(OrderId orderId, OrderEvent event) {
        kafkaTemplate.send(topic, orderId.getValue(), event);
    }
}
