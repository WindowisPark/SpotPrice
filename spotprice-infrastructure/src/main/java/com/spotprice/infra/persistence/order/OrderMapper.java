package com.spotprice.infra.persistence.order;

import com.spotprice.domain.order.IdempotencyKey;
import com.spotprice.domain.order.Order;
import com.spotprice.domain.order.OrderStatus;

public class OrderMapper {

    private OrderMapper() {
    }

    public static Order toDomain(OrderEntity entity) {
        return Order.restore(
                entity.getId(),
                entity.getUserId(),
                entity.getOfferId(),
                OrderStatus.valueOf(entity.getStatus()),
                entity.getLockedPrice(),
                new IdempotencyKey(entity.getIdempotencyKey()),
                entity.getCreatedAt()
        );
    }

    public static OrderEntity toEntity(Order order) {
        return new OrderEntity(
                order.getId(),
                order.getUserId(),
                order.getOfferId(),
                order.getStatus().name(),
                order.getLockedPrice(),
                order.getIdempotencyKey().value(),
                order.getCreatedAt()
        );
    }
}
