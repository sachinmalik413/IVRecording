package com.example.ivrecording.callrecorder;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.SimpleDateFormat;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.ivrecording.CallingRetrofitApi.APIClient;
import com.example.ivrecording.CallingRetrofitApi.ApiUrlInterfaces;
import com.example.ivrecording.Model.modelUserVideoRecord;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TService extends Service {
/*/////////////New ChangesData////////
    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".mp3";
    // private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    private MediaRecorder recorder = null;
    private int currentFormat = 0;
    private String file_exts[] = {AUDIO_RECORDER_FILE_EXT_3GP};
    private File FILEPATH;


   ///////////////////////////////////////*/

    //Call Recording varibales
    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".mp3";
    //private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    private MediaRecorder recorder = null;
    private int currentFormat = 0;
    private int output_formats[] = {MediaRecorder.OutputFormat.THREE_GPP};
    private String file_exts[] = {AUDIO_RECORDER_FILE_EXT_3GP};

    AudioManager audioManager;
    private File FILEPATH,file;


    private static final String TAG = "TService";
    ApiUrlInterfaces apiUrlInterfaces;
    private File fileLast;
    // MediaRecorder recorder;
    File audiofile;
    String name, phonenumber;
    String audio_format;
    public String Audio_Type;
    int audioSource;
    Context context;
    Timer timer;
    Boolean offHook = false, ringing = false;
    Toast toast;
    Boolean isOffHook = false;
    private boolean recordstarted = false;

    private static final String ACTION_IN = "android.intent.action.PHONE_STATE";
    private static final String ACTION_OUT = "android.intent.action.NEW_OUTGOING_CALL";
    private CallBr br_call;

    String phoneNr;
    int abcd;


//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//
//        final IntentFilter filter = new IntentFilter();
//        filter.addAction(ACTION_OUT);
//        filter.addAction(ACTION_IN);
//        this.br_call = new CallBr();
//        this.registerReceiver(this.br_call, filter);
//        Log.e("lkjhhj", "Qasim1");
//
//
//        return START_NOT_STICKY;
//    }

    public class CallBr extends BroadcastReceiver {
        Bundle bundle;
        String state;
        String inCall, outCall;
        public boolean wasRinging = false;
        String action ;


        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("lkjhhj", "Qasim2");


            TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
//            CustomPhoneStateListener customPhoneListener = new CustomPhoneStateListener();
//            telephony.listen(customPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);

            Bundle bundle = intent.getExtras();
            phoneNr = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            state = bundle.getString("state");
            action = intent.getAction();

            Log.d(TAG, "onReceive: "+ intent.getAction());

            Log.v(TAG, "phoneNr: "+phoneNr);

            if(action.equals(ACTION_IN)){

                Log.d(TAG, "onReceive: "+ action);

                if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){

                    wasRinging = true;
                    Toast.makeText(context, "IN : " + phoneNr, Toast.LENGTH_LONG).show();
                }
                else if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){

                    if (wasRinging) {

                        Toast.makeText(context, "ANSWERED", Toast.LENGTH_LONG).show();
                        StartRecording();
                    }
                }
                else if(state.equals(TelephonyManager.EXTRA_STATE_IDLE)){

                    wasRinging = false;
                    Toast.makeText(context, "REJECT || DISCO", Toast.LENGTH_LONG).show();
                    if (recordstarted) {
                        StopRecording();
                        recordstarted = false;
                    }
                }
            }
            else if(action.equals(ACTION_OUT)){

                phoneNr = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                Toast.makeText(context, "OUT : " + phoneNr, Toast.LENGTH_LONG).show();


            }


           /* if (intent.getAction().equals(ACTION_IN)) {
                Log.e("lkjhhj", "Qasim3");
                StartRecording();
               *//* if ((bundle = intent.getExtras()) != null) {
                    state = bundle.getString(TelephonyManager.EXTRA_STATE);
                    if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                        inCall = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                        wasRinging = true;
                        Log.e("lkjhhj", inCall + "");
                        Toast.makeText(context, "IN : " + inCall, Toast.LENGTH_LONG).show();
                    } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                        if (wasRinging == true) {
                            Log.e("lkjhhj", "ANSWERED");
                            Toast.makeText(context, "ANSWERED", Toast.LENGTH_LONG).show();
                            StartRecording();

                        }
                    } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                        wasRinging = false;
                        Log.e("lkjhhj", "REJECT || DISCO");


                        Toast.makeText(context, "REJECT || DISCO", Toast.LENGTH_LONG).show();

                        StopRecording();


                    }
                }*//*
            } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                wasRinging = false;

                Log.e("lkjhhj", "Qasim4");
                Log.e("lkjhhj", "REJECT || DISCO");


                Toast.makeText(context, "REJECT || DISCO", Toast.LENGTH_LONG).show();

                StopRecording();


            }


            else if (intent.getAction().equals(ACTION_OUT)) {
                Log.e("lkjhhj", "Qasim5" + intent.getExtras());
                *//*if ((bundle = intent.getExtras()) != null) {
                    outCall = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                    Log.e("lkjhhj", "Qasim4");
                    Log.e("lkjhhj", outCall + "");

                    StartRecording();
                }*//*
                Log.e("lkjhhj", "Qasim5");
                StartRecording();
            }*/


        }

        public class CustomPhoneStateListener extends PhoneStateListener {

            private static final String TAG = "CustomPhoneStateListener";

            public void onCallStateChange(int state, String incomingNumber) {

                Log.v(TAG, "WE ARE INSIDE!!!!!!!!!!!");
                Log.v(TAG, incomingNumber);

                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        Log.d(TAG, "RINGING");
                        break;
                }
            }
        }
    }

   /* private String getFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        // File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Test/");


        if (!file.exists()) {
            file.mkdirs();
        }

        //FILEPATH = new File(file.getAbsoluteFile() + "/" + System.currentTimeMillis() + ".mp3");
        FILEPATH = new File(file.getAbsolutePath() + "/" + System.currentTimeMillis() + file_exts[currentFormat]);


        Log.e("fggffgf", FILEPATH + "");

        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + file_exts[currentFormat]);
        // return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".mp3");
    }
    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);

        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        //recorder.setOutputFormat(output_formats[currentFormat]);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        // recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        recorder.setOutputFile(getFilename());
        recorder.setOnErrorListener(errorListener);
        recorder.setOnInfoListener(infoListener);


        try {
            recorder.prepare();
            recorder.start();

            //  binding.recordButton.setRecordView(binding.recordView);


        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            AppLog.logString("Error: " + what + ", " + extra);
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            AppLog.logString("Warning: " + what + ", " + extra);
        }
    };

    private void stopRecording() {
        if (null != recorder) {
            recorder.stop();
            recorder.reset();
            recorder.release();

            recorder = null;


            SendRecordAudioApi(FILEPATH);

        }
    }*/


    private void StartRecording() {

        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setSpeakerphoneOn(true);


        recorder = new MediaRecorder();
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {

            recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
        } else {

            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        }
//        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // recorder.setOutputFormat(output_formats[currentFormat]);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(getFilename());
        recorder.setOnErrorListener(errorListener);
        recorder.setOnInfoListener(infoListener);


        try {
            recorder.prepare();
            recorder.start();
            recordstarted = true;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void StopRecording() {
        audioManager.setSpeakerphoneOn(false);

        try {
            if (null != recorder) {
                recorder.stop();
                recorder.reset();
                recorder.release();
                recordstarted = false;
                recorder = null;
//                SendRecordAudioApi(FILEPATH);
            }
        } catch (RuntimeException stopException) {

            Log.d(TAG, "StopRecording: "+ stopException);
        }


    }

    private String getFilename() {

        Random rand = new Random();
        abcd = rand.nextInt(1000);
        Log.e("kjhgfgh",abcd+"");

        String filepath = Environment.getExternalStorageDirectory().getPath();
         file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }

        Log.e("fggffgf",  "Pasha"+file.getAbsolutePath() + "/"+abcd+"_" + phoneNr + file_exts[currentFormat]);
        FILEPATH = new File(file.getAbsolutePath() + "/" +abcd+"_" + phoneNr + file_exts[currentFormat]);

        return (file.getAbsolutePath() + "/"+abcd+"_" + phoneNr + file_exts[currentFormat]);
        //return (file.getAbsolutePath() + "/" + "Qasim" + file_exts[currentFormat]);

    }

    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Toast.makeText(TService.this, "Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            Toast.makeText(TService.this,"Warning: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
        }
    };

    ////////////////////////////////SendData////////////////
    private void SendRecordAudioApi(File audioFilePath) {
//    private void SendRecordAudioApi(String audioFilePath) {

        Log.d("kjhgfd", audioFilePath + "");

        apiUrlInterfaces = APIClient.getRetrofitClient().create(ApiUrlInterfaces.class);

        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), audioFilePath);

        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("first_image", audioFilePath.getName(), requestBody);
//        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("first_image", audioFilePath, requestBody);


        Call<modelUserVideoRecord> uploadProfile = apiUrlInterfaces.uploadVideo(fileToUpload);

        uploadProfile.enqueue(new Callback<modelUserVideoRecord>() {
            @Override
            public void onResponse(Call<modelUserVideoRecord> call, Response<modelUserVideoRecord> response) {
                Log.d("Ewqeqwewqeqw", new Gson().toJson(response.body()));

                Toast.makeText(TService.this, "File Upload Success", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<modelUserVideoRecord> call, Throwable t) {

                t.getMessage();

            }
        });

    }

    private void encodeAudio(String selectedPath) {

        byte[] audioBytes;
        try {

            // Just to check file size.. Its is correct i-e; Not Zero
            File audioFile = new File(selectedPath);
            long fileSize = audioFile.length();
            Log.d("Hamid", "FileSize: " + fileSize);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            FileInputStream fileInputStream = new FileInputStream(new File(selectedPath));
            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fileInputStream.read(buf)))
                byteArrayOutputStream.write(buf, 0, n);
            audioBytes = byteArrayOutputStream.toByteArray();

            // Here goes the Base64 string
            String _audioBase64 = Base64.encodeToString(audioBytes, Base64.DEFAULT);
            Log.d("Hamid", "encodeAudio: " + _audioBase64);

//            SendRecordAudioApi(new File(fileLast.getAbsolutePath()));
//            SendRecordAudioApi(_audioBase64);

        } catch (Exception e) {

            e.getMessage();
        }


    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub

        Log.e("lkjhhj", "Qasim0");

        return null;
    }

    @Override
    public void onDestroy() {
        Log.d("service", "destroy");

        super.onDestroy();

    }

}
