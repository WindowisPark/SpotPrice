package com.spotprice.api.web;

import com.spotprice.application.dto.PageQuery;
import com.spotprice.application.dto.PageResult;
import com.spotprice.application.dto.result.OrderResult;
import com.spotprice.application.port.in.GetMyOrdersUseCase;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebMyController {

    private final GetMyOrdersUseCase getMyOrdersUseCase;

    public WebMyController(GetMyOrdersUseCase getMyOrdersUseCase) {
        this.getMyOrdersUseCase = getMyOrdersUseCase;
    }

    @GetMapping("/my/orders")
    public String myOrders(@AuthenticationPrincipal Long userId,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "20") int size,
                           Model model) {
        PageResult<OrderResult> orders = getMyOrdersUseCase.getMyOrders(userId, PageQuery.of(page, size));
        model.addAttribute("orders", orders);
        return "my-orders";
    }
}
