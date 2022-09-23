package cn.fatcarter.wheel;

import java.util.Date;
import java.util.function.Consumer;

public class TimerTaskEntry extends AbstractTimingEntry {
    private final Consumer<TimerTaskEntry> consumer;

    public TimerTaskEntry(long timeout, Consumer<TimerTaskEntry> consumer) {
        super(timeout);
        this.consumer = consumer;
    }

    public TimerTaskEntry(Date date, Consumer<TimerTaskEntry> consumer) {
        super(date);
        this.consumer = consumer;
    }

    @Override
    public void fire() {
        consumer.accept(this);
    }
}
