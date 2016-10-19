package com.ddscanner.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.ddscanner.R;

public class AchievementAchievedDialogFragment extends DialogFragment {

    public AchievementAchievedDialogFragment() {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_achievemnt_achieved, null);
        builder.setView(view);
        builder.setTitle(null);

        return builder.create();
    }
}
