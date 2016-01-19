package com.cainwong.metronome.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.cainwong.metronome.App;
import com.cainwong.metronome.R;
import com.cainwong.metronome.core.Metronome;
import com.cainwong.metronome.rx.FilterTimeIntervalWindow;
import com.cainwong.metronome.rx.MapMillisToBpm;
import com.cainwong.metronome.rx.MapRoundFloatToInt;
import com.cainwong.metronome.rx.MapRoundToValue;
import com.cainwong.metronome.services.AudioService;
import com.jakewharton.rxbinding.view.RxView;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.TimeInterval;

public class MainActivity extends AppCompatActivity {

    public static final long MIN_DELAY = 215;
    public static final long MAX_DELAY = 3000;
    public static final int ROUNDTO_VALUE = 5;

    @Bind(R.id.tempo)
    TextView tempoView;

    @Bind(R.id.fab)
    FloatingActionButton fab;

    @Inject
    Metronome metronome;

    @Inject
    @Named("mainThread")
    Scheduler mainThreadScheduler;

    @Inject
    @Named("immediate")
    Scheduler intervalScheduler;

    @Inject
    @Named("newThread")
    Scheduler newThreadScheduler;

    Subscription delaySubscription;
    Subscription playStateSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.component(this).inject(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Handle click events
        RxView.clicks(tempoView)
                .timeInterval(intervalScheduler)
                .filter(new FilterTimeIntervalWindow(MIN_DELAY, MAX_DELAY))
                .subscribe(new Action1<TimeInterval<Void>>() {
                    @Override
                    public void call(TimeInterval<Void> timeInterval) {
                        metronome.setDelay((int) (timeInterval.getIntervalInMilliseconds()));
                    }
                });

        RxView.clicks(fab)
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        metronome.togglePlay();
                    }
                });


        // Handle metronome events
        delaySubscription = metronome.getDelayObservable()
                .observeOn(mainThreadScheduler)
                .map(new MapMillisToBpm())
                .map(new MapRoundFloatToInt())
                .map(new MapRoundToValue(ROUNDTO_VALUE))
                .map(new Func1<Integer, String>() {
                    @Override
                    public String call(Integer tempo) {
                        return getApplicationContext().getString(R.string.tempo_disp, tempo);
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        tempoView.setText(s);
                    }
                });


        // play state display
        playStateSubscription = metronome.getPlayStateObservable()
                .observeOn(mainThreadScheduler)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        fab.setImageResource(aBoolean ? R.drawable.ic_stop_white_18dp
                                : R.drawable.ic_play_arrow_white_18dp);
                        if(aBoolean){
                            startService(new Intent(getApplicationContext(), AudioService.class));
                        } else {
                            stopService(new Intent(getApplicationContext(), AudioService.class));
                        }
                    }
                });

    }

    @Override
    protected void onStop() {
        if(delaySubscription!=null && !delaySubscription.isUnsubscribed()){
            delaySubscription.unsubscribe();
        }
        if(playStateSubscription!=null && !playStateSubscription.isUnsubscribed()){
            playStateSubscription.unsubscribe();
        }
        super.onStop();
    }
}
