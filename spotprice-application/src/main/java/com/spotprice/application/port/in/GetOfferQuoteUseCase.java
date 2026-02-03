package com.spotprice.application.port.in;

import com.spotprice.application.dto.result.OfferQuoteResult;

public interface GetOfferQuoteUseCase {

    OfferQuoteResult getQuote(Long offerId);
}
