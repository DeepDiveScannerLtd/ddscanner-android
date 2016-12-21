package com.ddscanner.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;

import java.lang.ref.WeakReference;

public class DialogHelpers {

    private DialogHelpers() {

    }

    public static void showDialogAfterChanging(int titleResId, int messageResId, final Activity incomingActivity, Context incomingContext) {
        final WeakReference<Activity> activity = new WeakReference<>(incomingActivity);
        WeakReference<Context> context = new WeakReference<>(incomingContext);
        MaterialDialog.Builder dialog =new MaterialDialog.Builder(context.get());
        dialog.title(DDScannerApplication.getInstance().getString(titleResId))
                .content(DDScannerApplication.getInstance().getString(messageResId))
                .positiveText(R.string.cencel_dialog)
                .positiveColor(ContextCompat.getColor(context.get(), R.color.primary))
                .negativeColor(ContextCompat.getColor(context.get(), R.color.primary))
                .negativeText(R.string.leave)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog,
                                        @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog,
                                        @NonNull DialogAction which) {
                        activity.get().finish();
                    }
                });
        dialog.show();
    }

    public static void showDialogForEnableLocationProviders(final Activity incomingActivity) {
        final WeakReference<Activity> activity = new WeakReference<Activity>(incomingActivity);
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(activity.get());
        dialog.title(DDScannerApplication.getInstance().getString(R.string.can_not_locate))
                .content(DDScannerApplication.getInstance().getString(R.string.turn_on_gps))
                .positiveText("ok")
                .negativeText("cancel")
                .positiveColor(ContextCompat.getColor(activity.get(), R.color.primary))
                .negativeColor(ContextCompat.getColor(activity.get(), R.color.primary))
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        activity.get().startActivityForResult(intent, ActivitiesRequestCodes.REQUEST_CODE_LOCATION_PROVIDERS_NOT_AVAILABLE_ACTIVITY_TURN_ON_LOCATION_SETTINGS);
                    }
                });
        dialog.show();
    }

}
