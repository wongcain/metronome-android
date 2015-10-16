package com.cainwong.metronome.core;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class MetronomeTest {

    Metronome metronome;
    TestScheduler scheduler;
    TestSubscriber<Integer> beatSubscriber;
    TestSubscriber<Boolean> playStateSubscriber;

    @Before
    public void setup() throws Exception {
        metronome = new Metronome();
        scheduler = Schedulers.test();
        metronome.scheduler = scheduler;
        beatSubscriber = new TestSubscriber<>();
        metronome.getBeatObservable().subscribe(beatSubscriber);
        playStateSubscriber = new TestSubscriber<>();
        metronome.getPlayStateObservable().subscribe(playStateSubscriber);
    }

    @Test
    public void testPlayAndStop() throws Exception {
        metronome.togglePlay();
        scheduler.advanceTimeBy(3000, TimeUnit.MILLISECONDS);
        playStateSubscriber.assertValue(true);
        beatSubscriber.assertValues(1, 2, 3, 4, 1, 2);
        metronome.togglePlay();
        scheduler.advanceTimeBy(3000, TimeUnit.MILLISECONDS);
        playStateSubscriber.assertValues(true, false);
        beatSubscriber.assertValues(1, 2, 3, 4, 1, 2);

    }

    @Test
    public void testTempoChange() throws Exception {
        metronome.setDelay(1000);
        metronome.togglePlay();
        playStateSubscriber.assertValues(true);
        scheduler.advanceTimeBy(2000, TimeUnit.MILLISECONDS);
        beatSubscriber.assertValues(1, 2);
        metronome.setDelay(250);
        scheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);
        beatSubscriber.assertValues(1, 2, 3, 4, 1, 2);
    }

    @Test
    public void testNumBeatsChange() throws Exception {
        metronome.togglePlay();
        scheduler.advanceTimeBy(2000, TimeUnit.MILLISECONDS);
        playStateSubscriber.assertValue(true);
        beatSubscriber.assertValues(1, 2, 3, 4);
        metronome.setNumBeats(3);
        scheduler.advanceTimeBy(2000, TimeUnit.MILLISECONDS);
        beatSubscriber.assertValues(1, 2, 3, 4, 1, 2, 3, 1);
    }

}
