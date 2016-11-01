package com.ddscanner.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.R;

public class ActionSuccessDialogFragment extends DialogFragment {

    private TextView title;
    private TextView message;

    public ActionSuccessDialogFragment() {

    }

    public static ActionSuccessDialogFragment newInstance(int titleResId, int messageResId) {
        ActionSuccessDialogFragment actionSuccessDialogFragment = new ActionSuccessDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", DDScannerApplication.getInstance().getString(titleResId));
        args.putString("message", DDScannerApplication.getInstance().getString(messageResId));
        actionSuccessDialogFragment.setArguments(args);
        return actionSuccessDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_achievemnt_achieved, null);
        title = (TextView) view.findViewById(R.id.title);
        message = (TextView) view.findViewById(R.id.message);
        title.setText(getArguments().getString("title"));
        message.setText(getArguments().getString("message"));
        builder.setView(view);
        builder.setTitle(null);

        return builder.create();
    }
}
