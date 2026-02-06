package com.spotprice.infra.persistence.access;

import com.spotprice.application.port.out.AccessGrantRepositoryPort;
import com.spotprice.domain.access.AccessGrant;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AccessGrantRepositoryAdapter implements AccessGrantRepositoryPort {

    private final JpaAccessGrantRepository jpaRepository;

    public AccessGrantRepositoryAdapter(JpaAccessGrantRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public AccessGrant save(AccessGrant grant) {
        AccessGrantEntity entity = AccessGrantMapper.toEntity(grant);
        AccessGrantEntity saved = jpaRepository.save(entity);
        return AccessGrantMapper.toDomain(saved);
    }

    @Override
    public Optional<AccessGrant> findByOrderId(Long orderId) {
        return jpaRepository.findByOrderId(orderId)
                .map(AccessGrantMapper::toDomain);
    }
}
