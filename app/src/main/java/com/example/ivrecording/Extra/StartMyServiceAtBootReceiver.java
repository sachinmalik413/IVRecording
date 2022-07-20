package com.example.ivrecording.Extra;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.ivrecording.R;
import com.example.ivrecording.Service.ForegroundService;

public class StartMyServiceAtBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, ForegroundService.class);
            serviceIntent.putExtra("inputExtra", context.getResources().getString(R.string.app_name) + " Running");
            context.startService(serviceIntent);
            Log.e("sachin", "onReceive: " );
        }
    }
}
