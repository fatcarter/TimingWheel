package cn.fatcarter.wheel;

import java.util.Date;

public abstract class AbstractTimingEntry implements TimingEntry{
    private final long fireTime;

    private boolean cancelled = false;

    public AbstractTimingEntry(long timeout) {
        this.fireTime = System.currentTimeMillis() + timeout;
    }


    public AbstractTimingEntry(Date date) {
        this(date.getTime());
    }

    @Override
    public long getFireTime() {
        return fireTime;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void cancel() {
        this.cancelled = true;
    }
}
