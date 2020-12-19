package com.retrytech.vilo.utils;

import android.os.Handler;

import java.util.Timer;

public class TimerTask {
    java.util.TimerTask mTimerTask;

    public void doTimerTask() {
        Handler handler = new Handler();
        Timer t = new Timer();
        mTimerTask = new java.util.TimerTask() {
            public void run() {
                handler.post(() -> new GlobalApi().rewardUser("1"));
            }
        };

        t.schedule(mTimerTask, 600000, 600000);
    }

    public void stopTimerTask() {
        if (mTimerTask != null) {
            mTimerTask.cancel();

        }
    }
}