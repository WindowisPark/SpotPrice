package com.spotprice.infra.persistence.offer;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface JpaOfferRepository extends JpaRepository<OfferEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<OfferEntity> findWithLockById(Long id);
}
