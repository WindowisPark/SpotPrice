package com.spotprice.infra.clock;

import com.spotprice.application.port.out.ClockPort;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class SystemUtcClock implements ClockPort {

    @Override
    public Instant now() {
        return Instant.now();
    }
}
