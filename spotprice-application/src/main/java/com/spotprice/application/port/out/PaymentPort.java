package com.spotprice.application.port.out;

import java.math.BigDecimal;

public interface PaymentPort {

    PaymentResult process(Long orderId, BigDecimal amount);

    record PaymentResult(boolean success, String transactionId, String failureReason) {
        public static PaymentResult success(String transactionId) {
            return new PaymentResult(true, transactionId, null);
        }

        public static PaymentResult failure(String reason) {
            return new PaymentResult(false, null, reason);
        }
    }
}
