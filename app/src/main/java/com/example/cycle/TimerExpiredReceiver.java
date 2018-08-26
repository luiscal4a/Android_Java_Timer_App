package com.example.cycle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.cycle.util.PrefUtil;

public class TimerExpiredReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        PrefUtil.setTimerState(MainActivity.TimerState.Stopped, context);
        PrefUtil.setAlarmSetTime(0L, context);
    }
}
