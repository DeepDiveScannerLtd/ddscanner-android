package com.ddscanner.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.databinding.DialogYesNoBinding;
import com.ddscanner.interfaces.ConfirmationDialogClosedListener;

public class ConfirmationDialogFragment extends DialogFragment {

    private DialogYesNoBinding binding;

    private static final String ARG_TITLE = "TITLE";
    private static final String ARG_MESSAGE = "MESSAGE";
    private static final String ARG_POSITIVE = "POSITIVE";
    private static final String ARG_NEGATIVE = "NEGATIVE";
    private static final String ARG_CALLBACK_TYPE = "CALLBACK_TYPE";

    private CallbackType callbackType;

    public enum CallbackType {
        ACTIVITY, FRAGMENT
    }

    private static void show(FragmentManager fragmentManager, int titleRes, int messageRes, int positiveRes, int negativeRes, CallbackType callbackType) {
        ConfirmationDialogFragment confirmationDialogFragment = new ConfirmationDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, DDScannerApplication.getInstance().getString(messageRes));
        args.putString(ARG_TITLE, DDScannerApplication.getInstance().getString(titleRes));
        args.putString(ARG_POSITIVE, DDScannerApplication.getInstance().getString(positiveRes));
        args.putString(ARG_NEGATIVE, DDScannerApplication.getInstance().getString(negativeRes));
        args.putSerializable(ARG_CALLBACK_TYPE, callbackType);
        confirmationDialogFragment.setArguments(args);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(confirmationDialogFragment, null);
        fragmentTransaction.commitNowAllowingStateLoss();
    }

    public ConfirmationDialogFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_yes_no, null, false);
        binding.setHandlers(this);
        Bundle args = getArguments();
        callbackType = (CallbackType) args.getSerializable(ARG_CALLBACK_TYPE);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        binding.noButton.setText(args.getString(ARG_NEGATIVE));
        binding.yesButton.setText(args.getString(ARG_POSITIVE));
        binding.message.setText(args.getString(ARG_MESSAGE));
        builder.setView(binding.getRoot());
        builder.setCancelable(false);
        builder.setTitle(null);
        if (!args.getString(ARG_TITLE).isEmpty()) {
            binding.title.setVisibility(View.VISIBLE);
            binding.title.setText(args.getString(ARG_TITLE));
        } else {
//            binding.message.setPadding(Math.round(Helpers.convertDpToPixel(24, getContext())), Math.round(Helpers.convertDpToPixel(30, getContext())), Math.round(Helpers.convertDpToPixel(24, getContext())), Math.round(Helpers.convertDpToPixel(30, getContext())));
        }
        return builder.create();
    }

    public void positiveClicked(View view) {
        ConfirmationDialogClosedListener confirmationDialogClosedListener;
        dismiss();
        switch (callbackType) {
            case ACTIVITY:
                try {
                    confirmationDialogClosedListener = (ConfirmationDialogClosedListener) getActivity();
                    confirmationDialogClosedListener.onPositiveDialogClicked();
                } catch (ClassCastException e) {
                    throw new RuntimeException("Activity must implement DialogClosedListener interface");
                }
                break;
            case FRAGMENT:
                try {
                    confirmationDialogClosedListener = (ConfirmationDialogClosedListener) getParentFragment();
                    confirmationDialogClosedListener.onPositiveDialogClicked();
                } catch (ClassCastException e) {
                    throw new RuntimeException("Activity must implement DialogClosedListener interface");
                }
                break;
        }
    }

    public void negativeClicked(View view) {
        ConfirmationDialogClosedListener confirmationDialogClosedListener;
        dismiss();
        switch (callbackType) {
            case ACTIVITY:
                try {
                    confirmationDialogClosedListener = (ConfirmationDialogClosedListener) getActivity();
                    confirmationDialogClosedListener.onNegativeDialogClicked();
                } catch (ClassCastException e) {
                    throw new RuntimeException("Activity must implement DialogClosedListener interface");
                }
                break;
            case FRAGMENT:
                try {
                    confirmationDialogClosedListener = (ConfirmationDialogClosedListener) getParentFragment();
                    confirmationDialogClosedListener.onNegativeDialogClicked();
                } catch (ClassCastException e) {
                    throw new RuntimeException("Activity must implement DialogClosedListener interface");
                }
                break;
        }
    }

    public static void showForFragment(FragmentManager fragmentManager, int titleRes, int messageRes, int positiveRes, int negativeRes) {
        ConfirmationDialogFragment.show(fragmentManager, titleRes, messageRes, positiveRes, negativeRes, CallbackType.FRAGMENT);
    }

    public static void showForActivity(FragmentManager fragmentManager, int titleRes, int messageRes, int positiveRes, int negativeRes) {
        ConfirmationDialogFragment.show(fragmentManager, titleRes, messageRes, positiveRes, negativeRes, CallbackType.ACTIVITY);
    }

}
