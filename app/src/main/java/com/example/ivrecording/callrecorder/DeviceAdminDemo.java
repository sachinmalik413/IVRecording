package com.example.ivrecording.callrecorder;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.ServiceState;
import android.widget.Toast;

public class DeviceAdminDemo extends DeviceAdminReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    public void onEnabled(Context context, Intent intent) {
        Toast.makeText(context, "Enabled", Toast.LENGTH_LONG).show();
    }

    public void onDisabled(Context context, Intent intent) {
        Toast.makeText(context, "Disabled", Toast.LENGTH_LONG).show();
    }
}
