package com.spotprice.infra.persistence.offer;

import com.spotprice.application.port.out.OfferRepositoryPort;
import com.spotprice.domain.offer.Offer;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class OfferRepositoryAdapter implements OfferRepositoryPort {

    private final JpaOfferRepository jpaRepository;

    public OfferRepositoryAdapter(JpaOfferRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Offer> findById(Long id) {
        return jpaRepository.findById(id)
                .map(OfferMapper::toDomain);
    }

    @Override
    public Optional<Offer> findByIdForUpdate(Long id) {
        return jpaRepository.findWithLockById(id)
                .map(OfferMapper::toDomain);
    }

    @Override
    public Offer save(Offer offer) {
        OfferEntity entity = OfferMapper.toEntity(offer);
        OfferEntity saved = jpaRepository.save(entity);
        return OfferMapper.toDomain(saved);
    }
}
