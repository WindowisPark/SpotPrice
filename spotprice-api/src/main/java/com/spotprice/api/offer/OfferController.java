package com.spotprice.api.offer;

import com.spotprice.api.dto.ApiResponse;
import com.spotprice.application.dto.PageQuery;
import com.spotprice.application.dto.PageResult;
import com.spotprice.application.dto.result.OfferQuoteResult;
import com.spotprice.application.dto.result.OfferSummaryResult;
import com.spotprice.application.port.in.GetOfferQuoteUseCase;
import com.spotprice.application.port.in.ListOffersUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/offers")
public class OfferController {

    private final GetOfferQuoteUseCase getOfferQuoteUseCase;
    private final ListOffersUseCase listOffersUseCase;

    public OfferController(GetOfferQuoteUseCase getOfferQuoteUseCase,
                           ListOffersUseCase listOffersUseCase) {
        this.getOfferQuoteUseCase = getOfferQuoteUseCase;
        this.listOffersUseCase = listOffersUseCase;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResult<OfferSummaryResult>>> listOffers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResult<OfferSummaryResult> result = listOffersUseCase.listOpenOffers(PageQuery.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{offerId}/quote")
    public ResponseEntity<ApiResponse<OfferQuoteResult>> getQuote(@PathVariable Long offerId) {
        OfferQuoteResult result = getOfferQuoteUseCase.getQuote(offerId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
