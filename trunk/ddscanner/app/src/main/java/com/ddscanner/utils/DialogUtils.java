package com.ddscanner.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.ui.activities.DiveSpotDetailsActivity;

public class DialogUtils {

    private DialogUtils() {

    }

    public static void showConnectionErrorDialog(Context context) {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(context)
                .content(R.string.error_connection_failed)
                .neutralText(R.string.ok)
                .neutralColor(context.getResources().getColor(R.color.primary))
                .cancelable(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog,
                                        @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                });

        dialog.show();
    }
}
