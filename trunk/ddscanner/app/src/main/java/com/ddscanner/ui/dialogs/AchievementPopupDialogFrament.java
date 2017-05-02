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

import com.ddscanner.R;
import com.ddscanner.databinding.DialogAchievementPopupBinding;

public class AchievementPopupDialogFrament extends DialogFragment {

    private DialogAchievementPopupBinding binding;

    public AchievementPopupDialogFrament() {

    }

    public static void show(FragmentManager fragmentManager) {
        AchievementPopupDialogFrament achievementPopupDialogFrament = new AchievementPopupDialogFrament();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(achievementPopupDialogFrament, null);
        fragmentTransaction.commitNowAllowingStateLoss();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_achievement_popup, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(binding.getRoot());
        builder.setTitle(null);
        return builder.create();
    }
}
