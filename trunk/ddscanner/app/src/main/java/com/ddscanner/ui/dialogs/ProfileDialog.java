package com.ddscanner.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.ddscanner.R;

public class ProfileDialog extends DialogFragment implements View.OnClickListener {

    private Context context;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity.getBaseContext();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_profile_info, null);
        builder.setView(dialogView);
        Dialog dialog = builder.create();
        return dialog;
    }

    @Override
    public void onClick(View v) {

    }
}
