package com.example.ivrecording.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.ivrecording.callrecorder.PhoneStateTracker;

public class SimpleService extends Service {
    PhoneStateTracker phoneStateTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        phoneStateTracker = new PhoneStateTracker(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        phoneStateTracker.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        try {
            phoneStateTracker.stop(getApplicationContext());
            super.onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}