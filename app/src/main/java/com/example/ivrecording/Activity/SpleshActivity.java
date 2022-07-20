package com.example.ivrecording.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.ivrecording.Extra.Common;
import com.example.ivrecording.R;
import com.example.ivrecording.callrecorder.MainActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class SpleshActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private String Token;
    Context mContext = this;
    private static final int REQUEST= 112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splesh2);

        sharedPreferences=getSharedPreferences(Common.UserData,MODE_PRIVATE);

        if (sharedPreferences.contains(Common.UserToken)){

            Token=sharedPreferences.getString(Common.UserToken,"");
        }else{
            Token="";
        }

        checkPermission();


    }

    private void Initialization() {
        new Handler().postDelayed(() -> {
            if (!Token.equalsIgnoreCase("")){
                Intent mainIntent = new Intent(SpleshActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finishAffinity();
            }else{
                Intent mainIntent = new Intent(SpleshActivity.this, LoginActivity.class);
                startActivity(mainIntent);
                finishAffinity();
            }
        }, 2000);

    }

        private void checkPermission(){

        if (Build.VERSION.SDK_INT >= 23) {
            Log.d("TAG","@@@ IN IF Build.VERSION.SDK_INT >= 23");
            String[] PERMISSIONS = {
//                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.READ_PHONE_STATE,
                    android.Manifest.permission.INTERNET,
                    android.Manifest.permission.ACCESS_NETWORK_STATE,
                    android.Manifest.permission.ACCESS_WIFI_STATE,
//                    android.Manifest.permission.NFC,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS,
                    android.Manifest.permission.RECORD_AUDIO
            };

            if (!hasPermissions(mContext, PERMISSIONS)) {
                Log.d("TAG","@@@ IN IF hasPermissions");
                ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST );
            } else {
                Log.d("TAG","@@@ IN ELSE hasPermissions");
                Initialization();
            }
        }
        else {
            Log.d("TAG","@@@ IN ELSE  Build.VERSION.SDK_INT >= 23");
            Initialization();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG","@@@ PERMISSIONS grant");
                    Initialization();

                } else {
                    Log.d("TAG","@@@ PERMISSIONS Denied");
                    Toast.makeText(mContext, "PERMISSIONS Denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


}