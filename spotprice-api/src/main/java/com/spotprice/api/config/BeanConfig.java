package com.spotprice.api.config;

import com.spotprice.application.port.out.ClockPort;
import com.spotprice.application.port.out.LockManagerPort;
import com.spotprice.application.port.out.OfferRepositoryPort;
import com.spotprice.application.port.out.OrderRepositoryPort;
import com.spotprice.application.service.OfferQuoteService;
import com.spotprice.application.service.OrderService;
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

    @Bean
    public OrderService orderService(OfferRepositoryPort offerRepository,
                                     OrderRepositoryPort orderRepository,
                                     LockManagerPort lockManager,
                                     ClockPort clock,
                                     PriceCalculator priceCalculator) {
        return new OrderService(offerRepository, orderRepository, lockManager, clock, priceCalculator);
    }
}
