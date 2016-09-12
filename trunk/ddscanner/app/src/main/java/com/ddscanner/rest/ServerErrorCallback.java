package com.ddscanner.rest;

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

public abstract class ServerErrorCallback implements Callback<ResponseBody> {


    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        if (t instanceof ConnectException) {
            onConnectionFailure();
        } else {
            DDScannerApplication.bus.post(new UnknownErrorCatchedEvent());
        }
    }

    public abstract void onConnectionFailure();
}
