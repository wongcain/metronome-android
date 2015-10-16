package com.cainwong.metronome.dagger;

import android.app.Application;
import android.content.Context;
import android.media.AudioManager;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by cwong on 10/15/15.
 */
@Module
public class AppModule {

    private final Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return application;
    }

    @Provides
    @Singleton
    @Named("mainThread")
    Scheduler provideMainThreadScheduler() {
        return AndroidSchedulers.mainThread();
    }

    @Provides
    @Singleton
    @Named("newThread")
    Scheduler provideNewThreadScheduler() {
        return Schedulers.newThread();
    }

    @Provides
    @Singleton
    @Named("immediate")
    Scheduler provideImmediateScheduler() {
        return Schedulers.immediate();
    }

    @Provides
    @Singleton
    AudioManager provideAudioManager(){
        return (AudioManager)application.getSystemService(Context.AUDIO_SERVICE);
    }

}
