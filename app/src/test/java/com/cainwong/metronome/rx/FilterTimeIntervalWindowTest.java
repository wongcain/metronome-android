package com.cainwong.metronome.rx;

import org.junit.Test;

import rx.schedulers.TimeInterval;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FilterTimeIntervalWindowTest {

    @Test
    public void test(){
        FilterTimeIntervalWindow filter = new FilterTimeIntervalWindow(10l, 100l);
        TimeInterval timeInterval = mock(TimeInterval.class);
        when(timeInterval.getIntervalInMilliseconds()).thenReturn(9l);
        assertThat(filter.call(timeInterval)).isFalse();
        when(timeInterval.getIntervalInMilliseconds()).thenReturn(10l);
        assertThat(filter.call(timeInterval)).isTrue();
        when(timeInterval.getIntervalInMilliseconds()).thenReturn(100l);
        assertThat(filter.call(timeInterval)).isTrue();
        when(timeInterval.getIntervalInMilliseconds()).thenReturn(101l);
        assertThat(filter.call(timeInterval)).isFalse();
    }

}
