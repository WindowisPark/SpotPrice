package com.spotprice.infra.persistence.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaOrderRepository extends JpaRepository<OrderEntity, Long> {

    Optional<OrderEntity> findByIdempotencyKey(String idempotencyKey);
}
