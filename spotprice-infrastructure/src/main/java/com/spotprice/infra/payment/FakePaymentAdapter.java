package com.spotprice.infra.payment;

import com.spotprice.application.port.out.PaymentPort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * MVP용 가짜 결제 어댑터 — 항상 성공 반환
 */
@Component
public class FakePaymentAdapter implements PaymentPort {

    @Override
    public PaymentResult process(Long orderId, BigDecimal amount) {
        String transactionId = "fake-txn-" + UUID.randomUUID().toString().substring(0, 8);
        return PaymentResult.success(transactionId);
    }
}
