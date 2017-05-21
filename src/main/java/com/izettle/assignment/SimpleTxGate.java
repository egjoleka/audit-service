package com.izettle.assignment;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;


public class SimpleTxGate {

    private final AtomicLong iTotalTxs = new AtomicLong();
    private final AtomicLong iActiveTxs = new AtomicLong();
    private final AtomicBoolean iGateOpen = new AtomicBoolean(true);

    public void registerTx() {
        iTotalTxs.incrementAndGet();
        iActiveTxs.incrementAndGet();
    }

    public long unRegisterTx() {
        iActiveTxs.decrementAndGet();
        return iTotalTxs.get();
    }

    public long getTotalTxs() {
        return iTotalTxs.get();
    }

    public long getActiveTxs() {
        return iActiveTxs.get();
    }

    public void openGate() {
        iGateOpen.set(true);
    }

    public void closeGate() {
        iGateOpen.set(false);
    }

    public boolean isOpen() {
        return iGateOpen.get();
    }

    public long totalInvocations() {
        return getTotalTxs();
    }
}
