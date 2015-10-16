package com.cainwong.metronome.rx;

import rx.functions.Func1;

public class MapRoundFloatToInt implements Func1<Float, Integer> {

    @Override
    public Integer call(Float f) {
        return Math.round(f);
    }

}

