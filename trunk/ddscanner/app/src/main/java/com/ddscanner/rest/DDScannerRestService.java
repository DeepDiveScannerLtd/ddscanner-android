package com.ddscanner.rest;


import com.ddscanner.entities.request.IdentifyRequest;
import com.ddscanner.entities.request.RegisterRequest;
import com.ddscanner.entities.request.ValidationReguest;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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
    Call<ResponseBody> getDiveSpotById(@Path("id") String id, @QueryMap Map<String, String> map);

    @Headers("Content-type: application/json")
    @GET("/diving/divespots")
    Call<ResponseBody> getDivespots(@QueryMap Map<String, Object> map);

    @POST("/diving/login")
    Call<ResponseBody> registerUser(@Body RegisterRequest registerRequest);

    @POST("/diving/divespot/{id}/validation")
    Call<ResponseBody> divespotValidation(
            @Path("id") String id,
            @Body ValidationReguest validationReguest
            );

    @POST("/diving/divespot/{id}/favorite")
    Call<ResponseBody> addDiveSpotToFavourites(
            @Path("id") String id,
            @Body RegisterRequest registerRequest
            );

    @DELETE("/diving/divespot/{id}/favorite")
    Call<ResponseBody> deleteDiveSpotFromFavourites(
            @Path("id") String id,
            @Body RegisterRequest registerRequest
            );

    @POST("/diving/divespot/comment")
    @Multipart
    Call<ResponseBody> addCommentToDiveSpot(
            @Part("diveSpotId") RequestBody id,
            @Part("comment") RequestBody comment,
            @Part("rating") RequestBody rating,
            @Part List<MultipartBody.Part> image,
            @Part("token") RequestBody token,
            @Part("social") RequestBody sn,
            @Part("secret") RequestBody secret
    );

    @POST("/diving/identify")
    Call<ResponseBody> identify(@Body IdentifyRequest identifyRequest);

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
            @Part("visibilityMin") RequestBody visibilityMin,
            @Part("visibilityMax") RequestBody visibilityMax,
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

    @POST("diving/divespot/{id}/checkin")
    Call<ResponseBody> checkIn(
      @Path("id") String id,
      @Body RegisterRequest registerRequest
      );

    @DELETE("diving/divespot/{id}/checkin")
    Call<ResponseBody> checkOutUser( @Path("id") String id,
                                     @QueryMap Map<String, String> map);

    @POST("diving/divespot/{id}")
    @Multipart
    Call<ResponseBody> updateDiveSpot(
            @Path("id") String id,
            @Part("_method") RequestBody _method,
            @Part("name") RequestBody name,
            @Part("lat") RequestBody lat,
            @Part("lng") RequestBody lng,
            @Part("depth") RequestBody depth,
            @Part("visibilityMin") RequestBody visibilityMin,
            @Part("visibilityMax") RequestBody visibilityMax,
            @Part("currents") RequestBody currents,
            @Part("level") RequestBody level,
            @Part("object") RequestBody object,
            @Part("access") RequestBody access,
            @Part("description") RequestBody description,
            @Part List<MultipartBody.Part> sealife,
            @Part List<MultipartBody.Part> image_new,
            @Part List<MultipartBody.Part> image_del,
            @Part("token") RequestBody token,
            @Part("social") RequestBody sn,
            @Part("secret") RequestBody secret
    );

    @GET("diving/divespot/{id}/edit")
    Call<ResponseBody> getDiveSpotForEdit(@Path("id") String id, @QueryMap Map<String, String> map);

    @POST("diving/divespot/comment/{id}/dislike")
    Call<ResponseBody> dislikeComment(
      @Path("id") String id,
      @Body RegisterRequest registerRequest
    );

    @POST("diving/divespot/comment/{id}/like")
    Call<ResponseBody> likeComment(
      @Path("id") String id,
      @Body RegisterRequest registerRequest
    );

    @GET("diving/divespot/{id}/checkins")
    Call<ResponseBody> getCheckins(@Path("id") String id);

    @GET("diving/divespot/{id}/comments")
    Call<ResponseBody> getComments(@Path("id") String id, @QueryMap Map<String, String> map);

    @GET("diving/user/{id}")
    Call<ResponseBody> getUserInfo(@Path("id") String id);

    @POST("diving/user/{id}")
    @Multipart
    Call<ResponseBody> updateUserById(
            @Path("id") String id,
            @Part("_method") RequestBody _method,
            @Part MultipartBody.Part image,
            @Part("name") RequestBody name,
            @Part("username") RequestBody username,
            @Part("about") RequestBody about,
            @Part("token") RequestBody token,
            @Part("social") RequestBody sn,
            @Part("secret") RequestBody secret
    );

    @GET("diving/user/{id}/divespot/checkins")
    Call<ResponseBody> getUsersCheckins(@Path("id") String id, @QueryMap Map<String, String> map);

    @GET("diving/user/{id}/divespot/favorites")
    Call<ResponseBody> getUsersFavorites(@Path("id") String id, @QueryMap Map<String, String> map);

    @DELETE("diving/divespot/{id}/favorite")
    Call<ResponseBody> removeSpotFromFavorites( @Path("id") String id,
                                     @QueryMap Map<String, String> map);

    @GET("diving/user/{id}/divespot/added")
    Call<ResponseBody> getUsersAdded(@Path("id") String id, @QueryMap Map<String, String> map);

    @GET("diving/user/{id}/divespot/edited")
    Call<ResponseBody> getUsersEdited(@Path("id") String id, @QueryMap Map<String, String> map);

    @POST("diving/logout")
    Call<ResponseBody> logout(@Body RegisterRequest registerRequest);

    @GET("diving/user/{id}/notifications")
    Call<ResponseBody> getNotifications(@Path("id") String id, @QueryMap Map<String, String> map);

    @POST("diving/divespot/{id}/images")
    @Multipart
    Call<ResponseBody> addImagesToDiveSpot(
            @Path("id") String id,
            @Part("_method") RequestBody _method,
            @Part List<MultipartBody.Part> images,
            @Part("token") RequestBody token,
            @Part("social") RequestBody sn,
            @Part("secret") RequestBody secret
    );

    @GET("/diving/divespot/{id}/editors")
    Call<ResponseBody> getDiveSpotEditors(@Path("id") String id, @QueryMap Map<String, String> map);

    @POST("diving/divespot/search")
    @Multipart
    Call<ResponseBody> getDivespotsByParameters(
            @Part("search") RequestBody search,
            @Part List<MultipartBody.Part> like,
            @Part("order") RequestBody order,
            @Part("sort") RequestBody sort,
            @Part("limit") RequestBody limit,
            @Part List<MultipartBody.Part> select
    );

    @GET("diving/user/{id}/comment/likes")
    Call<ResponseBody> getForeignUserLikes(@Path("id") String userId, @QueryMap Map<String, String> map);

    @GET("diving/user/{id}/comment/dislikes")
    Call<ResponseBody> getForeignUserDislikes(@Path("id") String userId, @QueryMap Map<String, String> map);

    @DELETE("diving/divespot/comment/{id}")
    Call<ResponseBody> deleteComment( @Path("id") String id, @QueryMap Map<String, String> map);
}
