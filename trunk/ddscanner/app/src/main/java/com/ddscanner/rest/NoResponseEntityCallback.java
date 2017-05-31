package com.ddscanner.rest;

import android.app.Activity;

import com.ddscanner.interfaces.ShowPopupLstener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

class NoResponseEntityCallback extends BaseCallback<Void> {

    private WeakReference<Activity> weakReference;

    NoResponseEntityCallback(Gson gson, DDScannerRestClient.ResultListener<Void> resultListener, Activity context) {
        super(gson, resultListener);
        weakReference= new WeakReference<Activity>(context);
    }

    @Override
    public void onResponse(DDScannerRestClient.ResultListener<Void> resultListener, Call<ResponseBody> call, Response<ResponseBody> response) {
        if (response.isSuccessful()) {
            String responseString;
            JSONObject responseJsonObject;
            try {
                responseString = response.body().string();
            } catch (IOException e) {
                return;
            }
            try {
                responseJsonObject = new JSONObject(responseString);
                if (responseJsonObject.getString("popup") != null) {
                    try {
                        ShowPopupLstener showPopupLstener;
                        showPopupLstener = (ShowPopupLstener) weakReference.get();
                        showPopupLstener.onPopupMustBeShown(responseJsonObject.getString("popup"));
//                        DDScannerApplication.bus.post(new ShowPopupEvent(responseJsonObject.getString("popup")));
                    } catch (Exception e) {

                    }
                }
            } catch (JsonSyntaxException | JSONException e) {
//                resultListener.onError(DDScannerRestClient.ErrorType.JSON_SYNTAX_EXCEPTION, null, call.request().url().toString(), e.getMessage());
            }
            resultListener.onSuccess(null);
        } else {
            String responseString;
            try {
                responseString = response.errorBody().string();
            } catch (IOException e) {
                resultListener.onError(DDScannerRestClient.ErrorType.IO_ERROR, null, call.request().url().toString(), e.getMessage());
                return;
            }
            checkForError(call, response.code(), responseString, resultListener);
        }
    }
}
