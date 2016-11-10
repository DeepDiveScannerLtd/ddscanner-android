package com.ddscanner.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;

import com.ddscanner.R;

public class CheckedInDialogFragment extends DialogFragment {

    public CheckedInDialogFragment() {

    }

    public static CheckedInDialogFragment newInstance(String diveSpotId) {
        CheckedInDialogFragment checkedInDialogFragment = new CheckedInDialogFragment();
        Bundle args = new Bundle();
        args.putString("divespotid", diveSpotId);
        checkedInDialogFragment.setArguments(args);
        return checkedInDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_checked_in, null);
        builder.setView(view);
        builder.setTitle(null);

        return builder.create();
    }


}
