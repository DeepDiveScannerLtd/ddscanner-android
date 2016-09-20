package com.ddscanner.rest;

import com.ddscanner.utils.LogUtils;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

class NoResponseEntityCallback extends BaseCallback<Void> {

    NoResponseEntityCallback(Gson gson, DDScannerRestClient.ResultListener<Void> resultListener) {
        super(gson, resultListener);
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
                    resultListenerWeakReference.get().onError(DDScannerRestClient.ErrorType.IO_ERROR, null, call.request().url().toString(), e.getMessage());
                }
                return;
            }
            LogUtils.i("response body is " + responseString);
            checkForError(call, response.code(), responseString, resultListenerWeakReference.get());
        }
    }
}
