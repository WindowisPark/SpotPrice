package com.spotprice.application.port.in;

import com.spotprice.application.dto.result.OrderDetailResult;

public interface GetOrderDetailUseCase {

    OrderDetailResult getOrderDetail(Long userId, Long orderId);
}
