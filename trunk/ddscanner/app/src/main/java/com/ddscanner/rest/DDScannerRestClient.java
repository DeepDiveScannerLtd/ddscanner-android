package com.ddscanner.rest;

import android.support.annotation.NonNull;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.DiveSpotDetails;
import com.ddscanner.entities.request.RegisterRequest;
import com.ddscanner.utils.Helpers;
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
        final Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveSpotById(diveSpotId, map);
        call.enqueue(new ResponseEntityCallback<DiveSpotDetails>(gson, resultListener) {
            @Override
            void handleResponseString(String responseString) {
                DiveSpotDetails diveSpotDetails = new Gson().fromJson(responseString, DiveSpotDetails.class);
                resultListenerWeakReference.get().onSuccess(diveSpotDetails);
            }
        });
    }

    public void postCheckIn(String diveSpotId, @NonNull final ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().checkIn(diveSpotId, getRegisterRequest());
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void postCheckOut(String diveSpotId, @NonNull final ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().checkOutUser(diveSpotId, getUserQueryMapRequest());
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void postAddDiveSpotToFavourites(String diveSpotId, @NonNull final ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().addDiveSpotToFavourites(diveSpotId, getRegisterRequest());
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void postRemoveDiveSpotFromFavourites(String diveSpotId, @NonNull final ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().removeSpotFromFavorites(diveSpotId, getUserQueryMapRequest());
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
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

    public abstract static class ResultListener<T> {
        public abstract void onSuccess(T result);

        public abstract void onConnectionFailure();

        public abstract void onError(ErrorType errorType, Object errorData, String url, String errorMessage);

        protected void handleUnexpectedError(String url, String message) {
            // TODO May be should use another tracking mechanism
            EventsTracker.trackUnknownServerError(url, message);
            Helpers.showToast(DDScannerApplication.getInstance(), R.string.toast_server_error);
        }
    }

    public enum ErrorType {
        BAD_REQUEST_ERROR_400, USER_NOT_FOUND_ERROR_C801, DIVE_SPOT_NOT_FOUND_ERROR_C802, COMMENT_NOT_FOUND_ERROR_C803, UNPROCESSABLE_ENTITY_ERROR_422, SERVER_INTERNAL_ERROR_500, IO_ERROR, JSON_SYNTAX_EXCEPTION, UNKNOWN_ERROR
    }
}
