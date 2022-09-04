package com.example.ivrecording.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import com.example.ivrecording.CallingRetrofitApi.GlobalClassForAllApi;
import com.example.ivrecording.Extra.Common;
import com.example.ivrecording.LottieDialogFragment;
import com.example.ivrecording.R;
import com.example.ivrecording.callrecorder.MainActivity;
import com.example.ivrecording.databinding.ActivityLoginBinding;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private CompositeDisposable disposable = new CompositeDisposable();
    private LottieDialogFragment mDialogFragment;
    private String Email, Password, deviceName;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        sharedPreferences = getSharedPreferences(Common.UserData, MODE_PRIVATE);

        editor = sharedPreferences.edit();

        deviceName = android.os.Build.MANUFACTURER;

        Initialization();

    }

    private void Initialization() {
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Validation();
            }
        });
    }

    private void Validation() {

        Email = binding.etEmailOrMobile.getText().toString();
        Password = binding.etPassword.getText().toString();


        if (TextUtils.isEmpty(Email)) {
            binding.etEmailOrMobile.setError("Enter Email Id");
            binding.etEmailOrMobile.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(Password)) {
            binding.etPassword.setError("Enter Password");
            binding.etPassword.requestFocus();
            return;
        }


//        CallLoginApi();
        LoginApi();

    }

    private void CallLoginApi() {
        showProgressDialog();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("email", Email);
        hashMap.put("password", Password);
        hashMap.put("model_no", deviceName);

        Log.e("fgfghhf", hashMap + "");

        disposable.add(GlobalClassForAllApi.initRetrofit().LoginData(hashMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe((user, throwatble) -> {
                            hideProgressDialog();
                            if (user != null) {

                                Log.e("jfgkdshe", "Response size: " + new Gson().toJson(user));

                                if (user.getStatus().equals(200)) {
                                    Log.e("jkhkf", "Response size: " + new Gson().toJson(user));

                                    Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show();

                                    editor.putString(Common.UserToken, user.getData().getToken());
                                    editor.putString(Common.UserToken, user.getData().getUserData().getRecordingStorePath());
                                    editor.apply();
                                    editor.commit();

                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finishAffinity();
                                } else {
                                    Toast.makeText(this, "Credentials no matched", Toast.LENGTH_SHORT).show();

                                    Log.e("", "" + new Gson().toJson(throwatble));
                                    hideProgressDialog();

                                }
                            } else {

                                hideProgressDialog();
                            }

                        }
                )
        );
    }

    private void showProgressDialog() {

        mDialogFragment = new LottieDialogFragment();
        mDialogFragment.show(getSupportFragmentManager(), "");
    }

    private void hideProgressDialog() {
        mDialogFragment.dismiss();
    }

    private void LoginApi() {
        try {
            showProgressDialog();
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            String URL = ApiUrlInterfaces.BASE_URL + "login?email=" + Email + "&password=" + Password + "&model_no=" + deviceName;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    LoginApiSucces(response);
                    //Handle your success code here
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Handle your error code here
                    hideProgressDialog();
                    Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }

//                //Pass Your Parameters here
//                @Override
//                protected Map<String, String> getParams() {
//
//                    Map<String, String> params = new HashMap<>();
//                    params.put("email", Email);
//                    params.put("password", Password);
//                    params.put("model_no", deviceName);
//                    return params;
//                }
            };
            int socketTimeout = 90000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            hideProgressDialog();
            e.printStackTrace();
        }
    }

    public void LoginApiSucces(String result) {
        String user_name, user_data, status, api_token, data, user_email, user_id, department_id, role_id, user_mobile, recording_details = "", setting_key = "", setting_value = "", app_license, crm_popup_type, crm_type, repeat_index = "0", app_outbound_process = "1", app_outbound_number = "", crm_form_id = "";
        try {
            hideProgressDialog();
            JSONObject jsonObject1 = new JSONObject(result);
            status = jsonObject1.getString("status");
            if (status.equals("200")) {

                data = jsonObject1.getString("data");
                if (!data.equals("")) {
                    try {
                        Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show();

                        JSONObject jsonObject = new JSONObject(data.trim());
                        api_token = jsonObject.getString("token");
                        user_data = jsonObject.getString("user_data");
                        JSONObject userdata = new JSONObject(user_data);
                        user_name = userdata.getString("name");
                        user_email = userdata.getString("email");
                        user_mobile = userdata.getString("mobile");
                        department_id = userdata.getString("department_id");
                        role_id = userdata.getString("role_id");
                        recording_details = userdata.getString("recording_details");
                        Log.e("TAG", "LoginApiSucces: "+recording_details );
                        editor.putString(Common.UserToken, api_token);
                        editor.putString("mobile", user_mobile);
                        editor.putString("user_name", user_name);
                        editor.putString("user_email", user_email);
                        editor.putString("department_id", department_id);
                        editor.putString("role_id", role_id);
                        editor.putString("recording_details", recording_details);
                        editor.apply();
                        editor.commit();

                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finishAffinity();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), "Credentials no matched", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Something bad happened", Toast.LENGTH_SHORT).show();
        }
    }
}