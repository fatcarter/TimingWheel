package cn.fatcarter.wheel;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TimingWheelTimer implements Propeller {
    private static final AtomicInteger id = new AtomicInteger(0);
    private final ExecutorService executorService;
    private final TimingWheel delegate;
    private final DelayQueue<Bucket> queue;
    private final AtomicBoolean running = new AtomicBoolean(false);

    private Thread clockThread;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();


    public TimingWheelTimer(TimingWheel delegate, ExecutorService executorService) {
        this.delegate = delegate;
        this.executorService = executorService;
        this.queue = delegate.getQueue();
    }


    public void add(TimingEntry entry) {
        if (!delegate.add(entry)) {
            if (!entry.isCancelled()) {
                executorService.execute(entry.getTask());
            }
        }
    }

    @Override
    public void shutdown() {
        if (running.compareAndSet(true, false)) {
            executorService.shutdown();
        }
    }

    @Override
    public boolean isShutdown() {
        return !running.get();
    }

    @Override
    public void start() {
        if (running.compareAndSet(false, true)) {
            this.clockThread = new Thread(() -> {
                Lock writeLock = lock.writeLock();
                writeLock.lock();
                try {
                    while (running.get()) {
                        Bucket bucket = null;
                        try {
                            bucket = queue.poll(100, TimeUnit.MILLISECONDS);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        if (bucket == null) continue;
                        delegate.pushTime(bucket.getExpiration());
                        bucket.flush(this::add);
                    }
                }finally {
                    writeLock.unlock();
                }
            }, "timing-wheel-timer-" + id.getAndIncrement());
            this.clockThread.start();
        }
    }
}
