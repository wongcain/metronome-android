package com.cainwong.metronome.rx;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MapRoundToValueTest {

    @Test
    public void test() throws Exception {
        MapRoundToValue map = new MapRoundToValue(10);
        assertThat(map.call(4)).isEqualTo(0);
        assertThat(map.call(5)).isEqualTo(10);
        assertThat(map.call(14)).isEqualTo(10);
        assertThat(map.call(15)).isEqualTo(20);

        map.setRoundToValue(5);
        assertThat(map.call(2)).isEqualTo(0);
        assertThat(map.call(3)).isEqualTo(5);
        assertThat(map.call(12)).isEqualTo(10);
        assertThat(map.call(13)).isEqualTo(15);
    }

}
