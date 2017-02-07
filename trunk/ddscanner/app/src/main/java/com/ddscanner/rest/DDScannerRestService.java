package com.ddscanner.rest;


import com.ddscanner.entities.Translation;
import com.ddscanner.entities.request.DeleteImageRequest;
import com.ddscanner.entities.request.IdentifyRequest;
import com.ddscanner.entities.request.InstructorsSeeRequests;
import com.ddscanner.entities.request.RegisterRequest;
import com.ddscanner.entities.request.ReportImageRequest;
import com.ddscanner.entities.request.ReportRequest;
import com.ddscanner.entities.request.SignInRequest;
import com.ddscanner.entities.request.SignUpRequest;
import com.ddscanner.entities.request.UpdateLocationRequest;
import com.ddscanner.entities.request.ValidationRequest;

import java.util.ArrayList;
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

    @POST("/diving/login")
    Call<ResponseBody> login(@Body RegisterRequest registerRequest);

    @POST("/diving/divespot/{id}/validation")
    Call<ResponseBody> divespotValidation(
            @Path("id") String id,
            @Body ValidationRequest validationReguest
            );

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

    @GET("diving/user/{id}/comments")
    Call<ResponseBody> getUserComments(@Path("id") String id, @QueryMap Map<String, String> map);

    @POST("v2_0/user.login")
    Call<ResponseBody> loginUser(@Body SignInRequest signInRequest);

    @POST("v2_0/user.sign_up")
    Call<ResponseBody> signUpUser(@Body SignUpRequest signUpRequest);

    @GET("v2_0/user.profile.get")
    Call<ResponseBody> getSelfProfileInformation(@Query("include_photo_details") int value);

    @GET("v2_0/user.divecenter.profile.get")
    Call<ResponseBody> getSelfDiveCenterInformation(@Query("include_photo_details") int value);

    @GET("v2_0/user.profile.get")
    Call<ResponseBody> getUserInformation(@Query("id") String id, @Query("include_photo_details") int value);

    @GET("v2_0/divecenter.profile.get")
    Call<ResponseBody> getDiveCenterInformation(@Query("id") String id, @Query("include_photo_details") int value);

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

    @POST("v2_0/divespot.check_out")
    Call<ResponseBody> postCheckout(@Query("id") String diveSpotId);

    @GET("v2_0/divespot.checked_in.get")
    Call<ResponseBody> getDiveSpotsCheckedInUsers(@Query("id") String diveSpotId);

    @GET("v2_0/divespot.editors.get")
    Call<ResponseBody> getDiveSpotEditorsList(@Query("id") String diveSpotId);

    @GET("v2_0/divecenters.search")
    Call<ResponseBody> getDiveCentersList(@Query("query") String query, @Query("limit") String limit);

    @GET("v2_0/divespot.photos.get")
    Call<ResponseBody> getDiveSpotPhotos(@Query("id") String diveSpotId);

    @POST("v2_0/photo.like")
    Call<ResponseBody> postLikePhoto(@Query("id") String id);

    @POST("v2_0/photo.unlike")
    Call<ResponseBody> postDislikePhoto(@Query("id") String id);

    @GET("v2_0/divespot.maps.get")
    Call<ResponseBody> getDiveSpotMaps(@Query("id") String diveSpotId);

    @POST("v2_0/images.remove")
    Call<ResponseBody> postDeleteImage(@Body DeleteImageRequest images);

    @POST("v2_0/user.favorites.add")
    Call<ResponseBody> postAddToFavorites(@Query("id") String divespotId);

    @POST("v2_0/user.favorites.remove")
    Call<ResponseBody> postRemoveFromFavorites(@Query("id") String divespotId);

    @GET("v2_0/sealifes.get")
    Call<ResponseBody> getSealifesByLimit(@Query("limit") int limit);

    @Multipart
    @POST("v2_0/divespot.add")
    Call<ResponseBody> postAddDiveSpot(
            @Part("lat") RequestBody lat,
            @Part("lng") RequestBody lng,
            @Part("country_code") RequestBody countryCode,
            @Part("depth") RequestBody depth,
            @Part("diving_skill") RequestBody skill,
            @Part("currents") RequestBody currents,
            @Part("visibility_min") RequestBody visibility_min,
            @Part("visibility_max") RequestBody visibility_max,
            @Part("cover_number") RequestBody cover_number,
            @Part("translations") RequestBody translations,
            @Part("dive_spot_type") RequestBody type,
            @Part("is_editable") RequestBody isEditable,
            @Part("is_working_here") RequestBody isWorkingHere,
            @Part List<MultipartBody.Part> photos,
            @Part List<MultipartBody.Part> maps,
            @Part List<MultipartBody.Part> sealife

    );

    @Multipart
    @POST("v2_0/divespot.update")
    Call<ResponseBody> postUpdateDiveSpot(
            @Part("id") RequestBody id,
            @Part("lat") RequestBody lat,
            @Part("lng") RequestBody lng,
            @Part("country_code") RequestBody countryCode,
            @Part("depth") RequestBody depth,
            @Part("diving_skill") RequestBody skill,
            @Part("currents") RequestBody currents,
            @Part("visibility_min") RequestBody visibility_min,
            @Part("visibility_max") RequestBody visibility_max,
            @Part("cover_number") RequestBody cover_number,
            @Part("translations") RequestBody translations,
            @Part("dive_spot_type") RequestBody type,
            @Part("is_editable") RequestBody isEditable,
            @Part("is_working_here") RequestBody isWorkingHere,
            @Part List<MultipartBody.Part> new_photos,
            @Part List<MultipartBody.Part> deleted_photos,
            @Part List<MultipartBody.Part> new_maps,
            @Part List<MultipartBody.Part> deleted_maps,
            @Part List<MultipartBody.Part> sealife
    );

    @GET("v2_0/languages.get")
    Call<ResponseBody> getDivespotLanguages();

    @POST("v2_0/divecenter.divespot.add")
    Call<ResponseBody> postAddDiveSpotToDiveCenter(@Query("id") String divespotId);

    @POST("v2_0/divecenter.divespot.remove")
    Call<ResponseBody> postRemoveDiveSpotToDiveCenter(@Query("id") String divespotId);

    @POST("v2_0/divespot.approve")
    Call<ResponseBody> postApproveDiveSpot(@Query("id") String id, @Query("value") String value);

    @GET("v2_0/divespot.translations.get")
    Call<ResponseBody> getDiveSpotsTranslations(@Query("id") String id);

    @POST("v2_0/user.password.forgot")
    Call<ResponseBody> postForgotPassword(@Query("email") String email);

    @Multipart
    @POST("v2_0/user.profile.update")
    Call<ResponseBody> postUpdateUserProfile(
            @Part MultipartBody.Part image,
            @Part("name") RequestBody name,
            @Part("about") RequestBody about,
            @Part("dive_center_id") RequestBody diveCenterId,
            @Part("diving_skill") RequestBody skill
    );

    @Multipart
    @POST("v2_0/user.divecenter.profile.update")
    Call<ResponseBody> postUpdateDiveCenterProfile(
            @Part MultipartBody.Part image,
            @Part("name") RequestBody name,
            @Part("country") RequestBody country,
            @Part("addresses") RequestBody adresses,
            @Part("service") RequestBody service,
            @Part List<MultipartBody.Part> languages,
            @Part List<MultipartBody.Part> emails,
            @Part List<MultipartBody.Part> phones,
            @Part List<MultipartBody.Part> divespots
    );

    @GET("v2_0/countries.get")
    Call<ResponseBody> getListCountries();

    @Multipart
    @POST("v2_0/divespot.review.add")
    Call<ResponseBody> postLeaveComment(
            @Part List<MultipartBody.Part> photos,
            @Part("id") RequestBody id,
            @Part("rating") RequestBody rating,
            @Part("review") RequestBody review,
            @Part List<MultipartBody.Part> sealife
    );

    @Multipart
    @POST("v2_0/divespot.review.update")
    Call<ResponseBody> postUpdateReview(
            @Part List<MultipartBody.Part> newPhotos,
            @Part List<MultipartBody.Part> deletedPhotos,
            @Part("id") RequestBody id,
            @Part("rating") RequestBody rating,
            @Part("review") RequestBody review,
            @Part List<MultipartBody.Part> sealife
    );

    @POST("v2_0/divespot.review.delete")
    Call<ResponseBody> postDeleteReview(@Query("id") String commentId);

    @POST("v2_0/divespot.review.report")
    Call<ResponseBody> postReportReview(@Body ReportRequest reportRequest);

    @POST("v2_0/divespot.review.dislike")
    Call<ResponseBody> postDislikeReview(@Query("id") String commentId);

    @POST("v2_0/divespot.review.like")
    Call<ResponseBody> postLikeReview(@Query("id") String commentId);

    @GET("v2_0/user.dislikes.get")
    Call<ResponseBody> getUserDislikes(@Query("id") String userId);

    @GET("v2_0/user.likes.get")
    Call<ResponseBody> getUserLikes(@Query("id") String userId);

    @GET("v2_0/user.photos_added.get")
    Call<ResponseBody> getUserPhotos(@Query("id") String userId);

    @GET("v2_0/user.divespots.added.get")
    Call<ResponseBody> getUserAddedDiveSpots(@Query("id") String userId);

    @GET("v2_0/user.divespots.edited.get")
    Call<ResponseBody> getUserEditedDiveSpots(@Query("id") String userId);

    @GET("v2_0/user.divespots.checked_in.get")
    Call<ResponseBody> getUserCheckedInSpots(@Query("id") String userId);

    @GET("v2_0/user.divespots.favorites.get")
    Call<ResponseBody> getUserFavoritesSpots(@Query("id") String userId);

    @POST("v2_0/user.location.update")
    Call<ResponseBody> postUpdateUserLocation(@Body UpdateLocationRequest updateLocationRequest);

    @POST("v2_0/image.report")
    Call<ResponseBody> postReportImage(@Body ReportImageRequest reportImageRequest);

    @GET("v2_0/divespot.reviews.get")
    Call<ResponseBody> getCommentsForDiveSpot(@Query("id") String diveSpotId, @Query("include_photo_details") int value);

    @GET("v2_0/user.reviews.get")
    Call<ResponseBody> getSelfCommentsList(@Query("id") String diveCenterId, @Query("include_photo_details") int value);

    @POST("v2_0/instructor.divecenter.add")
    Call<ResponseBody> postAddIstructorToDiveCenter(@Query("id") String diveCenterId);

    @GET("v2_0/divecenter.instructors.get")
    Call<ResponseBody> getInstructorsList(@Query("id") String diveCenterId);

    @POST("v2_0/divecenter.instructors.see")
    Call<ResponseBody> postInstructorsSees(@Body InstructorsSeeRequests instructorsSeeRequests);

    @GET("v2_0/divecenter.divespots.get")
    Call<ResponseBody> getSelfDiveSpotsForDiveCenter();

    @POST("v2_0/divecenter.instructor.remove")
    Call<ResponseBody> postRemoveInstructorFromDIveCenter(@Query("id") String instructorId);

    @GET("v2_0/divecenter.languages.get")
    Call<ResponseBody> getDiveCenterLanguages();

    @GET("v2_0/divecenter.status_in.divespot.get")
    Call<ResponseBody> getDiveCenterStatusInSpot(@Query("id") String id);

    @GET("v2_0/user.status_in.divespot.get")
    Call<ResponseBody> getUserStatusInSpot(@Query("id") String id);

    @GET("v2_0/divespot.review.photos.get")
    Call<ResponseBody> getReviewPhotos(@Query("id") String id, @Query("include_photo_details") int value);

    @GET("v2_0/divespot.review.sealifes.get")
    Call<ResponseBody> getReviewSealifes(@Query("id") String reviewId);

}
