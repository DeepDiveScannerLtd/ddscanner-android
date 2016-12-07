package com.ddscanner.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ddscanner.R;
import com.ddscanner.ui.adapters.CheckedInDialogPhotosAdapter;

import java.util.ArrayList;

public class CheckedInDialogFragment extends DialogFragment implements View.OnClickListener {

    private CheckedInDialogPhotosAdapter checkedInDialogPhotosAdapter;
    private RecyclerView recyclerView;
    private ImageView closeImage;
    private Button closeButton;

    public CheckedInDialogFragment() {

    }

    public static CheckedInDialogFragment getCheckedInDialog(String diveSpotId, FragmentActivity activity) {
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
        builder.setView(view);
        builder.setTitle(null);
        checkedInDialogPhotosAdapter = new CheckedInDialogPhotosAdapter(getContext());
        findViews(view);
        return builder.create();
    }

    private void findViews(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.photos);
        closeButton = (Button) view.findViewById(R.id.button_close);
        closeImage = (ImageView) view.findViewById(R.id.image_close);
        setupRecyclerView();
        setupUi();

    }

    private void setupUi() {
        closeButton.setOnClickListener(this);
        closeImage.setOnClickListener(this);
    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(checkedInDialogPhotosAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_close:
            case R.id.button_close:
                this.dismiss();
                break;
        }
    }

    public void addPhotoToList(ArrayList<String> photos, int photosCount) {
        checkedInDialogPhotosAdapter.addPhotos(photos);
        recyclerView.scrollToPosition(photosCount);
    }

}
