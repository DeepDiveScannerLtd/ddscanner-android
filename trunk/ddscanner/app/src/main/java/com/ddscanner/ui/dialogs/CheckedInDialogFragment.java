package com.ddscanner.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ddscanner.R;
import com.ddscanner.analytics.EventsTracker;
import com.ddscanner.screens.reiews.add.LeaveReviewActivity;
import com.ddscanner.utils.ActivitiesRequestCodes;
import com.mapbox.mapboxsdk.geometry.LatLng;

public class CheckedInDialogFragment extends DialogFragment implements View.OnClickListener {

    private static final String ARG_LOCATION = "location";

    private ImageView closeImage;
    private Button closeButton;
    private String diveSpotId;
    private LatLng diveSpotLocation;

    public CheckedInDialogFragment() {

    }

    public static CheckedInDialogFragment getCheckedInDialog(String diveSpotId, LatLng diveSpotLocation, FragmentActivity activity) {
        CheckedInDialogFragment checkedInDialogFragment = new CheckedInDialogFragment();
        Bundle args = new Bundle();
        args.putString("divespotid", diveSpotId);
        args.putParcelable(ARG_LOCATION, diveSpotLocation);
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
        diveSpotLocation = getArguments().getParcelable(ARG_LOCATION);
        diveSpotId = getArguments().getString("divespotid");
        findViews(view);
        return builder.create();
    }

    private void findViews(View view) {
        closeButton = view.findViewById(R.id.button_close);
        closeImage = view.findViewById(R.id.image_close);
        setupUi();

    }

    private void setupUi() {
        closeButton.setOnClickListener(this);
        closeImage.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_close:
                this.dismiss();
                break;
            case R.id.button_close:
                this.dismiss();
                LeaveReviewActivity.showForResult(getActivity(), diveSpotId, 1, ActivitiesRequestCodes.REQUEST_CODE_DIVE_SPOT_DETAILS_ACTIVITY_LEAVE_REVIEW, diveSpotLocation, EventsTracker.SendReviewSource.CHECK_IN);
                break;
        }
    }


}
