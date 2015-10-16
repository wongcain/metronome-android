package com.cainwong.metronome.core;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Action1;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

/**
 * Created by cwong on 10/14/15.
 */
@Singleton
public class Metronome {

    public static final int DEFAULT_DELAY = 500;
    public static final int DEFAULT_BEATS = 4;

    private int delay;
    private int numBeats = DEFAULT_BEATS;
    private int beat = 0;

    private BehaviorSubject<Integer> delayObservable = BehaviorSubject.create();
    private BehaviorSubject<Integer> beatObservable = BehaviorSubject.create();
    private BehaviorSubject<Boolean> playStateObservable = BehaviorSubject.create();
    private PublishSubject<Object> stopTrigger= PublishSubject.create();

    @Inject
    @Named("newThread")
    Scheduler scheduler;

    @Inject
    public Metronome() {
        setDelay(DEFAULT_DELAY);
    }

    public void setDelay(int delay) {
        this.delay = delay;
        delayObservable.onNext(delay);
        restartIfPlaying();
    }

    public void setNumBeats(int numBeats) {
        this.numBeats = numBeats;
        restartIfPlaying();
    }

    public Observable<Integer> getDelayObservable() {
        return delayObservable;
    }

    public Observable<Integer> getBeatObservable(){
        return beatObservable;
    }

    public Observable<Boolean> getPlayStateObservable() {
        return playStateObservable;
    }

    public void togglePlay(){
        if(isPlaying()){
            stop();
        } else {
            play();
        }
    }

    private boolean isPlaying(){
        return Boolean.TRUE.equals(playStateObservable.getValue());
    }

    private void play(){
        Observable.interval(delay, TimeUnit.MILLISECONDS, scheduler)
                .takeUntil(stopTrigger)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long o) {
                        beat++;
                        beatObservable.onNext(beat);
                        if(beat==numBeats){
                            beat = 0;
                        }
                    }
                });
        playStateObservable.onNext(true);
    }

    private void stop(){
        stopTrigger.onNext(null);
        beat=0;
        playStateObservable.onNext(false);
    }

    private void restartIfPlaying(){
        if(isPlaying()) {
            stopTrigger.onNext(null);
            play();
        }
    }

}
