package cn.fatcarter.wheel;

import java.util.concurrent.DelayQueue;

/**
 * 时间轮
 */
public interface TimingWheel {

    boolean add(TimingEntry entry);

    void pushTime(long time);

    DelayQueue<Bucket> getQueue();

    boolean cancel(TimingEntry entry);
}
