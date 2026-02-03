package com.spotprice.application.port.out;

import java.util.function.Supplier;

public interface LockManagerPort {

    <T> T executeWithLock(String lockKey, Supplier<T> action);
}
