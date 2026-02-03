package com.spotprice.application.port.out;

import com.spotprice.domain.order.IdempotencyKey;
import com.spotprice.domain.order.Order;

import java.util.Optional;

public interface OrderRepositoryPort {

    Optional<Order> findById(Long id);

    Optional<Order> findByIdempotencyKey(IdempotencyKey key);

    Order save(Order order);
}
