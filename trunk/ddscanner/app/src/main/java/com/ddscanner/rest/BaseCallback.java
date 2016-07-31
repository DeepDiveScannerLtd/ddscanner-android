package com.ddscanner.rest;

import android.util.Log;
import android.widget.Toast;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public abstract class BaseCallback implements Callback<ResponseBody> {

    private static final String TAG = BaseCallback.class.getName();

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        Log.i(TAG, call.request().url().toString() + ": " + t.getMessage());
        EventsTracker.trackUnknownServerError(call.request().url().toString(), t.getMessage());
        Toast.makeText(DDScannerApplication.getInstance(), DDScannerApplication.getInstance().getText(R.string.unknown_error), Toast.LENGTH_LONG).show();
    }

}
