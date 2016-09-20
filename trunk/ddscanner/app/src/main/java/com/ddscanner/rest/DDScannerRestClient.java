package com.ddscanner.rest;

import android.support.annotation.NonNull;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.DiveSpotDetails;
import com.ddscanner.entities.errors.Field;
import com.ddscanner.entities.errors.GeneralError;
import com.ddscanner.entities.errors.ValidationError;
import com.ddscanner.entities.request.RegisterRequest;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.LogUtils;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DDScannerRestClient {

    private Gson gson = new Gson();

    public void getDiveSpotDetails(String diveSpotId, @NonNull final ResultListener<DiveSpotDetails> resultListener) {
        Map<String, String> map = getUserQueryMapRequest();
        map.put("isImageAuthor", "true");
        final Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveSpotById(diveSpotId, map);
        call.enqueue(new ResponseEntityCallback<DiveSpotDetails>(resultListener) {
            @Override
            void handleResponseString(String responseString) {
                DiveSpotDetails diveSpotDetails = new Gson().fromJson(responseString, DiveSpotDetails.class);
                resultListenerWeakReference.get().onSuccess(diveSpotDetails);
            }
        });
    }

    public void postCheckIn(String diveSpotId, @NonNull final ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().checkIn(diveSpotId, getRegisterRequest());
        call.enqueue(new NoResponseEntityCallback(resultListener));
    }

    public void postCheckOut(String diveSpotId, @NonNull final ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().checkOutUser(diveSpotId, getUserQueryMapRequest());
        call.enqueue(new NoResponseEntityCallback(resultListener));
    }

    public void postAddDiveSpotToFavourites(String diveSpotId, @NonNull final ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().addDiveSpotToFavourites(diveSpotId, getRegisterRequest());
        call.enqueue(new NoResponseEntityCallback(resultListener));
    }

    public void postRemoveDiveSpotFromFavourites(String diveSpotId, @NonNull final ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().removeSpotFromFavorites(diveSpotId, getUserQueryMapRequest());
        call.enqueue(new NoResponseEntityCallback(resultListener));
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

    private abstract class BaseCallback<T> implements Callback<ResponseBody> {
        WeakReference<ResultListener<T>> resultListenerWeakReference;

        public BaseCallback(ResultListener<T> resultListener) {
            this.resultListenerWeakReference = new WeakReference<>(resultListener);
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            if (t instanceof ConnectException) {
                if (resultListenerWeakReference.get() != null) {
                    resultListenerWeakReference.get().onConnectionFailure();
                }
            } else {
                if (resultListenerWeakReference.get() != null) {
                    resultListenerWeakReference.get().onError(ErrorType.UNKNOWN_ERROR, null, call.request().url().toString(), t.getMessage());
                }
            }
        }

        void checkForError(Call<ResponseBody> call, int responseCode, String json, ResultListener resultListener) {
            GeneralError generalError;
            ValidationError validationError;
            switch (responseCode) {
                case 400:
                    // bad request. for example event already happened or event preconditions are not held
                    try {
                        generalError = gson.fromJson(json, GeneralError.class);
                        if (resultListener != null) {
                            resultListener.onError(ErrorType.BAD_REQUEST_ERROR_400, generalError, call.request().url().toString(), generalError.getMessage());
                        }
                    } catch (JsonSyntaxException e) {
                        if (resultListener != null) {
                            resultListener.onError(ErrorType.JSON_SYNTAX_EXCEPTION, null, call.request().url().toString(), e.getMessage());
                        }
                    }
                    break;
                case 404:
                    // entity not found
                    try {
                        generalError = gson.fromJson(json, GeneralError.class);
                        if (resultListener != null) {
                            switch (generalError.getStatusCode()) {
                                case 801:
                                    // user not found
                                    resultListener.onError(ErrorType.USER_NOT_FOUND_ERROR_C801, generalError, call.request().url().toString(), generalError.getMessage());
                                    break;
                                case 802:
                                    // dive spot not found
                                    resultListener.onError(ErrorType.DIVE_SPOT_NOT_FOUND_ERROR_C802, generalError, call.request().url().toString(), generalError.getMessage());
                                    break;
                                case 803:
                                    // dive spot comment not found
                                    resultListener.onError(ErrorType.COMMENT_NOT_FOUND_ERROR_C803, generalError, call.request().url().toString(), generalError.getMessage());
                                    break;
                                default:
                                    // There is no error that comes with 404 http response code and statusCode not in [801, 802, 803] in API doc. So handle this case as an unknown error
                                    resultListener.onError(ErrorType.UNKNOWN_ERROR, generalError, call.request().url().toString(), generalError.getMessage());
                                    break;
                            }
                        }
                    } catch (JsonSyntaxException e) {
                        if (resultListener != null) {
                            resultListener.onError(ErrorType.JSON_SYNTAX_EXCEPTION, null, call.request().url().toString(), e.getMessage());
                        }
                    }
                    break;
                case 422:
                    // unprocessable entity error, aka validation error
                    try {
                        if (resultListener != null) {
                            validationError = new ValidationError();
                            JsonElement jsonElement = new JsonParser().parse(json);
                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                            Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
                            Field field;
                            for (Map.Entry<String, JsonElement> entry : entrySet) {
                                field = new Field();
                                field.setName(entry.getKey());
                                JsonArray jsonArray = entry.getValue().getAsJsonArray();
                                for (int i = 0; i < jsonArray.size(); i++) {
                                    field.addError(jsonArray.get(i).getAsString());
                                }
                                validationError.addField(field);
                            }
                            resultListener.onError(ErrorType.UNPROCESSABLE_ENTITY_ERROR_422, validationError, call.request().url().toString(), json);
                        }
                    } catch (JsonSyntaxException e) {
                        resultListener.onError(ErrorType.JSON_SYNTAX_EXCEPTION, null, call.request().url().toString(), e.getMessage());
                    }
                    break;
                case 500:
                    // unknown server error
                    try {
                        generalError = gson.fromJson(json, GeneralError.class);
                        if (resultListener != null) {
                            resultListener.onError(ErrorType.SERVER_INTERNAL_ERROR_500, generalError, call.request().url().toString(), generalError.getMessage());
                        }
                    } catch (JsonSyntaxException e) {
                        if (resultListener != null) {
                            resultListener.onError(ErrorType.JSON_SYNTAX_EXCEPTION, null, call.request().url().toString(), e.getMessage());
                        }
                    }
                default:
                    // If unexpected error code is received
                    try {
                        generalError = gson.fromJson(json, GeneralError.class);
                        if (resultListener != null) {
                            resultListener.onError(ErrorType.UNKNOWN_ERROR, null, call.request().url().toString(), generalError.getMessage());
                        }
                    } catch (JsonSyntaxException e) {
                        if (resultListener != null) {
                            resultListener.onError(ErrorType.JSON_SYNTAX_EXCEPTION, null, call.request().url().toString(), e.getMessage());
                        }
                    }
                    break;
            }
        }
    }

    private class NoResponseEntityCallback extends BaseCallback<Void> {

        NoResponseEntityCallback(ResultListener<Void> resultListener) {
            super(resultListener);
        }

        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            if (response.isSuccessful()) {
                if (resultListenerWeakReference.get() != null) {
                    resultListenerWeakReference.get().onSuccess(null);
                }
            } else {
                String responseString;
                try {
                    responseString = response.errorBody().string();
                } catch (IOException e) {
                    if (resultListenerWeakReference.get() != null) {
                        resultListenerWeakReference.get().onError(ErrorType.IO_ERROR, null, call.request().url().toString(), e.getMessage());
                    }
                    return;
                }
                LogUtils.i("response body is " + responseString);
                checkForError(call, response.code(), responseString, resultListenerWeakReference.get());
            }
        }
    }

    private abstract class ResponseEntityCallback<T> extends BaseCallback<T> {

        ResponseEntityCallback(ResultListener<T> resultListener) {
            super(resultListener);
        }

        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            String responseString;
            try {
                responseString = response.body().string();
            } catch (IOException e) {
                if (resultListenerWeakReference.get() != null) {
                    resultListenerWeakReference.get().onError(ErrorType.IO_ERROR, null, call.request().url().toString(), e.getMessage());
                }
                return;
            }
            LogUtils.i("response body is " + responseString);
            if (response.isSuccessful()) {
                try {
                    if (resultListenerWeakReference.get() != null) {
                        handleResponseString(responseString);
                    }
                } catch (JsonSyntaxException e) {
                    if (resultListenerWeakReference.get() != null) {
                        resultListenerWeakReference.get().onError(ErrorType.JSON_SYNTAX_EXCEPTION, null, call.request().url().toString(), e.getMessage());
                    }
                }
            } else {
                checkForError(call, response.code(), responseString, resultListenerWeakReference.get());
            }
        }

        abstract void handleResponseString(String responseString);
    }

    public enum ErrorType {
        BAD_REQUEST_ERROR_400, USER_NOT_FOUND_ERROR_C801, DIVE_SPOT_NOT_FOUND_ERROR_C802, COMMENT_NOT_FOUND_ERROR_C803, UNPROCESSABLE_ENTITY_ERROR_422, SERVER_INTERNAL_ERROR_500, IO_ERROR, JSON_SYNTAX_EXCEPTION, UNKNOWN_ERROR
    }
}
