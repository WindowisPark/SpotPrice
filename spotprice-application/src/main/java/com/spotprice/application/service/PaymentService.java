package com.spotprice.application.service;

import com.spotprice.application.dto.event.OrderPaidEvent;
import com.spotprice.application.dto.result.PaymentStatusResult;
import com.spotprice.application.port.in.PayOrderUseCase;
import com.spotprice.application.port.out.EventPublisherPort;
import com.spotprice.application.port.out.OrderRepositoryPort;
import com.spotprice.application.port.out.PaymentPort;
import com.spotprice.application.port.out.PaymentPort.PaymentResult;
import com.spotprice.domain.exception.InvalidOrderStatusException;
import com.spotprice.domain.exception.OrderNotFoundException;
import com.spotprice.domain.order.Order;
import com.spotprice.domain.order.OrderStatus;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

public class PaymentService implements PayOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final PaymentPort paymentPort;
    private final EventPublisherPort eventPublisher;

    public PaymentService(OrderRepositoryPort orderRepository,
                          PaymentPort paymentPort,
                          EventPublisherPort eventPublisher) {
        this.orderRepository = orderRepository;
        this.paymentPort = paymentPort;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public PaymentStatusResult pay(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStatusException(order.getStatus().name());
        }

        PaymentResult result = paymentPort.process(orderId, order.getLockedPrice());

        if (result.success()) {
            order.markPaid();
        } else {
            order.markFailed();
        }
        orderRepository.save(order);

        if (result.success()) {
            eventPublisher.publish(new OrderPaidEvent(orderId, Instant.now()));
        }

        return new PaymentStatusResult(
                orderId,
                result.success(),
                result.transactionId(),
                result.failureReason()
        );
    }
}
