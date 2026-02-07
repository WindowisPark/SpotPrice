package com.spotprice.api.order;

import com.spotprice.api.dto.ApiResponse;
import com.spotprice.application.dto.result.PaymentStatusResult;
import com.spotprice.application.port.in.PayOrderUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<ApiResponse<PaymentStatusResult>> pay(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long orderId) {
        PaymentStatusResult result = payOrderUseCase.pay(userId, orderId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
