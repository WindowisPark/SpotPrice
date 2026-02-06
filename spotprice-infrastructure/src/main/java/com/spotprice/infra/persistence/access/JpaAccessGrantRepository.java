package com.spotprice.infra.persistence.access;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaAccessGrantRepository extends JpaRepository<AccessGrantEntity, Long> {

    Optional<AccessGrantEntity> findByOrderId(Long orderId);
}
