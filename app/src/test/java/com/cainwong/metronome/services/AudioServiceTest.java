package com.cainwong.metronome.services;

import android.media.AudioManager;

import com.cainwong.metronome.BuildConfig;
import com.cainwong.metronome.core.Metronome;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ServiceController;

import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;
import rx.subjects.TestSubject;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 21, constants = BuildConfig.class)
public class AudioServiceTest {

    ServiceController<AudioService> serviceController;
    AudioService service;
    AudioManager audioManager;
    Metronome metronome;
    TestScheduler testScheduler;
    TestSubject<Integer> beatObservable;

    @Before
    public void setup() throws Exception {
        serviceController = Robolectric.buildService(AudioService.class).create();
        service = serviceController.get();

        audioManager = mock(AudioManager.class);
        service.audioManager = audioManager;

        metronome = mock(Metronome.class);
        testScheduler = Schedulers.test();
        beatObservable = TestSubject.create(testScheduler);
        when(metronome.getBeatObservable()).thenReturn(beatObservable);
        service.metronome = metronome;

        serviceController.startCommand(0, 1);
    }

    @Test
    public void testAudioClicks(){
        beatObservable.onNext(1);
        beatObservable.onNext(2);
        beatObservable.onNext(1);
        beatObservable.onNext(2);
        beatObservable.onNext(3);
        beatObservable.onNext(4);
        testScheduler.triggerActions();

        verify(audioManager, times(2)).playSoundEffect(AudioService.ACCENT_CLICK_SOUND, 1);
        verify(audioManager, times(4)).playSoundEffect(AudioService.CLICK_SOUND, 1);
    }

    @Test
    public void testUnsubscribe(){
        Subscription mockSubscription = mock(Subscription.class);
        service.beatSubscription = mockSubscription;
        serviceController.destroy();
        verify(mockSubscription).unsubscribe();
    }

}
