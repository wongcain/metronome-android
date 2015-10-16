package com.cainwong.metronome.rx;

import rx.functions.Func1;

public class MapRoundToValue implements Func1<Integer, Integer> {

    private int roundToValue;

    public MapRoundToValue(int roundToValue) {
        this.roundToValue = roundToValue;
    }

    @Override
    public Integer call(Integer i) {
        return Math.round(i.floatValue() / roundToValue) * roundToValue;
    }

    public void setRoundToValue(int roundToValue) {
        this.roundToValue = roundToValue;
    }

}
