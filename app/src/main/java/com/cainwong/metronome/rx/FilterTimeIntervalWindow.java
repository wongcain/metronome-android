package com.cainwong.metronome.rx;

import rx.functions.Func1;
import rx.schedulers.TimeInterval;

public class FilterTimeIntervalWindow implements Func1<TimeInterval, Boolean> {

    private final long minValue;
    private final long maxValue;

    public FilterTimeIntervalWindow(long minValue, long maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public Boolean call(TimeInterval timeInterval) {
        return (timeInterval.getIntervalInMilliseconds() >= minValue) && (timeInterval.getIntervalInMilliseconds() <= maxValue);
    }
}
