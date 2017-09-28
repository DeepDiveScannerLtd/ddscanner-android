package com.ddscanner.screens.user.profile;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddscanner.R;
import com.ddscanner.databinding.FragmentDiveCenterProfileBinding;
import com.ddscanner.entities.DiveCenterProfile;
import com.ddscanner.entities.DiveSpotListSource;
import com.ddscanner.screens.divecemter.profile.languages.DiveCenterProfileLanguagesActivity;
import com.ddscanner.screens.divespots.list.DiveSpotsListActivity;
import com.ddscanner.screens.instructors.InstructorsActivity;
import com.ddscanner.screens.profile.divecenter.DiveCenterProfileFragmentViewModel;
import com.ddscanner.screens.profile.divecenter.DiveCenterSpotsActivity;
import com.ddscanner.ui.adapters.UserPhotosListAdapter;
import com.ddscanner.utils.EmailIntentBuilder;
import com.ddscanner.utils.PhoneCallIntentBuilder;
import com.mapbox.mapboxsdk.geometry.LatLng;

public class UserDiveCenterProfileFragment extends Fragment {

    private DiveCenterProfile diveCenterProfile;
    private FragmentDiveCenterProfileBinding binding;
    private LatLng diveCenterLocation = null;
    private int diveCenterType;

    public static UserDiveCenterProfileFragment newInstance(DiveCenterProfile diveCenterProfile, int diveCenterType) {
        UserDiveCenterProfileFragment userDiveCenterProfileFragment = new UserDiveCenterProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", diveCenterProfile);
        bundle.putInt("type", diveCenterType);
        userDiveCenterProfileFragment.setArguments(bundle);
        return userDiveCenterProfileFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dive_center_profile, container, false);
        diveCenterProfile = (DiveCenterProfile) getArguments().getSerializable("user");
        binding.setDiveCenterViewModel(new DiveCenterProfileFragmentViewModel(diveCenterProfile));
        diveCenterType = getArguments().getInt("type", 0);
        if (diveCenterProfile.getAddresses() != null && diveCenterProfile.getAddresses().get(0).getPosition() != null) {
            diveCenterLocation = diveCenterProfile.getAddresses().get(0).getPosition();
        }
        View v = binding.getRoot();
        binding.setHandlers(this);
        setupUi();
        return v;
    }

    private void setupUi() {
        if (binding.getDiveCenterViewModel().getDiveCenterProfile().getPhotos() != null) {
            binding.photosList.setLayoutManager(new GridLayoutManager(getContext(), 4));
            binding.photosList.setAdapter(new UserPhotosListAdapter(binding.getDiveCenterViewModel().getDiveCenterProfile().getPhotos(), binding.getDiveCenterViewModel().getDiveCenterProfile().getPhotosCount(), getActivity(), String.valueOf(binding.getDiveCenterViewModel().getDiveCenterProfile().getId())));
        }
    }

    public void showDiveSpots(View view) {
        //TODO change
//        DiveCenterSpotsActivity.show(getContext(), String.valueOf(binding.getDiveCenterViewModel().getDiveCenterProfile().getId()), diveCenterLocation);
    }

    public void showCreated(View view) {
        if (binding.getDiveCenterViewModel().getDiveCenterProfile().getCreatedSpotsCount() > 0) {
            DiveSpotsListActivity.show(getContext(), DiveSpotListSource.ADDED, String.valueOf(binding.getDiveCenterViewModel().getDiveCenterProfile().getId()));
        }
    }

    public void showEdited(View view) {
        if (binding.getDiveCenterViewModel().getDiveCenterProfile().getEditedSpotsCount() > 0) {
            DiveSpotsListActivity.show(getContext(), DiveSpotListSource.EDITED, String.valueOf(binding.getDiveCenterViewModel().getDiveCenterProfile().getId()));
        }
    }

    public void showLanguages(View view) {
        if (binding.getDiveCenterViewModel().getDiveCenterProfile().getLanguages() != null) {
            DiveCenterProfileLanguagesActivity.show(String.valueOf(binding.getDiveCenterViewModel().getDiveCenterProfile().getId()), getContext());
        }
    }

    public void showInstructors(View view) {
        if (Integer.parseInt(binding.getDiveCenterViewModel().getDiveCenterProfile().getInstructorsCount()) > 0 && diveCenterType == 1) {
            InstructorsActivity.showForResult(getActivity(), -1, String.valueOf(binding.getDiveCenterViewModel().getDiveCenterProfile().getId()));
        }
    }

}
