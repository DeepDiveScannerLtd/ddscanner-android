package com.ddscanner.rest;

import android.support.annotation.NonNull;

import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.AchievmentsResponseEntity;
import com.ddscanner.entities.CheckIns;
import com.ddscanner.entities.Comments;
import com.ddscanner.entities.DiveCentersResponseEntity;
import com.ddscanner.entities.DiveSpot;
import com.ddscanner.entities.DiveSpotDetails;
import com.ddscanner.entities.DivespotsWrapper;
import com.ddscanner.entities.EditDiveSpotWrapper;
import com.ddscanner.entities.FiltersResponseEntity;
import com.ddscanner.entities.ForeignUserDislikesWrapper;
import com.ddscanner.entities.ForeignUserLikeWrapper;
import com.ddscanner.entities.Notifications;
import com.ddscanner.entities.RegisterResponse;
import com.ddscanner.entities.Sealife;
import com.ddscanner.entities.SealifeResponseEntity;
import com.ddscanner.entities.SignInType;
import com.ddscanner.entities.SignUpResponseEntity;
import com.ddscanner.entities.User;
import com.ddscanner.entities.UserResponseEntity;
import com.ddscanner.entities.request.DiveSpotsRequestMap;
import com.ddscanner.entities.request.IdentifyRequest;
import com.ddscanner.entities.request.RegisterRequest;
import com.ddscanner.entities.request.ReportRequest;
import com.ddscanner.entities.request.SignInRequest;
import com.ddscanner.entities.request.SignUpRequest;
import com.ddscanner.entities.request.ValidationRequest;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class DDScannerRestClient {

    private Gson gson = new Gson();

    public void getDiveSpotDetails(String diveSpotId, @NonNull final ResultListener<DiveSpotDetails> resultListener) {
        Map<String, String> map = getUserQueryMapRequest();
        map.put("isImageAuthor", "true");
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveSpotById(diveSpotId, map);
        call.enqueue(new ResponseEntityCallback<DiveSpotDetails>(gson, resultListener) {
            @Override
            void handleResponseString(DDScannerRestClient.ResultListener<DiveSpotDetails> resultListener, String responseString) {
                DiveSpotDetails diveSpotDetails = new Gson().fromJson(responseString, DiveSpotDetails.class);
                resultListener.onSuccess(diveSpotDetails);
            }
        });
    }

    public void postCheckIn(String diveSpotId, @NonNull final ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().checkIn(diveSpotId, getRegisterRequest());
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void postCheckOut(String diveSpotId, @NonNull final ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().checkOut(diveSpotId, getUserQueryMapRequest());
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void postAddDiveSpotToFavourites(String diveSpotId, @NonNull final ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().addDiveSpotToFavourites(diveSpotId, getRegisterRequest());
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void deleteDiveSpotFromFavourites(String diveSpotId, @NonNull final ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().removeSpotFromFavorites(diveSpotId, getUserQueryMapRequest());
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void getCheckIns(String diveSpotId, @NonNull final ResultListener<CheckIns> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getCheckins(diveSpotId);
        call.enqueue(new ResponseEntityCallback<CheckIns>(gson, resultListener) {
            @Override
            void handleResponseString(DDScannerRestClient.ResultListener<CheckIns> resultListener, String responseString) {
                CheckIns checkIns = new Gson().fromJson(responseString, CheckIns.class);
                resultListener.onSuccess(checkIns);
            }
        });
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

    public void getAddedDiveSpots(String userId, final ResultListener<DivespotsWrapper> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUsersAdded(userId, getUserQueryMapRequest());
        call.enqueue(new ResponseEntityCallback<DivespotsWrapper>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<DivespotsWrapper> resultListener, String responseString) {
                DivespotsWrapper divespotsWrapper = new Gson().fromJson(responseString, DivespotsWrapper.class);
                resultListener.onSuccess(divespotsWrapper);
            }
        });
    }

    public void getEditedDiveSpots(String userId, final ResultListener<DivespotsWrapper> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUsersEdited(userId, getUserQueryMapRequest());
        call.enqueue(new ResponseEntityCallback<DivespotsWrapper>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<DivespotsWrapper> resultListener, String responseString) {
                DivespotsWrapper divespotsWrapper = new Gson().fromJson(responseString, DivespotsWrapper.class);
                resultListener.onSuccess(divespotsWrapper);
            }
        });
    }

    public void getUsersCheckins(String userId, final ResultListener<DivespotsWrapper> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUsersCheckins(userId, getUserQueryMapRequest());
        call.enqueue(new ResponseEntityCallback<DivespotsWrapper>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<DivespotsWrapper> resultListener, String responseString) {
                DivespotsWrapper divespotsWrapper = new Gson().fromJson(responseString, DivespotsWrapper.class);
                resultListener.onSuccess(divespotsWrapper);
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

    public void postReportImage(String imageName, String reportName, String reportDescription, final ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().reportImage(getReportRequest(reportName, reportDescription, imageName));
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void deleteImage(String imageName, final ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().deleteImage(imageName, getUserQueryMapRequest());
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void getDiveSpotsByParameters(RequestBody name, List<MultipartBody.Part> like, RequestBody order, RequestBody sort, RequestBody limit, List<MultipartBody.Part> select, final ResultListener<DivespotsWrapper> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDivespotsByParameters(name, like, order, sort, limit, select);
        call.enqueue(new ResponseEntityCallback<DivespotsWrapper>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<DivespotsWrapper> resultListener, String responseString) {
                DivespotsWrapper divespotsWrapper = new Gson().fromJson(responseString, DivespotsWrapper.class);
                resultListener.onSuccess(divespotsWrapper);
            }
        });
    }

    public void getUserInformation(String userId, final ResultListener<UserResponseEntity> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUserInfo(userId, getUserQueryMapRequest());
        call.enqueue(new ResponseEntityCallback<UserResponseEntity>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<UserResponseEntity> resultListener, String responseString) throws JSONException {
                UserResponseEntity userResponseEntity = new Gson().fromJson(responseString, UserResponseEntity.class);
                resultListener.onSuccess(userResponseEntity);
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

    public void postLeaveReview(RequestBody id, RequestBody comment, RequestBody rating, List<MultipartBody.Part> image, RequestBody token, RequestBody sn, final ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().addCommentToDiveSpot(id, comment, rating, image, token, sn);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void putEditComment(String commentId, RequestBody _method, RequestBody comment, RequestBody rating, List<MultipartBody.Part> images_new, List<MultipartBody.Part> images_del, RequestBody token, RequestBody sn, ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().updateComment(commentId, _method, comment, rating, images_new, images_del, token, sn);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void postAddPhotosToDiveSpot(String diveSpotId, List<MultipartBody.Part> images, final ResultListener<Void> resultListener, RequestBody... requestBodyies) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().addImagesToDiveSpot(
                diveSpotId, images, requestBodyies[0], requestBodyies[1], requestBodyies[2]
        );
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

    public void getAllSealifes(final ResultListener<SealifeResponseEntity> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getSealifes();
        call.enqueue(new ResponseEntityCallback<SealifeResponseEntity>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<SealifeResponseEntity> resultListener, String responseString) throws JSONException {
                SealifeResponseEntity sealifeResponseEntity = new Gson().fromJson(responseString, SealifeResponseEntity.class);
                resultListener.onSuccess(sealifeResponseEntity);
            }
        });
    }

    public void postLogout(final ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().logout(getRegisterRequest());
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void putUpdateUserProfileInfo(final ResultListener<UserResponseEntity> resultListener, String id, MultipartBody.Part image, RequestBody... requestBodies) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().updateUserById(id, image, requestBodies[0], requestBodies[1], requestBodies[2], requestBodies[3], requestBodies[4], requestBodies[5]);
        call.enqueue(new ResponseEntityCallback<UserResponseEntity>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<UserResponseEntity> resultListener, String responseString) throws JSONException {
                UserResponseEntity userResponseEntity = new Gson().fromJson(responseString, UserResponseEntity.class);
                resultListener.onSuccess(userResponseEntity);
            }
        });
    }

    public void postValidateDiveSpot(String diveSpotId, boolean isValid, @NonNull final ResultListener<Void> resultListener) {
        ValidationRequest validationRequest = new ValidationRequest();
        validationRequest.setSocial(SharedPreferenceHelper.getSn());
        validationRequest.setToken(SharedPreferenceHelper.getToken());
        validationRequest.setAppId(FirebaseInstanceId.getInstance().getId());
        validationRequest.setpush(FirebaseInstanceId.getInstance().getToken());
        validationRequest.setValid(isValid);
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().divespotValidation(diveSpotId, validationRequest);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void getDiveSpotPhotos(String diveSpotId, @NonNull final ResultListener<DiveSpotDetails> resultListener) {
        Map<String, String> map = getUserQueryMapRequest();
        map.put("isImageAuthor", "true");
        final Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveSpotImages(diveSpotId, map);
        call.enqueue(new ResponseEntityCallback<DiveSpotDetails>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<DiveSpotDetails> resultListener, String responseString) {
                DiveSpotDetails diveSpotDetails = new Gson().fromJson(responseString, DiveSpotDetails.class);
                resultListener.onSuccess(diveSpotDetails);
            }
        });
    }

    public void getReportTypes(@NonNull final ResultListener<FiltersResponseEntity> resultListener) {
        final Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getFilters();
        call.enqueue(new ResponseEntityCallback<FiltersResponseEntity>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<FiltersResponseEntity> resultListener, String responseString) {
                FiltersResponseEntity filtersResponseEntity = new Gson().fromJson(responseString, FiltersResponseEntity.class);
                resultListener.onSuccess(filtersResponseEntity);
            }
        });
    }

    public void deleteUserComment(String commentId, @NonNull final ResultListener<Void> resultListener) {
        Map<String, String> map = getUserQueryMapRequest();
        final Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().deleteComment(commentId, map);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void postSendReportToComment(String reportType, String reportDescription, String commentId, @NonNull final ResultListener<Void> resultListener) {
        final Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().reportComment(commentId, getReportRequest(reportType, reportDescription));
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void postLikeReview(String commentId, @NonNull final ResultListener<Void> resultListener) {
        final Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().likeComment(commentId, getRegisterRequest());
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void postDislikeReview(String commentId, @NonNull final ResultListener<Void> resultListener) {
        final Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().dislikeComment(commentId, getRegisterRequest());
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void postIdentifyUser(String lat, String lng, ResultListener<Void> resultListener) {
        final Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().identify(getUserIdentifyData(lat, lng));
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void postLogin(String appId, SignInType signInType, String token, @NonNull final ResultListener<RegisterResponse> resultListener) {
        final Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().login(generateRegisterRequest(appId, signInType.getName(), token));
        call.enqueue(new ResponseEntityCallback<RegisterResponse>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<RegisterResponse> resultListener, String responseString) {
                RegisterResponse registerResponse = new Gson().fromJson(responseString, RegisterResponse.class);
                resultListener.onSuccess(registerResponse);
            }
        });
    }

    public void getFilters(@NonNull final ResultListener<FiltersResponseEntity> resultListener) {
        final Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getFilters();
        call.enqueue(new ResponseEntityCallback<FiltersResponseEntity>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<FiltersResponseEntity> resultListener, String responseString) {
                FiltersResponseEntity filtersResponseEntity = new Gson().fromJson(responseString, FiltersResponseEntity.class);
                resultListener.onSuccess(filtersResponseEntity);
            }
        });
    }

    public void getUserNotifications(@NonNull final ResultListener<Notifications> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getNotifications(SharedPreferenceHelper.getUserServerId(), getUserQueryMapRequest());
        call.enqueue(new ResponseEntityCallback<Notifications>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<Notifications> resultListener, String responseString) throws JSONException {
                Notifications notifications = new Gson().fromJson(responseString, Notifications.class);
                resultListener.onSuccess(notifications);
            }
        });

    }

    public void postAddDiveSpot(@NonNull final ResultListener<DiveSpot> resultListener, List<MultipartBody.Part> sealife, List<MultipartBody.Part> images, RequestBody... requestBodies) {
        if (requestBodies.length != 13) {
            throw new RuntimeException("RequestBody parameters count must be 13");
        }
        final Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().addDiveSpot(
                requestBodies[0], requestBodies[1], requestBodies[2], requestBodies[3], requestBodies[4], requestBodies[5],
                requestBodies[6], requestBodies[7], requestBodies[8], requestBodies[9],
                sealife, images, requestBodies[10], requestBodies[11], requestBodies[12]
        );
        call.enqueue(new ResponseEntityCallback<DiveSpot>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<DiveSpot> resultListener, String responseString) throws JSONException {
                JSONObject jsonObject = new JSONObject(responseString);
                String diveSpotString = jsonObject.getString(Constants.ADD_DIVE_SPOT_ACTIVITY_DIVESPOT);
                DiveSpot diveSpot = new Gson().fromJson(diveSpotString, DiveSpot.class);
                resultListener.onSuccess(diveSpot);
            }
        });
    }

    public void getUsersFavourites(String userId, @NonNull final ResultListener<DivespotsWrapper> resultListener) {
        final Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUsersFavorites(userId, getUserQueryMapRequest());
        call.enqueue(new ResponseEntityCallback<DivespotsWrapper>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<DivespotsWrapper> resultListener, String responseString) {
                DivespotsWrapper filtersResponseEntity = new Gson().fromJson(responseString, DivespotsWrapper.class);
                resultListener.onSuccess(filtersResponseEntity);
            }
        });
    }

    public void getDiveSpotForEdit(String diveSpotId, @NonNull final ResultListener<EditDiveSpotWrapper> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveSpotForEdit(diveSpotId, getUserQueryMapRequest());
        call.enqueue(new ResponseEntityCallback<EditDiveSpotWrapper>(gson, resultListener) {
            @Override
            void handleResponseString(DDScannerRestClient.ResultListener<EditDiveSpotWrapper> resultListener, String responseString) {
                EditDiveSpotWrapper diveSpotDetailsWrapper = new Gson().fromJson(responseString, EditDiveSpotWrapper.class);
                resultListener.onSuccess(diveSpotDetailsWrapper);
            }
        });
    }

    public void putUpdateDiveSpot(String diveSpotId, List<MultipartBody.Part> sealifeRequest, List<MultipartBody.Part> newImages, List<MultipartBody.Part> deletedImages, @NonNull final ResultListener<Void> resultListener, RequestBody... requestBodies) {
        if (requestBodies.length != 14) {
            throw new RuntimeException("RequestBody parameters count must be 14");
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().updateDiveSpot(
                diveSpotId,
                requestBodies[0],
                requestBodies[1],
                requestBodies[2],
                requestBodies[3],
                requestBodies[4],
                requestBodies[5],
                requestBodies[6],
                requestBodies[7],
                requestBodies[8],
                requestBodies[9],
                requestBodies[10],
                sealifeRequest,
                newImages,
                deletedImages,
                requestBodies[11],
                requestBodies[12],
                requestBodies[13]
        );
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void getDiveSpotsByArea(DiveSpotsRequestMap diveSpotsRequestMap, ResultListener<DivespotsWrapper> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDivespots(diveSpotsRequestMap);
        call.enqueue(new ResponseEntityCallback<DivespotsWrapper>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<DivespotsWrapper> resultListener, String responseString) {
                DivespotsWrapper divespotsWrapper = new Gson().fromJson(responseString, DivespotsWrapper.class);
                resultListener.onSuccess(divespotsWrapper);
            }
        });
    }

    public void postUserSignUp(String email, String password, String userType, String lat, String lng, ResultListener<SignUpResponseEntity> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().signUpUser(getSignUpRequest(email, password, userType, lat, lng));
        call.enqueue(new ResponseEntityCallback<SignUpResponseEntity>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<SignUpResponseEntity> resultListener, String responseString) throws JSONException {
                SignUpResponseEntity signUpResponseEntity = new Gson().fromJson(responseString, SignUpResponseEntity.class);
                resultListener.onSuccess(signUpResponseEntity);
            }
        });
    }

    public void postUserSignIn(String email, String password, String lat, String lng, SignInType signInType, String token, ResultListener<SignUpResponseEntity> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().loginUser(getSignInRequest(email, password, lat, lng, signInType, token));
        call.enqueue(new ResponseEntityCallback<SignUpResponseEntity>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<SignUpResponseEntity> resultListener, String responseString) throws JSONException {
                SignUpResponseEntity signUpResponseEntity = new Gson().fromJson(responseString, SignUpResponseEntity.class);
                resultListener.onSuccess(signUpResponseEntity);
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

    public void getUserAchievements(String userId, final ResultListener<AchievmentsResponseEntity> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUserAchievements(userId, getUserQueryMapRequest());
        call.enqueue(new ResponseEntityCallback<AchievmentsResponseEntity>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<AchievmentsResponseEntity> resultListener, String responseString) throws JSONException {
                AchievmentsResponseEntity achievmentsResponseEntity = new Gson().fromJson(responseString, AchievmentsResponseEntity.class);
                resultListener.onSuccess(achievmentsResponseEntity);
            }
        });
    }

    private ReportRequest getReportRequest(String reportType, String reportDescription) {
        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setType(reportType);
        reportRequest.setDescription(reportDescription);
        if (!SharedPreferenceHelper.getToken().isEmpty()) {
            reportRequest.setSocial(SharedPreferenceHelper.getSn());
            reportRequest.setToken(SharedPreferenceHelper.getToken());
        }
        return reportRequest;
    }

    private ReportRequest getReportRequest(String reportType, String reportDescription, String imageName) {
        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setType(reportType);
        reportRequest.setDescription(reportDescription);
        reportRequest.setName(imageName);
        if (!SharedPreferenceHelper.getToken().isEmpty()) {
            reportRequest.setSocial(SharedPreferenceHelper.getSn());
            reportRequest.setToken(SharedPreferenceHelper.getToken());
        }
        return reportRequest;
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
        if (SharedPreferenceHelper.isUserLoggedIn()) {
            map.put("social", SharedPreferenceHelper.getSn());
            map.put("token", SharedPreferenceHelper.getToken());
            if (SharedPreferenceHelper.getSn().equals("tw")) {
                map.put("secret", SharedPreferenceHelper.getSecret());
            }
        } else {
            return new HashMap<>();
        }
        return map;
    }

    private RegisterRequest getRegisterRequest() {
        RegisterRequest registerRequest = new RegisterRequest();
        if (!SharedPreferenceHelper.isUserLoggedIn()) {
            registerRequest.setAppId(FirebaseInstanceId.getInstance().getId());
            registerRequest.setpush(FirebaseInstanceId.getInstance().getToken());
            return registerRequest;
        }

        registerRequest.setSocial(SharedPreferenceHelper.getSn());
        registerRequest.setToken(SharedPreferenceHelper.getToken());
        if (SharedPreferenceHelper.getSn().equals("tw")) {
            registerRequest.setSecret(SharedPreferenceHelper.getSecret());
        }
        registerRequest.setAppId(FirebaseInstanceId.getInstance().getId());
        registerRequest.setpush(FirebaseInstanceId.getInstance().getToken());
        return registerRequest;
    }

    private IdentifyRequest getUserIdentifyData(String lat, String lng) {
        IdentifyRequest identifyRequest = new IdentifyRequest();
        identifyRequest.setAppId(FirebaseInstanceId.getInstance().getId());
        if (SharedPreferenceHelper.isUserLoggedIn()) {
            identifyRequest.setSocial(SharedPreferenceHelper.getSn());
            identifyRequest.setToken(SharedPreferenceHelper.getToken());
            if (SharedPreferenceHelper.getSn().equals("tw")) {
                identifyRequest.setSecret(SharedPreferenceHelper.getSecret());
            }
        }
        identifyRequest.setpush(FirebaseInstanceId.getInstance().getToken());
        if (lat != null && lng != null) {
            identifyRequest.setLat(lat);
            identifyRequest.setLng(lng);
        }
        identifyRequest.setType("android");
        return identifyRequest;
    }

    private RegisterRequest generateRegisterRequest(String appId, String socialNetworkName, String token) {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setAppId(appId);
        registerRequest.setSocial(socialNetworkName);
        registerRequest.setToken(token);
        return registerRequest;
    }

    private SignUpRequest getSignUpRequest(String email, String password, String userType, String lat, String lng) {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail(email);
        signUpRequest.setPassword(password);
        signUpRequest.setUser_type(Helpers.getUserType(userType));
        signUpRequest.setPush(FirebaseInstanceId.getInstance().getToken());
        signUpRequest.setApp_id(FirebaseInstanceId.getInstance().getId());
        signUpRequest.setLat(lat);
        signUpRequest.setLng(lng);
        signUpRequest.setDevice_type(2);
        return signUpRequest;
    }

    private SignInRequest getSignInRequest(String email, String password, String lat, String lng, SignInType signInType, String token) {
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setDevice_type(2);
        signInRequest.setEmail(email);
        signInRequest.setPassword(password);
        if (signInType != null) {
            switch (signInType) {
                case GOOGLE:
                    signInRequest.setProvider_type(2);
                    break;
                case FACEBOOK:
                    signInRequest.setProvider_type(1);
                    break;
                default:
                    signInRequest.setProvider_type(null);
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

    public interface ResultListener<T> {
        void onSuccess(T result);

        void onConnectionFailure();

        void onError(ErrorType errorType, Object errorData, String url, String errorMessage);
    }

    public enum ErrorType {
        BAD_REQUEST_ERROR_400, RIGHTS_NOT_FOUND_403, USER_NOT_FOUND_ERROR_C801, DIVE_SPOT_NOT_FOUND_ERROR_C802, COMMENT_NOT_FOUND_ERROR_C803, UNPROCESSABLE_ENTITY_ERROR_422, SERVER_INTERNAL_ERROR_500, IO_ERROR, JSON_SYNTAX_EXCEPTION, UNKNOWN_ERROR
    }
}
