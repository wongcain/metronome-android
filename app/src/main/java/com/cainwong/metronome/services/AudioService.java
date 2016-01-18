package com.cainwong.metronome.services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.cainwong.metronome.App;
import com.cainwong.metronome.core.Metronome;

import javax.inject.Inject;

import rx.Subscription;
import rx.functions.Action1;

public class AudioService extends Service {

    public static final int CLICK_SOUND = AudioManager.FX_KEYPRESS_DELETE;
    public static final int ACCENT_CLICK_SOUND = AudioManager.FX_KEYPRESS_STANDARD;

    @Inject
    AudioManager audioManager;

    @Inject
    Metronome metronome;

    Subscription beatSubscription;

    @Override
    public void onCreate() {
        super.onCreate();
        App.component(this).inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (beatSubscription == null || beatSubscription.isUnsubscribed()) {
            beatSubscription = metronome.getBeatObservable().subscribe(new Action1<Integer>() {
                @Override
                public void call(Integer beat) {
                    audioManager.playSoundEffect((beat == 1) ? ACCENT_CLICK_SOUND : CLICK_SOUND, 1);
                }
            });
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (beatSubscription != null && !beatSubscription.isUnsubscribed()) {
            beatSubscription.unsubscribe();
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
