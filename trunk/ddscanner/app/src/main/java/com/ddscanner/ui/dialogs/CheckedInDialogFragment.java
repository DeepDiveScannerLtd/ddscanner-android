package com.ddscanner.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.ui.adapters.CheckedInDialogPhotosAdapter;

public class CheckedInDialogFragment extends DialogFragment implements View.OnClickListener {

    private CheckedInDialogPhotosAdapter checkedInDialogPhotosAdapter;
    private RecyclerView recyclerView;
    private LinearLayout pickPhoto;

    public CheckedInDialogFragment() {

    }

    public static CheckedInDialogFragment newInstance(String diveSpotId) {
        CheckedInDialogFragment checkedInDialogFragment = new CheckedInDialogFragment();
        Bundle args = new Bundle();
        args.putString("divespotid", diveSpotId);
        checkedInDialogFragment.setArguments(args);
        return checkedInDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_checked_in, null);
        checkedInDialogPhotosAdapter = new CheckedInDialogPhotosAdapter(getContext());
     //   pickPhoto = (LinearLayout) view.findViewById(R.id.pick_photo);
//        pickPhoto.setOnClickListener(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.photos);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(checkedInDialogPhotosAdapter);
        builder.setView(view);
        builder.setTitle(null);

        return builder.create();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }
}
