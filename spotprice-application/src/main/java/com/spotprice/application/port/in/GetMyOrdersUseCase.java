package com.spotprice.application.port.in;

import com.spotprice.application.dto.PageQuery;
import com.spotprice.application.dto.PageResult;
import com.spotprice.application.dto.result.OrderResult;

public interface GetMyOrdersUseCase {

    PageResult<OrderResult> getMyOrders(Long userId, PageQuery pageQuery);
}
