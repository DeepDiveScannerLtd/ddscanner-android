package com.ddscanner.utils;

import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.ui.dialogs.ConfirmationDialogFragment;

import java.lang.ref.WeakReference;

public class DialogHelpers {

    public DialogHelpers() {

    }

    public static void showDialogForEnableLocationProviders(final Activity incomingActivity) {
        final WeakReference<Activity> activity = new WeakReference<>(incomingActivity);
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(activity.get());
        dialog.title(DDScannerApplication.getInstance().getString(R.string.can_not_locate))
                .content(DDScannerApplication.getInstance().getString(R.string.turn_on_gps))
                .positiveText("ok")
                .negativeText("cancel")
                .positiveColor(ContextCompat.getColor(activity.get(), R.color.primary))
                .negativeColor(ContextCompat.getColor(activity.get(), R.color.primary))
                .onNegative((dialog1, which) -> dialog1.dismiss())
                .onPositive((dialog12, which) -> {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    activity.get().startActivityForResult(intent, ActivitiesRequestCodes.REQUEST_CODE_LOCATION_PROVIDERS_NOT_AVAILABLE_ACTIVITY_TURN_ON_LOCATION_SETTINGS);
                });
        dialog.show();
    }

    public static void showDialogAfterChangesInActivity(FragmentManager fragmentManager) {
        ConfirmationDialogFragment.showForActivity(fragmentManager, R.string.empty_string, R.string.exit_without_saving, R.string.yes, R.string.cancel);
    }

    public static void showInstructorConfirmationDialog(FragmentManager fragmentManager) {
        ConfirmationDialogFragment.showForActivity(fragmentManager, R.string.empty_string, R.string.are_you_an_instructor, R.string.yes, R.string.no);
    }

    public void showNegativeApproveDialog(FragmentManager fragmentManager) {
        ConfirmationDialogFragment.showForActivity(fragmentManager, R.string.empty_string, R.string.what_to_do_with_dive_spot, R.string.dialog_remove_spot, R.string.edit_spot_dialog);
    }

}
