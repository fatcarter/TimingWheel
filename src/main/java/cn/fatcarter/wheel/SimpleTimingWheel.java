package com.fatcarter.wheel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.DelayQueue;

public class SimpleTimingWheel implements TimingWheel {
    public static final CopyOnWriteArrayList<Bucket> BUCKETS = new CopyOnWriteArrayList<>();
    private final int level;
    private final int size;
    private final long tickMs;
    private final DelayQueue<Bucket> queue;
    private final long interval;
    private final Bucket[] buckets;
    private long now;
    private SimpleTimingWheel overflowWheel;

    public SimpleTimingWheel(int size, long tickMs, DelayQueue<Bucket> queue) {
        this(size, tickMs, System.currentTimeMillis(), queue, 0);
    }

    private SimpleTimingWheel(int size, long tickMs, long startMs, DelayQueue<Bucket> queue, int level) {
        this.size = size;
        this.tickMs = tickMs;
        this.queue = queue;
        this.level = level;
        this.interval = (long) size * tickMs;
        this.buckets = new Bucket[size];
        for (int i = 0; i < size; i++) {
            buckets[i] = new SimpleBucket(level + "_" + i);
        }
        startMs = startMs - (startMs % tickMs);
        this.now = startMs;
    }


    @Override
    public boolean add(TimingEntry entry) {
        long expiration = entry.getFireTime();
        if (entry.isCancelled()) {
            return false;
        } else if (expiration < now + tickMs) {
            return false;
        }
        if (expiration > now + interval) {
            if (overflowWheel == null) overflowWheel = this.createOverflowWheel();
            return overflowWheel.add(entry);
        } else {
            // 对齐tick
            long id = expiration / tickMs;
            long time = id * tickMs;

            // 获取所在slot索引
            int index = (int) (id % size);
            Bucket bucket = buckets[index];
            bucket.addEntry(entry);
            if (bucket.setExpiration(time)) {
                BUCKETS.add(bucket);
                boolean ok = queue.offer(bucket);
                if (!ok) {
                    bucket.flush(null);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void pushTime(long time) {
        if(time >= now + tickMs){
            this.now = time - (time % tickMs);
            if (overflowWheel != null) overflowWheel.pushTime(time);
        }
    }

    public DelayQueue<Bucket> getQueue() {
        return this.queue;
    }

    protected SimpleTimingWheel createOverflowWheel() {
        return new SimpleTimingWheel(size, interval, now, queue, level + 1);
    }

}
