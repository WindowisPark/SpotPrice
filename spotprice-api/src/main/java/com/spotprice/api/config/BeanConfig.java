package com.spotprice.api.config;

import com.spotprice.application.port.in.IssueAccessGrantUseCase;
import com.spotprice.application.port.out.AccessGrantRepositoryPort;
import com.spotprice.application.port.out.ClockPort;
import com.spotprice.application.port.out.EventPublisherPort;
import com.spotprice.application.port.out.LockManagerPort;
import com.spotprice.application.port.out.OfferRepositoryPort;
import com.spotprice.application.port.out.OrderRepositoryPort;
import com.spotprice.application.port.out.PaymentPort;
import com.spotprice.application.service.AccessGrantService;
import com.spotprice.application.service.OfferQuoteService;
import com.spotprice.application.service.OrderService;
import com.spotprice.application.service.PaymentService;
import com.spotprice.domain.offer.PriceCalculator;
import com.spotprice.infra.event.OrderPaidEventListener;
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

    @Bean
    public PaymentService paymentService(OrderRepositoryPort orderRepository,
                                         PaymentPort paymentPort,
                                         EventPublisherPort eventPublisher) {
        return new PaymentService(orderRepository, paymentPort, eventPublisher);
    }

    @Bean
    public AccessGrantService accessGrantService(OrderRepositoryPort orderRepository,
                                                  OfferRepositoryPort offerRepository,
                                                  AccessGrantRepositoryPort accessGrantRepository) {
        return new AccessGrantService(orderRepository, offerRepository, accessGrantRepository);
    }

    @Bean
    public OrderPaidEventListener orderPaidEventListener(IssueAccessGrantUseCase issueAccessGrantUseCase) {
        return new OrderPaidEventListener(issueAccessGrantUseCase);
    }
}
