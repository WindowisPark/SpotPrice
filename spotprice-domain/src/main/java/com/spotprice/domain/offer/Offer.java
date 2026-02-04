package com.spotprice.domain.offer;

import com.spotprice.domain.common.DomainEvent;
import com.spotprice.domain.common.DomainEvents;
import com.spotprice.domain.common.Money;

import java.time.Instant;
import java.util.List;

/**
 * Offer 애그리거트 루트
 * - 가격 계산에 필요한 데이터와 상태/불변식을 보유
 * - 가격 계산 자체는 PriceCalculator 도메인 서비스에서 수행
 */
public class Offer {
    private Long id;
    private OfferStatus status;
    private DecayType decayType;

    private Money basePrice;
    private Money minPrice;

    private Instant startAt;
    private Instant endAt;
    private Instant expireAt;

    // DomainEvents 필드 추가 (컴포지션)
    private final DomainEvents events = new DomainEvents();

    public Offer(Money basePrice, Money minPrice, DecayType decayType,
                 Instant startAt, Instant endAt, Instant expireAt) {

        // 1. 가격 검증
        if (minPrice.isGreaterThan(basePrice)) {
            throw new IllegalArgumentException("기본가는 최저가보다 커야 합니다.");
        }

        // 2. 시간 검증
        if (!startAt.isBefore(endAt)) {
            throw new IllegalArgumentException("시작 시간은 종료 시간보다 빨라야 합니다.");
        }

        if (endAt.isAfter(expireAt)) {
            throw new IllegalArgumentException("감쇠 종료 시간은 만료 시간보다 늦을 수 없습니다.");
        }


        this.basePrice = basePrice;
        this.minPrice = minPrice;
        this.decayType = decayType;
        this.startAt = startAt;
        this.endAt = endAt;
        this.expireAt = expireAt;
        this.status = OfferStatus.OPEN;
    }

    // --- Getters (PriceCalculator에서 사용) ---

    public Long getId() {
        return id;
    }

    public OfferStatus getStatus() {
        return status;
    }

    public DecayType getDecayType() {
        return decayType;
    }

    public Money getBasePrice() {
        return basePrice;
    }

    public Money getMinPrice() {
        return minPrice;
    }

    public Instant getStartAt() {
        return startAt;
    }

    public Instant getEndAt() {
        return endAt;
    }

    public Instant getExpireAt() {
        return expireAt;
    }

    // --- 상태 전이 메서드 ---

    /**
     * TODO: sell() 구현
     *
     * ### 검증
     * - 현재 상태가 OPEN인지 확인
     * - SOLD, EXPIRED 상태에서 호출 시 예외 (어떤 예외? OfferNotOpenException?)
     *
     * ### 상태 변경
     * - status = SOLD
     *
     * ### 이벤트 등록
     * - events.register(new OfferSoldEvent(...))
     *
     * ### 엣지 케이스
     * - 만료 시간(expireAt) 지났는데 sell() 호출하면? 허용? 거부?
     * - 동시에 두 명이 구매 시도하면? (애플리케이션 레이어에서 동시성 제어)
     */
    public void sell() {
        sell(Instant.now());
    }

    public void sell(Instant now) {
        if (status != OfferStatus.OPEN) {
            throw new IllegalStateException("판매 불가능한 상태입니다. status=" + status);
        }

        // 만료 이후 판매 거부 (도메인 불변식)
        if (!now.isBefore(expireAt)) {
            throw new IllegalStateException("만료된 Offer는 판매할 수 없습니다. expireAt=" + expireAt + ", now=" + now);
        }

        this.status = OfferStatus.SOLD;
        events.register(new OfferSoldEvent(this.id, now));
    }

    public void expire() {
        expire(Instant.now());
    }
    public void expire(Instant now) {
        if (status == OfferStatus.SOLD) {
            return; // 이미 판매된 경우 만료 처리 없음 (멱등)
        }

        if (status != OfferStatus.OPEN) {
            return; // EXPIRED 등 다른 상태도 멱등 처리
        }

        // 만료 시간이 아직 안 됐으면 아무것도 하지 않음
        if (now.isBefore(expireAt)) {
            return;
        }

        this.status = OfferStatus.EXPIRED;
        events.register(new OfferExpiredEvent(this.id, now));
    }


    /**
     * 애그리거트에 누적된 이벤트를 가져오고 비운다.
     * - Application Layer에서 저장(save) 이후 발행(publish)할 때 사용
     */
    public List<DomainEvent> pullEvents() {
        return events.getAndClear();
    }
}
