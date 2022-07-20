package com.example.ivrecording.Extra;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.ivrecording.R;
import com.example.ivrecording.Service.ForegroundService;

public class TimerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Intent serviceIntent = new Intent(context, ForegroundService.class);
        serviceIntent.putExtra("inputExtra", context.getResources().getString(R.string.app_name) + " Running");
        context.startService(serviceIntent);

        Intent intent1 = new Intent(context, TimerReceiver.class);
        //PendingIntent pendingIntent = PendingIntent.getBroadcast(
        //      context.getApplicationContext(), 234324243, intent, 0);
        PendingIntent pendingIntent1 = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent1 = PendingIntent.getActivity
                    (context.getApplicationContext(), 234324243, intent1, PendingIntent.FLAG_MUTABLE);
        }
        else
        {
            pendingIntent1 = PendingIntent.getActivity
                    (context.getApplicationContext(), 234324243, intent1, PendingIntent.FLAG_ONE_SHOT);
        }
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                ,(5*60*1000), pendingIntent1);
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                +  1000, pendingIntent1);

    }
}