package com.ddscanner.screens.dialogs.popup;

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

import com.ddscanner.R;
import com.ddscanner.databinding.DialogAchievementPopupBinding;
import com.ddscanner.entities.Popup;
import com.google.gson.Gson;

public class AchievementPopupDialogFrament extends DialogFragment {

    private DialogAchievementPopupBinding binding;
    private Popup popup;

    public interface PopupHideListener {
        void onPopupClosed();
    }

    public AchievementPopupDialogFrament() {

    }

    public static void showDialog(FragmentManager fragmentManager, String popup) {
        AchievementPopupDialogFrament achievementPopupDialogFrament = new AchievementPopupDialogFrament();
        achievementPopupDialogFrament.setCancelable(false);
        Bundle bundle = new Bundle();
        bundle.putString("POPUP", popup);
        achievementPopupDialogFrament.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(achievementPopupDialogFrament, null);
        fragmentTransaction.commitNowAllowingStateLoss();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_achievement_popup, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        popup = new Gson().fromJson(getArguments().getString("POPUP"), Popup.class);
        binding.setViewModel(new AchievementPopupDialogViewModel(popup));
        builder.setCancelable(false);
        builder.setView(binding.getRoot());
        builder.setTitle(null);
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                PopupHideListener popupHideListener;
                popupHideListener = (PopupHideListener) getActivity();
                popupHideListener.onPopupClosed();
            }
        });
        return builder.create();
    }
}
