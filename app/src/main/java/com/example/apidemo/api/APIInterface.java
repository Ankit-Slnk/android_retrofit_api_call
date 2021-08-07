package com.example.apidemo.api;

import com.example.apidemo.models.CommonResponse;
import com.example.apidemo.models.UserResponse;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public interface APIInterface {

    @GET("users")
    @Headers({"Accept: application/json"})
    Call<UserResponse> getUsers();

//    @GET("apiName/{id}")
//    @Headers({"Accept: application/json"})
//    Call<CommonResponse> apiName(@Path("id") String id);

//    @POST("apiName")
//    @Headers({"Accept: application/json"})
//    Call<CommonResponse> apiName(@Header("Authorization") String token, @Body CommonResponse request);

    @Multipart
    @POST("UploadProfilePic")
    Call<CommonResponse> uploadProfilePhoto(@PartMap Map<String, RequestBody> map);

}
