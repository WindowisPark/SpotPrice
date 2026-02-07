package com.spotprice.api.order;

import com.spotprice.api.dto.ApiResponse;
import com.spotprice.application.dto.PageQuery;
import com.spotprice.application.dto.PageResult;
import com.spotprice.application.dto.result.OrderDetailResult;
import com.spotprice.application.dto.result.OrderResult;
import com.spotprice.application.port.in.GetMyOrdersUseCase;
import com.spotprice.application.port.in.GetOrderDetailUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderQueryController {

    private final GetMyOrdersUseCase getMyOrdersUseCase;
    private final GetOrderDetailUseCase getOrderDetailUseCase;

    public OrderQueryController(GetMyOrdersUseCase getMyOrdersUseCase,
                                GetOrderDetailUseCase getOrderDetailUseCase) {
        this.getMyOrdersUseCase = getMyOrdersUseCase;
        this.getOrderDetailUseCase = getOrderDetailUseCase;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResult<OrderResult>>> getMyOrders(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageResult<OrderResult> result = getMyOrdersUseCase.getMyOrders(userId, PageQuery.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDetailResult>> getOrderDetail(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long orderId) {
        OrderDetailResult result = getOrderDetailUseCase.getOrderDetail(userId, orderId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
