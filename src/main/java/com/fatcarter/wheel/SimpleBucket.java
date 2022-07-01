package com.fatcarter.wheel;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
public class SimpleBucket implements Bucket {
    private String id;
    private CopyOnWriteArrayList<TimingEntry> entries = new CopyOnWriteArrayList<>();
    private long expiration = 0;

    public SimpleBucket(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public void addEntry(TimingEntry entry) {
        entries.add(entry);
    }

    @Override
    public long getExpiration() {
        return this.expiration;
    }

    @Override
    public boolean setExpiration(long expire) {
        if (expiration > 0) return false;
        expiration = expire;
        return true;
    }

    @Override
    public List<TimingEntry> entries() {
        return new ArrayList<>(entries);
    }

    @Override
    public void flush(Consumer<TimingEntry> consumer) {
        List<TimingEntry> es = entries;
        entries = new CopyOnWriteArrayList<>();

        expiration = 0;
        if (consumer != null) {
            es.forEach(consumer);
        }
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(expiration - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        if (o == this) return 0;
        return Long.compare(this.getDelay(TimeUnit.MILLISECONDS), o.getDelay(TimeUnit.MILLISECONDS));
    }

}
