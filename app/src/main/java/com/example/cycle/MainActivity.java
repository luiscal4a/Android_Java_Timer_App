package com.example.cycle;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.cycle.util.NotificationUtil;
import com.example.cycle.util.PrefUtil;

import java.util.Calendar;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class MainActivity extends AppCompatActivity {


    public static Long setAlarm(Context context, Long nowSeconds, Long secondsRemaining){
        Long wakeUpTime = (nowSeconds + secondsRemaining) * 1000;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, TimerExpiredReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent);
        PrefUtil.setAlarmSetTime(nowSeconds, context);
        return wakeUpTime;
    }

    public static void removeAlarm(Context context){
        Intent intent = new Intent(context, TimerExpiredReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        PrefUtil.setAlarmSetTime(0L, context);
    }

    public static Long nowSeconds = Calendar.getInstance().getTimeInMillis()/1000;
    public static Long getNowSeconds(){
        nowSeconds = Calendar.getInstance().getTimeInMillis()/1000;
        return nowSeconds;
    }

    public enum TimerState{
        Stopped, Paused, Running
    }

    private CountDownTimer timer = null;
    private Long timerLengthSeconds = 0L;
    private TimerState timerState = TimerState.Stopped;
    private FloatingActionButton fab_start, fab_pause, fab_stop;
    private MaterialProgressBar progress_countdown;
    private TextView textView_countdown;

    private Long secondsRemaining = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.ic_timer);
        getSupportActionBar().setTitle("    Timer");


        fab_start = (FloatingActionButton) findViewById(R.id.fab_start);
        fab_pause = (FloatingActionButton) findViewById(R.id.fab_pause);
        fab_stop = (FloatingActionButton) findViewById(R.id.fab_stop);
        progress_countdown = (MaterialProgressBar) findViewById(R.id.progress_countdown);
        textView_countdown = (TextView) findViewById(R.id.textView_countdown);

        fab_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimer();
                timerState = TimerState.Running;
                updateButtons();
            }
        });

        fab_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.cancel();
                timerState = TimerState.Paused;
                updateButtons();
            }
        });

        fab_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.cancel();
                onTimerFinished();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        initTimer();

        removeAlarm(this);
        NotificationUtil.hideTimerNotification(this);
    }


    @Override
    protected void onPause() {
        super.onPause();

        if(timerState==TimerState.Running){
            timer.cancel();
            Long wakeUpTime = setAlarm(this, getNowSeconds(), secondsRemaining);
            NotificationUtil.showTimerRunning(this, wakeUpTime);
        }
        else if(timerState==TimerState.Paused){
            NotificationUtil.showTimerPaused(this);
        }

        PrefUtil.setPreviousTimerLengthSeconds(timerLengthSeconds, this);
        PrefUtil.setSecondsRemaining(secondsRemaining, this);
        PrefUtil.setTimerState(timerState, this);
    }


    private void initTimer(){
        timerState = PrefUtil.getTimerState(this);

        if(timerState == TimerState.Stopped)
            setNewTimerLength();
        else
            setPreviousTimerLength();

        secondsRemaining = (timerState == TimerState.Running || timerState == TimerState.Paused)?
            PrefUtil.getSecondsRemaining(this):timerLengthSeconds;


        Long alarmSetTime = PrefUtil.getAlarmSetTime(this);
        if(alarmSetTime > 0) secondsRemaining -= getNowSeconds() - alarmSetTime;

        if(secondsRemaining <= 0)
            onTimerFinished();
        else if(timerState == TimerState.Running)
            startTimer();

        updateButtons();
        updateCountdownUI();
    }

    private void onTimerFinished(){
        timerState = TimerState.Stopped;

        setNewTimerLength();

        progress_countdown.setProgress(0);

        PrefUtil.setSecondsRemaining(timerLengthSeconds, this);
        secondsRemaining = timerLengthSeconds;

        updateButtons();
        updateCountdownUI();
    }

    private void startTimer(){
        timerState = TimerState.Running;

        timer = new CountDownTimer(secondsRemaining*1000, 1000) {
            @Override
            public void onTick(long l) {
                secondsRemaining = l / 1000;
                updateCountdownUI();
            }

            @Override
            public void onFinish() {
                onTimerFinished();
            }
        }.start();
    }

    private void setNewTimerLength(){
        int lengthInMinutes = PrefUtil.getTimerLength(this);
        timerLengthSeconds = (lengthInMinutes*60L);
        progress_countdown.setMax(timerLengthSeconds.intValue());
    }

    private void setPreviousTimerLength(){
        timerLengthSeconds = PrefUtil.getPreviousTimerLengthSeconds(this);
        progress_countdown.setMax(timerLengthSeconds.intValue());
    }

    private void updateCountdownUI(){
        int minutesUntilFinished = secondsRemaining.intValue() / 60;
        int secondsInMinuteUntilFinished = secondsRemaining.intValue() - minutesUntilFinished * 60;
        String secondsStr = Integer.toString(secondsInMinuteUntilFinished);

        String newStr = secondsStr.length()==2? secondsStr:"0"+secondsStr;
        textView_countdown.setText(minutesUntilFinished+":"+newStr);

        progress_countdown.setProgress((timerLengthSeconds.intValue()-secondsRemaining.intValue()));
    }

    private void updateButtons(){
        switch(timerState){
            case Running:
                fab_start.setEnabled(false);
                fab_pause.setEnabled(true);
                fab_stop.setEnabled(true);
                break;

            case Paused:
                fab_start.setEnabled(true);
                fab_pause.setEnabled(false);
                fab_stop.setEnabled(true);
                break;

            case Stopped:
                fab_start.setEnabled(true);
                fab_pause.setEnabled(false);
                fab_stop.setEnabled(false);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
