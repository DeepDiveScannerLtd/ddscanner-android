package com.ddscanner.rest;

import android.support.annotation.NonNull;

import com.ddscanner.entities.CheckIns;
import com.ddscanner.entities.Comments;
import com.ddscanner.entities.DiveSpotDetails;
import com.ddscanner.entities.DivespotsWrapper;
import com.ddscanner.entities.FiltersResponseEntity;
import com.ddscanner.entities.ForeignUserDislikesWrapper;
import com.ddscanner.entities.ForeignUserLikeWrapper;
import com.ddscanner.entities.RegisterResponse;
import com.ddscanner.entities.SignInType;
import com.ddscanner.entities.request.IdentifyRequest;
import com.ddscanner.entities.request.RegisterRequest;
import com.ddscanner.entities.request.ReportRequest;
import com.ddscanner.entities.request.ValidationRequest;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

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

    public void postValidateDiveSpot(String diveSpotId, boolean isValid, @NonNull final ResultListener<Void> resultListener) {
        ValidationRequest validationRequest = new ValidationRequest();
        validationRequest.setSocial(SharedPreferenceHelper.getSn());
        validationRequest.setToken(SharedPreferenceHelper.getToken());
        validationRequest.setAppId(SharedPreferenceHelper.getUserAppId());
        validationRequest.setpush(SharedPreferenceHelper.getGcmId());
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
        final Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().reportComment(commentId, getReportRequest(reportType,  reportDescription));
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

    public void postRegisterUser(String appId, SignInType signInType, String token, @NonNull final ResultListener<RegisterResponse> resultListener) {
        final Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().registerUser(generateRegisterRequest(appId, signInType.getName(), token));
        call.enqueue(new ResponseEntityCallback<RegisterResponse>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<RegisterResponse> resultListener, String responseString) {
                RegisterResponse registerResponse = new Gson().fromJson(responseString, RegisterResponse.class);
                resultListener.onSuccess(registerResponse);
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
            registerRequest.setAppId(SharedPreferenceHelper.getUserAppId());
            registerRequest.setpush(SharedPreferenceHelper.getGcmId());
            return registerRequest;
        }

        registerRequest.setSocial(SharedPreferenceHelper.getSn());
        registerRequest.setToken(SharedPreferenceHelper.getToken());
        if (SharedPreferenceHelper.getSn().equals("tw")) {
            registerRequest.setSecret(SharedPreferenceHelper.getSecret());
        }
        registerRequest.setAppId(SharedPreferenceHelper.getUserAppId());
        registerRequest.setpush(SharedPreferenceHelper.getGcmId());
        return registerRequest;
    }

    private IdentifyRequest getUserIdentifyData(String lat, String lng) {
        IdentifyRequest identifyRequest = new IdentifyRequest();
        identifyRequest.setAppId(SharedPreferenceHelper.getUserAppId());
        if (SharedPreferenceHelper.isUserLoggedIn()) {
            identifyRequest.setSocial(SharedPreferenceHelper.getSn());
            identifyRequest.setToken(SharedPreferenceHelper.getToken());
            if (SharedPreferenceHelper.getSn().equals("tw")) {
                identifyRequest.setSecret(SharedPreferenceHelper.getSecret());
            }
        }
        identifyRequest.setpush(SharedPreferenceHelper.getGcmId());
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

    public interface ResultListener<T> {
        void onSuccess(T result);

        void onConnectionFailure();

        void onError(ErrorType errorType, Object errorData, String url, String errorMessage);
    }

    public enum ErrorType {
        BAD_REQUEST_ERROR_400, RIGHTS_NOT_FOUND_403, USER_NOT_FOUND_ERROR_C801, DIVE_SPOT_NOT_FOUND_ERROR_C802, COMMENT_NOT_FOUND_ERROR_C803, UNPROCESSABLE_ENTITY_ERROR_422, SERVER_INTERNAL_ERROR_500, IO_ERROR, JSON_SYNTAX_EXCEPTION, UNKNOWN_ERROR
    }
}
