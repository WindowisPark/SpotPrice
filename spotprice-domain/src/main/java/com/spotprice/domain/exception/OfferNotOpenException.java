package com.spotprice.domain.exception;

public class OfferNotOpenException extends DomainException {

    public OfferNotOpenException(Long offerId) {
        super("Offer is not open: " + offerId);
    }
}
