package com.spotprice.infra.persistence.order;

import com.spotprice.application.dto.PageQuery;
import com.spotprice.application.dto.PageResult;
import com.spotprice.application.port.out.OrderRepositoryPort;
import com.spotprice.domain.order.IdempotencyKey;
import com.spotprice.domain.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public Optional<Order> findByIdAndUserId(Long id, Long userId) {
        return jpaRepository.findByIdAndUserId(id, userId)
                .map(OrderMapper::toDomain);
    }

    @Override
    public Optional<Order> findByIdempotencyKey(IdempotencyKey key) {
        return jpaRepository.findByIdempotencyKey(key.value())
                .map(OrderMapper::toDomain);
    }

    @Override
    public PageResult<Order> findByUserId(Long userId, PageQuery pageQuery) {
        Page<OrderEntity> page = jpaRepository.findByUserIdOrderByCreatedAtDesc(
                userId, PageRequest.of(pageQuery.page(), pageQuery.size()));
        return new PageResult<>(
                page.getContent().stream().map(OrderMapper::toDomain).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = OrderMapper.toEntity(order);
        OrderEntity saved = jpaRepository.save(entity);
        return OrderMapper.toDomain(saved);
    }
}
