package com.spotprice.domain.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DomainEvents {

    private final List<DomainEvent> events = new ArrayList<>();

    public void register(DomainEvent event){
        Objects.requireNonNull(event, "이벤트 인자값에 null이 허용되지 않습니다.");
        events.add(event);
    }

    public List<DomainEvent> getAndClear() {
        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        List<DomainEvent> copied = List.copyOf(events);
        events.clear();
        return copied;
    }
}
