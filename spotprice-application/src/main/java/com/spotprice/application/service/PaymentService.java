package com.spotprice.application.service;

import com.spotprice.application.dto.result.PaymentStatusResult;
import com.spotprice.application.port.in.PayOrderUseCase;
import com.spotprice.application.port.out.OrderRepositoryPort;
import com.spotprice.application.port.out.PaymentPort;

/**
 * 결제 처리 서비스
 * TODO: 구현
 */
public class PaymentService implements PayOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final PaymentPort paymentPort;

    public PaymentService(OrderRepositoryPort orderRepository, PaymentPort paymentPort) {
        this.orderRepository = orderRepository;
        this.paymentPort = paymentPort;
    }

    @Override
    public PaymentStatusResult pay(Long orderId) {
        // TODO: 구현
        throw new UnsupportedOperationException("구현 필요");
    }
}
