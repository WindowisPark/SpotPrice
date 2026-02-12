package com.spotprice.api.web;

import com.spotprice.application.dto.command.CreateOrderCommand;
import com.spotprice.application.dto.result.AccessGrantResult;
import com.spotprice.application.dto.result.OrderDetailResult;
import com.spotprice.application.dto.result.OrderResult;
import com.spotprice.application.dto.result.PaymentStatusResult;
import com.spotprice.application.port.in.CreateOrderUseCase;
import com.spotprice.application.port.in.GetOrderDetailUseCase;
import com.spotprice.application.port.in.IssueAccessGrantUseCase;
import com.spotprice.application.port.in.PayOrderUseCase;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.UUID;

@Controller
public class WebOrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderDetailUseCase getOrderDetailUseCase;
    private final PayOrderUseCase payOrderUseCase;
    private final IssueAccessGrantUseCase issueAccessGrantUseCase;

    public WebOrderController(CreateOrderUseCase createOrderUseCase,
                              GetOrderDetailUseCase getOrderDetailUseCase,
                              PayOrderUseCase payOrderUseCase,
                              IssueAccessGrantUseCase issueAccessGrantUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderDetailUseCase = getOrderDetailUseCase;
        this.payOrderUseCase = payOrderUseCase;
        this.issueAccessGrantUseCase = issueAccessGrantUseCase;
    }

    @PostMapping("/orders")
    public String createOrder(@AuthenticationPrincipal Long userId,
                              @RequestParam Long offerId,
                              @RequestParam BigDecimal expectedPrice,
                              RedirectAttributes redirectAttributes) {
        try {
            OrderResult result = createOrderUseCase.createOrder(
                    new CreateOrderCommand(userId, offerId, expectedPrice, UUID.randomUUID().toString()));
            return "redirect:/orders/" + result.orderId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/offers/" + offerId;
        }
    }

    @GetMapping("/orders/{id}")
    public String orderConfirm(@AuthenticationPrincipal Long userId,
                               @PathVariable Long id,
                               Model model) {
        OrderDetailResult order = getOrderDetailUseCase.getOrderDetail(userId, id);
        model.addAttribute("order", order);
        return "order-confirm";
    }

    @PostMapping("/orders/{id}/pay")
    public String pay(@AuthenticationPrincipal Long userId,
                      @PathVariable Long id,
                      RedirectAttributes redirectAttributes) {
        PaymentStatusResult result = payOrderUseCase.pay(userId, id);
        if (result.success()) {
            return "redirect:/orders/" + id + "/result";
        } else {
            redirectAttributes.addFlashAttribute("error", "결제 실패: " + result.failureReason());
            return "redirect:/orders/" + id;
        }
    }

    @GetMapping("/orders/{id}/result")
    public String orderResult(@AuthenticationPrincipal Long userId,
                              @PathVariable Long id,
                              Model model) {
        OrderDetailResult order = getOrderDetailUseCase.getOrderDetail(userId, id);
        AccessGrantResult grant = issueAccessGrantUseCase.issue(id);
        model.addAttribute("order", order);
        model.addAttribute("grant", grant);
        return "order-result";
    }
}
