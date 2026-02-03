package com.spotprice.domain.exception;

public class OfferExpiredException extends DomainException {

    public OfferExpiredException(Long offerId) {
        super("Offer expired: " + offerId);
    }
}
