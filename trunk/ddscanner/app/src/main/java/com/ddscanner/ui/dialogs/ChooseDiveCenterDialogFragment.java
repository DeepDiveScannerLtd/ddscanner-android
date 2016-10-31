package com.ddscanner.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.ddscanner.R;
import com.ddscanner.utils.Helpers;

public class ChooseDiveCenterDialogFragment extends DialogFragment {

    private AppCompatRadioButton btn1;
    private RadioGroup radioGroup;
    private SearchView searchView;

    ColorStateList colorStateList = new ColorStateList(
            new int[][]{
                    new int[]{-android.R.attr.state_checked},
                    new int[]{android.R.attr.state_checked}
            },
            new int[]{
                    Color.parseColor("#a3a3a3")
                    , R.color.primary,
            }
    );

    public ChooseDiveCenterDialogFragment() {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_choose_dive_center, null);
        radioGroup = (RadioGroup) view.findViewById(R.id.radio_group);
       // searchView = (SearchView) view.findViewById(R.id.searchview);
        for (int i = 0; i < 25; i++) {
            AppCompatRadioButton appCompatRadioButton = new AppCompatRadioButton(getContext());
            appCompatRadioButton.setSupportButtonTintList(colorStateList);
            appCompatRadioButton.setText("Dive center " + String.valueOf(i));
            appCompatRadioButton.setPadding(Math.round(Helpers.convertDpToPixel(20,getContext())),Math.round(Helpers.convertDpToPixel(15,getContext())), Math.round(Helpers.convertDpToPixel(20,getContext())), Math.round(Helpers.convertDpToPixel(15,getContext())));
            radioGroup.addView(appCompatRadioButton);
        }
        builder.setView(view);
        builder.setTitle(null);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null)
            return;
        int dialogWidth = Math.round(Helpers.convertDpToPixel(300, getActivity()));
        int dialogHeight = LinearLayout.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
    }
}
