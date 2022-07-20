package com.example.ivrecording.CallingRetrofitApi;



import com.example.ivrecording.Model.modelLoginData;
import com.example.ivrecording.Model.modelProfileData;
import com.example.ivrecording.Model.modelUserVideoRecord;

import java.util.HashMap;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;

public interface ApiUrlInterfaces {

    String BASE_URL="https://royalpepperbanquets.in/api/";
    String BASE_URLNEW="https://royalpepperbanquets.in/api/";



   /* @FormUrlEncoded
    @POST("register")
    Single<modelCommonModel> LoginData(@FieldMap HashMap<String, Object> hashMap
            *//*@Header("unique") String apiKey*//*);
*/

    ///##### for Get Method

    @GET("login")
    Single<modelLoginData> LoginData(@QueryMap HashMap<String, Object> hashMap);


    @GET("getProfile")
    Single<modelProfileData> ProfileData(@Header("Authorization") String Token);


/////////////////TestData
    @Multipart
    @POST("add-image.php")
    Call<modelUserVideoRecord> uploadVideo(@Part MultipartBody.Part postAudio);



}
