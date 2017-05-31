package com.ddscanner.rest;


import com.ddscanner.entities.request.ChangePasswordRequest;
import com.ddscanner.entities.request.DeleteImageRequest;
import com.ddscanner.entities.request.InstructorsSeeRequests;
import com.ddscanner.entities.request.NotificationsReadRequest;
import com.ddscanner.entities.request.ReportImageRequest;
import com.ddscanner.entities.request.ReportRequest;
import com.ddscanner.entities.request.SignInRequest;
import com.ddscanner.entities.request.SignUpRequest;
import com.ddscanner.entities.request.UpdateLocationRequest;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface DDScannerRestService {

    @POST("user.login")
    Call<ResponseBody> loginUser(@Body SignInRequest signInRequest);

    @POST("user.sign_up")
    Call<ResponseBody> signUpUser(@Body SignUpRequest signUpRequest);

    @GET("user.profile.get")
    Call<ResponseBody> getSelfProfileInformation(@Query("include_photo_details") int value);

    @GET("user.divecenter.profile.get")
    Call<ResponseBody> getSelfDiveCenterInformation(@Query("include_photo_details") int value);

    @GET("user.profile.get")
    Call<ResponseBody> getUserInformation(@Query("id") String id, @Query("include_photo_details") int value);

    @GET("user.divecenter.profile.get")
    Call<ResponseBody> getDiveCenterInformation(@Query("id") String id, @Query("include_photo_details") int value);

    @GET("user.achievements.get")
    Call<ResponseBody> getUserAchievements();

    @GET("divespots.filter")
    Call<ResponseBody> getDiveSpotsByFilter(@QueryMap Map<String, Object> map, @Query(value = "sealifes[]", encoded = true) List<String> s);

    @GET("divespot.get")
    Call<ResponseBody> getDiveSpotDetails(@Query("id") String id);

    @Multipart
    @POST("divespot.maps.add")
    Call<ResponseBody> addMapsToDiveSpot(@Part("id") RequestBody  id, @Part List<MultipartBody.Part> image);

    @Multipart
    @POST("divespot.photos.add")
    Call<ResponseBody> addPhotosToDiveSpot(@Part("id") RequestBody  id, @Part List<MultipartBody.Part> image);

    @GET("sealife.get")
    Call<ResponseBody> getSealifeDetails(@Query("id") String id);

    @GET("divespots.search")
    Call<ResponseBody> getDivespotsByName(@Query("query") String query);

    @POST("divespot.check_in")
    Call<ResponseBody> postCheckin(@Query("id") String diveSpotId);

    @POST("divespot.check_out")
    Call<ResponseBody> postCheckout(@Query("id") String diveSpotId);

    @GET("divespot.checked_in.get")
    Call<ResponseBody> getDiveSpotsCheckedInUsers(@Query("id") String diveSpotId);

    @GET("divespot.editors.get")
    Call<ResponseBody> getDiveSpotEditorsList(@Query("id") String diveSpotId);

    @GET("divecenters.search")
    Call<ResponseBody> getDiveCentersList(@Query("query") String query, @Query("limit") String limit);

    @GET("divespot.photos.get")
    Call<ResponseBody> getDiveSpotPhotos(@Query("id") String diveSpotId);

    @POST("photo.like")
    Call<ResponseBody> postLikePhoto(@Query("id") String id);

    @POST("photo.unlike")
    Call<ResponseBody> postDislikePhoto(@Query("id") String id);

    @GET("divespot.maps.get")
    Call<ResponseBody> getDiveSpotMaps(@Query("id") String diveSpotId);

    @POST("images.remove")
    Call<ResponseBody> postDeleteImage(@Body DeleteImageRequest images);

    @POST("user.favorites.add")
    Call<ResponseBody> postAddToFavorites(@Query("id") String divespotId);

    @POST("user.favorites.remove")
    Call<ResponseBody> postRemoveFromFavorites(@Query("id") String divespotId);

    @GET("sealifes.get")
    Call<ResponseBody> getAllSealifes();

    @GET("sealifes.get")
    Call<ResponseBody> getAllSealifesByLocation(@Query("lat") double lat, @Query("lng") double lng);

    @Multipart
    @POST("divespot.add")
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
            @Part("type") RequestBody type,
            @Part("is_editable") RequestBody isEditable,
            @Part("is_working_here") RequestBody isWorkingHere,
            @Part List<MultipartBody.Part> photos,
            @Part List<MultipartBody.Part> maps,
            @Part List<MultipartBody.Part> sealife

    );

    @Multipart
    @POST("divespot.update")
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
            @Part("type") RequestBody type,
            @Part("is_editable") RequestBody isEditable,
            @Part("is_working_here") RequestBody isWorkingHere,
            @Part("cover_id") RequestBody cover_id,
            @Part List<MultipartBody.Part> new_photos,
            @Part List<MultipartBody.Part> deleted_photos,
            @Part List<MultipartBody.Part> new_maps,
            @Part List<MultipartBody.Part> deleted_maps,
            @Part List<MultipartBody.Part> sealife
    );

    @GET("languages.get")
    Call<ResponseBody> getDivespotLanguages();

    @POST("divecenter.divespot.add")
    Call<ResponseBody> postAddDiveSpotToDiveCenter(@Query("id") String divespotId);

    @POST("divecenter.divespot.remove")
    Call<ResponseBody> postRemoveDiveSpotToDiveCenter(@Query("id") String divespotId);

    @POST("divespot.approve")
    Call<ResponseBody> postApproveDiveSpot(@Query("id") String id, @Query("value") String value);

    @GET("divespot.translations.get")
    Call<ResponseBody> getDiveSpotsTranslations(@Query("id") String id);

    @POST("user.password.forgot")
    Call<ResponseBody> postForgotPassword(@Query("email") String email);

    @Multipart
    @POST("user.profile.update")
    Call<ResponseBody> postUpdateUserProfile(
            @Part MultipartBody.Part image,
            @Part("name") RequestBody name,
            @Part("about") RequestBody about,
            @Part("dive_center_id") RequestBody diveCenterId,
            @Part("diving_skill") RequestBody skill
    );

    @Multipart
    @POST("user.divecenter.profile.update")
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

    @GET("countries.get")
    Call<ResponseBody> getListCountries();

    @Multipart
    @POST("divespot.review.add")
    Call<ResponseBody> postLeaveComment(
            @Part List<MultipartBody.Part> photos,
            @Part("id") RequestBody id,
            @Part("rating") RequestBody rating,
            @Part("review") RequestBody review,
            @Part List<MultipartBody.Part> sealife
    );

    @Multipart
    @POST("divespot.review.update")
    Call<ResponseBody> postUpdateReview(
            @Part List<MultipartBody.Part> newPhotos,
            @Part List<MultipartBody.Part> deletedPhotos,
            @Part("id") RequestBody id,
            @Part("rating") RequestBody rating,
            @Part("review") RequestBody review,
            @Part List<MultipartBody.Part> sealife
    );

    @POST("divespot.review.delete")
    Call<ResponseBody> postDeleteReview(@Query("id") String commentId);

    @POST("divespot.review.report")
    Call<ResponseBody> postReportReview(@Body ReportRequest reportRequest);

    @POST("divespot.review.dislike")
    Call<ResponseBody> postDislikeReview(@Query("id") String commentId);

    @POST("divespot.review.like")
    Call<ResponseBody> postLikeReview(@Query("id") String commentId);

    @GET("user.dislikes.get")
    Call<ResponseBody> getUserDislikes(@Query("id") String userId);

    @GET("user.likes.get")
    Call<ResponseBody> getUserLikes(@Query("id") String userId);

    @GET("user.photos_added.get")
    Call<ResponseBody> getUserPhotos(@Query("id") String userId);

    @GET("user.divespots.added.get")
    Call<ResponseBody> getUserAddedDiveSpots(@Query("id") String userId);

    @GET("user.divespots.edited.get")
    Call<ResponseBody> getUserEditedDiveSpots(@Query("id") String userId);

    @GET("user.divespots.checked_in.get")
    Call<ResponseBody> getUserCheckedInSpots(@Query("id") String userId);

    @GET("user.divespots.favorites.get")
    Call<ResponseBody> getUserFavoritesSpots(@Query("id") String userId);

    @POST("user.location.update")
    Call<ResponseBody> postUpdateUserLocation(@Body UpdateLocationRequest updateLocationRequest);

    @POST("image.report")
    Call<ResponseBody> postReportImage(@Body ReportImageRequest reportImageRequest);

    @GET("divespot.reviews.get")
    Call<ResponseBody> getCommentsForDiveSpot(@Query("id") String diveSpotId, @Query("include_photo_details") int value);

    @GET("user.reviews.get")
    Call<ResponseBody> getUserComments(@Query("id") String diveCenterId, @Query("include_photo_details") int value);

    @POST("instructor.divecenter.add")
    Call<ResponseBody> postAddIstructorToDiveCenter(@Query("id") String diveCenterId);

    @GET("divecenter.instructors.get")
    Call<ResponseBody> getInstructorsList(@Query("id") String diveCenterId);

    @POST("divecenter.instructors.see")
    Call<ResponseBody> postInstructorsSees(@Body InstructorsSeeRequests instructorsSeeRequests);

    @GET("divecenter.divespots.get")
    Call<ResponseBody> getDiveSpotsForDiveCenter(@Query("id") String id);

    @POST("divecenter.instructor.remove")
    Call<ResponseBody> postRemoveInstructorFromDIveCenter(@Query("id") String instructorId);

    @GET("divecenter.languages.get")
    Call<ResponseBody> getDiveCenterLanguages(@Query("id") String diveCenterId);

    @GET("divecenter.status_in.divespot.get")
    Call<ResponseBody> getDiveCenterStatusInSpot(@Query("id") String id);

    @GET("user.status_in.divespot.get")
    Call<ResponseBody> getUserStatusInSpot(@Query("id") String id);

    @GET("divespot.review.photos.get")
    Call<ResponseBody> getReviewPhotos(@Query("id") String id, @Query("include_photo_details") int value);

    @GET("divespot.review.sealifes.get")
    Call<ResponseBody> getReviewSealifes(@Query("id") String reviewId);

    @Multipart
    @POST("sealife.add")
    Call<ResponseBody> postAddSealife(
            @Part MultipartBody.Part image,
            @Part("translations") RequestBody translations
    );

    @Multipart
    @POST("sealife.update")
    Call<ResponseBody> postUpdateSealife(
            @Part MultipartBody.Part image,
            @Part("translations") RequestBody translations,
            @Part("id") RequestBody id
    );

    @GET("divespot.review.get")
    Call<ResponseBody> getSingleReview(@Query("id") String id, @Query("include_photo_details") int value);

    @GET("divecenters.filter")
    Call<ResponseBody> getDiveCentersForDiveSpot(@Query("dive_spot_id") String id);

    @GET("user.notifications.get")
    Call<ResponseBody> getNotifications();

    @GET("user.notifications.activity.get")
    Call<ResponseBody> getActivityNotifications(@Query("start_from") String date, @Query("limit") int limit, @Query("include_photo_details") int value);

    @GET("user.notifications.personal.get")
    Call<ResponseBody> getPersonalNotifications(@Query("start_from") String date, @Query("limit") int limit, @Query("include_photo_details") int value);

    @GET("user.notification.photos.get")
    Call<ResponseBody> getNotificationPhotos(@Query("id") String id, @Query("include_photo_details") int value);

    @GET("divecenter.divespots.to_approve.count.get")
    Call<ResponseBody> getApproveCount();

    @GET("divecenter.divespots.to_approve.get")
    Call<ResponseBody> getDiveSpotsToApprove();

    //TODO change after server side will be ready
    @GET("user.notificayytions.new.count.get")
    Call<ResponseBody> getNewNotificationsCount();

    @POST("user.notifications.read")
    Call<ResponseBody> postNotificationsRead(@Body NotificationsReadRequest notifictionsReadedRequest);

    @POST("user.password.change")
    Call<ResponseBody> postChangeUserPassword(@Body ChangePasswordRequest changePasswordRequest);

}
