package com.cainwong.metronome.dagger;

import com.cainwong.metronome.core.Metronome;
import com.cainwong.metronome.services.AudioService;
import com.cainwong.metronome.ui.MainActivity;

import javax.inject.Singleton;

import dagger.Component;


@Component(
        modules = AppModule.class
)
@Singleton
public interface AppComponent {

    void inject(MainActivity mainActivity);

    void inject(AudioService service);

    Metronome getMetronome();

}
