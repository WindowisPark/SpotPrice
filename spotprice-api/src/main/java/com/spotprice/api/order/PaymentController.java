package com.spotprice.api.order;

import com.spotprice.application.dto.result.PaymentStatusResult;
import com.spotprice.application.port.in.PayOrderUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class PaymentController {

    private final PayOrderUseCase payOrderUseCase;

    public PaymentController(PayOrderUseCase payOrderUseCase) {
        this.payOrderUseCase = payOrderUseCase;
    }

    @PostMapping("/{orderId}/pay")
    public ResponseEntity<PaymentStatusResult> pay(@PathVariable Long orderId) {
        PaymentStatusResult result = payOrderUseCase.pay(orderId);
        return ResponseEntity.ok(result);
    }
}
