package com.spotprice.infra.persistence.offer;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface JpaOfferRepository extends JpaRepository<OfferEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<OfferEntity> findWithLockById(Long id);

    @Query("SELECT o FROM OfferEntity o WHERE o.status = 'OPEN' AND o.expireAt > :now ORDER BY o.expireAt ASC")
    Page<OfferEntity> findAllOpen(@Param("now") Instant now, Pageable pageable);
}
