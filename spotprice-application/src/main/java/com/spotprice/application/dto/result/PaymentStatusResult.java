package com.spotprice.application.dto.result;

public record PaymentStatusResult(
        Long orderId,
        boolean success,
        String transactionId,
        String failureReason
) {
}
