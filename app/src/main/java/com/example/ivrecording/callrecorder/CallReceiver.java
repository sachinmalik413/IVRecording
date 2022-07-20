package com.example.ivrecording.callrecorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class CallReceiver extends BroadcastReceiver {
    private MediaRecorder recorder = null;
    AudioManager audioManager;
   /* private AudioRecorder audioRecorder;
    private File recordFile;*/
    @Override
    public void onReceive(Context context, Intent intent) {



//        if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
//
//          /*  audioRecorder = new AudioRecorder();
//
//            recordFile = new File(context.getFilesDir(), UUID.randomUUID().toString() + ".mp3");
//
//            //recordFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Test/",UUID.randomUUID().toString() + ".mp3");
//
//            Log.d("mkjlk", recordFile+"");
//
//            try {
//                audioRecorder.start(recordFile.getPath());
//                // audioRecorder.start(recordFile.getAbsolutePath());
//                Log.d("RecordView", audioRecorder+"");
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            Log.d("RecordView", "onStart");
//           // Toast.makeText(context, "OnStartRecord", Toast.LENGTH_SHORT).show();
//*/
//
//            showToast(context,"Call started...");
//
//            context.startService(new Intent(context, TService.class));
//
//
//
//            Log.e("kjhg","Call started...");
//        }
//        else if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE)){
//            showToast(context,"Call ended...");
//            Log.e("kjhg","Call ended...");
//
//            /*audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//            audioManager.setMode(AudioManager.MODE_IN_CALL);
//            audioManager.setSpeakerphoneOn(true);
//
//            StopRecording();
//*/
//
//
//           // stopRecording(true);
//          //  context.stopService(new Intent(context, TService.class));
//        }
//        else if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)){
//            showToast(context,"Incoming call...");
//
//            context.startService(new Intent(context, TService.class));
//            Log.e("kjhg","Incoming call...");
//        }


    }

    void showToast(Context context,String message){
        Toast toast=Toast.makeText(context,message,Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

   /* private void stopRecording(boolean deleteFile) {
        audioRecorder.stop();
        if (recordFile != null && deleteFile) {
            recordFile.delete();
        }

        File file1 = new File(recordFile.getAbsolutePath());

        Log.e("Qasim12345", recordFile.getPath() + "");



    }
*/


    private void StopRecording() {
        audioManager.setSpeakerphoneOn(false);

        try {
            if (null != recorder) {
                recorder.stop();
                recorder.reset();
                recorder.release();

                recorder = null;
            }
        } catch (RuntimeException stopException) {

        }


    }

}
