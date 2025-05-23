package org.example.eventsourcing.infrastructure.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;
import org.example.eventsourcing.domain.event.OrderEvent;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Сущность, представляющая событие, сохраненное в MongoDB.
 */
@Data
@Document(collection = "events")
public class StoredEvent {
    @Id
    private String id;              // Идентификатор записи
    private String aggregateId;     // Идентификатор агрегата
    private String eventType;       // Тип события
    private int version;            // Версия события
    private String eventData;       // Данные события в формате JSON
    private Instant timestamp;      // Время создания события

    /**
     * Создает новое сохраненное событие.
     *
     * @param aggregateId идентификатор агрегата
     * @param event событие домена
     * @param eventData данные события в формате JSON
     * @param version версия события
     */
    @JsonCreator
    public StoredEvent(String aggregateId, String eventType, String eventData, int version) {
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.eventData = eventData;
        this.version = version;
        this.timestamp = Instant.now();
    }
}
