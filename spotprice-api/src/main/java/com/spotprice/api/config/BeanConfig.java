package com.spotprice.api.config;

import com.spotprice.application.port.in.IssueAccessGrantUseCase;
import com.spotprice.application.port.out.AccessGrantRepositoryPort;
import com.spotprice.application.port.out.AuditLogPort;
import com.spotprice.application.port.out.ClockPort;
import com.spotprice.application.port.out.EventPublisherPort;
import com.spotprice.application.port.out.LockManagerPort;
import com.spotprice.application.port.out.OfferRepositoryPort;
import com.spotprice.application.port.out.OrderRepositoryPort;
import com.spotprice.application.port.out.PasswordEncoderPort;
import com.spotprice.application.port.out.PaymentPort;
import com.spotprice.application.port.out.UserRepositoryPort;
import com.spotprice.application.service.AccessGrantService;
import com.spotprice.application.service.AuthService;
import com.spotprice.application.service.OfferListService;
import com.spotprice.application.service.OfferQuoteService;
import com.spotprice.application.service.OrderQueryService;
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
                                               PriceCalculator priceCalculator,
                                               AuditLogPort auditLogPort) {
        return new OfferQuoteService(offerRepository, clock, priceCalculator, auditLogPort);
    }

    @Bean
    public OfferListService offerListService(OfferRepositoryPort offerRepository,
                                             ClockPort clock,
                                             PriceCalculator priceCalculator) {
        return new OfferListService(offerRepository, clock, priceCalculator);
    }

    @Bean
    public AuthService authService(UserRepositoryPort userRepository,
                                   PasswordEncoderPort passwordEncoder,
                                   ClockPort clock) {
        return new AuthService(userRepository, passwordEncoder, clock);
    }

    @Bean
    public OrderService orderService(OfferRepositoryPort offerRepository,
                                     OrderRepositoryPort orderRepository,
                                     LockManagerPort lockManager,
                                     ClockPort clock,
                                     PriceCalculator priceCalculator,
                                     AuditLogPort auditLogPort) {
        return new OrderService(offerRepository, orderRepository, lockManager, clock, priceCalculator, auditLogPort);
    }

    @Bean
    public PaymentService paymentService(OrderRepositoryPort orderRepository,
                                         PaymentPort paymentPort,
                                         EventPublisherPort eventPublisher,
                                         AuditLogPort auditLogPort,
                                         ClockPort clock) {
        return new PaymentService(orderRepository, paymentPort, eventPublisher, auditLogPort, clock);
    }

    @Bean
    public AccessGrantService accessGrantService(OrderRepositoryPort orderRepository,
                                                  OfferRepositoryPort offerRepository,
                                                  AccessGrantRepositoryPort accessGrantRepository) {
        return new AccessGrantService(orderRepository, offerRepository, accessGrantRepository);
    }

    @Bean
    public OrderQueryService orderQueryService(OrderRepositoryPort orderRepository) {
        return new OrderQueryService(orderRepository);
    }

    @Bean
    public OrderPaidEventListener orderPaidEventListener(IssueAccessGrantUseCase issueAccessGrantUseCase) {
        return new OrderPaidEventListener(issueAccessGrantUseCase);
    }
}
