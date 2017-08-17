package com.ddscanner.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.interfaces.DialogClosedListener;

public class UserActionInfoDialogFragment extends DialogFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_REQUEST_CODE = "requestCode";
    private static final String ARG_CALLBACK_TYPE = "callbackType";
    private static final int CALLBACK_TYPE_NONE = 0;
    private static final int CALLBACK_TYPE_ACTIVITY = 1;
    private static final int CALLBACK_TYPE_FRAGMENT = 2;

    private TextView title;
    private TextView message;
    private Button button;

    public UserActionInfoDialogFragment() {

    }

    private static void show(FragmentManager fragmentManager, int titleResId, int messageResId, int requestCode, int callbackType) {
        UserActionInfoDialogFragment UserActionInfoDialogFragment = new UserActionInfoDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, DDScannerApplication.getInstance().getString(titleResId));
        args.putString(ARG_MESSAGE, DDScannerApplication.getInstance().getString(messageResId));
        args.putInt(ARG_REQUEST_CODE, requestCode);
        args.putInt(ARG_CALLBACK_TYPE, callbackType);
        UserActionInfoDialogFragment.setArguments(args);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(UserActionInfoDialogFragment, null);
        fragmentTransaction.commitNowAllowingStateLoss();
//        actionSuccessDialogFragment.show(activity.getSupportFragmentManager(), "");
    }

    public static void show(FragmentManager fragmentManager, int titleResId, int messageResId, boolean cancelable) {
        UserActionInfoDialogFragment.show(fragmentManager, titleResId, messageResId, 0, CALLBACK_TYPE_NONE);
    }

    public static void showForActivityResult(FragmentManager fragmentManager, int titleResId, int messageResId, int requestCode, boolean cancelable) {
        UserActionInfoDialogFragment.show(fragmentManager, titleResId, messageResId, requestCode, CALLBACK_TYPE_ACTIVITY);
    }

    public static void showForFragmentResult(FragmentManager fragmentManager, int titleResId, int messageResId, int requestCode, boolean cancelable) {
        UserActionInfoDialogFragment.show(fragmentManager, titleResId, messageResId, requestCode, CALLBACK_TYPE_FRAGMENT);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        final int requestCode = args.getInt(ARG_REQUEST_CODE);
        final int callbackType = args.getInt(ARG_CALLBACK_TYPE);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_achievemnt_achieved, null);
        title = view.findViewById(R.id.title);
        message = view.findViewById(R.id.message);
        button = view.findViewById(R.id.close_button);
        if (getArguments().getString(ARG_TITLE).isEmpty()) {
            title.setVisibility(View.GONE);
        } else {
            title.setText(getArguments().getString(ARG_TITLE));
        }
        message.setText(getArguments().getString(ARG_MESSAGE));
        builder.setView(view);
        builder.setTitle(null);
        builder.setCancelable(false);
        button.setOnClickListener(view1 -> {
            dismiss();
            DialogClosedListener dialogClosedListener;
            switch (callbackType) {
                case CALLBACK_TYPE_NONE:
                    break;
                case CALLBACK_TYPE_ACTIVITY:
                    try {
                        dialogClosedListener = (DialogClosedListener) getActivity();
                        dialogClosedListener.onDialogClosed(requestCode);
                    } catch (ClassCastException e) {
                        throw new RuntimeException("Activity must implement DialogClosedListener interface");
                    }
                    break;
                case CALLBACK_TYPE_FRAGMENT:
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

        });
        return builder.create();
    }



}
