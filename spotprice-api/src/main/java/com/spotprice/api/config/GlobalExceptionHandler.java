package com.spotprice.api.config;

import com.spotprice.api.dto.ApiResponse;
import com.spotprice.api.dto.ErrorCode;
import com.spotprice.domain.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OfferNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleOfferNotFound(OfferNotFoundException ex) {
        return toResponse(ErrorCode.OFFER_NOT_FOUND);
    }

    @ExceptionHandler(OfferExpiredException.class)
    public ResponseEntity<ApiResponse<Void>> handleExpired(OfferExpiredException ex) {
        return toResponse(ErrorCode.OFFER_EXPIRED);
    }

    @ExceptionHandler(OfferNotOpenException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotOpen(OfferNotOpenException ex) {
        return toResponse(ErrorCode.OFFER_ALREADY_SOLD);
    }

    @ExceptionHandler(PriceMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handlePriceMismatch(PriceMismatchException ex) {
        ErrorCode code = ErrorCode.PRICE_INCREASED;
        ApiResponse<Void> body = ApiResponse.error(code, Map.of("serverPrice", ex.getServerPrice()));
        return ResponseEntity.status(code.getHttpStatus()).body(body);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleOrderNotFound(OrderNotFoundException ex) {
        return toResponse(ErrorCode.ORDER_NOT_FOUND);
    }

    @ExceptionHandler(InvalidOrderStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidOrderStatus(InvalidOrderStatusException ex) {
        return toResponse(ErrorCode.INVALID_ORDER_STATUS);
    }

    private ResponseEntity<ApiResponse<Void>> toResponse(ErrorCode code) {
        return ResponseEntity.status(code.getHttpStatus()).body(ApiResponse.error(code));
    }
}
