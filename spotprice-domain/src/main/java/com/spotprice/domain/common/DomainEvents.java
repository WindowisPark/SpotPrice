package com.spotprice.domain.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DomainEvents {

    // TODO: 이벤트 목록 필드
    private final List<DomainEvent> events = new ArrayList<>();

    // TODO: register 메서드
    public void register(DomainEvent event){
        Objects.requireNonNull(event, "이벤트 인자값에 null이 허용되지 않습니다.");
        events.add(event);
    }
    // TODO: getAndClear 또는 get/clear 메서드

    public List<DomainEvent> getAndClear() {
        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        List<DomainEvent> copied = List.copyOf(events);
        events.clear();
        return copied;
    }
}
