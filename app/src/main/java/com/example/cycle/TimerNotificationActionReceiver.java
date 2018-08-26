package com.example.cycle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.cycle.util.NotificationUtil;
import com.example.cycle.util.PrefUtil;

public class TimerNotificationActionReceiver extends BroadcastReceiver {

    Long secondsRemaining, wakeUpTime;

    @Override
    public void onReceive(Context context, Intent intent) {
        switch(intent.getAction()){
            case AppConstants.ACTION_STOP:
                MainActivity.removeAlarm(context);
                PrefUtil.setTimerState(MainActivity.TimerState.Stopped, context);
                NotificationUtil.hideTimerNotification(context);
            break;

            case AppConstants.ACTION_PAUSE:
                secondsRemaining = PrefUtil.getSecondsRemaining(context);
                Long alarmSetTime = PrefUtil.getAlarmSetTime(context);
                Long nowSeconds = MainActivity.getNowSeconds();

                secondsRemaining -= nowSeconds - alarmSetTime;
                PrefUtil.setSecondsRemaining(secondsRemaining, context);

                MainActivity.removeAlarm(context);
                PrefUtil.setTimerState(MainActivity.TimerState.Paused, context);
                NotificationUtil.showTimerPaused(context);
                break;

            case AppConstants.ACTION_RESUME:
                secondsRemaining = PrefUtil.getSecondsRemaining(context);
                wakeUpTime = MainActivity.setAlarm(context, MainActivity.getNowSeconds(), secondsRemaining);
                PrefUtil.setTimerState(MainActivity.TimerState.Running, context);
                NotificationUtil.showTimerRunning(context, wakeUpTime);
                break;

            case AppConstants.ACTION_START:
                int minutesRemaining = PrefUtil.getTimerLength(context);
                secondsRemaining = minutesRemaining * 60L;
                wakeUpTime = MainActivity.setAlarm(context, MainActivity.getNowSeconds(), secondsRemaining);
                PrefUtil.setTimerState(MainActivity.TimerState.Running, context);
                PrefUtil.setSecondsRemaining(secondsRemaining, context);
                NotificationUtil.showTimerRunning(context, wakeUpTime);
                break;
        }
    }
}
