package com.ddscanner.rest;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

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

abstract class ResponseEntityCallback<T> extends BaseCallback<T> {

    private WeakReference<Activity> weakReference;

    ResponseEntityCallback(Gson gson, DDScannerRestClient.ResultListener<T> resultListener, Activity context) {
        super(gson, resultListener);
        weakReference= new WeakReference<Activity>(context);
    }

    @Override
    public void onResponse(DDScannerRestClient.ResultListener<T> resultListener, Call<ResponseBody> call, Response<ResponseBody> response) {
        if (response.isSuccessful()) {
            String responseString;
            JSONObject responseJsonObject;
            try {
                responseString = response.body().string();
            } catch (IOException e) {
                resultListener.onError(DDScannerRestClient.ErrorType.IO_ERROR, null, call.request().url().toString(), e.getMessage());
                return;
            }
            try {
                responseJsonObject = new JSONObject(responseString);
                responseString = responseJsonObject.getString("response");
                try {
                    if (!responseJsonObject.getString("popup").isEmpty()) {
                        ShowPopupLstener showPopupLstener;
                        try {
                            showPopupLstener = (ShowPopupLstener) weakReference.get();
                            showPopupLstener.onPopupMustBeShown(responseJsonObject.getString("popup"));
//                            DDScannerApplication.bus.post(new ShowPopupEvent(responseJsonObject.getString("popup")));
                        } catch (Exception e) {

                        }
                    }
                } catch (Exception e) {

                }
                handleResponseString(resultListener, responseString);
            } catch (JsonSyntaxException | JSONException e) {
                resultListener.onError(DDScannerRestClient.ErrorType.JSON_SYNTAX_EXCEPTION, null, call.request().url().toString(), e.getMessage());
            }
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

    abstract void handleResponseString(DDScannerRestClient.ResultListener<T> resultListener, String responseString) throws JSONException;
}
