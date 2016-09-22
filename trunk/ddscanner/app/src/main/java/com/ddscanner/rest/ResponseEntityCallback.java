package com.ddscanner.rest;

import com.ddscanner.utils.LogUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

abstract class ResponseEntityCallback<T> extends BaseCallback<T> {

    ResponseEntityCallback(Gson gson, DDScannerRestClient.ResultListener<T> resultListener) {
        super(gson, resultListener);
    }

    @Override
    public void onResponse(DDScannerRestClient.ResultListener<T> resultListener, Call<ResponseBody> call, Response<ResponseBody> response) {
        if (response.isSuccessful()) {
            String responseString;
            try {
                responseString = response.body().string();
            } catch (IOException e) {
                resultListener.onError(DDScannerRestClient.ErrorType.IO_ERROR, null, call.request().url().toString(), e.getMessage());
                return;
            }
            LogUtils.i("response body is " + responseString);
            try {
                handleResponseString(resultListener, responseString);
            } catch (JsonSyntaxException e) {
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

    abstract void handleResponseString(DDScannerRestClient.ResultListener<T> resultListener, String responseString);
}
