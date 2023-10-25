package kitchenpos.order.application;

import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import kitchenpos.menu.domain.Menu;
import kitchenpos.menu.domain.MenuRepository;
import kitchenpos.menu.exception.MenuException.NotFoundMenuException;
import kitchenpos.order.OrderStatus;
import kitchenpos.order.application.dto.OrderChangeStatusRequest;
import kitchenpos.order.application.dto.OrderCreateRequest;
import kitchenpos.order.application.dto.OrderLineItemRequest;
import kitchenpos.order.application.dto.OrderResponse;
import kitchenpos.order.application.dto.OrdersResponse;
import kitchenpos.order.domain.Order;
import kitchenpos.order.domain.OrderLineItem;
import kitchenpos.order.domain.OrderRepository;
import kitchenpos.order.domain.OrderValidator;
import kitchenpos.order.exception.OrderException;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final MenuRepository menuRepository;
    private final OrderRepository orderRepository;
    private final OrderValidator orderValidator;

    public OrderService(final MenuRepository menuRepository, final OrderRepository orderRepository,
                        final OrderValidator orderValidator) {
        this.menuRepository = menuRepository;
        this.orderRepository = orderRepository;
        this.orderValidator = orderValidator;
    }

    @Transactional
    public OrderResponse create(final OrderCreateRequest request) {
        final List<OrderLineItemRequest> orderLineItemRequests = request.getOrderLineItems();
        final List<Long> menuIds = orderLineItemRequests.stream()
                .map(OrderLineItemRequest::getMenuId)
                .collect(Collectors.toList());

        final Long orderTableId = request.getOrderTableId();
        orderValidator.validateCreate(orderTableId);

        final Order order = Order.from(orderTableId, orderLineItemRequests.size(), menuRepository.countByIdIn(menuIds));
        final Order savedOrder = orderRepository.save(order);
        associateOrderLineItem(orderLineItemRequests, savedOrder);

        return OrderResponse.from(savedOrder);
    }

    private void associateOrderLineItem(final List<OrderLineItemRequest> orderLineItemRequests,
                                        final Order savedOrder) {
        for (final OrderLineItemRequest orderLineItemRequest : orderLineItemRequests) {
            final Menu menu = menuRepository.findById(orderLineItemRequest.getMenuId())
                    .orElseThrow(NotFoundMenuException::new);
            final OrderLineItem orderLineItem = new OrderLineItem(menu.getName(), menu.getPrice(),
                    orderLineItemRequest.getQuantity());
            orderLineItem.confirmOrder(savedOrder);
        }
    }

    public OrdersResponse list() {
        return OrdersResponse.from(orderRepository.findAll());
    }

    @Transactional
    public OrderResponse changeOrderStatus(final Long orderId, final OrderChangeStatusRequest request) {
        final Order savedOrder = orderRepository.findById(orderId)
                .orElseThrow(OrderException.NotFoundOrderException::new);

        savedOrder.changeStatus(OrderStatus.valueOf(request.getOrderStatus()));
        return OrderResponse.from(savedOrder);
    }
}
