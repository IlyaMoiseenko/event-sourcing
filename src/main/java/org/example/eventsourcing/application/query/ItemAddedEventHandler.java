package org.example.eventsourcing.application.query;


import org.example.eventsourcing.domain.event.ItemAddedEvent;
import org.springframework.stereotype.Component;

/**
 * Обработчик события добавления товара.
 */
@Component
public class ItemAddedEventHandler implements EventHandler<ItemAddedEvent> {

    /**
     * Обрабатывает событие добавления товара.
     *
     * @param event событие добавления товара
     * @param view проекция заказа
     */
    @Override
    public void handle(ItemAddedEvent event, OrderView view) {
        view.getItems().add(event.getProduct());
    }
}
