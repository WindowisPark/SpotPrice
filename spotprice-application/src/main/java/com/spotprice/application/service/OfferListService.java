package com.spotprice.application.service;

import com.spotprice.application.dto.PageQuery;
import com.spotprice.application.dto.PageResult;
import com.spotprice.application.dto.result.OfferSummaryResult;
import com.spotprice.application.port.in.ListOffersUseCase;
import com.spotprice.application.port.out.ClockPort;
import com.spotprice.application.port.out.OfferRepositoryPort;
import com.spotprice.domain.common.Money;
import com.spotprice.domain.offer.Offer;
import com.spotprice.domain.offer.PriceCalculator;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

public class OfferListService implements ListOffersUseCase {

    private final OfferRepositoryPort offerRepository;
    private final ClockPort clock;
    private final PriceCalculator priceCalculator;

    public OfferListService(OfferRepositoryPort offerRepository, ClockPort clock,
                            PriceCalculator priceCalculator) {
        this.offerRepository = offerRepository;
        this.clock = clock;
        this.priceCalculator = priceCalculator;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<OfferSummaryResult> listOpenOffers(PageQuery pageQuery) {
        Instant now = clock.now();

        PageResult<Offer> page = offerRepository.findAllOpen(now, pageQuery);

        List<OfferSummaryResult> content = page.content().stream()
                .map(offer -> toResult(offer, now))
                .toList();

        return new PageResult<>(content, page.page(), page.size(), page.totalElements());
    }

    private OfferSummaryResult toResult(Offer offer, Instant now) {
        Money currentPrice = priceCalculator.calculate(offer, now);
        return new OfferSummaryResult(
                offer.getId(),
                offer.getBasePrice().amount(),
                currentPrice.amount(),
                offer.getMinPrice().amount(),
                offer.getStartAt(),
                offer.getEndAt(),
                offer.getExpireAt(),
                offer.getStatus().name()
        );
    }
}
