package com.spotprice.api.order;

import com.spotprice.application.dto.command.CreateOrderCommand;
import com.spotprice.application.dto.result.OrderResult;
import com.spotprice.application.port.in.CreateOrderUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;

    public OrderController(CreateOrderUseCase createOrderUseCase) {
        this.createOrderUseCase = createOrderUseCase;
    }

    @PostMapping
    public ResponseEntity<OrderResult> createOrder(@RequestBody CreateOrderRequest request) {
        CreateOrderCommand command = new CreateOrderCommand(
                request.offerId(),
                request.expectedPrice(),
                request.idempotencyKey()
        );

        OrderResult result = createOrderUseCase.createOrder(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
