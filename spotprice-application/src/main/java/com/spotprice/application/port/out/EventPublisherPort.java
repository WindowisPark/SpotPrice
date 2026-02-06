package com.spotprice.application.port.out;

public interface EventPublisherPort {

    void publish(Object event);
}
