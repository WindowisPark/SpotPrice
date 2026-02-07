package com.spotprice.infra.persistence.offer;

import com.spotprice.application.dto.PageQuery;
import com.spotprice.application.dto.PageResult;
import com.spotprice.application.port.out.OfferRepositoryPort;
import com.spotprice.domain.offer.Offer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.Instant;
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
    public PageResult<Offer> findAllOpen(Instant now, PageQuery pageQuery) {
        Page<OfferEntity> page = jpaRepository.findAllOpen(now, PageRequest.of(pageQuery.page(), pageQuery.size()));
        return new PageResult<>(
                page.getContent().stream().map(OfferMapper::toDomain).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }

    @Override
    public Offer save(Offer offer) {
        OfferEntity entity = OfferMapper.toEntity(offer);
        OfferEntity saved = jpaRepository.save(entity);
        return OfferMapper.toDomain(saved);
    }
}
