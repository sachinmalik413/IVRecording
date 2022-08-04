package com.example.ivrecording.callrecorder;

import static android.content.Context.MODE_PRIVATE;
import static com.example.ivrecording.Database.IvrDatabase.Table_Call_Reports;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
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

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class UpdateReport {
    Context m_context;
    IvrDatabase ivrDatabase;

    public void fetchFromDb(Context context) {
        try {
            m_context = context;
            NetworkChecking networkChecking = new NetworkChecking();
            boolean network_status = networkChecking.internet_checking(context);
            if (network_status) {
                ivrDatabase = new IvrDatabase(context);
                SQLiteDatabase sql_db_read = ivrDatabase.getReadableDatabase();
                Cursor cursor = sql_db_read.rawQuery(" Select * from " + Table_Call_Reports + " Where " + ivrDatabase.Table_Call_Reports_Sync_Status + " = 'No'", null);
                int count = cursor.getCount();
                if (count > 0) {
                    while (cursor.moveToNext()) {
                        String id = cursor.getString(cursor.getColumnIndex(ivrDatabase.Table_Call_Reports_Id));
                        String number = cursor.getString(cursor.getColumnIndex(ivrDatabase.Table_Call_Reports_Number));
                        String call_status = cursor.getString(cursor.getColumnIndex(ivrDatabase.Table_Call_Reports_Call_Status));
                        String call_type = cursor.getString(cursor.getColumnIndex(ivrDatabase.Table_Call_Reports_Call_Type));
                        String duration = cursor.getString(cursor.getColumnIndex(ivrDatabase.Table_Call_Reports_Duration));
                        String recording = cursor.getString(cursor.getColumnIndex(ivrDatabase.Table_Call_Reports_Recording));
                        String filepath = cursor.getString(cursor.getColumnIndex(ivrDatabase.Table_Call_Reports_Filepath));
                        String file_extension = cursor.getString(cursor.getColumnIndex(ivrDatabase.Table_Call_Reports_File_Extension));
                        String start_time = cursor.getString(cursor.getColumnIndex(ivrDatabase.Table_Call_Reports_Start_Time));
                        String end_time = cursor.getString(cursor.getColumnIndex(ivrDatabase.Table_Call_Reports_End_Time));
                        //Update Status
                        if(call_type.trim().isEmpty()){
                            call_type = "Incoming";
                        }
                        updateStatus("Processing", id);

                        UploadRecordingApi(m_context, number, call_type, call_status, duration, start_time, end_time, recording, file_extension, id, filepath);
                    }
                }
                cursor.close();
            } else {
                Toast.makeText(m_context, "No Internet", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void UploadRecordingApi(Context m_context, String number, String call_type, String call_status, String call_duration, String start_time, String end_time, String recording, String extension, String id, String filepath) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(m_context);
            String URL = ApiUrlInterfaces.BASE_URL + "updateReport";
            SharedPreferences sharedPreferences = m_context.getSharedPreferences(Common.UserData, MODE_PRIVATE);
            String UserToken = sharedPreferences.getString(Common.UserToken, "");
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //Handle your success code here
                    String data = number+" "+call_type+" "+call_status+" "+call_duration+" "+start_time+" "+filepath+" :"+recording.length();
                    AfterUploadTask(response, id, filepath,data);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Handle your error code here
                    String data = number+" "+call_type+" "+call_status+" "+call_duration+" "+start_time+" "+filepath;
                    data = data+" apistatus:"+error.networkResponse.toString()+" , recording retained \n \n";
                    writeToFile(data);
                    Toast.makeText(m_context, "Something Went Wrong", Toast.LENGTH_LONG).show();
                    updateStatus("No", id);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    params.put("Authorization", "Bearer " + UserToken);
                    Log.e("TAG", "getHeaders: " + params);
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
                    Log.e("TAG", "getParams: " + params);
                    return params;
                }
            };
            int socketTimeout = 90000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
            updateStatus("No", id);
        }
    }

    private void AfterUploadTask(String result, String id, String filepath, String data) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            String status = jsonObject.getString("status");
            if (status.equals("200")) {
                //Delete file from device
                data = data+" apistatus:200, recording deleted \n \n";
                writeToFile(data);
                File sourceLocation = new File(filepath);
                sourceLocation.delete();
                //Delete entry from DB
                SQLiteDatabase sql_db_write = ivrDatabase.getWritableDatabase();
                String query2 = "DELETE FROM " + Table_Call_Reports + " WHERE id=" + id;
                sql_db_write.execSQL(query2);
                sql_db_write.close();
                Toast.makeText(m_context, "Report Updated", Toast.LENGTH_LONG).show();
            } else {
                data = data+" apistatus:"+status+" , recording retained \n \n";
                writeToFile(data);
                updateStatus("No", id);
                Toast.makeText(m_context, "Report Not Updated", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateStatus(String status, String id) {
        //Update Status
        SQLiteDatabase sql_db_write = ivrDatabase.getWritableDatabase();
        String query2 = "UPDATE " + Table_Call_Reports + " SET " + ivrDatabase.Table_Call_Reports_Sync_Status + " ='" + status + "' WHERE id=" + id;
        sql_db_write.execSQL(query2);
        sql_db_write.close();
    }


    private void writeToFile(String data) {
        File dir = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" + "IVR");
        }
        else
        {
            dir = new File(Environment.getExternalStorageDirectory() + "/" + "IVR");
        }

        // Make sure the path directory exists.
        if (!dir.exists())
        {
            // Make it, if it doesn't exit
            boolean success = dir.mkdirs();
            if (!success)
            {
                dir = null;
            }
        }
        try
        {
            //File dir = new File(fullPath);

            OutputStream fOut = null;
            File file = new File(dir, "ivrlogs.txt");
            fOut = new FileOutputStream(file,true);
            fOut.write(data.getBytes());
            fOut.flush();
            fOut.close();
        }
        catch (Exception e)
        {
            Log.e("saveToExternalStorage()", e.getMessage());
        }
    }


}
