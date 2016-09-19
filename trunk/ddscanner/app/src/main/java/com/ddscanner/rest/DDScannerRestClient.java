package com.ddscanner.rest;

import android.support.annotation.NonNull;

import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.entities.DiveSpotDetails;
import com.ddscanner.entities.errors.BadRequestException;
import com.ddscanner.entities.errors.CommentNotFoundException;
import com.ddscanner.entities.errors.DiveSpotNotFoundException;
import com.ddscanner.entities.errors.Field;
import com.ddscanner.entities.errors.GeneralError;
import com.ddscanner.entities.errors.NotFoundException;
import com.ddscanner.entities.errors.ServerInternalErrorException;
import com.ddscanner.entities.errors.UnknownErrorException;
import com.ddscanner.entities.errors.UserNotFoundException;
import com.ddscanner.entities.errors.ValidationError;
import com.ddscanner.entities.errors.ValidationErrorException;
import com.ddscanner.utils.DialogUtils;
import com.ddscanner.utils.Helpers;
import com.ddscanner.utils.LogUtils;
import com.ddscanner.utils.SharedPreferenceHelper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
        final WeakReference<ResultListener<DiveSpotDetails>> resultListenerWeakReference = new WeakReference<ResultListener<DiveSpotDetails>>(resultListener);
        Map<String, String> map = getUserQueryMapRequest();
        map.put("isImageAuthor", "true");
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveSpotById(diveSpotId, map);
        call.enqueue(new BaseCallback(resultListenerWeakReference) {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                String responseString = "";
                try {
                    responseString = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                    if (resultListenerWeakReference.get() != null) {
                        resultListenerWeakReference.get().onError(ErrorType.IO_ERROR, null);
                    }
                    return;
                }
                LogUtils.i("response body is " + responseString);
                if (response.isSuccessful()) {
                    DiveSpotDetails diveSpotDetails = new Gson().fromJson(responseString, DiveSpotDetails.class);
                    if (resultListenerWeakReference.get() != null) {
                        resultListenerWeakReference.get().onSuccess(diveSpotDetails);
                    }
                } else {
                    checkForError(response.code(), responseString, resultListenerWeakReference);
                }
            }
        });
    }

//    private void postCheckIn(String diveSpotId) {
//        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().checkIn(diveSpotId,
//                Helpers.getRegisterRequest()
//        );
//        call.enqueue(new com.ddscanner.rest.BaseCallback() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful()) {
//                    getCheckins();
//                    EventsTracker.trackCheckIn(EventsTracker.CheckInStatus.SUCCESS);
//                } else {
//                    checkoutUi();
//                    String responseString = "";
//                    try {
//                        responseString = response.errorBody().string();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    LogUtils.i("response body is " + responseString);
//                    try {
//                        ErrorsParser.checkForError(response.code(), responseString);
//                    } catch (ServerInternalErrorException e) {
//                        // TODO Handle
//                        Helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
//                    } catch (BadRequestException e) {
//                        // TODO Handle
//                        Helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
//                    } catch (ValidationErrorException e) {
//                        // TODO Handle
//
//                    } catch (NotFoundException e) {
//                        // TODO Handle
//                        Helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
//                    } catch (UnknownErrorException e) {
//                        // TODO Handle
//                        Helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
//                    } catch (DiveSpotNotFoundException e) {
//                        // TODO Handle
//                        Helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
//                    } catch (UserNotFoundException e) {
//                        // TODO Handle
//                        isClickedCHeckin = true;
//                        showLoginActivity();
//                    } catch (CommentNotFoundException e) {
//                        // TODO Handle
//                        Helpers.showToast(DiveSpotDetailsActivity.this, R.string.toast_server_error);
//                    }
//                }
//            }
//
//            @Override
//            public void onConnectionFailure() {
//                DialogUtils.showConnectionErrorDialog(DiveSpotDetailsActivity.this);
//            }
//        });
//    }

    public void checkForError(int responseCode, String json, WeakReference<ResultListener<DiveSpotDetails>> resultListenerWeakReference) {
        GeneralError generalError;
        ValidationError validationError;
        switch (responseCode) {
            case 400:
                // bad request. for example event already happened or event preconditions are not held
                generalError = gson.fromJson(json, GeneralError.class);
                if (resultListenerWeakReference.get() != null) {
                    resultListenerWeakReference.get().onError(ErrorType.BAD_REQUEST_ERROR_400, generalError);
                }
                break;
            case 404:
                // entity not found
                generalError = gson.fromJson(json, GeneralError.class);
                switch (generalError.getStatusCode()) {
                    case 801:
                        // user not found
                        if (resultListenerWeakReference.get() != null) {
                            resultListenerWeakReference.get().onError(ErrorType.USER_NOT_FOUND_ERROR_C801, generalError);
                        }
                        break;
                    case 802:
                        // dive spot not found
                        if (resultListenerWeakReference.get() != null) {
                            resultListenerWeakReference.get().onError(ErrorType.DIVE_SPOT_NOT_FOUND_ERROR_C802, generalError);
                        }
                        break;
                    case 803:
                        // dive spot comment not found
                        if (resultListenerWeakReference.get() != null) {
                            resultListenerWeakReference.get().onError(ErrorType.COMMENT_NOT_FOUND_ERROR_C803, generalError);
                        }
                        break;
                    default:
                        if (resultListenerWeakReference.get() != null) {
                            resultListenerWeakReference.get().onError(ErrorType.NOT_FOUND_ERROR_404, generalError);
                        }
                        break;
                }
                break;
            case 422:
                // unprocessable entity error, aka validation error
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
                if (resultListenerWeakReference.get() != null) {
                    resultListenerWeakReference.get().onError(ErrorType.UNPROCESSABLE_ENTITY_ERROR_422, validationError);
                }
                break;
            case 500:
                // unknown server error
                generalError = gson.fromJson(json, GeneralError.class);
                if (resultListenerWeakReference.get() != null) {
                    resultListenerWeakReference.get().onError(ErrorType.SERVER_INTERNAL_ERROR_500, generalError);
                }
                break;
            default:
                // If unexpected error code is received
                generalError = gson.fromJson(json, GeneralError.class);
                if (resultListenerWeakReference.get() != null) {
                    resultListenerWeakReference.get().onError(ErrorType.UNKNOWN_ERROR, generalError);
                }
                break;
        }
    }

    private abstract class BaseCallback implements Callback<ResponseBody> {
        private WeakReference<ResultListener<DiveSpotDetails>> resultListenerWeakReference;

        public BaseCallback(WeakReference<ResultListener<DiveSpotDetails>> resultListenerWeakReference) {
            this.resultListenerWeakReference = resultListenerWeakReference;
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            if (t instanceof ConnectException) {
                if (resultListenerWeakReference.get() != null) {
                    resultListenerWeakReference.get().onConnectionFailure();
                }
            } else {
                if (resultListenerWeakReference.get() != null) {
                    resultListenerWeakReference.get().onError(ErrorType.UNKNOWN_ERROR, null);
                }
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
        BAD_REQUEST_ERROR_400, USER_NOT_FOUND_ERROR_C801, DIVE_SPOT_NOT_FOUND_ERROR_C802, COMMENT_NOT_FOUND_ERROR_C803, NOT_FOUND_ERROR_404, UNPROCESSABLE_ENTITY_ERROR_422, SERVER_INTERNAL_ERROR_500, IO_ERROR, UNKNOWN_ERROR
    }
}
