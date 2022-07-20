package com.example.ivrecording.callrecorder;

import android.Manifest;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.ivrecording.CallingRetrofitApi.GlobalClassForAllApi;
import com.example.ivrecording.Extra.Common;
import com.example.ivrecording.LottieDialogFragment;
import com.example.ivrecording.R;
import com.example.ivrecording.Service.ForegroundService;
import com.example.ivrecording.Service.SimpleService;
import com.google.gson.Gson;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private ImageView iv_UserImage;
    private TextView tv_fullName, tv_Email, tv_Department, tv_RoleType;
    private LottieDialogFragment mDialogFragment;
    private SharedPreferences sharedPreferences;
    private String UserToken;


    private CompositeDisposable disposable = new CompositeDisposable();


    private static final int REQUEST_CODE = 0;
    private DevicePolicyManager mDPM;
    private ComponentName mAdminName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sharedPreferences = getSharedPreferences(Common.UserData, MODE_PRIVATE);

        if (sharedPreferences.contains(Common.UserToken)) {
            UserToken = sharedPreferences.getString(Common.UserToken, "");
            Log.e("hgffgh", UserToken + "");

        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        }

        //Check Foreground service is running or Not
        boolean result = isMyServiceRunning(ForegroundService.class);
        boolean result2 = isMyServiceRunning(SimpleService.class);
        if (result || result2) {
            //Do nothing
        } else {
            //--------Start_Service--------------//
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent serviceIntent = new Intent(MainActivity.this, ForegroundService.class);
                    serviceIntent.putExtra("inputExtra", getResources().getString(R.string.app_name) + " Running");
                    ContextCompat.startForegroundService(MainActivity.this, serviceIntent);
                } else {
                    Intent intent1 = new Intent(MainActivity.this, SimpleService.class);
                    startService(intent1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Initialization();

    }

    private void Initialization() {
        iv_UserImage = findViewById(R.id.iv_UserImage);
        tv_fullName = findViewById(R.id.tv_fullName);
        tv_Email = findViewById(R.id.tv_Email);
        tv_Department = findViewById(R.id.tv_Department);
        tv_RoleType = findViewById(R.id.tv_RoleType);


        iv_UserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //hit Report API for pending reports
        //UpdateReport updateReport = new UpdateReport();
        //updateReport.fetchFromDb(getApplicationContext());
        Common.startBackgroundTask(getApplicationContext());
        GetProfileApi();
///////////CallRecordingData

        try {
            // Initiate DevicePolicyManager.
            mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            mAdminName = new ComponentName(this, DeviceAdminDemo.class);

            if (!mDPM.isAdminActive(mAdminName)) {
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Click on Activate button to secure your application.");
                startActivityForResult(intent, REQUEST_CODE);
            } else {
                // mDPM.lockNow();
                // Intent intent = new Intent(MainActivity.this,
                // TrackDeviceService.class);
                // startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void GetProfileApi() {

        //showProgressDialog();

        disposable.add(GlobalClassForAllApi.initRetrofit().ProfileData("Bearer " + UserToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe((user, throwatble) -> {
                            // hideProgressDialog();
                            if (user != null) {

                                Log.e("jfgkdshe", "Response size: " + new Gson().toJson(user));

                                if (user.getStatus().equals(200)) {
                                    Log.e("jkhkf", "Response size: " + new Gson().toJson(user));

                                    tv_fullName.setText(user.getData().getName());
                                    tv_Email.setText(user.getData().getEmail());
                                    try {
                                        if (user.getData().getDepartment().getName()!=null){
                                            tv_Department.setText(user.getData().getDepartment().getName());
                                        }
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    tv_RoleType.setText(user.getData().getType().getName());


                                } else {
                                    // Toast.makeText(this,  "Credentials no matched", Toast.LENGTH_SHORT).show();

                                    Log.e("", "" + new Gson().toJson(throwatble));
                                    // hideProgressDialog();

                                }
                            } else {

                                // hideProgressDialog();
                            }

                        }
                )
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    private void showProgressDialog() {

        mDialogFragment = new LottieDialogFragment();
        mDialogFragment.show(getSupportFragmentManager(), "");
    }

    private void hideProgressDialog() {
        mDialogFragment.dismiss();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (REQUEST_CODE == requestCode) {
            Intent intent = new Intent(MainActivity.this, TService.class);
            startService(intent);
        }
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
