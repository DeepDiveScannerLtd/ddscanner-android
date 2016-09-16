package com.ddscanner.rest;

import android.support.annotation.NonNull;

import com.ddscanner.entities.DiveSpotDetails;
import com.ddscanner.entities.errors.Field;
import com.ddscanner.entities.errors.GeneralError;
import com.ddscanner.entities.errors.ValidationError;
import com.ddscanner.utils.LogUtils;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class DDScannerRestClient {

    private Gson gson = new Gson();

    public void requestDiveSpotDetails(String diveSpotId, @NonNull final ResultListener<DiveSpotDetails> resultListener) {
        Map<String, String> map = getUserQueryMapRequest();
        map.put("isImageAuthor", "true");
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveSpotById(diveSpotId, map);
        call.enqueue(new BaseCallback(resultListener) {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                String responseString = "";
                try {
                    responseString = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                    resultListener.onError(ErrorType.IO_ERROR, null);
                    return;
                }
                LogUtils.i("response body is " + responseString);
                if (response.isSuccessful()) {
                    DiveSpotDetails diveSpotDetails = new Gson().fromJson(responseString, DiveSpotDetails.class);
                    resultListener.onSuccess(diveSpotDetails);
                } else {
                    checkForError(response.code(), responseString, resultListener);
                }
            }
        });
    }

    public void checkForError(int responseCode, String json, ResultListener resultListener) {
        GeneralError generalError;
        ValidationError validationError;
        switch (responseCode) {
            case 400:
                // bad request. for example event already happened or event preconditions are not held
                generalError = gson.fromJson(json, GeneralError.class);
                resultListener.onError(ErrorType.BAD_REQUEST_ERROR, generalError);
                break;
            case 404:
                // entity not found
                generalError = gson.fromJson(json, GeneralError.class);
                switch (generalError.getStatusCode()) {
                    case 801:
                        // user not found
                        resultListener.onError(ErrorType.USER_NOT_FOUND_ERROR, generalError);
                        break;
                    case 802:
                        // dive spot not found
                        resultListener.onError(ErrorType.DIVE_SPOT_NOT_FOUND_ERROR, generalError);
                        break;
                    case 803:
                        // dive spot comment not found
                        resultListener.onError(ErrorType.COMMENT_NOT_FOUND_ERROR, generalError);
                        break;
                    default:
                        resultListener.onError(ErrorType.NOT_FOUND_ERROR, generalError);
                        break;
                }
                break;
            case 422:
                // validation error
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
                resultListener.onError(ErrorType.VALIDATION_ERROR, validationError);
                break;
            case 500:
                // unknown server error
                generalError = gson.fromJson(json, GeneralError.class);
                resultListener.onError(ErrorType.SERVER_INTERNAL_ERROR, generalError);
                break;
            default:
                // If unexpected error code is received
                generalError = gson.fromJson(json, GeneralError.class);
                resultListener.onError(ErrorType.UNKNOWN_ERROR, generalError);
                break;
        }
    }

    private abstract class BaseCallback implements Callback<ResponseBody> {
        private ResultListener<DiveSpotDetails> resultListener;

        public BaseCallback(ResultListener<DiveSpotDetails> resultListener) {
            this.resultListener = resultListener;
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            if (t instanceof ConnectException) {
                resultListener.onConnectionFailure();
            } else {
                resultListener.onError(ErrorType.UNKNOWN_ERROR, null);
            }
        }
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

    public interface ResultListener<T> {
        void onSuccess(T result);
        void onConnectionFailure();
        void onError(ErrorType errorType, Object errorData);
    }

    public enum ErrorType {
        BAD_REQUEST_ERROR, USER_NOT_FOUND_ERROR, DIVE_SPOT_NOT_FOUND_ERROR, COMMENT_NOT_FOUND_ERROR, NOT_FOUND_ERROR, VALIDATION_ERROR,  SERVER_INTERNAL_ERROR, IO_ERROR, UNKNOWN_ERROR
    }
}
