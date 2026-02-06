package com.spotprice.api.offer;

import com.spotprice.application.dto.result.OfferQuoteResult;
import com.spotprice.application.port.in.GetOfferQuoteUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/offers")
public class OfferQuoteController {

    private final GetOfferQuoteUseCase getOfferQuoteUseCase;

    public OfferQuoteController(GetOfferQuoteUseCase getOfferQuoteUseCase) {
        this.getOfferQuoteUseCase = getOfferQuoteUseCase;
    }

    @GetMapping("/{offerId}/quote")
    public ResponseEntity<OfferQuoteResult> getQuote(@PathVariable Long offerId) {
        OfferQuoteResult result = getOfferQuoteUseCase.getQuote(offerId);
        return ResponseEntity.ok(result);
    }
}
