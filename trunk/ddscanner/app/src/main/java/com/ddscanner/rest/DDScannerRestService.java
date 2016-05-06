package com.ddscanner.rest;


import com.ddscanner.entities.request.RegisterRequest;
import com.ddscanner.entities.request.SendReviewRequest;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit.mime.MultipartTypedOutput;
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
    Call<ResponseBody> addSealife(
            @Part("name") RequestBody name,
            @Part("distribution") RequestBody distribution,
            @Part("habitat") RequestBody habitat,
            @Part MultipartBody.Part image,
            @Part("scName") RequestBody scName,
            @Part("length") RequestBody length,
            @Part("weight") RequestBody weight,
            @Part("depth") RequestBody depth,
            @Part("order") RequestBody order,
            @Part("scClass") RequestBody sealifeClass,
            @Part("token") RequestBody token,
            @Part("social") RequestBody sn,
            @Part("secret") RequestBody secret);

    @POST("diving/divespot")
    @Multipart
    Call<ResponseBody> addDiveSpot(
            @Part("name") RequestBody name,
            @Part("lat") RequestBody lat,
            @Part("lng") RequestBody lng,
            @Part("depth") RequestBody depth,
            @Part("visibility") RequestBody visibility,
            @Part("currents") RequestBody currents,
            @Part("level") RequestBody level,
            @Part("object") RequestBody object,
            @Part("access") RequestBody access,
            @Part("description") RequestBody description,
            @Part List<MultipartBody.Part> sealife,
            @Part List<MultipartBody.Part> image,
            @Part("token") RequestBody token,
            @Part("social") RequestBody sn,
            @Part("secret") RequestBody secret
            );

    @GET("diving/sealife")
    Call<ResponseBody> getSealifes();

}
