package com.ddscanner.rest;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.events.UnknownErrorCatchedEvent;
import com.ddscanner.ui.activities.DiveSpotDetailsActivity;

import java.net.ConnectException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public abstract class BaseCallback implements Callback<ResponseBody> {

    private static final String TAG = BaseCallback.class.getName();

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        if (t instanceof ConnectException) {
            onConnectionFailure();
        } else {
            Log.i(TAG, call.request().url().toString() + ": " + t.getMessage());
            EventsTracker.trackUnknownServerError(call.request().url().toString(), t.getMessage());
            Toast.makeText(DDScannerApplication.getInstance(), DDScannerApplication.getInstance().getText(R.string.unknown_error), Toast.LENGTH_LONG).show();
        }
    }

    public abstract void onConnectionFailure();

}
