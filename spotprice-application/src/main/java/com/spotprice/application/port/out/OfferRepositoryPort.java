package com.spotprice.application.port.out;

import com.spotprice.domain.offer.Offer;

import java.util.Optional;

public interface OfferRepositoryPort {

    Optional<Offer> findById(Long id);

    Optional<Offer> findByIdForUpdate(Long id);

    Offer save(Offer offer);
}
