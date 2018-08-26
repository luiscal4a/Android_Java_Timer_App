package com.example.cycle.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.example.cycle.MainActivity;

public class PrefUtil {

    private static final String TIMER_LENGTH_ID = "com.example.timer.timer_length";


    private static final String  PREVIOUS_TIMER_LENGTH_SECONDS_ID = "com.example.cycle.previous_timer_length";

    public static int getTimerLength(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(TIMER_LENGTH_ID, 10);
    }

    public static Long getPreviousTimerLengthSeconds(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, 0);
    }

    public static void setPreviousTimerLengthSeconds(Long seconds, Context context){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, seconds);
        editor.apply();
    }

    private static final String TIMER_STATE_ID = "com.example.cycle.timer_state";

    public static MainActivity.TimerState getTimerState(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int ordinal = preferences.getInt(TIMER_STATE_ID, 0);
        return MainActivity.TimerState.values()[ordinal];
    }

    public static void setTimerState(MainActivity.TimerState state, Context context){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        int ordinal = state.ordinal();
        editor.putInt(TIMER_STATE_ID, ordinal);
        editor.apply();
    }

    private static final String  SECONDS_REMAINING_ID = "com.example.cycle.seconds_remaining";

    public static Long getSecondsRemaining(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getLong(SECONDS_REMAINING_ID, 0);
    }

    public static void setSecondsRemaining(Long seconds, Context context){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putLong(SECONDS_REMAINING_ID, seconds);
        editor.apply();
    }

    private static final String ALARM_SET_TIME_ID = "com.example.cycle.backgrounded_time";

    public static Long getAlarmSetTime(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getLong(ALARM_SET_TIME_ID, 0);
    }

    public static void setAlarmSetTime(Long time, Context context){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putLong(ALARM_SET_TIME_ID, time);
        editor.apply();
    }
}
