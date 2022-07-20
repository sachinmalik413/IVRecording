package com.example.ivrecording.Service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.ivrecording.Extra.MyBroadcastReceiver;
import com.example.ivrecording.Extra.TimerReceiver;
import com.example.ivrecording.R;
import com.example.ivrecording.callrecorder.MainActivity;
import com.example.ivrecording.callrecorder.PhoneStateTracker;

import okhttp3.internal.Util;

public class ForegroundService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    PhoneStateTracker phoneState;

    @Override
    public void onCreate() {
        super.onCreate();
        //String input = intent.getStringExtra("inputExtra");

        Intent intent = new Intent(this, TimerReceiver.class);
        //PendingIntent pendingIntent = PendingIntent.getBroadcast(
        //      context.getApplicationContext(), 234324243, intent, 0);
        PendingIntent pendingIntent1 = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent1 = PendingIntent.getActivity
                    (getApplicationContext(), 234324243, intent, PendingIntent.FLAG_MUTABLE);
        }
        else
        {
            pendingIntent1 = PendingIntent.getActivity
                    (getApplicationContext(), 234324243, intent, PendingIntent.FLAG_ONE_SHOT);
        }
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                ,(5*60*1000), pendingIntent1);
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                +  1000, pendingIntent1);

        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = null;
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity
                    (this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE);
        }
        else
        {
            pendingIntent = PendingIntent.getActivity
                    (this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        }
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(getResources().getString(R.string.app_name) + " Running")
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
        phoneState = new PhoneStateTracker(this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {


           /* PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "MyApp::MyWakelockTag");
            wakeLock.acquire();
*/

            phoneState.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
             phoneState.stop(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}