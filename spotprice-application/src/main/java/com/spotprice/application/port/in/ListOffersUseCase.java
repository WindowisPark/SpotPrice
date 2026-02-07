package com.spotprice.application.port.in;

import com.spotprice.application.dto.PageQuery;
import com.spotprice.application.dto.PageResult;
import com.spotprice.application.dto.result.OfferSummaryResult;

public interface ListOffersUseCase {

    PageResult<OfferSummaryResult> listOpenOffers(PageQuery pageQuery);
}
