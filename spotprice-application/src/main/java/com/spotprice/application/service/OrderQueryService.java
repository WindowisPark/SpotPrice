package com.spotprice.application.service;

import com.spotprice.application.dto.PageQuery;
import com.spotprice.application.dto.PageResult;
import com.spotprice.application.dto.result.OrderDetailResult;
import com.spotprice.application.dto.result.OrderResult;
import com.spotprice.application.port.in.GetMyOrdersUseCase;
import com.spotprice.application.port.in.GetOrderDetailUseCase;
import com.spotprice.application.port.out.OrderRepositoryPort;
import com.spotprice.domain.exception.OrderNotFoundException;
import com.spotprice.domain.order.Order;
import org.springframework.transaction.annotation.Transactional;

public class OrderQueryService implements GetMyOrdersUseCase, GetOrderDetailUseCase {

    private final OrderRepositoryPort orderRepository;

    public OrderQueryService(OrderRepositoryPort orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<OrderResult> getMyOrders(Long userId, PageQuery pageQuery) {
        PageResult<Order> page = orderRepository.findByUserId(userId, pageQuery);
        return new PageResult<>(
                page.content().stream()
                        .map(this::toOrderResult)
                        .toList(),
                page.page(),
                page.size(),
                page.totalElements()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailResult getOrderDetail(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        return new OrderDetailResult(
                order.getId(),
                order.getOfferId(),
                order.getLockedPrice(),
                order.getStatus().name(),
                order.getCreatedAt()
        );
    }

    private OrderResult toOrderResult(Order order) {
        return new OrderResult(
                order.getId(),
                order.getOfferId(),
                order.getLockedPrice(),
                order.getStatus().name()
        );
    }
}
