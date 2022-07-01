package cn.fatcarter.wheel;

import java.util.List;
import java.util.concurrent.Delayed;
import java.util.function.Consumer;

public interface Bucket extends Delayed {
    void addEntry(TimingEntry entry);

    boolean setExpiration(long expire);

    long getExpiration();

    List<TimingEntry> entries();

    void flush(Consumer<TimingEntry> consumer);
}
