package ru.neoflex.dev.spring.aop_method.stuff;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CallsIncrementer {

    private AtomicInteger callsBeforeCount;
    private AtomicInteger callsAfterCount;

    public CallsIncrementer() {
        this.callsAfterCount = new AtomicInteger(0);
        this.callsBeforeCount = new AtomicInteger(0);
    }

    public void incrementCallsBefore() {
        this.callsBeforeCount.incrementAndGet();
    }

    public void incrementCallsAfter() {
        this.callsAfterCount.incrementAndGet();
    }

    public int getCallsBeforeCount() {
        return callsBeforeCount.get();
    }

    public int getCallsAfterCount() {
        return callsAfterCount.get();
    }
}
