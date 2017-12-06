package com.ddscanner.rest;


import com.ddscanner.BuildConfig;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.request.ChangePasswordRequest;
import com.ddscanner.entities.request.DeleteImageRequest;
import com.ddscanner.entities.request.DiveCenterRequestBookingRequest;
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

    @POST("v2_1/user.login")
    Call<ResponseBody> loginUser(@Body SignInRequest signInRequest);

    @POST("v2_1/user.sign_up")
    Call<ResponseBody> signUpUser(@Body SignUpRequest signUpRequest);

    @GET("v2_1/user.profile.get")
    Call<ResponseBody> getSelfProfileInformation(@Query("include_photo_details") int value);

    @GET("v2_3/user.divecenter.profile.get")
    Call<ResponseBody> getSelfDiveCenterInformation(@Query("include_photo_details") int value);

    @GET("v2_1/user.profile.get")
    Call<ResponseBody> getUserInformation(@Query("id") String id, @Query("include_photo_details") int value);

    @GET("v2_3/user.divecenter.profile.get")
    Call<ResponseBody> getDiveCenterInformation(@Query("id") String id, @Query("include_photo_details") int value);

    @GET("v2_1/user.achievements.get")
    Call<ResponseBody> getUserAchievements();

    @GET("v2_2/map.filter")
    Call<ResponseBody> getDiveSpotsByFilter(@QueryMap Map<String, Object> map, @Query(value = "ds_sealifes[]", encoded = true) List<String> s);

    @GET("v2_1/divespot.get")
    Call<ResponseBody> getDiveSpotDetails(@Query("id") String id, @Query("include_photo_details") int value);

    @Multipart
    @POST("v2_1/divespot.maps.add")
    Call<ResponseBody> addMapsToDiveSpot(@Part("id") RequestBody  id, @Part List<MultipartBody.Part> image);

    @Multipart
    @POST("v2_1/divespot.photos.add")
    Call<ResponseBody> addPhotosToDiveSpot(@Part("id") RequestBody  id, @Part List<MultipartBody.Part> image);

    @GET("v2_1/sealife.get")
    Call<ResponseBody> getSealifeDetails(@Query("id") String id);

    @GET("v2_1/divespots.search")
    Call<ResponseBody> getDivespotsByName(@Query("query") String query);

    @POST("v2_1/divespot.check_in")
    Call<ResponseBody> postCheckin(@Query("id") String diveSpotId);

    @POST("v2_1/divespot.check_out")
    Call<ResponseBody> postCheckout(@Query("id") String diveSpotId);

    @GET("v2_1/divespot.checked_in.get")
    Call<ResponseBody> getDiveSpotsCheckedInUsers(@Query("id") String diveSpotId);

    @GET("v2_1/divespot.editors.get")
    Call<ResponseBody> getDiveSpotEditorsList(@Query("id") String diveSpotId);

    @GET("v2_1/divecenters.search")
    Call<ResponseBody> getDiveCentersList(@Query("query") String query, @Query("limit") String limit);

    @GET("v2_1/divespot.photos.get")
    Call<ResponseBody> getDiveSpotPhotos(@Query("id") String diveSpotId);

    @POST("v2_1/photo.like")
    Call<ResponseBody> postLikePhoto(@Query("id") String id);

    @POST("v2_1/photo.unlike")
    Call<ResponseBody> postDislikePhoto(@Query("id") String id);

    @GET("v2_1/divespot.maps.get")
    Call<ResponseBody> getDiveSpotMaps(@Query("id") String diveSpotId);

    @POST("v2_1/images.remove")
    Call<ResponseBody> postDeleteImage(@Body DeleteImageRequest images);

    @POST("v2_1/user.favorites.add")
    Call<ResponseBody> postAddToFavorites(@Query("id") String divespotId);

    @POST("v2_1/user.favorites.remove")
    Call<ResponseBody> postRemoveFromFavorites(@Query("id") String divespotId);

    @GET("v2_1/sealifes.get")
    Call<ResponseBody> getAllSealifes();

    @GET("v2_1/sealifes.get")
    Call<ResponseBody> getAllSealifesByLocation(@Query("lat") double lat, @Query("lng") double lng);

    @Multipart
    @POST("v2_1/divespot.add")
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
    @POST("v2_1/divespot.update")
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

    @GET("v2_1/languages.get")
    Call<ResponseBody> getDivespotLanguages();

    @POST("v2_1/divecenter.divespot.add")
    Call<ResponseBody> postAddDiveSpotToDiveCenter(@Query("id") String divespotId);

    @POST("v2_1/divecenter.divespot.remove")
    Call<ResponseBody> postRemoveDiveSpotToDiveCenter(@Query("id") String divespotId);

    @POST("v2_1/divespot.approve")
    Call<ResponseBody> postApproveDiveSpot(@Query("id") String id, @Query("value") String value);

    @GET("v2_1/divespot.translations.get")
    Call<ResponseBody> getDiveSpotsTranslations(@Query("id") String id);

    @POST("v2_1/user.password.forgot")
    Call<ResponseBody> postForgotPassword(@Query("email") String email);

    @Multipart
    @POST("v2_2/user.divecenter.profile.update")
    Call<ResponseBody> postUpdateDiveCenterProfile(
            @Part MultipartBody.Part image,
            @Part("name") RequestBody name,
            @Part("country") RequestBody country,
            @Part("addresses") RequestBody adresses,
            @Part("is_dive_shop") RequestBody service,
            @Part("bio") RequestBody about,
            @Part List<MultipartBody.Part> languages,
            @Part List<MultipartBody.Part> emails,
            @Part List<MultipartBody.Part> phones,
            @Part List<MultipartBody.Part> divespots,
            @Part List<MultipartBody.Part> associations,
            @Part List<MultipartBody.Part> brands
    );

    @GET("v2_1/countries.get")
    Call<ResponseBody> getListCountries();

    @Multipart
    @POST("v2_1/divespot.review.add")
    Call<ResponseBody> postLeaveComment(
            @Part List<MultipartBody.Part> photos,
            @Part("id") RequestBody id,
            @Part("rating") RequestBody rating,
            @Part("review") RequestBody review,
            @Part List<MultipartBody.Part> sealife
    );

    @Multipart
    @POST("v2_1/divespot.review.update")
    Call<ResponseBody> postUpdateReview(
            @Part List<MultipartBody.Part> newPhotos,
            @Part List<MultipartBody.Part> deletedPhotos,
            @Part("id") RequestBody id,
            @Part("rating") RequestBody rating,
            @Part("review") RequestBody review,
            @Part List<MultipartBody.Part> sealife
    );

    @POST("v2_1/divespot.review.delete")
    Call<ResponseBody> postDeleteReview(@Query("id") String commentId);

    @POST("v2_1/divespot.review.report")
    Call<ResponseBody> postReportReview(@Body ReportRequest reportRequest);

    @POST("v2_1/divespot.review.dislike")
    Call<ResponseBody> postDislikeReview(@Query("id") String commentId);

    @POST("v2_1/divespot.review.like")
    Call<ResponseBody> postLikeReview(@Query("id") String commentId);

    @GET("v2_1/user.dislikes.get")
    Call<ResponseBody> getUserDislikes(@Query("id") String userId);

    @GET("v2_1/user.likes.get")
    Call<ResponseBody> getUserLikes(@Query("id") String userId);

    @GET("v2_1/user.photos_added.get")
    Call<ResponseBody> getUserPhotos(@Query("id") String userId);

    @GET("v2_1/user.divespots.added.get")
    Call<ResponseBody> getUserAddedDiveSpots(@Query("id") String userId);

    @GET("v2_1/user.divespots.edited.get")
    Call<ResponseBody> getUserEditedDiveSpots(@Query("id") String userId);

    @GET("v2_1/user.divespots.checked_in.get")
    Call<ResponseBody> getUserCheckedInSpots(@Query("id") String userId);

    @GET("v2_1/user.divespots.favorites.get")
    Call<ResponseBody> getUserFavoritesSpots(@Query("id") String userId);

    @POST("v2_1/user.location.update")
    Call<ResponseBody> postUpdateUserLocation(@Body UpdateLocationRequest updateLocationRequest);

    @POST("v2_1/image.report")
    Call<ResponseBody> postReportImage(@Body ReportImageRequest reportImageRequest);

    @GET("v2_1/divespot.reviews.get")
    Call<ResponseBody> getCommentsForDiveSpot(@Query("id") String diveSpotId, @Query("include_photo_details") int value);

    @GET("v2_1/user.reviews.get")
    Call<ResponseBody> getUserComments(@Query("id") String diveCenterId, @Query("include_photo_details") int value);

    @POST("v2_1/instructor.divecenter.add")
    Call<ResponseBody> postAddIstructorToDiveCenter(@Query("id") String diveCenterId);

    @GET("v2_1/divecenter.instructors.get")
    Call<ResponseBody> getInstructorsList(@Query("id") String diveCenterId);

    @POST("v2_1/divecenter.instructors.see")
    Call<ResponseBody> postInstructorsSees(@Body InstructorsSeeRequests instructorsSeeRequests);

    @GET("v2_1/divecenter.divespots.get")
    Call<ResponseBody> getDiveSpotsForDiveCenter(@Query("id") String id);

    @POST("v2_1/divecenter.instructor.remove")
    Call<ResponseBody> postRemoveInstructorFromDIveCenter(@Query("id") String instructorId);

    @GET("v2_1/divecenter.languages.get")
    Call<ResponseBody> getDiveCenterLanguages(@Query("id") String diveCenterId);

    @GET("v2_1/divecenter.status_in.divespot.get")
    Call<ResponseBody> getDiveCenterStatusInSpot(@Query("id") String id);

    @GET("v2_1/user.status_in.divespot.get")
    Call<ResponseBody> getUserStatusInSpot(@Query("id") String id);

    @GET("v2_1/divespot.review.photos.get")
    Call<ResponseBody> getReviewPhotos(@Query("id") String id, @Query("include_photo_details") int value);

    @GET("v2_1/divespot.review.sealifes.get")
    Call<ResponseBody> getReviewSealifes(@Query("id") String reviewId);

    @Multipart
    @POST("v2_1/sealife.add")
    Call<ResponseBody> postAddSealife(
            @Part MultipartBody.Part image,
            @Part("translations") RequestBody translations
    );

    @Multipart
    @POST("v2_1/sealife.update")
    Call<ResponseBody> postUpdateSealife(
            @Part MultipartBody.Part image,
            @Part("translations") RequestBody translations,
            @Part("id") RequestBody id
    );

    @GET("v2_1/divespot.review.get")
    Call<ResponseBody> getSingleReview(@Query("id") String id, @Query("include_photo_details") int value);

    @GET("v2_1/divecenters.filter")
    Call<ResponseBody> getDiveCentersForDiveSpot(@Query("dive_spot_id") String id);

    @GET("v2_1/user.notifications.get")
    Call<ResponseBody> getNotifications();

    @GET("v2_1/user.notifications.activity.get")
    Call<ResponseBody> getActivityNotifications(@Query("start_from") String date, @Query("limit") int limit, @Query("include_photo_details") int value);

    @GET("v2_1/user.notifications.personal.get")
    Call<ResponseBody> getPersonalNotifications(@Query("start_from") String date, @Query("limit") int limit, @Query("include_photo_details") int value);

    @GET("v2_1/user.notification.photos.get")
    Call<ResponseBody> getNotificationPhotos(@Query("id") String id, @Query("include_photo_details") int value);

    @GET("v2_1/divecenter.divespots.to_approve.count.get")
    Call<ResponseBody> getApproveCount();

    @GET("v2_1/divecenter.divespots.to_approve.get")
    Call<ResponseBody> getDiveSpotsToApprove();
    
    @GET("v2_1/user.new_notifications.count.get")
    Call<ResponseBody> getNewNotificationsCount();

    @POST("v2_1/user.notifications.read")
    Call<ResponseBody> postNotificationsRead(@Body NotificationsReadRequest notifictionsReadedRequest);

    @POST("v2_1/user.password.change")
    Call<ResponseBody> postChangeUserPassword(@Body ChangePasswordRequest changePasswordRequest);

    @POST("v2_1/instructor.divecenter.add")
    Call<ResponseBody> postAddInstructorToDiveCenter(@Query("id") int id, @Query("dive_center_type") int type);

    @POST("v2_1/user.legacy_divecenter.invite")
    Call<ResponseBody> postLegacyDiveCenterInvite(@Query("id") int id, @Query("email") String email, @Query("name") String name, @Query("address") String address, @Query("country_code") String country_code, @Query("bind_to_dive_center") int value);

    @POST("v2_1/user.divecenter.invite")
    Call<ResponseBody> postNewDiveCenterInvite(@Query("email") String email, @Query("name") String name, @Query("address") String address, @Query("country_code") String country_code, @Query("bind_to_dive_center") int value);

    @GET("v2_1/user.legacy_divecenter.profile.get")
    Call<ResponseBody> postLegacyDiveCenterInfoGet(@Query("id") int id);

    @GET("v2_1/divecenters.search")
    Call<ResponseBody> searchDivecenters(@Query("query") String query, @Query("limit") int limit, @Query("page") int page);

    @Multipart
    @POST("v2_1/user.profile.update")
    Call<ResponseBody> postUpdateUserProfile(
            @Part MultipartBody.Part image,
            @Part("name") RequestBody name,
            @Part("about") RequestBody about,
            @Part("dive_center_id") RequestBody diveCenterId,
            @Part("dive_center_type") RequestBody diveCenterType,
            @Part("diving_skill") RequestBody skill
    );

    @POST("v2_3/divecenter.request_booking")
    Call<ResponseBody> postRequestBooking(@Body DiveCenterRequestBookingRequest request);

    @GET("v2_2/brands.get")
    Call<ResponseBody> getBrands();

    @GET("v2_2/divecenter.brands.get")
    Call<ResponseBody> getDiveCenterBrands(@Query("id") String id);

    @GET("v2_2/divespot.all.photos.get")
    Call<ResponseBody> getAllDiveSpotPhotos(@Query("id") String id);

    @GET("v2_2/divespot.reviews.photos.get")
    Call<ResponseBody> getDiveSpotReviewsPhoto(@Query("id") String id);

    @GET("v2_2/divespot.photos.get")
    Call<ResponseBody> getOnlyDiveSpotPhotos(@Query("id") String id);

    @GET("v2_3/user.divecenter.products.get")
    Call<ResponseBody> getDiveCenterProducts(@Query("id") String id);

    @GET("v2_3/product.get")
    Call<ResponseBody> getProductDetails(@Query("id") long id);

}
