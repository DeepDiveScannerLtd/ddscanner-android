package com.ddscanner.rest;


import com.ddscanner.entities.request.IdentifyRequest;
import com.ddscanner.entities.request.RegisterRequest;
import com.ddscanner.entities.request.ReportRequest;
import com.ddscanner.entities.request.SignInRequest;
import com.ddscanner.entities.request.SignUpRequest;
import com.ddscanner.entities.request.ValidationRequest;

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
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface DDScannerRestService {

    @GET("/diving/filters")
    Call<ResponseBody> getFilters();

    @GET("/diving/divecenters")
    Call<ResponseBody> getDiveCenters(@QueryMap Map<String, String> map);

    @GET("/diving/divespot/{id}")
    Call<ResponseBody> getDiveSpotById(@Path("id") String id, @QueryMap Map<String, String> map);

    @Deprecated
    @Headers("Content-type: application/json")
    @GET("/diving/divespots")
    Call<ResponseBody> getDivespots(@QueryMap Map<String, Object> map);

    @POST("/diving/login")
    Call<ResponseBody> login(@Body RegisterRequest registerRequest);

    @POST("/diving/divespot/{id}/validation")
    Call<ResponseBody> divespotValidation(
            @Path("id") String id,
            @Body ValidationRequest validationReguest
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
            @Part("social") RequestBody sn
    );

    @POST("/diving/identify")
    Call<ResponseBody> identify(@Body IdentifyRequest identifyRequest);

    @POST("/diving/sealife")
    @Multipart
    Call<ResponseBody> addSealife(
            @Part MultipartBody.Part image,
            @Part("name") RequestBody name,
            @Part("distribution") RequestBody distribution,
            @Part("habitat") RequestBody habitat,
            @Part("scName") RequestBody scName,
            @Part("length") RequestBody length,
            @Part("weight") RequestBody weight,
            @Part("depth") RequestBody depth,
            @Part("order") RequestBody order,
            @Part("scClass") RequestBody sealifeClass,
            @Part("token") RequestBody token,
            @Part("social") RequestBody sn);

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
    Call<ResponseBody> checkOut(@Path("id") String id,
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

    @Deprecated
    @GET("diving/user/{id}")
    Call<ResponseBody> getUserInfo(@Path("id") String id, @QueryMap Map<String, String> map);

    @POST("diving/user/{id}")
    @Multipart
    Call<ResponseBody> updateUserById(
            @Path("id") String id,
            @Part MultipartBody.Part image,
            @Part("_method") RequestBody _method,
            @Part("name") RequestBody name,
            @Part("username") RequestBody username,
            @Part("about") RequestBody about,
            @Part("token") RequestBody token,
            @Part("social") RequestBody sn
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
            @Part List<MultipartBody.Part> images,
            @Part("_method") RequestBody _method,
            @Part("token") RequestBody token,
            @Part("social") RequestBody sn
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

    @POST("diving/divespot/comment/{id}")
    @Multipart
    Call<ResponseBody> updateComment(
            @Path("id") String id,
            @Part("_method") RequestBody _method,
            @Part("comment") RequestBody comment,
            @Part("rating") RequestBody rating,
            @Part List<MultipartBody.Part> images_new,
            @Part List<MultipartBody.Part> images_del,
            @Part("token") RequestBody token,
            @Part("social") RequestBody sn
    );

    @POST("diving/divespot/comment/{id}/report")
    Call<ResponseBody> reportComment(@Path("id") String id, @Body ReportRequest reportRequest);

    @POST("diving/image/report")
    Call<ResponseBody> reportImage(@Body ReportRequest reportRequest);

    @DELETE("diving/image/{name}")
    Call<ResponseBody> deleteImage(@Path("name") String imageName, @QueryMap Map<String, String> map);

    @GET("diving/divespot/{id}/images")
    Call<ResponseBody> getDiveSpotImages(@Path("id") String id, @QueryMap Map<String, String> map);

    @GET("diving/user/{id}/comments")
    Call<ResponseBody> getUserComments(@Path("id") String id, @QueryMap Map<String, String> map);

    @Deprecated
    @GET("diving/user/{id}/achievements")
    Call<ResponseBody> getUserAchievementsOld(@Path("id") String id, @QueryMap Map<String, String> map);

    @POST("v2_0/user.login")
    Call<ResponseBody> loginUser(@Body SignInRequest signInRequest);

    @POST("v2_0/user.sign_up")
    Call<ResponseBody> signUpUser(@Body SignUpRequest signUpRequest);

    @GET("v2_0/user.profile.get")
    Call<ResponseBody> getSelfProfileInformation();

    @GET("v2_0/user.profile.get")
    Call<ResponseBody> getUserInformation(@Query("id") String id);

    @GET("v2_0/user.achievements.get")
    Call<ResponseBody> getUserAchievements();

    @GET("v2_0/divespots.filter")
    Call<ResponseBody> getDiveSpotsByFilter(@QueryMap Map<String, Object> map);

    @GET("v2_0/divespot.get")
    Call<ResponseBody> getDiveSpotDetails(@Query("id") String id);

    @Multipart
    @POST("v2_0/divespot.maps.add")
    Call<ResponseBody> addMapsToDiveSpot(@Part("id") RequestBody  id, @Part List<MultipartBody.Part> image);

    @Multipart
    @POST("v2_0/divespot.photos.add")
    Call<ResponseBody> addPhotosToDiveSpot(@Part("id") RequestBody  id, @Part List<MultipartBody.Part> image);

    @GET("v2_0/sealife.get")
    Call<ResponseBody> getSealifeDetails(@Query("id") String id);

    @GET("v2_0/divespots.search")
    Call<ResponseBody> getDivespotsByName(@Query("query") String query);

    @POST("v2_0/divespot.check_in")
    Call<ResponseBody> postCheckin(@Query("id") String diveSpotId);

    @GET("v2_0/divespot.photos.get")
    Call<ResponseBody> getDiveSpotPhotos(@Query("id") String diveSpotId);

    @POST("v2_0/photo.like")
    Call<ResponseBody> postLikePhoto(@Query("id") String id);

    @POST("v2_0/photo.dislike")
    Call<ResponseBody> postDislikePhoto(@Query("id") String id);

}
