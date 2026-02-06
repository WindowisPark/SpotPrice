package com.spotprice.application.service;

import com.spotprice.application.dto.result.PaymentStatusResult;
import com.spotprice.application.port.in.PayOrderUseCase;
import com.spotprice.application.port.out.OrderRepositoryPort;
import com.spotprice.application.port.out.PaymentPort;
import com.spotprice.application.port.out.PaymentPort.PaymentResult;
import com.spotprice.domain.order.Order;
import com.spotprice.domain.order.OrderStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

public class PaymentService implements PayOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final PaymentPort paymentPort;

    public PaymentService(OrderRepositoryPort orderRepository, PaymentPort paymentPort) {
        this.orderRepository = orderRepository;
        this.paymentPort = paymentPort;
    }

    @Override
    @Transactional
    public PaymentStatusResult pay(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("결제 가능한 상태가 아닙니다. status=" + order.getStatus());
        }

        PaymentResult result = paymentPort.process(orderId, order.getLockedPrice());

        if (result.success()) {
            order.markPaid();
        } else {
            order.markFailed();
        }
        orderRepository.save(order);

        return new PaymentStatusResult(
                orderId,
                result.success(),
                result.transactionId(),
                result.failureReason()
        );
    }
}
