package com.example.ivrecording.callrecorder;

import static android.content.Context.MODE_PRIVATE;

import static com.example.ivrecording.Database.IvrDatabase.Table_Call_Reports;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ivrecording.CallingRetrofitApi.ApiUrlInterfaces;
import com.example.ivrecording.Database.IvrDatabase;
import com.example.ivrecording.Extra.Common;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PhoneStateTracker {

    Context ctx;
    TelephonyManager tm;
    OutgoingReceiver outgoingReceiver;
    CallStateListener callStateListener;
    String contact_no = "", callType = "", call_status = "", file_name = "";
    int call_duration = 0;

    public PhoneStateTracker(Context ctx) {
        this.ctx = ctx;
        callStateListener = new CallStateListener();
        outgoingReceiver = new OutgoingReceiver();
    }

    public void start() {
        try {
            tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
            tm.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
            ctx.registerReceiver(outgoingReceiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop(Context applicationContext) {
        System.out.println("**********Inside STOP DETECTING method*********");
        tm = (TelephonyManager) applicationContext.getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(callStateListener, PhoneStateListener.LISTEN_NONE);
        try {
            if (outgoingReceiver != null) {
                applicationContext.unregisterReceiver(outgoingReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getLatestFilefromDir(String dirPath) {
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }

        File lastModifiedFile = files[0];
        for (int i = 1; i < files.length; i++) {
            if (lastModifiedFile.lastModified() < files[i].lastModified()) {
                lastModifiedFile = files[i];
            }
        }
        return lastModifiedFile.getName();
    }

    private void getSystemRecording(String recording_store_path, String number) {
        try {
            String latest_file_name;
            latest_file_name = getLatestFilefromDir(recording_store_path);
            boolean isFound = false;
            try {
                 isFound = latest_file_name.contains(number);
            }
            catch (Exception e){
                e.printStackTrace();
            }

            if (isFound) {
                file_name = latest_file_name;
            } else {
                int index = number.indexOf(",");
                if (index > 0) {
                    String tmp_num = number.substring(0, index);
                    if (latest_file_name.contains(tmp_num)) {
                        file_name = latest_file_name;
                    }
                } else {
                    boolean isNameFound = false;
                    try {
                        isNameFound = latest_file_name.contains(getContactName(ctx, number));
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                    if (isNameFound) {
                        file_name = latest_file_name;
                    } else {
                        file_name = "Not Available";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getContactName(Context context, String ContactNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(ContactNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return contactName;
    }

    public void call_detail(Context context) {
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String recording_store_path = "", uploadFilePath = "", start_time = "", extension = "NA", recording = "NA";
        callType = "";
        call_status = "";

        //Get Recording storage path of mobile device from shared prefs
        SharedPreferences sharedPreference = context.getSharedPreferences(Common.UserData, MODE_PRIVATE);
        recording_store_path = sharedPreference.getString("recording_details", "");
        //Fetch recording name from default storage
        recording_store_path = Environment.getExternalStorageDirectory().getPath() + recording_store_path;

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //format do you want date and time

        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
        /* Query the CallDetail Content Provider */
        Cursor managedCursor = context.getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, null, null, strOrder);

        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);

        int type = managedCursor.getColumnIndex(android.provider.CallLog.Calls.TYPE);

        int dat = managedCursor.getColumnIndex(android.provider.CallLog.Calls.DATE);

        int duration = managedCursor.getColumnIndex(android.provider.CallLog.Calls.DURATION);

        String phNum;
        if (managedCursor.moveToFirst()) {
            phNum = managedCursor.getString(number);

            String prefix = phNum.substring(0, 3);
            if (prefix.equals("+91")) {
                contact_no = phNum.replace("+91", "").trim();
            } else {
                contact_no = phNum;
            }

            String strcallDate = managedCursor.getString(dat);

            Date callDate = new Date(Long.parseLong(strcallDate));
            start_time = df.format(callDate);

            String end_time = df.format(Calendar.getInstance().getTime());

            String callTypeCode = managedCursor.getString(type);

            String callDuration = managedCursor.getString(duration);

            call_duration = Integer.parseInt(callDuration);

            int callcode = Integer.parseInt(callTypeCode);

            switch (callcode) {
                case android.provider.CallLog.Calls.OUTGOING_TYPE:
                    callType = "Outgoing";
                    break;
                case android.provider.CallLog.Calls.INCOMING_TYPE:
                    callType = "Incoming";
                    break;
                case android.provider.CallLog.Calls.MISSED_TYPE:
                    callType = "Missed";
                    break;
            }
            try {
                if (callType.equals("Missed")) {
                    call_status = "Missed";
                } else if (call_duration == 0) {
                    call_status = "Not Answered";
                } else {
                    call_status = "Answered";
                    getSystemRecording(recording_store_path, contact_no);
                    if (!file_name.equals("Not Available") && !file_name.equals("")) {
                        //Upload recording
                        File sampleDir = new File(recording_store_path);
                        uploadFilePath = sampleDir + "/" + file_name;
                        File file = new File(uploadFilePath);
                        if (file.exists()) {
//                            Toast.makeText(ctx, file_name, Toast.LENGTH_LONG).show();
                            extension = file_name.substring(file_name.lastIndexOf(".") + 1);
                            recording = convertAudio(uploadFilePath, file_name);
                            //Move recorded file to internal app directory
                            MoveFile(uploadFilePath, file_name);
                        }
                    }
                }

                SharedPreferences sharedPref = context.getSharedPreferences("UserData", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("calling_number", "");
                editor.apply();

                //Insert in Local Table
                IvrDatabase ivrDatabase = new IvrDatabase(context);
                SQLiteDatabase sql_db_write = ivrDatabase.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(ivrDatabase.Table_Call_Reports_Number, contact_no);
                values.put(ivrDatabase.Table_Call_Reports_Call_Type, callType);
                values.put(ivrDatabase.Table_Call_Reports_Duration, String.valueOf(call_duration));
                values.put(ivrDatabase.Table_Call_Reports_Call_Status, call_status);
                values.put(ivrDatabase.Table_Call_Reports_Start_Time, start_time);
                values.put(ivrDatabase.Table_Call_Reports_End_Time, end_time);
                values.put(ivrDatabase.Table_Call_Reports_Recording, recording);
                values.put(ivrDatabase.Table_Call_Reports_File_Extension, extension);
                values.put(ivrDatabase.Table_Call_Reports_Filepath, uploadFilePath);
                sql_db_write.insert(Table_Call_Reports, null, values);

                //hit API Here
                //UpdateReport updateReport = new UpdateReport();
                //updateReport.fetchFromDb(context);
                Common.startBackgroundTask(context);
//            UploadRecordingApi(ctx, contact_no, callType, call_status, String.valueOf(call_duration), start_time, end_time, recording, extension);

                Intent intent = new Intent(ctx, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        managedCursor.close();
    }

    public class OutgoingReceiver extends BroadcastReceiver {
        public OutgoingReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("**********Inside BroadcaseReceiver class for detecting outgoing calls*********");
            String outgoing_number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Toast.makeText(context, "Outgoing: " + outgoing_number, Toast.LENGTH_LONG).show();
        }
    }

    private class CallStateListener extends PhoneStateListener {

        public CallStateListener() {
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Toast.makeText(ctx, "Incoming: " + incomingNumber, Toast.LENGTH_LONG).show();
                    SharedPreferences sharedPref = ctx.getSharedPreferences("UserData", MODE_PRIVATE);
                    SharedPreferences.Editor edit = sharedPref.edit();
                    edit.putString("calling_number", incomingNumber);
                    edit.apply();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if (!incomingNumber.equals("")) {
                        SharedPreferences sharedPrefs = ctx.getSharedPreferences("UserData", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPrefs.edit();
                        editor.putString("calling_number", incomingNumber);
                        editor.apply();

                        Toast.makeText(ctx, "Offhook: " + incomingNumber, Toast.LENGTH_LONG).show();
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    SharedPreferences sharedPreference = ctx.getSharedPreferences("UserData", MODE_PRIVATE);
                    String calling_number = sharedPreference.getString("calling_number", "");
                    if (!calling_number.equals("")) {
                        call_detail(ctx);
                    }
                    break;
            }
        }

    }

    private void MoveFile(String filepath, String filename) {
        try {
            File sourceLocation = new File(filepath);
            String sampleDir = ctx.getExternalFilesDir(Environment.DIRECTORY_MUSIC).getPath();
            File targetLocation = new File(sampleDir + "/" + filename);
            sourceLocation.renameTo(targetLocation);

            //Delete File
//            sourceLocation.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String convertAudio(String upload_file_path, String filename) {
        String bsfile = "";
        try {
            Uri url = Uri.parse(upload_file_path);

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(ctx, url);

            File file = new File(url.getPath());

            byte[] ByteArray = new byte[(int) file.length()];
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                fileInputStream.read(ByteArray);
                byte[] encodeFile = Base64.encode(ByteArray, 0);
                bsfile = new String(encodeFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bsfile;
    }

    private void UploadRecordingApi(Context m_context, String number, String call_type, String call_status, String call_duration, String start_time, String end_time, String recording, String extension) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(m_context);
            String URL = ApiUrlInterfaces.BASE_URL + "updateReport";
            SharedPreferences sharedPreferences = m_context.getSharedPreferences(Common.UserData, MODE_PRIVATE);
            String UserToken = sharedPreferences.getString(Common.UserToken, "");
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(m_context, "Report Processed", Toast.LENGTH_LONG).show();
                    //Handle your success code here
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Handle your error code here
                    Toast.makeText(m_context, "Something Went Wrong", Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    params.put("Authorization", "Bearer " + UserToken);
                    return params;
                }

                //Pass Your Parameters here
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<>();
                    params.put("customer_no", number);
                    params.put("call_type", call_type);
                    params.put("call_status", call_status);
                    params.put("call_duration", call_duration);
                    params.put("call_start_time", start_time);
                    params.put("call_end_time", end_time);
                    params.put("call_recording", recording);
                    params.put("file_extension", extension);
                    return params;
                }
            };
            int socketTimeout = 90000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
