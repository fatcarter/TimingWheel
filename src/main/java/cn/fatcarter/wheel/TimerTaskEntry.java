package cn.fatcarter.wheel;

import java.util.Date;

public class TimerTaskEntry implements TimingEntry {
    private final long fireTime;
    private final Runnable task;

    private boolean cancelled = false;

    public TimerTaskEntry(long timeout, Runnable task) {
        this.fireTime = System.currentTimeMillis() + timeout;
        this.task = task;
    }

    public TimerTaskEntry(Date date, Runnable task) {
        this(date.getTime(), task);
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

    @Override
    public Runnable getTask() {
        return task;
    }
}
