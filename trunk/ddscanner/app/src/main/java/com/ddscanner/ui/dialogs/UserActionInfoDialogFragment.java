package com.ddscanner.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;
import com.ddscanner.entities.DialogClosedListener;

public class UserActionInfoDialogFragment extends DialogFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_REQUEST_CODE = "requestCode";
    private static final String ARG_CALLBACK_TYPE = "callbackType";
    private static final int CALLBACK_TYPE_NONE = 0;
    private static final int CALLBACK_TYPE_ACTIVITY = 1;

    private TextView title;
    private TextView message;
    private Button button;

    public UserActionInfoDialogFragment() {

    }

    private static void show(FragmentActivity activity, int titleResId, int messageResId, int requestCode, int callbackType) {
        UserActionInfoDialogFragment userActionInfoDialogFragment = new UserActionInfoDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, DDScannerApplication.getInstance().getString(titleResId));
        args.putString(ARG_MESSAGE, DDScannerApplication.getInstance().getString(messageResId));
        args.putInt(ARG_REQUEST_CODE, requestCode);
        args.putInt(ARG_CALLBACK_TYPE, callbackType);
        userActionInfoDialogFragment.setArguments(args);
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(userActionInfoDialogFragment, null);
        fragmentTransaction.commitNowAllowingStateLoss();
//        actionSuccessDialogFragment.show(activity.getSupportFragmentManager(), "");
    }

    public static void show(FragmentActivity activity, int titleResId, int messageResId) {
        UserActionInfoDialogFragment.show(activity, titleResId, messageResId, 0, CALLBACK_TYPE_NONE);
    }

    public static void showForActivityResult(FragmentActivity activity, int titleResId, int messageResId, int requestCode) {
        UserActionInfoDialogFragment.show(activity, titleResId, messageResId, requestCode, CALLBACK_TYPE_ACTIVITY);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        final int requestCode = args.getInt(ARG_REQUEST_CODE);
        final int callbackType = args.getInt(ARG_CALLBACK_TYPE);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_achievemnt_achieved, null);
        title = (TextView) view.findViewById(R.id.title);
        message = (TextView) view.findViewById(R.id.message);
        button = (Button) view.findViewById(R.id.close_button);
        title.setText(getArguments().getString(ARG_TITLE));
        message.setText(getArguments().getString(ARG_MESSAGE));
        builder.setView(view);
        builder.setTitle(null);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                DialogClosedListener dialogClosedListener;
                switch (callbackType) {
                    case CALLBACK_TYPE_NONE:
                        break;
                    case CALLBACK_TYPE_ACTIVITY:
                        dialogClosedListener = (DialogClosedListener) getActivity();
                        dialogClosedListener.onDialogClosed(requestCode);
                        break;
                }

            }
        });
        return builder.create();
    }



}
