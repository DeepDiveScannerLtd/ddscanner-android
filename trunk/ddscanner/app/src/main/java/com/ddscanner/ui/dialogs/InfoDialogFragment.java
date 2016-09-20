package com.ddscanner.ui.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

public class InfoDialogFragment extends DialogFragment {

    private static final String TAG = InfoDialogFragment.class.getName();
    private static final String ARG_TITLE_RES_ID = "ARG_TITLE_RES_ID";
    private static final String ARG_MESSAGE_RES_ID = "ARG_MESSAGE_RES_ID";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        Bundle args = getArguments();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setTitle(args.getInt(ARG_TITLE_RES_ID))
                .setMessage(args.getInt(ARG_MESSAGE_RES_ID))
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

    public static void show(FragmentManager fragmentManager, int titleResId, int messageResId) {
        InfoDialogFragment infoDialogFragment = new InfoDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TITLE_RES_ID, titleResId);
        args.putInt(ARG_MESSAGE_RES_ID, messageResId);
        infoDialogFragment.setArguments(args);
        infoDialogFragment.show(fragmentManager, TAG);
    }
}
