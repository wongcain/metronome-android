package com.cainwong.metronome.rx;

import rx.functions.Func1;
import rx.schedulers.TimeInterval;

public class MapMillisToBpm implements Func1<Integer, Float> {

    @Override
    public Float call(Integer millis) {
        float tempo = 60000f / (float) (millis);
        return tempo;
    }

}
