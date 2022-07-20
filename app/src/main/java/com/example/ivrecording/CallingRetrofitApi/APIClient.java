package com.example.ivrecording.CallingRetrofitApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {


    public static Retrofit retrofit = null;


    public static synchronized Retrofit getRetrofitClient() {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(ApiUrlInterfaces.BASE_URLNEW)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
