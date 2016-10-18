package com.ddscanner.ui.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class InfoDialogFragment extends DialogFragment {

    private static final String TAG = InfoDialogFragment.class.getName();
    private static final String ARG_TITLE_RES_ID = "ARG_TITLE_RES_ID";
    private static final String ARG_MESSAGE_RES_ID = "ARG_MESSAGE_RES_ID";
    private static final String ARG_CALLBACK_TYPE = "ARG_CALLBACK_TYPE";
    private static final int ARG_VALUE_CALLBACK_TYPE_ACTIVITY = 1;
    private static final int ARG_VALUE_CALLBACK_TYPE_FRAGMENT = 2;
    private static final int ARG_VALUE_CALLBACK_TYPE_NONE = 3;
    private static final String ARG_REQUEST_CODE = "ARG_REQUEST_CODE";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        Bundle args = getArguments();
        final int callbackType = args.getInt(ARG_CALLBACK_TYPE, ARG_VALUE_CALLBACK_TYPE_NONE);
        final int requestCode = args.getInt(ARG_REQUEST_CODE);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setTitle(args.getInt(ARG_TITLE_RES_ID))
                .setMessage(args.getInt(ARG_MESSAGE_RES_ID))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        DialogClosedListener dialogClosedListener;
                        switch (callbackType) {
                            case ARG_VALUE_CALLBACK_TYPE_NONE:
                                break;
                            case ARG_VALUE_CALLBACK_TYPE_ACTIVITY:
                                try {
                                    dialogClosedListener = (DialogClosedListener) getActivity();
                                    dialogClosedListener.onDialogClosed(requestCode);
                                } catch (ClassCastException e) {
                                    throw new RuntimeException("Activity must implement DialogClosedListener interface");
                                }
                                break;
                            case ARG_VALUE_CALLBACK_TYPE_FRAGMENT:
                                if (getParentFragment() == null) {
                                    throw new RuntimeException("Parent fragment is null. Please check that you pass FragmentManger retrieved via getChildFragmentManager() when calling showForFragmentResult().");
                                }
                                try {
                                dialogClosedListener = (DialogClosedListener) getParentFragment();
                                dialogClosedListener.onDialogClosed(requestCode);
                                } catch (ClassCastException e) {
                                    throw new RuntimeException("Fragment must implement DialogClosedListener interface");
                                }
                                break;
                        }
                    }
                });
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
    }

    public static void showForActivityResult(FragmentManager fragmentManager, int titleResId, int messageResId, int requestCode, boolean cancelable) {
        InfoDialogFragment.show(fragmentManager, titleResId, messageResId, requestCode, ARG_VALUE_CALLBACK_TYPE_ACTIVITY, cancelable);
    }

    public static void showForFragmentResult(FragmentManager fragmentManager, int titleResId, int messageResId, int requestCode, boolean cancelable) {
        InfoDialogFragment.show(fragmentManager, titleResId, messageResId, requestCode, ARG_VALUE_CALLBACK_TYPE_FRAGMENT, cancelable);
    }

    public static void show(FragmentManager fragmentManager, int titleResId, int messageResId, boolean cancelable) {
        InfoDialogFragment.show(fragmentManager, titleResId, messageResId, 0, ARG_VALUE_CALLBACK_TYPE_NONE, cancelable);
    }

    private static void show(FragmentManager fragmentManager, int titleResId, int messageResId, int requestCode, int callbackType, boolean cancelable) {
        InfoDialogFragment infoDialogFragment = new InfoDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CALLBACK_TYPE, callbackType);
        args.putInt(ARG_TITLE_RES_ID, titleResId);
        args.putInt(ARG_MESSAGE_RES_ID, messageResId);
        args.putInt(ARG_REQUEST_CODE, requestCode);
        infoDialogFragment.setArguments(args);
        infoDialogFragment.setCancelable(cancelable);
        infoDialogFragment.show(fragmentManager, TAG);
    }

    public interface DialogClosedListener {
        void onDialogClosed(int requestCode);
    }
}
