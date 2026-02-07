package com.spotprice.application.service;

import com.spotprice.application.dto.command.CreateOrderCommand;
import com.spotprice.application.dto.result.OrderResult;
import com.spotprice.application.port.in.CreateOrderUseCase;
import com.spotprice.application.port.out.ClockPort;
import com.spotprice.application.port.out.LockManagerPort;
import com.spotprice.application.port.out.OfferRepositoryPort;
import com.spotprice.application.port.out.OrderRepositoryPort;
import com.spotprice.domain.common.Money;
import com.spotprice.domain.exception.OfferExpiredException;
import com.spotprice.domain.exception.OfferNotFoundException;
import com.spotprice.domain.exception.OfferNotOpenException;
import com.spotprice.domain.exception.PriceMismatchException;
import com.spotprice.domain.offer.Offer;
import com.spotprice.domain.offer.OfferStatus;
import com.spotprice.domain.offer.PriceCalculator;
import com.spotprice.domain.order.IdempotencyKey;
import com.spotprice.domain.order.Order;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

public class OrderService implements CreateOrderUseCase {

    private final OfferRepositoryPort offerRepository;
    private final OrderRepositoryPort orderRepository;
    private final LockManagerPort lockManager;
    private final ClockPort clock;
    private final PriceCalculator priceCalculator;

    public OrderService(OfferRepositoryPort offerRepository,
                        OrderRepositoryPort orderRepository,
                        LockManagerPort lockManager,
                        ClockPort clock,
                        PriceCalculator priceCalculator) {
        this.offerRepository = offerRepository;
        this.orderRepository = orderRepository;
        this.lockManager = lockManager;
        this.clock = clock;
        this.priceCalculator = priceCalculator;
    }

    @Override
    @Transactional
    public OrderResult createOrder(CreateOrderCommand command) {
        IdempotencyKey key = new IdempotencyKey(command.idempotencyKey());

        // 1. 멱등성 체크 — 같은 키로 이미 주문이 있으면 그대로 반환
        Optional<Order> existing = orderRepository.findByIdempotencyKey(key);
        if (existing.isPresent()) {
            return toResult(existing.get());
        }

        // 2. Offer 단위 락 획득 후 주문 처리
        return lockManager.executeWithLock("offer:" + command.offerId(), () -> {
            Instant now = clock.now();

            // 3. Offer 조회 (비관적 락)
            Offer offer = offerRepository.findByIdForUpdate(command.offerId())
                    .orElseThrow(() -> new OfferNotFoundException(command.offerId()));

            // 4. Offer 상태 검증
            if (offer.getStatus() != OfferStatus.OPEN) {
                throw new OfferNotOpenException(command.offerId());
            }

            if (!now.isBefore(offer.getExpireAt())) {
                throw new OfferExpiredException(command.offerId());
            }

            // 5. 가격 검증 — 서버 가격 > 클라이언트 기대 가격이면 거부
            Money serverPrice = priceCalculator.calculate(offer, now);
            if (serverPrice.amount().compareTo(command.expectedPrice()) > 0) {
                throw new PriceMismatchException(command.expectedPrice(), serverPrice.amount());
            }

            // 6. Offer 판매 처리
            offer.sell(now);
            offerRepository.save(offer);

            // 7. 주문 생성
            Order order = new Order(command.userId(), offer.getId(), serverPrice.amount(), key, now);
            Order saved = orderRepository.save(order);

            return toResult(saved);
        });
    }

    private OrderResult toResult(Order order) {
        return new OrderResult(
                order.getId(),
                order.getOfferId(),
                order.getLockedPrice(),
                order.getStatus().name()
        );
    }
}
