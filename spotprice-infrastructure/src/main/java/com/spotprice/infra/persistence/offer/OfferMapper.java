package com.spotprice.infra.persistence.offer;

import com.spotprice.domain.common.Money;
import com.spotprice.domain.offer.DecayType;
import com.spotprice.domain.offer.Offer;
import com.spotprice.domain.offer.OfferStatus;

public class OfferMapper {

    private OfferMapper() {
    }

    public static Offer toDomain(OfferEntity entity) {
        return Offer.restore(
                entity.getId(),
                OfferStatus.valueOf(entity.getStatus()),
                DecayType.valueOf(entity.getDecayType()),
                Money.of(entity.getBasePrice()),
                Money.of(entity.getMinPrice()),
                entity.getStartAt(),
                entity.getEndAt(),
                entity.getExpireAt()
        );
    }

    public static OfferEntity toEntity(Offer offer) {
        return new OfferEntity(
                offer.getId(),
                offer.getStatus().name(),
                offer.getDecayType().name(),
                offer.getBasePrice().amount(),
                offer.getMinPrice().amount(),
                offer.getStartAt(),
                offer.getEndAt(),
                offer.getExpireAt()
        );
    }
}
