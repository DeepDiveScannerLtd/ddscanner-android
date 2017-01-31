package com.ddscanner.rest;

import android.support.annotation.NonNull;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.entities.AchievmentsResponseEntity;
import com.ddscanner.entities.AddDiveSpotResponseEntity;
import com.ddscanner.entities.AddressComponent;
import com.ddscanner.entities.BaseIdNamePhotoEntity;
import com.ddscanner.entities.CommentEntity;
import com.ddscanner.entities.Comments;
import com.ddscanner.entities.CountryEntity;
import com.ddscanner.entities.DiveCenterProfile;
import com.ddscanner.entities.DiveCentersResponseEntity;
import com.ddscanner.entities.DiveSpotDetailsEntity;
import com.ddscanner.entities.DiveSpotPhoto;
import com.ddscanner.entities.DiveSpotPhotosResponseEntity;
import com.ddscanner.entities.DiveSpotShort;
import com.ddscanner.entities.DivespotsWrapper;
import com.ddscanner.entities.EditDiveSpotWrapper;
import com.ddscanner.entities.FiltersResponseEntity;
import com.ddscanner.entities.FlagsEntity;
import com.ddscanner.entities.ForeignUserDislikesWrapper;
import com.ddscanner.entities.ForeignUserLikeWrapper;
import com.ddscanner.entities.GoogleMapsGeocodeResponseEntity;
import com.ddscanner.entities.Instructor;
import com.ddscanner.entities.Language;
import com.ddscanner.entities.LikeEntity;
import com.ddscanner.entities.ProfileResponseEntity;
import com.ddscanner.entities.RegisterResponse;
import com.ddscanner.entities.Sealife;
import com.ddscanner.entities.SealifeShort;
import com.ddscanner.entities.SelfCommentEntity;
import com.ddscanner.entities.SignInType;
import com.ddscanner.entities.SignUpResponseEntity;
import com.ddscanner.entities.Translation;
import com.ddscanner.entities.User;
import com.ddscanner.entities.UserLikeEntity;
import com.ddscanner.entities.request.DeleteImageRequest;
import com.ddscanner.entities.request.DiveSpotsRequestMap;
import com.ddscanner.entities.request.IdentifyRequest;
import com.ddscanner.entities.request.InstructorsSeeRequests;
import com.ddscanner.entities.request.RegisterRequest;
import com.ddscanner.entities.request.ReportImageRequest;
import com.ddscanner.entities.request.ReportRequest;
import com.ddscanner.entities.request.SignInRequest;
import com.ddscanner.entities.request.SignUpRequest;
import com.ddscanner.entities.request.UpdateLocationRequest;
import com.ddscanner.entities.request.ValidationRequest;
import com.ddscanner.screens.user.profile.DiveCenterProfileFragment;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.Helpers;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

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

    public void getCountryCode(String lat, String lng, final ResultListener<String> resultListener) {
        Call<ResponseBody> call = RestClient.getGoogleMapsApiService().getCountryName(lat + "," + lng);
        call.enqueue(new ResponseEntityCallback<String>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<String> resultListener, String responseString) throws JSONException {
                GoogleMapsGeocodeResponseEntity responseEntity = gson.fromJson(responseString, GoogleMapsGeocodeResponseEntity.class);
                if (responseEntity.getResults().size() > 0 && responseEntity.getResults().get(0).getAddressComponents() != null) {
                    for (AddressComponent addressComponent : responseEntity.getResults().get(0).getAddressComponents()) {
                        if (addressComponent != null && addressComponent.getShortName() != null && addressComponent.getShortName().length() == 2) {
                            resultListener.onSuccess(addressComponent.getShortName());
                            return;
                        }
                    }
                }
            }
        });
    }

    public void getDiveSpotDetails(String diveSpotId, @NonNull final ResultListener<DiveSpotDetailsEntity> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveSpotDetails(diveSpotId);
        call.enqueue(new ResponseEntityCallback<DiveSpotDetailsEntity>(gson, resultListener) {
            @Override
            void handleResponseString(DDScannerRestClient.ResultListener<DiveSpotDetailsEntity> resultListener, String responseString) {
                DiveSpotDetailsEntity diveSpotResponseEntity = gson.fromJson(responseString, DiveSpotDetailsEntity.class);
                resultListener.onSuccess(diveSpotResponseEntity);
            }
        });
    }

    public void postCheckOut(String diveSpotId, @NonNull final ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postCheckout(diveSpotId);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void postAddDiveSpotToFavourites(String diveSpotId, @NonNull final ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postAddToFavorites(diveSpotId);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void deleteDiveSpotFromFavourites(String diveSpotId, @NonNull final ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postRemoveFromFavorites(diveSpotId);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void getComments(String diveSpotId, @NonNull final ResultListener<Comments> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getComments(diveSpotId, getUserQueryMapRequest());
        call.enqueue(new ResponseEntityCallback<Comments>(gson, resultListener) {
            @Override
            void handleResponseString(DDScannerRestClient.ResultListener<Comments> resultListener, String responseString) {
                Comments comments = new Gson().fromJson(responseString, Comments.class);
                resultListener.onSuccess(comments);
            }
        });
    }

    public void getUserLikes(String userId, final ResultListener<ForeignUserLikeWrapper> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getForeignUserLikes(userId, getUserQueryMapRequest());
        call.enqueue(new ResponseEntityCallback<ForeignUserLikeWrapper>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<ForeignUserLikeWrapper> resultListener, String responseString) {
                ForeignUserLikeWrapper foreignUserLikeWrapper = new Gson().fromJson(responseString, ForeignUserLikeWrapper.class);
                resultListener.onSuccess(foreignUserLikeWrapper);
            }
        });
    }

    public void getUserDislikes(String userId, final ResultListener<ForeignUserDislikesWrapper> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getForeignUserDislikes(userId, getUserQueryMapRequest());
        call.enqueue(new ResponseEntityCallback<ForeignUserDislikesWrapper>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<ForeignUserDislikesWrapper> resultListener, String responseString) {
                ForeignUserDislikesWrapper foreignUserDislikesWrapper = new Gson().fromJson(responseString, ForeignUserDislikesWrapper.class);
                resultListener.onSuccess(foreignUserDislikesWrapper);
            }
        });
    }

    public void getUsersComments(String userId, final ResultListener<Comments> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUserComments(userId, getUserQueryMapRequest());
        call.enqueue(new ResponseEntityCallback<Comments>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<Comments> resultListener, String responseString) throws JSONException {
                Comments comments = new Gson().fromJson(responseString, Comments.class);
                resultListener.onSuccess(comments);
            }
        });
    }

    public void putEditComment(String commentId, RequestBody _method, RequestBody comment, RequestBody rating, List<MultipartBody.Part> images_new, List<MultipartBody.Part> images_del, RequestBody token, RequestBody sn, ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().updateComment(commentId, _method, comment, rating, images_new, images_del, token, sn);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void postAddSealife(final ResultListener<Sealife> resultListener, MultipartBody.Part image, RequestBody... requestBodies) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().addSealife(
                image, requestBodies[0], requestBodies[1], requestBodies[2], requestBodies[3], requestBodies[4], requestBodies[5],
                requestBodies[6], requestBodies[7], requestBodies[8], requestBodies[9], requestBodies[10]
                );
        call.enqueue(new ResponseEntityCallback<Sealife>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<Sealife> resultListener, String responseString) throws JSONException {
                JSONObject jsonObject = new JSONObject(responseString);
                responseString = jsonObject.getString(Constants.ADD_DIVE_SPOT_ACTIVITY_SEALIFE);
                Sealife sealife = new Gson().fromJson(responseString, Sealife.class);
                resultListener.onSuccess(sealife);
            }
        });
    }

    public void postValidateDiveSpot(String diveSpotId, boolean isValid, @NonNull final ResultListener<Void> resultListener) {
        ValidationRequest validationRequest = new ValidationRequest();
        validationRequest.setSocial(DDScannerApplication.getInstance().getSharedPreferenceHelper().getSn());
        validationRequest.setToken(DDScannerApplication.getInstance().getSharedPreferenceHelper().getToken());
        validationRequest.setAppId(FirebaseInstanceId.getInstance().getId());
        validationRequest.setpush(FirebaseInstanceId.getInstance().getToken());
        validationRequest.setValid(isValid);
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().divespotValidation(diveSpotId, validationRequest);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void postLikeReview(String commentId, @NonNull final ResultListener<Void> resultListener) {
        final Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postLikeReview(commentId);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void postDislikeReview(String commentId, @NonNull final ResultListener<Void> resultListener) {
        final Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postDislikeReview(commentId);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    @Deprecated
    public void postAddDiveSpot(@NonNull final ResultListener<DiveSpotShort> resultListener, List<MultipartBody.Part> sealife, List<MultipartBody.Part> images, RequestBody... requestBodies) {
        if (requestBodies.length != 13) {
            throw new RuntimeException("RequestBody parameters count must be 13");
        }
        final Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().addDiveSpot(
                requestBodies[0], requestBodies[1], requestBodies[2], requestBodies[3], requestBodies[4], requestBodies[5],
                requestBodies[6], requestBodies[7], requestBodies[8], requestBodies[9],
                sealife, images, requestBodies[10], requestBodies[11], requestBodies[12]
        );
        call.enqueue(new ResponseEntityCallback<DiveSpotShort>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<DiveSpotShort> resultListener, String responseString) throws JSONException {
                JSONObject jsonObject = new JSONObject(responseString);
                String diveSpotString = jsonObject.getString(Constants.ADD_DIVE_SPOT_ACTIVITY_DIVESPOT);
                DiveSpotShort diveSpotShort = new Gson().fromJson(diveSpotString, DiveSpotShort.class);
                resultListener.onSuccess(diveSpotShort);
            }
        });
    }

    public void postAddDiveSpot(ResultListener<String> resultListener, List<MultipartBody.Part> sealifes, List<MultipartBody.Part> iamges,List<MultipartBody.Part> maps, RequestBody... requestBodies ) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postAddDiveSpot(requestBodies[0], requestBodies[1], requestBodies[2], requestBodies[3], requestBodies[4], requestBodies[5], requestBodies[6], requestBodies[7], requestBodies[8], requestBodies[9], requestBodies[10], requestBodies[11], requestBodies[12], iamges, maps, sealifes);
        call.enqueue(new ResponseEntityCallback<String>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<String> resultListener, String responseString) throws JSONException {
                resultListener.onSuccess(responseString);
            }
        });
    }

    public void postUpdateDiveSpot(ResultListener<Void> resultListener, List<MultipartBody.Part> sealifes, List<MultipartBody.Part> newPhotos, List<MultipartBody.Part> deletedPhotos, List<MultipartBody.Part> new_maps, List<MultipartBody.Part> deleted_maps, RequestBody... requestBodies) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postUpdateDiveSpot(requestBodies[0], requestBodies[1], requestBodies[2], requestBodies[3], requestBodies[4], requestBodies[5], requestBodies[6], requestBodies[7], requestBodies[8], requestBodies[9], requestBodies[10], requestBodies[11], requestBodies[12], requestBodies[13], newPhotos, deletedPhotos, new_maps, deleted_maps, sealifes);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void potUpdateUserProfile(ResultListener<Void> resultListener, MultipartBody.Part image, RequestBody userName, RequestBody userAbout, RequestBody skill, RequestBody diveCenterId) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postUpdateUserProfile(image, userName, userAbout, diveCenterId, skill);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void getDiveSpotsByArea(DiveSpotsRequestMap diveSpotsRequestMap, ResultListener<List<DiveSpotShort>> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveSpotsByFilter(diveSpotsRequestMap);
        call.enqueue(new ResponseEntityCallback<List<DiveSpotShort>>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<List<DiveSpotShort>> resultListener, String responseString) {
                Type listType = new TypeToken<List<DiveSpotShort>>(){}.getType();
                List<DiveSpotShort> diveSpots =gson.fromJson(responseString, listType);
                resultListener.onSuccess(diveSpots);
            }
        });
    }

    public void getDiveCenters(LatLng latLng, final ResultListener<DiveCentersResponseEntity> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveCenters(getDivecentersRequestmap(latLng));
        call.enqueue(new ResponseEntityCallback<DiveCentersResponseEntity>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<DiveCentersResponseEntity> resultListener, String responseString) throws JSONException {
                DiveCentersResponseEntity diveCentersResponseEntity = new Gson().fromJson(responseString, DiveCentersResponseEntity.class);
                resultListener.onSuccess(diveCentersResponseEntity);
            }
        });
    }

    /*Methods using in API v2_0*/

    public void getDiveCenterStatusInDiveSpot(ResultListener<FlagsEntity> resultListener, String diveSpotId) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveCenterStatusInSpot(diveSpotId);
        call.enqueue(new ResponseEntityCallback<FlagsEntity>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<FlagsEntity> resultListener, String responseString) throws JSONException {
                FlagsEntity flagsEntity = gson.fromJson(responseString, FlagsEntity.class);
                resultListener.onSuccess(flagsEntity);
            }
        });
    }

    public void getUserStatusInDiveSpot(ResultListener<FlagsEntity> resultListener, String diveSpotId) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUserStatusInSpot(diveSpotId);
        call.enqueue(new ResponseEntityCallback<FlagsEntity>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<FlagsEntity> resultListener, String responseString) throws JSONException {
                FlagsEntity flagsEntity = gson.fromJson(responseString, FlagsEntity.class);
                resultListener.onSuccess(flagsEntity);
            }
        });
    }

    public void getDiveCenterLanguages(ResultListener<ArrayList<Language>> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveCenterLanguages();
        call.enqueue(new ResponseEntityCallback<ArrayList<Language>>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<ArrayList<Language>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<Language>>(){}.getType();
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
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void getSelfDiveCenterDiveSpotsList(ResultListener<ArrayList<DiveSpotShort>> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getSelfDiveSpotsForDiveCenter();
        call.enqueue(new ResponseEntityCallback<ArrayList<DiveSpotShort>>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<ArrayList<DiveSpotShort>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<DiveSpotShort>>(){}.getType();
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
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void getDiveCenterInstructorsList(ResultListener<ArrayList<Instructor>> resultListener, String diveCenterId) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getInstructorsList(diveCenterId);
        call.enqueue(new ResponseEntityCallback<ArrayList<Instructor>>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<ArrayList<Instructor>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<Instructor>>(){}.getType();
                ArrayList<Instructor> instructors = gson.fromJson(responseString, listType);
                resultListener.onSuccess(instructors);
            }
        });
    }

    public void getReviewPhotos(ResultListener<ArrayList<DiveSpotPhoto>> resultListener, String id) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call= RestClient.getDdscannerServiceInstance().getReviewPhotos(id, 1);
        call.enqueue(new ResponseEntityCallback<ArrayList<DiveSpotPhoto>>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<ArrayList<DiveSpotPhoto>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<DiveSpotPhoto>>(){}.getType();
                ArrayList<DiveSpotPhoto> photos = gson.fromJson(responseString, listType);
                resultListener.onSuccess(photos);
            }
        });
    }

    public void postAddInstructorToDiveCenter(ResultListener<Void> resultListener, String diveCenterId) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postAddIstructorToDiveCenter(diveCenterId);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void getDiveCentersList(ResultListener<ArrayList<BaseIdNamePhotoEntity>> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveCentersList("%", "10000");
        call.enqueue(new ResponseEntityCallback<ArrayList<BaseIdNamePhotoEntity>>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<ArrayList<BaseIdNamePhotoEntity>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<BaseIdNamePhotoEntity>>(){}.getType();
                ArrayList<BaseIdNamePhotoEntity> list = gson.fromJson(responseString, listType);
                resultListener.onSuccess(list);
            }
        });
    }

    public void postUpdateDiveCenterProfile(ResultListener<Void> resultListener, MultipartBody.Part image, List<MultipartBody.Part> emails, List<MultipartBody.Part> phones, List<MultipartBody.Part> diveSpots, List<MultipartBody.Part> languages, RequestBody... requestBodies) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postUpdateDiveCenterProfile(image, requestBodies[0], requestBodies[1], requestBodies[2], requestBodies[3], languages, emails, phones, diveSpots);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void getUsersSelfComments(ResultListener<ArrayList<SelfCommentEntity>> resultListener, String userId) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getSelfCommentsList(userId);
        call.enqueue(new ResponseEntityCallback<ArrayList<SelfCommentEntity>>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<ArrayList<SelfCommentEntity>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<SelfCommentEntity>>(){}.getType();
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
        call.enqueue(new ResponseEntityCallback<ArrayList<LikeEntity>>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<ArrayList<LikeEntity>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<LikeEntity>>(){}.getType();
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
        call.enqueue(new ResponseEntityCallback<ArrayList<LikeEntity>>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<ArrayList<LikeEntity>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<LikeEntity>>(){}.getType();
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
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void postDeleteReview(ResultListener<Void> resultListener, String reviewId) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postDeleteReview(reviewId);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void getCommentsForDiveSpot(ResultListener<ArrayList<CommentEntity>> resultListener, String diveSpotId) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getCommentsForDiveSpot(diveSpotId, 1);
        call.enqueue(new ResponseEntityCallback<ArrayList<CommentEntity>>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<ArrayList<CommentEntity>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<CommentEntity>>(){}.getType();
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
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void postLeaveCommentForDiveSpot(ResultListener<Void> resultListener, List<MultipartBody.Part> images,  List<MultipartBody.Part> sealifes, RequestBody... requestBodies) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postLeaveComment(images, requestBodies[0], requestBodies[1], requestBodies[2], sealifes);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void getUsersFavourites(@NonNull final ResultListener<ArrayList<DiveSpotShort>> resultListener, String id) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        final Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUserFavoritesSpots(id);
        call.enqueue(new ResponseEntityCallback<ArrayList<DiveSpotShort>>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<ArrayList<DiveSpotShort>> resultListener, String responseString) {
                Type listType = new TypeToken<ArrayList<DiveSpotShort>>(){}.getType();
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
        call.enqueue(new ResponseEntityCallback<ArrayList<DiveSpotShort>>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<ArrayList<DiveSpotShort>> resultListener, String responseString) {
                Type listType = new TypeToken<ArrayList<DiveSpotShort>>(){}.getType();
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
        call.enqueue(new ResponseEntityCallback<ArrayList<DiveSpotShort>>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<ArrayList<DiveSpotShort>> resultListener, String responseString) {
                Type listType = new TypeToken<ArrayList<DiveSpotShort>>(){}.getType();
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
        call.enqueue(new ResponseEntityCallback<ArrayList<DiveSpotShort>>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<ArrayList<DiveSpotShort>> resultListener, String responseString) {
                Type listType = new TypeToken<ArrayList<DiveSpotShort>>(){}.getType();
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
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void getDiveSpotEditors(ResultListener<ArrayList<User>> resultListener, String diveSpotId) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveSpotEditorsList(diveSpotId);
        call.enqueue(new ResponseEntityCallback<ArrayList<User>>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<ArrayList<User>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<User>>(){}.getType();
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
        call.enqueue(new ResponseEntityCallback<ArrayList<User>>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<ArrayList<User>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<User>>(){}.getType();
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
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void getDiveSpotLanguages(ResultListener<ArrayList<Language>> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDivespotLanguages();
        call.enqueue(new ResponseEntityCallback<ArrayList<Language>>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<ArrayList<Language>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<Language>>(){}.getType();
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
        call.enqueue(new ResponseEntityCallback<ArrayList<BaseIdNamePhotoEntity>>(gson,resultListener) {
            @Override
            void handleResponseString(ResultListener<ArrayList<BaseIdNamePhotoEntity>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<BaseIdNamePhotoEntity>>(){}.getType();
                ArrayList<BaseIdNamePhotoEntity> countries  = gson.fromJson(responseString, listType);
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
        call.enqueue(new ResponseEntityCallback<ArrayList<DiveSpotShort>>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<ArrayList<DiveSpotShort>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<List<DiveSpotShort>>(){}.getType();
                ArrayList<DiveSpotShort> diveSpots = gson.fromJson(responseString, listType);
                resultListener.onSuccess(diveSpots);
            }
        });
    }

    public void getSealifesByQuery(ResultListener<ArrayList<SealifeShort>> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getSealifesByLimit(100000);
        call.enqueue(new ResponseEntityCallback<ArrayList<SealifeShort>>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<ArrayList<SealifeShort>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<SealifeShort>>(){}.getType();
                ArrayList<SealifeShort> sealifeShorts = gson.fromJson(responseString, listType);
                resultListener.onSuccess(sealifeShorts);
            }
        });

    }

    public void postUserSignUp(String email, String password, String userType, String lat, String lng, String name, ResultListener<SignUpResponseEntity> resultListener) {
        // TODO Implement name setting and remove hardcode
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().signUpUser(getSignUpRequest(email, password, name, userType, lat, lng));
        call.enqueue(new ResponseEntityCallback<SignUpResponseEntity>(gson, resultListener) {
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
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().loginUser(getSignInRequest(email, password, lat, lng, signInType, token));
        call.enqueue(new ResponseEntityCallback<SignUpResponseEntity>(gson, resultListener) {
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
        call.enqueue(new ResponseEntityCallback<User>(gson, resultListener) {
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
        call.enqueue(new ResponseEntityCallback<DiveCenterProfile>(gson, resultListener) {
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
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void postRemoveDiveSpotToDiveCenter(String diveSpotId, ResultListener<Void> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postRemoveDiveSpotToDiveCenter(diveSpotId);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void getUserProfileInformation(String id, final ResultListener<User> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUserInformation(id, 1);
        call.enqueue(new ResponseEntityCallback<User>(gson, resultListener) {
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
        call.enqueue(new ResponseEntityCallback<DiveCenterProfile>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<DiveCenterProfile> resultListener, String responseString) throws JSONException {
                DiveCenterProfile user = new Gson().fromJson(responseString, DiveCenterProfile.class);
                resultListener.onSuccess(user);
            }
        });
    }

    public void getUserAchivements(final ResultListener<AchievmentsResponseEntity> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUserAchievements();
        call.enqueue(new ResponseEntityCallback<AchievmentsResponseEntity>(gson, resultListener) {

            @Override
            void handleResponseString(ResultListener<AchievmentsResponseEntity> resultListener, String responseString) throws JSONException {
                AchievmentsResponseEntity achievmentsResponseEntity = new Gson().fromJson(responseString, AchievmentsResponseEntity.class);
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
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void getDiveSpotsTranslations(String diveSpotId, ResultListener<ArrayList<Translation>> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveSpotsTranslations(diveSpotId);
        call.enqueue(new ResponseEntityCallback<ArrayList<Translation>>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<ArrayList<Translation>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<Translation>>(){}.getType();
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
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void postMapsToDiveSpot(String id, ArrayList<String> images, final ResultListener<Void> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        List<MultipartBody.Part> imagesToSend = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            File image = new File(images.get(i));
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), image);
            MultipartBody.Part part = MultipartBody.Part.createFormData(Constants.ADD_DIVE_SPOT_ACTIVITY_IMAGES_ARRAY,
                    image.getName(), requestFile);
            imagesToSend.add(part);
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().addMapsToDiveSpot(RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), id), imagesToSend );
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void postPhotosToDiveSpot(String id, ArrayList<String> images, final ResultListener<Void> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        List<MultipartBody.Part> imagesToSend = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            File image = new File(images.get(i));
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), image);
            MultipartBody.Part part = MultipartBody.Part.createFormData("photos[]",
                    image.getName(), requestFile);
            imagesToSend.add(part);
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().addPhotosToDiveSpot(RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), id), imagesToSend );
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void getUserAddedPhotos(ResultListener<ArrayList<DiveSpotPhoto>> resultListener, String userId) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUserPhotos(userId);
        call.enqueue(new ResponseEntityCallback<ArrayList<DiveSpotPhoto>>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<ArrayList<DiveSpotPhoto>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<DiveSpotPhoto>>(){}.getType();
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
        call.enqueue(new ResponseEntityCallback<Sealife>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<Sealife> resultListener, String responseString) throws JSONException {
                Sealife sealife = gson.fromJson(responseString, Sealife.class);
                resultListener.onSuccess(sealife);
            }
        });
    }

    public void getDiveSpotPhotos(String id, ResultListener<DiveSpotPhotosResponseEntity> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveSpotPhotos(id);
        call.enqueue(new ResponseEntityCallback<DiveSpotPhotosResponseEntity>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<DiveSpotPhotosResponseEntity> resultListener, String responseString) throws JSONException {
                DiveSpotPhotosResponseEntity diveSpotPhotosResponseEntity = gson.fromJson(responseString, DiveSpotPhotosResponseEntity.class);
                resultListener.onSuccess(diveSpotPhotosResponseEntity);
            }
        });
    }

    public void postForgotPassword(String email, ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postForgotPassword(email);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void postDeleteImage(String id, ResultListener<Void> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        DeleteImageRequest deleteImageRequest = new DeleteImageRequest(id);
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postDeleteImage(deleteImageRequest);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void postLikePhoto(String id, ResultListener<Void> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postLikePhoto(id);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void postDislikePhoto(String id, ResultListener<Void> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postDislikePhoto(id);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void getDiveSpotMaps(String id, ResultListener<ArrayList<DiveSpotPhoto>> resultListener) {
        if (!Helpers.hasConnection(DDScannerApplication.getInstance())) {
            resultListener.onInternetConnectionClosed();
            return;
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveSpotMaps(id);
        call.enqueue(new ResponseEntityCallback<ArrayList<DiveSpotPhoto>>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<ArrayList<DiveSpotPhoto>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<DiveSpotPhoto>>(){}.getType();
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
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getIsUserSignedIn()) {
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

    private RegisterRequest generateRegisterRequest(String appId, String socialNetworkName, String token) {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setAppId(appId);
        registerRequest.setSocial(socialNetworkName);
        registerRequest.setToken(token);
        return registerRequest;
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
        BAD_REQUEST_ERROR_400, ENTITY_NOT_FOUND_404, RIGHTS_NOT_FOUND_403, UNAUTHORIZED_401, DIVE_SPOT_NOT_FOUND_ERROR_C802, COMMENT_NOT_FOUND_ERROR_C803, UNPROCESSABLE_ENTITY_ERROR_422, SERVER_INTERNAL_ERROR_500, IO_ERROR, JSON_SYNTAX_EXCEPTION, UNKNOWN_ERROR
    }
}
