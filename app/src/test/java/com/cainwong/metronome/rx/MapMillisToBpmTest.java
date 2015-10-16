package com.cainwong.metronome.rx;

import org.junit.Test;

import rx.schedulers.TimeInterval;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MapMillisToBpmTest {

    @Test
    public void test(){
        MapMillisToBpm map = new MapMillisToBpm();
        assertThat(map.call(1000)).isEqualTo(60);
        assertThat(map.call(500)).isEqualTo(120);
    }

}
