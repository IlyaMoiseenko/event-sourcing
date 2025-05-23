package org.example.eventsourcing.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Value;

import java.util.UUID;

/**
 * Класс, представляющий уникальный id заказа
 */
@Value
public class OrderId {
    String value;

    /**
     * Создает объект OrderId из строки (для десериализации из строки).
     *
     * @param value строковое значение идентификатора
     * @return новый объект OrderId
     */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static OrderId of(String value) {
        return new OrderId(value);
    }

    /**
     * Создает объект OrderId из объекта JSON (для обратной совместимости).
     *
     * @param value строковое значение идентификатора
     * @return новый объект OrderId
     */
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public OrderId(@JsonProperty("value") String value) {
        this.value = value;
    }

    /**
     * Генерирует новый идентификатор заказа.
     *
     * @return новый объект OrderId
     */
    public static OrderId generate() {
        return new OrderId(UUID.randomUUID().toString());
    }

    /**
     * Возвращает строковое представление идентификатора для сериализации.
     * @return строковое значение идентификатора
     */
    @JsonValue
    public String getValue() {
        return value;
    }
}
