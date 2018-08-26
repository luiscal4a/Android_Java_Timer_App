package com.example.cycle.util;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.cycle.AppConstants;
import com.example.cycle.MainActivity;
import com.example.cycle.R;
import com.example.cycle.TimerNotificationActionReceiver;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NotificationUtil {
    private static final String CHANNEL_ID_TIMER = "menu_timer";
    private static final String CHANNEL_NAME_TIMER = "Timer App Timer";
    private static final int TIMER_ID = 0;

    public static void showTimerExpired(Context context){
        Intent startIntent = new Intent(context, TimerNotificationActionReceiver.class);
        startIntent.setAction(AppConstants.ACTION_START);
        PendingIntent startPendingIntent = PendingIntent.getBroadcast(context, 0,
                startIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true);
        nBuilder.setContentTitle("Timer Expired!")
                .setContentText("Start again?")
                .setContentIntent(getPendingIntentWithStack(context, MainActivity.class))
                .addAction(R.drawable.ic_play, "Start", startPendingIntent);

        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true, nManager);

        nManager.notify(TIMER_ID, nBuilder.build());
    }

    public static void showTimerRunning(Context context, Long wakeUpTime){
        Intent stopIntent = new Intent(context, TimerNotificationActionReceiver.class);
        stopIntent.setAction(AppConstants.ACTION_STOP);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, 0,
                stopIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseIntent = new Intent(context, TimerNotificationActionReceiver.class);
        pauseIntent.setAction(AppConstants.ACTION_PAUSE);
        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(context, 0,
                pauseIntent,PendingIntent.FLAG_UPDATE_CURRENT);

         DateFormat df = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT);

        NotificationCompat.Builder nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true);
        nBuilder.setContentTitle("Timer is Running.")
                .setContentText("End: " +df.format(new Date(wakeUpTime)))
                .setContentIntent(getPendingIntentWithStack(context, MainActivity.class))
                .setOngoing(true)
                .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent)
                .addAction(R.drawable.ic_pause, "Pause", pausePendingIntent);

        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true, nManager);

        nManager.notify(TIMER_ID, nBuilder.build());
    }

    public static void showTimerPaused(Context context){
        Intent resumeIntent = new Intent(context, TimerNotificationActionReceiver.class);
        resumeIntent.setAction(AppConstants.ACTION_RESUME);
        PendingIntent resumePendingIntent = PendingIntent.getBroadcast(context, 0,
                resumeIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true);
        nBuilder.setContentTitle("Timer is paused.")
                .setContentText("Resume?")
                .setContentIntent(getPendingIntentWithStack(context, MainActivity.class))
                .setOngoing(true)
                .addAction(R.drawable.ic_play, "Resume", resumePendingIntent);

        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true, nManager);

        nManager.notify(TIMER_ID, nBuilder.build());
    }

    public static void hideTimerNotification(Context context){
        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.cancel(TIMER_ID);
    }

    private static NotificationCompat.Builder getBasicNotificationBuilder(Context context, String channelId, boolean playSound) {
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_timer)
                .setAutoCancel(true)
                .setDefaults(0);
        if(playSound)nBuilder.setSound(notificationSound);
        return nBuilder;
    }

    private static PendingIntent getPendingIntentWithStack(Context context, Class javaClass){
        Intent resultIntent = new Intent(context, javaClass);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(javaClass);
        stackBuilder.addNextIntent(resultIntent);

        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static void createNotificationChannel(String channelID, String channelName, boolean playSound, NotificationManager notifMan){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int channelImportance = playSound? NotificationManager.IMPORTANCE_DEFAULT:NotificationManager.IMPORTANCE_LOW;
            NotificationChannel nChannel = new NotificationChannel(channelID, channelName, channelImportance);
            nChannel.enableLights(true);
            nChannel.setLightColor(Color.BLUE);
            notifMan.createNotificationChannel(nChannel);
        }
    }
}
