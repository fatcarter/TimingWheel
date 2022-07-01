package cn.fatcarter.wheel;

public interface TimingEntry {
    long getFireTime();

    void cancel();

    boolean isCancelled();

    Runnable getTask();
}
