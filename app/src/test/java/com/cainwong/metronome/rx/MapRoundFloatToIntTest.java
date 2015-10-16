package com.cainwong.metronome.rx;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MapRoundFloatToIntTest {

    @Test
    public void test() {
        MapRoundFloatToInt map = new MapRoundFloatToInt();
        assertThat(map.call(1.49999f)).isEqualTo(1);
        assertThat(map.call(1.5f)).isEqualTo(2);
    }
}
