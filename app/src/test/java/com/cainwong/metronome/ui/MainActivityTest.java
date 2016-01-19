package com.cainwong.metronome.ui;

import com.cainwong.metronome.BuildConfig;
import com.cainwong.metronome.R;
import com.cainwong.metronome.core.Metronome;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;
import rx.subjects.TestSubject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 21, constants = BuildConfig.class)
public class MainActivityTest {

    ActivityController<MainActivity> activityController;
    MainActivity activity;
    TestScheduler testScheduler;
    Metronome metronome;
    TestSubject<Boolean> playStateObservable;
    TestSubject<Integer> delayObservable;
    TestSubject<Integer> beatObservable;

    @Before
    public void setup() throws Exception {
        activityController = Robolectric.buildActivity(MainActivity.class).create();
        activity = activityController.get();

        testScheduler = Schedulers.test();
        activity.intervalScheduler = testScheduler;
        activity.newThreadScheduler = activity.mainThreadScheduler;

        metronome = mock(Metronome.class);
        playStateObservable = TestSubject.create(testScheduler);
        when(metronome.getPlayStateObservable()).thenReturn(playStateObservable);
        delayObservable = TestSubject.create(testScheduler);
        when(metronome.getDelayObservable()).thenReturn(delayObservable);
        beatObservable = TestSubject.create(testScheduler);
        when(metronome.getBeatObservable()).thenReturn(beatObservable);
        activity.metronome = metronome;

        activityController.start();
    }

    @Test
    public void testTapTempoTooFast() throws Exception {
        reset(metronome);
        activity.tempoView.performClick();
        testScheduler.advanceTimeBy(MainActivity.MIN_DELAY - 1, TimeUnit.MILLISECONDS);
        activity.tempoView.performClick();
        verifyNoMoreInteractions(metronome);
    }

    @Test
    public void testTapTempoTooSlow() throws Exception {
        reset(metronome);
        activity.tempoView.performClick();
        testScheduler.advanceTimeBy(MainActivity.MAX_DELAY + 1, TimeUnit.MILLISECONDS);
        activity.tempoView.performClick();
        verifyNoMoreInteractions(metronome);
    }

    @Test
    public void testTapTempoValidRange() throws Exception {
        reset(metronome);
        activity.tempoView.performClick();
        testScheduler.advanceTimeBy(750, TimeUnit.MILLISECONDS);
        activity.tempoView.performClick();
        verify(metronome).setDelay(750);

        reset(metronome);
        testScheduler.advanceTimeBy(1000, TimeUnit.MILLISECONDS);
        activity.tempoView.performClick();
        verify(metronome).setDelay(1000);

        reset(metronome);
        testScheduler.advanceTimeBy(428, TimeUnit.MILLISECONDS);
        activity.tempoView.performClick();
        verify(metronome).setDelay(428);

        reset(metronome);
        testScheduler.advanceTimeBy(2000, TimeUnit.MILLISECONDS);
        activity.tempoView.performClick();
        verify(metronome).setDelay(2000);
    }

    @Test
    public void testTempoUpdateDisplay() throws Exception {
        delayObservable.onNext(750);
        testScheduler.triggerActions();
        assertThat(activity.tempoView.getText()).isEqualTo("80 BPM");

        delayObservable.onNext(1000);
        testScheduler.triggerActions();
        assertThat(activity.tempoView.getText()).isEqualTo("60 BPM");

        delayObservable.onNext(428);
        testScheduler.triggerActions();
        assertThat(activity.tempoView.getText()).isEqualTo("140 BPM");

        delayObservable.onNext(2000);
        testScheduler.triggerActions();
        assertThat(activity.tempoView.getText()).isEqualTo("30 BPM");
    }

    @Test
    public void testPlayStateUpdate() throws Exception {
        assertThat(activity.fab.getDrawable()).isEqualTo(activity.getDrawable(R.drawable.ic_play_arrow_white_18dp));

        playStateObservable.onNext(false);
        testScheduler.triggerActions();
        assertThat(activity.fab.getDrawable()).isEqualTo(activity.getDrawable(R.drawable.ic_play_arrow_white_18dp));

        playStateObservable.onNext(true);
        testScheduler.triggerActions();
        assertThat(activity.fab.getDrawable()).isEqualTo(activity.getDrawable(R.drawable.ic_stop_white_18dp));

        playStateObservable.onNext(true);
        testScheduler.triggerActions();
        assertThat(activity.fab.getDrawable()).isEqualTo(activity.getDrawable(R.drawable.ic_stop_white_18dp));

        playStateObservable.onNext(false);
        testScheduler.triggerActions();
        assertThat(activity.fab.getDrawable()).isEqualTo(activity.getDrawable(R.drawable.ic_play_arrow_white_18dp));
    }

    @Test
    public void testMetroTogglePlay() throws Exception {
        activity.fab.performClick();
        verify(metronome).togglePlay();
    }

    @Test
    public void testUnsubscribe(){
        Subscription delaySubscription = mock(Subscription.class);
        activity.delaySubscription = delaySubscription;
        Subscription playStateSubscription = mock(Subscription.class);
        activity.playStateSubscription = playStateSubscription;
        activityController.stop();
        verify(delaySubscription).unsubscribe();
        verify(playStateSubscription).unsubscribe();
    }

}
