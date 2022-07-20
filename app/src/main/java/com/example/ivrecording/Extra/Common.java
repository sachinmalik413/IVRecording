package com.example.ivrecording.Extra;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.ivrecording.Worker.DownloadWorker;

public class Common {

    public static  String UserData="USERDATA";
    public static  String UserToken="USERTOKEN";

    public static void startBackgroundTask(Context context){
        Log.e("TAG", "startBackgroundTask: +setting alaram" );

        /*Intent intent = new Intent(context, MyBroadcastReceiver.class);
        //PendingIntent pendingIntent = PendingIntent.getBroadcast(
          //      context.getApplicationContext(), 234324243, intent, 0);
        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity
                    (context.getApplicationContext(), 234324243, intent, PendingIntent.FLAG_MUTABLE);
        }
        else
        {
            pendingIntent = PendingIntent.getActivity
                    (context.getApplicationContext(), 234324243, intent, PendingIntent.FLAG_ONE_SHOT);
        }
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                +  100, pendingIntent);*/



        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        OneTimeWorkRequest mRequest = new OneTimeWorkRequest.Builder(DownloadWorker.class)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(context).enqueue(mRequest);



        //alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
          //      +  1000, pendingIntent);
        /*Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                //.setRequiresBatteryNotLow(true)
                .build();
        OneTimeWorkRequest mRequest = new OneTimeWorkRequest.Builder(DownloadWorker.class)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(context).enqueue(mRequest);*/



    }
}
