package com.spotprice.infra.persistence.order;

import com.spotprice.application.port.out.OrderRepositoryPort;
import com.spotprice.domain.order.IdempotencyKey;
import com.spotprice.domain.order.Order;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final JpaOrderRepository jpaRepository;

    public OrderRepositoryAdapter(JpaOrderRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Order> findById(Long id) {
        return jpaRepository.findById(id)
                .map(OrderMapper::toDomain);
    }

    @Override
    public Optional<Order> findByIdempotencyKey(IdempotencyKey key) {
        return jpaRepository.findByIdempotencyKey(key.value())
                .map(OrderMapper::toDomain);
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = OrderMapper.toEntity(order);
        OrderEntity saved = jpaRepository.save(entity);
        return OrderMapper.toDomain(saved);
    }
}
