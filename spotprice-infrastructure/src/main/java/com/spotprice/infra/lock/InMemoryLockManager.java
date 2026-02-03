package com.spotprice.infra.lock;

import com.spotprice.application.port.out.LockManagerPort;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * 인메모리 락 매니저 (단일 인스턴스용)
 * 분산 환경에서는 Redis/DB 기반 락으로 교체 필요
 */
@Component
public class InMemoryLockManager implements LockManagerPort {

    private final ConcurrentHashMap<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    @Override
    public <T> T executeWithLock(String lockKey, Supplier<T> action) {
        ReentrantLock lock = locks.computeIfAbsent(lockKey, k -> new ReentrantLock());
        lock.lock();
        try {
            return action.get();
        } finally {
            lock.unlock();
        }
    }
}
