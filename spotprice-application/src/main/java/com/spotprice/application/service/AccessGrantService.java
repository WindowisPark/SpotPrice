package com.spotprice.application.service;

import com.spotprice.application.dto.result.AccessGrantResult;
import com.spotprice.application.port.in.IssueAccessGrantUseCase;
import com.spotprice.application.port.out.AccessGrantRepositoryPort;
import com.spotprice.application.port.out.OfferRepositoryPort;
import com.spotprice.application.port.out.OrderRepositoryPort;
import com.spotprice.domain.access.AccessGrant;
import com.spotprice.domain.access.GrantType;
import com.spotprice.domain.offer.Offer;
import com.spotprice.domain.order.Order;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class AccessGrantService implements IssueAccessGrantUseCase {

    private final OrderRepositoryPort orderRepository;
    private final OfferRepositoryPort offerRepository;
    private final AccessGrantRepositoryPort accessGrantRepository;

    public AccessGrantService(OrderRepositoryPort orderRepository,
                              OfferRepositoryPort offerRepository,
                              AccessGrantRepositoryPort accessGrantRepository) {
        this.orderRepository = orderRepository;
        this.offerRepository = offerRepository;
        this.accessGrantRepository = accessGrantRepository;
    }

    @Override
    @Transactional
    public AccessGrantResult issue(Long orderId) {
        // 멱등성: 이미 발급된 경우 기존 결과 반환
        Optional<AccessGrant> existing = accessGrantRepository.findByOrderId(orderId);
        if (existing.isPresent()) {
            return toResult(existing.get());
        }

        // Order → offerId 조회
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + orderId));

        // Offer → validFrom/validTo (start_at/end_at) 조회
        Offer offer = offerRepository.findById(order.getOfferId())
                .orElseThrow(() -> new NoSuchElementException("Offer not found: " + order.getOfferId()));

        // PIN 생성 및 AccessGrant 생성
        String pin = generatePin();
        AccessGrant grant = new AccessGrant(
                orderId,
                GrantType.PIN,
                pin,
                offer.getStartAt(),
                offer.getEndAt()
        );

        AccessGrant saved = accessGrantRepository.save(grant);
        return toResult(saved);
    }

    private String generatePin() {
        int pin = ThreadLocalRandom.current().nextInt(100_000, 1_000_000);
        return String.valueOf(pin);
    }

    private AccessGrantResult toResult(AccessGrant grant) {
        return new AccessGrantResult(
                grant.getOrderId(),
                grant.getGrantType().name(),
                grant.getGrantValue(),
                grant.getValidFrom(),
                grant.getValidTo(),
                grant.getStatus().name()
        );
    }
}
