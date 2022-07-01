```java 
TimingWheel wheel = new SimpleTimingWheel(256,1,new DelayQueue());
TimingWheelTimer timer = new TimingWheelTimer(wheel,executorService);
timer.start();
timer.add(timingEntry)
```