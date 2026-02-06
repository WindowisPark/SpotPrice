package com.spotprice.application.port.out;

import com.spotprice.domain.access.AccessGrant;

import java.util.Optional;

public interface AccessGrantRepositoryPort {

    AccessGrant save(AccessGrant grant);

    Optional<AccessGrant> findByOrderId(Long orderId);
}
