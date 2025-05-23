package org.example.eventsourcing.infrastructure.event;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Репозиторий MongoDB для сохраненных событий.
 */
public interface EventRepository extends MongoRepository<StoredEvent, String> {

    /**
     * Находит события по идентификатору агрегата, отсортированные по времени.
     *
     * @param aggregateId идентификатор агрегата
     * @return список сохраненных событий
     */
    List<StoredEvent> findByAggregateIdOrderByTimestampAsc(String aggregateId);
}
