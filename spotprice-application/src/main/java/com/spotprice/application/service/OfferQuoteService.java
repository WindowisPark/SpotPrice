package com.spotprice.application.service;

import com.spotprice.application.dto.result.OfferQuoteResult;
import com.spotprice.application.port.in.GetOfferQuoteUseCase;
import com.spotprice.application.port.out.ClockPort;
import com.spotprice.application.port.out.OfferRepositoryPort;

/**
 * Offer 견적 조회 서비스
 * TODO: 구현
 */
public class OfferQuoteService implements GetOfferQuoteUseCase {

    private final OfferRepositoryPort offerRepository;
    private final ClockPort clock;

    public OfferQuoteService(OfferRepositoryPort offerRepository, ClockPort clock) {
        this.offerRepository = offerRepository;
        this.clock = clock;
    }

    @Override
    public OfferQuoteResult getQuote(Long offerId) {
        // TODO: 구현
        throw new UnsupportedOperationException("구현 필요");
    }
}
