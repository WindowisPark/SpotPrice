package com.spotprice.domain.exception;

public class OfferNotFoundException extends DomainException {

    public OfferNotFoundException(Long offerId) {
        super("Offer not found: " + offerId);
    }
}
