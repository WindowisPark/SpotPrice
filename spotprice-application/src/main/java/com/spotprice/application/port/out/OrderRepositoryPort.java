package com.spotprice.application.port.out;

import com.spotprice.application.dto.PageQuery;
import com.spotprice.application.dto.PageResult;
import com.spotprice.domain.order.IdempotencyKey;
import com.spotprice.domain.order.Order;

import java.util.Optional;

public interface OrderRepositoryPort {

    Optional<Order> findById(Long id);

    Optional<Order> findByIdAndUserId(Long id, Long userId);

    Optional<Order> findByIdempotencyKey(IdempotencyKey key);

    PageResult<Order> findByUserId(Long userId, PageQuery pageQuery);

    Order save(Order order);
}
