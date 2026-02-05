package com.spotprice.domain.offer;

import com.spotprice.domain.common.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;

/**
 * 시간에 따른 가격 계산 도메인 서비스
 */
public class PriceCalculator {

    public Money calculate(Offer offer, Instant at) {
        return switch (offer.getDecayType()) {
            case NONE -> offer.getBasePrice();
            case LINEAR -> calculateLinear(offer, at);
            case EXPONENTIAL -> throw new UnsupportedOperationException("EXPONENTIAL 미구현");
        };
    }

    private Money calculateLinear(Offer offer, Instant at) {
        BigDecimal progress = calculateProgress(offer, at);

        // price = basePrice - (basePrice - minPrice) * progress
        BigDecimal base = offer.getBasePrice().amount();
        BigDecimal min = offer.getMinPrice().amount();
        BigDecimal diff = base.subtract(min);

        BigDecimal price = base.subtract(diff.multiply(progress));

        return Money.of(price);
    }

    /**
     * 시간 진행률 계산 (0.0 ~ 1.0 clamp)
     */
    private BigDecimal calculateProgress(Offer offer, Instant at) {
        Instant start = offer.getStartAt();
        Instant end = offer.getEndAt();

        // 경계값 처리
        if (!at.isAfter(start)) {
            return BigDecimal.ZERO;
        }
        if (!at.isBefore(end)) {
            return BigDecimal.ONE;
        }

        // progress = (at - start) / (end - start)
        long elapsed = Duration.between(start, at).toMillis();
        long total = Duration.between(start, end).toMillis();

        return BigDecimal.valueOf(elapsed)
                .divide(BigDecimal.valueOf(total), 10, RoundingMode.HALF_UP);
    }
}
