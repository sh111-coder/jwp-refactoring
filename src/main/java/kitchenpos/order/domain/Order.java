package kitchenpos.order.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import kitchenpos.menu.domain.Menu;
import kitchenpos.order.OrderStatus;
import kitchenpos.order.exception.OrderException;
import kitchenpos.ordertable.domain.OrderTable;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private OrderTable orderTable;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private LocalDateTime orderedTime;

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    private final List<OrderLineItem> orderLineItems = new ArrayList<>();

    protected Order() {
    }

    private Order(final OrderTable orderTable, final OrderStatus orderStatus,
                  final LocalDateTime orderedTime) {
        this.orderTable = orderTable;
        this.orderStatus = orderStatus;
        this.orderedTime = orderedTime;
    }

    public static Order from(final OrderTable orderTable) {
        validateOrderTable(orderTable);
        return new Order(orderTable, OrderStatus.COOKING, LocalDateTime.now());
    }

    private static void validateOrderTable(final OrderTable orderTable) {
        if (orderTable.isEmpty()) {
            throw new OrderException.CannotOrderStateByOrderTableEmptyException();
        }
    }

    public void changeStatus(final OrderStatus orderStatus) {
        validateAvailableChangeStatus();
        this.orderStatus = orderStatus;
    }

    private void validateAvailableChangeStatus() {
        if (Objects.equals(OrderStatus.COMPLETION, this.orderStatus)) {
            throw new OrderException.CannotChangeOrderStatusByCurrentOrderStatusException();
        }
    }

    public void confirmOrderLineItem(final Menu menu, final long quantity) {
        final OrderLineItem orderLineItem = new OrderLineItem(menu, quantity);
        orderLineItem.confirmOrder(this);
    }

    public Long getId() {
        return id;
    }

    public OrderTable getOrderTable() {
        return orderTable;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public LocalDateTime getOrderedTime() {
        return orderedTime;
    }

    public List<OrderLineItem> getOrderLineItems() {
        return orderLineItems;
    }
}