package com.spotprice.application.port.in;

import com.spotprice.application.dto.command.CreateOrderCommand;
import com.spotprice.application.dto.result.OrderResult;

public interface CreateOrderUseCase {

    OrderResult createOrder(CreateOrderCommand command);
}
