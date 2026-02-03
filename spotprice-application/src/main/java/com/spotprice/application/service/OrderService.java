package com.spotprice.application.service;

import com.spotprice.application.dto.command.CreateOrderCommand;
import com.spotprice.application.dto.result.OrderResult;
import com.spotprice.application.port.in.CreateOrderUseCase;
import com.spotprice.application.port.out.ClockPort;
import com.spotprice.application.port.out.LockManagerPort;
import com.spotprice.application.port.out.OfferRepositoryPort;
import com.spotprice.application.port.out.OrderRepositoryPort;

/**
 * 주문 생성 서비스
 * TODO: 구현 - 동시성 처리, 멱등성 보장, 가격 검증
 */
public class OrderService implements CreateOrderUseCase {

    private final OfferRepositoryPort offerRepository;
    private final OrderRepositoryPort orderRepository;
    private final LockManagerPort lockManager;
    private final ClockPort clock;

    public OrderService(
            OfferRepositoryPort offerRepository,
            OrderRepositoryPort orderRepository,
            LockManagerPort lockManager,
            ClockPort clock) {
        this.offerRepository = offerRepository;
        this.orderRepository = orderRepository;
        this.lockManager = lockManager;
        this.clock = clock;
    }

    @Override
    public OrderResult createOrder(CreateOrderCommand command) {
        // TODO: 구현
        throw new UnsupportedOperationException("구현 필요");
    }
}
