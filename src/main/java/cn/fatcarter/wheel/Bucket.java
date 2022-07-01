package cn.fatcarter.wheel;

import java.util.List;
import java.util.concurrent.Delayed;
import java.util.function.Consumer;

/**
 * Timing Wheel slot
 */
public interface Bucket extends Delayed {
    /**
     * Add an entry to entry list
     *
     * @param entry the delayed entry object
     */
    void addEntry(TimingEntry entry);

    /**
     * Set expiration time(milliseconds) for this bucket , return true if it was set for the first time , false otherwise
     *
     * @param expire The bucket timeout timestamp
     */
    boolean setExpiration(long expire);

    /**
     * Get the bucket expiration timestamp
     *
     * @return
     */
    long getExpiration();


    List<TimingEntry> entries();

    /**
     * Flush the bucket, and call consumer for all elements in the entry list
     */
    void flush(Consumer<TimingEntry> consumer);
}
