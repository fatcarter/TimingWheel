package cn.fatcarter.wheel.test;


import cn.fatcarter.wheel.SimpleTimingWheel;
import cn.fatcarter.wheel.TimerTaskEntry;
import cn.fatcarter.wheel.TimingEntry;
import cn.fatcarter.wheel.TimingWheel;
import cn.fatcarter.wheel.TimingWheelTimer;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class TimingWheelTimerTest {

    @Test
    public void testTaskCancel() throws InterruptedException {
        TimingWheel tw = new SimpleTimingWheel(256, 1, new DelayQueue<>());
        ExecutorService executorService = new ThreadPoolExecutor(20, 20, 1, TimeUnit.SECONDS, new SynchronousQueue<>());
        TimingWheelTimer timer = new TimingWheelTimer(tw, executorService);
        timer.start();
        TimingEntry entry = new TimerTaskEntry(10000, e -> {
            System.out.println(LocalDateTime.now() + "entry timeout: " + e);
        });
        System.out.println(LocalDateTime.now() + "entry " + entry + " will be add to timer");
        timer.add(entry);

        Thread.sleep(9000);
        boolean cancelled = timer.cancel(entry);
        System.out.println(LocalDateTime.now() + "cancelled: " + cancelled);
        Thread.sleep(5000);

    }
}
