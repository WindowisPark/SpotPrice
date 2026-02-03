package com.spotprice.application.port.in;

import com.spotprice.application.dto.result.PaymentStatusResult;

public interface PayOrderUseCase {

    PaymentStatusResult pay(Long orderId);
}
