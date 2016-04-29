package com.ddscanner.rest;


import com.ddscanner.entities.request.CreateSealifeRequest;
import com.ddscanner.entities.request.RegisterRequest;
import com.ddscanner.entities.request.SendReviewRequest;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface DDScannerRestService {

    @GET("/diving/filters")
    Call<ResponseBody> getFilters();

    @GET("/diving/divecenters")
    Call<ResponseBody> getDiveCenters(@QueryMap Map<String, String> map);

    @GET("/diving/divespot/{id}")
    Call<ResponseBody> getDiveSpotById(@Path("id") String id);

    @Headers("Content-type: application/json")
    @GET("/diving/divespots")
    Call<ResponseBody> getDivespots(@QueryMap Map<String, Object> map);

    @POST("/diving/register")
    Call<ResponseBody> registerUser(@Body RegisterRequest registerRequest);

    @POST("/diving/comment")
    Call<ResponseBody> addCOmmentToDiveSpot(@Body SendReviewRequest sendReviewRequest);

    @POST("/diving/identify")
    Call<ResponseBody> identifyGcmToken(@Body String token);

    @POST("/diving/sealife")
    Call<ResponseBody> addSealife(@Body CreateSealifeRequest createSealifeRequest);

}
