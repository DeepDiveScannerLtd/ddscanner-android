package com.ddscanner.rest;


import com.ddscanner.entities.request.RegisterRequest;
import com.ddscanner.entities.request.SendReviewRequest;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit.mime.TypedFile;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
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
    @Multipart
    Call<ResponseBody> addSealife(@Part("name") String name, @Part("distribution") String distribution, @Part("habitat") String habitat, @Part("image") TypedFile image, @Part("scName") String scName, @Part("length") String length, @Part("weight") String weight, @Part("depth") String depth, @Part("order") String order, @Part("class") String sealifeClass, @Part("token") String token, @Part("social") String sn, @Part("secret") String secret);

}
