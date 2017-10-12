package com.ddscanner.rest;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.entities.AchievementTitle;
import com.ddscanner.entities.BaseIdNamePhotoEntity;
import com.ddscanner.entities.CommentEntity;
import com.ddscanner.entities.DiveCenter;
import com.ddscanner.entities.DiveCenterProfile;
import com.ddscanner.entities.DiveCenterSearchItem;
import com.ddscanner.entities.DiveSpotDetailsEntity;
import com.ddscanner.entities.DiveSpotPhoto;
import com.ddscanner.entities.DiveSpotPhotosResponseEntity;
import com.ddscanner.entities.DiveSpotShort;
import com.ddscanner.entities.FlagsEntity;
import com.ddscanner.entities.Instructor;
import com.ddscanner.entities.Language;
import com.ddscanner.entities.LikeEntity;
import com.ddscanner.entities.NotificationEntity;
import com.ddscanner.entities.NotificationsCountEntity;
import com.ddscanner.entities.NotificationsResonseEntity;
import com.ddscanner.entities.Sealife;
import com.ddscanner.entities.SealifeListResponseEntity;
import com.ddscanner.entities.SealifeShort;
import com.ddscanner.entities.SelfCommentEntity;
import com.ddscanner.entities.SignInType;
import com.ddscanner.entities.SignUpResponseEntity;
import com.ddscanner.entities.Translation;
import com.ddscanner.entities.User;
import com.ddscanner.entities.request.ChangePasswordRequest;
import com.ddscanner.entities.request.DeleteImageRequest;
import com.ddscanner.entities.request.DiveCenterRequestBookingRequest;
import com.ddscanner.entities.request.DiveSpotsRequestMap;
import com.ddscanner.entities.request.InstructorsSeeRequests;
import com.ddscanner.entities.request.NotificationsReadRequest;
import com.ddscanner.entities.request.RegisterRequest;
import com.ddscanner.entities.request.ReportImageRequest;
import com.ddscanner.entities.request.ReportRequest;
import com.ddscanner.entities.request.SignInRequest;
import com.ddscanner.entities.request.SignUpRequest;
import com.ddscanner.entities.request.UpdateLocationRequest;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class DDScannerRestClient {

    protected Gson gson = new Gson();
    protected Activity context;

    public void with(Activity context) {
        this.context = context;
    }

    public void getDiveSpotDetails(String diveSpotId, @NonNull final ResultListener<DiveSpotDetailsEntity> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveSpotDetails(diveSpotId, 1);
        call.enqueue(new ResponseEntityCallback<DiveSpotDetailsEntity>(gson, resultListener, context) {
            @Override
            void handleResponseString(DDScannerRestClient.ResultListener<DiveSpotDetailsEntity> resultListener, String responseString) {
                DiveSpotDetailsEntity diveSpotResponseEntity = gson.fromJson(responseString, DiveSpotDetailsEntity.class);
                resultListener.onSuccess(diveSpotResponseEntity);
            }
        });
    }

    public void inviteLegacyDiveCenter(ResultListener<Void> resultListener, String name, String email, int id, String address, String countryCode, boolean isBind) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        int bindToValue;
        bindToValue = !isBind ? 0 : 1;
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postLegacyDiveCenterInvite(id, email, name, address, countryCode, bindToValue);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener, context));
    }

    public void inviteNewDiveCenter(ResultListener<Integer> resultListener, String name, String email, String address, String countryCode, boolean isBind) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        int bindToValue;
        bindToValue = !isBind ? 0 : 1;
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postNewDiveCenterInvite(email, name, address, countryCode, bindToValue);
        call.enqueue(new ResponseEntityCallback<Integer>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<Integer> resultListener, String responseString) throws JSONException {
                int result = gson.fromJson(responseString, Integer.class);
                resultListener.onSuccess(result);
            }
        });
    }

    public void getDiveCentersByQuery(String query, int page, ResultListener<ArrayList<DiveCenterSearchItem>> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().searchDivecenters(query, 15, page);
        call.enqueue(new ResponseEntityCallback<ArrayList<DiveCenterSearchItem>>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<ArrayList<DiveCenterSearchItem>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<DiveCenterSearchItem>>(){}.getType();
                ArrayList<DiveCenterSearchItem> diveCenterSearchItems = gson.fromJson(responseString, listType);
                resultListener.onSuccess(diveCenterSearchItems);
            }
        });
    }

    public void postCheckOut(String diveSpotId, @NonNull final ResultListener<Void> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postCheckout(diveSpotId);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener, context));
    }

    public void postAddDiveSpotToFavourites(String diveSpotId, @NonNull final ResultListener<Void> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postAddToFavorites(diveSpotId);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener, context));
    }

    public void deleteDiveSpotFromFavourites(String diveSpotId, @NonNull final ResultListener<Void> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postRemoveFromFavorites(diveSpotId);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener, context));
    }

    public void getSingleReview(String reviewId, ResultListener<ArrayList<CommentEntity>> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getSingleReview(reviewId, 1);
        call.enqueue(new ResponseEntityCallback<ArrayList<CommentEntity>>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<ArrayList<CommentEntity>> resultListener, String responseString) throws JSONException {
                CommentEntity commentEntity = gson.fromJson(responseString, CommentEntity.class);
                ArrayList<CommentEntity> result = new ArrayList<CommentEntity>();
                result.add(commentEntity);
                resultListener.onSuccess(result);
            }
        });
    }

    public void getUsersComments(String userId, final ResultListener<ArrayList<CommentEntity>> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUserComments(userId, 1);
        call.enqueue(new ResponseEntityCallback<ArrayList<CommentEntity>>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<ArrayList<CommentEntity>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<CommentEntity>>() {
                }.getType();
                ArrayList<CommentEntity> comments = new Gson().fromJson(responseString, listType);
                resultListener.onSuccess(comments);
            }
        });
    }

    public void postAddSealife(final ResultListener<SealifeShort> resultListener, MultipartBody.Part image, RequestBody translations) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postAddSealife(image, translations);
        call.enqueue(new ResponseEntityCallback<SealifeShort>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<SealifeShort> resultListener, String responseString) throws JSONException {
                SealifeShort sealifeShort = gson.fromJson(responseString, SealifeShort.class);
                resultListener.onSuccess(sealifeShort);
            }
        });
    }

    public void postUpdateSealife(final ResultListener<Void> resultListener, MultipartBody.Part image, RequestBody translations, RequestBody id) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postUpdateSealife(image, translations, id);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener, context));
    }

    public void postLikeReview(String commentId, @NonNull final ResultListener<Void> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        final Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postLikeReview(commentId);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener, context));
    }

    public void postDislikeReview(String commentId, @NonNull final ResultListener<Void> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        final Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postDislikeReview(commentId);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener, context));
    }

    public void postAddDiveSpot(ResultListener<String> resultListener, List<MultipartBody.Part> sealifes, List<MultipartBody.Part> iamges, List<MultipartBody.Part> maps, RequestBody... requestBodies) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postAddDiveSpot(requestBodies[0], requestBodies[1], requestBodies[2], requestBodies[3], requestBodies[4], requestBodies[5], requestBodies[6], requestBodies[7], requestBodies[8], requestBodies[9], requestBodies[10], requestBodies[11], requestBodies[12], iamges, maps, sealifes);
        call.enqueue(new ResponseEntityCallback<String>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<String> resultListener, String responseString) throws JSONException {
                resultListener.onSuccess(responseString);
            }
        });
    }

    public void postUpdateDiveSpot(ResultListener<Void> resultListener, List<MultipartBody.Part> sealifes, List<MultipartBody.Part> newPhotos, List<MultipartBody.Part> deletedPhotos, List<MultipartBody.Part> new_maps, List<MultipartBody.Part> deleted_maps, RequestBody... requestBodies) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postUpdateDiveSpot(requestBodies[0], requestBodies[1], requestBodies[2], requestBodies[3], requestBodies[4], requestBodies[5], requestBodies[6], requestBodies[7], requestBodies[8], requestBodies[9], requestBodies[10], requestBodies[11], requestBodies[12], requestBodies[13], requestBodies[14], newPhotos, deletedPhotos, new_maps, deleted_maps, sealifes);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener, context));
    }

    public void potUpdateUserProfile(ResultListener<Void> resultListener, MultipartBody.Part image, RequestBody userName, RequestBody userAbout, RequestBody skill, RequestBody diveCenterId, RequestBody diveCenterType) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postUpdateUserProfile(image, userName, userAbout, diveCenterId, diveCenterType, skill);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener, context));
    }

    public void getDiveSpotsByArea(ArrayList<String> sealifes, DiveSpotsRequestMap diveSpotsRequestMap, ResultListener<List<DiveSpotShort>> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveSpotsByFilter(diveSpotsRequestMap, sealifes);
        call.enqueue(new ResponseEntityCallback<List<DiveSpotShort>>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<List<DiveSpotShort>> resultListener, String responseString) {
                Type listType = new TypeToken<List<DiveSpotShort>>() {
                }.getType();
                List<DiveSpotShort> diveSpots = gson.fromJson(responseString, listType);
                resultListener.onSuccess(diveSpots);
            }
        });
    }

    public void getDiveCenters(String id, final ResultListener<ArrayList<DiveCenter>> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveCentersForDiveSpot(id);
        call.enqueue(new ResponseEntityCallback<ArrayList<DiveCenter>>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<ArrayList<DiveCenter>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<DiveCenter>>() {
                }.getType();
                ArrayList<DiveCenter> diveCenters = new Gson().fromJson(responseString, listType);
                resultListener.onSuccess(diveCenters);
            }
        });
    }

    /*Methods using in API v2_0*/

    public void postChangeUserPassword(ResultListener<Void> resultListener, String oldPassword, String newPassword) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(oldPassword, newPassword);
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postChangeUserPassword(changePasswordRequest);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener, context));
    }

    public void getDiveCenterStatusInDiveSpot(ResultListener<FlagsEntity> resultListener, String diveSpotId) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveCenterStatusInSpot(diveSpotId);
        call.enqueue(new ResponseEntityCallback<FlagsEntity>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<FlagsEntity> resultListener, String responseString) throws JSONException {
                FlagsEntity flagsEntity = gson.fromJson(responseString, FlagsEntity.class);
                resultListener.onSuccess(flagsEntity);
            }
        });
    }

    public void getUserStatusInDiveSpot(ResultListener<FlagsEntity> resultListener, String diveSpotId) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUserStatusInSpot(diveSpotId);
        call.enqueue(new ResponseEntityCallback<FlagsEntity>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<FlagsEntity> resultListener, String responseString) throws JSONException {
                FlagsEntity flagsEntity = gson.fromJson(responseString, FlagsEntity.class);
                resultListener.onSuccess(flagsEntity);
            }
        });
    }

    public void getDiveCenterLanguages(ResultListener<ArrayList<Language>> resultListener, String id) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveCenterLanguages(id);
        call.enqueue(new ResponseEntityCallback<ArrayList<Language>>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<ArrayList<Language>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<Language>>() {
                }.getType();
                ArrayList<Language> languages = gson.fromJson(responseString, listType);
                resultListener.onSuccess(languages);
            }
        });
    }

    public void postRemoveInstructorFromDivecenter(ResultListener<Void> resultListener, String userId) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postRemoveInstructorFromDIveCenter(userId);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener, context));
    }

    public void getDiveCenterDiveSpotsList(ResultListener<ArrayList<DiveSpotShort>> resultListener, String id) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveSpotsForDiveCenter(id);
        call.enqueue(new ResponseEntityCallback<ArrayList<DiveSpotShort>>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<ArrayList<DiveSpotShort>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<DiveSpotShort>>() {
                }.getType();
                ArrayList<DiveSpotShort> result = gson.fromJson(responseString, listType);
                resultListener.onSuccess(result);
            }
        });
    }

    public void postInstructorsSee(ResultListener<Void> resultListener, ArrayList<String> ids) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        InstructorsSeeRequests instructorsSeeRequests = new InstructorsSeeRequests(ids);
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postInstructorsSees(instructorsSeeRequests);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener, context));
    }

    public void postNotificationsRead(ResultListener<Void> resultListener, ArrayList<String> id) {
        NotificationsReadRequest request = new NotificationsReadRequest(id);
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postNotificationsRead(request);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener, context));
    }

    public void getDiveCenterInstructorsList(ResultListener<ArrayList<Instructor>> resultListener, String diveCenterId) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getInstructorsList(diveCenterId);
        call.enqueue(new ResponseEntityCallback<ArrayList<Instructor>>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<ArrayList<Instructor>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<Instructor>>() {
                }.getType();
                ArrayList<Instructor> instructors = gson.fromJson(responseString, listType);
                resultListener.onSuccess(instructors);
            }
        });
    }

    public void getNotifications(ResultListener<NotificationsResonseEntity> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getNotifications();
        call.enqueue(new ResponseEntityCallback<NotificationsResonseEntity>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<NotificationsResonseEntity> resultListener, String responseString) throws JSONException {
                NotificationsResonseEntity notificationEntities = gson.fromJson(responseString, NotificationsResonseEntity.class);
                resultListener.onSuccess(notificationEntities);
            }
        });
    }

    public void getActivityNotifications(ResultListener<ArrayList<NotificationEntity>> resultListener, String lastDate) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getActivityNotifications(lastDate, 20, 1);
        call.enqueue(new ResponseEntityCallback<ArrayList<NotificationEntity>>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<ArrayList<NotificationEntity>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<NotificationEntity>>() {
                }.getType();
                new UpdateNotifications(resultListener).execute((ArrayList<NotificationEntity>) gson.fromJson(responseString, listType));
            }
        });
    }

    public void getPersonalNotifications(ResultListener<ArrayList<NotificationEntity>> resultListener, String lastNotificationDate) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getPersonalNotifications(lastNotificationDate, 20, 1);
        call.enqueue(new ResponseEntityCallback<ArrayList<NotificationEntity>>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<ArrayList<NotificationEntity>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<NotificationEntity>>() {
                }.getType();
                ArrayList<NotificationEntity> notificationEntities;
                new UpdateNotifications(resultListener).execute((ArrayList<NotificationEntity>) gson.fromJson(responseString, listType));
//                validateNotifications(resultListener, gson.fromJson(responseString, listType));
//                resultListener.onSuccess(notificationEntities);
            }
        });
    }

    public void getNotificationPhotos(ResultListener<ArrayList<DiveSpotPhoto>> resultListener, String notificationId) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getNotificationPhotos(notificationId, 1);
        call.enqueue(new ResponseEntityCallback<ArrayList<DiveSpotPhoto>>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<ArrayList<DiveSpotPhoto>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<DiveSpotPhoto>>() {
                }.getType();
                ArrayList<DiveSpotPhoto> photos = gson.fromJson(responseString, listType);
                resultListener.onSuccess(photos);
            }
        });
    }

    public void getApproveCount(ResultListener<Integer> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getApproveCount();
        call.enqueue(new ResponseEntityCallback<Integer>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<Integer> resultListener, String responseString) throws JSONException {
                Integer count = gson.fromJson(responseString, Integer.class);
                resultListener.onSuccess(count);
            }
        });
    }

    public void getDiveSpotsForApprove(ResultListener<ArrayList<DiveSpotShort>> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveSpotsToApprove();
        call.enqueue(new ResponseEntityCallback<ArrayList<DiveSpotShort>>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<ArrayList<DiveSpotShort>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<DiveSpotShort>>() {
                }.getType();
                ArrayList<DiveSpotShort> diveSpotShorts = gson.fromJson(responseString, listType);
                resultListener.onSuccess(diveSpotShorts);
            }
        });
    }

    public void getReviewPhotos(ResultListener<ArrayList<DiveSpotPhoto>> resultListener, String id) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getReviewPhotos(id, 1);
        call.enqueue(new ResponseEntityCallback<ArrayList<DiveSpotPhoto>>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<ArrayList<DiveSpotPhoto>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<DiveSpotPhoto>>() {
                }.getType();
                ArrayList<DiveSpotPhoto> photos = gson.fromJson(responseString, listType);
                resultListener.onSuccess(photos);
            }
        });
    }

    public void postAddInstructorToDiveCenter(ResultListener<Void> resultListener, int diveCenterId, int diveCenterType) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postAddInstructorToDiveCenter(diveCenterId, diveCenterType);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener, context));
    }

    public void postUpdateDiveCenterProfile(ResultListener<Void> resultListener, MultipartBody.Part image, List<MultipartBody.Part> emails, List<MultipartBody.Part> phones, List<MultipartBody.Part> diveSpots, List<MultipartBody.Part> languages, RequestBody... requestBodies) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postUpdateDiveCenterProfile(image, requestBodies[0], requestBodies[1], requestBodies[2], requestBodies[3], languages, emails, phones, diveSpots);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener, context));
    }

    public void getUserComments(ResultListener<ArrayList<SelfCommentEntity>> resultListener, String userId) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUserComments(userId, 1);
        call.enqueue(new ResponseEntityCallback<ArrayList<SelfCommentEntity>>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<ArrayList<SelfCommentEntity>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<SelfCommentEntity>>() {
                }.getType();
                ArrayList<SelfCommentEntity> comments = gson.fromJson(responseString, listType);
                resultListener.onSuccess(comments);
            }
        });
    }

    public void getUserLikes(ResultListener<ArrayList<LikeEntity>> resultListener, String userId) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUserLikes(userId);
        call.enqueue(new ResponseEntityCallback<ArrayList<LikeEntity>>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<ArrayList<LikeEntity>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<LikeEntity>>() {
                }.getType();
                ArrayList<LikeEntity> likes = gson.fromJson(responseString, listType);
                resultListener.onSuccess(likes);
            }
        });
    }

    public void getUserDislikes(ResultListener<ArrayList<LikeEntity>> resultListener, String userId) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUserDislikes(userId);
        call.enqueue(new ResponseEntityCallback<ArrayList<LikeEntity>>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<ArrayList<LikeEntity>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<LikeEntity>>() {
                }.getType();
                ArrayList<LikeEntity> dislikes = gson.fromJson(responseString, listType);
                resultListener.onSuccess(dislikes);
            }
        });
    }

    public void postReportReview(ResultListener<Void> resultListener, ReportRequest reportRequest) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postReportReview(reportRequest);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener, context));
    }

    public void postDeleteReview(ResultListener<Void> resultListener, String reviewId) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postDeleteReview(reviewId);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener, context));
    }

    public void getCommentsForDiveSpot(ResultListener<ArrayList<CommentEntity>> resultListener, String diveSpotId) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getCommentsForDiveSpot(diveSpotId, 1);
        call.enqueue(new ResponseEntityCallback<ArrayList<CommentEntity>>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<ArrayList<CommentEntity>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<CommentEntity>>() {
                }.getType();
                ArrayList<CommentEntity> comments = gson.fromJson(responseString, listType);
                resultListener.onSuccess(comments);
            }
        });
    }

    public void postUpdateReview(ResultListener<Void> resultListener, List<MultipartBody.Part> newPhotos, List<MultipartBody.Part> deletedPhotos, List<MultipartBody.Part> sealifes, RequestBody... requestBodies) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postUpdateReview(newPhotos, deletedPhotos, requestBodies[0], requestBodies[1], requestBodies[2], sealifes);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener, context));
    }

    public void postLeaveCommentForDiveSpot(ResultListener<Void> resultListener, List<MultipartBody.Part> images, List<MultipartBody.Part> sealifes, RequestBody... requestBodies) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postLeaveComment(images, requestBodies[0], requestBodies[1], requestBodies[2], sealifes);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener, context));
    }

    public void getUsersFavourites(@NonNull final ResultListener<ArrayList<DiveSpotShort>> resultListener, String id) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        final Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUserFavoritesSpots(id);
        call.enqueue(new ResponseEntityCallback<ArrayList<DiveSpotShort>>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<ArrayList<DiveSpotShort>> resultListener, String responseString) {
                Type listType = new TypeToken<ArrayList<DiveSpotShort>>() {
                }.getType();
                ArrayList<DiveSpotShort> diveSpots = gson.fromJson(responseString, listType);
                resultListener.onSuccess(diveSpots);
            }
        });
    }

    public void getAddedDiveSpots(final ResultListener<ArrayList<DiveSpotShort>> resultListener, String id) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUserAddedDiveSpots(id);
        call.enqueue(new ResponseEntityCallback<ArrayList<DiveSpotShort>>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<ArrayList<DiveSpotShort>> resultListener, String responseString) {
                Type listType = new TypeToken<ArrayList<DiveSpotShort>>() {
                }.getType();
                ArrayList<DiveSpotShort> diveSpots = gson.fromJson(responseString, listType);
                resultListener.onSuccess(diveSpots);
            }
        });
    }

    public void getEditedDiveSpots(final ResultListener<ArrayList<DiveSpotShort>> resultListener, String id) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUserEditedDiveSpots(id);
        call.enqueue(new ResponseEntityCallback<ArrayList<DiveSpotShort>>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<ArrayList<DiveSpotShort>> resultListener, String responseString) {
                Type listType = new TypeToken<ArrayList<DiveSpotShort>>() {
                }.getType();
                ArrayList<DiveSpotShort> diveSpots = gson.fromJson(responseString, listType);
                resultListener.onSuccess(diveSpots);
            }
        });
    }

    public void getUsersCheckins(final ResultListener<ArrayList<DiveSpotShort>> resultListener, String id) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUserCheckedInSpots(id);
        call.enqueue(new ResponseEntityCallback<ArrayList<DiveSpotShort>>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<ArrayList<DiveSpotShort>> resultListener, String responseString) {
                Type listType = new TypeToken<ArrayList<DiveSpotShort>>() {
                }.getType();
                ArrayList<DiveSpotShort> diveSpots = gson.fromJson(responseString, listType);
                resultListener.onSuccess(diveSpots);
            }
        });
    }

    public void postReportImage(ResultListener<Void> resultListener, ReportImageRequest reportImageRequest) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postReportImage(reportImageRequest);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener, context));
    }

    public void getDiveSpotEditors(ResultListener<ArrayList<User>> resultListener, String diveSpotId) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveSpotEditorsList(diveSpotId);
        call.enqueue(new ResponseEntityCallback<ArrayList<User>>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<ArrayList<User>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<User>>() {
                }.getType();
                ArrayList<User> users = gson.fromJson(responseString, listType);
                resultListener.onSuccess(users);
            }
        });
    }

    public void getDiveSpotsCheckedInUsers(ResultListener<ArrayList<User>> resultListener, String diveSpotId) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveSpotsCheckedInUsers(diveSpotId);
        call.enqueue(new ResponseEntityCallback<ArrayList<User>>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<ArrayList<User>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<User>>() {
                }.getType();
                ArrayList<User> users = gson.fromJson(responseString, listType);
                resultListener.onSuccess(users);
            }
        });
    }

    public void postUpdateUserLocation(ResultListener<Void> resultListener, UpdateLocationRequest updateLocationRequest) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postUpdateUserLocation(updateLocationRequest);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener, context));
    }

    public void getDiveSpotLanguages(ResultListener<ArrayList<Language>> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDivespotLanguages();
        call.enqueue(new ResponseEntityCallback<ArrayList<Language>>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<ArrayList<Language>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<Language>>() {
                }.getType();
                ArrayList<Language> result = gson.fromJson(responseString, listType);
                resultListener.onSuccess(result);
            }
        });
    }

    public void getListOfCountries(ResultListener<ArrayList<BaseIdNamePhotoEntity>> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getListCountries();
        call.enqueue(new ResponseEntityCallback<ArrayList<BaseIdNamePhotoEntity>>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<ArrayList<BaseIdNamePhotoEntity>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<BaseIdNamePhotoEntity>>() {
                }.getType();
                ArrayList<BaseIdNamePhotoEntity> countries = gson.fromJson(responseString, listType);
                resultListener.onSuccess(countries);
            }
        });
    }

    public void getDivespotsByName(String query, ResultListener<ArrayList<DiveSpotShort>> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDivespotsByName(query);
        call.enqueue(new ResponseEntityCallback<ArrayList<DiveSpotShort>>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<ArrayList<DiveSpotShort>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<List<DiveSpotShort>>() {
                }.getType();
                ArrayList<DiveSpotShort> diveSpots = gson.fromJson(responseString, listType);
                resultListener.onSuccess(diveSpots);
            }
        });
    }

    public void getAllSealifes(ResultListener<SealifeListResponseEntity> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getAllSealifes();
        call.enqueue(new ResponseEntityCallback<SealifeListResponseEntity>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<SealifeListResponseEntity> resultListener, String responseString) throws JSONException {
                SealifeListResponseEntity sealifeListResponseEntity = gson.fromJson(responseString, SealifeListResponseEntity.class);
                resultListener.onSuccess(sealifeListResponseEntity);
            }
        });

    }

    public void getSealifesByLocation(ResultListener<SealifeListResponseEntity> resultListener, LatLng latLng) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getAllSealifesByLocation(latLng.latitude, latLng.longitude);
        call.enqueue(new ResponseEntityCallback<SealifeListResponseEntity>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<SealifeListResponseEntity> resultListener, String responseString) throws JSONException {
                SealifeListResponseEntity sealifeListResponseEntity = gson.fromJson(responseString, SealifeListResponseEntity.class);
                resultListener.onSuccess(sealifeListResponseEntity);
            }
        });

    }


    public void postUserSignUp(String email, String password, String userType, String lat, String lng, String name, ResultListener<SignUpResponseEntity> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().signUpUser(getSignUpRequest(email, password, name, userType, lat, lng));
        call.enqueue(new ResponseEntityCallback<SignUpResponseEntity>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<SignUpResponseEntity> resultListener, String responseString) throws JSONException {
                SignUpResponseEntity signUpResponseEntity = new Gson().fromJson(responseString, SignUpResponseEntity.class);
                resultListener.onSuccess(signUpResponseEntity);
            }
        });
    }

    public void postUserLogin(String email, String password, String lat, String lng, SignInType signInType, String token, ResultListener<SignUpResponseEntity> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().loginUser(getSignInRequest(email, password, DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserLattitude(), DDScannerApplication.getInstance().getSharedPreferenceHelper().getUserLongitude(), signInType, token));
        call.enqueue(new ResponseEntityCallback<SignUpResponseEntity>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<SignUpResponseEntity> resultListener, String responseString) throws JSONException {
                SignUpResponseEntity signUpResponseEntity = new Gson().fromJson(responseString, SignUpResponseEntity.class);
                resultListener.onSuccess(signUpResponseEntity);
            }
        });
    }

    public void getUserSelfInformation(final ResultListener<User> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getSelfProfileInformation(1);
        call.enqueue(new ResponseEntityCallback<User>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<User> resultListener, String responseString) throws JSONException {
                User user = new Gson().fromJson(responseString, User.class);
                resultListener.onSuccess(user);
            }
        });
    }

    public void getDiveCenterSelfInformation(final ResultListener<DiveCenterProfile> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getSelfDiveCenterInformation(1);
        call.enqueue(new ResponseEntityCallback<DiveCenterProfile>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<DiveCenterProfile> resultListener, String responseString) throws JSONException {
                DiveCenterProfile user = new Gson().fromJson(responseString, DiveCenterProfile.class);
                resultListener.onSuccess(user);
            }
        });
    }

    public void postAddDiveSpotToDiveCenter(String diveSpotId, ResultListener<Void> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postAddDiveSpotToDiveCenter(diveSpotId);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener, context));
    }

    public void postRemoveDiveSpotToDiveCenter(String diveSpotId, ResultListener<Void> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postRemoveDiveSpotToDiveCenter(diveSpotId);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener, context));
    }

    public void getUserProfileInformation(String id, final ResultListener<User> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUserInformation(id, 1);
        call.enqueue(new ResponseEntityCallback<User>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<User> resultListener, String responseString) throws JSONException {
                User user = new Gson().fromJson(responseString, User.class);
                resultListener.onSuccess(user);
            }
        });
    }

    public void getDiveCenterInformation(String id, final ResultListener<DiveCenterProfile> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveCenterInformation(id, 1);
        call.enqueue(new ResponseEntityCallback<DiveCenterProfile>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<DiveCenterProfile> resultListener, String responseString) throws JSONException {
                DiveCenterProfile user = new Gson().fromJson(responseString, DiveCenterProfile.class);
                resultListener.onSuccess(user);
            }
        });
    }

    public void getLegacyDiveCenterInformation(String id, final ResultListener<DiveCenterProfile> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postLegacyDiveCenterInfoGet(Integer.parseInt(id));
        call.enqueue(new ResponseEntityCallback<DiveCenterProfile>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<DiveCenterProfile> resultListener, String responseString) throws JSONException {
                DiveCenterProfile user = new Gson().fromJson(responseString, DiveCenterProfile.class);
                resultListener.onSuccess(user);
            }
        });
    }

    public void getUserAchivements(final ResultListener<ArrayList<AchievementTitle>> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUserAchievements();
        call.enqueue(new ResponseEntityCallback<ArrayList<AchievementTitle>>(gson, resultListener, context) {

            @Override
            void handleResponseString(ResultListener<ArrayList<AchievementTitle>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<AchievementTitle>>() {
                }.getType();
                ArrayList<AchievementTitle> achievmentsResponseEntity = new Gson().fromJson(responseString, listType);
                resultListener.onSuccess(achievmentsResponseEntity);
            }
        });
    }

    public void postApproveDiveSpot(String diveSpotId, boolean value, ResultListener<Void> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        String result;
        if (value) {
            result = "1";
        } else {
            result = "0";
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postApproveDiveSpot(diveSpotId, result);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener, context));
    }

    public void getDiveSpotsTranslations(String diveSpotId, ResultListener<ArrayList<Translation>> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveSpotsTranslations(diveSpotId);
        call.enqueue(new ResponseEntityCallback<ArrayList<Translation>>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<ArrayList<Translation>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<Translation>>() {
                }.getType();
                ArrayList<Translation> translations = gson.fromJson(responseString, listType);
                resultListener.onSuccess(translations);
            }
        });
    }

    public void postCheckIn(String diveSpotId, @NonNull final ResultListener<Void> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postCheckin(diveSpotId);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener, context));
    }

    public void postMapsToDiveSpot(String id, ArrayList<String> images, final ResultListener<Void> resultListener, Context context) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        List<MultipartBody.Part> imagesToSend = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            File image = new File(images.get(i));
            image = Helpers.compressFile(image, context);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), image);
            MultipartBody.Part part = MultipartBody.Part.createFormData("maps[]",
                    image.getName(), requestFile);
            imagesToSend.add(part);
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().addMapsToDiveSpot(RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), id), imagesToSend);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener, this.context));
    }

    public void postPhotosToDiveSpot(String id, ArrayList<String> images, final ResultListener<Void> resultListener, Context context) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        List<MultipartBody.Part> imagesToSend = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            File image = new File(images.get(i));
            image = Helpers.compressFile(image, context);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), image);
            MultipartBody.Part part = MultipartBody.Part.createFormData("photos[]",
                    image.getName(), requestFile);
            imagesToSend.add(part);
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().addPhotosToDiveSpot(RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), id), imagesToSend);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener, this.context));
    }

    public void getUserAddedPhotos(ResultListener<ArrayList<DiveSpotPhoto>> resultListener, String userId) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUserPhotos(userId);
        call.enqueue(new ResponseEntityCallback<ArrayList<DiveSpotPhoto>>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<ArrayList<DiveSpotPhoto>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<DiveSpotPhoto>>() {
                }.getType();
                ArrayList<DiveSpotPhoto> photos = gson.fromJson(responseString, listType);
                resultListener.onSuccess(photos);
            }
        });
    }

    public void getSealifeDetails(String id, ResultListener<Sealife> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getSealifeDetails(id);
        call.enqueue(new ResponseEntityCallback<Sealife>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<Sealife> resultListener, String responseString) throws JSONException {
                Sealife sealife = gson.fromJson(responseString, Sealife.class);
                resultListener.onSuccess(sealife);
            }
        });
    }

    public void requestBooking(DiveCenterRequestBookingRequest request, ResultListener<Void> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postRequestBooking(request);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener, context));
    }

    public void getNewNotificationsCount(ResultListener<NotificationsCountEntity> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getNewNotificationsCount();
        call.enqueue(new ResponseEntityCallback<NotificationsCountEntity>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<NotificationsCountEntity> resultListener, String responseString) throws JSONException {
                NotificationsCountEntity notificationsCountEntity = gson.fromJson(responseString, NotificationsCountEntity.class);
                resultListener.onSuccess(notificationsCountEntity);
            }
        });
    }

    public void getDiveSpotPhotos(String id, ResultListener<DiveSpotPhotosResponseEntity> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveSpotPhotos(id);
        call.enqueue(new ResponseEntityCallback<DiveSpotPhotosResponseEntity>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<DiveSpotPhotosResponseEntity> resultListener, String responseString) throws JSONException {
                DiveSpotPhotosResponseEntity diveSpotPhotosResponseEntity = gson.fromJson(responseString, DiveSpotPhotosResponseEntity.class);
                resultListener.onSuccess(diveSpotPhotosResponseEntity);
            }
        });
    }

    public void postForgotPassword(String email, ResultListener<Void> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postForgotPassword(email);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener, context));
    }

    public void getReviewSealifes(ResultListener<ArrayList<SealifeShort>> resultListener, String reviewId) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getReviewSealifes(reviewId);
        call.enqueue(new ResponseEntityCallback<ArrayList<SealifeShort>>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<ArrayList<SealifeShort>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<SealifeShort>>() {
                }.getType();
                ArrayList<SealifeShort> sealifes = gson.fromJson(responseString, listType);
                resultListener.onSuccess(sealifes);
            }
        });
    }

    public void postDeleteImage(String id, ResultListener<Void> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        DeleteImageRequest deleteImageRequest = new DeleteImageRequest(id);
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postDeleteImage(deleteImageRequest);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener, context));
    }

    public void postLikePhoto(String id, ResultListener<Void> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postLikePhoto(id);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener, context));
    }

    public void postDislikePhoto(String id, ResultListener<Void> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postDislikePhoto(id);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener, context));
    }

    public void getDiveSpotMaps(String id, ResultListener<ArrayList<DiveSpotPhoto>> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveSpotMaps(id);
        call.enqueue(new ResponseEntityCallback<ArrayList<DiveSpotPhoto>>(gson, resultListener, context) {
            @Override
            void handleResponseString(ResultListener<ArrayList<DiveSpotPhoto>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<DiveSpotPhoto>>() {
                }.getType();
                ArrayList<DiveSpotPhoto> photos = gson.fromJson(responseString, listType);
                resultListener.onSuccess(photos);
            }
        });
    }

    private Map<String, String> getDivecentersRequestmap(LatLng latLng) {
        Map<String, String> map = new HashMap<>();
        map.put("latLeft", String.valueOf(latLng.latitude - 2.0));
        map.put("lngLeft", String.valueOf(latLng.longitude - 2.0));
        map.put("lngRight", String.valueOf(latLng.longitude + 2.0));
        map.put("latRight", String.valueOf(latLng.latitude + 2.0));
        return map;
    }

    private Map<String, String> getUserQueryMapRequest() {
        Map<String, String> map = new HashMap<>();
        if (SharedPreferenceHelper.getIsUserSignedIn()) {
            map.put("social", DDScannerApplication.getInstance().getSharedPreferenceHelper().getSn());
            map.put("token", DDScannerApplication.getInstance().getSharedPreferenceHelper().getToken());
            if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getSn().equals("tw")) {
                map.put("secret", DDScannerApplication.getInstance().getSharedPreferenceHelper().getSecret());
            }
        } else {
            return new HashMap<>();
        }
        return map;
    }

    private SignUpRequest getSignUpRequest(String email, String password, String name, String userType, String lat, String lng) {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail(email);
        signUpRequest.setPassword(password);
        signUpRequest.setName(name);
        signUpRequest.setUserType(Helpers.getUserType(userType));
        signUpRequest.setPush(FirebaseInstanceId.getInstance().getToken());
        signUpRequest.setApp_id(FirebaseInstanceId.getInstance().getId());
        signUpRequest.setLat(lat);
        signUpRequest.setLng(lng);
        signUpRequest.setDeviceType(2);
        return signUpRequest;
    }

    private SignInRequest getSignInRequest(String email, String password, String lat, String lng, SignInType signInType, String token) {
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setDeviceType(2);
        signInRequest.setEmail(email);
        signInRequest.setPassword(password);
        if (signInType != null) {
            switch (signInType) {
                case GOOGLE:
                    signInRequest.setProviderType(2);
                    break;
                case FACEBOOK:
                    signInRequest.setProviderType(1);
                    break;
                default:
                    signInRequest.setProviderType(null);
                    break;
            }
        }
        signInRequest.setToken(token);
        signInRequest.setPush(FirebaseInstanceId.getInstance().getToken());
        signInRequest.setApp_id(FirebaseInstanceId.getInstance().getId());
        signInRequest.setLat(lat);
        signInRequest.setLng(lng);
        return signInRequest;
    }

    public static abstract class ResultListener<T> {
        private boolean isCancelled;

        public abstract void onSuccess(T result);

        public abstract void onConnectionFailure();

        public abstract void onError(ErrorType errorType, Object errorData, String url, String errorMessage);

        public abstract void onInternetConnectionClosed();

        public boolean isCancelled() {
            return isCancelled;
        }

        public void setCancelled(boolean cancelled) {
            isCancelled = cancelled;
        }
    }

    public enum ErrorType {
        BAD_REQUEST_ERROR_400, ENTITY_NOT_FOUND_404, RIGHTS_NOT_FOUND_403, UNAUTHORIZED_401, DATA_ALREADY_EXIST_409, DIVE_SPOT_NOT_FOUND_ERROR_C802, COMMENT_NOT_FOUND_ERROR_C803, UNPROCESSABLE_ENTITY_ERROR_422, SERVER_INTERNAL_ERROR_500, IO_ERROR, JSON_SYNTAX_EXCEPTION, UNKNOWN_ERROR
    }

    private class UpdateNotifications extends AsyncTask<ArrayList<NotificationEntity>, Void, ArrayList<NotificationEntity>> {

        ResultListener<ArrayList<NotificationEntity>> resultListener;

        UpdateNotifications(ResultListener<ArrayList<NotificationEntity>> resultListener) {
            this.resultListener = resultListener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<NotificationEntity> notificationEntities) {
            resultListener.onSuccess(notificationEntities);
        }

        @Override
        protected ArrayList<NotificationEntity> doInBackground(ArrayList<NotificationEntity>[] arrayLists) {
            ArrayList<NotificationEntity> newList = new ArrayList<>();
            for (NotificationEntity notificationEntity : arrayLists[0]) {
                switch (notificationEntity.getActivityType()) {
                    case DIVE_SPOT_ADDED:
                    case DIVE_SPOT_CHANGED:
                    case DIVE_SPOT_CHECKIN:
                        if (notificationEntity.getUser() == null || notificationEntity.getDiveSpot() == null) {
                            notificationEntity.setType(-1);
                        }
                        break;
                    case DIVE_SPOT_PHOTO_LIKE:
                        if (notificationEntity.getPhotos() == null || notificationEntity.getUser() == null) {
                            notificationEntity.setType(-1);
                        }
                        break;
                    case DIVE_SPOT_REVIEW_ADDED:
                        if (notificationEntity.getDiveSpot() == null || notificationEntity.getUser() == null || notificationEntity.getReview() == null) {
                            notificationEntity.setType(-1);
                        }
                        break;
                    case DIVE_SPOT_PHOTOS_ADDED:
                        if (notificationEntity.getDiveSpot() == null || notificationEntity.getUser() == null || notificationEntity.getPhotos() == null) {
                            notificationEntity.setType(-1);
                        }
                        break;
                    case DIVE_SPOT_MAPS_ADDED:
                        if (notificationEntity.getDiveSpot() == null || notificationEntity.getUser() == null || notificationEntity.getMaps() == null) {
                            notificationEntity.setType(-1);
                        }
                        break;
                    case DIVE_SPOT_REVIEW_LIKE:
                    case DIVE_SPOT_REVIEW_DISLIKE:
                        if (notificationEntity.getUser() == null || notificationEntity.getReview() == null) {
                            notificationEntity.setType(-1);
                        }
                        break;
                    case ACHIEVEMENT_GETTED:
                        if (notificationEntity.getAchievement() == null) {
                            notificationEntity.setType(-1);
                        }
                        break;
                    case INSTRUCTOR_LEFT_DIVE_CENTER:
                    case DIVE_CENTER_INSTRUCTOR_REMOVE:
                    case DIVE_CENTER_INSTRUCTOR_ADD:
                        if (notificationEntity.getUser() == null) {
                            notificationEntity.setType(-1);
                        }
                        break;
                    default:
                        break;
                }
                notificationEntity.calculateTime();
                notificationEntity.buildLinks();
                newList.add(notificationEntity);
            }
            return newList;
        }
    }


}
