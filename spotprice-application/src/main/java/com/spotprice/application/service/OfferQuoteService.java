package com.spotprice.application.service;

import com.spotprice.application.dto.AuditEvent;
import com.spotprice.application.dto.AuditEventType;
import com.spotprice.application.dto.result.OfferQuoteResult;
import com.spotprice.application.port.in.GetOfferQuoteUseCase;
import com.spotprice.application.port.out.AuditLogPort;
import com.spotprice.application.port.out.ClockPort;
import com.spotprice.application.port.out.OfferRepositoryPort;
import com.spotprice.domain.common.Money;
import com.spotprice.domain.exception.OfferExpiredException;
import com.spotprice.domain.exception.OfferNotFoundException;
import com.spotprice.domain.exception.OfferNotOpenException;
import com.spotprice.domain.offer.Offer;
import com.spotprice.domain.offer.OfferStatus;
import com.spotprice.domain.offer.PriceCalculator;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

public class OfferQuoteService implements GetOfferQuoteUseCase {

    private final OfferRepositoryPort offerRepository;
    private final ClockPort clock;
    private final PriceCalculator priceCalculator;
    private final AuditLogPort auditLogPort;

    public OfferQuoteService(OfferRepositoryPort offerRepository, ClockPort clock,
                             PriceCalculator priceCalculator, AuditLogPort auditLogPort) {
        this.offerRepository = offerRepository;
        this.clock = clock;
        this.priceCalculator = priceCalculator;
        this.auditLogPort = auditLogPort;
    }

    @Override
    @Transactional(readOnly = true)
    public OfferQuoteResult getQuote(Long offerId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new OfferNotFoundException(offerId));

        if (offer.getStatus() != OfferStatus.OPEN) {
            throw new OfferNotOpenException(offerId);
        }

        Instant now = clock.now();

        if (!now.isBefore(offer.getExpireAt())) { // now >= expireAt
            throw new OfferExpiredException(offerId);
        }

        Money currentPrice = priceCalculator.calculate(offer, now);

        OfferQuoteResult result = new OfferQuoteResult(
                offerId,
                currentPrice.amount(),
                now,
                offer.getExpireAt()
        );

        auditLogPort.log(new AuditEvent(
                AuditEventType.OFFER_VIEW, null, "OFFER", offerId,
                Map.of("price", currentPrice.amount(), "quotedAt", now.toString()),
                now));

        return result;
    }
}
