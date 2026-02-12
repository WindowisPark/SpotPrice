package com.spotprice.application.service;

import com.spotprice.application.dto.AuditEvent;
import com.spotprice.application.dto.AuditEventType;
import com.spotprice.application.dto.event.OrderPaidEvent;
import com.spotprice.application.dto.result.PaymentStatusResult;
import com.spotprice.application.port.in.PayOrderUseCase;
import com.spotprice.application.port.out.AuditLogPort;
import com.spotprice.application.port.out.ClockPort;
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
import java.util.Map;

public class PaymentService implements PayOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final PaymentPort paymentPort;
    private final EventPublisherPort eventPublisher;
    private final AuditLogPort auditLogPort;
    private final ClockPort clock;

    public PaymentService(OrderRepositoryPort orderRepository,
                          PaymentPort paymentPort,
                          EventPublisherPort eventPublisher,
                          AuditLogPort auditLogPort,
                          ClockPort clock) {
        this.orderRepository = orderRepository;
        this.paymentPort = paymentPort;
        this.eventPublisher = eventPublisher;
        this.auditLogPort = auditLogPort;
        this.clock = clock;
    }

    @Override
    @Transactional
    public PaymentStatusResult pay(Long userId, Long orderId) {
        Instant now = clock.now();

        auditLogPort.log(new AuditEvent(
                AuditEventType.PAY_ATTEMPT, userId, "ORDER", orderId,
                Map.of(), now));

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
            eventPublisher.publish(new OrderPaidEvent(orderId, now));
            auditLogPort.log(new AuditEvent(
                    AuditEventType.PAY_SUCCESS, userId, "ORDER", orderId,
                    Map.of("lockedPrice", order.getLockedPrice()), now));
        } else {
            auditLogPort.log(new AuditEvent(
                    AuditEventType.PAY_FAIL, userId, "ORDER", orderId,
                    Map.of("reason", result.failureReason()), now));
        }

        return new PaymentStatusResult(
                orderId,
                result.success(),
                result.transactionId(),
                result.failureReason()
        );
    }
}
