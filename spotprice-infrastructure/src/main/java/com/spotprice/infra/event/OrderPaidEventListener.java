package com.spotprice.infra.event;

import com.spotprice.application.dto.event.OrderPaidEvent;
import com.spotprice.application.port.in.IssueAccessGrantUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

public class OrderPaidEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderPaidEventListener.class);

    private final IssueAccessGrantUseCase issueAccessGrantUseCase;

    public OrderPaidEventListener(IssueAccessGrantUseCase issueAccessGrantUseCase) {
        this.issueAccessGrantUseCase = issueAccessGrantUseCase;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(OrderPaidEvent event) {
        log.info("OrderPaidEvent 수신: orderId={}", event.orderId());
        try {
            issueAccessGrantUseCase.issue(event.orderId());
            log.info("AccessGrant 발급 완료: orderId={}", event.orderId());
        } catch (Exception e) {
            log.error("AccessGrant 발급 실패: orderId={}", event.orderId(), e);
        }
    }
}
