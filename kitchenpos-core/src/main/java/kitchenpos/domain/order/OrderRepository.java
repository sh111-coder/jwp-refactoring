package kitchenpos.domain.order;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    boolean existsByOrderTableIdAndOrderStatusIn(final Long orderTableId, final List<OrderStatus> orderStatus);
    boolean existsByOrderTableIdInAndOrderStatusIn(final List<Long> orderTableIds, final List<OrderStatus> orderStatus);
}