package kitchenpos.ordertable.application;

import kitchenpos.order.application.event.OrderCreateEvent;
import kitchenpos.order.exception.OrderException;
import kitchenpos.ordertable.domain.OrderTable;
import kitchenpos.ordertable.domain.OrderTableRepository;
import kitchenpos.ordertable.exception.OrderTableException.NotFoundOrderTableException;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OrderTableEventListener {

    private final OrderTableRepository orderTableRepository;

    public OrderTableEventListener(final OrderTableRepository orderTableRepository) {
        this.orderTableRepository = orderTableRepository;
    }

    @EventListener
    public void validateOrderTable(final OrderCreateEvent orderCreateEvent) {
        final Long orderTableId = orderCreateEvent.getOrderTableId();
        final OrderTable orderTable = orderTableRepository.findById(orderTableId)
                .orElseThrow(NotFoundOrderTableException::new);

        if (orderTable.isEmpty()) {
            throw new OrderException.CannotOrderStateByOrderTableEmptyException();
        }
    }
}