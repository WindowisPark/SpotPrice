package com.spotprice.api.web;

import com.spotprice.application.dto.PageQuery;
import com.spotprice.application.dto.PageResult;
import com.spotprice.application.dto.result.OfferQuoteResult;
import com.spotprice.application.dto.result.OfferSummaryResult;
import com.spotprice.application.port.in.GetOfferQuoteUseCase;
import com.spotprice.application.port.in.ListOffersUseCase;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebOfferController {

    private final ListOffersUseCase listOffersUseCase;
    private final GetOfferQuoteUseCase getOfferQuoteUseCase;

    public WebOfferController(ListOffersUseCase listOffersUseCase,
                              GetOfferQuoteUseCase getOfferQuoteUseCase) {
        this.listOffersUseCase = listOffersUseCase;
        this.getOfferQuoteUseCase = getOfferQuoteUseCase;
    }

    @GetMapping("/")
    public String index(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "20") int size,
                        Model model) {
        PageResult<OfferSummaryResult> offers = listOffersUseCase.listOpenOffers(PageQuery.of(page, size));
        model.addAttribute("offers", offers);
        return "index";
    }

    @GetMapping("/offers/{id}")
    public String offerDetail(@PathVariable Long id, Model model) {
        OfferQuoteResult quote = getOfferQuoteUseCase.getQuote(id);
        model.addAttribute("quote", quote);
        return "offer-detail";
    }

    @GetMapping("/offers/{id}/price-fragment")
    public String priceFragment(@PathVariable Long id, Model model) {
        OfferQuoteResult quote = getOfferQuoteUseCase.getQuote(id);
        model.addAttribute("quote", quote);
        return "fragments/price-fragment";
    }
}
