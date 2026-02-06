package com.spotprice.api.config;

import com.spotprice.application.port.out.ClockPort;
import com.spotprice.application.port.out.OfferRepositoryPort;
import com.spotprice.application.service.OfferQuoteService;
import com.spotprice.domain.offer.PriceCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public PriceCalculator priceCalculator() {
        return new PriceCalculator();
    }

    @Bean
    public OfferQuoteService offerQuoteService(OfferRepositoryPort offerRepository,
                                               ClockPort clock,
                                               PriceCalculator priceCalculator) {
        return new OfferQuoteService(offerRepository, clock, priceCalculator);
    }
}
