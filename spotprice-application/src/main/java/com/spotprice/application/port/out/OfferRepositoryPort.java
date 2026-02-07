package com.spotprice.application.port.out;

import com.spotprice.application.dto.PageQuery;
import com.spotprice.application.dto.PageResult;
import com.spotprice.domain.offer.Offer;

import java.time.Instant;
import java.util.Optional;

public interface OfferRepositoryPort {

    Optional<Offer> findById(Long id);

    Optional<Offer> findByIdForUpdate(Long id);

    PageResult<Offer> findAllOpen(Instant now, PageQuery pageQuery);

    Offer save(Offer offer);
}
